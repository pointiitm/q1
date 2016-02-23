package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class GeneratePinData implements Response {

	private boolean error;
	private String message;

	public boolean getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public void setError(boolean b) {
		this.error = b;
	}

}
