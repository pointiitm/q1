package com.quopn.wallet.fragments;

/**
 * @author Sumeet
 *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.gc.materialdesign.widgets.Dialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quopn.wallet.AboutUsActivity;
import com.quopn.wallet.FAQActivity;
import com.quopn.wallet.FeedBackActivity;
import com.quopn.wallet.HistoryActivity;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.NotificationActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.QuopnApplication.TrackerName;
import com.quopn.wallet.R;
import com.quopn.wallet.RegistrationScreen;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.analysis.scheduler.SchedulingService;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.model.GeneratePinData;
import com.quopn.wallet.data.model.LogoutData;
import com.quopn.wallet.data.model.NotifyStatusData;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.RequestPinData;
import com.quopn.wallet.data.model.ShmartCheckStatusData;
import com.quopn.wallet.data.model.ShmartGenerateOTPData;
import com.quopn.wallet.data.model.ShmartRequestOTPData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.data.model.citrus.CitrusWalletListData;
import com.quopn.wallet.data.model.citrus.WalletListData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.QuopnTextView;
import com.quopn.wallet.walletshmart.ShmartOtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainMenuFragment extends BaseFragment implements
		OnItemClickListener, OnCheckedChangeListener, ConnectionListener {

	private TypedArray mIcons = null;

	private String[] mName = null;
	private TypedArray mIDs = null;

	private ListView mMainMenuList;
	private ConnectionListener mConnectionListener_cart;
	private ConnectionListener mConnectionListener_myqoupn;
	private String TAG = "Quopn/MainMenuFragment";
	SlidingMenu mSlidingMenu;

	public static String TUTORIAL_USER_STATUS = "tutorial_user_status";
	private final String NOTITY_OFF = "0";
	private final String NOTITY_ON = "1";
	private String NOTITY_STATUS = "";
	private boolean IS_TUT_API_SWITCHED = false;
	private int temp_tut_position;

	private SampleAdapter adapter;
	private String invitCount = "0";
	private AnalysisManager mAnalysisManager;
	private Tracker googleAnalyticTracker;

	private Switch notification_switch;
	private ImageView imgProfilePic;
	final Gson gson = new GsonBuilder().serializeNulls().create();
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	private MainActivity mMainActivity;
    String requestpin;
    private ImageView progressBar;
	private boolean isMultiWalletsEnabled = false;
	private boolean isWalletClicked = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMainActivity = (MainActivity) activity;
	}

	@Override
	public void onPause() {
		super.onPause();
//		new AnalysisManager(mMainActivity).close();
//		mAnalysisManager.close();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mName = getResources().getStringArray(R.array.main_menu_items);
		mIDs = getResources().obtainTypedArray(R.array.menu_ids);
		mIcons = getResources().obtainTypedArray(R.array.menu_icons);
		Log.d(TAG, "there are " + mIDs.length() + " ids");

		View rootView = inflater.inflate(R.layout.mainmenu, null);
		mMainMenuList = (ListView) rootView.findViewById(R.id.main_menu_list);
		mMainMenuList.setOnItemClickListener(this);
        progressBar = (ImageView) rootView.findViewById(R.id.progressBar_mainmenu);
        AnimationDrawable animation = (AnimationDrawable) progressBar.getDrawable();
        animation.start();
        mAnalysisManager = ((QuopnApplication) mMainActivity.getApplicationContext()).getAnalysisManager();
		googleAnalyticTracker = ((QuopnApplication) mMainActivity
				.getApplication()).getTracker(TrackerName.APP_TRACKER);

		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(mMainActivity));

		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(0))
				.build();
		return rootView;
	}

	@SuppressLint("NewApi")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SampleAdapter(mMainActivity);

		invitCount = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.INVITE_COUNT);
		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(mMainActivity).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT);
		QuopnConstants.NOTIFICATION_COUNT = PreferenceUtil.getInstance(mMainActivity).getPreference_int(SHARED_PREF_KEYS.NOTIFICATIONCOUNT);

		for (int i = 0; i < mIDs.length(); i++) {
			adapter.add(new SampleItem(mIDs.getResourceId(i, 0), mName[i], mIcons.getResourceId(i, 0)));
		}

		{
			String mobileWallets = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.MOBILE_WALLETS_KEY);
			isMultiWalletsEnabled = false;
			if (!mobileWallets.isEmpty()) {
				if (mobileWallets.equalsIgnoreCase("1") || mobileWallets.equalsIgnoreCase("2|1") || mobileWallets.equalsIgnoreCase("1|2")) {
					isMultiWalletsEnabled = true;
				}
			}
		}
		{
			isWalletClicked = false;
			adapter.hiddenPositions.clear();
			adapter.hiddenPositions.add(2);
			adapter.hiddenPositions.add(3);
		}

		mMainMenuList.setAdapter(adapter);
	}

	@SuppressLint("NewApi")
	public void initSlideMenuHeader() {
		imgProfilePic = (ImageView) mMainActivity.findViewById(R.id.slidemenu_profile_img);
		setImage();
		String userWalletId = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.WALLET_ID_KEY);
		QuopnTextView walletId = (QuopnTextView) mMainActivity.findViewById(R.id.slidemenu_wallet_id);
		walletId.setText(getResources().getString(R.string.wallet_id) + " " + userWalletId);
		walletId.setTypeface(null, Typeface.BOLD);

		if (requestpin != null) {
			String userPinNo = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.PIN_KEY);
			requestpin = userPinNo;
			QuopnTextView pinNo = (QuopnTextView) mMainActivity.findViewById(R.id.slidemenu_pin_no);
			pinNo.setText(getResources().getString(R.string.pin_no) + " " + requestpin);
			pinNo.setTypeface(null, Typeface.BOLD);
		} else {
			String userPinNo = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.PIN_KEY);
			QuopnTextView pinNo = (QuopnTextView) mMainActivity.findViewById(R.id.slidemenu_pin_no);
			pinNo.setText(getResources().getString(R.string.pin_no) + " " + userPinNo);
			pinNo.setTypeface(null, Typeface.BOLD);
		}

	}

    private void callRequestPin() {

        if (QuopnUtils.isInternetAvailable(mMainActivity)) {
            //mCustomProgressDialog.show();
            Map<String, String> params = new HashMap<String, String>();
            params.put("walletid",PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.WALLET_ID_KEY));

            ConnectionFactory connectionFactory = new ConnectionFactory(mMainActivity, this);
            connectionFactory.setPostParams(params);
            connectionFactory.createConnection(QuopnConstants.REQUEST_PIN_CODE);
        } else {
            //Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            // dialog.show();
        }
    }

	public boolean isInternetAvailable(){
		return QuopnUtils.isInternetAvailable(mMainActivity);
	}

	private void loadImageFromStorage(String path)
	{

	    try {
	        File f=new File(path, QuopnConstants.PROFILE_IMG_NAME);
	        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
	        imgProfilePic.setImageBitmap(b);
	    }
	    catch (FileNotFoundException e)
	    {
	        e.printStackTrace();
	    }

	}

	private void setImage(){


		if (TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
			QuopnConstants.PROFILE_DATA = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.PROFILE_DATA);
		}
		ProfileData response = (ProfileData) gson.fromJson(
				QuopnConstants.PROFILE_DATA, ProfileData.class);
		String imageUrl =response.getUser().getPic();
		String imagePath = PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.PROFILE_IMAGE_PATH);
		if(imageUrl != null || !TextUtils.isEmpty(imageUrl) && QuopnUtils.isInternetAvailable(mMainActivity)){
			mImageLoader.displayImage(imageUrl,imgProfilePic, mDisplayImageOptions, null);
		} else if(imagePath != null){
			loadImageFromStorage(imagePath);//LOCAL IMAGE FROM INTERNAL STORAGE
		}

	}

	private void CheckCitrusWalletList(){
		if (QuopnUtils.isInternetAvailable(mMainActivity)) {
			progressBar.setVisibility(View.VISIBLE);
			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "2");
			ConnectionFactory connectionFactory = new ConnectionFactory(mMainActivity, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.QUOPN_MOBILE_CITRUS_LIST_WALLET);
		} else {
			Dialog dialog=new Dialog(mMainActivity, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initSlideMenuHeader();
//        callRequestPin();
        mMainMenuList.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
	}

	private class SampleItem {
		public int id;
		public String tag;
		public int iconRes;

		public SampleItem(int id, String tag, int iconRes) {
			this.id = id;
			this.tag = tag;
			if (tag.equals("")) { tag = "  "; }
			this.iconRes = iconRes;
		}
	}

	TextView invite_no_user;

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		ArrayList<Integer> hiddenPositions = new ArrayList<>();

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		@SuppressLint("ResourceAsColor")
		public View getView(int position, View convertView, ViewGroup parent) {

			// Skipping hidden rows of citrus and shmart
			for(Integer hiddenIndex : hiddenPositions) {
				if(hiddenIndex <= position) {
					position = position + 1;
				}
			}

			int menuID = mIDs.getResourceId(position, 0);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row_menu, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			if (getItem(position).iconRes > 0) {
				icon.setImageResource(getItem(position).iconRes);
			} else {
				icon.setImageBitmap(null);
			}
			final TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			final TextView btn_Default = (TextView) convertView.findViewById(R.id.btn_default);

			btn_Default.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					//btn_Default.requestFocus();
					//Validations.CustomErrorMessage(mMainActivity.getApplicationContext(), R.string.citrus_wallet_default_message, null, btn_Default, 3);
//					btn_Default.setHint(R.string.citrus_wallet_default_message);
					Dialog dialog = new Dialog(mMainActivity,R.string.dialog_title, R.string.citrus_wallet_default_message);
					dialog.show();
				}
			});

			invite_no_user = (TextView) convertView
					.findViewById(R.id.row_no_user);
			notification_switch = (Switch) convertView
					.findViewById(R.id.tut_switch);
			LinearLayout root = (LinearLayout) convertView
					.findViewById(R.id.root);

//			LinearLayout parentLayout = (LinearLayout) convertView
//					.findViewById(R.id.parentLayout);
			title.setTextColor(Color.BLACK);
//			if (hiddenPositions.size() == 0) {
//				// Back strip colour- #e9e5e5
//				if (menuID == R.id.menu_item_shmart1) {
//					title.setTextColor(Color.BLACK);
//				} else if (menuID == R.id.menu_item_citrus) {
//					title.setTextColor(Color.BLACK);
//
//				}
//			}

			btn_Default.setVisibility(View.GONE);

//			Logger.d("M:%d Size:%d\nmenuID:%d defWallet:%d", (isMultiWalletsEnabled ? 1 : 0), hiddenPositions.size(), menuID, QuopnApplication.getInstance().getDefaultWallet().ordinal());
			if (menuID == R.id.menu_item_seperator) {
				root.setBackgroundColor(Color.parseColor("#b2b2b2"));
				icon.setVisibility(View.INVISIBLE);
			} else {
				root.setBackgroundResource(R.drawable.slidemenu_bg);
				icon.setVisibility(View.VISIBLE);
			}

			if (isMultiWalletsEnabled && hiddenPositions.size() == 0) {
				QuopnConstants.WalletType defWallet = QuopnApplication.getInstance().getDefaultWallet();
//				Logger.d("M:%d Size:%d\nmenuID:%d defWallet:%d",isMultiWalletsEnabled, hiddenPositions.size(), menuID, defWallet.ordinal());
				if (menuID == R.id.menu_item_shmart1 || menuID == R.id.menu_item_citrus) {
					root.setBackgroundResource(R.drawable.backstrip_wallet);
				}
				if (menuID == R.id.menu_item_shmart1 && defWallet == QuopnConstants.WalletType.SHMART) {
					btn_Default.setVisibility(View.VISIBLE);
					//root.setBackgroundResource(R.drawable.backstrip_wallet);
				} else if (menuID == R.id.menu_item_citrus && defWallet == QuopnConstants.WalletType.CITRUS) {
					btn_Default.setVisibility(View.VISIBLE);
//					root.setBackgroundResource(R.drawable.backstrip_wallet);
				}
			}

			if (menuID == R.id.menu_item_cart) {

				switch (menuID) {
					case R.id.menu_item_cart:
						invite_no_user.setText("" + (QuopnConstants.MY_CART_COUNT));
						if (QuopnConstants.MY_CART_COUNT <= 0) {
							invite_no_user.setVisibility(View.INVISIBLE);
						} else {
							invite_no_user.setVisibility(View.VISIBLE);
						}
						notification_switch.setVisibility(View.GONE);
						break;
					default:
						break;
				}
			} else if (menuID == R.id.menu_item_notifications) {
				invite_no_user.setText("" + (QuopnConstants.NOTIFICATION_COUNT));
				if (QuopnConstants.NOTIFICATION_COUNT <= 0) {
					invite_no_user.setVisibility(View.INVISIBLE);
				} else {
					invite_no_user.setVisibility(View.VISIBLE);
				}
				notification_switch.setVisibility(View.GONE);
			} else {
				invite_no_user.setVisibility(View.GONE);
				notification_switch.setVisibility(View.GONE);
			}

			notification_switch.setOnCheckedChangeListener(MainMenuFragment.this);

//			if (menuID == R.id.menu_item_seperator) {
//				root.setBackgroundColor(Color.parseColor("#b2b2b2"));
//				icon.setVisibility(View.INVISIBLE);
//			} else {
//				root.setBackgroundResource(R.drawable.slidemenu_bg);
//				icon.setVisibility(View.VISIBLE);
//			}

			return convertView;
		}

		// hiding citrus and shmart
		@Override
		public int getCount() {
			return mIDs.length() - hiddenPositions.size();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// Skipping hidden rows of citrus and shmart
		for(Integer hiddenIndex : adapter.hiddenPositions) {
			if(hiddenIndex <= position) {
				position = position + 1;
			}
		}

		int menuID = mIDs.getResourceId(position, 0);

		switch (menuID) {
			case R.id.menu_item_profile:
				mAnalysisManager.send(AnalysisEvents.PROFILE);
				// Build and send an Event.
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel("PROFILE").build());
				progressBar.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
					QuopnConstants.PROFILE_DATA = PreferenceUtil.getInstance(
							mMainActivity).getPreference(
							SHARED_PREF_KEYS.PROFILE_DATA);
				}

				if (!TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
					Fragment profileFragment = new ProfileFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.menu_frame, profileFragment).commit();
					((QuopnApplication) mMainActivity.getApplication())
							.getFragmentsStack_menu().add(profileFragment);
				} else {
//				Toast.makeText(
//						mMainActivity,
//						getResources().getString(
//								R.string.please_connect_to_internet),
//						Toast.LENGTH_SHORT).show();
					QuopnUtils.isInternetAvailableAndShowDialog(mMainActivity);
				}

				break;

			case R.id.menu_item_cart:
				mAnalysisManager.send(AnalysisEvents.MYCART);
				mAnalysisManager.send(AnalysisEvents.MYQUOPN);
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel("MYCART").build());

				mMainActivity.toggle();
				mMainActivity.getSlidingMenu().showSecondaryMenu(isAdded());

				break;

			case R.id.menu_item_shops:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					mAnalysisManager.send(AnalysisEvents.SHOPEAROUND);
					mAnalysisManager.send(AnalysisEvents.MYQUOPN);
					googleAnalyticTracker.send(new HitBuilders.EventBuilder()
							.setCategory("Mainmenu").setAction("Click")
							.setLabel("SHOPSAROUND").build());
					//mMainActivity.toggle();
					progressBar.setVisibility(View.VISIBLE);
					mMainActivity.showShopsAround();
//					Intent shopsaroundMap = new Intent(mMainActivity, QuopnStoreList.class);
//					mMainActivity.startActivityForResult(shopsaroundMap,
//							QuopnConstants.HOME_PRESS);
				} else {
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;

			case R.id.menu_item_history:
				mAnalysisManager.send(AnalysisEvents.MYHISTORY);
				mAnalysisManager.send(AnalysisEvents.MYQUOPN);
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel("MYHISTORY").build());
				//mMainActivity.toggle();
				progressBar.setVisibility(View.VISIBLE);
				Intent historyactivity = new Intent(mMainActivity,
						HistoryActivity.class);
				mMainActivity.startActivityForResult(historyactivity,
						QuopnConstants.HOME_PRESS);
				break;

			case R.id.menu_item_change_pin:
				//mMainActivity.toggle();
				//Intent intent = new Intent(mMainActivity, ChangePinActivity.class);
				//mMainActivity.startActivityForResult(intent, QuopnConstants.HOME_PRESS);
				if (!QuopnUtils.isInternetAvailableAndShowDialog(mMainActivity)) {
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				callChangePin();
				break;

			case R.id.menu_item_feedback:
				if (!QuopnUtils.isInternetAvailableAndShowDialog(mMainActivity)) {
					return;
				}
				mAnalysisManager.send(AnalysisEvents.MYQUOPN);
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel("FEEDBACK").build());
				//mMainActivity.toggle();
				progressBar.setVisibility(View.VISIBLE);
				Intent feedIntent = new Intent(mMainActivity,
						FeedBackActivity.class);
				mMainActivity.startActivityForResult(feedIntent,
						QuopnConstants.HOME_PRESS);
				break;

			case R.id.menu_item_tour:
				progressBar.setVisibility(View.VISIBLE);
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel("TOUR").build());
				mMainActivity.showAppTour();
				break;

			case R.id.menu_item_about:
				mAnalysisManager.send(AnalysisEvents.ABOUT);
				googleAnalyticTracker.send(new HitBuilders.EventBuilder()
						.setCategory("Mainmenu").setAction("Click")
						.setLabel(getResources().getString(R.string.aboutus)).build());
				//mMainActivity.toggle();
				progressBar.setVisibility(View.VISIBLE);
				Intent aboutIntent = new Intent(mMainActivity, AboutUsActivity.class);
				aboutIntent.putExtra(QuopnConstants.ABOUT_US_GROUP_TO_EXPAND, -1);
				mMainActivity.startActivityForResult(aboutIntent,
						QuopnConstants.HOME_PRESS);
				break;

			case R.id.menu_item_invite:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					progressBar.setVisibility(View.VISIBLE);
					mMainActivity.showInviteScreen();
				} else {
					progressBar.setVisibility(View.INVISIBLE);
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			case R.id.menu_item_promo:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					progressBar.setVisibility(View.VISIBLE);
					mMainActivity.showPromoScreen("");
				} else {
					progressBar.setVisibility(View.INVISIBLE);
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			case R.id.menu_item_logout:
				try {

					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_Logout, R.string.logout_confirm);
					dialog.addOkButton("YES");
					dialog.addCancelButton("NO");
					dialog.setOnAcceptButtonClickListener(onDialogLogoutConfirmClickListner);
					dialog.show();

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case R.id.menu_item_contact:
				//mMainActivity.toggle();
				progressBar.setVisibility(View.VISIBLE);
				Intent dialIntent = new Intent();
				dialIntent.setAction(Intent.ACTION_DIAL);

				Uri phoneUri = Uri.parse(QuopnConstants.PHONE_PROTO
						+ QuopnConstants.CONTACT_US);
				dialIntent.setData(phoneUri);
				mMainActivity.startActivity(dialIntent);

				break;
			case R.id.menu_item_notifications:
				if (!QuopnUtils.isInternetAvailableAndShowDialog(mMainActivity)) {
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				Intent notification = new Intent(mMainActivity, NotificationActivity.class);
				notification.putExtra("DONOT_CREATE_PARENT", "DUMP");
				mMainActivity.startActivityForResult(notification, QuopnConstants.HOME_PRESS);
				break;
			case R.id.menu_item_faqs:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					mAnalysisManager.send(AnalysisEvents.FAQ);
					googleAnalyticTracker.send(new HitBuilders.EventBuilder()
							.setCategory("Mainmenu").setAction("Click")
							.setLabel("FAQ").build());
					//mMainActivity.toggle();
					//progressBar.setVisibility(View.VISIBLE);
					Intent faqIntent = new Intent(mMainActivity, FAQActivity.class);
					mMainActivity.startActivityForResult(faqIntent, QuopnConstants.HOME_PRESS);
				} else {
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			case R.id.menu_item_mywallet:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					mAnalysisManager.send(AnalysisEvents.SHMART_MENU_CLICKED);
					googleAnalyticTracker.send(new HitBuilders.EventBuilder().setCategory("Mainmenu").setAction("Click").setLabel("FAQ").build());
					if (isMultiWalletsEnabled) {
						isWalletClicked = !isWalletClicked;
						if (isWalletClicked) {
							adapter.hiddenPositions.clear();
						} else {
							adapter.hiddenPositions.clear();
							adapter.hiddenPositions.add(2);
							adapter.hiddenPositions.add(3);
						}
						adapter.notifyDataSetChanged();
					} else {
						callCitrus();
					}
				} else {
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			case R.id.menu_item_shmart1:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					QuopnApplication.getInstance().setCurrentWalletMode(QuopnConstants.WalletType.SHMART);
					mAnalysisManager.send(AnalysisEvents.SHMART_MENU_CLICKED);
					googleAnalyticTracker.send(new HitBuilders.EventBuilder().setCategory("Mainmenu").setAction("Click").setLabel("FAQ").build());
					//progressBar.setVisibility(View.VISIBLE);
					//mMainActivity.toggle();

//			ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
					mMainActivity.sendCheckWalletStatus();
				} else {
					//progressBar.setVisibility(View.INVISIBLE);
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			case R.id.menu_item_citrus:
				if (QuopnUtils.isInternetAvailable(mMainActivity)) {
					mAnalysisManager.send(AnalysisEvents.SHMART_MENU_CLICKED);
					googleAnalyticTracker.send(new HitBuilders.EventBuilder().setCategory("Mainmenu").setAction("Click").setLabel("FAQ").build());
					callCitrus();

				} else {
					//progressBar.setVisibility(View.INVISIBLE);
					Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				}
				break;
			default:
				break;
		}
	}

	/*public void sendCheckWalletStatus(){

		ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
		String argWalletId = profileData.getUser().getWalletid();
		if (QuopnUtils.isInternetAvailable(mMainActivity)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnConstants.CONN_PARAMS.walletId, argWalletId);
			params.put(QuopnConstants.CONN_PARAMS.mobileWalletId, "1");

			ConnectionFactory connectionFactory = new ConnectionFactory(
					mMainActivity, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.SHMART_CHECK_STATUS);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
			Dialog dialog=new Dialog(mMainActivity, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}*/

	private OnClickListener onDialogLogoutConfirmClickListner=new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				Intent service = new Intent(mMainActivity, SchedulingService.class);
				mMainActivity.startService(service);
				logoutApi();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void logoutApi(){
		if (QuopnUtils.isInternetAvailable(mMainActivity)) {
			Map<String, String> headers = new HashMap<String, String>();


			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", PreferenceUtil.getInstance(mMainActivity)
					.getPreference(SHARED_PREF_KEYS.USER_ID));
			params.put("device_id", QuopnConstants.android_id);

			ConnectionFactory connectionFactory = new ConnectionFactory(mMainActivity, this);
			connectionFactory.setHeaderParams(headers);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.LOGOUT_CODE);
			logoutCleanUp();
		} else {
		}
	}

	public void logoutCleanUp(){
		QuopnUtils.citrusLogout(QuopnApplication.getInstance().getApplicationContext());
		QuopnApplication.getInstance().setIsCitrusInitiated(false);
		try {

			String imagePath = PreferenceUtil.getInstance(mMainActivity)
					.getPreference(
							SHARED_PREF_KEYS.PROFILE_IMAGE_PATH);
			File f = new File(imagePath, QuopnConstants.PROFILE_IMG_NAME);
			boolean isDeleted = f.delete();

			QuopnConstants.PROFILE_PIC_DATA = null;
			PreferenceUtil.getInstance(mMainActivity).setPreference(SHARED_PREF_KEYS.API_KEY, "");
			PreferenceUtil.getInstance(mMainActivity).clearPreference();
			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_CATEGORY, null, null);
//			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_CITIES, null, null);
			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, null, null);
			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_GIFTS, null, null);
			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, null, null);
			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART, null, null);
