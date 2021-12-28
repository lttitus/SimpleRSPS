package org.mightykill.rsps.io.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientSocket {
	
	private Socket sock;
	private InputStream in;
	private OutputStream out;
	
	public ClientSocket(Socket s) throws IOException {
		this.sock = s;
		this.in = s.getInputStream();
		this.out = s.getOutputStream();
	}
	
	public byte readByte() throws IOException {
		int recv = in.read();
		if(recv == -1) close();	//The Socket is dead at this point, mark for removal
		return (byte)(recv);
	}
	
	public byte[] readBytes(int num) throws IOException {
		byte[] bytes = new byte[num];
		for(int i=0;i<num;i++) {
			bytes[i] = readByte();
		}
		return bytes;
	}
	
	public int getAvail() throws IOException {
		return this.in.available();
	}
	
	public InetAddress getAddress() {
		return this.sock.getInetAddress();
	}
	
	/*public int readShort() throws IOException {
		return (readByte() << 8) + readByte();
	}
	
	public int readInt() throws IOException {
		return (readByte() << 24) + 
				(readByte() << 16) + 
				(readByte() << 8) + 
				readByte();
	}
	
	public int readLong() throws IOException {
		return (readByte() << 56) + 
				(readByte() << 48) + 
				(readByte() << 40) + 
				(readByte() << 32) +
				(readByte() << 24) + 
				(readByte() << 16) + 
				(readByte() << 8) + 
				readByte();
	}*/
	
	public void writeByte(byte b) throws IOException {
		out.write(b);
		//System.out.println("Wrote: "+b);
	}
	
	public void writeBytes(byte[] bytes) throws IOException {
		out.write(bytes);
		/*for(byte b:bytes) {
			writeByte(b);
		}*/
	}
	
	/*public void writeShort(int s) throws IOException {
		writeByte(s >> 8);
		writeByte(s);
	}
	
	public void writeLong(long l) throws IOException {
		writeByte((int)l >> 56);
		writeByte((int)l >> 48);
		writeByte((int)l >> 40);
		writeByte((int)l >> 32);
		writeByte((int)l >> 24);
		writeByte((int)l >> 16);
		writeByte((int)l >> 8);
		writeByte((int)l);
	}*/
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void close() {
		try {
			this.sock.close();
		} catch (IOException e) {
			System.err.println("Error closing Socket");
		}
	}
	
	public boolean isActive() {
		return !this.sock.isClosed();
	}

}
