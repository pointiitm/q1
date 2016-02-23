package com.quopn.wallet.shmart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.ShmartTransactionsAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.shmart.TransactionsResponse;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.List;

public class TransactionsActivity extends ActionBarActivity {
    private ListView lvTransactions;
    private CustomProgressDialog progress;
    private AnalysisManager mAnalysisManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_MY_TRANSACTIONS);
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
        lvTransactions = (ListView) findViewById(R.id.lvTransactions);

        progress = new CustomProgressDialog(this);
        if (QuopnUtils.isInternetAvailable(TransactionsActivity.this)) {
            ShmartFlow.getInstance().onTransactionsActivityCreated(this);
        }else{
            new Dialog(TransactionsActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
        }

        if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.SHMART)) {

            ImageView imageViewShmart = (ImageView)findViewById(R.id.udio_logo);
            imageViewShmart.setVisibility(View.VISIBLE);

        }else if(QuopnApplication.getInstance().getCurrentWalletMode().equals(QuopnConstants.WalletType.CITRUS)){

            ImageView imageViewCitrus = (ImageView)findViewById(R.id.citrus_logo);
            imageViewCitrus.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onDestroy() {
        ShmartFlow.getInstance().onTransactionsActivityDestroyed();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onTransactionsActivityRestored(this);
    }

    public void showTransactions(List<TransactionsResponse.Transaction> transactions) {
        ShmartTransactionsAdapter adapter = new ShmartTransactionsAdapter(this, transactions);
        lvTransactions.setAdapter(adapter);
    }

    public void showProgress() {
        progress.show();
    }

    public void stopProgress() {
        progress.dismiss();
    }

    public void showMessage(final boolean isSuccess, final String message) {
        final DialogInterface.OnDismissListener dismissListener
                = new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isSuccess) { TransactionsActivity.this.finish(); }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                String title = "Success";
//                if (!isSuccess) { title = "Failure"; }

                Dialog dialog = new Dialog(TransactionsActivity.this, R.string.dialog_title, message);
                dialog.setOnDismissListener(dismissListener);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }
}
