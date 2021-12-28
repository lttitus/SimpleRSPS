package org.mightykill.rsps.io.packets.outgoing;

import java.io.IOException;

import org.mightykill.rsps.io.client.ClientSocket;
import org.mightykill.rsps.io.packets.Packet;

public abstract class OutgoingPacket extends Packet {
	
	private boolean sendSize = false;
	private boolean isShort = false;
	private boolean sendId = true;
	private int wPointer = 0;
	
	public OutgoingPacket(int packetId, int packetSize, boolean sendSize, boolean isShort) {
		super(packetId, packetSize, new byte[0]);
		this.sendSize = sendSize;
		this.isShort = isShort;
	}

	public OutgoingPacket(int packetId, int packetSize, boolean sendSize) {
		this(packetId, packetSize, sendSize, false);
		/*super(packetId, packetSize, new int[0]);
		this.sendSize = sendSize;*/
	}
	
	public OutgoingPacket(int packetId, int packetSize) {
		this(packetId, packetSize, false);
	}
	
	public OutgoingPacket(int packetId, int packetSize, boolean sendSize, boolean isShort, boolean sendId) {
		super(packetId, packetSize, new byte[0]);
		this.sendSize = sendSize;
		this.isShort = isShort;
		this.sendId = sendId;
	}

	private void expandBuffer(int e) {
		byte[] tmpData = this.data;
		this.data = new byte[this.data.length+e];
		for(int i=0;i<tmpData.length;i++) {
			this.data[i] = tmpData[i];
		}
	}
	
	public void addByte(int b) {
		expandBuffer(1);
		this.data[wPointer++] = (byte) b;
	}
	
	public void addBytes(byte[] arr) {
		for(byte b:arr) {
			addByte(b);
		}
	}
	
	public void addBytes(byte[] arr, int start, int length) {
		if(start > arr.length || length+start > arr.length) return;
		for(int i=start;i<length;i++) {
			addByte(arr[i]);
		}
	}
	
	public void addByteA(int b) {
		addByte((byte)(b + 128));
	}
	
	public void addByteS(int b) {
		addByte((byte)(128 - b));
	}
	
	public void addByteC(int b) {
		addByte((byte)(-b));
	}
	
	public void addShort(int s) {
		addByte((s >> 8));
		addByte((s));
	}
	
	public void addShortA(int s) {
		addByte((byte)(s >> 8));
		addByte((byte)(s + 128));
	}
	
	public void addShortBigEndian(int s) {
		addByte((byte)(s));
		addByte((byte)(s >> 8));
	}
	
	public void addShortBigEndianA(int s) {
		addByte((byte)(s + 128));
		addByte((byte)(s >> 8));
	}
	
	public void addInt(int i) {
		addByte((byte)(i >> 24));
		addByte((byte)(i >> 16));
		addByte((byte)(i >> 8));
		addByte((byte)(i));
	}
	
	public void addInt_v1(int i) {
		addByte((byte)(i >> 8));
		addByte((byte)(i));
		addByte((byte)(i >> 24));
		addByte((byte)(i >> 16));
	}
	
	public void addInt_v2(int i) {
		addByte((byte)(i >> 16));
		addByte((byte)(i >> 24));
		addByte((byte)(i));
		addByte((byte)(i >> 8));
	}
	
	public void addIntBigEndian(int i) {
		addByte((byte)(i));
		addByte((byte)(i >> 8));
		addByte((byte)(i >> 16));
		addByte((byte)(i >> 24));
	}
	
	public void addLong(long l) {
		addInt((int)(l >> 32));
		addInt((int)(l & -1L));
		/*addByte((int)(l >> 56));
		addByte((int)(l >> 48));
		addByte((int)(l >> 40));
		addByte((int)(l >> 32));
		addByte((int)(l >> 24));
		addByte((int)(l >> 16));
		addByte((int)(l >> 8));
		addByte((int) l);*/
	}
	
	/*public void addInt(int i) {
		addShort(i >> 16);
		addShort(i & 0xffff);
	}
	
	public void addLong(long l) {
		addInt((int)l >> 32);
		addInt((int)l & 0xffffffff);
	}*/
	
	public void addString(String s) {
		byte[] stringBytes = s.getBytes();
		for (int i = 0; i < s.length(); i++)
			addByte((byte)stringBytes[i]);
		addByte(0);
    }
	
	public void sendPacket(ClientSocket s) throws IOException {
		if(sendId) s.writeByte((byte)this.packetId);
		if(sendSize) {
			this.packetSize = data.length;//packetSize!=0?data.length:0;
			//System.out.println("Packet Size: "+packetSize);
			//System.out.println("");
			if(isShort) {
				s.writeByte((byte)(this.packetSize >> 8));
				s.writeByte((byte)(this.packetSize));
				//s.writeShort(this.packetSize);
			}else {
				s.writeByte((byte)this.packetSize);
			}
		}
		
		s.writeBytes(data);
		s.flush();
	}

}
