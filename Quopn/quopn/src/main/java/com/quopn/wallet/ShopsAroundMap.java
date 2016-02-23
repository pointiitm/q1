package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.DataBaseHelper;
import com.quopn.wallet.data.model.MapData;
import com.quopn.wallet.data.model.QuopnStoreListData;
import com.quopn.wallet.data.model.StoreDataList;
import com.quopn.wallet.data.model.StoreTypeList;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.QuopnTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import com.quopn.imageloading.ImageLoader;


public class ShopsAroundMap extends ActionBarActivity implements
		ConnectionListener {

	private double latitude;
	private double longitude;
	//CustomProgressDialog mCustomProgressDialog;
	private double priviouslatitute=0;
	private double priviouslongitute=0;
	ConnectionListener mConnectionListener_shops_around;
	private GoogleMap googleMap;
	private Marker storeMarker = null;
	private Marker storeMarker1 = null;
	private Marker storeMarker2 = null;
	private  String store_name,store_adress,store_opentime,store_uid;//store_type
	int store_type_id,store_accuracy,store_accuracy1;
	private HashMap<Integer,Bitmap> bitmapHashMap = new HashMap<Integer,Bitmap>();

	DataBaseHelper dbHelper;
	List<StoreDataList> storeDataList = new ArrayList<StoreDataList>();
	List<StoreTypeList> mStoreTypeLists = new ArrayList<StoreTypeList>();
	private Timer timer;
	boolean runtimer =false;
	LatLng location;
	BitmapDescriptor bitmapMarker;
	@SuppressLint("SetJavaScriptEnabled")
	String business_name,business_add,open_hours;
	double current_lat,current_long;
	private RelativeLayout relLay_Legend;

	EditText search_maptext;
	Circle mycircle;
	Circle mycircle1;
	boolean openhour = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.shops_around);

		dbHelper = DataBaseHelper.getInstance(this);
		storeDataList = dbHelper.getAllLocationDetail();
		mStoreTypeLists = dbHelper.getAllStoreTypes();
		bitmapHashMap = QuopnApplication.getInstance().getBitmapHashMap();
//		Thread t = new Thread(){
//			@Override
//			public void run() {
//
//			}
//		};
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				relLay_Legend = (RelativeLayout)findViewById(R.id.show_mapdesc);
				{
					if (!mStoreTypeLists.isEmpty() && mStoreTypeLists.size() > 0) {

//				relLay_Legend.removeAllViews();
						LinearLayout parent1 = new LinearLayout(ShopsAroundMap.this);
						parent1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
						parent1.setOrientation(LinearLayout.VERTICAL);

						for (int i = 0; i < mStoreTypeLists.size(); i++) {
							// dynamic legend
							LinearLayout parent = new LinearLayout(ShopsAroundMap.this);
							LinearLayout.LayoutParams params_parent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							params_parent.bottomMargin = 10;
							parent.setLayoutParams(params_parent);
							parent.setOrientation(LinearLayout.HORIZONTAL);

							String cat_name = mStoreTypeLists.get(i).getCat_name();
							int cat_id = mStoreTypeLists.get(i).getCat_id();

							// IV
							ImageView iv = new ImageView(ShopsAroundMap.this);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							params.gravity = Gravity.CENTER_VERTICAL;
							iv.setLayoutParams(params);
							Bitmap bitmap = null;
							if (!bitmapHashMap.isEmpty() && bitmapHashMap.containsKey(cat_id)) {
								bitmap = bitmapHashMap.get(cat_id);
							} else {
								switch (cat_id) {
									case 1:
										// general
										bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
												R.drawable.generalstore_pin_ico);
										break;
									case 2:
										//medical
										bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
												R.drawable.wellnessstore_pin_ico);
										break;
									case 3:
										//hypercity
										bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
												R.drawable.hyperlstore_pin_ico);
										break;
									default:
										bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
												R.drawable.generalstore_pin_ico);
										break;
								}
							}
							iv.setImageBitmap(bitmap);

							// QuopnTextView
							QuopnTextView qtv = new QuopnTextView(ShopsAroundMap.this);
							LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							params1.leftMargin = 10;
							params1.gravity = Gravity.CENTER_VERTICAL;
							qtv.setLayoutParams(params1);
							qtv.setTextSize(13);
							qtv.setText(cat_name);

							// adding
							parent.addView(iv);
							parent.addView(qtv);

							parent1.addView(parent);
						}

						relLay_Legend.addView(parent1,0);
					}
				}
			}
		});