//			mMainActivity.getContentResolver().delete(ConProvider.CONTENT_URI_STATES, null, null);


			//Cancel all existing notifications
			NotificationManager nManager = (NotificationManager) mMainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
			nManager.cancelAll();

//			CitrusClient.getInstance(mMainActivity).signOut(new Callback<CitrusResponse>() {
//				@Override
//				public void error(CitrusError citrusError) {
//					Logger.d("CitrusClient signOut error %s", citrusError.getMessage());
//					Logger.json(citrusError.getTransactionResponse().getJsonResponse());
//				}
//
//				@Override
//				public void success(CitrusResponse citrusResponse) {
//					Logger.d("CitrusClient signOut success");
//				}
//			});


			Log.d("quopn/logout", "from MainMenuFragment");
			Intent intent = new Intent(mMainActivity, RegistrationScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			mMainActivity.finish();
//			startActivity(new Intent(mMainActivity, RegistrationScreen.class));
			}catch (Exception ex){

			}
	}


	private void callChangePin() {

        if (QuopnUtils.isInternetAvailable(mMainActivity)) {
            //mCustomProgressDialog.show();
			ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
			User user = profileData.getUser();

			Map<String, String> headers = new HashMap<String, String>();
			headers.put(QuopnApi.ParamKey.AUTHORIZATION, user.getApi_key());

            Map<String, String> params = new HashMap<String, String>();
            params.put("walletid", PreferenceUtil.getInstance(mMainActivity).getPreference(SHARED_PREF_KEYS.WALLET_ID_KEY));

            ConnectionFactory connectionFactory = new ConnectionFactory(mMainActivity, this);
			connectionFactory.setHeaderParams(headers);
            connectionFactory.setPostParams(params);
            connectionFactory.createConnection(QuopnConstants.GENERATE_PIN_CODE);
        } else {
            Dialog dialog=new Dialog(mMainActivity, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

	public void callCitrus() {
		QuopnApplication.getInstance().setCurrentWalletMode(QuopnConstants.WalletType.CITRUS);
		if (QuopnApplication.getInstance().getIsCitrusInitiated()) {
			// skip initiation
			mMainActivity.walletCitrusStatus();
		} else {
			// api call for getting citrus credentials
			CheckCitrusWalletList();
			return;
		}
	}

	@Override
	public void onBackPressed(FragmentActivity activity) {

	}

	private void setOnTutorialSetting() {
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_DETAILS,
				QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_LISTING,
				QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_MYQUOPNS,
				QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_GIFTING,
				QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.PREF_ALL_TUTS_SEEN, "N");
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.PREF_ALL_TUTS_COUNT, "0");
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_GiftTutShown, false);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_CatTutShown, false);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_TutShown, false);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_ANY_TUT_ON, false);
	}

	private void setOffTutorialSetting() {
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_DETAILS,
				QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_LISTING,
				QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_MYQUOPNS,
				QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_GIFTING,
				QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				QuopnConstants.PREF_ALL_TUTS_SEEN, "Y");
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_GiftTutShown, true);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_CatTutShown, true);
		PreferenceUtil.getInstance(mMainActivity).setPreference(
				SHARED_PREF_KEYS.IS_TutShown, true);
	}

	/*@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		System.out.println("VAIBHAV IN MAIN MENU FRAGMENT onCheckedChanged() : " + isChecked);
		if (isChecked && buttonView.getVisibility() == View.VISIBLE) {
			NOTITY_STAUS = NOTITY_ON;
			setNotificationStatus();
			IS_TUT_API_SWITCHED = false;

		} else if (buttonView.getVisibility() == View.VISIBLE) {
			NOTITY_STAUS = NOTITY_OFF;
			setNotificationStatus();
			IS_TUT_API_SWITCHED = false;
		}

	}*/

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

	private void setNotificationStatus() {
		if (QuopnUtils.isInternetAvailable(mMainActivity)) {
//			mCustomProgressDialog.show();

			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", PreferenceUtil.getInstance(mMainActivity)
					.getPreference(SHARED_PREF_KEYS.USER_ID));
			params.put("status", NOTITY_STATUS);
			ConnectionFactory connectionFactory = new ConnectionFactory(
					mMainActivity, this);
			connectionFactory.setPostParams(params);
			connectionFactory
					.createConnection(QuopnConstants.NOTIFY_STATUS_CODE);
		} else {
			setSwitchMode();
			Dialog dialog=new Dialog(mMainActivity, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}

	private void setSwitchMode() {
		if (notification_switch.isChecked()) {
			notification_switch.setChecked(false);
		} else {
			notification_switch.setChecked(true);
		}
	}

	public void updateCounters() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResponse(int responseResult, Response response) {
		if(progressBar != null) { // ankur
			progressBar.setVisibility(View.INVISIBLE);
		}
		switch (responseResult) {
			case RESPONSE_OK:
				if (response instanceof NotifyStatusData) {
					NotifyStatusData notifyStatusData = (NotifyStatusData) response;

					if (notifyStatusData.isError()) {
						IS_TUT_API_SWITCHED = true;
						updateView();
						Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_error, notifyStatusData.getMessage());
						dialog.show();

					} else {
						IS_TUT_API_SWITCHED = false;
						if (notifyStatusData.getStatus() != null
								&& notifyStatusData.getStatus().equals(NOTITY_ON)) {
							setOnTutorialSetting();
						} else {
							setOffTutorialSetting();
						}
						Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_success, notifyStatusData.getMessage());
						dialog.show();
					}
				} else if (response instanceof GeneratePinData) {
					if (progressBar != null) {
						progressBar.setVisibility(View.GONE);
					}
					GeneratePinData data = (GeneratePinData) response;

					if (data.getError() == false) {
						final Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_change_pin, data.getMessage());
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();

							}
						});
						dialog.show();

						// ankur: refreshing the pin
						callRequestPin();
					} else {
						if (data.getMessage() != null) {
							Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_change_pin, data.getMessage());
							dialog.show();
						} else {
							Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title_change_pin, "Error in changing pin. Try after sometime");
							dialog.show();
						}
					}
				} else if (response instanceof RequestPinData) {
					RequestPinData requestpindata = (RequestPinData) response;
					if (!requestpindata.getError()) {
						requestpin = requestpindata.getPin();
						PreferenceUtil.getInstance(mMainActivity).setPreference(SHARED_PREF_KEYS.PIN_KEY, requestpin);
						QuopnUtils.setDefaultWalletInAppAndPref(requestpindata.getDefaultWallet(), mMainActivity.getApplicationContext());
						QuopnTextView pinNo = (QuopnTextView) mMainActivity.findViewById(R.id.slidemenu_pin_no);
						pinNo.setText(getResources().getString(R.string.pin_no) + " " + requestpin);
						pinNo.setTypeface(null, Typeface.BOLD);
					} else {
						Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title, requestpindata.getMessage());
						dialog.show();
					}

