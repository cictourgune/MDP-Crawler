package org.tourgune.mdp.booking.facade;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.torugune.mdp.log.DatabaseLogger;
import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.booking.exception.SelectorFailedException;
import org.tourgune.mdp.booking.utils.BookingConfig;
import org.tourgune.mdp.booking.utils.Constants;
import org.tourgune.mdp.booking.utils.ErrorDescriptions;
import org.tourgune.mdp.booking.utils.Selectors;
import org.tourgune.misc.utils.CustomConfig;
import org.tourgune.misc.utils.JSoupUtils;

public class BookingScraperFacade {

	private static BookingScraperFacade instance = null;
	private BookingConfig configClass = BookingConfig.getInstance();
	private CustomConfig customConfig = CustomConfig.getInstance();
	private Selectors selectorsClass = Selectors.getInstance();
	private ErrorDescriptions errorDescriptions = ErrorDescriptions.getInstance();
	
	private BookingScraperFacade() { }
	
	public static synchronized BookingScraperFacade getInstance() {
		if (instance == null) {
			instance = new BookingScraperFacade();
		}
		return instance;
	}
	
	public JSONObject getRegionHDEPrices (int regionId, String accommodationType, String checkin, String checkout, Logger logger, DatabaseLogger dbLogger) {
		JSONObject jsonRegionHDEPricesTask = new JSONObject();
		Map<String, String> mapTask = new HashMap<String, String>();
		
		JSONArrayProxy jsonRegionHDEPrices = new JSONArrayProxy();
		JSONArray jsonPartialHDEPrices = null;
		String regionHDEPricesUrlStatic = null;
		String regionHDEPricesUrlDynamic = null;
		org.jsoup.nodes.Document doc = null;
		boolean exit = false;
		
		Connection httpConn = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Integer rowStart = new Integer(configClass.getProperty(Constants.SCR_PRICES_HDE_ROWSTART));
		Integer nRows = new Integer(configClass.getProperty(Constants.SCR_PRICES_HDE_ROWS));
		
		regionHDEPricesUrlStatic = _getRegionHDEPricesUrl(regionId, accommodationType, checkin, checkout);
		
		try {
			// 1. Creamos el objeto "task"
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_REGIONID), new Integer(regionId).toString());
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES_HDE));
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_OFFERDATE), sdf.format(Calendar.getInstance().getTime()));
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_INDATE), checkin);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_OUTDATE), checkout);
			jsonRegionHDEPricesTask.put(configClass.getProperty(Constants.BS_CONSTANT_TASK), mapTask);
					
			while (!exit) {
				regionHDEPricesUrlDynamic = regionHDEPricesUrlStatic.replace(configClass.getProperty(Constants.BK_REPLACE_ROWSTART), rowStart.toString());
				httpConn = Jsoup.connect(regionHDEPricesUrlDynamic);
				try {
					doc = httpConn
							.timeout(Integer.parseInt(customConfig.getProperty(Constants.TIMEOUT)))
							.userAgent(customConfig.getProperty(Constants.BK_USER_AGENT))
							.header("Accept-Language", customConfig.getProperty(Constants.BK_ACCEPT_LANG))
							.get();
				
					logger.log("\t\tGot REGION HDE PRICES from " + regionHDEPricesUrlDynamic + ". Scrapping... ");
					jsonPartialHDEPrices = _scrapRegionHDEPrices(doc, regionId, accommodationType, checkin, checkout, logger, dbLogger);
					rowStart = rowStart + nRows;
					if (jsonPartialHDEPrices.length() < nRows) { // Si hay <nRows accommodationes
						if (jsonPartialHDEPrices.length() != 0){ // Pero no son igual a 0
							for (int i = 0; i<jsonPartialHDEPrices.length(); i++) {
								JSONObject jsonHDEPrice = (JSONObject) jsonPartialHDEPrices.get(i);
								jsonRegionHDEPrices.put(jsonHDEPrice);
							}
						}
						exit = true;
					} else {
						for (int i = 0; i<jsonPartialHDEPrices.length(); i++) {
							JSONObject jsonHDEPrice = (JSONObject) jsonPartialHDEPrices.get(i);
							jsonRegionHDEPrices.put(jsonHDEPrice);
						}
					}
				} catch (SocketTimeoutException ste) {
//					logger.log(Thread.currentThread().getName() + " - Socket timeout. Retrying in " + configClass.getProperty(Constants.RETRY_TIMEOUT) + " ms. " +
					logger.log(Thread.currentThread().getName() + " - Socket timeout. Retrying in " + customConfig.getProperty(Constants.RETRY_TIMEOUT) + " ms. " +
																	" - URL: " + regionHDEPricesUrlDynamic, "BookingScraperFacade");
//					Thread.sleep(Integer.parseInt(configClass.getProperty(Constants.RETRY_TIMEOUT)));
					Thread.sleep(Integer.parseInt(customConfig.getProperty(Constants.RETRY_TIMEOUT)));
				} catch (Exception e) {
					e.printStackTrace();
					logger.print("Error.");
					logger.push("BookingScraperFacade", LogPriority.ERROR);
				}
			}
			jsonRegionHDEPricesTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), jsonRegionHDEPrices.getJSONArray());
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade", LogPriority.ERROR);

		}
		return jsonRegionHDEPricesTask;
		
	}
	
	public JSONObject getAccommodationPrices (String accommodationIdBooking, String countryCode, String checkin, String checkout, Logger logger, DatabaseLogger dbLogger) throws InterruptedException {
		JSONObject jsonDayPrices = null;
		
		String accommodationPricesUrl = null;
		
		Connection httpConn = null;
		boolean socketConnected = false;
		org.jsoup.nodes.Document doc = null; 
		
		accommodationPricesUrl = _getAccommodationPricesUrl(accommodationIdBooking, countryCode, checkin, checkout);
		httpConn = Jsoup.connect(accommodationPricesUrl);
		do{
			try {
				doc = httpConn
						.timeout(Integer.parseInt(customConfig.getProperty(Constants.TIMEOUT)))
						.userAgent(customConfig.getProperty(Constants.BK_USER_AGENT))
						.header("Accept-Language", customConfig.getProperty(Constants.BK_ACCEPT_LANG))
						.get();
				socketConnected = true;
				logger.print("\tGot response from " + accommodationPricesUrl + ". Scrapping... ");
			} catch (SocketTimeoutException e) {
				socketConnected = false;
//				System.out.println("Socket timeout. Retrying in " + configClass.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
//				Thread.sleep(Integer.parseInt(configClass.getProperty(Constants.RETRY_TIMEOUT)));
				System.out.println("Socket timeout. Retrying in " + customConfig.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
				Thread.sleep(Integer.parseInt(customConfig.getProperty(Constants.RETRY_TIMEOUT)));
			} catch (Exception e) {
				logger.log("Error sending request: " + accommodationPricesUrl + " --> " + e.getMessage(), LogPriority.ERROR);
				throw new InterruptedException();
			}
		}while(!socketConnected);
		
		try {
			jsonDayPrices = _scrapPrices(doc, accommodationIdBooking, countryCode, checkin, checkout, logger, dbLogger);
			logger.print("\tDone.");
			logger.push("\tBookingScraperFacade", LogPriority.INFO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade", LogPriority.ERROR);
		}
		
		return jsonDayPrices;
	}
	
	public JSONObject getAccommodationRatings(String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) throws InterruptedException {
		JSONObject jsonRatings = null;
		
		String accommodationRatingsUrl = null;
		
		Connection httpConn = null;
		boolean socketConnected = false;
		org.jsoup.nodes.Document doc = null; 
		
		accommodationRatingsUrl = _getAccommodationRatingsUrl(accommodationIdBooking, countryCode);
		httpConn = Jsoup.connect(accommodationRatingsUrl);
		do{
			try {
				doc = httpConn
						.timeout(Integer.parseInt(customConfig.getProperty(Constants.TIMEOUT)))
						.userAgent(customConfig.getProperty(Constants.BK_USER_AGENT))
						.header("Accept-Language", customConfig.getProperty(Constants.BK_ACCEPT_LANG))
						.get();
				socketConnected = true;
				logger.log("\tGot ACCOMMODATION RATINGS from " + accommodationRatingsUrl + ". Scrapping... ");
			} catch (SocketTimeoutException e) {
				socketConnected = false;
//				System.out.println("Socket timeout. Retrying in " + configClass.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
//				Thread.sleep(Integer.parseInt(configClass.getProperty(Constants.RETRY_TIMEOUT)));
				System.out.println("Socket timeout. Retrying in " + customConfig.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
				Thread.sleep(Integer.parseInt(customConfig.getProperty(Constants.RETRY_TIMEOUT)));
			} catch (Exception e) {
				logger.log("\tError sending request: " + accommodationRatingsUrl + " --> " + e.getMessage(), LogPriority.ERROR);
				throw new InterruptedException();
			}
		}while(!socketConnected);
		
		try {
			jsonRatings = _scrapRatings(doc, accommodationIdBooking, countryCode, logger, dbLogger);
			logger.print("\tDone.");
			logger.push("\tBookingScraperFacade", LogPriority.INFO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("\tError.");
			logger.push("\tBookingScraperFacade", LogPriority.ERROR);
		}
		return jsonRatings;
		
	}
	
	public JSONObject getAccommodationReviews(String accommodationIdBooking, String countryCode, Date limitDate, Logger logger, DatabaseLogger dbLogger) throws InterruptedException {
		JSONObject jsonReviewsTask = new JSONObject();
		Map<String, String> mapTask = new HashMap<String, String>();
		
		Connection httpConn = null;
		
		JSONArray jsonReviews = new JSONArray();
		JSONArray jsonPartialReviews = null;
		String accommodationReviewsUrlStatic = null;
		String accommodationReviewsUrlDynamic = null;
		org.jsoup.nodes.Document doc = null; 
		boolean exit = false;
		
		Integer rowStart = new Integer(configClass.getProperty(Constants.SCR_REVIEWS_ROWSTART));
		Integer nRows = new Integer(configClass.getProperty(Constants.SCR_REVIEWS_ROWS));
		
		accommodationReviewsUrlStatic = _getAccommodationReviewsUrl(accommodationIdBooking, countryCode);
		
		try {
			// 1. Creamos el objeto "task"
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_COUNTRY), countryCode);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID), accommodationIdBooking);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), configClass.getProperty(Constants.BS_CONSTANT_TASK_REVIEWS));
			jsonReviewsTask.put(configClass.getProperty(Constants.BS_CONSTANT_TASK), mapTask);
					
			while (!exit) {
				accommodationReviewsUrlDynamic = accommodationReviewsUrlStatic.replace(configClass.getProperty(Constants.BK_REPLACE_ROWSTART), rowStart.toString());
				httpConn = Jsoup.connect(accommodationReviewsUrlDynamic);
				try {
					doc = httpConn
							.timeout(Integer.parseInt(customConfig.getProperty(Constants.TIMEOUT)))
							.userAgent(customConfig.getProperty(Constants.BK_USER_AGENT))
							.header("Accept-Language", customConfig.getProperty(Constants.BK_ACCEPT_LANG))
							.get();
					//while (doc == null);
				
					logger.log("\tGot ACCOMMODATION REVIEWS from " + accommodationReviewsUrlDynamic + ". Scrapping... ");
					jsonPartialReviews = _scrapReviews(doc, limitDate, accommodationIdBooking, logger, dbLogger);
					rowStart = rowStart + nRows;
					if (jsonPartialReviews.length() < nRows) { // Si hay <nRows reviews
						if (jsonPartialReviews.length() != 0){ // Pero no son igual a 0 
							for (int i = 0; i<jsonPartialReviews.length(); i++) {
								JSONObject jsonReview = (JSONObject) jsonPartialReviews.get(i);
								jsonReviews.put(jsonReview);
							} 
						}
						exit = true;
					} else{
						for (int i = 0; i<jsonPartialReviews.length(); i++) {
							JSONObject jsonReview = (JSONObject) jsonPartialReviews.get(i);
							jsonReviews.put(jsonReview);
						}
					}
				} catch (SocketTimeoutException ste) {
//					System.out.println("Socket timeout. Retrying in " + configClass.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
//					Thread.sleep(Integer.parseInt(configClass.getProperty(Constants.RETRY_TIMEOUT)));
					System.out.println("Socket timeout. Retrying in " + customConfig.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
					Thread.sleep(Integer.parseInt(customConfig.getProperty(Constants.RETRY_TIMEOUT)));
				} catch (Exception e) {
					e.printStackTrace();
					logger.print("Error.");
					logger.push("BookingScraperFacade", LogPriority.ERROR);
					continue;
				}
			}
			jsonReviewsTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), jsonReviews);
		} catch (InterruptedException ie) {
			throw ie;
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade", LogPriority.ERROR);

		}
		return jsonReviewsTask;
		
	}

	public JSONObject getAccommodationServices(String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) throws InterruptedException {
		
		JSONObject jsonServices = null;
		String accommodationServicesUrl = null;
		org.jsoup.nodes.Document doc = null;
		Connection httpConn = null;	// org.jsoup.Connection
		boolean socketConnected = false;
		
		accommodationServicesUrl = _getAccommodationServicesUrl(accommodationIdBooking, countryCode);
		httpConn = Jsoup.connect(accommodationServicesUrl);
		do{
			try {
				doc = httpConn
						.timeout(Integer.parseInt(customConfig.getProperty(Constants.TIMEOUT)))
						.userAgent(customConfig.getProperty(Constants.BK_USER_AGENT))
						.header("Accept-Language", customConfig.getProperty(Constants.BK_ACCEPT_LANG))
						.get();
				socketConnected = true;
				//doc = Jsoup.connect(accommodationServicesUrl).get();
				System.out.println("\tGot ACCOMMODATION SERVICES from " + accommodationServicesUrl + ". Scrapping... ");
			} catch (SocketTimeoutException e) {
				socketConnected = false;
//				System.out.println("Socket timeout. Retrying in " + configClass.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
//				Thread.sleep(Integer.parseInt(configClass.getProperty(Constants.RETRY_TIMEOUT)));
				System.out.println("Socket timeout. Retrying in " + customConfig.getProperty(Constants.RETRY_TIMEOUT) + " ms.");
				Thread.sleep(Integer.parseInt(customConfig.getProperty(Constants.RETRY_TIMEOUT)));
			} catch (Exception e) {
				System.out.println("\tError sending request: " + accommodationServicesUrl + " --> " + e.getMessage());
				throw new InterruptedException();
			}
		}while(!socketConnected);
		
		try {
			jsonServices = _scrapServices(doc, accommodationIdBooking, countryCode, logger, dbLogger);
			logger.log("Request sent", "BookingScraperFacade", LogPriority.INFO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log("Error sending request", "BookingScraperFacade", LogPriority.ERROR);
		}
		return jsonServices;
		
	}
	
	private JSONArray _scrapRegionHDEPrices(org.jsoup.nodes.Document xml, int regionId, String accmType, String checkin, String checkout, Logger logger, DatabaseLogger dbLogger) throws Exception {
		List<String> errorList = new LinkedList<String>();
		
		JSONArray jsonRegionHDEPricesData = new JSONArray();
		String[] roomFilters = new String[2];
		
		String accommodationName = "", accommodationUrl = "", accommodationIdChannel = "",
				roomType = "", price = "", strCoords = "", category = "";
		
		if (!accmType.equals("201")) {
			roomFilters[0] = configClass.getProperty(Constants.BS_CONSTANT_DOUBLEROOM);
			roomFilters[1] = configClass.getProperty(Constants.BS_CONSTANT_TWINROOM);
		}

		try {
			while (xml == null); // A veces el xml viene a null porque no le da tiempo a la respuesta.
		
			Elements elemHDEPrices = xml.select(selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_LIST));
//			if(elemHDEPrices.size() == 0) {
//				String errorDescription = errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED);
//				String[] selectorArray = {selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_LIST)};
//				dbLogger.setReferenceCode(xml.toString());
//				dbLogger.log(errorDescriptions.generateErrorString(errorDescription, selectorArray), "");
//				dbLogger.clearReferenceCode();
//			}
			
			for (Element elemHDEPrice : elemHDEPrices) {
				roomType = "";
//				if(!elemHDEPrice.text().contains("to instantly reveal") && !elemHDEPrice.text().contains("so booking good")){
				try {
					// Es más efectivo mirar si tiene "data-hotelid" que un texto que puede ir cmabiando
					JSoupUtils.selectElementValueOrAttr(elemHDEPrice, 
							selectorsClass.getProperty(Constants.SELECTOR_REGION_IS_HOTEL),
							dbLogger);
					try {
						// Nombre del accommodation
						try{
							accommodationName = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_NAME),
									dbLogger);
//							accommodationName = _selectElementText(elemHDEPrice,
//									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_NAME),
//									dbLogger);
						}catch(SelectorFailedException e){
							errorList.add(e.getMessage());
						}
						
						// URL del accommodation
						try{
							accommodationUrl = JSoupUtils.selectElementValueOrAttr(elemHDEPrice, 
									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_URL),
									dbLogger);
//							accommodationUrl = _selectElementAttribute(elemHDEPrice,
//									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_URL),
//									configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_HREF),
//									dbLogger);
						}catch(SelectorFailedException e){
							errorList.add(e.getMessage());
						}
						
						// ID channel de booking
						accommodationIdChannel = elemHDEPrice.attr(selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_ID_CHANNEL_ATTR));
						if (accommodationIdChannel.isEmpty())
							errorList.add(errorDescriptions.generateErrorString(
											errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
											new String[]{selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_ID_CHANNEL_ATTR)}));
						else
							accommodationIdChannel = selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_ID_CHANNEL_PREFIX) + accommodationIdChannel;
						
						// Tipo de habitación
						try{
							if (accmType.equals("201")) {
								roomType = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
										selectorsClass.getProperty(Constants.SELECTOR_REGION_APARTMENT_MAX_PEOPLE),
										dbLogger);
//								roomType = _selectElementAttribute(elemHDEPrice,
//										selectorsClass.getProperty(Constants.SELECTOR_REGION_APARTMENT_MAX_PEOPLE),
//										"class",
//										dbLogger);
							} else
								roomType = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
										selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_ROOM_TYPE),
										dbLogger);
