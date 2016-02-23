package com.quopn.wallet.adapter;

/**
 * @author Sandeep
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quopn.wallet.ListingByCategoryActivity;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.QuopnDetailsActivity;
import com.quopn.wallet.QuopnDetailsActivity.QuopnDetailAddToCartListener;
import com.quopn.wallet.QuopnOperations;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.ListQuopnContainer;
import com.quopn.wallet.data.model.QuopnData;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.utils.QuopnUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class QuopnListAdapter extends ArrayAdapter<ListQuopnContainer> {

    private LayoutInflater mLayoutInflater;
    private DisplayImageOptions mDisplayImageOptions;
    private String mQuopnId;
    private ImageLoader mImageLoader;
    private Animation mAnimationPushLeftIn = null;
    private Animation mAnimationHyperspaceOut = null;
    private Animation mFadeIn = null;
    private Animation mPushRightIn = null;
    private Animation mPushRightOut = null;
    private Animation mPushLeftOut = null;
    private final int QUOPN_OPEN_STATE = 0;
    private final int QUOPN_CLOSE_STATE = 1;
    private final int QUOPN_NORMAL_STATE = 2;
    private QuopnSelectedListener quopnSelectedListener;
    private QuopnDetailAddToCartListener mQuopnDetailAddToCartListener;
    private Context mContext;
    private ConnectionListener call_connectionlistener;
    private ConnectionListener sms_connectionlistener;
    private ConnectionListener video_connectionlistener;
    private ConnectionListener webissue_connectionlistener;
    private ConnectionListener ucn_connectionlistener;

    private AnalysisManager mAnalysisManager;
    private int counter1;
    private int counter2;
    private String lastselectedquopnid = "";
    private String TAG = "Quopn/QuopnList";
    private Bitmap bitmap;
    private Uri uri;
    private ViewHolderItem viewHolder;
    private static ArrayList<String> listAddToCartCampaignId = new ArrayList<String>();

    public interface QuopnSelectedListener extends Serializable {
        void onQuopnOpened();
    }

    public QuopnListAdapter(Context context, int resource,
                            List<ListQuopnContainer> values,
                            QuopnSelectedListener quopnSelectedListener, QuopnDetailAddToCartListener quopnDetailAddToCartListener) {
        super(context, resource, values);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        mAnalysisManager = ((QuopnApplication) context.getApplicationContext()).getAnalysisManager();
        this.quopnSelectedListener = quopnSelectedListener;
        this.mQuopnDetailAddToCartListener = quopnDetailAddToCartListener;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.placeholder_prodlisting)
                .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
                .build();
    }

    static class ViewHolderItem implements QuopnOperationsListener {
        String quopnid;
        TextView productname1;
        //		TextView productname2;
        ImageView mImage1;
        //		ImageView mImage2;
        LinearLayout mCardView;
        TextView mRowText1;
        //		TextView mRowText2;
        RelativeLayout mQuopn1;
        //		RelativeLayout mQuopn2;
        RelativeLayout mAddtocart_relativelayout1;
        //		RelativeLayout mAddtocart_relativelayout2;
        TextView mAddtocard_counter_txt1;
        //		TextView mAddtocard_counter_txt2;
        ImageView mProgressBar;
        static boolean isaddquopn = true;

        @Override
        public void onQuopnIssued(String campaignID, boolean isfromgift, String webissueresponse) {

            //isaddquopn =false;
            isaddquopn = true;

            System.out.println("==========onQuopnIssued==QuopnListAdapter====");
            //mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListQuopnContainer item = getItem(position);
        viewHolder = new ViewHolderItem();

        if (convertView == null || convertView.getTag() == null) {

            convertView = mLayoutInflater.inflate(R.layout.list_template, null);

            viewHolder.productname1 = (TextView) convertView
                    .findViewById(R.id.productname1);

//			viewHolder.productname2 = (TextView) convertView
//					.findViewById(R.id.productname2);

            viewHolder.mAddtocard_counter_txt1 = (TextView) convertView
                    .findViewById(R.id.addtocard_counter_txt1);

//			viewHolder.mAddtocard_counter_txt2 = (TextView) convertView
//					.findViewById(R.id.addtocard_counter_txt2);

            viewHolder.mImage1 = (ImageView) convertView
                    .findViewById(R.id.row_icon1);
//			viewHolder.mImage2 = (ImageView) convertView
//					.findViewById(R.id.row_icon2);

            viewHolder.mCardView = (LinearLayout) convertView
                    .findViewById(R.id.cardview);

            viewHolder.mRowText1 = (TextView) convertView
                    .findViewById(R.id.row_txt1);

//			viewHolder.mRowText2 = (TextView) convertView
//					.findViewById(R.id.row_txt2);

            viewHolder.mQuopn1 = (RelativeLayout) convertView
                    .findViewById(R.id.quopn1);

//			viewHolder.mQuopn2 = (RelativeLayout) convertView
//					.findViewById(R.id.quopn2);

            viewHolder.mAddtocart_relativelayout1 = (RelativeLayout) convertView
                    .findViewById(R.id.addtocart_relativelayout1);

//			viewHolder.mAddtocart_relativelayout2 = (RelativeLayout) convertView
//					.findViewById(R.id.addtocart_relativelayout2);

            viewHolder.mProgressBar = (ImageView) convertView.findViewById(R.id.progressBar_individualItem);


            // Store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        final ImageView mprogressbar = viewHolder.mProgressBar;
        AnimationDrawable animation = (AnimationDrawable) mprogressbar.getDrawable();
        animation.start();

        mImageLoader.displayImage(item.getQuopnData1().getThumb_icon(),
                viewHolder.mImage1, mDisplayImageOptions, null);

        viewHolder.productname1.setText(item.getQuopnData1().getBrand()/*getProductname()*/.toUpperCase());
        //viewHolder.mRowText1.setText(item.getQuopnData1().getShort_desc());
        //String productDesc= item.getQuopnData1().getDescription_highlight().toUpperCase() + " "+ item.getQuopnData1().getDescription_end();
        viewHolder.mRowText1.setText(Html.fromHtml("<bold><font color=\"#FF0000\">" + item.getQuopnData1().getDescription_highlight() + "</font></small></i>" + "<font color=\"#000\">" + item.getQuopnData1().getDescription_end() + "</font>"));

        if (item.getQuopnData1().getAvailable_quopns() != null
                && item.getQuopnData1().getAlready_issued() != null) {
            counter1 = Integer.parseInt(item.getQuopnData1().getAvailable_quopns()) + Integer.parseInt(item.getQuopnData1().getAlready_issued());
        }
        viewHolder.mAddtocard_counter_txt1.setText("" + counter1);

