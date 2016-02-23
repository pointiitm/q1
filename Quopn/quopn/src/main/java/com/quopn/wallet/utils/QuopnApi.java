package com.quopn.wallet.utils;

import android.util.Log;

public interface QuopnApi {
	public enum Mode {
		DEMO("demo", "Demo Server")
		, STAGING("staging", "Staging Server")
		, PROD("prod", "");

		private String serverPrefix;
		private String splashCaption;
		private static String TAG = "QuopnApi";

		Mode(String serverPrefix, String splashCaption) {
			this.serverPrefix = serverPrefix;
			this.splashCaption = splashCaption;
		}

		public String getServerPrefix() { return serverPrefix; }
		public String getSplashCaption() { return splashCaption; }
	}

	public enum Scheme {
		INSECURE("http")
		, SECURE("https");

		private String scheme;
		Scheme(String scheme) { this.scheme = scheme; }
		private String getScheme() { return scheme; }
	}

	public enum Branch {
		USER("user")
		, SERVICES("services")
		, GATEWAY("paymentGateway");

		private String branch;
		Branch(String branch) { this.branch = branch; }
		public String getBranch() { return branch; }
	}

	Mode currentMode = Mode.STAGING;

	interface ParamKey{
		String WALLETID="walletid"; 
		String AUTHORIZATION="Authorization";
		public static final String x_session = "x-session";
		public static final String session_id_old = "session_id_old";
		public static final String session_id_new = "session_id_new";
		String USER_ID="userid";
		String STATUS="status";
		String CAMPAIGN_ID="campaignid";  
		String SOURCE="source"; 
		String NAME="name";
		String MOBILE_NO="mobileno";
		String DEVICE_ID="deviceid";
		String MOBILE="mobile";
		String GENDER="gender";
		String VIDEO="video"; 
		String DIRECT_WALLET="direct_wallet";
		String VIDEO_WALLET="video_wallet";
		String UCNID="ucnid";
		String CARTID="cartid";
		String QUOPNCODE="quopncode";
		String PROMOCODE="promocode";
        String CATEGORYID="categoryid";
		String DEFAULTWALLET="defaultWallet";

		// citrus
		String APINAME="apiName";
		String APITYPE="apiType";
		String REQUESTPARAMS="requestParams";
		String RESPONSEPARAMS="responseParams";
		String CREATEUSER="createUser";
		String ADDBENEFICIARY="addBeneficiary";
		String DELETEBENEFICIERY="deleteBeneficiery";
		String GETBENEFICIARYLIST="getBeneficiaryList";
		String WALLETTOWALLETTRANSFER="walletToWalletTransfer";
		String REFRESHACCESSTOKEN="refreshAccessToken";
		String LOADWALLET="loadWallet";
		String TRANSFERTOBANK="transferToBank";
		String APITYPE_P="P";
		String APITYPE_D="D";
		String CONSUMER_TYPE="consumer_type";
		String EMAIL="email";
		String FIRSTNAME="firstName";
		String LASTNAME="lastName";
		String UUID="uuid";

		// generic
		String TRANSACTION_PIN="transaction_pin";
	}
	 
	static final String BASE_PATH = ".quopn.com/quopnwallet/v2/";
//	static final String BASE_PATH_SHMART_DIRECT = "180.179.146.81/wallet/v1/";
	String SERVER_PATH_SERVICES
	 		= Scheme.SECURE.getScheme() + "://" + currentMode.getServerPrefix() + BASE_PATH + Branch.SERVICES.getBranch() + "/";
	String SERVER_PATH_USER
			= Scheme.SECURE.getScheme() + "://" + currentMode.getServerPrefix() + BASE_PATH + Branch.USER.getBranch() + "/";
	String SERVER_PATH_GATEWAY
			= Scheme.SECURE.getScheme() + "://" + currentMode.getServerPrefix() + BASE_PATH + Branch.GATEWAY.getBranch() + "/";
	String SERVER_PATH_SHMART_HTTP
			= Scheme.SECURE.getScheme() + "://" + currentMode.getServerPrefix() + BASE_PATH + Branch.GATEWAY.getBranch() + "/";

