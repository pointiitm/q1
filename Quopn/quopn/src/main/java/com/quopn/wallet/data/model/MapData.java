package com.quopn.wallet.data.model;
/**
 * @author Sandeep
 *
 */
import com.quopn.wallet.interfaces.Response;

public class MapData implements Response {

	private boolean error;
	private String message;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	
	

	
}
