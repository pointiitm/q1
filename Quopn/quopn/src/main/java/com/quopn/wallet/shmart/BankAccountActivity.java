package com.quopn.wallet.shmart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

public class BankAccountActivity extends ActionBarActivity {
    private ToggleButton tgAccountType;
    private EditText etAccountHolderName, etAccountMobile, etAccountNumber, etAccountNumberRepeat
            , etAccountIFSCCode, etAccountBranchName, etAccountTxnPwd;
    private Button btAccountAdd, btAccountConfirm;
    private RelativeLayout rlAccountConfirm;
    private CustomProgressDialog progress;
    private AnalysisManager mAnalysisManager;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btAccountAdd:

                    break;
                case R.id.btBankAccountConfirm:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_ADDNEW_BANKACCOUNT);
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
        tgAccountType = (ToggleButton) findViewById(R.id.tgAccountType);
        etAccountHolderName = (EditText) findViewById(R.id.etAccountHolderName);
//        etAccountHolderName.setMaxEms(Validations.ACCOUNTHOLDER_MAX);
        etAccountMobile = (EditText) findViewById(R.id.etAccountMobile);
        etAccountNumber = (EditText) findViewById(R.id.etAccountNumber);
//        etAccountNumber.setMaxEms(Validations.ACCOUNTNO_MAX);
        etAccountNumberRepeat = (EditText) findViewById(R.id.etAccountNumberRepeat);
//        etAccountNumberRepeat.setMaxEms(Validations.ACCOUNTNO_MAX);
        etAccountIFSCCode = (EditText) findViewById(R.id.etAccountIFSCCode);
        etAccountBranchName = (EditText) findViewById(R.id.etAccountBranchName);
        etAccountBranchName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendAddAccount();
                    return true;
                }
                return false;
            }
        });

        btAccountAdd = (Button) findViewById(R.id.btAccountAdd);
        btAccountConfirm = (Button) findViewById(R.id.btBankAccountConfirm);
        etAccountTxnPwd = (EditText) findViewById(R.id.etBankAccountTxnPwd);
        etAccountTxnPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendConfirm();
                    return true;
                }
                return false;
            }
        });

        rlAccountConfirm = (RelativeLayout) findViewById(R.id.rlBankAccountConfirm);

        progress = new CustomProgressDialog(this);

        btAccountAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddAccount();
            }
        });

        btAccountConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfirm();
            }
        });

        ShmartFlow.getInstance().onBankAccountActivityCreated(this);

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }
    }

    public void sendAddAccount(){
        if (QuopnUtils.isInternetAvailable(this)) {
            if (!Validations.isValidAccountHolder(etAccountHolderName.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_holder
                        , etAccountHolderName, null, 0);
                return;
            } else if (etAccountMobile.getText().toString().isEmpty()
                    || !Validations.isValidMobile(etAccountMobile.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_shmart_mobile
                        , etAccountMobile, null, 0);
                return;
            } else if (etAccountNumber.getText().toString().isEmpty()
                    || !Validations.isValidAccount(etAccountNumber.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_number
                        , etAccountNumber, null, 0);
                return;
            } else if (!etAccountNumber.getText().toString().equals(etAccountNumberRepeat.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_arcount_number_repeat
                        , etAccountNumberRepeat, null, 0);
                return;
            } else if (etAccountIFSCCode.getText().toString().isEmpty()
                    || !Validations.isIFSCValid(etAccountIFSCCode.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_ifsc
                        , etAccountIFSCCode, null, 0);
                return;
            } else if (etAccountBranchName.getText().toString().isEmpty()
                    || !Validations.isValidBranchName(etAccountBranchName.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_branch
                        , etAccountBranchName, null, 0);
                return;
            }


            btAccountAdd.setVisibility(View.GONE);
            rlAccountConfirm.setVisibility(View.VISIBLE);
        } else {
            Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public void sendConfirm(){
        if (QuopnUtils.isInternetAvailable(this)) {
            if (!Validations.isValidAccountHolder(etAccountHolderName.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_holder
                        , etAccountHolderName, null, 0);
                return;
            } else if (etAccountMobile.getText().toString().isEmpty()
                    || !Validations.isValidMobile(etAccountMobile.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_shmart_mobile
                        , etAccountMobile, null, 0);
                return;
            } else if (etAccountNumber.getText().toString().isEmpty()
                    || !Validations.isValidAccount(etAccountNumber.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_number
                        , etAccountNumber, null, 0);
                return;
            } else if (!etAccountNumber.getText().toString().equals(etAccountNumberRepeat.getText().toString())) {
                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_arcount_number_repeat
                        , etAccountNumberRepeat, null, 0);
                return;
            } else if (etAccountIFSCCode.getText().toString().isEmpty()
                    || !Validations.isIFSCValid(etAccountIFSCCode.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_ifsc
                        , etAccountIFSCCode, null, 0);
                return;
            } else if (etAccountBranchName.getText().toString().isEmpty()
                    || !Validations.isValidBranchName(etAccountBranchName.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_bank_account_branch
                        , etAccountBranchName, null, 0);
                return;
            }else if (etAccountTxnPwd.getText().toString().isEmpty()
                    || !Validations.isValidTxnPIN(etAccountTxnPwd.getText().toString())) {

                Validations.CustomErrorMessage(BankAccountActivity.this
                        , R.string.validation_invalid_txn_pwd
                        , etAccountTxnPwd, null, 0);
                return;
            }

            String accountType = tgAccountType.isChecked() ? "current" : "savings";

//            if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
                ShmartFlow.getInstance().onBankDetailsSubmitted(
                        etAccountHolderName.getText().toString()
                        , etAccountNumber.getText().toString()
                        , accountType
                        , etAccountIFSCCode.getText().toString()
                        , etAccountBranchName.getText().toString()//"Mumbai"
                        , etAccountMobile.getText().toString()
                        , etAccountTxnPwd.getText().toString()
                );
//            } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
//                // citrus flow
//                ShmartFlow.getInstance().onBankDetailsSubmitted(
//                        etAccountHolderName.getText().toString()
//                        , etAccountNumber.getText().toString()
//                        , accountType
//                        , etAccountIFSCCode.getText().toString()
//                        , etAccountBranchName.getText().toString()//"Mumbai"
//                        , etAccountMobile.getText().toString()
//                        , etAccountTxnPwd.getText().toString()
//                );
//            }
        } else {
            Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public void clearTextfield(){
        etAccountTxnPwd.setText("");
    }
    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onBankActivityDestroyed();
        super.onDestroy();
    }

    public void showMessage(final boolean isSuccess, final String message) {
        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) { BankAccountActivity.this.finish(); }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String title = "Success";
                if (!isSuccess) { title = "Failure"; }

                Dialog dialog = new Dialog(BankAccountActivity.this, R.string.dialog_title, message);
                dialog.setOnDismissListener(dismissListener);
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
}