//		if (item.getQuopnData2() != null) {
//			counter2=Integer.parseInt(item.getQuopnData2().getAvailable_quopns()) + Integer.parseInt(item.getQuopnData2().getAlready_issued());
//			mImageLoader.displayImage(item.getQuopnData2().getThumb_icon(),
//					viewHolder.mImage2, mDisplayImageOptions, null);
//			//viewHolder.mRowText2.setText(item.getQuopnData2().getShort_desc());
//			viewHolder.mRowText2.setText(Html.fromHtml("<bold><font color=\"#FF0000\">" + item.getQuopnData2().getDescription_highlight()  + "</font></small></i>" + "<font color=\"#000\">" + item.getQuopnData2().getDescription_end() + "</font>"));
//			viewHolder.productname2.setText(item.getQuopnData2().getBrand().toUpperCase());
//			viewHolder.mAddtocard_counter_txt2.setText(""+counter2);
//			viewHolder.mQuopn2.setVisibility(View.VISIBLE);
//		} else {
//			viewHolder.mQuopn2.setVisibility(View.INVISIBLE);
//		}

        if (item.getQUOPN_STATE() == QUOPN_OPEN_STATE) {
        } else if (item.getQUOPN_STATE() == QUOPN_CLOSE_STATE) {
        } else if (item.getQUOPN_STATE() == QUOPN_NORMAL_STATE) {

            viewHolder.mCardView.setVisibility(View.VISIBLE);

        }

        viewHolder.mImage1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ankur
                if (!QuopnUtils.isInternetAvailableAndShowDialog(mContext)) {
                    return;
                }
                if (listAddToCartCampaignId.contains(item.getQuopnData1().getQuopnId())) {
                    Log.d(TAG, "image details ignored for " + item.getQuopnData1().getQuopnId());
                    return;
                } else {
                    Log.d(TAG, "image details for " + item.getQuopnData1().getQuopnId());
                }

                quopnSelectedListener.onQuopnOpened();
                mAnalysisManager.send(AnalysisEvents.QUOPN, item
                        .getQuopnData1().getQuopnId());

                Intent quopIntent = new Intent(getContext(),
                        QuopnDetailsActivity.class);
                quopIntent.putExtra("tag", item.getQuopnData1().getQuopnId());
