package org.vaultdb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.bytedeco.javacpp.Loader;

public class EmpJniUtilities {
	
    public static String getPropertyFile()  {
	String propertyFile = Utilities.getVaultDBRoot() + "/src/main/resources/org/bytedeco/javacpp/properties/";
	propertyFile += Loader.getPlatform();
	propertyFile += "-emp.properties";
	 
	return propertyFile;
    }

	// output handling
	public static List<String> revealStringOutput(boolean[] alice, boolean[] bob, int tupleWidth) {
		assert(alice.length == bob.length);
		boolean[] decrypted = new boolean[alice.length];
		int tupleBits = tupleWidth*8; // 8 bits / char
		int tupleCount = alice.length / tupleBits;
		List<String> output = new ArrayList<String>();
		

		
		for(int i = 0; i < alice.length; ++i) {
			decrypted[i] = alice[i] ^ bob[i];	
		}

		
		byte[] allBytes = deserializeBooleans(decrypted);
		byte[] bytes;
		
		int readIdx = 0;
		for(int i = 0; i < tupleCount; ++i) {
			int fromIdx = readIdx;
			int toIdx = fromIdx + tupleWidth;
			
			bytes = Arrays.copyOfRange(allBytes, fromIdx, toIdx);
			String tuple = new String(bytes);
			
			output.add(tuple);
			readIdx = toIdx;
		}
		
		return output;		
	}
	
	
	// for remote executions
	public static List<String> revealStringOutputFromBytes(byte[] alice, byte[] bob, int tupleWidth) {
		assert(alice.length == bob.length);
		byte[] decrypted = new byte[alice.length];
		int tupleBits = tupleWidth*8; // 8 bits / char
		int tupleCount = alice.length / tupleBits;
		List<String> output = new ArrayList<String>();
		

		
		for(int i = 0; i < alice.length; ++i) {
			decrypted[i] = (byte) (alice[i] ^ bob[i]);	
		}

		
	
		byte[] bytes;
		
		int fromIdx = 0;
		for(int i = 0; i < tupleCount; ++i) {
			int toIdx = fromIdx + tupleWidth;
			
			bytes = Arrays.copyOfRange(decrypted, fromIdx, toIdx);
			String tuple = new String(bytes);
			
			output.add(tuple);
			fromIdx = toIdx;
		}
		
		return output;		
		
	}
	
	public static byte[] deserializeBooleans(boolean[] src) {
		int byteCount = (int) Math.ceil(src.length / 8.0);
		
		byte[] dst = new byte[byteCount];
		
		for(int i = 0; i < byteCount; ++i) {
			dst[i] = (byte)((src[i*8]?1<<7:0) + (src[i*8+1]?1<<6:0) + (src[i*8+2]?1<<5:0) + 
	                (src[i*8+3]?1<<4:0) + (src[i*8+4]?1<<3:0) + (src[i*8+5]?1<<2:0) + 
	                (src[i*8+6]?1<<1:0) + (src[i*8+7]?1:0));
		}
		
		return dst;
	}

	public static String deserializeString(boolean[] src) {
		assert(src.length % 8 == 0);
		int chars = src.length / 8;
		String value = new String();
		
		for(int i = 0; i < chars; ++i)
		{
			boolean[] bits = Arrays.copyOfRange(src, i*8, (i+1)*8);
			value += deserializeChar(bits);
			
		}
		
		return value;
		
		
	}

	
	
	public static char deserializeChar(boolean[] bits) {
		assert(bits.length == 8);

	    int n = 0;
	    for (boolean b : bits)
	        n = (n << 1) | (b ? 1 : 0);
	    return (char) n;
	}


	public static boolean[] serializeString(String src) {

			boolean[] ret = new boolean[src.length()*8];
			int writeIdx = 0;
			
			if(src != null) {
				BitSet bs = BitSet.valueOf(src.getBytes());
				for(int i = 0; i < src.length()*8; ++i) {
					ret[writeIdx] = bs.get(i) ? true : false;
					++writeIdx;
				}
			}
			return ret;
	
	}
			
	 
}
