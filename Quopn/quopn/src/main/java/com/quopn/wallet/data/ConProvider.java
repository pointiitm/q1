package com.quopn.wallet.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class ConProvider extends ContentProvider {

	// database
	private DBHelper database;

	// used for the UriMacher
	private static final int ID_CATEGORY_TABLE = 10;
	private static final int ID_QUOPN_TABLE = 20;
	private static final int ID_TODO = 30;
	private static final int ID_NOTIFICATION_TABLE = 40;
	private static final int ID_GIFTS_TABLE = 50;
	private static final int ID_STATES_TABLE = 60;
	private static final int ID_CITIES_TABLE = 70;
	private static final int ID_MYCART_TABLE = 80;
    private static final int ID_VOUCHER_TABLE = 90;

	private static final String AUTHORITY = "quopn.wallet.contentprovider";

	private static final String BASE_PATH_CATEGORY = "category";
	public static final Uri CONTENT_URI_CATEGORY = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_CATEGORY);

	private static final String BASE_PATH_QUOPN = "quopn";
	public static final Uri CONTENT_URI_QUOPN = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_QUOPN);
	
	private static final String BASE_PATH_NOTIFICATION = "notification";
	public static final Uri CONTENT_URI_NOTIFICATION = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_NOTIFICATION);
	
	private static final String BASE_PATH_GIFTS = "gifts";
	public static final Uri CONTENT_URI_GIFTS = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_GIFTS);
	
	private static final String BASE_PATH_STATES = "states";
	public static final Uri CONTENT_URI_STATES = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_STATES);
	
	private static final String BASE_PATH_CITIES = "cities";
	public static final Uri CONTENT_URI_CITIES = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_CITIES);
	
	private static final String BASE_PATH_MYCART = "mycart";
	public static final Uri CONTENT_URI_MYCART = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_PATH_MYCART);

    private static final String BASE_PATH_VOUCHER = "voucher";
    public static final Uri CONTENT_URI_VOUCHER = Uri.parse("content://"
            + AUTHORITY + "/" + BASE_PATH_VOUCHER);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/categories";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/category";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_CATEGORY, ID_CATEGORY_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_QUOPN, ID_QUOPN_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_NOTIFICATION, ID_NOTIFICATION_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_GIFTS, ID_GIFTS_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_CATEGORY + "/#", ID_TODO);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_STATES, ID_STATES_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_CITIES, ID_CITIES_TABLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_MYCART, ID_MYCART_TABLE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_VOUCHER, ID_VOUCHER_TABLE);
	}

	@Override
	public boolean onCreate() {
		database = new DBHelper(getContext());

		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
//		checkColumns(projection);

		// Set the table
//		queryBuilder.setTables(ITableData.TABLE_CATEGORY.TABLE_NAME);
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = null;

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ID_CATEGORY_TABLE:
			cursor = db.query(ITableData.TABLE_CATEGORY.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_STATES_TABLE:
			cursor = db.query(ITableData.TABLE_STATES.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_CITIES_TABLE:
			cursor = db.query(ITableData.TABLE_CITIES.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_QUOPN_TABLE:
			cursor = db.query(ITableData.TABLE_QUOPNS.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_NOTIFICATION_TABLE:
			cursor = db.query(ITableData.TABLE_NOTIFICATIONS.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_GIFTS_TABLE:
			cursor = db.query(ITableData.TABLE_GIFTS.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_MYCART_TABLE:
			cursor = db.query(ITableData.TABLE_MYCART.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case ID_TODO:
			// adding the ID to the original query
			queryBuilder.appendWhere(ITableData.TABLE_CATEGORY.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
        case ID_VOUCHER_TABLE:
             cursor = db.query(ITableData.TABLE_VOUCHER.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
             break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		
//		Cursor cursor = queryBuilder.query(db, projection, selection,
//				selectionArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case ID_CATEGORY_TABLE:
			id = sqlDB.insert(ITableData.TABLE_CATEGORY.TABLE_NAME, null, values);
			break;
		case ID_STATES_TABLE:
			id = sqlDB.insert(ITableData.TABLE_STATES.TABLE_NAME, null, values);
			break;
		case ID_CITIES_TABLE:
			id = sqlDB.insert(ITableData.TABLE_CITIES.TABLE_NAME, null, values);
			break;
		case ID_QUOPN_TABLE:
			id = sqlDB.insert(ITableData.TABLE_QUOPNS.TABLE_NAME, null, values);
			break;
		case ID_NOTIFICATION_TABLE:
			id = sqlDB.insert(ITableData.TABLE_NOTIFICATIONS.TABLE_NAME, null, values);
			break;
		case ID_GIFTS_TABLE:
			id = sqlDB.insert(ITableData.TABLE_GIFTS.TABLE_NAME, null, values);
			break;
		case ID_MYCART_TABLE:
			id = sqlDB.insert(ITableData.TABLE_MYCART.TABLE_NAME, null, values);
			break;
        case ID_VOUCHER_TABLE:
            id = sqlDB.insert(ITableData.TABLE_VOUCHER.TABLE_NAME, null, values);
            break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH_CATEGORY + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case ID_CATEGORY_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_CATEGORY.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_STATES_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_STATES.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_CITIES_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_CITIES.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_QUOPN_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_QUOPNS.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_NOTIFICATION_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_NOTIFICATIONS.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_GIFTS_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_GIFTS.TABLE_NAME,
					selection, selectionArgs);
			break;
		case ID_MYCART_TABLE:
			rowsDeleted = sqlDB.delete(ITableData.TABLE_MYCART.TABLE_NAME,
					selection, selectionArgs);
			break;
        case ID_VOUCHER_TABLE:
            rowsDeleted = sqlDB.delete(ITableData.TABLE_VOUCHER.TABLE_NAME,
                        selection, selectionArgs);
                break;
		case ID_TODO:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ITableData.TABLE_CATEGORY.TABLE_NAME,
						ITableData.TABLE_CATEGORY.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(ITableData.TABLE_CATEGORY.TABLE_NAME,
						ITableData.TABLE_CATEGORY.COLUMN_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
					
		case ID_CATEGORY_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_CATEGORY.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case ID_STATES_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_STATES.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case ID_CITIES_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_CITIES.TABLE_NAME,
					values, selection, selectionArgs);
			break;
			
		case ID_QUOPN_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_QUOPNS.TABLE_NAME,
					values, selection, selectionArgs);
			break;
			
		case ID_NOTIFICATION_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_NOTIFICATIONS.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case ID_GIFTS_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_GIFTS.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case ID_MYCART_TABLE:
			rowsUpdated = sqlDB.update(ITableData.TABLE_MYCART.TABLE_NAME,
					values, selection, selectionArgs);
			break;
        case ID_VOUCHER_TABLE:
             rowsUpdated = sqlDB.update(ITableData.TABLE_VOUCHER.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
		case ID_TODO:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ITableData.TABLE_CATEGORY.TABLE_NAME,
						values, ITableData.TABLE_CATEGORY.COLUMN_ID + "=" + id,
						null);
			} else {
				rowsUpdated = sqlDB.update(ITableData.TABLE_CATEGORY.TABLE_NAME,
						values, ITableData.TABLE_CATEGORY.COLUMN_ID + "=" + id
								+ " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { ITableData.TABLE_CATEGORY.COLUMN_ICON,
				ITableData.TABLE_CATEGORY.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}
