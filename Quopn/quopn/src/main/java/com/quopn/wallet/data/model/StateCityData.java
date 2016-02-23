package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class StateCityData implements Response {

	private boolean error;
	private String message;
	private List<StateData>states=new ArrayList<StateData>();
	public boolean getError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<StateData> getStates() {
		return states;
	}
	public void setStates(List<StateData> states) {
		this.states = states;
	}
	
	
}
