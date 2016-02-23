package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Sandeep
 *
 */

public class CartData implements Response {

	private boolean error;
	private List<CartListData> cartdetails=new ArrayList<CartListData>();
	private String message;
	private String walletid;
	private String approx_saving;
	
	
	
	public String getApprox_saving() {
		return approx_saving;
	}
	public void setApprox_saving(String approx_saving) {
		this.approx_saving = approx_saving;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	public List<CartListData> getCartdetails() {
		return cartdetails;
	}
	
	public void setCartdetails(List<CartListData> cartdetails) {
		this.cartdetails = cartdetails;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	
	public void setWalletid(String walletid) {
		this.walletid = walletid;
	}
	public String getWalletid() {
		return walletid;
	}
	
	

	
}
