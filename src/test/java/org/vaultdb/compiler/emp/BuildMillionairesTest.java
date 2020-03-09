package org.vaultdb.compiler.emp;

import org.vaultdb.util.EmpJniUtilities;

import junit.framework.TestCase;

// workflow for building executor/Millionaires demo with supporting JNI classes
public class BuildMillionairesTest   extends TestCase {
	final String fullyQualifiedClassName = "org.vaultdb.compiler.emp.generated.Millionaires"; 	 
    
	public void testBuildMillionaires() throws Exception {
		
			EmpJniUtilities.buildEmpProgram("Millionaires");
		   
		   System.out.println("Build millionaires successful!");
	}
}
