package com.quopn.wallet.data.model;
/**
 * @author Sandeep
 *
 */
import com.quopn.wallet.interfaces.Response;

public class WebIssueData implements Response {

	private boolean error;
	private String message;
	SingleCartDetails cart_details=new SingleCartDetails();
	
	
	
	public SingleCartDetails getSingleCart_details() {
		return cart_details;
	}
	public void setSingleCart_details(SingleCartDetails cart_details) {
		this.cart_details = cart_details;
	}
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
