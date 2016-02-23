package com.quopn.wallet;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

public class ChangePinActivity extends ActionBarActivity implements
		ConnectionListener, OnClickListener {

	private EditText mEditOldPin;
	private EditText mEditNewPin;
	private EditText mEditNewConfirmPin;
	private TextView mButtonSave;
	private CustomProgressDialog mCustomProgressDialog;
	private String mStringNewPin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		
//		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		getSupportActionBar().setDisplayShowCustomEnabled(true);
//		getSupportActionBar().setDisplayUseLogoEnabled(false);
//		getSupportActionBar().setDisplayShowHomeEnabled(false);
//		getSupportActionBar().setBackgroundDrawable(
//		getResources().getDrawable(R.drawable.action_bar_bg));
//
//		View actionBarView = View.inflate(this, R.layout.actionbar_layout, null);
//		getSupportActionBar().setCustomView(actionBarView);
//
//		ImageView slider = (ImageView) actionBarView.findViewById(R.id.slider);
//		slider.setImageResource(R.drawable.back);
//		slider.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//
//		ImageView home_btn = (ImageView) actionBarView
//				.findViewById(R.id.home_btn);
//		home_btn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				setResult(RESULT_OK);
//				finish();
//
//			}
//		});
		
//		SearchView searchView = (SearchView) actionBarView
//				.findViewById(R.id.fragment_address_search);
//		searchView.setVisibility(View.INVISIBLE);
//		ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
//		mCommonCartButton.setVisibility(View.INVISIBLE);
//		TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
//		mNotification_Counter_tv.setVisibility(View.INVISIBLE);
//		TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
//		mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
//
//		setContentView(R.layout.changepin_screen);
//		mEditOldPin = (EditText) findViewById(R.id.mEditOldPin);
//		mEditNewPin = (EditText) findViewById(R.id.mEditNewPin);
//		mEditNewConfirmPin = (EditText) findViewById(R.id.mEditNewConfirmPin);
//		mButtonSave = (TextView) findViewById(R.id.mButtonSave);
//		mButtonSave.setOnClickListener(this);
		
		mCustomProgressDialog = new CustomProgressDialog(this); 
	}

	@Override
	public void onResponse(int responseResult, Response response) {

		switch (responseResult) {
		case ConnectionListener.CONNECTION_ERROR:
			mCustomProgressDialog.dismiss();
			break;
		case ConnectionListener.PARSE_ERR0R:
			mCustomProgressDialog.dismiss();
			break;

		case ConnectionListener.RESPONSE_OK:
			GeneratePinData data = (GeneratePinData) response;
			
			if (data.getError() == false) {
				mCustomProgressDialog.dismiss();
				PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY, mStringNewPin);
				Dialog dialog = new Dialog(this,R.string.dialog_title_change_pin,data.getMessage());
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				dialog.show();

			} else {
				mCustomProgressDialog.dismiss();
				mEditOldPin.requestFocus();
				if (data.getMessage() != null) {
					Dialog dialog=new Dialog(this, R.string.dialog_title_change_pin,data.getMessage()); 
					dialog.show();
				} else {
					Dialog dialog=new Dialog(this, R.string.dialog_title_change_pin,"Error in changing pin. Try after sometime"); 
					dialog.show();
				}
			}

			break;

		default:
			break;
		}

	}
	

//	private boolean checkPinValidation() {
//
//		String oldpin = mEditOldPin.getText().toString();
//		String newpin = mEditNewPin.getText().toString();
//		String re_newpin = mEditNewConfirmPin.getText().toString();
//
//		if (TextUtils.isEmpty(oldpin) || !(oldpin.length() == 4)
//				|| !oldpin.matches(QuopnConstants.OTPPATTERN)) {
//
//			Validations.CustomErrorMessage(this,
//					R.string.oldpin_validation, mEditOldPin, null, 0);
//		} else if (TextUtils.isEmpty(newpin) || !(newpin.length() == 4)
//				|| !newpin.matches(QuopnConstants.OTPPATTERN)) {
//
//			Validations.CustomErrorMessage(this,
//					R.string.newpin_validation, mEditNewPin, null, 0);
//		} else if (TextUtils.isEmpty(re_newpin) || !(re_newpin.length() == 4)
//				|| !re_newpin.matches(QuopnConstants.OTPPATTERN)) {
//
//			Validations.CustomErrorMessage(this,
//					R.string.retrypin_validation, mEditNewConfirmPin, null, 0);
//		} else if (!newpin.equals(re_newpin)) {
//
//			Validations.CustomErrorMessage(this,
//					R.string.newpin_retrypin_validation, mEditNewConfirmPin, null, 0);
//		} else {
//
//			mStringNewPin = mEditNewPin.getText().toString();
//			return true;
//		}
//
//		return false;
//
//	}

	//VAIBHAV COMMENTED
	/*@Override
	public void onBackPressed(FragmentActivity activity) {
		super.onBackPressed(activity);
		activity.getSupportFragmentManager().beginTransaction().remove(this)
				.commit();
		Fragment settings = new SettingsFragment();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.menu_frame, settings)
				.commit();
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mButtonSave:
			callChangePin();
			break;

		default:
			break;
		}

	}

	private void callChangePin() {

			if (QuopnUtils.isInternetAvailable(this)) {
				mCustomProgressDialog.show();
				Map<String, String> params = new HashMap<String, String>();
				params.put("walletid",PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
				params.put(QuopnApi.EWalletRequestParam.MOBILE_WALLET_ID.getName(),QuopnApplication.getInstance().getDefaultWalletID());

				ConnectionFactory connectionFactory = new ConnectionFactory(
						this, this);
				connectionFactory.setPostParams(params);

				connectionFactory.createConnection(QuopnConstants.GENERATE_PIN_CODE);

			} else {
				Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
				dialog.show();
			}
		}



	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
					mCustomProgressDialog.dismiss();
					Dialog dialog=new Dialog(ChangePinActivity.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
					dialog.show();
				}
			}
		});
		
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}
}
