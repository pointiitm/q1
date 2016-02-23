package com.quopn.wallet.data.model;
/**
 * @author Sumeet
 *
 *@Date 07-10-2014
 */
import com.quopn.wallet.interfaces.Response;

public class NotifyStatusData implements Response {

	private boolean error;
	private String message;
	private String status;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public String getStatus() {
		return status;
	}
	
	
}
