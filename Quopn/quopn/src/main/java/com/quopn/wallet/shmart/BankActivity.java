package com.quopn.wallet.shmart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.PaymentResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.citrus.WalletFragmentListener;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.ShmartVerifyOTPData;
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
import java.util.TreeMap;

public class BankActivity extends ActionBarActivity implements WalletFragmentListener, ConnectionListener {
    private RelativeLayout rlBankBalance;
    private TextView tvBankBalance, tvBankPayee, tvBankName, tvBankAccount;
    private LinearLayout llBankAddAccount, llBankAccount, llBankConfirm, llEssentials,lleditResendOtp;
    private TextView btBankSend, btBankConfirm, tvDeleting;
    private EditText etBankAmount, etBankOTP;
    private ImageView ivDelete;
    private String acName, acAccount, acIfsc;
    private double amount;
    private double balance;
    private boolean hasBeneficiary;
    private boolean isSending;
    private CustomProgressDialog progress;
    private AnalysisManager mAnalysisManager;
    WalletFragmentListener mListener;
    private CitrusClient citrusClient = null;
    private Context mContext = this;
    //private CountDownTimer mCountDownTimer;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(30000, 1000);
    private TextView editResendOtp;
    private final int RESPONSE_SUCCESS_MESSAGE = 100;
    //private final long mStartTime = 30 * 1000;
    //private final long mInterval = 1 * 1000;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llBankAddAccount:
                    //showTransferToBank();
                    if (QuopnUtils.isInternetAvailable(BankActivity.this)) {
                    if (hasBeneficiary) {
                        final Dialog dialog = new Dialog(BankActivity.this, R.string.dialog_title
                                , getString(R.string.prompt_existing_beneficiary));

                        View.OnClickListener yesListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShmartFlow.getInstance().onAddAccountChosen();
                            }
                        };

