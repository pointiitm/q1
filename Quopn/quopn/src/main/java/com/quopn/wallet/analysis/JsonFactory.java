package com.quopn.wallet.analysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.quopn.wallet.analysis.database.ColumnName;
import com.quopn.wallet.analysis.dataclasses.MainMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonFactory {
	Context mContext;
	private static final String TAG="Config";

	public static SharedPreferences sharedpreferences;
	List<MainMenu> mMainMenuAnalysisList;
	String analysisDataString;
	StringFactory tStringFactory;
	String tEncodedString=null;
	public JsonFactory(Context context) {
		this.mContext=context;
		tStringFactory=new StringFactory();
	}
	
	public String convertTableToString(Cursor tableCursor,String tableName){
		JSONObject tJSONObject=null;
		 String eventValue="NULL";
		 tableCursor.moveToFirst();
		 for(int i=0;i<tableCursor.getCount();i++){
			 tJSONObject=new JSONObject();
			 try {
				tJSONObject.put("timeStamp", tableCursor.getString(tableCursor.getColumnIndex(ColumnName.KEY_TIMESTAMP)));
				tJSONObject.put("eventId", tableCursor.getString(tableCursor.getColumnIndex(ColumnName.KEY_ID)));

				switch(tableCursor.getInt(tableCursor.getColumnIndex(ColumnName.KEY_PROFILE))){
				case AnalysisEvents.PROFILE : 
					eventValue="profile";
					break;
				case AnalysisEvents.MYCART: 
					eventValue="mycart";
					break;
				case AnalysisEvents.MYQUOPN : 
					eventValue="myquopn";
					break;
				case AnalysisEvents.SHOPEAROUND : 
					eventValue="shoparound";
					break;
				case AnalysisEvents.MYHISTORY : 
					eventValue="myhistory";
					break;
				case AnalysisEvents.ABOUT : 
					eventValue="about";
					break;
				case AnalysisEvents.INVITE : 
					eventValue="invite";
					break;
				}
				tJSONObject.put("eventName",eventValue);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 tEncodedString+=tStringFactory.stringEncoding(tStringFactory.convertJsonToString(tJSONObject));
			 tableCursor.moveToNext();
		 
		  }
		 if(tableCursor != null)
		 tableCursor.close();
		return tEncodedString;
	}

}
