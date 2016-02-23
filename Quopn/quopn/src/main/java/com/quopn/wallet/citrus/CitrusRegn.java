package com.quopn.wallet.citrus;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
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
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.LinkUserExtendedResponse;
import com.citrus.sdk.classes.LinkUserPasswordType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.citrus.CitrusLogWalletStats;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.shmart.ShmartFlow;
import com.quopn.wallet.shmart.Shmart_Tnc_Activity;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class CitrusRegn extends ActionBarActivity implements ConnectionListener {

    private Gson gson = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    private EditText mEditEmail;
    String argEmailID;
    String argMobileNo;
    //EditText editEmail;
    TextView activate_button;
    EditText editOtpfield;
    Button submit_button;
    private CitrusClient citrusClient = null;
    private CitrusConfig citrusConfig = null;
    private LinkUserExtendedResponse linkUserExtended;
    private ProfileData profileData;
    private Context mContext = this;
    private RelativeLayout relLayCitOtp;
    private RelativeLayout relLayCitRegn;
    private EditText editTransPass;
    private EditText editReEnterTransPass;
    private CustomProgressDialog progress;
    PopupMenu popupMenu;
    private TextView editResendOtp;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(30000, 1000);
    private final int RESPONSE_SUCCESS_MESSAGE = 100;
    private SmsListener mSmsListener = new SmsListener();
    String transPass;
    String reEnterTransPass;
    String otpPassword;
    AnimationDrawable Anim;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.citrusregn);
        relLayCitOtp=(RelativeLayout)findViewById(R.id.relLayCitOtp);
        relLayCitRegn=(RelativeLayout)findViewById(R.id.relLayCitRegn);
        mEditEmail = (EditText) findViewById(R.id.editEmail);

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

        ((QuopnApplication) getApplicationContext()).getAnalysisManager().send(AnalysisEvents.SCREEN_CITRUS_REGTNC);

        SearchView searchView = (SearchView) actionBarView
                .findViewById(R.id.fragment_address_search);
        searchView.setVisibility(View.INVISIBLE);
        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        citrusClient = CitrusClient.getInstance(getApplicationContext());
        progress = new CustomProgressDialog(this);
        // ankur
//        {
//            citrusClient.getPrepaidToken(new com.citrus.sdk.Callback<AccessToken>() {
//                @Override
//                public void error(CitrusError citrusError) {
//                    System.out.println("=======GetAccessToken==Errror===" + citrusError.toString());
//                }
//
//                @Override
//                public void success(AccessToken accessToken) {
//                    accessToken.setAccessToken("asd");
//                    accessToken.getJSON();
//                    System.out.println("=======GetAccessToken=====" + accessToken.getJSON());
//                }
//            });


//            citrusClient.getPrepaidToken(new com.citrus.sdk.Callback<AccessToken>() {
//                @Override
//                public void error(CitrusError citrusError) {
//                    System.out.println("=======GetAccessToken==Errror===" + citrusError.toString());
//                }
//
//                @Override
//                public void success(AccessToken accessToken) {
////                    accessToken.setAccessToken("asd");
//                    accessToken.getJSON();
//                    System.out.println("=======GetAccessToken=====" + accessToken.getJSON());
//                }
//            });
//        }


        //citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);
        citrusConfig = CitrusConfig.getInstance();
//        citrusClient.enableLog(true);
        profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
        argEmailID = profileData.getUser().getEmailid();
        argMobileNo = profileData.getUser().getMobile();

        //bindViews();
        //addAdapterToViews();

