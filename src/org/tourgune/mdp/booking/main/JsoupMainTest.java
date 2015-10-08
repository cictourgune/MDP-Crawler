package org.tourgune.mdp.booking.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupMainTest {

	public static void main(String[] args) {
		try {

//			Document doc = Jsoup.connect("http://www.booking.com/hotel/es/hesperiafinisterre.en-us.html?checkin=2014-05-21&checkout=2014-05-22").get();
			Document doc = Jsoup.connect("http://www.booking.com/hotel/es/los-jameos-playa.en-us.html?checkin=2014-05-27&checkout=2014-05-28").get();
			
			Elements names = doc.select("tr[class*=maintr]");
			for (Element elem : names) {
				boolean exit = false;
				System.out.println("NOMBRE: " + elem.select("td a.togglelink").text());
				Element auxSibling = elem;
				while (!exit) {
					String elemClass = elem.className().trim();
					auxSibling = auxSibling.nextElementSibling();
					String siblingClass = auxSibling == null ? null : auxSibling.className().trim();
					if (siblingClass != null && elemClass.contains(siblingClass)) {
						System.out.println("\tBREAKFAST: " + auxSibling.select("td.ratepolicy span:contains(breakfast included)").text());
						System.out.println("\tBREAKFAST PRICE: " + _stripPrice(auxSibling.select("td.ratepolicy span:contains(breakfast €)").text()));
						System.out.println("\tHALF: " + auxSibling.select("td.ratepolicy span:contains(half board included)").text());
						System.out.println("\tFULL: " + auxSibling.select("td.ratepolicy span:contains(full board included)").text());
						System.out.println("\tALL: " + auxSibling.select("td.ratepolicy span:contains(all-inclusive)").text());
						System.out.println("\tFREE CANCELLATION: " + auxSibling.select("td.ratepolicy span:contains(free cancellation)").text());
						System.out.println("\tPAY STAY: " + auxSibling.select("td.ratepolicy span:contains(special conditions, pay when you stay)").text());
						System.out.println("\tPAY LATER: " + auxSibling.select("td.ratepolicy span:contains(pay later)").text());
						System.out.println("\tNON REFUNDABLE: " + auxSibling.select("td.ratepolicy span:contains(non refundable)").text());
						System.out.println("\tPRICE: " + _stripPrice(auxSibling.select("td.roomPrice strong[data-price-without-addons]").text()));
						System.out.println("\tOCCUPANCY: " + auxSibling.attr("data-occupancy"));
						System.out.println("\tCHILD: " + auxSibling.select("td.roomMaxPersons > div.roomDefaultUse > div[title*=max child]").attr("title"));
						System.out.println("\t--------------------------------------");
					} else {
						exit = true;
					}
				}
			}
//			System.out.println(name.text());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String _stripPrice(String strPrice) {
		int euroSymbol = 0x20AC;
		StringBuffer buf = new StringBuffer(strPrice);
		int index = strPrice.indexOf(euroSymbol);
		if(index >= 0){
			buf = new StringBuffer();
			for(; index < strPrice.length(); index++){
				char nextChar = strPrice.charAt(index);
				if(nextChar == ' ' || Character.isDigit(nextChar))
					buf.append(nextChar);
			}
		}
		return buf.toString().trim();
	}
}
