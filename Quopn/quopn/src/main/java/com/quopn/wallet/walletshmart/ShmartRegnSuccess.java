package com.quopn.wallet.walletshmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quopn.wallet.R;
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
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.views.CustomProgressDialog;

/**
 * Created by Administrator on 21-Sep-15.
 */
public class ShmartRegnSuccess extends ActionBarActivity implements ConnectionListener {

    private ConnectionFactory mConnectionFactory;
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private String TAG = "Quopn/ShmartOtp";
    private final int RESPONSE_SUCCESS_MESSAGE=100;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(5000, 1000);
    private CustomProgressDialog progress;
    private boolean verifySent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_regn_success);

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
        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        progress = new CustomProgressDialog(this);

        final ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
        final User user = profileData.getUser();

        final Intent intent = getIntent();

        TextView textViewMsg = (TextView) findViewById(R.id.textshmart_regn_success_msg);
        textViewMsg.setText(intent.getStringExtra(QuopnConstants.INTENT_KEYS.shmart_regn_success_msg));

        countDownTimer.start();
    }




    @Override
    public void onResponse(int responseResult, Response response) {
        if (response instanceof ShmartGenerateOTPData) {
            ShmartGenerateOTPData shmartCreateUserData = (ShmartGenerateOTPData) response;
            String errorCode = shmartCreateUserData.getError_code();
            String message = shmartCreateUserData.getMessage();

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (response instanceof ShmartRequestOTPData) {
            stopProgress();

            ShmartRequestOTPData shmartRequestOTPData = (ShmartRequestOTPData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (response instanceof ShmartVerifyOTPData) {
            stopProgress();

            ShmartVerifyOTPData shmartRequestOTPData = (ShmartVerifyOTPData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.INCORRECT_OTP)) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else{
                Log.d(TAG, "error code - "+ errorCode +", error_message: "+message);
            }
        } else if (response instanceof ShmartVerifyOTPAndChangeTransPwdData) {
            stopProgress();

            ShmartVerifyOTPAndChangeTransPwdData shmartRequestOTPData = (ShmartVerifyOTPAndChangeTransPwdData) response;
            String errorCode = shmartRequestOTPData.getError_code();
            String message = shmartRequestOTPData.getMessage();

            if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {
                if (verifySent) {
                    finish();
                }
            } else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "error code - "+ errorCode +", error_message: "+message);
            }
        }

    }

    private void showShmartWallet() {
        ShmartFlow.getInstance().setContext(this);
        ShmartFlow.getInstance().start();
        finish();
    }

    public void showError(String argMsg){
        Toast.makeText(this, argMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout(ConnectRequest request) {
    }

    @Override
    public void myTimeout(String requestTag) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    private void showProgress() {
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    private void stopProgress() {
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }


}
