package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.OTPData;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.ResendOTPData;
import com.quopn.wallet.fragments.MainMenuFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTPScreen extends Activity implements ConnectionListener {
	private static final String TAG="OTPScreen";

	public boolean isSMSLISTNERREGISTERED = false;
	protected CustomProgressDialog mProgressDialog;
	private TextView mBtnLogin;
	private EditText mEditTextOtp;
	private TextView mTextViewResendOtp, mProgressText, mManualEntryText;

	private String mUserId = "", mMobileNo = "", mOtp = "";
	private SmsListener mSmsListener = new SmsListener();
	private LinearLayout mSmswaitProgress;
	private CountDownTimer mCountDownTimer;
	private final long mStartTime = 30 * 1000;
	private final long mInterval = 1 * 1000;

	private final int mMaxSmsAttempts = 3;
	private int mSmsAttempt = 0;
	private String stateId;
	private String cityId;
	private Context context;
	private Context aContext; // activity
	private static final int RESPONSE_SUCCESS_MESSAGE=100;
	private AnalysisManager mAnalysisManager;

	private Handler messagehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESPONSE_SUCCESS_MESSAGE:
				mEditTextOtp.setText(msg.getData().getString("message"));
				underlineText();
				mTextViewResendOtp.setVisibility(View.VISIBLE);
				mManualEntryText.setVisibility(View.GONE);
				mSmswaitProgress.setVisibility(View.GONE);
				getVerify();
				break;

			default:
				break;
			}

		}
	};

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			mTextViewResendOtp.setVisibility(View.VISIBLE);
			mManualEntryText.setVisibility(View.GONE);
			mSmswaitProgress.setVisibility(View.GONE);
			underlineText();
		}

		@Override
		public void onTick(long millisUntilFinished) {
//			mProgressText.setText(" "+millisUntilFinished / 1000 + " secs" );
			String format = String.format(getResources().getString(R.string.waiting_for_sms), millisUntilFinished / 1000);
			mProgressText.setText(format);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.otp_screen);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		context=getApplicationContext();
		aContext = this;

		mAnalysisManager = ((QuopnApplication)getApplicationContext()).getAnalysisManager();

		mProgressDialog = new CustomProgressDialog(this);
		Bundle bundle = getIntent().getExtras();
		mUserId = bundle.getString(QuopnConstants.INTENT_EXTRA_USERID);
		mMobileNo = bundle.getString(QuopnConstants.INTENT_EXTRA_MOBILE_NO);

		mOtp = bundle.getString(QuopnConstants.INTENT_EXTRA_OTP_NO);

		//mTextViewOtp = (TextView) findViewById(R.id.otp_text);
		//mTextViewOtp.append(mOtp);

		mTextViewResendOtp = (TextView) findViewById(R.id.resendotp);

		mEditTextOtp = (EditText) findViewById(R.id.editOTP);
		mEditTextOtp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditTextOtp.setError(null);
			}
		});

		mEditTextOtp.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_GO) {
					getVerify();
				}
				return false;
			}
		});
		mBtnLogin = (TextView) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getVerify();
			}
		});

		registerReceiver(mSmsListener, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
		isSMSLISTNERREGISTERED = true;

		mTextViewResendOtp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resendOTP();
			}
		});

		mSmswaitProgress = (LinearLayout) findViewById(R.id.smswait_progress);
		mManualEntryText = (TextView) findViewById(R.id.textView3);
		mProgressText = (TextView) findViewById(R.id.progresstext);
		mCountDownTimer = new MyCountDownTimer(mStartTime, mInterval);
		mCountDownTimer.start();

	}

	public void getVerify() {
		String quopnPIN = mEditTextOtp.getText().toString();
		if(!QuopnUtils.isInternetAvailableAndShowDialog(aContext)){
			return;
		} else if(TextUtils.isEmpty(quopnPIN)){
			Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_pin_validation, mEditTextOtp, null, 0);
	           return;
		} else if(!quopnPIN.matches(QuopnConstants.PINPATTERN)){
			Validations.CustomErrorMessage(getApplicationContext(), R.string.pin_validation, mEditTextOtp, null, 0);
	           return;
		} else{
			showProgress();
			mCountDownTimer.cancel();

			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", mUserId);
			params.put("pin", mEditTextOtp.getText().toString());
			params.put("mobileno", mMobileNo);
			params.put("utm_source", PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_SOURCE) + "");
			params.put("utm_content", PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CONTENT) + "");
			params.put("x-session", PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID) + "");
			params.put("utm_campaign", PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CAMPAIGN) + "");
			params.put("device_id", QuopnConstants.android_id);
			params.put("version_name", QuopnConstants.versionName);
			params.put("version_code", "" + QuopnConstants.versionCode);
			params.put("brand",Build.BRAND );
			params.put("device", Build.DEVICE);
			params.put("model", Build.MODEL);
			params.put("build", Build.ID);
			params.put("product", Build.PRODUCT);
			params.put("sdk", Build.VERSION.SDK);
			params.put("release",Build.VERSION.RELEASE );
			params.put("increment", Build.VERSION.INCREMENTAL);

			ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.OTP_CODE);
		}
	}


	public void resendOTP() {
		if (QuopnUtils.isInternetAvailableAndShowDialog(aContext)) {
			if (mSmsAttempt >= mMaxSmsAttempts) {
				mTextViewResendOtp.setVisibility(View.VISIBLE);
				mTextViewResendOtp.setText(R.string.resend_otp_morethan_3times_validation);
				return;
			}
			mProgressDialog.show();
			Map<String, String> params = new HashMap<String, String>();
			params.put("mobileno", mMobileNo);

			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.RESEND_OTP_CODE);
			mTextViewResendOtp.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isSMSLISTNERREGISTERED) {
			unregisterReceiver(mSmsListener);
			isSMSLISTNERREGISTERED = false;
		}
	}

	// handle the server response for registration
	@Override
	public void onResponse(int responseType,Response response) {

		// ankur
		stopProgress();

		switch(responseType){
		case RESPONSE_OK :
		if (response instanceof OTPData) {
			OTPData registerResponse = (OTPData) response;

			if (registerResponse.isError() == true) {
				mCountDownTimer.start();
				Dialog dialog=new Dialog(this, R.string.dialog_title_error,registerResponse.getMessage());
				dialog.show();
				mAnalysisManager.send(AnalysisEvents.OTP_FAILED);

			} else {

				mAnalysisManager.send(AnalysisEvents.OTP_VERIFIED);
				mProgressDialog.cancel();
				String apiKey = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
				Gson gson = new GsonBuilder().serializeNulls().create();
				QuopnConstants.PROFILE_DATA = gson.toJson(response);
				
				/* Save the returned profile data if not saved already */
				PreferenceUtil.getInstance(getApplicationContext()).saveProfileIfNull(QuopnConstants.PROFILE_DATA);
				/* End: Save returned profile */

				if(registerResponse.getUser().getState() != null){
					stateId = registerResponse.getUser().getState();
					cityId = registerResponse.getUser().getCity();
				}

				String gender = registerResponse.getUser().getGender();
				String dob = registerResponse.getUser().getDob();
				String email = registerResponse.getUser().getEmailid();
				List listInterests = registerResponse.getUser().getInterestedid();
				if(apiKey == null){
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY,registerResponse.getUser().getApi_key());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY, mMobileNo);
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL, ""/*registerResponse.getUser().getVideo()*/);
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_NAME, registerResponse.getUser().getSender_name());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_PIC, registerResponse.getUser().getSender_pic());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY, registerResponse.getUser().getNotification());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(MainMenuFragment.TUTORIAL_USER_STATUS, registerResponse.getUser().getTutorial());

					if(registerResponse.getUser().getTutorial()!=null &&  registerResponse.getUser().getTutorial().equals("1")){
						makeTutsOn();
					}else{
						makeTutsOff();
					}
				}
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY, registerResponse.getUser().getWalletid());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY, registerResponse.getUser().getNotification());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL, ""/*registerResponse.getUser().getVideo()*/);
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_COUNT, registerResponse.getUser().getInvite_count());


				if(gender == null  || dob == null || stateId == null || cityId == null ||
					listInterests == null || listInterests.size() == 0 ){
					Intent intent = new Intent(this, ProfileCompletionScreen.class);
					startActivity(intent);
					finish();
				} else if(registerResponse.getUser().getVideo()!=null&&registerResponse.getUser().getVideo().length()>0){
					Intent gotoGiftInfo = new Intent(this, GiftInfo.class);
					startActivity(gotoGiftInfo);
					finish();
				} else {
					Intent intent = new Intent(this, MainActivity.class);
					// handling pre-register case, launch wallet after home
					if (!registerResponse.getUser().getPre_registered().isEmpty() && (registerResponse.getUser().getPre_registered().equalsIgnoreCase("true") || registerResponse.getUser().getPre_registered().equalsIgnoreCase("1"))) {
						PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, false);
					}
					startActivity(intent);
					finish();
				}


			}
		} else if (response instanceof ResendOTPData) {
			ResendOTPData registerResponse = (ResendOTPData) response;
			if (registerResponse.isError() == true) {
				mSmsAttempt = mSmsAttempt + 1;
				Dialog dialog=new Dialog(this, R.string.dialog_title_error,registerResponse.getMessage());
				dialog.show();
			} else {
				mSmsAttempt = mSmsAttempt + 1;
				Dialog dialog=new Dialog(this, R.string.dialog_title_resend_pin,registerResponse.getMessage());
				dialog.show();

				// make resend button invisible on click of it and show waiting for sms for 2mins
//				mTextViewResendOtp.setVisibility(View.GONE);
				mSmswaitProgress.setVisibility(View.VISIBLE);
				mManualEntryText.setVisibility(View.VISIBLE);
				mCountDownTimer.start();

			}
		} else if (response instanceof ProfileData) {
//			Gson gson = new Gson();
//			String resp = gson.toJson(response);
			QuopnConstants.PROFILE_DATA = response.toString();
			ProfileData interestsData = (ProfileData) response;

			if (interestsData.isError() == true) {
				Dialog dialog=new Dialog(this, R.string.dialog_title_error,interestsData.getMessage());
				dialog.show();
			} else {
				stateId = interestsData.getUser().getState();
				cityId = interestsData.getUser().getCity();
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY, interestsData.getUser().getWalletid());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USERNAME_KEY, interestsData.getUser().getUsername());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY, interestsData.getUser().getMobile());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.EMAIL_KEY, interestsData.getUser().getEmailid());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY, interestsData.getUser().getPIN());
			}
		}
		break;
		case CONNECTION_ERROR :

			Dialog dialog=new Dialog(this, R.string.server_error_title,R.string.server_error);
			dialog.show();

			break;
		case PARSE_ERR0R :

			Dialog dialog1=new Dialog(this, R.string.server_error_title,R.string.server_error);
			dialog1.show();
			break;
		}
	}

	public void underlineText(){
		SpannableString content = new SpannableString(getResources().getString(R.string.resendpin_txt));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		mTextViewResendOtp.setText(content);
	}

	class SmsListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {
				Bundle bundle = intent.getExtras(); // ---get the SMS
													// message passed
				SmsMessage[] msgs = null;
				String msg_from;
				if (bundle != null) {
					// ---retrieve the SMS message received---
					try {
						Object[] pdus = (Object[]) bundle.get("pdus");
						msgs = new SmsMessage[pdus.length];
						for (int i = 0; i < msgs.length; i++) {
							msgs[i] = SmsMessage
									.createFromPdu((byte[]) pdus[i]);
							msg_from = msgs[i].getOriginatingAddress();
							if(msg_from.contains("mQUOPN")){
								String msgBody = msgs[i].getMessageBody();
								Message msg = Message.obtain();
								msgBody = msgBody.replaceAll("[^-?0-9]+", "");
								if(msgBody.length()==4){
									msg.what=RESPONSE_SUCCESS_MESSAGE;
									Bundle b = new Bundle();
									b.putString("message", msgBody);
									msg.setData(b);
									messagehandler.sendMessage(msg);
									break;
								}
							}
						}
					} catch (Exception e) {
						// Log.d("Exception caught",e.getMessage());
					}
				}
			}
		}

	}

	private void makeTutsOn(){
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_DETAILS, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_LISTING, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_MYQUOPNS, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_GIFTING, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_ON);
		/*
		 * two new keys added for start on and of (N for all tuts are not seen by user and 0 is the count of tuts at first)
		 * Y will denote that all seven tuts are seen by user.
		 * At present we have 7 tuts so we will increase the tuts count as user sees the tuts.
		*/
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.PREF_ALL_TUTS_SEEN, "N");
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.PREF_ALL_TUTS_COUNT, "0");
	}

	private void makeTutsOff(){
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_DETAILS, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_LISTING, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_MYQUOPNS, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_GIFTING, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_OFF);
		/*
		 * two new keys added for start on and of (N for all tuts are not seen by user and 0 is the count of tuts at first)
		 * Y will denote that all seven tuts are seen by user.
		 * At present we have 7 tuts so we will increase the tuts count as user sees the tuts.
		*/
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.PREF_ALL_TUTS_SEEN, "Y");
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.PREF_ALL_TUTS_COUNT, "6");
}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				stopProgress();

				// ankur
				try {
					Dialog dialog = new Dialog(OTPScreen.this, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
					dialog.show();
				} catch (Exception e) {
					Log.e(TAG,e.getLocalizedMessage());
				}
			}
		});

	}

	@Override
	public void myTimeout(String requestTag) {

	}

	private void showProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.show();
		}
	}

	private void stopProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}
