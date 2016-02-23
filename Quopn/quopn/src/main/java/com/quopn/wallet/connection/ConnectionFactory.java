package com.quopn.wallet.connection;

/** @author Sumeet
 * */

import android.content.Context;

import com.android.volley.Request;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Connector;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;

import java.util.Map;

public class ConnectionFactory extends ConnectRequest {
	
	Connector connector = null;

		public ConnectionFactory(Context context,
			ConnectionListener connectionListener) {
		super();
		this.mConnectionListener = connectionListener;
		this.mContext = context;
	}

	public void setHeaderParams(Map<String, String> headerParams) {
		this.mHeaderParams = headerParams;
	}

	public void setPostParams(Map<String, String> postParams) {
		this.mPostParams = postParams;
	}

    public Connector createConnection(int type, String argID) {
        String requestTag = type + "+" + argID;

        switch (type) {
        case QuopnConstants.NEW_CAMPAIGNLSTING_CODE:
        connector = new RequestManager(mContext, mConnectionListener,QuopnApi.NEWCAMPAIGNLISTING+argID,QuopnConstants.NEW_CAMPAIGNLSTING_CODE,Request.Method.GET, requestTag);
        break;
        case QuopnConstants.QUOPN_MOBILE_WALLET_ANNOUNCEMENT:
        connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_MOBILE_WALLET_ANNOUNCMENT_URL+argID, QuopnConstants.QUOPN_MOBILE_WALLET_ANNOUNCEMENT,Request.Method.GET,requestTag);
        break;
            default:
            break;
        }


        if (mPostParams != null) connector.setPostParams(mPostParams);

        if (mHeaderParams != null) connector.setHeaderParams(mHeaderParams);

        connector.connect();

        return connector;
    }

	public Connector createConnection(int type) {
		switch (type) {
			case QuopnConstants.REGISTRATION_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.REGISTRATION, QuopnConstants.REGISTRATION_CODE);
				break;

			case QuopnConstants.OTP_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.OTP_VERIFICATION, QuopnConstants.OTP_CODE);
				break;

			case QuopnConstants.CATEGORY_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.CATEGORIES, QuopnConstants.CATEGORY_CODE, Request.Method.GET);
				break;

			case QuopnConstants.TERMS_AND_CONDITIONS:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.TERMS_AND_CONDITION_URL, QuopnConstants.TERMS_AND_CONDITIONS, Request.Method.GET);
				break;

			case QuopnConstants.PRIVACY_POLICY:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.PRIVACY_POLICY_URL, QuopnConstants.PRIVACY_POLICY, Request.Method.GET);
				break;

			case QuopnConstants.FAQ:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.FAQA_URL, QuopnConstants.FAQ, Request.Method.GET);
				break;

			case QuopnConstants.CAMPAIGN_DETAILS_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.CAMPAIGN_DETAILS_URL, QuopnConstants.CAMPAIGN_DETAILS_CODE, Request.Method.POST);
				break;

			case QuopnConstants.FOOTER_TAG_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.FOOTER_BAR, QuopnConstants.FOOTER_TAG_CODE);
				break;

			case QuopnConstants.RESEND_OTP_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.RESEND_OTP, QuopnConstants.RESEND_OTP_CODE);
				break;

			case QuopnConstants.PROFILE_UPDATE_CODE:
				connector = new ProfileUpdateRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.UCN_NUMBER_CODE:
				connector = new UCNNumberRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.PROFILE_GET_CODE:
//			connector = new RequestManager(mContext, mConnectionListener,QuopnConstants.PROFILE_GET_URL,QuopnConstants.PROFILE_GET_CODE);
				connector = new InterestsRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.GENERATE_PIN_CODE:
