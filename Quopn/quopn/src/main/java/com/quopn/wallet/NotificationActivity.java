package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.NotifyStatusData;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.AspectRatioImageView;
import com.quopn.wallet.views.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends ActionBarActivity implements OnItemClickListener, ConnectionListener {

	private String TAG = "Notification ACTIVITY";
	CustomProgressDialog mCustomProgressDialog;

	private ListView mNotificationsList;
	private Cursor cursor;
	private ImageView mImgNoNotifications;
	private TextView no_notify_text;
	private boolean CREATE_PARENT = false;
	private RelativeLayout rlyNotifications;

	private DisplayImageOptions mDisplayImageOptionsNotification;
	private ImageLoader mImageLoaderNotification;
	private ImageView notification_switch;
	private final String NOTIFY_OFF = "0";
	private final String NOTIFY_ON = "1";
	private String NOTIFY_STATUS = "";
	private boolean IS_TUT_API_SWITCHED = false;
//	private SampleAdapter mAdapter;
	private MyCursorAdapter myCursorAdapter;
	private String mExpandedRowId = "";
	private AnalysisManager mAnalysisManager;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.notifications);
		notification_switch = (ImageView) findViewById(R.id.tut_switch);

		String pref = PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY);
		NOTIFY_STATUS = pref;

		if (NOTIFY_STATUS != null && NOTIFY_STATUS.equals(NOTIFY_OFF)) {
			notification_switch.setImageResource(R.drawable.notify_off);
		} else {
			notification_switch.setImageResource(R.drawable.notify_on);
		}

		mAnalysisManager = ((QuopnApplication)getApplicationContext()).getAnalysisManager();

		/*notification_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					NOTITY_STATUS = NOTITY_ON;
					setNotificationStatus();
				} else {
					NOTITY_STATUS = NOTITY_OFF;
					setNotificationStatus();
				}
			}
		});*/


		notification_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setNotificationStatus();
			}
		});


		if (getIntent().getExtras() != null
				&& getIntent().getExtras().getString("DONOT_CREATE_PARENT") != null) {
			CREATE_PARENT = false;
		} else {
			CREATE_PARENT = true;
		}

		mImageLoaderNotification = ImageLoader.getInstance();
		mImageLoaderNotification.init(ImageLoaderConfiguration
				.createDefault(this));

		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		mDisplayImageOptionsNotification = builder.cacheOnDisc(true).cacheInMemory(true)
				.resetViewBeforeLoading(true)
				.displayer(new FadeInBitmapDisplayer(1500))
				.showStubImage(R.drawable.placeholder_myquopns).build();

		mCustomProgressDialog = new CustomProgressDialog(this);
		// mCustomProgressDialog.show();

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.action_bar_bg));

		View actionBarView = View
				.inflate(this, R.layout.actionbar_layout, null);
		getSupportActionBar().setCustomView(actionBarView);

		ImageView slider = (ImageView) actionBarView.findViewById(R.id.slider);
		slider.setImageResource(R.drawable.back);
		slider.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ImageView home_btn = (ImageView) actionBarView
				.findViewById(R.id.home_btn);
		home_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, 0); //reset the notification counter
				sendBroadCast(NotificationActivity.this);
//				updateNewBandImage();
				setResult(RESULT_OK);
				finish();

			}
		});

		SearchView searchView = (SearchView) actionBarView
				.findViewById(R.id.fragment_address_search);
		searchView.setVisibility(View.INVISIBLE);
		ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
		mCommonCartButton.setVisibility(View.INVISIBLE);
		TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
		mNotification_Counter_tv.setVisibility(View.INVISIBLE);
		TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
		mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

		TextView tvFindQuopns = (TextView) findViewById(R.id.text_find_quopns);

		tvFindQuopns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
				LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
				finish();
			}
		});

		cursor = getContentResolver().query(
				ConProvider.CONTENT_URI_NOTIFICATION, null, null, null,
				ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " desc");


