package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.tourgune.mdp.amazon.bean.Accommodation;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class AccommodationDao {

	public HashMap<Integer, String> getAvailableAccommodationTypes (Connection con) throws Exception {
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		HashMap<Integer, String> hashAvailableAccommodationTypes = new HashMap<Integer, String>();
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.GET_ACCOMMODATION_TYPES));
				ps = con.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				while (rs.next()) {
					int accType = rs.getInt(TablesDB.DAT_ID_ACCOMMODATION_TYPE);
					String accTypeName = rs.getString(TablesDB.DAT_NAME);
					hashAvailableAccommodationTypes.put(accType, accTypeName);
				}
				rs.close();
				ps.close();	
				
				return hashAvailableAccommodationTypes;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Accommodation> selectAccommodations(String country, String nuts2, String nuts3, String locality, String accommodationType, Connection con) throws Exception {
		
		String rowSql = null, fullSql = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Accommodation> listAccommodations = new ArrayList<Accommodation>();
		List<String> listParams = new ArrayList<String>();
		
		try {
			if (con != null) {
				rowSql = QueriesConfig.getInstance().getProperty(Constants.SELECT_SCRAPPABLE_ACCOMMODATIONS);
				
				if (country.equals("all"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, "");
				else if (country.equals("null"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, " AND dg.country IS NULL ");
				else if (country.equals("notnull"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, " AND dg.country IS NOT NULL ");
				else {
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, " AND dg.country = ?");
					listParams.add(country);
				}
				
				if (nuts2.equals("all"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS2_CONSTRAINT, "");
				else if (nuts2.equals("null"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS2_CONSTRAINT, " AND dg.NUTS2 IS NULL");
				else if (nuts2.equals("notnull"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS2_CONSTRAINT, " AND dg.NUTS2 IS NOT NULL");
				else {
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS2_CONSTRAINT, " AND dg.NUTS2 = ?");
					listParams.add(nuts2);
				}
				
				if (nuts3.equals("all"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS3_CONSTRAINT, "");
				else if (nuts3.equals("null"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS3_CONSTRAINT, " AND dg.NUTS3 IS NULL");
				else if (nuts3.equals("notnull"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS3_CONSTRAINT, " AND dg.NUTS3 IS NOT NULL");
				else {
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_NUTS3_CONSTRAINT, " AND dg.NUTS3 = ?");
					listParams.add(nuts3);
				}
				
				if (locality.equals("all"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_LOCALITY_CONSTRAINT, "");
				else if (locality.equals("null"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_LOCALITY_CONSTRAINT, " AND dg.locality IS NULL");
				else if (locality.equals("notnull"))
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_LOCALITY_CONSTRAINT, " AND dg.locality IS NOT NULL");
				else {
					fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_LOCALITY_CONSTRAINT, " AND dg.locality = ?");
					listParams.add(locality);
				}
				
				fullSql = fullSql.replace(Constants.SELECT_SCRAPPABLE_ACCOMMODATIONTYPE_CONSTRAINT, " AND da.id_accommodation_type = ?");
				listParams.add(accommodationType);
				
				ps = con.prepareStatement(fullSql);
				for (int i = 0; i < listParams.size(); i++)
					ps.setString(i + 1, listParams.get(i));
				rs = ps.executeQuery();
				while (rs.next()) {
					Accommodation accommodation = new Accommodation();
					accommodation.setIdAccommodation(rs.getInt(TablesDB.NMAC_ID_ACCOMMODATION));
					accommodation.setUrlAccommodationChannel(rs.getString(TablesDB.NMAC_URL_ACCOMMODATION_CHANNEL));
					listAccommodations.add(accommodation);
				}
				rs.close();
				ps.close();	
				
				return listAccommodations;
			} else {
				throw new Exception("Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int insertAccommodation(Accommodation accommodation, int idGeography, String accommodationType, Connection con) throws Exception {
		int idAccommodation = 0;
		long curTime = new java.util.Date().getTime();
		QueriesConfig c = QueriesConfig.getInstance();
		String[] sqlStatements = {
				c.getProperty(Constants.INSERT_DAC),
				c.getProperty(Constants.INSERT_NMAC)
		};
		
		try{
			if(con != null){
				PreparedStatement[] ps = {
						con.prepareStatement(sqlStatements[0], Statement.RETURN_GENERATED_KEYS),	// Necesario para poder utilizar getGeneratedKeys()
						con.prepareStatement(sqlStatements[1])
				};
				
				ps[0].setString(1, accommodation.getName());
				ps[0].setInt(2, idGeography);
				ps[0].setInt(3, accommodation.getCategory());
				ps[0].setDouble(4, accommodation.getLatitude());
				ps[0].setDouble(5, accommodation.getLongitude());
				ps[0].setString(6, accommodationType);
				ps[0].setString(7, accommodation.getPostalCode());
				ps[0].setString(8, accommodation.getStreetNumber());
				ps[0].setString(9, accommodation.getRoute());
				ps[0].setString(10, accommodation.getLocality());
				ps[0].setString(11, accommodation.getAdministrativeAreaLevel1());
				ps[0].setString(12, accommodation.getAdministrativeAreaLevel2());
				ps[0].setString(13, accommodation.getAdministrativeAreaLevel3());
				ps[0].setString(14, accommodation.getAdministrativeAreaLevel4());
				ps[0].setString(15, accommodation.getCountry());
				ps[0].setInt(16, accommodation.getLocked());
				
				// Insertamos el accommodation en la primera tabla y obtenemos el ID asociado (auto-increment)
				ps[0].executeUpdate();
				ResultSet rs = ps[0].getGeneratedKeys();
				if(rs.next())
					idAccommodation = rs.getInt(1);
				
				// Ahora que ya tenemos el ID que le han asignado en la primera tabla, lo insertamos en la segunda
				ps[1].setInt(1, idAccommodation);
				ps[1].setString(2, accommodation.getIdAccommodationChannel());
				ps[1].setString(3, accommodation.getUrlAccommodationChannel());
				ps[1].setDate(4, new java.sql.Date(curTime));	// first_seen
				ps[1].setDate(5, new java.sql.Date(curTime));	// last_seen
				
				ps[1].executeUpdate();
				
				ps[0].close();
				ps[1].close();
			}else
				throw new Exception("Database connection NULL");
		}catch(SQLException e){
			throw e;
		}
		
		return idAccommodation;
	}
	
	/**
	 * Busca un accommodation por su 'id_accommodation_channel'.
	 * 
	 * @param accommodationIdChannel	'id_accommodation_channel' del accommodation que se quiere buscar
	 * @param con	Conexión con la base de datos
	 * @return	El accommodation buscado, o null si no existe
	 * @throws Exception
	 */
	public Accommodation findAccommodation(String accommodationIdChannel, Connection con) throws Exception {
		Accommodation accommodation = null;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.FIND_ACCOMMODATION_ID_CHANNEL));
				
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, accommodationIdChannel);
				
				rs = ps.executeQuery();
				if (rs.next()) {
					accommodation = new Accommodation();
					accommodation.setIdAccommodation(rs.getInt(TablesDB.NMAC_ID_ACCOMMODATION));
					accommodation.setUrlAccommodationChannel(rs.getString(TablesDB.NMAC_URL_ACCOMMODATION_CHANNEL));
					accommodation.setIdAccommodationChannel(rs.getString(TablesDB.NMAC_ID_ACCOMMODATION_CHANNEL));
					accommodation.setName(rs.getString(TablesDB.DAC_NAME));
					accommodation.setCategory(rs.getInt(TablesDB.DAC_CATEGORY));
					accommodation.setLatitude(rs.getDouble(TablesDB.DAC_LATITUDE));
					accommodation.setLongitude(rs.getDouble(TablesDB.DAC_LONGITUDE));
					accommodation.setPostalCode(rs.getString(TablesDB.DAC_POSTALCODE));
					accommodation.setStreetNumber(rs.getString(TablesDB.DAC_STREETNUMBER));
					accommodation.setRoute(rs.getString(TablesDB.DAC_ROUTE));
					accommodation.setLocality(rs.getString(TablesDB.DAC_LOCALITY));
					accommodation.setCountry(rs.getString(TablesDB.DAC_COUNTRY));
					accommodation.setAdministrativeAreaLevel1(rs.getString(TablesDB.DAC_AAL1));
					accommodation.setAdministrativeAreaLevel2(rs.getString(TablesDB.DAC_AAL2));
					accommodation.setAdministrativeAreaLevel3(rs.getString(TablesDB.DAC_AAL3));
					accommodation.setAdministrativeAreaLevel4(rs.getString(TablesDB.DAC_AAL4));
					accommodation.setLocked(rs.getInt(TablesDB.DAC_LOCKED));
				}
				
				rs.close();
				ps.close();	
				
				return accommodation;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public int findIdGeography(int regionId, int idChannel, Connection con) throws Exception {
		int idGeography = 0;
		PreparedStatement ps = null;
		
		try{
			if(con != null){
				ps = con.prepareStatement(QueriesConfig.getInstance().getProperty(Constants.FIND_ACCOMMODATION_GEOGRAPHY_CHANNEL));
//				ps.setInt(1, idChannel);
				ps.setInt(1, regionId);
				
				ResultSet rs = ps.executeQuery();
				if(rs.next())
					idGeography = rs.getInt(1);
				
				rs.close();
				ps.close();
			}
		}catch(SQLException e){
			throw (Exception) e;
		}
		
		return idGeography;
	}

	public void updateAccommodation(int idAccommodation, int idGeography, String idAccommodationType, Accommodation accommodation, Connection con) throws Exception {
		PreparedStatement ps = null;
		
		try {
			if (con != null) {
				ps = con.prepareStatement(QueriesConfig.getInstance().getProperty(Constants.UPDATE_ACCOMMODATION));
				ps.setString(1, accommodation.getName());
				ps.setInt(2, idGeography);
				ps.setInt(3, accommodation.getCategory());
				ps.setDouble(4, accommodation.getLatitude());
				ps.setDouble(5, accommodation.getLongitude());
				ps.setString(6, idAccommodationType);
				ps.setString(7, accommodation.getCountry());
				ps.setString(8, accommodation.getAdministrativeAreaLevel1());
				ps.setString(9, accommodation.getAdministrativeAreaLevel2());
				ps.setString(10, accommodation.getAdministrativeAreaLevel3());
				ps.setString(11, accommodation.getAdministrativeAreaLevel4());
				ps.setString(12, accommodation.getLocality());
				ps.setString(13, accommodation.getRoute());
				ps.setString(14, accommodation.getStreetNumber());
				ps.setString(15, accommodation.getPostalCode());
				ps.setString(16, accommodation.getIdAccommodationChannel());
				ps.setString(17, accommodation.getUrlAccommodationChannel());
				ps.setDate(18, new java.sql.Date(new java.util.Date().getTime()));
				ps.setInt(19, idAccommodation);
				
				ps.executeUpdate();
				
				ps.close();
			}
		} catch (SQLException e) {
			throw (Exception) e;
		}
	}
	
	public void updateAccommodationLastSeen(int idAccommodation, Connection con) throws Exception {
		PreparedStatement ps = null;
		
		try {
			if (con != null) {
				ps = con.prepareStatement(QueriesConfig.getInstance().getProperty(Constants.UPDATE_ACCOMMODATION_LAST_SEEN));
				ps.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
				ps.setInt(2, idAccommodation);
				
				ps.executeUpdate();
				
				ps.close();
			}
		} catch (SQLException e) {
			throw e;
		}
	}
}