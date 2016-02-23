package com.quopn.wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.GeneratePinData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.QuopnTextView;

import java.util.HashMap;
import java.util.Map;

public class PromoCodeActivity extends ActionBarActivity implements
		ConnectionListener, OnClickListener {

	private EditText mEditPromoCode;
	private QuopnTextView mButtonApply, mPromotext,mPromoBottomtext;
	private CustomProgressDialog mCustomProgressDialog;
    Uri uri;
    String Promo_from_link;
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));


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
		
		setContentView(R.layout.promocode_screen);

		initUI();
		mCustomProgressDialog = new CustomProgressDialog(this);

		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	private void initUI() {

        String promo_from_notification=getIntent().getStringExtra(QuopnConstants.INTENT_KEYS.promo);
        String promo_text  =PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROMO_TOP_MESSAGE);
		String promo_bottom_text  =PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROMO_BOTTOM_MESSAGE);
		
		mPromotext = (QuopnTextView) findViewById(R.id.promotext);
		mPromoBottomtext=(QuopnTextView)findViewById(R.id.promobottomtext);
		mEditPromoCode = (EditText) findViewById(R.id.mEditPromoCode);
		mButtonApply = (QuopnTextView) findViewById(R.id.mButtonApply);
		mButtonApply.setOnClickListener(this);

        if (getIntent().getAction() == Intent.ACTION_VIEW
				&& (getIntent().getScheme().equals("http")
					|| getIntent().getScheme().equals("quopn"))) {
            //Toast.makeText(getApplicationContext(), "" + getIntent().getScheme(), Toast.LENGTH_SHORT).show();
            uri = getIntent().getData();
            Promo_from_link = uri.getLastPathSegment();
            if(Promo_from_link!=null) {
                mEditPromoCode.setText("" + Promo_from_link);//.substring(1,Promo_from_link.length()));
            }
        }

		if(promo_from_notification!=null && !promo_from_notification.matches("")){
            mEditPromoCode.setText(""+promo_from_notification);
        }
		mEditPromoCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEditPromoCode.setError(null);	
			}
		});
		
		mEditPromoCode.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		        if (arg1 == EditorInfo.IME_ACTION_GO) {
		        	onApplyClicked();
		        }
		        return false;
		    }
		});
		
		if(null != promo_text){
			mPromotext.setText(promo_text);
		}
		if(null != promo_bottom_text){
			String previoustext=promo_bottom_text.substring(0, promo_bottom_text.indexOf("http"));
			final String httplink=promo_bottom_text.substring(promo_bottom_text.indexOf("http"), promo_bottom_text.length());
			mPromoBottomtext.setText(Html.fromHtml(previoustext+"\t"+"<bold><font color=\"#0000FF\">"+httplink+ "</font>"));
			mPromoBottomtext.setClickable(true);
			mPromoBottomtext.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(httplink));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
				}
			});
		}
	}

	@Override
	public void onResponse(int responseResult, Response response) {

		switch (responseResult) {
		case ConnectionListener.CONNECTION_ERROR:
			mCustomProgressDialog.dismiss();
			Dialog dialog=new Dialog(this, R.string.dialog_title_error,R.string.please_connect_to_internet); 
			dialog.show();
			break;
		case ConnectionListener.PARSE_ERR0R:
			mCustomProgressDialog.dismiss();
			Dialog dialog1=new Dialog(this, R.string.dialog_title_error,R.string.server_error); 
			dialog1.show();
			break;

		case ConnectionListener.RESPONSE_OK:
			GeneratePinData data = (GeneratePinData) response;

			if (data.getError() == false) {
				mCustomProgressDialog.dismiss();
				Dialog dialog2=new Dialog(this, R.string.dialog_title_promocode,data.getMessage()); 
				dialog2.setOnAcceptButtonClickListener(clickListener);
				dialog2.setCanceledOnTouchOutside(false);
				dialog2.setCancelable(false);
				dialog2.show();

			} else {
				mCustomProgressDialog.dismiss();
				mEditPromoCode.requestFocus();
				mEditPromoCode.setText("");
				if (data.getMessage() != null) {
					Dialog dialog3=new Dialog(this, R.string.dialog_title_promocode,data.getMessage()); 
					dialog3.show();
				} else {
					Dialog dialog4=new Dialog(this, R.string.dialog_title_promocode,"Error!. Try after sometime"); 
					dialog4.show();
				}
			}

			break;

		default:
			break;
		}

	}
	
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
			intent.putExtra("REFRESH", "REFRESH_GIFTS");
			LocalBroadcastManager.getInstance(PromoCodeActivity.this).sendBroadcast(intent);
			finish();
			
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.mButtonApply:
			onApplyClicked();
			break;

		default:
			break;
		}

	}

	private void onApplyClicked() {
		if (!validatePromoCode()) {
			return;
		}
		postToServer();
	}

	private void postToServer() {
		if (QuopnUtils.isInternetAvailable(this)) {
			mCustomProgressDialog.show();
			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnApi.ParamKey.WALLETID, PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
			params.put(QuopnApi.ParamKey.PROMOCODE, mEditPromoCode.getText().toString().trim());

			ConnectionFactory connectionFactory = new ConnectionFactory(
					this, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.PROMO_CODE);
		} else {
			Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet); 
			dialog.show();
		}

	}

	private boolean validatePromoCode() {
		if (TextUtils.isEmpty(mEditPromoCode.getText().toString().trim())) {
			Validations.CustomErrorMessage(this,
					R.string.promo_validation, mEditPromoCode, null, 0);
			return false;
		} else {
			mEditPromoCode.setText(mEditPromoCode.getText().toString().trim());
			return true;
		}
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
					mCustomProgressDialog.dismiss();
					Dialog dialog=new Dialog(PromoCodeActivity.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
					dialog.show();
				}
				
			}
		});
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}

}
