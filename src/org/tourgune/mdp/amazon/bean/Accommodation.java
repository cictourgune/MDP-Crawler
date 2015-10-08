package org.tourgune.mdp.amazon.bean;


public class Accommodation {

	private String name;
	private int category;
	private double latitude;
	private double longitude;
	private int idAccommodation;
	private String idAccommodationChannel;
	private String urlAccommodationChannel;
	private String country;
	private String postalCode;
	private String streetNumber;
	private String route;
	private String locality;
	private String admAreaLevel1, admAreaLevel2, admAreaLevel3, admAreaLevel4;
	private int locked = 0;
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		
		if (obj instanceof Accommodation) {
			Accommodation otherAccommodation = (Accommodation) obj;

			try {
				isEqual = (otherAccommodation.getName() == null && this.name == null ? true : otherAccommodation.getName().equalsIgnoreCase(this.name)) &&
						otherAccommodation.getCategory() == this.category && otherAccommodation.getLatitude() == this.latitude && otherAccommodation.getLongitude() == this.longitude &&
						(otherAccommodation.getIdAccommodationChannel() == null && this.idAccommodationChannel == null ? true : otherAccommodation.getIdAccommodationChannel().equalsIgnoreCase(this.idAccommodationChannel)) &&
						(otherAccommodation.getUrlAccommodationChannel() == null && this.urlAccommodationChannel == null ? true : otherAccommodation.getUrlAccommodationChannel().equalsIgnoreCase(this.urlAccommodationChannel));
				// El campo 'id_accommodation' no lo comprobamos porque el accommodation que viene de booking no tiene este valor
			} catch (NullPointerException e) {
				isEqual = false;
			}
		}
		
		return isEqual;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getAdministrativeAreaLevel1() {
		return admAreaLevel1;
	}
	public void setAdministrativeAreaLevel1(String admAreaLevel1) {
		this.admAreaLevel1 = admAreaLevel1;
	}
	public String getAdministrativeAreaLevel2() {
		return admAreaLevel2;
	}
	public void setAdministrativeAreaLevel2(String admAreaLevel2) {
		this.admAreaLevel2 = admAreaLevel2;
	}
	public String getAdministrativeAreaLevel3() {
		return admAreaLevel3;
	}
	public void setAdministrativeAreaLevel3(String admAreaLevel3) {
		this.admAreaLevel3 = admAreaLevel3;
	}
	public String getAdministrativeAreaLevel4() {
		return admAreaLevel4;
	}
	public void setAdministrativeAreaLevel4(String admAreaLevel4) {
		this.admAreaLevel4 = admAreaLevel4;
	}
	public String getIdAccommodationChannel() {
		return idAccommodationChannel;
	}
	public void setIdAccommodationChannel(String idAccommodationChannel) {
		this.idAccommodationChannel = idAccommodationChannel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
	public int getIdAccommodation() {
		return idAccommodation;
	}
	public void setIdAccommodation(int idAccommodation) {
		this.idAccommodation = idAccommodation;
	}
	public String getUrlAccommodationChannel() {
		return urlAccommodationChannel;
	}
	public void setUrlAccommodationChannel(String urlAccommodationChannel) {
		this.urlAccommodationChannel = urlAccommodationChannel;
	}
		public int getLocked() {
		return locked;
	}
	public void setLocked(int locked) {
		this.locked = locked;
	}
	@Override
	public String toString() {
		return idAccommodation + " --> " + urlAccommodationChannel;
	}
}
