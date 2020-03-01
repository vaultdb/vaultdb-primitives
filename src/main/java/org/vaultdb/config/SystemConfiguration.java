package org.vaultdb.config;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.tools.FrameworkConfig;
//import org.smcql.executor.smc.OperatorExecution;
import org.vaultdb.util.Utilities;

public class SystemConfiguration {
	
	public static final SqlDialect DIALECT = SqlDialect.DatabaseProduct.POSTGRESQL.getDialect();
	
	static SystemConfiguration instance = null;
	private static final Logger logger =
	        Logger.getLogger(SystemConfiguration.class.getName());
	
	
	private Map<String, String> config;
	// package name --> SQL statement
	
	
	String configFile = null;

	// calcite parameters
	SchemaPlus pdnSchema;
	CalciteConnection calciteConnection;
	FrameworkConfig calciteConfig;
	
	int operatorCounter = -1;
	int queryCounter = -1;
	int portCounter = 54320;
	
	
	protected SystemConfiguration() throws Exception {
		config = new HashMap<String, String>();

		
		configFile = System.getProperty("vaultdb.setup");
		
		if(configFile == null) 
			configFile = Utilities.getVaultDBRoot() + "/conf/setup";
		System.out.println("config file: " + configFile);
		File f = new File(configFile); // may not always exist in remote invocations
		if(f.exists()) {
			List<String> parameters = Utilities.readFile(configFile);
			parseConfiguration(parameters);
			
		}		

		initializeLogger();
	}
	
	private void initializeLogger() throws SecurityException, IOException  {
		String filename = config.get("log-file");
		if(filename == null)
			filename = "vaultdb.log";

		String logFile = Utilities.getVaultDBRoot() + "/" + filename;

		
		String logLevel = config.get("log-level");
		if(logLevel != null)
			logLevel = logLevel.toLowerCase();

		
		logger.setUseParentHandlers(false);
		
		if(logLevel != null && logLevel.equals("debug")) {
				logger.setLevel(Level.FINE);
		}
		else if(logLevel != null && logLevel.equals("off")) {
				logger.setLevel(Level.OFF);
		}
		else {
				logger.setLevel(Level.INFO);
		}

		SimpleFormatter fmt = new SimpleFormatter();
		 StreamHandler sh = new StreamHandler(System.out, fmt);
		 logger.addHandler(sh);
		 
		try {
			FileHandler handler = new FileHandler(logFile);
			
		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);

		logger.addHandler(handler);
		} catch (Exception e) { // fall back to home dir
			logFile = "%h/vaultdb.log";
			FileHandler handler = new FileHandler(logFile);
			SimpleFormatter formatter = new SimpleFormatter();
			handler.setFormatter(formatter);

			logger.addHandler(handler);

			// show in console to verify
			logger.setUseParentHandlers(true);
			
		}
		
		
	}

	
	public static SystemConfiguration getInstance() throws Exception {
		if(instance == null) {
			instance = new SystemConfiguration();
		}
		return instance;
	}
	
	public  Logger getLogger() {
		return logger;
	}
	
	public void setProperty(String key, String value) {
		config.put(key, value);
	}
	
	
	public String getVaultDBRoot() {
		return Utilities.getVaultDBRoot();
	}
	
	public String getConfigFile() {
		return configFile;
	}
	
	private void parseConfiguration(List<String> parameters) {
		String prefix = null;

		for(String p : parameters) {
			if(!p.startsWith("#")) { // skip comments
				if(p.startsWith("[") && p.endsWith("]")) {
					prefix = p.substring(1, p.length() - 1);
				}
				else if(p.contains("=")){
					String[] tokens = p.split("=");
					String key = tokens[0];
					String value = (tokens.length > 1) ? tokens[1] : null;
					if(prefix != null) {
						key = prefix + "-" + key;
					}

					config.put(key, value);

				}
				
			}
		}
	
	}

	
	public String getProperty(String p) {
		if(config.containsKey(p)) 
			return config.get(p);
		
		return null;
	}
	
	public String getOperatorId() {
        ++operatorCounter;
        return String.valueOf(operatorCounter);
	}
	
	public String getQueryName() {
		++queryCounter;
		
		return "query" + queryCounter;
	}
	
	public ConnectionManager.WorkerConfiguration getHonestBrokerConfig() throws ClassNotFoundException, SQLException {
		/// public WorkerConfiguration(String worker, 
		//String h, int p, String dbName, String user, String pass)  // psql
		
		String host = config.get("psql-host");
		int port = Integer.parseInt(config.get("psql-port"));
		String dbName = config.get("psql-db");
		String user = config.get("psql-user");
		String pass = config.get("psql-password");
		String empBridgePath = Utilities.getVaultDBRoot() + "/deps/emp/emp-bridge";


		return new ConnectionManager.WorkerConfiguration("honest-broker", host, port, dbName, user, pass, empBridgePath);
	}
	
	public FrameworkConfig getCalciteConfiguration() {
		return calciteConfig;
	}
	
	public SchemaPlus getPdnSchema() {
		return pdnSchema;
	}
	
	public CalciteConnection getCalciteConnection() {
		return calciteConnection;
	}

	public int readAndIncrementPortCounter() {
		++portCounter;
		return portCounter;
	}

	public void resetCounters() {
		operatorCounter = -1;
		queryCounter = -1;
		portCounter = 54320;
	}


	public Map<String, String> getProperties() {
		return config;
	}
}
