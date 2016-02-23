package com.quopn.wallet.data;

public interface ITableData {
	public interface TABLE_CATEGORY {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_CATEGORY.TABLE_NAME + "("
				+ ITableData.TABLE_CATEGORY.COLUMN_ID
				+ " integer primary key autoincrement, "
				+ ITableData.TABLE_CATEGORY.COLUMN_CATEGORY
				+ " text , "
				+ ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID
				+ " text , " +ITableData.TABLE_CATEGORY.COLUMN_ICON
//				+ " text ," + ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_NAME
                + " text ," + ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_TYPE
//              + " text ," + ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_THUMBIMAGE
                + " text ," + ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE
                + " integer default 0 "
                + ");";

		public final String TABLE_NAME = "category";
		public final String COLUMN_ID = "_id";
		public final String COLUMN_CATEGORY = "category";
		public final String COLUMN_ICON = "icon";
		public final String COLUMN_CATEGORY_ID = "categoryid";
        //new columns added as per new flow of categories
//        public final String COLUMN_CATEGORY_NAME = "name";
        public final String COLUMN_CATEGORY_TYPE="type";
//        public final String COLUMN_CATEGORY_THUMBIMAGE="thumbimage";
        public final String COLUMN_SEQUENCE="sequence";
	}
	
	public interface TABLE_STATES {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_STATES.TABLE_NAME + "("
				+ ITableData.TABLE_STATES.COLUMN_STATE_ID
				+ " text not null, "
				+ ITableData.TABLE_STATES.COLUMN_STATE_NAME
				+ " text not null" + ");";

		public final String TABLE_NAME = "states";
		public final String COLUMN_STATE_ID = "_id";
		public final String COLUMN_STATE_NAME = "state_name";
		
	}
	
	public interface TABLE_CITIES {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_CITIES.TABLE_NAME + "("
				+ ITableData.TABLE_CITIES.COLUMN_CITY_ID
				+ " text not null, "
				+ ITableData.TABLE_CITIES.COLUMN_CITY_NAME
				+ " text not null, "
				+ ITableData.TABLE_CITIES.COLUMN_STATE_ID
				+ " text not null" + ");";

		public final String TABLE_NAME = "cities";
		public final String COLUMN_CITY_ID = "_id";
		public final String COLUMN_CITY_NAME = "city_name";
		public final String COLUMN_STATE_ID = "state_id";
	}
	
	public interface TABLE_MYCART {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_MYCART.TABLE_NAME + "("
				+ ITableData.TABLE_MYCART.COLUMN_ID
				+ " integer primary key autoincrement, "+ ITableData.TABLE_MYCART.COLUMN_WALLETID  
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING 
				+ " text default \'0\', " + ITableData.TABLE_MYCART.COLUMN_CARTID 
				+ " integer default 0 , " + ITableData.TABLE_MYCART.COLUMN_CAMPAIGNID 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_CAMPAIGNNAME 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_QUOPNCODE 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_COMPANYNAME 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_LONG_DESCRIPTION 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_BRANDNAME 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_THUMB_ICON1 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_ENDDATE 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_QUOPNID 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_SAVING_VALUE 
				+ " text default \'0\', " + ITableData.TABLE_MYCART.COLUMN_CART_DESC 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_CART_IMAGE 
				+ " text , " + ITableData.TABLE_MYCART.COLUMN_TITLE 
				+ " text "
				+ ");";
		public final String TABLE_NAME = "mycart";
		public final String COLUMN_ID = "_id";
		public final String COLUMN_WALLETID = "walletid";
		public final String COLUMN_APPROX_SAVING = "approx_saving";
		public final String COLUMN_CARTID = "cartid";
		public final String COLUMN_CAMPAIGNID = "campaignid";
		public final String COLUMN_CAMPAIGNNAME = "campaignname";
		public final String COLUMN_QUOPNCODE = "quopncode";
		public final String COLUMN_COMPANYNAME = "companyname";
		public final String COLUMN_LONG_DESCRIPTION = "long_description";
		public final String COLUMN_BRANDNAME = "brandname";
		public final String COLUMN_THUMB_ICON1 = "thumb_icon1";
		public final String COLUMN_ENDDATE = "enddate";
		public final String COLUMN_QUOPNID = "quopnid";
		public final String COLUMN_SAVING_VALUE = "saving_value";
		public final String COLUMN_CART_DESC = "cart_desc";
		public final String COLUMN_CART_IMAGE = "cart_image";
		public final String COLUMN_TITLE = "title";
		
	}