//								roomType = _selectElementText(elemHDEPrice,
//										selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_ROOM_TYPE),
//										dbLogger);
						}catch(SelectorFailedException e){
							// intentamos con la segunda manera
							try {
								roomType = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
										selectorsClass.getProperty(Constants.SELECTOR_REGION_APARTMENT_MAX_PEOPLE),
										dbLogger);
//								roomType = _selectElementAttribute(elemHDEPrice,
//										selectorsClass.getProperty(Constants.SELECTOR_REGION_APARTMENT_MAX_PEOPLE),
//										"data-title",
//										dbLogger);
							} catch (SelectorFailedException e2) {
								// y esta vez sí, logueamos el error
								errorList.add(e2.getMessage());
							}
						}
						
						// Precio del accommodation
						try{
							price = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_PRICE),
									dbLogger);
//							price = _selectElementText(elemHDEPrice,
//									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_PRICE),
//									dbLogger);
						}catch(SelectorFailedException e){
							errorList.add(e.getMessage());
						}
						
						// Coordenadas del accommodation
						try{
							strCoords = JSoupUtils.selectElementValueOrAttr(elemHDEPrice,
									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_COORDS),
									dbLogger);
//							strCoords = _selectElementAttribute(elemHDEPrice,
//									selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_COORDS),
//									configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATION_COORDS_ATTR),
//									dbLogger);
						}catch(SelectorFailedException e){
							errorList.add(e.getMessage());
						}
						
						// Núm. estrellas del accommodation
						category = elemHDEPrice.attr(selectorsClass.getProperty(Constants.PROPERTY_ACCOMMODATION_CATEGORY));
						if (category.isEmpty())	// la categoría no debe estar vacía: esto es un error en el selector
							errorDescriptions.generateErrorString(
									errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
									new String[]{selectorsClass.getProperty(Constants.PROPERTY_ACCOMMODATION_CATEGORY)});
						
						// Es el precio de la HDE
						JSONObject jsonHDEPrice = new JSONObject();
						double[] coords = _stripCoords(strCoords);
						boolean roomTypeFilter = false;
						
						if (accmType.equals("201"))
							roomTypeFilter = roomType.matches(configClass.getProperty(Constants.BS_CONSTANT_MAX_PEOPLE_REGEX_JQ))
									|| roomType.matches(configClass.getProperty(Constants.BS_CONSTANT_MAX_PEOPLE_REGEX_JQ_2))
									|| roomType.matches(configClass.getProperty(Constants.BS_CONSTANT_MAX_PEOPLE_REGEX_I));
						else
							roomTypeFilter = roomType.toLowerCase().contains(roomFilters[0]) || roomType.toLowerCase().contains(roomFilters[1]);
						
						if(accommodationIdChannel.isEmpty() || (roomType.isEmpty() && !accmType.equals("201")) || price.isEmpty()) {
							jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_HDEPRICE), "-1");
						} else if (roomTypeFilter)
							jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_HDEPRICE), _stripPrice(price));
						else {
							// No hemos encontrado la habitación que buscábamos así que registramos el error como siempre
							String errorDesc = errorDescriptions.getProperty(Constants.BS_ERROR_ROOM_TYPE_NOT_MATCH);
							errorList.add(errorDescriptions.generateErrorString(errorDesc, new String[]{roomType}));
							jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_HDEPRICE), "0");
						}
						
						/*
						 * Loguea en la BD todos los errores que nos han surgido con este accommodation.
						 * Valor que se meterá en el campo 'accommodation', que indica en qué accommodation estamos,
						 * y por orden de preferencia:
						 * 		1. ID Accommodation Channel de booking ("hotel_XXXXX").
						 * 		2. URL del accommodation ("/hotel/xx/xxxxx").
						 * 		3. Nombre del accommodation.
						 * 		4. Cadena vacía.
						 */
						if(!errorList.isEmpty()){
							String logAccommodationId;
							
							if(!accommodationIdChannel.isEmpty())
								logAccommodationId = accommodationIdChannel;
							else if(!accommodationUrl.isEmpty())
								logAccommodationId = accommodationUrl;
							else if(!accommodationName.isEmpty())
								logAccommodationId = accommodationName;
							else
								logAccommodationId = "";
							
							//dbLogger.setReferenceCode(elemHDEPrice.toString());
							
							//for(String error: errorList)
								//dbLogger.log(error, logAccommodationId);
							
							errorList.clear();
							//dbLogger.clearReferenceCode();
						}
						
						// Mete los datos obtenidos en el JSON
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONIDCHANNEL), accommodationIdChannel);
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONNAME), accommodationName);
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_LATITUDE), coords[0]);
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_LONGITUDE), coords[1]);
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_CATEGORY), _stripCategory(category));
						jsonHDEPrice.put(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_URL), _stripUrl(accommodationUrl));
						
						jsonRegionHDEPricesData.put(jsonHDEPrice);
					} catch (Exception e) {
						logger.log("\tError scraping HDE PRICES: " + elemHDEPrice.toString() + ". Message: " + e.getMessage());
						continue;
					}
				} catch (Exception e) {
					// No se hace nada ya que simplemnte ha aparecido un elemento de "Iniciar Sesión"
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade._scrapRegionHDEPrices", LogPriority.ERROR);
		}
		
		return jsonRegionHDEPricesData;
	}
	
	private JSONObject _scrapPrices(org.jsoup.nodes.Document xml, String accommodationIdBooking, String countryCode, String checkin, String checkout, Logger logger, DatabaseLogger dbLogger) throws Exception {
		List<String> errorList = new LinkedList<String>();
		JSONObject jsonDayTask = new JSONObject();
		JSONArray jsonDayData = new JSONArray();
		Map<String, String> mapTask = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String name = "",
				breakfast = "", breakfastPrice = "",
				halfBoard = "", fullBoard = "", allInclusive = "",
				freeCancellation = "", payStay = "", payLater = "", nonRefundable = "", price = "",
				occupancy = "";
		
		try {
			// 1. Creamos el objeto "task"
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_COUNTRY), countryCode);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID), accommodationIdBooking);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), Constants.BS_CONSTANT_TASK_PRICES);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_OFFERDATE), sdf.format(Calendar.getInstance().getTime()));
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_INDATE), checkin);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_OUTDATE), checkout);
			jsonDayTask.put(configClass.getProperty(Constants.BS_CONSTANT_TASK), mapTask);
			
			while (xml == null); // A veces el xml viene a null porque no le da tiempo a la respuesta.
			
			Elements elemRoomPrices = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCTS));
			if(elemRoomPrices.size() == 0){
				String errorDescription = errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED);
				String[] selectorArray = {selectorsClass.getProperty(Constants.SELECTOR_REGION_ACCOMMODATION_LIST)};
				errorList.add(errorDescriptions.generateErrorString(errorDescription, selectorArray));
			}
			
			for (Element roomPrice : elemRoomPrices) {
				boolean exit = false;
				Element auxSibling = roomPrice;

				// Obtenemos el nombre del accommodation
				try{
					name = JSoupUtils.selectElementValueOrAttr(roomPrice,
							selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_NAME),
							dbLogger);
//					name = _selectElementText(roomPrice,
//							selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_NAME),
//							dbLogger);
				}catch(SelectorFailedException e){
					name = "";
					errorList.add(e.getMessage());
				}
				
	        	while (!exit) {
	                String elemClass = roomPrice.className().trim();	// Obtenemos el atributo "class" del <tr> (room_loop_counterX (maintr))
					auxSibling = auxSibling.nextElementSibling();	// Obtenemos el siguiente elemento que está al mismo nivel que nosotros
					String siblingClass = auxSibling == null ? null : auxSibling.className().trim();
					
					if (siblingClass != null && elemClass.contains(siblingClass)) {
						// Si hay desayuno ("1") o no ("0")
						try{
							breakfast = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_BREAKFAST),
									dbLogger)
//							breakfast = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_BREAKFAST),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							breakfast = "";
							errorList.add(e.getMessage());
						}
						
						// Precio del desayuno
						try{
							breakfastPrice = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_BREAKFAST_PRICE),
									dbLogger)
