package org.tourgune.mdp.booking.utils;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class HtmlUtils {
	
	
	public static Document convertStringToDocument(String xmlStr) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		try  {  
			builder = factory.newDocumentBuilder();  
			Document doc = builder.parse(new InputSource(new StringReader(xmlStr))); 
			return doc;
		} catch (Exception e) {  
			e.printStackTrace();  
		} 
		return null;
	}
	
	public static org.jsoup.nodes.Document convertString2Document(String html) throws Exception {
		
		org.jsoup.nodes.Document doc = (org.jsoup.nodes.Document) Jsoup.parse(html);
		
		
		return doc;
	}
}
