package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class FooterTagData implements Response {

	private boolean error;
	private List<FooterTag> category = new ArrayList<FooterTag>();

	public boolean isError() {
		return error;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}

	public List<FooterTag> getQuopns() {
		return category;
	}

}
