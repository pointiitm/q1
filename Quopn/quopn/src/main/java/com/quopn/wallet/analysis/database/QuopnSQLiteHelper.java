package com.quopn.wallet.analysis.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuopnSQLiteHelper extends SQLiteOpenHelper{

		private static final String DATABASE_NAME = "quopnAnalysisDataBase.db";
	  	private static final int DATABASE_VERSION = 1;
	  
	  	private final String TABLE_MAINMENU = "main_menu";
		private final String COLUMN_ID = "_id";
		private final String COLUMN_EVENT_ID = "event_id";
		private final String COLUMN_TIMESTAMP = "timestamp";
		private final String COLUMN_EVENT = "event";
	  
	// Database creation sql statement
		private final String DATABASE_CREATE = "create table "
				+ TABLE_MAINMENU + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_EVENT_ID
				+ " text not null," + COLUMN_TIMESTAMP
				+ " text not null," + COLUMN_EVENT
				+ " text not null);";
	  
	public QuopnSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase quopnAnalysisDataBase) {
		  quopnAnalysisDataBase.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public String getTABLE_MAINMENU() {
		return TABLE_MAINMENU;
	}

	public String getCOLUMN_ID() {
		return COLUMN_ID;
	}

	public String getCOLUMN_EVENT_ID() {
		return COLUMN_EVENT_ID;
	}

	public String getCOLUMN_TIMESTAMP() {
		return COLUMN_TIMESTAMP;
	}

	public String getCOLUMN_EVENT() {
		return COLUMN_EVENT;
	}
}
