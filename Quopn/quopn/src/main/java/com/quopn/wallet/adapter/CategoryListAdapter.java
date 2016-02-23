package com.quopn.wallet.adapter;

/**
 * @author Sumeet
 *
 */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.NewCategoryList;
import com.quopn.wallet.data.model.NewListCategoryContainer;

import java.io.Serializable;
import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<NewListCategoryContainer> {

	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoader mImageLoader;
	private CategorySelectedListener mCategorySelectedListener;
	private ViewHolderItem viewHolder;
	private AnalysisManager mAnalysisManager;
	public interface CategorySelectedListener extends Serializable {
		void onCategorySelected(NewCategoryList category);
	}
	

	@SuppressWarnings("deprecation")
	public CategoryListAdapter(Context context, int resource,
			List<NewListCategoryContainer> values,
			CategorySelectedListener categorySelectedListener) {
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		//mImageLoader = new ImageLoader(context);
		mAnalysisManager=((QuopnApplication)context.getApplicationContext()).getAnalysisManager();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		mDisplayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showStubImage(R.drawable.placeholder_image)
				.cacheOnDisc(true).considerExifParams(true).build();
		this.mCategorySelectedListener = categorySelectedListener;
	}

	static class ViewHolderItem {
		ImageView row_icon1;
//		ImageView row_icon2;
		LinearLayout cardview;
		TextView category_txt1;
//		TextView category_txt2;
		LinearLayout cat_1;
//		LinearLayout cat_2;
		ImageView mProgressBar1;
//		ProgressBar mProgressBar2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		

		final NewListCategoryContainer item = getItem(position);

		if (convertView == null || convertView.getTag()==null) {
			viewHolder = new ViewHolderItem();
			
			convertView = mLayoutInflater.inflate(R.layout.
                            list_template_category,
					null);
			
			viewHolder.row_icon1 = (ImageView) convertView
					.findViewById(R.id.row_icon1);
//			viewHolder.row_icon2 = (ImageView) convertView
//					.findViewById(R.id.row_icon2);
			
			viewHolder.category_txt1 = (TextView) convertView
					.findViewById(R.id.category_txt1);
			
//			viewHolder.category_txt2 = (TextView) convertView
//					.findViewById(R.id.category_txt2);

			viewHolder.cardview = (LinearLayout) convertView
					.findViewById(R.id.cardview);
			
			viewHolder.cat_1 = (LinearLayout) convertView
					.findViewById(R.id.cat_1);
			
//			viewHolder.cat_2 = (LinearLayout) convertView
//					.findViewById(R.id.cat_2);
			viewHolder.mProgressBar1= (ImageView) convertView
					.findViewById(R.id.progressBar_category_list);
//			viewHolder.mProgressBar2= (ProgressBar) convertView
//					.findViewById(R.id.progressBar2);
			// Store the holder with the view.
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

//		switch (position % 2) {
//		case 0:
//			viewHolder.row_icon1
//					.setBackgroundResource(R.drawable.quopn_bg_grey);
//			viewHolder.row_icon2.setBackgroundResource(R.drawable.quopn_bg_red);
//			break;
//
//		default:
//			viewHolder.row_icon1.setBackgroundResource(R.drawable.quopn_bg_red);
//			viewHolder.row_icon2
//					.setBackgroundResource(R.drawable.quopn_bg_grey);
//			break;
//		}

        final ImageView mprogressbar = viewHolder.mProgressBar1;
        AnimationDrawable animation = (AnimationDrawable) mprogressbar.getDrawable();
        animation.start();

		if (item.getCategory1() != null){
			
			
			mImageLoader.displayImage(item.getCategory1().getThumbimage(),
					viewHolder.row_icon1, mDisplayImageOptions, null);
			/*mImageLoader.displayImage(item.getCategory1().getIcon(),
					viewHolder.row_icon1,viewHolder.mProgressBar1);*/
			viewHolder.category_txt1.setText(item.getCategory1().getName());
			viewHolder.cat_1.setVisibility(View.VISIBLE);
		}else{
			viewHolder.cat_1.setVisibility(View.INVISIBLE);
		}

//		if (item.getCategory2() != null){
//			mImageLoader.displayImage(item.getCategory2().getIcon(),
//					viewHolder.row_icon2, mDisplayImageOptions, null);
//			/*mImageLoader.displayImage(item.getCategory2().getIcon(),
//					viewHolder.row_icon2,viewHolder.mProgressBar2);*/
//			viewHolder.category_txt2.setText(item.getCategory2().getCategory());
//			viewHolder.cat_2.setVisibility(View.VISIBLE);
//		}else{
//			viewHolder.cat_2.setVisibility(View.INVISIBLE);
//		}
		
		viewHolder.row_icon1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mContext instanceof MainActivity){
					((MainActivity) mContext).showCategoryScreen(item.getCategory1(), item.getCategory1().getName(), mCategorySelectedListener);
				}
			}
		});

//		viewHolder.row_icon2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mAnalysisManager.send(AnalysisEvents.CATEGORY,item.getCategory2()
//						.getCategoryid());
//				ProductCatFragment.QUOPN_CATEGORY_ID = item.getCategory2()
//						.getCategoryid();
//				ProductCatFragment.QUOPN_CATEGORY_TYPE = item.getCategory2()
//						.getCategory();
//				mCategorySelectedListener.onCategorySelected(item.getCategory2());
//			}
//		});

		return convertView;
	}

}