//                if (requestpindata.getError() == false) {
//                    Dialog dialog = new Dialog(mMainActivity,R.string.dialog_title_change_pin,requestpindata.getMessage());
//                    dialog.setOnAcceptButtonClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //finish();
//                        }
//                    });
//                    dialog.show();
//
//                } else {
//
//                    if (requestpindata.getPin() != null) {
//
//                    }
//                }
				} else if (response instanceof ShmartCheckStatusData) {
				/*ShmartCheckStatusData shmartCreateUserData = (ShmartCheckStatusData) response;
				String errorCode = shmartCreateUserData.getError_code();
//				errorCode="104";
				String consumerId = shmartCreateUserData.getConsumer_id();
				String message = shmartCreateUserData.getMessage();

				ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
				User user = profileData.getUser();

				PreferenceUtil.getInstance(mMainActivity)
						.setPreference(SHARED_PREF_KEYS.SHMART_STATUS, errorCode);

				if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)){//000
					showShmartWallet();
				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.TRANS_PWD_BLANK)) {//001
					sendRequestOTP(user.getApi_key(), user.getWalletid());
				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_NOT_EXIST)) {//11
					showRegnDemoScreen();
				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.OTP_ACTIVATION_PENDING)) {//100//Shmart registered but not activated
					sendGenerateOTP(user.getApi_key(), user.getWalletid());
//				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.MOBILE_NUM_EXISTS)) {//101
//					showOTPScreenAndGenerateOTP();
				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_DOES_NOT_EXIST)) {//104
					showRegnDemoScreen();
				} else if(errorCode.equals(QuopnApi.SHMART_ERROR_CODES.ACTIVATION_PENDING)) {//106
					Log.d(TAG, "error code - 106");
//					showOTPScreenAndRequestOTP();
				} else {
					Log.d(TAG, "error code - " + errorCode);
//					showRegnDemoScreen();
//					showError(message);
				}*/
				} else if (response instanceof ShmartGenerateOTPData) {
				/*ShmartGenerateOTPData shmartCreateUserData = (ShmartGenerateOTPData) response;
				String errorCode = shmartCreateUserData.getError_code();
				String message = shmartCreateUserData.getMessage();

				if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
					showOTPScreenAndVerifyOTP();
				} else {
					Log.d(TAG, "error code - "+ errorCode +", error_message: "+message);
				}*/
				} else if (response instanceof ShmartRequestOTPData) {
				/*ShmartRequestOTPData shmartRequestOTPData = (ShmartRequestOTPData) response;
				String errorCode = shmartRequestOTPData.getError_code();
				String message = shmartRequestOTPData.getMessage();

				if (errorCode.equals(QuopnApi.SHMART_ERROR_CODES.CUSTOMER_READY)) {//000
					showOTPScreenAndChangeTransPwd();
				} else {
					Log.d(TAG, "error code - "+ errorCode +", error_message: "+message);
				}*/
				} else if (response instanceof LogoutData) {
					LogoutData shmartRequestOTPData = (LogoutData) response;
					String errorCode = shmartRequestOTPData.getError();
					String message = shmartRequestOTPData.getMessage();
					if (errorCode.equals(QuopnConstants.TRUE)) {

					} else {
						//Dialog dialog = new Dialog(mMainActivity, R.string.dialog_title, message);
						//dialog.show();
						//logoutCleanUp();
					}
				} else if (response instanceof CitrusWalletListData) {
					CitrusWalletListData citruswalletlistData = (CitrusWalletListData) response;
					ArrayList<WalletListData> walletListDatas = citruswalletlistData.getWalletList();
					if (!walletListDatas.isEmpty()) {
						for (WalletListData walletListData : walletListDatas) {
							if (walletListData.getId() == QuopnConstants.WalletType.CITRUS.ordinal()) {
								String signup_id = walletListData.getSignup_id();
								String signin_id = walletListData.getSignin_id();
								String client_signup_secret = walletListData.getClient_signup_secret();
								String client_signin_secret = walletListData.getClient_signin_secret();
								String vanity = walletListData.getVanity();
								CitrusClient mCitrusClient = CitrusClient.getInstance(QuopnApplication.getInstance().getApplicationContext());
								Environment environment = Environment.PRODUCTION;
								if (QuopnApi.currentMode != QuopnApi.Mode.PROD) {
									mCitrusClient.enableLog(true);
									environment = Environment.SANDBOX;
								}
								mCitrusClient.init(signup_id, client_signup_secret, signin_id, client_signin_secret, vanity, environment);
								QuopnApplication.getInstance().setIsCitrusInitiated(true);
								// call walletcitrusHome
								mMainActivity.walletCitrusStatus();
								return;
							}
						}
					}

//            progress.setVisibility(View.GONE);
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

	public void showOTPScreenAndChangeTransPwd(){
		Intent shmartRegn = new Intent(mMainActivity, ShmartOtp.class);
		shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callChangeTransPwd, true);
		mMainActivity.startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
	}

	public void showOTPScreenAndVerifyOTP(){
		Intent shmartRegn = new Intent(mMainActivity, ShmartOtp.class);
		shmartRegn.putExtra(QuopnConstants.INTENT_KEYS.callVerifyOTP, true);
		mMainActivity.startActivityForResult(shmartRegn, QuopnConstants.HOME_PRESS);
	}

	public void showError(String argMsg){
		if(TextUtils.isEmpty(argMsg)) {
			Toast.makeText(mMainActivity, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(mMainActivity, argMsg, Toast.LENGTH_SHORT).show();
		}
	}



	private void updateView() {
//		View view = mMainMenuList.getChildAt(TUTS);
//		if (view == null)
//			return;
//		Switch tut_switch = (Switch) view.findViewById(R.id.tut_switch);
//		tut_switch.toggle();

	}

	@Override
	public void onTimeout(ConnectRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void myTimeout(String requestTag) {
		System.out.println("========MainmenuFragmentRequestTag====="+requestTag);
	}

	public void showProgress() {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	public void stopProgress() {
		if (progressBar != null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}
