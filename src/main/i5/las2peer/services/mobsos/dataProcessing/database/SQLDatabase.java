package i5.las2peer.services.mobsos.dataProcessing.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * Stores the database credentials and provides access to query execution. The original code was taken from the
 * QueryVisualizationService.
 * 
 * @author Peter de Lange
 * 
 */
public class SQLDatabase {

	public Connection connection = null;
	private boolean isConnected = false;

	private SQLDatabaseType jdbcInfo = null;
	private String username = null;
	private String password = null;
	private String database = null;
	private String host = null;
	private int port = -1;

	/**
	 * 
	 * Constructor for a database instance.
	 * 
	 * @param jdbcInfo the driver you are using
	 * @param username login name
	 * @param password password
	 * @param database database name
	 * @param host host for the connection
	 * @param port port of the SQL server
	 * 
	 */
	public SQLDatabase(SQLDatabaseType jdbcInfo, String username, String password, String database, String host,
			int port) {
		this.jdbcInfo = jdbcInfo;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.database = database;
	}

	/**
	 * 
	 * Connects to the database.
	 * 
	 * @return true, if connected
	 * 
	 * @throws ClassNotFoundException if the driver was not found
	 * @throws SQLException if connecting did not work
	 * 
	 */
	public boolean connect() throws Exception {
		try {
			Class.forName(jdbcInfo.getDriverName()).newInstance();
			String urlPrefix = jdbcInfo.getURLPrefix(this.host, this.database, this.port);
			this.connection = DriverManager.getConnection(urlPrefix, this.username, this.password);

			if (!this.connection.isClosed()) {
				this.isConnected = true;
				return true;
			} else {
				return false;
			}
		} catch (ClassNotFoundException e) {
			throw new Exception(
					"JDBC-Driver for requested database type not found! Make sure the library is defined in the settings and is placed in the library folder!",
					e);
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * 
	 * Disconnects from the database.
	 * 
	 * @return true, if correctly disconnected
	 * 
	 */
	public boolean disconnect() {
		try {
			this.connection.close();
			this.isConnected = false;
			this.connection = null;

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			this.isConnected = false;
			this.connection = null;
		}
		return false;
	}

	/**
	 * 
	 * Checks, if this database instance is currently connected.
	 * 
	 * @return true, if connected
	 * 
	 */
	public boolean isConnected() {
		try {
			return (this.isConnected && this.connection != null && !this.connection.isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * Executes a SQL statement to insert an entry into the database.
	 * 
	 * @param SQLStatment a SQLStatement
	 * 
	 * @return true, if correctly inserted
	 * 
	 * @throws SQLException problems inserting
	 * 
	 */
	public boolean store(String SQLStatment) throws SQLException {
		// make sure one is connected to a database
		if (!isConnected()) {
			System.err.println("No database connection.");
			return false;
		}

		Statement statement = connection.createStatement();
		statement.executeUpdate(SQLStatment);
		return true;

	}

	public String getUser() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getDatabase() {
		return this.database;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public SQLDatabaseType getJdbcInfo() {
		return jdbcInfo;
	}

}
