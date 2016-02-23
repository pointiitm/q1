package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.LinkUserExtendedResponse;
import com.citrus.sdk.classes.LinkUserPasswordType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.adapter.CategoryListAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.analysis.database.QuopnSQLiteHelper;
import com.quopn.wallet.analysis.scheduler.AlarmReceiver;
import com.quopn.wallet.citrus.CitrusRegn;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CategoryQuopnData;
import com.quopn.wallet.data.model.FooterTag;
import com.quopn.wallet.data.model.Gift;
import com.quopn.wallet.data.model.GiftsContainer;
import com.quopn.wallet.data.model.LogoutData;
import com.quopn.wallet.data.model.NewCategoryData;
import com.quopn.wallet.data.model.NewCategoryList;
import com.quopn.wallet.data.model.NotificationData;
import com.quopn.wallet.data.model.NotifyStatusData;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.QuopnList;
import com.quopn.wallet.data.model.RefreshSessionData;
import com.quopn.wallet.data.model.RequestPinData;
import com.quopn.wallet.data.model.ShmartCheckStatusData;
import com.quopn.wallet.data.model.ShmartGenerateOTPData;
import com.quopn.wallet.data.model.ShmartRequestOTPData;
import com.quopn.wallet.data.model.ShmartVoucherListData;
import com.quopn.wallet.data.model.SingleCartDetails;
import com.quopn.wallet.data.model.UCNNumberData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.data.model.VersionCheckData;
import com.quopn.wallet.data.model.VoucherList;
import com.quopn.wallet.data.model.WebIssueData;
import com.quopn.wallet.data.model.shmart.AnnouncementData;
import com.quopn.wallet.fragments.MainMenuFragment;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.shmart.ShmartFlow;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.walletshmart.ShmartOtp;
import com.quopn.wallet.walletshmart.ShmartRegn;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MainActivity extends BaseActivity implements ConnectionListener, QuopnOperationsListener {
	private static final String TAG = "MainActivity";
	FrameLayout mFrameContainer;
	public String searchText;
	private SearchView mSearchView;
	private ActionBar mQuopnActionBar;
	private ConnectionFactory mConnectionFactory;
	private ImageView mLeftSlider, mHomeButton, mCommonCartButton;
	private TextView mNotification_Counter_tv, mAddtoCard_Counter_tv;
	private View mActionBarView;
	private Map<String, String> params, headerParams;
	//	private ProgressBar progress;
	private ConnectionListener connectionlistener_videoissue;
	private Gson gson = new Gson();
	private User mUser;
	public static String GIFT_TITLE;

	private String stateId;
	private CustomProgressDialog mCustomProgressDialog;
	private boolean isNotFromBroadCast = true;
	private boolean isShownFromCache = false;
	private AnalysisManager mAnalysisManager;
	/*GCM Content */
	private GoogleCloudMessaging gcm;
	private Context context;
	private String regId;
	private ProductCatFragment mProdCatFragment;
	private static ConnectionListener webissue_connectionlistener;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	/*GCM Content ends */
	private AlarmReceiver mAlarmReceiver = new AlarmReceiver();
	//Animation for CartCounter
	Animation m_increaseAnimation;
	Animation m_decreaseAnimation;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	public MainActivity() {
		super(R.string.app_name);
	}

	public static SharedPreferences sharedpreferences;
	private QuopnSQLiteHelper mQuopnSQLiteHelper;
	private Uri uri;
	private ArrayList<QuopnOperationsListener> addQuopnoperationArrayList = new ArrayList<QuopnOperationsListener>();
	private CitrusClient mCitrusClient = null;
	private Context mContext = this;
	private LinkUserExtendedResponse linkUserExtended;
	String editOtpText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Logger.d("");
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		// Loads in extras passed with the activity intent
		setContentView(R.layout.activitysample);

		mCitrusClient = CitrusClient.getInstance(getApplicationContext());
		mCustomProgressDialog = new CustomProgressDialog(MainActivity.this);
		//mCustomProgressDialog.show();

		mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();

		/*this single line inserted on 19/02/2014
		 * user has come to this activity means profile completion screen is completed
		 * */
//    String session_uuid = UUID.randomUUID().toString();
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.PROFILE_COMPLETE_KEY, "YES");
//    PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID,session_uuid);


		context = getApplicationContext();
		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			//Log.d("RegisterActivity", "GCM RegId: " + regId);
		} else {
			//Log.d("RegisterActivity", "GCM RegId: " + regId);
		}

		sharedpreferences = getSharedPreferences("QuopanPreference", Context.MODE_PRIVATE);
		Intent intent = getIntent();
		if (intent.getExtras() != null && intent.getExtras().getString(QuopnConstants.DELETE_NOTIFICATIONS) != null) {
			getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, null, null);
		}

		mQuopnSQLiteHelper = new QuopnSQLiteHelper(MainActivity.this);
		mAlarmReceiver.setAlarm(this);

		mQuopnActionBar = getSupportActionBar();
		mQuopnActionBar.setDisplayHomeAsUpEnabled(false);
		mQuopnActionBar.setDisplayShowTitleEnabled(false);
		mQuopnActionBar.setDisplayShowCustomEnabled(true);
		mQuopnActionBar.setDisplayUseLogoEnabled(false);
		mQuopnActionBar.setDisplayShowHomeEnabled(false);
		mQuopnActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));

		mActionBarView = View.inflate(this, R.layout.actionbar_layout, null);
		mLeftSlider = (ImageView) mActionBarView.findViewById(R.id.slider);
		mHomeButton = (ImageView) mActionBarView.findViewById(R.id.home_btn);
		mCommonCartButton = (ImageView) mActionBarView.findViewById(R.id.cmn_cart_btn);
		mNotification_Counter_tv = (TextView) mActionBarView.findViewById(R.id.notification_counter_txt);
		mAddtoCard_Counter_tv = (TextView) mActionBarView.findViewById(R.id.addtocard_counter_txt);
		//Initialise the Animations
		m_increaseAnimation = AnimationUtils.loadAnimation(this, R.anim.increase);
		m_decreaseAnimation = AnimationUtils.loadAnimation(this, R.anim.decrease);


		QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT);
		Log.i(TAG, "In Oncreate" + QuopnConstants.NOTIFICATION_COUNT);
		if (QuopnConstants.NOTIFICATION_COUNT <= 0) {
			mNotification_Counter_tv.setVisibility(View.INVISIBLE);
		} else {
			mNotification_Counter_tv.setVisibility(View.VISIBLE);
			mNotification_Counter_tv.setText("" + QuopnConstants.NOTIFICATION_COUNT);
		}

		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
		mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
		if (QuopnConstants.MY_CART_COUNT <= 0) {
			mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
		} else {
			mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
		}


		mLeftSlider.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSlidingMenu().toggle();
				QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
			}
		});

		mHomeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backeventReceiver(false);
			}
		});

		mCommonCartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSlidingMenu().showSecondaryMenu(true);
			}
		});

		getSupportActionBar().setCustomView(mActionBarView);
		getSupportActionBar().setElevation(0);

		mFrameContainer = (FrameLayout) findViewById(R.id.fram_container);
//		progress = (ProgressBar) findViewById(R.id.progress);

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSearchView = (SearchView) findViewById(R.id.fragment_address_search);
		mSearchView.setQueryHint(getResources().getString(R.string.search));

		try {
			Field searchField = SearchView.class.getDeclaredField("mSearchButton");
			searchField.setAccessible(true);
			ImageView searchBtn = (ImageView) searchField.get(mSearchView);
			searchBtn.setImageResource(R.drawable.search_glass);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}

		mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public boolean onClose() {
				mHomeButton.setVisibility(View.VISIBLE);
				mLeftSlider.setVisibility(View.VISIBLE);
				return false;
			}
		});

		mSearchView.setOnSearchClickListener(new OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				mHomeButton.setVisibility(View.INVISIBLE);
				mLeftSlider.setVisibility(View.INVISIBLE);

			}
		});

		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
//				 Log.v(TAG,"QueryText=>"+arg0);
				searchText = arg0;
				QuopnConstants.SEARCHTEXT = arg0;
				mAnalysisManager.send(AnalysisEvents.SEARCH_WORD, arg0);
				mProdCatFragment.notifySearchTextChanged(
						searchText, MainActivity.this);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});

		LocalBroadcastManager.getInstance(this).registerReceiver(message,
				new IntentFilter(QuopnConstants.BROADCAST_UPDATE_QUOPNS));

		LocalBroadcastManager.getInstance(this).registerReceiver(initialmycartcounter,
				new IntentFilter(QuopnConstants.BROADCAST_INITIAL_MYCARTCOUNTER));

		LocalBroadcastManager.getInstance(this).registerReceiver(updatemycartcounter,
				new IntentFilter(QuopnConstants.BROADCAST_UPDATE_MYCARTCOUNTER));

		LocalBroadcastManager.getInstance(this).registerReceiver(notificationcounter,
				new IntentFilter(QuopnConstants.BROADCAST_UPDATE_NOTIFICATIONCOUNTER));

		LocalBroadcastManager.getInstance(this).registerReceiver(receiverShowCart,
				new IntentFilter(QuopnConstants.BROADCAST_SHOW_CART));

		LocalBroadcastManager.getInstance(this).registerReceiver(recSendChangeSessIdCommand,
				new IntentFilter(QuopnConstants.BROADCAST_CHANGE_SESSION_ID));
		((QuopnApplication) getApplicationContext()).startTimer();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiverLogoutInvalidSession,
				new IntentFilter(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION));

		LocalBroadcastManager.getInstance(this).registerReceiver(receiverParseDeepLinks,
				new IntentFilter(QuopnConstants.BROADCAST_PARSE_ANNOUNCEMENT_DEEP_LINKS));

		LocalBroadcastManager.getInstance(this).registerReceiver(receiverParseNotifActivityLinks,
				new IntentFilter(QuopnConstants.BROADCAST_PARSE_NOTIF_ACTIVITY_DEEP_LINKS));

		/**Checking Internet Connection*/
		if (QuopnUtils.isInternetAvailable(MainActivity.this)) {

			// set the fragment in the main view
			mProdCatFragment = new ProductCatFragment();
			getSupportFragmentManager().beginTransaction()
					.addToBackStack("Category")
					.replace(R.id.fram_container, mProdCatFragment, "Category").commit();
			((QuopnApplication) getApplication()).getFragmentsStack().add(mProdCatFragment);

			Cursor cursor = getContentResolver().query(

					ConProvider.CONTENT_URI_CATEGORY, null, null, null, ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE);
			
			/*
			 * Checking if quopns are present in the database.
			 *
			 */

			if (cursor != null && cursor.getCount() >= 1) {

				isShownFromCache = true;
				String strFooter = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.FOOTER_TAGS);
				if (strFooter != null) {
					QuopnConstants.MAIN_BOTTOM_BAR = strFooter.split(",");
				}

				showGiftSection();
			} else {
				getAuthKeyVerify();
			}
			getProfile();


		} else {

			String strFooter = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.FOOTER_TAGS);
			if (strFooter != null) {
				QuopnConstants.MAIN_BOTTOM_BAR = strFooter.split(",");
			}

			// set the fragment in the main view
			mProdCatFragment = new ProductCatFragment();
			getSupportFragmentManager().beginTransaction()
					.addToBackStack("Category")
					.replace(R.id.fram_container, mProdCatFragment, "Category").commit();
			((QuopnApplication) getApplication()).getFragmentsStack().add(mProdCatFragment);

			showGiftSection();
		}

		versionCheck();
