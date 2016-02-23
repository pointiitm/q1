package com.quopn.wallet.shmart;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.AccessToken;
import com.gc.materialdesign.widgets.Dialog;
import com.orhanobut.logger.Logger;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.ShmartOperationsAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.shmart.FeaturesResponse;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ShmartHomeActivity extends ActionBarActivity {
    private CustomProgressDialog progressDialog;
    private GridView gvShmartOperations;
    private TextView tvBalance;
    private ShmartFlow shmartFlow;
    private ShmartOperationsAdapter adapter;
    private AnalysisManager mAnalysisManager;
    private List<FeaturesResponse.Feature> features;
    private CitrusClient mCitrusClient = null;
    private Context mContext = this;

    private AdapterView.OnItemClickListener itemClickListener
            = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long longID) {
                if (FeaturesResponse.Status.ENABLED == adapter.getStatusForFeature(position)){
                shmartFlow.onShmartOpChosen((int) longID);
               }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_shmart_home);

        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_HOME);
        progressDialog = new CustomProgressDialog(this);

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
        mCitrusClient = CitrusClient.getInstance(getApplicationContext());

        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            this.features = ShmartFlow.getInstance().getCitrusFeatures();
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            this.features = ShmartFlow.getInstance().getShmartFeatures();
        } else {
            Logger.e("features for wallet none");
        }

        if (this.features!= null && !this.features.isEmpty()) {
            adapter = new ShmartOperationsAdapter(this);
            adapter.setFeatureListResponseInOps(features); // ankur
            gvShmartOperations = (GridView) findViewById(R.id.gvShmartOptions);
            gvShmartOperations.setAdapter(adapter);
            gvShmartOperations.setOnItemClickListener(itemClickListener);

            tvBalance = (TextView) findViewById(R.id.tvBalance);
            //TextView tvCurrBal = (TextView) findViewById(R.id.tvCurrBal);
            //tvCurrBal.setTypeface(null, Typeface.BOLD);

            this.shmartFlow = ShmartFlow.getInstance();
            shmartFlow.onShmartHomeCreated(this);
            shmartFlow.citrusLogWalletStatsForRefreshToken();
        } else {
//            TODO: inform server
        }

        ImageView imgRefresh = (ImageView)findViewById(R.id.imgRefresh);
        imgRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showCustomProgress();
               shmartFlow.fetchBalance();
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
    protected void onDestroy() {
        if(shmartFlow!=null) {
            shmartFlow.onShmartHomeDestroyed();
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onShmartHomeRestored(this);
    }

    public void showComingSoon() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();

    }

    public void showBalance(final double balance) {
        Runnable runnableShowBalance = new Runnable() {
            @Override
            public void run() {
                tvBalance.setText(String.valueOf(balance));

               // getBalance();
            }
        };
        runOnUiThread(runnableShowBalance);
    }

//    private void getBalance() {
//
//        mCitrusClient.getBalance(new Callback<Amount>() {
//            @Override
//            public void success(Amount amount) {
//                QuopnConstants.showToast(mContext, "Balance : " + amount.getValue());
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                QuopnConstants.showToast(mContext, error.getMessage());
//            }
//        });
//    }

    public void showFeatures(final List<FeaturesResponse.Feature> features) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                adapter.updateOps(features);
            }
        };
        runOnUiThread(runnable);
    }

    public void showMessage(final boolean isSuccess, final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                String title = "Success";
//                if (!isSuccess) { title = "Failure"; }

                Dialog dialog = new Dialog(ShmartHomeActivity.this, R.string.dialog_title, message);
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

 public void showCustomProgress() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        };
        runOnUiThread(runnable);
     progressDialog.show();
    }

    public void hideCustomProgress() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        };
        runOnUiThread(runnable);
        progressDialog.dismiss();
    }
}
