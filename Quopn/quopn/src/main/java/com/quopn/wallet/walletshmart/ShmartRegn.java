package com.quopn.wallet.walletshmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.ShmartCreateUserData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.shmart.ShmartFlow;
import com.quopn.wallet.shmart.Shmart_Tnc_Activity;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 21-Sep-15.
 */
public class ShmartRegn extends ActionBarActivity implements ConnectionListener {

    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private SlidingUpPanelLayout mSlidinguppanellayout;
    private CustomProgressDialog progress;

    private ConnectionFactory mConnectionFactory;
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private AnalysisManager mAnalysisManager;
    private EditText editName;
    private EditText editEmail;
    private EditText editNumber;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_regn);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_REGISTRATION);
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
                mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_DIDNOT_REGISTER);
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


        mPager = (ViewPager) findViewById(R.id.pager);
        final ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position < (NUM_PAGES-1)){
                    mPagerAdapter.changeButtonText(getResources().getString(R.string.skip_text));
                } else if(position == (NUM_PAGES-1)){
                    mPagerAdapter.changeButtonText(getResources().getString(R.string.register_now));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSlidinguppanellayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        final ImageView btn_dots = (ImageView) findViewById(R.id.termscodition_btn);
        btn_dots.setVisibility(View.GONE);

        mSlidinguppanellayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(slideOffset == 0) {
                    showPoweredByLayout();
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                btn_dots.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                btn_dots.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {}

            @Override
            public void onPanelHidden(View panel) {}
        });

        progress = new CustomProgressDialog(this);

        btn_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("ShmartRegn ===", "Onclick tried");
                if (mSlidinguppanellayout.isPanelExpanded()) {
                    mSlidinguppanellayout.collapsePanel();
                    showPoweredByLayout();
                } else {
                    mSlidinguppanellayout.expandPanel();
                    hidePoweredByLayout();
                }
            }
        });


        ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
        user = profileData.getUser();

        editName = (EditText) findViewById(R.id.editName);
        if (!TextUtils.isEmpty(user.getUsername())) {
            editName.setText(user.getUsername());
            editName.setSelection(editName.getText().length());
        }
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setError(null);
            }
        });
        editName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        editEmail = (EditText) findViewById(R.id.editEmail);
        if (!TextUtils.isEmpty(user.getEmailid())) {
            editEmail.setText(user.getEmailid());
        }
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmail.setError(null);
            }
        });
        editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendRegistrationRequest();
                    return true;
                }
                return false;
            }
        });

        final ImageView imgEmailInfo = (ImageView) findViewById(R.id.imgEmailInfo);
        imgEmailInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.shmart_email_info, editEmail, null, 2);
            }
        });

        editNumber = (EditText) findViewById(R.id.editMobileNumber);
        editNumber.setText(profileData.getUser().getMobile());

        TextView textTermsAndCon = (TextView) findViewById(R.id.textTerms);
        textTermsAndCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttnc = new Intent(ShmartRegn.this, Shmart_Tnc_Activity.class);
                startActivity(intenttnc);
            }
        });

        TextView textSubmit = (TextView) findViewById(R.id.textSubmit);
        textSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistrationRequest();
            }
        });

        TextView textSkipRegn = (TextView) findViewById(R.id.textSkipRegn);
        textSkipRegn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShmartRegn.this, R.string.skip_regn_msg, Toast.LENGTH_LONG);
                finish();
            }
        });
        hidePoweredByLayout();
        mSlidinguppanellayout.expandPanel();
    }

    public void sendRegistrationRequest(){
        if (QuopnUtils.isInternetAvailableAndShowDialog(ShmartRegn.this)) {
            String name = editName.getText().toString().trim();
            String number = editNumber.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_name_validation, editName, null, 0);
                return;
            } else {
                if (Validations.isValidName(name)) {
                    editName.setError(null);
                } else {
                    Validations.CustomErrorMessage(getApplicationContext(), R.string.name_validation, editName, null, 0);
                    return;
                }
            }
            if (TextUtils.isEmpty(email)) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.emailid_validation, editEmail, null, 0);
                return;
            } else if (!TextUtils.isEmpty(email)) {
                if (!Validations.isValidEmail(email)) {
                    Validations.CustomErrorMessage(getBaseContext(), R.string.entered_emailid_validation, editEmail, null, 0);
                    return;
                }
                editEmail.setError(null);
            }
            if (TextUtils.isEmpty(number)) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.emailid_validation, editNumber, null, 0);
                return;
            } else if (!TextUtils.isEmpty(number)) {
                editNumber.setError(null);
            }

            sendCreateUser(user.getApi_key(), user.getWalletid(), editName.getText().toString(), editEmail.getText().toString());
        }
    }

    public void hidePoweredByLayout(){
        LinearLayout linLayPoweredBy = (LinearLayout)findViewById(R.id.textPoweredBy);
        linLayPoweredBy.setVisibility(View.GONE);
    }

    public void showPoweredByLayout(){
        LinearLayout linLayPoweredBy = (LinearLayout)findViewById(R.id.textPoweredBy);
        linLayPoweredBy.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)linLayPoweredBy.getLayoutParams();
        params.setMargins(0, 0, 0, 70);
        linLayPoweredBy.setLayoutParams(params);
    }

    public void sendCreateUser(String argAuthKey, String argWalletId, String argName, String argEmail) {
        Map<String, String> params;

        showProgress();

        params = new HashMap<String, String>();
//            params.put("Authorization", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
        params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
        params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");
        params.put(QuopnConstants.CONN_PARAMS.name, argName);
        params.put(QuopnConstants.CONN_PARAMS.email_id, argEmail);
        mConnectionFactory = new ConnectionFactory(this, this);
        mConnectionFactory.setPostParams(params);
        mConnectionFactory.createConnection(QuopnConstants.SHMART_CREATE_USER);
    }


    @Override
    public void onBackPressed() {

        if (mSlidinguppanellayout.isPanelExpanded()) {
            mSlidinguppanellayout.collapsePanel();
            showPoweredByLayout();
        } else
            super.onBackPressed();
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        stopProgress();
        if (response instanceof ShmartCreateUserData) {
            ShmartCreateUserData shmartCreateUserData = (ShmartCreateUserData) response;
            String errorCode = shmartCreateUserData.getError_code();
            String consumerId = shmartCreateUserData.getConsumer_id();
            String message = shmartCreateUserData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
                if (TextUtils.isEmpty(consumerId)) {//New Consumer

                } else {//Consumer already registered
                    showOTPScreen(false);
                    return;
                }
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_NOT_EXIST)) {//11

            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.EMAIL_EXISTS)) {//14
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.NAME_INVALID)) {//157
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.OTP_ACTIVATION_PENDING)) {//100
                if (TextUtils.isEmpty(consumerId)) {//New Consumer

                } else {//Consumer already registered
                    Intent intent = new Intent(ShmartRegn.this, ShmartOtp.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.REG_CUST_FAIL)) {//266
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.REG_RECORD_QUERY_FAIL)) {//274
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.UPDATE_CUST_FAIL)) {//304
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.MOBILE_NUM_EXISTS)) {//101
                showOTPScreen(true);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.INTERNAL_SERVER_ERROR)) {//102
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.OTP_ALREADY_VALIDATED)) {//103
                showShmartWallet();
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.ACTIVATION_PENDING)) {//106
                showOTPScreen(false);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.MOBILE_NUMBER_INVALID_1)) {//114
                showError(message);
            } else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.MOBILE_NUMBER_INVALID_2)) {//313
                showError(message);
            }
        }
    }

    public void showOTPScreen(boolean isActive) {
        Intent shmartRegn = new Intent(this, ShmartOtp.class);
        if (isActive) {
            shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, true);
        } else {
            shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, true);
        }
        startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
        finish();
    }

    private void showShmartWallet() {
        ShmartFlow.getInstance().setContext(this);
        ShmartFlow.getInstance().start();
    }

    public void showError(String argMsg) {
        Toast.makeText(this, argMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    @Override
    public void myTimeout(String requestTag) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePageFragment imgFragment;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            imgFragment = new ScreenSlidePageFragment();
            imgFragment.setSlider(mSlidinguppanellayout);
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            imgFragment.setArguments(bundle);
            return imgFragment;
        }

        public void changeButtonText(String argMsg){
            imgFragment.changeButtonText(argMsg);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
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
}
