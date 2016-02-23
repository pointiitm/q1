package com.quopn.wallet.fragments;

/**
 * @author Sandeep
 *
 *Modified by Sumeet (15/09/2014)
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.orhanobut.logger.Logger;
import com.quopn.wallet.GiftDetailsActivity.GiftDetailAddToCartListener;
import com.quopn.wallet.ListingByCategoryActivity;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.QuopnDetailsActivity.QuopnDetailAddToCartListener;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.CategoryListAdapter;
import com.quopn.wallet.adapter.CategoryListAdapter.CategorySelectedListener;
import com.quopn.wallet.adapter.GiftingListAdapter;
import com.quopn.wallet.adapter.GiftingListAdapter.GiftSelectedListener;
import com.quopn.wallet.adapter.QuopnListAdapter;
import com.quopn.wallet.adapter.QuopnListAdapter.QuopnSelectedListener;
import com.quopn.wallet.adapter.VoucherListAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.GiftsContainer;
import com.quopn.wallet.data.model.ListQuopnContainer;
import com.quopn.wallet.data.model.NewCategoryList;
import com.quopn.wallet.data.model.NewListCategoryContainer;
import com.quopn.wallet.data.model.QuopnData;
import com.quopn.wallet.data.model.VoucherList;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnDownloadManager;
import com.quopn.wallet.utils.QuopnDownloadManager.DownloadingListner;
import com.quopn.wallet.views.QuopnTextView;
import com.quopn.wallet.views.RefreshableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.support.annotation.Nullable;
//import com.quopn.wallet.views.RefreshableListView.OnRefreshListener;

@SuppressLint({ "ValidFragment", "DefaultLocale" })
public class ProductCategoryListFragment extends Fragment implements
		OnRefreshListener , DownloadingListner, OnClickListener
		, OnFocusChangeListener {

    private static final String TAG = "Quopn/ProdCatList";
    private Activity mParentActivity;
	private ProductCategoryListFragment mProductCategoryListFragment;
	private ProgressDialog mProgressDialog;

	private static final String ARG_POSITION = "position";
	private String mPagerPosition;
	private List<ListQuopnContainer> mListQuopnContainer = new ArrayList<ListQuopnContainer>();
	private List<NewListCategoryContainer> mListCategoryContainer = new ArrayList<NewListCategoryContainer>();
	private List<GiftsContainer> mListGiftsContainer = new ArrayList<GiftsContainer>();
    private List<VoucherList> mVoucherList = new ArrayList<VoucherList>();
	private String mTitle;
	private CategorySelectedListener mCategorySelectedListener;
	private QuopnSelectedListener mQuopnSelectedListener;
	private QuopnDetailAddToCartListener mQuopnDetailAddToCartListener;
	private GiftDetailAddToCartListener mGiftDetailAddToCartListener;
	private GiftSelectedListener mGiftSelectedListener;
	private NewListCategoryContainer mCategoryContainer;
	private TextView mHeader;
	private ImageView mImgNoQuopns;
	private TextView mNoQuoponText;
	private TextView mImgNoQuopnsButton;
	private String mTagNewQuopns = "New Quopns";
	private String mTagSearchQuopns = "Search Quopns";
	private String mTagNewQuopns_Category = "New Quopns Category";
	private QuopnListAdapter mQuopnListAdapter;
	private CategoryListAdapter mCategoryListAdapter;
	private RefreshableListView mListView;
	private TextView mfooter_text;
	private TextView mskip_text;
	private String	mPersonalVideoUrl;
	private QuopnDownloadManager mQuopnDownloadManager;
	private String personal_message_downloaded;
	private GiftingListAdapter myQuopnsAdapter;
	private AnalysisManager mAnalysisManager;
	private Handler mQuopnListHandler;
	private EditText etSearchQuopns;
	private OnSearchFocusedListener onSearchFocusedListener;
	private RelativeLayout rlSearchQuopns;
	private ImageView ivSearchQuopns;
	private View vwSwallowAutoFocus;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private SwipeRefreshLayout mSwipeRefresh_empty_content_layout;
	private int NumColumn_Gift = 1;
	private int NumColumn_Quopns_Category = 2;
	private SearchWatcher searchWatcher = new SearchWatcher();
    private VoucherListAdapter mVoucherListAdapter;


	class SearchWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			OnClickListener listener;

			String searchWord = s.toString();

			int drawable;
			if (searchWord != null && !searchWord.isEmpty()) {
				drawable = R.drawable.clear_search;
				listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						etSearchQuopns.setText("");
					}
				};
			} else {
				drawable = R.drawable.cmn_search_btn;
				listener = ProductCategoryListFragment.this;
			}

			ivSearchQuopns.setImageDrawable(
					mParentActivity.getResources().getDrawable(drawable));
			ivSearchQuopns.setOnClickListener(listener);

			QuopnConstants.SEARCHTEXT = searchWord;
			if(!TextUtils.isEmpty(searchWord)) {
				mAnalysisManager.send(AnalysisEvents.SEARCH_WORD, searchWord);
			}
			updateViews(mPagerPosition);

		}
	}

	public interface OnSearchFocusedListener {
		public void onSearchFocused();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d("");
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.d("");
		mAnalysisManager=((QuopnApplication)mParentActivity.getApplicationContext()).getAnalysisManager();
		View rootView = inflater.inflate(R.layout.categorylist, null);
		mListView = (RefreshableListView) rootView.findViewById(R.id.items);
		//mListView.setonRefreshListener(this);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_light);

		mSwipeRefresh_empty_content_layout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh_empty_content);
		mSwipeRefresh_empty_content_layout.setOnRefreshListener(this);
		mSwipeRefresh_empty_content_layout.setColorScheme(android.R.color.holo_red_light);

		mHeader = mListView.getHeaderTextView();
		mHeader.setText("");
		//mHeader.setBackgroundColor(Color.rgb(243, 243, 243));
		mHeader.setVisibility(View.GONE); // to hide the header
		mPagerPosition = getArguments().getString(ARG_POSITION);
		mImgNoQuopns = (ImageView) rootView.findViewById(R.id.img_no_new_quopns);
		mNoQuoponText = (QuopnTextView) rootView.findViewById(R.id.no_quopn_text);
		mImgNoQuopnsButton = (TextView) rootView.findViewById(R.id.img_no_new_quopns_text);
		mImgNoQuopnsButton.setText(R.string.browse_current_categories);
		mImgNoQuopnsButton.setTag(mTagNewQuopns);
		mImgNoQuopnsButton.setOnClickListener(this);

		rlSearchQuopns = (RelativeLayout) rootView.findViewById(R.id.rlSearchQuopns);

		etSearchQuopns = (EditText) rootView.findViewById(R.id.etSearchQuopns);
		etSearchQuopns.setOnClickListener(this);
		etSearchQuopns.addTextChangedListener(searchWatcher);
		etSearchQuopns.setOnFocusChangeListener(this);

		ivSearchQuopns = (ImageView) rootView.findViewById(R.id.ivSearchQuopns);

		dataProvider(mPagerPosition);
		updateViews(mPagerPosition);

		vwSwallowAutoFocus
			= rootView.findViewById(R.id.vwSwallowAutoFocus);

		mProductCategoryListFragment = this;
		callCitrus(); // first install case call citrus
		return rootView;
	}

	private void callCitrus(){
		PreferenceUtil prefUtil = PreferenceUtil.getInstance(mParentActivity);
		if(!prefUtil.hasContainedPreferenceKey(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN)) {
//			prefUtil.setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, true);
			if(mParentActivity instanceof MainActivity){
				((MainActivity) mParentActivity).callCitrus();
			}
		} else {
			if (!prefUtil.getPreference_bool(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN)) {
				if(mParentActivity instanceof MainActivity){
					((MainActivity) mParentActivity).callCitrus();
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MainActivity) {
			mParentActivity = (MainActivity) activity;
		} else if(activity instanceof ListingByCategoryActivity){
			mParentActivity = (ListingByCategoryActivity) activity;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void dataProvider(String tag){

		if (MainActivity.GIFT_TITLE != null
				&& MainActivity.GIFT_TITLE.equalsIgnoreCase(tag)) {
			myQuopnsAdapter = new GiftingListAdapter(mParentActivity, 0, mListGiftsContainer,
					mGiftSelectedListener,mGiftDetailAddToCartListener);
            mListView.setNumColumns(NumColumn_Gift);
			mListView.setAdapter(myQuopnsAdapter);
			showSearchBar(false);
		}
        else if ("VOUCHERS".equalsIgnoreCase(tag)) {
            mVoucherListAdapter = new VoucherListAdapter(mParentActivity,0,mVoucherList);
            mListView.setNumColumns(NumColumn_Gift);
            mListView.setAdapter(mVoucherListAdapter);
            showSearchBar(false);
        }
		else if ("Categories".equalsIgnoreCase(tag)) {
            mCategoryListAdapter = new CategoryListAdapter(mParentActivity, 0,
			mListCategoryContainer, mCategorySelectedListener);
            mListView.setNumColumns(NumColumn_Quopns_Category);
			mListView.setAdapter(mCategoryListAdapter);
			showSearchBar(false);
		} else if ("All".equalsIgnoreCase(tag)) {
			mQuopnListAdapter = new QuopnListAdapter(mParentActivity, 0,
					mListQuopnContainer, mQuopnSelectedListener,mQuopnDetailAddToCartListener);
            mListView.setNumColumns(NumColumn_Quopns_Category);
			mListView.setAdapter(mQuopnListAdapter);
			showSearchBar(true);
		} else if ("Expiring".equalsIgnoreCase(tag)) {
			mQuopnListAdapter = new QuopnListAdapter(mParentActivity, 0,
					mListQuopnContainer, mQuopnSelectedListener,mQuopnDetailAddToCartListener);
            mListView.setNumColumns(NumColumn_Quopns_Category);
			mListView.setAdapter(mQuopnListAdapter);
			showSearchBar(true);
		} else if (getResources().getString(R.string.search).equalsIgnoreCase(tag)) {
			mQuopnListAdapter = new QuopnListAdapter(mParentActivity, 0,
					mListQuopnContainer, mQuopnSelectedListener,mQuopnDetailAddToCartListener);
            mListView.setNumColumns(NumColumn_Quopns_Category);
			mListView.setAdapter(mQuopnListAdapter);
		} else {
			mQuopnListAdapter = new QuopnListAdapter(mParentActivity, 0,
					mListQuopnContainer, mQuopnSelectedListener,mQuopnDetailAddToCartListener);
            mListView.setNumColumns(NumColumn_Quopns_Category);
			mListView.setAdapter(mQuopnListAdapter);
			showSearchBar(true);
		}
	}


	public static ProductCategoryListFragment newInstance(GiftSelectedListener giftSelectedListener,QuopnSelectedListener quopnSelectedListener,CategorySelectedListener categorySelectedListener,String position,QuopnDetailAddToCartListener quopnDetailAddToCartListener,GiftDetailAddToCartListener giftDetailAddToCartListener){
        Log.d(TAG, position);
		ProductCategoryListFragment categoryListFragment = new ProductCategoryListFragment();
		Bundle b1 = new Bundle();
		b1.putString(ARG_POSITION, position);
		categoryListFragment.setArguments(b1);
		categoryListFragment.setTitle(position);
		categoryListFragment.setCategorySelectedListener(categorySelectedListener);
		categoryListFragment.setGiftSelectedListener(giftSelectedListener);
		categoryListFragment.setQuopnSelectedListener(quopnSelectedListener);
		categoryListFragment.setQuopnDetailAddToCartListener(quopnDetailAddToCartListener);
		categoryListFragment.setGiftDetailAddToCartListener(giftDetailAddToCartListener);
		return categoryListFragment;

	}

	private void setGiftDetailAddToCartListener(GiftDetailAddToCartListener giftDetailAddToCartListener) {
		this.mGiftDetailAddToCartListener = giftDetailAddToCartListener;
	}

	private void setCategorySelectedListener(CategorySelectedListener categorySelectedListener){
		this.mCategorySelectedListener = categorySelectedListener;
	}

	private void setGiftSelectedListener(GiftSelectedListener giftSelectedListener){
		this.mGiftSelectedListener = giftSelectedListener;
	}

	private void setQuopnSelectedListener(QuopnSelectedListener mQuopnSelectedListener){
		this.mQuopnSelectedListener = mQuopnSelectedListener;
	}

	private void setQuopnDetailAddToCartListener(QuopnDetailAddToCartListener quopnDetailAddToCartListener) {
		this.mQuopnDetailAddToCartListener = quopnDetailAddToCartListener;
	}

	/*public ProductCategoryListFragment(
			CategorySelectedListener categorySelectedListener) {
		super();
		this.mCategorySelectedListener = categorySelectedListener;
	}*/

	/**
	 *
	 * @param cursor
	 * @return
	 *
	 * this method takes cursor as a param and converts it into a list of Category.
	 * since we have used ListView to display category as a grid type, we have a container class ListCategoryContainer
	 * which contains two category objects.mCategoryListAdapter = new CategoryListAdapter(getActivity(), 0,
					mListCategoryContainer, mCategorySelectedListener);
	 */
	private List<NewListCategoryContainer> convertCategoryListDatafromCursor(
			Cursor cursor) {
		List<NewListCategoryContainer> mListCategoryList = new ArrayList<NewListCategoryContainer>();
		if(cursor!=null||cursor.getCount()>=0){


		while (cursor.moveToNext()) {
			NewCategoryList category = new NewCategoryList(); //Left Category
			mCategoryContainer = new NewListCategoryContainer();

            String catType;

//			category.setIcon(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_ICON)));
			category.setCategoryid(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID)));
//			category.setCategory(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY)));
            category.setName(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY)));
            catType = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_TYPE));
            category.setType(catType);
            category.setThumbimage(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_ICON)));
            category.setSequence(cursor.getInt(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE)));
            mCategoryContainer.setCategory1(category);
            if(!catType.equalsIgnoreCase("G")) {
                mListCategoryList.add(mCategoryContainer);
            }

