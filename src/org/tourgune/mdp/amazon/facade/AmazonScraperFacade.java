package org.tourgune.mdp.amazon.facade;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.amazon.bean.Accommodation;
import org.tourgune.mdp.amazon.bean.Product;
import org.tourgune.mdp.amazon.bean.ProductPrice;
import org.tourgune.mdp.amazon.bean.Rating;
import org.tourgune.mdp.amazon.bean.Region;
import org.tourgune.mdp.amazon.bean.Review;
import org.tourgune.mdp.amazon.bean.Services;
import org.tourgune.mdp.amazon.dao.AccommodationDao;
import org.tourgune.mdp.amazon.dao.DatabaseDao;
import org.tourgune.mdp.amazon.dao.ProductDao;
import org.tourgune.mdp.amazon.dao.RatingDao;
import org.tourgune.mdp.amazon.dao.RegionDao;
import org.tourgune.mdp.amazon.dao.ReviewDao;
import org.tourgune.mdp.amazon.dao.ServicesDao;
import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.latlon2geo.main.LatLon2Geo;
import org.tourgune.mdp.misc.db.Database;

public class AmazonScraperFacade {
	
	private static AmazonScraperFacade instance = null;
	private AmazonConfig configClass = AmazonConfig.getInstance();
//	private boolean available = false;
	
	private AmazonScraperFacade() { }
	
	public static synchronized AmazonScraperFacade getInstance() {
		if (instance == null) {
			instance = new AmazonScraperFacade();
		}
		return instance;
	}
	
	public double getDbSize(Logger logger) {
		DatabaseDao dbDao = new DatabaseDao();
		double dbSize = -1;
		Database db = new Database();		
		
		try {
			Connection con;
			con = db.connect();
			
			if (con != null) {
				dbSize = dbDao.getDbSize(con);
			}
		} catch (Exception e) {
			logger.log("EXCEPTION: Problems getting database size", "AmazonScraperFacade", LogPriority.ERROR);
		}
		try {
			db.disconnect();
		} catch (Exception dbExc) {
			logger.log("EXCEPTION: Problem trying to disconnect from database", LogPriority.ERROR);
		}
		return dbSize;
	}
	
	public HashMap<Integer, String> getAvailableAccommodationTypes (Logger logger) {
		AccommodationDao accommodationDao = new AccommodationDao();
		Database db = new Database();
		
		HashMap<Integer, String> hashAvailableAccommodationTypes = new HashMap<Integer, String>();
		
		try {
			Connection con;
			con = db.connect();
			if (con != null) {
				hashAvailableAccommodationTypes = accommodationDao.getAvailableAccommodationTypes(con);
			}
		} catch (Exception e) {
			logger.log("EXCEPTION: Problems getting available accommodation types from d_accommodation_type", "AmazonScraperFacade", LogPriority.ERROR);
		}
		try {
			db.disconnect();
		} catch (Exception dbExc) {
			logger.log("EXCEPTION: Problem trying to disconnect from database", LogPriority.ERROR);
		}
		return hashAvailableAccommodationTypes;
	}
	
