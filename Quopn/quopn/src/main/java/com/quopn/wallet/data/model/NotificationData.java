package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

public class NotificationData implements Response{
	
	private String image_url;
	private String title;
	private String desc;
	private String campaign_id;
	private String screen;
	private String value;
	private String caption;
	private String type;
    private String typeid;
	private String notification_id;
	private String deep_link;
	private String big_image_url;
	private String big_image_value;

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getImage_url() {
		return image_url;
	}
	public String getTitle() {
		return title;
	}
	public String getDesc() {
		return desc;
	}
	public String getCampaign_id() {
		return campaign_id;
	}
	public void setCampaign_id(String campaign_id) {
		this.campaign_id = campaign_id;
	}
	public String getScreen() { return screen; }
	public String getValue() { return value; }
	public String getCaption() { return caption; }
	public String getNotification_id() {
		return notification_id;
	}

	public void setNotification_id(String notification_id) {
		this.notification_id = notification_id;
	}

	public String getDeepLink() {
		return deep_link;
	}

	public void setDeepLink(String deep_link) {
		this.deep_link = deep_link;
	}

	public String getType() {
		return type;
	}

	public void setType(String argType) {
		type = argType;
	}

	public String getBig_image_url() {
		if (big_image_url == null) {
			big_image_url = image_url;
		}
		return big_image_url;
	}

	public String getBig_image_value() {
		return big_image_value;
	}
}
