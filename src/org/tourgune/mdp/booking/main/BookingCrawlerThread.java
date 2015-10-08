package org.tourgune.mdp.booking.main;

import org.json.JSONObject;
import org.torugune.mdp.log.DatabaseLogger;
import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.booking.facade.BookingScraperFacade;
import org.tourgune.mdp.booking.utils.BookingConfig;
import org.tourgune.mdp.booking.utils.Constants;

public class BookingCrawlerThread extends Thread {

	private BookingScraperFacade bookingFacade = BookingScraperFacade.getInstance();
	BookingConfig configClass = BookingConfig.getInstance();
	
	private int id;
	
	private String accommodationName;
	private int regionId;
	private String accommodationType;
	private String countryCode;
	private String checkin;
	private String checkout;
	private String task;
	private Logger logger;
	private DatabaseLogger dbLogger;
	private JSONObject jsonDayPrices;
	
	private boolean available;
	private boolean ready;
	private boolean die;
	
	public BookingCrawlerThread(int id){
		this.id = id;
		
		this.available = true;
		this.ready = false;
		this.die = false;
		this.jsonDayPrices = null;
		
		start();
	}

	public JSONObject scrapPrices(String accommodationName, Integer regionId, String accommodationType, String countryCode, String checkin, String checkout, String task, Logger logger, DatabaseLogger dbLogger) {
		this.accommodationName = accommodationName;
		this.regionId = regionId;
		this.accommodationType = accommodationType;
		this.countryCode = countryCode;
		this.checkin = checkin;
		this.checkout = checkout;
		this.task = task;
		this.logger = logger;
		this.dbLogger = dbLogger;
		
		setReady(true);
		
		return jsonDayPrices;
	}
	
	public JSONObject getLastPrice(){
		setAvailable(true);	// Si no al final se queda colgado
		return jsonDayPrices;
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
			
			try {
				if (configClass.getProperty(Constants.BS_CONSTANT_TASK_PRICES_HDE).equals(task)) {
					logger.log("\t" + this.getName() + " --> REGION: " + regionId + " || Checkin: " + checkin + " || Checkout: " + checkout);
					jsonDayPrices = bookingFacade.getRegionHDEPrices(regionId, accommodationType, checkin, checkout, logger, dbLogger);
					logger.log("\t" + this.getName() + " DONE --> REGION: " + regionId + " || Checkin: " + checkin + " || Checkout: " + checkout);
				} else {
					logger.log("\t" + this.getName() + " --> Accommodation: " + accommodationName + " || Checkin: " + checkin + " || Checkout: " + checkout);
					jsonDayPrices = bookingFacade.getAccommodationPrices(accommodationName, countryCode, checkin, checkout, logger, dbLogger);
					logger.log("\t" + this.getName() + " DONE --> Accommodation: " + accommodationName + " || Checkin: " + checkin + " || Checkout: " + checkout);
				}
				
				
			} catch(Exception e) {
				logger.log(this.getName() + "EXCEPTION 'Exception: " + e.getMessage(), "BookingCrawlerThread.scrapPrices", LogPriority.ERROR);
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
