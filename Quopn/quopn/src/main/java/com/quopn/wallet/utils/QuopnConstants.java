package com.quopn.wallet.utils;

import android.content.Context;
import android.widget.Toast;

import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.data.model.CityData;
import com.quopn.wallet.data.model.StateData;

import java.util.List;

/**
 * @author Sandeep
 *
 */
public class QuopnConstants {
    //public static boolean ISADDTOCARTINCOMPLETE =true;
	public static boolean ISREMOVEFROMCART=false;
	public static String PARTNER_CODE="VOUCHER CODE";
	public static final int LEFTQUOPN = 0;
	public static final int RIGHTQUOPN = 1;
	public static String UPDATE_FLAG="update_flag";
	public static String UPDATION_LINK="updation_link";
	public static String UPDATION_MESSAGE="updation_message";
	public static String SELECT_STATE_ID="-1203";
	public static String SELECT_CITY_ID="-1204";
	public static String STATE_CITY_FILE="state_city.txt";
	public static String android_id = "";
	public static int versionCode = -1;
	public static String versionName = "";
	public static final String QUOPN_DEVICEID_FOLDER = ".SYS_";
	public static final String QUOPN_DEVICEID_FILE = "SYS_FILE.txt";
	public static final String QUOPN_LOG_FOLDER = "QUOPN_LOG";
	public static final String QUOPN_LOG_FILE = "QUOPN_LOG_FILE.txt";
	public static final int DETAIL_PAGE=1; 
	public static final int NO_DETAIL_PAGE=0;
	public static final String QUOPN_LOG_TAG = "QUOPN LOG";
	public static final String VIDEO_COMPLETED = "Video Completed";
	public static final int VIDEO_COMPLETED_REQUESTCODE = 100;
	public static final int GIFTVIDEO_COMPLETED_REQUESTCODE = 200;
	public static final int VERSION_CHECK_REQUESTCODE = 300;

	public static final int HOME_PRESS = 50;
	public static final int VIDEO_COMPLETED_RESULTCODE = 101;
	public static final String GIFTVIDEO_URL = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
	
	
	public static String SOURCE="";
	public static String CAMPAIGNID="";
	public static int AVAILABLEQUOPN=-1;
	public static int ALREADYISSUED=-1;
//	public static boolean isSMSLISTNERREGISTERED = false;


	// directory name to store captured images and videos
	public static final String IMAGE_DIRECTORY_NAME = "QuopnWallet";

	// Intent Extras constants
	public static final String INTENT_EXTRA_MOBILE_NO = "mobileno";
	public static final String INTENT_EXTRA_OTP_NO = "otp";
	public static final String INTENT_EXTRA_USERID = "userid";
	public static final String INTENT_EXTRA_IMAGETYPE = "IMAGETYPE";
	public static final String INTENT_EXTRA_CATEGORYNAME="CATEGORYNAME";
	public static final String INTENT_EXTRA_CATEGORYICONURL="CATEGORYICON_URL";
	public static final String INTENT_EXTRA_ID="ID";
	public static final String INTENT_EXTRA_CATEGORYID="CATEGORYID";
	public static final String INTENT_EXTRA_CAMPAIGNNAME="CAMPAIGNNAME";
	public static final String INTENT_EXTRA_PRODUCTNAME="PRODUCTNAME";
	public static final String INTENT_EXTRA_PRODUCTTYPE="PRODUCTTYPE";
	public static final String INTENT_EXTRA_THUMB_ICON="THUMB_ICON";
	public static final String INTENT_EXTRA_BIG_IMAGE="BIG_IMAGE";
	public static final String INTENT_EXTRA_SHORT_DESC="SHORT_DESC";
	public static final String INTENT_EXTRA_LONG_DESC="LONG_DESC";
	public static final String INTENT_EXTRA_MASTER_TAG="MASTER_TAG";
	public static final String INTENT_EXTRA_FOOTER_TAG="FOOTER_TAG";
	public static final String INTENT_EXTRA_BRAND="BRAND";
	public static final String INTENT_EXTRA_QUOPN_COUNT="QUOPN_COUNT";
	public static final String INTENT_EXTRA_CALL_TO_ACTION="CALL_TO_ACTION";
	public static final String INTENT_EXTRA_CTA_TEXT="CTA_TEXT";
	public static final String INTENT_EXTRA_STARTFROM="STARTFROM";
	public static final String INTENT_EXTRA_ENDDATE="ENDDATE";
	public static final String INTENT_EXTRA_TERMS_COND="TERMS_COND";
	public static final String INTENT_EXTRA_SUBMITTED_BY="SUBMITTED_BY";
//	public static final String[] MAIN_BOTTOM_BAR = {"My Gifts","Categories", "All", "Featured", "New", "Recommended","Expiring", "Search"};
	public static String[] MAIN_BOTTOM_BAR;
	public static final String[] CART_BOTTOM_BAR = {"My Cart"};// {"My Quopns","My Cart","My History"};
	public static final int CONNECTION_TIME_OUT = 1000 * 30;
	
