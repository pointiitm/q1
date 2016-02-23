package com.quopn.wallet.shmart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

public class UseQuopnActivity extends ActionBarActivity {
    private Button btAlwaysUse;
    private Button btUseForNext;
    private LinearLayout rlTxnPwd;
    private Button btConfirm;
    private EditText etPrefTxnPwd;
    private ImageView ivForNext;
    private ImageView ivForAlwaysUse;
//    private ShmartFlow.EWalletUsagePref pref = ShmartFlow.EWalletUsagePref.NEVER;
    private ShmartFlow.EWalletUsagePref tempPref = ShmartFlow.EWalletUsagePref.NEVER;
    private ShmartFlow.EWalletUsagePref originalPref = ShmartFlow.EWalletUsagePref.NEVER;
    private CustomProgressDialog progress;
    private AnalysisManager mAnalysisManager;
    private QuopnConstants.WalletType originalDefWallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_quopn);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SHOPAT_STORES);
        btAlwaysUse = (Button) findViewById(R.id.btPrefAlwaysUse);
        ivForNext = (ImageView) findViewById(R.id.tick_logo_fornext);
        btUseForNext = (Button) findViewById(R.id.btPrefUseForNext);
        ivForAlwaysUse = (ImageView) findViewById(R.id.tick_logo_foralways);
        rlTxnPwd = (LinearLayout) findViewById(R.id.rlPrefTxnPwd);
        btConfirm = (Button) findViewById(R.id.btPrefConfirm);
        etPrefTxnPwd = (EditText) findViewById(R.id.etPrefTxnPwd);
        etPrefTxnPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    confirmClicked();
                    return true;
                }
                return false;
            }
        });

        btAlwaysUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useAlwaysClicked();
            }
        });

        btUseForNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useForNextClicked();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClicked();
            }
        });

        rlTxnPwd.setVisibility(View.GONE);


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

        ShmartFlow.getInstance().onUseQuopnActivityCreated(this);

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }

        originalDefWallet = QuopnApplication.getInstance().getDefaultWallet();
    }

    private int getSpecialDiff () {
        Map<ShmartFlow.EWalletUsagePref, Integer> myMap = new HashMap<ShmartFlow.EWalletUsagePref, Integer>(3);
        myMap.put(ShmartFlow.EWalletUsagePref.NEVER,0);
        myMap.put(ShmartFlow.EWalletUsagePref.NEXT_TIME,1);
        myMap.put(ShmartFlow.EWalletUsagePref.ALWAYS,1);

        return (myMap.get(originalPref) - myMap.get(tempPref));
    }

    public void useForNextClicked(){
        if (!tempPref.equals(ShmartFlow.EWalletUsagePref.NEXT_TIME)) {
            showNewPref(ShmartFlow.EWalletUsagePref.NEXT_TIME);
        } else {
            showNewPref(ShmartFlow.EWalletUsagePref.NEVER);
        }
    }

    public void useAlwaysClicked(){
        if (!tempPref.equals(ShmartFlow.EWalletUsagePref.ALWAYS)) {
            showNewPref(ShmartFlow.EWalletUsagePref.ALWAYS);
        } else {
            showNewPref(ShmartFlow.EWalletUsagePref.NEVER);
        }
    }

    private void controlTxnPwdRLayout () {
        if (tempPref.equals(originalPref)) {
            rlTxnPwd.setVisibility(View.GONE);
        } else {
            rlTxnPwd.setVisibility(View.VISIBLE);
        }
    }

    public void confirmClicked() {
        if (QuopnUtils.isInternetAvailable(this)) {
            String transPass = etPrefTxnPwd.getText().toString().trim();
            if (TextUtils.isEmpty(transPass)) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_txnpin_validation, etPrefTxnPwd, null, 0);
                return;
            } else if (!Validations.isValidTxnPIN(transPass)) {
                Validations.CustomErrorMessage(getApplicationContext(), R.string.validation_invalid_txn_pwd, etPrefTxnPwd, null, 0);
                return;
            } else {
                etPrefTxnPwd.setError(null);
            }
            ShmartFlow.getInstance().onUseQuopnPrefSet(tempPref, transPass);
        } else {
            Dialog dialog = new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public void clearTextfield(){
        etPrefTxnPwd.setText("");
    }

    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onUseQuopnActivityDestroyed();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onUseQuopnActivityRestored(this);
    }

    public void showCurrentPref(ShmartFlow.EWalletUsagePref pref) {
        this.originalPref = pref;
        this.tempPref = pref;

        changeUI(pref);
    }

    public void showNewPref(ShmartFlow.EWalletUsagePref pref) {
        this.tempPref = pref;
        changeUI(pref);
    }

    public void changeUI (ShmartFlow.EWalletUsagePref pref) {

        if (pref.equals(ShmartFlow.EWalletUsagePref.NEVER)) {
            btAlwaysUse.setBackgroundColor(getResources().getColor(R.color.bg_shop_at_quopn_button));
            btUseForNext.setBackgroundColor(getResources().getColor(R.color.bg_shop_at_quopn_button));
            ivForAlwaysUse.setVisibility(View.GONE);
            ivForNext.setVisibility(View.GONE);
        } else if (pref.equals(ShmartFlow.EWalletUsagePref.ALWAYS)) {
            btAlwaysUse.setBackgroundColor(getResources().getColor(R.color.add_to_cart_bg_color));
            btUseForNext.setBackgroundColor(getResources().getColor(R.color.bg_shop_at_quopn_button));
            ivForAlwaysUse.setVisibility(View.VISIBLE);
            ivForNext.setVisibility(View.GONE);
        } else if (pref.equals(ShmartFlow.EWalletUsagePref.NEXT_TIME)) {
            btAlwaysUse.setBackgroundColor(getResources().getColor(R.color.bg_shop_at_quopn_button));
            btUseForNext.setBackgroundColor(getResources().getColor(R.color.add_to_cart_bg_color));
            ivForAlwaysUse.setVisibility(View.GONE);
            ivForNext.setVisibility(View.VISIBLE);
        }
        controlTxnPwdRLayout();
    }

    public void showMessage(final boolean isSuccess, final String message) {
        stopProgress();

        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) { UseQuopnActivity.this.finish(); }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String proMessage = "";
                if (isSuccess) {
                    int special = getSpecialDiff();
                    QuopnConstants.WalletType newDefWallet = QuopnApplication.getInstance().getDefaultWallet();
                    proMessage = getString(R.string.quopn_merchant_redemption_updated);
                    if (originalDefWallet != newDefWallet) {
                        if (tempPref == ShmartFlow.EWalletUsagePref.NEVER) {
                            proMessage = getString(R.string.quopn_merchant_redemption_updated_noselection);
                        } else {
                            if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
                                proMessage = getString(R.string.quopn_merchant_redemption_updated_citrus);
                            } else {
                                proMessage = getString(R.string.quopn_merchant_redemption_updated_udio);
                            }
                        }
                    } else {
                        if (tempPref == ShmartFlow.EWalletUsagePref.NEVER) {
                            proMessage = getString(R.string.quopn_merchant_redemption_updated_noselection);
                        } else if (special != 0) {
                            if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
                                proMessage = getString(R.string.quopn_merchant_redemption_updated_citrus);
                            } else {
                                proMessage = getString(R.string.quopn_merchant_redemption_updated_udio);
                            }
                        }
                    }
                    Dialog dialog = new Dialog(UseQuopnActivity.this, R.string.dialog_title, proMessage);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                } else {
                    Dialog dialog = new Dialog(UseQuopnActivity.this, R.string.dialog_title, message);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }
            }
        };
        runOnUiThread(runnable);
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
}