//			if (cursor.moveToNext()) {
//				category= new Category(); //Right Category
//				category.setIcon(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_ICON)));
//				category.setCategoryid(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY_ID)));
//				category.setCategory(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_CATEGORY.COLUMN_CATEGORY)));
//				mCategoryContainer.setCategory2(category);
//			}



		  }
		  cursor.close();
		 }
		
		return mListCategoryList;
	}

	/**
	 * mCategoryListAdapter = new CategoryListAdapter(getActivity(), 0,
					mListCategoryContainer, mCategorySelectedListener);
	 * @param cursor
	 * @return
	 * 
	 * this method takes cursor as a param and converts it into a list of Quopns.
	 * since we have used ListView to display quopns as a grid type, we have a container class ListQuopnContainer
	 * which contains two quopn objects.
	 */
	private List<ListQuopnContainer> convertQuopnListDatafromCursor(
			Cursor cursor) {
		List<ListQuopnContainer> listQuopnContainer = new ArrayList<ListQuopnContainer>();
		StringBuffer allQuopnIdInJson= new StringBuffer();
		allQuopnIdInJson.append("[");
		while (cursor.moveToNext()) {

			ListQuopnContainer container = new ListQuopnContainer();

			QuopnData quopnData1 = new QuopnData();
			quopnData1.setCampaign(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN)));
			quopnData1
					.setThumb_icon(cursor.getString(cursor
							.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON)));
			quopnData1.setQuopnId(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID)));
			allQuopnIdInJson.append(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID))+",");
			quopnData1
					.setCall_to_action(cursor.getString(cursor
							.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION)));
			quopnData1.setCta_text(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT)));
			quopnData1
			.setMaster_tag(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG)));
			quopnData1
					.setMastertag_url(cursor.getString(cursor
							.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL)));
			quopnData1.setCta_value(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE)));

			quopnData1.setEndingdate(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_END_DATE)));
			
			quopnData1.setProductname(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME)));
			
			quopnData1.setShort_desc(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC)));
			
			quopnData1.setBrand(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_BRAND)));
			
			quopnData1.setAvailable_quopns(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS)));
			
			quopnData1.setAlready_issued(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED)));

			quopnData1.setDescription_highlight(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC)));
			
			quopnData1.setDescription_end(cursor.getString(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_END_DESC)));
			
			quopnData1.setSort_index(cursor.getInt(cursor
					.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX)));
			
			container.setQuopnData1(quopnData1);

