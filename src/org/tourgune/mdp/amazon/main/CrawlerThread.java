package org.tourgune.mdp.amazon.main;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import org.json.JSONArray;
import org.torugune.mdp.log.DatabaseLogger;
import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.amazon.exception.HttpParseException;
import org.tourgune.mdp.amazon.facade.AmazonScraperFacade;
import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;
import org.tourgune.mdp.booking.facade.BookingMainFacade;
import org.tourgune.mdp.misc.db.Database;

public class CrawlerThread extends Thread {

	private AmazonScraperFacade amazonFacade = AmazonScraperFacade.getInstance();
	AmazonConfig configClass = AmazonConfig.getInstance();
	
	private int id;
	
	private int accommodationId;
	private String accommodationName;
	private int regionId;
	private String accommodationType;
	private String countryCode;
	private String task;
	private Logger logger;
	private DatabaseLogger dbLogger;
	
	private boolean available;
	private boolean ready;
	private boolean die;
	
	public CrawlerThread(int id){
		this.id = id;
		
		this.available = true;
		this.ready = false;
		this.die = false;
		
		start();
	}

	public void scrap(Integer accommodationId, String accommodationName, Integer regionId, String accommodationType, String countryCode, String task, Logger logger, DatabaseLogger dbLogger) {

		this.accommodationId = accommodationId;
		this.accommodationName = accommodationName;
		this.regionId = regionId;
		this.accommodationType = accommodationType;
		this.countryCode = countryCode;
		this.task = task;
		this.logger = logger;
		this.dbLogger = dbLogger;
		
		setReady(true);
	}
	
	@Override
	public void run() {
		while(!isDie()) {
			while(!isReady() && !isDie()) {
				try {
					sleep(10);
				} catch(Exception ex) {	}
			}
			if(isDie())
				break;
			
			JSONArray response = new JSONArray();
			Database db = new Database();
			Connection con = null;
			Date reviewLimitDate = null;
			try {
				con = db.connect();
				BookingMainFacade booking = new BookingMainFacade(); // Revisar si merece la pena hacerlo con new o que sea Singleton
				
				if (configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES_HDE).equals(task)) {
					response = booking.scrapHDEPrices(regionId, accommodationType, task, logger, dbLogger);
					logger.log("\t" + Thread.currentThread().getName() + " --> response received!");
					if(response != null) {
						amazonFacade.insertPricesHDE(response, con, regionId, accommodationType, logger);
					}
					logger.log(Thread.currentThread().getName() + " REGION: " + regionId + " --> Done");
				} else {
					if (configClass.getProperty(Constants.BS_CONSTANT_TASK_REVIEWS).equals(task)) {
						reviewLimitDate = amazonFacade.getLatestReviewDate(con, accommodationId, logger);
					}
					response = booking.scrap(accommodationName, countryCode, task, reviewLimitDate, logger, dbLogger);
					logger.log("\t" + Thread.currentThread().getName() + " --> response received!");
					if(response != null) {
						if (configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES).equals(task)){
							amazonFacade.insertProducts(response, con, accommodationId, logger);
						} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_RATINGS).equals(task)){
							amazonFacade.insertRatings(response, con, accommodationId, logger);
						} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_REVIEWS).equals(task)){
							amazonFacade.insertReviews(response, con, accommodationId, logger);
						} else if (configClass.getProperty(Constants.BS_CONSTANT_TASK_SERVICES).equals(task)){
							amazonFacade.insertServices(response, con, accommodationId, logger);
						}
					}	
					logger.log(Thread.currentThread().getName() + " ACCOMMODATION: " + accommodationName + " (" + accommodationId + ") --> Done");
				}
			} catch(HttpParseException e){
				e.printStackTrace();
				logger.log(Thread.currentThread().getName() + "EXCEPTION 'HttpParseException': " + e.getMessage(), "AmazonScraperFacade.scrap" + task, LogPriority.ERROR);
			} catch(IOException e){
				e.printStackTrace();
				logger.log(Thread.currentThread().getName() + "EXCEPTION 'IOException': " + e.getMessage(), "AmazonScraperFacade.scrap" + task, LogPriority.ERROR);
			} catch(Exception e) {
				e.printStackTrace();
				logger.log(Thread.currentThread().getName() + "EXCEPTION 'Exception: " + e.getMessage(), "AmazonScraperFacade.scrap" + task, LogPriority.ERROR);
			}
			try {
				db.disconnect();
			} catch (Exception dbExc) {
				logger.log("EXCEPTION: Problem trying to disconnecting from database", LogPriority.ERROR);
			}
			setReady(false);
			setAvailable(true);
		}
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public boolean isDie() {
		return die;
	}

	public void setDie(boolean die) {
		this.die = die;
	}
}