	public void insertPricesHDE (JSONArray response, Connection con, int regionId, String idAccommodationType, Logger logger) throws Exception{
		ProductDao productDao = new ProductDao();
		AccommodationDao accommodationDao = new AccommodationDao();
		Accommodation dbAccommodation = null, curAccommodation = null;
		int idGeography = 0, idAccommodation = 0, priceFlag = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		logger.log(Thread.currentThread().getName() + " INICIO insertPricesHDE");
		try {
			JSONArray jsonArray = response;
			
			for(int objIndex = 0; objIndex < jsonArray.length(); objIndex++){
			
				JSONObject jsonObj = jsonArray.getJSONObject(objIndex);
				JSONArray dataArray = jsonObj.getJSONArray(configClass.getProperty(Constants.BS_CONSTANT_DATA));
			
				for(int hdePriceIndex = 0; hdePriceIndex < dataArray.length(); hdePriceIndex++) {
					// 1. Iniciamos transacción
					con.setAutoCommit(false);
					
					JSONObject jsonHDEPrice = dataArray.getJSONObject(hdePriceIndex);
					ProductPrice productPrice = new ProductPrice();
					
					try {
						// 1. Recogemos los valores del JSON
						String accommodationIdChannel = jsonHDEPrice.getString(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONIDCHANNEL));
						
						double hdePrice = jsonHDEPrice.getDouble(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_HDEPRICE));
						
						String offerDate = jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
								.getString(configClass.getProperty(Constants.BS_CONSTANT_OFFERDATE));
						String inDate = jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
								.getString(configClass.getProperty(Constants.BS_CONSTANT_INDATE));
						
						curAccommodation = new Accommodation();
						curAccommodation.setName(jsonHDEPrice.getString(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONNAME)));
						curAccommodation.setCategory(jsonHDEPrice.getInt(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_CATEGORY)));
						curAccommodation.setLatitude(jsonHDEPrice.getDouble(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_LATITUDE)));
						curAccommodation.setLongitude(jsonHDEPrice.getDouble(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_LONGITUDE)));
						curAccommodation.setIdAccommodationChannel(accommodationIdChannel);
						curAccommodation.setUrlAccommodationChannel(jsonHDEPrice.getString(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_URL)));
						
						// 2. Buscamos el identificador del accommodation en base al que nos ha dado Booking
						dbAccommodation = accommodationDao.findAccommodation(accommodationIdChannel, con);
						if (dbAccommodation != null && dbAccommodation.getLocked() == 1) {
							// Si está a 1 el valor de locked, quiere decir que se ha tocado externamente con lo que no se puede modificar
							idAccommodation = dbAccommodation.getIdAccommodation();
						} else {
							if(dbAccommodation == null || !dbAccommodation.equals(curAccommodation)){
								if(dbAccommodation == null || (dbAccommodation.getLatitude() != curAccommodation.getLatitude()) || (dbAccommodation.getLongitude() != curAccommodation.getLongitude())) {
									/*
									 * Si las coordenadas del accommodation han cambiado, o si se trata de un nuevo accommodation, pedimos a Geocoder que nos dé información 
									 * sobre la ubicación de este accommodation.
									 * En caso contrario no es necesario, ya que suponemos que los nombres de las calles, código postal, etc. no han cambiado.
									 * Las peticiones a Geocoder son bastante rápidas, por lo que no sería muy costoso hacer la petición siempre.
									 * El problema es que la cantidad de peticiones que podemos hacer por día está limitada, y si malgastamos las peticiones
									 * con accommodations cuyas coordenadas no han cambiado, quizás ya no podamos hacer más peticiones para accommodations nuevos, u accommodations que
									 * sí que han cambiado de ubicación.
									 */
									Map<String, String> geoInfo = LatLon2Geo.getGeoInfo(Double.toString(curAccommodation.getLatitude()), Double.toString(curAccommodation.getLongitude()));
									
									if(geoInfo != null){
										// Puede que no estén todos los campos, pero en ese caso, el Map nos devolverá null y en la base de datos se registrará un null
										curAccommodation.setCountry(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_COUNTRY)));
										curAccommodation.setPostalCode(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_POSTAL_CODE)));
										curAccommodation.setStreetNumber(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_STREET_NUMBER)));
										curAccommodation.setRoute(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_ROUTE)));
										curAccommodation.setLocality(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_LOCALITY)));
										curAccommodation.setAdministrativeAreaLevel1(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_AAL1)));
										curAccommodation.setAdministrativeAreaLevel2(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_AAL2)));
										curAccommodation.setAdministrativeAreaLevel3(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_AAL3)));
										curAccommodation.setAdministrativeAreaLevel4(geoInfo.get(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATION_AAL4)));
									}
								} else {
									curAccommodation.setCountry(dbAccommodation.getCountry());
									curAccommodation.setPostalCode(dbAccommodation.getPostalCode());
									curAccommodation.setStreetNumber(dbAccommodation.getStreetNumber());
									curAccommodation.setRoute(dbAccommodation.getRoute());
									curAccommodation.setLocality(dbAccommodation.getLocality());
									curAccommodation.setAdministrativeAreaLevel1(dbAccommodation.getAdministrativeAreaLevel1());
									curAccommodation.setAdministrativeAreaLevel2(dbAccommodation.getAdministrativeAreaLevel2());
									curAccommodation.setAdministrativeAreaLevel3(dbAccommodation.getAdministrativeAreaLevel3());
									curAccommodation.setAdministrativeAreaLevel4(dbAccommodation.getAdministrativeAreaLevel4());
								}
								
								// Necesitamos obtener el 'id_geography' del accommodation antes de nada
								idGeography = accommodationDao.findIdGeography(
										regionId,																// id_geography_channel
										Integer.parseInt(configClass.getProperty(Constants.BOOKING_CHANNEL)),	// id_channel
										con);
								try {
									if (dbAccommodation == null) {
										// Este accommodation no existe en la BD. Vamos a introducirlo.
										idAccommodation = accommodationDao.insertAccommodation(curAccommodation, idGeography, idAccommodationType, con);
									} else {
										// El accommodation existe pero sus datos han cambiado. Vamos a actualizarlo.
										accommodationDao.updateAccommodation(dbAccommodation.getIdAccommodation(), idGeography, idAccommodationType, curAccommodation, con);
										idAccommodation = dbAccommodation.getIdAccommodation();
									}
								} catch (SQLException se) {
									logger.print("EXCEPTION 'SQLException' during INSERT/UPDATE 'd_accommodation': " + se.getMessage());
									logger.push("AmazonScraperFacade.insertPricesHDE()", LogPriority.ERROR);
									con.rollback();
									
									continue; // Siguiente iteración del bucle
								}
								
							}else{
								try {
									idAccommodation = dbAccommodation.getIdAccommodation();
									accommodationDao.updateAccommodationLastSeen(idAccommodation, con);
								} catch (SQLException se) {
									logger.print("EXCEPTION 'SQLException' during UPDATE 'd_accommodation.last_seen': " + se.getMessage());
									logger.push("AmazonScraperFacade.insertPricesHDE()", LogPriority.ERROR);
									con.rollback();
									
									continue; // Siguiente iteración del bucle
								}
								
							}
						}
						curAccommodation.setIdAccommodation(idAccommodation);
						
						// 3.- Preparamos el bean para su inserción
						productPrice.setIdProduct(new Integer(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_PRODUCTID))); // Ponemos el producto HDE como el 0
						productPrice.setIdAccommodation(curAccommodation.getIdAccommodation());
						productPrice.setIdChannel(new Integer(configClass.getProperty(Constants.BOOKING_CHANNEL))); // Ponemos el canal al de booking (== 1)
						productPrice.setIdBookingDate(sdf.parse(offerDate));
						productPrice.setIdCheckinDate(sdf.parse(inDate));
						productPrice.setLengthOfStay(new Integer(configClass.getProperty(Constants.LENGTH_OF_STAY)));
						
						if (hdePrice == -1) {
							productPrice.setPrice(-1);
							productPrice.setPriceFlag(-1);
						} else if (hdePrice == 0) {
							productPrice.setPrice(-1);
							productPrice.setPriceFlag(-2);
						} else {
							productPrice.setPrice((float) hdePrice);
							productPrice.setPriceFlag(
									productDao.checkThreshold(productPrice.getIdAccommodation(), hdePrice, con)
									);
						}
						
						// 4. Insertamos el producto
						productDao.insertProductPrice(productPrice, con);
						
						curAccommodation = null;
					} catch (SQLException e) {
						logger.print("EXCEPTION 'SQLException' during INSERT into 'ft_product_price': " + e.getMessage());
						logger.newline().print("Data: id = " + productPrice.getIdProduct() + ", inDate = " + productPrice.getIdCheckinDate() + ", price = " + productPrice.getPrice());
						//logger.newline().print("Accommodation ID: " + jsonObj.getJSONObject("task").getString("hotelid"));
						logger.push("AmazonScraperFacade.insertPricesHDE()", LogPriority.ERROR);
						con.rollback();
						
						continue; // Siguiente iteración del bucle
					} catch (JSONException e) {
						logger.log("EXCEPTION 'Exception' parsing JSONObject: " + e.getMessage());
						con.rollback();
					} catch (Exception e) {
						logger.log("EXCEPTION Unexpected Exception: " + e.getMessage());
						con.rollback();
					}
					
					// 4. Hacemos commit explícito
					con.commit();
				}
			}
		} catch (Exception e){
			throw e;
		}
		logger.log(Thread.currentThread().getName() + " FIN insertPricesHDE");
	}
	
	public Date getLatestReviewDate(Connection con, int idAccommodation, Logger logger) {
		Date date = null;
		ReviewDao reviewDao = new ReviewDao();
		
		try {
			date = (java.util.Date) reviewDao.getLatestReviewDate(idAccommodation, con);
		} catch (Exception e) {
			logger.log("EXCEPTION: Could not retrieve latest review date: " + e.getMessage());
		}
		
		return date;
	}
	
	public void insertProducts(JSONArray response, Connection con, int intIdAccommodation, Logger logger) throws Exception{
		ProductDao productDao = new ProductDao();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
//		while (!available) { 	//Esperar hasta que se pueda usar el método
//			try { 
//				wait(); 
//			} catch (InterruptedException e) {
//				throw e;
//			}
//		}
		logger.log(Thread.currentThread().getName() + " INICIO insertProducts");
		try {
			JSONArray jsonArray = response;
			
			for(int objIndex = 0; objIndex < jsonArray.length(); objIndex++){
			
				JSONObject jsonObj = jsonArray.getJSONObject(objIndex);
				JSONArray dataArray = jsonObj.getJSONArray(configClass.getProperty(Constants.BS_CONSTANT_DATA));
			
				for(int prodIndex = 0; prodIndex < dataArray.length(); prodIndex++) {
					// 1. Iniciamos transacción
					con.setAutoCommit(false);
					
					try {
						JSONObject jsonProduct = null; 
						jsonProduct = dataArray.getJSONObject(prodIndex);
//						logger.log("\tJson product data: " + jsonProduct.toString(), LogPriority.INFO);
						Product product = new Product();
						int intIdProduct = 0;
						
						product.setName(jsonProduct.getString(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_NAME)));
						product.setAdultAmount(jsonProduct.getString(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_OCCUPANCY)));
						product.setChildrenAmount(jsonProduct.getString(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_CHILD)));
						product.setBreakfastPrice(jsonProduct.getString(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_BREAKFASTPRICE)));
						product.setBreakfastIncluded(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_BREAKFAST)));
						product.setHalfBoard(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_HALF)));
						product.setFullBoard(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_FULL)));
						product.setAllInclusive(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_ALL)));
						product.setFreeCancellation(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_CANCELLATION)));
						product.setPayStay(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PAYSTAY)));
						product.setPayLater(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PAYLATER)));
						product.setNonRefundable(jsonProduct.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_NONRFUNDABLE)));
						
						// 2. Buscamos si el producto ya existe
						try{

							intIdProduct = productDao.findProduct(product, con);
							
						}catch(SQLException e){
							logger.log("EXCEPTION 'SQLException' during SELECT to 'd_product': " + e.getMessage(), "AmazonScraperFacade.insertAccommodation()", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
						
						// 2b. Si no existe el producto, lo insertamos en la tabla "d_product" y obtenemos su IDENTIFICADOR
						try {
							if(intIdProduct == 0)
								intIdProduct = productDao.insertProduct(product, con);
						} catch(SQLException e){
							logger.print("EXCEPTION 'SQLException' during INSERT into 'd_product': " + e.getMessage());
							logger.newline().print("Data: name = " + product.getName() + ", adult_amount = " + product.getAdultAmount() + ", children_amount = " + product.getChildrenAmount() + 
									", breakfast_price = " + product.getBreakfastPrice() + ", breakfast_included = " + product.isBreakfastIncluded() +	", half-board = " + product.isHalfBoard() + 
									", full-board = " + product.isFullBoard() + ", all-inclusive = " + product.isAllInclusive() + ", free_cancellation = " + product.isFreeCancellation() +
									"pay_stay = " + product.isPayStay() + ", pay_later = " + product.isPayLater() + ", non_refundable = " + product.isNonRefundable());
							logger.push("AmazonScraperFacade.insertAccommodation(" + jsonObj.getJSONObject("task").getString("hotelid") + ")", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
						
						// 3. Insertamos el precio asociado al producto en la tabla "ft_product_price"
						try {
							String offerDate = jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
									.getString(configClass.getProperty(Constants.BS_CONSTANT_OFFERDATE)).split("T")[0];
							String inDate = jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
									.getString(configClass.getProperty(Constants.BS_CONSTANT_INDATE)).split("T")[0];
							double price = jsonProduct.getDouble(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PRICE));
							
							ProductPrice productPrice = new ProductPrice();
							productPrice.setIdProduct(intIdProduct);
							productPrice.setIdAccommodation(intIdAccommodation);
							productPrice.setIdChannel(new Integer(configClass.getProperty(Constants.BOOKING_CHANNEL)));
							productPrice.setIdBookingDate(sdf.parse(offerDate));
							productPrice.setIdCheckinDate(sdf.parse(inDate));
							productPrice.setLengthOfStay(new Integer(configClass.getProperty(Constants.LENGTH_OF_STAY)));
							productPrice.setPrice((float) price);
							try {
								productDao.insertProductPrice(productPrice, con);
							} catch (SQLException e) {
								logger.print("EXCEPTION 'SQLException' during INSERT into 'ft_product_price': " + e.getMessage());
								logger.newline().print("Data: id = " + productPrice.getIdProduct() + ", inDate = " + productPrice.getIdCheckinDate() + ", price = " + productPrice.getPrice());
								logger.newline().print("Accommodation ID: " + jsonObj.getJSONObject("task").getString("hotelid"));
								logger.push("AmazonScraperFacade.insertAccommodation()", LogPriority.ERROR);
								con.rollback();
								continue; // Siguiente iteración del bucle
							}
						} catch (NumberFormatException e) {
							logger.log("EXCEPTION 'NumberFormatException': 'BOOKING_CHANNEL' and 'LENGTH_OF_STAY' must both be integer numbers.", LogPriority.ERROR);
							con.rollback();
							// Los parámetros de configuración están mal, así que salimos
							break;
						} catch (JSONException e) {
							logger.log("EXCEPTION 'JSONException': " + e.getMessage(), LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
					} catch (Exception e) {
						logger.log("EXCEPTION 'Exception' parsing JSONObject: " + e.getMessage());
						con.rollback();
					}	
					
					// 4. Hacemos commit explícito
					con.commit();
				}
			}
//			notify();
		} catch (Exception e){
			throw e;
		}
		logger.log(Thread.currentThread().getName() + " FIN insertProducts");
	}
	
	public void insertServices (JSONArray response, Connection con, int intIdAccommodation, Logger logger) throws Exception{
		ServicesDao servicesDao = new ServicesDao();
		
		Date today = new Date();
		
		
//		while (!available) { 	//Esperar hasta que se pueda usar el método
//		try { 
//			wait(); 
//		} catch (InterruptedException e) {
//			throw e;
//		}
//	}
		logger.log(Thread.currentThread().getName() + " INICIO insertServices");
		try {
			JSONArray jsonArray = response;
			
			for(int objIndex = 0; objIndex < jsonArray.length(); objIndex++){
			
				JSONObject jsonObj = jsonArray.getJSONObject(objIndex);
				JSONArray dataArray = jsonObj.getJSONArray(configClass.getProperty(Constants.BS_CONSTANT_DATA));
			
				for(int serviceIndex = 0; serviceIndex < dataArray.length(); serviceIndex++) {
					// 1. Iniciamos transacción
					con.setAutoCommit(false);
					
					try {
						JSONObject jsonService = dataArray.getJSONObject(serviceIndex);
						
						Services services = new Services();
						
						boolean freeWifi = jsonService.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_FREEWIFI));
						boolean freeParking = jsonService.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_FREEPARKING));
						boolean petsAllowed = jsonService.getBoolean(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_PETSALLOWED));
						String activities = jsonService.getString(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_ACTIVITIES));
						
						services.setIdAccommodation(intIdAccommodation);
						services.setScrapingDate(today);
						services.setFreeWifi(freeWifi);
						services.setFreeParking(freeParking);
						services.setPetsAllowed(petsAllowed);
						services.setActivities(activities);
												
						// 2. Insertamos los services en la tabla "ft_accommodation_services"
						try {
							if(!servicesDao.servicesRecorded(services, con))
								servicesDao.insertServices(services, con);
						} catch (SQLException e) {
							logger.print("EXCEPTION 'SQLException' during INSERT into 'ft_accommodation_services': " + e.getMessage());
							logger.newline().print("Data: idAcc = " + services.getIdAccommodation() + ", date = " + services.getScrapingDate());
							logger.push("AmazonScraperFacade.insertServices(" + jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
									.getString(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID)) + ")", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
					} catch (Exception e) {
						logger.log("EXCEPTION 'Exception' parsing JSONObject: " + e.getMessage());
						con.rollback();
					}	
					
					// 4. Hacemos commit explícito
					con.commit();
				}
			}
