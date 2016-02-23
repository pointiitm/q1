package com.quopn.wallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.wallet.adapter.QuopnStoreListAdapter;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.DataBaseHelper;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.QuopnStoreListData;
import com.quopn.wallet.data.model.StoreDataList;
import com.quopn.wallet.data.model.StoreTypeList;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class QuopnStoreList extends ActionBarActivity {

	ListView mListview;
	List<StoreDataList> mStoreDatalist = new ArrayList<StoreDataList>();
	List<StoreTypeList> mStoreTypeLists = new ArrayList<StoreTypeList>();
	ConnectionListener mConnectionListener_storelist;
	QuopnStoreListAdapter mstoreAdapter;
	CustomProgressDialog mCustomProgressDialog;
	WebView webview;
	private boolean isNetworkProvider;
	private LocationManager locationManager;
	private Location location;
	private boolean isGPSProvider;
	private static final long MIN_TIME_BW_UPDATES = 600000;
	private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 600000;
	DataBaseHelper dbHelper;
	private double latitude;
	private double longitude;
	ImageView search_image;
	EditText edit_search_text;
	String search_result;
	private Timer timer;
	boolean runtimer =false;
	@Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quopn_store_list);
		mListview = (ListView) findViewById(R.id.list);
		mstoreAdapter = new QuopnStoreListAdapter(this, 0, mStoreDatalist);
		// mListview.setAdapter(mstoreAdapter);
		mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
		dbHelper = DataBaseHelper.getInstance(this);
		//dbHelper.deleteStoreListTable();
		//timer = new Timer();
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
				if(timer!=null){
					timer.cancel();
				}
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

		search_image = (ImageView)findViewById(R.id.search_btn);
		search_image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				search_result = edit_search_text.getText().toString();
				if(timer!=null){
					timer.cancel();

				}

				if(search_result.equals("")){

					return;
				}else{

					if (QuopnUtils.isInternetAvailable(QuopnStoreList.this)) {
						//QuopnUtils.getStoreList(this,mConnectionListener_storelist);
						mCustomProgressDialog.show();
						search_result = edit_search_text.getText().toString();
						mstoreAdapter.clear();
						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						//location = getLocation();
						if(location!=null){
							longitude = location.getLongitude();
							latitude = location.getLatitude();
							sendLatLongForSearch("" + latitude, "" + longitude);

						}else{
							// send empty latitude and longitude values
							sendLatLongForSearch("", "");
						}

					}else{
						//mTextNoQuopns.setText(R.string.please_connect_to_internet);
						Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
						dialog.show();
					}
				}
			}
		});
		edit_search_text = (EditText)findViewById(R.id.search_text);

		edit_search_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(runtimer==false){

					runtimer=true;
					if(timer!=null){
						timer.cancel();
						timer.purge();
					}
					// mCustomProgressDialog.show();
					timer = new Timer();
					timer.schedule(new ExeTimerTask(), 3000, 3000);

				}
				else{
					timer.cancel();
					runtimer=true;
					timer.purge();
					timer = new Timer();
					timer.schedule(new ExeTimerTask(), 3000, 3000);
					//mCustomProgressDialog.show();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		mConnectionListener_storelist = new ConnectionListener() {

			@Override
			public void onResponse(int responseResult, Response response) {
				// TODO Auto-generated method stub
				if (response instanceof QuopnStoreListData) {
//				CartActivity.mProgressBar.setVisibility(View.INVISIBLE);
					final QuopnStoreListData storedatalist = (QuopnStoreListData) response;

					//mStoreDatalist.clear();

					if (storedatalist.isError() == true) {
//						ErrorHandler.showResponseErrorMessage(getActivity(),historydata.getMessage());
						//mListview.setAdapter(mstoreAdapter);
						mListview.setVisibility(View.INVISIBLE);
						mCustomProgressDialog.dismiss();
						if (storedatalist.getMessage() != null) {

							Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_store_search,storedatalist.getMessage());
							dialog.setOnAcceptButtonClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									edit_search_text.setText("");
									if (storedatalist != null) {
										mStoreDatalist.addAll(storedatalist.getStore());
										mStoreTypeLists.addAll(storedatalist.getStore_cat());
									} else {
										Log.e("QuopnStoreList","storedatalist is null");
									}
								}
							});
							dialog.show();
						}

					}else {

						mStoreDatalist.addAll(storedatalist.getStore());
						mStoreTypeLists.addAll(storedatalist.getStore_cat());
//						mCustomProgressDialog.dismiss();
						//mListview.invalidate();
						//mstoreAdapter.notifyDataSetChanged();

					}

					if (mstoreAdapter.getCount()>0) {
						mListview.setVisibility(View.VISIBLE);
						mListview.setAdapter(mstoreAdapter);
						dbHelper.deleteStoreListTable();

						for(int i=0;i<mStoreDatalist.size();i++){

							String store_Uid = mStoreDatalist.get(i).getStore_uid();
							String buisness_name = mStoreDatalist.get(i).getBusiness_Name();
							String buisness_add = mStoreDatalist.get(i).getBusiness_Address();
							String telephone = mStoreDatalist.get(i).getTelephone1();
							String telephone1 = mStoreDatalist.get(i).getTelephone2();
							String open_hours = mStoreDatalist.get(i).getOpen_hours();
							String distance = mStoreDatalist.get(i).getDistance();
							double latitude = mStoreDatalist.get(i).getLatitude();
							double longitude = mStoreDatalist.get(i).getLongitude();
							String store_type = mStoreDatalist.get(i).getStoretype();
							int store_accuracy = mStoreDatalist.get(i).getAccuracy();
							int store_type_id = mStoreDatalist.get(i).getStoretypeid();
							dbHelper.addStoreData(new StoreDataList(store_Uid,buisness_name,buisness_add,telephone,telephone1,latitude,longitude,open_hours,distance,store_type,store_accuracy,store_type_id));
						}

						// ankur
//						for(int i=0;i<mStoreTypeLists.size();i++){
//							String cat_name = mStoreTypeLists.get(i).getCat_name();
//							int cat_id = mStoreTypeLists.get(i).getCat_id();
//							String cat_icon_url = mStoreTypeLists.get(i).getCat_icon_url();
//							dbHelper.addStoreTypeData(new StoreTypeList(cat_name, cat_id, cat_icon_url));
//						}

						// adding to local db and downloading images

//						if (!mStoreTypeLists.isEmpty() && mStoreTypeLists.size() > 0) {
//							for (int i = 0; i < mStoreTypeLists.size(); i++) {
//								String cat_name = mStoreTypeLists.get(i).getCat_name();
//								int cat_id = mStoreTypeLists.get(i).getCat_id();
//								String cat_icon_url = mStoreTypeLists.get(i).getCat_icon_url();
//								dbHelper.addStoreTypeData(new StoreTypeList(cat_name, cat_id, cat_icon_url));
//							}
//						}

						if (!mStoreTypeLists.isEmpty() && mStoreTypeLists.size() > 0) {
							dbHelper.deleteStoreTypeData(); // clearing entries
							for (int i = 0; i < mStoreTypeLists.size(); i++) {
								String cat_name = mStoreTypeLists.get(i).getCat_name();
								int cat_id = mStoreTypeLists.get(i).getCat_id();
								String cat_icon_url = mStoreTypeLists.get(i).getCat_icon_url();
								dbHelper.addStoreTypeData(new StoreTypeList(cat_name, cat_id, cat_icon_url));
							}
							QuopnApplication.getInstance().downloadBitmaps(dbHelper.getStoreTypeHashMap());
						}

						Log.d("getStoreTypeHashMap",String.valueOf(dbHelper.getStoreTypeHashMap().size()));

					} else {
						mListview.setAdapter(mstoreAdapter);
						mstoreAdapter.notifyDataSetChanged();
					}

					mCustomProgressDialog.dismiss();

				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
							mCustomProgressDialog.dismiss();
							Dialog dialog=new Dialog(QuopnStoreList.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection);
							dialog.show();
						}
					}
				});
			}

			@Override
			public void myTimeout(String requestTag) {
				if(requestTag.equals("39")){
					mCustomProgressDialog.dismiss();
				}
			}
		};

		if (QuopnUtils.isInternetAvailable(this)) {
			//QuopnUtils.getStoreList(this,mConnectionListener_storelist);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			location = getLocation();
			if(location!=null){
				longitude = location.getLongitude();
				latitude = location.getLatitude();
				sendLatLong("" + latitude, "" + longitude);
				System.out.println("=======Get=Lat=Long=="+ latitude + longitude);
			}else{
				// send empty latitude and longitude values
				sendLatLong("", "");
			}

		}else{
			//mTextNoQuopns.setText(R.string.please_connect_to_internet);
			Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//dbHelper.deleteStoreListTable();
		//edit_search_text.setText("");
	}

