package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Sandeep
 *
 */

public class HistoryData implements Response {

	private boolean error;
	private List<HistoryListData> historydetails=new ArrayList<HistoryListData>();
	private String message;
	private String walletid;
	private String total_savings;
	
	public String getTotal_savings() {
		return total_savings;
	}
	public void setTotal_savings(String total_savings) {
		this.total_savings = total_savings;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public List<HistoryListData> getHistorydetails() {
		return historydetails;
	}
	public String getMessage() {
		return message;
	}
	public String getWalletid() {
		return walletid;
	}
	
	

	
}
