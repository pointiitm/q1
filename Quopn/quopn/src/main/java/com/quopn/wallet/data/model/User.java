package com.quopn.wallet.data.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String userid;
	private String username;
	private String mobile;
	private String pic;
	private String gender;
	private String dob;
	private String emailid;
	private String location;
	private String State;
	private String city;
	private String walletid;
	private String PIN;
	private String api_key;
	private int gift_count;
	private String invite_count;
	private String invite_message;
	private String video=null;
	private String notification;
	private String sender_name;
	private String sender_pic;
	private List<InterestedId> interestedid = new ArrayList<InterestedId>();
	private FlashFileData flash_file;
	private String tutorial;
	private String invite_top_message;
	private String promo_message;
	private String promo_top_message;
	private String invite_sms;
	private String customer_care_number;
	private String promo_bottom_message;
	private String type_of_user;
    private String annoucement_url;

	private String defaultWallet;
	private String access_token;
	private String refresh_token;
	private String pre_registered;

	public String getPre_registered() {
		if (pre_registered == null || pre_registered.isEmpty()) {
			pre_registered = "false";
		}
		return pre_registered;
	}

	public void setPre_registered(String pre_registered) {
		this.pre_registered = pre_registered;
	}

	public String getCitrus_email() {
		return citrus_email;
	}

	public void setCitrus_email(String citrus_email) {
		this.citrus_email = citrus_email;
	}

	private String citrus_email;
	private String mobile_wallets;

	public String getMobile_wallets() {
		return mobile_wallets;
	}

	public void setMobile_wallets(String mobile_wallets) {
		this.mobile_wallets = mobile_wallets;
	}

	public String getDefaultWallet() {
		return defaultWallet;
	}

	public void setDefaultWallet(String defaultWallet) {
		this.defaultWallet = defaultWallet;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getPromo_top_message() {
		return promo_top_message;
	}

	public void setPromo_top_message(String promo_top_message) {
		this.promo_top_message = promo_top_message;
	}

	public String getPromo_bottom_message() {
		return promo_bottom_message;
	}

	public void setPromo_bottom_message(String promo_bottom_message) {
		this.promo_bottom_message = promo_bottom_message;
	}

	public String getInvite_sms() {
		return invite_sms;
	}

	public void setInvite_sms(String invite_sms) {
		this.invite_sms = invite_sms;
	}

	public String getPromo_message() {
		return promo_message;
	}
	
	

	public String getInvite_top_message() {
		return invite_top_message;
	}

	public void setInvite_top_message(String invite_top_message) {
		this.invite_top_message = invite_top_message;
	}

	public String getTutorial() {
		return tutorial;
	}

	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
	}

	public String getSender_name() {
		return sender_name;
	}

	public String getSender_pic() {
		return sender_pic;
	}

	
	public int getGift_count() {
		return gift_count;
	}

	public void setGift_count(int gift_count) {
		this.gift_count = gift_count;
	}

	public String getInvite_count() {
		return invite_count;
	}

	public void setInvite_count(String invite_count) {
		this.invite_count = invite_count;
	}
	
	public String getInvite_message() {
		return invite_message;
	}

	public void setInvite_message(String invite_message) {
		this.invite_message = invite_message;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	/*public String getUserid() {
		return userid;
	}*/

	/*public void setUserid(String userid) {
		this.userid = userid;
	}*/

	public String getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public String getPic() {
		return pic;
	}

	public String getEmailid() {
		if (emailid == null) {
			emailid = "";
		}
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getState() {
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		return State;
	}

	public String getCity() {
		if (city == null) {
			city = "";
		}
		return city;
	}

	public void setState(String state) {
		State = state;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public String getWalletid() {
		return walletid;
	}

	public String getPIN() {
		return PIN;
	}

	public String getApi_key() {
		return api_key;
	}

	public List<InterestedId> getInterestedid() {
		return interestedid;
	}

	public void setInterestedid(List<InterestedId> interestedid) {
		this.interestedid = interestedid;
	}

	public void setUserid(String id) {
		this.userid = id;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public void setPIN(String pIN) {
		PIN = pIN;
	}

	public void setWalletid(String walletid) {
		this.walletid = walletid;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getCustomer_care_number() {
		return customer_care_number;
	}

	public void setCustomer_care_number(String customer_care_number) {
		this.customer_care_number = customer_care_number;
	}

    public String getType_of_user() {
        return type_of_user;
    }

    public void setType_of_user(String type_of_user) {
        this.type_of_user = type_of_user;
    }

    public String get_Annoucement_url() {
        return annoucement_url;
    }

    public void set_Annoucement_url(String annoucement_url) {
        this.annoucement_url = annoucement_url;
    }
}
