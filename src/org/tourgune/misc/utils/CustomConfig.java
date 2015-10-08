package org.tourgune.misc.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CustomConfig {
	
	
	Properties properties = null;
	 
    public final static String CONFIG_FILE = "properties/custom.properties";
    
    private CustomConfig() {
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
    public static CustomConfig getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final CustomConfig INSTANCE = new CustomConfig();
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
