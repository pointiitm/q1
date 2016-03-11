package com.quopn.wallet.fragments;

/**
 * @author Sandeep
 * @modified by Sivnarayan
 *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.CartAdapter;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CartData;
import com.quopn.wallet.data.model.CartListData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.QuopnTextView;
import com.quopn.wallet.views.RefreshableListView;

import java.util.ArrayList;
import java.util.List;
//import com.quopn.wallet.views.RefreshableListView.OnRefreshListener;

@SuppressLint("ValidFragment")
public class CartFragment extends Fragment implements OnItemClickListener, OnClickListener ,OnRefreshListener{
	private static final String TAG="Quopn/CartFragment";
	//old code
	//private ListView mListview;
	
	//siv new code for making the list refreshable on drag down 
	private RefreshableListView mListview;
//	public ProgressDialog mProgressDialog;
	private CartFragment mCartFragment;
	
	private ConnectionListener mConnectionListener_cart;
	private List<CartListData> mCartListContainer = new ArrayList<CartListData>();
	private CartAdapter mCartAdapter;
	private String mTitle;

	private int mColorRes = -1;
	private static final String ARG_POSITION = "position";
	private String mPagerPosition;
	private RelativeLayout mRelLayNoQuopns;
	//private LinearLayout mRelLayNoQuopns;
	private ImageView mImgNoQuopns;
	private TextView mTextNoQuopns;
	private TextView mImgNoQuopnsButton;
	private TextView mHeader_text;
	private TextView mSaving_text1;
	private TextView mSaving_text2;
	private TextView mHeader;
	private String mTagMyCart = "My Cart";
	private double mTotalSaving=0;
	private ProgressBar mProgressBar;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private SwipeRefreshLayout mSwipeRefresh_empty_content_layout;
	private SlidingFragmentActivity mSlidingFragActivity;
	
	public CartFragment() {
		setRetainInstance(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mSlidingFragActivity = (SlidingFragmentActivity) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCartFragment = this;
		mCartAdapter = new CartAdapter(mSlidingFragActivity, 0,mCartListContainer);
		mListview.setAdapter(mCartAdapter);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.myproducts, null);
		
		mListview = (RefreshableListView) rootView.findViewById(R.id.items);
		//mListview.setonRefreshListener(this);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_light);
		
		mSwipeRefresh_empty_content_layout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh_empty_content);
		mSwipeRefresh_empty_content_layout.setOnRefreshListener(this);
		mSwipeRefresh_empty_content_layout.setColorScheme(android.R.color.holo_red_light);
		
		mHeader = mListview.getHeaderTextView();
		mHeader.setText("");
		mHeader.setVisibility(View.GONE); // to hide the header
		
		mRelLayNoQuopns = (RelativeLayout) rootView.findViewById(R.id.rellay_empty_content);
		mImgNoQuopns = (ImageView) rootView.findViewById(R.id.img_empty_content);
		mTextNoQuopns = (QuopnTextView) rootView.findViewById(R.id.text_empty_content);
		mImgNoQuopnsButton = (TextView) rootView.findViewById(R.id.img_empty_content_text);
		mHeader_text=(TextView)rootView.findViewById(R.id.header_text);
		mSaving_text1 = (TextView)rootView.findViewById(R.id.saving_text1);
		mSaving_text1.setTypeface(null, Typeface.BOLD);
		mSaving_text2=(TextView)rootView.findViewById(R.id.saving_text2);
		mSaving_text2.setTypeface(null, Typeface.BOLD);
		mListview.setOnItemClickListener(this);
		mHeader_text.setText(getString(R.string.mycart_header_text));
		mProgressBar=(ProgressBar)rootView.findViewById(R.id.progressbar);
		updateViews();


		return rootView;
	}

	public void setSavingsAmount(double argSavings){
		mSaving_text2.setText(getResources().getString(R.string.rupee_symbol) + argSavings);
	}

	public void updateViews() {
		//mProgressBar.setVisibility(View.VISIBLE);
			if(QuopnConstants.ISREMOVEFROMCART==true){
				Cursor cursor1 = null;
				cursor1 = mSlidingFragActivity.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null,ITableData.TABLE_MYCART.COLUMN_CARTID + " desc");
				if(cursor1==null || cursor1.getCount()==0){
					mTotalSaving=0;
					mCartListContainer.clear();
//					mSaving_text2.setText(String.valueOf(mTotalSaving));
					setSavingsAmount(mTotalSaving);
					mProgressBar.setVisibility(View.INVISIBLE);
					mListview.setVisibility(View.INVISIBLE);
					mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
					mRelLayNoQuopns.setVisibility(View.VISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
					mImgNoQuopns.setImageResource(R.drawable.my_cart_empty);
					mTextNoQuopns.setText(R.string.text_empty_my_cart);
					mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
					mImgNoQuopnsButton.setText(R.string.browse_current_categories);
					mImgNoQuopnsButton.setTag(mTagMyCart);
					mImgNoQuopnsButton.setOnClickListener(CartFragment.this);
				}else{
					mProgressBar.setVisibility(View.INVISIBLE);
					mRelLayNoQuopns.setVisibility(View.INVISIBLE);
					mListview.setVisibility(View.VISIBLE);
					mSwipeRefreshLayout.setVisibility(View.VISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
					mCartListContainer.clear();
					while(cursor1.moveToNext()){
						String approxsaving=cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING));
						mTotalSaving=Double.parseDouble(approxsaving);
						mCartListContainer.add(getCartItemFromCursor(cursor1));
					}
//					mSaving_text2.setText(""+mTotalSaving);
					setSavingsAmount(mTotalSaving);
					mListview.setAdapter(mCartAdapter);
				}

			}else{ // refreshing part
				mProgressBar.setVisibility(View.INVISIBLE);
				mConnectionListener_cart=new ConnectionListener() {
					@Override
					public void onResponse(int responseResult,Response response) {						
						
						if (response instanceof CartData) {
							CartData cartdata = (CartData) response;
							mCartListContainer.clear();
							mTotalSaving=0;
								if (cartdata.isError() == true) {
									mListview.setVisibility(View.INVISIBLE);
									mRelLayNoQuopns.setVisibility(View.VISIBLE);
									mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
									mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
									mImgNoQuopns.setImageResource(R.drawable.my_cart_empty);
									mTextNoQuopns.setText(R.string.text_empty_my_cart);
									mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
									mImgNoQuopnsButton.setText(R.string.browse_current_categories);
									mImgNoQuopnsButton.setTag(mTagMyCart);
//									mSaving_text2.setText(""+mTotalSaving);
									setSavingsAmount(mTotalSaving);
									mImgNoQuopnsButton.setOnClickListener(CartFragment.this);
									mListview.setAdapter(mCartAdapter);
								}else {
									
									//delete the all mycart data from database
									mSlidingFragActivity.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART,null, null);
									for (CartListData cartListData: cartdata.getCartdetails()) {
										// MyCart data
										populateMyCartDB(cartListData,cartdata.getWalletid(),cartdata.getApprox_saving());
									}
									
								}
								Cursor cursor1 = null;
								cursor1 = mSlidingFragActivity.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null,ITableData.TABLE_MYCART.COLUMN_CARTID + " desc");
								//Log.i(TAG, ""+cursor1.getCount());
								QuopnConstants.MY_CART_COUNT=cursor1.getCount();
								PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT, QuopnConstants.MY_CART_COUNT);
								if(cursor1 == null || cursor1.getCount() == 0){
									cursor1.moveToFirst();
//									mSaving_text2.setText(""+mTotalSaving);
									setSavingsAmount(mTotalSaving);
									mListview.setVisibility(View.INVISIBLE);
									mRelLayNoQuopns.setVisibility(View.VISIBLE);
									mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
									mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
									mImgNoQuopns.setImageResource(R.drawable.my_cart_empty);
									mTextNoQuopns.setText(R.string.text_empty_my_cart);
									mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
									mImgNoQuopnsButton.setText(R.string.browse_current_categories);
									mImgNoQuopnsButton.setTag(mTagMyCart);
									mImgNoQuopnsButton.setOnClickListener(CartFragment.this);
									mListview.setAdapter(mCartAdapter);
									
								} else {
									cursor1.moveToFirst();
									cartdata.setWalletid(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_WALLETID)));
									cartdata.setApprox_saving(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING)));
									mTotalSaving=Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING)));
									mCartListContainer.clear();
									while (!cursor1.isAfterLast()) {
										mCartListContainer.add(getCartItemFromCursor(cursor1));
										cursor1.moveToNext();
									}
//									mSaving_text2.setText(""+mTotalSaving);
									setSavingsAmount(mTotalSaving);
									mRelLayNoQuopns.setVisibility(View.INVISIBLE);
									mListview.setVisibility(View.VISIBLE);
									mSwipeRefreshLayout.setVisibility(View.VISIBLE);
									mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
									mListview.setAdapter(mCartAdapter);
								}
								mCartAdapter.notifyDataSetChanged();
								sendBroadCast(mSlidingFragActivity);
						}
					
					}

					@Override
					public void onTimeout(ConnectRequest request) {
						
					}

					@Override
					public void myTimeout(String requestTag) {
						//mProgressBar.setVisibility(View.INVISIBLE);
					}

				};
				if (QuopnUtils.isInternetAvailable(mSlidingFragActivity)) {
					QuopnUtils.getCart(mSlidingFragActivity,mConnectionListener_cart);
					
				}else{
					mTextNoQuopns.setText(R.string.please_connect_to_internet);
				}
			}
		

	}
	
	@Override
	public void onRefresh() {
		Log.d(TAG, "OnRefresh is called..........");
		QuopnConstants.ISREMOVEFROMCART=false;
		/*mProgressDialog = new ProgressDialog(mCartFragment.getActivity(), "",Color.RED);
		mProgressDialog.show();*/
		refreshpage_whilePulltoRefresh();
	}
	
	private void refreshpage_whilePulltoRefresh(){
		if(QuopnConstants.ISREMOVEFROMCART==false){
			mProgressBar.setVisibility(View.INVISIBLE);
			mConnectionListener_cart=new ConnectionListener() {
				@Override
				public void onResponse(int responseResult,Response response) {						
					
					if (response instanceof CartData) {
						CartData cartdata = (CartData) response;
						mCartListContainer.clear();
						mTotalSaving=0;
							if (cartdata.isError() == true) {
								mListview.setVisibility(View.INVISIBLE);
								mRelLayNoQuopns.setVisibility(View.VISIBLE);
								mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
								mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
								mImgNoQuopns.setImageResource(R.drawable.my_cart_empty);
								mTextNoQuopns.setText(R.string.text_empty_my_cart);
								mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
								mImgNoQuopnsButton.setText(R.string.browse_current_categories);
								mImgNoQuopnsButton.setTag(mTagMyCart);
//								mSaving_text2.setText(""+mTotalSaving);
								setSavingsAmount(mTotalSaving);
								mImgNoQuopnsButton.setOnClickListener(CartFragment.this);
								mListview.setAdapter(mCartAdapter);
							}else {
								
								//delete the all mycart data from database
								mSlidingFragActivity.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART,null, null);
								for (CartListData cartListData: cartdata.getCartdetails()) {
									// MyCart data
									populateMyCartDB(cartListData,cartdata.getWalletid(),cartdata.getApprox_saving());
								}
								
							}
							Cursor cursor1 = null;
							cursor1 = mSlidingFragActivity.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null,ITableData.TABLE_MYCART.COLUMN_CARTID + " desc");
							//Log.i(TAG, ""+cursor1.getCount());
							QuopnConstants.MY_CART_COUNT=cursor1.getCount();
							PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT, QuopnConstants.MY_CART_COUNT);
							if(cursor1 == null || cursor1.getCount() == 0){
								cursor1.moveToFirst();
//								mSaving_text2.setText(""+mTotalSaving);
								setSavingsAmount(mTotalSaving);
								mListview.setVisibility(View.INVISIBLE);
								mRelLayNoQuopns.setVisibility(View.VISIBLE);
								mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
								mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
								mImgNoQuopns.setImageResource(R.drawable.my_cart_empty);
								mTextNoQuopns.setText(R.string.text_empty_my_cart);
								mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
								mImgNoQuopnsButton.setText(R.string.browse_current_categories);
								mImgNoQuopnsButton.setTag(mTagMyCart);
								mImgNoQuopnsButton.setOnClickListener(CartFragment.this);
								mListview.setAdapter(mCartAdapter);
								
							} else {
								cursor1.moveToFirst();
								cartdata.setWalletid(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_WALLETID)));
								cartdata.setApprox_saving(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING)));
								mTotalSaving=Double.parseDouble(cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING)));
								mCartListContainer.clear();
								while (!cursor1.isAfterLast()) {
									mCartListContainer.add(getCartItemFromCursor(cursor1));
									cursor1.moveToNext();
								}
