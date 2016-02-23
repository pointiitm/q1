package com.quopn.wallet.data.model;

public class VoucherList {

	private int partner_id;
	private String partner_name;
	private String thumb_icon;
	private String big_image;
	private String total_coupons;
	private String available_coupons;
	private String issue_available;
    private String issued_coupons;
    private String purchase_value;

    public String getPurchase_value() {
        return purchase_value;
    }

    public void setPurchase_value(String purchase_value) {
        this.purchase_value = purchase_value;
    }

    public int getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(int partner_id) {
        this.partner_id = partner_id;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getThumb_icon() {
        return thumb_icon;
    }

    public void setThumb_icon(String thumb_icon) {
        this.thumb_icon = thumb_icon;
    }

    public String getBig_image() {
        return big_image;
    }

    public void setBig_image(String big_image) {
        this.big_image = big_image;
    }

    public String getTotal_coupons() {
        return total_coupons;
    }

    public void setTotal_coupons(String total_coupons) {
        this.total_coupons = total_coupons;
    }

    public String getAvailable_coupons() {
        return available_coupons;
    }

    public void setAvailable_coupons(String available_coupons) {
        this.available_coupons = available_coupons;
    }

    public String getIssue_available() {
        return issue_available;
    }

    public void setIssue_available(String issue_available) {
        this.issue_available = issue_available;
    }

    public String getIssued_coupons() {
        return issued_coupons;
    }

    public void setIssued_coupons(String issued_coupons) {
        this.issued_coupons = issued_coupons;
    }
}
