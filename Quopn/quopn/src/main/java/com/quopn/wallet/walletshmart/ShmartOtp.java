package com.quopn.wallet.walletshmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.telephony.SmsMessage;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.ShmartGenerateOTPData;
import com.quopn.wallet.data.model.ShmartRequestOTPData;
import com.quopn.wallet.data.model.ShmartVerifyOTPAndChangeTransPwdData;
import com.quopn.wallet.data.model.ShmartVerifyOTPData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.shmart.ShmartFlow;
import com.quopn.wallet.shmart.ShmartResponseListener;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 21-Sep-15.
 */
public class ShmartOtp extends ActionBarActivity implements ConnectionListener {

    private ConnectionFactory mConnectionFactory;
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private String TAG = "Quopn/ShmartOtp";
    private EditText editOtp;
    private EditText editTransPass;
    private EditText editReEnterTransPass;
    private TextView editResendOtp;
    private Intent intent;
    private final int RESPONSE_SUCCESS_MESSAGE = 100;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(30000, 1000);
    private CustomProgressDialog progress;
    private SmsListener mSmsListener = new SmsListener();
    private boolean verifySent = false;
    private ShmartResponseListener shmartResponseListener;
    private AnalysisManager mAnalysisManager;
    private User user;
//    private CountDownTimerForDialogClosing countDownTimerForDialogClosing = new CountDownTimerForDialogClosing(5000, 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_otp);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_OTP);
        getSupportActionBar().setElevation(0);
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

        SearchView searchView = (SearchView) actionBarView
                .findViewById(R.id.fragment_address_search);
        searchView.setVisibility(View.INVISIBLE);
        ImageView mCommonCartButton = (ImageView) actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv = (TextView) actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv = (TextView) actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        progress = new CustomProgressDialog(this);

        final ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
        user = profileData.getUser();

        intent = getIntent();
        /*if(intent.hasExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP)){
            if(intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, false)) {
                sendRequestOTP(user.getApi_key(), user.getWalletid());
            }
        }
        if(intent.hasExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd)){
            if(intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, false)) {
                sendGenerateOTP(user.getApi_key(), user.getWalletid());
            }
        }*/


        editOtp = (EditText) findViewById(R.id.editEnterOTP);
//        final ImageView imgEnterOtpError = (ImageView) findViewById(R.id.imgEnterOtpError);
        editOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOtp.setError(null);
            }
        });
        editOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        editTransPass = (EditText) findViewById(R.id.editTransPass);
        editTransPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTransPass.setError(null);
            }
        });
        editTransPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        final ImageView imgTransPassInfo = (ImageView) findViewById(R.id.imgTransPassInfo);
        imgTransPassInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.shmart_trans_pwd_info, editTransPass, null, 2);
            }
        });
//        final ImageView imgTransPassErr = (ImageView) findViewById(R.id.imgTransPassError);

        editReEnterTransPass = (EditText) findViewById(R.id.editReEnterTransPass);
        editReEnterTransPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editReEnterTransPass.setError(null);
            }
        });
        editReEnterTransPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendSubmit();
                    return true;
                }
                return false;
            }
        });

//        final ImageView imgReEnterTransPassErr = (ImageView) findViewById(R.id.imgReEnterTransPassError);


        EditText editConfirmOtp = (EditText) findViewById(R.id.editSubmit);
        editConfirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSubmit();
            }
        });

        editResendOtp = (TextView) findViewById(R.id.editResendOtp);
        editResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "operation: " + editResendOtp.getText().toString()
                        + ", verify: " + intent.hasExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP)
                        + ", txn: " + intent.hasExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd));

                if (QuopnUtils.isInternetAvailable(ShmartOtp.this)) {
                    if (editResendOtp.getText().toString().equals(getResources().getString(R.string.resendotp_txt))) {

                        if (intent.hasExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP)) {
                            if (intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, false)) {
                                // sendVerifyOTP(user.getApi_key(), user.getWalletid(), editOtp.getText().toString(), editTransPass.getText().toString());
                                sendGenerateOTP(user.getApi_key(), user.getWalletid());
                                return;
                            } else {
                                Log.d(TAG, "error in intent");
                            }
                        } else if (intent.hasExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd)) {
                            if (intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, false)) {
                                //sendVerifyOTPAndChangeTrxPwd(user.getApi_key(), user.getWalletid(), editOtp.getText().toString(), editTransPass.getText().toString());
                                sendRequestOTP(user.getApi_key(), user.getWalletid());
                            } else {
                                Log.d(TAG, "error in intent");
                            }
                        }
