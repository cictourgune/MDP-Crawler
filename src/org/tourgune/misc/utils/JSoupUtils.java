package org.tourgune.misc.utils;

import java.util.Arrays;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.booking.exception.SelectorFailedException;

public class JSoupUtils {
	
	public static String selectElementValueOrAttr (Element rootElement, Object selectorObj, Logger logger) throws Exception {
		Elements children = null;
		String elemValue = "";
		String singleSelector = null, multipleSelectors[] = null;
		String selector = null;
		String selectorArray[] = null;
		String attribute = null;
		int multipleSelectorIndex = 0;
		
		try{
			singleSelector = (String) selectorObj;			// 'selector' es un único selector
		}catch(ClassCastException cce){
			multipleSelectors = (String[]) selectorObj;	// 'selector' es un array de selectores
		}
		
		// Tenemos el/los selector/es necesarios. Ahora seleccionamos los hijos.
		if(singleSelector != null) {
			selectorArray = singleSelector.split("--");
			
			// Cogemos la parte del selector
			selector = selectorArray[0].trim();
			try {
				// COgemos el atributo (si lo tiene)
				attribute = selectorArray[1].trim();
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				// NO pasa nada, no tiene atributo, se recogerá el texto
			}
			if (!selector.isEmpty())
				children = rootElement.select(selector);
		}else{
			// Si tenemos varios selectores, cogemos el primero que funcione.
			do{
				selectorArray = multipleSelectors[multipleSelectorIndex++].split("--");
				
				// Cogemos la parte del selector
				selector = selectorArray[0].trim();
				try {
					// COgemos el atributo (si lo tiene)
					attribute = selectorArray[1].trim();
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					// NO pasa nada, no tiene atributo, se recogerá el texto
				}
				if (!selector.isEmpty())
					children = rootElement.select(selector);
			}while(children.size() == 0 && multipleSelectorIndex < multipleSelectors.length);
		}
		
		if(children != null && children.size() == 0){
			throw new SelectorFailedException("ERROR JSoupUtils.selectElementText parsing -->" + Arrays.toString((String[]) selectorArray) + "<--");
//			String[] selectorArray = {(singleSelector != null ? singleSelector : multipleSelectors[multipleSelectorIndex - 1])};
//			throw new SelectorFailedException(
//					errorDescriptions.generateErrorString(
//							errorDescriptions.getProperty(Constants.BS_ERROR_SELECTOR_FAILED),
//							selectorArray));
		}else {
			if (children == null) {
				if (attribute != null)
					elemValue = rootElement.attr(attribute);
				else
					elemValue = rootElement.text();
			} else {
				if (attribute != null)
					elemValue = children.attr(attribute);
				else
					elemValue = children.text();
			}
			
		}
		
		return elemValue;
	}
}