//		relLay_Legend = (RelativeLayout)findViewById(R.id.show_mapdesc);
//		{
//			if (!mStoreTypeLists.isEmpty() && mStoreTypeLists.size() > 0) {
//
////				relLay_Legend.removeAllViews();
//				LinearLayout parent1 = new LinearLayout(this);
//				parent1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//				parent1.setOrientation(LinearLayout.VERTICAL);
//
//				for (int i = 0; i < mStoreTypeLists.size(); i++) {
//					// dynamic legend
//					LinearLayout parent = new LinearLayout(this);
//					LinearLayout.LayoutParams params_parent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//					params_parent.bottomMargin = 10;
//					parent.setLayoutParams(params_parent);
//					parent.setOrientation(LinearLayout.HORIZONTAL);
//
//					String cat_name = mStoreTypeLists.get(i).getCat_name();
//					int cat_id = mStoreTypeLists.get(i).getCat_id();
//
//					// IV
//					ImageView iv = new ImageView(this);
//					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//					params.gravity = Gravity.CENTER_VERTICAL;
//					iv.setLayoutParams(params);
//					Bitmap bitmap = null;
//					if (!bitmapHashMap.isEmpty() && bitmapHashMap.containsKey(cat_id)) {
//						bitmap = bitmapHashMap.get(cat_id);
//					} else {
//						switch (cat_id) {
//							case 1:
//								// general
//								bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//										R.drawable.generalstore_pin_ico);
//								break;
//							case 2:
//								//medical
//								bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//										R.drawable.wellnessstore_pin_ico);
//								break;
//							case 3:
//								//hypercity
//								bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//										R.drawable.generalstore_pin_ico);
//								break;
//							default:
//								bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//										R.drawable.generalstore_pin_ico);
//								break;
//						}
//					}
//					iv.setImageBitmap(bitmap);
//
//					// QuopnTextView
//					QuopnTextView qtv = new QuopnTextView(this);
//					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
//							LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//					params1.leftMargin = 10;
//					qtv.setLayoutParams(params1);
//					qtv.setTextSize(10);
//					qtv.setText(cat_name);
//
//					// adding
//					parent.addView(iv);
//					parent.addView(qtv);
//
//					parent1.addView(parent);
//				}
//
//				relLay_Legend.addView(parent1,0);
//			}
//		}
		setConnectionListner();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			business_name = extras.getString("business_name");
			business_add = extras.getString("buisness_add");
			open_hours = extras.getString("open_hours");
			current_lat = extras.getDouble("latitude");
			current_long = extras.getDouble("longitude");
//			store_type = extras.getString("store_type");
			store_accuracy = extras.getInt("store_accuracy");
			store_type_id = extras.getInt("store_type_id");
//			store_uid = extras.getString("buisness_uid");
		}



		if (googleMap == null) {
			googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
			else
				googleMap.setMyLocationEnabled(true);

		}

		googleMap.setInfoWindowAdapter(new CustomWindowAdapter());
//		bitmapMarker = getBitmapDesriptorForStoreTypeID(store_type_id);


//		if(!bitmapHashMap.isEmpty()){
//			bitmapMarker = BitmapDescriptorFactory.fromBitmap();
//
//		}
//		else if(){
//			bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.wellnessstore_pin_ico);
//
//		}



