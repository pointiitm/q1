package com.quopn.wallet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "Quopn/DBHelper";
	
	private static final String DATABASE_NAME = "quopn.db";
	private static final int DATABASE_VERSION = 9;
	// Database creation SQL statement

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ITableData.TABLE_CATEGORY.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_QUOPNS.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_NOTIFICATIONS.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_GIFTS.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_STATES.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_CITIES.CREATE_STATEMENT);
		database.execSQL(ITableData.TABLE_MYCART.CREATE_STATEMENT);
        database.execSQL(ITableData.TABLE_VOUCHER.CREATE_STATEMENT);
	}

//	@Override
//	public void onUpgrade(SQLiteDatabase database, int oldVersion,
//			int newVersion) {
//		if(oldVersion == 1 && newVersion == 3){
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_CATEGORY.TABLE_NAME);
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME);
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME);
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_GIFTS.TABLE_NAME);
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_STATES.TABLE_NAME);
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_CITIES.TABLE_NAME);
//			onCreate(database);
//		} else if(newVersion == 2){
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_CATEGORY.TABLE_NAME);
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_QUOPNS.TABLE_NAME);
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME);
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_GIFTS.TABLE_NAME);
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_STATES.TABLE_NAME);
//		database.execSQL("DROP TABLE IF EXISTS "
//				+ ITableData.TABLE_CITIES.TABLE_NAME);
//		onCreate(database);
//		}else if(newVersion == 3){
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC +" text " );
//				
//		}else if(newVersion == 4){
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS +" text default \'0\'" );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED +" text default \'0\'" );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_QUOPNS.COLUMN_END_DESC +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX +" int " );
//			
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_GIFTS.GIFT_TYPE  +" text default \'E\'" );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_GIFTS.PARTNER_CODE  +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_GIFTS.TERMS_COND  +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_GIFTS.SORT_INDEX  +" int " );
//			
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME +" text " );
//			database.execSQL("ALTER TABLE "
//					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
//					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG +" integer default 0 " );
//			
//			database.execSQL(ITableData.TABLE_MYCART.CREATE_STATEMENT);
//			
//		}else if(newVersion == 5){
//			database.execSQL("DROP TABLE IF EXISTS "
//					+ ITableData.TABLE_MYCART.TABLE_NAME);
//			database.execSQL(ITableData.TABLE_MYCART.CREATE_STATEMENT);
//
//		}
//	}
	
	
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		if(oldVersion < 2 && newVersion >=2){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_GIFTS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_STATES.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_CITIES.TABLE_NAME);
			onCreate(database);
			return;
		}
		if(oldVersion < 3 && newVersion >= 3){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC + " text ");
				
		}
		if(oldVersion < 4 && newVersion >= 4){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS +" text default \'0\'" );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED +" text default \'0\'" );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_QUOPNS.COLUMN_END_DESC +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX +" int " );
			
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_GIFTS.GIFT_TYPE  +" text default \'E\'" );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_GIFTS.PARTNER_CODE  +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_GIFTS.TERMS_COND  +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_GIFTS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_GIFTS.SORT_INDEX  +" int " );
			
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME +" text " );
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME +" ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG +" integer default 0 " );
			
			database.execSQL(ITableData.TABLE_MYCART.CREATE_STATEMENT);
			
		}
		if(oldVersion <5 && newVersion >= 5){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_MYCART.TABLE_NAME);
			database.execSQL(ITableData.TABLE_MYCART.CREATE_STATEMENT);

		}
		if(oldVersion <6 && newVersion >= 6){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_QUOPNS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_GIFTS.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_STATES.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_CITIES.TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS "
					+ ITableData.TABLE_MYCART.TABLE_NAME);
			onCreate(database);
			return;
		}
        if(oldVersion < 7 && newVersion >= 7){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN + " text ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN_VALUE + " text ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_CAPTION + " text ");

			/*database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME + " ADD "
					+ ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_NAME + " text ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME + " ADD "
					+ ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_TYPE + " text ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME + " ADD "
					+ ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_THUMBIMAGE + " text ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_CATEGORY.TABLE_NAME + " ADD "
					+ ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE + " integer ");*/

			database.execSQL("DROP TABLE  IF EXISTS " + ITableData.TABLE_CATEGORY.TABLE_NAME);
			database.execSQL(ITableData.TABLE_CATEGORY.CREATE_STATEMENT);
        }
        if(oldVersion < 8 && newVersion >= 8){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
            database.execSQL("ALTER TABLE "
                    + ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
                    + ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID + " integer ");
                    database.execSQL("ALTER TABLE "
                    + ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
                    + ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID + " integer ");

        }

		if(oldVersion < 9 && newVersion >= 9){
			Log.d(TAG, "oldVersion: '" + oldVersion + "', newVersion: '" + newVersion + "'");
//			database.execSQL("DROP TABLE  IF EXISTS " + ITableData.TABLE_NOTIFICATIONS.CREATE_STATEMENT);
//			onCreate(database);
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_URL + " integer ");
			database.execSQL("ALTER TABLE "
					+ ITableData.TABLE_NOTIFICATIONS.TABLE_NAME + " ADD "
					+ ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_VALUE + " integer ");
		}
	}

}