//			notify();
		} catch (Exception e){
			throw e;
		}
		logger.log(Thread.currentThread().getName() + " FIN insertServices");
	}
	
	public void insertReviews (JSONArray response, Connection con, int intIdAccommodation, Logger logger) throws Exception{
		ReviewDao reviewDao = new ReviewDao();
		SimpleDateFormat sdf = new SimpleDateFormat(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_DATE_FORMAT), Locale.US);
		
		Date today = new Date();
		
		
//		while (!available) { 	//Esperar hasta que se pueda usar el método
//		try { 
//			wait(); 
//		} catch (InterruptedException e) {
//			throw e;
//		}
//	}
		logger.log(Thread.currentThread().getName() + " INICIO insertReviews");
		try {
			JSONArray jsonArray = response;
			
			for(int objIndex = 0; objIndex < jsonArray.length(); objIndex++){
			
				JSONObject jsonObj = jsonArray.getJSONObject(objIndex);
				JSONArray dataArray = jsonObj.getJSONArray(configClass.getProperty(Constants.BS_CONSTANT_DATA));
			
				for(int reviewIndex = 0; reviewIndex < dataArray.length(); reviewIndex++) {
					// 1. Iniciamos transacción
					con.setAutoCommit(false);
					
					try {
						JSONObject jsonReview = null; 
						jsonReview = dataArray.getJSONObject(reviewIndex);
						
						Review review= new Review();
						int intIdSegment = 0;
						
						
						String typeTrip = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_TYPE_TRIP));
						String segment = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_USERSEGMENT));
						String typeRoom = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_TYPE_ROOM));
						String stayNights = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_STAY_NIGHTS));
						String withPet = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_WITH_PET));
