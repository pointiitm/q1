package com.quopn.wallet.fragments;

/**
 * @author Sandeep
 *
 *Modified by Sumeet 20/10/2014
 *
 *This class is the container for ViewPager which shows the gifting page, category page, and the quopn listing in the
 *swipe display.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quopn.wallet.GiftDetailsActivity.GiftDetailAddToCartListener;
import com.quopn.wallet.ListingByCategoryActivity;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.MainActivity.OnQuopnLoaded;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.QuopnDetailsActivity.QuopnDetailAddToCartListener;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.CategoryListAdapter.CategorySelectedListener;
import com.quopn.wallet.adapter.GiftingListAdapter.GiftSelectedListener;
import com.quopn.wallet.adapter.QuopnListAdapter.QuopnSelectedListener;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.NewCategoryList;
import com.quopn.wallet.fragments.ProductCategoryListFragment.OnSearchFocusedListener;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.views.PagerSlidingTabStrip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("ValidFragment")
public class ProductCatFragment extends BaseFragment implements
		OnPageChangeListener, OnQuopnLoaded, CategorySelectedListener,
		GiftSelectedListener, QuopnSelectedListener,QuopnDetailAddToCartListener
		,GiftDetailAddToCartListener, OnSearchFocusedListener {

	private static final long serialVersionUID = 1L;
	private PagerSlidingTabStrip mTabs;
	private ViewPager mPager;
	private MyPagerAdapter mAdapter;
	private int mColorRes = -1;

	public static final String DEFAULT_CATEGORY_TYPE = "ALL";
	public static String QUOPN_CATEGORY_TYPE = DEFAULT_CATEGORY_TYPE;
	public static final String DEFAULT_CATEGORY_ID = "";
	public static String QUOPN_CATEGORY_ID = DEFAULT_CATEGORY_ID;

	public static final String QUOPN_CALL_TO_ACTION = "CALL";
	public static final String QUOPN_SMS = "SMS";
	public static final String QUOPN_VIDEO = "VIDEO";
	public static final String QUOPN_WEBISSUE = "WEB";
	public static final String QUOPN_UCN = "UCN";

	private boolean isTutShown = false;
	private boolean isCatTutShown = false;
	private boolean isGiftTutShown = false;
	private boolean isViaSearch = false;

	private ArrayList<String> titles_temp = new ArrayList<String>();
	private ArrayList<String> titles_sorted = new ArrayList<String>();

	// set default to zero.
	private int CATEGORY_INDEX = 0;
	// set default to one
	private int ALL_INDEX = 1;

	private String isAttach = null;
	private boolean isGiftAvailable = false;
	private String TAG = "Quopn/PRODUCTCATFRAGMENT";

	private AnalysisManager mAnalysisManager;
	private MainActivity mMainActivity;
    Uri uri;
    String cat_frag;
    String listing_new;
    String listing_expire;

	public ProductCatFragment() {
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.productcatlisting, null);
		mTabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		mPager = (ViewPager) rootView.findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(4);
		mTabs.setOnPageChangeListener(this);


		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAnalysisManager=((QuopnApplication)mMainActivity.getApplicationContext()).getAnalysisManager();
		LocalBroadcastManager.getInstance(mMainActivity).registerReceiver(receiverShowTab,
				new IntentFilter(QuopnConstants.BROADCAST_SHOW_TAB));
		LocalBroadcastManager.getInstance(mMainActivity).registerReceiver(receiverGiftTab,
				new IntentFilter(QuopnConstants.BROADCAST_SHOW_GIFT_TAB));
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(mMainActivity).unregisterReceiver(receiverShowTab);
		LocalBroadcastManager.getInstance(mMainActivity).unregisterReceiver(receiverGiftTab);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}

	public void notifySearchTextChanged(String argSearchText, Activity activity) {
		if(mPager != null && mAdapter != null){
			mAdapter.notifyDataSetChanged();
		mPager.setCurrentItem(titles_sorted.size() - 1);
		}
	}
	
	public void notifyAddToCartCounterChanged() {
		if(mPager != null && mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 
	 * @author Sumeet
	 * 
	 *         This is the adapter class for the ViewPager. It contains
	 *         ProductCategoryListFragment as pages to show the listing of
	 *         gifts, categories
	 * 
	 */
	public class MyPagerAdapter extends FragmentStatePagerAdapter implements
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private HashMap<Integer, ProductCategoryListFragment> map;

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);

		}

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			addTitiletolist();
			map = new HashMap<Integer, ProductCategoryListFragment>();
		}

		private void addTitiletolist() {
			titles_sorted.clear();
			titles_temp.clear();
//            titles_temp.add("VOUCHERS");
			if (QuopnConstants.MAIN_BOTTOM_BAR != null) {
				for (int i = 0; i < QuopnConstants.MAIN_BOTTOM_BAR.length; i++) {
                     titles_temp.add(QuopnConstants.MAIN_BOTTOM_BAR[i]);
				}

				titles_temp.remove(getResources().getString(R.string.search));
				if (isGiftAvailable) {
					titles_sorted.add(mMainActivity.GIFT_TITLE);
				}
				for (int i = 0; i < titles_temp.size(); i++) {
					/*if (titles_temp.contains("My Gifts")) {
						titles_temp.remove("My Gifts");
					}*/


					if (titles_temp.contains("All")) {
						titles_sorted.add("All");
						titles_temp.remove("All");
					}
					if (titles_temp.contains("Categories")) {
						titles_sorted.add("Categories");
						titles_temp.remove("Categories");
					}
					
				}
				titles_sorted.addAll(titles_temp);
				//titles_sorted.add(getResources().getString(R.string.search)); //This is removed search tab
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles_sorted.get(position);
		}

		@Override
		public int getCount() {
			return titles_sorted.size();
		}

		@Override
		public Fragment getItem(int position) {
            Log.d(TAG, "Position : " + position);

			ProductCategoryListFragment categoryListFragment = ProductCategoryListFragment
					.newInstance(ProductCatFragment.this,
							ProductCatFragment.this, ProductCatFragment.this,
							titles_sorted.get(position),ProductCatFragment.this,ProductCatFragment.this);
			categoryListFragment.setOnSearchFocusedListener(ProductCatFragment.this);
			
			map.put(position, categoryListFragment);
			
			return categoryListFragment;
		}
		
		public ProductCategoryListFragment itemAt(int index) {
			return map.get(index);
		}

		@Override
		public int getItemPosition(Object object) {
			ProductCategoryListFragment fragment = (ProductCategoryListFragment) object;
			fragment.clearSearch();
			String title = fragment.getTitle();
			int position = titles_sorted.indexOf(title);
			fragment.updateViews(title);

			if (position >= 0) {
				return position;
			} else {
				return POSITION_NONE;
			}

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

		if (titles_sorted.get(arg0).equals(mMainActivity.GIFT_TITLE)) {

		} else if (titles_sorted.get(arg0).equals("Categories")) {
			
		} else if (titles_sorted.get(arg0).equals("All")) {
			mAdapter.itemAt(arg0).clearSearch();
		}
	}

	@Override
	public void onPageSelected(int position) {
//		showTutorial(position);
		sendAnalysis(position);
			if (isViaSearch
				&& titles_sorted.get(position).equalsIgnoreCase("All")) {
				
				mAdapter.itemAt(position).focusOnSearch();
				
				isViaSearch = false;
			} else {
				if (mAdapter != null && mAdapter.itemAt(position) != null) {
					mAdapter.itemAt(position).unfocusFromSearch();
				}
			}
	}

	@Override
	public void onAttach(Activity argActivity) {
		super.onAttach(argActivity);
		mMainActivity = (MainActivity)argActivity;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		// pager.getAdapter().notifyDataSetChanged();
		super.onStart();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (isAttach != null && isGiftAvailable) {
			setViewPagerAdapter(true);
		}else if(isAttach != null){
			setViewPagerAdapter(false);
		}
	}

	public void isAttached() {
		isAttach = "Attached";
	}
	
	public void setGiftAvailable(boolean isGiftAvailable){
		this.isGiftAvailable = isGiftAvailable;
	}

	public void setViewPagerAdapter(boolean isGiftAvailable) {
		this.isGiftAvailable = isGiftAvailable;
		mAdapter = new MyPagerAdapter(getChildFragmentManager());
		try {
			mPager.setAdapter(mAdapter);
		} catch (Exception e) {
//			System.out.println(""+e);
		}
		
		
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		mPager.setPageMargin(pageMargin);
		mPager.setCurrentItem(0);
		mTabs.setViewPager(mPager);
//		showTutorial(0);
	}

	private BroadcastReceiver receiverShowTab=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String tabName = intent.getStringExtra("value");
			int position = titles_sorted.indexOf(tabName);
			if(position != -1){
				mPager.setCurrentItem(position);
			}
		}
	};

	private BroadcastReceiver receiverGiftTab=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String tabName = intent.getStringExtra("value");
			int position = titles_sorted.indexOf(tabName);
			if(position != -1){
				mPager.setCurrentItem(position);
			}
		}
	};

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

	@Override
	public void quopnLoaded() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCategorySelected(NewCategoryList category) {
		Intent listingIntent
			= new Intent(mMainActivity, ListingByCategoryActivity.class);
		listingIntent.putExtra("category", category.getCategoryid());
		listingIntent.putExtra("categoryname", category.getName());
		mMainActivity.startActivityForResult(listingIntent, QuopnConstants.HOME_PRESS);
	}
	
	public void switchToAllQuopnsTab(){
		try{
			if(mMainActivity.getSlidingMenu().isMenuShowing() ){
				 mMainActivity.getSlidingMenu().toggle();
			}
			ALL_INDEX = titles_sorted.indexOf("All");
			mPager.setCurrentItem(ALL_INDEX);
		} catch(Exception ex){
			
		}
	}

	@Override
	public void onBackPressed(FragmentActivity activity) {
		ALL_INDEX = titles_sorted.indexOf("All");
		if (mPager.getCurrentItem() != ALL_INDEX) {
			mPager.setCurrentItem(ALL_INDEX);
		} else {
			//Log.i("getactivity", ""+getActivity());
			if (getExitable())
				activity.finish();

		}

	}

	@Override
	public void onQuopnOpened() {
		// implement when any quopon is opened..
		
	}
	
	public void sendAnalysis(int position){
		/*if (titles_sorted.get(position).equalsIgnoreCase(getResources().getString(R.string.search))) {
			mAnalysisManager.send(AnalysisEvents.SEARCH);
		}else*/ if(titles_sorted.get(position).equalsIgnoreCase(mMainActivity.GIFT_TITLE)){
			mAnalysisManager.send(AnalysisEvents.PROFILE);
		}else if(titles_sorted.get(position).equalsIgnoreCase("Categories")){
			mAnalysisManager.send(AnalysisEvents.CATEGORIES);
		}else if(titles_sorted.get(position).equalsIgnoreCase("All")){
			mAnalysisManager.send(AnalysisEvents.ALL);
		}else if(titles_sorted.get(position).equalsIgnoreCase("Featured")){
			mAnalysisManager.send(AnalysisEvents.FEATURED);
		}else if(titles_sorted.get(position).equalsIgnoreCase("New")){
			mAnalysisManager.send(AnalysisEvents.NEW);
		}else if(titles_sorted.get(position).equalsIgnoreCase("Expiring")){
			mAnalysisManager.send(AnalysisEvents.EXPIRING);
		}
	}
	
	/*private void showTutorial(int position) {
		try {
			if (!titles_sorted.get(position).equalsIgnoreCase(getResources().getString(R.string.search))) {
				((MainActivity)getActivity()).searchText = "";
				((MainActivity)getActivity()).closeSearchBar();
			}else{
				((MainActivity)getActivity()).searchText = getResources().getString(R.string.search);
				((MainActivity)getActivity()).openSearchBar();
				((MainActivity)getActivity()).clearSearchView();
				mPager.getAdapter().notifyDataSetChanged();
			}
		} catch (Exception e) {
			return;
		}
		
		
		if (position == 0) {
			((SlidingFragmentActivity) getActivity()).getSlidingMenu()
				.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		}else{
			((SlidingFragmentActivity) getActivity()).getSlidingMenu()
				.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		}

		if (titles_sorted.get(position).equals(MainActivity.GIFT_TITLE)) {
			if (!PreferenceUtil
					.getInstance(getActivity())
					.getPreference_bool(
							PreferenceUtil.SHARED_PREF_KEYS.IS_GiftTutShown)
					&& QuopnConstants.TUTORIAL_ON.equals(PreferenceUtil
							.getInstance(getActivity()).getPreference(
									QuopnConstants.TUTORIAL_PREF_GIFTING))
					&& !PreferenceUtil
							.getInstance(getActivity())
							.getPreference_bool(
									PreferenceUtil.SHARED_PREF_KEYS.IS_ANY_TUT_ON)) {
				PreferenceUtil
					.getInstance(getActivity())
					.setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_GiftTutShown, true);
				getActivity().startActivity(
						new Intent(getActivity(), GiftListingTutorial.class));
			}

		} else if (titles_sorted.get(position).equals("Categories")) {
			if (!QUOPN_CATEGORY_TYPE.equals(DEFAULT_CATEGORY_TYPE)) {
				QUOPN_CATEGORY_TYPE = DEFAULT_CATEGORY_TYPE;
				mPager.getAdapter().notifyDataSetChanged();
			}
			if (!PreferenceUtil
					.getInstance(getActivity())
					.getPreference_bool(
							PreferenceUtil.SHARED_PREF_KEYS.IS_CatTutShown)
					&& QuopnConstants.TUTORIAL_ON.equals(PreferenceUtil
							.getInstance(getActivity()).getPreference(
									QuopnConstants.TUTORIAL_PREF_CAT)) 
					&& !PreferenceUtil
							.getInstance(getActivity())
							.getPreference_bool(
										PreferenceUtil.SHARED_PREF_KEYS.IS_ANY_TUT_ON)) {
				PreferenceUtil
					.getInstance(getActivity())
					.setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_CatTutShown, true);
				getActivity().startActivity(
						new Intent(getActivity(), CategoryTutorial.class));
			}
		} else {
			if (!PreferenceUtil
					.getInstance(getActivity())
					.getPreference_bool(
							PreferenceUtil.SHARED_PREF_KEYS.IS_TutShown)
					&& QuopnConstants.TUTORIAL_ON.equals(PreferenceUtil
							.getInstance(getActivity()).getPreference(
									QuopnConstants.TUTORIAL_PREF_LISTING))
					&& !PreferenceUtil
							.getInstance(getActivity())
							.getPreference_bool(
										PreferenceUtil.SHARED_PREF_KEYS.IS_ANY_TUT_ON)) {
				PreferenceUtil
					.getInstance(getActivity())
					.setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_TutShown, true);
				getActivity().startActivity(
						new Intent(getActivity(), QuopnListingTutorial.class));
			}

		}

	}*/

	@Override
	public void onQuopnDetailAddToCartSuccess() {
		mMainActivity.AllCartCounterRefreshed();
	}

	@Override
	public void onGiftDetailAddToCartSuccess() {
		mMainActivity.AllCartCounterRefreshed();
	}
	
	public void onSearchFocused() {
		int allTabPosition = titles_sorted.indexOf("All");

		/* We are already on All tab, so no need to move anywhere */
		if (mPager.getCurrentItem() == allTabPosition) {
			ProductCategoryListFragment fragment
				= (ProductCategoryListFragment)
					mAdapter.itemAt(mPager.getCurrentItem());
			fragment.focusOnSearch();
			return;
		}
		
		if (allTabPosition >= 0 && allTabPosition < titles_sorted.size()) {
			isViaSearch = true;
			mPager.setCurrentItem(allTabPosition);
		}
	}

}
