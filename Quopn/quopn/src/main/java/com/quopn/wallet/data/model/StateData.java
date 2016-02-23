package com.quopn.wallet.data.model;

import java.util.ArrayList;
import java.util.List;

public class StateData {
	private String state_id;
	private String state_name;
	private List<CityData>cities=new ArrayList<CityData>();

	public String getStateId() {
		return state_id;
	}

	public String getStateName() {
		return state_name;
	}
	
	public List<CityData> getCities() {
		return cities;
	}

	public void setCities(List<CityData> cities) {
		this.cities = cities;
	}

	public void setState_id(String state_id) {
		this.state_id = state_id;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	@Override
	public String toString() {
		return state_name;
	}
	
}
