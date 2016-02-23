package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Sandeep
 *
 */

public class MyQuopnData implements Response {

	private boolean error;
	private List<MyQuopnListData> coupondetails=new ArrayList<MyQuopnListData>();
	private String message;
	private String walletid;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	
	public List<MyQuopnListData> getCoupondetails() {
		return coupondetails;
	}
	public String getMessage() {
		return message;
	}
	public String getWalletid() {
		return walletid;
	}
	

	
}
