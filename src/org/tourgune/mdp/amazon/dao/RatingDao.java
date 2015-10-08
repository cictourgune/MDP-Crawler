package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.tourgune.mdp.amazon.bean.Rating;
import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.amazon.utils.Utils;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class RatingDao {
	
	public int findIdSegment(Rating rating, Connection con) throws Exception {
		
		int intIdSegment = 0;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.FIND_RATING_VISITOR_SEGMENT_ID));
				
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, rating.getSegment().toLowerCase());
				
				rs = ps.executeQuery();
				if (rs.next())
					intIdSegment = rs.getInt(TablesDB.DVS_ID_VISITOR_SEGMENT);
				
				rs.close();
				ps.close();	
				
				return intIdSegment;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int insertVisitorSegment(Rating rating, Connection con) throws Exception {
		
		int intIdSegment = 0;
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_RATING_VISITOR_SEGMENT));
				
				ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, rating.getSegment().toLowerCase());
				
				/*
				 * No recibe ningún parámetro, ya que si no, ejecuta el método heredado de Statement
				 * y no sustituye los '?'
				 */
				ps.executeUpdate();
				
				rs = ps.getGeneratedKeys();
				if(rs.next())
					intIdSegment = rs.getInt(1);
				
				rs.close();
				ps.close();	
				
				return intIdSegment;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int insertRatings (Rating rating, Connection con) throws Exception {
		AmazonConfig config = AmazonConfig.getInstance();
		QueriesConfig queriesConfig = QueriesConfig.getInstance();
		
		PreparedStatement ps = null;
		List<Float> valueList = new ArrayList<Float>();
		List<String> nameList = new ArrayList<String>();
		Iterator<Float> valuesIt = null;
		Iterator<String> namesIt = null;
		StringBuilder names = new StringBuilder(queriesConfig.getProperty(Constants.INSERT_RATING) +
				config.getProperty(Constants.BS_CONSTANT_FIELD_ID_ACCOMMODATION) + ", " +
				config.getProperty(Constants.BS_CONSTANT_FIELD_ID_SCRAPING_DATE) + ", " +
				config.getProperty(Constants.BS_CONSTANT_FIELD_ID_VISITOR_SEGMENT) + ", "),
				values = new StringBuilder(" VALUES (?, ?, ?, ");
		int i = 0;
		int affectedRows = 0;
		
		try {
			if (con != null) {
				int idAccommodation = rating.getIdAccommodation();
				Date scrapingDate = rating.getScrapingDate();
				int idSegment = rating.getIdSegment();
				float avg = 0;
				
				if (rating.getComfort() >= 0) {
					valueList.add(new Float(rating.getComfort()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_COMFORT));
				}
				
				if (rating.getValue() >= 0) {
					valueList.add(new Float(rating.getValue()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_VALUE));
				}
				
				if (rating.getLocation() >= 0) {
					valueList.add(new Float(rating.getLocation()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_LOCATION));
				}
				
				if (rating.getClean() >= 0) {
					valueList.add(new Float(rating.getClean()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_CLEAN));
				}
				
				if (rating.getServices() >= 0) {
					valueList.add(new Float(rating.getServices()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_SERVICES));
				}
				
				if (rating.getStaff() >= 0) {
					valueList.add(new Float(rating.getStaff()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_STAFF));
				}
				
				if (rating.getWifi() > 0) {
					/*
					 * Hay que tener cuidado con el WiFi. Hay veces en booking que el rating WiFi aparece únicamente en algunos
					 * segmentos de usuario. En estos casos, en los segmentos en los que no aparece, booking no lo tiene en cuenta
					 * para calcular la media, sin embargo, lo marca como wifi=0.
					 * Parece que es un bug de booking, pero si ellos no lo tienen en cuenta, nosotros tampoco.
					 */
					valueList.add(new Float(rating.getWifi()));
					nameList.add(config.getProperty(Constants.BS_CONSTANT_RATING_WIFI));
				}
				
				if (valueList.size() > 0) {
					for (float value : valueList)
						avg += value;
					avg /= valueList.size();
				}
				
				namesIt = nameList.iterator();
				valuesIt = valueList.iterator();
				while(namesIt.hasNext() && valuesIt.hasNext()) {
					names.append(namesIt.next() + ", ");
					values.append("?, ");
				}
				names.append(config.getProperty(Constants.BS_CONSTANT_RATING_AVG) +
					", " +
					config.getProperty(Constants.BS_CONSTANT_RATING_TTL_USERS) + ")");
				values.append("?, ?)");
				
				ps = con.prepareStatement(names.toString() + values.toString());
				
				// Establecemos primero los datos fijos
				ps.setInt(1, idAccommodation);
				ps.setDate(2, Utils.convert2DbDate(scrapingDate));
				ps.setInt(3, idSegment);
				
				// Ahora los datos variables
				for (i = 0; i < valueList.size(); i++)
					ps.setFloat((i + 3 + 1), valueList.get(i));
				
				// Y por último, la media (avg) y la cantidad de usuarios (ttl_users)
				ps.setFloat((i + 3 + 1), avg);
				ps.setInt((i + 3 + 2), rating.getTtlUsers());
				
				/*
				 * No recibe ningún parámetro, ya que si no, ejecuta el método heredado de Statement
				 * y no sustituye los '?'
				 */
				affectedRows = ps.executeUpdate();
								
				ps.close();	

			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
		
		return affectedRows;
	}
	
}
