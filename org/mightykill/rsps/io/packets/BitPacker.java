package org.mightykill.rsps.io.packets;

public class BitPacker {
	
	private static int[] bitMaskOut = new int[32];
    static {
        for (int i = 0; i < 32; i++) {
            bitMaskOut[i] = (1 << i) - 1;
        }
    }
	private byte[] data;
	private int bitPosition = 0;
	
	public BitPacker() {
		data = new byte[1];
	}
	
	public void expand() {
        byte[] oldBuffer = data;
        data = new byte[oldBuffer.length+1];
        System.arraycopy(oldBuffer, 0, data, 0, oldBuffer.length);
    }
	
	public void addBit(int bit, int pos) {
        if (pos >= data.length) {
            expand();
        }
        data[pos] &= ~ bit;
    }

    public void placeBit(int bit, int pos) {
        if (pos >= data.length) {
            expand();
        }
        data[pos] |= bit;
    }

    public void addBits(int numBits, int value) {
        int bytePos = bitPosition >> 3;
        int bitOffset = 8 - (bitPosition & 7);
        bitPosition += numBits;
        for (; numBits > bitOffset; bitOffset = 8) {
            addBit(bitMaskOut[bitOffset], bytePos);
            placeBit(((value >> (numBits - bitOffset)) & bitMaskOut[bitOffset]), bytePos++);
            numBits -= bitOffset;
        }
        if (numBits == bitOffset) {
            addBit(bitMaskOut[bitOffset], bytePos);
            placeBit((value & bitMaskOut[bitOffset]), bytePos);
        } else {
            addBit((bitMaskOut[numBits] << (bitOffset - numBits)), bytePos);
            placeBit((value & bitMaskOut[numBits]) << (bitOffset - numBits), bytePos);
        }
    }
	
	/**
	 * Expands the byte buffer, if required. This can be called recursively.
	 * @param numBits The number of bits that are going to be written
	 */
	/*private void expand(int numBits) {
		byte[] tmpdata = data;
		int bytesRequired = ((bitPosition+numBits+7)/8)+1;	//Number of full bytes required to hold all bits - eg 23+11+7=41/8=5+1=6
		data = new byte[bytesRequired];
		for(int i=0;i<tmpdata.length;i++) {
			data[i] = tmpdata[i];
		}
	}
	
	public void addBits(int numBits, int value) {
		int bytePosition = bitPosition/8;	//Which byte we are writing to
		int currWritePos = bitPosition%8;	//Position within the byte we are writing to
		int bitsRemaining = 8-currWritePos;
		
		if((bitPosition+numBits)/8 > bytePosition) {	//If the resulting written bits spill into another byte
			expand(numBits);
			int numUnwritableBits = numBits-bitsRemaining;	//Bits that will still need to be written; will also use this as a shift
			int writableValueMask = (1 << bitsRemaining)-1;
			int writableValue = value >> numUnwritableBits;	//Shift out the bits that are not going to be able to be written
			int remainingValueMask = (1 << numUnwritableBits)-1;
			int remainingValue = remainingValueMask & value;
			//System.out.println("Stuff: "+numBits+"; "+writableValue+"; "+remainingValue);
			
			data[bytePosition] |= (writableValueMask & writableValue);
			bitPosition += bitsRemaining;
			addBits(numUnwritableBits, remainingValue);	//Recurse until this is finished
			//TODO: I'm sure that there is a better way of doing this
		}else {	//If the written bits stay within this byte
			
			int shiftBits = bitsRemaining-numBits;	//Number of bits to shift to be in the correct position
			int bitMask = (1 << bitsRemaining)-1;	//Cover the bits that need to be written
			//System.out.println("Other Stuff: "+numBits+"; "+(bitMask & (value << shiftBits)));
			data[bytePosition] |= (bitMask & (value << shiftBits));	//AND the shifted bits to be written with the mask, then OR that to the current data
			bitPosition += numBits;
		}
	}*/
	
	/*public byte[] getBytes() {
		return this.data;
	}*/
	
	public byte[] getBytes(int num) {
		byte[] bytes = new byte[num];
		for(int i=0;i<num;i++) {
			bytes[i] = data[i];
		}
		return bytes;
	}
	
	/**
	 * Get the current stream of packed data
	 * @return An array of Bytes containing the packed bits
	 */
	public byte[] getBytes() {
		int numBytes = (bitPosition+7)/8;
		return getBytes(numBytes);
	}

}