                        View.OnClickListener noListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        };

                        dialog.addOkButton("Yes");
                        dialog.addCancelButton("No");

                        dialog.setOnAcceptButtonClickListener(yesListener);
                        dialog.setOnCancelButtonClickListener(noListener);

                        dialog.show();
                    } else {
                        ShmartFlow.getInstance().onAddAccountChosen();
                    }}else{
                        new Dialog(BankActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
                    }
                    break;
                case R.id.btBankSend: {
                      if(QuopnUtils.isInternetAvailable(BankActivity.this)) {
                        if (etBankAmount.getText().toString().isEmpty()
                                || !Validations.isValidNumberNotDoubleNotZero(etBankAmount.getText().toString())) {

                            Validations.CustomErrorMessage(BankActivity.this
                                    , R.string.validation_shmart_amount
                                    , etBankAmount, null, 0);
                            return;
                        }

                        amount = Double.parseDouble(etBankAmount.getText().toString());
                        if (amount > balance) {
                            Validations.CustomErrorMessage(BankActivity.this
                                    , R.string.validation_insufficient_balance
                                    , etBankAmount, null, 0);
                            return;
                        } else if (amount > 5000.0) {
                            Validations.CustomErrorMessage(BankActivity.this
                                    , R.string.validation_shmart_amount_bank
                                    , etBankAmount, null, 0);
                            return;
                        }
                        view.setVisibility(View.GONE);
                        llBankConfirm.setVisibility(View.VISIBLE);
                        lleditResendOtp.setVisibility(View.VISIBLE);
                        isSending = true;

                        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
                            ShmartFlow.getInstance().generateShmartOTP();
                        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
                            ShmartFlow.getInstance().requestOTP();
                            countDownTimer.start();
                            System.out.println("===========SendMoney===Coundowntimer=====");
                        }
                    }else{
                        new Dialog(BankActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
                    }
                }

                    break;
                case R.id.btBankConfirm: {
                    if(QuopnUtils.isInternetAvailable(BankActivity.this)) {
                        if (isSending) {

                            if (etBankAmount.getText().toString().isEmpty()
                                    || !Validations.isValidNumberNotDoubleNotZero(etBankAmount.getText().toString())) {
                                Validations.CustomErrorMessage(BankActivity.this
                                        , R.string.validation_shmart_amount
                                        , etBankAmount, null, 0);
                                return;
                            }

                            amount = Double.parseDouble(etBankAmount.getText().toString());
                            if (amount > balance) {
                                Validations.CustomErrorMessage(BankActivity.this
                                        , R.string.validation_insufficient_balance
                                        , etBankAmount, null, 0);
                                return;
                            } else if (amount > 5000.0) {
                                Validations.CustomErrorMessage(BankActivity.this
                                        , R.string.validation_shmart_amount_bank
                                        , etBankAmount, null, 0);
                                return;
                            }

                            if (etBankOTP.getText().length() != 6) {
                                Validations.CustomErrorMessage(BankActivity.this
                                        , R.string.validation_invalid_otp
                                        , etBankOTP, null, 0);
                                return;
                            }

                            if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
                                ShmartFlow.getInstance().onBankTransferChosen(amount
                                        , etBankOTP.getText().toString());
                            } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
                                showProgress();
                                ConnectionFactory factory
                                        = new ConnectionFactory(BankActivity.this, BankActivity.this);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
                                        , PreferenceUtil.getInstance(getApplicationContext()).getPreference(
                                        PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
                                map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
                                        , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
                                map.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(),"11111" );
                                map.put(QuopnApi.EWalletRequestParam.OTP.getName(),etBankOTP.getText().toString());
                                factory.setPostParams(map);
                                factory.createConnection(QuopnConstants.SHMART_VERIFY_OTP);
                            }
                        } else {

                            // delete beneficiary
                            if (etBankOTP.getText().length() != 4) {
                                Validations.CustomErrorMessage(BankActivity.this
                                        , R.string.validation_invalid_txn_pwd
                                        , etBankOTP, null, 0);
                                return;
                            }

//                            if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
                                ShmartFlow.getInstance().onDeleteBeneficiaryChosen(
                                        etBankOTP.getText().toString());
//                            } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
//                                showProgress();
//                                ConnectionFactory factory
//                                        = new ConnectionFactory(BankActivity.this, BankActivity.this);
//                                Map<String, String> map = new HashMap<String, String>();
//                                map.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName()
//                                        , PreferenceUtil.getInstance(getApplicationContext()).getPreference(
//                                        PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
//                                map.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName()
//                                        , QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
//                                map.put(QuopnApi.EWalletRequestParam.TXN_PWD.getName(),etBankOTP.getText().toString());
//                                factory.setPostParams(map);
//                                factory.createConnection(QuopnConstants.QUOPN_VALIDATE_TXN_PIN);
//                            }
                        }
                    }else{
                        new Dialog(BankActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
                    }
                }
                    break;

                case R.id.ivBankDelete:
                    view.setVisibility(View.GONE);
                    llEssentials.setVisibility(View.GONE);
                    tvBankName.setVisibility(View.GONE);
                    tvDeleting.setVisibility(View.VISIBLE);
                    llBankConfirm.setVisibility(View.VISIBLE);
                    etBankOTP.setText("");
                    etBankOTP.setHint(getResources().getString(R.string.enter_trans_pass));
                    InputFilter[] filters = new InputFilter[]{ new InputFilter.LengthFilter(4) };
                    etBankOTP.setFilters(filters);
                    isSending = false;

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_TRANSFER_TO_BANK);
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

        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);

        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
        rlBankBalance = (RelativeLayout) findViewById(R.id.rlBankBalance);
        tvBankBalance = (TextView) findViewById(R.id.tvBankBalance);
        if(getIntent().hasExtra(QuopnConstants.INTENT_KEYS.shmart_balance)){
            balance = getIntent().getDoubleExtra(QuopnConstants.INTENT_KEYS.shmart_balance, 0);
            tvBankBalance.setText(""+balance);
        }

        llBankAddAccount = (LinearLayout) findViewById(R.id.llBankAddAccount);
        llBankAccount = (LinearLayout) findViewById(R.id.llBankAccount);
        llEssentials = (LinearLayout) findViewById(R.id.llBankEssentials);
        tvDeleting = (TextView) findViewById(R.id.tvBankDeleting);
        tvBankPayee = (TextView) findViewById(R.id.tvBankPayee);
        tvBankName = (TextView) findViewById(R.id.tvBankName);
        tvBankAccount = (TextView) findViewById(R.id.tvBankAccount);
        btBankSend = (TextView) findViewById(R.id.btBankSend);
        llBankConfirm = (LinearLayout) findViewById(R.id.llBankConfirm);
        btBankConfirm = (TextView) findViewById(R.id.btBankConfirm);
        etBankOTP = (EditText) findViewById(R.id.etBankOTP);
        etBankAmount = (EditText) findViewById(R.id.etBankTxnPwd);
        ivDelete = (ImageView) findViewById(R.id.ivBankDelete);
        lleditResendOtp = (LinearLayout) findViewById(R.id.lleditResendOtp);
        llBankAddAccount.setOnClickListener(clickListener);
        btBankSend.setOnClickListener(clickListener);
        btBankConfirm.setOnClickListener(clickListener);
        ivDelete.setOnClickListener(clickListener);

        progress = new CustomProgressDialog(this);

        if (QuopnUtils.isInternetAvailable(this)) {
            ShmartFlow.getInstance().onBankActivityCreated(this);
        } else {
            Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
        }

        mListener = (WalletFragmentListener) this;
        citrusClient = CitrusClient.getInstance(mContext);

        editResendOtp = (TextView) findViewById(R.id.editResendOtp);
        editResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QuopnUtils.isInternetAvailable(BankActivity.this)) {
                    if (editResendOtp.getText().toString().equals(getResources().getString(R.string.resendotp_txt))) {

                        ShmartFlow.getInstance().requestOTP();
                        countDownTimer.start();
                    }
                } else {
                    Dialog dialog = new Dialog(BankActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
                    dialog.show();
                }
            }
        });

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);


        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ShmartFlow.getInstance().onBankActivityRestored(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onBankActivityRestored(this);

    }

    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onBankActivityDestroyed();
        super.onDestroy();
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
                    etBankOTP.setText(msg.getData().getString("message"));
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

    public void showBalance(double balance) {
        this.balance = balance;
        tvBankBalance.setText("" + balance);
    }

    public void showAddBeneficiary() {
        llBankAccount.setVisibility(View.GONE);
        llBankAddAccount.setVisibility(View.VISIBLE);
    }

    public void showMessage(final boolean isSuccess, final String message) {
        stopProgress();
        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) { BankActivity.this.finish(); }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                String title = "Success";