//		mAdapter = new SampleAdapter(this);


		rlyNotifications = (RelativeLayout) findViewById(R.id.rlyNotifications);
		mNotificationsList = (ListView) findViewById(R.id.notification_list);
		mImgNoNotifications = (ImageView) findViewById(R.id.img_no_notifications);
		no_notify_text = (TextView) findViewById(R.id.no_notify_text);
		myCursorAdapter = new MyCursorAdapter(this, cursor , 0);
		if (cursor != null && cursor.getCount() >= 1 ) {

			while (cursor.moveToNext()) {
				String strDate = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME));

				if(cursor.getPosition()>19||getDateDifference(strDate)>=4){
					getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " = ? ",	new String[] { ""+cursor.getPosition() });

				}else{
				/*mAdapter.add(new SampleItem(
						cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION)),
						cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL)),
						cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC)),
						cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID)),
						cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME)),
						cursor.getInt(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG)),
                        cursor.getString(cursor
								.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_PROMO))
								));*/


				}

			}

			no_notify_text.setVisibility(View.GONE);
			mImgNoNotifications.setVisibility(View.GONE);
			mNotificationsList.setVisibility(View.VISIBLE);
			tvFindQuopns.setVisibility(View.GONE);
			rlyNotifications.setBackgroundColor(Color.argb(1, 0xc3, 0xc3, 0xc3));
			mNotificationsList.setAdapter(myCursorAdapter);
			mNotificationsList.setOnItemClickListener(this);

		} else {
			rlyNotifications.setBackgroundColor(Color.WHITE);
			mNotificationsList.setVisibility(View.GONE);
			no_notify_text.setVisibility(View.VISIBLE);
			mImgNoNotifications.setVisibility(View.VISIBLE);
			tvFindQuopns.setVisibility(View.VISIBLE);
		}



	}

	private int getDateDifference(String strDate){
		int diffDays = 0;
		try{
			if(strDate.contains("-15"))
				strDate = strDate.replaceFirst("-15", "-2015");
			SimpleDateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			//Date notifDate = Date.valueOf(strDate.substring(0, strDate.length()-5));
			java.util.Date notifDate  = dfmt.parse(strDate);
			Calendar cal = Calendar.getInstance();
			java.util.Date today = dfmt.parse(dfmt.format(cal.getTime()));
			diffDays = (int) ((today.getTime() - notifDate.getTime())/ (1000 * 60 * 60 * 24));

		}
		catch(Exception e){
			Log.d(TAG, e.getMessage());
		}
		return diffDays;
	}
	@Override
	protected void onResume() {
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, 0); //reset the notification counter
		sendBroadCast(this);
		//Cancel all existing notifications
		NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancelAll();

		super.onResume();
	}

	public void getFlag(){

		 cursor.getInt(cursor
				.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG));
	}
	private void setNotificationStatus() {
		if (QuopnUtils.isInternetAvailable(this)) {
			mCustomProgressDialog.show();
//			String userid=PreferenceUtil.getInstance(this).getPreference(QuopnConstants.USER_ID);
			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", PreferenceUtil.getInstance(getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_ID));
			if(NOTIFY_STATUS!=null && NOTIFY_STATUS.equals(NOTIFY_ON)){
				params.put("status", NOTIFY_OFF);
			}else if(NOTIFY_STATUS!=null && NOTIFY_STATUS.equals(NOTIFY_OFF)){
				params.put("status", NOTIFY_ON);
			}else{
				params.put("status", NOTIFY_OFF);
			}
			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			connectionFactory
					.createConnection(QuopnConstants.NOTIFY_STATUS_CODE);

		} else {
//			setSwitchMode();
			Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}

	private void setSwitchMode(String argStatus) {
		NOTIFY_STATUS = argStatus;
		 if (NOTIFY_STATUS != null && NOTIFY_STATUS.equals(NOTIFY_OFF)){
			notification_switch.setImageResource(R.drawable.notify_off);
		}else if (NOTIFY_STATUS != null && NOTIFY_STATUS.equals(NOTIFY_ON)){
			notification_switch.setImageResource(R.drawable.notify_on);
		}else{
			notification_switch.setImageResource(R.drawable.notify_on);
		}
		PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY, argStatus);
	}




	private class SampleItem {
		public String tag;
		public String iconRes;
		public String desc;
		public String campaign_id;
		public String date_time;
		public int isNew;
        public String promo;

		public SampleItem(String tag, String iconRes, String desc,String campaignid,String datetime, int iNew,String promo) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.desc = desc;
			this.campaign_id=campaignid;
			this.date_time=datetime;
			this.isNew = iNew;
            this.promo=promo;

		}
	}
	static class ViewHolderItem {
		ImageView icon;
		TextView datetime;
		TextView title;
		TextView desc;
		ImageView newicon;
	}
	ViewHolderItem viewHolder = null;
	public class SampleAdapter extends ArrayAdapter<SampleItem> {


		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder=new ViewHolderItem();

			if (convertView == null) {
				convertView =LayoutInflater.from(getContext()).inflate(
						R.layout.notification_row, null);
				viewHolder.icon = (AspectRatioImageView) convertView
						.findViewById(R.id.row_icon);

				viewHolder.datetime = (TextView) convertView
						.findViewById(R.id.row_datetime);

				viewHolder.title = (TextView) convertView
						.findViewById(R.id.row_title);

				viewHolder.desc = (TextView) convertView
						.findViewById(R.id.row_no_user);
				viewHolder.newicon = (ImageView) convertView
						.findViewById(R.id.new_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}
			viewHolder.datetime.setText(""+getItem(position).date_time);
			viewHolder.title.setText(getItem(position).tag);
			viewHolder.desc.setText(getItem(position).desc);

			if(null != getItem(position).iconRes && !TextUtils.isEmpty(getItem(position).iconRes)){
				try {
					mImageLoaderNotification.displayImage(
							getItem(position).iconRes, viewHolder.icon,
							mDisplayImageOptionsNotification, null);
				} catch (Exception e) {
					Log.i("Exception in cart", e.getMessage());
				}

			}else{
				viewHolder.icon.setImageResource(R.drawable.placeholder_myquopns);
			}

			//Set New Icon
			if(getItem(position).isNew==QuopnConstants.BOOL_FALSE)
				viewHolder.newicon.setVisibility(View.INVISIBLE);
			else
				viewHolder.newicon.setVisibility(View.VISIBLE);

			// icon.setImageResource(getItem(position).iconRes);

			return convertView;
		}

	}

	public void refreshCursorAdapter(){
		cursor = getContentResolver().query(
				ConProvider.CONTENT_URI_NOTIFICATION, null, null, null,
				ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " desc");
		myCursorAdapter.changeCursor(cursor);
	}

	public class MyCursorAdapter extends CursorAdapter {
		private LayoutInflater cursorInflater;

		// Default constructor
		public MyCursorAdapter(Context context, Cursor cursor, int flags) {
			super(context, cursor, flags);
			cursorInflater = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		public void bindView(View view, final Context context, Cursor cursor) {
//			TextView textViewTitle = (TextView) view.findViewById(R.id.articleTitle);
//			String title = cursor.getString( cursor.getColumnIndex( MyTable.COLUMN_TITLE ) );
//			textViewTitle.setText(title);

			System.out.println("VAIBHAV IN NOTIF ACTIVITY bindView() 1 : " + view + " : " + view.getId());
			String _id = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_ID));
//			if(!_id.equals(mExpandedRowId)/* && (view.getId() ==  R.id.notif_row)*/) {
				ImageView icon = (AspectRatioImageView) view.findViewById(R.id.row_icon);
				String iconURL = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_IMAGE_URL));
				if(TextUtils.isEmpty(iconURL)){
					icon.setVisibility(View.GONE);
				} else {
					mImageLoaderNotification.displayImage(iconURL, icon, mDisplayImageOptionsNotification, null);
				}

			final String notificationID = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID));

				TextView textViewTimeStamp = (TextView) view.findViewById(R.id.row_datetime);
				String dateTime = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DATE_TIME));
				textViewTimeStamp.setText(dateTime);

				TextView textViewTitle = (TextView) view.findViewById(R.id.row_title);
				String title = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION));
				textViewTitle.setText(title);

				TextView textViewDesc = (TextView) view.findViewById(R.id.row_no_user);
				String desc = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC));
				textViewDesc.setText(desc);

				ImageView newicon = (ImageView) view.findViewById(R.id.new_icon);
				int isNew = cursor.getInt(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG));

				System.out.println("VAIBHAV IN NOTIF ACTIVITY bindView() 2 : " + isNew);
				if (isNew == QuopnConstants.BOOL_FALSE) {
					newicon.setVisibility(View.GONE);
				} else {
					newicon.setVisibility(View.VISIBLE);
				}
				if(TextUtils.isEmpty(iconURL)){
					newicon.setVisibility(View.GONE);
				}

