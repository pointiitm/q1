/**
 * 
 */
package com.quopn.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.views.CustomProgressDialog;

/**
 * This class is use for showing tour to user Basically here is three function
 * for control tour previous/next/skip
 * 
 * @author ravi
 * */
public class TourActivity extends Activity {
	private static final String TAG = "TourActivity";

	private static final int PREVIOUS = 0x0001;
	private static final int NEXT = 0x0002;
	private static final int SKIP = 0x0003;
	private static final int[] TOUR_SEQUENCE = new int[] { R.drawable.tour01 };

	private ImageView tourImage;
	private TextView mPrevious, mNext, mSkip,firsNext;
	private LinearLayout nextAndPreviousBar;
	private Handler mTourHandler;
	private TypedArray tourDrawables;
	private Drawable tourDrawable;
	private int tourIndex = 0;
	private String mPersonalVideoUrl=null,mComingFrom;
	private AnalysisManager mAnalysisManager;
	private int num_of_tour_viewed = 1;//by default user will atleast view first slide.


	private static final int NUM_PAGES = 10;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private int[] mResources = {
			R.drawable.tour01,
			R.drawable.tour02,
			R.drawable.tour03,
			R.drawable.tour04,
			R.drawable.tour05,
			R.drawable.tour06,
			R.drawable.tour07,
			R.drawable.tour08,
			R.drawable.tour09,
			R.drawable.tour10,
	};
	private TextView textViewButton;
	private CustomProgressDialog mCustomProgressDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.tour);
		textViewButton = (TextView) findViewById(R.id.skip);
		textViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCustomProgressDialog = new CustomProgressDialog(TourActivity.this);
				mCustomProgressDialog.show();
				finish();
			}
		});
		mAnalysisManager=((QuopnApplication)getApplicationContext()).getAnalysisManager();
		PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.TOUR_REACHED, true);
		mPersonalVideoUrl = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL);
		
		
		
		init();
		mTourHandler = new Handler() {
			@Override
			public void handleMessage(Message msssage) {
				switch (msssage.what) {
				case PREVIOUS:
					previous();
					break;
				case NEXT:
					next();
					break;
				case SKIP:
					skip();
					break;
				}
			}
		};




		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(this);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(position < (NUM_PAGES -1)){
					textViewButton.setText("SKIP");
				} else if(position == (NUM_PAGES -1)){
					textViewButton.setText("GET STARTED");
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}


	class ScreenSlidePagerAdapter extends PagerAdapter {

		Context mContext;
		LayoutInflater mLayoutInflater;

		public ScreenSlidePagerAdapter(Context context) {
			mContext = context;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View itemView = mLayoutInflater.inflate(R.layout.tour_app_img, container, false);
			ImageView imageView = (ImageView) itemView.findViewById(R.id.tourImage);
			imageView.setImageResource(mResources[position]);
			container.addView(itemView);

			return itemView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((RelativeLayout) object);
		}
	}


	public void init() {
		
		if(getIntent().getExtras()!=null)
		mComingFrom=getIntent().getExtras().getString("coming_from");

//		nextAndPreviousBar = (LinearLayout) this.findViewById(R.id.nextAndPreviousBar);
		tourImage = (ImageView) this.findViewById(R.id.tourImage);
//		firsNext = (ImageButton) this.findViewById(R.id.firsNext);
//		mPrevious = (ImageButton) this.findViewById(R.id.previous);
//		mNext = (ImageButton) this.findViewById(R.id.next);
		mSkip = (TextView) this.findViewById(R.id.skip);
		tourDrawables = getResources().obtainTypedArray(R.array.tour_sequence);
		
		/*if(tourIndex==0){
			nextAndPreviousBar.setVisibility(View.GONE);
			firsNext.setVisibility(View.VISIBLE);
		}else{
			nextAndPreviousBar.setVisibility(View.VISIBLE);
			firsNext.setVisibility(View.GONE);
		}*/
			

	}

	public void onClick(View view) {
		switch (view.getId()) {
		/*case R.id.previous:
			mTourHandler.sendEmptyMessage(PREVIOUS);
			break;
		case R.id.firsNext:
			mTourHandler.sendEmptyMessage(NEXT);
			break;
		case R.id.next:
			mTourHandler.sendEmptyMessage(NEXT);
			break;*/
		case R.id.skip:
			mTourHandler.sendEmptyMessage(SKIP);
			break;
		}
	}

	/**This method is use for back tracking of Application tour
	 * */
	private void previous() {
		try{
		if ((--tourIndex) >= 0) {
			if(tourIndex==0){
				nextAndPreviousBar.setVisibility(View.GONE);
				firsNext.setVisibility(View.VISIBLE);
			}else{
				nextAndPreviousBar.setVisibility(View.VISIBLE);
				firsNext.setVisibility(View.GONE);
			}
			
			tourDrawable = tourDrawables.getDrawable(tourIndex);
			tourImage.setImageDrawable(tourDrawable);
		}else{
			tourIndex=0;
		}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
		
	}

	private void next() {
		try{
			//here increase the tuts view count;
			num_of_tour_viewed++;
			
		if ((++tourIndex) <= (tourDrawables.length() - 1)) {
			
			if(tourIndex==0){
				nextAndPreviousBar.setVisibility(View.GONE);
				firsNext.setVisibility(View.VISIBLE);
			}else{
				nextAndPreviousBar.setVisibility(View.VISIBLE);
				firsNext.setVisibility(View.GONE);
			}
			
			tourDrawable = tourDrawables.getDrawable(tourIndex);
			tourImage.setImageDrawable(tourDrawable);
		}else{
			
			//Set the analysis code here for the number of pages visited
			mAnalysisManager.send(AnalysisEvents.TOUR, Integer.toString(num_of_tour_viewed));
			
			if(mPersonalVideoUrl !=null && mComingFrom!=null && mComingFrom.equalsIgnoreCase("ProfileCompletionScreen")){
				Intent gotoGiftInfo = new Intent(this, GiftInfo.class);
				startActivity(gotoGiftInfo);
				finish();
			}else{
				Intent intent = new Intent(TourActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
			
			
		}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}

	private void skip() {
		
		//Set the analysis code here for the number of pages visited
		mAnalysisManager.send(AnalysisEvents.TOUR, Integer.toString(num_of_tour_viewed));
		
		if(mPersonalVideoUrl !=null && mComingFrom!=null && mComingFrom.equalsIgnoreCase("ProfileCompletionScreen")){
			Intent gotoGiftInfo = new Intent(TourActivity.this, GiftInfo.class);
			startActivity(gotoGiftInfo);
			finish();
		}else{
			Intent intent = new Intent(TourActivity.this, MainActivity.class);
			intent.putExtra(QuopnConstants.INTENT_KEYS.should_announcement_be_called, false);
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mAnalysisManager!=null){
			mAnalysisManager.close();
		}
	}

}
