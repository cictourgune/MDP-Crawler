package org.tourgune.mdp.booking.facade;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.torugune.mdp.log.DatabaseLogger;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.booking.main.BookingCrawlerThread;
import org.tourgune.mdp.booking.main.ThreadManager;
import org.tourgune.mdp.booking.utils.BookingConfig;
import org.tourgune.mdp.booking.utils.Constants;
import org.tourgune.misc.utils.CustomConfig;

public class BookingMainFacade {

	ThreadManager threadmanager = null;
	BookingScraperFacade bookingFacade = BookingScraperFacade.getInstance();
	BookingConfig configClass = BookingConfig.getInstance();
	CustomConfig customConfig = CustomConfig.getInstance();

	public JSONArray scrap(String accommodationIdBooking, String countryCode, String task, Date reviewLimitDate, Logger logger, DatabaseLogger dbLogger) {
		
		JSONArray json = new JSONArray();
		
		// Se llama al método que realiza la task especificada
		if (configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES).equals(task)){
			json = scrapAccommodationPrices(accommodationIdBooking, countryCode, task, logger, dbLogger);
		} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_RATINGS).equals(task)){
			json = scrapAccommodationRatings(accommodationIdBooking, countryCode, logger, dbLogger);
		} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_REVIEWS).equals(task)){
			json = scrapAccommodationReviews(accommodationIdBooking, countryCode, reviewLimitDate, logger, dbLogger);
		} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_SERVICES).equals(task)){
			json = scrapAccommodationServices(accommodationIdBooking, countryCode, logger, dbLogger);
		}
		
		return json;
	}
	
	public JSONArray scrapHDEPrices(int regionId, String accommodationType, String task, Logger logger, DatabaseLogger dbLogger) {
		
		// Se llama al método que realiza la task especificada
		JSONArray json = scrapRegionHDEPrices(regionId, accommodationType, task, logger, dbLogger);
		return json;
	}
	
	private JSONArray scrapRegionHDEPrices(int regionId, String accommodationType, String task, Logger logger, DatabaseLogger dbLogger) {
		logger.log("REGION HDE PRICES --> Region ID: " + regionId);
		
//		// Por seguir el mismo formato (devolver de un JSONArray), utilizamos un Array y un JSONObject
//		JSONArray jsonRegionHDEPrices= new JSONArray();
//		JSONObject jsonHDEPrices = new JSONObject();
//		
//		jsonHDEPrices = bookingFacade.getRegionHDEPrices(regionId, logger);
//		
//		jsonRegionHDEPrices.put(jsonHDEPrices);
//		return jsonRegionHDEPrices;
		
		int i = 0, ttlAccommodationPrices = 0;
		String checkin = null;
		String checkout = null;
		JSONArray jsonRegonHDEPrices = new JSONArray();
		JSONObject jsonDayHDEPrices = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calCheckin, calCheckout;
		
		// 0. Creamos el ThreadManager
//		threadmanager = new ThreadManager(new Integer(configClass.getProperty(Constants.THREAD_COUNT)));
		threadmanager = new ThreadManager(new Integer(customConfig.getProperty(Constants.THREAD_COUNT)));
				
		// 1. Se obtienen las fechas y duración
//		String propDates = configClass.getProperty(Constants.SCR_DATES);
		String propDates = customConfig.getProperty(Constants.SCR_DATES);
		String[] arrayDates = propDates.split(",");
//		int lengthOfStay = new Integer(configClass.getProperty(Constants.SCR_LENGTH_OF_STAY));
		int lengthOfStay = new Integer(customConfig.getProperty(Constants.SCR_LENGTH_OF_STAY));
		
		logger.log("REGION HDE PRICES --> Region ID: " + regionId + " || arrayDates: " + Arrays.toString(arrayDates) + " || lengthOfStay: " + lengthOfStay);
		
		// 2. Se itera por los días
		for(i = 0; i < arrayDates.length || ttlAccommodationPrices < arrayDates.length; i++){
			if(i < arrayDates.length){
				// Calcula las fechas de entrada (checkin) y salida (checkout)
				int days = new Integer(arrayDates[i]).intValue();
				calCheckin = Calendar.getInstance();
				calCheckout = Calendar.getInstance();
				calCheckin.add(Calendar.DAY_OF_YEAR, days);
				calCheckout.add(Calendar.DAY_OF_YEAR, days + lengthOfStay);
				checkin = sdf.format(calCheckin.getTime());
				checkout = sdf.format(calCheckout.getTime());
				// Pon el hilo en marcha para que empiece a sacar los precios
				jsonDayHDEPrices = threadmanager.getThread().scrapPrices("", regionId, accommodationType, "", checkin, checkout, task, logger, dbLogger);
			}else{
				// Ya hemos mandado peticiones para todas las fechas, así que sólo nos queda recoger las que están pendientes de procesar
				BookingCrawlerThread thread = threadmanager.getThread();
				jsonDayHDEPrices = thread.getLastPrice();
				thread.setDie(true);	// Ya hemos terminado con este hilo, así que lo matamos para que no volvamos a coger el mismo valor
			}
			
			if(jsonDayHDEPrices != null){
				// Mete los precios obtenidos en el array JSON para devolvérselo al cliente
				jsonRegonHDEPrices.put(jsonDayHDEPrices);
				ttlAccommodationPrices++;
			}
			
			checkin = null;
			checkout = null;
			jsonDayHDEPrices = null;
		}
		
		threadmanager.killThreads();	// Matamos todos los hilos, por si quedase alguno vivo
		logger.log("Total accommodation prices: " + ttlAccommodationPrices);
		return jsonRegonHDEPrices;
	}
	
	private JSONArray scrapAccommodationServices(String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) {
		logger.log("SERVICES --> Accommodation ID: " + accommodationIdBooking, Thread.currentThread().getName());
		
		// Por seguir el mismo formato (devolver de un JSONArray), utilizamos un Array y un JSONObject
		JSONArray jsonAccommodationServices = new JSONArray();
		JSONObject jsonServices = new JSONObject();
		
		try {
			jsonServices = bookingFacade.getAccommodationServices(accommodationIdBooking, countryCode, logger, dbLogger);
			jsonAccommodationServices.put(jsonServices);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return jsonAccommodationServices;
	}
	
	private JSONArray scrapAccommodationRatings(String accommodationIdBooking, String countryCode, Logger logger, DatabaseLogger dbLogger) {
		logger.log("RATINGS --> Accommodation ID: " + accommodationIdBooking);
		
		// Por seguir el mismo formato (devolver de un JSONArray), utilizamos un Array y un JSONObject
		JSONArray jsonAccommodationRatings = new JSONArray();
		JSONObject jsonRatings = new JSONObject();
		
		try {
			jsonRatings = bookingFacade.getAccommodationRatings(accommodationIdBooking, countryCode, logger, dbLogger);
			jsonAccommodationRatings.put(jsonRatings);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return jsonAccommodationRatings;
	}
	private JSONArray scrapAccommodationReviews(String accommodationIdBooking, String countryCode, Date limitDate, Logger logger, DatabaseLogger dbLogger) {
		logger.log("REVIEWS --> Accommodation ID: " + accommodationIdBooking);
		
		JSONArray jsonReviewsArray = new JSONArray();
		JSONObject jsonAccommodationReviews = new JSONObject();
		
		try {
			jsonAccommodationReviews = bookingFacade.getAccommodationReviews(accommodationIdBooking, countryCode, limitDate, logger, dbLogger);
			jsonReviewsArray.put(jsonAccommodationReviews);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return jsonReviewsArray;
	}

	private JSONArray scrapAccommodationPrices(String accommodationName, String countryCode, String task, Logger logger, DatabaseLogger dbLogger) {
		
		int i = 0, ttlAccommodationPrices = 0;
		String checkin = null;
		String checkout = null;
		JSONArray jsonAccommodationPrices = new JSONArray();
		JSONObject jsonDayPrices = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calCheckin, calCheckout;
		
		// 0. Creamos el ThreadManager
//		threadmanager = new ThreadManager(new Integer(configClass.getProperty(Constants.THREAD_COUNT)));
		threadmanager = new ThreadManager(new Integer(customConfig.getProperty(Constants.THREAD_COUNT)));
				
		// 1. Se obtienen las fechas y duración
//		String propDates = configClass.getProperty(Constants.SCR_DATES);
		String propDates = customConfig.getProperty(Constants.SCR_DATES);
		String[] arrayDates = propDates.split(",");
//		int lengthOfStay = new Integer(configClass.getProperty(Constants.SCR_LENGTH_OF_STAY));
		int lengthOfStay = new Integer(customConfig.getProperty(Constants.SCR_LENGTH_OF_STAY));
		
		logger.log("PRICES --> Accommodation Name: " + accommodationName + " || arrayDates: " + Arrays.toString(arrayDates) + " || lengthOfStay: " + lengthOfStay);
		
		// 2. Se itera por los días
		for(i = 0; i < arrayDates.length || ttlAccommodationPrices < arrayDates.length; i++){
			if(i < arrayDates.length){
				// Calcula las fechas de entrada (checkin) y salida (checkout)
				int days = new Integer(arrayDates[i]).intValue();
				calCheckin = Calendar.getInstance();
				calCheckout = Calendar.getInstance();
				calCheckin.add(Calendar.DAY_OF_YEAR, days);
				calCheckout.add(Calendar.DAY_OF_YEAR, days + lengthOfStay);
				checkin = sdf.format(calCheckin.getTime());
				checkout = sdf.format(calCheckout.getTime());
				// Pon el hilo en marcha para que empiece a sacar los precios
				jsonDayPrices = threadmanager.getThread().scrapPrices(accommodationName, 0, "", countryCode, checkin, checkout, task, logger, dbLogger);
			}else{
				// Ya hemos mandado peticiones para todas las fechas, así que sólo nos queda recoger las que están pendientes de procesar
				BookingCrawlerThread thread = threadmanager.getThread();
				jsonDayPrices = thread.getLastPrice();
				thread.setDie(true);	// Ya hemos terminado con este hilo, así que lo matamos para que no volvamos a coger el mismo valor
			}
			
			if(jsonDayPrices != null){
				// Mete los precios obtenidos en el array JSON para devolvérselo al cliente
				jsonAccommodationPrices.put(jsonDayPrices);
				ttlAccommodationPrices++;
			}
			
			checkin = null;
			checkout = null;
			jsonDayPrices = null;
		}
		
		threadmanager.killThreads();	// Matamos todos los hilos, por si quedase alguno vivo
		logger.log("Total accommodation prices: " + ttlAccommodationPrices);
		return jsonAccommodationPrices;
	}
	
	public static Date addDays(Date baseDate, int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }
}
