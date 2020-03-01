package org.vaultdb.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.vaultdb.util.Utilities;

// read config and connect to psql instances
public class ConnectionManager {
	
	private Map<String, WorkerConfiguration> workers;
	private Map<Integer, WorkerConfiguration> workersById;
	private List<String> hosts;
	
	private static ConnectionManager instance = null;
	
	
	protected ConnectionManager() throws Exception {

		workers = new LinkedHashMap<String, WorkerConfiguration>();
		hosts = new ArrayList<String>();
		
		
		workersById = new HashMap<Integer, WorkerConfiguration>();
		initialize();
	}
	

	   public static ConnectionManager getInstance() throws Exception {
		      if(instance == null) {
		         instance = new ConnectionManager();
		      }
		   
		      return instance;
		   }

	   
	public void reinitialize() {
		instance = null;
	}
	
	
	public void close() throws ClassNotFoundException, SQLException {
		for(WorkerConfiguration w : workers.values()) {
			Connection  c = w.getDbConnection();
			c.close();
			
		}
	}
	
	private void initialize() throws Exception, SQLException {
		List<String> hosts = null;
		
		String connectionParameters = System.getProperty("vaultdb.connections.str");
		if(connectionParameters != null) {
			hosts = Arrays.asList(StringUtils.split(connectionParameters, '\n'));
		}
		
		else {
			String connectionsFile = SystemConfiguration.getInstance().getProperty("data-providers");		
			String configHosts = Utilities.getVaultDBRoot() + "/" + connectionsFile;

			 hosts = Utilities.readFile(configHosts);

		}
		
		for(String h : hosts) {
			parseConnection(h);
		}
		
	}
	
	
	private void parseConnection(String c) throws NumberFormatException, Exception, SQLException {
		if(c.startsWith("#")) { // comment in spec
			return;
		}
     

		WorkerConfiguration worker = new WorkerConfiguration(c);

		if(!hosts.contains(worker.hostname)) 
			hosts.add(worker.hostname);

		workers.put(worker.workerId,  worker);
		workersById.put(worker.dbId, worker);
	}

	
 	// list of all hostnames in vaultdb deployment
	public List<String> getHosts() {
		return hosts;
	}
	
	
	public List<WorkerConfiguration> getWorkerConfigurations() {
		return new ArrayList<WorkerConfiguration>(workers.values());
	}
	
	public Set<String> getDataSources() {
		return  workers.keySet();
	}
	
	public WorkerConfiguration getWorker(String workerId) {
		return workers.get(workerId);
	}
	
	public Connection getConnection(String workerId) throws SQLException, ClassNotFoundException {
		return workers.get(workerId).getDbConnection();
	}
	
	public Connection getConnectionById(int id) throws SQLException, ClassNotFoundException {
		return workersById.get(id).getDbConnection();
	}
	
		
	
	
	// get first connection
	public String getAlice() {
		List<String> keys = new ArrayList<String>(workers.keySet());
		return keys.get(0);
	}
	
	// get second connection
	public String getBob() {
		List<String> keys = new ArrayList<String>(workers.keySet());
		return keys.get(1);
		
	}

	// for C++/pqxx connection
	// 1 = alice, 2 = bob
	public String getConnectionString(int party) {
		WorkerConfiguration config = workersById.get(party);
		String connectionString = new String();
		// "dbname = " + db + " user = " + user_db + " host = " + host_db + " port = " + port_db;
		
		connectionString = "dbname=" + config.dbName + " user=" + config.user
				+ " host=" + config.hostname + " port=" + config.dbPort;
		
		return connectionString;
		
	}

	public static class WorkerConfiguration  {

		public String workerId;
		public int dbId;


		public String hostname;

		// psql config
		public String dbName;
		public String user;
		public String password;
		public String vaultDBRoot;
		public int dbPort;
		public int empPort = 0;


		transient Connection  dbConnection = null; // psql connection, will be reconstructed for remote runs

		// Plaintext channel: port for client commands (e.g., running execution steps) and passing around serialized objects
		//	public int clientPort;

		public WorkerConfiguration(String worker,
				String h, int p, String dbName, String user, String pass, String vaultdbRoot)  // psql
						throws ClassNotFoundException, SQLException {
			workerId = worker;
			hostname = h;
			dbPort = p;
			this.dbName = dbName;
			this.user = user;
			password = pass;
			this.vaultDBRoot = vaultdbRoot.replace("$VAULTDB_ROOT", Utilities.getVaultDBRoot()); // replace pointer


		}


		// from configuration file
		public WorkerConfiguration(String c) throws Exception {


			String[] tokens = c.split("=");
			workerId = tokens[0];

			String details = tokens[1];

			String[] parsed = details.split(":|,");
			hostname = parsed[0];

			dbPort = Integer.parseInt(parsed[1]);
			dbName = parsed[2];


			vaultDBRoot = parsed[3];
			vaultDBRoot = vaultDBRoot.replace("$VAULTDB_ROOT", Utilities.getVaultDBRoot()); // replace pointer
			empPort = Integer.parseInt(parsed[4]);

			dbId = Integer.parseInt(parsed[5]);

			SystemConfiguration globalConf = SystemConfiguration.getInstance();
			user = globalConf.getProperty("psql-user");
			password = globalConf.getProperty("psql-password");

		}

		@Override
		public String toString() {
			return workerId + "@" + hostname + " psql(" + dbName + ":" + dbPort + ")" ;
		}

		public Connection getDbConnection() throws SQLException, ClassNotFoundException {
			if(dbConnection == null) {
				Class.forName("org.postgresql.Driver");
				String url = "jdbc:postgresql://" + hostname + ":" + dbPort + "/" + dbName;
				Properties props = new Properties();

				props.setProperty("user", user);
				props.setProperty("password",password);
				dbConnection = DriverManager.getConnection(url, props);

			}
			return dbConnection;
		}



	}
}

