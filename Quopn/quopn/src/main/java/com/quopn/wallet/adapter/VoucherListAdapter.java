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
import android.widget.ProgressBar;
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
import com.quopn.wallet.data.model.VoucherList;
import com.quopn.wallet.fragments.ProductCatFragment;
import com.quopn.wallet.utils.QuopnConstants;

import java.io.Serializable;
import java.util.List;

public class VoucherListAdapter extends ArrayAdapter<VoucherList> {
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
	private VoucherListAdapter.VoucherSelectedListener voucherSelectedListener;
	private AnalysisManager mAnalysisManager;
	//private GiftDetailAddToCartListener mGiftDetailAddToCartListener;
	private MainActivity mMainActivity;
    int addCounter;
    int subCounter;
    int subCounter1;
    public interface VoucherSelectedListener extends Serializable {
		void onQuopnOpened();
	}

	public VoucherListAdapter(Context context, int resource,List<VoucherList> values) {
		super(context, resource, values);
		mMainActivity = (MainActivity)context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.voucherSelectedListener = voucherSelectedListener;
		//this.mGiftDetailAddToCartListener=giftDetailAddToCartListener;
		//mAnalysisManager=((QuopnApplication)context.getApplicationContext()).getAnalysisManager();
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
		  TextView mIssuedcoupn_txt;
		  ImageView mAdd_voucher;
		  TextView missuavailable_txt;
		//RelativeLayout mAddtocart_Relativelayout;
       // ProgressBar mProgressbar;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final VoucherList item = getItem(position);

        ViewHolderItem viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.list_template_voucher, null);
			viewHolder.mImage = (ImageView) convertView.findViewById(R.id.row_icon);
			viewHolder.mIssuedcoupn_txt = (TextView) convertView.findViewById(R.id.issued_coupn);
			viewHolder.mAdd_voucher = (ImageView) convertView.findViewById(R.id.add_voucher);
			viewHolder.missuavailable_txt = (TextView) convertView.findViewById(R.id.issue_available);
			//viewHolder.mAddtocart_Relativelayout=(RelativeLayout) convertView.findViewById(R.id.addtocart_relativelayout);
//            viewHolder.mProgressbar=(ProgressBar) convertView.findViewById(R.id.progressBar_individualItem);
					
			// Store the holder with the view.
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}


        viewHolder.missuavailable_txt.setText(item.getIssue_available());
        viewHolder.mIssuedcoupn_txt.setText(item.getIssued_coupons());


		return convertView;
	}

	
}
