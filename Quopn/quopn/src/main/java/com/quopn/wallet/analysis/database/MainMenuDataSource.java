package com.quopn.wallet.analysis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.quopn.wallet.analysis.dataclasses.MainMenu;

import java.util.ArrayList;
import java.util.List;

public class MainMenuDataSource {

	private SQLiteDatabase quopnAnalysisDataBase;
	private QuopnSQLiteHelper quopnAnalysisDataBaseHelper;

	  public MainMenuDataSource(Context context) {
		  quopnAnalysisDataBaseHelper = new QuopnSQLiteHelper(context);
	  }
	  public void open() throws SQLException {
		  quopnAnalysisDataBase = quopnAnalysisDataBaseHelper.getWritableDatabase();
		  }
	  public void close() {
		  quopnAnalysisDataBaseHelper.close();
		  }

		public void ensureOpen() {
			if (quopnAnalysisDataBase == null || !quopnAnalysisDataBase.isOpen()) {
				open();
			}
		}

	  public long insertInMainMenu(MainMenu argMainMenu){
		    ensureOpen();
			ContentValues initialValues = new ContentValues();
			initialValues.put(quopnAnalysisDataBaseHelper.getCOLUMN_TIMESTAMP(),argMainMenu.getTimeStamp());
			initialValues.put(quopnAnalysisDataBaseHelper.getCOLUMN_EVENT_ID(), argMainMenu.getEventId() );
			initialValues.put(quopnAnalysisDataBaseHelper.getCOLUMN_EVENT(), argMainMenu.getValue());
			return quopnAnalysisDataBase.insert(quopnAnalysisDataBaseHelper.getTABLE_MAINMENU(), null, initialValues);
		}

		public long updateInMainMenu(MainMenu argMainMenu){
			ensureOpen();
			ContentValues initialValues = new ContentValues();
			initialValues.put(ColumnName.KEY_TIMESTAMP, argMainMenu.getEventId());
			initialValues.put(ColumnName.KEY_ID, argMainMenu.getTimeStamp());
			initialValues.put(ColumnName.KEY_PROFILE, argMainMenu.getValue());
			return quopnAnalysisDataBase.update(quopnAnalysisDataBaseHelper.getTABLE_MAINMENU(), initialValues,null,null);
		}
		public MainMenu selectSingleProfile(){
			  ensureOpen();
			  MainMenu tempMainMenu=new MainMenu();
			  Cursor mCursor = quopnAnalysisDataBase.query(quopnAnalysisDataBaseHelper.getTABLE_MAINMENU(), new String[] {},null , null, null, null, null);
			  mCursor.moveToFirst();
			  
			  String tempUserID = mCursor.getString(mCursor.getColumnIndex(ColumnName.KEY_ID));
			  String tempTimeStamp = mCursor.getString(mCursor.getColumnIndex(ColumnName.KEY_TIMESTAMP));
			  int tempValue = mCursor.getInt(mCursor.getColumnIndex(ColumnName.KEY_PROFILE));
			  
			  mCursor.close();
			  
			  tempMainMenu.setEventId(tempUserID);
			  tempMainMenu.setTimeStamp(tempTimeStamp);
			  tempMainMenu.setValue(tempValue);
			  
			  return tempMainMenu;
		}
		public List<MainMenu> retrivewAllInMainMenu(){
			 ensureOpen();
			 List<MainMenu> tMainMenuList = new ArrayList<MainMenu>();
			 MainMenu tMainMenu=null;
			 Cursor mCursor = quopnAnalysisDataBase.query(quopnAnalysisDataBaseHelper.getTABLE_MAINMENU(), new String[] {}, null , null, null, null, null);
			 mCursor.moveToFirst();
			  for(int i=0;i<mCursor.getCount();i++){
				  tMainMenu=new MainMenu();
				  tMainMenu.setEventId(mCursor.getString(mCursor.getColumnIndex(quopnAnalysisDataBaseHelper.getCOLUMN_EVENT_ID())));
				  tMainMenu.setTimeStamp(mCursor.getString(mCursor.getColumnIndex(quopnAnalysisDataBaseHelper.getCOLUMN_TIMESTAMP())));
				  tMainMenu.setMainMenuKey(mCursor.getInt(mCursor.getColumnIndex(quopnAnalysisDataBaseHelper.getCOLUMN_EVENT())));
				  tMainMenuList.add(tMainMenu);
				  mCursor.moveToNext();
			  }
			
			  mCursor.close();
			return tMainMenuList;
		}

		public void deleteAllDeta(String tableName){
			ensureOpen();
			quopnAnalysisDataBase.delete(tableName, null,null);
		}

		public Cursor retriveTableDataByQuery(String tableName){
			ensureOpen();
			Cursor tCursor;
			tCursor=quopnAnalysisDataBase.query(tableName, new String[] {}, null , null, null, null, null);
			int count=tCursor.getCount();
			return tCursor;
		}
		
		public void deleteAllData(){
			ensureOpen();
			quopnAnalysisDataBase.delete(quopnAnalysisDataBaseHelper.getTABLE_MAINMENU(), null,null);
		}

	  
}
