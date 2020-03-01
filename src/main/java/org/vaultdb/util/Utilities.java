package org.vaultdb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.util.CommandOutput;


public class Utilities {
	

	
	public static String getVaultDBRoot() {
		String root = System.getProperty("smcql.root"); // for remote systems
	    if(root != null && root != "") {
	    		return root;
	    }
	       
	    // fall back to local path
	    URL location = Utilities.class.getProtectionDomain().getCodeSource().getLocation();
	    String path = location.getFile();
	       
	    // chop off trailing "/bin/src/"
	    if(path.endsWith("src/")) { // ant build
	        path = path.substring(0, path.length()-"src/".length());
	    }
	       
	    if(path.endsWith("bin/")) { // eclipse and ant build
	    		path = path.substring(0, path.length() - "/bin/".length());
	    }
	       
	    if(path.endsWith("target/classes/")) 
	    		path = path.substring(0, path.length() - "/target/classes/".length());

	    if(path.endsWith(".jar"))
	    	path = path.substring(0, path.length() - "target/smcql-open-source-0.5.jar/".length());
	    
	    return path;
	}	
	 
	 
    	public static List<String> readFile(String filename) throws IOException  {	
    		List<String> lines = null;
		
	    if(System.getProperty("smcql.root") != null) {
	    		InputStream is = Utilities.class.getClassLoader().getResourceAsStream(filename);
	    		lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
	    } else {
	    		lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
	    }
		
	    return lines;				
    	}

	public static void writeFile(String fname, String contents) throws FileNotFoundException, UnsupportedEncodingException {
         String path = FilenameUtils.getFullPath(fname);
         File f = new File(path);
         f.mkdirs();

         PrintWriter writer = new PrintWriter(fname, "UTF-8");
         writer.write(contents);
         writer.close();


	 }

		public static byte[] readBinaryFile(String filename) throws IOException {
			  System.out.println("reading in bytecode for " + filename);
		 	  Path p = FileSystems.getDefault().getPath("", filename);
		 	  return Files.readAllBytes(p);	 
		}


		public static void mkdir(String path) throws Exception {
			
			String cmd = "mkdir -p "  + path;
			
			CommandOutput output = runCmd(cmd);
			
			if(output.exitCode != 0 && output.exitCode != 1) { // 1 = already exists
				throw new Exception("Failed to create path " + path + "!");
			}
			
			
		}

		public static void mkdir(String path, String workingDirectory) throws Exception {
			
			String cmd = "mkdir -p "  + path;
			
			CommandOutput output = runCmd(cmd, workingDirectory);
			
			if(output.exitCode != 0 && output.exitCode != 1) { // 1 = already exists
				throw new Exception("Failed to create path " + path + "!");
			}
			
			
		}

		
	public static void cleanDir(String path) throws Exception {
			
			String cmd = "rm -rf "  + path + "/*" ;
			CommandOutput output = runCmd(cmd);
			
			if(output.exitCode != 0) {
				throw new Exception("Failed to clear out " + path + "!");
			}
			
			
		}
	
		
	public static CommandOutput runCmd(String aCmd) throws IOException, InterruptedException {
		
		String[] cmd = StringUtils.split(aCmd, ' ');
		return runCmd(cmd, null);
	}
	
public static CommandOutput runCmd(String aCmd, String aWorkingDirectory) throws IOException, InterruptedException {
		
		String[] cmd = StringUtils.split(aCmd, ' ');
		return runCmd(cmd, aWorkingDirectory);
	}
	
	
	
	public static CommandOutput runCmd(String[] cmd, String workingDirectory) throws IOException, InterruptedException {

		
		if(workingDirectory == null || workingDirectory == "") {
			workingDirectory = Utilities.getVaultDBRoot();
		}
		
		File dir = new File(workingDirectory);
		
		Process p = java.lang.Runtime.getRuntime().exec(cmd, null, dir);

		BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String err, out;
		
		CommandOutput cmdOutput = new CommandOutput();
		
		err = stderr.readLine();
		out = stdout.readLine();
		while(err != null || out != null) {
			if(err != null) {
				cmdOutput.output += err + "\n";
				err = stderr.readLine();
			}
			if(out != null) {
				cmdOutput.output += out + "\n";
				out = stdout.readLine();
			}
		}		
		
		p.waitFor();
		
		cmdOutput.exitCode = p.exitValue();
		return cmdOutput;
	}


	public static String getOperatorId(String packageName) {
		int idx = packageName.lastIndexOf('.');
		return packageName.substring(idx+1, packageName.length());
	
	}

	public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
       return sdf.format(cal.getTime());
	}

	public static float getElapsed(Date start, Date end) {
		return  (end.getTime() - start.getTime()) / 1000F; // ms --> secs
		
	}

	public static boolean dirsEqual(String lhs, String rhs) throws IOException, InterruptedException {
		String cmd = "diff -r " + lhs + " " + rhs;


		CommandOutput output =  runCmd(cmd);
		if(output.exitCode != 0) {
			System.out.println("diff: " + output.output);
		}
		return output.exitCode == 0;
	}

	
	
	public static int getEmpPort() throws Exception {
		int port;
		// try local source
		String empPort = SystemConfiguration.getInstance().getProperty("emp-port");
		if(empPort != null && empPort != "") {
			port = Integer.parseInt(empPort); // TODO: check if it is numeric
		}
		else {
			// handle remote case
			port = Integer.parseInt(System.getProperty("emp.port"));
		}
		return port;
	}



	}
