package com.quopn.wallet.connection;

import android.content.Context;

import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.Map;

public class ConnectRequest {
	protected Context mContext;
	protected Map<String, String> mPostParams;
	protected Map<String, String> mHeaderParams;
	protected ConnectionListener mConnectionListener;
	protected CustomProgressDialog mProgressDialog;
	
}
