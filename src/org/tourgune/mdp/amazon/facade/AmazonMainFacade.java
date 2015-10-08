package org.tourgune.mdp.amazon.facade;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.torugune.mdp.log.DatabaseLogger;
import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.amazon.bean.Accommodation;
import org.tourgune.mdp.amazon.bean.Admin;
import org.tourgune.mdp.amazon.bean.Region;
import org.tourgune.mdp.amazon.dao.AdminDao;
import org.tourgune.mdp.amazon.main.ThreadManager;
import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.misc.db.DatabaseConfig;
import org.tourgune.mdp.misc.db.MiscConstants;
import org.tourgune.misc.utils.CustomConfig;

public class AmazonMainFacade {

	ThreadManager threadmanager = null;
	AmazonScraperFacade amazonFacade = AmazonScraperFacade.getInstance();
	AmazonConfig configClass = AmazonConfig.getInstance();
	CustomConfig customConfig = CustomConfig.getInstance(); 
	DatabaseConfig dbConfigClass = DatabaseConfig.getInstance();

	public void work(Logger logger, String country, String nuts2, String nuts3, String locality, String task, String accommodationType) {
		String taskHDEPrices = configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES_HDE);
		
		try{
			Map<String, String> dbProperties = new HashMap<String, String>(5, 1f);
			dbProperties.put(DatabaseLogger.DB_HOST, dbConfigClass.getProperty(MiscConstants.DB_HOST));
			dbProperties.put(DatabaseLogger.DB_PORT, dbConfigClass.getProperty(MiscConstants.DB_PORT));
			dbProperties.put(DatabaseLogger.DB_NAME, dbConfigClass.getProperty(MiscConstants.DB_DB));
			dbProperties.put(DatabaseLogger.DB_USER, dbConfigClass.getProperty(MiscConstants.DB_USER));
			dbProperties.put(DatabaseLogger.DB_PASS, dbConfigClass.getProperty(MiscConstants.DB_PASS));
			DatabaseLogger dbLogger = new DatabaseLogger(task, dbProperties);	//OJO en 'task'!
			
			Map<Integer, Integer> mapRegions = new HashMap<Integer, Integer>();
			Map<Integer, String> mapAccommodationUrls = new HashMap<Integer, String>();
			
			// 0. Creamos el ThreadManager
//			threadmanager = new ThreadManager(new Integer(configClass.getProperty(Constants.THREAD_COUNT)));
			threadmanager = new ThreadManager(new Integer(customConfig.getProperty(Constants.THREAD_COUNT)));
			
			// Se revisa el tipo de tarea para derivar a trabajar individualmente con accommodationes o con listados de accommodationes
			if (task.equals(taskHDEPrices)) {
				mapRegions = getRegions(logger, country, nuts2, nuts3, locality);
				logger.log("Regions found: " + mapRegions.size());
				scrapHDEPrices(mapRegions, task, accommodationType, logger, dbLogger);
			} else {
				// 1. Se obtienen las URLS de los alojamientos del sistema
				mapAccommodationUrls = getAccommodations(logger, country, nuts2, nuts3, locality, accommodationType);
				logger.log("Accommodations found: " + mapAccommodationUrls.size());
				scrapAccommodations(task, mapAccommodationUrls, logger, dbLogger);
			}
			
			dbLogger.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private Map<Integer, Integer> getRegions(Logger logger, String country, String nuts2, String nuts3, String locality) {
		Map<Integer, Integer> mapHDEPrices = new HashMap<Integer, Integer>();
		try {
			List<Region> listRegions = amazonFacade.selectScrappableRegions(country, nuts2, nuts3, locality);
			Iterator<Region> it = listRegions.iterator();
			while(it.hasNext()) {
				Region region = (Region) it.next();
				mapHDEPrices.put(region.getIdGeography(), region.getIdGeographyChannel());
			}
		} catch (Exception e) {
			logger.log("EXCEPTION 'Exception': " + e.getMessage(), "AmazonScraperFacade.getAccommodations", LogPriority.ERROR);
		}
		return mapHDEPrices;
	}
	
	private Map<Integer, String> getAccommodations(Logger logger, String country, String nuts2, String nuts3, String locality, String accommodationType) {
		Map<Integer, String> mapAccommodationUrls = new HashMap<Integer, String>();
		try {
			List<Accommodation> listAccommodations = amazonFacade.selectScrappableAccommodations(country, nuts2, nuts3, locality, accommodationType);
			Iterator<Accommodation> it = listAccommodations.iterator();
			while(it.hasNext()) {
				Accommodation accommodation = (Accommodation) it.next();
				mapAccommodationUrls.put(accommodation.getIdAccommodation(), accommodation.getUrlAccommodationChannel());
			}
		} catch (Exception e) {
			logger.log("EXCEPTION 'Exception': " + e.getMessage(), "AmazonScraperFacade.getAccommodations", LogPriority.ERROR);
		}
		return mapAccommodationUrls;
	}
	
	
	private void scrapAccommodations(String task, Map<Integer, String> accommodationUrls, Logger logger, DatabaseLogger dbLogger) {
		Set<Integer> accommodationIds = accommodationUrls.keySet();
		logger.log("Accommodationes a scrapear (" + accommodationUrls.size() + ") " + accommodationUrls.toString());
		for(Iterator<Integer> it = accommodationIds.iterator(); it.hasNext();){
			int accommodationId = it.next();
			String accommodationUrl = accommodationUrls.get(accommodationId);
			String countryCode = _getCountryCode(accommodationUrl);
			String accommodationName = _getAccommodationFromUrl(accommodationUrl);
			threadmanager.getThread().scrap(accommodationId, accommodationName, 0, "", countryCode, task, logger, dbLogger);
		}
		threadmanager.killThreads();
	}

	private void scrapHDEPrices(Map<Integer, Integer> map, String task, String accommodationType, Logger logger, DatabaseLogger dbLogger) {
		Set<Integer> ids = map.keySet();
		logger.log("Regiones a scrapear (" + map.size() + ") " + map.toString());
		for(Iterator<Integer> it = ids.iterator(); it.hasNext();){
			int regionId = it.next();
			Integer bookingRegionId = map.get(regionId);
			threadmanager.getThread().scrap(0, "", bookingRegionId, accommodationType, "", task, logger, dbLogger);
		}
		threadmanager.killThreads();
	}
	
	private String _getAccommodationFromUrl(String accommodationUrl) {
		String accommodationName = null;

		String accommodationRegex = ".*/hotel/.+/(.*)"; 
		Pattern pattern = Pattern.compile(accommodationRegex);
		Matcher matcher = pattern.matcher(accommodationUrl);
		if (matcher.matches()) 
			accommodationName = matcher.group(1);
		
		return accommodationName;
	}

	private String _getCountryCode(String accommodationUrl) {
		String countryCode = null;

		String countryRegex = ".*/hotel/(.+)/.*"; 
		Pattern pattern = Pattern.compile(countryRegex);
		Matcher matcher = pattern.matcher(accommodationUrl);
		if (matcher.matches()) 
			countryCode = matcher.group(1);
		
		return countryCode;
	}
	
	public void insertAdminInfo (long start, long end, String totalTime, double dataSize, Logger logger) throws Exception {
		AdminDao adminDao = new AdminDao();
		Admin admin = new Admin();
		try {
			String instance = customConfig.getProperty(Constants.INSTANCE_NAME);
			Timestamp startTime = new Timestamp(start);
			Timestamp endTime = new Timestamp(end);
			
			admin.setInstance(instance);
			admin.setStartTime(startTime);
			admin.setEndTime(endTime);
			admin.setTotalTime(totalTime);
			admin.setDataSize(dataSize);
			
			adminDao.insertAdminInfo(admin);
			
		} catch (Exception e) {
			logger.log("EXCEPTION 'Exception': " + e.getMessage(), "AmazonMainFacade.insertAdminInfo", LogPriority.ERROR);
		}
	}
}
