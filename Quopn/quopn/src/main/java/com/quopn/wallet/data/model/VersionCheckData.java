package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class VersionCheckData implements Response {

	private boolean error;
	private boolean update_action;
	private int current_version;
	private String link;
	private String message;
	
	
	public boolean getError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean getUpdate_action() {
		return update_action;
	}
	public void setUpdate_action(boolean update_action) {
		this.update_action = update_action;
	}
	public int getCurrent_version() {
		return current_version;
	}
	public void setCurrent_version(int current_version) {
		this.current_version = current_version;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
	
	
	
}
