package org.mightykill.rsps.io;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.incoming.IncomingPacket;

public class LobbyManager implements Runnable {
	
	private boolean acceptingPlayers = false;
	private ArrayList<Client> connections = new ArrayList<Client>();
	
	public LobbyManager() {
		this.acceptingPlayers = true;
	}

	public void run() {
		while(acceptingPlayers) {
			try {
				for(Iterator<Client> it=connections.iterator();it.hasNext();) {	//Iterate through and handle their packets to get them logged in
					Client client = it.next();
					
					if(client.getSocket().isActive()) {	//This Client's Socket is no longer open, so it can be removed
						if(!client.isLoggedIn()) {
							IncomingPacket packet = client.getNextPacket(null, true);
							if(packet != null) {
								packet.handlePacket();
							}
						}
					}else {
						it.remove();
						//System.out.println("Removed old Client from queue ["+client+"]");	//Give us the textual representation for now
					}
				}
			} catch(ConcurrentModificationException cme) {
				continue;
			} catch(NullPointerException npe) {
				continue;
			}
		}
	}
	
	public void close() {
		this.acceptingPlayers = false;
	}
	
	public void addClient(Client c) {
		this.connections.add(c);
	}

}