//			} else if(_id.equals(mExpandedRowId)/* && (view.getId() ==  R.id.notif_row_expanded)*/){
				ImageView iconBig = (AspectRatioImageView) view.findViewById(R.id.image);
				String iconBigURL = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_URL));
				if(TextUtils.isEmpty(iconBigURL)){
					iconBig.setVisibility(View.GONE);
				} else {
					mImageLoaderNotification.displayImage(iconBigURL, iconBig, mDisplayImageOptionsNotification, null);
					TextView textViewDynamic = (TextView) view.findViewById(R.id.text_dynamic);
					String titledynamic = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_BIG_IMAGE_VALUE));
					Typeface plain = Typeface.createFromAsset(getAssets(), "fonts/open-sans.bold.ttf");
					textViewDynamic.setTypeface(plain,Typeface.BOLD);
					textViewDynamic.setText(titledynamic);
				}

				TextView textViewBigTitle = (TextView) view.findViewById(R.id.text1);
				String titleBig = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION));
				textViewBigTitle.setText(titleBig);

				TextView textViewBigDesc = (TextView) view.findViewById(R.id.text2);
				String descBig = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_DESC));
				textViewBigDesc.setText(descBig);

//				final String campaignId = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAMPAIGN_ID));
				final String screen = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN));
				final String value = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_SCREEN_VALUE));
				final String caption = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_CAPTION));
			    final String typeId = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_TYPE_ID));
				System.out.println("VAIBHAV IN NOTIF ACTIVITY bindView() 3 : " + screen + " : " + value + " : " + caption);
				TextView textViewButton = (TextView) view.findViewById(R.id.view_quopns_txt);
				textViewButton.setText(caption);
				if(screen!=null){
					if(screen.equals(QuopnConstants.GCM_DEEP_LINK.campaign)) {
						textViewButton.setVisibility(View.VISIBLE);
						if(TextUtils.isEmpty(value)) {
							textViewButton.setVisibility(View.GONE);
						} else {
							Cursor cursorCampaign = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_QUOPN, new String[]{ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID}, ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ", new String[]{value}, null);
							Cursor cursor_gift = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_GIFTS, null, ITableData.TABLE_GIFTS.COLUMN_GIFT_ID + " = ? ", new String[]{value}, null);
							if (cursorCampaign.getCount() == 0 && cursor_gift.getCount() == 0) {
								cursorCampaign.moveToFirst();
								textViewButton.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										sendNotificationForNotID(notificationID,screen);
										Dialog dialog = new Dialog(NotificationActivity.this, R.string.dialog_title_error, R.string.dialog_message_campaignid_not_present);
										dialog.setOnAcceptButtonClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {

												PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, 0); //reset the notification counter
												sendBroadCast(context);
												//										alertDialog.dismiss();
											}
										});
										dialog.show();
									}
								});

							} else if (cursorCampaign.getCount() > 0) {
								cursorCampaign.moveToFirst();
								textViewButton.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										sendNotificationForNotID(notificationID,screen);
										Intent quopIntent = new Intent(getApplicationContext(), QuopnDetailsActivity.class);
										quopIntent.putExtra("tag", value);
										QuopnDetailsActivity.launch((Activity) context, quopIntent, null, /*text1,text2,*/null, false);
										//								alertDialog.dismiss();
										finish();
									}
								});
							} else if (cursor_gift.getCount() > 0) {
								cursor_gift.moveToFirst();
								final String id = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.COLUMN_GIFT_ID));
								final String giftscamapaignname = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CAMPAIGN_NAME));
								final String giftsbigImage = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.BIG_IMAGE));
								final String giftslongdesc = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.LONG_DESC));
								final String giftstermsncondition = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.TERMS_COND));
								final String giftsctatext = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CTA_TEXT));
								final String giftsctavalue = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CTA_VALUE));
								final String giftssource = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CALL_TO_ACTION));
								final String giftsmastertag = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.MASTER_TAG_IMAGE));
								final String productname = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.PRODUCT_NAME));
								final String gifttype = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.GIFT_TYPE));
								final String termscondition = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.TERMS_COND));
								final String giftpartnercode = cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.PARTNER_CODE));
								textViewButton.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										sendNotificationForNotID(notificationID,screen);
										Intent quopIntent = new Intent(NotificationActivity.this, GiftDetailsActivity.class);
										quopIntent.putExtra("id", id);
										quopIntent.putExtra("giftscomapignname", giftscamapaignname);
										quopIntent.putExtra("giftsbigImage", giftsbigImage);
										quopIntent.putExtra("giftslongdesc", giftslongdesc);
										quopIntent.putExtra("giftstermsncondition", giftstermsncondition);
										quopIntent.putExtra("giftsctatext", giftsctatext);
										quopIntent.putExtra("giftsctavalue", giftsctavalue);
										quopIntent.putExtra("giftssource", giftssource);
										quopIntent.putExtra("giftsmastertag", giftsmastertag);
										quopIntent.putExtra("productname", productname);
										quopIntent.putExtra("gifttype", gifttype);
										quopIntent.putExtra("termscondition", termscondition);
										if (gifttype.equals("E")) {
											quopIntent.putExtra("giftpartnercode", giftpartnercode);
										} else {
											quopIntent.putExtra("giftpartnercode", "");
										}

										GiftDetailsActivity.launch((Activity) context, quopIntent, null, /*text1,*/null, false);
										//								alertDialog.dismiss();
									}
								});
							}
						}
					}else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.promo)){
						textViewButton.setVisibility(View.VISIBLE);
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								sendNotificationForNotID(notificationID,screen);
								Intent promoIntent = new Intent(getApplicationContext(),PromoCodeActivity.class);
								promoIntent.putExtra(QuopnConstants.INTENT_KEYS.promo, value);
								startActivity(promoIntent);
								finish();
	//							alertDialog.dismiss();
							}
						});
					} else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.invite)){
						textViewButton.setVisibility(View.VISIBLE);
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								sendNotificationForNotID(notificationID, screen);
								Intent inviteUser = new Intent(getApplicationContext(), InviteUserActivity.class);
								startActivityForResult(inviteUser, QuopnConstants.HOME_PRESS);
								finish();
							}
						});
					} else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.category)){
						if(TextUtils.isEmpty(value)) {
							textViewButton.setVisibility(View.GONE);
						}
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if(!TextUtils.isEmpty(value)) {
									sendNotificationForNotID(notificationID, screen);
									ProductCatFragment.QUOPN_CATEGORY_ID = value;
									Cursor cursor = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_CATEGORY, new String[]{ITableData.TABLE_CATEGORY.COLUMN_CATEGORY}, ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID + " = ? ", new String[]{value}, null);
									if (cursor.getCount() > 0) {
										cursor.moveToFirst();
										String categoryName = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY));
										ProductCatFragment.QUOPN_CATEGORY_TYPE = categoryName;
										Intent listingIntent = new Intent(context, ListingByCategoryActivity.class);
										listingIntent.putExtra("category", value);
										listingIntent.putExtra("categoryname", categoryName);
										context.startActivity(listingIntent);
									} else {
										Toast.makeText(context, "Category does not exist", Toast.LENGTH_SHORT).show();
									}
									finish();
								}
							}
						});
					} else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.allquopns)){
						textViewButton.setVisibility(View.VISIBLE);
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								sendNotificationForNotID(notificationID, screen);
								Intent intent = new Intent(QuopnConstants.BROADCAST_SHOW_TAB);
								intent.putExtra("value", value);
								LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
								finish();
							}
						});
					} else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.apptour)){
						textViewButton.setVisibility(View.VISIBLE);
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								/*Intent intent = new Intent(QuopnConstants.BROADCAST_SHOW_GIFT_TAB);//apptour
								LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
								sendNotificationForNotID(notificationID, screen);
								LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
								Intent intent = new Intent(QuopnConstants.BROADCAST_PARSE_NOTIF_ACTIVITY_DEEP_LINKS);
								intent.putExtra(QuopnConstants.NOTIF_INTENT_DATA.screen, screen);
								intent.putExtra(QuopnConstants.NOTIF_INTENT_DATA.value, value);
								localBrdcastMgr.sendBroadcast(intent);
								finish();
							}
						});
					} else if(screen.equals(QuopnConstants.GCM_DEEP_LINK.wallet)){
						textViewButton.setVisibility(View.VISIBLE);
						textViewButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								/*Intent intent = new Intent(QuopnConstants.BROADCAST_SHOW_CART);//wallet
								LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
								sendNotificationForNotID(notificationID,screen);
								LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
								Intent intent = new Intent(QuopnConstants.BROADCAST_PARSE_NOTIF_ACTIVITY_DEEP_LINKS);
								intent.putExtra(QuopnConstants.NOTIF_INTENT_DATA.screen, screen);
								intent.putExtra(QuopnConstants.NOTIF_INTENT_DATA.value, value);
								localBrdcastMgr.sendBroadcast(intent);
								finish();
							}
						});
					}else{
						textViewButton.setVisibility(View.GONE);
						textViewButton.setOnClickListener(null);
					}

				}
			if(typeId.equals(QuopnConstants.NOTIFICATION_ID.TEXT) || TextUtils.isEmpty(caption)){
				textViewButton.setVisibility(View.GONE);
			}
			RelativeLayout rlCollapsed = (RelativeLayout) view.findViewById(R.id.notif_row);
//			rlCollapsed.setOnClickListener(NotificationActivity.this);
			RelativeLayout rlExpanded = (RelativeLayout) view.findViewById(R.id.notif_row_expanded);
//			rlExpanded.setOnClickListener(NotificationActivity.this);
//			if(!TextUtils.isEmpty(mExpandedRowId)) {
				if (_id.equals(mExpandedRowId)) {
					rlCollapsed.setVisibility(View.GONE);
					rlExpanded.setVisibility(View.VISIBLE);
				} else {
					rlCollapsed.setVisibility(View.VISIBLE);
					rlExpanded.setVisibility(View.GONE);
				}
//			}
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View v = cursorInflater.inflate(R.layout.notification_row, parent, false);
			return v;
		}

		@Override
		public int getViewTypeCount() {
			return getCount();
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}
	}

	/*public interface NOTIFICATION_SCREEN{
		final public String CAMPAIGN = "campaign";
		final public String PROMO = "promo";
		final public String INVITE = "invite";
		final public String CATEGORY = "category";
		final public String LISTING = "listing";
		final public String GIFTS = "gifts";
		final public String CART = "cart";
	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//not deleted notification database because maximum notification in the lists are least 20 or last 3 days notification as per new design
//		getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, null,null);

		/*if (CREATE_PARENT) {
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (!NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
		}*/
//		updateNewBandImage();

	}

	private static void sendBroadCast(Context context){
		Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_NOTIFICATIONCOUNTER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View argView, int argPosition,
			long id) {
//		popInstructionsDialog((SampleItem)adapter.getItemAtPosition(position),NotificationActivity.this, adapter, view, position);

		//TODO - Clear the New Icon if any
		/*mAdapter.getItem(position).isNew = QuopnConstants.BOOL_FALSE;
		mAdapter.notifyDataSetChanged();*/
        String notification_id = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NOTIFICATION_ID));
        mAnalysisManager.send(AnalysisEvents.NOTIFICATION_ID, notification_id);

		cursor.moveToPosition(argPosition);
		String _id = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_ID));
		int isNew = cursor.getInt(cursor.getColumnIndex(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG));

		if(_id.equals(mExpandedRowId)){
			mExpandedRowId = "";
		} else{
			mExpandedRowId = _id;
		}

		RelativeLayout rlCollapsed = (RelativeLayout) argView.findViewById(R.id.notif_row);
		RelativeLayout rlExpanded = (RelativeLayout) argView.findViewById(R.id.notif_row_expanded);
		System.out.println("VAIBHAV IN NOTIF ACTIVITY onItemClick() 1 : " + _id + " : " + mExpandedRowId);
