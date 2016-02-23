package com.quopn.wallet.analysis.scheduler;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.database.MainMenuDataSource;
import com.quopn.wallet.analysis.dataclasses.MainMenu;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.AddToCartData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService implements ConnectionListener {
    public static final String TAG = "SchedulingService";
    public SchedulingService() {
        super("SchedulingService");
    }
   private String analysisDataString="";
    

    MainMenuDataSource mMainMenuDataSource;
    List<MainMenu> mMainMenuList;
    @Override
    protected void onHandleIntent(Intent intent) {
        // Release the wake lock provided by the BroadcastReceiver.
    	AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
        mMainMenuDataSource=new MainMenuDataSource(getApplicationContext());
		mMainMenuDataSource.open();
		
		mMainMenuList=mMainMenuDataSource.retrivewAllInMainMenu();
		mMainMenuDataSource.close();
		
		String analysisString=convertTableToString(mMainMenuList);
		if(QuopnUtils.isInternetAvailable(getApplicationContext())&&analysisString!=null)
		sendAnalysisToServer(analysisString);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(mMainMenuDataSource != null)
    	mMainMenuDataSource.close();
    }
    
 
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */


    /** 
     * Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from www.google.com.
     * @return String version of InputStream.
     * @throws IOException
     */

    private void sendAnalysisToServer(String analysisString){
    	if(analysisString != null){
    	Map<String, String> params = new HashMap<String, String>();
		params.put("walletid", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY) );
		params.put("jsonstats",analysisString);

		params.put("device_id",QuopnConstants.android_id);//for device id added for stats 04022015
		params.put("version", QuopnConstants.versionName);

		
		
		ConnectionFactory connectionFactory = new ConnectionFactory(this,this);
		connectionFactory.setPostParams(params);
		connectionFactory.createConnection(QuopnConstants.ANALYSIS_CODE);
    	}
    }

	@Override
	public void onResponse(int responseResult, Response response) {
		switch(responseResult){
		case ConnectionListener.RESPONSE_OK :
		if(response instanceof AddToCartData){
			AddToCartData analysisDataSendResponse = (AddToCartData) response;
			if (analysisDataSendResponse.getError() == true) {
			}else{
				   mMainMenuDataSource=new MainMenuDataSource(getApplicationContext());
				   mMainMenuDataSource.open();
      			   mMainMenuDataSource.deleteAllData();
				   mMainMenuDataSource.close();
					
			}
			break;
		}
		}
		
	}
	
	public String convertTableToString(List<MainMenu> mainMenuList){
		JSONArray tJSONArray=new JSONArray();
		JSONObject tJSONObject=null;
		 String eventValue="none";

		String wID = "";
		if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
			wID = "_"+ QuopnApi.EWalletDefault.MOBILE_WALLET_CITRUS_ID;
		} else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
			wID = "_"+ QuopnApi.EWalletDefault.MOBILE_WALLET_SHMART_ID;
		}
		
		 for(int i=0;i<mainMenuList.size();i++){
			 tJSONObject=new JSONObject();
			 try {
				 //keyId=eventID
				tJSONObject.put("keyId", mainMenuList.get(i).getEventId());
				tJSONObject.put("timeStamp",mainMenuList.get(i).getTimeStamp());
			
				switch(mainMenuList.get(i).getMainMenuKey()){
				case AnalysisEvents.PROFILE :
					eventValue="profile";
					break;
					case AnalysisEvents.MYCART:
						eventValue="mycart";
						break;
					case AnalysisEvents.MYQUOPN :
						eventValue="myquopn";
						break;
					case AnalysisEvents.SHOPEAROUND :
						eventValue="shoparound";
						break;
					case AnalysisEvents.MYHISTORY :
						eventValue="myhistory";
						break;
					case AnalysisEvents.ABOUT :
						eventValue="about";
						break;
					case AnalysisEvents.FAQ :
						eventValue="faq";
						break;

					case AnalysisEvents.MYGIFTS :
						eventValue="gift";
						break;
					case AnalysisEvents.CATEGORIES :
						eventValue="categories";
						break;
					case AnalysisEvents.ALL :
						eventValue="all";
						break;
					case AnalysisEvents.FEATURED :
						eventValue="featured";
						break;
					case AnalysisEvents.NEW :
						eventValue="new";
						break;
					case AnalysisEvents.EXPIRING :
						eventValue="expiring";
						break;
					case AnalysisEvents.SEARCH :
						eventValue="search";
						break;
					case AnalysisEvents.QUOPN :
						eventValue="quopn";
						break;
					case AnalysisEvents.QUOPN_ISSUE:
						eventValue="issue";
						break;
					case AnalysisEvents.QUOPN_DETAIL :
						eventValue="detail";
						break;
					case AnalysisEvents.QUOPN_SHARE :
						eventValue="share";
						break;
					case AnalysisEvents.ADD_TO_CART :
						eventValue="addtocart";
						break;
					case AnalysisEvents.REMOVE_FROM_CART :
						eventValue="removefromcart";
						break;

					case AnalysisEvents.VIDEO_WATCHED_TIME :
						eventValue="videoWatchedTime";
						break;
					case AnalysisEvents.GIFT :
						eventValue="gift";
						break;
					case AnalysisEvents.GIFT_DETAIL :
						eventValue="giftDetail";
						break;
					case AnalysisEvents.GIFT_ISSUE :
						eventValue="giftIssue";
						break;
					case AnalysisEvents.GIFT_SHARE :
						eventValue="giftShare";
						break;
					case AnalysisEvents.SEARCH_WORD :
						eventValue="searchWord";
						break;
					case AnalysisEvents.IMPRESSION :
						eventValue="impression";
						break;
					case AnalysisEvents.TOUR :
						eventValue="tour";
						break;
					case AnalysisEvents.PROFILE_COMPLETED :
						eventValue="profile_completed";
						break;
					case AnalysisEvents.MOBILE_SUBMITTED :
						eventValue="mobile_submitted";
						break;
					case AnalysisEvents.PROFILE_CHANGED :
						eventValue="profile_changed";
						break;
					case AnalysisEvents.CRASH :
						eventValue="crash";
						break;
					case AnalysisEvents.OTP_FAILED :
						eventValue="otp_failed";
						break;
					case AnalysisEvents.OTP_VERIFIED :
						eventValue="otp_verified";
						break;

					case AnalysisEvents.VIDEO_PLAYER_STARTED :
						eventValue="video_player_started";
						break;

					case AnalysisEvents.VIDEO_PLAYING :
						eventValue="video_playing";
						break;

					case AnalysisEvents.MYQUOPN_CAMPAIGN_DETAILS :
						eventValue="myquopn_campaign_details";
						break;

					case AnalysisEvents.MYCART_CAMPAIGN_DETAILS :
						eventValue="mycart_campaign_details";
						break;

					case AnalysisEvents.PROMO :
						eventValue="promo";
						break;
					case AnalysisEvents.NOTIFICATION_ID :
						eventValue="notification_read";
						break;

					case AnalysisEvents.ANNOUNCEMENT_CLOSED :
						eventValue="announcement_closed";
						break;

					case AnalysisEvents.SHMART_MENU_CLICKED :
						eventValue="shmart_menu_clicked";
						break;

					case AnalysisEvents.ANNOUNCEMENT_CLICKED :
						eventValue="announcement_clicked";
						break;

					case AnalysisEvents.ANNOUNCEMENT_APPEARED :
						eventValue="announcement_appeared";
						break;

					case AnalysisEvents.SCREEN_WALLET_REGISTRATION :
						eventValue="screen_wallet_registration"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_OTP :
						eventValue="screen_wallet_otp"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_HOME :
						eventValue="screen_wallet_home"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_ADD_MONEY :
						eventValue="screen_wallet_add_money"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_SEND_MONEY :
						eventValue="screen_wallet_send_money"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_SETTING :
						eventValue="screen_wallet_setting"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_FAQS :
						eventValue="screen_wallet_faqs"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_TNC :
						eventValue="screen_wallet_tncs"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_MY_TRANSACTIONS :
						eventValue="screen_wallet_my_transactions"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_TRANSFER_TO_BANK :
						eventValue="screen_wallet_transfer_to_bank"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_ADDNEW_BANKACCOUNT :
						eventValue="screen_wallet_addnew_bankaccount"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_ADDING_MONEY_SHMART :
						eventValue="screen_wallet_adding_money_shmart"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_UNABLE_TO_SENDMONEY :
						eventValue="screen_wallet_unable_to_sendmoney"+wID;
						break;
					case AnalysisEvents.SCREEN_WALLET_DIDNOT_REGISTER :
						eventValue="screen_wallet_didnot_register"+wID;
						break;
					case AnalysisEvents.ADDED_BENIFICIARY :
						eventValue="added_benificiary"+wID;
						break;
					case AnalysisEvents.DELETED_BENIFICIARY :
						eventValue="deleted_benificiary"+wID;
						break;
					case AnalysisEvents.SENT_MONEY :
						eventValue="sent_money"+wID;
						break;
					case AnalysisEvents.SHOPAT_STORES :
						eventValue="shop_at_store"+wID;
						break;
					case AnalysisEvents.UPGRADE_CLICKED :
						eventValue="upgrade";
						break;
					case AnalysisEvents.UPGRADE_NOT_CLICKED :
						eventValue="upgrade_not_clicked";
						break;
					case AnalysisEvents.SCREEN_CITRUS_REGTNC :
						eventValue="screen_citrus_regtnc";
						break;

                    default :
						eventValue="defaultEvent";
						break;
				}
				tJSONObject.put("eventName",eventValue);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 tJSONArray.put(tJSONObject);
		  }
		 String finalAnalysisDataForServer=null;
		 if(tJSONArray.length()>0) {
			 finalAnalysisDataForServer=encoding(tJSONArray.toString());

		 }
		return finalAnalysisDataForServer;
	}
	
	public String convertJsonToString(JSONArray argJSONObject){
		Gson gson =new Gson();
		String tempTest=gson.toJson(argJSONObject);
		return tempTest;
	}
	
	@SuppressWarnings("deprecation")
	public String encoding(String stringToEncoding){
		String tempTest=URLEncoder.encode(stringToEncoding);
		return tempTest;
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}

}
