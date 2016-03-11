package com.quopn.wallet;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnDetailsActivity.QuopnDetailAddToCartListener;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.adapter.QuopnListAdapter.QuopnSelectedListener;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.SingleCartDetails;
import com.quopn.wallet.data.model.WebIssueData;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.fragments.ProductCategoryListFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ListingByCategoryActivity extends ActionBarActivity implements QuopnSelectedListener,QuopnOperationsListener, QuopnDetailAddToCartListener {

	private static final String TAG = "Quopn/ListingByCategory";
	private ProductCategoryListFragment fragment;
	private TextView mHeader;
    private Uri uri;
    private String category_id_new;
	private ArrayList<QuopnOperationsListener> addQuopnoperationArrayList = new ArrayList<QuopnOperationsListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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

		setContentView(R.layout.listing_by_category);

        //mAnalysisManager.send(AnalysisEvents.CATEGORY, value);
        if (getIntent().getAction() == Intent.ACTION_VIEW
				&& (getIntent().getScheme().equals("http")
					|| getIntent().getScheme().equals("quopn"))) {
            // Toast.makeText(getApplicationContext(), "" + getIntent().getScheme(), Toast.LENGTH_SHORT).show();
            uri = getIntent().getData();
            category_id_new = uri.getLastPathSegment();
            ProductCatFragment.QUOPN_CATEGORY_ID = category_id_new;
            Cursor cursor = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_CATEGORY,new String[]{ITableData.TABLE_CATEGORY.COLUMN_CATEGORY},ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID + " = ? ",	new String[] { category_id_new },null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                String categoryName = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY));
                ProductCatFragment.QUOPN_CATEGORY_TYPE = categoryName;

            } else {
                //Toast.makeText(ListingByCategoryActivity.this, "Category does not exist", Toast.LENGTH_SHORT).show();

            }

        } else if (!getIntent().getStringExtra("category").equals("")) {
            ProductCatFragment.QUOPN_CATEGORY_ID
                    = getIntent().getStringExtra("category");
            ProductCatFragment.QUOPN_CATEGORY_TYPE
                    = getIntent().getStringExtra("categoryname");
        }


		TextView txtCatName = (TextView) findViewById(R.id.txtCatName);
		txtCatName.setText(ProductCatFragment.QUOPN_CATEGORY_TYPE);
		
		fragment
			= ProductCategoryListFragment.newInstance(null
				, this, null, "Category"
				, this, null);
		
		RelativeLayout rlPlaceHolder
			= (RelativeLayout) findViewById(R.id.rlPlaceHolder);
		
		getSupportFragmentManager().beginTransaction()
			.add(R.id.rlPlaceHolder, fragment).commit();
	}

	@Override
	public void onQuopnOpened() {}

	@Override
	public void onQuopnIssued(String campaignID,boolean isFromGift,String webissueresponse) {
		fragment.updateViews("Category");
	}

	@Override
	public void onQuopnDetailAddToCartSuccess() {
		fragment.updateViews("Category");
	}

	public void sendWebIssueAddToCart(final String cta_text, final String cta_value,
									  final String source, final String campaign_id,
									  final Context context, final boolean isFromDetails,
									  final int availablequopn, final int alreadyissued,final boolean isFromGift,final ImageView mProgressBar, final ArrayList<String> arrList) {
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
						Dialog dialog=new Dialog(context, R.string.dialog_title_error, R.string.slow_internet_connection);
						dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								if (isFromDetails) {
									Activity activity = (Activity)context;
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
										PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT) + 1;
						PreferenceUtil.getInstance(getApplicationContext()).setPreference(
								PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT,
								QuopnConstants.MY_CART_COUNT);

						SingleCartDetails singleCartDetails=webIssueData.getSingleCart_details();
						Cursor cursor1=null;
						BigDecimal mTotalSaving=null;
						BigDecimal mIndividual_cartSaving=null;
						cursor1 = context.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null,ITableData.TABLE_MYCART.COLUMN_ID + " desc");
						if(cursor1==null || cursor1.getCount()==0){
							cursor1.moveToFirst();
							mIndividual_cartSaving=new BigDecimal(singleCartDetails.getSaving_value());
							mTotalSaving = mIndividual_cartSaving;
							String.format("%.2f",mTotalSaving);
							populateMyCartDB(context,""+mTotalSaving,singleCartDetails.getCartid(),singleCartDetails.getCampaignid(),singleCartDetails.getCampaignname(),singleCartDetails.getQuopncode(),
									singleCartDetails.getCompanyname(),singleCartDetails.getLong_description(),singleCartDetails.getBrandname(),singleCartDetails.getThumb_icon1(),singleCartDetails.getEnddate(),singleCartDetails.getQuopnid(),
									singleCartDetails.getSaving_value(),singleCartDetails.getCart_desc(),singleCartDetails.getCart_image(),singleCartDetails.getTitle());
							ContentValues cv=new ContentValues();
							cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING, String.valueOf(mTotalSaving));
							context.getContentResolver().update(ConProvider.CONTENT_URI_MYCART, cv, null, null);
							cursor1.close();

						}else{
							while(cursor1.moveToNext()){
								String approxsaving=cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING));
								mTotalSaving = new BigDecimal(approxsaving);
								mIndividual_cartSaving=new BigDecimal(singleCartDetails.getSaving_value());
								mTotalSaving = mTotalSaving.add(mIndividual_cartSaving);
								mTotalSaving.setScale(2, BigDecimal.ROUND_CEILING);
							}
							String.format("%.2f",mTotalSaving);
							populateMyCartDB(context,""+mTotalSaving,singleCartDetails.getCartid(),singleCartDetails.getCampaignid(),singleCartDetails.getCampaignname(),singleCartDetails.getQuopncode(),
									singleCartDetails.getCompanyname(),singleCartDetails.getLong_description(),singleCartDetails.getBrandname(),singleCartDetails.getThumb_icon1(),singleCartDetails.getEnddate(),singleCartDetails.getQuopnid(),
									singleCartDetails.getSaving_value(),singleCartDetails.getCart_desc(),singleCartDetails.getCart_image(),singleCartDetails.getTitle());
							ContentValues cv=new ContentValues();
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
										new String[] { campaign_id });
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
										new String[] { "" + campaign_id });
							}

							// Quopns adapter refresh
							for (QuopnOperationsListener listener:addQuopnoperationArrayList) {
								listener.onQuopnIssued(campaign_id, isFromGift, webIssueData.getMessage());
								System.out.println("========SendWebissue=====Listner======");
							}
						} else {
							// remove current item
							context.getContentResolver().delete(
									ConProvider.CONTENT_URI_GIFTS,
									ITableData.TABLE_GIFTS.COLUMN_GIFT_ID
											+ " = ? ",
									new String[] { campaign_id });
							// Quopns adapter refresh
							for (QuopnOperationsListener listener:addQuopnoperationArrayList) {
								listener.onQuopnIssued(campaign_id, isFromGift, webIssueData.getMessage());
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
				}else {
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

	private void populateMyCartDB(Context context,String approxsaving,String cartid,String campaignid,String campaignname,String quopncode,
								  String companyname,String long_description,String brandname,String thumb_icon1,String enddate,String quopnid,
								  String saving_value,String cart_desc,String cart_image,String title){
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
		cv.put(ITableData.TABLE_MYCART.COLUMN_TITLE , title);
		context.getContentResolver().insert(ConProvider.CONTENT_URI_MYCART,cv);
	}

	public void showQuopnDetails(Intent argIntent, View argTransitionView, QuopnDetailsActivity.QuopnDetailAddToCartListener mQuopnDetailAddToCartListener){
		QuopnDetailsActivity.launch(this,argIntent, argTransitionView, mQuopnDetailAddToCartListener, true);
	}
}
