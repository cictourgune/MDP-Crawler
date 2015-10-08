package org.tourgune.mdp.amazon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import org.tourgune.mdp.amazon.bean.Product;
import org.tourgune.mdp.amazon.bean.ProductPrice;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.amazon.utils.Utils;
import org.tourgune.mdp.misc.db.QueriesConfig;
import org.tourgune.mdp.misc.db.TablesDB;

public class ProductDao {

	/**
	 * Devuelve el valor que deberíamos poner en el campo 'price_flag', uno de los siguientes:
	 * 		- 2 --> El precio supera el 'threshold'.
	 * 		- 0 --> El precio se encuentra entre 'max_price' y 'threshold', o la consulta no ha devuelto registros.
	 * 		- 1 --> El precio es normal.
	 * 
	 * @param idAccommodation
	 * @param price
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public int checkThreshold(int idAccommodation, double price, Connection con) throws Exception {
		int priceFlag = 1;
		boolean hasNext;
		StringBuffer sql = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				/*
				 * La consulta que hacemos es:
				 * 		SELECT (<query 1>), (<query 2>);
				 * Y nos devolvería dos columnas con un número en cada una, que significarían lo siguiente:
				 * 		+----+----+
				 * 		|  0 |  0 |    -->    1 (precio normal)
				 * 		+----+----+
				 * 		|  1 |  0 |    -->    2 (precio supera threshold)
				 * 		+----+----+
				 * 		|  0 |  1 |    -->    0 (revisión manual)
				 * 		+----+----+
				 *      |NULL|NULL|    -->    0 (no devuelve nada, revisión manual)
				 *      +----+----+
				 */
				sql = new StringBuffer("SELECT (");
				sql.append(QueriesConfig.getInstance().getProperty(Constants.QUERY_ACCOMMODATION_THRESHOLD));
				sql.append("),(");
				sql.append(QueriesConfig.getInstance().getProperty(Constants.QUERY_ACCOMMODATION_REVISION));
				sql.append(")");
				
				ps = con.prepareStatement(sql.toString());
				ps.setInt(1, idAccommodation);
				ps.setInt(3, idAccommodation);
				ps.setDouble(2, price);
				ps.setDouble(4, price);
				ps.setDouble(5, price);
				
				rs = ps.executeQuery();
				hasNext = rs.next();
				
				if (!hasNext)
					priceFlag = 0;
				else if (rs.getInt(1) == 0 && rs.getInt(2) > 0)
					priceFlag = 0;
				else if (rs.getInt(1) > 0 && rs.getInt(2) == 0)
					priceFlag = 2;
				else	// esto significa que los valores devueltos son 0-0 (es imposible que devuelva 1-1)
					priceFlag = 1;
				
				rs.close();
				ps.close();
			} else {
				throw new Exception("[MDP] Database connection was NULL");
			}
		} catch (Exception e) {
			throw e;
		}
		
		return priceFlag;
	}
	
	public int findProduct(Product product, Connection con) throws Exception {
		int intIdProduct = 0;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.FIND_PRODUCT));
				
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, product.getName());
				ps.setString(2, product.getAdultAmount());
				ps.setString(3, product.getChildrenAmount());
				ps.setString(4, product.getBreakfastPrice());
				ps.setBoolean(5, product.isBreakfastIncluded());
				ps.setBoolean(6, product.isHalfBoard());
				ps.setBoolean(7, product.isFullBoard());
				ps.setBoolean(8, product.isAllInclusive());
				ps.setBoolean(9, product.isFreeCancellation());
				ps.setBoolean(10, product.isPayStay());
				ps.setBoolean(11, product.isPayLater());
				ps.setBoolean(12, product.isNonRefundable());
				
				rs = ps.executeQuery();
				if (rs.next())
					intIdProduct = rs.getInt(TablesDB.DP_ID_PRODUCT);
				
				rs.close();
				ps.close();	
				
				return intIdProduct;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int insertProduct(Product product, Connection con) throws Exception {
		
		int intIdProduct = 0;
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_PRODUCT));
				
				ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getName());
				ps.setString(2, product.getAdultAmount());
				ps.setString(3, product.getChildrenAmount());
				ps.setString(4, product.getBreakfastPrice());
				ps.setBoolean(5, product.isBreakfastIncluded());
				ps.setBoolean(6, product.isHalfBoard());
				ps.setBoolean(7, product.isFullBoard());
				ps.setBoolean(8, product.isAllInclusive());
				ps.setBoolean(9, product.isFreeCancellation());
				ps.setBoolean(10, product.isPayStay());
				ps.setBoolean(11, product.isPayLater());
				ps.setBoolean(12, product.isNonRefundable());
				
				/*
				 * No recibe ningún parámetro, ya que si no, ejecuta el método heredado de Statement
				 * y no sustituye los '?'
				 */
				ps.executeUpdate();
				
				rs = ps.getGeneratedKeys();
				if(rs.next())
					intIdProduct = rs.getInt(1);
				
				rs.close();
				ps.close();	
				
				return intIdProduct;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int insertProductPrice(ProductPrice productPrice, Connection con) throws Exception {
		
		int intIdProduct = 0;
		
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (con != null) {
				sql.append(QueriesConfig.getInstance().getProperty(Constants.INSERT_PRODUCT_PRICE));
				
				ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, productPrice.getIdProduct());
				ps.setInt(2, productPrice.getIdAccommodation());
				ps.setInt(3, productPrice.getIdChannel());
				ps.setDate(4, Utils.convert2DbDate(productPrice.getIdBookingDate()));
				ps.setDate(5, Utils.convert2DbDate(productPrice.getIdCheckinDate()));
				ps.setInt(6, productPrice.getLengthOfStay());
				ps.setInt(8, productPrice.getPriceFlag());
				
				if (productPrice.getPrice() == -1)
					ps.setNull(7, Types.NULL);
				else
					ps.setDouble(7, productPrice.getPrice());
				
				/*
				 * No recibe ningún parámetro, ya que si no, ejecuta el método heredado de Statement
				 * y no sustituye los '?'
				 */
				ps.executeUpdate();
				
				rs = ps.getGeneratedKeys();
				if(rs.next())
					intIdProduct = rs.getInt(1);
				
				rs.close();
				ps.close();	
				
				return intIdProduct;
			} else {
				throw new Exception("[MDP] Database connection NULL");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
