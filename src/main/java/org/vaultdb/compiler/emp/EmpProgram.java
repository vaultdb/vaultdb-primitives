package org.vaultdb.compiler.emp;

public class EmpProgram {

	protected int party = 0;
	protected int port = 0;
	
	public EmpProgram(int aParty, int aPort) {
		party = aParty;
		port = aPort;
		
	}
	
	
	// delegate to implementing classes
    public  byte[] runProgram() {
    	return null;
    }
    
    
    
    
	public static boolean[] byteArray2BitArray(byte[] bytes) {
	    boolean[] bits = new boolean[bytes.length * 8];
	    for (int i = 0; i < bytes.length * 8; i++) {
	      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0)
	        bits[i] = true;
	    }
	    return bits;
	  }



}
