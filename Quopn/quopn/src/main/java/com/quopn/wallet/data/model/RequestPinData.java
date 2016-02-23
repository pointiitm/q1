package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class RequestPinData implements Response {

	private boolean error;
	private String pin;
	private String message;
	private String defaultWallet;

	public boolean getError() {
		return error;
	}

	public String getPin() {
		return pin;
	}

    public void setPin(String pin) {
        this.pin = pin;
    }

	public String getMessage() {
		return message;
	}

	public void setError(boolean b) {
		this.error = b;
	}

	public String getDefaultWallet() {
		return defaultWallet;
	}

}
