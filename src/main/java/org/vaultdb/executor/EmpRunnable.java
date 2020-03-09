package org.vaultdb.executor;

import java.io.ByteArrayOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.vaultdb.compiler.emp.EmpBuilder;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.util.EmpJniUtilities;
import org.vaultdb.util.Utilities;

// class for invoking emp within a thread
// for testing on localhost 
public class EmpRunnable implements Runnable {
    String className;
 	int party, port;
 	boolean[] output;
 	boolean compile = false;
 	final String vaultdbRoot = Utilities.getVaultDBRoot(); // directory with pom.xml
 	String input;
 	

	// find jar for javacpp, e.g., ~/.m2/repository/org/bytedeco/javacpp/1.4.4/javacpp-1.4.4.jar"
	String javaCppJar; // initialize shortly
	final String javaCppWorkingDirectory = vaultdbRoot + "/target/classes";

	

	
	public EmpRunnable(String aClassName, int aParty, int aPort, String anInput, boolean compile) {
		configure(aClassName, aParty, aPort, anInput, compile);
	}

 	public void configure(String aClassName, int aParty, int aPort, String anInput, boolean compile) {
 		className = aClassName;
 		party = aParty;
 		port = aPort;
 		this.compile = compile;
 		input = anInput;
 		
		try {
			javaCppJar = System.getProperty("user.home") + "/" + SystemConfiguration.getInstance().getProperty("javacpp-jar");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Failed to get configuration, error: " + e.getMessage());
			System.exit(1);
		}
	}
 	
 	
    public void run(){
	    ByteArrayOutputStream stdout = new ByteArrayOutputStream(); // use this for debug info
	    ByteArrayOutputStream stderr = new ByteArrayOutputStream(); // use this to capture output
	    
    	try {
    	
    		if(compile) {
    			EmpJniUtilities.buildEmpProgram(className);
    		}
    		


    	    PumpStreamHandler psh = new PumpStreamHandler(stdout, stderr);
    	    String command = "java -cp " + javaCppJar + ":" +  javaCppWorkingDirectory + " " + className + " " + party + " " + port + " " + input;
	        
    	    // need to fork and exec because EMP needs its own process space 
    	    //System.out.println("Command: " + command);
    	    CommandLine cl = CommandLine.parse(command);
    	    DefaultExecutor exec = new DefaultExecutor();
    	    exec.setStreamHandler(psh);
    	    int exitValue = exec.execute(cl);
    	    assert(0 == exitValue);
    	    
    	    String bitString = stderr.toString();
    
	    // translate to bools
    	    output = new boolean[bitString.length()];
    	    
    	    System.out.println("Party " + party + " returned " + bitString.length() + " bits.");
    	    for(int i = 0; i < bitString.length(); ++i) {
    	    	output[i] = bitString.charAt(i) == '1' ? true : false;
    	    }
    	    
    	    
    		
    	} catch (Exception e) {
    		System.out.println("Running emp on party " + party + " failed! " + e.getMessage());
    		e.printStackTrace();
    		System.out.println("Std err: " + stderr.toString());
    		System.out.println("Std out: " + stdout.toString());
    		
    	}
    }
    
    public boolean[] getOutput() {
    	return output;
    }
    
}
