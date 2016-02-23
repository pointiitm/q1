package com.quopn.wallet.shmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.response.CitrusError;
import com.gc.materialdesign.widgets.Dialog;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.citrus.CitrusRegn;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.connection.RequestManager;
import com.quopn.wallet.data.model.RemoteOtp;
import com.quopn.wallet.data.model.shmart.FeaturesResponse;
import com.quopn.wallet.data.model.shmart.LoadWalletStatusResponse;
import com.quopn.wallet.data.model.shmart.LocalOtp;
import com.quopn.wallet.data.model.shmart.TransactionsResponse;
import com.quopn.wallet.data.model.shmart.UpdatePrefResponse;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.walletshmart.ShmartOtp;
import com.quopn.wallet.walletshmart.ShmartRegn;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hari on 21/9/15.
 * This class is a super controller which controls all the features of
 * Cashback wallet after a user is verified as registered and active.
 */
public class ShmartFlow implements RequestManager.OntimeOutListner,ConnectionListener {
    @Override
    public void onResponse(int responseResult, Response response) {

    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    @Override
    public void myTimeout(String requestTag) {
        System.out.println("=====ShmartFlow=====" + requestTag);
    }

    public interface OtpListener {
        public void onLocalOtpRequest();

        public void onRemoteOtpRequest();
    }

    private static final String TAG = "Quopn/ShmartFlow";
    RequestManager requestManager;
    //public int mDataTag;


    /**
     * onTimeout
     *
     * @param requestManager This is called by the network operation started by requestManager
     *                       whenever a request takes more than a minute
     */
    @Override
    public void onTimeout(RequestManager requestManager) {
        Log.d(TAG, "Timeout from tag " + requestManager.getmDataTag());
        QuopnApi.EWalletApi api = QuopnApi.EWalletApi.fromCode(requestManager.getmDataTag());
        if (api.getApiCode() == QuopnApi.EWalletApi.LOAD.getApiCode()) {
            loadWalletActivity.hideCustomProgress();
            loadWalletActivity.showMessage(false, "Timeout while loading loadwallet");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.TXNS.getApiCode()) {
            transactionsActivity.stopProgress();
            transactionsActivity.showMessage(false, "Timeout while loading transactions");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.BALANCE.getApiCode()) {
            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(false, "Timeout while loading balance");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.BENEFICIARIES.getApiCode()) {
            bankActivity.stopProgress();
            bankActivity.showMessage(false, "Timeout while loading beneficiaries");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode()) {
            bankAccountActivity.stopProgress();
            bankAccountActivity.showMessage(false, "Timeout while loading add beneficiary");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.TRANSFER_TO_BENEFICIARY.getApiCode()) {
            bankActivity.stopProgress();
            bankActivity.showMessage(false, "Timeout while loading transfer to beneficiary");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.TRANSFER_TO_MOBILE.getApiCode()) {
            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(false, "Timeout while loading tranfer to mobile");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode()) {
            System.out.println("======ChangeTxnPwd=======");
            settingsActivity.stopProgress();
            settingsActivity.showMessage(false, "Timeout while setting transaction PIN");
        } else if (api.getApiCode() == QuopnApi.EWalletApi.DELETE_BENEFICIARY.getApiCode()) {
            bankActivity.showProgress();
            bankActivity.showMessage(false, "Timeout while delete benificiary");
        }
    }

    /**
     * This enum contains values which refer to how to use
     * the Wallet balance during future shopping transactions.
     */
    public enum EWalletUsagePref {
        ALWAYS("A"), NEXT_TIME("N"), NEVER("");

        private String pref;

        EWalletUsagePref(String pref) {
            this.pref = pref;
        }

        public String getPref() {
            return pref;
        }

        public static EWalletUsagePref fromPref(String setting) {
            for (EWalletUsagePref pref : EWalletUsagePref.values()) {
                if (pref.getPref().equals(setting)) {
                    return pref;
                }
            }

            return NEVER;
        }
    }

    /**
     * Dummy data values were used before the Shmart API
     * was integrated. These values were used for testing various
     * conditions to see if our wallet screens work correctly
     */
    enum Dummy {
        CUSTOMER_ID("5420");

        private String data;

        Dummy(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    /**
     * PrefKey enum contains key names that are
     * used in shared preferences related to Cashback Wallet
     */
    public enum PrefKey {
        CUSTOMER_ID("customerID");

        private String name;

        PrefKey(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * The singleton instance of this controller
     */
    private static ShmartFlow instance;

    /**
     * All the member variables
     */
    private Context context;
    //    private double balance;
    private double balanceCitrus;
    private double balanceShmart;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ShmartResponseListener shmartResponseListener;
    private ShmartHomeActivity activity;
    private LoadWalletActivity loadWalletActivity;
    private SendMoneyActivity sendMoneyActivity;
    private BankActivity bankActivity;
    private BankAccountActivity bankAccountActivity;
    private IFrameActivity iFrameActivity;
    private SettingsActivity settingsActivity;
    private UseQuopnActivity useQuopnActivity;
    private TransactionsActivity transactionsActivity;
    private DeepLinkActivity deepLinkActivity;
    private ShmartOtp shmartOtpActivity;
    private String customerID;
    private String beneficiaryCode_shmart;
    private String beneficiaryCode_citrus;
    private String loadWalletHtml;
    private String loadWalletUrl;
    private double sendMoneyAmount;
    private AnalysisManager mAnalysisManager;
    private OtpListener otpListener;
    private List<FeaturesResponse.Feature> shmartFeatures;
    private List<FeaturesResponse.Feature> citrusFeatures;
    private CitrusClient mCitrusClient = null;
    private MainActivity mMainActivity;
    private CitrusRegn citrusRegn;

    private ShmartFlow() {
        shmartResponseListener = new ShmartResponseListener(this);
        mCitrusClient = CitrusClient.getInstance(QuopnApplication.getInstance().getApplicationContext());
    }

    public List<FeaturesResponse.Feature> getCitrusFeatures() {
        return citrusFeatures;
    }

    public void setCitrusFeatures(List<FeaturesResponse.Feature> citrusFeatures) {
        this.citrusFeatures = citrusFeatures;
    }

    public List<FeaturesResponse.Feature> getShmartFeatures() {
        return shmartFeatures;
    }

    public void setShmartFeatures(List<FeaturesResponse.Feature> shmartFeatures) {
        this.shmartFeatures = shmartFeatures;
    }

//    public List<FeaturesResponse.Feature> getFeatureList() {
//        return this.features;
//    }

    public static ShmartFlow getInstance() {
        if (instance == null) {
            instance = new ShmartFlow();
        }
        return instance;
    }

    /**
     * setContext
     *
     * @param context Sets the context off which this flow should start working.
     *                As soon as the context is set, the shared preferences are initialised.
     */
    public void setContext(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    public void setOtpListener(OtpListener otpListener) {
        this.otpListener = otpListener;
    }

    /**
     * setBalance
     *
     * @param balance Sets the current balance of the cashback wallet.
     *                This method is called after the latest balance value is received from the server.
     *                This method will also update the balance display of the cashback wallet home screen
     *                and the Send Money screen, if any of the two happens to be open.
     */
    public void setBalance(double balance) {
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.balanceCitrus = balance;
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.balanceShmart = balance;
        }
        if (activity != null) {
            activity.showBalance(balance);
        }
        if (sendMoneyActivity != null) {
            sendMoneyActivity.showBalance(balance);
        }
    }

    public ShmartHomeActivity getActivity() {
        return activity;
    }

    public String getBeneficiaryCode_citrus() {
        return beneficiaryCode_citrus;
    }

    /**
     * Entry point for this flow. This method is called by the class that uses this class
     * for the first time, which is typically Quopn's main screen.
     */
    public void start() {
        /* Fetch data from server to get the customerID corresponding to the mobile number */
        /* If customer id is not found, then go through the registration flow */

        /* Use dummy */
        this.customerID = Dummy.CUSTOMER_ID.getData();

        /* If customer id is found, then start the main operations flow */
        startOperations();

        /* Fetch the balance via API */
//        fetchBalance();
    }

    /**
     * Kick starts the flow.
     */
    private void startOperations() {
        /* Get the balance from the server */
        fetchBalance();

        /* Get the enabled features */
        getFeatures();

        // vishal
//        /* Kickstart the home screen */
//        Intent shmartHomeIntent = new Intent(context, ShmartHomeActivity.class);
//        context.startActivity(shmartHomeIntent);
    }

    /**
     * Starts the cashback wallet home activity. This screen presents the list of options
     * on what can be done with the cashback wallet.
     */
    private void startHomeActivity() {
        // Kill others if they exist
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            try {
                if (this.shmartOtpActivity != null) {
                    this.shmartOtpActivity.finishAll();
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            try {
                if (this.citrusRegn != null) {
                    this.citrusRegn.finishAll();
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }
        /* Kickstart the home screen */
        Intent shmartHomeIntent = new Intent(context, ShmartHomeActivity.class);
        context.startActivity(shmartHomeIntent);
    }

    /**
     * Get the list of supported features from the server.
     * These features will be used as the options shown to the user.
     * If the features have already been downloaded and cached, then do not
     * download again, just start the home activity directly.
     */
    private void getFeatures() {
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            if (this.shmartFeatures == null) {
                ConnectionFactory factory = new ConnectionFactory(context, shmartResponseListener);
                factory.createConnectionWithRESTParam(QuopnApi.EWalletApi.FEATURES.getApiCode()
                        , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            } else {
                startHomeActivity();
            }
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            if (this.citrusFeatures == null) {
                ConnectionFactory factory = new ConnectionFactory(context, shmartResponseListener);
                factory.createConnectionWithRESTParam(QuopnApi.EWalletApi.FEATURES.getApiCode()
                        , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            } else {
                startHomeActivity();
            }
        }
    }

    /**
     * Bring the latest cashback wallet balance from the server
     */
    public void fetchBalance() {
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            //activity.showCustomProgress();
            ConnectionFactory factory = new ConnectionFactory(context, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(activity).getPreference(
                    PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);

            factory.setPostParams(params);
//        factory.createConnection(QuopnApi.EWalletApi.BALANCE.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.BALANCE.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            //activity.showCustomProgress();
            getBalanceCitrus();
        }
    }

    /**
     * Home page has been created. Please show the balance to the user.
     */

    private void getBalanceCitrus() {
//        Logger.d("");
        if (mCitrusClient == null) {
            //QuopnConstants.showToast(context, "mCitrusClient null");
        }
        mCitrusClient.getBalance(new Callback<Amount>() {
            @Override
            public void success(Amount amount) {
//                Logger.d("");
                //activity.hideCustomProgress();
                //QuopnConstants.showToast(context, "Balance : " + amount.getValue());
                ShmartFlow.getInstance().setBalance(amount.getValueAsDouble());
                if (activity != null) {
                    activity.hideCustomProgress();
                }
            }

            @Override
            public void error(CitrusError error) {
                //QuopnConstants.showToast(context, error.getMessage());
            }
        });
    }

    public void onShmartHomeCreated(ShmartHomeActivity activity) {
        this.activity = activity;
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            activity.showBalance(balanceShmart);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
            activity.showBalance(balanceCitrus);
        }
    }

    public void onShmartHomeDestroyed() {
    }

    public void onShmartHomeRestored(ShmartHomeActivity activity) {
        this.activity = activity;
    }

    /**
     * onShmartOpChosen
     *
     * @param shmartOpID This method is called after the user chooses an option tile from the main screen
     *                   Based on whatever is chosen, the appropriated sub-flow is kickstarted.
     */
    public void onShmartOpChosen(int shmartOpID) {
        switch (shmartOpID) {
            case R.id.shmart_op_loadwallet:
                startLoadWallet();
                break;
            case R.id.shmart_op_terms:
                //startLoadTnC();
                Intent intenttnc = new Intent(activity, Shmart_Tnc_Activity.class);
                activity.startActivity(intenttnc);
                break;
            case R.id.shmart_op_transfertobank:
                if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
                    startBankTransfer(balanceShmart);
                } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
                    startBankTransfer(balanceCitrus);
                }
//                startBankTransfer(balance);
                break;
            case R.id.shmart_op_sendmoney:
                if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
                    startSendMoney(balanceShmart);
                } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
                    startSendMoney(balanceCitrus);
                }
//                startSendMoney(balance);
                break;
            case R.id.shmart_op_settings:
                startSettingsFlow();
                break;
            case R.id.shmart_op_mytransactions:
                startTransactions();
                break;
            case R.id.shmart_op_faqs:
                //activity.showComingSoon();
                Intent intent = new Intent(activity, Shmart_Faq_Activity.class);
                activity.startActivity(intent);
                break;
            case R.id.shmart_op_shopatquopn:
                //activity.showComingSoon();
                startShopAtQuopn();
                break;
        }
    }

    /**
     * onFeaturesReceived
     *
     * @param features Server has sent the list of features. We now start the home screen.
     */
    public void onFeaturesReceived(List<FeaturesResponse.Feature> features) {
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.setCitrusFeatures(features);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.setShmartFeatures(features);
        }
        startHomeActivity();
//        activity.showFeatures(features);
    }

    /**
     * Load Wallet section
     */
    private void startLoadWallet() {
        Intent loadWalletIntent = new Intent(activity, LoadWalletActivity.class);
        activity.startActivity(loadWalletIntent);

    }

    /**
     * Load wallet screen is created. Show the latest balance on it.
     *
     * @param loadWalletActivity
     */
    public void onLoadWalletActivityCreated(LoadWalletActivity loadWalletActivity) {
        this.loadWalletActivity = loadWalletActivity;
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.loadWalletActivity.setBalance(this.balanceCitrus);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.loadWalletActivity.setBalance(this.balanceShmart);
        }
//        this.loadWalletActivity.setBalance(this.balance);
    }

    public void onLoadWalletActivityDestroyed() {

    }

    /**
     * Load wallet screen was re-opened after wallet loading operation was completed.
     *
     * @param activity
     */
    public void onLoadWalletActivityRestored(LoadWalletActivity activity) {
        this.loadWalletActivity = activity;
    }

    /**
     * After the user clicks to load money after having filled the amount
     * this method is called.
     *
     * @param amount: the amount that the user wants to transfer
     */
    public void startLoadingFlow(double amount) {
        loadWalletActivity.showCustomProgress();

        sendMoneyAmount = amount;

        ConnectionFactory factory
                = new ConnectionFactory(loadWalletActivity, shmartResponseListener);
        Map<String, String> map = new TreeMap<String, String>();
        map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                , PreferenceUtil.getInstance(sendMoneyActivity).getPreference(
                PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
        map.put(QuopnApi.EWalletRequestParam.AMOUNT.getName(), String.valueOf(amount));
        map.put(QuopnApi.EWalletRequestParam.PLATFORM.getName()
                , QuopnApi.EWalletDefault.PLATFORM);
        factory.setPostParams(map);
        factory.createConnection(QuopnApi.EWalletApi.LOAD.getApiCode());
    }

    /**
     * Once server sends the HTML code for the payment facilitator page (Shmart page),
     * this method is invoked.
     * This method then opens a screen with a webview to show the HTML content.
     *
     * @param html: The HTML code to show in the webview
     */
    public void onLoadWalletHtmlAvailable(String html) {
        loadWalletActivity.hideCustomProgress();

        this.loadWalletHtml = html;
        Intent intent = new Intent(loadWalletActivity, IFrameActivity.class);
        loadWalletActivity.startActivity(intent);
    }


    /**
     * Once server sends the URL for the payment facilitator page (Shmart page),
     * this method is invoked.
     * This method then opens a screen with a webview to show the URL.
     *
     * @param url: The URL to show in the webview
     */
    public void onLoadWalletUrlAvailable(String url) {
        loadWalletActivity.hideCustomProgress();

        this.loadWalletUrl = url;
        Intent intent = new Intent(loadWalletActivity, IFrameActivity.class);
        loadWalletActivity.startActivity(intent);
    }

    /**
     * The screen with the webview is created.
     *
     * @param iFrameActivity: Reference to the webview screen that was created.
     *                        The screen that was created.
     */
    public void onIFrameCreated(IFrameActivity iFrameActivity) {
        this.iFrameActivity = iFrameActivity;
        if (this.loadWalletHtml != null) {
            iFrameActivity.loadHtml(loadWalletHtml);
        } else if (this.loadWalletUrl != null) {
            iFrameActivity.loadUrl(loadWalletUrl);
        }

    }

    public void onTimeOutWallet() {

        loadWalletActivity.hideCustomProgress();
        //loadWalletActivity.showMessage(false, "Please check your internet connection and try again");

    }

    /**
     * After loading the wallet is either successful or has met failure,
     * this method is called along with the success / failure message.
     * This method gets rid of extra punctuation / encoding and shows the
     * re-formatted message.
     *
     * @param message
     */
    public void onLoadWalletResponse(String message) {
        message = message.substring(1, message.length() - 1);
        message = message.replace("\\\"", "\"");

        Log.d(TAG, "Sanitised response: " + message);

        try {
            LoadWalletStatusResponse response = new LoadWalletStatusResponse(message);
            if (response.getStatusCode() == 1) {
                message = "Wallet loaded successfully";
                loadWalletActivity.showMessage(true, message);
                fetchBalance();
            } else {
                loadWalletActivity.showMessage(false, response.getMessage());
            }
        } catch (JSONException e) {
        }
    }

    /**
     * Terms And Conditions section
     */
    private void startLoadTnC() {
        /* Load T&C screen */
    }

    /**
     * Send Money section
     */
    private void startSendMoney(double argBalance) {
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            Intent intent = new Intent(activity, SendMoneyActivity.class);
            intent.putExtra(QuopnConstants.INTENT_KEYS.shmart_balance, argBalance);
            activity.startActivity(intent);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            Intent intent = new Intent(activity, SendMoneyActivity.class);
            intent.putExtra(QuopnConstants.INTENT_KEYS.citrus_balance, argBalance);
            activity.startActivity(intent);
        }
    }

    /**
     * Send Money screen has been created & loaded
     *
     * @param sendMoneyActivity
     */
    public void onSendMoneyCreated(SendMoneyActivity sendMoneyActivity) {
        this.sendMoneyActivity = sendMoneyActivity;
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.sendMoneyActivity.showBalance(this.balanceCitrus);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.sendMoneyActivity.showBalance(this.balanceShmart);
        }
//        this.sendMoneyActivity.showBalance(balance);
    }

    /**
     * OTP screen has been created & loaded
     *
     * @param shmartOtpActivity
     */
    public void onShmartOtpCreated(ShmartOtp shmartOtpActivity) {
        this.shmartOtpActivity = shmartOtpActivity;
    }

    public void onSendMoneyDestroyed() {
    }

    /* Send Money screen has re-opened after another screen on top of it was closed */
    public void onSendMoneyRestored(SendMoneyActivity activity) {
        this.sendMoneyActivity = activity;
    }

    /**
     * User has submitted the details needed to send money to a recipient.
     *
     * @param mobile:  Mobile number of the recipient.
     * @param amount:  Amount to be sent to the recipient.
     * @param message: Message shown in the SMS sent to the recipient.
     * @param txnPwd:  The transaction password entered to confirm the send operation.
     *                 The method submits the above details to the server to complete the send operation.
     */
    public void onSendMoneyRequested(String mobile, double amount, String message, String txnPwd) {

        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            sendMoneyActivity.showCustomProgress();

            ConnectionFactory factory
                    = new ConnectionFactory(sendMoneyActivity, shmartResponseListener);

            Map<String, String> map = new TreeMap<String, String>();
            map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(sendMoneyActivity).getPreference(
                    PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            map.put(QuopnApi.EWalletRequestParam.W2W_AMOUNT.getName(), "" + amount);
            map.put(QuopnApi.EWalletRequestParam.W2W_FRIEND_MOBILE.getName(), mobile);
            map.put(QuopnApi.EWalletRequestParam.W2W_FRIEND_NAME.getName()
                    , mobile);
            map.put(QuopnApi.EWalletRequestParam.W2W_FRIEND_EMAIL.getName()
                    , QuopnApi.EWalletDefault.FRIEND_EMAIL);
            map.put(QuopnApi.EWalletRequestParam.W2W_MESSAGE.getName()
                    , message);
            map.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);

            factory.setPostParams(map);
//        factory.createConnection(QuopnApi.EWalletApi.TRANSFER_TO_MOBILE.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.TRANSFER_TO_MOBILE.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {

//            Map<String, String> params = new TreeMap<String, String>();
//            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
//            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(),QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
//            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.WALLETTOWALLETTRANSFER);
//            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);
//
//            JSONObject mergedObj = new JSONObject();
//            try {
//                mergedObj.put("id", "id");
//                mergedObj.put("customer", number);
//                mergedObj.put("mobile", type);
//                mergedObj.put("merchant", );
//                mergedObj.put("type", );
//                mergedObj.put("date", );
//                mergedObj.put("amount", );
//                mergedObj.put("value", );
//                mergedObj.put("currency", );
//
//                mergedObj.put("status", );
//                mergedObj.put("reason", );
//                mergedObj.put("balance", );
//                mergedObj.put("value", );
//                mergedObj.put("currency", );
//
//                mergedObj.put("ref", );
//                mergedObj.put("transaction_pwd", "");
//                mergedObj.put("transaction_id", );
//
//
//                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_ADDR.getName(), branch);
//                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_IFSC.getName(), ifsc);
//                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_MOBILE.getName(), mobile);
//                mergedObj.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
//            } catch (JSONException e) {
//            }
//
//            params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
//            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
//
////            ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
//            ConnectionFactory connectionFactory
//                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);
//            connectionFactory.setPostParams(params);
//            Logger.d(params.toString());
//            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);

        }
    }




