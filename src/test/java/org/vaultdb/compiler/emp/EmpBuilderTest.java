package org.vaultdb.compiler.emp;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.util.EmpJniUtilities;
import org.vaultdb.util.FileUtilities;
import org.vaultdb.util.Utilities;

import junit.framework.TestCase;

// demonstrates how to compile, load, and invoke a newly generated query class
public class EmpBuilderTest  extends TestCase {

	
	SystemConfiguration config;
	Logger logger;
	
	String srcPath, dstPath;
	
	  protected void setUp() throws Exception {
		    config = SystemConfiguration.getInstance();
		    logger = config.getLogger();
		
		
	    	 srcPath = config.getVaultDBRoot() + "/src/test/java/org/vaultdb/compiler/emp/source/";
	    	 dstPath =  config.getVaultDBRoot() + "/src/main/java/org/vaultdb/compiler/emp/generated/";

		  }
		



	// tests emp-jni link with minimal dependencies
	public void testEmpJniDemo() throws Exception {	
		String className = "EmpJniDemo";
		String expectedClass = "I am a org.vaultdb.compiler.emp.generated.EmpJniDemo!";
		
		setupTest(className);
		testCase(className, expectedClass);	
	}
	
	
	/* tests emp-jni link with jdbc for data inputs
		public void testCount() throws Exception {
			String className = "Count";		
			String expectedClass = "I am a org.vaultdb.compiler.emp.generated.Count!";

			setupTest(className);
			testCase(className, expectedClass);	
		}*/

	protected void testCase(String className, String expectedClass) throws Exception {		

		
		String fullyQualifiedClassName = EmpJniUtilities.getFullyQualifiedClassName(className);
		EmpJniUtilities.buildEmpProgram(className);
		
		logger.info("Finished compiling test class");
		EmpProgram instance = EmpBuilder.getClassInstance(fullyQualifiedClassName, 1, 54321);
		assert(instance != null);
			
		// verify that it loads and is runnable
		String observedClass = instance.helloWorld();
		assertEquals(observedClass, expectedClass);

		
		EmpJniUtilities.cleanEmpCode(className);
	}
	
	// copy the files for the new class into the generated path to simulate codegen
    void setupTest(String testName) throws Exception {
    	
    	String srcFile = srcPath + testName;
    	String dstFile = dstPath + testName;
    	
    	FileUtilities.copyFile(srcFile + ".java_", dstFile + ".java");
    	FileUtilities.copyFile(srcFile + ".h_", dstFile + ".h");
    }
    
    
    
    
	
}
	


