package com.quopn.wallet.analysis;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;

import com.quopn.wallet.analysis.database.DatabaseManager;
import com.quopn.wallet.analysis.database.MainMenuDataSource;
import com.quopn.wallet.analysis.dataclasses.AnalysisBase;
import com.quopn.wallet.analysis.dataclasses.MainMenu;

public class AnalysisManager extends AnalysisBase{
	private static final String TAG="AnalysisManager";
	private Context mContext;
	private MainMenu mMainMenu;
//	private MainMenu mMainMenuResponse;
//	private List<MainMenu> mMainMenuAnalysisList;
	private MainMenuDataSource mMainMenuDataSource;

	public AnalysisManager(Context context){
		
		super.timeStamp=""+System.currentTimeMillis();
		mMainMenu=new MainMenu();
		mContext=context;
//		mMainMenuAnalysisList=new ArrayList<MainMenu>();
		mMainMenuDataSource=new MainMenuDataSource(mContext);
		mMainMenuDataSource.open();
	}

	public AnalysisManager getAnalysisManager(Context argContext){
		/*if (mAnalysisManager == null){
			mAnalysisManager = new AnalysisManager(argContext);
		}
		return mAnalysisManager;*/
		return this;
	}

	/**
	 * send() : This method is use of inserting events value in analysis database 
	 * @param evnetNameID : Store id in data base then convert into name before sending
	 * In THis method we are sending default value of eventId it is NULL
	 */
	public void send(int evnetNameID){
		mMainMenu.setTimeStamp(getTimeStamp());
		mMainMenu.setEventId("NULL");
		mMainMenu.setValue(evnetNameID);
		mMainMenuDataSource.insertInMainMenu(mMainMenu);
	}
	
	/**
	 * send() : This method is use of inserting events value in analysis database 
	 * @param evnetNameID : Store id in data base then convert into name before sending
	 * @param eventId :  eventId means value id of event name like quopnId, categoryId
	 */
	public void send(int evnetNameID,String eventId){
		mMainMenu.setTimeStamp(getTimeStamp());
		mMainMenu.setEventId(""+eventId);
		mMainMenu.setValue(evnetNameID);
		mMainMenuDataSource.insertInMainMenu(mMainMenu);
	}
	
	public String receive(){
		String tEncodedString=null;
		try{
		Cursor tCursor;
		DatabaseManager tDatabaseManager=new DatabaseManager(mContext);
		JsonFactory tJsonFactory=new JsonFactory(mContext);
		tCursor=tDatabaseManager.retrive(DatabaseManager.TableName.MAINMENU);
		
		if(tCursor != null && tCursor.getCount()>0){
		 tEncodedString=tJsonFactory.convertTableToString(tCursor, DatabaseManager.TableName.MAINMENU);
		}
		}catch(SQLiteCantOpenDatabaseException e){}catch(Exception e){
			e.printStackTrace();
		}
				
		return tEncodedString;
	}

	
	public void wipeAllAnalysisData(Context context){
		try{
		mContext=context;
		DatabaseManager tDatabaseManager=new DatabaseManager(mContext);
		tDatabaseManager.deleteTableData(DatabaseManager.TableName.MAINMENU);
		}catch(SQLiteCantOpenDatabaseException e){}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		if(mMainMenuDataSource!=null)
		mMainMenuDataSource.close();
	}

}
