package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.tourgune.mdp.amazon.bean.Services;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.amazon.utils.Utils;
import org.tourgune.mdp.misc.db.QueriesConfig;

public class ServicesDao {
	
	public boolean servicesRecorded (Services services, Connection con) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		
		try{
			if(con != null){
				String sql = QueriesConfig.getInstance().getProperty(Constants.FIND_SERVICES_ENTRY);
				
				ps = con.prepareStatement(sql);
				ps.setInt(1, services.getIdAccommodation());
				ps.setBoolean(2, services.isFreeWifi());
				ps.setBoolean(3, services.isFreeParking());
				ps.setBoolean(4, services.isPetsAllowed());
				ps.setString(5, services.getActivities());
				
				ResultSet rs = ps.executeQuery();
				result = rs.next();
				
				ps.close();
			}else
				throw new Exception ("[MDP] Database connection NULL");
		}catch(SQLException e){
			throw (Exception) e;
		}
		
		return result;
	}
	
	public void insertServices (Services services, Connection con) throws Exception {
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		
		try {
			if (con != null) {
				int idAccommodation = services.getIdAccommodation();
				Date scrapingDate = services.getScrapingDate();
				boolean freeWifi = services.isFreeWifi();
				boolean freeParking = services.isFreeParking();
				boolean petsAllowed = services.isPetsAllowed();
				String activities = services.getActivities();
				
				
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_SERVICES));
				
				ps = con.prepareStatement(sql.toString());
				ps.setInt(1, idAccommodation);
				ps.setDate(2, Utils.convert2DbDate(scrapingDate));
				ps.setBoolean(3, freeWifi);
				ps.setBoolean(4, freeParking);
				ps.setBoolean(5, petsAllowed);
				ps.setString(6, activities);

				/*
				 * No recibe ningún parámetro, ya que si no, ejecuta el método heredado de Statement
				 * y no sustituye los '?'
				 */
				ps.executeUpdate();
								
				ps.close();	

			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
}
