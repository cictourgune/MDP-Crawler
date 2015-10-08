package org.tourgune.mdp.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Selectors {
	
	
	Properties properties = null;
	 
    public final static String CONFIG_FILE = "properties/selectors.properties";
    
    private Selectors() {
        this.properties = new Properties();
        try {
//            properties.load(Selectors.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
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
    public static Selectors getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final Selectors INSTANCE = new Selectors();
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
    
    /**
     * Devuelve las propiedades de configuración solicitadas,
     * como un array de valores con sus respectivas claves.
     * 
     * @param keys Claves de las propiedades de configuración solicitadas.
     * @return Array de Strings con los valores de las propiedades de configuración solicitadas,
     * en el mismo orden que el array de claves <code>keys</code>.
     */
    public String[] getProperty(String[] keys) {
    	String[] properties = new String[keys.length];
    	
    	for(int keyIndex = 0; keyIndex < keys.length; keyIndex++)
    		properties[keyIndex] = this.properties.getProperty(keys[keyIndex]);
    	
    	return properties;
    }
}
