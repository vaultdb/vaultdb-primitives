package org.vaultdb.compiler.emp;

import junit.framework.TestCase;

// workflow for building executor/Millionaires demo with supporting JNI classes
public class BuildMillionairesTest   extends TestCase {
	final String fullyQualifiedClassName = "org.vaultdb.compiler.emp.generated.Millionaires"; 	 
    
	public void testBuildMillionaires() throws Exception {
		
		   EmpBuilder builder = new EmpBuilder();
		   assertTrue(builder.compile(fullyQualifiedClassName, true));

		   
		   System.out.println("Build millionaires successful!");
	}
}
