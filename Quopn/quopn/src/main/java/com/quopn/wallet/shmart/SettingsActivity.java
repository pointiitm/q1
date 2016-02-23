package com.quopn.wallet.shmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

public class SettingsActivity extends ActionBarActivity {
    private Button btReset;
    private LinearLayout llTxnPwd;
    private EditText etOTP, etTxnPwd, etRepeat;
    private Button btConfirm;
    private CustomProgressDialog progress;
    private AnalysisManager mAnalysisManager;
    private TextView editResendOtp;
    private String TAG = "Quopn/SettingsActivity";
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(30000, 1000);
    private final int RESPONSE_SUCCESS_MESSAGE = 100;
    private SmsListener mSmsListener = new SmsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_SETTING);
        btReset = (Button) findViewById(R.id.btReset);
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTxnPin();
            }
        });
        llTxnPwd = (LinearLayout) findViewById(R.id.llResetTxnPwd);
        etOTP = (EditText) findViewById(R.id.etResetOTP);
        etTxnPwd = (EditText) findViewById(R.id.etResetTxnPwd);
        etRepeat = (EditText) findViewById(R.id.etResetRepeatTxnPwd);
        etRepeat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        btConfirm = (Button) findViewById(R.id.btResetConfirm);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        llTxnPwd.setVisibility(View.GONE);

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
        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        progress = new CustomProgressDialog(this);
        if(QuopnUtils.isInternetAvailable(SettingsActivity.this)) {
            ShmartFlow.getInstance().onSettingsActivityCreated(this);
        }else{
            new Dialog(SettingsActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }
        editResendOtp = (TextView) findViewById(R.id.editResendOtp);
        editResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QuopnUtils.isInternetAvailable(SettingsActivity.this)) {
                    if (editResendOtp.getText().toString().equals(getResources().getString(R.string.resendotp_txt))) {

                        ShmartFlow.getInstance().requestOTP();
                        countDownTimer.start();
                    }
                } else {
                    Dialog dialog = new Dialog(SettingsActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
                    dialog.show();
                }
            }
        });

        countDownTimer.start();
        registerReceiver(mSmsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }
    }

    public void resetTxnPin(){
        if(QuopnUtils.isInternetAvailable(SettingsActivity.this)){
            if(llTxnPwd.getVisibility() != View.VISIBLE) {
                ShmartFlow.getInstance().requestOTP();
                llTxnPwd.setVisibility(View.VISIBLE);
            }
        }else {
            new Dialog(SettingsActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }
    }

    public void submit(){
        if (QuopnUtils.isInternetAvailable(SettingsActivity.this)) {
            if (etOTP.getText().toString().isEmpty()) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.blank_otp_validation
                        , etOTP, null, 0);
                return;
            } else if (!Validations.isValidOTP(etOTP.getText().toString())) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.blank_otp_validation
                        , etOTP, null, 0);
                return;
            }

            if (etTxnPwd.getText().toString().isEmpty() ) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.set_txnpin_validation
                        , etTxnPwd, null, 0);
                return;
            } else if (!Validations.isValidTxnPIN(etTxnPwd.getText().toString())) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.validation_invalid_txn_pwd
                        , etTxnPwd, null, 0);
                return;
            }

            if (etRepeat.getText().toString().isEmpty() ) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.blank_txnpin_validation
                        , etRepeat, null, 0);
                return;
            } else if (!Validations.isValidTxnPIN(etRepeat.getText().toString())) {
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.blank_txnpin_validation
                        , etRepeat, null, 0);
                return;
            }

            if (!etTxnPwd.getText().toString().equals(etRepeat.getText().toString())) {
                etTxnPwd.setText("");
                etRepeat.setText("");
                Validations.CustomErrorMessage(SettingsActivity.this
                        , R.string.validation_repeat_txn_pwd
                        , etTxnPwd, null, 0);
                return;
            }

            ShmartFlow.getInstance().onTxnPwdResetConfirm(etTxnPwd.getText().toString()
                    , etOTP.getText().toString());
        }else {
            new Dialog(SettingsActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }
    }

    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onSettingsActivityDestroyed();
        unregisterReceiver(mSmsListener);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(QuopnUtils.isInternetAvailable(SettingsActivity.this)) {
            ShmartFlow.getInstance().onSettingsActivityRestored(this);
        }else{
            new Dialog(SettingsActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet).show();
        }
    }

    public void showMessage(final boolean isSuccess, final String message) {
        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) { SettingsActivity.this.finish(); }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String proMessage = message;
                if (isSuccess) {
                    String mobileWallets = PreferenceUtil.getInstance(SettingsActivity.this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY);
                    if (!mobileWallets.isEmpty()) {
                        if (mobileWallets.equalsIgnoreCase("2|1") || mobileWallets.equalsIgnoreCase("1|2")) {
                            proMessage = getApplicationContext().getResources().getString(R.string.txnpin_changed_successfully_citrus_udio);
                        }
                    } else {
                        Logger.e("context issue");
                    }
                }

                Dialog dialog = new Dialog(SettingsActivity.this, R.string.dialog_title, proMessage);
                dialog.setOnDismissListener(dismissListener);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }

    public void showMessage(final String title, final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new Dialog(SettingsActivity.this, R.string.dialog_title, message);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }

    public void showProgress() {
        progress.show();
    }

    public void stopProgress() {
        progress.dismiss();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            editResendOtp.setText(getResources().getString(R.string.resendotp_txt));
            underlineText();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String format = String.format(getResources().getString(R.string.waiting_for_sms), millisUntilFinished / 1000);
            editResendOtp.setText(format);
        }
    }

    private Handler messagehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS_MESSAGE:
                    etOTP.setText(msg.getData().getString("message"));
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
