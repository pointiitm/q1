package com.quopn.wallet;

/**
 *@author Sumeet
 *@modified by : Ravishankar and code optimised on 03/10/2014
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.imageloading.RoundedImageView;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.QuopnTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import eu.janmuller.android.simplecropimage.CropImage;

public class RegistrationScreen extends Activity implements	ConnectionListener, OnClickListener {
	private static final String TAG="RegistrationScreen";
	private static final int MESSAGE_SEND_REGISTRATION_REQUEST_TO_SERVER = 0x0001;
    private static final int RESPONSE_REGISTRATION_REQUEST_ERROR = 0x0101;
	private static final int RESPONSE_REGISTRATION_REQUEST_SUCCESS = 0x0102;
	private static final int REQUEST_PROFILE_PIC_DIALOG = 0x0103;
	    
    private static final int CAMERA_CAPTURE = 1;// keep track of camera capture intent
	private static final int TAKE_FROM_GALLERY = 2;	// keep track of gallery capture intent
	private static final int PIC_CROP = 3;	// keep track of cropping intent
	private static final int MEDIA_TYPE_IMAGE = 1;// captured picture uri
	private int previousRequestCode = 0;

	private Uri mPicUri;
	private CustomProgressDialog mProgressDialog;
	private Handler mRegistrationHandler;
	
	private RoundedImageView mProfileImageView;
	private EditText mEditName, mEditMobile;
	private QuopnTextView mGreetingText;
	private Context context;
	private TextView terms_privacypolicy_text1,terms_privacypolicy_text2,terms_privacypolicy_text3,terms_privacypolicy_text4;
	private boolean morethantwicepressed;
	private static final int TIME_INTERVAL = 3000; // # milliseconds, desired time passed between two back presses.
	private ConnectionFactory mConnectionFactory;
	private Map<String, String> params;
	private AnalysisManager mAnalysisManager;
	private int T_AND_C = 0;
	private int PRIV_POLICY = 1;
	private String profilePicPath="";
	private TextView txtSubmit;

	private String regId;
	private GoogleCloudMessaging gcm;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	private LocationManager locationManager;
	private boolean isNetworkProvider;
	private Location location;
	private boolean isGPSProvider;
	private static final long MIN_TIME_BW_UPDATES = 600000;
	private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 600000;
	private double latitude;
	private double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration_screen);
		context=this;

		QuopnApplication quopnApp = (QuopnApplication) getApplication();
		quopnApp.generateSessionId();
		mAnalysisManager = ((QuopnApplication)getApplicationContext()).getAnalysisManager();
		
		boolean isFirstLaunch = PreferenceUtil.getInstance(getApplicationContext()).getPreference_boolean(PreferenceUtil.SHARED_PREF_KEYS.IS_FIRSTLAUNCH);
		 Log.v("Thread","isFirstLaunch value in MainSplash Screen"+isFirstLaunch);
		 
		if(!isFirstLaunch){ 
			if (QuopnUtils.isInternetAvailable(RegistrationScreen.this)){
				params = new HashMap<String, String>();
				params.put("device_id", QuopnConstants.android_id);
				mConnectionFactory = new ConnectionFactory(this, this);
				mConnectionFactory.setPostParams(params);
				mConnectionFactory.createConnection(QuopnConstants.IS_FIRSTINSTALL_TRACKER);
		        PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_FIRSTLAUNCH,true);
		   	 	Log.v("Thread","Install Tracker Request send to server by Mainsplash screen");
			}else {
				// Ignore
			}
		}
		
		init(); //Initialize all the member variable
		mEditName.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						mEditMobile.requestFocus();
						return true;
					}
				}
				return false;
			}
		});
		mEditMobile.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {
			            if (keyCode == KeyEvent.KEYCODE_ENTER) {
			               getRegister();
			                return true;
			            }
			        }
			        return false;
			}
		});

		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			//Log.d("RegisterActivity", "GCM RegId: " + regId);
		} else {
			//Log.d("RegisterActivity", "GCM RegId: " + regId);
		}

		location = getLocation();
		
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {
			registerInBackground();
//			Log.d("RegisterActivity",
//					"registerGCM - successfully registered with GCM server - regId: "
//							+ regId);
		} else {
			registerInBackground(); //as per server side changes.
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
//			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
//			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(QuopnConstants.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;
//                    sendNotificationID(regId);
					//storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
//					Log.d("RegisterActivity", "Error: " + msg);
				}
//				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				/*Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();*/
			}
		}.execute(null, null, null);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