//		MarkerOptions markerOptions1= new MarkerOptions().position(new LatLng(current_lat, current_long)).title(business_name).snippet(business_add).icon(bitmapMarker);
//		storeMarker1 = googleMap.addMarker(markerOptions1);
//		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(current_lat, current_long));
//		googleMap.moveCamera(center);
//		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));
//		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));
//
//		priviouslatitute = current_lat;
//		priviouslongitute = current_long;
//
//		CircleOptions circleOptions = new CircleOptions()
//				.center(new LatLng(current_lat, current_long))
//				.radius(store_accuracy)
//				.fillColor((0x404487FF))
//				.strokeColor(0x404487FF)
//				.strokeWidth(1);
//
//		mycircle = googleMap.addCircle(circleOptions);
//
		//storeMarker1.showInfoWindow();

		for (StoreDataList storedatalist : storeDataList) {

			store_name = storedatalist.getBusiness_Name();
			store_adress = storedatalist.getBusiness_Address();
			latitude = storedatalist.getLatitude();
			longitude = storedatalist.getLongitude();
			store_opentime = storedatalist.getOpen_hours();
//			store_type = storedatalist.getStoretype();
			store_accuracy1 = storedatalist.getAccuracy();
			store_type_id = storedatalist.getStoretypeid();

			bitmapMarker = getBitmapDesriptorForStoreTypeID(store_type_id);
//			if(store_type.equals("General")){
//				bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.generalstore_pin_ico);
//			}
//			else if(store_type.equals("Medical")){
//				bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.wellnessstore_pin_ico);
//			}

			MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(store_name).snippet(store_adress).icon(bitmapMarker);

			if (current_lat == latitude && current_long == longitude) {
				//storeMarker.showInfoWindow();
//				showCustomInfoWindow(storeMarker);
				storeMarker1 = storeMarker;
				storeMarker1 = googleMap.addMarker(markerOptions);
				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(current_lat, current_long));
				googleMap.moveCamera(center);
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));

				priviouslatitute = current_lat;
				priviouslongitute = current_long;

				CircleOptions circleOptions = new CircleOptions()
						.center(new LatLng(current_lat, current_long))
						.radius(store_accuracy)
						.fillColor((0x404487FF))
						.strokeColor(0x404487FF)
						.strokeWidth(1);

				mycircle = googleMap.addCircle(circleOptions);
				storeMarker1.showInfoWindow();

			} else {
				storeMarker = googleMap.addMarker(markerOptions);
			}
//			}
		}