	public interface TABLE_QUOPNS {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_QUOPNS.TABLE_NAME + "("
				+ ITableData.TABLE_QUOPNS.COLUMN_ID
				+ " integer primary key autoincrement, "+ ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID 
				+ " text not null, " + ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_TYPE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_BIG_IMG
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_FOOTER_TAG
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_BRAND
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_QUOPN_COUNT
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_START_DATE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_END_DATE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_TERMS_COND
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_SUBMITTED_BY
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_END_DATE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN_MEDIA
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_MULTI_ISSUE
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_ISSUE_LIMIT
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_CAP
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_PROMOTION_ENABLED
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_TOTAL_COUPONS_BLOCKED
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON_2
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_SEARCH_TAGS
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS
				+ " text default \'0\', " + ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED
				+ " text default \'0\', " + ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_END_DESC
				+ " text , " + ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX
				+ " int "
				
				+ ");";
		public final String TABLE_NAME = "quopns";
		public final String COLUMN_ID = "_id";
		public final String COLUMN_QUOPN_ID = "id";
		public final String COLUMN_CATEGORY_ID = "categoryid";
		public final String COLUMN_CAMPAIGN = "campaignname";
		public final String COLUMN_PRODUCT_NAME = "productname";
		public final String COLUMN_PRODUCT_TYPE = "producttype";
		public final String COLUMN_THUMB_ICON = "thumb_icon";
		public final String COLUMN_BIG_IMG = "big_image";
		public final String COLUMN_SHORT_DESC = "short_desc";
		public final String COLUMN_LONG_DESC = "long_desc";
		public final String COLUMN_MASTER_TAG = "master_tag";
		public final String COLUMN_MASTER_TAG_URL = "mastertag_image";
		public final String COLUMN_FOOTER_TAG = "footer_tag";
		public final String COLUMN_BRAND = "brand";
		public final String COLUMN_QUOPN_COUNT = "quopn_count";
		public final String COLUMN_CALL_TO_ACTION = "call_to_action";
		public final String COLUMN_CTA_TEXT = "cta_text";
		public final String COLUMN_CTA_VALUE = "cta_value";
		public final String COLUMN_START_DATE = "startfrom";
		public final String COLUMN_END_DATE = "enddate";
		public final String COLUMN_TERMS_COND = "terms_cond";
		public final String COLUMN_SUBMITTED_BY = "submitted_by";
		public final String COLUMN_REDEMPTION_END_DATE = "redemption_expiry_date";
		public final String COLUMN_CAMPAIGN_MEDIA = "campaign_media";
		public final String COLUMN_MULTI_ISSUE = "multi_issue";
		public final String COLUMN_ISSUE_LIMIT = "issue_limit";
		public final String COLUMN_REDEMPTION_CAP = "redemption_cap";
		public final String COLUMN_PROMOTION_ENABLED = "promotion_enabled";
		public final String COLUMN_TOTAL_COUPONS_BLOCKED = "total_coupons_blocked";
		public final String COLUMN_THUMB_ICON_2 = "thumb_icon2";
		public final String COLUMN_SEARCH_TAGS = "search_tags";
		public final String COLUMN_AVAILABLE_QUOPNS = "available_quopns";
		public final String COLUMN_ALREADY_ISSUED = "already_issued";
		public final String COLUMN_HIGHLIGHT_DESC = "description_highlight";
		public final String COLUMN_END_DESC = "description_end";
		public final String COLUMN_SORT_INDEX = "sort_index";
	}
	
	public interface TABLE_NOTIFICATIONS {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + "("
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_ID
				+ " integer primary key autoincrement, "
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION
				+ " text not null, "
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL
				+ " text, "
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC
				+ " text, "
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID
				+ " text,"
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME
				+ " text,"
				+ ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG
				+ " integer default 0,"
				+ TABLE_NOTIFICATIONS.COLUMN_SCREEN
				+ " text,"
				+ TABLE_NOTIFICATIONS.COLUMN_SCREEN_VALUE
				+ " text,"
				+ TABLE_NOTIFICATIONS.COLUMN_CAPTION
				+ " text,"
                + ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID
                + " integer, "
                + ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID
                + " integer, "
				+ TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_URL
				+ " text, "
				+ TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_VALUE
				+ " text "
				+ ");";

