package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.interfaces.Response;

/**
 * @author Vaibhav
 *
 */

public class AnnouncementData implements Response {

	private String data;
    private String image;
    private String url;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
		return data;
	}

	public void setDat6a(String argData) {
		this.data = argData;
	}

}
