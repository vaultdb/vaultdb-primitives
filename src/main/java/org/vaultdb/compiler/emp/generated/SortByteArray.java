package org.vaultdb.compiler.emp.generated;






import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Const;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;
import org.vaultdb.compiler.emp.EmpProgram;






@Platform(include={"SortByteArray.h"}, 
			compiler = "cpp11")





@Namespace("SortByteArray")
public class SortByteArray  extends EmpProgram {
	

	byte[] inputs;
	int tupleWidth; // in bytes
	
	public SortByteArray(int party, int port, int tupleWidth, byte[] inputs) {
		super(party, port);
		this.inputs = inputs;
		this.tupleWidth = tupleWidth;
		
	}

	public static class SortByteArrayClass extends Pointer {
	
        static {

            Loader.load(); 
	       } 
       
        public SortByteArrayClass() { 
        	allocate(); 
        	}
        
        private native void allocate();

        // length of array and width of each tuple in bytes
        public native void setInput(int length, int tupleWidth, @Const BytePointer inputs);
                
        public native  BytePointer  run(int party, int port); 
        
        public native int getOutputSize();
        public native void cleanUp();
        
        
	}
	
	   boolean[] queryOutput = null;
	   
	    
	   	@Override
        public byte[] runProgram() {
        	
	   		SortByteArrayClass theQuery = new SortByteArrayClass();
        	
	   		
	   		int bytesIn  = inputs.length;
        	theQuery.setInput(bytesIn, tupleWidth, new BytePointer(inputs));
        	

        	
        	BytePointer output = theQuery.run(party, port);
        	int outputSize = theQuery.getOutputSize();
        	byte[] outputBytes = new byte[outputSize];
        	output.get(outputBytes);
        	
        	theQuery.cleanUp();  	
        	theQuery.close();
        	
        	return outputBytes;
	        
	        
        }
        
	
	// for testing
	public static void main(String[] args) {
           int party = Integer.parseInt(args[0]);
	       int port = Integer.parseInt(args[1]);
	       
	       String inputs = args[2];
	       String[] inputStrings = inputs.split(",");

	       int tupleWidth = inputStrings[0].length();
	       
	      
	       inputs = inputs.replaceAll(",", "");
	       System.out.println("Inputs: " + inputs + " tupleWidth = " + tupleWidth);

	    
	    SortByteArray qc = new SortByteArray(party, port, tupleWidth, inputs.getBytes());
	
		byte[] bytes = qc.runProgram();
		boolean[] bits = EmpProgram.byteArray2BitArray(bytes);
		
		char b;
		
		// write bitstring to stderr
		for(int i = 0; i < bits.length; ++i) {
			b = (bits[i] == true) ? '1' : '0';
			System.err.print(b);
		}
	
			
	        
    }        
	
    	
}