//		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(current_lat, current_long));
//		googleMap.moveCamera(center);
//		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));
//		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_lat, current_long), 14.0f));
//
//		priviouslatitute = current_lat;
//		priviouslongitute = current_long;
//
//		CircleOptions circleOptions = new CircleOptions()
//				.center(new LatLng(current_lat, current_long))
//				.radius(store_accuracy)
//				.fillColor((0x404487FF))
//				.strokeColor(0x404487FF)
//				.strokeWidth(1);
//
//		mycircle = googleMap.addCircle(circleOptions);
//
//		storeMarker1.showInfoWindow();


		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				//Intent callIntent = new Intent(Intent.ACTION_CALL);
				//callIntent.setData(Uri.parse("tel:09323915104"));
				//startActivity(callIntent);
				showCustomInfoWindow(marker);

				return true;
			}
		});


		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				// TODO Auto-generated method stub

				if(runtimer==false){

					runtimer=true;
					if(timer!=null){
						timer.cancel();
						timer.purge();
					}
					timer = new Timer();
					timer.schedule(new ExeTimerTask(), 3000, 10000);

				}
				else{
					timer.cancel();
					runtimer=true;
					timer.purge();
					timer = new Timer();
					timer.schedule(new ExeTimerTask(), 3000, 10000);
				}

				//runtimer =false;

			}


		});


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
				openhour=false;
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


		search_maptext = (EditText)findViewById(R.id.search_maptext);
		search_maptext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();

			}
		});
	}

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

				location = googleMap.getCameraPosition().target;
				if(location!=null){
					longitude = location.longitude;
					latitude = location.latitude;
					sendLatLong("" + latitude, "" + longitude);
					System.out.println("=======Get=Lat=Long=Move_Camera="+ latitude + longitude);

					for (StoreDataList storedatalist : storeDataList) {

						store_name = storedatalist.getBusiness_Name();
						store_adress = storedatalist.getBusiness_Address();
						latitude = storedatalist.getLatitude();
						longitude = storedatalist.getLongitude();
						store_opentime = storedatalist.getOpen_hours();
//						store_type = storedatalist.getStoretype();
						store_accuracy1 = storedatalist.getAccuracy();
						store_type_id = storedatalist.getStoretypeid();

						bitmapMarker = getBitmapDesriptorForStoreTypeID(store_type_id);
//						if(store_type.equals("General")){
//							bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.generalstore_pin_ico);
//
//						}
//						else if(store_type.equals("Medical")){
//							bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.wellnessstore_pin_ico);
//
//						}
						MarkerOptions markerOptions2 = new MarkerOptions().position(new LatLng(latitude,longitude)).title(store_name).snippet(store_adress).icon(bitmapMarker);
						storeMarker2 = googleMap.addMarker(markerOptions2);

					}


				}else{
					// send empty latitude and longitude values
					//sendLatLong("", "");
				}

			}else{
				//mTextNoQuopns.setText(R.string.please_connect_to_internet);
			}


		}


	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		openhour=false;
	}

	public void setConnectionListner(){

		mConnectionListener_shops_around = new ConnectionListener() {

			@Override
			public void onResponse(int responseResult, Response response) {
				// TODO Auto-generated method stub
				if (response instanceof QuopnStoreListData) {
//					CartActivity.mProgressBar.setVisibility(View.INVISIBLE);
					QuopnStoreListData storedatalist = (QuopnStoreListData) response;
					//mStoreDatalist.clear();

					if (storedatalist.isError() == true) {
//							ErrorHandler.showResponseErrorMessage(getActivity(),historydata.getMessage());
						//mListview.setAdapter(mstoreAdapter);
						//System.out.println("=======CheckResponse====="+storedatalist.isError());

					}else {
						storeDataList.addAll(storedatalist.getStore());
						mStoreTypeLists.addAll(storedatalist.getStore_cat());// ankur

						//dbHelper.deleteStoreListTable();
					}

//						if (storeDataList.size()>0) {

					//mListview.setVisibility(View.VISIBLE);
					//mListview.setAdapter(mstoreAdapter);


//						} else {

					//mListview.setAdapter(mstoreAdapter);
//						}
					//mstoreAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				// TODO Auto-generated method stub

			}

			@Override
			public void myTimeout(String requestTag) {

			}
		};
	}

	private void showCustomInfoWindow(Marker marker) {
		int store_accy = 0;
		Projection projection = googleMap.getProjection();

		LatLng markerLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
		Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
		Point halfScreenAbove = new Point(markerScreenPosition.x, markerScreenPosition.y);
		LatLng aboveMarkerLatLng = projection.fromScreenLocation(halfScreenAbove);
		marker.showInfoWindow();
		CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);

		double clickedMarkerLat = marker.getPosition().latitude;
		double clickedMarkerLong = marker.getPosition().longitude;

		priviouslatitute = clickedMarkerLat;
		priviouslongitute = clickedMarkerLong;

		for(int i=0;i<storeDataList.size();i++){

			double lat = storeDataList.get(i).getLatitude();
			double longitude  = storeDataList.get(i).getLongitude();

			if((lat == clickedMarkerLat) && (longitude == clickedMarkerLong))
			{
				store_accy = storeDataList.get(i).getAccuracy();
				break;

			}

		}

		if(priviouslatitute!= 0 && priviouslongitute!= 0){

			if(mycircle1!=null){
				mycircle1.remove();

			}
			else if(mycircle!=null){
				mycircle.remove();
			}
		}

		CircleOptions circleOptions1 = new CircleOptions()
				.center(aboveMarkerLatLng)
				.fillColor(0x404487FF)
				.strokeColor(0x404487FF)
				.radius(store_accy)
				.strokeWidth(1);

		mycircle1 = googleMap.addCircle(circleOptions1);

		googleMap.moveCamera(center);
	}


	private class CustomWindowAdapter implements InfoWindowAdapter {

		private View view;

		public CustomWindowAdapter() {
			// TODO Auto-generated constructor stub
			view = getLayoutInflater().inflate(R.layout.storeinfowindow, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			if(storeMarker != null && storeMarker.isInfoWindowShown()) {
				storeMarker.hideInfoWindow();
				storeMarker.showInfoWindow();
			}
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			String store_opentime1 = null;
			TextView tvTitle = ((TextView) view.findViewById(R.id.title));
			tvTitle.setText(marker.getTitle().trim());
			tvTitle.setTypeface(null, Typeface.BOLD);
			TextView tvSnippet = ((TextView) view.findViewById(R.id.snippet));
			tvSnippet.setText(marker.getSnippet().trim());
			TextView tvOpenhours = ((TextView)view.findViewById(R.id.open_hours_tv));


			if(openhour==false){

				if(open_hours!=null && !open_hours.matches("")){
					tvOpenhours.setVisibility(View.VISIBLE);
					tvOpenhours.setText("OPEN HOURS:"+ open_hours);
					openhour =true;
				}
				else{
					//tvOpenhours.setVisibility(View.INVISIBLE);
					//openhour = true;

					double clickedMarkerLat1 = marker.getPosition().latitude;
					double clickedMarkerLong1 = marker.getPosition().longitude;

					for(int i=0;i<storeDataList.size();i++){

						double lat1 = storeDataList.get(i).getLatitude();
						double longitude1  = storeDataList.get(i).getLongitude();
						if((lat1 == clickedMarkerLat1) && (longitude1 == clickedMarkerLong1))
						{
							store_opentime1 = storeDataList.get(i).getOpen_hours();
							break;

						}

					}

					if(store_opentime1!=null){
						tvOpenhours.setVisibility(View.VISIBLE);
						tvOpenhours.setText("OPEN HOURS:"+ store_opentime1);
					}
					else{

						tvOpenhours.setVisibility(View.INVISIBLE);

					}
				}
			}
			return view;
		}
	}

	@Override
	public void onResponse(int responseResult, Response response) {

		switch (responseResult) {
			case CONNECTION_ERROR:
				//mCustomProgressDialog.dismiss();
				Dialog dialog=new Dialog(this, R.string.server_error_title,R.string.server_error);
				dialog.show();
				break;
			case PARSE_ERR0R:
				//mCustomProgressDialog.dismiss();
				Dialog dialog1=new Dialog(this, R.string.server_error_title,R.string.server_error);
				dialog1.show();
				break;

			case RESPONSE_OK:
				MapData data = (MapData) response;

				if (data.isError() == true) {
					Dialog dialog2=new Dialog(this, R.string.dialog_title_error, data.getMessage());
					dialog2.show();
				} else {
					if (data.getMessage() != null) {
						//webview.loadUrl(data.getMessage(), null);
					} else {
					}
				}
				break;

			default:
				break;
		}

	}

	private void sendLatLong(String lat, String long_) {

		if (QuopnUtils.isInternetAvailable(this)) {

			Map<String, String> params = new HashMap<String, String>();
//			params.put("walletid", PreferenceUtil.getInstance(this)
//					.getPreference(QuopnConstants.WALLET_ID_KEY));
			//params.put("devid", QuopnConstants.android_id);
			params.put("lat", lat);
			params.put("long", long_);

			//params.put("city", city + "~" + state);

			ConnectionFactory connectionFactory = new ConnectionFactory(this,mConnectionListener_shops_around);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SHOP_LIST_CODE);

		} else {
			//mCustomProgressDialog.dismiss();
			Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}

	}

	@Override
	public void onTimeout(ConnectRequest request) {
		// TODO Auto-generated method stub

	}

	private BitmapDescriptor getBitmapDesriptorForStoreTypeID (int storeTypeId) {
		BitmapDescriptor bitmapDescriptor = null;
		if (!bitmapHashMap.isEmpty() && bitmapHashMap.containsKey(storeTypeId)) {
			bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmapHashMap.get(storeTypeId));
		} else {
			Log.e("Bitmap", "bitmapHashMap is empty");
		}
		return (bitmapDescriptor != null) ? bitmapDescriptor : BitmapDescriptorFactory.fromResource(R.drawable.generalstore_pin_ico);
	}

	@Override
	public void myTimeout(String requestTag) {

	}
}
