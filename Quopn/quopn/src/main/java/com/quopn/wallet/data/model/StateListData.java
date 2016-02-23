package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class StateListData implements Response {

	private boolean error;
	private List<StateData> state = new ArrayList<StateData>();

	public boolean isError() {
		return error;
	}

	public List<StateData> getState() {
		return state;
	}

	public void setError(boolean b) {
		this.error = b;
	}

}
