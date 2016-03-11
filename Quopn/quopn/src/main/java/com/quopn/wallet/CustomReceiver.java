package com.quopn.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.AddToCartData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;

import java.util.HashMap;
import java.util.Map;

/*
 *  A simple Broadcast Receiver to receive an INSTALL_REFERRER
 *  intent and pass it to other receivers, including
 *  the Google Analytics receiver.
 */
public class CustomReceiver extends BroadcastReceiver implements
ConnectionListener{
	private static final String TAG="Thread";
	private ConnectionFactory mConnectionFactory;
	private Map<String, String> params;
	public Context mContext;
	private boolean isDefaultAlreadyCalled=false;
	private final String UTM_SOURCE = "utm_source";
	private final String UTM_CONTENT = "utm_content";
	private final String UTM_CAMPAIGN = "utm_campaign";
	
  @Override
  public void onReceive(Context context, Intent intent) {
	  Log.v(TAG,"Referral Broadcast Received");
	  mContext=context;
	
	  String allKeyValue[]=null;
	  String tempReferrerUrl=null;
	  
	
			try{
			 
				QuopnConstants.android_id= Secure.getString(mContext.getContentResolver(),Secure.ANDROID_ID); 
				if( QuopnConstants.android_id==null
					|| QuopnConstants.android_id.length()<=0
					|| QuopnConstants.android_id.equals("")){

					{
						Log.e(TAG,"no android_id");
//						Toast.makeText(context, TAG+" no android_id", Toast.LENGTH_SHORT).show();
//						return;
					}

//					File file = new File(
//						Environment.getExternalStoragePublicDirectory(
//							Environment.DIRECTORY_DCIM).getPath()
//							+"/"+QuopnConstants.QUOPN_DEVICEID_FOLDER+"/"
//							+QuopnConstants.QUOPN_DEVICEID_FILE);
//					if(!file.exists()){
//						QuopnUtils.writeDeviceId(true);
//					}else{
//						QuopnUtils.readDeviceId();
//					}
				} else {
//					Toast.makeText(mContext, TAG+" android_id is "+QuopnConstants.android_id, Toast.LENGTH_SHORT).show();
				}
			 
			params = new HashMap<String, String>();
			params.put("device_id",QuopnConstants.android_id);
			 Log.v(TAG,"device_id Sent to server :"+QuopnConstants.android_id);
			
			if(intent.hasExtra("referrer")){
			
				tempReferrerUrl=intent.getStringExtra("referrer");
				 Log.v(TAG,"Referrer value we got from intent : "+tempReferrerUrl);
			
			if(tempReferrerUrl!=null){
				allKeyValue =tempReferrerUrl.split("&");
			
				if (allKeyValue != null) {
					for (String tempSingleKeyValue : allKeyValue) {
						if (tempSingleKeyValue.contains("=")) {
							String tempSplitKeyValue[] = tempSingleKeyValue.split("=");

							if (tempSplitKeyValue != null) {
								params.put(tempSplitKeyValue[0],
										tempSplitKeyValue[1]);
								
								
								/*Check for utm_source and utm_content in the tempSplitKeyValue
								 * put those values in the shared preferences to pass these values
								 * in the OTP verification call for referrer purpose
								 * */
								if(tempSplitKeyValue[0].equals(UTM_SOURCE)){
									PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_SOURCE, tempSplitKeyValue[1]);
								}else if(tempSplitKeyValue[0].equals(UTM_CONTENT)){
									PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CONTENT, tempSplitKeyValue[1]);
								}else if(tempSplitKeyValue[0].equals(UTM_CAMPAIGN)){
									PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CAMPAIGN, tempSplitKeyValue[1]);
								}
								
								
								Log.v("Thread Key", "" + tempSplitKeyValue[0]);
								Log.v("Thread Value", "" + tempSplitKeyValue[1]);
							}else{
								 sendDefault(mContext,intent.getStringExtra("referrer"),"tempSplitKeyValue");
								 Log.v(TAG,"Could not split key value : tempSplitKeyValue is null");
							}
						}else{
							 sendDefault(mContext,intent.getStringExtra("referrer"),"tempSingleKeyValue");
							 Log.v(TAG,"Invalid Key Value Pair : = not found in tempSingleKeyValue");
						}
						
					}
			}else{
				 Log.v(TAG,"allKeyValue : NULL");
				 sendDefault(mContext,intent.getStringExtra("referrer"),"allKeyValue");
			}
			}else{
				 Log.v(TAG,"tempReferrerData : NULL");
				 sendDefault(mContext,"tempReferrerData","tempReferrerData");
			}
		     Log.v(TAG,"Referrer Data"+intent.getStringExtra("referrer"));
			}
			if(!isDefaultAlreadyCalled){
				mConnectionFactory = new ConnectionFactory(context, this);
				mConnectionFactory.setPostParams(params);
				mConnectionFactory.createConnection(QuopnConstants.IS_FIRSTINSTALL_TRACKER);
				Log.v(TAG,"Request sent to server via onReceive");
			}
			}catch(Exception e){
				sendDefault(mContext,intent.getStringExtra("referrer"),"Exception");
				  Log.v("Thread","Exception comming");
				e.printStackTrace();
			}
			
			// Pass the intent to other receivers.
			Log.d(TAG, "Campaign:BroadcastReceiver - received :"+intent.getStringExtra("referrer"));
			// When you're done, pass the intent to the Google Analytics receiver.
			
			new CampaignTrackingReceiver().onReceive(context, intent);
   
  }
@Override
public void onResponse(int responseResult, Response response) {
	 Log.v(TAG,"Response get");
	if (response instanceof AddToCartData) {

		AddToCartData tAddToCartData = (AddToCartData) response;


		if (tAddToCartData.getError() == true) {
			 Log.v(TAG,"We Got Error in response of Referral Request");
		}else{
			if(mContext!=null)
	        PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_FIRSTLAUNCH,true);
			Log.v(TAG,"Response Message : "+tAddToCartData.getMessage());
		}
		}

}
public void sendDefault(Context context,String referralData,String callFrom){
	Log.v(TAG, "sendDefault called from"+callFrom);
	params = new HashMap<String, String>();
	params.put("device_id",QuopnConstants.android_id);
	params.put("utm_source",referralData);
	mConnectionFactory = new ConnectionFactory(context, this);
	mConnectionFactory.setPostParams(params);
	mConnectionFactory.createConnection(QuopnConstants.IS_FIRSTINSTALL_TRACKER);
	isDefaultAlreadyCalled=true;
	Log.v(TAG,"Request sent to server via sendDefault");
}
@Override
public void onTimeout(ConnectRequest request) {
	// TODO Auto-generated method stub
	
}

	@Override
	public void myTimeout(String requestTag) {

	}
}