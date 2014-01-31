package ca.mcgill.mcb.pcingola.snpSql.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hsqldb.Server;

/**
 * This class performs two main tasks:
 * 
 * 	- HSQLDB server management
 * 	- Hibernate session factory 
 *
 * @author Pablo Cingolani
 */
public class DbUtil {

	public static final String JDBC = "jdbc:hsqldb:file:";

	private static DbUtil dbUtil;

	boolean debug;
	boolean create;
	boolean cached;
	Server server; // HSQLDB dataabse server process
	String addr; // Connection IP address
	int port; // Connection Port
	String dbName;
	String dbPath; // Path to data
	Connection connection; // Direct connection to server
	SessionFactory sessionFactory; // Hibernate session
	ServiceRegistry serviceRegistry; // Hibernate session

	/**
	 * Begin a database transaction
	 * @return
	 */
	public static Session beginTransaction() {
		Session s = getCurrentSession();
		s.beginTransaction();
		return s;
	}

	/**
	 * Close session factory.
	 * Opening a session factory is very expensive, so this is only done at the end of the program. 
	 */
	public static void close() {
		getSessionFactory().close();
	}

	/**
	 * COmmit database transaction
	 */
	public static void commit() {
		Session s = getCurrentSession();
		if (s != null) {
			Transaction tx = s.getTransaction();
			if (tx != null) tx.commit();
		}
	}

	/**
	 * Create a new server
	 * @param addr : IP address (set to localhost if null)
	 * @param port : Port. Use default port if negative (9001)
	 * @param dbName : Database name
	 * @param dbPath : Path to database file
	 * @param debug : Use debug mode
	 */
	public static DbUtil create(String addr, int port, String dbName, String dbPath, boolean create, boolean cached, boolean debug) {
		dbUtil = new DbUtil(addr, port, dbName, dbPath, create, cached, debug);
		return get();
	}

	/**
	 * Create a new server at localhost:default port
	 * @param dbName : Database name
	 * @param dbPath : Path to database file
	 * @param debug : Use debug mode
	 */
	public static DbUtil create(String dbName, String dbPath, boolean create, boolean cached, boolean debug) {
		dbUtil = new DbUtil(null, -1, dbName, dbPath, create, cached, debug);
		return get();
	}

	public static DbUtil get() {
		return dbUtil;
	}

	/**
	 * Get current session. Here is a note from Hibernate's web 
	 * Note: 	Hibernate bind the "current session" to the current Java thread. It is opened when 
	 * 			getCurrentSession() is called for the first time, but in a "proxied" state that doesn't 
	 * 			allow you to do anything except start a transaction. When the transaction ends, either 
	 * 			through commit or roll back, the "current" Session is closed automatically.
	 */
	public static Session getCurrentSession() {
		return getSessionFactory().getCurrentSession();
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, you could look up in JNDI here
		return dbUtil.sessionFactory;
	}

	/**
	 * Perform a query that returns a list of objects
	 * @param queryName
	 * @return 
	 */
	public static List query(String queryName) {
		Session s = DbUtil.getCurrentSession();
		Query q = s.getNamedQuery(queryName);
		List results = q.list();
		return results;
	}

	/**
	 * Perform a named query that returns one integer
	 * @param name : Query name
	 * @return
	 */
	public static int queryInt(String queryName) {
		Session s = getCurrentSession();
		Query q = s.getNamedQuery(queryName);
		List<Integer> results = q.list();
		// Return first element in result list (if any)
		if ((results == null) || (results.size() <= 0) || (results.get(0) == null)) return 0;
		return results.get(0);
	}

	/**
	 * Perform a named query that returns one integer
	 * @param name : Query name
	 * @return
	 */
	public static long queryLong(String queryName) {
		Session s = getCurrentSession();
		Query q = s.getNamedQuery(queryName);
		List<Long> results = q.list();
		// Return first element in result list (if any)
		if ((results == null) || (results.size() <= 0) || (results.get(0) == null)) return 0;
		return results.get(0);
	}

	/**
	 * Perform a query that returns several rows and columns
	 * @param queryName
	 * @return 
	 */
	public static List<Object[]> queryMultiColumn(String queryName) {
		Session s = DbUtil.getCurrentSession();
		Query q = s.getNamedQuery(queryName);
		List<Object[]> results = q.list();
		return results;
	}

	/**
	 * Roll back (abort) database transaction)
	 */
	public static void rollback() {
		Session s = getCurrentSession();
		if (s != null) {
			Transaction tx = s.getTransaction();
			if (tx != null) tx.rollback();
		}
	}

	/**
	 * Close caches and connection pools
	 */
	public static void shutdown() {
		getSessionFactory().close();
	}

	private DbUtil(String addr, int port, String dbName, String dbPath, boolean create, boolean cached, boolean debug) {
		this.addr = addr;
		this.port = port;
		this.dbName = dbName;
		this.dbPath = dbPath;
		this.create = create;
		this.cached = cached;
		this.debug = debug;

		dbUtil = this;
		init();
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * Initialize server and hibernate 
	 */
	public void init() {
		initServer();
		initHibernate();
	}

	/**
	 * Initialize connection and set some server defaults
	 */
	void initConnection() {
		try {
			//---
			// Connect to server
			//---
			connection = DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath, "SA", "");

			//---
			// Set default tables as cached (otherwise it is memory and all info is lost after server is stopped)
			//---
			if (cached) {
				connection.createStatement().executeUpdate("SET DATABASE DEFAULT TABLE TYPE CACHED;");
				connection.createStatement().executeUpdate("SET FILES LOG FALSE");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Hibernate config and session
	 */
	void initHibernate() {
		try {
			// Configure
			Configuration configuration = new Configuration();
			configuration.configure();
			configuration.setProperty("hibernate.connection.url", JDBC + dbPath); // Set database URL here
			if (create) configuration.setProperty("hibernate.hbm2ddl.auto", "create"); // Create database

			// Create 
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(dbUtil.serviceRegistry);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Start HSQLDB server
	 */
	void initServer() {
		server = new Server();

		// Logging
		if (debug) server.setLogWriter(new PrintWriter(System.err));
		else server.setLogWriter(null);
		server.setSilent(!debug);
		server.setTrace(debug);

		// Db name, path, etc.
		server.setDatabaseName(0, dbName);
		server.setDatabasePath(0, dbPath);

		if (addr == null) addr = "localhost";
		server.setAddress(addr);
		if (port > 0) server.setPort(port); // Use non-default port?

		// Start server
		server.start();

		// Set some server defaults
		initConnection();
	}

	/**
	 * Close Hibernate and stop HSQLDB server
	 */
	public void stop() {
		close(); // Close hibernate connections

		// Shutdown HSQLDB server
		server.shutdownCatalogs(org.hsqldb.Database.CLOSEMODE_NORMAL);
		server.stop();
	}
}
