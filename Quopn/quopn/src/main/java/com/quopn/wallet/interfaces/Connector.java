package com.quopn.wallet.interfaces;

/**
 * @author Sumeet
 *
 */

import java.util.Map;

public interface Connector {

	public void setPostParams(Map<String, String> postParams);
	public void setHeaderParams(Map<String, String> headerParams);
	public void connect();
	public void parseJson(String response);
	public void abort();

}
