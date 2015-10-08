package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.misc.db.QueriesConfig;

public class DatabaseDao {

	public double getDbSize(Connection con) throws Exception {
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		double dbSize = 0.0;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.GET_DATABASE_SIZE));
				ps = con.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if (rs.next()) {
					dbSize = rs.getDouble("dbSize");
				}
				rs.close();
				ps.close();	
				
				return dbSize;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
