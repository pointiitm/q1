package com.quopn.wallet;

/**
 * 
 *@author Sumeet
 */

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.NotificationData;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class GCMNotificationIntentService extends IntentService {

	NotificationCompat.Builder builder;
	final static String GROUP_KEY_EMAILS = "group_key_emails";
	private Cursor cursor = null;
	Calendar cal = Calendar.getInstance();
	int year = cal.get(Calendar.YEAR);
	int month = cal.get(Calendar.MONTH)+1;
	int day = cal.get(Calendar.DATE);
	int hour = cal.get(Calendar.HOUR_OF_DAY);
	int minute = cal.get(Calendar.MINUTE);
	int sec = cal.get(Calendar.SECOND);
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntent";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(),"",intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "+ extras.toString(),"",intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				Gson gson = new GsonBuilder().serializeNulls().create();
				NotificationData messageData = (NotificationData) gson.fromJson((String) extras.get(QuopnConstants.MESSAGE_KEY),NotificationData.class);
				NotificationData redeemData = (NotificationData) gson.fromJson((String) extras.get(QuopnConstants.REDEEM_NOTIFICATION_KEY), NotificationData.class);

				if(messageData != null) {//Indicates Silent and Picture Notification
					Logger.d(messageData.toString());
					String typeid = "";
					if (!TextUtils.isEmpty(messageData.getTypeid())) {
						typeid = messageData.getTypeid();
					} else if (!TextUtils.isEmpty(messageData.getType())) {
						typeid = messageData.getType();
					}
					if (messageData != null && !TextUtils.isEmpty(typeid)) {
						if (typeid.equals(QuopnConstants.NOTIFICATION_ID.SILENT)) {
							Intent redeem_Intent = new Intent(QuopnConstants.BROADCAST_REDEEM_QUOPNS);
							LocalBroadcastManager.getInstance(this).sendBroadcast(redeem_Intent);

							if (messageData.getImage_url() != null && !messageData.getImage_url().matches("") && messageData.getCampaign_id() != null && !messageData.getCampaign_id().matches("")) {
							} else if (messageData.getTitle() != null && !messageData.getTitle().matches("") && messageData.getDesc() != null && !messageData.getDesc().matches("")) {
								sendNotificationRedeemed(messageData.getTitle(), messageData.getDesc(), intent);
							} else if (messageData.getTitle() == null || messageData.getTitle().matches("")) {
								sendNotificationRedeemed("Q-Alert", getResources().getString(R.string.quopns_redeemed), intent);
							} else {
								sendNotificationRedeemed(messageData.getTitle(), getResources().getString(R.string.quopns_redeemed), intent);
							}
						} else if (typeid.equals(QuopnConstants.NOTIFICATION_ID.TEXT)) {
							insertNotifIntoDB(messageData);
							sendNotification(messageData.getTitle(), messageData.getDesc(), intent);
						} else if (typeid.equals(QuopnConstants.NOTIFICATION_ID.IMAGE)) {
							insertNotifIntoDB(messageData);
							if (messageData.getImage_url() != null && !messageData.getImage_url().matches("")) {
								sendNotificationWithPic(messageData.getTitle(), messageData.getImage_url(), messageData.getDesc(), intent);
							}
						} else if (typeid.equals(QuopnConstants.NOTIFICATION_ID.DYNAMIC)) {
							insertNotifIntoDB(messageData);
							if (messageData.getImage_url() != null && !messageData.getImage_url().matches("")) {
								sendNotificationWithPic(messageData.getTitle(), messageData.getImage_url(), messageData.getDesc(), intent);
							}
						}
					}
				} else if (redeemData != null) {//Redeemption Notification
					Intent redeem_Intent = new Intent(QuopnConstants.BROADCAST_REDEEM_QUOPNS);
					LocalBroadcastManager.getInstance(this).sendBroadcast(redeem_Intent);

					try {
						if (messageData.getImage_url() != null && !messageData.getImage_url().matches("") && messageData.getCampaign_id() != null && !messageData.getCampaign_id().matches("")) {
						} else if (messageData.getTitle() != null && !messageData.getTitle().matches("") && messageData.getDesc() != null && !messageData.getDesc().matches("")) {
							sendNotificationRedeemed(redeemData.getTitle(), redeemData.getDesc(), redeem_Intent);
						} else if (messageData.getTitle() == null || messageData.getTitle().matches("")) {
							sendNotificationRedeemed("Q-Alert", getResources().getString(R.string.quopns_redeemed), redeem_Intent);
						} else {
							sendNotificationRedeemed(redeemData.getTitle(), getResources().getString(R.string.quopns_redeemed), redeem_Intent);
						}

					} catch (Exception e) {
					}
				}

				/*if (extras.get(QuopnConstants.MESSAGE_KEY) != null) {

					String message = extras.getString(QuopnConstants.MESSAGE_KEY);
					if (message.startsWith("http://")
							&& (message.endsWith(".jpg") || message.endsWith(".jpeg")
									|| message.endsWith(".png"))) {
						
						String alt = extras.getString(QuopnConstants.ALT_KEY);
						sendNotificationWithPic("Q-Alert", message, alt);
					} else {
						
						try {
							Gson gson = new Gson();
							NotificationData data = (NotificationData) gson.fromJson((String) extras.get(QuopnConstants.MESSAGE_KEY),NotificationData.class);

                            // vishal
                            if (data.getTypeid() == "1") {
                                return;
                            }
							ContentValues cv = new ContentValues();
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION,	data.getTitle());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL,	data.getImage_url());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC,	data.getDesc());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID,	data.getCampaign_id());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME,	ServerDateFormat());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG,	QuopnConstants.BOOL_TRUE);
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN, data.getScreen());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN_VALUE, data.getValue());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAPTION, data.getCaption());
                            cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID, data.getTypeid());
							cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID, data.getNotification_id());


							getContentResolver().insert(
									ConProvider.CONTENT_URI_NOTIFICATION, cv);
							
							if (data.getImage_url() != null && !data.getImage_url().matches("")) {
								sendNotificationWithPic(data.getTitle(), data.getImage_url(), data.getDesc());
							}else{
								sendNotification(data.getTitle(),data.getDesc());
							}
							
						} catch (JsonSyntaxException eJsonSyntaxException) {
							Log.e(TAG, "Parse Error :" + eJsonSyntaxException.getMessage());
						} catch (Exception eException) {
							Log.e(TAG,"Parse Error :"+eException.getMessage());
						}
						
						
					}

				} else if (extras.get(QuopnConstants.REDEEM_NOTIFICATION_KEY) != null) {

					Intent redeem_Intent = new Intent(QuopnConstants.BROADCAST_REDEEM_QUOPNS);
					LocalBroadcastManager.getInstance(this).sendBroadcast(redeem_Intent);
					
					try {
						Gson gson = new Gson();
						NotificationData data = (NotificationData) gson.fromJson((String) extras.get(QuopnConstants.REDEEM_NOTIFICATION_KEY),NotificationData.class);
						if (data.getImage_url() != null && !data.getImage_url().matches("") && data.getCampaign_id() != null && !data.getCampaign_id().matches("")) {
						}else if(data.getTitle()!=null && !data.getTitle().matches("") && data.getDesc()!=null && !data.getDesc().matches("")){
							sendNotificationRedeemed(data.getTitle(),data.getDesc());
						}else if(data.getTitle()==null || data.getTitle().matches("")){
							sendNotificationRedeemed("Q-Alert",getResources().getString(R.string.quopns_redeemed));
						}else{
							sendNotificationRedeemed(data.getTitle(),getResources().getString(R.string.quopns_redeemed));
						}
						
					} catch (Exception e) {
					}
					
					
				}*/

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	public void insertNotifIntoDB(NotificationData argData){
		ContentValues cv = new ContentValues();
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION, argData.getTitle());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL, argData.getImage_url());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC, argData.getDesc());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID, argData.getCampaign_id());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME, ServerDateFormat());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG, QuopnConstants.BOOL_TRUE);
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN, argData.getScreen());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN_VALUE, argData.getValue());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAPTION, argData.getCaption());
		if(!TextUtils.isEmpty(argData.getTypeid())){
			cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID, argData.getTypeid());
		} else if(!TextUtils.isEmpty(argData.getType())){
			cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID, argData.getType());
		}

		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID, argData.getNotification_id());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_URL, argData.getBig_image_url());
		cv.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_VALUE, argData.getBig_image_value());

		getContentResolver().insert(ConProvider.CONTENT_URI_NOTIFICATION, cv);
	}
	
	
	private String ServerDateFormat() {
        StringBuffer dString = new StringBuffer();
        dString.append((day>9)?String.valueOf(day):("0"+day)); dString.append("-"); 
        dString.append((month>9)?String.valueOf(month):("0"+month));dString.append("-");
//        dString.append(String.valueOf(year).substring(2, String.valueOf(year).length()));dString.append(" ");
        dString.append(String.valueOf(year));dString.append(" ");
        dString.append((hour>9)?String.valueOf(hour):("0"+hour));dString.append(":"); 
        dString.append((minute>9)?String.valueOf(minute):("0"+minute));
//        dString.append(":"); 
//        dString.append((sec>9)?String.valueOf(sec):("0"+sec));
        return dString.toString();

	}

	private void sendNotification(String msg,String desc, Intent argIntent) {
		if (!isQuopnActivityOnTop()) {

			try {
				QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(getApplicationContext()).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
				if(QuopnConstants.NOTIFICATION_COUNT<=20) {
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
				}
				sendBroadCast(this);
				
			/* //This is previous code 
				NotificationCompat.InboxStyle notify_style = new InboxStyle();
				cursor = getContentResolver().query(
						ConProvider.CONTENT_URI_NOTIFICATION, null, null, null,
						ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " desc");
				

				if (cursor != null && cursor.getCount() >= 1) {
					while (cursor.moveToNext()) {
						notify_style
								.addLine(cursor.getString(cursor
										.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION)));
					}

					if (cursor.getCount() == 1)
						notify_style.setBigContentTitle(cursor.getCount()
								+ " Q-Alert");
					else
						notify_style.setBigContentTitle(cursor.getCount()
								+ " Q-Alerts");
				} else {
					notify_style.setBigContentTitle("Q-Alert");
					notify_style.addLine(msg);
				}

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(true).setStyle(notify_style)
						.setContentTitle("Q-Alert");

				if (cursor.getCount() == 1)
					mBuilder.setContentText(cursor.getCount() + " Q-Alert");
				else
					mBuilder.setContentText(cursor.getCount() + " Q-Alerts");
			
			*/
				
				NotificationCompat.InboxStyle notify_style = new InboxStyle();

				cursor = getContentResolver().query(
						ConProvider.CONTENT_URI_NOTIFICATION, null, null, null,
						ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " desc");

				String strTitle = "";
				String strDesc = "";
				int iUnreadCount = 0; 
				if (cursor != null && cursor.getCount() >= 1) {
					//Set Descriptions
					while (cursor.moveToNext()) {
						if(cursor.getInt(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG))==QuopnConstants.BOOL_TRUE)
						{
							notify_style.addLine(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION)));
							iUnreadCount++;
						}
					}
					
					if(iUnreadCount>1){
						strTitle = iUnreadCount + " New Q-Alerts";
						strDesc = "Drag to know more";
					}else{
						//Single Notification Title, Message
						strTitle = msg;
						strDesc = desc;
						notify_style = new InboxStyle();
						notify_style.addLine(strDesc);
					}		
				}			
