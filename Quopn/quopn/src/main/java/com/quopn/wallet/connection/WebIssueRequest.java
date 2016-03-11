package com.quopn.wallet.connection;

/**
 * @author Sandeep
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
import com.quopn.wallet.data.model.WebIssueData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Connector;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class WebIssueRequest extends ConnectRequest implements Connector, RequestTimer.RequestTimerListener {
    private static final String TAG = "Quopn/WebIssueRequest";
    private WebIssueData mResponseWebIssue;
    private RequestTimer requestTimer;

    //QuopnOperations quopnOperations;
    public WebIssueRequest(Context mContext,
                           ConnectionListener connectionListener) {
        mProgressDialog = new CustomProgressDialog(mContext);
        //quopnOperations = new QuopnOperations();
        /*mProgressDialog.setTitle(mContext.getResources().getString(
                R.string.webissue_txt));*/
        this.mConnectionListener = connectionListener;
        this.mContext = mContext;
        requestTimer = new RequestTimer();
        requestTimer.setListener(this);
        this.mHeaderParams = new HashMap<String, String>();
        this.mHeaderParams.put(QuopnApi.ParamKey.AUTHORIZATION, PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
        this.mHeaderParams.put(QuopnApi.ParamKey.x_session, PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID));
    }

    @Override
    public void connect() {
        Log.d(TAG, "Request from " + QuopnApi.WEB_ISSUE + " with tag " + TAG);
        Log.d(TAG, "Headers : " + mHeaderParams);
        Log.d(TAG, "Post : " + mPostParams);


//		mProgressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST,
                QuopnApi.WEB_ISSUE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestTimer.cancel();
                        Log.d(TAG, response);
                        QuopnUtils.QuopnLogWriteFile(TAG, response, true);
//						mProgressDialog.dismiss();
                        parseJson(response);

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                requestTimer.cancel();
                if (error != null) {
                    if (!TextUtils.isEmpty(error.getMessage())) {
                        //Log.d(TAG, "Response Error: " + error.getMessage());
                        QuopnUtils.QuopnLogWriteFile(TAG, error.getMessage(), true);
                    }
                    mConnectionListener.onResponse(ConnectionListener.CONNECTION_ERROR, new NetworkError(error));

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        //Log.d(TAG, "Response: " + new String(networkResponse.data));

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
                            Log.d(TAG, "Response:Network is unreachable1111");
//                            Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//                            if (dialog != null) {
//                                dialog.show();
//                            }
                            //mConnectionListener.myTimeout(requestTag);
                            //onTimeoutDialog();
                            QuopnUtils.showDialogForContext(mContext,"Please check your internet connection and try again");
                        } else {
                            Log.d(TAG, "Response:Network is unreachable22222");
                            //mConnectionListener.myTimeout(requestTag);
//                            Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//                            if (dialog != null) {
//                                dialog.show();
//                            }
                            QuopnUtils.showDialogForContext(mContext,"Please check your internet connection and try again");
                        }
                    }
                } else {
                    Log.d(TAG, "Response:Network is unreachable33333");
//                    Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
//                    if (dialog != null) {
//                        dialog.show();
//                    }
                    //mConnectionListener.myTimeout(requestTag);
                    QuopnUtils.showDialogForContext(mContext,"Please check your internet connection and try again");
                }

                //QuopnUtils.QuopnLogWriteFile(TAG, error.getMessage(), true);
                if (error.getMessage() != null) {
                    Log.d(TAG, "Response:Network is unreachabl4444444");
                    //mProgressDialog.dismiss();
                    //Dialog dialog = new Dialog(mContext, R.string.dialog_title_error, R.string.slow_internet_connection);
                    //dialog.show();
                    QuopnUtils.showDialogForContext(mContext, "Please check your internet connection and try again");

                } else {
                    mProgressDialog.dismiss();
                    Log.d(TAG, "Response:Network is unreachabl55555555");
//                    Dialog dialog = new Dialog(mContext, R.string.server_error_title, R.string.server_error);
//                    dialog.show();
                    QuopnUtils.showDialogForContext(mContext,"Please check your internet connection and try again");
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(QuopnConstants.CONNECTION_TIME_OUT, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postRequest.setTag(TAG);
		QuopnApplication.getRequestQueue().add(postRequest);
        requestTimer.start();
    }

    @Override
    public void parseJson(String data) {
        try {
            Gson gson = new Gson();
            mResponseWebIssue = (WebIssueData) gson.fromJson(data, WebIssueData.class);
			Log.v(TAG,"ResponseOk");
            mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, mResponseWebIssue);
        } catch (JsonSyntaxException eJsonSyntaxException) {
            QuopnUtils.QuopnLogWriteFile(TAG, data, true);
			Log.v(TAG, "Parse Error :" + eJsonSyntaxException.getMessage());
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null);
        } catch (Exception eException) {
            QuopnUtils.QuopnLogWriteFile(TAG, data, true);
			Log.v(TAG, "Parse Error :" + eException.getMessage());
        }

    }

    @Override
    public void setPostParams(Map<String, String> postParams) {
        this.mPostParams = postParams;
    }

    @Override
    public void setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams.putAll(headerParams);
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
                        Dialog dialog = new Dialog(mContext, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
                        dialog.show();
                    }

                }
            });
        }
        RequestPool.getInstance(mContext)
                .cancellAllPreviousRequestWithSameTag(TAG);
        mConnectionListener.onTimeout(this);
    }

    public void stopProgress(RequestTimer requestTimer) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();

                        //Dialog dialog=new Dialog(mContext, R.string.slow_internet_connection_title,R.string.slow_internet_connection);
                        //dialog.show();
                    }

                }
            });
        }
        RequestPool.getInstance(mContext)
                .cancellAllPreviousRequestWithSameTag(TAG);
        mConnectionListener.onTimeout(this);
    }

}
