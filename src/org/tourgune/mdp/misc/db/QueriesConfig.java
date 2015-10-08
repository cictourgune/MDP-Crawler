package org.tourgune.mdp.misc.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.tourgune.mdp.misc.db.QueriesConfig;

public class QueriesConfig {

	Properties properties = null;
	 
    public final static String QUERIES_FILE = "properties/queries.properties";
 
    private QueriesConfig() {
        this.properties = new Properties();
        try {
//            properties.load(Queries.class.getClassLoader().getResourceAsStream(QUERIES_FILE));
        	// Necesario hacerlo así para que pueda coger los properties 
        	// a partir de la carpeta donde se encuentre el JAR
        	properties.load(new FileInputStream(QUERIES_FILE)); 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//Configuration
 
    /**
     * Implementando Singleton
     *
     * @return
     */
    public static QueriesConfig getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final QueriesConfig INSTANCE = new QueriesConfig();
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
