/**
 * @author Ravishankar
 *
 *@date 04/09/2014
 */
package com.quopn.errorhandling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.quopn.wallet.MainSplashScreen;
import com.quopn.wallet.R;
import com.quopn.wallet.utils.MultipartRequest;
import com.quopn.wallet.utils.QuopnApi;

import java.util.HashMap;

public class ShowExceptionActivity extends Activity {

	private static final String TAG="ShowExceptionActivity";
	private TextView mError;
	private Button mBtnDismiss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.exception);

		HashMap<String, String> map = new HashMap<String, String>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				MultipartRequest request = new MultipartRequest();
				request.setUrl(QuopnApi.ERROR_REPORT);
				for (String key:getIntent().getExtras().keySet()) {
					request.addFieldValue(key, getIntent().getExtras().getString(key));
					//System.out.println("==========ShowEXception=====VALUE=====" + getIntent().getExtras().getString(key));
					//System.out.println("==========ShowEXception=====KEY====="+key);
				}
				request.request();
			}
		}).start();


		mBtnDismiss = (Button) findViewById(R.id.btnDismiss);
		mBtnDismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		dismiss();
	}
	
	private void reset() {
		startActivity(new Intent(ShowExceptionActivity.this, MainSplashScreen.class));
	}
	
	private void dismiss() {
//		HashMap<String, String> map = new HashMap<String, String>();
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				MultipartRequest request = new MultipartRequest();
//				request.setUrl(QuopnApi.ERROR_REPORT);
//				for (String key:getIntent().getExtras().keySet()) {
//					request.addFieldValue(key, getIntent().getExtras().getString(key));
//				}
//				request.request();
//			}
//		}).start();

		reset();
		finish();
	}
}