	 String REGISTRATION = SERVER_PATH_USER+"register";//SERVER_PATH_V2_TEST+"register";
	 String OTP_VERIFICATION = SERVER_PATH_USER+"verifypin";//SERVER_PATH_V2_TEST+"verifypin";
	 String RESEND_OTP = SERVER_PATH_USER+"resendpin";
	 String PROFILE = SERVER_PATH_USER+"auth";//SERVER_PATH_V2_TEST+"auth";
	 String CATEGORIES = SERVER_PATH_SERVICES+"categoryListing"; //SERVER_PATH_SERVICES+"campaignListing";
     String NEWCAMPAIGNLISTING = SERVER_PATH_SERVICES+"campaignListing?categoryid=";
	 String FOOTER_BAR = SERVER_PATH_SERVICES+"footer";
	 String INVITE = SERVER_PATH_USER+"inviteuser";// SERVER_PATH_V2_TEST + "inviteuser";

		
	 String PROFILE_UPDATE = SERVER_PATH_USER+"profile";
	 //String CHANGEPIN = SERVER_PATH_USER +"changepin";
	 String CHANGE_NUMBER = SERVER_PATH_SERVICES +"walletTransfer";
	 String STATE_LIST = SERVER_PATH_USER + "states";
	 String CITY_LIST = SERVER_PATH_USER + "cities";
	 String GCM_REG  = SERVER_PATH_USER + "send_gcm";
	 String STATE_CITY_LIST = SERVER_PATH_USER + "statecities";
	 String VERSION_CHECK_URL = SERVER_PATH_USER + "check_version";

	 String MY_QUOPN = SERVER_PATH_SERVICES+"getUserQuopns";//SERVER_PATH_SOLUTIONS+"myquopn";
	 String MY_HISTORY = SERVER_PATH_SERVICES+"getUserHistory";
	 String CART_URL =  SERVER_PATH_SERVICES+"getUserCart";
	 String ADD_TO_CART =  SERVER_PATH_SERVICES+"addToCart";
	 String REMOVE_FROM_CART =  SERVER_PATH_SERVICES+"removeFromCart";
	 String ADD_ALL_TO_CART =  SERVER_PATH_SERVICES+"addAllCart";
	 String REMOVE_ALL_FROM_CART =  SERVER_PATH_SERVICES+"removeAllCart";
	 String CAMPAIGN_VALIDATION =  SERVER_PATH_SERVICES+"campaignValidation";
	 String CAMPAIGN_DETAILS_URL =  SERVER_PATH_SERVICES+"campaignDetails";
	
	 String NOTIFICATIONS_STATUS =  SERVER_PATH_USER+"notification_status";
	 String TUTORIAL_STATUS =  SERVER_PATH_USER+"tutorial_status";
	 String NOTIFICATIONS_SENT =  SERVER_PATH_USER+"update_gcm";
	 String MAP_URL = SERVER_PATH_USER + "map";
	 //String FEEDBACK_URL =  SERVER_PATH_USER+"getfeedback"; // old code 
	 //siv
	 String FEEDBACK_URL =  SERVER_PATH_USER+"feedback";
	 String ANALYSIS =  SERVER_PATH_USER+"stats";
	