//								mSaving_text2.setText(""+mTotalSaving);
								setSavingsAmount(mTotalSaving);
								mRelLayNoQuopns.setVisibility(View.INVISIBLE);
								mListview.setVisibility(View.VISIBLE);
								mSwipeRefreshLayout.setVisibility(View.VISIBLE);
								mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
								mListview.setAdapter(mCartAdapter);
							}
							mCartAdapter.notifyDataSetChanged();
							sendBroadCast(mSlidingFragActivity);
							//mProgressDialog.dismiss();
							mSwipeRefreshLayout.setRefreshing(false);
							mSwipeRefresh_empty_content_layout.setRefreshing(false);
							mListview.onRefreshComplete();
					}
				
				}

				@Override
				public void onTimeout(ConnectRequest request) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void myTimeout(String requestTag) {
					//mProgressBar.setVisibility(View.INVISIBLE);
				}

			};
			if (QuopnUtils.isInternetAvailable(mSlidingFragActivity)) {
				QuopnUtils.getCart(mSlidingFragActivity,mConnectionListener_cart);
				
			}else{
				mTextNoQuopns.setText(R.string.please_connect_to_internet);
			}
		}
	}

	
	private CartListData getCartItemFromCursor(Cursor cursor) {
		if (cursor.isBeforeFirst() || cursor.isAfterLast()) { return null; }
		
		CartListData container = new CartListData();
		
		container.setCartid(cursor.getInt(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_CARTID )));
		container.setCampaignid(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNID )));
		container.setCampaignname(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNNAME )));
		container.setQuopncode(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_QUOPNCODE )));
		container.setCompanyname(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_COMPANYNAME )));
		container.setLong_description(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_LONG_DESCRIPTION)));
		container.setBrandname(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_BRANDNAME )));
		container.setThumb_icon1(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_THUMB_ICON1 )));
		container.setEnddate(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_ENDDATE )));
		container.setQuopnid(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_QUOPNID )));
		container.setSaving_value(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_SAVING_VALUE )));
		container.setCart_desc(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_CART_DESC )));
		container.setCart_image(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_CART_IMAGE )));
		container.setTitle(cursor.getString(cursor
				.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_TITLE)));
		
		return container;
	}
	
	
	private void populateMyCartDB(CartListData cartListData,String walletid,String approxsaving){
			ContentValues cv = new ContentValues();
			cv.put(ITableData.TABLE_MYCART.COLUMN_WALLETID, walletid);
			cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING, approxsaving);
			cv.put(ITableData.TABLE_MYCART.COLUMN_CARTID, cartListData.getCartid());
			cv.put(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNID, cartListData.getCampaignid());
			cv.put(ITableData.TABLE_MYCART.COLUMN_CAMPAIGNNAME, cartListData.getCampaignname());
			cv.put(ITableData.TABLE_MYCART.COLUMN_QUOPNCODE, cartListData.getQuopncode());
			cv.put(ITableData.TABLE_MYCART.COLUMN_COMPANYNAME, cartListData.getCompanyname());
			cv.put(ITableData.TABLE_MYCART.COLUMN_LONG_DESCRIPTION, cartListData.getLong_description());
			cv.put(ITableData.TABLE_MYCART.COLUMN_BRANDNAME, cartListData.getBrandname());
			cv.put(ITableData.TABLE_MYCART.COLUMN_THUMB_ICON1, cartListData.getThumb_icon1());
			cv.put(ITableData.TABLE_MYCART.COLUMN_ENDDATE, cartListData.getEnddate());
			cv.put(ITableData.TABLE_MYCART.COLUMN_QUOPNID, cartListData.getQuopnid());
			cv.put(ITableData.TABLE_MYCART.COLUMN_SAVING_VALUE, cartListData.getSaving_value());
			cv.put(ITableData.TABLE_MYCART.COLUMN_CART_DESC, cartListData.getCart_desc());
			cv.put(ITableData.TABLE_MYCART.COLUMN_CART_IMAGE, cartListData.getCart_image());
			cv.put(ITableData.TABLE_MYCART.COLUMN_TITLE , cartListData.getTitle());
		mSlidingFragActivity.getContentResolver().insert(ConProvider.CONTENT_URI_MYCART,cv);
	}
	
	
	private static void sendBroadCast(Context context){
		Intent intent = new Intent(QuopnConstants.BROADCAST_INITIAL_MYCARTCOUNTER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}



	private class SampleItem {
		public int iconRes;

		public SampleItem(int iconRes) {
			this.iconRes = iconRes;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		

	}
	

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.img_empty_content_text){
			TextView imgView = (TextView)v;
			String strTag = (String) imgView.getTag();
			 if (mSlidingFragActivity.getSlidingMenu().isSecondaryMenuShowing() || mSlidingFragActivity.getSlidingMenu().isMenuShowing()) {
				mSlidingFragActivity.getSlidingMenu().toggle();
		        }
			Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
			LocalBroadcastManager.getInstance(mSlidingFragActivity).sendBroadcast(intent);
		}
	}
	
	
	public void refreshpage(){
		updateViews(/*mPagerPosition*/);
	}
	
	private BroadcastReceiver redeem_BroadCast = new BroadcastReceiver() {
		@Override
        public void onReceive(Context context, Intent intent) {
			QuopnConstants.ISREMOVEFROMCART=false;
			refreshpage();
				//Toast is commented as per new design
				//Toast.makeText(getActivity(), getResources().getString(R.string.quopns_redeemed), Toast.LENGTH_LONG).show();
        }
    };
    
    private BroadcastReceiver update_MyCartList_AfterRemoveFromCart=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//set logic for refreshing
			QuopnConstants.ISREMOVEFROMCART=true;
			refreshpage();
		}
	};
    
    @Override
	public void onResume() {
    	super.onResume();
    	LocalBroadcastManager.getInstance(mSlidingFragActivity).registerReceiver(redeem_BroadCast,
                new IntentFilter(QuopnConstants.BROADCAST_REDEEM_QUOPNS));
    	
    	LocalBroadcastManager.getInstance(mSlidingFragActivity).registerReceiver(update_MyCartList_AfterRemoveFromCart,
                new IntentFilter(QuopnConstants.BROADCAST_UPDATE_MYCART_AFTER_REMOVEFROMCART));
    	
    };
    
    public void onDestroy() {
    	super.onDestroy();
    	LocalBroadcastManager.getInstance(mSlidingFragActivity).unregisterReceiver(redeem_BroadCast);
    	LocalBroadcastManager.getInstance(mSlidingFragActivity).unregisterReceiver(update_MyCartList_AfterRemoveFromCart);
    }
	

	

	
}
