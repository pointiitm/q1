package com.quopn.wallet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CampaignValidationData;
import com.quopn.wallet.data.model.SingleCartDetails;
import com.quopn.wallet.data.model.Video;
import com.quopn.wallet.data.model.VideoIssueData;
import com.quopn.wallet.data.model.WebIssueData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

public class QuopnOperations {
	
	public interface QuopnOperationsListener {
		public void onQuopnIssued(String campaignID,boolean isfromgift,String webissueresponse);
	}


	BroadcastReceiver videoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			sendVideoIssue();
			context.unregisterReceiver(this);
		}
	};

	protected static final String TAG = "Quopn/QuopnOperations";
	
	private String campaignID;
	private Context context;
	private String source;
	private int available;
	private int issued;


    ArrayList<QuopnOperationsListener> addQuopnoperationArrayList = new ArrayList<QuopnOperationsListener>();
	
	public void addQuopnOperationsListener(QuopnOperationsListener listener) {
		//this.listener = listener;
        addQuopnoperationArrayList.add(listener);
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
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {

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
										SHARED_PREF_KEYS.MYCARTCOUNT) + 1;
						PreferenceUtil.getInstance(context).setPreference(
								SHARED_PREF_KEYS.MYCARTCOUNT,
								QuopnConstants.MY_CART_COUNT);

						SingleCartDetails singleCartDetails=webIssueData.getSingleCart_details();
						Cursor cursor1=null;
						BigDecimal mTotalSaving=null;
						BigDecimal mIndividual_cartSaving=null;
						cursor1 = context.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null, ITableData.TABLE_MYCART.COLUMN_ID + " desc");
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


	public void sendWebIssue(final String cta_text, final String cta_value,
			final String source, final String campaign_id,
			final Context context, final boolean isFromDetails,
			final int availablequopn, final int alreadyissued,final boolean isFromGift,final ImageView mProgressBar) {

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
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							
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
										SHARED_PREF_KEYS.MYCARTCOUNT) + 1;
						PreferenceUtil.getInstance(context).setPreference(
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
//                            if(mProgressBar!=null){
//                                mProgressBar.setVisibility(View.INVISIBLE);
//                                QuopnConstants.ISADDTOCARTINCOMPLETE =true;
//                            }

					}
				} else {
					Log.e(TAG, "response not an instanceof WebIssueData");
				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				// ankur
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void myTimeout(String requestTag) {

			}

		};

		QuopnUtils.getWebIssueResponse(campaign_id, source, context,
				webissue_connectionlistener);

	}
	
	
	public void videoIssue(final Context context, final String cta_text
		, final String cta_value, final String source, final String campaign_id
		, final int availablequopn, final int alreayissued) {
		Dialog dialog = new Dialog(context,"QUOPN",R.string.dialog_message_video_issue);
		dialog.addOkButton("YES");
		dialog.addCancelButton("NO");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnCancelButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //QuopnConstants.ISADDTOCARTINCOMPLETE =true;
            }
        });
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConnectionListener video_connectionlistener=new ConnectionListener() {
					@Override
					public void onResponse(int responseResult,Response response) {
						if(response instanceof CampaignValidationData){
                            //QuopnConstants.ISADDTOCARTINCOMPLETE =true;
							CampaignValidationData campaignvalidationdata=(CampaignValidationData)response;
							if(campaignvalidationdata.isError()==true){
								Dialog dialog=new Dialog(context, R.string.dialog_title_error,R.string.slow_internet_connection);
								dialog.setOnAcceptButtonClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
								        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
									}
								});
								dialog.show();
							
							}else{
								sendVideo(cta_text, cta_value, source,campaign_id,context,availablequopn,alreayissued);
							}
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
				QuopnUtils.getCampaignValidationResponse(campaign_id, context,video_connectionlistener);
			}
		});
		dialog.show();
	}

	public void sendVideo(String cta_text, String cta_value,String source,String campaign_id,Context context,int availablequopn,int alreadyissued) {
		if (QuopnUtils.isInternetAvailable(context)) {
			QuopnConstants.CAMPAIGNID=campaign_id;
			QuopnConstants.SOURCE=source;
			QuopnConstants.AVAILABLEQUOPN=availablequopn;
			QuopnConstants.ALREADYISSUED=alreadyissued;
			
			this.campaignID = campaign_id;
			this.context = context;
			this.source = source;
			this.available = availablequopn;
			this.issued = alreadyissued;
			
			Intent intent = null;
			try {
				intent = new Intent(context,
					Class.forName("com.quopn.wallet.VideoPlayerActivity"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			context.registerReceiver(videoReceiver
				, new IntentFilter(QuopnConstants.INTENT_ACTION_VIDEO_WATCHED));
			
			Video video = new Video(cta_value); //"http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"
			intent.putExtra(Video.class.getName(), video);
			((Activity) context).startActivityForResult(intent,
				QuopnConstants.VIDEO_COMPLETED_REQUESTCODE);

		} else {
			 Dialog dialog=new Dialog(context, R.string.dialog_title_no_internet,R.string.please_connect_to_internet); 
			 dialog.show();

		}
	
	}
	
	private void sendVideoIssue() {
		ConnectionListener connectionlistener_videoissue=new ConnectionListener() {
			@Override
			public void onResponse(int responseResult,Response response) {
				if(response instanceof VideoIssueData){
					VideoIssueData videoIssueData=(VideoIssueData)response;
					if(videoIssueData.isError()==true){
                       // QuopnConstants.ISADDTOCARTINCOMPLETE =true;
						Dialog dialog=new Dialog(context, R.string.dialog_title_error,videoIssueData.getMessage()); 
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
						        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
							}
						});
						dialog.show();
						
					}else{
						QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(context).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT)  + 1;
						PreferenceUtil.getInstance(context).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT, QuopnConstants.MY_CART_COUNT);
								
						SingleCartDetails singleCartDetails=videoIssueData.getSingleCart_details();
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
						
								if(QuopnConstants.ALREADYISSUED!=-1 && QuopnConstants.AVAILABLEQUOPN!=-1){
									if(QuopnConstants.ALREADYISSUED>0){
										QuopnConstants.ALREADYISSUED--;
									}else if(QuopnConstants.AVAILABLEQUOPN>0){
										QuopnConstants.AVAILABLEQUOPN--;
									}
									if( (QuopnConstants.ALREADYISSUED+QuopnConstants.AVAILABLEQUOPN) == 0){
										//remove current item
										context.getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ", new String[]{QuopnConstants.CAMPAIGNID});
									}else{
										//update both values of counters.
										ContentValues cv=new ContentValues();
										cv.put(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED, String.valueOf(QuopnConstants.ALREADYISSUED));
										cv.put(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS, String.valueOf(QuopnConstants.AVAILABLEQUOPN));
										context.getContentResolver().update(ConProvider.CONTENT_URI_QUOPN, cv, ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ", new String[]{""+QuopnConstants.CAMPAIGNID});
									}

									//Quopns adapter refresh
									boolean isFromGift=false;
                                    for (QuopnOperationsListener listener:addQuopnoperationArrayList) {
                                        listener.onQuopnIssued(campaignID, isFromGift, videoIssueData.getMessage());
                                    }
								}
                                // QuopnConstants.ISADDTOCARTINCOMPLETE =true;
					}

				
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
		QuopnUtils.getVideoIssueResponse(QuopnConstants.CAMPAIGNID, QuopnConstants.SOURCE, context, connectionlistener_videoissue);
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
}
