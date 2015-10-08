package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import org.tourgune.mdp.amazon.bean.Admin;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.misc.db.Database;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class AdminDao {

	public void insertAdminInfo (Admin admin) throws Exception {
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String country, accType = null;
		Integer numAccommodations, rowsOk, rowsKo, rowsHDE_NF, rowsTotal = 0;
		
		Database db = new Database();
		
		try {
			Connection con;
			con = db.connect();
		
			if (con != null) {
				
				// 1. Getting daily rows
				sql	.append(QueriesConfig.getInstance().getProperty(Constants.SELECT_DAILY_INFO));
				
				ps = con.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				while (rs.next()) {
					country = rs.getString(TablesDB.DAC_COUNTRY);
					accType = rs.getString(TablesDB.DAT_NAME);
					numAccommodations = rs.getInt("num_accommodations");
					rowsOk = rs.getInt("ok");
					rowsKo = rs.getInt("ko");
					rowsHDE_NF = rs.getInt("hde_not_found");
					rowsTotal = rs.getInt("total");
					
					admin.setCountry(country);
					admin.setAccType(accType);
					admin.setAccCount(numAccommodations);
					admin.setRowsOk(rowsOk);
					admin.setRowsKo(rowsKo);
					admin.setRowsHDE_NF(rowsHDE_NF);
					admin.setRowsTotal(rowsTotal);
					
				// 2. Inserting info
					sql = new StringBuffer();
					sql	.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_ADMIN_INFO));
			
					ps = con.prepareStatement(sql.toString());
					ps.setString(1, admin.getInstance());
					ps.setTimestamp(2, admin.getStartTime());
					
					if (admin.getEndTime() == null)
						ps.setNull(3, Types.NULL);
					else
						ps.setTimestamp(3, admin.getEndTime());
					
					if (admin.getTotalTime() == null)
						ps.setNull(4, Types.NULL);
					else
						ps.setString(4, admin.getTotalTime());
					
					if (admin.getDataSize() == null)
						ps.setNull(5, Types.NULL);
					else
						ps.setDouble(5, admin.getDataSize());	
				// Country se pone cadena vacía ya que es parte de la PK	
					if (admin.getCountry() == null)
						ps.setString(6, "");
					else
						ps.setString(6, admin.getCountry());
					
					if (admin.getAccType() == null)
						ps.setNull(7, Types.NULL);
					else	
						ps.setString(7, admin.getAccType());
					
					if (admin.getAccCount() == null)
						ps.setNull(8, Types.NULL);
					else
						ps.setInt(8, admin.getAccCount());
					
					if (admin.getRowsTotal() == null)
						ps.setNull(9, Types.NULL);
					else
						ps.setInt(9, admin.getRowsTotal());
					
					if (admin.getRowsOk() == null)
						ps.setNull(10, Types.NULL);
					else
						ps.setInt(10, admin.getRowsOk());
					
					if (admin.getRowsKo() == null)
						ps.setNull(11, Types.NULL);
					else
						ps.setInt(11, admin.getRowsKo());
					
					if (admin.getRowsHDE_NF() == null)
						ps.setNull(12, Types.NULL);
					else
						ps.setInt(12, admin.getRowsHDE_NF());
					
					ps.executeUpdate();
					
					ps.close();
				}
			}
			db.disconnect();
		} catch (Exception e) {
			db.disconnect();
			throw e;
		}
	}
}