//							breakfastPrice = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_BREAKFAST_PRICE),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							breakfastPrice = "";
							errorList.add(e.getMessage());
						}
						
						// Si es a media pensión ("1") o no ("0")
						try{
							halfBoard = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_HALF),
									dbLogger)
//							halfBoard = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_HALF),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							halfBoard = "";
							errorList.add(e.getMessage());
						}
						
						// Si es a pensión completa ("1") o no ("0")
						try{
							fullBoard = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_FULL),
									dbLogger)
//							fullBoard = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_FULL),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							fullBoard = "";
							errorList.add(e.getMessage());
						}
						
						// Si es todo incluído ("1") o no ("0")
						try{
							allInclusive = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_ALL),
									dbLogger)
//							allInclusive = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_ALL),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							allInclusive = "";
							errorList.add(e.getMessage());
						}
						
						// Si es con cancelación gratuita ("1") o no ("0")
						try{
							freeCancellation = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_FREE_CANCELLATION),
									dbLogger)
//							freeCancellation = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_FREE_CANCELLATION),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							freeCancellation = "";
							errorList.add(e.getMessage());
						}
						
						// Si se paga al salir ("1") o no ("0")
						try{
							payStay = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PAY_STAY),
									dbLogger)
//							payStay = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PAY_STAY),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							payStay = "";
							errorList.add(e.getMessage());
						}
						
						// Si se paga al llegar ("1") o no ("0")
						try{
							payLater = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PAY_LATER),
									dbLogger)
