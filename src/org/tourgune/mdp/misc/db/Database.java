package org.tourgune.mdp.misc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;


public class Database {

	private String host = DatabaseConfig.getInstance().getProperty(MiscConstants.DB_HOST);
	private String port = DatabaseConfig.getInstance().getProperty(MiscConstants.DB_PORT);
	private String db = DatabaseConfig.getInstance().getProperty(MiscConstants.DB_DB);
	private String username = DatabaseConfig.getInstance().getProperty(MiscConstants.DB_USER);
	private String password = DatabaseConfig.getInstance().getProperty(MiscConstants.DB_PASS);
	
	private Connection con;
		
	
	public Connection connect() throws Exception {
		con = null;
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, username, password);
	
//			if(!con.isClosed())
//				System.out.println("[MDP] Database connection OK");
		} catch (SQLException se) {
			System.out.println("[MDP] Database connection ERROR");
			throw se;
		} catch(Exception e) {
			System.err.println("Exception: " + e.getMessage());
			throw e;
		}
		return con;
	}
	
	public Connection disconnect() throws Exception{
		try {
			if(con != null){
				con.close();
				con = null;
			}
//			System.out.println("[MDP] Database disconnection OK");
		} catch(Exception e) {
			System.err.println("[MDP] Database connection ERROR");
			throw e;
		}
		return con;
	}
}
