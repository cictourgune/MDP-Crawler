package org.tourgune.mdp.misc.db;

public class TablesDB {
	
	// d_geography
			public static String TABLE_D_GEOGRAPHY				= "d_geography";
			public static String DG_ABBR						= "dg";
			
			public static String DG_ID_GEOGRAPHY				= "id_geography";
			public static String DG_ID_GEOGRAPHY_CHANNEL		= "id_geography_channel";
			
	// nm_geography_channel
		public static String TABLE_NM_GEOGRAPHY_CHANNEL		= "nm_geography_channel";
		public static String NMGC_ABBR						= "nmgc";
		
		public static String NMGC_ID_GEOGRAPHY				= "id_geography";
		public static String NMGC_ID_GEOGRAPHY_CHANNEL		= "id_geography_channel";
	
	// nm_accommodation_channel
		public static String TABLE_NM_ACCOMMODATION_CHANNEL	= "nm_accommodation_channel";
		public static String NMAC_ABBR						= "nmac";
	
		public static String NMAC_ID_ACCOMMODATION			= "id_accommodation";
//		public static String NMAC_ID_CHANNEL				= "id_channel";
		public static String NMAC_ID_ACCOMMODATION_CHANNEL	= "id_accommodation_channel";
		public static String NMAC_URL_ACCOMMODATION_CHANNEL	= "url_accommodation_channel";
//		public static String NMAC_ROOM_AMOUNT				= "room_amount";
//		public static String NMAC_LAST_SEEN					= "last_seen";
		
	// d_accommodation	
		public static String DAC_CATEGORY					= "category";
		public static String DAC_NAME						= "name";
		public static String DAC_LATITUDE					= "latitude";
		public static String DAC_LONGITUDE					= "longitude";
		public static String DAC_POSTALCODE					= "gm_postal_code";
		public static String DAC_STREETNUMBER				= "gm_street_number";
		public static String DAC_ROUTE						= "gm_route";
		public static String DAC_LOCALITY					= "gm_locality";
		public static String DAC_COUNTRY					= "gm_country";
		public static String DAC_AAL1						= "gm_aal1";
		public static String DAC_AAL2						= "gm_aal2";
		public static String DAC_AAL3						= "gm_aal3";
		public static String DAC_AAL4						= "gm_aal4";
		public static String DAC_ACCMTYPE					= "id_accommodation_type";
		public static String DAC_LOCKED						= "locked";
	
	// ft_product_price
		public static String FTPP_BOOKINGDATE				= "id_booking_date";
	// d_product
		public static String TABLE_D_PRODUCT				= "d_product";
		public static String DP_ABBR						= "dp";
		
		public static String DP_ID_PRODUCT					= "id_product";
//		public static String DP_NAME						= "name";
//		public static String DP_ADULT_AMOUNT				= "adult_amount";
//		public static String DP_CHILDREN_AMOUNT				= "children_amount";
//		public static String DP_BREAKFAST_INCLUDED			= "breakfast_included";
//		public static String DP_FREE_CANCELLATION			= "free_cancellation";

	// d_visitor_segment
		public static String TABLE_D_VISITOR_SEGMENT		= "d_visitor_segment";
		public static String DVS_ABBR						= "dvs";
		
		public static String DVS_ID_VISITOR_SEGMENT			= "id_visitor_segment";
	
	// d_accommodation_type
		public static String TABLE_D_ACCOMMODATION_TYPE		= "d_accommodation_type";
		public static String DAT_ID_ACCOMMODATION_TYPE		= "id_accommodation_type";
		public static String DAT_NAME						= "name";
}
