package com.quopn.wallet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.adapter.HistoryAdapter;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.model.CartListData;
import com.quopn.wallet.data.model.HistoryData;
import com.quopn.wallet.data.model.HistoryListData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.QuopnTextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG="HistoryActivity";
	private ListView mListview;
	private ConnectionListener mConnectionListener_history;
	private List<HistoryListData> mHistoryListContainer = new ArrayList<HistoryListData>();
	private HistoryAdapter mHistoryAdapter;
	private String mTitle;

	private int mColorRes = -1;
	private RelativeLayout mRelLayNoQuopns;
	private ImageView mImgNoQuopns;
	private TextView mTextNoQuopns;
	private TextView mImgNoQuopnsButton;
	private TextView mHeader_text,mSaving_text2;
	private TextView mSaving_text1;
	private String mTagMyHistory = "My History";
	static OnClickListener mConfirmDialogListener;
	List<CartListData> newList;
	private double mTotalSaving=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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
		home_btn.setOnClickListener(new View.OnClickListener() {

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
		
		setContentView(R.layout.myhistoryproducts);
		mListview = (ListView) findViewById(R.id.items);
		mRelLayNoQuopns = (RelativeLayout) findViewById(R.id.rellay_empty_content);
		mImgNoQuopns = (ImageView) findViewById(R.id.img_empty_content);
		mTextNoQuopns = (QuopnTextView) findViewById(R.id.text_empty_content);
		mImgNoQuopnsButton = (TextView) findViewById(R.id.img_empty_content_text);
		mHeader_text=(TextView)findViewById(R.id.header_text);
		mHeader_text.setText(getString(R.string.history));
		
		mSaving_text1 = (TextView) findViewById(R.id.saving_text1);
		mSaving_text1.setTypeface(null, Typeface.BOLD);
		
		mSaving_text2=(TextView)findViewById(R.id.saving_text2);
		mSaving_text2.setTypeface(null, Typeface.BOLD);
		setSavingsAmount(0);
		
		
		mConnectionListener_history=new ConnectionListener() {
			@Override
			public void onResponse(int responseResult,Response response) {
				if (response instanceof HistoryData) {
					HistoryData historydata = (HistoryData) response;
					mHistoryListContainer.clear();
					mTotalSaving=0;
					
						if (historydata.isError() == true) {
							setSavingsAmount(mTotalSaving);
							mListview.setVisibility(View.INVISIBLE);
							mRelLayNoQuopns.setVisibility(View.VISIBLE);
							mImgNoQuopns.setImageResource(R.drawable.my_history_empty);
							mTextNoQuopns.setText(R.string.text_empty_history);
							mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
							mImgNoQuopnsButton.setText(R.string.browse_current_categories);
							mImgNoQuopnsButton.setTag(mTagMyHistory);
							mImgNoQuopnsButton.setOnClickListener(HistoryActivity.this);
							mListview.setAdapter(mHistoryAdapter);
						}else {
								mHistoryListContainer.addAll(historydata.getHistorydetails());
						}
						if (mHistoryAdapter.getCount()>0) {
							mTotalSaving=Double.parseDouble(historydata.getTotal_savings());
							setSavingsAmount(mTotalSaving);
							mRelLayNoQuopns.setVisibility(View.INVISIBLE);
							mListview.setVisibility(View.VISIBLE);
							mListview.setAdapter(mHistoryAdapter); 
							
						} else {
							setSavingsAmount(mTotalSaving);
							mListview.setVisibility(View.INVISIBLE);
							mRelLayNoQuopns.setVisibility(View.VISIBLE);
							mImgNoQuopns.setImageResource(R.drawable.my_history_empty);
							mTextNoQuopns.setText(R.string.text_empty_history);
							mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
							mImgNoQuopnsButton.setText(R.string.browse_current_categories);
							mImgNoQuopnsButton.setTag(mTagMyHistory);
							mImgNoQuopnsButton.setOnClickListener(HistoryActivity.this);
							mListview.setAdapter(mHistoryAdapter);
						}
						mHistoryAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				mHistoryListContainer.clear();
				mTotalSaving=0;
				setSavingsAmount(mTotalSaving);
				mListview.setVisibility(View.INVISIBLE);
				mRelLayNoQuopns.setVisibility(View.VISIBLE);
				mImgNoQuopns.setImageResource(R.drawable.my_history_empty);
				mTextNoQuopns.setText(R.string.text_empty_history);
				mImgNoQuopnsButton.setBackgroundResource(R.drawable.no_quopns_blankbutton);
				mImgNoQuopnsButton.setText(R.string.browse_current_categories);
				mImgNoQuopnsButton.setTag(mTagMyHistory);
				mImgNoQuopnsButton.setOnClickListener(HistoryActivity.this);
				mListview.setAdapter(mHistoryAdapter);
			}

			@Override
			public void myTimeout(String requestTag) {

			}
		};
		if (QuopnUtils.isInternetAvailable(HistoryActivity.this)) {
			QuopnUtils.getHistory(HistoryActivity.this,mConnectionListener_history);
		}else{
			mTextNoQuopns.setText(R.string.please_connect_to_internet);
		}
		
	}

	public void setSavingsAmount(double argAmount){
		mSaving_text2.setText(getResources().getString(R.string.rupee_symbol) + mTotalSaving);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHistoryAdapter = new HistoryAdapter(HistoryActivity.this, 0,mHistoryListContainer);
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.img_empty_content_text){
			TextView imgView = (TextView)v;
			String strTag = (String) imgView.getTag();
			Intent intent = new Intent(QuopnConstants.BROADCAST_SWITCH_TO_ALL_TAB);
			LocalBroadcastManager.getInstance(HistoryActivity.this).sendBroadcast(intent);
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);
		if (resultcode == RESULT_OK) {
			switch (requestcode) {
			case QuopnConstants.HOME_PRESS:
				finish();
				break;

			default:
				break;
			}

		}

	}
	

}