//						String subSegment = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_USERSUBSEGMENT));
						String from = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_FROM));
						String date = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_DATE));
						String lang = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_LANG));
						String reviewGood = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_REVIEWGOOD));
						String reviewBad = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_REVIEWBAD));
						String score = jsonReview.getString(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_SCORE));
						
						review.setIdAccommodation(intIdAccommodation);
						review.setScrapingDate(today);
						review.setTypeTrip(typeTrip);
						review.setSegment(segment);
						review.setTypeRoom(typeRoom);
						review.setStayNights(new Integer(stayNights));
						review.setWithPet(withPet);
//						review.setSubSegment(subSegment);
						review.setFrom(from);
						review.setDate(sdf.parse(date));
						review.setLang(lang);
						review.setReviewGood(reviewGood);
						review.setReviewBad(reviewBad);
						review.setScore(new Float(score));
						
						// 2. Buscamos el id del segmento del usuario
						try{
							intIdSegment = reviewDao.findIdSegment(review, con);
						}catch(SQLException e){
							logger.log("EXCEPTION 'SQLException' during SELECT to 'd_visitor_segment': " + e.getMessage(), "AmazonScraperFacade.insertReviews()", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
						
						// 2b. Si no existe el segmento, lo insertamos en la tabla "d_visitor_segment" y obtenemos su IDENTIFICADOR
						try {
							if(intIdSegment == 0)
								intIdSegment = reviewDao.insertVisitorSegment(review, con);
							if (intIdSegment != 0) // Guardamos el idSegment obtenido
								review.setIdSegment(intIdSegment);
						} catch(SQLException e){
							logger.print("EXCEPTION 'SQLException' during INSERT into 'd_visitor_segment': " + e.getMessage());
							logger.newline().print("Data: segment = " + review.getSegment());
							logger.push("AmazonScraperFacade.insertReviews(" + jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
									.getString(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID)) + ")", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}
						
						// 3. Insertamos las reviews en la tabla "ft_accommodation_review"
						
						try {
							reviewDao.insertReview(review, con);
						} catch (SQLException e) {
							logger.print("EXCEPTION 'SQLException' during INSERT into 'ft_accommodation_review': " + e.getMessage());
							logger.newline().print("Data: idAcc = " + review.getIdAccommodation() + ", date = " + review.getScrapingDate() + ", Segment = " + review.getSegment());
							logger.push("AmazonScraperFacade.insertRatings(" + jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
									.getString(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID)) + ")", LogPriority.ERROR);
							con.rollback();
							continue; // Siguiente iteración del bucle
						}

					} catch (Exception e) {
						logger.log("EXCEPTION 'Exception' parsing JSONObject: " + e.getMessage());
						con.rollback();
					}	
					
					// 4. Hacemos commit explícito
					con.commit();
				}
			}