	 String VIDEO_ISSUE = SERVER_PATH_SERVICES+"issueWalletQuopn";
	 String UCN_URL = SERVER_PATH_SERVICES+"ucn";
	 String WEB_ISSUE = SERVER_PATH_SERVICES+"issueWalletQuopn";
	 String INVITE_MOBILE_NO_CHECKING = SERVER_PATH_USER+"checkinviteuser";
	 String USER_ID = "userId";
	
//	 String TNC_URL = SERVER_PATH_USER + "apptnc";
//	 String PRIVACY_POLICY_URL = SERVER_PATH_USER + "privacypolicy";
	 String PRIVACY_POLICY_URL = SERVER_PATH_USER + "infopage/privacypolicy";
	 String FAQA_URL = SERVER_PATH_USER + "infopage/faqs";
	 String TERMS_AND_CONDITION_DETAILS_URL = SERVER_PATH_USER + "gettnc";
	 String TERMS_AND_CONDITION_URL = SERVER_PATH_USER + "infopage/termsofuse";
	 
	 String STORE_LIST_URL = SERVER_PATH_USER + "getstoredata";
	 String SEARCH_LIST_URL = SERVER_PATH_USER + "getstoredatasearch";
	 
	 String PLAY_STORE_ULR = "http://app.quo.pn";
	String QUOPN_DEEP_LINK_URL = "http://app.quo.pn/campaign/";
	 
	 String ABOUT_US = SERVER_PATH_USER+"aboutus";
	 
	 String FIRST_INSTALL_TRACKER = SERVER_PATH_USER+"InstallTrack";
	 
	 String ERROR_REPORT = SERVER_PATH_USER + "App_error_Track";
	 
	 String LOGOUT = SERVER_PATH_USER + "UpdateLogOut";
	 
	 String PROMOCODE = SERVER_PATH_SERVICES + "authenticatePromoCode";
     String GENERATE_PIN = SERVER_PATH_USER +"generatepin";
     String REQUEST_PIN = SERVER_PATH_USER +"requestpin";
     String REFRESH_SESSION = SERVER_PATH_USER +"refreshsession";

    String SHMART_CHECK_STATUS = SERVER_PATH_SHMART_HTTP + "checkStatus";

    String SHMART_CREATE_USER = SERVER_PATH_SHMART_HTTP + "createUser";

    String SHMART_GENERATE_OTP = SERVER_PATH_SHMART_HTTP + "generateOTP";

    String SHMART_VERIFY_OTP = SERVER_PATH_SHMART_HTTP + "verifyOTP";

    String SHMART_VOUCHER_LIST = SERVER_PATH_GATEWAY + "paidVoucherListing";

	String SHMART_REQUEST_OTP = SERVER_PATH_SHMART_HTTP + "requestOTP";

	String SHMART_VERIFY_AND_CHANGE_TRX_PWD = SERVER_PATH_SHMART_HTTP + "changeTransactionPassword";

    String QUOPN_MOBILE_WALLET_FAQ_URL_SHMART = SERVER_PATH_USER + "mobile_wallet_info/"+EWalletDefault.MOBILE_WALLET_SHMART_ID+"/faqs";

    String QUOPN_MOBILE_WALLET_TNC_URL_SHMART = SERVER_PATH_USER + "mobile_wallet_info/"+EWalletDefault.MOBILE_WALLET_SHMART_ID+"/tnc";

	String QUOPN_MOBILE_WALLET_FAQ_URL_CITRUS = SERVER_PATH_USER + "mobile_wallet_info/"+EWalletDefault.MOBILE_WALLET_CITRUS_ID+"/faqs";

	String QUOPN_MOBILE_WALLET_TNC_URL_CITRUS = SERVER_PATH_USER + "mobile_wallet_info/"+EWalletDefault.MOBILE_WALLET_CITRUS_ID+"/tnc";

    String QUOPN_MOBILE_WALLET_ANNOUNCMENT_URL = SERVER_PATH_USER + "announcement/";

	String QUOPN_CITRUS_LIST_WALLET_CREDENTIAL = SERVER_PATH_GATEWAY + "listWallets";

	String QUOPN_CITRUS_LOGWALLETSTATS = SERVER_PATH_GATEWAY + "logWalletStats";

	String QUOPN_CITRUS_VALIDATE_TXN_PIN = SERVER_PATH_GATEWAY + "validateTxnPin";

