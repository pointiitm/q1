package com.quopn.wallet;

/**
 * @author Sumeet
 * @author Sivnarayan Roul
 * Completely changing the code and design to incorporate the new quopn design decisions.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.RegisterData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.CustomSeekbar;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends ActionBarActivity implements ConnectionListener,OnItemSelectedListener {
	/*private class FeedBackJSInterface {
		@JavascriptInterface
		public void finish() { FeedBackActivity.this.finish(); }
	}*/
	
	private String TAG = "FEEDBACK ACTIVITY";
	CustomProgressDialog mCustomProgressDialog;
	
	private CustomSeekbar mEase_of_use_feedback_description_ui_component;
	private CustomSeekbar mSimple_to_understand_feedback_description_ui_component;
	private CustomSeekbar mCool_design_feedback_description_ui_component;
	private CustomSeekbar mGreat_value_feedback_description_ui_component;
	private CustomSeekbar mOverall_feedback_description_ui_component;
	
	
	private ToggleButton mRecommend_toggButton;
	private Spinner mSelectOptionSpner;
	private String mSelectedCategoryName = null;
	private EditText mEditTextFeedback;
	private Button mBtnSubmit; 
	//@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.feedback);
		
		/*mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
*/
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.action_bar_bg));

		View actionBarView = View
				.inflate(this, R.layout.actionbar_layout, null);
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

/*		webview = (WebView) findViewById(R.id.webview);
		webview.setWebViewClient(new myWebClient());
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new FeedBackJSInterface(), "appWindow");
*///	webview.loadUrl(QuopnConstants.FEEDBACK_URL);
		