//        getVoucherListing();
		if (intent.hasExtra(QuopnConstants.INTENT_KEYS.should_announcement_be_called)) {
			if (intent.getBooleanExtra(QuopnConstants.INTENT_KEYS.should_announcement_be_called, false)) {
				getAnnouncmentUrl();
			}
		} else {
			getAnnouncmentUrl();
		}
		parseDeepLinks();
		parseNotifDeepLinks(getIntent());
		stopCustomProgress();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onPostCreate(savedInstanceState, persistentState);
	}

	public void parseNotifDeepLinks(Intent argIntent) {
		Bundle extras = argIntent.getExtras();

		if (extras != null) {
			Gson gson = new Gson();
			NotificationData data = (NotificationData) gson.fromJson((String) extras.get(QuopnConstants.MESSAGE_KEY), NotificationData.class);

			if (data != null) {
				/*String deepLink = (String) data.getDeepLink();
				parseDeepLinkUri(deepLink);*/
				String screen = QuopnConstants.GCM_DEEP_LINK.allquopns;
				if (data.getScreen() != null) {
					screen = data.getScreen();
				}
				String value = "";
				if (data.getValue() != null) {
					value = data.getValue();
				}
				System.out.println("VAIBHAV IN MAIN ACTIVITY parseNotifDeepLinks() 2 : " + screen + " : " + value);
				String str = QuopnConstants.GCM_DEEP_LINK.SCHEME + "://" + screen + "/" + value;
//				parseDeepLinkUri(str);
				Intent intent = new Intent(this, NotificationActivity.class);
				startActivity(intent);
			}
		}
	}

	public void parseDeepLinksFromNotifActivity(Intent intent) {
		Bundle extras = intent.getExtras();

		if (extras != null) {
			String screen = intent.getStringExtra(QuopnConstants.NOTIF_INTENT_DATA.screen);
			String value = intent.getStringExtra(QuopnConstants.NOTIF_INTENT_DATA.value);
			if (value == null) {
				value = "";
			}
			if (!TextUtils.isEmpty(screen)) {
				String str = QuopnConstants.GCM_DEEP_LINK.SCHEME + "://" + screen + "/" + value;
				parseDeepLinkUri(str);
			}
		}
	}

	public void parseAnnouncementDeepLinks(Intent intent) {
		Bundle extras = intent.getExtras();
		String deepLink = ((Uri) (extras.get(QuopnConstants.GCM_DEEP_LINK.KEY_01))).toString();
		parseDeepLinkUri(deepLink);
	}

	public void parseDeepLinkUri(String deepLink) {
		if (!TextUtils.isEmpty(deepLink)) {
			Uri uri = Uri.parse(deepLink);
			if (uri != null) {
				if (uri.getScheme().equals(QuopnConstants.GCM_DEEP_LINK.SCHEME)) {

					String authority = uri.getAuthority();

					if (authority != null) {
						List<String> listPathSegments = uri.getPathSegments();
						if (authority.equals(QuopnConstants.DEEP_LINK.promo)) {
							String promoCode = "";
							if (listPathSegments != null && listPathSegments.size() > 0) {
								promoCode = listPathSegments.get(0);
							}
							showPromoScreen(promoCode);
						} else if (authority.equals(QuopnConstants.DEEP_LINK.invite)) {
							showInviteScreen();
						} else if (authority.equals(QuopnConstants.DEEP_LINK.campaign)) {
							String campaignId = "";
							if (listPathSegments != null && listPathSegments.size() > 0) {
								campaignId = listPathSegments.get(0);
							}
							if (!TextUtils.isEmpty(campaignId)) {
								Cursor cursor = getContentResolver().query(ConProvider.CONTENT_URI_QUOPN, new String[]{ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID},
										ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ?", new String[]{campaignId}, null);
								//							DatabaseUtils.dumpCursor(cursor);
								cursor.moveToFirst();
								if (cursor != null && cursor.getCount() > 0) {
									campaignId = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID));
									if (!TextUtils.isEmpty(campaignId)) {
										Intent quopIntent = new Intent(this, QuopnDetailsActivity.class);
										quopIntent.putExtra("tag", /*item.getQuopnData1().getQuopnId()*/campaignId);
										View view = new View(this);
										view.setVisibility(View.GONE);
										showQuopnDetails(quopIntent,/*v*/view, /*mQuopnDetailAddToCartListener*/null);
									}
								}
							}
						} else if (authority.equals(QuopnConstants.DEEP_LINK.category)) {
							String categoryId = "";
							if (listPathSegments != null && listPathSegments.size() > 0) {
								categoryId = listPathSegments.get(0);
							}
							if (!TextUtils.isEmpty(categoryId)) {
								Cursor cursor = getContentResolver().query(ConProvider.CONTENT_URI_CATEGORY, new String[]{ITableData.TABLE_CATEGORY.COLUMN_CATEGORY},
										ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID + " = ?", new String[]{categoryId}, null);
								cursor.moveToFirst();
								if (cursor != null && cursor.getCount() > 0) {
									String categoryName = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY));
									if (!TextUtils.isEmpty(categoryName)) {
										showCategoryCoupons(categoryId, categoryName);
									}
								}
							}
						} else if (authority.equals(QuopnConstants.DEEP_LINK.apptour)) {
							showAppTour();
						} else if (authority.equals(QuopnConstants.DEEP_LINK.wallet)) {
//							sendCheckWalletStatus();
//							walletCitrusStatus();
							callCitrus();
						} else if (authority.equals(QuopnConstants.DEEP_LINK.allquopns)) {
						}

					}


				}
			}
		}
	}

	public void parseDeepLinks() {
		Intent intent = getIntent();
		Uri data = intent.getData();
		if (data != null) {
			List<String> listPathSegments = data.getPathSegments();
			if (listPathSegments != null && listPathSegments.size() > 0) {
				if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.promo)) {
					String promoCode = "";
					if (listPathSegments.size() > 1) {
						promoCode = listPathSegments.get(1);
					}
					showPromoScreen(promoCode);
				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.invite)) {
					showInviteScreen();
				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.campaign)) {
					String campaignId = "";
					if (listPathSegments.size() > 1) {
						campaignId = listPathSegments.get(1);
					}
					if (!TextUtils.isEmpty(campaignId)) {
						Cursor cursor = getContentResolver().query(ConProvider.CONTENT_URI_QUOPN, new String[]{ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID},
								ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ?", new String[]{campaignId}, null);
//							DatabaseUtils.dumpCursor(cursor);
						cursor.moveToFirst();
						if (cursor != null && cursor.getCount() > 0) {
							campaignId = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID));
							if (!TextUtils.isEmpty(campaignId)) {
								Intent quopIntent = new Intent(this, QuopnDetailsActivity.class);
								quopIntent.putExtra("tag", /*item.getQuopnData1().getQuopnId()*/campaignId);
								View view = new View(this);
								view.setVisibility(View.GONE);
								showQuopnDetails(quopIntent,/*v*/view, /*mQuopnDetailAddToCartListener*/null);
							}
						}
					}
				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.category)) {
					String categoryId = "";
					if (listPathSegments.size() > 1) {
						categoryId = listPathSegments.get(1);
					}
					if (!TextUtils.isEmpty(categoryId)) {
						Cursor cursor = getContentResolver().query(ConProvider.CONTENT_URI_CATEGORY, new String[]{ITableData.TABLE_CATEGORY.COLUMN_CATEGORY},
								ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID + " = ?", new String[]{categoryId}, null);
						cursor.moveToFirst();
						if (cursor != null && cursor.getCount() > 0) {
							String categoryName = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY));
							if (!TextUtils.isEmpty(categoryName)) {
								showCategoryCoupons(categoryId, categoryName);
							}
						}
					}
				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.apptour)) {
					showAppTour();
				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.wallet)) {
//						sendCheckWalletStatus();
//						walletCitrusStatus();
					new CountDownTimer(2000,1000) {
						@Override
						public void onTick(long millisUntilFinished) {

						}

						@Override
						public void onFinish() {
							callCitrus();
						}
					}.start();

				} else if (listPathSegments.get(0).equals(QuopnConstants.DEEP_LINK.allquopns)) {
					// default screen so no redirect
				} else {
					// handling any other deep link
					// default screen so no redirect
				}
			}
		}
	}

	public void checkShmartWalletShown() {
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment) {
			//sendCheckWalletStatus();
			walletCitrusStatus();
		}
	}

	private void versionCheck() {
		//Log.v(TAG, "*****Version check****");
		String api_key = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY);
		if (QuopnUtils.isInternetAvailable(this)) {
			if (!TextUtils.isEmpty(api_key)) {
				Map<String, String> headerParams = new HashMap<String, String>();
				Map<String, String> postparams = new HashMap<String, String>();
				headerParams.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
				postparams.put("device_id", QuopnConstants.android_id);
				postparams.put("device_os", Build.BRAND);
				postparams.put("os_version", Build.VERSION.RELEASE);
				postparams.put("notification_id", regId);
				try {
					postparams.put("app_version", Integer.toString(QuopnConstants.versionCode));
				} catch (Exception ex) {
					Log.e(TAG, "app version issue in versionCheck, ex.getLocalizedMessage: " + ex.getLocalizedMessage());
				}

				ConnectionFactory mConnectionFactory = new ConnectionFactory(this, this);
				mConnectionFactory.setHeaderParams(headerParams);
				mConnectionFactory.setPostParams(postparams);
				mConnectionFactory.createConnection(QuopnConstants.VERSION_CHECK_CODE);
			} else {
				// show error
			}
		} else {

		}

	}

	private void showGiftSection() {
		/*
		 * Checking if gifts are present in the database.
		 * 
		 */
		Cursor gifts = getContentResolver().query(ConProvider.CONTENT_URI_GIFTS, null, null, null, ITableData.TABLE_GIFTS.SORT_INDEX + " desc");

		if (gifts != null && gifts.getCount() >= 1) {
			GIFT_TITLE = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.GIFT_TITLE);
			mProdCatFragment.isAttached();
			mProdCatFragment.setGiftAvailable(true);
		} else {
			mProdCatFragment.isAttached();
			mProdCatFragment.setGiftAvailable(false);
		}
	}

	public void getProfile() {
		Log.v(TAG, "*****Getting User Profile Data*****");

		if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(
				SHARED_PREF_KEYS.API_KEY))) {

			params = new HashMap<String, String>();
			params.put("Authorization", PreferenceUtil.getInstance(this).getPreference(
					SHARED_PREF_KEYS.API_KEY));
			mConnectionFactory = new ConnectionFactory(this, this);
			mConnectionFactory.setHeaderParams(params);
			mConnectionFactory.createConnection(QuopnConstants.PROFILE_GET_CODE);
		} else {
			// show error
		}
	}

	public void getAnnouncmentUrl() {
		Log.v(TAG, "*****Getting User Profile Data*****");
		//ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
		//User user = profileData.getUser();
		//String walletId = user.getWalletid();
		String walletId = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.WALLET_ID_KEY);
		if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(
				SHARED_PREF_KEYS.API_KEY))) {

			//params= new HashMap<String, String>();
			//params.put("Authorization", PreferenceUtil.getInstance(this).getPreference(
			//        PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
			mConnectionFactory = new ConnectionFactory(this, this);
			//mConnectionFactory.setHeaderParams(params);
			mConnectionFactory.createConnection(QuopnConstants.QUOPN_MOBILE_WALLET_ANNOUNCEMENT, walletId);
		} else {
			// show error
		}
	}


	public void getAuthKeyVerify() {
		//Log.v(TAG,"*****Getting Auth Key*****");
		if (QuopnUtils.isInternetAvailable(this)) {
			if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY))) {
				headerParams = new HashMap<String, String>();
				headerParams.put("Authorization", PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY));
				Log.v(TAG, "*****Getting Auth Key*****" + PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY));
				params = new HashMap<String, String>();

				ConnectionFactory mConnectionFactory = new ConnectionFactory(this, this);
				mConnectionFactory.setHeaderParams(headerParams);
				mConnectionFactory.setPostParams(params);
				mConnectionFactory.createConnection(QuopnConstants.CATEGORY_CODE);
			} else {
				// show error
			}
		}
	}


	public void getNewCapaignListing(String categoryid) {
//        Log.v(TAG, "*****Getting NewCapaignListing*****");
		if (QuopnUtils.isInternetAvailable(this)) {
			//showCustomProgress();
			if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY))) {
				headerParams = new HashMap<String, String>();
				headerParams.put("Authorization", PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY));