	enum EWalletApi {
		LIST("mobileWalletListing", 101)
		, BALANCE("balanceEnquiry", 102)
		, LOAD("loadWallet", 103)
		, BENEFICIARIES("getBeneficieryList", 104)
		, TRANSFER_TO_MOBILE("walletToWalletTransfer", 105)
		, PAY("walletDirectDebit", 106)
		, ADD_BENEFICIARY("addBeneficiary", 107)
		, TRANSFER_TO_BENEFICIARY("transferToBank", 108)
		, REQUEST("transactionRequery", 109)
		, PREFERENCE("updateRedemptionPreference", 110)
		, OTP("generateOTP", 111)
		, LOCAL_OTP("requestOTP", 112)
		, CHANGE_TXN_PWD("changeTransactionPassword", 113)
		, GET_PREF("getRedemptionPreference", 114)
		, SET_PREF("updateRedemptionPreference", 115)
		, TXNS("getLastTransactions", 116)
		, FEATURES("listWalletFeatures", 117)
		, DELETE_BENEFICIARY("deleteBeneficiery", 118)
		, CHECK_STATUS("checkStatus", 119);

		private String endPoint;
		private int apiCode;
		EWalletApi(String endPoint, int apiCode) {
			this.endPoint = endPoint;
			this.apiCode = apiCode;
		}
		public String getEndPoint() {
			if (apiCode >= 100 && apiCode < 200) {
				return SERVER_PATH_GATEWAY + endPoint;
			} else {
//				return BASE_PATH_SHMART_DIRECT + endPoint;
				Log.e("getEndPoint","Shmart staging path requested");
				return SERVER_PATH_GATEWAY + endPoint;
			}
		}
		public int getApiCode() { return apiCode; }

		public static EWalletApi fromCode(int apiCode) {
			for (EWalletApi api:EWalletApi.values()) {
				if (api.apiCode == apiCode) {
					return api;
				}
			}

			return null;
		}
	}

	enum EWalletRequestParam {
		WALLET_ID("walletId")
		, MOBILE_WALLET_ID("mobileWalletId")
		, TXN_PWD("transaction_pwd")
		, OTP("otp")
		, W2W_AMOUNT("amount")
		, W2W_FRIEND_MOBILE("friend_mobileNo")
		, W2W_FRIEND_EMAIL("friend_email")
		, W2W_FRIEND_NAME("friend_name")
		, W2W_MESSAGE("message")
		, PAYEE_ACCT_NUM("beneficiary_accountNo")
		, PAYEE_ACCT_TYPE("beneficiary_accountType")
		, PAYEE_NAME("beneficiary_name")
		, PAYEE_MOBILE("beneficiary_mobileNo")
		, PAYEE_IFSC("ifsc_code")
		, PAYEE_ADDR("addr")
		, BENEFICIARY("BeneficiaryCode")
		, BENEFICIARY_CODE("beneficiary_code")
		, AMOUNT("amount")
		, PLATFORM("platform")
		, PREF("setting");

		private String name;
		EWalletRequestParam(String name) { this.name = name; }
		public String getName() { return name; }
	}

	enum EWalletResponseParam {

	}

	enum EWalletHeader {
		AUTH("Authorization");

		private String header;
		EWalletHeader(String header) { this.header = header; }
		public String getHeader() { return header; }
	}

	interface EWalletDefault {
		String AUTH_HEADER = "MTg2ZWY0MmZlMTYxM2FlYTJjMTAyZTViNWM3YzM0MGE6YThkZTZlYjczNTUxODg2YzBhNzA5NThjMDQ0ZWNlYWU=";
		String MOBILE_WALLET_SHMART_ID = "1";
		String MOBILE_WALLET_CITRUS_ID = "2";
		String MOBILE_WALLET_NONE = "0";
		String FRIEND_NAME = "fr13nd";
		String FRIEND_EMAIL = "testpoint1@mailinator.com";
		String PAYEE_ADDR = "Mumbai";
		String PLATFORM = "android";
	}