//                quopIntent.putExtra("byteArray", item.getQuopnData1().getThumb_icon());
                /*QuopnDetailsActivity.launch((Activity) getContext(),
                        quopIntent, v, *//*viewHolder.productname1,viewHolder.mRowText1,*//*mQuopnDetailAddToCartListener, true);*/
                if(mContext instanceof MainActivity){
                    MainActivity mMainActivity = (MainActivity) mContext;
                    mMainActivity.showQuopnDetails(quopIntent,v, mQuopnDetailAddToCartListener);
                } else if(mContext instanceof ListingByCategoryActivity){
                    ListingByCategoryActivity mListByCategActivity = (ListingByCategoryActivity) mContext;
                    mListByCategActivity.showQuopnDetails(quopIntent,v, mQuopnDetailAddToCartListener);
                }

            }});

//		viewHolder.mImage2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				quopnSelectedListener.onQuopnOpened();
//
//				Intent quopIntent = new Intent(getContext(),
//						QuopnDetailsActivity.class);
//				quopIntent.putExtra("tag", item.getQuopnData2().getQuopnId());
//
//				QuopnDetailsActivity.launch((Activity) getContext(),
//						quopIntent, v, /*viewHolder.productname2,viewHolder.mRowText2,*/mQuopnDetailAddToCartListener,true);
//				mAnalysisManager.send(AnalysisEvents.QUOPN, item
//						.getQuopnData2().getQuopnId());
//			}
//		});


        viewHolder.mAddtocart_relativelayout1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ankur
                if (listAddToCartCampaignId.contains(item.getQuopnData1().getQuopnId())) {
                    Log.d(TAG, "adding ignored for " + item.getQuopnData1().getQuopnId());
                    return;
                } else {
                    Log.d(TAG, "adding for " + item.getQuopnData1().getQuopnId());
                }

                //Log.d(TAG + " In the onclick event 1111", "" + QuopnConstants.ISADDTOCARTINCOMPLETE);

                System.out.println("==========viewHolder.mAddtocart_relativelayout1====");
//                if (ViewHolderItem.isaddquopn) {

                performCallToAction(item.getQuopnData1(), Integer.parseInt(item.getQuopnData1().getAvailable_quopns()), Integer.parseInt(item.getQuopnData1().getAlready_issued()), mprogressbar, viewHolder);
                mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE, item.getQuopnData1().getQuopnId());
                Log.d(TAG, "In the onclick event 44444 else part: isaddquopn:" + ViewHolderItem.isaddquopn);
                ViewHolderItem.isaddquopn = false;
                //QuopnConstants.ISADDTOCARTINCOMPLETE=false;

//            }else {
                //Log.d(TAG + " In the onclick event 44444 else part", "" + QuopnConstants.ISADDTOCARTINCOMPLETE);

//        }
            }
        });


