package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.tourgune.mdp.amazon.bean.Region;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class RegionDao {

	public List<Region> selectRegions(Connection con, String country, String nuts2, String nuts3, String locality) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Region> listRegions = new ArrayList<Region>();
		List<String> listParams = new ArrayList<String>();
		
		try {
			if (con != null) {
				String rowSql = QueriesConfig.getInstance().getProperty(Constants.SELECT_SCRAPPABLE_REGIONS);
				String fullSql = rowSql;
				
				if (country.equals("all"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, "");
				else if (country.equals("null"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, " AND dg.country IS NULL");
				else if (country.equals("notnull"))
					fullSql = rowSql.replace(Constants.SELECT_SCRAPPABLE_COUNTRY_CONSTRAINT, " AND dg.country IS NOT NULL");
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
				
				
				ps = con.prepareStatement(fullSql);
				for (int i = 0; i < listParams.size(); i++)
					ps.setString(i + 1, listParams.get(i));
				rs = ps.executeQuery();
				while (rs.next()) {
					Region dbRegion = new Region();
					dbRegion.setIdGeography(rs.getInt(TablesDB.DG_ID_GEOGRAPHY));
					dbRegion.setIdGeographyChannel(rs.getInt(TablesDB.DG_ID_GEOGRAPHY_CHANNEL));
					listRegions.add(dbRegion);
				}
				rs.close();
				ps.close();	
				
				return listRegions;
			} else {
				throw new Exception("Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}