//		if(!TextUtils.isEmpty(mExpandedRowId)) {
			if (_id.equals(mExpandedRowId)) {
				rlCollapsed.setVisibility(View.GONE);
				rlExpanded.setVisibility(View.VISIBLE);

			} else {
				rlCollapsed.setVisibility(View.VISIBLE);
				rlExpanded.setVisibility(View.GONE);
			}
//		}

		System.out.println("VAIBHAV IN NOTIF ACTIVITY onItemClick() 2 : " + _id + " : " + isNew);
		if(isNew == QuopnConstants.BOOL_TRUE) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG, QuopnConstants.BOOL_FALSE);
			int rowsUpdated = getContentResolver().update(ConProvider.CONTENT_URI_NOTIFICATION, contentValues, ITableData.TABLE_NOTIFICATIONS.COLUMN_ID + " = ? ", new String[]{_id});
			System.out.println("VAIBHAV IN NOTIF ACTIVITY onItemClick() 3 : " + rowsUpdated);
			refreshCursorAdapter();
		}
		myCursorAdapter.notifyDataSetChanged();
	}

//	 AlertDialog.Builder builder;
//	 AlertDialog alertDialog;
//	@SuppressLint("InflateParams")
//	public void popInstructionsDialog(final SampleItem sampleItem,final Context context, final AdapterView<?> argParent, final View argView, final int argPosition){
//
//	    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View layout=null;
//	    if(layout==null){
//	    	layout = inflater.inflate(R.layout.notification_detail_popup, null);
//	    }
//
//	    final TextView text1 = (TextView) layout.findViewById(R.id.text1);
//	    text1.setText(sampleItem.tag);
//
//	    AspectRatioImageView image = (AspectRatioImageView) layout.findViewById(R.id.image);
//
//	    if(null != sampleItem.iconRes && !TextUtils.isEmpty(sampleItem.iconRes)){
//	    mImageLoaderNotification.displayImage(
//	    		sampleItem.iconRes, image,
//				mDisplayImageOptionsNotification, null);
//	    }else{
//
//	    }
//
//	    final TextView text2 = (TextView) layout.findViewById(R.id.text2);
//	    text2.setText(sampleItem.desc);
//
//	    TextView tvViewQuopns = (TextView) layout.findViewById(R.id.view_quopns_txt);
//	    if(sampleItem.campaign_id!=null && !sampleItem.campaign_id.matches("")){
//            tvViewQuopns.setText(R.string.view_quopns);
//	    	tvViewQuopns.setVisibility(View.VISIBLE);
//	    	Cursor cursor = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_QUOPN,new String[]{ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID},ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",	new String[] { sampleItem.campaign_id },null);
//			Cursor cursor_gift = getApplicationContext().getContentResolver().query(ConProvider.CONTENT_URI_GIFTS,null,ITableData.TABLE_GIFTS.COLUMN_GIFT_ID + " = ? ",	new String[] { sampleItem.campaign_id },null);
//			if(cursor.getCount()==0 && cursor_gift.getCount()==0){
//				cursor.moveToFirst();
//				 tvViewQuopns.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Dialog dialog=new Dialog(NotificationActivity.this, R.string.dialog_title_error, R.string.dialog_message_campaignid_not_present);
//						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								PreferenceUtil.getInstance(getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTIFICATIONCOUNT, 0); //reset the notification counter
//								sendBroadCast(context);
//								alertDialog.dismiss();
//							}
//						});
//						dialog.show();
//					}
//				});
//
//			}else if(cursor.getCount()>0){
//				cursor.moveToFirst();
//		 	    tvViewQuopns.setOnClickListener(new OnClickListener() {
//
//		 			@Override
//		 			public void onClick(View v) {
//		 				Intent quopIntent = new Intent(getApplicationContext(),QuopnDetailsActivity.class);
//	 					quopIntent.putExtra("tag", sampleItem.campaign_id);
//	 					QuopnDetailsActivity.launch((Activity) context,quopIntent, null, /*text1,text2,*/null,false);
//		 				alertDialog.dismiss();
//		 			}
//		 		});
//			}else if(cursor_gift.getCount()>0){
//				cursor_gift.moveToFirst();
//				final String id=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.COLUMN_GIFT_ID));
//				final String giftscamapaignname=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CAMPAIGN_NAME));
//				final String giftsbigImage=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.BIG_IMAGE));
//				final String giftslongdesc=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.LONG_DESC));
//				final String giftstermsncondition=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.TERMS_COND));
//				final String giftsctatext=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CTA_TEXT));
//				final String giftsctavalue=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CTA_VALUE));
//				final String giftssource=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.CALL_TO_ACTION));
//				final String giftsmastertag=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.MASTER_TAG_IMAGE));
//				final String productname=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.PRODUCT_NAME));
//				final String gifttype=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.GIFT_TYPE));
//				final String termscondition=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.TERMS_COND));
//				final String giftpartnercode=cursor_gift.getString(cursor_gift.getColumnIndex(ITableData.TABLE_GIFTS.PARTNER_CODE));
//		 	    tvViewQuopns.setOnClickListener(new OnClickListener() {
//
//		 			@Override
//		 			public void onClick(View v) {
//		 				Intent quopIntent = new Intent(NotificationActivity.this,GiftDetailsActivity.class);
//						quopIntent.putExtra("id", id);
//						quopIntent.putExtra("giftscomapignname", giftscamapaignname);
//						quopIntent.putExtra("giftsbigImage", giftsbigImage);
//						quopIntent.putExtra("giftslongdesc", giftslongdesc);
//						quopIntent.putExtra("giftstermsncondition", giftstermsncondition);
//						quopIntent.putExtra("giftsctatext", giftsctatext);
//						quopIntent.putExtra("giftsctavalue", giftsctavalue);
//						quopIntent.putExtra("giftssource", giftssource);
//						quopIntent.putExtra("giftsmastertag", giftsmastertag);
//						quopIntent.putExtra("productname", productname);
//						quopIntent.putExtra("gifttype", gifttype);
//						quopIntent.putExtra("termscondition", termscondition);
//						if(gifttype.equals("E")){
//							quopIntent.putExtra("giftpartnercode", giftpartnercode);
//						}else{
//							quopIntent.putExtra("giftpartnercode", "");
//						}
//
//						GiftDetailsActivity.launch((Activity) context,quopIntent, null, /*text1,*/null,false);
//						alertDialog.dismiss();
//		 			}
//		 		});
//			}
//
//
//	    }else if(sampleItem.promo!=null && !sampleItem.promo.matches("")){
//            tvViewQuopns.setText(R.string.apply_promo);
//            tvViewQuopns.setVisibility(View.VISIBLE);
//            tvViewQuopns.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent promoIntent = new Intent(getApplicationContext(),PromoCodeActivity.class);
//                    promoIntent.putExtra(QuopnConstants.INTENT_KEYS.promo, sampleItem.promo);
//                    startActivity(promoIntent);
//                    alertDialog.dismiss();
//                }
//            });
//        }else{
//	    	tvViewQuopns.setVisibility(View.GONE);
//	    	tvViewQuopns.setOnClickListener(null);
//	    }
//
//
//	    builder = new AlertDialog.Builder(this);
//	    builder.setView(layout);
//	    alertDialog = builder.create();
//	    alertDialog.setCanceledOnTouchOutside(true);
//	    alertDialog.show();
//
//
//	}

	@Override
	public void onResponse(int responseResult, Response response) {
		mCustomProgressDialog.dismiss();
		switch (responseResult) {
		case RESPONSE_OK:
			if (response instanceof NotifyStatusData) {
				NotifyStatusData notifyStatusData = (NotifyStatusData) response;
				if (notifyStatusData.isError()) {
					IS_TUT_API_SWITCHED = true;
//					updateView();
					Dialog dialog=new Dialog(this, R.string.dialog_title_error,notifyStatusData.getMessage());
					dialog.show();

				} else {
					IS_TUT_API_SWITCHED = false;
					if (notifyStatusData.getStatus() != null
							&& notifyStatusData.getStatus().equals(NOTIFY_ON)) {
//						setOnTutorialSetting();
					} else {
//						setOffTutorialSetting();
					}
					Dialog dialog=new Dialog(this, R.string.dialog_title_success,notifyStatusData.getMessage());
					dialog.show();
					setSwitchMode(notifyStatusData.getStatus());
				}

			}
			break;

		case PARSE_ERR0R:
			break;

		case CONNECTION_ERROR:
			break;

		default:
			break;
		}
	}