//			if (cursor.moveToNext()) {
//				QuopnData quopnData2 = new QuopnData();
//				quopnData2
//						.setCampaign(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN)));
//				quopnData2
//						.setThumb_icon(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON)));
//
//				quopnData2
//						.setQuopnId(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID)));
//				allQuopnIdInJson.append(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID))+",");
//
//				quopnData2
//						.setCall_to_action(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION)));
//				quopnData2
//						.setCta_text(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT)));
//				quopnData2
//				.setMaster_tag(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG)));
//				quopnData2
//						.setMastertag_url(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL)));
//				quopnData2
//						.setCta_value(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE)));
//
//				quopnData2
//						.setEndingdate(cursor.getString(cursor
//								.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_END_DATE)));
//
//				quopnData2.setProductname(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME)));
//
//				quopnData2.setShort_desc(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC)));
//
//				quopnData2.setBrand(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_BRAND)));
//
//				quopnData2.setAvailable_quopns(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS)));
//
//				quopnData2.setAlready_issued(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED)));
//
//				quopnData2.setDescription_highlight(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC)));
//
//				quopnData2.setDescription_end(cursor.getString(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_END_DESC)));
//
//				quopnData2.setSort_index(cursor.getInt(cursor
//						.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX)));
//
//				container.setQuopnData2(quopnData2);
//			}

			listQuopnContainer.add(container);

		}
		
		cursor.close();
		allQuopnIdInJson.append("]");
		allQuopnIdInJson.append("|");
		allQuopnIdInJson.append(ProductCatFragment.QUOPN_CATEGORY_ID);
		allQuopnIdInJson.append("|");
		allQuopnIdInJson.append(mPagerPosition);
		mAnalysisManager.send(AnalysisEvents.IMPRESSION, allQuopnIdInJson.toString());
		return listQuopnContainer;

	}
	
	/**
	 * mCategoryListAdapter = new CategoryListAdapter(getActivity(), 0,
					mListCategoryContainer, mCategorySelectedListener);
	 * @param cursor
	 * @return
	 * 
	 * this method takes cursor as a param and converts it into a list of Quopns.
	 * since we have used ListView to display quopns as a grid type, we have a container class ListQuopnContainer
	 * which contains two quopn objects.
	 */
	@SuppressWarnings("unused")
	private List<GiftsContainer> convertGiftListDatafromCursor(
			Cursor cursor) {
		List<GiftsContainer> listgiftContainer = new ArrayList<GiftsContainer>();

		while (cursor.moveToNext()) {

            GiftsContainer container = new GiftsContainer();

            container.setGIFT_STATE(cursor.getInt(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.GIFT_STATE)));
            container.setCampaignname(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.CAMPAIGN_NAME)));
            container.setThumb_icon(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.THUMB_ICON)));
            container.setId(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.COLUMN_GIFT_ID)));
            container.setCall_to_action(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.CALL_TO_ACTION)));
            container.setCta_text(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.CTA_TEXT)));
            container.setCta_value(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.CTA_VALUE)));
            container.setMaster_tag(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.MASTER_TAG)));
            container.setMastertag_image(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.MASTER_TAG_IMAGE)));
            container.setEnddate(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.END_DATE)));
            container.setProductname(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.PRODUCT_NAME)));
            container.setShort_desc(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.SHORT_DESC)));
            container.setBig_image(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.BIG_IMAGE)));
            container.setLong_desc(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.LONG_DESC)));
            container.setGift_type(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.GIFT_TYPE)));
            container.setPartner_code(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.PARTNER_CODE)));
            container.setTerms_cond(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.TERMS_COND)));
            container.setSort_index(cursor.getInt(cursor
                    .getColumnIndex(ITableData.TABLE_GIFTS.SORT_INDEX)));



            listgiftContainer.add(container);

        }
		
		cursor.close();
		
		return listgiftContainer;

	}

    private List<VoucherList> convertVoucherListDatafromCursor(
            Cursor cursor) {
        List<VoucherList> listvoucherContainer = new ArrayList<VoucherList>();

        while (cursor.moveToNext()) {

            VoucherList container = new VoucherList();

            container.setPartner_id(cursor.getInt(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.COLUMN_PARTNER_ID)));
            container.setPartner_name(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.PARTNER_NAME)));
            container.setThumb_icon(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.THUMB_ICON)));
            container.setBig_image(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.BIG_IMAGE)));
            container.setTotal_coupons(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.TOTAL_COUPONS)));
            container.setAvailable_coupons(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.AVAILABLE_COUPONS)));
            container.setIssue_available(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.ISSUE_AVAILABLE)));
            container.setIssued_coupons(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.ISSUED_COUPONS)));
            container.setPurchase_value(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_VOUCHER.PURCHASE_VALUE)));

            listvoucherContainer.add(container);

        }

        cursor.close();

        return listvoucherContainer;

    }

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param position
	 * this method takes the position and depending on it shows the data to the use.
	 * Type : category, quopn and gifting.
	 * 
	 */
	@SuppressLint("DefaultLocale") public void updateViews(String position) {
		
		if(MainActivity.GIFT_TITLE != null && MainActivity.GIFT_TITLE.equalsIgnoreCase(position)){

			View footer = mParentActivity.getLayoutInflater().inflate(R.layout.footerview, null);
			mfooter_text=(QuopnTextView)footer.findViewById(R.id.footer_text);
			mskip_text=(QuopnTextView)footer.findViewById(R.id.skip_text);
			
			mListGiftsContainer.clear();
			Cursor cursor_gifts = mParentActivity.getContentResolver().query(
					ConProvider.CONTENT_URI_GIFTS, null, null, null, ITableData.TABLE_GIFTS.SORT_INDEX /*+ " desc"*/);
			
			if(cursor_gifts == null || cursor_gifts.getCount() == 0){
				mListView.setVisibility(View.INVISIBLE);
				mSwipeRefreshLayout.setVisibility(View.INVISIBLE);				
				mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
			} else {
				mListGiftsContainer.addAll(convertGiftListDatafromCursor(cursor_gifts));
				mListView.setVisibility(View.VISIBLE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
				myQuopnsAdapter.notifyDataSetChanged();
			}
			
//			if (mListView.getFooterViewsCount()==0) {
////				mListView.addFooterView(footer);
//			}
			footer.setVisibility(View.GONE);
			
			cursor_gifts.close();
			
			/*
			 * Gift Video Related stuff starts 
			 * 
			 */
			personal_message_downloaded=PreferenceUtil.getInstance(mParentActivity).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_PATH);
			if(personal_message_downloaded!=null){ //video URL available
//				mfooter_text.setText(getResources().getString(R.string.personal_message));
//				mfooter_text.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						QuopnUtils.sendVideoForGift(personal_message_downloaded, getActivity());
//					}
//				});
			}else{  //video URL NOT available
				mPersonalVideoUrl=PreferenceUtil.getInstance(mParentActivity).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL);
				if(mPersonalVideoUrl!=null){
//					mQuopnDownloadManager=new QuopnDownloadManager(getActivity(), mPersonalVideoUrl,ProductCategoryListFragment.this);
//					mfooter_text.setText(getResources().getString(R.string.download_message));
//					mskip_text.setText(getResources().getString(R.string.skip_message));
//					mfooter_text.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							mskip_text.setVisibility(View.VISIBLE);
//							mQuopnDownloadManager.startDownload();
//							mfooter_text.setVisibility(View.GONE);
//						}
//					});
//					mskip_text.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							mskip_text.setVisibility(View.GONE);
//							mfooter_text.setVisibility(View.VISIBLE);
//								mQuopnDownloadManager.cancelDownloading();
//								mQuopnDownloadManager.destroy();
//						}
//					});
				}else{
					footer.setVisibility(View.GONE);
				}
			}
			/*
			 * Gift Video Related stuff ends 
			 * 
			 */
			
//			mHeader.setText(getActivity().getResources().getString(R.string.gift_page_title));
			mHeader.setVisibility(View.GONE);
		}
        else if("VOUCHERS".equalsIgnoreCase(position)){
            mListCategoryContainer.clear();
            Cursor cursor11 = mParentActivity.getContentResolver().query(
                    ConProvider.CONTENT_URI_VOUCHER, null, null, null, ITableData.TABLE_VOUCHER.COLUMN_ID);
            mVoucherList.addAll(convertVoucherListDatafromCursor(cursor11));
            mHeader.setVisibility(View.GONE);
            mVoucherListAdapter.notifyDataSetChanged();
        }
        else if("Categories".equalsIgnoreCase(position)){
			mListCategoryContainer.clear();
			Cursor cursor0 = mParentActivity.getContentResolver().query(
					ConProvider.CONTENT_URI_CATEGORY, null, null, null, ITableData.TABLE_CATEGORY.COLUMN_SEQUENCE);
			mListCategoryContainer.addAll(convertCategoryListDatafromCursor(cursor0));
			mHeader.setVisibility(View.GONE);
			mCategoryListAdapter.notifyDataSetChanged();
		}else if("All".equalsIgnoreCase(position)){
			mListQuopnContainer.clear();
			Cursor cursor1 = null;
				
			String searchCondition = null;
			String[] searchValues = null;
			
			if (QuopnConstants.SEARCHTEXT != null
					&& !QuopnConstants.SEARCHTEXT.equals("")) {
				
				String searchTerm = "%" + QuopnConstants.SEARCHTEXT + "%";
					
				searchCondition = ITableData.TABLE_QUOPNS.COLUMN_SEARCH_TAGS 
			        + " LIKE ? OR "
					+ ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN
					+ " LIKE ? OR "
					+ ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC
					+ " LIKE ? OR "
					+ ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC
					+ " LIKE ? OR "
					+ ITableData.TABLE_QUOPNS.COLUMN_BRAND
					+ " LIKE ? ";
				
				String[] values = {
					searchTerm, searchTerm, searchTerm, searchTerm, searchTerm
				};
				searchValues = values;
			}
			
			cursor1 = mParentActivity.getContentResolver().query(
					ConProvider.CONTENT_URI_QUOPN
					, null
					, searchCondition, searchValues,
					ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
//			mHeader.setText(ProductCatFragment.QUOPN_CATEGORY_TYPE + " QUOPNS");
			mHeader.setVisibility(View.GONE);
			
			if(cursor1 == null || cursor1.getCount() == 0){
				mListView.setVisibility(View.INVISIBLE);
				mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
				mImgNoQuopns.setImageResource(R.drawable.search_quopns);
				mNoQuoponText.setText(getResources().getString(R.string.search_quopns));
				mImgNoQuopnsButton.setVisibility(View.VISIBLE);
				mImgNoQuopnsButton.setTag(mTagSearchQuopns);
				mImgNoQuopnsButton.setOnClickListener(this);
			} else {
				mListQuopnContainer.addAll(convertQuopnListDatafromCursor(cursor1));
				mListView.setVisibility(View.VISIBLE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
				mQuopnListAdapter.notifyDataSetChanged();
			}
			
			cursor1.close();
			
		} else if("Expiring".equalsIgnoreCase(position)){
			try {
				mListQuopnContainer.clear();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				String currentMillis = formatter.format(new Date(System.currentTimeMillis()));
				
				long sevenDayMillis = 60 * 60 * 24 * 1000 * 7;
				long endDayMillis = System.currentTimeMillis() + sevenDayMillis;
				String endDayMillisString = formatter.format(new Date(endDayMillis));
				
				Cursor cursor1 = null;
				if (!ProductCatFragment.QUOPN_CATEGORY_TYPE
						.equals(ProductCatFragment.DEFAULT_CATEGORY_TYPE)) {
					
					cursor1 = mParentActivity.getContentResolver().query(
							ConProvider.CONTENT_URI_QUOPN, null,
							ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID + " = ? AND " + ITableData.TABLE_QUOPNS.COLUMN_END_DATE + " BETWEEN ? AND ? ",
							new String[] { ProductCatFragment.QUOPN_CATEGORY_ID, currentMillis, endDayMillisString},
							ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
//					mHeader.setText(getActivity().getResources().getString(R.string.expiring_page_title));
					mHeader.setVisibility(View.GONE);
				} else {
					cursor1 = mParentActivity.getContentResolver().query(
							ConProvider.CONTENT_URI_QUOPN, null,
							ITableData.TABLE_QUOPNS.COLUMN_END_DATE + " BETWEEN ? AND ? ",
							new String[] { currentMillis,endDayMillisString},
							ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
//					mHeader.setText(mParentActivity.getResources().getString(R.string.expiring_page_title));
					mHeader.setVisibility(View.GONE);
				}
				
				
				if(cursor1 == null || cursor1.getCount() == 0){
					mImgNoQuopns.setImageResource(R.drawable.expiring_quopns);
					mNoQuoponText.setText(getResources().getString(R.string.expiring_quopns_soon));
					mImgNoQuopnsButton.setVisibility(View.VISIBLE);
					mImgNoQuopnsButton.setTag(mTagNewQuopns);
					mImgNoQuopnsButton.setOnClickListener(this);
					mListView.setVisibility(View.INVISIBLE);
					mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
				} else{
					mListQuopnContainer.addAll(convertQuopnListDatafromCursor(cursor1));
					mListView.setVisibility(View.VISIBLE);
					mSwipeRefreshLayout.setVisibility(View.VISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
					mQuopnListAdapter.notifyDataSetChanged();
				}
				
				cursor1.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("Category".equalsIgnoreCase(position)) {
			mListQuopnContainer.clear();
			
			showSearchBar(false);

			Cursor cursor1 = mParentActivity.getContentResolver().query(
					ConProvider.CONTENT_URI_QUOPN, null,
					ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID + " = ?",
					new String[] { ProductCatFragment.QUOPN_CATEGORY_ID},
					ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
			
			if(cursor1 == null || cursor1.getCount() == 0){
				mImgNoQuopnsButton.setTag(mTagNewQuopns_Category);
				mImgNoQuopnsButton.setOnClickListener(this);
				mListView.setVisibility(View.INVISIBLE);
				mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
			} else{
				mListQuopnContainer.addAll(convertQuopnListDatafromCursor(cursor1));
				mListView.setVisibility(View.VISIBLE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
				mQuopnListAdapter.notifyDataSetChanged();
			}
//			mHeader.setText(ProductCatFragment.QUOPN_CATEGORY_TYPE + " QUOPNS");
			mHeader.setVisibility(View.GONE);
			
		}else if(getResources().getString(R.string.search).equalsIgnoreCase(position)){
			if (!TextUtils.isEmpty(QuopnConstants.SEARCHTEXT)) {
				mListQuopnContainer.clear();
				String searchString = "%" + QuopnConstants.SEARCHTEXT + "%";
				Cursor cursor6 = mParentActivity.getContentResolver().query(
						ConProvider.CONTENT_URI_QUOPN,
						null,
						ITableData.TABLE_QUOPNS.COLUMN_SEARCH_TAGS 
						        + " LIKE ? OR "
								+ ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN
								+ " LIKE ? OR "
								+ ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC
								+ " LIKE ? OR "
								+ ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC
								+ " LIKE ? OR "
								+ ITableData.TABLE_QUOPNS.COLUMN_BRAND
								+ " LIKE ? ",
						new String[] { searchString, searchString,
								searchString, searchString }, ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
				
				
				if(cursor6 == null || cursor6.getCount() == 0){
					mListView.setVisibility(View.INVISIBLE);
					mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
					mNoQuoponText.setText("NO SEARCH RESULTS FOR : "+searchString.replace("%", ""));
					QuopnConstants.SEARCHTEXT = "";
				} else {
					mListQuopnContainer.addAll(convertQuopnListDatafromCursor(cursor6));
					mListView.setVisibility(View.VISIBLE);
					mSwipeRefreshLayout.setVisibility(View.VISIBLE);
					mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
					mQuopnListAdapter.notifyDataSetChanged();
				}
				
				cursor6.close();
				
			} else {
				mListView.setVisibility(View.INVISIBLE);
				mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
				mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
//				mNoQuoponText.setText("PLEASE CLICK SEARCH ICON TO SEARCH!");
				mNoQuoponText.setText(R.string.search_quopns);
				mImgNoQuopns.setImageResource(R.drawable.search_quopns);
//				mImgNoQuopnsButton.setVisibility(View.INVISIBLE);
				mImgNoQuopnsButton.setText(R.string.search_now);
				mImgNoQuopnsButton.setTag(mTagSearchQuopns);
				mImgNoQuopnsButton.setOnClickListener(this);
			}
//			mHeader.setText(position.toUpperCase());
			mHeader.setVisibility(View.GONE);
		}else{
			getCategoryDataFromDB(position);
		}
		
	}

	@Override
	public void onRefresh() {
		updateQuopnList();
		/*mProgressDialog = new ProgressDialog(mProductCategoryListFragment.getActivity(), "",Color.RED);
		mProgressDialog.show();*/
		
		new Timertask().execute();
	}

	/**
	 * 
	 * @author Sumeet
	 *
	 */
	private class Timertask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.currentTimeMillis();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//mProgressDialog.dismiss();
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefresh_empty_content_layout.setRefreshing(false);
			mListView.onRefreshComplete();
		}
	}
	
	
	private void updateQuopnList()
	{
		Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
		LocalBroadcastManager.getInstance(mParentActivity).sendBroadcast(intent);
	}
	
	private void getCategoryDataFromDB(String position){
		Cursor cursor = null;
		mListQuopnContainer.clear();
		if (!ProductCatFragment.QUOPN_CATEGORY_TYPE
				.equals(ProductCatFragment.DEFAULT_CATEGORY_TYPE)) {
			cursor = mParentActivity
					.getContentResolver()
					.query(ConProvider.CONTENT_URI_QUOPN,
							null,
							ITableData.TABLE_QUOPNS.COLUMN_FOOTER_TAG
									+ " like ? AND "
									+ ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID
									+ " = ? ",
							new String[] {
									"%" + position.toLowerCase() + "%",
									ProductCatFragment.QUOPN_CATEGORY_ID },
									ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
//			mHeader.setText(ProductCatFragment.QUOPN_CATEGORY_TYPE + " QUOPNS");
			mHeader.setVisibility(View.GONE);
		} else {
			cursor = mParentActivity.getContentResolver().query(
					ConProvider.CONTENT_URI_QUOPN,
					null,
					ITableData.TABLE_QUOPNS.COLUMN_FOOTER_TAG + " like ? ",
					new String[] { "%" + position
							.toLowerCase() + "%"}, ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
//			mHeader.setText(position.toUpperCase() + " QUOPNS");
			mHeader.setVisibility(View.GONE);

		}
		if(cursor == null || cursor.getCount() == 0){
			mListView.setVisibility(View.INVISIBLE);
			mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
			mSwipeRefresh_empty_content_layout.setVisibility(View.VISIBLE);
			mImgNoQuopnsButton.setTag(mTagNewQuopns);
			mImgNoQuopnsButton.setOnClickListener(this);
		} else{
			mListQuopnContainer.addAll(convertQuopnListDatafromCursor(cursor));
			mListView.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setVisibility(View.VISIBLE);
			mSwipeRefresh_empty_content_layout.setVisibility(View.INVISIBLE);
			mQuopnListAdapter.notifyDataSetChanged();
		}
		
		cursor.close();
	}

	@Override
	public void onDownloadCancel() {
		Toast.makeText(mParentActivity, "Download Cancelled", Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onDownloadFailed() {
		Toast.makeText(mParentActivity, "Unable to Download personal message", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDownloadComplete(String filePath) {
		mskip_text.setVisibility(View.GONE);
		mfooter_text.setVisibility(View.VISIBLE);
		mfooter_text.setText(getResources().getString(R.string.personal_message));
		mfooter_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				QuopnUtils.sendVideoForGift(personal_message_downloaded/*QuopnConstants.GIFTVIDEO_URL*/,getActivity()); //added video for gift sandeep
			}
		});
		PreferenceUtil.getInstance(mParentActivity).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_PATH, filePath);
		Toast.makeText(mParentActivity, "Download Complete", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.img_no_new_quopns_text){
			TextView imgView = (TextView)v;
			String tag = (String) imgView.getTag();
			if(tag.equals(mTagNewQuopns)){
				//switch to all tab
				Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
				LocalBroadcastManager.getInstance(mParentActivity).sendBroadcast(intent);
			}else if(tag.equals(mTagNewQuopns_Category)){
				//switch to all tab
				mParentActivity.finish();
				Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
				LocalBroadcastManager.getInstance(mParentActivity).sendBroadcast(intent);
			} else if (tag.equals(mTagSearchQuopns)){
				if (!mPagerPosition.equals("All")) {
					Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
					LocalBroadcastManager.getInstance(mParentActivity).sendBroadcast(intent);
				} else {
					clearSearch();
				}
			}
		} else if (v.getId() == R.id.etSearchQuopns) {
			onSearchFocusedListener.onSearchFocused();
		} else if (v.getId() == R.id.ivSearchQuopns) {
			onSearchFocusedListener.onSearchFocused();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) { onSearchFocusedListener.onSearchFocused(); }
	}

	public void setOnSearchFocusedListener(OnSearchFocusedListener listener) {
		onSearchFocusedListener = listener;
	}
	
	public void focusOnSearch() {
		if (etSearchQuopns.requestFocus()) {
			InputMethodManager imm = (InputMethodManager)
					mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(etSearchQuopns, InputMethodManager.SHOW_IMPLICIT);
        }
	}
	
	public void unfocusFromSearch() {
		boolean success = vwSwallowAutoFocus.requestFocus();
		InputMethodManager imm = (InputMethodManager)
				mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchQuopns.getWindowToken()
    		, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void showSearchBar(boolean shouldShow) {
		rlSearchQuopns.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
	}
	
	public void clearSearch() {
		etSearchQuopns.setText("");
	}
}
