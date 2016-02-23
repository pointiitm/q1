package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class ProfileData implements Response {

	private boolean error;
	private String message;
	private User user;
	private FlashFileData flash_file;
	
	
	

	/**
	 * @return the flash_file
	 */
	public FlashFileData getFlash_file() {
		return flash_file;
	}



	/**
	 * @param flash_file the flash_file to set
	 */
	public void setFlash_file(FlashFileData flash_file) {
		this.flash_file = flash_file;
	}



	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}



	public User getUser() {
		return user;
	}

	public boolean isError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

}