//			notify();
		} catch (Exception e){
			throw e;
		}
		logger.log(Thread.currentThread().getName() + " FIN insertReviews");
	}
	
	public void insertRatings(JSONArray response, Connection con, int intIdAccommodation, Logger logger) throws Exception{
		int affectedRows = 0;
		
//		List<Float> listClean = null;
//		List<Float> listComfort = null;
//		List<Float> listLocation = null;
//		List<Float> listServices = null;
//		List<Float> listStaff = null;
//		List<Float> listValue = null;
//		List<Float> listWifi = null;
//		List<Integer> listTtlUsers = null;
		
		Date today = new Date();
		
//		String segmentTotal = configClass.getProperty(Constants.BS_CONSTANT_RATING_ID_SEGMENT_GENERAL);
		
//		while (!available) { 	//Esperar hasta que se pueda usar el método
//			try { 
//				wait(); 
//			} catch (InterruptedException e) {
//				throw e;
//			}
//		}
		logger.log(Thread.currentThread().getName() + " INICIO insertRatings");
		try {
			JSONArray jsonArray = response;
			
			for(int objIndex = 0; objIndex < jsonArray.length(); objIndex++){
			
//				listClean = new ArrayList<Float>();
//				listComfort = new ArrayList<Float>();
//				listLocation = new ArrayList<Float>();
//				listServices = new ArrayList<Float>();
//				listStaff = new ArrayList<Float>();
//				listValue = new ArrayList<Float>();
//				listWifi = new ArrayList<Float>();
//				listTtlUsers = new ArrayList<Integer>();
				
				JSONObject jsonObj = jsonArray.getJSONObject(objIndex);
				JSONArray dataArray = jsonObj.getJSONArray(configClass.getProperty(Constants.BS_CONSTANT_DATA));
			
				for(int ratingIndex = 0; ratingIndex < dataArray.length(); ratingIndex++) {
					String segment = "unknown", clean = "-1", comfort = "-1", location = "-1", services = "-1", staff = "-1", value = "-1", wifi = "-1", ttlUsers = "0";
					
					try {
						JSONObject jsonRating = null; 
						jsonRating = dataArray.getJSONObject(ratingIndex);
						
						Rating rating = new Rating();
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_USERSEGMENT)))
							segment = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_USERSEGMENT));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_CLEAN)))
							clean = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_CLEAN));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_COMFORT)))
							comfort = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_COMFORT));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_LOCATION)))
							location = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_LOCATION));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_SERVICES)))
							services = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_SERVICES));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_STAFF)))
							staff = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_STAFF));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_VALUE)))
							value = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_VALUE));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_WIFI)))
							wifi = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_WIFI));
						
						if (jsonRating.has(configClass.getProperty(Constants.BS_CONSTANT_RATING_TTL_USERS)))
							ttlUsers = jsonRating.getString(configClass.getProperty(Constants.BS_CONSTANT_RATING_TTL_USERS));
						

					/**
					 * ACTUALIZACIÓN 02/09/2015
					 * Booking ha cambiado los selectores de los ratings por lo que se modifica el código.
					 * Ahora sólo se obtiene el segmento TOTAL con lo que no tiene sentido lo siguiente
					 */
					// Se inserta el rating SÓLO si el segmento NO ES 1 (General)
					// Se hace esto porque la información de dicho segmento que ofrece booking no es correcta.
					// Mejor que la calcule el proceso