//		sendFeedBackCall();
		
		initSliderFeedbacksView();
		addListenerOnSpinnerItemSelection();
		//mRecommend_rdBtn = (RadioButton)findViewById(R.id.recommend_rd_btn);
		//mRecommend_rdBtn.setChecked(true);
		mRecommend_toggButton = (ToggleButton)findViewById(R.id.recommend_toggleButton1);
		mEditTextFeedback = (EditText) findViewById(R.id.fdbck_text);
		mEditTextFeedback.setText("");
		
		mSelectOptionSpner = (Spinner) findViewById(R.id.spnr_select_option);
		ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this,android.
				R.layout.simple_spinner_item , getResources().getStringArray(R.array.select_option));   ///R.layout.simple_spinner_dropdown_item , simple_list_item_1
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSelectOptionSpner.setAdapter(dataAdapter);	
	//	mSelectOptionSpner.setPrompt(getResources().getString(R.string.select_option_prompt));
		
		mBtnSubmit = (Button) findViewById(R.id.feedback_submit_btn);
		 
		mBtnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendFeedback();
			}
		});
		
		
	}
	
	public void initSliderFeedbacksView()
	{
		LinearLayout slider_feedbacks_container_layout = (LinearLayout)findViewById(R.id.slider_feedbacks_container);
		mEase_of_use_feedback_description_ui_component = new CustomSeekbar(this, getResources().getString(R.string.ease_of_use_feedback_description));
		mSimple_to_understand_feedback_description_ui_component = new CustomSeekbar(this, getResources().getString(R.string.simple_to_understand_feedback_description) );
		mCool_design_feedback_description_ui_component = new CustomSeekbar(this, getResources().getString(R.string.cool_design_feedback_description) );
		mGreat_value_feedback_description_ui_component = new CustomSeekbar(this, getResources().getString(R.string.great_value_feedback_description) );
		mOverall_feedback_description_ui_component = new CustomSeekbar(this, getResources().getString(R.string.overall_feedback_description) );
			
		slider_feedbacks_container_layout.addView(mEase_of_use_feedback_description_ui_component);
		slider_feedbacks_container_layout.addView(mSimple_to_understand_feedback_description_ui_component);
		slider_feedbacks_container_layout.addView(mCool_design_feedback_description_ui_component);
		slider_feedbacks_container_layout.addView(mGreat_value_feedback_description_ui_component);
		slider_feedbacks_container_layout.addView(mOverall_feedback_description_ui_component);
		
		
	}
	
	 public void addListenerOnSpinnerItemSelection() {
		 	mSelectOptionSpner = (Spinner) findViewById(R.id.spnr_select_option);
		 	mSelectOptionSpner.setOnItemSelectedListener(this);
		 	
		  }
	
	public void sendFeedback()
	{
		if (QuopnUtils.isInternetAvailable(this)) {
			Map<String, String> params;
			Map<String, String> headerParams;

			headerParams = new HashMap<String, String>();
			headerParams.put("Authorization", PreferenceUtil.getInstance(getApplicationContext()).getPreference(
					PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
			Log.d(TAG,"AUTH KEY "+ PreferenceUtil.getInstance(getApplicationContext()).getPreference(
					PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
			params = new HashMap<String, String>();
			params.put("walletid", PreferenceUtil.getInstance(getApplicationContext())
					.getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
			Log.d(TAG, "wallet id "+  PreferenceUtil.getInstance(getApplicationContext())
					.getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
			
			params.put("easy_use", mEase_of_use_feedback_description_ui_component.getUserProvidedFeedbackValue());
			params.put("simple_understand", mSimple_to_understand_feedback_description_ui_component.getUserProvidedFeedbackValue());
			params.put("cool_desing", mCool_design_feedback_description_ui_component.getUserProvidedFeedbackValue());
			params.put("greate_value", mGreat_value_feedback_description_ui_component.getUserProvidedFeedbackValue());
			params.put("overall", mOverall_feedback_description_ui_component.getUserProvidedFeedbackValue());
			if(mRecommend_toggButton.isChecked())
				params.put("recommend", "1");
			else
				params.put("recommend", "0");

			if(TextUtils.isEmpty(mSelectedCategoryName)) {
				Toast.makeText(this,"Please select a Feedback Category", Toast.LENGTH_SHORT).show();
				return;
			}
			params.put("feedback_cat", mSelectedCategoryName);
			
			params.put("feedback_text", mEditTextFeedback.getText().toString());

			

			ConnectionFactory mConnectionFactory = new ConnectionFactory(this,
					this);
			mConnectionFactory.setHeaderParams(headerParams);
			mConnectionFactory.setPostParams(params);
			mConnectionFactory.createConnection(QuopnConstants.FEEDBACK_API_CODE);
		} else {
			//mCustomProgressDialog.dismiss();
			//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.dialog_title_no_internet,R.string.please_connect_to_internet));
			//new Dialog(this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show(); 
			Dialog dialog=new Dialog(this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet); 
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
	        dialog.show();

		}
	}
	 
	public void sendFeedBackCall(){
		
		if (QuopnUtils.isInternetAvailable(this)) {
			Map<String, String> params;
			Map<String, String> headerParams;

			headerParams = new HashMap<String, String>();
			headerParams.put("Authorization", PreferenceUtil.getInstance(getApplicationContext()).getPreference(
					PreferenceUtil.SHARED_PREF_KEYS.API_KEY));

			params = new HashMap<String, String>();
			params.put("walletid", PreferenceUtil.getInstance(getApplicationContext())
					.getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));

			ConnectionFactory mConnectionFactory = new ConnectionFactory(this,
					this);
			mConnectionFactory.setHeaderParams(headerParams);
			mConnectionFactory.setPostParams(params);
			mConnectionFactory.createConnection(QuopnConstants.FEEDBACK_API_CODE);
		} else {
			//mCustomProgressDialog.dismiss();
			//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.dialog_title_no_internet,R.string.please_connect_to_internet));
			//new Dialog(this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
			Dialog dialog=new Dialog(this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet); 
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
	        dialog.show();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	
	
	
	/*private class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			mCustomProgressDialog.dismiss();
			super.onPageFinished(view, url);

		}
		
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			mCustomProgressDialog.dismiss();
			//view.loadUrl("file:///android_asset/myerrorpage.html");

		}
	}
*/
	@Override
	public void onResponse(int responseResult, Response response) {
		
		Dialog errDialog = new Dialog(this,R.string.server_error_title,R.string.server_error);
		errDialog.setCancelable(false);
		errDialog.setCanceledOnTouchOutside(false);
		switch (responseResult) {
		case CONNECTION_ERROR:
			//mCustomProgressDialog.dismiss();
			//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.server_error_title,R.string.server_error));
			Dialog dialog=new Dialog(this,R.string.server_error_title,R.string.server_error); 
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
	        dialog.show();
			
			break;
		case PARSE_ERR0R:
			//mCustomProgressDialog.dismiss();
			//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.server_error_title,R.string.server_error));
		/*	Dialog errDialog = new Dialog(this,R.string.server_error_title,R.string.server_error);
			errDialog.setCancelable(false);*/
			errDialog.show(); 
	        
			break;
			
		case RESPONSE_OK:
			RegisterData data = (RegisterData) response;
			
			if(data.getError() == false){
				//webview.loadUrl(data.getMessage(), null); // old code.
				//siv
				Log.d(TAG, data.getMessage());
				mSelectOptionSpner.setSelected(false);
				//finish();
				Dialog dialogResponse=new Dialog(this,R.string.feedback_title,data.getMessage()); 
		        /*dialog.addOkButton("YES");
		        dialog.addCancelButton("NO");*/
				dialogResponse.setCancelable(false);
				dialogResponse.setCanceledOnTouchOutside(false);
				dialogResponse.setOnAcceptButtonClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						onBackPressed();
					}
				});
				dialogResponse.show();
				
			} else {
				//mCustomProgressDialog.dismiss();
				if(data.getMessage()!=null){
					//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.dialog_title_error, data.getMessage()));
					//new Dialog(this,R.string.dialog_title_error, data.getMessage()).show(); 
					errDialog.show(); 
				}else{
					//AlertManager.show(new DialogConfigData(this,DialogConfigData.TYPE_ERROR,R.string.server_error_title,R.string.server_error));
					//new Dialog(this,R.string.server_error_title,R.string.server_error).show(); 
					errDialog.show(); 
			}
			}
			break;
			
		default:
			break;
		}
		
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if( position == 0)
			mSelectedCategoryName = "";
		else
			mSelectedCategoryName = parent.getItemAtPosition(position).toString();
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
		
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}

}
