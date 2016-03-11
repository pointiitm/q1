package com.quopn.wallet.connection;

/**
 * @author Sumeet
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.data.model.AddToCartData;
import com.quopn.wallet.data.model.CampaignDetailsQuopnData;
import com.quopn.wallet.data.model.CampaignValidationData;
import com.quopn.wallet.data.model.CartData;
import com.quopn.wallet.data.model.CategoryQuopnData;
import com.quopn.wallet.data.model.CityListData;
import com.quopn.wallet.data.model.FooterTagData;
import com.quopn.wallet.data.model.GeneratePinData;
import com.quopn.wallet.data.model.HistoryData;
import com.quopn.wallet.data.model.InviteData;
import com.quopn.wallet.data.model.InviteMobileNoCheckingData;
import com.quopn.wallet.data.model.LogoutData;
import com.quopn.wallet.data.model.MapData;
import com.quopn.wallet.data.model.MyQuopnData;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.NewCategoryData;
import com.quopn.wallet.data.model.NotifyStatusData;
import com.quopn.wallet.data.model.OTPData;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.QuopnStoreListData;
import com.quopn.wallet.data.model.RefreshSessionData;
import com.quopn.wallet.data.model.RegisterData;
import com.quopn.wallet.data.model.RemoteOtp;
import com.quopn.wallet.data.model.RemoveFromCartData;
import com.quopn.wallet.data.model.RequestPinData;
import com.quopn.wallet.data.model.ResendOTPData;
import com.quopn.wallet.data.model.ShmartCheckStatusData;
import com.quopn.wallet.data.model.ShmartCreateUserData;
import com.quopn.wallet.data.model.ShmartGenerateOTPData;
import com.quopn.wallet.data.model.ShmartRequestOTPData;
import com.quopn.wallet.data.model.ShmartVerifyOTPAndChangeTransPwdData;
import com.quopn.wallet.data.model.ShmartVerifyOTPData;
import com.quopn.wallet.data.model.ShmartVoucherListData;
import com.quopn.wallet.data.model.StateCityData;
import com.quopn.wallet.data.model.StateListData;
import com.quopn.wallet.data.model.TAndCAndPrivPolData;
import com.quopn.wallet.data.model.UCNNumberData;
import com.quopn.wallet.data.model.VersionCheckData;
import com.quopn.wallet.data.model.VideoIssueData;
import com.quopn.wallet.data.model.WebIssueData;
import com.quopn.wallet.data.model.citrus.CitrusLogWalletStats;
import com.quopn.wallet.data.model.ValidateTxnPinResponse;
import com.quopn.wallet.data.model.citrus.CitrusWalletListData;
import com.quopn.wallet.data.model.shmart.AddBeneficiaryResponse;
import com.quopn.wallet.data.model.shmart.AnnouncementData;
import com.quopn.wallet.data.model.shmart.BalanceResponse;
import com.quopn.wallet.data.model.shmart.BeneficiariesResponse;
import com.quopn.wallet.data.model.shmart.ChangeTxnPwdResponse;
import com.quopn.wallet.data.model.shmart.DeleteBeneficiaryResponse;
import com.quopn.wallet.data.model.shmart.FeaturesResponse;
import com.quopn.wallet.data.model.shmart.GetPrefResponse;
import com.quopn.wallet.data.model.shmart.LoadWalletResponse;
import com.quopn.wallet.data.model.shmart.LocalOtp;
import com.quopn.wallet.data.model.shmart.TransactionsResponse;
import com.quopn.wallet.data.model.shmart.TransferToBankResponse;
import com.quopn.wallet.data.model.shmart.TransferToMobileResponse;
import com.quopn.wallet.data.model.shmart.UpdatePrefResponse;
import com.quopn.wallet.data.model.shmart.WalletStatusResponse;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Connector;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestManager extends ConnectRequest implements Connector, RequestTimer.RequestTimerListener {
    private static final String TAG = "Quopn/RequestManager";
    private static final int CONNECTION_TIME_OUT = 1000 * 5;
    private static final int N0OF_RETRY = 0;
    String mRequestUrl;
    String requestTag;
    private int mDataTag;
    private StringRequest postRequest;
    int connectionType = Request.Method.POST;
    private RequestTimer requestTimer;
    private OntimeOutListner listener;
    private Map<String, String> params, headerParams;

    //protected CustomProgressDialog mProgressDialog;
    public int getmDataTag() {
        return mDataTag;
    }

    public interface OntimeOutListner {

        public void onTimeout(RequestManager requestManager);

    }

    public RequestManager(Context mContext, ConnectionListener connectionListener, String requestUrl, int dataTag) {
        super();
        this.mConnectionListener = connectionListener;
        this.mContext = mContext;
        this.mRequestUrl = requestUrl;
        this.mDataTag = dataTag;
        requestTag = String.valueOf(dataTag);
        requestTimer = new RequestTimer();
        requestTimer.setListener(this);
        this.mHeaderParams = new HashMap<String, String>();
        this.mHeaderParams.put(QuopnApi.ParamKey.AUTHORIZATION, PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
        this.mHeaderParams.put(QuopnApi.ParamKey.x_session, PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID));
        if (mContext != null) {
            if (PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID) == null) {
                Log.e(TAG, "session null for API " + requestUrl);
            }
            if (PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY) == null) {
                Log.e(TAG, "API null for API " + requestUrl);
            }
        } else {
            Log.e(TAG, "mContext null for API " + requestUrl);
        }
    }

    public RequestManager(Context mContext, ConnectionListener connectionListener, String requestUrl, int dataTag, int connectionType) {
        this(mContext, connectionListener, requestUrl, dataTag);
        this.connectionType = connectionType;

    }

    public RequestManager(Context mContext, ConnectionListener connectionListener, String requestUrl, int dataTag, int connectionType, String requestTag) {
        this(mContext, connectionListener, requestUrl, dataTag, connectionType);
        this.requestTag = requestTag;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public void connect() {
        Log.d(TAG, "Request from " + mRequestUrl + " with tag " + requestTag);
        Log.d(TAG, "Headers : " + mHeaderParams);
        Log.d(TAG, "Post : " + mPostParams);

        RequestPool.getInstance(this.mContext).cancellAllPreviousRequestWithSameTag(requestTag);
        postRequest = new StringRequest(connectionType, mRequestUrl,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestTimer.cancel();
                        Log.d(TAG, response);
                        parseJson(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                requestTimer.cancel();
                if (error != null) {
                    if (!TextUtils.isEmpty(error.getMessage())) {
                        Log.d(TAG, "Response Error: " + error.getMessage());
                        QuopnUtils.QuopnLogWriteFile(TAG, error.getMessage(), true);
                    }
                    mConnectionListener.onResponse(ConnectionListener.CONNECTION_ERROR, new NetworkError(error));

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.d(TAG, "Response: " + new String(networkResponse.data));

                        if (networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {//Invalid session
                            LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(mContext);
                            Intent logoutInvalidSessionIntent = new Intent(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
                            localBrdcastMgr.sendBroadcast(logoutInvalidSessionIntent);
                            return;
                        }
                    } else {
//							(networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED)
                        //Invalid session
                        if (QuopnUtils.isUnauthorized(error)) {

                            Log.d(TAG, "Sending a broadcast to log out " + QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
                            LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(mContext);
                            Intent logoutInvalidSessionIntent = new Intent(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
                            localBrdcastMgr.sendBroadcast(logoutInvalidSessionIntent);
                            return;
                        } else if (QuopnUtils.isTimedOut(error)) {
                            // timeout code
                            Log.d(TAG, "Response:Network is unreachable");
//									Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//									if (dialog != null) {
//										dialog.show();
//									}
                            QuopnUtils.showDialogForContext(mContext, "Please check your internet connection and try again");
                            mConnectionListener.myTimeout(requestTag);
                            //onTimeoutDialog();

                        } else {
                            mConnectionListener.myTimeout(requestTag);
                        }
                    }
                } else {
                    Log.d(TAG, "Response Error: null");
                    QuopnUtils.showDialogForContext(mContext, "Please check your internet connection and try again");
//							Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//							if (dialog != null) {
//								dialog.show();
//							}
                    mConnectionListener.myTimeout(requestTag);
                }
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                if (mPostParams == null) {
                    return null;
                } else {
                    return mPostParams;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaderParams == null) {
                    return Collections.emptyMap();
                } else {
                    return mHeaderParams;
                }

            }

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(QuopnConstants.CONNECTION_TIME_OUT, N0OF_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(postRequest);
    }

    public void setmDataTag(int dataTag) {
        dataTag = this.mDataTag;
    }

    @Override
    public void parseJson(String data) {
        Log.d(TAG, "Data available from server for request code " + mDataTag);

        try {
            Gson gson = new Gson();
            switch (this.mDataTag) {

                case QuopnConstants.REGISTRATION_CODE:
                    ProfileData tRegistrationResponse = new ProfileData();
                    tRegistrationResponse = (ProfileData) gson.fromJson(data, ProfileData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tRegistrationResponse);
                    break;

                case QuopnConstants.OTP_CODE:
                    OTPData tOTPData = new OTPData();
                    tOTPData = (OTPData) gson.fromJson(data, OTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tOTPData);
                    break;

                case QuopnConstants.CATEGORY_CODE:
                    NewCategoryData newCategoryData = new NewCategoryData();
                    newCategoryData = (NewCategoryData) gson.fromJson(data, NewCategoryData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, newCategoryData);
                    break;

                case QuopnConstants.NEW_CAMPAIGNLSTING_CODE:
                    Log.d(TAG, "Quopn data available");
                    CategoryQuopnData tCategoryQuopnData = new CategoryQuopnData();
                    tCategoryQuopnData = (CategoryQuopnData) gson.fromJson(data, CategoryQuopnData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tCategoryQuopnData);
                    break;

                case QuopnConstants.FOOTER_TAG_CODE:
                    FooterTagData tFooterTagData = new FooterTagData();
                    tFooterTagData = (FooterTagData) gson.fromJson(data, FooterTagData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tFooterTagData);
                    break;

                case QuopnConstants.RESEND_OTP_CODE:
                    ResendOTPData tResendOTPData = new ResendOTPData();
                    tResendOTPData = (ResendOTPData) gson.fromJson(data, ResendOTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tResendOTPData);
                    break;

                case QuopnConstants.PROFILE_UPDATE_CODE:
                    RegisterData tRegisterData = new RegisterData();
                    tRegisterData = (RegisterData) gson.fromJson(data, RegisterData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tRegisterData);
                    break;

                case QuopnConstants.UCN_NUMBER_CODE:
                    UCNNumberData tUCNNumberData = new UCNNumberData();
                    tUCNNumberData = (UCNNumberData) gson.fromJson(data, UCNNumberData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tUCNNumberData);
                    break;

                case QuopnConstants.PROFILE_GET_CODE:
                    ProfileData tInterestsData = new ProfileData();
                    tInterestsData = (ProfileData) gson.fromJson(data, ProfileData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tInterestsData);
                    break;

                case QuopnConstants.INVITE_USER_CODE:
                    OTPData tOTPData1 = new OTPData();
                    tOTPData1 = (OTPData) gson.fromJson(data, OTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tOTPData1);
                    break;

                case QuopnConstants.MYQUOPN_CODE:
                    MyQuopnData tMyQuopnData = new MyQuopnData();
                    tMyQuopnData = (MyQuopnData) gson.fromJson(data, MyQuopnData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tMyQuopnData);
                    break;

                case QuopnConstants.GENERATE_PIN_CODE:
                    GeneratePinData tGeneratePinData = new GeneratePinData();
                    tGeneratePinData = (GeneratePinData) gson.fromJson(data, GeneratePinData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tGeneratePinData);
                    break;

                case QuopnConstants.STATE_LIST_CODE:
                    StateListData tStateListData = new StateListData();
                    tStateListData = (StateListData) gson.fromJson(data, StateListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tStateListData);
                    break;

                case QuopnConstants.CITY_LIST_CODE:
                    CityListData tCityListData = new CityListData();
                    tCityListData = (CityListData) gson.fromJson(data, CityListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tCityListData);
                    break;

                case QuopnConstants.INVITE_USER:
                    InviteData tInviteData = new InviteData();
                    tInviteData = (InviteData) gson.fromJson(data, InviteData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tInviteData);
                    break;

                case QuopnConstants.HISTORY_CODE:
                    HistoryData tHistoryData = new HistoryData();
                    tHistoryData = (HistoryData) gson.fromJson(data, HistoryData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tHistoryData);
                    break;

                case QuopnConstants.CART_CODE:
                    CartData tCartData = new CartData();
                    tCartData = (CartData) gson.fromJson(data, CartData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tCartData);
                    break;

                case QuopnConstants.ADD_TO_CART_CODE:
                    AddToCartData tAddToCartData = new AddToCartData();
                    tAddToCartData = (AddToCartData) gson.fromJson(data, AddToCartData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tAddToCartData);
                    break;

                case QuopnConstants.REMOVE_FROM_CART_CODE:
                    RemoveFromCartData tRemoveFromCartData = new RemoveFromCartData();
                    tRemoveFromCartData = (RemoveFromCartData) gson.fromJson(data, RemoveFromCartData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tRemoveFromCartData);
                    break;

                case QuopnConstants.CAMPAIGN_VALIDATION_CODE:
                    CampaignValidationData tCampaignValidationData = new CampaignValidationData();
                    tCampaignValidationData = (CampaignValidationData) gson.fromJson(data, CampaignValidationData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tCampaignValidationData);
                    break;

                case QuopnConstants.VIDEO_ISSUE_CODE:
                    VideoIssueData tVideoIssueData = new VideoIssueData();
                    tVideoIssueData = (VideoIssueData) gson.fromJson(data, VideoIssueData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tVideoIssueData);
                    break;

                case QuopnConstants.WEB_ISSUE_CODE:
                    WebIssueData tWebIssueData = new WebIssueData();
                    tWebIssueData = (WebIssueData) gson.fromJson(data, WebIssueData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tWebIssueData);
                    break;

                case QuopnConstants.INVITE_MOBIEL_NO_CHECKING:
                    InviteMobileNoCheckingData tInviteMobileNoCheckingData = new InviteMobileNoCheckingData();
                    tInviteMobileNoCheckingData = (InviteMobileNoCheckingData) gson.fromJson(data, InviteMobileNoCheckingData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tInviteMobileNoCheckingData);
                    break;

                case QuopnConstants.NOTIFY_STATUS_CODE:
                    NotifyStatusData tNotifyStatusData = new NotifyStatusData();
                    tNotifyStatusData = (NotifyStatusData) gson.fromJson(data, NotifyStatusData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tNotifyStatusData);
                    break;

                case QuopnConstants.NOTIFY_STATUS_SENT:
                    NotifyStatusData tNotifyStatusData1 = new NotifyStatusData();
                    tNotifyStatusData1 = (NotifyStatusData) gson.fromJson(data, NotifyStatusData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tNotifyStatusData1);
                    break;

                case QuopnConstants.ANALYSIS_CODE:
                    AddToCartData tAddToCartData1 = new AddToCartData();
                    tAddToCartData1 = (AddToCartData) gson.fromJson(data, AddToCartData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tAddToCartData1);
                    break;

                case QuopnConstants.MAP_API_CODE:
                    MapData mapData = new MapData();
                    mapData = (MapData) gson.fromJson(data, MapData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, mapData);
                    break;

                case QuopnConstants.FEEDBACK_API_CODE:
                    RegisterData reg_data = new RegisterData();
                    reg_data = (RegisterData) gson.fromJson(data, RegisterData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, reg_data);
                    break;

                case QuopnConstants.IS_FIRSTINSTALL_TRACKER:
                    AddToCartData firstInstallTrackerResponse = new AddToCartData();
                    firstInstallTrackerResponse = (AddToCartData) gson.fromJson(data, AddToCartData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, firstInstallTrackerResponse);
                    break;

                case QuopnConstants.STATE_CITY_LIST_CODE:
                    StateCityData stateCityData = (StateCityData) gson.fromJson(data, StateCityData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, stateCityData);
                    break;
                case QuopnConstants.LOGOUT_CODE:
                    LogoutData logoutData = (LogoutData) gson.fromJson(data, LogoutData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, logoutData);
                    break;
                case QuopnConstants.PROMO_CODE:
                    GeneratePinData promoResponse = new GeneratePinData();
                    promoResponse = (GeneratePinData) gson.fromJson(data, GeneratePinData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, promoResponse);
                    break;

                case QuopnConstants.VERSION_CHECK_CODE:
                    VersionCheckData versionCheckData = (VersionCheckData) gson.fromJson(data, VersionCheckData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, versionCheckData);
                    break;
                case QuopnConstants.SHOP_LIST_CODE:
                    Log.d(TAG, "Data available from server for request code bitmap:" + data + "END");
                    QuopnStoreListData StoreDataListResponse = new QuopnStoreListData();
                    StoreDataListResponse = (QuopnStoreListData) gson.fromJson(data, QuopnStoreListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, StoreDataListResponse);
                    break;
                case QuopnConstants.SEARCH_LIST_CODE:
                    QuopnStoreListData StoreDataListResponseSearch = new QuopnStoreListData();
                    StoreDataListResponseSearch = (QuopnStoreListData) gson.fromJson(data, QuopnStoreListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, StoreDataListResponseSearch);
                case QuopnConstants.TERMS_AND_CONDITIONS:
                    TAndCAndPrivPolData termsAndCond = new TAndCAndPrivPolData();
                    termsAndCond = (TAndCAndPrivPolData) gson.fromJson(data, TAndCAndPrivPolData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, termsAndCond);
                    break;
                case QuopnConstants.PRIVACY_POLICY:
                    TAndCAndPrivPolData policyData = new TAndCAndPrivPolData();
                    policyData = (TAndCAndPrivPolData) gson.fromJson(data, TAndCAndPrivPolData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, policyData);
                    break;
                case QuopnConstants.FAQ:
                    TAndCAndPrivPolData faqData = new TAndCAndPrivPolData();
                    faqData = (TAndCAndPrivPolData) gson.fromJson(data, TAndCAndPrivPolData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, faqData);
                    break;
                case QuopnConstants.CAMPAIGN_DETAILS_CODE:
                    CampaignDetailsQuopnData campaigndetailData = new CampaignDetailsQuopnData();
                    campaigndetailData = (CampaignDetailsQuopnData) gson.fromJson(data, CampaignDetailsQuopnData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, campaigndetailData);
                    break;
                case QuopnConstants.REQUEST_PIN_CODE:
                    RequestPinData requestPinDataData = new RequestPinData();
                    requestPinDataData = (RequestPinData) gson.fromJson(data, RequestPinData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, requestPinDataData);
                    break;
                case QuopnConstants.REFRESH_SESSION_CODE:
                    RefreshSessionData refreshSessionData = new RefreshSessionData();
                    refreshSessionData = (RefreshSessionData) gson.fromJson(data, RefreshSessionData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, refreshSessionData);
                    break;
                case QuopnConstants.SHMART_CHECK_STATUS:
                    ShmartCheckStatusData shmartCheckStatus = new ShmartCheckStatusData();
                    shmartCheckStatus = (ShmartCheckStatusData) gson.fromJson(data, ShmartCheckStatusData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartCheckStatus);
                    break;
                case QuopnConstants.SHMART_CREATE_USER:
                    ShmartCreateUserData shmartCreateUserData = new ShmartCreateUserData();
                    shmartCreateUserData = (ShmartCreateUserData) gson.fromJson(data, ShmartCreateUserData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartCreateUserData);
                    break;
                case QuopnConstants.SHMART_GENERATE_OTP:
                    ShmartGenerateOTPData shmartGenOtpData = new ShmartGenerateOTPData();
                    shmartGenOtpData = (ShmartGenerateOTPData) gson.fromJson(data, ShmartGenerateOTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartGenOtpData);
                    break;
                case QuopnConstants.SHMART_VERIFY_OTP:
                    ShmartVerifyOTPData shmartVerifyOtpData = new ShmartVerifyOTPData();
                    shmartVerifyOtpData = (ShmartVerifyOTPData) gson.fromJson(data, ShmartVerifyOTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartVerifyOtpData);
                    break;

                case QuopnConstants.SHMART_VOUCHER_LIST_CODE:
                    ShmartVoucherListData shmartVoucherListData = new ShmartVoucherListData();
                    shmartVoucherListData = (ShmartVoucherListData) gson.fromJson(data, ShmartVoucherListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartVoucherListData);
                    break;

                case QuopnConstants.SHMART_REQUEST_OTP:
                    ShmartRequestOTPData shmartRequestOtpData = new ShmartRequestOTPData();
                    shmartRequestOtpData = (ShmartRequestOTPData) gson.fromJson(data, ShmartRequestOTPData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartRequestOtpData);
                    break;
                case QuopnConstants.SHMART_VERIFY_OTP_AND_CHNG_TRX_PWD:
                    ShmartVerifyOTPAndChangeTransPwdData shmartVerifOtpAndChgTrxPwdData = new ShmartVerifyOTPAndChangeTransPwdData();
                    shmartVerifOtpAndChgTrxPwdData = (ShmartVerifyOTPAndChangeTransPwdData) gson.fromJson(data, ShmartVerifyOTPAndChangeTransPwdData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, shmartVerifOtpAndChgTrxPwdData);
                    break;
                case QuopnConstants.QUOPN_MOBILE_WALLET_FAQ:
                    TAndCAndPrivPolData faqWalletData = new TAndCAndPrivPolData();
                    faqWalletData = (TAndCAndPrivPolData) gson.fromJson(data, TAndCAndPrivPolData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, faqWalletData);
                    break;
                case QuopnConstants.QUOPN_MOBILE_WALLET_TNC:
                    TAndCAndPrivPolData tncWalletData = new TAndCAndPrivPolData();
                    tncWalletData = (TAndCAndPrivPolData) gson.fromJson(data, TAndCAndPrivPolData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, tncWalletData);
                    break;
                case QuopnConstants.QUOPN_MOBILE_WALLET_ANNOUNCEMENT:
                    AnnouncementData announceMentData = new AnnouncementData();
                    announceMentData = (AnnouncementData) gson.fromJson(data, AnnouncementData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, announceMentData);
                    break;
                case QuopnConstants.QUOPN_MOBILE_CITRUS_LIST_WALLET:
                    CitrusWalletListData citrusWalletListData = new CitrusWalletListData();
                    citrusWalletListData = (CitrusWalletListData) gson.fromJson(data, CitrusWalletListData.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, citrusWalletListData);
                    break;
                case QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS:
                    CitrusLogWalletStats citruslogwalletstats = new CitrusLogWalletStats();
                    citruslogwalletstats = (CitrusLogWalletStats) gson.fromJson(data, CitrusLogWalletStats.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, citruslogwalletstats);
                    break;
                case QuopnConstants.QUOPN_VALIDATE_TXN_PIN:
                    ValidateTxnPinResponse validateTxnPinResponse = new ValidateTxnPinResponse();
                    validateTxnPinResponse = (ValidateTxnPinResponse) gson.fromJson(data, ValidateTxnPinResponse.class);
                    this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, validateTxnPinResponse);
                    break;
            }

			/* E-Wallet responses */
            QuopnApi.EWalletApi api = QuopnApi.EWalletApi.fromCode(this.mDataTag);
            if (api != null) {
                Response response = null;

                Log.d(TAG, data);
                if (api.getApiCode() == QuopnApi.EWalletApi.BALANCE.getApiCode()) {
                    response = new BalanceResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.BENEFICIARIES.getApiCode()) {
                    response = new BeneficiariesResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode()) {
                    response = new AddBeneficiaryResponse(data);
                } else if (api.getApiCode()
                        == QuopnApi.EWalletApi.TRANSFER_TO_BENEFICIARY.getApiCode()) {

                    response = new TransferToBankResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.LOAD.getApiCode()) {
                    response = new LoadWalletResponse(data);
                } else if (api.getApiCode()
                        == QuopnApi.EWalletApi.TRANSFER_TO_MOBILE.getApiCode()) {

                    response = new TransferToMobileResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode()) {
                    response = new ChangeTxnPwdResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.GET_PREF.getApiCode()) {
                    response = new GetPrefResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.SET_PREF.getApiCode()) {
                    response = new UpdatePrefResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.FEATURES.getApiCode()) {
//                    if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
//                        data = "{\"status\":\"success\",\"FeatureList\":[{\"id\":1,\"mobile_wallet_id\":1,\"feature\":\"LOAD Wallet\",\"status\":1,\"status_text\":null},{\"id\":2,\"mobile_wallet_id\":1,\"feature\":\"Send Money\",\"status\":1,\"status_text\":null},{\"id\":3,\"mobile_wallet_id\":1,\"feature\":\"Transfer TO Bank\",\"status\":1,\"status_text\":null},{\"id\":4,\"mobile_wallet_id\":1,\"feature\":\"Purchase Vouchers\",\"status\":1,\"status_text\":null},{\"id\":5,\"mobile_wallet_id\":1,\"feature\":\"SHOP AT STORES\",\"status\":1,\"status_text\":\"\"},{\"id\":6,\"mobile_wallet_id\":1,\"feature\":\"My Transactions\",\"status\":1,\"status_text\":null},{\"id\":7,\"mobile_wallet_id\":1,\"feature\":\"Settings\",\"status\":1,\"status_text\":null},{\"id\":8,\"mobile_wallet_id\":1,\"feature\":\"Tnc\",\"status\":1,\"status_text\":null},{\"id\":9,\"mobile_wallet_id\":1,\"feature\":\"Faqs\",\"status\":1,\"status_text\":null}]}";
//                    }
                    response = new FeaturesResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.TXNS.getApiCode()) {
                    response = new TransactionsResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.DELETE_BENEFICIARY.getApiCode()) {
                    response = new DeleteBeneficiaryResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.CHECK_STATUS.getApiCode()) {
                    response = new WalletStatusResponse(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.OTP.getApiCode()) {
                    response = new RemoteOtp(data);
                } else if (api.getApiCode() == QuopnApi.EWalletApi.LOCAL_OTP.getApiCode()) {
                    response = new LocalOtp(data);
                }

                mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, response);
            }


        } catch (JsonSyntaxException eJsonSyntaxException) {
            QuopnUtils.QuopnLogWriteFile(TAG, data, true);
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null);
        } catch (JSONException e) {
            e.printStackTrace();
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null);
        } catch (Exception eException) {
            QuopnUtils.QuopnLogWriteFile(TAG, data, true);
        }

    }

    @Override
    public void setPostParams(Map<String, String> postParams) {
        this.mPostParams = postParams;

    }

    @Override
    public void setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams.putAll(headerParams);

    }

    public OntimeOutListner getListener() {
        return listener;
    }

    public void setListener(OntimeOutListner listener) {
        this.listener = listener;
    }

    @Override
    public void abort() {
        postRequest.cancel();
    }

    @Override
    public void onTimeout(RequestTimer requestTimer) {
        if (listener != null) {
            listener.onTimeout(this);
        }
        if (mContext instanceof Activity) { //sandeep added 21072015
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
//						Dialog dialog=new Dialog(mContext, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
//						dialog.show();
                    }

                }
            });
        }
        RequestPool.getInstance(mContext)
                .cancellAllPreviousRequestWithSameTag(requestTag);
        mConnectionListener.onTimeout(this);
    }

    public void onTimeoutDialog() {

        if (mContext instanceof Activity) { //sandeep added 21072015
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
                dialog.show();
            }
        }
    }

}