//	private void setOnTutorialSetting() {
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_DETAILS,
//				QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_LISTING,
//				QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_MYQUOPNS,
//				QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_GIFTING,
//				QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_ON);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.PREF_ALL_TUTS_SEEN, "N");
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.PREF_ALL_TUTS_COUNT, "0");
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_GiftTutShown, false);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_CatTutShown, false);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_TutShown, false);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_ANY_TUT_ON, false);
//	}
//
//	private void setOffTutorialSetting() {
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_DETAILS,
//				QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_LISTING,
//				QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_MYQUOPNS,
//				QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_GIFTING,
//				QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_OFF);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				QuopnConstants.PREF_ALL_TUTS_SEEN, "Y");
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_GiftTutShown, true);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_CatTutShown, true);
//		PreferenceUtil.getInstance(getApplicationContext()).setPreference(
//				PreferenceUtil.SHARED_PREF_KEYS.IS_TutShown, true);
//	}
//
//	private void updateNewBandImage(){
//		//Update all Notifications as Read
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(ITableData.TABLE_NOTIFICATIONS.COLUMN_NEW_FLAG, QuopnConstants.BOOL_FALSE);
//		getContentResolver().update(ConProvider.CONTENT_URI_NOTIFICATION,contentValues, null,null);
//	}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
					mCustomProgressDialog.dismiss();
					Dialog dialog = new Dialog(NotificationActivity.this, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
					dialog.show();
				}


			}
		});


	}

	@Override
	public void myTimeout(String requestTag) {

	}

	private void sendNotificationForNotID(String notificationID, String screenName) {
		if (notificationID != null) {
			mAnalysisManager.send(AnalysisEvents.NOTIFICATION_CALL_TO_ACTION, notificationID+"||"+screenName);
		} else {
			mAnalysisManager.send(AnalysisEvents.NOTIFICATION_CALL_TO_ACTION, "||"+screenName);
		}
	}
}
