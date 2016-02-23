package com.quopn.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quopn.wallet.data.model.StoreDataList;
import com.quopn.wallet.data.model.StoreTypeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final String TAG = "Quopn/DBHelper";
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "quopndatabase";

	// Contacts table name
	private static final String TABLE_STORE_LIST = "storelist";

	// Store type table name
	private static final String TABLE_STORE_TYPE_LIST = "storetypelist";

	// Contacts Table Columns names
	private static final String STORE_ID = "_id";
	private static final String STORE_KEY = "store_uid";
	private static final String BUSINESS_NAME = "business_name";
	private static final String BUSINESS_ADDRESS = "business_address";
	private static final String TELEPHONE1 = "telephone1";
	private static final String TELEPHONE2 = "telephone2";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String OPEN_HOURS = "open_hours";
	private static final String DISTANCE = "distance";
	private static final String STORE_TYPE = "storetype";
	private static final String STORE_TYPE_ID = "storetypeid";
	private static final String STORE_ACCURACY = "accuracy";

	// Store type table columns name
	private static final String STORE_TYPE_PRI_ID = "_id";
	private static final String STORE_TYPE_CAT_NAME = "cat_name";
	private static final String STORE_TYPE_CAT_ID = "cat_id";
	private static final String STORE_TYPE_CAT_ICON_URL = "cat_icon_url";

	private static DataBaseHelper thisInstance;
	private Context context;

	private DataBaseHelper(Context argContext) {
		super(argContext, DATABASE_NAME, null, DATABASE_VERSION);
		context =argContext;
	}

	public static DataBaseHelper getInstance(Context context) {
		if (thisInstance == null) {
			thisInstance = new DataBaseHelper(context);
		}

		return thisInstance;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_STORE_LIST_TABLE = "CREATE TABLE " + TABLE_STORE_LIST + "("
				+ STORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + STORE_KEY + " TEXT,"+ BUSINESS_NAME + " TEXT,"+ BUSINESS_ADDRESS + " TEXT,"+  TELEPHONE1 + " TEXT,"+ TELEPHONE2 + " TEXT,"+ LATITUDE + " TEXT,"+ LONGITUDE + " TEXT,"+ OPEN_HOURS + " TEXT,"+ DISTANCE + " TEXT," + STORE_TYPE + " TEXT," + STORE_ACCURACY + " TEXT," + STORE_TYPE_ID + " INTEGER" + ")";
		db.execSQL(CREATE_STORE_LIST_TABLE);

		String CREATE_STORE_TYPE_LIST_TABLE = "CREATE TABLE " + TABLE_STORE_TYPE_LIST + "("
				+ STORE_TYPE_PRI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + STORE_TYPE_CAT_NAME + " TEXT,"+ STORE_TYPE_CAT_ID + " INTEGER,"+ STORE_TYPE_CAT_ICON_URL + " TEXT" + ")";
		db.execSQL(CREATE_STORE_TYPE_LIST_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 2 && newVersion >= 2){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			db.execSQL("DROP TABLE  IF EXISTS " + TABLE_STORE_LIST);
			onCreate(db);
		}
		// Drop older table if existed
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKOUT_QTY);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYSTEM_CLICKS);

		// Create tables again

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void addStoreData(StoreDataList storedatalist){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(STORE_KEY, storedatalist.getStore_uid());
		values.put(BUSINESS_NAME, storedatalist.getBusiness_Name());
		values.put(BUSINESS_ADDRESS, storedatalist.getBusiness_Address());
		values.put(TELEPHONE1, storedatalist.getTelephone1());
		values.put(TELEPHONE2, storedatalist.getTelephone2());
		values.put(LATITUDE, storedatalist.getLatitude());
		values.put(LONGITUDE, storedatalist.getLongitude());
		values.put(OPEN_HOURS, storedatalist.getOpen_hours());
		values.put(DISTANCE, storedatalist.getDistance());
		values.put(STORE_TYPE, storedatalist.getStoretype());
		values.put(STORE_ACCURACY, storedatalist.getAccuracy());
		values.put(STORE_TYPE_ID, storedatalist.getStoretypeid());
		db.insert(TABLE_STORE_LIST, null, values);
		//System.out.println("========CheckStoreAccuracy=222222===="+storedatalist.getAccuracy());
		db.close();
	}

	public void addStoreTypeData(StoreTypeList storeTypeList){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(STORE_TYPE_CAT_NAME, storeTypeList.getCat_name());
		values.put(STORE_TYPE_CAT_ID, storeTypeList.getCat_id());
		values.put(STORE_TYPE_CAT_ICON_URL, storeTypeList.getCat_icon_url());
		db.insert(TABLE_STORE_TYPE_LIST, null, values);
		db.close();
	}

	// Getting All Contacts
	public List<StoreDataList> getAllLocationDetail() {
		List<StoreDataList> storeList = new ArrayList<StoreDataList>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_STORE_LIST;
		SQLiteDatabase db = this.getWritableDatabase();
		//Cursor cursor = db.rawQuery(selectQuery, null);
		Cursor cursor = db.query(TABLE_STORE_LIST, null, null, null, null, null, null);

		// looping through all rows and adding to list


		if (cursor.moveToFirst()) {
			do {
				StoreDataList storedatalist = new StoreDataList();
				storedatalist.set_id(Integer.parseInt(cursor.getString(0)));
				storedatalist.setStore_uid(cursor.getString(1));
				storedatalist.setBusiness_Name(cursor.getString(2));
				storedatalist.setBusiness_Address(cursor.getString(3));
				storedatalist.setTelephone1(cursor.getString(4));
				storedatalist.setTelephone2(cursor.getString(5));
				storedatalist.setLatitude(Double.parseDouble(cursor.getString(6)));
				storedatalist.setLongitude(Double.parseDouble(cursor.getString(7)));
				storedatalist.setOpen_hours(cursor.getString(8));
				storedatalist.setDistance(cursor.getString(9));
				storedatalist.setStoretype(cursor.getString(10));
				storedatalist.setAccuracy(Integer.parseInt(cursor.getString(11)));
				storedatalist.setStoretypeid(Integer.parseInt(cursor.getString(12)));
				// Adding contact to list
				storeList.add(storedatalist);

			} while (cursor.moveToNext());
		}

		// return contact list
		return storeList;
	}

	public List<StoreTypeList> getAllStoreTypes() {
		List<StoreTypeList> storeTypeLists = new ArrayList<StoreTypeList>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_STORE_TYPE_LIST, null, null, null, null, null, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				StoreTypeList storeTypeList = new StoreTypeList();
				storeTypeList.set_id(cursor.getInt(0));
				storeTypeList.setCat_name(cursor.getString(1));
				storeTypeList.setCat_id(cursor.getInt(2));
				storeTypeList.setCat_icon_url(cursor.getString(3));

				storeTypeLists.add(storeTypeList);
			} while (cursor.moveToNext());
		}
		return storeTypeLists;
	}

	public HashMap<Integer, String> getStoreTypeHashMap() {
		HashMap<Integer,String> storeTypeHashMap = new HashMap<Integer,String>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_STORE_TYPE_LIST, null, null, null, null, null, null);
		Log.d("Bitmap", String.valueOf(cursor.getCount()));
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				storeTypeHashMap.put(Integer.parseInt(cursor.getString(2)), cursor.getString(3));
			} while (cursor.moveToNext());
		}
		return storeTypeHashMap;
	}

	public void deleteStoreListTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		//db.delete(TABLE_CHECKOUT_QTY,null,null);
//		db.execSQL("delete from "+ TABLE_CHECKOUT_QTY);
		db.delete(TABLE_STORE_LIST, null, null);
		System.out.println("=======deleteStoreListTable====");
		db.close();
	}

	public void deleteStoreTypeData() {
		SQLiteDatabase db = this.getWritableDatabase();
		//db.delete(TABLE_CHECKOUT_QTY,null,null);
//		db.execSQL("delete from "+ TABLE_CHECKOUT_QTY);
		if (this.isTableExists(db,TABLE_STORE_TYPE_LIST)) {
			db.delete(TABLE_STORE_TYPE_LIST, null, null);
			System.out.println("DataBaseHelper deleting storetypelist");
		}
		db.close();
	}

	private boolean isTableExists(SQLiteDatabase db, String tableName)
	{
		if (tableName == null || db == null || !db.isOpen())
		{
			return false;
		}
		Cursor cursor = db.rawQuery("SELECT 1 FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
		if (!cursor.moveToFirst())
		{
			return false;
		}
		return true;
	}

}
