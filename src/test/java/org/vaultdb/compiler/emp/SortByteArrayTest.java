package org.vaultdb.compiler.emp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaultdb.executor.EmpRunnable;
import org.vaultdb.util.EmpJniUtilities;

import junit.framework.TestCase;

public class SortByteArrayTest  extends TestCase  {

	
	final String fullyQualifiedClassName = "org.vaultdb.compiler.emp.generated.SortByteArray"; 	 
    final int tupleWidth = 4; // chars in string

    
	public void testSortByteArrays() throws Exception {
		
		// comma separated inputs
		EmpRunnable aliceRunnable = new EmpRunnable(fullyQualifiedClassName, 1, 54321, "0043,3592", false);
		EmpRunnable bobRunnable = new EmpRunnable(fullyQualifiedClassName, 2, 54321, "0414,7123", false);

		EmpJniUtilities.buildEmpProgram("SortByteArray");

	   
		
		Thread alice = new Thread(aliceRunnable);
		alice.start();
		
		Thread bob = new Thread(bobRunnable);
		bob.start();
		
		alice.join();
		bob.join();
		
		boolean[] aliceOutput = aliceRunnable.getOutput();
		boolean[] bobOutput = bobRunnable.getOutput();
		
		System.out.println("Revealing output!");
		
		List<String> output = EmpJniUtilities.revealOutput(aliceOutput, bobOutput, tupleWidth);
		System.out.println("Query output: " + output);

		List<String> expectedOutput = new ArrayList<>(Arrays.asList("0043", "0414", "3592",  "7123"));
 
		assertEquals(expectedOutput, output);
	}
	
	
	

    
	 
	 

	 
	 


}