//						if (segmentTotal != null && !segmentTotal.equals(segment)) {
//							
//							if (clean != "-1") listClean.add(new Float(clean));
//							if (comfort != "-1") listComfort.add(new Float(comfort));
//							if (location != "-1") listLocation.add(new Float(location));
//							if (services != "-1") listServices.add(new Float(services));
//							if (staff != "-1") listStaff.add(new Float(staff));
//							if (value != "-1") listValue.add(new Float(value));
//							if (wifi != "-1") listWifi.add(new Float(wifi));
//							if (ttlUsers != "0") listTtlUsers.add(new Integer(ttlUsers));
														
							rating.setIdAccommodation(intIdAccommodation);
							rating.setScrapingDate(today);
							rating.setSegment(segment);
							rating.setClean(new Float(clean));
							rating.setComfort(new Float(comfort));
							rating.setLocation(new Float(location));
							rating.setServices(new Float(services));
							rating.setStaff(new Float(staff));
							rating.setValue(new Float(value));
							rating.setWifi(new Float(wifi));
							rating.setTtlUsers(new Integer(ttlUsers));
//						} else { 
//							// Segmento "TOTAL", próxima iteración
//							continue;
//						}
						try {
							affectedRows += _prepareAndInsertRating(rating, con, jsonObj, logger);
						} catch (Exception e) {
							String message = e.getMessage();
							if ("continue".equals(message))
								continue;
							else
								throw e;
						}
						
					} catch (Exception e) {
						if (e.getMessage().equals("empty String"))
							System.out.println("Empty results for user segment \"" + segment + "\"");
						else
							logger.log("EXCEPTION 'Exception' parsing JSONObject: " + e.getMessage());
						con.rollback();
					}	
				}
				/**
				 * ACTUALIZACIÓN 02/09/2015
				 * Booking ha cambiado los selectores de los ratings por lo que se modifica el código.
				 * Ya se obtiene el segmento TOTAL por lo que no hay que calcular nada
				 */