	//Tutorial
	public static final String TUTORIAL_PREF = "tuts_toggle";
	public static final String USER_TUTORIAL_PREF = "user_tuts_toggle";
	public static final String TUTORIAL_ON = "ON";
	public static final String TUTORIAL_OFF = "OFF";
	public static final String TUTORIAL_PREF_CAT = "tuts_toggle_cat";
	public static final String TUTORIAL_PREF_DETAILS = "tuts_toggle_details";
	public static final String TUTORIAL_PREF_LISTING = "tuts_toggle_lisitng";
	public static final String TUTORIAL_PREF_GIFTING = "tuts_toggle_gifting";
	public static final String TUTORIAL_PREF_CART = "tuts_toggle_cart";
	public static final String TUTORIAL_PREF_MYQUOPNS = "tuts_toggle_myquopns";
	public static final String TUTORIAL_PREF_OPEN = "tuts_toggle_open";
	public static final String PREF_ALL_TUTS_SEEN = "tuts_all_seen";
	public static final String PREF_ALL_TUTS_COUNT = "tuts_all_count";
	public static final int ALL_TUTS_COUNT = 5;

	// Connection codes
		public static final int REGISTRATION_CODE = 0;
		public static final int OTP_CODE = 1;
		public static final int CATEGORY_CODE = 2;
		public static final int RESEND_OTP_CODE = 3;
		public static final int PROFILE_UPDATE_CODE = 4;
		public static final int UCN_NUMBER_CODE = 5;
		public static final int PROFILE_GET_CODE = 6;
		public static final int INVITE_USER_CODE = 7;
		public static final int MYQUOPN_CODE = 8;
		public static final int FOOTER_TAG_CODE = 9;
		public static final int GENERATE_PIN_CODE = 10;
		public static final int STATE_LIST_CODE = 11;
		public static final int CITY_LIST_CODE = 12;
		public static final int INVITE_USER = 13;
		public static final int HISTORY_CODE = 14;
		public static final int CART_CODE = 15;
		public static final int ADD_TO_CART_CODE = 16;
		public static final int REMOVE_FROM_CART_CODE = 17;
		public static final int CAMPAIGN_VALIDATION_CODE = 18;
		public static final int VIDEO_ISSUE_CODE = 19;
		public static final int WEB_ISSUE_CODE = 20;
		public static final int INVITE_MOBIEL_NO_CHECKING = 21;
		public static final int NOTIFY_STATUS_CODE = 22;
		public static final int NOTIFY_STATUS_SENT = 23;
		public static final int MAP_API_CODE = 24;
		public static final int FEEDBACK_API_CODE = 25;
		public static final int ANALYSIS_CODE = 27;
		public static final int TUTORIAL_STATUS_CODE = 28;
		public static final int SILENT_TUTORIAL_STATUS_CODE = 29;
		public static final int CART_ADD_ALL = 30;
		public static final int CART_REMOVE_ALL = 31;
		public static final int IS_FIRSTINSTALL_TRACKER = 32;
		public static final int CAMPAIGN_DETAILS_CODE = 33;
		public static final int STATE_CITY_LIST_CODE = 34;
		public static final int ERROR_REPORT_CODE = 35;
		public static final int LOGOUT_CODE = 36;
		public static final int PROMO_CODE = 37;
		public static final int VERSION_CHECK_CODE = 38;
		public static final int SHOP_LIST_CODE = 39;
		public static final int SEARCH_LIST_CODE = 40;
		public static final int TERMS_AND_CONDITIONS = 41;
		public static final int PRIVACY_POLICY = 42;
		public static final int FAQ = 43;
        public static final int NEW_CAMPAIGNLSTING_CODE = 44;
        public static final int REQUEST_PIN_CODE = 45;
		public static final int SHMART_CHECK_STATUS = 46;
		public static final int SHMART_CREATE_USER = 47;
		public static final int SHMART_GENERATE_OTP = 48;
	    public static final int SHMART_VERIFY_OTP = 49;
        public static final int REFRESH_SESSION_CODE = 50;
        public static final int SHMART_VOUCHER_LIST_CODE = 51;
		public static final int SHMART_REQUEST_OTP = 52;
		public static final int SHMART_VERIFY_OTP_AND_CHNG_TRX_PWD = 53;
        public static final int QUOPN_MOBILE_WALLET_FAQ = 54;
        public static final int QUOPN_MOBILE_WALLET_TNC = 55;
        public static final int QUOPN_MOBILE_WALLET_ANNOUNCEMENT = 56;
		public static final int QUOPN_MOBILE_CITRUS_LIST_WALLET = 58;
		public static final int QUOPN_CITRUS_LOGWALLETSTATS = 59;
		public static final int QUOPN_VALIDATE_TXN_PIN = 60;


	
	public static String SEARCHTEXT = "";
	public static String PROFILE_DATA = "";
	public static String PROFILE_PIC_DATA = null;
	public static boolean PROFILE_SAVE_FLAG = false;
	