    /**
     * This method is called after the server sends a response after a 'Send Money' request
     * is successful.
     *
     * @param message: The success message. This message is shown to the user.
     */
    public void onSendMoneySuccess(String message) {
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(true, message);
            mAnalysisManager = ((QuopnApplication) sendMoneyActivity.getApplicationContext()).getAnalysisManager();
            mAnalysisManager.send(AnalysisEvents.SENT_MONEY);
            fetchBalance();
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(true, message);
            // mAnalysisManager = ((QuopnApplication) sendMoneyActivity.getApplicationContext()).getAnalysisManager();
            //mAnalysisManager.send(AnalysisEvents.SENT_MONEY);
            fetchBalance();
        }
    }

    /**
     * This method is called after the server sends a response after a 'Send Money' request fails.
     *
     * @param message: The failure message. This message is shown to the user.
     */
    public void onSendMoneyFailure(int code, String message) {

        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(false, message);
            sendMoneyActivity.clearTextfield();
            mAnalysisManager = ((QuopnApplication) sendMoneyActivity.getApplicationContext()).getAnalysisManager();
            mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_UNABLE_TO_SENDMONEY);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {

            sendMoneyActivity.hideCustomProgress();
            sendMoneyActivity.showMessage(false, message);
            sendMoneyActivity.clearTextfield();
            //mAnalysisManager = ((QuopnApplication) sendMoneyActivity.getApplicationContext()).getAnalysisManager();
            //mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_UNABLE_TO_SENDMONEY);
        }
    }

    /**
     * Shop at Quopn section
     * Once the user chooses this section, this method is called.
     * This method fetches the user's latest preference.
     */
    private void startShopAtQuopn() {

        // TODO: get response before launching activity
        Intent intent = new Intent(activity, UseQuopnActivity.class);
        activity.startActivity(intent);
        if (QuopnUtils.isInternetAvailable(context)) {
            getLatestPref();
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    /**
     * This method fetches the user's latest preference from the server.
     */
    private void getLatestPref() {
        //useQuopnActivity.showProgress();
        ConnectionFactory factory = new ConnectionFactory(activity, shmartResponseListener);
        Map<String, String> map = new TreeMap<String, String>();
        map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                , PreferenceUtil.getInstance(activity).getPreference(
                PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        String mobileWalledId = QuopnApi.QUOPN_CITRUS_LOGWALLETSTATS;
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
        }
        factory.setPostParams(map);
        factory.createConnection(QuopnApi.EWalletApi.GET_PREF.getApiCode());
    }

    /**
     * Use Quopn preference screen has been created.
     *
     * @param useQuopnActivity
     */
    public void onUseQuopnActivityCreated(UseQuopnActivity useQuopnActivity) {
        this.useQuopnActivity = useQuopnActivity;
    }

    public void onUseQuopnActivityDestroyed() {
    }

    /**
     * Use Quopn screen re-opened after another screen on top of it was closed
     *
     * @param activity
     */
    public void onUseQuopnActivityRestored(UseQuopnActivity activity) {
        this.useQuopnActivity = activity;
    }

    /**
     * User's preference has been returned by the server
     *
     * @param pref: User's preference as returned by the server
     */
    public void onPrefReceived(EWalletUsagePref pref) {
        //useQuopnActivity.stopProgress();
        useQuopnActivity.showCurrentPref(pref);
    }

    /**
     * User has set a new preference, overwriting the older one
     *
     * @param pref:   User's new preference
     * @param txnPwd: Transaction password used by the user to change the preference
     */
    public void onUseQuopnPrefSet(EWalletUsagePref pref, String txnPwd) {
        setPref(pref, txnPwd);
    }

    /**
     * Make a call to the server to set the user's latest preference
     *
     * @param pref:   User's new preference
     * @param txnPwd: Transaction password used by the user to change the preference
     */
    private void setPref(EWalletUsagePref pref, String txnPwd) {
        useQuopnActivity.showProgress();
        ConnectionFactory factory = new ConnectionFactory(useQuopnActivity, shmartResponseListener);
        Map<String, String> map = new TreeMap<String, String>();
        map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                , PreferenceUtil.getInstance(activity).getPreference(
                PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
        }
        map.put(QuopnApi.EWalletRequestParam.PREF.getName(), pref.getPref());
        map.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
        factory.setPostParams(map);
        factory.createConnection(QuopnApi.EWalletApi.SET_PREF.getApiCode());
    }

    /**
     * If the server has saved the user's preference successfully, then this method is called.
     *
     * @param message: The success message sent by the server. This is shown to the user.
     */
    public void onPrefSetSuccess(String message, UpdatePrefResponse updatePrefResponse) {
        // updating the defaultwallet
        String defaultWallet = updatePrefResponse.getDefaultWallet();
        if (!defaultWallet.isEmpty()) {
            QuopnUtils.setDefaultWalletInAppAndPref(defaultWallet, QuopnApplication.getInstance().getApplicationContext());
        }
        useQuopnActivity.stopProgress();
        useQuopnActivity.showMessage(true, message);
    }


    /**
     * If the server fails to set the user's preference, then this method is called.
     *
     * @param message: The error message sent by the server. This is shown to the user.
     */
    public void onPrefSetFailure(int errorCode, String message) {
        useQuopnActivity.stopProgress();
        useQuopnActivity.showMessage(false, message);
        useQuopnActivity.clearTextfield();
    }

    /**
     * Transfer to Bank section
     */
    private void startBankTransfer(double argBalance) {
        /* Start the transfer to bank activity */
        Intent intent = new Intent(activity, BankActivity.class);
        intent.putExtra(QuopnConstants.INTENT_KEYS.shmart_balance, argBalance);
        activity.startActivity(intent);
    }

    /**
     * Fetch the list of beneficiaries from the server
     */
    private void fetchBeneficiaries() {

        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            bankActivity.showProgress();
            ConnectionFactory factory = new ConnectionFactory(activity, shmartResponseListener);
            Map<String, String> map = new TreeMap<String, String>();
            map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(activity).getPreference(
                    PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            factory.setPostParams(map);
//        factory.createConnection(QuopnApi.EWalletApi.BENEFICIARIES.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.BENEFICIARIES.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {

            Map<String, String> params = new TreeMap<String, String>();

            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.GETBENEFICIARYLIST);
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);
            JSONObject emptyObj = new JSONObject();
            params.put(QuopnApi.ParamKey.REQUESTPARAMS, emptyObj.toString());
            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");

            ConnectionFactory connectionFactory
                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);
            connectionFactory.setPostParams(params);
//            Logger.d(params.toString());
            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
        }
    }

    /**
     * Bank screen is created and shown. As soon as this method is called, the list of beneficiaries
     * is fetched.
     */
    public void onBankActivityCreated(BankActivity bankActivity) {
        Log.d(TAG, "Bank activity created");
        this.bankActivity = bankActivity;
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.bankActivity.showBalance(this.balanceCitrus);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.bankActivity.showBalance(this.balanceShmart);
        }
