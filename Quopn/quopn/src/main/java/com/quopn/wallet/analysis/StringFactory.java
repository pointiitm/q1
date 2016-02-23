package com.quopn.wallet.analysis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.URLEncoder;

public class StringFactory {
	private Gson gson ;
	public StringFactory() {
		gson = new Gson();
	}
	
	public String convertJsonToString(JSONObject argJSONObject){
		JsonObject tJsonObject =new JsonObject ();
		return gson.toJson(argJSONObject);
	}
	
	public String stringEncoding(String stringToEncoding){
		return URLEncoder.encode(stringToEncoding);
	}
	
}
