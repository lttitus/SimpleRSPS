package org.mightykill.rsps.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.mightykill.rsps.io.client.Client;

public class ConnectionManager implements Runnable {
	
	private int port;
	private boolean acceptingConnections = false;
	
	private ServerSocket socket;
	private LobbyManager lobby;
	
	public ConnectionManager(int port) throws IOException {
		this.socket = new ServerSocket(port, 16);
		this.lobby = new LobbyManager();
		this.acceptingConnections = true;
		(new Thread(lobby)).start();
	}

	public void run() {
		while(acceptingConnections) {
			try {	//Connecting new Clients
				Socket s = socket.accept();
				Client c = new Client(s);
				
				System.out.println("Accepting new Client from "+s.getInetAddress().getHostAddress()+" ["+c+"]");
				lobby.addClient(c);
			} catch(IOException ioe) {
				System.err.println("Problem accepting Client...");
			}
		}
		
		System.err.println("Connections are no longer being accepted.");
	}
	
	public void close() {
		this.acceptingConnections = false;
	}
	
	public int getPort() {
		return this.port;
	}

}
