package org.tourgune.mdp.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BookingConfig {
	
	
	Properties properties = null;
	 
    public final static String CONFIG_FILE = "properties/booking_config.properties";
    
    private BookingConfig() {
        this.properties = new Properties();
        try {
//            properties.load(BookingConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
        	// Necesario hacerlo así para que pueda coger los properties 
        	// a partir de la carpeta donde se encuentre el JAR
        	properties.load(new FileInputStream(CONFIG_FILE)); 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//Configuration
 
    /**
     * Implementando Singleton
     *
     * @return
     */
    public static BookingConfig getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final BookingConfig INSTANCE = new BookingConfig();
    }
 
    /**
     * Devuelve la propiedad de configuración solicitada
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
//    	System.out.println("hago el getproperty con el key " + key + " y devuelvo el valor " + this.properties.getProperty(key));
    	return this.properties.getProperty(key);
    }
}