//            params=new HashMap<String, String>();
//            params.put(QuopnApi.ParamKey.CATEGORYID,categoryid);
				mConnectionFactory = new ConnectionFactory(this, this);
				mConnectionFactory.setHeaderParams(headerParams);
				//mConnectionFactory.setPostParams(params);
				System.out.println("Vishal IN REQUEST MANAGER getNewCapaignListing() : ");
				mConnectionFactory.createConnection(QuopnConstants.NEW_CAMPAIGNLSTING_CODE, categoryid);
			} else {
				// show error
			}
		}
	}

//	public void getVoucherListing() {
//		Log.v(TAG, "*****Getting VoucherListing*****");
//
//		if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY))) {
//			headerParams = new HashMap<String, String>();
//			headerParams.put("Authorization", PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY));
//			params = new HashMap<String, String>();
//			params.put(QuopnConstants.CONN_PARAMS.walletId, PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.WALLET_ID_KEY));
//			mConnectionFactory = new ConnectionFactory(this, this);
//			mConnectionFactory.setHeaderParams(headerParams);
//			mConnectionFactory.setPostParams(params);
//			System.out.println("Vishal IN REQUEST MANAGER getVoucherListing() : ");
//			mConnectionFactory.createConnection(QuopnConstants.SHMART_VOUCHER_LIST_CODE);
//		} else {
//			// show error
//		}
//	}

	@Override
	public void onResponse(int responseResult, Response response) {
		Log.v(TAG, String.format("*****onResponse*****" + response, responseResult));
		 
		/*if (response instanceof CategoryQuopnData) {
			CategoryQuopnData categoryquopnresponse = (CategoryQuopnData) response;

			if (categoryquopnresponse.isError() == true) {
					Dialog dialog=new Dialog(context, R.string.dialog_title_error,categoryquopnresponse.getMessage()); 
					dialog.show();
					
//				Log.v(TAG,"Response : CategoryQuopnData=> Error occur");
			} else {
//				Log.v(TAG,"Response : CategoryQuopnData=> Successful Response");
				getContentResolver().delete(ConProvider.CONTENT_URI_CATEGORY,null, null);
				getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN,null, null);
				getContentResolver().delete(ConProvider.CONTENT_URI_GIFTS,null, null);

				for (Category category: categoryquopnresponse.getQuopns()) {
					populateCategoriesDB(category);
					// Quopns data
					for (QuopnList quopn : category.getList()) {
							populateQuopnsDB(quopn);
					}
				}
				
				List<FooterTag> footerList = categoryquopnresponse.getFooter();
				String[] arrFooter = new String[footerList.size()];
				String footerPref = "";
				for (int i = 0; i < footerList.size(); i++) {
					FooterTag footerName = footerList.get(i);
					arrFooter[i] = footerName.getFooter();
					footerPref = footerPref + footerName.getFooter() + ",";
				}
				
				footerPref.substring(0, footerPref.length() - 1);
				PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.FOOTER_TAGS, footerPref);
				QuopnConstants.MAIN_BOTTOM_BAR = arrFooter;
				
				
				Gift gift = categoryquopnresponse.getGift();
				//mGiftsContainer.clear();
				if(gift != null){
					
					GIFT_TITLE = gift.getTitle();
					PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.GIFT_TITLE, gift.getTitle()); //for getting title for gift newly added in json
					for (GiftsContainer gifts: gift.getList()) {
						populateGiftsDB(gifts);
					}
					
					if(gift.getList().size() >= 1){
						if(isNotFromBroadCast && !isShownFromCache)
						mProdCatFragment.setViewPagerAdapter(true);
					}
					else{
						if(isNotFromBroadCast && !isShownFromCache)
						mProdCatFragment.setViewPagerAdapter(false);
					}
				}else{
					if(isNotFromBroadCast && !isShownFromCache)
					mProdCatFragment.setViewPagerAdapter(false);
				}
				
			}
			
			mProdCatFragment.quopnLoaded();
			progress.setVisibility(View.GONE);
			if(mCustomProgressDialog!=null&&mCustomProgressDialog.isShowing()){
				mCustomProgressDialog.dismiss();
			}
			
		} */
		if (response instanceof NewCategoryData) {
			NewCategoryData newCategoryData = (NewCategoryData) response;
			stopCustomProgress();
			if (newCategoryData.isError() == true) {
				//Dialog dialog=new Dialog(context, R.string.dialog_title_error,newCategoryData.getMessage());
				//dialog.show();

				Log.v(TAG, "Response : NewCategoryData=> Error occur");
			} else {
				Log.v(TAG, "Response : NewCategoryData=> Successful Response");
				getContentResolver().delete(ConProvider.CONTENT_URI_CATEGORY, null, null);
				getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, null, null);
				getContentResolver().delete(ConProvider.CONTENT_URI_GIFTS, null, null);
				boolean isFirst = true;
				for (NewCategoryList category : newCategoryData.getCategorylist()) {
					if (isFirst) {
						showCustomProgress();
					} else {
						stopCustomProgress();
					}
					Log.d(TAG, "category.getName" + category.getCategoryid());
					populateCategoriesDB(category);
//                    // Quopns data
//                    for (QuopnList quopn : category.getList()) {
//                        populateQuopnsDB(quopn);
//                    }
					getNewCapaignListing(category.getCategoryid());

				}
			}

			List<FooterTag> footerList = newCategoryData.getFooter();
			System.out.println("MainActivity.onResponse 594");
			String[] arrFooter = new String[footerList.size()];
			String footerPref = "";
			for (int i = 0; i < footerList.size(); i++) {
				FooterTag footerName = footerList.get(i);
				arrFooter[i] = footerName.getFooter();
				footerPref = footerPref + footerName.getFooter() + ",";
			}

			footerPref.substring(0, footerPref.length() - 1);
			PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.FOOTER_TAGS, footerPref);
			QuopnConstants.MAIN_BOTTOM_BAR = arrFooter;

			mProdCatFragment.quopnLoaded();
//            progress.setVisibility(View.GONE);
			stopCustomProgress();


		} else if (response instanceof CategoryQuopnData) {
			CategoryQuopnData categoryquopnresponse = (CategoryQuopnData) response;
			if (categoryquopnresponse.isError() == true) {
				stopCustomProgress();
				Dialog dialog = new Dialog(context, R.string.dialog_title_error, R.string.slow_internet_connection);
				dialog.show();

				Log.v(TAG, "Response : CategoryQuopnData=> Error occur");
			} else {
				System.out.println("MainActivity.onResponse 622");
				Log.v(TAG, "Response : CategoryQuopnData=> Successful Response" + categoryquopnresponse.getQuopns().size());


				List<QuopnList> quopnList = categoryquopnresponse.getQuopns();
				for (QuopnList quopn : quopnList) {
					populateQuopnsDB(quopn);

				}


//                List<FooterTag> footerList = categoryquopnresponse.getFooter();
//                String[] arrFooter = new String[footerList.size()];
//                String footerPref = "";
//                for (int i = 0; i < footerList.size(); i++) {
//                    FooterTag footerName = footerList.get(i);
//                    arrFooter[i] = footerName.getFooter();
//                    footerPref = footerPref + footerName.getFooter() + ",";
//                }
//
//                footerPref.substring(0, footerPref.length() - 1);
//                PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.FOOTER_TAGS, footerPref);
//                QuopnConstants.MAIN_BOTTOM_BAR = arrFooter;


				Gift gift = categoryquopnresponse.getGift();
				//mGiftsContainer.clear();
				if (gift != null) {

					GIFT_TITLE = gift.getTitle();
					PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.GIFT_TITLE, gift.getTitle()); //for getting title for gift newly added in json
					for (GiftsContainer gifts : gift.getList()) {
						populateGiftsDB(gifts);
					}
					System.out.println("MainActivity.onResponse 657");
					if (gift.getList().size() >= 1) {
						if (isNotFromBroadCast && !isShownFromCache)
							mProdCatFragment.setViewPagerAdapter(true);
					} else {
						if (isNotFromBroadCast && !isShownFromCache)
							mProdCatFragment.setViewPagerAdapter(false);
					}
				} else {
					if (isNotFromBroadCast && !isShownFromCache)
						mProdCatFragment.setViewPagerAdapter(false);
				}

			}

			mProdCatFragment.quopnLoaded();