//                        countDownTimer.start();
                    }
                } else {
                    Dialog dialog = new Dialog(ShmartOtp.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
                    dialog.show();
                }
            }
        });
        ShmartFlow.getInstance().onShmartOtpCreated(this); // ankur
        registerReceiver(mSmsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        countDownTimer.start();

    }

    public void sendSubmit(){
        String otp = editOtp.getText().toString().trim();
        String transPass = editTransPass.getText().toString().trim();
        String reEnterTransPass = editReEnterTransPass.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_otp_validation, editOtp, null, 0);
            return;
        } else if (!Validations.isValidOTP(otp)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.validation_invalid_otp, editOtp, null, 0);
            return;
        } else {
            editOtp.setError(null);
        }

        if (TextUtils.isEmpty(transPass)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_txnpin_validation, editTransPass, null, 0);
            return;
        } else if (!Validations.isValidTxnPIN(transPass)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.validation_invalid_txn_pwd, editTransPass, null, 0);
            return;
        } else {
            editTransPass.setError(null);
        }

        if (TextUtils.isEmpty(reEnterTransPass)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_txnpin_validation, editReEnterTransPass, null, 0);
            return;
        }  else if (!Validations.isValidTxnPIN(reEnterTransPass)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.validation_invalid_txn_pwd, editReEnterTransPass, null, 0);
            return;
        } else {
            editReEnterTransPass.setError(null);
        }

        if (!transPass.equals(reEnterTransPass)) {
//                    Toast.makeText(getBaseContext(), getResources().getString(R.string.trans_pass_mismatch), Toast.LENGTH_SHORT).show();
            editTransPass.setText("");
            editReEnterTransPass.setText("");
            Validations.CustomErrorMessage(getApplicationContext(), R.string.trans_pass_mismatch, editTransPass, null, 0);
            return;
        } else {
            try {
                editReEnterTransPass.setError(null);
            } catch (Exception ex) {
                Log.e(TAG, "editReEnterTransPass set erro");
            }
        }

        if (intent.hasExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP)) {
            if (intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, false)) {
                sendVerifyOTP(user.getApi_key(), user.getWalletid(), otp, transPass);
                return;
            } else {
                Log.e(TAG, " "+Thread.currentThread().getStackTrace()[2].getLineNumber());
            }
        } else if (intent.hasExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd)) {
            if (intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, false)) {
                sendVerifyOTPAndChangeTrxPwd(user.getApi_key(), user.getWalletid(), otp, transPass);
            } else {
                Log.e(TAG, " "+Thread.currentThread().getStackTrace()[2].getLineNumber());
            }
        }
    }

    public void sendVerifyOTP(String argAuthKey, String argWalletId, String argOTP, String argTransPass) {
        if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
            Map<String, String> params, headerParams;

            headerParams = new HashMap<String, String>();
            headerParams.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

            params = new HashMap<String, String>();
//            params.put("Authorization", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
            params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
            params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");
            params.put(QuopnConstants.CONN_PARAMS.otp, argOTP);
            params.put(QuopnConstants.CONN_PARAMS.trans_pass, argTransPass);
            mConnectionFactory = new ConnectionFactory(this, this);
            mConnectionFactory.setHeaderParams(headerParams);
            mConnectionFactory.setPostParams(params);
            mConnectionFactory.createConnection(QuopnConstants.SHMART_VERIFY_OTP);
        }

    }

    public void sendVerifyOTPAndChangeTrxPwd(String argAuthKey, String argWalletId, String argOTP, String argTransPass) {
        if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
            Map<String, String> params, headerParams;

            if (!TextUtils.isEmpty(PreferenceUtil.getInstance(getApplicationContext()).getPreference(
                    PreferenceUtil.SHARED_PREF_KEYS.API_KEY))) {

                verifySent = true;

                showProgress();

                headerParams = new HashMap<String, String>();
                headerParams.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

                params = new HashMap<String, String>();
//            params.put("Authorization", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
                params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
                params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");
                params.put(QuopnConstants.CONN_PARAMS.otp, argOTP);
                params.put(QuopnConstants.CONN_PARAMS.trans_pass, argTransPass);
                mConnectionFactory = new ConnectionFactory(this, this);
                mConnectionFactory.setHeaderParams(headerParams);
                mConnectionFactory.setPostParams(params);
                mConnectionFactory.createConnection(QuopnConstants.SHMART_VERIFY_OTP_AND_CHNG_TRX_PWD);
            } else {
                Log.e(TAG, "No API key");
            }
        }
    }

    private void sendRequestOTP(String argAuthKey, String argWalletId) {
        if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
            showProgress();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
            params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");

            ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
            connectionFactory.setHeaderParams(headers);
            connectionFactory.setPostParams(params);
            connectionFactory.createConnection(QuopnConstants.SHMART_REQUEST_OTP);
        }
    }

    public void sendGenerateOTP(String argAuthKey, String argWalletId) {
        if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
            showProgress();
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
            params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");
            ConnectionFactory mConnectionFactory = new ConnectionFactory(this, this);
            mConnectionFactory.setHeaderParams(headers);
            mConnectionFactory.setPostParams(params);
            mConnectionFactory.createConnection(QuopnConstants.SHMART_GENERATE_OTP);
        }
    }


    @Override
    public void onResponse(int responseResult, Response response) {
        stopProgress();
        if (response instanceof ShmartGenerateOTPData) {

            ShmartGenerateOTPData shmartCreateUserData = (ShmartGenerateOTPData) response;
            String errorCode = shmartCreateUserData.getError_code();
            String message = shmartCreateUserData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {
                countDownTimer.start();
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (response instanceof ShmartRequestOTPData) {

            ShmartRequestOTPData shmartRequestOTPData = (ShmartRequestOTPData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {
                countDownTimer.start();
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (response instanceof ShmartVerifyOTPData) {

            ShmartVerifyOTPData shmartRequestOTPData = (ShmartVerifyOTPData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.INCORRECT_OTP)) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {
                showMessage ();
            } else {
                Log.d(TAG, "error code - " + errorCode + ", error_message: " + message);

            }
        } else if (response instanceof ShmartVerifyOTPAndChangeTransPwdData) {

            ShmartVerifyOTPAndChangeTransPwdData shmartRequestOTPData = (ShmartVerifyOTPAndChangeTransPwdData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {
                if (verifySent) {
//                    Intent intent = new Intent(this, ShmartRegnSuccess.class);
//                    intent.putExtra(QuopnConstants.INTENT_KEYS.shmart_regn_success_msg, message);
//                    startActivity(intent);
//                    ShmartOtp.CustomAlertDialog_call("123", message, this);
//                    showSuccessDialog(message);
//                    countDownTimerForDialogClosing.start();
//                    finish();
                    showMessage ();
                }
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "error code - " + errorCode + ", error_message: " + message);
            }
        }

    }

    public void showMessage () {
//        String message = R.string.shmart_regn_success_msg;
        final Dialog dialog = new Dialog(this, R.string.dialog_title
                , R.string.shmart_regn_success_msg);

        View.OnClickListener yesListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showShmartWallet();
            }
        };

//        View.OnClickListener noListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        };

        dialog.addOkButton("OK");
//        dialog.addCancelButton("No");

        dialog.setOnAcceptButtonClickListener(yesListener);
//        dialog.setOnCancelButtonClickListener(noListener);

        dialog.show();
    }

    public void showSuccessDialog(String argMsg) {
        /*final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.wallet_regn_success);
        dialog.setTitle("Title...");

        TextView textViewMsg = (TextView) dialog.findViewById(R.id.textshmart_regn_success_msg);
        textViewMsg.setText(argMsg);*/

        // set the custom dialog components - text, image and button
        /*TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);*/

        /*Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        /*dialog.show();*/
    }

    /*public static void CustomAlertDialog_call(final String cta_text, final String argMsg, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(context);
        String mobileno = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);
        *//*title.setText(context.getResources().getString(R.string.call_txt)
                + "\n\nCURRENTLY REGISTERED MOBILE NUMBER IS: " + mobileno);*//*
        title.setText(argMsg);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 20, 10, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        builder.setCustomTitle(title);
        builder.setMessage(argMsg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (argMsg == null || argMsg.length() < 0) {
//                    sendCall(cta_text, "1234567890", context);
                } else {
//                    sendCall(cta_text, cta_value, context);
                }
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }*/

    private void showShmartWallet() {
        showProgress();
        ShmartFlow.getInstance().setContext(this);
        ShmartFlow.getInstance().start();
    }

    public void finishAll () {
        stopProgress();
        finish();
    }

    public void showError(String argMsg) {
        Toast.makeText(this, argMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout(ConnectRequest request) {
        if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
            stopProgress();
        }
    }

    @Override
    public void myTimeout(String requestTag) {

    }

    @Override
    protected void onDestroy() {
        stopProgress();
        super.onDestroy();
        unregisterReceiver(mSmsListener);
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            editResendOtp.setText(getResources().getString(R.string.resendotp_txt));
            underlineText();
            /*SpannableString content = new SpannableString("Content");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            editResendOtp.setText(content);*/
            /*editResendOtp.setPaintFlags(editResendOtp.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);*/
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String format = String.format(getResources().getString(R.string.waiting_for_sms), millisUntilFinished / 1000);
            editResendOtp.setText(format);
        }
    }

    public void showProgress() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    public void stopProgress() {
        if (progress != null &&  progress.isShowing()) {
            progress.dismiss();
        }
    }

    private Handler messagehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS_MESSAGE:
                    editOtp.setText(msg.getData().getString("message"));
                    countDownTimer.cancel();
                    editResendOtp.setText(getResources().getString(R.string.resendotp_txt));
                    underlineText();
//                    editResendOtp.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }

        }
    };

    public void underlineText() {
        SpannableString content = new SpannableString(getResources().getString(R.string.resendotp_txt));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        editResendOtp.setText(content);
    }

    class SmsListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    "android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras(); // ---get the SMS
                // message passed
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null) {
                    // ---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage
                                    .createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            if (msg_from.contains("mQUOPN") || msg_from.contains("SHMART")) {
                                String msgBody = msgs[i].getMessageBody();
                                Message msg = Message.obtain();
                                msgBody = msgBody.replaceAll("[^-?0-9]+", "");
                                if (msgBody.length() > 3) {
                                    msg.what = RESPONSE_SUCCESS_MESSAGE;
                                    Bundle b = new Bundle();
                                    b.putString("message", msgBody);
                                    msg.setData(b);
                                    messagehandler.sendMessage(msg);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Log.d("Exception caught",e.getMessage());
                    }
                }
            }
        }

    }
}
