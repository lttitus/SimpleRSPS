package org.mightykill.rsps;

public class Start {

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		int worldId = Integer.parseInt(args[1]);
		
		(new Thread(new Engine(port, worldId))).start();
	}

}
