package org.mightykill.rsps.io.packets;

public abstract class Packet {
	
	protected int packetId;
	protected int packetSize;
	protected byte[] data;
	
	
	public Packet(int packetId, int packetSize, byte[] data) {
		this.packetId = packetId;
		this.packetSize = packetSize;
		this.data = data;
	}
	
	public int getPacketId() {
		return this.packetId;
	}
	
	public int getPacketSize() {
		return this.data.length;//this.packetSize;
	}
	
	public byte[] getBytes(int start, int offs) {
		byte[] i = new byte[offs];
		for(int k = 0;k<offs;k++) {
			i[k] = data[start+k];
		}
		return i;
	}
	
	public void setByte(byte b, int pos) {
		if(pos > data.length-1) expandBuffer();
		this.data[pos] = b;
	}
	
	public byte[] getBytes(int start) {
		int offs = data.length-start;
		return getBytes(start, offs);
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public void expandBuffer() {
		byte[] tmp = data;
		data = new byte[data.length+1];
		
		for(int i=0;i<tmp.length;i++) {
			data[i] = tmp[i];
		}
	}

}
