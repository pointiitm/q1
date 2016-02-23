package com.quopn.wallet;

/**
 * @author Sandeep
 *
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CampaignDetailsQuopnData;
import com.quopn.wallet.data.model.QuopnList;
import com.quopn.wallet.data.model.UCNNumberData;
import com.quopn.wallet.data.model.VideoIssueData;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.AspectRatioImageView;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class QuopnDetailsActivity extends ActionBarActivity implements
		ConnectionListener, QuopnOperationsListener {

	private static final String EXTRA_IMAGE = "QuopnDetailsActivity";
	private static final String EXTRA_IMAGE_01 = "title";
	private static final String EXTRA_IMAGE_02 = "description";
	private static final String TAG = "QuopnDetailsActivity";
	
	private DisplayImageOptions mOptions;
	String cta_text = "";
	String cta_value = "";
	String campaign_id = "";
	String source = "";
	String share_productname="";
	String avaialblequopn="";
	String alreadyissued="";
	SlidingUpPanelLayout mSlidinguppanellayout;
	private boolean isTutShown = false;
	private ConnectionListener call_connectionlistener;
	private ConnectionListener sms_connectionlistener;
	private ConnectionListener video_connectionlistener;
	private ConnectionListener webissue_connectionlistener;
	private ConnectionListener ucn_connectionlistener;
	private DisplayImageOptions mOptions_featuredtag;
	private ConnectionListener connectionlistener_videoissue;
	WebView termscodition_webview;
	CustomProgressDialog mCustomProgressDialog;
	private AnalysisManager mAnalysisManager;
	ScrollView productdescription_scrollviewlayout;
	private static QuopnDetailAddToCartListener mQuopnDetailAddToCartListener;
	private static boolean mFlag=false;
    ImageView mProgressbar;
	Uri uri;
    String tag;
    //String tagaction;
    private Map<String, String> params,headerParams;
    private ConnectionFactory mConnectionFactory;
    TextView productdescription_title_tv;
    TextView productdescription_tv;
    ImageView call_to_action;
    ImageView featuredtag_btn;
    AspectRatioImageView icon1;
    ImageView share_btn;
    ImageView close_btn;
    Cursor cursor;
    Bitmap bitmap;
	public interface QuopnDetailAddToCartListener {
		void onQuopnDetailAddToCartSuccess();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
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
        ImageView mCommonCartButton = (ImageView) actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv = (TextView) actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv = (TextView) actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);

        setContentView(R.layout.product_detail);
//		mCustomProgressDialog = new CustomProgressDialog(this);
//		mCustomProgressDialog.show();


         if (getIntent().getAction() == Intent.ACTION_VIEW &&
                 (getIntent().getScheme().equals("http")
                 || getIntent().getScheme().equals("quopn"))) {
             // Toast.makeText(getApplicationContext(), "" + getIntent().getScheme(), Toast.LENGTH_SHORT).show();
             uri = getIntent().getData();
             String campaignid = uri.getLastPathSegment();
             tag = campaignid;
             System.out.println("============QuopnDetailsActivity====Get==Tag===="+tag);
             getQuopnCampaignDetails(campaignid);
         } else if (getIntent().getExtras().getString("tag")!= null) {

             tag = getIntent().getExtras().getString("tag");

         }
        mSlidinguppanellayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        icon1 = (AspectRatioImageView) findViewById(R.id.row_icon);
        productdescription_scrollviewlayout = (ScrollView) findViewById(R.id.productdescription_scrollviewlayout);
        ViewCompat.setTransitionName(icon1, EXTRA_IMAGE);
        featuredtag_btn = (ImageView) findViewById(R.id.featuredtag_btn);

        productdescription_title_tv = (TextView) findViewById(R.id.productdescription_title_tv);
        productdescription_title_tv.setTypeface(null, Typeface.BOLD);
//		ViewCompat.setTransitionName(productdescription_title_tv, EXTRA_IMAGE_01);

        productdescription_tv = (TextView) findViewById(R.id.productdescription_tv);
//		ViewCompat.setTransitionName(productdescription_tv, EXTRA_IMAGE_02);
        call_to_action = (ImageView) findViewById(R.id.call_to_action_btn);
        share_btn = (ImageView) findViewById(R.id.share_btn);
        close_btn = (ImageView) findViewById(R.id.close_btn);
        ImageView termscodition_btn = (ImageView) findViewById(R.id.termscodition_btn);
        termscodition_webview = (WebView) findViewById(R.id.termscodition_web);
        mProgressbar = (ImageView) findViewById(R.id.progressBar_individualItem);
        AnimationDrawable animation = (AnimationDrawable) mProgressbar.getDrawable();
        animation.start();

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        mOptions = builder.cacheOnDisc(true).cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .showStubImage(R.drawable.placeholder_details).build();

        DisplayImageOptions.Builder builder1 = new DisplayImageOptions.Builder();
        mOptions_featuredtag = builder1.cacheOnDisc(true).cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .build();


        if(tag!=null) {

            cursor = getContentResolver().query(
                    ConProvider.CONTENT_URI_QUOPN, null,
                    ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",
                    new String[]{tag}, ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);

            if (cursor != null) {

                cursor = getContentResolver().query(
                        ConProvider.CONTENT_URI_QUOPN, null,
                        ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",
                        new String[]{tag}, ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);

                cursor.moveToFirst();
                productdescription_title_tv.setText(Html.fromHtml(cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME)).toUpperCase()));

                //String thumbImage = getIntent().getExtras().getString("byteArray");

                //ImageLoader.getInstance().displayImage((thumbImage), icon1, mOptions);

                ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_BIG_IMG)),
                       icon1, mOptions);

                ImageLoader.getInstance().displayImage(cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL)), featuredtag_btn, mOptions_featuredtag);

                productdescription_tv.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC))));

//		productdescription_tv.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here <h3>fdgjh jsdjsodeijfosdj </h3><br>jsoidjfoijsdjf oijsoidjfoijsdfj ojsodj <br><br>sdjfjdsjfoisj sdjfjsdjfoi jsdjf sjdfjosdjfjsdijfoijsdfjoisdjfjso<br> jsoidjfjsdfjosdjfoijsdfjoidsjfoijsdfjoidsjf ksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfj ksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfj sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad</p>"));

                cta_text = cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT));
                cta_value = cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE));
                campaign_id = cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID));
                source = cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION));
                share_productname = cursor.getString(cursor
                        .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME));
                avaialblequopn = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS));
                alreadyissued = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED));

                termscodition_webview.setWebViewClient(new myWebClient());
                termscodition_webview.getSettings().setJavaScriptEnabled(true);
                termscodition_webview.loadUrl(QuopnApi.TERMS_AND_CONDITION_DETAILS_URL + "/" + campaign_id);

                if (cursor
                        .getString(
                                cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                        .equals(ProductCatFragment.QUOPN_CALL_TO_ACTION)) {
                    call_to_action.setImageResource(R.drawable.addtocart);
                    call_to_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                            QuopnUtils.CustomAlertDialog_call(cta_text, cta_value, QuopnDetailsActivity.this);
                        }
                    });
                } else if (cursor
                        .getString(
                                cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                        .equals(ProductCatFragment.QUOPN_SMS)) {
                    call_to_action.setImageResource(R.drawable.addtocart);
                    call_to_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                            QuopnUtils.CustomAlertDialog_sms(cta_text, cta_value, QuopnDetailsActivity.this);
                        }
                    });
                } else if (cursor
                        .getString(
                                cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                        .equals(ProductCatFragment.QUOPN_UCN)) {
                    call_to_action.setImageResource(R.drawable.addtocart);
                    call_to_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                            QuopnUtils.CustomAlertDialog_ucn(cta_text, cta_value, campaign_id, QuopnDetailsActivity.this, QuopnDetailsActivity.this);
                        }
                    });
                } else if (cursor
                        .getString(
                                cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                        .equals(ProductCatFragment.QUOPN_VIDEO)) {

                    call_to_action.setImageResource(R.drawable.addtocart);
                    call_to_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                            QuopnOperations operations = new QuopnOperations();
                            operations.addQuopnOperationsListener(
                                    QuopnDetailsActivity.this);
                            operations.videoIssue(QuopnDetailsActivity.this
                                    , cta_text, cta_value, source, campaign_id
                                    , Integer.parseInt(avaialblequopn)
                                    , Integer.parseInt(alreadyissued));
                        }
                    });
                } else if (cursor
                        .getString(
                                cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                        .equals(ProductCatFragment.QUOPN_WEBISSUE)) {
                    call_to_action.setImageResource(R.drawable.addtocart);
                    call_to_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (QuopnUtils.isInternetAvailableAndShowDialog(QuopnDetailsActivity.this)) {
                                mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                                QuopnOperations operations = new QuopnOperations();
                                operations.addQuopnOperationsListener(QuopnDetailsActivity.this);
                                operations.sendWebIssue(cta_text, cta_value, source, campaign_id, QuopnDetailsActivity.this, true,
                                        Integer.parseInt(avaialblequopn),
                                        Integer.parseInt(alreadyissued), false, mProgressbar);
                            }
                        }
                    });

                }


                share_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAnalysisManager.send(AnalysisEvents.QUOPN_SHARE, campaign_id);
                        QuopnUtils.ShareIntent(QuopnDetailsActivity.this, share_productname, campaign_id);
                    }
                });
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                cursor.close();
            }
        }
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

//    public void onResume(){
//        super.onResume();
//        // put your code here...
//        onCursorData(tag);
//    }

    public void onCursorData(String tag){

       cursor = getContentResolver().query(
                ConProvider.CONTENT_URI_QUOPN, null,
                ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",
                new String[]{tag}, ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX /*+ " desc"*/);
       if(cursor!= null) {
            cursor.moveToFirst();
            productdescription_title_tv.setText(Html.fromHtml(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME)).toUpperCase()));


            ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_BIG_IMG)),
                    icon1, mOptions);

            ImageLoader.getInstance().displayImage(cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL)), featuredtag_btn, mOptions_featuredtag);

            productdescription_tv.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC))));

