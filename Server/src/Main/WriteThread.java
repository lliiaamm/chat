package Main;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WriteThread extends Thread {

	private PrintWriter writer;
	private Socket socket;
	private ChatClient client;

	public WriteThread(Socket socket, ChatClient client) {
		this.socket = socket;
		this.client = client;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.out.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		Scanner console = new Scanner(System.in);
		System.out.println("\nEnter your name: ");
		while (true) {
			if (client.getUserName() != null)
				break;
			String userName = console.nextLine();
			writer.println(userName);
		}
		String text = "";

		while (!text.contains("disconnnect")) {
			text = console.nextLine();
			writer.println(text);
		}
		console.close();

		try {
			socket.close();
		} catch (IOException ex) {

			System.out.println("Error writing to server: " + ex.getMessage());
		}
	}

}
