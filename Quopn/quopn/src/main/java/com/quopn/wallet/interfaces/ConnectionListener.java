package com.quopn.wallet.interfaces;

import com.quopn.wallet.connection.ConnectRequest;

/**
 * @author Sumeet
 *
 */

public interface ConnectionListener extends ResponseResults{
	/**
	 * @param responseResult There is two possible value of response CONNECTION_ERROR & RESPONSE_OK 
	 * if request not reached at server we return CONNECTION_ERROR otherwise return RESPONSE_OK.
	 * 
	 * @param response This is the response object we get it from Server side.
	 * */
	public void onResponse(int responseResult,Response response);
	public void onTimeout(ConnectRequest request);
	public void myTimeout(String requestTag);
}