//        this.bankActivity.showBalance(this.balance);

        /* Get the list of payees */
        fetchBeneficiaries();
    }

    public void onBankActivityDestroyed() {
    }

    public void onBankActivityRestored(BankActivity bankActivity) {
        Log.d(TAG, "Bank activity restored");
        this.bankActivity = bankActivity;
    }

    /**
     * This method is called if there are no beneficiaries registered for this user.
     */
    public void onNoBeneficiary() {
        bankActivity.stopProgress();
        this.bankActivity.showAddBeneficiary();
    }

    /**
     * This method is called, when the user chooses to register a new beneficiary.
     */
    public void onAddAccountChosen() {
        Intent intent = new Intent(bankActivity, BankAccountActivity.class);
        bankActivity.startActivity(intent);
    }

    /**
     * This method is called as soon as the screen to fill in a new beneficiary's details
     * is created & loaded.
     *
     * @param bankAccountActivity
     */
    public void onBankAccountActivityCreated(BankAccountActivity bankAccountActivity) {
        this.bankAccountActivity = bankAccountActivity;
    }

    public void onBankAccountActivityDestroyed() {
    }

    public void onBankAccountActivityRestored(BankAccountActivity activity) {
        this.bankAccountActivity = activity;
    }

    /**
     * This method is called when the user submits the details of a new beneficiary
     *
     * @param holder: Name of the account holder
     * @param number: Account number
     * @param type:   Current or savings
     * @param ifsc:   IFSC code of the branch
     * @param branch: Name of the branch
     * @param mobile: Mobile number of the account holder
     * @param txnPwd: Transaction PIN entered by the user while adding the beneficiary
     *                The details of the account are sent to the server, to add the beneficiary to the user's
     *                Shmart account.
     */
    public void onBankDetailsSubmitted(
            String holder, String number, String type, String ifsc, String branch, String mobile
            , String txnPwd) {

        bankAccountActivity.showProgress();

        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {

            ConnectionFactory factory
                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);

            Map<String, String> mapPost = new TreeMap<String, String>();
            mapPost.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankAccountActivity).getPreference(
                    PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            mapPost.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                    , QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_NAME.getName(), holder);
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_ACCT_NUM.getName(), number);
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_ACCT_TYPE.getName(), type);
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_ADDR.getName(), "Mumbai");
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_IFSC.getName(), ifsc);
            mapPost.put(QuopnApi.EWalletRequestParam.PAYEE_MOBILE.getName(), mobile);
            mapPost.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);

            factory.setPostParams(mapPost);