//							payLater = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PAY_LATER),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							payLater = "";
							errorList.add(e.getMessage());
						}
						
						// Si es no reembolsable ("1") o no ("0")
						try{
							nonRefundable = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_NON_REFUNDABLE),
									dbLogger)
//							nonRefundable = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_NON_REFUNDABLE),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							nonRefundable = "";
							errorList.add(e.getMessage());
						}

						// El precio de la habitación
						try{
							price = JSoupUtils.selectElementValueOrAttr(auxSibling,
									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PRICE),
									dbLogger)
//							price = _selectElementText(auxSibling,
//									selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_PRICE),
//									dbLogger)
									.trim();
						}catch(SelectorFailedException e){
							price = "";
							errorList.add(e.getMessage());
						}
						
						// Cantidad de personas permitida
						// Esta es una propiedad, no el texto de una etiqueta
						occupancy = auxSibling.attr(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_OCCUPANCY)).trim();
						if(occupancy.isEmpty())
							errorList.add(
									errorDescriptions.generateErrorString(
											errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
											new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_OCCUPANCY)}));
						
						String child = null;
						Elements childrenAmountRoot = auxSibling.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_CHILD));
						if(childrenAmountRoot.size() > 0){
							child = _getChildren(childrenAmountRoot.attr(configClass.getProperty(Constants.BS_CONSTANT_HDEPRICE_TITLE)));
							if(child.isEmpty())
								errorList.add(
										errorDescriptions.generateErrorString(
												errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
												new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_PRODUCT_OCCUPANCY)}));
						}
						
						/*
						 * Loguea en la BD todos los errores que nos han surgido con este accommodation.
						 * En el campo 'accommodation' meteremos la URL del accommodation en booking ("/hotel/<country code>/<accommodationid>").
						 */
						//for(String error: errorList)
							//dbLogger.log(error, "/hotel/" + countryCode + "/" + accommodationIdBooking);
						errorList.clear();
						
						// Mete los datos en el JSON
						JSONObject jsonRoom = new JSONObject();
	                	
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_NAME), name);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_BREAKFAST), !"".equals(breakfast) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_BREAKFASTPRICE), !"".equals(breakfastPrice) ? Double.parseDouble(_stripPrice(breakfastPrice)) : 0f);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_HALF), !"".equals(halfBoard) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_FULL), !"".equals(fullBoard) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_ALL), !"".equals(allInclusive) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_CANCELLATION), !"".equals(freeCancellation) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PAYSTAY), !"".equals(payStay) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PAYLATER), !"".equals(payLater) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_NONRFUNDABLE), !"".equals(nonRefundable) ? true : false);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_PRICE), Double.parseDouble(_stripPrice(price)));
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_OCCUPANCY), occupancy);
	                	jsonRoom.put(configClass.getProperty(Constants.BS_CONSTANT_PRODUCT_CHILD), child != null && !"".equals(child) ? child : "0");
	                	
	//                	System.out.println(jsonRoom);
	                	
	                	jsonDayData.put(jsonRoom);
	        		} else {
	        			exit = true;
	        		}
	        	}
			}
			jsonDayTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), jsonDayData);
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade._scrapPrices", LogPriority.ERROR);
		}
		
		return jsonDayTask;
	}

	private JSONObject _scrapServices(org.jsoup.nodes.Document xml, String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) throws Exception {
		List<String> errorList = new LinkedList<String>();
		
		JSONObject jsonServicesTask = new JSONObject();
		JSONArray jsonServicesData = new JSONArray();
		Map<String, String> mapTask = new HashMap<String, String>();
		
		String freeWifi = "", freeParking = "",
				activities = "",
				petsAllowed = "";
		
		try {
			// 1. Creamos el objeto "task"
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_COUNTRY), countryCode);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID), accommodationIdBooking);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), configClass.getProperty(Constants.BS_CONSTANT_TASK_SERVICES));
			jsonServicesTask.put(configClass.getProperty(Constants.BS_CONSTANT_TASK), mapTask);
			
			while (xml == null); // A veces el xml viene a null porque no le da tiempo a la respuesta.
			
			Elements elemServices = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICES));
			if(elemServices.size() > 0){
				Element elemService = elemServices.get(0);
//			for (Element elemService : elemServices) {
				try {
					// Si hay WiFi gratuito o no
					try{
						freeWifi = JSoupUtils.selectElementValueOrAttr(elemService,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_INTERNET_FREE_WIFI),
								dbLogger)
//						freeWifi = _selectElementText(elemService,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_INTERNET_FREE_WIFI),
//								dbLogger)
								.trim();
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					// Si hay parking gratuito o no
					try{
						freeParking = JSoupUtils.selectElementValueOrAttr(elemService,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_PARKING_FREE),
								dbLogger)
//						freeParking = _selectElementText(elemService,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_PARKING_FREE),
//								dbLogger)
								.trim();
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					// Las actividades que se ofrecen
					try{
						activities = JSoupUtils.selectElementValueOrAttr(elemService,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_ACTIVITIES),
								dbLogger);
//						activities = _selectElementText(elemService,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_ACTIVITIES),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					// Si se permiten animales o no.
					// OJO aquí scrapeamos el elemento XML y no el "elemService".
					// org.jsoup.nodes.Document (la variable 'xml') hereda de Elements, así que podemos usar
					// el método _selectElementText() igual que con los demás.
					try{
						petsAllowed = JSoupUtils.selectElementValueOrAttr(xml,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_PETS_ALLOWED),
								dbLogger);
//						petsAllowed = _selectElementText(xml,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICE_PETS_ALLOWED),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
//					for(String error: errorList)
//						dbLogger.log(error, "/hotel/" + countryCode + "/" + accommodationIdBooking);
					errorList.clear();
					
					JSONObject jsonService = new JSONObject();
		               	
					jsonService.put(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_FREEWIFI), freeWifi != null && !"".equals(freeWifi) ? true : false);
					jsonService.put(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_FREEPARKING), freeParking != null && !"".equals(freeParking) ? true : false);
					jsonService.put(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_PETS_ALLOWED), petsAllowed != null && !"".equals(petsAllowed) ? true : false);
					jsonService.put(configClass.getProperty(Constants.BS_CONSTANT_SERVICE_ACTIVITIES), activities);
					
		                            	
					jsonServicesData.put(jsonService);
				} catch (Exception e) {
					logger.log("\tError scraping SERVICES: " + elemService.toString() + ". Message: " + e.getMessage());
//					continue;
				}
			}else{
				String errorDesc = errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED);