//				Rating rating = new Rating();
//				rating.setIdAccommodation(intIdAccommodation);
//				rating.setScrapingDate(today);
//				rating.setSegment(segmentTotal);
//				
//				_calculateRatingTotal(rating, listClean, listComfort, listLocation, listServices, listStaff, listValue, listWifi, listTtlUsers);
//				
//				affectedRows += _prepareAndInsertRating(rating, con, jsonObj, logger);
			}
//			notify();
		} catch (Exception e){
			throw e;
		}
		logger.log(Thread.currentThread().getName() + " FIN insertRatings (" + affectedRows + " tuplas insertadas)");
	}
	
	

	public List<Accommodation> selectScrappableAccommodations(String country, String nuts2, String nuts3, String locality, String accommodationType) throws Exception {
		AccommodationDao dao = new AccommodationDao();
		List<Accommodation> listAccommodations = null;
		Database db = new Database();
		try {
			Connection con;
			con = db.connect();
			
			listAccommodations = dao.selectAccommodations(country, nuts2, nuts3, locality, accommodationType, con);
			
			return listAccommodations;
			
		} catch (Exception e) {
			throw e;
		}
	
	}
	
	public List<Region> selectScrappableRegions(String country, String nuts2, String nuts3, String locality) throws Exception {
		RegionDao dao = new RegionDao();
		List<Region> listRegions = null;
		Database db = new Database();
		try {
			Connection con;
			con = db.connect();
			
			listRegions = dao.selectRegions(con, country, nuts2, nuts3, locality);
			
			return listRegions;
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	private int _prepareAndInsertRating(Rating rating, Connection con, JSONObject jsonObj, Logger logger) throws Exception {
		int intIdSegment = 0;
		int affectedRows = 0;
		
		RatingDao ratingDao = new RatingDao();
		
		// 1. Iniciamos transacción
		con.setAutoCommit(false);
			
		
		// 2. Buscamos el id del segmento del usuario
		try{

			intIdSegment = ratingDao.findIdSegment(rating, con);
			
		}catch(SQLException e){
			logger.log("EXCEPTION 'SQLException' during SELECT to 'd_visitor_segment': " + e.getMessage(), "AmazonScraperFacade.insertRatings()", LogPriority.ERROR);
			con.rollback();
			throw new Exception("continue"); // Siguiente iteración del bucle
		}
		
		// 2b. Si no existe el segmento, lo insertamos en la tabla "d_visitor_segment" y obtenemos su IDENTIFICADOR
		try {
			if(intIdSegment == 0)
				intIdSegment = ratingDao.insertVisitorSegment(rating, con);
			if (intIdSegment != 0) // Guardamos el idSegment obtenido
				rating.setIdSegment(intIdSegment);
		} catch(SQLException e){
			logger.print("EXCEPTION 'SQLException' during INSERT into 'd_visitor_segment': " + e.getMessage());
			logger.newline().print("Data: segment = " + rating.getSegment());
			logger.push("AmazonScraperFacade.insertRatings(" + jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
					.getString(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID)) + ")", LogPriority.ERROR);
			con.rollback();
			throw new Exception("continue"); // Siguiente iteración del bucle
		}
		
		// 3. Insertamos los ratings en la tabla "ft_accommodation_rating"
		
		try {
			affectedRows = ratingDao.insertRatings(rating, con);
		} catch (SQLException e) {
			logger.print("EXCEPTION 'SQLException' during INSERT into 'ft_accommodation_rating': " + e.getMessage());
			logger.newline().print("Data: idAcc = " + rating.getIdAccommodation() + ", date = " + rating.getScrapingDate() + ", Segment = " + rating.getSegment());
			logger.push("AmazonScraperFacade.insertRatings(" + jsonObj.getJSONObject(configClass.getProperty(Constants.BS_CONSTANT_TASK))
					.getString(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID)) + ")", LogPriority.ERROR);
			con.rollback();
			throw new Exception("continue"); // Siguiente iteración del bucle
		}
		
		// 4. Hacemos commit explícito
		con.commit();
		
		return affectedRows;
	}
	private void _calculateRatingTotal(Rating rating, List<Float> listClean, List<Float> listComfort, List<Float> listLocation, List<Float> listServices, 
			List<Float> listStaff, List<Float> listValue, List<Float> listWifi, List<Integer> listTtlUsers) {
		
		Float clean = new Float(0);
		Float comfort = new Float(0);
		Float location = new Float(0);
		Float services = new Float(0);
		Float staff = new Float(0);
		Float value = new Float(0);
		Float wifi = new Float(0);
		Integer ttlUsers = 0;
		
		for (Float cleanElem : listClean) {clean += cleanElem; } clean = clean/listClean.size();
		for (Float comfortElem : listComfort) {comfort += comfortElem; } comfort = comfort/listComfort.size();
		for (Float locationElem : listLocation) {location += locationElem; } location = location/listLocation.size();
		for (Float servicesElem : listServices) {services += servicesElem; } services = services/listServices.size();
		for (Float staffElem : listStaff) {staff += staffElem; } staff = staff/listStaff.size();
		for (Float valueElem : listValue) {value += valueElem; } value = value/listValue.size();
		for (Float wifiElem : listWifi) {wifi += wifiElem; } wifi = wifi/listWifi.size();
		for (Integer ttlUsersElem : listTtlUsers) {ttlUsers += ttlUsersElem; }
		
		rating.setClean(clean);
		rating.setComfort(comfort);
		rating.setLocation(location);
		rating.setServices(services);
		rating.setStaff(staff);
		rating.setValue(value);
		rating.setWifi(wifi);
		rating.setTtlUsers(ttlUsers);
	}
	
}