		public final String TABLE_NAME = "notifications";
		public final String COLUMN_ID = "_id";
		public final String COLUMN_NOTIFICATION = "notification";
		public final String COLUMN_IMAGE_URL = "image_url";
		public final String COLUMN_DESC = "desc";
		public final String COLUMN_CAMPAIGN_ID = "campaign_id";
		public final String COLUMN_DATE_TIME = "date_time";
		public final String COLUMN_NEW_FLAG = "new_flag";
		public final String COLUMN_SCREEN = "screen";
		public final String COLUMN_SCREEN_VALUE = "value";
		public final String COLUMN_CAPTION = "caption";
        public final String COLUMN_NOTIFICATION_ID = "notification_id";
        public final String COLUMN_TYPE_ID = "typeid";
		public final String COLUMN_BIG_IMAGE_URL = "big_image_url";
		public final String COLUMN_BIG_IMAGE_VALUE = "big_image_value";
	}
	
	public interface TABLE_GIFTS {
		public final String CREATE_STATEMENT = "create table "
				+ ITableData.TABLE_GIFTS.TABLE_NAME + "("
				+ ITableData.TABLE_GIFTS.COLUMN_ID
				+ " integer primary key autoincrement, "
				+ ITableData.TABLE_GIFTS.GIFT_STATE
				+ " integer, "
				+ ITableData.TABLE_GIFTS.COLUMN_GIFT_ID
				+ " integer, " 
				+ ITableData.TABLE_GIFTS.COLUMN_CATEGORY_ID
				+ " integer, " 
				+ ITableData.TABLE_GIFTS.CAMPAIGN_NAME
				+ " text, " 
				+ ITableData.TABLE_GIFTS.PRODUCT_NAME
				+ " text, " 
				+ ITableData.TABLE_GIFTS.THUMB_ICON
				+ " text, " 
				+ ITableData.TABLE_GIFTS.BIG_IMAGE
				+ " text, "
				+ ITableData.TABLE_GIFTS.SHORT_DESC
				+ " text, "
				+ ITableData.TABLE_GIFTS.LONG_DESC
				+ " text, "
				+ ITableData.TABLE_GIFTS.MASTER_TAG
				+ " text, "
				+ ITableData.TABLE_GIFTS.MASTER_TAG_IMAGE
				+ " text, "
				+ ITableData.TABLE_GIFTS.FOOTER_TAG
				+ " text, "
				+ ITableData.TABLE_GIFTS.BRAND
				+ " text, "
				+ ITableData.TABLE_GIFTS.QUOPN_COUNT
				+ " integer, "
				+ ITableData.TABLE_GIFTS.CALL_TO_ACTION
				+ " text, "
				+ ITableData.TABLE_GIFTS.CTA_TEXT
				+ " text, "
				+ ITableData.TABLE_GIFTS.CTA_VALUE
				+ " text, "
				+ ITableData.TABLE_GIFTS.START_FORM
				+ " text, "
				+ ITableData.TABLE_GIFTS.END_DATE
				+ " text, "
				+ ITableData.TABLE_GIFTS.REDEMPTION_EXPIRY_DATE
				+ " text, "
				+ ITableData.TABLE_GIFTS.CAMPAIGN_MEDIA
				+ " text, "
				+ ITableData.TABLE_GIFTS.MULTI_ISSUE
				+ " text, "
				+ ITableData.TABLE_GIFTS.ISSUE_LIMIT
				+ " text, "
				+ ITableData.TABLE_GIFTS.REDEMPTION_CAP
				+ " text, "
				+ ITableData.TABLE_GIFTS.PROMOTION_ENABLED
				+ " text, "
				+ ITableData.TABLE_GIFTS.TOTAL_COUPONS_BLOCKED
				+ " text, "
				+ ITableData.TABLE_GIFTS.THUMB_ICON2
				+ " text, "
				+ ITableData.TABLE_GIFTS.SEARCH_TAGS
				+ " text, "
				+ ITableData.TABLE_GIFTS.GIFT_TARGET
				+ " text, "
				+ ITableData.TABLE_GIFTS.SERVE_CAP
				+ " text,"
				+ ITableData.TABLE_GIFTS.GIFT_TYPE
				+ " text default \'E\', "
				+ ITableData.TABLE_GIFTS.PARTNER_CODE
				+ " text, "
				+ ITableData.TABLE_GIFTS.TERMS_COND
				+ " text,"
				+ ITableData.TABLE_GIFTS.SORT_INDEX
				+ " int "
				