// 				else {
// 					if (cursor.getCount() == 1)
// 					//Unknown
//					notify_style.setBigContentTitle("Q-Alert");
//					notify_style.addLine(msg);
//				}

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(true)
						.setContentTitle(strTitle)
						.setContentText(strDesc)
						.setStyle(notify_style);

//				if (cursor.getCount() != 1)
//					mBuilder.setContentTitle(cursor.getCount() + " Q-Alerts");
				
				

				int mId = 1;
				String walledId = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
				if (!TextUtils.isEmpty(walledId)) {
					getNotificationManager(mBuilder, MainSplashScreen.class, argIntent).notify(mId, mBuilder.build());
				}else{
					getNotificationManager(mBuilder, RegistrationScreen.class, argIntent).notify(mId, mBuilder.build());
				}

			} catch (Exception e) {
				// un-handle exception
			}

		} else {
			// not to show push
			QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(getApplicationContext()).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
			if(QuopnConstants.NOTIFICATION_COUNT<=20)
				PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
			sendBroadCast(this);
		}
	}

	private void sendNotificationWithPic(String title, String url, String alt, Intent argIntent) {
		if (!isQuopnActivityOnTop()) {
			try {
				QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(getApplicationContext()).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
				if(QuopnConstants.NOTIFICATION_COUNT<=20)
					PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
				sendBroadCast(this);
				
				NotificationCompat.BigPictureStyle notify_style = new BigPictureStyle();
				Bitmap bitmap = getBitmapFromURL(url);
				if (bitmap != null) {
					notify_style.bigPicture(bitmap);
				}
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(true).setStyle(notify_style)
						.setContentText(alt)
						.setContentTitle(title);

				notify_style.setSummaryText(alt);
				int mId = 2;
				String walledId = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
				if (!TextUtils.isEmpty(walledId)) {
					getNotificationManager(mBuilder, MainSplashScreen.class, argIntent).notify(mId, mBuilder.build());
				}else{
					getNotificationManager(mBuilder, RegistrationScreen.class, argIntent).notify(mId, mBuilder.build());
				}
				

			} catch (Exception e) {
				// un-handle exception
			}
		} else {
			// not to show push
			QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(getApplicationContext()).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
			if(QuopnConstants.NOTIFICATION_COUNT<=20)
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
			sendBroadCast(this);
		}
	}
	
	private void sendNotificationRedeemed(String title,String msg, Intent argIntent) {
		if (!isQuopnActivityOnTop()) {

			try {
				
//				QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(this).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
//				if(QuopnConstants.NOTIFICATION_COUNT<=20)
//					PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
//				sendBroadCast(this);

				NotificationCompat.InboxStyle notify_style = new InboxStyle();

//				notify_style.setBigContentTitle("Q-Alert"*);
				notify_style.addLine(msg);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(true).setStyle(notify_style)
						.setContentTitle(title).setContentText(msg);
				
				int mId = 3;
				getNotificationManager(mBuilder, MainSplashScreen.class, argIntent).notify(mId, mBuilder.build());
				
//				String walledId = PreferenceUtil.getInstance(this).getPreference(QuopnConstants.WALLET_ID_KEY);
//				if (!TextUtils.isEmpty(walledId)) {
//					getNotificationManager(mBuilder, NotificationActivity.class).notify(mId, mBuilder.build());
//				}else{
//					getNotificationManager(mBuilder, RegistrationScreen.class).notify(mId, mBuilder.build());
//				}

			} catch (Exception e) {
			}

		} else {
			// not to show push
//			QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(this).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT)+1;
//			if(QuopnConstants.NOTIFICATION_COUNT<=20)
//				PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, QuopnConstants.NOTIFICATION_COUNT);
//			sendBroadCast(this);
		}
	}
	
	private static void sendBroadCast(Context context){
		Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_NOTIFICATIONCOUNTER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	private NotificationManager getNotificationManager(
			NotificationCompat.Builder mBuilder, Class<?> c, Intent argIntent) {

		Intent resultIntent = new Intent(this, c);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//resultIntent.putExtra(QuopnConstants.DELETE_NOTIFICATIONS, "Yes");
		PendingIntent notifyIntent = null;
		if(c.getName().equals("MainActivity")){
			
			notifyIntent= PendingIntent.getBroadcast(this, 0, resultIntent, 0);
			
		}else if(c.getName().equals("RegistrationScreen")){
			
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(RegistrationScreen.class);
			stackBuilder.addNextIntent(resultIntent);
			notifyIntent= stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_ONE_SHOT);
			
		}
		else{
			Bundle extras = argIntent.getExtras();
			if(extras != null) {
				resultIntent.putExtras(extras);//Extras are used for Deep Linking through Notification
			}

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			notifyIntent= stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_ONE_SHOT);
		}
		
		mBuilder.setContentIntent(notifyIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		return mNotificationManager;
	}

	private boolean isQuopnActivityOnTop() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		if (componentInfo.getPackageName().equalsIgnoreCase("com.quopn.wallet")) {
			return true;
		} else {
			return false;
		}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}

}
