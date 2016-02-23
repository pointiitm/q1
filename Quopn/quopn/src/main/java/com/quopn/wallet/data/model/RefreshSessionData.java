package com.quopn.wallet.data.model;
import com.quopn.wallet.interfaces.Response;

/**
 * @author Sandeep
 *
 */

public class RefreshSessionData implements Response {

	private String error;
	private String message;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	
	
	
	

	
}