//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		edit_search_text.setText("");
//	}

	public class ExeTimerTask extends TimerTask {
		@Override
		public void run() {

			if(runtimer==true){
				mHandler.obtainMessage(1).sendToTarget();
				runtimer = false;
			}
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (QuopnUtils.isInternetAvailable(getApplicationContext())) {

				//mCustomProgressDialog.show();
				search_result = edit_search_text.getText().toString();
				mstoreAdapter.clear();
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				location = getLocation();
				if(location!=null){
					longitude = location.getLongitude();
					latitude = location.getLatitude();
					sendLatLongForSearch("" + latitude, "" + longitude);

				}else{
					// send empty latitude and longitude values
					sendLatLongForSearch("", "");
				}

			}else{
				//mTextNoQuopns.setText(R.string.please_connect_to_internet);
				Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
				dialog.show();
			}

		}


	};

	private void sendLatLong(String lat, String long_) {

		if (QuopnUtils.isInternetAvailable(this)) {

			String city = "", state = "";
			if(PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY) != null
					&& PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE) != null){

				Cursor city_cursor=getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_CITY_ID + " = ? "  , new String[]{PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY)},null);
				city_cursor.moveToFirst();
				Cursor state_cursor=getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, ITableData.TABLE_STATES.COLUMN_STATE_ID + " = ? "  , new String[]{PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE)},null);
				state_cursor.moveToFirst();
				city=city_cursor.getString(city_cursor.getColumnIndex(ITableData.TABLE_CITIES.COLUMN_CITY_NAME));
				state=state_cursor.getString(state_cursor.getColumnIndex(ITableData.TABLE_STATES.COLUMN_STATE_NAME));

				city = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY);
				state = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE);

			}

			Map<String, String> params = new HashMap<String, String>();