//				dbLogger.log(
//						errorDescriptions.generateErrorString(
//								errorDesc,
//								new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_SERVICES)}),
//								"/hotel/" + countryCode + "/" + accommodationIdBooking);
			}
//			}
			jsonServicesTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), jsonServicesData);
		} catch (Exception e){
			e.printStackTrace();
			logger.log("Error", "BookingScraperFacade._scrapServices", LogPriority.ERROR);
		}
		
		return jsonServicesTask;
	}
	
	private JSONObject _scrapRatings(org.jsoup.nodes.Document xml, String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) throws Exception {
		JSONObject jsonRatingsTask = new JSONObject();
		JSONArray jsonRatingsData = new JSONArray();
		Map<String, String> mapTask = new HashMap<String, String>();
		Map<String, JSONObject> segmentAttributes = new HashMap<String, JSONObject>();
		
		Map<String, String> dataAttrs = null;
		String curKey = null, parts[] = null;
		JSONObject properties = null;
		
		try {
			// 1. Creamos el objeto "task"
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_COUNTRY), countryCode);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_ACCOMMODATIONID), accommodationIdBooking);
			mapTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), configClass.getProperty(Constants.BS_CONSTANT_TASK_RATINGS));
			jsonRatingsTask.put(configClass.getProperty(Constants.BS_CONSTANT_TASK), mapTask);
			
			while (xml == null); // A veces el xml viene a null porque no le da tiempo a la respuesta.
			
			/**
			 * ACTUALIZACIÓN 02/09/2015
			 * Booking ha cambiado los selectores de los ratings por lo que se modifica el código 
			 */
