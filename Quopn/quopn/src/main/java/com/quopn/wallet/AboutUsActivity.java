package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.adapter.AboutUsListAdapter;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.AboutUsData;
import com.quopn.wallet.data.model.TAndCAndPrivPolData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutUsActivity extends ActionBarActivity implements ConnectionListener {

	private AboutUsListAdapter mListAdapter;
	private FloatingGroupExpandableListView  mAboutList;
	private List<AboutUsData> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	protected int previousGroup = -1;
	private int currentGroup = -1;
	private CustomProgressDialog mCustomProgressDialog;
	private String textTermsAndCon = "";
	private String textPrivacyPolicy = "";
	private Intent mIntent;
	private int T_AND_C = 0;
	private int PRIV_POLICY = 1;

	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.aboutusmenu);
		
		mIntent = getIntent();
		
		mAboutList = (FloatingGroupExpandableListView ) findViewById(R.id.about_menu_list);
		
		mCustomProgressDialog = new CustomProgressDialog(this);

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
		slider.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ImageView home_btn = (ImageView) actionBarView
				.findViewById(R.id.home_btn);
		home_btn.setOnClickListener(new  View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
//		mAboutList = (ExpandableListView) rootView
//				.findViewById(R.id.about_menu_list);
//		return rootView;
		
	}
	
	public void getTermsAndConditions(){
		mCustomProgressDialog.show();
		
		final ConnectionFactory mConnectionFactory = new ConnectionFactory(getApplicationContext(),this);
		mConnectionFactory.createConnection(QuopnConstants.TERMS_AND_CONDITIONS);
		
		mCustomProgressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface argDialog) {
				mConnectionFactory.cancel();
				mAboutList.collapseGroup(T_AND_C);
				Dialog dialog=new  Dialog(AboutUsActivity.this, R.string.dialog_title_no_internet,R.string.t_and_c_load_error);
				dialog.show();
			}
		});
	}
	
	public void getPrivacyPolicy(){
		mCustomProgressDialog.show();
		
		final ConnectionFactory mConnectionFactory = new ConnectionFactory(getApplicationContext(),this);
		mConnectionFactory.createConnection(QuopnConstants.PRIVACY_POLICY);
		
		mCustomProgressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface argDialog) {
				mConnectionFactory.cancel();
				mAboutList.collapseGroup(PRIV_POLICY);
				Dialog dialog=new  Dialog(AboutUsActivity.this, R.string.dialog_title_no_internet,R.string.priv_poli_load_error);
				dialog.show();
			}
		});
	}

	 @Override
		protected void onResume(){
		super.onResume();
		
		// preparing list data
				prepareListData();

				mListAdapter = new AboutUsListAdapter(this, listDataHeader, listDataChild);
				WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(mListAdapter);

				// setting list adapter
				mAboutList.setAdapter(wrapperAdapter);

				/*mAboutList.setOnGroupExpandListener(new OnGroupExpandListener() {
			        @Override
			        public void onGroupExpand(int groupPosition) {
			        	if(QuopnUtils.isInternetAvailable(AboutUsActivity.this)){
			        		if(groupPosition != previousGroup ){
				            	mAboutList.collapseGroup(previousGroup);
				            	previousGroup = groupPosition;
				            	if(groupPosition == 0){
				            		getTermsAndConditions();
				            	} else if(groupPosition == 1){
				            		getPrivacyPolicy();
				            	}
				            }
				            previousGroup = groupPosition;
				        	} else{
				        		new Dialog(AboutUsActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
				        	}
			        }
			    });*/
				
				mAboutList.setOnGroupClickListener(new OnGroupClickListener() {
					
					@Override
					public boolean onGroupClick(ExpandableListView parent, View v,
							int groupPosition, long id) {
						if(QuopnUtils.isInternetAvailable(AboutUsActivity.this)){
							currentGroup = groupPosition;
							if(groupPosition != previousGroup ){
				            	mAboutList.collapseGroup(previousGroup);
				            	previousGroup = groupPosition;
							}
							previousGroup = groupPosition;
							if(mAboutList.isGroupExpanded(groupPosition)){
								mAboutList.collapseGroup(groupPosition);
							} else {
								mAboutList.collapseGroup(groupPosition);
								if(groupPosition == 0){
									if(TextUtils.isEmpty(textTermsAndCon)){
										getTermsAndConditions();
									} else{
										setCachedText(textTermsAndCon);
									}
				            	} else if(groupPosition == 1){
				            		if(TextUtils.isEmpty(textPrivacyPolicy)){
				            			getPrivacyPolicy();
				            		} else{
				            			setCachedText(textPrivacyPolicy);
				            		}
				            	}
							}
//							changeGroupIcon(groupPosition);
						} else{
			        		new Dialog(AboutUsActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
			        	}
						return false;
					}
				});
				
//				mAboutList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
//					
//					@Override
//					public void onGroupCollapse(int groupPosition) {
//						changeGroupIcon();
//					}
//				});
	
				// Listview on child click listener
				mAboutList.setOnChildClickListener(new OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent, View v,
							int groupPosition, int childPosition, long id) {
						return false;
					}
				});
				
				int groupToExpand = mIntent.getIntExtra(QuopnConstants.ABOUT_US_GROUP_TO_EXPAND, -1);
				if(groupToExpand == -1){
					
				} else{
					mAboutList.expandGroup(groupToExpand);
					if(groupToExpand == T_AND_C){
						getTermsAndConditions();
					} else if(groupToExpand == PRIV_POLICY){
						getPrivacyPolicy();
					}
				}
	}
	 
	/*@Override
	public void onBackPressed(FragmentActivity activity) {
		super.onBackPressed(activity);
		activity.getSupportFragmentManager().beginTransaction().remove(this)
				.commit();
		Fragment mainmenu = new MainMenuFragment();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.menu_frame, mainmenu)
				.commit();

	}*/

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<AboutUsData>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		//listDataHeader.add(new AboutUsData("EULA",R.drawable.icon_eula));
		listDataHeader.add(new AboutUsData("Terms of Use",R.drawable.icon_tnc));
		listDataHeader.add(new AboutUsData("Privacy Policy",R.drawable.icon_privacypolicy));
