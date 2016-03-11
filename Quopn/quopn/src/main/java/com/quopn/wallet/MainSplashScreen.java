package com.quopn.wallet;

/**
 * 
 * Version information :1.1
 *
 * Date : 02:09:14 (Modify by Ravishankar Ahirwar)
 * 
 * */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnApplication.TrackerName;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.AddToCartData;
import com.quopn.wallet.data.model.CityData;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.StateCityData;
import com.quopn.wallet.data.model.StateData;
import com.quopn.wallet.gifplayer.GifMovieView;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.AssetsFileProvider;
import com.quopn.wallet.utils.DateValidator;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainSplashScreen extends Activity implements
		ConnectionListener {
    private final static String TAG = "Quopn/MainSplashScreen";
    /**
     * Duration of wait *
     */
    private final static int SPLASH_DISPLAY_TIME = 3000; /* 3 Second */
    private Handler mSplashHandler;
    private Intent mRedirectToMainActivity;

    private ConnectionFactory mConnectionFactory;
    private Map<String, String> params;
	private Map<String, String> postParams;
    private String stateId;
    private String cityId;

    ImageView mCustomProgressDialogWhite;

    private ImageView imageview;
    private ImageView imageview_trrain;
    private ImageButton imagebutton;
    private DateValidator dateValidator;
    private String startdate = "01/11/2014", enddate = "30/11/2014";
    private String todayDate = "06/11/2014";

    private TextView version_txt;
    private Tracker googleAnalyticTracker;
    Gson gson;
    private GifMovieView gifMovie;
    String type_of_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.d("statecity", "onCreate");

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.main_splash_screen);

		PackageInfo packageInfo = null;
		try {
			packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e2) {
			Logger.e("null context");
		}

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Received broadcast to log out " + intent.getAction());
				QuopnUtils.logoutCleanUp(MainSplashScreen.this);
			}
		};
		Log.d(TAG, "Registering register for broadcast '" + QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION + "'");
		IntentFilter filter = new IntentFilter();
		filter.addAction(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

		if (packageInfo != null) {
			QuopnConstants.versionCode = packageInfo.versionCode;
			QuopnConstants.versionName = packageInfo.versionName;
		}

		googleAnalyticTracker = ((QuopnApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		QuopnConstants.android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID) + "p08031";

		if (QuopnConstants.android_id == null
				|| QuopnConstants.android_id.length() <= 0
				|| QuopnConstants.android_id.equals("")) {
			File file = new File(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DCIM).getPath()
							+ "/" + QuopnConstants.QUOPN_DEVICEID_FOLDER + "/"
							+ QuopnConstants.QUOPN_DEVICEID_FILE);
			if (!file.exists()) {
				QuopnUtils.writeDeviceId(true);
			} else {
				QuopnUtils.readDeviceId();
			}
		}

		if (QuopnUtils.isInternetAvailable(this)) {
			getStateCityList();
			Log.d("statecity", "oncreategetstatecitylist");
		} //else {

		Cursor cursor_statecity = getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, null, null, null);
		if (cursor_statecity == null || cursor_statecity.getCount() == 0) {
			AssetsFileProvider assetsFileProvider = new AssetsFileProvider(getApplicationContext());
			String jsonformat = assetsFileProvider.loadJSONFromAsset(QuopnConstants.STATE_CITY_FILE);
			gson = new Gson();
			StateCityData stateCityData = (StateCityData) gson.fromJson(jsonformat, StateCityData.class);
			//Log.d("location", "cursor_statecity = null");
			Log.d("statecity", "oncreateAsset");
			setDefaultValuesForStateCity();
			Log.d("statecity", "oncreateAsset1111");
			for (StateData statedata : stateCityData.getStates()) {
				insertStateData(statedata);
				for (CityData cityData : statedata.getCities()) {
					insertCityData(statedata.getStateId(), cityData);
					Log.d("statedata", "oncreateAsset22222");
				}
			}
		}

			dateValidator = new DateValidator();
			imageview = (ImageView) findViewById(R.id.imageview);
			imageview_trrain = (ImageView) findViewById(R.id.imageview_trrain);
			version_txt = (TextView) findViewById(R.id.version_txt);
			version_txt.setText("Version ");
			version_txt.append(QuopnConstants.versionName + " ");
			version_txt.append(QuopnApi.currentMode.getSplashCaption());

			String splashScreenLocalFilePath = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_LOCAL_PATH);
