package org.tourgune.misc.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.torugune.mdp.log.EchoLogger;
import org.torugune.mdp.log.Logger;

public class JSoupUtilsTestMain {

	public static void main(String[] args) {

		Connection httpConn = null;
		org.jsoup.nodes.Document doc = null; 
		Logger logger = null;
		
		try {
			logger = new EchoLogger("Log");
				
			httpConn = Jsoup.connect("http://es.wikipedia.org/wiki/HTML5");
			doc = httpConn.get();
			
			String selectorFull = "h1#firstHeading --lang";
			
			String kk = JSoupUtils.selectElementValueOrAttr(doc, selectorFull, logger);
			System.out.println(kk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		

	}

}
