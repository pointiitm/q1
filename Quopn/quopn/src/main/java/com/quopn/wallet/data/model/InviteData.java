package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class InviteData implements Response {
	private boolean error;
	private String message;
	private String user_invite_count;

	public void setError(boolean error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public void setInvite_count(String user_invite_count) {
		this.user_invite_count = user_invite_count;
	}

	public String getInvite_count() {
		return user_invite_count;
	}
}