//        final ImageView imgEmailInfo = (ImageView) findViewById(R.id.imgEmailInfo);
//        imgEmailInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Validations.CustomErrorMessage(getApplicationContext(), R.string.shmart_email_info, mEditEmail, null, 2);
//            }
//        });

        activate_button = (TextView) findViewById(R.id.textActivate);
        activate_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (QuopnUtils.isInternetAvailableAndShowDialog(mContext)) {

                    linkUserExtended();

//                    relLayCitOtp.setVisibility(View.VISIBLE);
//                    relLayCitRegn.setVisibility(View.GONE);
                } else {
                    showMessage(false, getApplicationContext().getResources().getString(R.string.please_connect_to_internet));
                }
            }
        });

        TextView textTermsAndCon = (TextView) findViewById(R.id.textTerms);
        textTermsAndCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttnc = new Intent(CitrusRegn.this, Shmart_Tnc_Activity.class);
                startActivity(intenttnc);
            }
        });

        TextView textSkipRegn = (TextView) findViewById(R.id.textSkipRegn);
        textSkipRegn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView textSkipRegnActivate = (TextView) findViewById(R.id.textSkipRegnActivate);
        textSkipRegnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageView imgTransPassInfo = (ImageView) findViewById(R.id.imgTransPassInfo);
        imgTransPassInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.shmart_trans_pwd_info, editTransPass, null, 2);
            }
        });

        // otp layout
        editOtpfield = (EditText) findViewById(R.id.editEnterOTP);
        submit_button = (Button) findViewById(R.id.editSubmit);
        submit_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (QuopnUtils.isInternetAvailableAndShowDialog(mContext)) {
                    sendSubmit();
                    mEditEmail.setError(null);
                } else {
                    showMessage(false, getApplicationContext().getResources().getString(R.string.please_connect_to_internet));
                }
            }
        });

        editResendOtp = (TextView) findViewById(R.id.editResendOtp);
        editResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QuopnUtils.isInternetAvailable(CitrusRegn.this)) {
                    if (editResendOtp.getText().toString().equals(getResources().getString(R.string.resendotp_txt))) {

                        linkUserExtendedResend();
                        countDownTimer.start();
                    }
                } else {
                    Dialog dialog = new Dialog(CitrusRegn.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
                    dialog.show();
                }
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

        Button btnEmail = (Button) findViewById(R.id.btnEmailDropDown);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        mEditEmail.setText(item.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        popupMenu = new PopupMenu(this,btnEmail);
//        if(!argEmailID.isEmpty()){
//            popupMenu.getMenu().add(argEmailID);
//        }
        for (String email:getEmailListData()) {
            popupMenu.getMenu().add(email);
        }

        if(popupMenu.getMenu().size() > 0){
            mEditEmail.setText(popupMenu.getMenu().getItem(0).getTitle());
        } else {
            btnEmail.setEnabled(false);
        }

        //popupMenu.getMenu().add(getEmailListData().toString());
        //mEditEmail.setText(argEmailID);
        //ImageView img_animation = (ImageView) findViewById(R.id.imgheadIntro);
//        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
//                0.0f, 0.0f);
//        animation.setDuration(5000);
//        animation.setRepeatCount(5);
//        animation.setRepeatMode(2);
//        animation.setFillAfter(true);
//        img_animation.startAnimation(animation);

//        try {
//            BitmapDrawable frame1 = (BitmapDrawable) getResources().getDrawable(
//                    R.drawable.top_headintro_01);
//            BitmapDrawable frame2 = (BitmapDrawable) getResources().getDrawable(
//                    R.drawable.top_headintro_02);
//            BitmapDrawable frame3 = (BitmapDrawable) getResources().getDrawable(
//                    R.drawable.top_headintro_03);
//            BitmapDrawable frame4 = (BitmapDrawable) getResources().getDrawable(
//                    R.drawable.top_headintro_04);
//
//            Anim = new AnimationDrawable();
//            Anim.addFrame(frame1, 1200);
//            Anim.addFrame(frame2, 1200);
//            Anim.addFrame(frame3, 1200);
//            Anim.addFrame(frame4, 1200);
//            Anim.setOneShot(false);
//            img_animation.setBackgroundDrawable(Anim);
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//
//                public void run() {
//
//                    Anim.start();
//
//                }
//            }, 3000);
//
//        } catch (Exception e) {
//            // TODO: handle exception
//        }



        ImageView img_animation = (ImageView) findViewById(R.id.imgheadIntro);
        AnimationDrawable animation = (AnimationDrawable) img_animation.getDrawable();
        //img_animation.setBackgroundResource(R.drawable.animation_citrus);
        //animation = (AnimationDrawable) img_animation.getBackground();
        if (animation != null) { animation.start(); }
    }


    private ArrayList<String> getEmailListData() {
        ArrayList<String> accountsList = new ArrayList<String>();
        profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
        String email = profileData.getUser().getEmailid();
        if (!email.isEmpty()) {
            accountsList.add(email);
        }

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                if (!email.isEmpty()) {
                    if (!email.equalsIgnoreCase(possibleEmail)) {
                        accountsList.add(possibleEmail);
                    }
                } else {
                    accountsList.add(possibleEmail);
                }
            }
        }

        return accountsList;
    }

    public void sendSubmit() {
         otpPassword = editOtpfield.getText().toString().trim();
         transPass = editTransPass.getText().toString().trim();
         reEnterTransPass = editReEnterTransPass.getText().toString().trim();

        if (TextUtils.isEmpty(otpPassword)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_otp_validation, editOtpfield, null, 0);
            return;
        } else if (!Validations.isValidOTP(otpPassword)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.validation_invalid_otp, editOtpfield, null, 0);
            return;
        } else {
            editOtpfield.setError(null);
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
        } else if (!Validations.isValidTxnPIN(reEnterTransPass)) {
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

            }
        }
        linkUserExtendedSignIn();
    }
