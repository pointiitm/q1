package com.quopn.wallet.shmart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.response.CitrusError;
import com.gc.materialdesign.widgets.Dialog;
import com.orhanobut.logger.Logger;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.citrus.CardPaymentFragment;
import com.quopn.wallet.citrus.WalletFragmentListener;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
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

public class LoadWalletActivity extends ActionBarActivity implements WalletFragmentListener, ConnectionListener {
    private CustomProgressDialog mCustomProgressDialog;
    private EditText etAmount;
    private Button btLoad;
    private ShmartFlow shmartFlow;
    private double balance;
    private AnalysisManager mAnalysisManager;
    WalletFragmentListener mListener;
    private FragmentManager fragmentManager = null;
    private String fragName = "pay";
    private Boolean shouldReload = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_load_wallet);
        fragmentManager = getSupportFragmentManager();
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_ADD_MONEY);
        //mCustomProgressDialog = new CustomProgressDialog(this);
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


        etAmount = (EditText) findViewById(R.id.etLoadWalletAmount);

        btLoad = (Button) findViewById(R.id.btLoadWalletLoad);
        btLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuopnUtils.hideSoftKeyboard(LoadWalletActivity.this, v);
                etAmount.clearFocus();
                addMoney();
            }
        });

        etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    QuopnUtils.hideSoftKeyboard(LoadWalletActivity.this, v);
                    etAmount.clearFocus();
                    addMoney();
                    return true;
                }
                return false;
            }
        });

        mCustomProgressDialog = new CustomProgressDialog(LoadWalletActivity.this);

        shmartFlow = ShmartFlow.getInstance();
        shmartFlow.onLoadWalletActivityCreated(this);

        mListener = (WalletFragmentListener) this;

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }
    }

    public void addMoney(){
        if (QuopnUtils.isInternetAvailable(LoadWalletActivity.this)) {
            if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

                /* Validations */
                /* Valid number */
                if (etAmount.getText().toString().isEmpty()
                        || !Validations.isValidNumberNotDoubleNotZero(etAmount.getText().toString())) {
                    Validations.CustomErrorMessage(LoadWalletActivity.this
                            , R.string.validation_shmart_amount
                            , etAmount, null, 0);
                    return;
                }
                double amount = 0.0;
                amount = Double.parseDouble(etAmount.getText().toString());
                if (balance + amount > 10000.0) {
                    Validations.CustomErrorMessage(LoadWalletActivity.this
                            , R.string.validation_excessive_amount
                            , etAmount, null, 0);
                    return;
                }

                shmartFlow.startLoadingFlow(amount);
//                     clearAll();
            }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

                if (etAmount.getText().toString().isEmpty()
                        || !Validations.isValidNumberNotDoubleNotZero(etAmount.getText().toString())) {
                    Validations.CustomErrorMessage(LoadWalletActivity.this
                            , R.string.validation_shmart_amount
                            , etAmount, null, 0);
                    return;
                }
                double amount = 0.0;
                amount = Double.parseDouble(etAmount.getText().toString());
                if (balance + amount > 10000.0) {
                    Validations.CustomErrorMessage(LoadWalletActivity.this
                            , R.string.validation_excessive_amount
                            , etAmount, null, 0);
                    return;
                }
                mListener.onPaymentTypeSelected(QuopnConstants.PaymentType.LOAD_MONEY, new Amount(Double.toString(amount)));
            }
        }else{
            new Dialog(LoadWalletActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
        }
    }