//            progress.setVisibility(View.GONE);
			stopCustomProgress();
		} else if (response instanceof UCNNumberData) {
			UCNNumberData ucnNumberData = (UCNNumberData) response;

			if (ucnNumberData.isError() == true) {
				Dialog dialog = new Dialog(context, R.string.dialog_title_error, ucnNumberData.getMessage());
				dialog.show();
			} else {
				Dialog dialog = new Dialog(context, R.string.dialog_title_success, ucnNumberData.getMessage());
				dialog.show();
			}
//			 Log.v(TAG,"Response : UCNNumberData ");
		} else if (response instanceof ProfileData) {
			Logger.d("");
			ProfileData interestsData = (ProfileData) response;
//			Log.v(TAG,"Response : InterestsData");

			if (interestsData.isError() == true) {
				stopCustomProgress();
				Dialog dialog = new Dialog(context, R.string.dialog_title_error, interestsData.getMessage());
				dialog.show();
//				Log.v(TAG,"Response : InterestsData=> Error occur");
			} else {
//				Log.v(TAG,"Response : InterestsData=> Successful Response");
				stateId = interestsData.getUser().getState();
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.WALLET_ID_KEY, interestsData.getUser().getWalletid());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.USERNAME_KEY, interestsData.getUser().getUsername());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.MOBILE_KEY, interestsData.getUser().getMobile());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.INVITE_MESSAGE, interestsData.getUser().getInvite_message());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.INVITE_TOP_MESSAGE, interestsData.getUser().getInvite_top_message());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.INVITE_SMS, interestsData.getUser().getInvite_sms());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.PROMO_MESSAGE, interestsData.getUser().getPromo_message());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.PROMO_TOP_MESSAGE, interestsData.getUser().getPromo_top_message());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.PROMO_BOTTOM_MESSAGE, interestsData.getUser().getPromo_bottom_message());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.EMAIL_KEY, interestsData.getUser().getEmailid());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.USER_STATE, interestsData.getUser().getState()); //commented 11022015
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.USER_CITY, interestsData.getUser().getCity());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.PIN_KEY, interestsData.getUser().getPIN());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.INVITE_COUNT, interestsData.getUser().getInvite_count());
				//PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.ANNOUCEMENT_URL, interestsData.getUser().get_Annoucement_url());

				QuopnConstants.CONTACT_US = interestsData.getUser().getCustomer_care_number();

				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_URL, interestsData.getFlash_file().getImagepath());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_NAME, interestsData.getFlash_file().getImagename());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_TYPE, interestsData.getFlash_file().getImagetype());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_START_DATE, interestsData.getFlash_file().getStartdate());
				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_END_DATE, interestsData.getFlash_file().getEnddate());

				PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.NOTITY_STAUS_KEY, interestsData.getUser().getNotification());
				String splashScreenLocalFilePath = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_LOCAL_PATH);
				String splashScreenLocalFileName = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_NAME);


				String splashScreenUrl = interestsData.getFlash_file().getImagepath();
				final String splashScreenNameServer = interestsData.getFlash_file().getImagename();
				String splashScreenType = interestsData.getFlash_file().getImagetype();
