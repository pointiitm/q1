package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class RegisterData implements Response {

	private boolean error;
	private String userid;
	private String pin;
	private String message;

	public boolean getError() {
		return error;
	}

	public String getUserid() {
		return userid;
	}

	public String getOTP() {
		return pin;
	}

	public String getMessage() {
		return message;
	}

	public void setError(boolean b) {
		this.error = b;
	}

}