//			/*
//			 * Obtenemos los ratings de cada segmento de usuario.
//			 * Los segmentos de usuario disponibles se irán añadiendo dinámicamente.
//			 */
//			Element ratings = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING)).first();
//			if (ratings != null) {
//				dataAttrs = ratings.dataset();	// obtenemos los atributos que empiezan por "data-"
//				for (Iterator<String> keyIt = dataAttrs.keySet().iterator(); keyIt.hasNext();) {
//					curKey = keyIt.next();
//					parts = curKey.split("_hotel_");
//					
//					if (!segmentAttributes.containsKey(parts[0])) {
//						properties = new JSONObject();
//						properties.put(configClass.getProperty(Constants.BS_CONSTANT_RATING_USERSEGMENT), parts[0]);
//						segmentAttributes.put(parts[0], properties);
//					} else
//						properties = segmentAttributes.get(parts[0]);
//					
//					properties.put(parts[1], dataAttrs.get(curKey));
//				}
//			}
			
			Elements ratings = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING));
			if (ratings != null) {
				for (Element rating : ratings) {
					String segment = "total";
					String attribute = JSoupUtils.selectElementValueOrAttr(rating, 
							selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING_ATTRIBUTE), 
							dbLogger);
					attribute = attribute.replace("hotel_", "");
					
					String attributeValue = JSoupUtils.selectElementValueOrAttr(rating, 
							selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING_ATTRIBUTE_VALUE), 						
							dbLogger);
					// Ahora los ratings ya no vienen segmentados, siempre son segmento TOTAL
					if (!segmentAttributes.containsKey(segment)) {
						properties = new JSONObject();
						properties.put(configClass.getProperty(Constants.BS_CONSTANT_RATING_USERSEGMENT), segment);
						segmentAttributes.put(segment, properties);
					} else 
						properties = segmentAttributes.get(segment);
					
					properties.put(attribute, attributeValue);
				}
			}
			
			/**
			 * ACTUALIZACIÓN 02/09/2015
			 * Booking ha cambiado los selectores de los ratings por lo que se modifica el código 
			 */