//			params.put("walletid", PreferenceUtil.getInstance(this)
//					.getPreference(QuopnConstants.WALLET_ID_KEY));
//			params.put("devid", QuopnConstants.android_id);
			params.put("lat", lat);
			params.put("long", long_);

			params.put("cityid", city);
			params.put("stateid", state);


			ConnectionFactory connectionFactory = new ConnectionFactory(this, mConnectionListener_storelist);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SHOP_LIST_CODE);

		} else {
			//mCustomProgressDialog.dismiss();
			Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}

	}

	private void sendLatLongForSearch(String lat, String long_) {

		if (QuopnUtils.isInternetAvailable(this)) {

			String city = "", state = "";
			if(PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY) != null
					&& PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE) != null){

				Cursor city_cursor=getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_CITY_ID + " = ? "  , new String[]{PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY)},null);
				city_cursor.moveToFirst();
				Cursor state_cursor=getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, ITableData.TABLE_STATES.COLUMN_STATE_ID + " = ? "  , new String[]{PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE)},null);
				state_cursor.moveToFirst();
				city=city_cursor.getString(city_cursor.getColumnIndex(ITableData.TABLE_CITIES.COLUMN_CITY_NAME));
				state=state_cursor.getString(state_cursor.getColumnIndex(ITableData.TABLE_STATES.COLUMN_STATE_NAME));

				city = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY);
				state = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE);

			}

			Map<String, String> params = new HashMap<String, String>();
//			params.put("walletid", PreferenceUtil.getInstance(this)
//					.getPreference(QuopnConstants.WALLET_ID_KEY));
//			params.put("devid", QuopnConstants.android_id);
			params.put("lat", lat);
			params.put("long", long_);
			params.put("search", search_result);

			params.put("cityid", city);
			params.put("stateid", state);

			mCustomProgressDialog.show();
			ConnectionFactory connectionFactory = new ConnectionFactory(this, mConnectionListener_storelist);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SEARCH_LIST_CODE);
			//connectionFactory.createConnection(QuopnConstants.MAP_API_CODE);

		} else {
			//mCustomProgressDialog.dismiss();
			Dialog dialog=new Dialog(QuopnStoreList.this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}

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

	@SuppressWarnings("deprecation")
	public void showLocationSettingsDialog() {

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		alertDialog.setTitle("Location Service");

		alertDialog.setMessage(getString(R.string.location_service));

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				Intent i = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

				if (i.resolveActivity(QuopnStoreList.this.getPackageManager()) == null) {
					Toast.makeText(QuopnStoreList.this,
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


}
