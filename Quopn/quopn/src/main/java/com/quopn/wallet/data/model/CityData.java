package com.quopn.wallet.data.model;

public class CityData {
	private String city_id;
	private String city_name;

	public String getCityId() {
		return city_id;
	}

	public String getCityName() {
		return city_name;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	
	@Override
	public String toString() {
		return city_name;
	}
}
