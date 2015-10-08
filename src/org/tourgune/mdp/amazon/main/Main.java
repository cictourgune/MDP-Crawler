package org.tourgune.mdp.amazon.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.torugune.mdp.log.EchoLogger;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.amazon.facade.AmazonMainFacade;
import org.tourgune.mdp.amazon.facade.AmazonScraperFacade;
import org.tourgune.mdp.amazon.utils.AmazonConfig;
import org.tourgune.mdp.amazon.utils.Constants;

public class Main {
	
	private static AmazonConfig configClass = AmazonConfig.getInstance();
	
	public static void main(String[] args) {
		long tsInicio = 0, tsFin = 0;
		double dbSizeInitial = 0;
		double dbSizeFinal = 0;
		HashMap<Integer, String> hashAccommodationTypes = null;
		String country = null, nuts2 = null, nuts3 = null, locality = null, task = null, accommodationType = null;
		
		Logger logger = null;
		
		
		AmazonMainFacade mainFacade = new AmazonMainFacade();
		AmazonScraperFacade amazonFacade = AmazonScraperFacade.getInstance();
		
		try {
			logger = new EchoLogger("Log");

			dbSizeInitial = amazonFacade.getDbSize(logger);
			logger.log("Initial database size: " + dbSizeInitial + " MB");
			
			tsInicio = new Date().getTime();
			logger.log("Started AmazonScraper.");
			
			try {
				country = args[0];
				nuts2 = args[1];
				nuts3 = args[2];
				locality = args[3];
				task = args[4];
				accommodationType = args[5];
				
				// Verificar TASK
				String validTasks = configClass.getProperty(Constants.VALID_TASKS);
				String[] validTasksArray = validTasks.split(",");
				List<String> listValidTasks = Arrays.asList(validTasksArray);
				if (!listValidTasks.contains(task)) {
					logger.print("[MDP] Arguments: args[4] <task> is not valid. Posible values: " + listValidTasks.toString());
					logger.push();
					System.exit(0);
				}
				
				// Verificar ACCOMMODATION_TYPE
				hashAccommodationTypes = amazonFacade.getAvailableAccommodationTypes(logger);
				if (!hashAccommodationTypes.containsKey(Integer.parseInt(accommodationType))) {
					logger.print("[MDP] Arguments: args[5] <accommodationType> is not valid. Possible values: [");
					Set<Integer> accTypes = hashAccommodationTypes.keySet();
					for(Iterator<Integer> it = accTypes.iterator(); it.hasNext();){
						int accTypeId = it.next();
						String accTypeName = hashAccommodationTypes.get(accTypeId);
						logger.print("(" + accTypeId + " for " + accTypeName + ")");
					}
					logger.print("]");
					logger.push();
					System.exit(0);
				}
			} catch (ArrayIndexOutOfBoundsException aioobe) { // Si no se pasan params pega una excepción
				logger.print("[MDP] Arguments: <country> <nuts2> <nuts3> <locality> <task> <accommodationType>(for hotels enter 204).");
				logger.print("[MDP] Possible options for <country>, <nuts2>, <nuts3>, and <locality>: all/null/notnull/<value>");
				logger.push();
				System.exit(0);
			}
						
			mainFacade.work(logger, country, nuts2, nuts3, locality, task, accommodationType);
			
			tsFin = new Date().getTime();
			logger.log("Finished AmazonScraper.");
			
			GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			calendar.setTime(new Date(tsFin - tsInicio));
			String totalTime = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
			logger.log("Total time: " + totalTime);
			
			dbSizeFinal = amazonFacade.getDbSize(logger);
			logger.log("Final database size: " + dbSizeFinal + " MB");
			
			mainFacade.insertAdminInfo(tsInicio, tsFin, totalTime, dbSizeFinal-dbSizeInitial, logger);
			
			logger.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.log("[MDP] ERROR - Main. Uncatchable error");
		}
	}
}
