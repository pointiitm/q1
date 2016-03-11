package com.quopn.wallet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;

public class UpdateScreen extends Activity {
	
	private TextView btnUpgrade;
	private TextView btnQuit;
	private TextView alerttitle;
	private TextView alertmessage;
	private boolean isButtonClicked = false;
	boolean updateflag;
	String updationlink="";
	String updationmessage="";
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			isButtonClicked = true;
			QuopnConstants.isUpdateTrue_ForAnnouncement = false;
			QuopnConstants.isUpdateTrue_ForWallet = false;
			proceed(v == btnUpgrade, v == btnQuit);
		}
	};
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		updateflag=getIntent().getExtras().getBoolean(QuopnConstants.UPDATE_FLAG);
		updationlink=getIntent().getExtras().getString(QuopnConstants.UPDATION_LINK);
		updationmessage=getIntent().getExtras().getString(QuopnConstants.UPDATION_MESSAGE);
		alerttitle=(TextView)findViewById(R.id.alerttitle);
		alertmessage=(TextView)findViewById(R.id.alertmessage);
		btnUpgrade = (TextView) findViewById(R.id.btnUpgrade);
		btnUpgrade.setOnClickListener(clickListener);
		btnUpgrade.setText(R.string.caption_button_upgrade);
		btnQuit = (TextView) findViewById(R.id.btnQuit);
		alertmessage.setText(updationmessage);
		if(updateflag){
			btnQuit.setText(R.string.caption_button_quit);
			btnQuit.setVisibility(View.GONE);
		}else{
			btnQuit.setText(R.string.caption_button_continue);
		}
		btnQuit.setOnClickListener(clickListener);
	}
	
	public void proceed(boolean shouldUpgrade,boolean isquitorclose) {
		if (shouldUpgrade) {
			PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_UPDATED, true);
			((QuopnApplication) getApplicationContext()).getAnalysisManager().send(AnalysisEvents.UPGRADE_CLICKED);
			Intent viewIntent = new Intent();
			viewIntent.setAction(Intent.ACTION_VIEW);
			viewIntent.setData(Uri.parse(updationlink));
			viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(viewIntent);
		}
		
		if(isquitorclose){
			((QuopnApplication) getApplicationContext()).getAnalysisManager().send(AnalysisEvents.UPGRADE_NOT_CLICKED);
			if(updateflag){
				setResult(RESULT_OK);
				finish();
				moveTaskToBack(true);
			}else{
				this.finish();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (!isButtonClicked) { proceed(false,false); }
	}
	
	@Override
	public void onBackPressed() {
		
	}
}