//                String annouceMent_url =interestsData.getUser().get_Annoucement_url();
//                Intent annouceMent = new Intent(MainActivity.this,AnnoucementActivity.class);
//                if(annouceMent_url!=null) {
//                    annouceMent.putExtra("url", annouceMent_url);
//                    startActivity(annouceMent);
//                }
				//Download Splash screen in background 

				if (splashScreenLocalFilePath == null || (!splashScreenLocalFileName.equalsIgnoreCase(splashScreenNameServer))) {

					if (splashScreenUrl != null && splashScreenUrl.length() > 0) {

						if (splashScreenType.equalsIgnoreCase("png") || splashScreenType.equalsIgnoreCase("jpg")) {

							RequestQueue rq = Volley.newRequestQueue(MainActivity.this);

							ImageRequest ir = new ImageRequest(splashScreenUrl, new Listener<Bitmap>() {
								@Override
								public void onResponse(Bitmap response) {
									saveImage(response, splashScreenNameServer, MainActivity.this);
								}

							}, 0, 0, null, null);

							rq.add(ir);
						} else if (splashScreenType.equalsIgnoreCase("gif")) {
							try {

								String localFilePath = Environment.getExternalStorageDirectory() + File.separator + splashScreenNameServer;
								//String localFilePath=getDataDir()+ File.separator +splashScreenNameServer;
								new GifDownloader().execute(new String[]{splashScreenUrl, localFilePath});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

			}

		} else if (response instanceof NotifyStatusData) {
			NotifyStatusData cityList = (NotifyStatusData) response;
			if (cityList.isError() == true) {
				// dont store gcm in the shared pref...
			} else {
//				{
				// store gcm in the shared pref....
				storeRegistrationId(context, regId);
			}
		} else if (response instanceof VersionCheckData) {
			VersionCheckData versionCheckData = (VersionCheckData) response;
			//Log.v(TAG,"Version check response called");
			if (versionCheckData.getError() == true) {

			} else {
				//QuopnConstants.versionCode=13;//cheat
//				Log.v(TAG,"Version Code is: "+versionCheckData.getCurrent_version());
				if ((versionCheckData.getCurrent_version() > QuopnConstants.versionCode) && QuopnConstants.versionCode != (-1)) {
					//updateflag is true
					//forcefully upgrade the app
					//Upgrade and Quit button

					//updateflag is false
					//Not force to upgrade the app
					//Upgrade and Close button

					Intent upgradeIntent = new Intent(this, UpdateScreen.class);
					upgradeIntent.putExtra(QuopnConstants.UPDATE_FLAG, versionCheckData.getUpdate_action());
					upgradeIntent.putExtra(QuopnConstants.UPDATION_LINK, versionCheckData.getLink());
					upgradeIntent.putExtra(QuopnConstants.UPDATION_MESSAGE, versionCheckData.getMessage());
					startActivityForResult(upgradeIntent, QuopnConstants.VERSION_CHECK_REQUESTCODE);
				}
			}
		} else if (response instanceof RequestPinData) {
			RequestPinData requestpindata = (RequestPinData) response;
			PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.PIN_KEY, requestpindata.getPin());
			QuopnUtils.setDefaultWalletInAppAndPref(requestpindata.getDefaultWallet(), getApplicationContext());
			//requestpindata.getPin();

//                if (requestpindata.getError() == false) {
//                    Dialog dialog = new Dialog(mSlidingFragActivity,R.string.dialog_title_change_pin,requestpindata.getMessage());
//                    dialog.setOnAcceptButtonClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //finish();
//                        }
//                    });
//                    dialog.show();
//
//                } else {
//
//                    if (requestpindata.getPin() != null) {
//
//                    }
//                }
		} else if (response instanceof ShmartVoucherListData) {
			ShmartVoucherListData shmartvoucherlistdata = (ShmartVoucherListData) response;
			if (shmartvoucherlistdata.getStatus().equalsIgnoreCase("success")) {
				List<VoucherList> voucherList = shmartvoucherlistdata.getVoucherList();
				for (VoucherList voucher : voucherList) {
					populateVoucherDB(voucher);

				}
			} else {

			}

		} else if (response instanceof RefreshSessionData) {
			RefreshSessionData refreshSessionResp = (RefreshSessionData) response;
			String error = refreshSessionResp.getError();
			String message = refreshSessionResp.getMessage();
			if (error.equals(QuopnConstants.FALSE)) {
				if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment) {
//						((MainMenuFragment) mMenuFragment).logoutApi();
				}
				QuopnApplication quopnApp = (QuopnApplication) getApplication();
				quopnApp.saveSessionId();
			} else {
			}
		} else if (response instanceof LogoutData) {
			LogoutData logoutData = (LogoutData) response;
			String error = logoutData.getError();
			String message = logoutData.getMessage();
			if (error.equals(QuopnConstants.TRUE)) {

			} else {
				if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment) {
					((MainMenuFragment) mMenuFragment).logoutCleanUp();
				}
			}
		} else if (response instanceof AnnouncementData) {
			AnnouncementData announcementData = (AnnouncementData) response;
			String annouceMent_image = announcementData.getImage();//"http://goldprice.org/goldprice/img/menu-gold-coin.jpg";
			String announcement_url = announcementData.getUrl();//"quopn://allquopns/10";
			//mCustomProgressDialog.dismiss();
			PreferenceUtil prefUtil = PreferenceUtil.getInstance(this);
			if (prefUtil.hasContainedPreferenceKey(SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN)) {
//				prefUtil.setPreference(SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, true);
				Intent annouceMent = new Intent(MainActivity.this, AnnoucementActivity.class);
				if (annouceMent_image != null || announcement_url != null) {
					annouceMent.putExtra("image", annouceMent_image);
					annouceMent.putExtra("url", announcement_url);
					startActivity(annouceMent);
				}
			}
		} else if (response instanceof ShmartCheckStatusData) {
			ShmartCheckStatusData shmartCreateUserData = (ShmartCheckStatusData) response;
			String errorCode = shmartCreateUserData.getError_code();
//				errorCode="104";
			String consumerId = shmartCreateUserData.getConsumer_id();
			String message = shmartCreateUserData.getMessage();

			ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
			User user = profileData.getUser();

			PreferenceUtil.getInstance(this).setPreference(SHARED_PREF_KEYS.SHMART_STATUS, errorCode);

			if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
				showShmartWallet();
			} else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)) {//001
				sendRequestOTP(user.getApi_key(), user.getWalletid());
			} else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_NOT_EXIST)) {//11
				showRegnDemoScreen();
			} else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.OTP_ACTIVATION_PENDING)) {//100//Shmart registered but not activated
				sendGenerateOTP(user.getApi_key(), user.getWalletid());
//				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.MOBILE_NUM_EXISTS)) {//101
//					showOTPScreenAndGenerateOTP();
			} else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_DOES_NOT_EXIST)) {//104
				showRegnDemoScreen();
			} else if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.ACTIVATION_PENDING)) {//106
				Log.d(TAG, "error code - 106");
//					showOTPScreenAndRequestOTP();
			} else {
				Log.d(TAG, "error code - " + errorCode);
//					showRegnDemoScreen();
//					showError(message);
			}
		} else if (response instanceof ShmartGenerateOTPData) {
			ShmartGenerateOTPData shmartCreateUserData = (ShmartGenerateOTPData) response;
			String errorCode = shmartCreateUserData.getError_code();
			String message = shmartCreateUserData.getMessage();

			if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
				showOTPScreenAndVerifyOTP();
			} else {
				Log.d(TAG, "error code - " + errorCode + ", error_message: " + message);
			}
		} else if (response instanceof ShmartRequestOTPData) {
			ShmartRequestOTPData shmartRequestOTPData = (ShmartRequestOTPData) response;
			String errorCode = shmartRequestOTPData.getError_code();
			String message = shmartRequestOTPData.getMessage();

			if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
				showOTPScreenAndChangeTransPwd();
			} else {
				Log.d(TAG, "error code - " + errorCode + ", error_message: " + message);
			}
		}

	}

	public void showOTPScreenAndVerifyOTP() {
		Intent shmartRegn = new Intent(this, ShmartOtp.class);
		shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, true);
		startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
	}

	public void showOTPScreenAndChangeTransPwd() {
		Intent shmartRegn = new Intent(this, ShmartOtp.class);
		shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, true);
		startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
	}

	private void sendRequestOTP(String argAuthKey, String argWalletId) {
		if (QuopnUtils.isInternetAvailable(this)) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
			params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");

			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setHeaderParams(headers);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SHMART_REQUEST_OTP);
		} else {
		}
	}

	public void showRegnDemoScreen() {
		Intent shmartRegn = new Intent(this, ShmartRegn.class);
		startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
	}

	public void sendGenerateOTP(String argAuthKey, String argWalletId) {
		if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY))) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(QuopnApi.ParamKey.AUTHORIZATION, argAuthKey);

			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
			params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");
			ConnectionFactory mConnectionFactory = new ConnectionFactory(this, this);
			mConnectionFactory.setHeaderParams(headers);
			mConnectionFactory.setPostParams(params);
			mConnectionFactory.createConnection(QuopnConstants.SHMART_GENERATE_OTP);
		} else {
			// show error
		}
	}

	public void populateCategoriesDB(NewCategoryList category) {
		ContentValues cv = new ContentValues();
//		cv.put(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY, category.getCategory());
		cv.put(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID, category.getCategoryid());
//		cv.put(ITableData.TABLE_CATEGORY.COLUMN_ICON, category.getIcon());
		cv.put(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY, category.getName());
		cv.put(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_TYPE, category.getType());
		cv.put(ITableData.TABLE_CATEGORY.COLUMN_ICON, category.getThumbimage());
		cv.put(ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE, category.getSequence());
		getContentResolver().insert(ConProvider.CONTENT_URI_CATEGORY, cv);
	}

	public void populateQuopnsDB(QuopnList quopn) {
		mCustomProgressDialog.show();
		boolean exists = false;

		Cursor cursor = getContentResolver().query(ConProvider.CONTENT_URI_QUOPN, null
				, ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ?"
				, new String[]{"" + quopn.getId()}, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				exists = true;
			} else {
				exists = false;
			}
			cursor.close();
		}

		ContentValues contentValues = new ContentValues();
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID, quopn.getCategoryid());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN, quopn.getCampaignname());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME, quopn.getProductname());
		//contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_TYPE,quopn.getProducttype());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON, quopn.getThumb_icon());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_BIG_IMG, quopn.getBig_image());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC, quopn.getShort_desc());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC, quopn.getLong_desc());
		//contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL,quopn.getMastertag_image());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG, quopn.getMaster_tag());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_FOOTER_TAG, quopn.getFooter_tag());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_BRAND, quopn.getBrand());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_COUNT, quopn.getQuopn_count());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION, quopn.getCall_to_action());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT, quopn.getCta_text());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE, quopn.getCta_value());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_START_DATE, quopn.getStartfrom());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_END_DATE, quopn.getEnddate());
		//contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_TERMS_COND,quopn.getTerms_cond());
		//contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SUBMITTED_BY,quopn.getSubmitted_by());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_END_DATE, quopn.getRedemption_expiry_date());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN_MEDIA, quopn.getCampaign_media());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MULTI_ISSUE, quopn.getMulti_issue());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_ISSUE_LIMIT, quopn.getIssue_limit());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_CAP, quopn.getRedemption_cap());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PROMOTION_ENABLED, quopn.getPromotion_enabled());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_TOTAL_COUPONS_BLOCKED, quopn.getTotal_coupons_blocked());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON_2, quopn.getThumb_icon2());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SEARCH_TAGS, quopn.getSearch_tags());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS, quopn.getAvailable_quopns());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED, quopn.getAlready_issued());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC, quopn.getDescription_highlight());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_END_DESC, quopn.getDescription_end());
		contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX, quopn.getSort_index());
		if (!exists) {
			contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID, quopn.getId());
			getContentResolver().insert(ConProvider.CONTENT_URI_QUOPN, contentValues);
		} else {
			getContentResolver().update(ConProvider.CONTENT_URI_QUOPN, contentValues
					, ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ?"
					, new String[]{"" + quopn.getId()});
		}
	}


	public void populateGiftsDB(GiftsContainer gistGiftsContainer) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(ITableData.TABLE_GIFTS.GIFT_STATE, 2); // by default 2 means normal state
		contentValues.put(ITableData.TABLE_GIFTS.COLUMN_GIFT_ID, gistGiftsContainer.getId());
		contentValues.put(ITableData.TABLE_GIFTS.COLUMN_CATEGORY_ID, gistGiftsContainer.getCategoryid());
		contentValues.put(ITableData.TABLE_GIFTS.CAMPAIGN_NAME, gistGiftsContainer.getCampaignname());
		contentValues.put(ITableData.TABLE_GIFTS.PRODUCT_NAME, gistGiftsContainer.getProductname());
		contentValues.put(ITableData.TABLE_GIFTS.THUMB_ICON, gistGiftsContainer.getThumb_icon());
		contentValues.put(ITableData.TABLE_GIFTS.BIG_IMAGE, gistGiftsContainer.getBig_image());
		contentValues.put(ITableData.TABLE_GIFTS.SHORT_DESC, gistGiftsContainer.getShort_desc());
		contentValues.put(ITableData.TABLE_GIFTS.LONG_DESC, gistGiftsContainer.getLong_desc());
		contentValues.put(ITableData.TABLE_GIFTS.MASTER_TAG, gistGiftsContainer.getMaster_tag());
		contentValues.put(ITableData.TABLE_GIFTS.MASTER_TAG_IMAGE, gistGiftsContainer.getMastertag_image());
		contentValues.put(ITableData.TABLE_GIFTS.FOOTER_TAG, gistGiftsContainer.getFooter_tag());
		contentValues.put(ITableData.TABLE_GIFTS.BRAND, gistGiftsContainer.getBrand());
		contentValues.put(ITableData.TABLE_GIFTS.QUOPN_COUNT, gistGiftsContainer.getQuopn_count());
		contentValues.put(ITableData.TABLE_GIFTS.CALL_TO_ACTION, gistGiftsContainer.getCall_to_action());
		contentValues.put(ITableData.TABLE_GIFTS.CTA_TEXT, gistGiftsContainer.getCta_text());
		contentValues.put(ITableData.TABLE_GIFTS.CTA_VALUE, gistGiftsContainer.getCta_value());
		contentValues.put(ITableData.TABLE_GIFTS.START_FORM, gistGiftsContainer.getStartfrom());
		contentValues.put(ITableData.TABLE_GIFTS.END_DATE, gistGiftsContainer.getEnddate());
		contentValues.put(ITableData.TABLE_GIFTS.REDEMPTION_EXPIRY_DATE, gistGiftsContainer.getRedemption_expiry_date());
		contentValues.put(ITableData.TABLE_GIFTS.CAMPAIGN_MEDIA, gistGiftsContainer.getCampaign_media());
		contentValues.put(ITableData.TABLE_GIFTS.MULTI_ISSUE, gistGiftsContainer.getMulti_issue());
		contentValues.put(ITableData.TABLE_GIFTS.ISSUE_LIMIT, gistGiftsContainer.getIssue_limit());
		contentValues.put(ITableData.TABLE_GIFTS.REDEMPTION_CAP, gistGiftsContainer.getRedemption_cap());
		contentValues.put(ITableData.TABLE_GIFTS.PROMOTION_ENABLED, gistGiftsContainer.getPromotion_enabled());
		contentValues.put(ITableData.TABLE_GIFTS.TOTAL_COUPONS_BLOCKED, gistGiftsContainer.getTotal_coupons_blocked());
		contentValues.put(ITableData.TABLE_GIFTS.THUMB_ICON2, gistGiftsContainer.getThumb_icon2());
		contentValues.put(ITableData.TABLE_GIFTS.SEARCH_TAGS, gistGiftsContainer.getSearch_tags());
		contentValues.put(ITableData.TABLE_GIFTS.GIFT_TARGET, gistGiftsContainer.getGift_target());
		contentValues.put(ITableData.TABLE_GIFTS.SERVE_CAP, gistGiftsContainer.getServe_cap());
		contentValues.put(ITableData.TABLE_GIFTS.GIFT_TYPE, gistGiftsContainer.getGift_type());
		contentValues.put(ITableData.TABLE_GIFTS.PARTNER_CODE, gistGiftsContainer.getPartner_code());
		contentValues.put(ITableData.TABLE_GIFTS.TERMS_COND, gistGiftsContainer.getTerms_cond());
		contentValues.put(ITableData.TABLE_GIFTS.SORT_INDEX, gistGiftsContainer.getSort_index());
		getContentResolver().insert(ConProvider.CONTENT_URI_GIFTS, contentValues);
	}

	public void populateVoucherDB(VoucherList shmartVoucherListdata) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ITableData.TABLE_VOUCHER.COLUMN_PARTNER_ID, shmartVoucherListdata.getPartner_id());
		contentValues.put(ITableData.TABLE_VOUCHER.PARTNER_NAME, shmartVoucherListdata.getPartner_name());
		contentValues.put(ITableData.TABLE_VOUCHER.THUMB_ICON, shmartVoucherListdata.getThumb_icon());
		contentValues.put(ITableData.TABLE_VOUCHER.BIG_IMAGE, shmartVoucherListdata.getBig_image());
		contentValues.put(ITableData.TABLE_VOUCHER.TOTAL_COUPONS, shmartVoucherListdata.getTotal_coupons());
		contentValues.put(ITableData.TABLE_VOUCHER.AVAILABLE_COUPONS, shmartVoucherListdata.getAvailable_coupons());
		contentValues.put(ITableData.TABLE_VOUCHER.ISSUE_AVAILABLE, shmartVoucherListdata.getIssue_available());
		contentValues.put(ITableData.TABLE_VOUCHER.ISSUED_COUPONS, shmartVoucherListdata.getIssued_coupons());
		contentValues.put(ITableData.TABLE_VOUCHER.PURCHASE_VALUE, shmartVoucherListdata.getPurchase_value());
		getContentResolver().insert(ConProvider.CONTENT_URI_VOUCHER, contentValues);
		Log.v(TAG, "Response : populateVoucherDB==>" + contentValues);
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.quopn.wallet/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.quopn.wallet/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	public interface OnQuopnLoaded {
		void quopnLoaded();
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);

		if (resultcode == RESULT_OK) {
			switch (requestcode) {
				case QuopnConstants.HOME_PRESS:
					backeventReceiver(false);
					break;

				case QuopnConstants.VERSION_CHECK_REQUESTCODE:
					this.finish();
					startActivity(new Intent(this, MainSplashScreen.class));
					break;

				default:
					break;
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!mSearchView.isIconified()) {
				mSearchView.setIconified(true);
				mSearchView.invalidate();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void closeSearchBar() {
		if (!mSearchView.isIconified()) {
			mSearchView.setQuery("", true);
			mSearchView.setIconified(true);
			mSearchView.invalidate();
		}
	}

	public void openSearchBar() {
		if (mSearchView.isIconified()) {
			mSearchView.setIconified(false);
			mSearchView.invalidate();
			mSearchView.setQuery(searchText, true);
		}
	}

	public void openSearchBarFromSerachNow() {
		if (mSearchView.isIconified()) {
			mSearchView.setIconified(false);
			mSearchView.invalidate();
		}
	}

	public void clearSearchView() {
		mSearchView.setQuery("", false);
	}


	// GCM Content 
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
					sendNotificationID(regId);
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

	private void sendNotificationID(String regID) {

		if (QuopnUtils.isInternetAvailable(this)) {
			// if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY))) {
			//Map<String, String> headerParams = new HashMap<String, String>();
			//headerParams.put("Authorization",PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
			//headerParams.put(QuopnApi.ParamKey.x_session,((QuopnApplication)getApplicationContext()).getSessionId());

			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", PreferenceUtil.getInstance(this)
					.getPreference(SHARED_PREF_KEYS.USER_ID));
			params.put("gcm_id", regID);
			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			//connectionFactory.setHeaderParams(headerParams);
			connectionFactory.createConnection(QuopnConstants.NOTIFY_STATUS_SENT);
			//}
		} else {

		}
	}


	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
//		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}


	private BroadcastReceiver message = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			
			/*
			 * Code added to support promocode 24-04-2015
			 */
			if (null != intent.getExtras() && null != intent.getExtras().getString("REFRESH")) {

				if (mSlidingMenu.isMenuShowing()) {
					mSlidingMenu.toggle();
				}
				showCustomProgress();

				String refreshType = intent.getExtras().getString("REFRESH");

				if (refreshType.equals("REFRESH")) {
					PullToRefreshFunction(false);
				} else if (refreshType.equals("REFRESH_TYPE")) {
					PullToRefreshFunction(true);

				}
//				getAuthKeyVerify();
				if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
					((MainMenuFragment) mMenuFragment).updateCounters();
				isNotFromBroadCast = true;
				isShownFromCache = false;
				return;

			}
			/*
			 * end
			 */

			if (intent.getAction().equals(QuopnConstants.BROADCAST_UPDATE_QUOPNS)) {
//			mCustomProgressDialog=new CustomProgressDialog(MainActivity.this);
//			mCustomProgressDialog.show();
				isNotFromBroadCast = false;
				PullToRefreshFunction(false);
//                getAuthKeyVerify();
				if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
					((MainMenuFragment) mMenuFragment).updateCounters();
			}
			QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
			mAddtoCard_Counter_tv.setText("" + QuopnConstants.MY_CART_COUNT);
			if (QuopnConstants.MY_CART_COUNT <= 0) {
				mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
			} else {
				mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
			}
		}
	};


	private BroadcastReceiver notificationcounter = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT);
			Log.i(TAG, "In BroadcastReceiver notificationcounter: " + QuopnConstants.NOTIFICATION_COUNT);
			if (QuopnConstants.NOTIFICATION_COUNT <= 0) {
				mNotification_Counter_tv.setVisibility(View.INVISIBLE);
			} else {
				mNotification_Counter_tv.setVisibility(View.VISIBLE);
				mNotification_Counter_tv.setText("" + QuopnConstants.NOTIFICATION_COUNT);
				mNotification_Counter_tv.startAnimation(m_increaseAnimation);
			}
			if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
				((MainMenuFragment) mMenuFragment).updateCounters();
		}
	};

	private BroadcastReceiver receiverShowCart = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			getSlidingMenu().showSecondaryMenu(true);
		}
	};

	private BroadcastReceiver recSendChangeSessIdCommand = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			sendChangeSessId(intent.getStringExtra(QuopnConstants.INTENT_KEYS.x_session_old), intent.getStringExtra(QuopnConstants.INTENT_KEYS.x_session_new));
		}
	};

	private BroadcastReceiver receiverLogoutInvalidSession = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment) {
				((MainMenuFragment) mMenuFragment).logoutApi();
			}
		}
	};

	private BroadcastReceiver receiverParseDeepLinks = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			parseAnnouncementDeepLinks(intent);
		}
	};

	private BroadcastReceiver receiverParseNotifActivityLinks = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			parseDeepLinksFromNotifActivity(intent);
		}
	};

	private void sendChangeSessId(String argSessIdOld, String argSessIdNew) {
		String api_key = PreferenceUtil.getInstance(this).getPreference(SHARED_PREF_KEYS.API_KEY);
		if (QuopnUtils.isInternetAvailable(this)) {
			if (!TextUtils.isEmpty(api_key)) {
				Map<String, String> headerParams = new HashMap<String, String>();
				headerParams.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
				headerParams.put(QuopnApi.ParamKey.x_session, argSessIdOld);

				Map<String, String> postparams = new HashMap<String, String>();
				postparams.put(QuopnApi.ParamKey.session_id_new, argSessIdNew);

				ConnectionFactory mConnectionFactory = new ConnectionFactory(this, this);
				mConnectionFactory.setHeaderParams(headerParams);
				mConnectionFactory.setPostParams(postparams);
				mConnectionFactory.createConnection(QuopnConstants.REFRESH_SESSION_CODE);
			} else {
				// show error
			}
		} else {

		}

	}

	private BroadcastReceiver initialmycartcounter = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
			mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
			if (QuopnConstants.MY_CART_COUNT <= 0) {
				mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
			} else {
				mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
			}
			if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
				((MainMenuFragment) mMenuFragment).updateCounters();
		}
	};

	private BroadcastReceiver updatemycartcounter = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
			mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
			if (QuopnConstants.MY_CART_COUNT <= 0) {
				mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
			} else {
				mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
				mAddtoCard_Counter_tv.startAnimation(m_decreaseAnimation);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		QuopnConstants.ISREMOVEFROMCART = false;
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				mProdCatFragment.switchToAllQuopnsTab();
			}
		}, new IntentFilter(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB));

		AllCartCounterRefreshed_FromCategory();
		stopCustomProgress();

	}

	;

	private void PullToRefreshFunction(String categoryID) {
		Cursor cursor = getContentResolver().query(
				ConProvider.CONTENT_URI_CATEGORY, null, null, null, ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE);
		if (cursor != null && cursor.getCount() >= 1) {
			while (cursor.moveToNext()) {
				String categoryid = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID));

				if (categoryID.equals("0") || categoryID.equals(categoryid)) {
					getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, null, null);
					getNewCapaignListing(categoryid);
				}
			}
		}
	}

	private void PullToRefreshFunction(boolean giftsOnly) {
		if (QuopnUtils.isInternetAvailable(MainActivity.this)) {
			if (giftsOnly) {
				Cursor cursor = getContentResolver().query(
						ConProvider.CONTENT_URI_CATEGORY, null, ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_TYPE + " = ?", new String[]{"G"}, ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE);
				if (cursor != null && cursor.getCount() > 0) {
					String categoryID = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID));
					PullToRefreshFunction(categoryID);
				}
			} else {
				PullToRefreshFunction("0");
			}
		} else {
			Dialog dialog = new Dialog(MainActivity.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
			dialog.show();
		}
	}

    /*@Override
    protected void onPause() {
    	super.onPause();
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(message);
    }*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(message);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationcounter);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(initialmycartcounter);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(updatemycartcounter);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverShowCart);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(recSendChangeSessIdCommand);
		((QuopnApplication) getApplicationContext()).stoptimertask();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverLogoutInvalidSession);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverParseDeepLinks);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverParseNotifActivityLinks);
	}

	public void saveImage(Bitmap bmp, String name, Context c) {
		FileOutputStream fos = null;
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();

			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + name + ".jpg");
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(bytes.toByteArray());
			fos.close();
			if (file.length() > 0) {
				PreferenceUtil.getInstance(MainActivity.this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_LOCAL_PATH, file.getAbsolutePath());
				PreferenceUtil.getInstance(MainActivity.this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_NAME, name);
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class GifDownloader extends AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... urls) {
			try {
				URL u = new URL(urls[0]);
				File outputFile = new File(urls[1]);
				if (!outputFile.exists()) {
					outputFile.createNewFile();
				}
				URLConnection conn = u.openConnection();
				int contentLength = conn.getContentLength();

				DataInputStream stream = new DataInputStream(u.openStream());

				byte[] buffer = new byte[contentLength];
				stream.readFully(buffer);
				stream.close();

				DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
				fos.write(buffer);
				fos.flush();
				fos.close();
				if (outputFile.length() > 0) {
					PreferenceUtil.getInstance(MainActivity.this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_LOCAL_PATH, outputFile.getAbsolutePath());
					PreferenceUtil.getInstance(MainActivity.this).setPreference(SHARED_PREF_KEYS.SPLASH_SCREEN_FILE_NAME, urls[1]);
				}
			} catch (FileNotFoundException e) {
				return null; // swallow a 404
			} catch (IOException e) {
				return null; // swallow a 404
			}
			return null;
		}

	}

	public String getDataDir() {
		try {
			return getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0).applicationInfo.dataDir;
		} catch (Exception e) {
			//		   Log.v("Your Tag", "Data Directory error:", e);
			return null;
		}
	}

	@Override
	public void onQuopnIssued(String campaignID, boolean isFromGift, String webissueresponse) {
		if (isFromGift) {
			Dialog dialog = new Dialog(MainActivity.this, R.string.dialog_title_success, webissueresponse);
			dialog.show();
		}
		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
		mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
		if (QuopnConstants.MY_CART_COUNT <= 0) {
			mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
		} else {
			mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
			mAddtoCard_Counter_tv.startAnimation(m_increaseAnimation);
		}
		mProdCatFragment.notifyAddToCartCounterChanged();
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
			((MainMenuFragment) mMenuFragment).updateCounters();
	}

	public void AllCartCounterRefreshed() {
		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
		mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
		if (QuopnConstants.MY_CART_COUNT <= 0) {
			mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
		} else {
			mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
			mAddtoCard_Counter_tv.startAnimation(m_increaseAnimation);
		}
		mProdCatFragment.notifyAddToCartCounterChanged();
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
			((MainMenuFragment) mMenuFragment).updateCounters();
	}

	public void AllCartCounterRefreshed_FromCategory() {
		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(MainActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
		mAddtoCard_Counter_tv.setText("" + (QuopnConstants.MY_CART_COUNT));
		if (QuopnConstants.MY_CART_COUNT <= 0) {
			mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
		} else {
			mAddtoCard_Counter_tv.setVisibility(View.VISIBLE);
		}
		mProdCatFragment.notifyAddToCartCounterChanged();
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
			((MainMenuFragment) mMenuFragment).updateCounters();
	}


	public void QuopnlistingAdapterRefreshed() {
		mProdCatFragment.notifyAddToCartCounterChanged();
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment)
			((MainMenuFragment) mMenuFragment).updateCounters();
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
					mCustomProgressDialog.dismiss();
					Dialog dialog = new Dialog(MainActivity.this, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
					System.out.println("=============MainActivityOntimeout========");
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//Refrsh the whole page again.
							LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(message,
									new IntentFilter(QuopnConstants.BROADCAST_UPDATE_QUOPNS));
						}
					});
					dialog.show();
				}

			}
		});
	}

	@Override
	public void myTimeout(String requestTag) {
		System.out.println("=============MainActivityOntimeout=======11111=" + requestTag);

	}


	public void sendWebIssueAddToCart(final String cta_text, final String cta_value,
									  final String source, final String campaign_id,
									  final Context context, final boolean isFromDetails,
									  final int availablequopn, final int alreadyissued, final boolean isFromGift, final ImageView mProgressBar, final ArrayList<String> arrList) {

		mProgressBar.setVisibility(View.VISIBLE);

		ConnectionListener webissue_connectionlistener = new ConnectionListener() {

			@Override
			public void onResponse(int responseResult, Response response) {
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.INVISIBLE);
				}
				if (response instanceof WebIssueData) {
					WebIssueData webIssueData = (WebIssueData) response;
					if (webIssueData.isError()) {
//                            if(mProgressBar!=null ){
//                                mProgressBar.setVisibility(View.INVISIBLE);
//                                QuopnConstants.ISADDTOCARTINCOMPLETE =true;
//                            }
						Dialog dialog = new Dialog(context, R.string.dialog_title_error, R.string.slow_internet_connection);
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (isFromDetails) {
									Activity activity = (Activity) context;
									activity.finish();
								}
								Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
								LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
							}
						});
						dialog.show();

					} else {

						QuopnConstants.MY_CART_COUNT = PreferenceUtil
								.getInstance(context).getPreference_int(
										SHARED_PREF_KEYS.MYCARTCOUNT) + 1;
						PreferenceUtil.getInstance(context).setPreference(
								SHARED_PREF_KEYS.MYCARTCOUNT,
								QuopnConstants.MY_CART_COUNT);

						SingleCartDetails singleCartDetails = webIssueData.getSingleCart_details();
						Cursor cursor1 = null;
						BigDecimal mTotalSaving = null;
						BigDecimal mIndividual_cartSaving = null;
						cursor1 = context.getContentResolver().query(ConProvider.CONTENT_URI_MYCART, null, null, null, ITableData.TABLE_MYCART.COLUMN_ID + " desc");
						if (cursor1 == null || cursor1.getCount() == 0) {
							cursor1.moveToFirst();
							mIndividual_cartSaving = new BigDecimal(singleCartDetails.getSaving_value());
							mTotalSaving = mIndividual_cartSaving;
							String.format("%.2f", mTotalSaving);
							populateMyCartDB(context, "" + mTotalSaving, singleCartDetails.getCartid(), singleCartDetails.getCampaignid(), singleCartDetails.getCampaignname(), singleCartDetails.getQuopncode(),
									singleCartDetails.getCompanyname(), singleCartDetails.getLong_description(), singleCartDetails.getBrandname(), singleCartDetails.getThumb_icon1(), singleCartDetails.getEnddate(), singleCartDetails.getQuopnid(),
									singleCartDetails.getSaving_value(), singleCartDetails.getCart_desc(), singleCartDetails.getCart_image(), singleCartDetails.getTitle());
							ContentValues cv = new ContentValues();
							cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING, String.valueOf(mTotalSaving));
							context.getContentResolver().update(ConProvider.CONTENT_URI_MYCART, cv, null, null);
							cursor1.close();

						} else {
							while (cursor1.moveToNext()) {
								String approxsaving = cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING));
								mTotalSaving = new BigDecimal(approxsaving);
								mIndividual_cartSaving = new BigDecimal(singleCartDetails.getSaving_value());
								mTotalSaving = mTotalSaving.add(mIndividual_cartSaving);
								mTotalSaving.setScale(2, BigDecimal.ROUND_CEILING);
							}
							String.format("%.2f", mTotalSaving);
							populateMyCartDB(context, "" + mTotalSaving, singleCartDetails.getCartid(), singleCartDetails.getCampaignid(), singleCartDetails.getCampaignname(), singleCartDetails.getQuopncode(),
									singleCartDetails.getCompanyname(), singleCartDetails.getLong_description(), singleCartDetails.getBrandname(), singleCartDetails.getThumb_icon1(), singleCartDetails.getEnddate(), singleCartDetails.getQuopnid(),
									singleCartDetails.getSaving_value(), singleCartDetails.getCart_desc(), singleCartDetails.getCart_image(), singleCartDetails.getTitle());
							ContentValues cv = new ContentValues();
							cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING, String.valueOf(mTotalSaving));
							context.getContentResolver().update(ConProvider.CONTENT_URI_MYCART, cv, null, null);

							cursor1.close();
						}


						int mAlreadyissued = alreadyissued;
						int mAvailablequopn = availablequopn;
						if (mAlreadyissued != -1 && mAvailablequopn != -1) {
							if (mAlreadyissued > 0) {
								mAlreadyissued--;
							} else if (mAvailablequopn > 0) {
								mAvailablequopn--;
							}


							if ((mAlreadyissued + mAvailablequopn) == 0) {
								// remove current item
								context.getContentResolver().delete(
										ConProvider.CONTENT_URI_QUOPN,
										ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID
												+ " = ? ",
										new String[]{campaign_id});
							} else {
								// update both values of counters.
								ContentValues cv = new ContentValues();
								cv.put(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED,
										String.valueOf(mAlreadyissued));
								cv.put(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS,
										String.valueOf(mAvailablequopn));
								context.getContentResolver().update(
										ConProvider.CONTENT_URI_QUOPN,
										cv,
										ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID
												+ " = ? ",
										new String[]{"" + campaign_id});
							}

							// Quopns adapter refresh
							for (QuopnOperationsListener listener : addQuopnoperationArrayList) {
								onQuopnIssued(campaign_id, isFromGift, webIssueData.getMessage());
								System.out.println("========SendWebissue=====Listner======");
							}
						} else {
							// remove current item
							context.getContentResolver().delete(
									ConProvider.CONTENT_URI_GIFTS,
									ITableData.TABLE_GIFTS.COLUMN_GIFT_ID
											+ " = ? ",
									new String[]{campaign_id});
							// Quopns adapter refresh
							for (QuopnOperationsListener listener : addQuopnoperationArrayList) {
								onQuopnIssued(campaign_id, isFromGift, webIssueData.getMessage());
							}
						}
						System.out.println("==========QuopnOperation=========");
						mProgressBar.setVisibility(View.INVISIBLE);

						// ankur
						if (arrList.contains(campaign_id)) {
							Log.d(TAG, "removed from list: " + campaign_id);
							arrList.remove(campaign_id);
						} else {
							Log.e(TAG, "Error: not present in list: " + campaign_id);
						}
					}
				} else {
					Log.e(TAG, "response not an instanceof WebIssueData");
				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				// TODO Auto-generated method stub
				if (campaign_id != null) {
					if (arrList.contains(campaign_id)) {
						Log.d(TAG, "onTimeout: removed from list: " + campaign_id);
						arrList.remove(campaign_id);
					} else {
						Log.e(TAG, "onTimeout: Error: not present in list: " + campaign_id);
					}
				}
				mProgressBar.setVisibility(View.INVISIBLE);
				//Dialog dialog = new Dialog(context, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
				//dialog.show();

			}

			@Override
			public void myTimeout(String requestTag) {
				System.out.println("=============MainActivityOntimeout========" + requestTag);
			}
		};

		// Extra internet check
		if (!QuopnUtils.isInternetAvailable(context)) {
			if (campaign_id != null) {
				if (arrList.contains(campaign_id)) {
					Log.d(TAG, "InternetUnavailable: removed from list: " + campaign_id);
					arrList.remove(campaign_id);
				} else {
					Log.e(TAG, "InternetUnavailable: Error: not present in list: " + campaign_id);
				}
			}
			mProgressBar.setVisibility(View.INVISIBLE);
			Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
			dialog.show();

			return;
		}

		QuopnUtils.getWebIssueResponse(campaign_id, source, context,
				webissue_connectionlistener);

	}

	public void addQuopnOperationsListener(QuopnOperationsListener listener) {
		//this.listener = listener;
		addQuopnoperationArrayList.add(listener);
	}

	private void populateMyCartDB(Context context, String approxsaving, String cartid, String campaignid, String campaignname, String quopncode,
								  String companyname, String long_description, String brandname, String thumb_icon1, String enddate, String quopnid,
								  String saving_value, String cart_desc, String cart_image, String title) {
		ContentValues cv = new ContentValues();
		cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING, approxsaving);
		cv.put(ITableData.TABLE_MYCART.COLUMN_CARTID, cartid);
		cv.put(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNID, campaignid);
		cv.put(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNNAME, campaignname);
		cv.put(ITableData.TABLE_MYCART.COLUMN_QUOPNCODE, quopncode);
		cv.put(ITableData.TABLE_MYCART.COLUMN_COMPANYNAME, companyname);
		cv.put(ITableData.TABLE_MYCART.COLUMN_LONG_DESCRIPTION, long_description);
		cv.put(ITableData.TABLE_MYCART.COLUMN_BRANDNAME, brandname);
		cv.put(ITableData.TABLE_MYCART.COLUMN_THUMB_ICON1, thumb_icon1);
		cv.put(ITableData.TABLE_MYCART.COLUMN_ENDDATE, enddate);
		cv.put(ITableData.TABLE_MYCART.COLUMN_QUOPNID, quopnid);
		cv.put(ITableData.TABLE_MYCART.COLUMN_SAVING_VALUE, saving_value);
		cv.put(ITableData.TABLE_MYCART.COLUMN_CART_DESC, cart_desc);
		cv.put(ITableData.TABLE_MYCART.COLUMN_CART_IMAGE, cart_image);
		cv.put(ITableData.TABLE_MYCART.COLUMN_TITLE, title);
		context.getContentResolver().insert(ConProvider.CONTENT_URI_MYCART, cv);
	}

	public void showInviteScreen() {
		if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
			mAnalysisManager.send(AnalysisEvents.INVITE);
//			progress.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this, InviteUserActivity.class);
			intent.setData(getIntent().getData());
			startActivityForResult(intent, QuopnConstants.HOME_PRESS);
		}
	}

	public void showShopsAround() {
		if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {

			Intent shopsaroundMap = new Intent(this, QuopnStoreList.class);
			shopsaroundMap.setData(getIntent().getData());
			startActivityForResult(shopsaroundMap,
					QuopnConstants.HOME_PRESS);
		}
	}

	public void showPromoScreen(String argPromoCode) {
		if (QuopnUtils.isInternetAvailableAndShowDialog(this)) {
			mAnalysisManager.send(AnalysisEvents.PROMO);
//			progress.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this, PromoCodeActivity.class);
			intent.setData(getIntent().getData());
			intent.putExtra(QuopnConstants.INTENT_KEYS.promo, argPromoCode);
			startActivityForResult(intent, QuopnConstants.HOME_PRESS);
		}
	}

	public void showQuopnDetails(Intent argIntent, View argTransitionView, QuopnDetailsActivity.QuopnDetailAddToCartListener mQuopnDetailAddToCartListener) {
		QuopnDetailsActivity.launch(this, argIntent, argTransitionView, mQuopnDetailAddToCartListener, false);
	}

	public void showCategoryScreen(NewCategoryList argCat, String argCatName, CategoryListAdapter.CategorySelectedListener argCategSelListner) {
		mAnalysisManager.send(AnalysisEvents.CATEGORY, argCat.getCategoryid());
		ProductCatFragment.QUOPN_CATEGORY_ID = argCat.getCategoryid();
		ProductCatFragment.QUOPN_CATEGORY_TYPE = argCatName;
		argCategSelListner.onCategorySelected(argCat);
	}

	public void showCategoryCoupons(String argCategId, String argCategName) {
		ProductCatFragment.QUOPN_CATEGORY_ID = argCategId;
		ProductCatFragment.QUOPN_CATEGORY_TYPE = argCategName;
		Intent listingIntent = new Intent(this, ListingByCategoryActivity.class);
		listingIntent.putExtra("category", argCategId);
		listingIntent.putExtra("categoryname", argCategName);
		startActivityForResult(listingIntent, QuopnConstants.HOME_PRESS);
	}

	public void showAppTour() {
		//mMainActivity.toggle();
		Intent tourIntent = new Intent(this, TourActivity.class);
		tourIntent.putExtra("coming_from", "MainMenuFragment");
		startActivity(tourIntent);
	}

	public void showShmartWallet() {
//		showProgress();
		ShmartFlow.getInstance().setContext(this);
		ShmartFlow.getInstance().start();
	}

	public void sendCheckWalletStatus() {
		ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
		String argWalletId = profileData.getUser().getWalletid();
		if (QuopnUtils.isInternetAvailable(this)) {
			showCustomProgress();
			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
			params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");

			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SHMART_CHECK_STATUS);
		} else {
			//progress.setVisibility(View.INVISIBLE);
			stopCustomProgress();
			Dialog dialog = new Dialog(this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
			dialog.show();
		}
	}

	public void callCitrus() {
		Logger.d("");
		if (mMenuFragment != null && mMenuFragment instanceof MainMenuFragment) {
			PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, true);
			((MainMenuFragment) mMenuFragment).callCitrus();
		} else {
		}
	}

	public void walletCitrusStatus() {
		PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, true);

		mCitrusClient.isUserSignedIn(new Callback<Boolean>() {
			@Override
			public void success(Boolean isUserLoggedIn) {
				if (isUserLoggedIn) {
					//QuopnConstants.showToast(mContext, mCitrusClient.getUserEmailId());
					//mCitrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);
//					Logger.d("mCitrusClient.isUserSignedIn success loggedin for email %s", mCitrusClient.getCitrusUser().getEmailId());
					showCustomProgress();
					showShmartWallet();// launch citrus
				} else {
					// is citrus activated
					final String mobileWallets = PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.MOBILE_WALLETS_KEY);
					Logger.d("mCitrusClient.isUserSignedIn success not logged in, mobileWallets %s", mobileWallets);
					if (mobileWallets.isEmpty()) {
						// launch citrus
					} else {
						if (mobileWallets.equalsIgnoreCase("2") || mobileWallets.equalsIgnoreCase("2|1") || mobileWallets.equalsIgnoreCase("1|2")) {
							// existing user
							Logger.d("mCitrusClient.isUserSignedIn linkUserExtended start for email %s mobile %S", PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.CITRUS_EMAIL_KEY), PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.MOBILE_KEY));
							CitrusClient.getInstance(getApplicationContext()).linkUserExtended(PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.CITRUS_EMAIL_KEY), PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.MOBILE_KEY), new Callback<LinkUserExtendedResponse>() {
								@Override
								public void success(LinkUserExtendedResponse linkUserExtendedResponse) {
									Logger.d("mCitrusClient.isUserSignedIn linkUserExtendedResponse %s", linkUserExtendedResponse.getJSON());
									if (QuopnUtils.isActivityRunningForContext(mContext)) {
										linkUserExtended = linkUserExtendedResponse;
										onCitrusOTPDialogShow();
									}
								}

								@Override
								public void error(CitrusError citrusError) {
									Logger.d("mCitrusClient.isUserSignedIn error %s", citrusError.getMessage());
								}
							});
						} else {
							// new user// citrus inactivated
							Intent intent = new Intent(MainActivity.this, CitrusRegn.class);
							startActivity(intent);
						}
					}

				}
			}

			@Override
			public void error(CitrusError error) {
				//textMessage.setText(error.getMessage());
				//Logger.e(error.getMessage());
				//QuopnConstants.showToast(mContext, error.getMessage());
			}
		});
	}

	private void showCitrusLoginMessage(final boolean isSuccess, final String message) {
		if (!((Activity) mContext).isFinishing()) {
			stopCustomProgress();
			final DialogInterface.OnDismissListener dismissListener
					= new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					onCitrusOTPDialogShow();
				}
			};
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Dialog dialog = new Dialog(MainActivity.this, R.string.dialog_title, message);
					dialog.setOnDismissListener(dismissListener);
					dialog.show();
				}
			};
			runOnUiThread(runnable);
		} else {
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	public void onCitrusOTPDialogShow() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				final android.app.Dialog dialog = new android.app.Dialog(MainActivity.this, R.style.DialogTheme);
				dialog.setContentView(R.layout.custom_dialog);
				final EditText dialogEditOtpText = (EditText) dialog.findViewById(R.id.editOtpText);
				Button dialogButtonConfirm = (Button) dialog.findViewById(R.id.dialogButtonConfirm);
				Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
				Button dialogButtonResend = (Button) dialog.findViewById(R.id.dialogButtonResend);

				// if button is clicked, close the custom dialog
				dialogButtonConfirm.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showCustomProgress();
						String linkUserPassword = null;
						editOtpText = dialogEditOtpText.getText().toString();
						if (Validations.isValidOTP(editOtpText)) {
							LinkUserPasswordType linkUserPasswordType = LinkUserPasswordType.Otp;
							linkUserPassword = editOtpText;

							mCitrusClient.linkUserExtendedSignIn(linkUserExtended, linkUserPasswordType, linkUserPassword, new Callback<CitrusResponse>() {
								@Override
								public void success(CitrusResponse citrusResponse) {
									Logger.d(citrusResponse.getMessage());
									showShmartWallet();
									stopCustomProgress();
									CitrusClient.getInstance(mContext.getApplicationContext()).getPrepaidToken(new Callback<AccessToken>() {
										@Override
										public void error(CitrusError citrusError) {
											Logger.d(citrusError.getMessage());
										}

										@Override
										public void success(AccessToken accessToken) {
											Logger.d(accessToken.getAccessToken());
											QuopnApplication.setAccessToken(accessToken);
										}
									});
								}

								@Override
								public void error(CitrusError error) {
									showCitrusLoginMessage(false, error.getMessage());
//								if (TextUtils.isEmpty(editOtpText)) {
//									//Validations.CustomErrorMessage(getApplicationContext(), R.string.blank_otp_validation, dialogEditOtpText, null, 0);
//									QuopnUtils.showDialog(MainActivity.this, R.string.dialog_title, getApplicationContext().getResources().getString(R.string.blank_otp_validation));
//									return;
//								} else {
//									dialogEditOtpText.setText("");
//									QuopnUtils.showDialog(MainActivity.this, R.string.dialog_title, getApplicationContext().getResources().getString(R.string.citrus_dialog_otp_validation));
//								}
								}
							});
							dialog.dismiss();
						} else {
							dialog.dismiss();
							showCitrusLoginMessage(false, getApplicationContext().getResources().getString(R.string.citrus_dialog_otp_validation));
						}
					}
				});
				dialogButtonResend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showCustomProgress();
						dialogEditOtpText.setText("");
						CitrusClient.getInstance(getApplicationContext()).linkUserExtended(PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.CITRUS_EMAIL_KEY), PreferenceUtil.getInstance(mContext).getPreference(SHARED_PREF_KEYS.MOBILE_KEY), new Callback<LinkUserExtendedResponse>() {
							@Override
							public void success(LinkUserExtendedResponse linkUserExtendedResponse) {
								Logger.d("mCitrusClient.isUserSignedIn linkUserExtendedResponse %s", linkUserExtendedResponse.getJSON());
								// mOTP sigin
								if (QuopnUtils.isActivityRunningForContext(mContext)) {
									linkUserExtended = linkUserExtendedResponse;
								}
								stopCustomProgress();
							}

							@Override
							public void error(CitrusError citrusError) {
								dialog.dismiss();
								showCitrusLoginMessage(false, citrusError.getMessage());
							}
						});
					}
				});
				dialogButtonCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});

				dialog.show();
			}

		});
	}

	public void finishAll() {
		finish();

	}

	public void stopCustomProgress() {
		if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
			mCustomProgressDialog.dismiss();

		}
	}

	public void showCustomProgress() {
		if (mCustomProgressDialog == null) {
			new CustomProgressDialog(MainActivity.this);
			mCustomProgressDialog.show();
		} else if (!mCustomProgressDialog.isShowing()) {
			mCustomProgressDialog.show();
		}
	}
}