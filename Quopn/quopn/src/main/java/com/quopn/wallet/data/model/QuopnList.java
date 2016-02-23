package com.quopn.wallet.data.model;
/**
 * @author Sandeep
 *
 */
public class QuopnList {

	private int id;
	private int categoryid;
	private String campaignname;
	private String productname;
	//private String producttype; //b
	private String thumb_icon;
	private String big_image;
	private String short_desc;
	private String long_desc;
	private String master_tag;
	//private String mastertag_image;  //b
	private String footer_tag;
	private String brand;
	private int quopn_count;
	private String call_to_action;
	private String cta_text;
	private String startfrom;
	private String enddate;
	//private String terms_cond; //n
	//private String submitted_by; //n
	private String cta_value;
	private String redemption_expiry_date;
	private String campaign_media;
	private String multi_issue;
	private int issue_limit;
	private int redemption_cap;
	private String promotion_enabled;
	private int total_coupons_blocked;
	private String thumb_icon2;
	private String search_tags;
	private String available_quopns;
	private String already_issued;
	private String description_highlight;
	private String description_end;
	private int sort_index;
	
	
	
	public int getSort_index() {
		return sort_index;
	}
	public void setSort_index(int sort_index) {
		this.sort_index = sort_index;
	}
	public String getDescription_end() {
		return description_end;
	}
	public void setDescription_end(String description_end) {
		this.description_end = description_end;
	}
	public String getDescription_highlight() {
		return description_highlight;
	}
	public void setDescription_highlight(String description_highlight) {
		this.description_highlight = description_highlight;
	}
	public String getAvailable_quopns() {
		return available_quopns;
	}
	public void setAvailable_quopns(String available_quopns) {
		this.available_quopns = available_quopns;
	}
	public String getAlready_issued() {
		return already_issued;
	}
	public void setAlready_issued(String already_issued) {
		this.already_issued = already_issued;
	}
	public String getCta_value() {
		return cta_value;
	}
//	public String getMastertag_image() {
//		return mastertag_image;
//	}
	public int getId() {
		return id;
	}
	public int getCategoryid() {
		return categoryid;
	}
	public String getCampaignname() {
		return campaignname;
	}
	public String getProductname() {
		return productname;
	}
//	public String getProducttype() {
//		return producttype;
//	}
	public String getThumb_icon() {
		return thumb_icon;
	}
	public String getBig_image() {
		return big_image;
	}
	public String getShort_desc() {
		return short_desc;
	}
	public String getLong_desc() {
		return long_desc;
	}
	public String getMaster_tag() {
		return master_tag;
	}
	public String getFooter_tag() {
		return footer_tag;
	}
	public String getBrand() {
		return brand;
	}
	public int getQuopn_count() {
		return quopn_count;
	}
	public String getCall_to_action() {
		return call_to_action;
	}
	public String getCta_text() {
		return cta_text;
	}
	public String getStartfrom() {
		return startfrom;
	}
	public String getEnddate() {
		return enddate;
	}
//	public String getTerms_cond() {
//		return terms_cond;
//	}
//	public String getSubmitted_by() {
//		return submitted_by;
//	}
	public String getRedemption_expiry_date() {
		return redemption_expiry_date;
	}
	public String getCampaign_media() {
		return campaign_media;
	}
	public String getMulti_issue() {
		return multi_issue;
	}
	public int getIssue_limit() {
		return issue_limit;
	}
	public int getRedemption_cap() {
		return redemption_cap;
	}
	public String getPromotion_enabled() {
		return promotion_enabled;
	}
	public int getTotal_coupons_blocked() {
		return total_coupons_blocked;
	}
	public String getThumb_icon2() {
		return thumb_icon2;
	}
	public String getSearch_tags() {
		return search_tags;
	}
}
