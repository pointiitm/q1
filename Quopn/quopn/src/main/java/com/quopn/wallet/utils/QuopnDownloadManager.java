package com.quopn.wallet.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class QuopnDownloadManager {
	private final static String TAG="QuopnDownloadManager";
	private Context mContext;
	private String mDownloadFileUrl;
	private DownloadManager mDownloadManager = null;
	private long lastDownload = -1L;
	
	public interface DownloadingListner{
		public void onDownloadComplete(String filePath);
		public void onDownloadCancel();
		public void onDownloadFailed();
	}

	DownloadingListner mDownloadingListner;
	public QuopnDownloadManager(Context context,String downloadUrl,DownloadingListner areDownloadingListner) {
		this.mContext = context;
		mDownloadFileUrl=downloadUrl;
		mDownloadingListner=areDownloadingListner;
		mDownloadManager = (DownloadManager) this.mContext.getSystemService(this.mContext.DOWNLOAD_SERVICE);

		context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		context.registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

	}
	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void startDownload() {
		Uri uri = Uri.parse(mDownloadFileUrl);
		
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
String fileExtension=getFileExtension(mDownloadFileUrl.substring(mDownloadFileUrl.lastIndexOf("/")));
		lastDownload = mDownloadManager
				.enqueue(new DownloadManager.Request(uri)
						.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE)
						.setAllowedOverRoaming(false)
						.setTitle("Personal Message")
						.setDescription("Downloading")
						
						.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"PersonalMessage"+fileExtension));
	  }
	

	BroadcastReceiver onComplete = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			
			if(DownloadManager.STATUS_FAILED==queryStatus()){
				mDownloadingListner.onDownloadFailed();
				
			}else if(DownloadManager.STATUS_SUCCESSFUL==queryStatus()){
				
			mDownloadingListner.onDownloadComplete(getFilePath(intent));
			}
		}
	};

	BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			
			Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
			
			if (i.resolveActivity(mContext.getPackageManager()) == null) {
				Toast.makeText(mContext,
						"There are no applications to handle your request",
						Toast.LENGTH_LONG).show();
			} else {
				mContext.startActivity(i);
			}
			
		}
	};
	
	public String getFilePath(Intent intent){
		 String title=null;
		Bundle extras = intent.getExtras();
		DownloadManager.Query q = new DownloadManager.Query();
		q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
		Cursor c = mDownloadManager.query(q);
		 
		if (c.moveToFirst()) {
		    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
		    if (status == DownloadManager.STATUS_SUCCESSFUL) {
		        // process download 
//		        title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
		    	title  = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
		        // get other required data by changing the constant passed to getColumnIndex 
		    } 
		} 
		return title;
	}

	public void viewLog(View v) {
		
		Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
		
		if (i.resolveActivity(mContext.getPackageManager()) == null) {
			Toast.makeText(mContext,
					"There are no applications to handle your request",
					Toast.LENGTH_LONG).show();
		} else {
			mContext.startActivity(i);
		}
		
		
	}

	public void cancelDownloading() {
		if(lastDownload!=-1L){
		mDownloadManager.remove(lastDownload);
		mDownloadingListner.onDownloadCancel();
		}
		
	}

	private String statusMessage(Cursor c) {
		String msg = "???";

		switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
		case DownloadManager.STATUS_FAILED:
			msg = "Download failed!";
			break;

		case DownloadManager.STATUS_PAUSED:
			msg = "Download paused!";
			break;

		case DownloadManager.STATUS_PENDING:
			msg = "Download pending!";
			break;

		case DownloadManager.STATUS_RUNNING:
			msg = "Download in progress!";
			break;

		case DownloadManager.STATUS_SUCCESSFUL:
			msg = "Download complete!";
			break;

		default:
			msg = "Download is nowhere in sight";
			break;
		}

		return (msg);
	}

	public int queryStatus() {
		int status=0;
		try{
		Cursor c = mDownloadManager.query(new DownloadManager.Query()
				.setFilterById(lastDownload));

		if (c == null) {
			Toast.makeText(mContext, "Download not found!", Toast.LENGTH_LONG)
					.show();
		} else {
			c.moveToFirst();

//			Log.d(getClass().getName(),	"COLUMN_ID: "+ c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
//			Log.d(getClass().getName(),	"COLUMN_BYTES_DOWNLOADED_SO_FAR: "+ c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
//			Log.d(getClass().getName(),"COLUMN_LAST_MODIFIED_TIMESTAMP: "+ c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
//			Log.d(getClass().getName(),"COLUMN_LOCAL_URI: "	+ c.getString(c	.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
			status=c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//			Log.d(getClass().getName(),	"COLUMN_STATUS: " +status);
//			Log.d(getClass().getName(),	"COLUMN_REASON: "+ c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
			
		}
		}catch(Exception e){Log.v(TAG, "Crash on bradcast sending");}
		return status;
	}

	public void destroy() {
		mContext.unregisterReceiver(onComplete);
		mContext.unregisterReceiver(onNotificationClick);
	}
	private String getFileExtension(String downloadFileName) {
	  
	    int lastIndexOf = downloadFileName.lastIndexOf(".");
	    if (lastIndexOf == -1) {
	        return ""; // empty extension
	    }
	    return downloadFileName.substring(lastIndexOf);
	}
}