//		productdescription_tv.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here <h3>fdgjh jsdjsodeijfosdj </h3><br>jsoidjfoijsdjf oijsoidjfoijsdfj ojsodj <br><br>sdjfjdsjfoisj sdjfjsdjfoi jsdjf sjdfjosdjfjsdijfoijsdfjoisdjfjso<br> jsoidjfjsdfjosdjfoijsdfjoidsjfoijsdfjoidsjf ksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfj ksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfj sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad sajdfjlksdjlkfj lksadjflk jsadfjlksajdflk jsdfjksdhflj lsdhfl hdslkfl slkdjflkjsdlk jlkkdsjflkjdsf ljsdafjlk sajdfjlksdjlkfj lksadjflk jsad</p>"));

            cta_text = cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT));
            cta_value = cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE));
            campaign_id = cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID));
            source = cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION));
            share_productname = cursor.getString(cursor
                    .getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME));
            avaialblequopn = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS));
            alreadyissued = cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED));

            termscodition_webview.setWebViewClient(new myWebClient());
            termscodition_webview.getSettings().setJavaScriptEnabled(true);
            termscodition_webview.loadUrl(QuopnApi.TERMS_AND_CONDITION_DETAILS_URL + "/" + campaign_id);

            if (cursor
                    .getString(
                            cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                    .equals(ProductCatFragment.QUOPN_CALL_TO_ACTION)) {
                call_to_action.setImageResource(R.drawable.addtocart);
                call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                        QuopnUtils.CustomAlertDialog_call(cta_text, cta_value, QuopnDetailsActivity.this);
                    }
                });
            } else if (cursor
                    .getString(
                            cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                    .equals(ProductCatFragment.QUOPN_SMS)) {
                call_to_action.setImageResource(R.drawable.addtocart);
                call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                        QuopnUtils.CustomAlertDialog_sms(cta_text, cta_value, QuopnDetailsActivity.this);
                    }
                });
            } else if (cursor
                    .getString(
                            cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                    .equals(ProductCatFragment.QUOPN_UCN)) {
                call_to_action.setImageResource(R.drawable.addtocart);
                call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                        QuopnUtils.CustomAlertDialog_ucn(cta_text, cta_value, campaign_id, QuopnDetailsActivity.this, QuopnDetailsActivity.this);
                    }
                });
            } else if (cursor
                    .getString(
                            cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                    .equals(ProductCatFragment.QUOPN_VIDEO)) {

                call_to_action.setImageResource(R.drawable.addtocart);
                call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);
                        QuopnOperations operations = new QuopnOperations();
                        operations.addQuopnOperationsListener(
                                QuopnDetailsActivity.this);
                        operations.videoIssue(QuopnDetailsActivity.this
                                , cta_text, cta_value, source, campaign_id
                                , Integer.parseInt(avaialblequopn)
                                , Integer.parseInt(alreadyissued));
                    }
                });
            } else if (cursor
                    .getString(
                            cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION))
                    .equals(ProductCatFragment.QUOPN_WEBISSUE)) {
                call_to_action.setImageResource(R.drawable.addtocart);
                call_to_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(QuopnUtils.isInternetAvailableAndShowDialog(QuopnDetailsActivity.this)) {
                            mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, campaign_id);

                            QuopnOperations operations = new QuopnOperations();
                            operations.addQuopnOperationsListener(QuopnDetailsActivity.this);
                            operations.sendWebIssue(cta_text, cta_value, source, campaign_id, QuopnDetailsActivity.this, true,
                                    Integer.parseInt(avaialblequopn),
                                    Integer.parseInt(alreadyissued), false, mProgressbar);
                        }
                    }
                });

            }


            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAnalysisManager.send(AnalysisEvents.QUOPN_SHARE, campaign_id);
                    QuopnUtils.ShareIntent(QuopnDetailsActivity.this, share_productname, campaign_id);
                }
            });
            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            cursor.close();
        }
    }

	@Override
	public void onResponse(int responseResult,Response response) {
		if (response instanceof UCNNumberData) {
			UCNNumberData ucnNumberData = (UCNNumberData) response;
			
			if(ucnNumberData.isError()==true){
				Dialog dialog=new Dialog(QuopnDetailsActivity.this, R.string.dialog_title_error,ucnNumberData.getMessage()); 
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								QuopnDetailsActivity.this.finish();
							}
						});
						dialog.show();
			}else{
				Dialog dialog=new Dialog(QuopnDetailsActivity.this, R.string.dialog_title_UCN,ucnNumberData.getMessage()); 
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						QuopnDetailsActivity.this.finish();
					}
				});
				dialog.show();
			}

		}else if(response instanceof CampaignDetailsQuopnData){
            CampaignDetailsQuopnData campaigndetailData = (CampaignDetailsQuopnData)response;

            if(campaigndetailData.isError()==true){
                Dialog dialog=new Dialog(QuopnDetailsActivity.this, R.string.dialog_title_error,campaigndetailData.getMessage());
                dialog.setOnAcceptButtonClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuopnDetailsActivity.this.finish();
                    }
                });
                dialog.show();
                Log.v(TAG,"CampaignDetailsQuopnData");
            }else{
                Log.v(TAG,"Successful Response");
                List<QuopnList> quopnList = campaigndetailData.getCampaignDetails();
                for (QuopnList quopn  : quopnList) {
                    populateQuopnDetailsDB(quopn);
                    onCursorData(tag);
                }

            }
        }

	}

    public void populateQuopnDetailsDB(QuopnList quopn) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID, quopn.getId());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CATEGORY_ID,quopn.getCategoryid());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN,quopn.getCampaignname());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_NAME,quopn.getProductname());
        //contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PRODUCT_TYPE,quopn.getProducttype());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON,quopn.getThumb_icon());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_BIG_IMG,quopn.getBig_image());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SHORT_DESC,quopn.getShort_desc());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_LONG_DESC,	quopn.getLong_desc());
        //contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG_URL,quopn.getMastertag_image());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MASTER_TAG,quopn.getMaster_tag());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_FOOTER_TAG,quopn.getFooter_tag());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_BRAND, quopn.getBrand());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_QUOPN_COUNT,quopn.getQuopn_count());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CALL_TO_ACTION,quopn.getCall_to_action());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CTA_TEXT,quopn.getCta_text());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CTA_VALUE,quopn.getCta_value());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_START_DATE,quopn.getStartfrom());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_END_DATE, quopn.getEnddate());
        //contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_TERMS_COND,quopn.getTerms_cond());
        //contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SUBMITTED_BY,quopn.getSubmitted_by());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_END_DATE,quopn.getRedemption_expiry_date());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_CAMPAIGN_MEDIA,quopn.getCampaign_media());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_MULTI_ISSUE,quopn.getMulti_issue());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_ISSUE_LIMIT,quopn.getIssue_limit());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_REDEMPTION_CAP,quopn.getRedemption_cap());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_PROMOTION_ENABLED,quopn.getPromotion_enabled());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_TOTAL_COUPONS_BLOCKED,quopn.getTotal_coupons_blocked());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_THUMB_ICON_2,quopn.getThumb_icon2());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SEARCH_TAGS,quopn.getSearch_tags());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS,quopn.getAvailable_quopns());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED,quopn.getAlready_issued());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_HIGHLIGHT_DESC,quopn.getDescription_highlight());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_END_DESC,quopn.getDescription_end());
        contentValues.put(ITableData.TABLE_QUOPNS.COLUMN_SORT_INDEX,quopn.getSort_index());
        getContentResolver().insert(ConProvider.CONTENT_URI_QUOPN,contentValues);
        //Log.v(TAG,"Response :populateQuopnsDB==>"+contentValues);
        Log.v(TAG,"Response :populateGetProductName==>"+quopn.getProductname());
    }
    public void getQuopnCampaignDetails(String campaignid) {

        if (!TextUtils.isEmpty(PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY))) {
            headerParams = new HashMap<String, String>();
            headerParams.put("Authorization",PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
            params=new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.CAMPAIGN_ID,campaignid);
            mConnectionFactory= new ConnectionFactory(this,this);
            mConnectionFactory.setHeaderParams(headerParams);
            mConnectionFactory.setPostParams(params);
            mConnectionFactory.createConnection(QuopnConstants.CAMPAIGN_DETAILS_CODE);
        } else {
            // show error
        }
    }

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);

		if (resultcode == RESULT_OK) {
			switch (requestcode) {
			case QuopnConstants.VIDEO_COMPLETED_REQUESTCODE:
				connectionlistener_videoissue=new ConnectionListener() {
					@Override
					public void onResponse(int responseResult,Response response) {
						if(response instanceof VideoIssueData){
							VideoIssueData videoIssueData=(VideoIssueData)response;
							if(videoIssueData.isError()==true){
								Dialog dialog=new Dialog(QuopnDetailsActivity.this, R.string.dialog_title_error,videoIssueData.getMessage()); 
								dialog.setOnAcceptButtonClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										QuopnDetailsActivity.this.finish();
									}
								});
								dialog.show();
							}else{
								QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(
										QuopnDetailsActivity.this).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT)  + 1;
								PreferenceUtil.getInstance(QuopnDetailsActivity.this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT, QuopnConstants.MY_CART_COUNT);
								Dialog dialog=new Dialog(QuopnDetailsActivity.this, R.string.dialog_title_success,videoIssueData.getMessage()); 
								dialog.setOnAcceptButtonClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										QuopnDetailsActivity.this.finish();
									}
								});
								dialog.show();
							}

							Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
							LocalBroadcastManager.getInstance(QuopnDetailsActivity.this).sendBroadcast(intent);
						}
					}

					@Override
					public void onTimeout(ConnectRequest request) {
						// TODO Auto-generated method stub
						
					}

                    @Override
                    public void myTimeout(String requestTag) {

                    }
                };
				QuopnUtils.getVideoIssueResponse(QuopnConstants.CAMPAIGNID, QuopnConstants.SOURCE, QuopnDetailsActivity.this, connectionlistener_videoissue);
			
				break;
			
			default:
				break;
			}

		}

	}
	
	
	@SuppressWarnings("unchecked")
	public static void launch(Activity activity,Intent intent,View transitionView,/*View transitionView01,View transitionView02,*/QuopnDetailAddToCartListener quopnDetailAddToCartListener,boolean flag) {
		mFlag=flag;
		if(mFlag){
			mQuopnDetailAddToCartListener=quopnDetailAddToCartListener;
			ActivityOptionsCompat options = ActivityOptionsCompat
					.makeSceneTransitionAnimation(activity, Pair.create(transitionView, EXTRA_IMAGE)/*,Pair.create(transitionView01, EXTRA_IMAGE_01),Pair.create(transitionView02, EXTRA_IMAGE_02)*/);
			ActivityCompat.startActivityForResult(activity, intent, QuopnConstants.HOME_PRESS ,options.toBundle());
		}else{
			ActivityCompat.startActivityForResult(activity, intent, QuopnConstants.HOME_PRESS ,null);
		}
		
	}

	@Override
	public void onQuopnIssued(String campaignID,boolean isFromGift,String webissueresponse) {
		finish();
		if(mFlag){
			mQuopnDetailAddToCartListener.onQuopnDetailAddToCartSuccess();
		}else{
			Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
            LocalBroadcastManager.getInstance(QuopnDetailsActivity.this).sendBroadcast(intent);
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