//		String splashScreenUrl = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_URL);
//		String splashScreenName = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_NAME);
			String splashScreenType = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_TYPE);
			String splashScreenStartDate = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_START_DATE);
			String splashScreenEndDate = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SPLASH_SCREEN_END_DATE);


			if (splashScreenStartDate != null && enddate != null) {
				//Log.d("statecity", "splashScreenStartDate != null && enddate != null");
				if (dateValidator.isTodayDateWithinRange(splashScreenStartDate, splashScreenEndDate, "yyyy-MM-dd")) {
					Bitmap myBitmap;
					if (splashScreenType.equalsIgnoreCase("png") || splashScreenType.equalsIgnoreCase("jpg")) {
						if (splashScreenLocalFilePath != null) {
							if (new File(splashScreenLocalFilePath).exists()) {
								myBitmap = BitmapFactory.decodeFile(splashScreenLocalFilePath);
								imageview.setImageBitmap(myBitmap);
							} else {
								setStaticLogo();
							}
						} else {
							setStaticLogo();
						}

					} else if (splashScreenType.equalsIgnoreCase("gif")) {
						if (splashScreenLocalFilePath != null) {
							if (new File(splashScreenLocalFilePath).exists()) {
								File splashScreenFile = new File(splashScreenLocalFilePath);
								gifMovie = (GifMovieView) findViewById(R.id.gifview);
								gifMovie.setMovieFromFile(splashScreenFile);

							} else {
								setStaticLogo();
							}
						} else {
							setStaticLogo();
						}
					}
				} else {
					setStaticLogo();
				}
			} else {
				setStaticLogo();
			}


			mSplashHandler = new Handler();

			mCustomProgressDialogWhite = (ImageView) findViewById(R.id.progress);
			AnimationDrawable animation = (AnimationDrawable) mCustomProgressDialogWhite.getDrawable();
			if (animation != null) {
				animation.start();
			}


			if (QuopnUtils.isInternetAvailable(MainSplashScreen.this)) {

				if (!PreferenceUtil.getInstance(getApplicationContext())
						.hasContainedPreferenceKey(PreferenceUtil.SHARED_PREF_KEYS.API_KEY)) {
					//Log.d("statecity", "oncreate==starttimeredirect==");
					startTimerAndRedirect();
				} else {
					//Log.d("statecity", "oncreate==starttimeredirect==else");
					mCustomProgressDialogWhite.setVisibility(View.VISIBLE);
					getProfile();
				}

			} else {
				//Log.d("statecity", "oncreate==starttimeredirect==else222");
				startTimerAndRedirect();
			}


		}

	private void setStaticLogo(){
        type_of_user = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.TYPE_OF_USER);
        if(type_of_user == null || type_of_user.equalsIgnoreCase("quopn")){
            imageview.setImageResource(R.drawable.quopn_logo_red);
           }else{
            imageview_trrain.setVisibility(View.VISIBLE);
            imageview.setImageResource(R.drawable.quopn_logo_red);
            imageview_trrain.setImageResource(R.drawable.trrainlogo_img);
        }
    }

	private void setDefaultValuesForStateCity(){
		//Log.d("statecity", "SetDefaultValueForCity");
		ContentValues cv_state = new ContentValues();
		cv_state.put(ITableData.TABLE_STATES.COLUMN_STATE_ID, QuopnConstants.SELECT_STATE_ID);
		cv_state.put(ITableData.TABLE_STATES.COLUMN_STATE_NAME, "Select State");
		getContentResolver().insert(ConProvider.CONTENT_URI_STATES,	cv_state);
		
		ContentValues cv_city = new ContentValues();
		cv_city.put(ITableData.TABLE_CITIES.COLUMN_CITY_ID, QuopnConstants.SELECT_CITY_ID);
		cv_city.put(ITableData.TABLE_CITIES.COLUMN_CITY_NAME,"Select City");
		cv_city.put(ITableData.TABLE_CITIES.COLUMN_STATE_ID,QuopnConstants.SELECT_STATE_ID);
		getContentResolver().insert(ConProvider.CONTENT_URI_CITIES,	cv_city);
	}
	
	private void insertStateData(StateData stateData){
		//Log.d("statecity", "InsertStateData");
		ContentValues cv = new ContentValues();
		cv.put(ITableData.TABLE_STATES.COLUMN_STATE_ID, stateData.getStateId());
		cv.put(ITableData.TABLE_STATES.COLUMN_STATE_NAME, stateData.getStateName());
		getContentResolver().insert(ConProvider.CONTENT_URI_STATES,	cv);
	}
	
	private void insertCityData(String stateid,CityData cityData){
		//Log.d("statecity", "InsertCityData");
		ContentValues cv = new ContentValues();
		cv.put(ITableData.TABLE_CITIES.COLUMN_CITY_ID,cityData.getCityId());
		cv.put(ITableData.TABLE_CITIES.COLUMN_CITY_NAME,cityData.getCityName());
		cv.put(ITableData.TABLE_CITIES.COLUMN_STATE_ID,stateid);
		getContentResolver().insert(ConProvider.CONTENT_URI_CITIES, cv);
	}

	private void startTimerAndRedirect() {
		//Log.d("statecity", "startTimerAndRedirect");

		/**
		 * New Handler to start the MainActivity and close this MainSplashScreen
		 * after 3 seconds.
		 */
		mSplashHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				/**
				 * Create an Intent that will start the Next Activity. After 3
				 * seconds redirect to another intent
				 */
				if (PreferenceUtil.getInstance(getApplicationContext())
						.hasContainedPreferenceKey(PreferenceUtil.SHARED_PREF_KEYS.API_KEY) && PreferenceUtil.getInstance(getApplicationContext())
						.getPreference(QuopnConstants.PROFILE_COMPLETE_KEY) != null) {

					mRedirectToMainActivity = new Intent(MainSplashScreen.this,MainActivity.class);
					if(getIntent().getData() != null){
						mRedirectToMainActivity.setData(getIntent().getData());//Data is used for Deep Linking through link click
					}
					if(getIntent().getExtras() != null) {
						mRedirectToMainActivity.putExtras(getIntent().getExtras());//Extras are used for Deep Linking through Notifications
					}
//					Log.v(TAG, "Redirect to MainActivity");
					startActivity(mRedirectToMainActivity);
					//Log.d("statecity", "1616161616161616");
					finish();
				} else {
					//Log.d("statecity", "SplashHandlerfordeeplinking==else");
					mRedirectToMainActivity = new Intent(getBaseContext(),
							RegistrationScreen.class);
//					Log.v(TAG, "Redirect to RegistrationScreen");
					startActivity(mRedirectToMainActivity);
					finish(); // Destroy activity
				}
			}

		}, SPLASH_DISPLAY_TIME);

	}

	
	private void getProfile() {
//		Log.v(TAG, "*****Getting User Profile Data*****");
		String api_key = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
		Log.d(TAG, "api key: " + api_key);
		//Log.d("statecity", "GetprofileApifire");
		if (!TextUtils.isEmpty(api_key)) {
			params = new HashMap<String, String>();
			params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
			postParams = new HashMap<String, String>();
			postParams.put(QuopnApi.ParamKey.APP_VERSION, Integer.toString(QuopnUtils.getAppVersionCode()));
			postParams.put(QuopnApi.ParamKey.APP_VERSION_NAME, QuopnUtils.getAppVersionCode_Name());
			mConnectionFactory = new ConnectionFactory(this, this);
			mConnectionFactory.setHeaderParams(params);
			mConnectionFactory.setPostParams(postParams);
			mConnectionFactory.createConnection(QuopnConstants.PROFILE_GET_CODE); // auth api
		} else {
			// show error
		}
	}

	private void getStateCityList() {
//		Log.v(TAG, "*****Getting State List Data*****");
		//Log.d("statecity", "Getcitystatelistapifire");
		params = new HashMap<String, String>();
		mConnectionFactory = new ConnectionFactory(this, this);
		mConnectionFactory.setHeaderParams(params);
		mConnectionFactory.createConnection(QuopnConstants.STATE_CITY_LIST_CODE);
	}

	private void getCityList(String argStateIndex) {
		//Log.d("statecity", "GetCityListApiFire");
		String apiKey = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
		if (!TextUtils.isEmpty(apiKey)) {
			Map<String, String> headerParams = new HashMap<String, String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("stateid", argStateIndex);
			
			ConnectionFactory mConnectionFactory = new ConnectionFactory(this,this);
			mConnectionFactory.setHeaderParams(headerParams);
			mConnectionFactory.setPostParams(params);
			mConnectionFactory.createConnection(QuopnConstants.CITY_LIST_CODE);
		} else {
			// show error
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResponse(int responseResult, Response response) {
		Log.d(TAG, "" + response);
		//Log.d("statecity", "Onresponse type" + response.getClass().getCanonicalName());
		if (response instanceof ProfileData) { // response for auth api
			//Log.d("statecity", "OnresponseProfiledata");
			ProfileData registerResponse = (ProfileData) response;
			//Log.v("statecity", "Response : InterestsData");

			if (registerResponse.isError() == true) {
				Log.v("statecity", "Response : InterestsData=> Error occur");
				 Dialog dialog=new Dialog(MainSplashScreen.this, R.string.dialog_title_error,registerResponse.getMessage()); 
				 dialog.show();
			} else {
				//Log.v("statecity", "Response : InterestsData=> Successful Response");

				String apiKey = PreferenceUtil.getInstance(getApplicationContext()).getPreference(
						PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
				if (apiKey == null) {
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY,
							registerResponse.getUser().getApi_key());

				}
				Gson gson = new GsonBuilder().serializeNulls().create();
				QuopnConstants.PROFILE_DATA = gson.toJson(response);
				if (registerResponse.getUser().getState() != null) {
					//Log.d("statecity", "ProfileDatasendstateId");
					stateId = registerResponse.getUser().getState();
					getCityList(stateId);
					cityId = registerResponse.getUser().getCity();
				}
				//Log.d("statecity", "registerResponse");
				PreferenceUtil.getInstance(getApplicationContext()).saveProfileIfNull(QuopnConstants.PROFILE_DATA);
				String gender = registerResponse.getUser().getGender();
				String dob = registerResponse.getUser().getDob();
				String email = registerResponse.getUser().getEmailid();
                //String type_of_user = registerResponse.getUser().getType_of_user();
                PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.TYPE_OF_USER, registerResponse.getUser().getType_of_user());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY, registerResponse.getUser().getMobile_wallets());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.ACCESS_TOKEN_KEY, registerResponse.getUser().getAccess_token());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.REFRESH_TOEKN_KEY, registerResponse.getUser().getRefresh_token());
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.CITRUS_EMAIL_KEY, registerResponse.getUser().getCitrus_email());

				QuopnUtils.setDefaultWalletInAppAndPref(registerResponse.getUser().getDefaultWallet(),getApplicationContext());// set default wallet
				List listInterests = registerResponse.getUser()
						.getInterestedid();
				
				if (gender == null || dob == null || email == null
						|| stateId == null || cityId == null
						|| listInterests == null || listInterests.size() == 0) {
					//Log.d("statecity", "StartRegistration");
					Intent intent = new Intent(this,
							RegistrationScreen.class);
					startActivity(intent);
					finish();
				} else if (registerResponse.getUser().getGift_count() > 0) {
					//Log.d("statecity", "StartGift");
					Intent gotoGiftInfo = new Intent(this, GiftInfo.class);
					startActivity(gotoGiftInfo);
					finish();
				} else {
					//Log.d("statecity", "StartMainActivity");
					Intent intent = new Intent(this, MainActivity.class);
					if(getIntent().getData() != null) {
						intent.setData(getIntent().getData());//Data is used for Deep Linking through link click
					}
					if(getIntent().getExtras() != null) {
						intent.putExtras(getIntent().getExtras());//Extras are used for Deep Linking through Notifications
					}
					startActivity(intent);
					finish();
					PreferenceUtil.getInstance(getApplicationContext())
							.setPreference(QuopnConstants.PROFILE_COMPLETE_KEY,
									"YES");
				}

			}
		} else if (response instanceof StateCityData) {
			//Log.d(TAG,"response instanceof StateCityData" );
			StateCityData stateCityData = (StateCityData) response;
			if (stateCityData.getError()) {

			} else {
				getContentResolver().delete(ConProvider.CONTENT_URI_STATES, null, null);
				getContentResolver().delete(ConProvider.CONTENT_URI_CITIES, null, null);
				setDefaultValuesForStateCity();
				for(StateData statedata: stateCityData.getStates()){
					insertStateData(statedata);
					for(CityData cityData: statedata.getCities()){
						insertCityData(statedata.getStateId(),cityData);
					}
//					Log.d("statecity", "GetStateCityDataForLoop");
				}
//				Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_STATE_CITY);
//				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}
		}else if (response instanceof AddToCartData) {
			//Log.d("statecity", "response instanceof AddToCartData");
			AddToCartData tAddToCartData = (AddToCartData) response;
			if (tAddToCartData.getError() == true) {
				Log.v("Thread","We Got Error in response of Referral Request from Mainsplash Screen");
			}else{
				Log.v("Thread", "Response Message from Mainsplash Screen: " + tAddToCartData.getMessage());
			}
		} else if (response instanceof NetworkError) {
			//Log.d("statecity", "response instanceof NetworkError");
			NetworkError networkError = (NetworkError) response;
			if (networkError.getError().networkResponse != null) {
				Log.d("statecity", "networkError.getError().networkResponse != null");
				NetworkResponse networkResponse = networkError.getError().networkResponse;
				if (networkResponse.statusCode == 401) {
					Log.d("statecity", "networkResponse.statusCode == 401");
					Intent intent = new Intent(this, RegistrationScreen.class);
					this.startActivity(intent);
					finish();
				}
			}
		}
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		
		runOnUiThread(new Runnable() { //sandeep added 21072015
			@Override
			public void run() {
				if (mCustomProgressDialogWhite != null && mCustomProgressDialogWhite.isShown()) {
					//Log.d("statecity", "onTimeout(ConnectRequest request)");
					mCustomProgressDialogWhite.setVisibility(View.INVISIBLE);
					Dialog dialog=new Dialog(MainSplashScreen.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//Goes into mainactivity the whole page again,just like when it there is no internet connection available. 
								startTimerAndRedirect();
						}
					});
					dialog.show();
				}
				
			}
		});
	}

	@Override
	public void myTimeout(String requestURL) {

	}
}
