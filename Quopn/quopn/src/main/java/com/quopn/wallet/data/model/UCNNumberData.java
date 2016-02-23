package com.quopn.wallet.data.model;
/**
 * @author Sandeep
 *
 */
import com.quopn.wallet.interfaces.Response;

public class UCNNumberData implements Response {

	private boolean error;
	private String message;

	public boolean isError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public void setError(boolean b) {
		this.error = b;
	}

}