//			Log.d("RegisterActivity",
//					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	public void showAboutScreen(int argCode){
		if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
			Intent aboutIntent = new Intent(context, AboutUsActivity.class);
			aboutIntent.putExtra(QuopnConstants.ABOUT_US_GROUP_TO_EXPAND, argCode);
			startActivityForResult(aboutIntent,
					QuopnConstants.HOME_PRESS);
		}
	}

	private void init(){
		
		txtSubmit = ((TextView) findViewById(R.id.submit));
		txtSubmit.setOnClickListener(this);
		
		terms_privacypolicy_text1=(QuopnTextView)findViewById(R.id.terms_privacypolicy_text1);
		terms_privacypolicy_text2=(QuopnTextView)findViewById(R.id.terms_privacypolicy_text2);
		terms_privacypolicy_text3=(QuopnTextView)findViewById(R.id.terms_privacypolicy_text3);
		terms_privacypolicy_text4=(QuopnTextView)findViewById(R.id.terms_privacypolicy_text4);
		terms_privacypolicy_text1.setText(R.string.registration_bottom_first_text);
		terms_privacypolicy_text3.setText(R.string.registration_bottom_third_text);
		terms_privacypolicy_text2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*Intent webview=new Intent(RegistrationScreen.this, WebViewActivity.class);
				webview.putExtra("url", QuopnApi.TERMS_AND_CONDITION_URL);
				startActivity(webview);*/
				showAboutScreen(T_AND_C);
			}
		});
		
		terms_privacypolicy_text4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Intent webview=new Intent(RegistrationScreen.this, WebViewActivity.class);
				webview.putExtra("url", QuopnApi.PRIVACY_POLICY_URL);
				startActivity(webview);*/
				showAboutScreen(PRIV_POLICY);
			}
		});

		mEditMobile = (EditText) findViewById(R.id.userMobileNumber);
		mEditMobile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditMobile.setError(null);
			}
		});
		mEditName = (EditText) findViewById(R.id.userName);
		mEditName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditName.setError(null);
			}
		});
		
		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false); // ankur
		mProgressDialog.setTitle(R.string.progress_registering_txt);

		
		mGreetingText = (QuopnTextView)findViewById(R.id.greetingText);
		mGreetingText.setText(getDayGreeting());
		
		//This is is for developer side to know which build is currently working i.e demo or server
		 mGreetingText.setClickable(true);
		mGreetingText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (morethantwicepressed) {
				        Toast.makeText(RegistrationScreen.this, "This is "+QuopnApi.SERVER_PATH_SERVICES.substring(7, 11)+" server & app version number is "+getVersion(RegistrationScreen.this), Toast.LENGTH_SHORT).show();
				        morethantwicepressed=false;
				        mGreetingText.setClickable(false);
				        return;
				    }
				
				 new Handler().postDelayed(new Runnable() {
				        @Override
				        public void run() {
				            morethantwicepressed=true;                       
				        }
				    }, TIME_INTERVAL);
			}
		});
		mProfileImageView = (RoundedImageView) findViewById(R.id.userProfilePicture);
		
		mProfileImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				mRegistrationHandler.sendEmptyMessage(REQUEST_PROFILE_PIC_DIALOG);
				QuopnConstants.PROFILE_SAVE_FLAG = false;
				final Dialog dialog = new Dialog(RegistrationScreen.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				dialog.setContentView(R.layout.dialog_profile_pic);
				
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialog.setTitle("Select");
				dialog.show();

				Button buttonCamera = (Button) dialog
						.findViewById(R.id.buttonCamera);
				buttonCamera.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						previousRequestCode = CAMERA_CAPTURE;
						try {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							mPicUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
							
							if (intent.resolveActivity(context.getPackageManager()) == null) {
								Toast.makeText(context,
										"There are no applications to handle your request",
										Toast.LENGTH_LONG).show();
							} else {
								startActivityForResult(intent, CAMERA_CAPTURE);
							}
							
							dialog.dismiss();
						} catch (ActivityNotFoundException anfe) {
							// display an error message
							String errorMessage = "Whoops - your device doesn't support capturing images!";
							 com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_profile,errorMessage); 
							 dialog.show();

						}
					}
				});

				Button buttonGallery = (Button) dialog
						.findViewById(R.id.buttonGallery);
				buttonGallery.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						previousRequestCode = TAKE_FROM_GALLERY;
						try {
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							
							if (i.resolveActivity(context.getPackageManager()) == null) {
								Toast.makeText(context,
										"There are no applications to handle your request",
										Toast.LENGTH_LONG).show();
							} else {
								startActivityForResult(i, TAKE_FROM_GALLERY);
							}
							
							dialog.dismiss();
						} catch (ActivityNotFoundException anfe) {
							// display an error message
							String errorMessage = "Whoops - your device doesn't support capturing images!";
							 com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_profile,errorMessage); 
							 dialog.show();
						}
					}
				});
			}
		});
		
		mRegistrationHandler = new Handler() {
			@Override
			public void handleMessage(Message msssage) {
				switch (msssage.what) {
	            case MESSAGE_SEND_REGISTRATION_REQUEST_TO_SERVER:
	            	if( mProgressDialog!=null||(!mProgressDialog.isShowing())){
	            		//mProgressDialog.setMessage("Connecting...");
//	        			mProgressDialog.show();
	            	}
//	    			registringOnServer();
	    			break;
	            case RESPONSE_REGISTRATION_REQUEST_SUCCESS :
	            	if(mProgressDialog.isShowing() || mProgressDialog!=null){
	            	//mProgressDialog.setMessage("Done");
	            	mProgressDialog.dismiss();
	            	}
	            	break;
	            case RESPONSE_REGISTRATION_REQUEST_ERROR :
	            	if(mProgressDialog.isShowing() || mProgressDialog!=null){
	            	//mProgressDialog.setMessage("Error");
	            	mProgressDialog.dismiss();
	            	}
	            	break;
	            case REQUEST_PROFILE_PIC_DIALOG :

	            	break;
	}
			}
		};
	}
	
	public int getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }
	
	private String getDayGreeting(){
		int hrs = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		String greeting = "";
		if (hrs >=  0 && hrs <12) greeting = "GOOD MORNING \n WELCOME TO";      // 
		if (hrs >= 12 && hrs <17) greeting = "GOOD AFTERNOON \n WELCOME TO";    // After 12pm
		if (hrs >= 17 && hrs <=23) greeting = "GOOD EVENING \n WELCOME TO";      // After 5pm
//		Log.v(TAG, "Greeting Message"+greeting);
		return greeting;
	}
	
	private void getRegister() {

		String name=mEditName.getText().toString().trim();
		String mobileno=mEditMobile.getText().toString();
		
		if(!QuopnUtils.isInternetAvailable(this)){
			com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet); 
			dialog.show();
			return;
		}

		if (TextUtils.isEmpty(name)) {
			Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_name_validation, mEditName, null, 0);
			return;
		} else if (!Validations.isValidName(name)) {
			Validations.CustomErrorMessage(getApplicationContext(), R.string.name_validation, mEditName, null, 0);
			return;
		}
		else if( (name.startsWith(" ") && name.endsWith(" ")) || (name.startsWith(" ")) || (name.endsWith(" "))  ){
			mEditName.setText(name.trim());
			return;
		}else if(TextUtils.isEmpty(mobileno)) {
			Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_mobile_no, mEditMobile, null, 0);
			return;
		}else if ( (!mobileno.matches(QuopnConstants.MOBILEPATTERN))) {
			Validations.CustomErrorMessage(getApplicationContext(), R.string.mobileno_validation, mEditMobile, null, 0);
			return;
		}else if(!Validations.validateFirstCharOfMobNo(mobileno)) {
			Validations.CustomErrorMessage(getApplicationContext(), R.string.mobileno_validation, mEditMobile, null, 0);
			return;
		}else{
			registringOnServer();
//			mRegistrationHandler.sendEmptyMessage(MESSAGE_SEND_REGISTRATION_REQUEST_TO_SERVER);

			// Set analysis manager to set that the mobile number if registration is completed successfully
			mAnalysisManager.send(AnalysisEvents.MOBILE_SUBMITTED, mobileno);
		}
			
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSProvider = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkProvider = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSProvider && !isNetworkProvider) {
				//mCustomProgressDialog.dismiss();
				showLocationSettingsDialog();
			} else {
				if (isNetworkProvider) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				else if (isGPSProvider) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	public void showLocationSettingsDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		alertDialog.setTitle("Location Service");

		alertDialog.setMessage(getString(R.string.location_service));
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				Intent i = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

				if (i.resolveActivity(RegistrationScreen.this.getPackageManager()) == null) {
					Toast.makeText(RegistrationScreen.this,
							"There are no applications to handle your request",
							Toast.LENGTH_LONG).show();
				} else {
					startActivityForResult(i, 0);

				}

			}
		});
		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	private void registringOnServer(){
		if (QuopnUtils.isInternetAvailable(RegistrationScreen.this)) {
			//ankur
			if (mProgressDialog != null) {
				mProgressDialog.show();
			}

			// IMEI
			TelephonyManager telephonyManager;
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imei = "";
			if (telephonyManager != null) {
				imei = telephonyManager.getDeviceId();
				if (imei == null) {
					imei = "";
				}
			}


			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnApi.ParamKey.NAME, mEditName.getText().toString().trim());
			params.put(QuopnApi.ParamKey.MOBILE_NO, mEditMobile.getText().toString());
			if(QuopnConstants.PROFILE_PIC_DATA != null){
				params.put("pic", QuopnConstants.PROFILE_PIC_DATA);
			}else{
				params.put("pic", "");
			}
			params.put(QuopnApi.ParamKey.DEVICE_ID, QuopnConstants.android_id);
			params.put("notification_token", regId);
			params.put("lat", "" + latitude);
			params.put("long", "" + longitude);
			params.put("imei",imei);
			params.put(QuopnApi.ParamKey.APP_VERSION, Integer.toString(QuopnUtils.getAppVersionCode()));
			params.put(QuopnApi.ParamKey.APP_VERSION_NAME, QuopnUtils.getAppVersionCode_Name());
			ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.REGISTRATION_CODE);
		} else {
			 com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			 dialog.show();
		}
	}
        
	public void saveProfileImagePath(String argImagePath) {
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_IMAGE_PATH, argImagePath);
	}
	
	private void loadImageFromStorage(String path)
	{

	    try {
	        File f=new File(path, QuopnConstants.PROFILE_IMG_NAME);
	        
	        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//	        mProfileImageView.setImageBitmap(QuopnUtils.getCircleBitmap(b));
	        mProfileImageView.setImageBitmap(b);
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

	}
	
	private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir(QuopnConstants.PROFILE_IMG_FOLDER, Context.MODE_PRIVATE);
        File mypath=new File(directory,QuopnConstants.PROFILE_IMG_NAME);    // Create imageDir
        FileOutputStream fos = null;
        try {           
            fos = new FileOutputStream(mypath);
       // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

	// handle the server response for registration
	@Override
	public void onResponse(int responseResult, Response response) {
//		Log.v(TAG, "Get Response from server");

		// ankur
		if( mProgressDialog !=null || mProgressDialog.isShowing() ){
			mProgressDialog.dismiss();
		}

		switch(responseResult){
		case RESPONSE_OK :
			if(response instanceof ProfileData){
				ProfileData registerResponse = (ProfileData) response;
				if (registerResponse.isError() == true) {

//				 		mRegistrationHandler.sendEmptyMessage(RESPONSE_REGISTRATION_REQUEST_ERROR);
					com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_error,registerResponse.getMessage());
					dialog.show();
				} else {
					User user = registerResponse.getUser();
					
					String gender = user.getGender();
					String dob = user.getDob();
					String stateId = null;
					String cityId = null;
					
					if(user.getState() != null){
						stateId = user.getState();
						cityId = user.getCity();
					}
					
					Gson gson = new Gson();
					QuopnConstants.PROFILE_DATA = gson.toJson(response);
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY, user.getApi_key());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY, user.getWalletid());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USERNAME_KEY, user.getUsername());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY, user.getMobile());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.EMAIL_KEY, user.getEmailid());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY, user.getPIN());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_ID, user.getUserid());
//					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.DEFAULT_WALLET_KEY, user.getDefaultWallet());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_WALLETS_KEY, user.getMobile_wallets());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.ACCESS_TOKEN_KEY, user.getAccess_token());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.REFRESH_TOEKN_KEY, user.getRefresh_token());
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.CITRUS_EMAIL_KEY, user.getCitrus_email());


					// settings for citrus/wallet