//			connector = new ChangePinRequest(mContext, mConnectionListener);
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.GENERATE_PIN, QuopnConstants.GENERATE_PIN_CODE);
				break;

			case QuopnConstants.STATE_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.STATE_LIST, QuopnConstants.STATE_LIST_CODE);
				break;

			case QuopnConstants.CITY_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.CITY_LIST, QuopnConstants.CITY_LIST_CODE);
				break;

			case QuopnConstants.INVITE_USER:
				connector = new InviteUserRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.HISTORY_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.MY_HISTORY, QuopnConstants.HISTORY_CODE);
				break;

			case QuopnConstants.CART_CODE:
				connector = new CartRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.REMOVE_FROM_CART_CODE:
				connector = new RemoveFromCartRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.CAMPAIGN_VALIDATION_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.CAMPAIGN_VALIDATION, QuopnConstants.CAMPAIGN_VALIDATION_CODE);
				break;

			case QuopnConstants.VIDEO_ISSUE_CODE:
				connector = new VideoIssueRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.WEB_ISSUE_CODE:
				connector = new WebIssueRequest(mContext, mConnectionListener);
				break;

			case QuopnConstants.INVITE_MOBIEL_NO_CHECKING:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.INVITE_MOBILE_NO_CHECKING, QuopnConstants.INVITE_MOBIEL_NO_CHECKING);
				break;

			case QuopnConstants.NOTIFY_STATUS_CODE:
