package com.quopn.wallet.analysis.database;

import android.content.Context;
import android.database.Cursor;

public class DatabaseManager {
	Context mContext;
	private Cursor mCursor;
	
	public DatabaseManager(Context context) {
	}
	public interface TableName {
		String MAINMENU="mainmenu"; 
		}
	
	
	public void send(String query){
		
	}
	public Cursor retrive(String tableName){
	return mCursor;
	}
	public void deleteTableData(String tableName){
	}

}