	public static final int MOBILE_NO_LENGTH=10;
	public static final int OTP_NO_LENGTH=4;
	public static final String SPACEPATTERN = "^(?=[^ ])[a-zA-Z0-9 +&-]+(?<=[^ ])$"; //regular DOT not valid
	public static final String SPACEPATTERN_MORETHANONE = "^[\\s]+[\\s]"; //regular DOT not valid
	public static final String NAMEPATTERN = "^(?=[^ ])[a-zA-Z0-9 +&-]+(?<=[^ ])$"; //regular DOT not valid
//  "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$"  //regular with DOT also valid
//  "^\pL+[\pL\pZ\pP]{0,}$" //unicode
	public static final String MOBILEPATTERN = "[0-9]{10}";
	public static final String OTPPATTERN = "[0-9]{6,8}";
	public static final String PINPATTERN = "[0-9]{4}";
	public static final String TXNPINPATTERN = "[0-9]{4}";
	public static final String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-zA-Z0-9\\-\\_\\.]+\\.[a-zA-Z0-9]{2,}";//"[a-zA-Z0-9._-]+@[a-z0-9-]+\\.+[a-z]+";

//	public static final String SERVERERROR = "Please try again!";
	public static final String PARSE_ERROR_TITLE = "Technical Error";
//	public static final String PARSE_ERROR_MESSAGE = "Please try again!";
	//public static final String PARSE_ERROR_TITLE = "Unexpected Error";
	//public static final String PARSE_ERROR_MESSAGE = "Something unexpected happened";
	public static final String ERROR = "Error";
	

	// Google Project Number
	//public static final String GOOGLE_PROJECT_ID = "222388234257";
	public static final String GOOGLE_PROJECT_ID = "1021246341303";
	public static final String MESSAGE_KEY = "message";
	public static final String ALT_KEY = "alt";
	public static final String REDEEM_NOTIFICATION_KEY = "redeemed";
	
	

	// Pref Keys

	
	public static List<StateData> stateData;
	public static List<CityData> cityData;
	
	//public static int MY_QUOPN_COUNT;
	public static int MY_CART_COUNT;
	public static int NOTIFICATION_COUNT;
	
	/* Phone number for 'Contact Us' section */
	public static String PHONE_PROTO = "tel:";
	public static String CONTACT_US = "+919004590045";
	
	public static String PROFILE_COMPLETE_KEY = "isProfileComplete";
	
	public static String BROADCAST_UPDATE_QUOPNS = "updateQuopn";
	public static String BROADCAST_REDEEM_QUOPNS = "redeemQuopn";
	public static String BROADCAST_SWITCH_TO_ALL_TAB = "Show_All_Quopns_Tab";
	public static String BROADCAST_UPDATE_NOTIFICATIONCOUNTER = "updateNotificationCounter";
	public static String BROADCAST_INITIAL_MYCARTCOUNTER = "InitialMyCartCounter";
	public static String BROADCAST_UPDATE_MYCARTCOUNTER = "updateMyCartCounter";
	public static String BROADCAST_UPDATE_MYCART_AFTER_REMOVEFROMCART = "updateMyCart";
	public static String BROADCAST_SHOW_TAB = "showTab";
	public static String BROADCAST_SHOW_GIFT_TAB = "showGiftTab";
	public static String BROADCAST_SHOW_CART = "showCart";
	public static String BROADCAST_CHANGE_SESSION_ID = "changeSessionId";
	public static String BROADCAST_LOGOUT_INVALID_SESSION = "logoutDueToInvalidSession";
	public static String BROADCAST_UPDATE_STATE_CITY = "updateStateCity";
	public static String BROADCAST_PARSE_ANNOUNCEMENT_DEEP_LINKS = "parseAnnouncementDeepLinks";
	public static String BROADCAST_PARSE_NOTIF_ACTIVITY_DEEP_LINKS = "parseNotifActivityDeepLinks";
	
	public static String DELETE_NOTIFICATIONS = "delete_notifications";
	public static String INTENT_ACTION_VIDEO_WATCHED = "com.quopn.intent.action.videoWatched";
	
	//siv