//        factory.createConnection(QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            Map<String, String> params = new TreeMap<String, String>();
//            mapPost.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
//                    , PreferenceUtil.getInstance(bankAccountActivity).getPreference(
//                    PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.ADDBENEFICIARY);
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);

            JSONObject mergedObj = new JSONObject();
            try {
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_NAME.getName(), holder);
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_ACCT_NUM.getName(), number);
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_ACCT_TYPE.getName(), type);
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_ADDR.getName(), branch);
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_IFSC.getName(), ifsc);
                mergedObj.put(QuopnApi.EWalletRequestParam.PAYEE_MOBILE.getName(), mobile);
                mergedObj.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
            } catch (JSONException e) {
            }

            params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");

//            ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
            ConnectionFactory connectionFactory
                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);
            connectionFactory.setPostParams(params);
//            Logger.d(params.toString());
            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
            // TODO response for logwalletstats


//            factory.setPostParams(mapPost);
////        factory.createConnection(QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode());
//            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.ADD_BENEFICIARY.getApiCode());
//            requestManager.setListener(this);
        }
    }

    /**
     * This method is called when a beneficiary is successfully added.
     *
     * @param message: The success message sent by the server. This message is shown to
     *                 the user.
     */
    public void onBeneficiaryAdded(String message) {
//        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
        bankAccountActivity.stopProgress();
        bankAccountActivity.showMessage(true, message);
        mAnalysisManager = ((QuopnApplication) bankAccountActivity.getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.ADDED_BENIFICIARY);
        fetchBeneficiaries();
//        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){
//            bankAccountActivity.stopProgress();
//            bankAccountActivity.showMessage(true, message);
//            //mAnalysisManager = ((QuopnApplication) bankAccountActivity.getApplicationContext()).getAnalysisManager();
//            //mAnalysisManager.send(AnalysisEvents.ADDED_BENIFICIARY);
//            fetchBeneficiaries();
//        }
    }


    /**
     * This method is called when a beneficiary addition fails.
     *
     * @param errorCode:    The error code for the failure.
     * @param errorMessage: The error message sent from the server. This message is shown to the user.
     */
    public void onBeneficiaryNotAdded(int errorCode, String errorMessage) {
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            bankAccountActivity.stopProgress();
            bankAccountActivity.showMessage(false, errorMessage);
            bankAccountActivity.clearTextfield();
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
            bankAccountActivity.stopProgress();
            bankAccountActivity.showMessage(false, errorMessage);
            bankAccountActivity.clearTextfield();
        }
    }

    /**
     * This method is called when the server has fetched the user's beneficiary from the server
     *
     * @param code:    Code used to identify the beneficiary. This code is used during tranfer.
     * @param name:    Name of the beneficiary.
     * @param account: Account number of the beneficiary.
     * @param bank:    Name of the bank of the beneficiary's account.
     */
    public void onBeneficiaryAvailable(String code, String name, String account, String bank) {
        bankActivity.stopProgress();
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            this.beneficiaryCode_shmart = code;
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
            this.beneficiaryCode_citrus = code;
        }
        bankActivity.showBeneficiary(name, account, bank);
    }

    /**
     * Called when the user chooses to delete the beneficiary.
     *
     * @param txnPwd: Transaction PIN used by the user when deleting the beneficiary.
     */
    public void onDeleteBeneficiaryChosen(String txnPwd) {
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            bankActivity.showProgress();

            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            params.put(QuopnApi.EWalletRequestParam.BENEFICIARY_CODE.getName(), beneficiaryCode_shmart);
            params.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
            factory.setPostParams(params);
            factory.createConnection(QuopnApi.EWalletApi.DELETE_BENEFICIARY.getApiCode());
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {
            bankActivity.showProgress();
            Map<String, String> params = new TreeMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.DELETEBENEFICIERY);
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);

            JSONObject mergedObj = new JSONObject();
            try {
                mergedObj.put(QuopnApi.EWalletRequestParam.BENEFICIARY_CODE.getName(), beneficiaryCode_citrus);
                mergedObj.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);

            } catch (JSONException e) {
            }

            params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");

