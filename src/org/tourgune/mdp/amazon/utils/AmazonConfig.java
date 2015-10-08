package org.tourgune.mdp.amazon.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AmazonConfig {
	
	
	Properties properties = null;
	 
    public final static String CONFIG_FILE = "properties/amazon_config.properties";
    
    private AmazonConfig() {
        this.properties = new Properties();
        try {
        	//properties.load(AmazonConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
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
    public static AmazonConfig getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final AmazonConfig INSTANCE = new AmazonConfig();
    }
 
    /**
     * Devuelve la propiedad de configuraci�n solicitada
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
//    	System.out.println("hago el getproperty con el key " + key + " y devuelvo el valor " + this.properties.getProperty(key));
    	return this.properties.getProperty(key);
    }
}
