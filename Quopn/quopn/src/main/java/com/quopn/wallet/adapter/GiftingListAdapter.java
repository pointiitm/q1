package com.quopn.wallet.adapter;

/**
 * @author Sumeet
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quopn.wallet.GiftDetailsActivity;
import com.quopn.wallet.GiftDetailsActivity.GiftDetailAddToCartListener;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.QuopnOperations;
import com.quopn.wallet.QuopnOperations.QuopnOperationsListener;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.GiftsContainer;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.utils.QuopnConstants;

import java.io.Serializable;
import java.util.List;

public class GiftingListAdapter extends ArrayAdapter<GiftsContainer> {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private DisplayImageOptions mDisplayImageOptions;
	private DisplayImageOptions mMasterDisplayImageOptions;
	private ImageLoader mImageLoader;
	private final int QUOPN_OPEN_STATE = 0;
	private final int QUOPN_CLOSE_STATE = 1;
	private final int QUOPN_NORMAL_STATE = 2;

	private Animation mAnimationPushLeftIn = null;
	private Animation mAnimationHyperspaceOut = null;
	private Animation mFadeIn = null;
	private Animation mPushRightOut = null;
	private GiftSelectedListener giftSelectedListener;
	private AnalysisManager mAnalysisManager;
	private GiftDetailAddToCartListener mGiftDetailAddToCartListener;
	private MainActivity mMainActivity;
	
	public interface GiftSelectedListener extends Serializable {
		void onQuopnOpened();
	}

	public GiftingListAdapter(Context context, int resource,
			List<GiftsContainer> values,
			GiftSelectedListener giftSelectedListener,GiftDetailAddToCartListener giftDetailAddToCartListener) {
		super(context, resource, values);
		mMainActivity = (MainActivity)context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.giftSelectedListener = giftSelectedListener;
		this.mGiftDetailAddToCartListener=giftDetailAddToCartListener;
		mAnalysisManager=((QuopnApplication)context.getApplicationContext()).getAnalysisManager();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.showStubImage(R.drawable.placeholder_gifts)
				.cacheOnDisc(true)
				.considerExifParams(true)
				.resetViewBeforeLoading(true)
				.build();
		

	}

	static class ViewHolderItem {
		ImageView mImage;
		TextView mRowText;
		ImageView mPlussign_icon;
		TextView mAddtocard_txt;
		RelativeLayout mAddtocart_Relativelayout;
        ImageView mProgressbar;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final GiftsContainer item = getItem(position);

        ViewHolderItem viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.list_template_gifting, null);
			viewHolder.mImage = (ImageView) convertView.findViewById(R.id.row_icon);
			viewHolder.mRowText = (TextView) convertView.findViewById(R.id.row_txt);
			viewHolder.mPlussign_icon = (ImageView) convertView.findViewById(R.id.plussign_icon);
			viewHolder.mAddtocard_txt = (TextView) convertView.findViewById(R.id.addtocard_txt);
			viewHolder.mAddtocart_Relativelayout=(RelativeLayout) convertView.findViewById(R.id.addtocart_relativelayout);
            viewHolder.mProgressbar=(ImageView) convertView.findViewById(R.id.progressBar_individualItem);
					
			// Store the holder with the view.
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

        final ImageView progressbar=viewHolder.mProgressbar;

		if(item.getGift_type().equalsIgnoreCase("P")){
			viewHolder.mPlussign_icon.setVisibility(View.VISIBLE);
			viewHolder.mAddtocard_txt.setText(R.string.add_to_cart);
			viewHolder.mAddtocart_Relativelayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GiftsContainer giftsData = (item);
					performCallToAction(giftsData,progressbar);
					mAnalysisManager.send(AnalysisEvents.GIFT_ISSUE,item.getId());
				}
			});
			
		}else if(item.getGift_type().equalsIgnoreCase("E")){
			viewHolder.mPlussign_icon.setVisibility(View.GONE);
			if(item.getPartner_code()!=null){
				viewHolder.mAddtocard_txt.setText(QuopnConstants.PARTNER_CODE+": "+item.getPartner_code());
			}else{
				viewHolder.mAddtocard_txt.setText(QuopnConstants.PARTNER_CODE+": "+"");
			}
			viewHolder.mAddtocart_Relativelayout.setOnClickListener(null);
		}
		
		
		viewHolder.mImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				giftSelectedListener.onQuopnOpened();
				mAnalysisManager.send(AnalysisEvents.GIFT,item.getId());
//				mAnalysisManager.send(AnalysisEvents.GIFT_DETAIL,item.getId());
				Intent quopIntent = new Intent((MainActivity) getContext(),	GiftDetailsActivity.class);
				quopIntent.putExtra("id", item.getId());
				quopIntent.putExtra("giftscomapignname", item.getCampaignname());
				quopIntent.putExtra("giftsbigImage", item.getBig_image());
				quopIntent.putExtra("giftslongdesc", item.getLong_desc());
				quopIntent.putExtra("giftstermsncondition", item.getTerms_cond());
				quopIntent.putExtra("giftsctatext", item.getCta_text());
				quopIntent.putExtra("giftsctavalue", item.getCta_value());
				quopIntent.putExtra("giftssource", item.getCall_to_action());
				quopIntent.putExtra("giftsmastertag", item.getMastertag_image());
				quopIntent.putExtra("productname", item.getProductname());
				quopIntent.putExtra("gifttype", item.getGift_type());
				quopIntent.putExtra("termscondition", item.getTerms_cond());
				if(item.getGift_type().equals("E")){
					quopIntent.putExtra("giftpartnercode", item.getPartner_code());
				}else{
					quopIntent.putExtra("giftpartnercode", "");
				}
				
				GiftDetailsActivity.launch((Activity) getContext(),quopIntent,v, /*viewHolder.mRowText,*/mGiftDetailAddToCartListener,true);
			}
		});
		

		mImageLoader.displayImage(item.getThumb_icon(), viewHolder.mImage,mDisplayImageOptions, null);
		viewHolder.mRowText.setText(item.getShort_desc().toUpperCase()); 

		return convertView;
	}

	
	private void performCallToAction(GiftsContainer giftsData,ImageView mProgressbar) {

		if (giftsData != null) {
			if (giftsData.getCall_to_action().equals(
					ProductCatFragment.QUOPN_WEBISSUE)) {
				final String cta_text = giftsData.getCta_text();
				final String cta_value = giftsData.getCta_value();
				final String source=giftsData.getCall_to_action();
				final String campaign_id=giftsData.getId();
				QuopnOperations quopnOperations = new QuopnOperations();
				QuopnOperationsListener listener
					= (QuopnOperationsListener) mContext;
				quopnOperations.addQuopnOperationsListener(listener);
				
				quopnOperations.sendWebIssue(cta_text, cta_value,source,campaign_id,mContext, false,-1,-1,true,mProgressbar);
			}
		}

	}



}
