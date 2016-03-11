package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.citrus.sdk.classes.AccessToken;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.DataBaseHelper;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class QuopnApplication extends Application {
	  // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-57562119-1";
	private static QuopnApplication instance;
	private AnalysisManager mAnalysisManager;
	private String sessionId;
	private Timer timer;
	private TimerTask timerTask;
	private HashMap<Integer,Bitmap> bitmapHashMap = new HashMap<Integer,Bitmap>();

	// citrus
	private Boolean isCitrusInitiated = false;
	private QuopnConstants.WalletType currentWalletMode = QuopnConstants.WalletType.NONE;
	private QuopnConstants.WalletType defaultWallet = QuopnConstants.WalletType.NONE;
	private AccessToken accessToken;

	public enum TrackerName {
		    APP_TRACKER, // Tracker used only in this app.
		    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
		  }

	 HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	private static RequestQueue sRequestQueue;
	private List<Fragment> mFragmentsStack = new ArrayList<Fragment>();
	private List<Fragment> mFragmentsStackMenu = new ArrayList<Fragment>();

	public List<Fragment> getFragmentsStack() {
		return mFragmentsStack;
	}

	public List<Fragment> getFragmentsStack_menu() {
		return mFragmentsStackMenu;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		generateSessionId();
		sRequestQueue = Volley.newRequestQueue(getApplicationContext());
		initImageLoader();
		mAnalysisManager = new AnalysisManager(getApplicationContext());
//		startTimer();

		// initiating Logger
		if (QuopnApi.currentMode != QuopnApi.Mode.PROD) {
			Logger.init().setLogLevel(com.orhanobut.logger.LogLevel.FULL).hideThreadInfo();
		} else {
			Logger.init().setLogLevel(com.orhanobut.logger.LogLevel.NONE).hideThreadInfo();
		}
		instance = this;
	}

	public void generateSessionId(){
		String storedSessId = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID);
		if(TextUtils.isEmpty(storedSessId)){
			sessionId = createSessionId();
			saveSessionId(); // ankur
		} else{
			sessionId = storedSessId;
		}
	}

	public static QuopnApplication getInstance() {
		return instance;
	}

	public String getSessionId(){
		return sessionId;
	}

	public AnalysisManager getAnalysisManager(){
		return mAnalysisManager;
	}

	public static RequestQueue getRequestQueue() {
		return sRequestQueue;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	private void initImageLoader() {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app

				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}
	
	/**Google Analytics code*/
	public synchronized Tracker getTracker(TrackerName trackerId) {
	    if (!mTrackers.containsKey(trackerId)) {

	      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	      Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
	          : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
	              : analytics.newTracker(R.xml.global_tracker);
	      
	      mTrackers.put(trackerId, t);

	   // Set the log level to verbose.
	          analytics.getLogger().setLogLevel(LogLevel.VERBOSE);
	      
	    }
	    return mTrackers.get(trackerId);
  	}

	private String createSessionId(){
		sessionId = UUID.randomUUID().toString();
		return sessionId;
	}

	public void startTimer() {
		timer = new Timer();
		initializeTimerTask();
		//schedule the timer, after the first 0ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 1800000, 1800000); //
	}

	public void stoptimertask() {
		//stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
				Intent changeSessIdIntent = new Intent(QuopnConstants.BROADCAST_CHANGE_SESSION_ID);
				changeSessIdIntent.putExtra(QuopnConstants.INTENT_KEYS.x_session_old, sessionId);
				sessionId = createSessionId();                                  //Pls don't change position of this method call
				changeSessIdIntent.putExtra(QuopnConstants.INTENT_KEYS.x_session_new, sessionId);
				localBrdcastMgr.sendBroadcast(changeSessIdIntent);
			}
		};
	}

	public void saveSessionId(){
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID, sessionId);
		Log.d("QuopnApplication", "PreferenceUtil SESSION_ID "+PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID));
	}

	public void downloadBitmaps(HashMap<Integer, String> storeTypeHashMap) {
		Log.d("Bitmap", "downloadBitmaps started");
		if (!storeTypeHashMap.isEmpty()) {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					String msg = "";
					// cleaning bitmaphashmap
					Log.d("Bitmap","downloadBitmaps - entry");
					if (!bitmapHashMap.isEmpty()) {
						bitmapHashMap.clear();
					}
					DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
					DisplayImageOptions mOptions = builder.cacheInMemory(true).cacheOnDisk(true).build();
					DataBaseHelper dbHelper =  DataBaseHelper.getInstance(QuopnApplication.getInstance());
					for (final Map.Entry<Integer, String> entry : dbHelper.getStoreTypeHashMap().entrySet()) {
						Log.d("Bitmap", "downloadBitmaps ImageLoadingListener started for "+entry.getKey());
						if (entry.getValue() != null) {
//					Bitmap bitmap = ImageLoader.getInstance().loadImageSync(entry.getValue(), mOptions);
							Log.d("Bitmap","downloadBitmaps - downloading");
							ImageLoader.getInstance().loadImage(entry.getValue(), mOptions, new ImageLoadingListener() {
								@Override
								public void onLoadingStarted(String s, View view) {
									Log.d("Bitmap","downloadBitmaps onLoadingStarted for "+s);
								}

								@Override
								public void onLoadingFailed(String s, View view, FailReason failReason) {
									Log.d("Bitmap","downloadBitmaps onLoadingFailed for key: "+entry.getKey()+ " and reason:"+failReason.getType().name());
									Bitmap resBitmap=null;
									switch (entry.getKey()) {
										case 1:
											// general
											resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
													R.drawable.generalstore_pin_ico);
											break;
										case 2:
											//medical
											resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
													R.drawable.wellnessstore_pin_ico);
											break;
										case 3:
											//hypercity
											resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
													R.drawable.hyperlstore_pin_ico);
											break;
										default:
											resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
													R.drawable.generalstore_pin_ico);
											break;
									}
									if (resBitmap!=null) {
										bitmapHashMap.put(entry.getKey(), getScaledBitmap(resBitmap));
									}
								}

								@Override
								public void onLoadingComplete(String s, View view, Bitmap bitmap) {
									Log.d("Bitmap","downloadBitmaps onLoadingComplete for key:"+entry.getKey());
									if (bitmap != null) {
										bitmapHashMap.put(entry.getKey(), getScaledBitmap(bitmap));
									} else {
										Bitmap resBitmap=null;
										switch (entry.getKey()) {
											case 1:
												// general
												resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
														R.drawable.generalstore_pin_ico);
												break;
											case 2:
												//medical
												resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
														R.drawable.wellnessstore_pin_ico);
												break;
											case 3:
												//hypercity
												resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
														R.drawable.hyperlstore_pin_ico);
												break;
											default:
												resBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
														R.drawable.generalstore_pin_ico);
												break;
										}
										if (resBitmap!=null) {
											bitmapHashMap.put(entry.getKey(), getScaledBitmap(resBitmap));
										}
									}
								}

								@Override
								public void onLoadingCancelled(String s, View view) {

								}
							});
						} else {
							Log.d("Bitmap", "downloadBitmaps entry.getValue() = null for key: "+entry.getKey());
						}
					}
					return msg;
				}

				@Override
				protected void onPostExecute(String msg) {
				/*Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();*/
				}
			}.execute(null, null, null);
		} else {
			Log.e("Bitmap", "downloadBitmaps storetypehashmap is empty");
		}
		Log.d("Bitmap", "downloadBitmaps exiting");
	}

	public HashMap<Integer, Bitmap> getBitmapHashMap() {
		return bitmapHashMap;
	}

	public void setBitmapHashMap(HashMap<Integer, Bitmap> bitmapHashMap) {
		this.bitmapHashMap = bitmapHashMap;
	}

	private Bitmap getScaledBitmap (Bitmap bitmapOrg) {

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		float scaleWidth = getDeviceScale()*0.8f;
		float scaleHeight = getDeviceScale()*0.8f;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		if (resizedBitmap == null) {
			Log.d("Bitmap", "resizedBitmap == null");
		}
		return (resizedBitmap != null) ? resizedBitmap : bitmapOrg;
	}

	public float getDeviceScale () {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		if (windowManager != null) {
			windowManager.getDefaultDisplay().getMetrics(metrics);

			return metrics.scaledDensity;
		} else {
			return 1.0f;
		}
	}

	// Citrus
	public Boolean getIsCitrusInitiated() {
		return isCitrusInitiated;
	}

	public void setIsCitrusInitiated(Boolean isCitrusInitiated) {
		this.isCitrusInitiated = isCitrusInitiated;
	}

	public QuopnConstants.WalletType getCurrentWalletMode() {
		return currentWalletMode;
	}

	public void setCurrentWalletMode(QuopnConstants.WalletType currentWalletMode) {
		this.currentWalletMode = currentWalletMode;
		if (currentWalletMode == QuopnConstants.WalletType.NONE) {
			Logger.e("NONE currentwalletmode");
		}
	}

	public QuopnConstants.WalletType getDefaultWallet() {
		return defaultWallet;
	}

	public void setDefaultWallet(QuopnConstants.WalletType defaultWallet) {
		this.defaultWallet = defaultWallet;
		if (defaultWallet == QuopnConstants.WalletType.NONE) {
			Logger.e("NONE defaultWallet");
		}
	}

	public String getDefaultWalletID () {
		if (this.currentWalletMode == QuopnConstants.WalletType.SHMART) {
			return QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID;
		} else if (this.currentWalletMode == QuopnConstants.WalletType.CITRUS) {
			return QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID;
		} else {
			return QuopnApi.EWalletDefault.MOBILE_WALLET_NONE;
		}
	}

	public static AccessToken getAccessToken() {
		return instance.accessToken;
	}

	public static void setAccessToken(AccessToken accessToken) {
		instance.accessToken = accessToken;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}