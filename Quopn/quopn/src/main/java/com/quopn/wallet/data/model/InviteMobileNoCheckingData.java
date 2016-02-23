package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class InviteMobileNoCheckingData implements Response{
	private boolean error;
	private String message;
	private int isInviteSuccess;
	
	
	
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
	
	public void setIsInviteSuccess(int isInviteSuccess) {
		this.isInviteSuccess = isInviteSuccess;
	}
	public int getIsInviteSuccess() {
		return isInviteSuccess;
	}

	
}