//		viewHolder.mAddtocart_relativelayout2.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				performCallToAction(item.getQuopnData2(),Integer.parseInt(item.getQuopnData2().getAvailable_quopns()),Integer.parseInt(item.getQuopnData2().getAlready_issued()));
//				mAnalysisManager.send(AnalysisEvents.QUOPN_ISSUE,item.getQuopnData2().getQuopnId());
//			}
//		});

        return convertView;
    }


    private void performCallToAction(QuopnData quopnData, final int availablequopn, final int alreayissued, final ImageView mProgressBar, ViewHolderItem viewholder) {

        if (quopnData != null) {
            if (quopnData.getCall_to_action().equals(
                    ProductCatFragment.QUOPN_CALL_TO_ACTION)) {
                final String cta_text = quopnData.getCta_text();
                final String cta_value = quopnData.getCta_value();
                final String campaign_id = quopnData.getQuopnId();
                QuopnUtils.CustomAlertDialog_call(cta_text, cta_value, mContext);
                //QuopnConstants.ISADDTOCARTINCOMPLETE=true;

            } else if (quopnData.getCall_to_action().equals(
                    ProductCatFragment.QUOPN_SMS)) {
                final String cta_text = quopnData.getCta_text();
                final String cta_value = quopnData.getCta_value();
                final String campaign_id = quopnData.getQuopnId();
                QuopnUtils.CustomAlertDialog_sms(cta_text, cta_value, mContext);
                // QuopnConstants.ISADDTOCARTINCOMPLETE=true;
            } else if (quopnData.getCall_to_action().equals(
                    ProductCatFragment.QUOPN_VIDEO)) {
                final String cta_text = quopnData.getCta_text();
                final String cta_value = quopnData.getCta_value();
                final String campaign_id = quopnData.getQuopnId();
                final String source = quopnData.getCall_to_action();

                QuopnOperations operations = new QuopnOperations();
                QuopnOperationsListener listener
                        = (QuopnOperationsListener) mContext;
                //operations.setQuopnOperationsListener(listener);
                operations.addQuopnOperationsListener(listener);
                operations.addQuopnOperationsListener(viewHolder);
                operations.videoIssue(mContext, cta_text, cta_value, source
                        , campaign_id, availablequopn, alreayissued);


            } else if (quopnData.getCall_to_action().equals(
                    ProductCatFragment.QUOPN_WEBISSUE)) {
                final String cta_text = quopnData.getCta_text();
                final String cta_value = quopnData.getCta_value();
                final String campaign_id = quopnData.getQuopnId();
                final String source = quopnData.getCall_to_action();

                QuopnOperations quopnOperations = new QuopnOperations();
                QuopnOperationsListener listener = (QuopnOperationsListener) mContext;
                quopnOperations.addQuopnOperationsListener(listener);
                quopnOperations.addQuopnOperationsListener(viewHolder);
                /*if(mContext instanceof MainActivity) {
                    ((MainActivity) mContext).addQuopnOperationsListener(viewHolder);
                } else if(mContext instanceof ListingByCategoryActivity){
                    ((ListingByCategoryActivity) mContext).addQuopnOperationsListener(viewHolder);
                }*/
                startAddingToCartForQuopnId(quopnData.getQuopnId()); // ankur
                quopnOperations.sendWebIssueAddToCart(cta_text, cta_value, source, campaign_id, mContext, false, availablequopn, alreayissued, false, mProgressBar, listAddToCartCampaignId);
                /*if(mContext instanceof MainActivity) {
                    ((MainActivity) mContext).sendWebIssueAddToCart(cta_text, cta_value, source, campaign_id, mContext, false, availablequopn, alreayissued, false, mProgressBar, listAddToCartCampaignId);
                } else if(mContext instanceof ListingByCategoryActivity){
                    ((ListingByCategoryActivity) mContext).sendWebIssueAddToCart(cta_text, cta_value, source, campaign_id, mContext, false, availablequopn, alreayissued, false, mProgressBar, listAddToCartCampaignId);
                }*/
            } else if (quopnData.getCall_to_action().equals(
                    ProductCatFragment.QUOPN_UCN)) {
                final String cta_text = quopnData.getCta_text();
                final String cta_value = quopnData.getCta_value();
                final String campaign_id = quopnData.getQuopnId();
                MainActivity activty = (MainActivity) mContext;
                QuopnUtils.CustomAlertDialog_ucn(cta_text, cta_value, campaign_id, mContext, (ConnectionListener) activty);
                //QuopnConstants.ISADDTOCARTINCOMPLETE =true;
            }
        }

    }

    public void startAddingToCartForQuopnId(String campaignId) {
        // disable controls
        if (listAddToCartCampaignId.contains(campaignId)) {
            Log.e(TAG, "Error: already in list: " + campaignId);
        } else {
            Log.d(TAG, "added to list: " + campaignId);
            listAddToCartCampaignId.add(campaignId);
        }
    }

    public void removeAddingToCartForCampaignId(String campaignId) {
        // enable controls
        if (listAddToCartCampaignId.contains(campaignId)) {
            Log.d(TAG, "removed from list: " + campaignId);
            listAddToCartCampaignId.remove(campaignId);
        } else {
            Log.e(TAG, "Error: not present in list: " + campaignId);
        }
    }
}
