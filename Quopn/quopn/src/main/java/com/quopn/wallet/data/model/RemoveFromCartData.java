package com.quopn.wallet.data.model;
import com.quopn.wallet.interfaces.Response;
/**
 * @author Sandeep
 *
 */

public class RemoveFromCartData implements Response {

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
