package com.quopn.wallet.shmart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.PaymentResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.ValidateTxnPinResponse;
import com.quopn.wallet.data.model.citrus.CitrusLogWalletStats;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMoneyActivity extends ActionBarActivity implements ConnectionListener {
    private static final String TAG = "Quopn/SendMoney";
    private TextView tvSendMoneyBalance;
    private EditText etSendMoneyMobile, etSendMoneyMessage, etSendMoneyAmount;
    private ImageButton ibSendMoneyContacts;
    private Button btSendMoney;
    private RelativeLayout rlSendMoneyTxnPwd;
    private Button btSendMoneyConfirm;
    private EditText etSendMoneyTxnPwd;
    private double amount;
    private double balance;
    public static final int PICK_CONTACT = 1;
    int digits = 10;
    int plus_sign_pos = 0;
    private String mobNumber;
    private CustomProgressDialog mCustomProgressDialog;
    private AnalysisManager mAnalysisManager;
    private CitrusClient mCitrusClient = null;
    private Context mContext = this;
//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//
//                case R.id.ibSendMoneyContacts: {
//                    etSendMoneyMobile.setText("");
//                    /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                    startActivityForResult(intent, 0);*/
//                    readcontact();
//                }
//                    break;
//
//                case R.id.btSendMoney: {
//
//                   // showSendMoneyPrompt();
//                    /* Validation of phone, message and amount */
//                    if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
//                        Gson gson = new GsonBuilder().serializeNulls().create();
//                        ProfileData response = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
//                        if (etSendMoneyMobile.getText().toString().isEmpty()
//                                || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_mobile_with_contact_picker
//                                    , etSendMoneyMobile, null, 0);
//                            return;
//                        } else if (response.getUser().getMobile().contains(etSendMoneyMobile.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_send_money_to_self
//                                    , etSendMoneyMobile, null, 0);
//                            return;
//                        } else if (etSendMoneyAmount.getText().toString().isEmpty()
//                                || !Validations.isValidNumberNotDoubleNotZero(etSendMoneyAmount.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_shmart_amount
//                                    , etSendMoneyAmount, null, 0);
//                            return;
//                        }/* else if (etSendMoneyMessage.getText().toString().isEmpty()) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_send_money_message
//                                    , etSendMoneyMessage, null, 0);
//                            return;
//                        }*/
//
//                        amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
//                        if (amount > balance) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_insufficient_balance
//                                    , etSendMoneyAmount, null, 0);
//                            return;
//                        }
//
//                        promptTxnPwd();
//                    } else {
//                        new Dialog(SendMoneyActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
//                    }
//                }
//                    break;
//
//                case R.id.btSendMoneyConfirm:
////                    amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
////                    if (etSendMoneyMobile.getText().toString().isEmpty()
////                            || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
////                        Validations.CustomErrorMessage(SendMoneyActivity.this
////                                , R.string.validation_shmart_mobile
////                                , etSendMoneyMobile, null, 0);
////                        return;
////                    } else if (etSendMoneyAmount.getText().toString().isEmpty()
////                            || !Validations.isValidAmount(etSendMoneyAmount.getText().toString())) {
////                        Validations.CustomErrorMessage(SendMoneyActivity.this
////                                , R.string.validation_shmart_amount
////                                , etSendMoneyAmount, null, 0);
////                        return;
////                    } /*else if (etSendMoneyMessage.getText().toString().isEmpty()) {
////                        Validations.CustomErrorMessage(SendMoneyActivity.this
////                                , R.string.validation_send_money_message
////                                , etSendMoneyMessage, null, 0);
////                        return;
////                    }*/ else if (amount > balance) {
////                        Validations.CustomErrorMessage(SendMoneyActivity.this
////                                , R.string.validation_insufficient_balance
////                                , etSendMoneyAmount, null, 0);
////                        return;
//                {
//                    /* Validation of phone, message and amount */
//                    if(QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
//                        Gson gson = new GsonBuilder().serializeNulls().create();
//                        ProfileData response = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
//                        if (etSendMoneyMobile.getText().toString().isEmpty()
//                                || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_mobile_with_contact_picker
//                                    , etSendMoneyMobile, null, 0);
//                            return;
//                        } else if (response.getUser().getMobile().contains(etSendMoneyMobile.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_send_money_to_self
//                                    , etSendMoneyMobile, null, 0);
//                            return;
//                        } else if (etSendMoneyAmount.getText().toString().isEmpty()
//                                || !Validations.isValidNumberNotDoubleNotZero(etSendMoneyAmount.getText().toString())) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_shmart_amount
//                                    , etSendMoneyAmount, null, 0);
//                            return;
//                        }/* else if (etSendMoneyMessage.getText().toString().isEmpty()) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_send_money_message
//                                    , etSendMoneyMessage, null, 0);
//                            return;
//                        }*/
//
//                        amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
//                        if (amount > balance) {
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_insufficient_balance
//                                    , etSendMoneyAmount, null, 0);
//                            return;
//                        } else if (etSendMoneyTxnPwd.getText().toString().isEmpty()
//                                || !Validations.isValidTxnPIN(etSendMoneyTxnPwd.getText().toString())) {
//
//                            Validations.CustomErrorMessage(SendMoneyActivity.this
//                                    , R.string.validation_invalid_txn_pwd
//                                    , etSendMoneyTxnPwd, null, 0);
//                            return;
//                        }
//                    }else{
//                        new Dialog(SendMoneyActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
//                    }
//                    if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
//                        ShmartFlow.getInstance().onSendMoneyRequested(
//                                etSendMoneyMobile.getText().toString()
//                                , amount, etSendMoneyMessage.getText().toString()
//                                , etSendMoneyTxnPwd.getText().toString());
//                    }
//                }
//                break;
//
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_send_money);
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCitrusClient = CitrusClient.getInstance(getApplicationContext());

        getSupportActionBar().setElevation(0);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_SEND_MONEY);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg));

        View actionBarView = View.inflate(this, R.layout.actionbar_layout, null);
        getSupportActionBar().setCustomView(actionBarView);

        ImageView slider = (ImageView) actionBarView.findViewById(R.id.slider);
        slider.setImageResource(R.drawable.back);
        slider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        ImageView home_btn = (ImageView) actionBarView
                .findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();

            }
        });

        ImageView mCommonCartButton = (ImageView) actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv = (TextView) actionBarView.findViewById(R.id.notification_counter_txt);

        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv = (TextView) actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        tvSendMoneyBalance = (TextView) findViewById(R.id.tvSendMoneyBalance);
        if (getIntent().hasExtra(QuopnConstants.INTENT_KEYS.shmart_balance)) {
            balance = getIntent().getDoubleExtra(QuopnConstants.INTENT_KEYS.shmart_balance, 0);
            tvSendMoneyBalance.setText("" + balance);
        }
        etSendMoneyMobile = (EditText) findViewById(R.id.etSendMoneyMobile);
        ibSendMoneyContacts = (ImageButton) findViewById(R.id.ibSendMoneyContacts);
        etSendMoneyMessage = (EditText) findViewById(R.id.etSendMoneyMessage);
        etSendMoneyMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMoney();
                    return true;
                }
                return false;
            }
        });
        etSendMoneyAmount = (EditText) findViewById(R.id.etSendMoneyAmount);
        btSendMoney = (Button) findViewById(R.id.btSendMoney);
        rlSendMoneyTxnPwd = (RelativeLayout) findViewById(R.id.rlSendMoneyTxnPwd);
        btSendMoneyConfirm = (Button) findViewById(R.id.btSendMoneyConfirm);
        etSendMoneyTxnPwd = (EditText) findViewById(R.id.etSendMoneyTxnPwd);
        etSendMoneyTxnPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMoneyConfirm();
                    return true;
                }
                return false;
            }
        });

        ibSendMoneyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });
        btSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoney();
                //showSendMoneyPrompt();
            }
        });
        btSendMoneyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoneyConfirm();
            }
        });

        if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
            ShmartFlow.getInstance().onSendMoneyCreated(this);
        } else {
            new Dialog(SendMoneyActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }
    }

    public void selectContact(){
        etSendMoneyMobile.setText("");
                    /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 0);*/
        readcontact();
    }

    public void sendMoney(){
        /* Validation of phone, message and amount */
        if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
            if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ProfileData response = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
                if (etSendMoneyMobile.getText().toString().isEmpty()
                        || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_mobile_with_contact_picker
                            , etSendMoneyMobile, null, 0);
                    return;
                } else if (response.getUser().getMobile().contains(etSendMoneyMobile.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_send_money_to_self
                            , etSendMoneyMobile, null, 0);
                    return;
                } else if (etSendMoneyAmount.getText().toString().isEmpty()
                        || !Validations.isValidNumberNotDoubleNotZero(etSendMoneyAmount.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_shmart_amount
                            , etSendMoneyAmount, null, 0);
                    return;
                }/* else if (etSendMoneyMessage.getText().toString().isEmpty()) {
                            Validations.CustomErrorMessage(SendMoneyActivity.this
                                    , R.string.validation_send_money_message
                                    , etSendMoneyMessage, null, 0);
                            return;
                        }*/

                amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
                if (amount > balance) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_insufficient_balance
                            , etSendMoneyAmount, null, 0);
                    return;
                }
            } else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

                Gson gson = new GsonBuilder().serializeNulls().create();
                ProfileData response = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
                if (etSendMoneyMobile.getText().toString().isEmpty()
                        || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_mobile_with_contact_picker
                            , etSendMoneyMobile, null, 0);
                    return;
                } else if (response.getUser().getMobile().contains(etSendMoneyMobile.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_send_money_to_self
                            , etSendMoneyMobile, null, 0);
                    return;
                } else if (etSendMoneyAmount.getText().toString().isEmpty()
                        || !Validations.isValidNumberNotDoubleNotZero(etSendMoneyAmount.getText().toString())) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_shmart_amount
                            , etSendMoneyAmount, null, 0);
                    return;
                }/* else if (etSendMoneyMessage.getText().toString().isEmpty()) {
                            Validations.CustomErrorMessage(SendMoneyActivity.this
                                    , R.string.validation_send_money_message
                                    , etSendMoneyMessage, null, 0);
                            return;
                        }*/

                amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
                if (amount > balance) {
                    Validations.CustomErrorMessage(SendMoneyActivity.this
                            , R.string.validation_insufficient_balance
                            , etSendMoneyAmount, null, 0);
                    return;
                }

            }
            promptTxnPwd();
        } else {
            new Dialog(SendMoneyActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }
    }

    public void sendMoneyConfirm() {
        if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ProfileData response = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
                    /* Validation of phone, message and amount */
            if (etSendMoneyMobile.getText().toString().isEmpty()
                    || !Validations.isValidMobile(etSendMoneyMobile.getText().toString())) {
                Validations.CustomErrorMessage(SendMoneyActivity.this
                        , R.string.validation_mobile_with_contact_picker
                        , etSendMoneyMobile, null, 0);
                return;
            } else if (response.getUser().getMobile().contains(etSendMoneyMobile.getText().toString())) {
                Validations.CustomErrorMessage(SendMoneyActivity.this
                        , R.string.validation_send_money_to_self
                        , etSendMoneyMobile, null, 0);
                return;
            } else if (etSendMoneyAmount.getText().toString().isEmpty()
                    || !Validations.isValidNumberNotDoubleNotZero(etSendMoneyAmount.getText().toString())) {
                Validations.CustomErrorMessage(SendMoneyActivity.this
                        , R.string.validation_shmart_amount
                        , etSendMoneyAmount, null, 0);
                return;
            }

            amount = Double.parseDouble(etSendMoneyAmount.getText().toString());
            if (amount > balance) {
                Validations.CustomErrorMessage(SendMoneyActivity.this
                        , R.string.validation_insufficient_balance
                        , etSendMoneyAmount, null, 0);
                return;
            } else if (etSendMoneyTxnPwd.getText().toString().isEmpty()
                    || !Validations.isValidTxnPIN(etSendMoneyTxnPwd.getText().toString())) {

                Validations.CustomErrorMessage(SendMoneyActivity.this
                        , R.string.validation_invalid_txn_pwd
                        , etSendMoneyTxnPwd, null, 0);
                return;
            }

            if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {
                ShmartFlow.getInstance().onSendMoneyRequested(
                        etSendMoneyMobile.getText().toString()
                        , amount, etSendMoneyMessage.getText().toString()
                        , etSendMoneyTxnPwd.getText().toString());
            } else if (QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)) {

               showCustomProgress();
                ConnectionFactory factory
                        = new ConnectionFactory(this,this);
                Map<String, String> map = new HashMap<String, String>();
                map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                        , PreferenceUtil.getInstance(getApplicationContext()).getPreference(
                        PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
                map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                        , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
                map.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(), etSendMoneyTxnPwd.getText().toString());
                factory.setPostParams(map);
                factory.createConnection(QuopnConstants.QUOPN_VALIDATE_TXN_PIN);
            }
        } else {
            Dialog dialog = new Dialog(SendMoneyActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onSendMoneyDestroyed();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onSendMoneyRestored(this);
    }

    public void clearTextfield(){
        etSendMoneyTxnPwd.setText("");
    }
    public void readcontact() {
        try {
            Intent intent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

            if (intent.resolveActivity(this
                    .getPackageManager()) == null) {
                Toast.makeText(this,
                        "There are no applications to handle your request",
                        Toast.LENGTH_LONG).show();
            } else {
                startActivityForResult(intent, PICK_CONTACT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                @SuppressWarnings("deprecation")
                Cursor c = this.managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
//                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    number = QuopnUtils.getIndianMobileFromContactNumber(number);
                    if (number != "0") {
                        etSendMoneyMobile.setText(number);
                    } else {
                        Dialog dialog=new Dialog(this, R.string.dialog_title_error,R.string.validation_mobile_with_contact_picker);
                        dialog.show();
                    }
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void showBalance(double balance) {
        this.balance = balance;
        tvSendMoneyBalance.setText("" + balance);
    }

    private void promptTxnPwd() {
        btSendMoney.setVisibility(View.GONE);
        rlSendMoneyTxnPwd.setVisibility(View.VISIBLE);
    }

    public void showMessage(final boolean isSuccess, final String message) {
        if (QuopnUtils.isActivityRunningForContext(SendMoneyActivity.this)) {
            final DialogInterface.OnDismissListener dismissListener
                    = new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (isSuccess) {
                        SendMoneyActivity.this.finish();
                    }
                }
            };
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    Dialog dialog = new Dialog(SendMoneyActivity.this, R.string.dialog_title_success, message);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }
            };
            runOnUiThread(runnable);
        }
    }

    public void showCustomProgress() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCustomProgressDialog.show();
            }
        };
        runOnUiThread(runnable);

    }

    public void hideCustomProgress() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCustomProgressDialog.dismiss();
            }
        };
        runOnUiThread(runnable);

    }

    private void sendMoneyCitrus() {
        if (QuopnUtils.isInternetAvailable(SendMoneyActivity.this)) {
            mCitrusClient.sendMoneyToMoblieNo(new Amount(Double.toString(amount)), etSendMoneyMobile.getText().toString(), etSendMoneyMessage.getText().toString(), new Callback<PaymentResponse>() {
                @Override
                public void success(PaymentResponse paymentResponse) {
                    showMessage(true, getApplicationContext().getString(R.string.citrus_sendmoney_success));
                    ShmartFlow.getInstance().setBalance(paymentResponse.getBalanceAmount().getValueAsDouble());

                    if (QuopnUtils.isInternetAvailable(getApplicationContext())) {
                        Logger.d("sendMoneyToMoblieNo success");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
                        params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
                        params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.WALLETTOWALLETTRANSFER);
                        params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);
                        JSONObject mergedObj = new JSONObject();
                        try {
                            mergedObj.put("id", QuopnUtils.sendNonNullValueForString(paymentResponse.getTransactionId()));
                            mergedObj.put("customer", QuopnUtils.sendNonNullValueForString(paymentResponse.getCustomer()));
                            mergedObj.put("mobile", etSendMoneyMobile.getText().toString());
                            mergedObj.put("merchant", QuopnUtils.sendNonNullValueForString(paymentResponse.getMerchantName()));
                            mergedObj.put("type", "Transfer");
                            mergedObj.put("date", QuopnUtils.sendNonNullValueForString(paymentResponse.getDate()));
                            JSONObject log_Amount = new JSONObject();
                            {
                                log_Amount.put("value", QuopnUtils.sendNonNullValueForString(paymentResponse.getTransactionAmount().getValue()));
                                log_Amount.put("currency", "INR");
                            }
                            mergedObj.put("amount", log_Amount);
                            mergedObj.put("status", QuopnUtils.sendNonNullValueForString(paymentResponse.getStatus().toString()));
                            mergedObj.put("reason", QuopnUtils.sendNonNullValueForString(paymentResponse.getMessage()));
                            JSONObject log_Balance = new JSONObject();
                            {
                                log_Balance.put("value", QuopnUtils.sendNonNullValueForString(paymentResponse.getBalanceAmount().getValue()));
                                log_Balance.put("currency", "INR");
                            }
                            mergedObj.put("balance", log_Balance);
                            mergedObj.put("ref", QuopnUtils.sendNonNullValueForString(paymentResponse.getTransactionId()));
                            mergedObj.put("transaction_pwd", etSendMoneyTxnPwd.getText().toString());
                            mergedObj.put("transaction_id", QuopnUtils.sendNonNullValueForString(paymentResponse.getTransactionId()));

                            params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                            params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                            ConnectionFactory connectionFactory = new ConnectionFactory(SendMoneyActivity.this, SendMoneyActivity.this);
                            connectionFactory.setPostParams(params);
                            connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
                        } catch (JSONException e) {

                        }
                    }
                }

                @Override
                public void error(CitrusError error) {
                    showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_tiny_error));
//                clearTextfield();
                    sendMoneyCitrusDump(error);
                }
            });
        } else {
            Dialog dialog = new Dialog(SendMoneyActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    private void sendMoneyCitrusDump(CitrusError error) {
        if (QuopnUtils.isInternetAvailable(mContext)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.WALLETTOWALLETTRANSFER);
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_D);
            JSONObject mergedObj = new JSONObject();
            try {
                if (error != null) {
                    mergedObj.put(QuopnApi.CITRUS_PARAMS.ERRORMESSAGE, error.getMessage());
                    if (error.getStatus() != null) {
                        mergedObj.put(QuopnApi.CITRUS_PARAMS.ERRORSTATUS, error.getStatus().name());
                    }
                }
                mergedObj.put("mobile", etSendMoneyMobile.getText().toString());
                mergedObj.put("amount", amount);

                params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                ConnectionFactory connectionFactory = new ConnectionFactory(SendMoneyActivity.this, SendMoneyActivity.this);
                connectionFactory.setPostParams(params);
                connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
            } catch (JSONException e) {

            }
        }
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if (response != null) {
            if (response instanceof ValidateTxnPinResponse) {
                hideCustomProgress();
                if (((ValidateTxnPinResponse) response).isSuccess()) {
                    sendMoneyCitrus();
                } else {
                    hideCustomProgress();
                    final Dialog dialog = new Dialog(this, R.string.dialog_title
                            , getString(R.string.trans_pwd_validation));
                    View.OnClickListener yesListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    };
                    dialog.addOkButton("Ok");
                    dialog.setOnAcceptButtonClickListener(yesListener);
                    dialog.show();
                }
            } else if (response instanceof CitrusLogWalletStats) {
                CitrusLogWalletStats citrusLogwalletStats = (CitrusLogWalletStats) response;
                Logger.d(citrusLogwalletStats.getJSON().toString());
            } else if (response instanceof NetworkError) {
                NetworkError networkError = (NetworkError) response;
                if (networkError.getError().networkResponse != null) {
                    NetworkResponse networkResponse = networkError.getError().networkResponse;
                    if (networkResponse.statusCode == 401) {
                        // TODO: check session invalidation
                    }
                }
            }
        } else {
            hideCustomProgress();
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    @Override
    public void myTimeout(String requestTag) {

    }
}
