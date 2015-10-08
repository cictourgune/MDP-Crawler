package org.tourgune.mdp.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ErrorDescriptions {

	Properties properties = null;
	 
    public final static String CONFIG_FILE = "properties/error_descriptions.properties";
    
    private ErrorDescriptions() {
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
    public static ErrorDescriptions getInstance() {
        return ConfigurationHolder.INSTANCE;
    }
 
    private static class ConfigurationHolder {
 
        private static final ErrorDescriptions INSTANCE = new ErrorDescriptions();
    }

    /**
     * Sustituye todos los símbolos <code>?</code> que encuentra por el valor del array <code>params</code>
     * de la misma posición.
     * Si hay más símbolos <code>?</code> que valores en el array <code>params</code>, sustituye los primeros hasta
     * agotar todos los valores del array y deja el resto de los símbolos tal cual.
     * Si hay más valores en el array <code>params</code> que símbolos <code>?</code>, sustituye todos los símbolos
     * e ignora los valores restantes del array.
     * 
     * @param errorDesc	Cadena con símbolos <code>?</code> (opcionalmente) que se sustituirán.
     * @param params Array de strings cuyos valores serán los que sustituyan a los símbolos <code>?</code>
     * en el mismo orden en el que aparecen.
     * @return La cadena <code>errorDesc</code> con los símbolos <code>?</code> sustituídos por los valores
     * del array <code>params</code>.
     */
    public String generateErrorString(String errorDesc, String[] params) {
    	int paramCount = 0;
    	StringBuffer errorString = new StringBuffer();	// StringBuffer es thread-safe. StringBuilder no.
    	
    	for(int characterIndex = 0; characterIndex < errorDesc.length(); characterIndex++){
    		char curChar = errorDesc.charAt(characterIndex);
    		if(curChar == '?' && paramCount < params.length)
    			errorString.append(params[paramCount++]);
    		else
    			errorString.append(curChar);
    	}
    	
    	return errorString.toString();
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
