package org.vaultdb.util;

public class CommandOutput {
	public String stdout;
	public String stderr;
	public int exitCode;
	
	public CommandOutput() {
		stdout = new String();
	}
};
