package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class CampaignDetailsQuopnData implements Response{

	private boolean error;
	private String message;
	private List<QuopnList> campaignDetails=new ArrayList<QuopnList>();
	//private List<FooterTag> footer = new ArrayList<FooterTag>();
	private Gift gift;
	

	public void setError(boolean error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return error;
	}
	
	public List<QuopnList> getCampaignDetails() {
		return campaignDetails;
	}
	
	public String getMessage() {
		return message;
	}

	public Gift getGift() {
		return gift;
	}

//	public List<FooterTag> getFooter() {
//		return footer;
//	}
	
	

}
