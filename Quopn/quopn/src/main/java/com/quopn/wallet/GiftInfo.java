package com.quopn.wallet;

/**
 * @author ravi
 * @Date 27/09/2014
 * */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnDownloadManager;
import com.quopn.wallet.views.CustomProgressDialog;

public class GiftInfo extends ActionBarActivity implements OnClickListener,
	QuopnDownloadManager.DownloadingListner {
	private static final String TAG="GiftInfo";
	private String mPersonalVideoUrl,mPersonalSenderName = "Someone",mPersonalSenderPic;
	private CustomProgressDialog mProgressDialog;
	private ImageView sender_image;
	private TextView giftHeaderMessage;
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoader mImageLoader;
	QuopnDownloadManager mQuopnDownloadManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.giftinfo);

		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(130)).build();

		mPersonalVideoUrl = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL);
		mPersonalSenderPic = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_PIC);
		mPersonalSenderName = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_NAME);
		mQuopnDownloadManager = new QuopnDownloadManager(getApplicationContext(),mPersonalVideoUrl, this);

		sender_image = (ImageView) findViewById(R.id.sender_image);
		giftHeaderMessage = (TextView) findViewById(R.id.giftHeaderMessage);

		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.setTitle(R.string.progress_registering_txt);

		((TextView) findViewById(R.id.skipGiftVideo)).setOnClickListener(this);
		((TextView) findViewById(R.id.nextGiftVideo)).setOnClickListener(this);
		((TextView) findViewById(R.id.downloadPersonalMessage)).setOnClickListener(this);
		((TextView) findViewById(R.id.playPersonalMessage)).setOnClickListener(this);

		giftHeaderMessage.setText(mPersonalSenderName);
		giftHeaderMessage.append(" "
				+ this.getResources().getString(
						R.string.gift_info_header_message));
		mImageLoader.displayImage(mPersonalSenderPic, sender_image,
				mDisplayImageOptions, null);

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.skipGiftVideo:
			Intent gotoGiftInfo = new Intent(this, MainActivity.class);
			startActivity(gotoGiftInfo);
			this.finish();
			break;
		case R.id.nextGiftVideo:
			Intent nextGiftInfo = new Intent(this, MainActivity.class);
			startActivity(nextGiftInfo);
			this.finish();
			break;
		case R.id.downloadPersonalMessage:
			mQuopnDownloadManager.startDownload();
			break;
		case R.id.playPersonalMessage:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mQuopnDownloadManager.destroy();
	}

	@Override
	public void onDownloadCancel() {
		Toast.makeText(this, "Unfortunately download has cancelled!!",Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDownloadFailed() {
		Dialog dialog=new Dialog(this, R.string.dialog_title,R.string.error_unable_to_download);
		dialog.show();
		}

	@Override
	public void onDownloadComplete(String filePath) {
		mPersonalVideoUrl = filePath;
		((TextView) findViewById(R.id.playPersonalMessage)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.downloadPersonalMessage)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.skipGiftVideo)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.nextGiftVideo)).setVisibility(View.VISIBLE);
		
//		Log.i(TAG,"filePath in GiftInfo"+filePath);
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_PATH,filePath);
	}
	
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);
		
		if (resultcode == RESULT_OK) {
			switch (requestcode) {
			case QuopnConstants.GIFTVIDEO_COMPLETED_REQUESTCODE:
				Intent nextGiftInfo = new Intent(this, MainActivity.class);
				startActivity(nextGiftInfo);
				this.finish();
				break;

			default:
				break;
			}

		}
	}
	

}