//            ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
            ConnectionFactory connectionFactory
                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);
            connectionFactory.setPostParams(params);
//            Logger.d(params.toString());
            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
        }
    }

    /**
     * Called when deletion of the beneficiary is successful.
     *
     * @param message: The success message sent by the server. This message is shown to the user.
     *                 On success, the new beneficiary's details are shown on the screen.
     */
    public void onDeleteBeneficiarySuccess(String message) {
        bankActivity.stopProgress();
        bankActivity.showMessage(true, message);
        bankActivity.removeBeneficiary();
        bankActivity.restoreViewState();
        mAnalysisManager = ((QuopnApplication) bankActivity.getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.DELETED_BENIFICIARY);
    }

    /**
     * Called when the deletion of the beneficiary fails.
     *
     * @param message: The error message sent by the server, which is to be shown to the user.
     */
    public void onDeleteBeneficiaryFailure(String message) {
        bankActivity.stopProgress();
        bankActivity.showMessage(false, message);
    }

    /**
     * Called when a user chooses to transfer money to the beneficiary after entering the amount.
     *
     * @param amount: Amount to be transferred to the beneficiary
     * @param otp:    OTP used by the user while transferring
     */
    public void onBankTransferChosen(double amount, String otp) {
        transferToBank(amount, otp);
    }

    /**
     * This method initiates a request on the server to transfer money to the beneficiary
     *
     * @param amount: Amount to be sent to the beneficiary
     * @param otp:    OTP used by the user while transferring
     */
    private void transferToBank(double amount, String otp) {

        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            bankActivity.showProgress();

            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "1");
            params.put(QuopnApi.EWalletRequestParam.BENEFICIARY.getName(), beneficiaryCode_shmart);
            params.put(QuopnApi.EWalletRequestParam.AMOUNT.getName(), "" + amount);
            params.put(QuopnApi.EWalletRequestParam.OTP.getName(), otp);
            factory.setPostParams(params);
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.TRANSFER_TO_BENEFICIARY.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {

//            Map<String, String> params = new TreeMap<String, String>();
//            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
//            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), String.valueOf(QuopnConstants.WalletType.CITRUS.ordinal()));
//            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.TRANSFERTOBANK);
//            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);
//
//            JSONObject mergedObj = new JSONObject();
//            try {
//                mergedObj.put(QuopnApi.EWalletRequestParam.BENEFICIARY_CODE.getName(), beneficiaryCode_citrus);
//                mergedObj.put(QuopnApi.EWalletRequestParam.AMOUNT.getName(), "" + amount);
//
//            } catch (JSONException e) {
//            }
//
//            params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
//            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
//
////            ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
//            ConnectionFactory connectionFactory
//                    = new ConnectionFactory(bankAccountActivity, shmartResponseListener);
//            connectionFactory.setPostParams(params);
//            Logger.d(params.toString());
//            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
        }
    }

    /**
     * This method is used when an OTP is required from Shmart
     */
    public void generateShmartOTP() {
        ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
        Map<String, String> params = new TreeMap<String, String>();
        params.put(
                QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                , PreferenceUtil.getInstance(bankActivity).getPreference(
                        PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "1");
        factory.setPostParams(params);
        factory.createConnection(QuopnApi.EWalletApi.OTP.getApiCode());
    }

    /**
     * This method is used when an OTP is required from the Quopn server
     */
    public void requestOTP() {
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "1");
            factory.setPostParams(params);
            factory.createConnection(QuopnApi.EWalletApi.LOCAL_OTP.getApiCode());
        }else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {

            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "2");
            factory.setPostParams(params);
            factory.createConnection(QuopnApi.EWalletApi.LOCAL_OTP.getApiCode());
        }
    }

    /**
     * This method is called when a transfer to beneficiary is successful
     *
     * @param message: The success message sent from the server to be shown to the user
     */
    public void onBankTransferSuccess(String message) {
        bankActivity.stopProgress();
        bankActivity.showMessage(true, message);
        fetchBalance();

    }

    /**
     * Called when a transfer to beneficiary fails
     *
     * @param errorCode: Error code for the failure
     * @param message:   Error message for the failure to be shown to the user
     */
    public void onBankTransferFailure(int errorCode, String message) {
        bankActivity.stopProgress();
        bankActivity.showMessage(false, message);
    }

    /**
     * Settings section
     */
    public void startSettingsFlow() {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    /**
     * This method is called when settings activity is opened
     *
     * @param activity
     */
    public void onSettingsActivityCreated(SettingsActivity activity) {
        this.settingsActivity = activity;
    }

    public void onSettingsActivityDestroyed() {
    }

    public void onSettingsActivityRestored(SettingsActivity activity) {
        this.settingsActivity = activity;
    }

    /**
     * This method is called when the user chooses to change the transaction password
     *
     * @param txnPwd: New transaction PIN chosen by the user
     * @param otp:    OTP used by the user while changing the PIN.
     */
    public void onTxnPwdResetConfirm(String txnPwd, String otp) {
//        Logger.d("");
        settingsActivity.showProgress();
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "1");
            params.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
            params.put(QuopnApi.EWalletRequestParam.OTP.getName(), otp);
            factory.setPostParams(params);
            factory.createConnection(QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode());
            requestManager.setListener(this);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ConnectionFactory factory = new ConnectionFactory(bankActivity, shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(bankActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "2");
            params.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), txnPwd);
            params.put(QuopnApi.EWalletRequestParam.OTP.getName(), otp);
            factory.setPostParams(params);
            factory.createConnection(QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode());
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.CHANGE_TXN_PWD.getApiCode());
            requestManager.setListener(this);
        }
    }

    /**
     * This method is called when the server has successfully changed the txn PIN.
     *
     * @param message: Success message sent from the server, which is to be shown to the user.
     */
    public void onTxnPwdChanged(String message) {
        settingsActivity.stopProgress();
        settingsActivity.showMessage(true, message);
    }

    /**
     * This method is called when the server has failed to change the txn PIN.
     *
     * @param errorCode: Error code sent by the server for the failure.
     * @param message:   Error message sent by the server for the failure, This is shown to the user.
     */
    public void onTxnPwdNotChanged(int errorCode, String message) {
        settingsActivity.stopProgress();
        settingsActivity.showMessage(false, message);
    }

    /**
     * Transactions
     */
    private void startTransactions() {
        Intent intent = new Intent(activity, TransactionsActivity.class);
        activity.startActivity(intent);
        if (QuopnUtils.isInternetAvailable(context)) {
            fetchTransactions();
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }

    }

    public void onTransactionsActivityCreated(TransactionsActivity transactionsActivity) {
        this.transactionsActivity = transactionsActivity;
    }

    public void onTransactionsActivityDestroyed() {
    }

    public void onTransactionsActivityRestored(TransactionsActivity activity) {
        this.transactionsActivity = activity;
    }

    /**
     * Fetch all the transactions performed on the user's cashback wallet.
     */
    private void fetchTransactions() {
        //transactionsActivity.showProgress();
        if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
            ConnectionFactory factory = new ConnectionFactory(transactionsActivity
                    , shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(transactionsActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID);
            factory.setPostParams(params);
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.TXNS.getApiCode());
            requestManager.setListener(this);
        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){
            ConnectionFactory factory = new ConnectionFactory(transactionsActivity
                    , shmartResponseListener);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(
                    QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                    , PreferenceUtil.getInstance(transactionsActivity).getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            factory.setPostParams(params);
            RequestManager requestManager = (RequestManager) factory.createConnection(QuopnApi.EWalletApi.TXNS.getApiCode());
            requestManager.setListener(this);
        }
    }


    /**
     * Called when the server returns the list of transactions performed on a cashback wallet
     *
     * @param transactions: The list of transactions
     *                      This method shows the list of transactions on the transactions screen.
     */
    public void onTransactionsReceived(List<TransactionsResponse.Transaction> transactions) {
        //transactionsActivity.stopProgress();
        transactionsActivity.showTransactions(transactions);
    }

    /** Deep link */
    /**
     * This method checks if the user is registered for cashback and if the cashback wallet is
     * activated.
     */
    private void checkStatus() {

        ConnectionFactory factory = new ConnectionFactory(deepLinkActivity
                , shmartResponseListener);
        Map<String, String> params = new TreeMap<String, String>();
        params.put(
                QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                , PreferenceUtil.getInstance(transactionsActivity).getPreference(
                        PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), "1");
        factory.setPostParams(params);
        factory.createConnection(QuopnApi.EWalletApi.CHECK_STATUS.getApiCode());
    }

    /**
     * This method is called after a transparent screen which serves as the landing page for deep links
     * to cashback wallet is created & loaded
     *
     * @param deepLinkActivity: The transparent screen that was created
     * @param scheme:           The protocol for the deep link
     * @param host:             The host component of the deep link
     * @param path:             The path of the deep link
     *                          This method checks the user's registration & activation status and redirects the user
     *                          to the appropriate screen.
     */
    public void onDeepLinkActivityCreated(DeepLinkActivity deepLinkActivity
            , String scheme, String host, String path) {

        this.deepLinkActivity = deepLinkActivity;
        Log.d(TAG, "scheme " + scheme + " host " + host);
        if (scheme.equals("quopn")) {
            if (host.equals("wallet") || host.equals("register")
                    || host.equals("activate") || host.equals("transaction_pwd")) {
                Log.d(TAG, "checkStatus");
                checkStatus();
            } else {
                Log.e(TAG, "wrong deep link");
            }
        } else if (scheme.equals("http") || scheme.equals("https")) {
            if (path.startsWith("/wallet")) {
                Log.d(TAG, "checkStatus");
                checkStatus();

            } else {
                Log.e(TAG, "wrong deep link");
            }
        } else {
            Log.e(TAG, "wrong scheme");
        }
//        if ((scheme.equals("quopn") && (host.equals("wallet") || host.equals("register")
//                    || host.equals("activate") || host.equals("transaction_pwd")))
//                || scheme.equals("http") && path.startsWith("/wallet")) {

//            checkStatus();

//        }
    }

    public void onDeepLinkActivityRestored(DeepLinkActivity deepLinkActivity) {
        this.deepLinkActivity = deepLinkActivity;
    }

    /**
     * Called when the registration check status is returned by the server.
     *
     * @param walletStatusCode: The status of registration & activation.
     */
    public void onWalletStatusAvailable(String walletStatusCode) {
        Log.d(TAG, "Wallet status code: " + walletStatusCode);

        if (walletStatusCode == null || walletStatusCode.isEmpty()) {

            showRegScreen();

        } else if (walletStatusCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {

            fetchBalance();
            getFeatures();
            Intent shmartHomeIntent = new Intent(deepLinkActivity, ShmartHomeActivity.class);
            deepLinkActivity.startActivity(shmartHomeIntent);

//        } else if (walletStatusCode.equals("1"))){
//            Log.d(TAG, "walletStatusCode.equals(\"1\") is true");
//            fetchBalance();
//            getFeatures();
//            Intent shmartHomeIntent = new Intent(deepLinkActivity, ShmartHomeActivity.class);
//            deepLinkActivity.startActivity(shmartHomeIntent);

        } else if (walletStatusCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_NOT_EXIST)) {

            showRegScreen();

        } else if (walletStatusCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_DOES_NOT_EXIST)) {

            showRegScreen();

        } else if (walletStatusCode.equals(QuopnApi.SHMART_ERROR_CODES.OTP_ACTIVATION_PENDING)) {

            showOTPScreen(true);

        } else if (walletStatusCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK) || walletStatusCode.equals("1")) {
            showOTPScreen(false);
        }

        this.deepLinkActivity.finish();
    }

    public void onInternetNotAvailable() {

    }

    /**
     * Show the registration screen
     */
    private void showRegScreen() {
        Intent shmartRegn = new Intent(deepLinkActivity, ShmartRegn.class);
        deepLinkActivity.startActivity(shmartRegn);
    }

    /**
     * Show the OTP screen
     *
     * @param asShmart: Whether a Shmart or Quopn OTP is to be generated.
     */
    private void showOTPScreen(boolean asShmart) {
        if (asShmart) {
            generateShmartOTP();
        } else {
            requestOTP();
        }

        Intent shmartOtp = new Intent(deepLinkActivity, ShmartOtp.class);
        if (asShmart) {
            shmartOtp.putExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, true);
        } else {
            shmartOtp.putExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, true);
        }
        deepLinkActivity.startActivity(shmartOtp);
    }

    /** Network responses */
    /**
     * This method called when the server returns with the latest balance in response to
     * a balance fetch queries.
     *
     * @param balance: The balance returned by the server.
     */
    public void onBalanceAvailable(double balance) {
        setBalance(balance);
    }

    /**
     * This method is called when the server confirms that an OTP has been sent
     * from the Quopn server
     *
     * @param otpResponse
     */
    public void onLocalOtp(LocalOtp otpResponse) {
        settingsActivity.showMessage("OTP Sent", otpResponse.getMessage());
    }


    /**
     * This method is called when the server confirms that an OTP has been sent
     * from the Shmart server
     *
     * @param otpResponse
     */
    public void onRemoteOtp(RemoteOtp otpResponse) {

    }

    public void citrusLogWalletStatsForRefreshToken() {

        if (QuopnApplication.getAccessToken() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(activity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.REFRESHACCESSTOKEN);
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);

            JSONObject mergedObj = new JSONObject();
            try {
                mergedObj.put("access_token", QuopnApplication.getAccessToken().getAccessToken());
                mergedObj.put("refresh_token", QuopnApplication.getAccessToken().getRefreshToken());
                mergedObj.put("token_type", QuopnApplication.getAccessToken().getTokenType());
                mergedObj.put("expires_in", QuopnApplication.getAccessToken().getExpiresIn());
                mergedObj.put("scope", QuopnApplication.getAccessToken().getScope());

                params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                ConnectionFactory connectionFactory = new ConnectionFactory(activity, shmartResponseListener);
                connectionFactory.setPostParams(params);
//                Logger.d(params.toString());
                connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
                QuopnApplication.setAccessToken(null);
            } catch (JSONException e) {
            }
        }
//            else if (error != null) {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
//                params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
//                params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.REFRESHACCESSTOKEN);
//                params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_D);
//
//                JSONObject mergedObj = new JSONObject();
//                try {
//                    mergedObj.put("error_message", error.getMessage());
//                    mergedObj.put("error_status", error.getStatus().name());
//
//                    params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
//                    params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
//                    ConnectionFactory connectionFactory = new ConnectionFactory(context, MainActivity.this);
//                    connectionFactory.setPostParams(params);
//                    Logger.d(params.toString());
//                    connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
//                } catch (JSONException e) {
//                }
//            }
    }
}