//			/*
//			 * Basándonos en los segmentos de usuario que ya hemos obtenido,
//			 * obtenemos la cantidad de usuarios que había en cada uno de ellos.
//			 */
//			String ttlUsers = null;
//			Element ttlUsersElem = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING_P)).first();
//			if (ttlUsersElem != null) {
//				dataAttrs = ttlUsersElem.dataset();
//				for (Iterator<String> keyIt = dataAttrs.keySet().iterator(); keyIt.hasNext();) {
//					curKey = keyIt.next();
//					ttlUsers = dataAttrs.get(curKey);
//					Matcher ttlUsersMatcher = Pattern.compile("\\d+ review").matcher(ttlUsers.toLowerCase().trim());
//					if (ttlUsersMatcher.find())
//						ttlUsers = ttlUsersMatcher.group().split(" ")[0];
//					if (segmentAttributes.get(curKey) != null) {
//						segmentAttributes.get(curKey).put(configClass.getProperty(Constants.BS_CONSTANT_RATING_TTL_USERS), ttlUsers);
//					}
//				}
//			}
			
			String ttlUsers = null;
			Element ttlUsersElem = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_RATING_P)).first();
			if (ttlUsersElem != null) {
				String segment = "total";
				ttlUsers = ttlUsersElem.text();
				Matcher ttlUsersMatcher = Pattern.compile("\\d+ review").matcher(ttlUsers.toLowerCase().trim());
				if (ttlUsersMatcher.find())
					ttlUsers = ttlUsersMatcher.group().split(" ")[0];
				if (segmentAttributes.get(segment) != null) {
					segmentAttributes.get(segment).put(configClass.getProperty(Constants.BS_CONSTANT_RATING_TTL_USERS), ttlUsers);
				}
			}
			
			for (Iterator<String> keyIt = segmentAttributes.keySet().iterator(); keyIt.hasNext();)
				jsonRatingsData.put(segmentAttributes.get(keyIt.next()));
			
			jsonRatingsTask.put(configClass.getProperty(Constants.BS_CONSTANT_DATA), jsonRatingsData);
		} catch (Exception e){
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade._scrapRatings", LogPriority.ERROR);
		}
		
		return jsonRatingsTask;
	}
	
	private JSONArray _scrapReviews(org.jsoup.nodes.Document xml, Date limitDate, String accommodationIdBooking, Logger logger, DatabaseLogger dbLogger) throws Exception {
		List<String> errorList = new LinkedList<String>();
		SimpleDateFormat dateParser = new SimpleDateFormat(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_DATE_FORMAT), Locale.US);
		JSONArray jsonReviewsData = new JSONArray();
		
		String unknown = configClass.getProperty(Constants.BS_CONSTANT_REVIEW_USERSEGMENT_UNKNOWN);
		String typeTrip = null, segment = unknown, typeRoom = null, stayNights = null, withPet = null, from = null, date = null, lang = unknown,
				reviewGood = null, reviewBad = null, score = null;
		
		Matcher matcher = null;
		
		try {
			
			while (xml == null); // A veces el xml viene a null porque no le da tiempo a la respuesta.
			
			Date reviewDate = null;
			Elements elemReviews = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_LIST));
			if(elemReviews.size() == 0){
				String errorDesc = errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED);
//				dbLogger.log(
//						errorDescriptions.generateErrorString(errorDesc,
//								new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_LIST)}),
//								accommodationIdBooking);
			}
			
			for (int elemIndex = 0; elemIndex < elemReviews.size(); elemIndex++) {
				Element elemReview = elemReviews.get(elemIndex);
				try {
								
//					Elements reviewList = xml.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SEGMENT));
//					if (reviewList.size() > 0) {
//						segment = reviewList.get(1).text();
//						matcher = Pattern.compile("[A-Za-z]+").matcher(segment);
//						if (matcher.find())
//							segment = matcher.group().toLowerCase();
//						else
//							segment = unknown;
//					} else
//						dbLogger.log(
//								errorDescriptions.generateErrorString(errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
//									new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SEGMENT)}),
//								accommodationIdBooking);
					try{
						typeTrip = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_TYPE_TRIP),
								dbLogger);
//						typeTrip = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_TYPE_TRIP),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					try{
						segment = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SEGMENT),
								dbLogger);
//						segment = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SEGMENT),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					try{
						typeRoom = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_TYPE_ROOM),
								dbLogger);
//						typeRoom = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_TYPE_ROOM),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					try{
						stayNights = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_STAY_NIGHTS),
								dbLogger);
//						stayNights = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_STAY_NIGHTS),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					try{
						withPet = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_WITH_PET),
								dbLogger);
//						withPet = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_WITH_PET),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					try{
						from = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_FROM),
								dbLogger);
//						from = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_FROM),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					try{
						date = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_DATE),
								dbLogger);
//						date = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_DATE),
//								dbLogger);
						matcher = Pattern.compile("[A-Za-z]+ \\d{1,2}?, ?\\d{4}").matcher(date);
						if (matcher.find())
							date = matcher.group();
						else
							date = unknown;
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
					reviewGood = elemReview.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_GOOD)).text();
					reviewBad = elemReview.select(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_BAD)).text();
					
					try{
						score = JSoupUtils.selectElementValueOrAttr(elemReview,
								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SCORE),
								dbLogger);
//						score = _selectElementText(elemReview,
//								selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_SCORE),
//								dbLogger);
					}catch(SelectorFailedException e){
						errorList.add(e.getMessage());
					}
					
//					lang = elemReview.attr(selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_LANG));
//					if(lang.isEmpty())
//						errorList.add(
//								errorDescriptions.generateErrorString(
//										errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
//										new String[]{selectorsClass.getProperty(Constants.SELECTOR_ACCOMMODATION_REVIEW_LANG)}));
					
//					for(String error: errorList)
//						dbLogger.log(error, accommodationIdBooking);
					errorList.clear();
					
					reviewDate = dateParser.parse(date);
					
					if(limitDate != null && reviewDate.before(limitDate))
						/*
						 * Si el review es anterior a la fecha límite, los siguientes también lo serán
						 * así que terminamos aquí.
						 */
						break;
					else{
						/*
						 * Sólo mete el review si es posterior a la fecha límite.
						 * Si limitDate == null no hay límite de fecha así que metemos la review
						 * independientemente de su fecha.
						 */
						JSONObject jsonReview = new JSONObject();
		               	
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_TYPE_TRIP), _stripReviewsBullet(typeTrip));
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_USERSEGMENT), _stripReviewsBullet(segment));
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_TYPE_ROOM), _stripReviewsBullet(typeRoom));
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_STAY_NIGHTS), _stripStayNights(_stripReviewsBullet(stayNights)));
//						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_SUBSEGMENT), !subSegment.equals(unknown) ? _getSubSegment(subSegment) : unknown);
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_WITH_PET), _stripReviewsBullet(withPet));
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_FROM), from);
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_DATE), date);
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_LANG), lang);
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_REVIEWGOOD), reviewGood);
						jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_REVIEWBAD), reviewBad);
			            jsonReview.put(configClass.getProperty(Constants.BS_CONSTANT_REVIEW_SCORE), score);
			            
			            jsonReviewsData.put(jsonReview);
					}
				} catch (Exception e) {
					logger.log("\tError scraping REVIEWS: " + elemReview.toString() + ". Message: " + e.getMessage());
					continue;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.print("Error.");
			logger.push("BookingScraperFacade._scrapReviews", LogPriority.ERROR);
		}
		
		return jsonReviewsData;
	}
	
	private String _getChildren (String childElementText) {
		String childrenAmount = "0";
		
		String childrenRegex = selectorsClass.getProperty(Constants.REGEX_PRICES_CHILDREN_AMOUNT); 
		Pattern pattern = Pattern.compile(childrenRegex);
		Matcher matcher = pattern.matcher(childElementText);
		if (matcher.matches()) 
			childrenAmount = matcher.group(1);
		
		return childrenAmount;
	}