//                if (!isSuccess) { title = "Failure"; }

                Dialog dialog = new Dialog(BankActivity.this, R.string.dialog_title, message);
                dialog.setOnDismissListener(dismissListener);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }

    public void showBeneficiary(String name, String account, String bank) {
        acName = name;
        acAccount = account;
        acIfsc = bank;
        hasBeneficiary = true;
        tvBankPayee.setText(name);
        tvBankAccount.setText(account);
        tvBankName.setText(bank);
        llBankAccount.setVisibility(View.VISIBLE);
    }

    public void removeBeneficiary() {
        hasBeneficiary = false;
        tvBankPayee.setText("");
        tvBankAccount.setText("");
        tvBankName.setText("");
        llBankAccount.setVisibility(View.GONE);
    }

    public void showProgress() {
        if (progress != null) {
            progress.show();
        }
    }

    public void stopProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }



    public void restoreViewState() {
        ivDelete.setVisibility(View.VISIBLE);
        llEssentials.setVisibility(View.VISIBLE);
        tvBankName.setVisibility(View.VISIBLE);
        tvDeleting.setVisibility(View.GONE);
        llBankConfirm.setVisibility(View.GONE);
        etBankOTP.setText("");// ankur
        etBankAmount.setText("");
        etBankOTP.setHint("ENTER OTP");
        InputFilter[] filters = new InputFilter[]{ new InputFilter.LengthFilter(6) };
        etBankOTP.setFilters(filters);
    }

    public void resendOTP(){

    }

    @Override
    public void onPaymentComplete(TransactionResponse transactionResponse) {

    }

    @Override
    public void onPaymentTypeSelected(QuopnConstants.PaymentType paymentType, Amount amount) {

    }

    @Override
    public void onPaymentTypeSelected(QuopnConstants.DPRequestType dpRequestType, Amount originalAmount, String couponCode, Amount alteredAmount) {

    }

    @Override
    public void onCashoutSelected(CashoutInfo cashoutInfo) {

//        citrusClient.saveCashoutInfo(cashoutInfo, new Callback<CitrusResponse>() {
//            @Override
//            public void success(CitrusResponse citrusResponse) {
//                QuopnConstants.showToast(getApplicationContext(), citrusResponse.getMessage());
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                QuopnConstants.showToast(getApplicationContext(), error.getMessage());
//            }
//        });

        citrusClient.cashout(cashoutInfo, new Callback<PaymentResponse>() {
            @Override
            public void success(PaymentResponse paymentResponse) {
                ShmartFlow.getInstance().setBalance(paymentResponse.getBalanceAmount().getValueAsDouble());
                Map<String, String> params = new TreeMap<String, String>();
                params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
                params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), String.valueOf(QuopnConstants.WalletType.CITRUS.ordinal()));
                params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.TRANSFERTOBANK);
                params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);

                JSONObject mergedObj = new JSONObject();
                try {
                    mergedObj.put(QuopnApi.EWalletRequestParam.BENEFICIARY.getName(), ShmartFlow.getInstance().getBeneficiaryCode_citrus());
                    mergedObj.put(QuopnApi.EWalletRequestParam.AMOUNT.getName(), "" + amount);
                    mergedObj.put("transaction_id", QuopnUtils.sendNonNullValueForString(paymentResponse.getTransactionId()));
                    mergedObj.put("txMsg", QuopnUtils.sendNonNullValueForString(paymentResponse.getMessage()));
                    mergedObj.put("platform", "android");

                    params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                    params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");

                    ConnectionFactory connectionFactory
                            = new ConnectionFactory(BankActivity.this, BankActivity.this);
                    connectionFactory.setPostParams(params);
                    Logger.d(params.toString());
                    connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
                } catch (JSONException e) {
                }
                if (mContext != null && (mContext instanceof Activity)) {
                    restoreViewState();

                    if (!((Activity) mContext).isFinishing()) {
                        showMessage(true, getApplicationContext().getString(R.string.citrus_tranfer_to_bank_success));
                    } else {
                        QuopnConstants.showToast(getApplicationContext(), getApplicationContext().getString(R.string.citrus_tranfer_to_bank_success));
                    }
                }
            }

            @Override
            public void error(CitrusError error) {
                if (mContext != null && (mContext instanceof Activity)) {
                    if (!((Activity) mContext).isFinishing()) {
                        showMessage(true, getApplicationContext().getResources().getString(R.string.citrus_tiny_error));
                    } else {
                        QuopnConstants.showToast(getApplicationContext(), error.getMessage());
                        return;
                    }
                }
                Map<String, String> params = new TreeMap<String, String>();
                params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
                params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), String.valueOf(QuopnConstants.WalletType.CITRUS.ordinal()));
                params.put(QuopnApi.ParamKey.APINAME, QuopnApi.ParamKey.TRANSFERTOBANK);
                params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_D);

                JSONObject mergedObj = new JSONObject();
                try {
                    mergedObj.put(QuopnApi.EWalletRequestParam.BENEFICIARY.getName(), ShmartFlow.getInstance().getBeneficiaryCode_citrus());
                    mergedObj.put(QuopnApi.EWalletRequestParam.AMOUNT.getName(), "" + amount);
                    mergedObj.put("transaction_id", "");
                    if (error != null) {
                        mergedObj.put(QuopnApi.CITRUS_PARAMS.ERRORMESSAGE, error.getMessage());
                        if (error.getStatus() != null) {
                            mergedObj.put(QuopnApi.CITRUS_PARAMS.ERRORSTATUS, error.getStatus().name());
                        }
                    }
                    mergedObj.put("txMsg", error.getMessage());
                    mergedObj.put("platform", "android");

                    params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                    params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");

                    ConnectionFactory connectionFactory
                            = new ConnectionFactory(BankActivity.this, BankActivity.this);
                    connectionFactory.setPostParams(params);
                    connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
                } catch (JSONException e) {
                }
            }
        });
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if (response != null) {
            if (response instanceof ValidateTxnPinResponse) {
                if (((ValidateTxnPinResponse) response).isSuccess()) {
                    // call citrus

                } else {
                    stopProgress();
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
            } else if (response instanceof ShmartVerifyOTPData) {
                ShmartVerifyOTPData shmartVerifyOTPData = (ShmartVerifyOTPData) response;

                if(!shmartVerifyOTPData.getError_code().equals("000")){
                    stopProgress();
                    showMessage(false, shmartVerifyOTPData.getMessage());
                }else{
                    CashoutInfo cashoutInfo = new CashoutInfo(new Amount(Double.toString(amount)), acAccount, acName, acIfsc);
                    mListener.onCashoutSelected(cashoutInfo);
                    //showMessage(true, shmartVerifyOTPData.getMessage());

                }
            } else if (response instanceof CitrusLogWalletStats) {
                CitrusLogWalletStats citrusLogwalletStats = (CitrusLogWalletStats) response;
                Logger.d(citrusLogwalletStats.toString());
            }
            else if (response instanceof NetworkError) {
                NetworkError networkError = (NetworkError) response;
                if (networkError.getError().networkResponse != null) {
                    NetworkResponse networkResponse = networkError.getError().networkResponse;
                    if (networkResponse.statusCode == 401) {
                        // TODO: check session invalidation
                    }
                }
            }
        } else {

        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    @Override
    public void myTimeout(String requestTag) {

    }
}
