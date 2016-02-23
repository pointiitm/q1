package com.quopn.wallet;

/**
 * @author Sandeep
 *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.model.UCNNumberData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.AspectRatioImageView;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.SlidingUpPanelLayout;

@SuppressLint("SetJavaScriptEnabled")
public class GiftDetailsActivity extends ActionBarActivity implements
		ConnectionListener, QuopnOperationsListener {

	private DisplayImageOptions mOptions;
	private DisplayImageOptions mOptions_featuredtag;
	
	private static final String EXTRA_IMAGE = "GiftDetailsActivity";
	private static final String EXTRA_IMAGE_01 = "title";
	private static final String TAG = "Quopn/GiftDetailsActivity";

	private String cta_text = "";
	private String cta_value = "";
	private String ucn_id = "";
	private String source = "";
	private String campaign_id = "";
	private SlidingUpPanelLayout mSlidinguppanellayout;
	private boolean isTutShown = false;
	private ConnectionListener webissue_connectionlistener;
	private String mProductname;
	private WebView termscodition_webview;
	private CustomProgressDialog mCustomProgressDialog;
	private AnalysisManager mAnalysisManager;
	private ScrollView productdescription_scrollviewlayout;
	private static GiftDetailAddToCartListener mGiftDetailAddToCartListener;
	private String mPartnercode;
	private String mTermsAndCond;
	private static boolean mFlag=false;
	private ImageView mProgressbar;

	public interface GiftDetailAddToCartListener {
		void onGiftDetailAddToCartSuccess();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		mAnalysisManager=((QuopnApplication)getApplicationContext()).getAnalysisManager();
//		if (!isTutShown
//				&& QuopnConstants.TUTORIAL_ON.equals(PreferenceUtil
//						.getInstance(this).getPreference(
//								QuopnConstants.TUTORIAL_PREF_DETAILS))) {
//			isTutShown = true;
//			startActivity(
//					new Intent(this, QuopnDetailsTutorial.class));
//		}

		
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

		setContentView(R.layout.product_detail);
//		mCustomProgressDialog = new CustomProgressDialog(this);
//		mCustomProgressDialog.show();

		String giftsid = getIntent().getExtras().getString("id");
		String giftscomapignname = getIntent().getExtras().getString("giftscomapignname");
		String giftsbigImage = getIntent().getExtras().getString("giftsbigImage");
		String giftslongdesc = getIntent().getExtras().getString("giftslongdesc");
		String giftstermsncondition = getIntent().getExtras().getString("giftstermsncondition");
		String giftsctatext = getIntent().getExtras().getString("giftsctatext");
		String giftsctavalue = getIntent().getExtras().getString("giftsctavalue");
		String giftssource = getIntent().getExtras().getString("giftssource");
		String giftsmastertag = getIntent().getExtras().getString("giftsmastertag");
		String gifttype = getIntent().getExtras().getString("gifttype");
		String giftpartnercode = getIntent().getExtras().getString("giftpartnercode");
		mProductname = getIntent().getExtras().getString("productname");
		mTermsAndCond=getIntent().getExtras().getString("termscondition");
		if(gifttype.equals("E")){
			mPartnercode=giftpartnercode;
		}
		

		mSlidinguppanellayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		AspectRatioImageView icon1 = (AspectRatioImageView) findViewById(R.id.row_icon);
		productdescription_scrollviewlayout=(ScrollView)findViewById(R.id.productdescription_scrollviewlayout);
		ViewCompat.setTransitionName(icon1, EXTRA_IMAGE);

		ImageView featuredtag_Image = (ImageView) findViewById(R.id.featuredtag_btn);

		TextView productdescription_title_tv = (TextView) findViewById(R.id.productdescription_title_tv);
		productdescription_title_tv.setTypeface(null, Typeface.BOLD);
//		ViewCompat.setTransitionName(productdescription_title_tv, EXTRA_IMAGE_01);
		
		TextView productdescription_tv = (TextView) findViewById(R.id.productdescription_tv);
		final ImageView call_to_action = (ImageView) findViewById(R.id.call_to_action_btn);
		ImageView share_btn = (ImageView) findViewById(R.id.share_btn);
		ImageView close_btn = (ImageView) findViewById(R.id.close_btn);
		ImageView termscodition_btn = (ImageView) findViewById(R.id.termscodition_btn);
		termscodition_webview = (WebView) findViewById(R.id.termscodition_web);
        mProgressbar=(ImageView)findViewById(R.id.progressBar_individualItem);

		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		mOptions = builder.cacheOnDisc(true).cacheInMemory(true)
				.resetViewBeforeLoading(true)
				.showStubImage(R.drawable.placeholder_image).build();
		DisplayImageOptions.Builder builder1 = new DisplayImageOptions.Builder();
		mOptions_featuredtag=builder1.cacheOnDisc(true).cacheInMemory(true)
				.resetViewBeforeLoading(true)
				.build();
		
		if(gifttype.equals("E")){
			productdescription_title_tv.setText(Html.fromHtml(mProductname.toUpperCase())+"\n"+QuopnConstants.PARTNER_CODE+": "+mPartnercode);
		}else{
			productdescription_title_tv.setText(Html.fromHtml(mProductname.toUpperCase()));
		}
		
		ImageLoader.getInstance().displayImage(giftsbigImage,icon1, mOptions);
		ImageLoader.getInstance().displayImage(giftsmastertag,featuredtag_Image, mOptions_featuredtag);
		
	
		
		productdescription_tv.setText(Html.fromHtml(giftslongdesc));
		cta_text = giftsctatext;
		cta_value = giftsctavalue;
		ucn_id=giftsid;
		source=giftssource;
		campaign_id=giftsid;
		
		if(gifttype.equals("E")){
			termscodition_webview.getSettings().setJavaScriptEnabled(true);
			termscodition_webview.loadDataWithBaseURL(null, mTermsAndCond, "text/html", "utf-8", null);
		}else{
			termscodition_webview.setWebViewClient(new myWebClient());
			termscodition_webview.getSettings().setJavaScriptEnabled(true);
			termscodition_webview.loadUrl(QuopnApi.TERMS_AND_CONDITION_DETAILS_URL+"/"+campaign_id);
		}
		

		
		call_to_action.setImageResource(R.drawable.addtocart);
		if(gifttype.equals("E")){
			call_to_action.setFocusable(false);
			call_to_action.setEnabled(false);
			call_to_action.setOnClickListener(null);
			call_to_action.setAlpha(0);
		}else{
			call_to_action.setFocusable(true);
			call_to_action.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mAnalysisManager.send(AnalysisEvents.GIFT_ISSUE,campaign_id);
					
					QuopnOperations operations = new QuopnOperations();
					//operations.setQuopnOperationsListener(GiftDetailsActivity.this);
                    operations.addQuopnOperationsListener(GiftDetailsActivity.this);
					operations.sendWebIssue(cta_text, cta_value, source, campaign_id, GiftDetailsActivity.this, true, -1, -1,true,mProgressbar);
				}
			});
		}
	

		share_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuopnUtils.ShareIntent(GiftDetailsActivity.this, mProductname , campaign_id);
				mAnalysisManager.send(AnalysisEvents.GIFT_SHARE,campaign_id);
			}
		});
		close_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}
	
	
	private class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			mCustomProgressDialog.dismiss();
			super.onPageFinished(view, url);

		}
		
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
//			mCustomProgressDialog.dismiss();
			view.loadUrl("file:///android_asset/myerrorpage.html");

		}
	}

	@Override
	public void onBackPressed() {

		if (mSlidinguppanellayout.isPanelExpanded()) {
			mSlidinguppanellayout.collapsePanel();
		} else
			super.onBackPressed();
	}

	@Override
	public void onResponse(int responseResult,Response response) {
		if (response instanceof UCNNumberData) {
			UCNNumberData ucnNumberData = (UCNNumberData) response;
			if(ucnNumberData.isError()==true){
				Dialog dialog=new Dialog(GiftDetailsActivity.this, R.string.dialog_title_UCN,ucnNumberData.getMessage()); 
				dialog.show();
			}else{
				Dialog dialog=new Dialog(GiftDetailsActivity.this, R.string.dialog_title_UCN,ucnNumberData.getMessage()); 
				dialog.show();
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public static void launch(Activity activity,Intent intent,View transitionView,/*View transitionView01,*/GiftDetailAddToCartListener giftDetailAddToCartListener,boolean flag) {
		mFlag=flag;
		if(mFlag){
			mGiftDetailAddToCartListener=giftDetailAddToCartListener;
			ActivityOptionsCompat options = ActivityOptionsCompat
					.makeSceneTransitionAnimation(activity, Pair.create(transitionView, EXTRA_IMAGE)/*,Pair.create(transitionView01, EXTRA_IMAGE_01)*/);
			ActivityCompat.startActivityForResult(activity, intent, QuopnConstants.HOME_PRESS ,options.toBundle());
		}else{
			ActivityCompat.startActivityForResult(activity, intent, QuopnConstants.HOME_PRESS ,null);
		}
		
	}

	@Override
	public void onQuopnIssued(String campaignID,boolean isFromGift,String webissueresponse) {
		if(isFromGift){
			Dialog dialog=new Dialog(GiftDetailsActivity.this, "QUOPN", webissueresponse);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			dialog.show();
		}
		
		if(mFlag){
//			Log.d(TAG, "From GiftListing");
			mGiftDetailAddToCartListener.onGiftDetailAddToCartSuccess();
		}else{
//			Log.d(TAG, "From Notificationpage");
			Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
            LocalBroadcastManager.getInstance(GiftDetailsActivity.this).sendBroadcast(intent);
		}
	}

	@Override
	public void onTimeout(ConnectRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}

}
