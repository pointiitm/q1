package com.quopn.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quopn.wallet.R;
import com.quopn.wallet.data.model.HistoryListData;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryListData> {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private DisplayImageOptions mDisplayImageOptionsHistory;
	private ImageLoader mImageLoaderHistory;
	
	

	public HistoryAdapter(Context context, int resource,
			List<HistoryListData> values) {
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		mImageLoaderHistory = ImageLoader.getInstance();
		mImageLoaderHistory.init(ImageLoaderConfiguration.createDefault(context));
		mDisplayImageOptionsHistory = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.showStubImage(R.drawable.placeholder_myquopns)
				.build();
	}
	
	static class ViewHolderItem {
		ImageView imgProduct_history;
		TextView textProduct;
		TextView textquopncode;
		TextView textReedemed;
		TextView textat;
		TextView textbusinessname;
		
	}
	ViewHolderItem viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final HistoryListData item = getItem(position);
		viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.history,null);
			viewHolder.imgProduct_history = (ImageView) convertView.findViewById(R.id.leftside_imageview_history);
			viewHolder.textProduct = (TextView)convertView.findViewById(R.id.textView);
			viewHolder.textquopncode=(TextView)convertView.findViewById(R.id.textView1);
			viewHolder.textReedemed = (TextView)convertView.findViewById(R.id.textView2);
			viewHolder.textat=(TextView)convertView.findViewById(R.id.textView3);
			viewHolder.textbusinessname=(TextView)convertView.findViewById(R.id.textView4);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		try {
			mImageLoaderHistory.displayImage(item.getThumb_icon1(),viewHolder.imgProduct_history, mDisplayImageOptionsHistory, null);
		} catch (Exception e) {
//			 e.printStackTrace();
//				Log.i("Exception in history", e.getMessage());
		}
		/*viewHolder.textProduct.setEllipsize(TruncateAt.MARQUEE);
		viewHolder.textProduct.setSingleLine(true);
		viewHolder.textProduct.setMarqueeRepeatLimit(-1);
		viewHolder.textProduct.setSelected(true);*/
		viewHolder.textProduct.setText(item.getProductname().toUpperCase()/*item.getCampaignname().toUpperCase()*/); 
		
		viewHolder.textquopncode.setText("QUOPN CODE: "+item.getQuopncode().toUpperCase());
		String date=item.getRedeemed_time();
		date=date.substring(0, date.length()-8);
		viewHolder.textReedemed.setText("ON: "+date.toUpperCase()); 
		viewHolder.textat.setText("AT: ");
		/*viewHolder.textbusinessname.setEllipsize(TruncateAt.MARQUEE);
		viewHolder.textbusinessname.setSingleLine(true);
		viewHolder.textbusinessname.setMarqueeRepeatLimit(-1);
		viewHolder.textbusinessname.setSelected(true);*/
		if (item.getBusiness_name() != null && !item.getBusiness_name().isEmpty()) {
			viewHolder.textbusinessname.setText(item.getBusiness_name().toUpperCase());
		}
		return convertView;
	}

}
