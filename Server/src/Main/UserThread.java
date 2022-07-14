package Main;

import java.io.*;
import java.net.*;
import java.util.Set;

public class UserThread extends Thread {

	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;
	private String username;

	public UserThread(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
		this.username = "";
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			printUsers();

			boolean permited = false;
			while (!permited) {
				String tempUserName = reader.readLine();
				if (tempUserName.length() == 0)
					System.out.println("Please write a username.");
				permited = true;
				Set<UserThread> threads = server.getUserThreads();
				for (UserThread user : threads) {
					if (user.username.length() == 0)
						continue;
					if (user.username.contentEquals(tempUserName)) {
						writer.println("Username taken. Choose another username.");
						permited = false;
						break;
					}
				}
				if (permited) {
					server.addUserName(tempUserName);
					username = tempUserName;
					writer.println("set_name");
					writer.println(username);
				}
			}

			String serverMessage = "New user connected: " + username;
			server.broadcast(serverMessage, this);

			String clientMessage = "";

			while (!clientMessage.equals("disconnect")) {
				clientMessage = reader.readLine();

				switch (clientMessage) {
				case "":
					break;
				case "disconnect":
					server.removeUser(username, this);
					break;
				case "set_msg":
					writer.println("Choose receiptent");
					printUsers();
					String receiptent = reader.readLine();
					UserThread rec = null;
					for (Object user : server.getUserThreads().toArray()) {
						UserThread current = (UserThread) user;
						if (current.username.contentEquals(receiptent)) {
							rec = current;
							break;
						}
					}
					if (rec != null) {
						writer.println("Message to " + receiptent);
						server.personal("[" + username + " (Private)]: " + reader.readLine(), rec);
					} else
						writer.println("User doesn't exist");
					break;
				case "get_users":
					printUsers();
					break;
				default:
					serverMessage = "[" + username + "]: " + clientMessage;
					server.broadcast(serverMessage, this);
				}

			}
			socket.close();

			serverMessage = username + " has quitted.";
			server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
			server.removeUser(username, this);
		}
	}

	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Connected users: " + server.getUserNames());
		} else {
			writer.println("No other users connected");
		}
	}

	void sendMessage(String message) {
		writer.println(message);
	}

}
