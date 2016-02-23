package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class ResendOTPData implements Response{

	private boolean error;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean b) {
		this.error = b;
	}
	
}