//			connector = new NotifyStatusRequest(mContext, mConnectionListener);
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.NOTIFICATIONS_STATUS, QuopnConstants.NOTIFY_STATUS_CODE);
				break;

			case QuopnConstants.NOTIFY_STATUS_SENT:
				//connector = new NotifyIdSentRequest(mContext, mConnectionListener);
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.NOTIFICATIONS_SENT, QuopnConstants.NOTIFY_STATUS_SENT);
				break;

			case QuopnConstants.ANALYSIS_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.ANALYSIS, QuopnConstants.ANALYSIS_CODE);
				break;

			case QuopnConstants.SILENT_TUTORIAL_STATUS_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.TUTORIAL_STATUS, QuopnConstants.SILENT_TUTORIAL_STATUS_CODE);
				break;

			case QuopnConstants.MAP_API_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.MAP_URL, QuopnConstants.MAP_API_CODE);
				break;

			case QuopnConstants.FEEDBACK_API_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.FEEDBACK_URL, QuopnConstants.FEEDBACK_API_CODE);
				break;

			case QuopnConstants.IS_FIRSTINSTALL_TRACKER:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.FIRST_INSTALL_TRACKER, QuopnConstants.IS_FIRSTINSTALL_TRACKER);
				break;

			case QuopnConstants.STATE_CITY_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.STATE_CITY_LIST, QuopnConstants.STATE_CITY_LIST_CODE);
				break;

			case QuopnConstants.ERROR_REPORT_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.ERROR_REPORT, QuopnConstants.ERROR_REPORT_CODE);
				break;

			case QuopnConstants.LOGOUT_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.LOGOUT, QuopnConstants.LOGOUT_CODE);
				break;

			case QuopnConstants.PROMO_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.PROMOCODE, QuopnConstants.PROMO_CODE);
				break;

			case QuopnConstants.VERSION_CHECK_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.VERSION_CHECK_URL, QuopnConstants.VERSION_CHECK_CODE);
				break;
			case QuopnConstants.SHOP_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.STORE_LIST_URL, QuopnConstants.SHOP_LIST_CODE);
				break;
			case QuopnConstants.SEARCH_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SEARCH_LIST_URL, QuopnConstants.SEARCH_LIST_CODE);
				break;
			case QuopnConstants.REQUEST_PIN_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.REQUEST_PIN, QuopnConstants.REQUEST_PIN_CODE);
				break;
			case QuopnConstants.REFRESH_SESSION_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.REFRESH_SESSION, QuopnConstants.REFRESH_SESSION_CODE);
				break;
			case QuopnConstants.SHMART_CHECK_STATUS:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_CHECK_STATUS, QuopnConstants.SHMART_CHECK_STATUS);
				break;

			case QuopnConstants.SHMART_CREATE_USER:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_CREATE_USER, QuopnConstants.SHMART_CREATE_USER);
				break;

			case QuopnConstants.SHMART_GENERATE_OTP:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_GENERATE_OTP, QuopnConstants.SHMART_GENERATE_OTP);
				break;

			case QuopnConstants.SHMART_VERIFY_OTP:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_VERIFY_OTP, QuopnConstants.SHMART_VERIFY_OTP);
				break;
			case QuopnConstants.SHMART_VOUCHER_LIST_CODE:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_VOUCHER_LIST, QuopnConstants.SHMART_VOUCHER_LIST_CODE);
				break;

			case QuopnConstants.SHMART_REQUEST_OTP:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_REQUEST_OTP, QuopnConstants.SHMART_REQUEST_OTP);
				break;
			case QuopnConstants.SHMART_VERIFY_OTP_AND_CHNG_TRX_PWD:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.SHMART_VERIFY_AND_CHANGE_TRX_PWD, QuopnConstants.SHMART_VERIFY_OTP_AND_CHNG_TRX_PWD);
				break;
			case QuopnConstants.QUOPN_MOBILE_WALLET_FAQ:
				if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
					connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_MOBILE_WALLET_FAQ_URL_SHMART, QuopnConstants.QUOPN_MOBILE_WALLET_FAQ, Request.Method.GET);
				} else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
					connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_MOBILE_WALLET_FAQ_URL_CITRUS, QuopnConstants.QUOPN_MOBILE_WALLET_FAQ, Request.Method.GET);
				}
				break;
			case QuopnConstants.QUOPN_MOBILE_WALLET_TNC:
				if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
					connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_MOBILE_WALLET_TNC_URL_SHMART, QuopnConstants.QUOPN_MOBILE_WALLET_TNC, Request.Method.GET);
				} else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
					connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_MOBILE_WALLET_TNC_URL_CITRUS, QuopnConstants.QUOPN_MOBILE_WALLET_TNC, Request.Method.GET);
				}
				break;
			case QuopnConstants.QUOPN_MOBILE_CITRUS_LIST_WALLET:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_CITRUS_LIST_WALLET_CREDENTIAL, QuopnConstants.QUOPN_MOBILE_CITRUS_LIST_WALLET);
				break;
			case QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_CITRUS_LOGWALLETSTATS, QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
				break;
			case QuopnConstants.QUOPN_VALIDATE_TXN_PIN:
				connector = new RequestManager(mContext, mConnectionListener, QuopnApi.QUOPN_CITRUS_VALIDATE_TXN_PIN, QuopnConstants.QUOPN_VALIDATE_TXN_PIN);
				break;
			default:
				break;
		}

		/* E-Wallet APIs */
		QuopnApi.EWalletApi api = QuopnApi.EWalletApi.fromCode(type);
		if (api != null) {
			connector = new RequestManager(
					mContext, mConnectionListener, api.getEndPoint(), api.getApiCode());
		}
		
		if (connector != null) {
			if (mPostParams != null) connector.setPostParams(mPostParams);

			if (mHeaderParams != null) connector.setHeaderParams(mHeaderParams);

			connector.connect();
		}
		
		return connector;
	}

	public Connector createConnectionWithRESTParam(int apiCode, String mobileWalletId) {
		Connector connector = null;

		QuopnApi.EWalletApi api = QuopnApi.EWalletApi.fromCode(apiCode);
		if (api.equals(QuopnApi.EWalletApi.FEATURES)) {
			String url = api.getEndPoint();
			url += "/" + mobileWalletId;
			RequestManager manager = new RequestManager(mContext, mConnectionListener, url, apiCode);
			manager.setConnectionType(Request.Method.GET);
			connector = manager;
		}

		if (connector != null) {
			if (mPostParams != null) connector.setPostParams(mPostParams);

			if (mHeaderParams != null) connector.setHeaderParams(mHeaderParams);

			connector.connect();
		}

		return connector;
	}

	public void cancel(){
		connector.abort();
	}
}
