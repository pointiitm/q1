package com.quopn.wallet.data.model;

/**
 * 
 * @author Sumeet
 * @date 29/09/2014
 */

public class AboutUsData {

	public AboutUsData(String name, int pic) {
		this.name = name;
		this.pic = pic;
	}

	private String name;
	private int pic;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}

}