//	public static final String CONFIRM_BUTTON_LBL = "Confirm";
//	public static final String INVITE_REQUEST_SUCESSFULLY_REGISTERED_MSG = "Send SMS to complete the invite.";
//	public static final String INVITE_REQUEST_SUCESSFULLY_REGISTERED_TITLE = "Invite request sucessfully registered";
//	public static final String CAMERA_NOT_FOUND = "Device doesnot have/support a camera.";
//	public static final String CAMERA_INITIALIZATION_FAILED = "Camera initialization failed.";
//	public static final CharSequence START_CAMERA = "Start Camera";
	public static final String PERSONAL_VIDEO_FILE_PATH = "personal_video_file_path";
	
	public static final int AGE_CONSTRAINT = 18;
	
	public static final String PROFILE_IMG_FOLDER = "QuopnProfile";
	public static final String PROFILE_IMG_NAME = "profile.jpg";
	public static final String ABOUT_US_GROUP_TO_EXPAND = "ABOUT_US_GROUP_TO_EXPAND";
	
	public static final int BOOL_TRUE = 1;
	public static final int BOOL_FALSE = 0;
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	// upgrade popup control

	public static boolean isUpdateTrue_ForWallet = false;
	public static boolean isUpdateTrue_ForAnnouncement = false;

	public interface CONN_PARAMS{
		public static final String walletId = "walletId";
		public static final String mobileWalletId = "mobileWalletId";
		public static final String name = "name";
		public static final String email_id = "email_id";
		public static final String otp = "otp";
		public static final String trans_pass = "transaction_pwd";

	}

	public interface INTENT_KEYS{
		public static final String callVerifyOTP = "callVerifyOTP";
		public static final String callChangeTransPwd = "callChangeTransPwd";
		public static final String x_session_old = "session_id_old";
		public static final String x_session_new = "session_id_new";
		public static final String should_announcement_be_called = "should_announcement_be_called";
		public static final String shmart_regn_success_msg = "shmart_regn_success_msg";
		public static final String shmart_balance = "shmart_balance";
		public static final String citrus_balance = "citrus_balance";
		public static final String promo = "promo";
	}

	public interface DEEP_LINK {
		public static final String invite = "invite";
		public static final String promo = "promo";
		public static final String campaign = "campaign";
		public static final String category = "category";
		public static final String apptour = "apptour";
		public static final String wallet = "wallet";
		public static final String allquopns = "allquopns";
	}

	public interface NOTIFICATION_ID {
		public static final String SILENT = "1";
		public static final String TEXT = "2";
		public static final String IMAGE = "3";
		public static final String DYNAMIC = "4";
	}

	public interface GCM_DEEP_LINK {
		public static final String KEY_01 = "deep_link";
		public static final String SCHEME = "quopn";
		public static final String invite = "invite";
		public static final String promo = "promo";
		public static final String campaign = "campaign";
		public static final String category = "category";
		public static final String apptour = "apptour";
		public static final String wallet = "wallet";
		public static final String allquopns = "allquopns";
	}

	public interface NOTIF_INTENT_DATA {
		public static final String screen = "screen";
		public static final String value = "value";
		public static final String caption = "caption";
	}

	public static enum PaymentType {
		LOAD_MONEY, CITRUS_CASH, PG_PAYMENT, DYNAMIC_PRICING;
	}

	public enum DPRequestType {
		SEARCH_AND_APPLY, CALCULATE_PRICING, VALIDATE_RULE;
	}

	public static void showToast(Context context, String message) {
		if (context == null) {
			context = QuopnApplication.getInstance().getApplicationContext();
		}
		Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public static enum WalletType {
		NONE, SHMART, CITRUS;
	}
}
