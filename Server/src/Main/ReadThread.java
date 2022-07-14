package Main;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {

	private BufferedReader reader;
	private ChatClient client;
	private Socket socket;

	public ReadThread(Socket socket, ChatClient client) {
		this.client = client;
		this.socket = socket;
		try {
			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
		} catch (IOException ex) {
			System.out.println("Error getting input stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		while (!client.isDisconnect()) {
			try {
				String response = reader.readLine();
				if (!(response != null)) {
					reader.close();
					socket.close();
					client.setDisconnect(true);
					break;
				}
				if(response.contentEquals("set_name"))
					client.setUserName(reader.readLine());
				else
				System.out.println(response);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
				break;
			}
		}
	}

}
