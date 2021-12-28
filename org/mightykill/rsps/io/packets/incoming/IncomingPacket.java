package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.Packet;

public abstract class IncomingPacket extends Packet {
	
	private int dPointer = 0;
	protected Client origin;

	public IncomingPacket(int packetId, int packetSize, byte[] data, Client origin) {
		super(packetId, packetSize, data);
		this.origin = origin;
	}
	
	public abstract void handlePacket();
	
	/**
	 * Gets the next Signed Byte in the stream, then increments the pointer
	 * @return The next Signed Byte
	 * @throws IndexOutOfBoundsException If the pointer is out of the bounds of the received data
	 */
	public byte nextSignedByte() {
		if(dPointer > data.length) throw new IndexOutOfBoundsException("End of data stream");
		return data[dPointer++];
	}
	
	public byte nextSignedByteC() {
		return (byte) -nextSignedByte();
	}
	
	public byte nextSignedByteS() {
		return (byte) (128 - nextSignedByte());
	}
	
	public int nextUnsignedByte() {
		int ub = (nextSignedByte() & 0xff);
		return ub;
	}
	
	public byte[] nextSignedBytes(int n) {
		byte[] i = new byte[n];
		for(int k=0;k<n;k++) {
			i[k] = nextSignedByte();
		}
		return i;
	}
	
	public int[] nextUnsignedBytes(int n) {
		int[] i = new int[n];
		for(int k=0;k<n;k++) {
			i[k] = nextUnsignedByte();
		}
		return i;
	}
	
	public String nextString() {
		StringBuffer sb = new StringBuffer();
        byte b;
        while ((b = (byte)nextSignedByte()) != 0)
        	sb.append((char)b);
        return sb.toString();
	}
	
	public int nextUnsignedShort() {
		return (nextUnsignedByte() << 8) | 
				nextUnsignedByte();
	}
	
	public int nextUnsignedShortBigEndian() {
		return (nextUnsignedByte()) | 
				nextUnsignedByte() << 8;
	}
	
	public int nextUnsignedShortA() {
		return (nextUnsignedByte() << 8) | 
				((nextUnsignedByte() -128)&0xff);
	}
	
	public int nextUnsignedShortBigEndianA() {
		return ((nextUnsignedByte() -128)&0xff) | 
				nextUnsignedByte() << 8;
	}
	
	public int nextSignedShort() {
		int s = ((int)nextSignedByte() << 8 |
				(int)nextSignedByte());
		if(s > 32767) s -= 0x10000;
		return s;
	}
	
	public int nextSignedShortBigEndian() {
		int s = ((int)nextSignedByte() |
				(int)nextSignedByte() << 8);
		if(s > 32767) s -= 0x10000;
		return s;
	}
	
	public int nextSignedShortA() {
		int s = ((int)nextSignedByte() << 8 |
				(int)nextSignedByte() -128);
		if(s > 32767) s -= 0x10000;
		return s;
	}
	
	public int nextSignedShortBigEndianA() {
		int s = ((int)nextSignedByte() -128 |
				(int)nextSignedByte() << 8);
		if(s > 32767) s -= 0x10000;
		return s;
	}
	
	public int nextInt() {
		return (nextUnsignedByte() << 24) | 
				(nextUnsignedByte() << 16) | 
				(nextUnsignedByte() << 8) | 
				 nextUnsignedByte();
	}
	
	public int nextInt_v1() {
		return (nextUnsignedByte() << 8) | 
				(nextUnsignedByte()) | 
				(nextUnsignedByte() << 24) | 
				 nextUnsignedByte() << 16;
	}
	
	public int nextInt_v2() {
		return (nextUnsignedByte() << 16) | 
				(nextUnsignedByte() << 24) |
				 nextUnsignedByte() | 
				 (nextUnsignedByte() << 8);
	}
	
	public long nextLong() {
		//System.out.println("Getting long here");
		return ((long)nextUnsignedByte() << 56) + 
				((long)nextUnsignedByte() << 48) + 
				((long)nextUnsignedByte() << 40) + 
				((long)nextUnsignedByte() << 32) + 
				((long)nextUnsignedByte() << 24) + 
				((long)nextUnsignedByte() << 16) + 
				((long)nextUnsignedByte() << 8) + 
				(long)nextUnsignedByte();
	}
	
	/*public int nextShort() {
		return (nextByte() << 8) + nextByte();
	}
	
	public int nextInt() {
		return (nextShort() << 16) + nextShort();
	}
	
	public long nextLong() {
		return (nextInt() << 32) + nextInt();
	}*/
	
	protected void resetPointer() {
		this.dPointer = 0;
	}

}