//    private void bindViews() {
//        //mACTVCountry = (AutoCompleteTextView) findViewById(R.id.actv_country);
//
//        mACTVLogin = (EditText) findViewById(R.id.editEmail);
//        mACTVLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus && TextUtils.isEmpty(mACTVLogin.getText().toString())) {
//                    mACTVLogin.setText(argEmailID);
//                    //System.out.println("========onFocusChanged==Email==" + argEmailID);
//                    //System.out.println("========onFocusChanged==Mobile=="+argMobileNo);
//                    System.out.println("========onFocusChanged==Email=1111=" + argEmailID);
//                } else if (!hasFocus && mACTVLogin.getText().toString().equals(argEmailID)) {
//                    mACTVLogin.setText("");
//                    System.out.println("========onFocusChanged==Email==2222=" + argEmailID);
//                }
//            }
//        });
//
//        //mACTVAddress = (AutoCompleteTextView) findViewById(R.id.actv_address);
//    }

//    private void addAdapterToViews() {
//         Account[] accounts = AccountManager.get(this).getAccounts();
//        Set<String> emailSet = new HashSet<String>();
//        for (Account account : accounts) {
//            if (EMAIL_PATTERN.matcher(account.name).matches()) {
//                emailSet.add(account.name);
//            }
//        }
//        mACTVLogin.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
//
//    }

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
                    editOtpfield.setText(msg.getData().getString("message"));
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

    private void linkUserExtended() {
//        argEmailID = "vishal.nema@gmail.com";
//        argMobileNo = "9323915104";
        argEmailID = mEditEmail.getText().toString();
        argMobileNo = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);

        if (TextUtils.isEmpty(argEmailID)) {
            Validations.CustomErrorMessage(getApplicationContext(), R.string.emailid_validation, mEditEmail, null, 0);
            return;
        } else if (!TextUtils.isEmpty(argEmailID)) {
            if (!Validations.isValidEmail(argEmailID)) {
                Validations.CustomErrorMessage(getBaseContext(), R.string.entered_emailid_validation, mEditEmail, null, 0);
                return;
            }
            mEditEmail.setError(null);
        }
        showProgress();
        citrusClient.linkUserExtended(argEmailID, argMobileNo, new Callback<LinkUserExtendedResponse>() {
            @Override
            public void success(LinkUserExtendedResponse linkUserExtendedResponse) {
                if (linkUserExtendedResponse != null) {
                    stopProgress();
                    countDownTimer.start();
                    registerReceiver(mSmsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                    linkUserExtended = linkUserExtendedResponse;

                    // TODO check email and mobile deviations
                    if (QuopnUtils.areMobileNosSame(argMobileNo, linkUserExtended.getLinkUserMobile())) {
                        if (argEmailID.equalsIgnoreCase(linkUserExtended.getLinkUserEmail())) {
                            // success
                            QuopnUtils.showDialog(mContext, R.string.dialog_title, getApplicationContext().getResources().getString(R.string.blank_otp_validation));
                        } else {
                            // msg about updating email and sucess
                            argEmailID = linkUserExtended.getLinkUserEmail();
                            String message = getApplicationContext().getResources().getString(R.string.citrus_email_updated) + "\n" + linkUserExtended.getLinkUserEmail();
                            QuopnUtils.showDialog(mContext, R.string.dialog_title, message);
                        }
                        relLayCitOtp.setVisibility(View.VISIBLE);
                        relLayCitRegn.setVisibility(View.GONE);
                    } else {
                        // show msg to change email
                        QuopnUtils.showDialog(mContext, R.string.dialog_title, getApplicationContext().getResources().getString(R.string.citrus_email_alreadyRegistered));
                    }
                }
            }

            @Override
            public void error(CitrusError error) {
                showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
            }
        });
    }

    private void linkUserExtendedResend() {
//
        citrusClient.linkUserExtended(argEmailID, argMobileNo, new Callback<LinkUserExtendedResponse>() {
            @Override
            public void success(LinkUserExtendedResponse linkUserExtendedResponse) {
                if (linkUserExtendedResponse != null) {
                    stopProgress();
                    countDownTimer.start();
                    registerReceiver(mSmsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                    linkUserExtended = linkUserExtendedResponse;
                }
            }

            @Override
            public void error(CitrusError error) {
                showMessage(false, error.getMessage());
            }
        });
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

    private void linkUserExtendedSignIn() {

        String linkUserPassword = editOtpfield.getText().toString().trim();
        if (Validations.isValidOTP(linkUserPassword)) {
            LinkUserPasswordType linkUserPasswordType = LinkUserPasswordType.Otp;
            showProgress();
            citrusClient.linkUserExtendedSignIn(linkUserExtended, linkUserPasswordType, linkUserPassword, new Callback<CitrusResponse>() {
                @Override
                public void success(CitrusResponse citrusResponse) {
                    CitrusClient.getInstance(mContext.getApplicationContext()).getPrepaidToken(new com.citrus.sdk.Callback<AccessToken>() {
                        @Override
                        public void error(CitrusError citrusError) {
                            showMessage(false, citrusError.getMessage());
                        }

                        @Override
                        public void success(AccessToken accessToken) {
                            if (mContext == null) {
                                showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
                                return;
                            } else {
                                if (!(mContext instanceof Activity)) {
                                    showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
                                    return;
                                }
                            }

                            if (!((Activity) mContext).isFinishing()) {
                                logWalletStatsForSignIn(accessToken);
                            } else {
                                showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
                                resetUI();
                            }
                        }
                    });
                    //  mListener.onShowWalletScreen();
                }

                @Override
                public void error(CitrusError error) {
                    showMessage(false, error.getMessage());
                }
            });
        } else {
            showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_dialog_otp_validation));
        }
    }

    public void showShmartWallet() {
//		showProgress();
        ShmartFlow.getInstance().setContext(this);
        ShmartFlow.getInstance().start();

    }

    private void showMessage(final boolean isSuccess, final String message) {
        if (!((Activity) mContext).isFinishing()) {
            stopProgress();
            final DialogInterface.OnDismissListener dismissListener
                    = new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (isSuccess) {
                        showProgress();
                        finish();
                        showShmartWallet();
                    }
                }
            };
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog(CitrusRegn.this, R.string.dialog_title, message);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }
            };
            runOnUiThread(runnable);
        } else { // contingency plan in case of successful signup
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            if (isSuccess) {
                showShmartWallet();
            }
        }
    }

    private void logWalletStatsForSignIn (AccessToken accessToken) {
//        String api_key = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        showProgress();
        if (QuopnUtils.isInternetAvailable(mContext)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(),PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME,QuopnApi.ParamKey.CREATEUSER );
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);

            JSONObject mergedObj = new JSONObject();
            try {
//                requestJsonObj = linkUserExtended.getJSON();

                Iterator i1 = accessToken.getJSON().keys();
                Iterator i2 = linkUserExtended.getJSON().keys();
                String tmp_key;
                while(i1.hasNext()) {
                    tmp_key = (String) i1.next();
                    mergedObj.put(tmp_key, accessToken.getJSON().get(tmp_key));
                }
                while(i2.hasNext()) {
                    tmp_key = (String) i2.next();
                    mergedObj.put(tmp_key, linkUserExtended.getJSON().get(tmp_key));
                }
                mergedObj.put(QuopnApi.ParamKey.CONSUMER_TYPE,"N"); // TODO: get from linkeduserdata
                mergedObj.put(QuopnApi.ParamKey.EMAIL,linkUserExtended.getLinkUserEmail());
                mergedObj.put(QuopnApi.ParamKey.FIRSTNAME,"");
                mergedObj.put(QuopnApi.ParamKey.LASTNAME,"");
                mergedObj.put(QuopnApi.ParamKey.UUID,linkUserExtended.getLinkUserUUID());
                mergedObj.put(QuopnApi.ParamKey.TRANSACTION_PIN,reEnterTransPass); // TODO trasaction pin
                params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
                connectionFactory.setPostParams(params);
                Logger.d(params.toString());
                connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
            } catch (JSONException e) {
                showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
            }
        } else {
            stopProgress();
            showMessage(false, getApplicationContext().getResources().getString(R.string.please_connect_to_internet));
            resetUI();
        }
    }

    /**
     * In case of failure clear all the fields, variables and show the registration screen again
     */
    private void resetUI () {
        // TODO: clear citrus logged data in case of successful login/signup
        editOtpfield.setText("");
        editTransPass.setText("");
        editReEnterTransPass.setText("");
        otpPassword = "";
        transPass = "";
        mEditEmail.setText(argEmailID);
        relLayCitOtp.setVisibility(View.GONE);
        relLayCitRegn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if (response != null) {
            if (response instanceof CitrusLogWalletStats) {
                CitrusLogWalletStats citrusLogwalletStats = (CitrusLogWalletStats) response;
                if (citrusLogwalletStats.isSuccess()) {
                    if (!citrusLogwalletStats.getDefaultWallet().isEmpty()) {
                        // setting prefs
                        String mobileWallets = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY);
                        if (mobileWallets.equalsIgnoreCase("0")) {
                            PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY, QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
                        } else if (mobileWallets.equalsIgnoreCase("1")) {
                            PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY, "1|2");
                        }
                        PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.CITRUS_EMAIL_KEY, argEmailID);
                        QuopnUtils.setDefaultWalletInAppAndPref(citrusLogwalletStats.getDefaultWallet(), getApplicationContext());
                        showMessage(true, citrusLogwalletStats.getMessage());
                    }
                } else {
                    showMessage(false, citrusLogwalletStats.getMessage());
                }
            }
        } else {
            showMessage(false, getApplicationContext().getResources().getString(R.string.citrus_registration_error));
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {
        stopProgress();
    }

    @Override
    public void myTimeout(String requestTag) {
        stopProgress();
    }

    private void showProgress() {
        if (progress != null) {
            progress.show();
        }
    }

    private void stopProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void finishAll () {
        stopProgress();
        finish();
    }
}
