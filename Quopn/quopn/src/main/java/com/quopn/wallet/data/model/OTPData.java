package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class OTPData implements Response {

	private boolean error;
	private String message;
	private User user;
	
	
	public void setError(boolean error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public User getUser() {
		return user;
	}

}
