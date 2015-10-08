package org.tourgune.mdp.amazon.utils;

public class Utils {
	
	public static java.sql.Date convert2DbDate(java.util.Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}

}
