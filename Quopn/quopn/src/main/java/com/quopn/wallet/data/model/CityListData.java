package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class CityListData implements Response {

	private boolean error;
	private List<CityData> city = new ArrayList<CityData>();

	public void setError(boolean error){
		this.error = error;
	}
	
	public boolean isError() {
		return error;
	}

	public List<CityData> getCityList() {
		return city;
	}

}
