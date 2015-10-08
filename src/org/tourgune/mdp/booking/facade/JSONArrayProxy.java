package org.tourgune.mdp.booking.facade;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tourgune.mdp.booking.utils.BookingConfig;
import org.tourgune.mdp.booking.utils.Constants;

public class JSONArrayProxy {

	private JSONArray _jsonArray;
	
	public JSONArrayProxy() {
		this._jsonArray = new JSONArray();
	}
	
	public JSONArrayProxy put(JSONObject a) {
		for (int i = 0; i < this._jsonArray.length(); i++) {
			JSONObject b = this._jsonArray.getJSONObject(i);
			if (isEqual(a, b))
				return this;
		}
		
		this._jsonArray.put(a);
		
		return this;
	}

	public JSONArray getJSONArray() {
		return this._jsonArray;
	}
	
	private boolean isEqual(JSONObject a, JSONObject b) {
		boolean isEqual = false;
		BookingConfig bk = BookingConfig.getInstance();
		
		try {
			isEqual = a.getString(bk.getProperty(Constants.BS_CONSTANT_HDEPRICE_URL)).equals(b.getString(bk.getProperty(Constants.BS_CONSTANT_HDEPRICE_URL))) &&
					a.getString(bk.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONIDCHANNEL)).equals(b.getString(bk.getProperty(Constants.BS_CONSTANT_HDEPRICE_ACCOMMODATIONIDCHANNEL)));
		} catch (NullPointerException npe) {
			isEqual = false;
		}
		
		return isEqual;
	}
}