//	private String _getSubSegment (String subSegmentElementText) {
//		String subSegment = "";
//		
//		String subsegmentRegex = selectorsClass.getProperty(Constants.REGEX_REVIEWS_SUBSEGMENT); 
//		Pattern pattern = Pattern.compile(subsegmentRegex);
//		Matcher matcher = pattern.matcher(subSegmentElementText);
//		if (matcher.matches()) 
//			subSegment = matcher.group(1);
//		
//		return subSegment;
//	}

	private String _stripPrice(String strPrice) {
		boolean digitFound = false;
		int euroSymbol = 0x20AC;
		StringBuffer buf = new StringBuffer(strPrice);
		int index = strPrice.indexOf(euroSymbol);
		if(index++ >= 0){	// Si hemos encontrado el símbolo del euro, lo saltamos y pasamos a los números
			buf = new StringBuffer();
			for(; index < strPrice.length(); index++){
				char nextChar = strPrice.charAt(index);
				if(Character.isDigit(nextChar) || nextChar == '.' || nextChar == ','){
					digitFound = true;
					if (nextChar != ',')	// 1,000 --> 1000
						buf.append(nextChar);
				}else if(!Character.isDigit(nextChar) && digitFound)
					break;
			}
		}
		return buf.toString().trim();
	}
	
	private double[] _stripCoords(String strCoords) {
		double lat = 0,
				lon = 0;
		
		String[] parts = strCoords.split(",");
		try{
			if(parts.length == 2){
				lat = Double.parseDouble(parts[1]);
				lon = Double.parseDouble(parts[0]);
			}
		}catch(NumberFormatException nfe){
			lat = 0;
			lon = 0;
		}
		
		return new double[]{lat, lon};
	}
	
	private int _stripCategory(String strCategory) {
		int category = -1;
		
		try{
			category = Integer.parseInt(strCategory);
		}catch(NumberFormatException nfe){
			category = -1;
		}
		
		return category;
	}
	
	private String _stripUrl(String aHref) {
		String bookingUrl = "";
		
		String[] parts = aHref.split("\\.", 2);
		bookingUrl = parts[0];
		
		return bookingUrl;
	}
	
	private String _stripReviewsBullet(String bullet) {
		String bulletClean = "";
		if (bullet != null)
//		 \u2022 símbolo del bullet que sale en "TYPE_TRIP", "TYPE_ROOM", "STAY_NIGHTS" y "SEGMENT"
			bulletClean = bullet.replace("\u2022", "").trim();
		return bulletClean;
	}
	
	private String _stripStayNights (String stayNights) {
		String stayNightsClean = null;
		
		if (stayNights != null) {
			String stayNightsRegex = selectorsClass.getProperty(Constants.REGEX_REVIEWS_STAY_NIGHTS); 
			Pattern pattern = Pattern.compile(stayNightsRegex);
			Matcher matcher = pattern.matcher(stayNights);
			if (matcher.matches()) 
				stayNightsClean = matcher.group(1);
		}
		return stayNightsClean;
	}
	/*
	 * Formato del array:
	 * 	+--------------+-----------+
	 *	| country code | accommodation URL |
	 *	+--------------+-----------+
	 */
//	private String[] _splitAccommodationUrl(String accommodationUrl) {
//		String[] parts = accommodationUrl.split("\\/");
//		return (parts.length >= 3 && parts[1].equals("hotel")) ? new String[]{parts[2], _stripUrl(parts[3])} : null;
//	}

	
	private String _getRegionHDEPricesUrl(int regionId, String accommodationType, String checkin, String checkout) {
		String regionUrl = null;
		
		regionUrl = configClass.getProperty(Constants.BK_HOST) + configClass.getProperty(Constants.BK_PATH_PRICES_HDE);
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_REGION), new Integer(regionId).toString());
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_PROPERTYTYPE), 
				configClass.getProperty(Constants.SCR_PRICES_HDE_PROPERTY_TYPE).replace(configClass.getProperty(Constants.BK_REPLACE_ACCTYPE), accommodationType));
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_RADIUS), configClass.getProperty(Constants.SCR_PRICES_HDE_RADIUS));
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_INDATE), checkin);
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_OUTDATE), checkout);
		regionUrl = regionUrl.replace(configClass.getProperty(Constants.BK_REPLACE_NROWS), configClass.getProperty(Constants.SCR_PRICES_HDE_ROWS));
		
		return regionUrl;
	}
	
	private String _getAccommodationPricesUrl(String accommodationIdBooking, String countryCode, String checkin, String checkout) {
		String accommodationUrl = null;
		
		accommodationUrl = configClass.getProperty(Constants.BK_HOST) + configClass.getProperty(Constants.BK_PATH_PRICES);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_ACCOMMODATIONID), accommodationIdBooking);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_COUNTRY), countryCode);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_INDATE), checkin);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_OUTDATE), checkout);
		
		return accommodationUrl;
	}
	
	private String _getAccommodationRatingsUrl(String accommodationIdBooking, String countryCode) {
		String accommodationUrl = null;
		
		accommodationUrl = configClass.getProperty(Constants.BK_HOST) + configClass.getProperty(Constants.BK_PATH_RATINGS);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_ACCOMMODATIONID), accommodationIdBooking);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_COUNTRY), countryCode);
		
		return accommodationUrl;
	}
	
	private String _getAccommodationReviewsUrl(String accommodationIdBooking, String countryCode) {
		String accommodationUrl = null;
		
		accommodationUrl = configClass.getProperty(Constants.BK_HOST) + configClass.getProperty(Constants.BK_PATH_REVIEWS);

		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_ACCOMMODATIONID), accommodationIdBooking);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_COUNTRY), countryCode);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_USER_SEGMENT), configClass.getProperty(Constants.BS_SEGMENT_TYPE));
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_NROWS), configClass.getProperty(Constants.SCR_REVIEWS_ROWS));
		
		return accommodationUrl;
	}
	
	private String _getAccommodationServicesUrl(String accommodationIdBooking, String countryCode) {
		String accommodationUrl = null;
		
		accommodationUrl = configClass.getProperty(Constants.BK_HOST) + configClass.getProperty(Constants.BK_PATH_SERVICES);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_ACCOMMODATIONID), accommodationIdBooking);
		accommodationUrl = accommodationUrl.replace(configClass.getProperty(Constants.BK_REPLACE_COUNTRY), countryCode);
		
		return accommodationUrl;
	}


}