//		listDataHeader.add(new AboutUsData("FAQs",R.drawable.icon_faq));

		// Adding child data
		/*List<String> EULA = new ArrayList<String>();
		EULA.add("http://solutions.techshastra.com/quopnapi/v2_test/eula");*/

		List<String> Terms = new ArrayList<String>();
		Terms.add(QuopnApi.TERMS_AND_CONDITION_URL);

		List<String> Privacy = new ArrayList<String>();
		Privacy.add(QuopnApi.PRIVACY_POLICY_URL);
		
//		List<String> FAQs = new ArrayList<String>();
//		FAQs.add(QuopnApi.FAQA_URL);

		//listDataChild.put(listDataHeader.get(0).getName(), EULA); // Header, Child data
		listDataChild.put(listDataHeader.get(0).getName(), Terms);
		listDataChild.put(listDataHeader.get(1).getName(), Privacy);
//		listDataChild.put(listDataHeader.get(2).getName(), FAQs);
	}

	@Override
	public void onResponse(int responseResult, Response response) {
			mCustomProgressDialog.dismiss();
			if(response != null){
				try {
					TAndCAndPrivPolData data = (TAndCAndPrivPolData) response;
					setCachedText(data.getData());
				}catch (ClassCastException e)
				{

				}
			}
	}
	
	public void setCachedText(String argText){
		mListAdapter.setText(argText);
		if(currentGroup == 0){
			textTermsAndCon = argText;
		}else if(currentGroup == 1){
			textPrivacyPolicy = argText;
		}
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
					mCustomProgressDialog.dismiss();
					Dialog dialog=new Dialog(AboutUsActivity.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection); 
					dialog.show();
				}
				
			}
		});
		
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}
}
