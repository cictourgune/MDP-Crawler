package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;

import org.tourgune.mdp.amazon.bean.Review;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.amazon.utils.Utils;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class ReviewDao {
	
	public java.sql.Date getLatestReviewDate(int idAccommodation, Connection con) throws Exception {
		java.sql.Date date = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			if(con != null) {
				String sql = QueriesConfig.getInstance().getProperty(Constants.FIND_LATEST_REVIEW_DATE);
				ps = con.prepareStatement(sql);
				
				ps.setInt(1, idAccommodation);
				
				rs = ps.executeQuery();
				if(rs.next())
					date = rs.getDate(1);
			}else{
				throw new Exception("[MDP] Database connection NULL");
			}
		}catch(Exception e){
			throw e;
		}
		
		return date;
	}
	
	public int findIdSegment(Review review, Connection con) throws Exception {
		
		int intIdSegment = 0;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.FIND_REVIEW_VISITOR_SEGMENT_ID));
				
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, review.getSegment().toLowerCase());
//				ps.setString(2, review.getSubSegment().toLowerCase());
				
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
	
	public int insertVisitorSegment(Review review, Connection con) throws Exception {
		
		int intIdSegment = 0;
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_REVIEW_VISITOR_SEGMENT));
				
				ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, review.getSegment().toLowerCase());
//				ps.setString(2, review.getSubSegment().toLowerCase());
				
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
	
	public void insertReview (Review review, Connection con) throws Exception {
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		
		try {
			if (con != null) {
				int idAccommodation = review.getIdAccommodation();
				String typeTrip = review.getTypeTrip();
				int idSegment = review.getIdSegment();
				String typeRoom = review.getTypeRoom();
				Integer stayNights = review.getStayNights();
				String withPet = review.getWithPet();
				Date scrapingDate = review.getScrapingDate();
				String from = review.getFrom();
				Date reviewDate = review.getDate();
				String lang = review.getLang();
				String reviewGood = review.getReviewGood();
				String reviewBad = review.getReviewBad();
				float score = review.getScore();
				
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_REVIEW));
				ps = con.prepareStatement(sql.toString());
				ps.setInt(1, idAccommodation);
				ps.setDate(2, Utils.convert2DbDate(scrapingDate));
				ps.setInt(3, idSegment);
				if (typeTrip == null || typeTrip.isEmpty())
					ps.setNull(4, Types.VARCHAR);
				else
					ps.setString(4,  typeTrip);
				if (typeRoom == null || typeRoom.isEmpty())
					ps.setNull(5, Types.VARCHAR);
				else
					ps.setString(5,  typeRoom);
				if (stayNights == null)
					ps.setNull(6, Types.VARCHAR);
				else
					ps.setInt(6,  stayNights);
				if (withPet == null || withPet.isEmpty())
					ps.setNull(7, Types.VARCHAR);
				else
					ps.setString(7,  withPet);
				ps.setString(8, from);
				ps.setDate(9, Utils.convert2DbDate(reviewDate));
				
				if (lang == null || lang.equals("unknown"))
					ps.setNull(10, Types.VARCHAR);
				else
					ps.setString(10, lang.substring(0, 5));
				
				if (reviewGood == null || reviewGood.isEmpty())
					ps.setNull(11, Types.VARCHAR);
				else
					ps.setString(11, reviewGood);
				
				if (reviewBad == null || reviewBad.isEmpty())
					ps.setNull(12, Types.VARCHAR);
				else
					ps.setString(12, reviewBad);
				ps.setFloat(13, score);
				
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
