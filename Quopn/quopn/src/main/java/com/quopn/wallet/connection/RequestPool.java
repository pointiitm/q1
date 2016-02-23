package com.quopn.wallet.connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestPool {
	private static RequestPool mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestPool(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestPool getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestPool(context);
        }
        return mInstance;
    }

    public void cancellAllPreviousRequestWithSameTag(String tag){
    	mRequestQueue.cancelAll(tag);
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

 
}