				+ ");";

		public final String TABLE_NAME = "gifts";
		public final String COLUMN_ID = "_id";
		public final String GIFT_STATE = "gift_state"; //integer default is 2
		
		public final String COLUMN_GIFT_ID = "id";
		public final String COLUMN_CATEGORY_ID =  "categoryid"; //integer
		public final String CAMPAIGN_NAME = "campaignname";
		public final String PRODUCT_NAME = "productname";
		public final String THUMB_ICON = "thumb_icon";
		public final String BIG_IMAGE = "big_image";
		public final String SHORT_DESC = "short_desc";
		public final String LONG_DESC = "long_desc";
		public final String MASTER_TAG = "master_tag";
		public final String MASTER_TAG_IMAGE = "mastertag_image";
		public final String FOOTER_TAG = "footer_tag";
		public final String BRAND = "brand";
		public final String QUOPN_COUNT = "quopn_count"; //integer
		public final String CALL_TO_ACTION = "call_to_action";
		public final String CTA_TEXT = "cta_text";
		public final String CTA_VALUE = "cta_value";
		public final String START_FORM = "startfrom";
		public final String END_DATE = "enddate";
		public final String REDEMPTION_EXPIRY_DATE = "redemption_expiry_date";
		public final String CAMPAIGN_MEDIA = "campaign_media";
		public final String MULTI_ISSUE ="multi_issue";
		public final String ISSUE_LIMIT ="issue_limit";
		public final String REDEMPTION_CAP ="redemption_cap";
		public final String PROMOTION_ENABLED ="promotion_enabled";
		public final String TOTAL_COUPONS_BLOCKED ="total_coupons_blocked";
		public final String THUMB_ICON2 ="thumb_icon2";
		public final String SEARCH_TAGS ="search_tags";
		public final String GIFT_TARGET ="gift_target";
		public final String SERVE_CAP ="serve_cap";
		public final String GIFT_TYPE ="gift_type";
		public final String PARTNER_CODE ="partner_code";
		public final String TERMS_COND ="terms_cond";
		public final String SORT_INDEX ="sort_index";
		
	}

    public interface TABLE_VOUCHER {
        public final String CREATE_STATEMENT = "create table "
                + ITableData.TABLE_VOUCHER.TABLE_NAME + "("
                + ITableData.TABLE_VOUCHER.COLUMN_ID
                + " integer primary key autoincrement, "
                + ITableData.TABLE_VOUCHER.COLUMN_PARTNER_ID
                + " integer, "
                + ITableData.TABLE_VOUCHER.PARTNER_NAME
                + " text, "
                + ITableData.TABLE_VOUCHER.THUMB_ICON
                + " text, "
                + ITableData.TABLE_VOUCHER.BIG_IMAGE
                + " text, "
                + ITableData.TABLE_VOUCHER.TOTAL_COUPONS
                + " integer, "
                + ITableData.TABLE_VOUCHER.AVAILABLE_COUPONS
                + " integer, "
                + ITableData.TABLE_VOUCHER.ISSUE_AVAILABLE
                + " integer, "
                + ITableData.TABLE_VOUCHER.ISSUED_COUPONS
                + " text,"
                + ITableData.TABLE_VOUCHER.PURCHASE_VALUE
                + " integer"

                + ");";

        public final String TABLE_NAME = "voucher";
        public final String COLUMN_ID = "_id";
        public final String COLUMN_PARTNER_ID = "partner_id";
        public final String PARTNER_NAME = "partner_name";
        public final String THUMB_ICON = "thumb_icon";
        public final String BIG_IMAGE = "big_image";
        public final String TOTAL_COUPONS = "total_coupons";
        public final String AVAILABLE_COUPONS = "available_coupons";
        public final String ISSUE_AVAILABLE = "issue_available";
        public final String ISSUED_COUPONS = "issued_coupons";
        public final String PURCHASE_VALUE = "purchase_value";

    }
}