//					QuopnApplication.getInstance().setDefaultWallet(QuopnUtils.walletTypeFromInt(Integer.parseInt(user.getDefaultWallet())));
					QuopnUtils.setDefaultWalletInAppAndPref(user.getDefaultWallet(),getApplicationContext());
					
					if(gender == null  || dob == null || stateId == null || cityId == null ){
						Intent intent = new Intent(this, ProfileCompletionScreen.class);
						intent.putExtra(QuopnConstants.INTENT_EXTRA_USERID, user.getUserid());
						intent.putExtra(QuopnConstants.INTENT_EXTRA_MOBILE_NO, mEditMobile.getText().toString());
						intent.putExtra(QuopnConstants.INTENT_EXTRA_OTP_NO, user.getPIN());
						startActivity(intent);
						finish();
					} else{
//						mRegistrationHandler.sendEmptyMessage(RESPONSE_REGISTRATION_REQUEST_SUCCESS);
						PreferenceUtil.getInstance(getApplicationContext()).setPreference(QuopnConstants.INTENT_EXTRA_MOBILE_NO, mEditMobile.getText().toString());
						Intent otp = new Intent(this, OTPScreen.class);
						otp.putExtra(QuopnConstants.INTENT_EXTRA_USERID, user.getUserid());
						otp.putExtra(QuopnConstants.INTENT_EXTRA_MOBILE_NO, mEditMobile.getText().toString());
						otp.putExtra(QuopnConstants.INTENT_EXTRA_OTP_NO, user.getPIN());
						startActivity(otp);
						
						if (mProgressDialog.isShowing()) { mProgressDialog.dismiss(); }
						this.finish();
					}
				}
			}
			break;
		case CONNECTION_ERROR :
			com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.server_error_title,R.string.server_error);
			dialog.show();
			break;
		case PARSE_ERR0R :
			com.gc.materialdesign.widgets.Dialog dialog1=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.server_error_title,R.string.server_error);
			dialog1.show();

			break;
		}
	
		
	}
	
	/**mUserProfilePicture
	 * Handle user returning from both capturing and cropping the image
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// user is returning from capturing an image using the camera
			if (requestCode == CAMERA_CAPTURE) {
				// carry out the crop operation
//				performCrop();
				startCropImage(mPicUri);
			} else if (requestCode == TAKE_FROM_GALLERY) {
				mPicUri = data.getData();
//				performCrop();
				startCropImage(mPicUri);
			}
			// user is returning from cropping the image
//			else if (requestCode == PIC_CROP) {
//				Bundle extras = data.getExtras();// get the returned data
//				if(extras == null){
//					com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, ""/*R.string.dialog_title_registration*/,R.string.image_capture_failed);
//					dialog.show();
//				} else{
////					Bitmap thePic = extras.getParcelable("data");// get the cropped bitmap
////					Uri imageUri = extras.getParcelable(CropImage.IMAGE_PATH);
//			        Bitmap thePic;
//			        try {
//						thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mPicUri);
//						 profilePicPath = saveToInternalSorage(thePic);
//						saveProfileImagePath(profilePicPath);
//						loadImageFromStorage(profilePicPath);
//						ByteArrayOutputStream bao = new ByteArrayOutputStream();
//						Bitmap bitmap = ((BitmapDrawable) mProfileImageView.getDrawable())
//						.getBitmap();
//						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
//						byte[] ba = bao.toByteArray();
//						byte[] picByte = Base64.encode(ba, 0);
//						  
//						QuopnConstants.PROFILE_PIC_DATA = new String(picByte);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				
//				}
//			} 
			
			else if (requestCode == PIC_CROP) {
				 String path = data.getStringExtra(CropImage.IMAGE_PATH);
	                if (path == null) {
	                	com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_capture_failed);
						dialog.show();
	                }else{
	                	Bitmap thePic = BitmapFactory.decodeFile(path);
	                	profilePicPath = saveToInternalSorage(thePic);
						saveProfileImagePath(profilePicPath);
						loadImageFromStorage(profilePicPath);
						
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						Bitmap bitmap = ((BitmapDrawable) mProfileImageView.getDrawable())
						.getBitmap();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao); 
						byte[] ba = bao.toByteArray(); byte[] picByte = Base64.encode(ba, 0);
						  
						QuopnConstants.PROFILE_PIC_DATA = new String(picByte);
	                }
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == RESULT_CANCELED) {
			if (requestCode == CAMERA_CAPTURE) {
				com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_capture_cancelled); 
				dialog.show();
			} else if (requestCode == TAKE_FROM_GALLERY) {
				com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_selection_cancelled); 
				dialog.show();
			} else if (requestCode == PIC_CROP) {
				if(previousRequestCode == CAMERA_CAPTURE){
					com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_capture_cancelled); 
					dialog.show();
				} else if(previousRequestCode == TAKE_FROM_GALLERY){
					com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_selection_cancelled); 
					dialog.show();
				}
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.dialog_title_registration,R.string.image_capture_failed); 
			dialog.show();
		}
	}
	
	 private void startCropImage(Uri picUri) {
		 	try {
		 		
		 		if(picUri.toString().contains("file")){
		 			String path = picUri.toString(); // "file:///mnt/sdcard/FileName.mp3"
		 			File file = new File(new URI(path));
					Intent intent = new Intent(RegistrationScreen.this, CropImage.class);
			        intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
			        intent.putExtra(CropImage.SCALE, true);

			        intent.putExtra(CropImage.ASPECT_X, 7);
			        intent.putExtra(CropImage.ASPECT_Y, 7);

			        startActivityForResult(intent, PIC_CROP);
		 		}else{
		 			String path = getRealPathFromURI(picUri).toString(); // "file:///mnt/sdcard/FileName.mp3"
		 			path=path.replace(" ", "%20");
		 			if(path.contains("WhatsApp")){
		 				File file = new File(new URI("file://"+path));
		 				Intent intent = new Intent(RegistrationScreen.this, CropImage.class);
				        intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
				        intent.putExtra(CropImage.SCALE, true);

				        intent.putExtra(CropImage.ASPECT_X, 7);
				        intent.putExtra(CropImage.ASPECT_Y, 7);

				        startActivityForResult(intent, PIC_CROP);
		 			}else{
		 				File file = new File(new URI("file:///"+path));
		 				Intent intent = new Intent(RegistrationScreen.this, CropImage.class);
				        intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
				        intent.putExtra(CropImage.SCALE, true);

				        intent.putExtra(CropImage.ASPECT_X, 7);
				        intent.putExtra(CropImage.ASPECT_Y, 7);

				        startActivityForResult(intent, PIC_CROP);
		 			}
		 			
					
		 		}
		 		
				
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	 
	 public String getRealPathFromURI(Uri contentUri) 
	 {
	      String[] proj = { MediaStore.Audio.Media.DATA };
	      Cursor cursor = RegistrationScreen.this.managedQuery(contentUri, proj, null, null, null);
	      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	      cursor.moveToFirst();
	      return cursor.getString(column_index);
	 }

	/**
	 * Helper method to carry out crop operation
	 */
	private void performCrop() {
		// take care of exceptions
		try {
//			Log.v(TAG, "Going => for cropping profile picture");
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri & set crop properties
			cropIntent.setDataAndType(mPicUri, "image/*");
			cropIntent.putExtra("crop", "false");
			cropIntent.putExtra("circleCrop", "circleCrop");
			cropIntent.putExtra("aspectX", 1);// indicate aspect of desired crop
			cropIntent.putExtra("aspectY", 1);// indicate aspect of desired crop
			cropIntent.putExtra("outputX", 256);// indicate output X and Y
			cropIntent.putExtra("outputY", 256);// indicate output X and Y
			cropIntent.putExtra("return-data", true);// indicate output X and Y
			// start the activity - we handle returning in onActivityResult
			
			if (cropIntent.resolveActivity(RegistrationScreen.this.getPackageManager()) == null) {
				Toast.makeText(RegistrationScreen.this,
						"There are no applications to handle your request",
						Toast.LENGTH_LONG).show();
			} else {
				startActivityForResult(cropIntent, PIC_CROP);
			}
			
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			// display an error if there is no camera available
			Toast.makeText(this, getString(R.string.error_no_camera_support), Toast.LENGTH_SHORT).show();;

		}
	}

	/*
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		try {
			return Uri.fromFile(getOutputMediaFile(type));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
		
	}

	/**
	 * Returns Image File URI*/
	private static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(Environment	.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				QuopnConstants.IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
//				Log.d(QuopnConstants.IMAGE_DIRECTORY_NAME,
//						"Oops! Failed create "
//								+ QuopnConstants.IMAGE_DIRECTORY_NAME
//								+ " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		/*case R.id.pickupImage:
			previousRequestCode = CAMERA_CAPTURE;
			try {
				Intent intentImageCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mPicUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				intentImageCapture.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
				if (intentImageCapture.resolveActivity(RegistrationScreen.this.getPackageManager()) == null) {
					Toast.makeText(RegistrationScreen.this,
							"There are no applications to handle your request",
							Toast.LENGTH_LONG).show();
				} else {
					startActivityForResult(intentImageCapture, CAMERA_CAPTURE);
				}
				
			} catch (ActivityNotFoundException anfe) {
				AlertManager.show(mDialogConfigData);
			}
			break;*/
		/*case R.id.userProfilePicture:
			previousRequestCode = TAKE_FROM_GALLERY;
			try {
				Intent intentImagePickFromGallery  = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				if (intentImagePickFromGallery.resolveActivity(RegistrationScreen.this.getPackageManager()) == null) {
					Toast.makeText(RegistrationScreen.this,
							"There are no applications to handle your request",
							Toast.LENGTH_LONG).show();
				} else {
					startActivityForResult(intentImagePickFromGallery, TAKE_FROM_GALLERY);
				}
				
			} catch (ActivityNotFoundException anfe) {
				AlertManager.show(mDialogConfigData);
			}
			break;*/
		case R.id.submit:
			getRegister();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(RegistrationScreen.this, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
					dialog.show();
				}
				
			}
		});
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}
}