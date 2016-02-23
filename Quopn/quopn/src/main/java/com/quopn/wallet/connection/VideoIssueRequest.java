package com.quopn.wallet.connection;

/**
 * @author Sandeep
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.VideoIssueData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Connector;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import org.apache.http.HttpStatus;

import java.util.Map;

public class VideoIssueRequest extends ConnectRequest implements Connector,RequestTimer.RequestTimerListener {
	private static final String TAG="VideoIssueRequest";
	private VideoIssueData mResponseVideoIssue;
	private RequestTimer requestTimer;
	public VideoIssueRequest(Context mContext,
			ConnectionListener connectionListener) {
		 mProgressDialog = new CustomProgressDialog(mContext);
		this.mConnectionListener = connectionListener;
		this.mContext = mContext;
		requestTimer = new RequestTimer();
		requestTimer.setListener(this);
	}

	@Override
	public void connect() {

		 mProgressDialog.show();

		StringRequest postRequest = new StringRequest(Request.Method.POST,
				QuopnApi.VIDEO_ISSUE,
				new com.android.volley.Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						requestTimer.cancel();
//						Log.d("Response", response);
						QuopnUtils.QuopnLogWriteFile(TAG,response, true);
						if (mProgressDialog != null && mProgressDialog.isShowing()) {
							try {
								mProgressDialog.dismiss();
							} catch (Exception e) {}
						}
						parseJson(response);

					}
				}, new com.android.volley.Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						requestTimer.cancel();
						if (error != null) {
							if (!TextUtils.isEmpty(error.getMessage())) {
								Log.d(TAG, "Response Error: " + error.getMessage());
								QuopnUtils.QuopnLogWriteFile(TAG, error.getMessage(), true);
							}
							mConnectionListener.onResponse(ConnectionListener.CONNECTION_ERROR, new NetworkError(error));

							NetworkResponse networkResponse = error.networkResponse;
							if (networkResponse != null) {
								Log.d(TAG, "Response: " + new String(networkResponse.data));

								if (networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {//Invalid session
									LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(mContext);
									Intent logoutInvalidSessionIntent = new Intent(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
									localBrdcastMgr.sendBroadcast(logoutInvalidSessionIntent);
									return;
								}
							} else {
//							(networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED)
								//Invalid session
								if (QuopnUtils.isUnauthorized(error)) {

									Log.d(TAG, "Sending a broadcast to log out " + QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
									LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(mContext);
									Intent logoutInvalidSessionIntent = new Intent(QuopnConstants.BROADCAST_LOGOUT_INVALID_SESSION);
									localBrdcastMgr.sendBroadcast(logoutInvalidSessionIntent);
									return;
								}else if (QuopnUtils.isTimedOut(error)) {
									// timeout code
									Log.d(TAG, "Response:Network is unreachable");
									QuopnUtils.showDialogForContext(mContext, "Please check your internet connection and try again");
//									Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//									if (dialog != null) {
//										dialog.show();
//									}
									//mConnectionListener.myTimeout(requestTag);
									//onTimeoutDialog();
								} else {
									//mConnectionListener.myTimeout(requestTag);
								}
							}
						} else {
							Log.d(TAG, "Response Error: null");
							QuopnUtils.showDialogForContext(mContext, "Please check your internet connection and try again");
//							Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//							if (dialog != null) {
//								dialog.show();
//							}
							//mConnectionListener.myTimeout(requestTag);
						}
					}
		}) {
			@Override
			protected Map<String, String> getParams() {
				return mPostParams;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return mHeaderParams;
			}

		};
		postRequest.setRetryPolicy(new DefaultRetryPolicy(QuopnConstants.CONNECTION_TIME_OUT,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		postRequest.setTag(TAG);
		QuopnApplication.getRequestQueue().add(postRequest);
		requestTimer.start();

	}

	@Override
	public void parseJson(String data) {
		try {
			Gson gson = new Gson();
			mResponseVideoIssue= (VideoIssueData) gson.fromJson(data,VideoIssueData.class);
//			Log.v(TAG,"ResponseOk");
			mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK,mResponseVideoIssue);
		}catch (JsonSyntaxException eJsonSyntaxException) {
			QuopnUtils.QuopnLogWriteFile(TAG,data, true);
//			Log.v(TAG,"Parse Error :"+eJsonSyntaxException.getMessage());
			mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R,null);
		} catch (Exception eException) {
			QuopnUtils.QuopnLogWriteFile(TAG,data, true);
//			Log.v(TAG,"Parse Error :"+eException.getMessage());
		}

	}

	@Override
	public void setPostParams(Map<String, String> postParams) {
		this.mPostParams = postParams;
	}

	@Override
	public void setHeaderParams(Map<String, String> headerParams) {
		this.mHeaderParams = headerParams;
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTimeout(RequestTimer requestTimer) {
		if (mContext instanceof Activity) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
						Dialog dialog=new Dialog(mContext, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
						dialog.show();
					}
					
				}
			});
		}
		RequestPool.getInstance(mContext)
			.cancellAllPreviousRequestWithSameTag(TAG);
		mConnectionListener.onTimeout(this);
	}
}
