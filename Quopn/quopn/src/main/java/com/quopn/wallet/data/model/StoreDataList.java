package com.quopn.wallet.data.model;

public class StoreDataList {

	int _id;
	private String store_uid;
	private String Business_Name;
	private String Business_Address;
	private String Telephone1;
	private String Telephone2;
	private String open_hours;
	private double latitude;
	private double longitude;
	private String distance;
	private String storetype;
	private int accuracy;
	private int storetypeid;

	public StoreDataList(){

	}
	public StoreDataList(String store_uid,String Business_Name,String Business_Address,String Telephone1,String Telephone2,double latitude, double longitude,String open_hours,String distance,String storetype,int accuracy,int storetypeid ){

		this.Business_Name = Business_Name;
		this.Business_Address = Business_Address;
		this.Telephone1 = Telephone1;
		this.Telephone2 = Telephone2;
		this.latitude = latitude;
		this.longitude = longitude;
		this.open_hours = open_hours;
		this.distance = distance;
		this.storetype = storetype;
		this.storetypeid = storetypeid;
		this.store_uid = store_uid;
		this.accuracy = accuracy;

	}

	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}


	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}


	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getBusiness_Name() {
		return Business_Name;
	}
	public void setBusiness_Name(String business_Name) {
		Business_Name = business_Name;
	}
	public String getBusiness_Address() {
		return Business_Address;
	}
	public void setBusiness_Address(String business_Address) {
		Business_Address = business_Address;
	}
	public String getTelephone1() {
		return Telephone1;
	}
	public void setTelephone1(String telephone1) {
		Telephone1 = telephone1;
	}
	public String getTelephone2() {
		return Telephone2;
	}
	public void setTelephone2(String telephone2) {
		Telephone2 = telephone2;
	}
	public String getOpen_hours() {
		return open_hours;
	}
	public void setOpen_hours(String open_hours) {
		this.open_hours = open_hours;
	}

	public String getStoretype() {
		return storetype;
	}
	public void setStoretype(String storetype) {
		this.storetype = storetype;
	}
	public int getStoretypeid() {
		return storetypeid;
	}
	public void setStoretypeid(int storetypeid) {
		this.storetypeid = storetypeid;
	}
	public String getStore_uid() {
		return store_uid;
	}
	public void setStore_uid(String store_uid) {
		this.store_uid = store_uid;
	}


}
