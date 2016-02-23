package com.quopn.wallet.data.model;

public class FlashFileData {
	private String imagesize;
	private String imagetype;
	private String imagename;
	private String imagepath;
	private String startdate;
	private String enddate;


	/**
	 * @return the imagename
	 */
	public String getImagename() {
		return this.imagename;
	}

	/**
	 * @param imagename the imagename to set
	 */
	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	/**
	 * @return the imagesize
	 */
	public String getImagesize() {
		return this.imagesize;
	}

	/**
	 * @param imagesize the imagesize to set
	 */
	public void setImagesize(String imagesize) {
		this.imagesize = imagesize;
	}

	/**
	 * @return the imagetype
	 */
	public String getImagetype() {
		return this.imagetype;
	}

	/**
	 * @param imagetype the imagetype to set
	 */
	public void setImagetype(String imagetype) {
		this.imagetype = imagetype;
	}

	/**
	 * @return the imagepath
	 */
	public String getImagepath() {
		return this.imagepath;
	}

	/**
	 * @param imagepath the imagepath to set
	 */
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	/**
	 * @return the startdate
	 */
	public String getStartdate() {
		return startdate;
	}

	/**
	 * @param startdate the startdate to set
	 */
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	/**
	 * @return the enddate
	 */
	public String getEnddate() {
		return enddate;
	}

	/**
	 * @param enddate the enddate to set
	 */
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
}