//    private void showPrompt(final QuopnConstants.PaymentType paymentType,double amount) {
////        final AlertDialog.Builder alert = new AlertDialog.Builder(LoadWalletActivity.this);
////        String message = null;
////        String positiveButtonText = null;
//
//        switch (paymentType) {
//            case LOAD_MONEY:
//                //message = "Please enter the amount to load.";
//               // positiveButtonText = "Load Money";
//               // mListener.onPaymentTypeSelected(paymentType, new Amount(Double.parseDouble(amount));
//
//                //input.clearFocus();
//                // Hide the keyboard.
//                //InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(
//                        Context.INPUT_METHOD_SERVICE);
//                //imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
//                break;
//            case CITRUS_CASH:
//                //message = "Please enter the transaction amount.";
//                //positiveButtonText = "Pay";
//                break;
//            case PG_PAYMENT:
//                //message = "Please enter the transaction amount.";
//                //positiveButtonText = "Make Payment";
//                break;
//        }
//
////        LinearLayout linearLayout = new LinearLayout(LoadWalletActivity.this);
////        linearLayout.setOrientation(LinearLayout.VERTICAL);
////
////        alert.setTitle("Transaction Amount?");
////        alert.setMessage(message);
//        // Set an EditText view to get user input
//        final EditText input = new EditText(LoadWalletActivity.this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        //alert.setView(input);
////        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
////
////            public void onClick(DialogInterface dialog, int whichButton) {
////                String value = input.getText().toString();
////
////                mListener.onPaymentTypeSelected(paymentType, new Amount(value));
////
////                input.clearFocus();
////                // Hide the keyboard.
////                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(
////                        Context.INPUT_METHOD_SERVICE);
////                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
////            }
////        });
////
////        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int whichButton) {
////                dialog.cancel();
////            }
////        });
////
////        input.requestFocus();
////        alert.show();
//    }

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
       // mCustomProgressDialog.dismiss();
    }


    @Override
    protected void onDestroy() {
        shmartFlow.onLoadWalletActivityDestroyed();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onLoadWalletActivityRestored(this);
//        clearAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldReload) {
        } else {
            clearAll();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (shouldReload) {
            onBackPressed();
            shouldReload = false;
        }
    }

    public void showMessage(final boolean isSuccess, final String message) {
        Logger.e(message);
        shouldReload = true;
//        if (fragmentManager != null) {
//            fragmentManager.popBackStack(fragName,FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) {
                    LoadWalletActivity.this.finish();
                } else {
//                    LoadWalletActivity.this.recreate();
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                if (LoadWalletActivity.this.fragmentManager != null) {
//                    LoadWalletActivity.this.fragmentManager.popBackStack(fragName,FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                }
                Dialog dialog = new Dialog(LoadWalletActivity.this, R.string.dialog_title, message);
                dialog.setOnDismissListener(dismissListener);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }

    private void clearAll() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                etAmount.setText("");
            }
        };
        runOnUiThread(runnable);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public void onPaymentComplete(TransactionResponse transactionResponse) {
        Logger.d(transactionResponse.toString());
        if (transactionResponse.getBalanceAmount() != null) {
            ShmartFlow.getInstance().setBalance(transactionResponse.getBalanceAmount().getValueAsDouble());
        }

        if (QuopnUtils.isInternetAvailable(getApplicationContext())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME,QuopnApi.ParamKey.LOADWALLET );
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_P);
            JSONObject mergedObj = new JSONObject();
            try {
                mergedObj.put(QuopnApi.CITRUS_PARAMS.MERCHANTTRANSACTIONID,"");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.CUSTOMER,"");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.MERCHANT,"");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.PLATFORM,QuopnApi.EWalletDefault.PLATFORM);
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TRANSACTION_ID,QuopnUtils.sendNonNullValueForString(transactionResponse.getTransactionDetails().getTransactionId()));
                JSONObject log_Amount = new JSONObject();
                {
                    log_Amount.put(QuopnApi.CITRUS_PARAMS.VALUE,QuopnUtils.sendNonNullValueForString(transactionResponse.getTransactionAmount().getValue()));
                    log_Amount.put(QuopnApi.CITRUS_PARAMS.CURRENCY,QuopnApi.CITRUS_PARAMS.INR);
                }
                mergedObj.put(QuopnApi.CITRUS_PARAMS.AMOUNT,log_Amount);
                mergedObj.put(QuopnApi.CITRUS_PARAMS.DESCRIPTION, QuopnApi.CITRUS_PARAMS.LOAD_MONEY);
                mergedObj.put(QuopnApi.CITRUS_PARAMS.SIGNATURE,"");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.MERCHANTACCESSKEY,"");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.PGRESPCODE,"0");
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TRANSACTIONDATETIME,QuopnUtils.sendNonNullValueForString(transactionResponse.getTransactionDetails().getTransactionDateTime()));
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TXMSG,QuopnUtils.sendNonNullValueForString(transactionResponse.getMessage()));
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TRANSACTIONSTATUS,QuopnUtils.sendNonNullValueForString(transactionResponse.getTransactionStatus().name()));
                params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                ConnectionFactory connectionFactory = new ConnectionFactory(LoadWalletActivity.this,LoadWalletActivity.this);
                connectionFactory.setPostParams(params);
                connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);

            } catch (JSONException e) {

            }
        }
        showMessage(true, QuopnUtils.sendNonNullValueForString(transactionResponse.getMessage()));
    }

    public void error(CitrusError error) {
        Logger.e(error.getMessage());
        if (QuopnUtils.isInternetAvailable(getApplicationContext())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.EWalletRequestParam.WALLET_ID.getName(), PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
            params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(), QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID);
            params.put(QuopnApi.ParamKey.APINAME,QuopnApi.ParamKey.LOADWALLET );
            params.put(QuopnApi.ParamKey.APITYPE, QuopnApi.ParamKey.APITYPE_D);
            JSONObject mergedObj = new JSONObject();
            try {
                JSONObject log_Amount = new JSONObject();
                {
                    log_Amount.put(QuopnApi.CITRUS_PARAMS.VALUE,QuopnUtils.sendNonNullValueForString(etAmount.getText().toString()));
                    log_Amount.put(QuopnApi.CITRUS_PARAMS.CURRENCY,QuopnApi.CITRUS_PARAMS.INR);
                }
                mergedObj.put(QuopnApi.CITRUS_PARAMS.AMOUNT,log_Amount);
                mergedObj.put(QuopnApi.CITRUS_PARAMS.DESCRIPTION, QuopnApi.CITRUS_PARAMS.LOAD_MONEY);
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TXMSG,QuopnUtils.sendNonNullValueForString(error.getMessage()));
                mergedObj.put(QuopnApi.CITRUS_PARAMS.TRANSACTIONSTATUS,QuopnUtils.sendNonNullValueForString(error.getStatus().name()));
                params.put(QuopnApi.ParamKey.REQUESTPARAMS, mergedObj.toString());
                params.put(QuopnApi.ParamKey.RESPONSEPARAMS, "");
                ConnectionFactory connectionFactory = new ConnectionFactory(LoadWalletActivity.this,LoadWalletActivity.this);
                connectionFactory.setPostParams(params);
                connectionFactory.createConnection(QuopnConstants.QUOPN_CITRUS_LOGWALLETSTATS);
            } catch (JSONException e) {

            }
        }
        String message = error.getMessage();
        if (message == null || message.length()<1) {
            message = getApplicationContext().getString(R.string.citrus_loadwallet_failure);
        }
        showMessage(false, message);
    }

    @Override
    public void onPaymentTypeSelected(QuopnConstants.PaymentType paymentType, Amount amount) {
            //showCustomProgress();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, CardPaymentFragment.newInstance(paymentType, amount));

        fragmentTransaction.addToBackStack(fragName);
        fragmentTransaction.commit();
    }

    @Override
    public void onPaymentTypeSelected(QuopnConstants.DPRequestType dpRequestType, Amount originalAmount, String couponCode, Amount alteredAmount) {

    }

    @Override
    public void onCashoutSelected(CashoutInfo cashoutInfo) {

    }


    @Override
    public void onResponse(int responseResult, Response response) {
        if (response != null) {
            if (response instanceof CitrusLogWalletStats) {
                CitrusLogWalletStats citrusLogwalletStats = (CitrusLogWalletStats) response;
                Logger.d(citrusLogwalletStats.getJSON().toString());
            } else {
                Logger.d("response not null for unknown request");
            }
        } else {
            Logger.d("response null for ");
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    @Override
    public void myTimeout(String requestTag) {

    }
}