    public interface SHMART_ERROR_CODES {
        public final String CUSTOMER_READY = "000";
		public final String TRANS_PWD_BLANK = "001";
        public final String CUSTOMER_NOT_EXIST = "11";//Customer does not exist
		public final String WALLET_BAL_INSUFF = "12";//No sufficient balance in wallet
        public final String EMAIL_EXISTS = "14";//Email address already in use
        public final String NAME_INVALID = "157";//Invalid parameter Name
		public final String IFSC_INVALID = "158";//Invalid parameter BankIfscode
		public final String ACC_NO_INVALID = "159";//Invalid parameter BankAccountNumber
		public final String BENEFICIARY_CODE_INVALID = "160";//Invalid parameter BeneficiaryCode
		public final String BENEFICIARY_EMAIL_INVALID = "165";//Invalid parameter BeneficiaryEmail
		public final String BENEFICIARY_MOBILE_INVALID = "166";//Invalid parameter BeneficiaryMobile
		public final String TRANS_EXPIRED = "205";//Requested Transaction has been expired
		public final String INSUFF_DATA_FOR_VALIDATING_CARD = "208";//Insufficient Data for validating CardLoad
		public final String MAX_BAL_FOR_ACC = "218";//Max. Balance allowed in this Wallet: xxxx. Existing Balance: xxxx. Amount tried: xxxx
		public final String MAX_BAL_FOR_CUST = "219";//Max. Balance allowed for the customer: xxxx. Existing Balance: xxxx. Amount tried: xxxx
		public final String INSUFF_DATA_FOR_LOADING_WALLET = "221";//Insufficient Data for loading wallet
		public final String TRANS_FAIL_TECH_FAILURE = "228";//Transaction failed due to some technical problem, please try again later.
		public final String ACTIVE_BENEF_NOT_FOUND = "252";//Active Beneficiary Not Found
		public final String FUNDS_CANT_TRF_FROM_PROMO_WALLET = "255";//Funds can not be transferred from Promo Wallet
		public final String FUNDS_CANT_TRF_TO_SAME_CUST = "257";//Funds can not be transferred to same customer
		public final String WALLET_TRF_FAILURE = "259";//Wallet Transfer not successful
		public final String DATA_MISS_ADD_CUST = "261";//Data missing for adding customer
		public final String TRANS_CODE_GEN_FAIL = "265";//Unable to generate transaction Code
		public final String REG_CUST_FAIL = "266";//Unable to register customer
		public final String CUST_BAL_FAIL = "270";//Unable to check the customer balance
		public final String WALLET_BAL_FAIL = "271";//Unable to check the wallet balance 271
		public final String REG_RECORD_QUERY_FAIL = "274";//Unable to get query registration record
		public final String STATEMENT_GEN_FAIL = "275";//Unable to generate the statement
		public final String INVALID_BENEF_CODE = "276";//Invalid BeneficiaryCode
		public final String BENEF_DEACTIVATION_FAIL = "277";//Unable to deactivate beneficiary record
		public final String BENEF_QUERY_FEATURE_NOT_ALLOWED = "278";//Query Beneficiary feature is not allowed for this product
		public final String REMITTER_WALL_CODE_INVAL = "300";//Remitter Wallet Code is not valid
		public final String UPDATE_CUST_FAIL = "304";//Unable to update Customer
		public final String BENEF_REC_FAIL = "306";//Unable to get Beneficiary record
		public final String REMIT_TRANS_FAIL = "307";//Remittance Transaction not successful
		public final String REMIT_NOT_ALLOWED = "308";//Remittance not allowed
		public final String REMIT_INSUFF_DATA = "309";//Insufficient Data for validating Remittance
		public final String INSUFF_FUND_ = "310";//Customer does not have sufficient fund. Customer Balance: xxxx Amount to be deducted: xxxx
		public final String AMT_PER_TRANS_EXCEEDED = "311";//Amount exceeds Max. Amount Per Txn
		public final String AMT_PER_WALLET_EXCEEDED = "312";//Remittance Amount will exceed Max. Amount of xxxx Remittance Allowed for Wallet. Max xxxx Remittance Allowed: xxxx. Amount of Remittance already done: xxxx. Amount tried: xxxx
		public final String RECORD_QUERY_FAIL = "317";//Unable to get query record
		public final String QUERY_REQ_OR_BENEF_CODE_INVALID = "318";//Invalid either QueryReqNo or BeneficiaryCode
		public final String QUERY_REQ_NO_INVALID = "319";//Invalid QueryReqNo
		public final String BENEF_REC_QUERY_FAIL = "320";//Unable to get query beneficiary record
		public final String CUST_NO_BENEF = "321";//Customer does not have any beneficiary
		public final String BENEF_ADDR_LONG = "352";//Beneficiary Address is too long
		public final String BANK_DETAILS_INCOMP = "353";//Bank details are incomplete
		public final String BENEF_WITH_SAME_ACC_EXISTS = "355";//Beneficiary with same Bank Account No. exists
		public final String BENEF_REGN_FAIL = "357";//Unable to register beneficiary
		public final String TRANS_REQ_FAIL = "360";//Debit Transaction Request not successful
		public final String TRANS_NOT_FOUND = "361";//Transaction not found
		public final String TRANS_CODE_FOR_REFUND_FAIL = "365";//Transaction Code for refund amount could not be generated at this time. Please try later.
		public final String REMIT_REQ_DATA_MISSING = "367";//Remittance request data missing!
        public final String OTP_ACTIVATION_PENDING = "100";//User not  activated , OTP activation is pending
        public final String MOBILE_NUM_EXISTS = "101";// User with mobileNo already exists
        public final String INTERNAL_SERVER_ERROR = "102";// Internal error; Server error.
        public final String OTP_ALREADY_VALIDATED = "103";//Customer already activated (OTP Already validated)
        public final String CUSTOMER_DOES_NOT_EXIST = "104";//Customer does not exists
        public final String INCORRECT_OTP = "105";//Incorrect OTP
        public final String ACTIVATION_PENDING = "106";//Customer not activated
		public final String BALANCE_FETCH_FAIL = "107";//Could not fetch balance
		public final String NO_VIRTUAL_FLOAT = "108";//No virtual float
		public final String VIRTUAL_FLOAT_BAL_INSUFF = "109";//Insufficient virtual float balance
		public final String PARAM_VALUE_INSUFF = "110";//Mandatory parameter missing in the request, invalid parameter values
		public final String MERCHANT_REF_ID_DUPLICATE = "111";//Duplicate merchant ref ID (in transaction api)
        public final String MOBILE_NUMBER_INVALID_1 = "114";//Invalid mobile number. Only 10 digit mobile number can be used
        public final String MOBILE_NUMBER_INVALID_2 = "313";//Invalid mobile number. Only 10 digit mobile number can be used
    }

	public interface CITRUS_PARAMS {
		public final String MERCHANTTRANSACTIONID = "merchantTransactionId";
		public final String MERCHANT = "merchant";
		public final String CUSTOMER = "customer";
		public final String PLATFORM = "platform";
		public final String TRANSACTION_ID = "transaction_id";
		public final String AMOUNT = "amount";
		public final String VALUE = "value";
		public final String CURRENCY = "currency";
		public final String INR = "INR";
		public final String DESCRIPTION = "description";
		public final String LOAD_MONEY = "load money";
		public final String SIGNATURE = "signature";
		public final String MERCHANTACCESSKEY = "merchantAccessKey";
		public final String PGRESPCODE = "pgRespCode";
		public final String TXMSG = "txMsg";
		public final String TRANSACTIONDATETIME = "transactionDateTime";
		public final String TRANSACTIONSTATUS = "transactionStatus";
		public final String MESSAGE = "message";
	}
}
