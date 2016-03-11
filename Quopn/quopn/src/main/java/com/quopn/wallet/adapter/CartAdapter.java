package com.quopn.wallet.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CartListData;
import com.quopn.wallet.data.model.RemoveFromCartData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.PreferenceUtil.SHARED_PREF_KEYS;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.AspectRatioImageView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends ArrayAdapter<CartListData> {
	protected static final String TAG = "Quopn/CartAdapter";
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private DisplayImageOptions mDisplayImageOptionsCart;
	private ImageLoader mImageLoaderCart;
	private ConnectionListener mConnectionListener_removefromcart,mConnectionListener_campaigndetails;
	private AnalysisManager mAnalysisManager;
	private int lastSelectedCartId = -1;
	List<CartListData> mCartListData;

	public CartAdapter(Context context, int resource,
			List<CartListData> values) {
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.mCartListData=values;
		mAnalysisManager=((QuopnApplication)context.getApplicationContext()).getAnalysisManager();
		mImageLoaderCart = ImageLoader.getInstance();
		mImageLoaderCart.init(ImageLoaderConfiguration.createDefault(context));
		mDisplayImageOptionsCart = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.showStubImage(R.drawable.placeholder_myquopns)
				.build();
	}
	
	static class ViewHolderItem {
		int cartId;
		ImageView imgProduct_cart;
		TextView textProduct;
		TextView textCampaignName;
		TextView textCount;
		RelativeLayout removequopns_imgview_bg_layout;
		RelativeLayout mycartlayout;
		
		RelativeLayout detaillayout;
		AspectRatioImageView detail_upper_imageview;
		TextView detail_textView;
		TextView detail_textView1;
		TextView detail_textView2;
		RelativeLayout detail_removequopns_imgview_bg_layout;
		ImageView mProgressBar;
		
	}
	ViewHolderItem viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final CartListData item = getItem(position);
		viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.cart,null);
			viewHolder.imgProduct_cart = (ImageView) convertView.findViewById(R.id.leftside_imageview_cart);
			viewHolder.textProduct = (TextView)convertView.findViewById(R.id.textView);
			viewHolder.textCampaignName = (TextView)convertView.findViewById(R.id.textView1);
			viewHolder.textCount=(TextView)convertView.findViewById(R.id.textView2);
			viewHolder.removequopns_imgview_bg_layout=(RelativeLayout) convertView.findViewById(R.id.removequopns_imgview_bg_layout);
			viewHolder.mycartlayout=(RelativeLayout) convertView.findViewById(R.id.mycartlayout);
			
			viewHolder.detaillayout=(RelativeLayout)convertView.findViewById(R.id.detaillayout);
			viewHolder.detail_upper_imageview=(AspectRatioImageView)convertView.findViewById(R.id.detail_upper_imageview);
			viewHolder.detail_textView=(TextView)convertView.findViewById(R.id.detail_textView);
			viewHolder.detail_textView1=(TextView)convertView.findViewById(R.id.detail_textView1);
			viewHolder.detail_textView2=(TextView)convertView.findViewById(R.id.detail_textView2);
			viewHolder.detail_removequopns_imgview_bg_layout=(RelativeLayout)convertView.findViewById(R.id.detail_removequopns_imgview_bg_layout);
			viewHolder.mProgressBar = (ImageView) convertView.findViewById(R.id.progressBar_individualItem);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		viewHolder.cartId = item.getCartid();
		
		try {
			mImageLoaderCart.displayImage(item.getThumb_icon1(),viewHolder.imgProduct_cart, mDisplayImageOptionsCart, null);
		} catch (Exception e) {
		}
		if(item.getTitle()!=null){
			viewHolder.textProduct.setText(item.getTitle().toUpperCase()); 
		}else{
			viewHolder.textProduct.setText(""); 
		}
		if(item.getCampaignname()!=null){
			viewHolder.textCampaignName.setText(item.getCampaignname()); 
		}else{
			viewHolder.textCampaignName.setText(""); 
		}
		
		viewHolder.removequopns_imgview_bg_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteCartClick(item);
			}
		});
		
	  viewHolder.mycartlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RelativeLayout mainLayout = (RelativeLayout) v.getParent();
				ViewHolderItem clickedViewHolder
					= (ViewHolderItem) mainLayout.getTag();
				lastSelectedCartId = clickedViewHolder.cartId;
				
				CartAdapter.this.notifyDataSetChanged();
			}
		});
	  
	  
	  viewHolder.detaillayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lastSelectedCartId = -1;
				CartAdapter.this.notifyDataSetChanged();
			}
		});
	  
	  
	  
	  if (lastSelectedCartId==viewHolder.cartId) {
			viewHolder.mycartlayout.setVisibility(View.GONE);
			viewHolder.detaillayout.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mycartlayout.setVisibility(View.VISIBLE);
			viewHolder.detaillayout.setVisibility(View.GONE);
		}
	
	    if(item.getTitle()!=null){
	    	viewHolder.detail_textView.setText(item.getTitle().toUpperCase());
	    }else{
	    	viewHolder.detail_textView.setText("");
	    }
	    
	    if(item.getCart_desc()!=null){
	    	viewHolder.detail_textView1.setText(item.getCart_desc());
	    }else{
	    	viewHolder.detail_textView1.setText("");
	    }
		
		
		String date=item.getEnddate();
		date=date.substring(0, date.length()-8);
		viewHolder.detail_textView2.setText("EXPIRES ON: "+date); 
			try {
				mImageLoaderCart.displayImage(item.getCart_image(),viewHolder.detail_upper_imageview, mDisplayImageOptionsCart, null);
			} catch (Exception e) {
			}
	
		viewHolder.detail_removequopns_imgview_bg_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteCartClick(item);
			}
		});
	
	
		return convertView;
	}
	
	
	private void deleteCartClick(CartListData item){
		final CartListData mItem=item;
		mConnectionListener_removefromcart=new ConnectionListener() {
			
			@Override
			public void onResponse(int responseResult,Response response) {
				if(response instanceof RemoveFromCartData){
					RemoveFromCartData removefromcartdata=(RemoveFromCartData)response;
					if(removefromcartdata.isError()==true){
						Dialog dialog=new Dialog(mContext, R.string.dialog_title_error, removefromcartdata.getMessage());
						dialog.show();
					}else{
						//Remove this current quopn from mycart list and added this quopn in Quopn listing database.
						sendBroadCast(mContext,mItem.getCartid(),mItem.getSaving_value(),mItem.getCampaignid());
						
					}
				}
			}

			@Override
			public void onTimeout(ConnectRequest request) {
				
			}

			@Override
			public void myTimeout(String requestTag) {

			}
		};
		getRemoveFromCart(mContext,mConnectionListener_removefromcart,mItem.getCartid());
	}
	
	private void sendBroadCast(Context context,int cartid,String savingvalue,String campaignid){
		
		//Remove this current quopn from mycart list and saving value is minus from Total approx saving value and set into database.
		//Added this quopn in Quopn listing database.
		
		QuopnConstants.MY_CART_COUNT = PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference_int(SHARED_PREF_KEYS.MYCARTCOUNT)  - 1;
		PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MYCARTCOUNT, QuopnConstants.MY_CART_COUNT);
		
		Intent intent = new Intent(QuopnConstants.BROADCAST_UPDATE_MYCARTCOUNTER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        
		context.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART,ITableData.TABLE_MYCART.COLUMN_CARTID + " = ? ",	new String[] { ""+cartid });
		Cursor cursor1 = null;
		BigDecimal mTotalSaving=null;
		BigDecimal mIndividual_cartSaving=null;
		cursor1 = context.getContentResolver().query(ConProvider.CONTENT_URI_MYCART,null,null,null,ITableData.TABLE_MYCART.COLUMN_ID + " desc");
		if(cursor1==null || cursor1.getCount()==0){
			
		}else{
			while(cursor1.moveToNext()){
				String approxsaving=cursor1.getString(cursor1.getColumnIndex(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING));
				mTotalSaving =new BigDecimal(approxsaving) ;
				mIndividual_cartSaving=new BigDecimal(savingvalue);
				mTotalSaving = mTotalSaving.subtract(mIndividual_cartSaving);
				mTotalSaving.setScale(2, BigDecimal.ROUND_CEILING);
				
			}
		}
		String.format("%.2f",mTotalSaving);
		ContentValues cv = new ContentValues();
		cv.put(ITableData.TABLE_MYCART.COLUMN_APPROX_SAVING,String.valueOf(mTotalSaving));
		context.getContentResolver().update(ConProvider.CONTENT_URI_MYCART,	cv,null,null);
		
		Intent intent1 = new Intent(QuopnConstants.BROADCAST_UPDATE_MYCART_AFTER_REMOVEFROMCART);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
		
		 	Cursor cursor = null;
			cursor = context.getContentResolver().query(ConProvider.CONTENT_URI_QUOPN,new String[]{ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID,ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED,ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS},ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",	new String[] { campaignid },null);
	        if(cursor.getCount()==0){
	        	Intent intent2 = new Intent(QuopnConstants.BROADCAST_UPDATE_QUOPNS);
	            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
	        }else{
	        	cursor.moveToFirst();
	        	int alreadyissued_count=-1;
	        	int availablequopn_count=-1;
	        	String alreadyissued=cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED));
	        	String availablequopn=cursor.getString(cursor.getColumnIndex(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS));
	        	alreadyissued_count=Integer.parseInt(alreadyissued);
	        	availablequopn_count=Integer.parseInt(availablequopn);
	        	if(alreadyissued_count>0){
	        		alreadyissued_count++;
	        	}else if(availablequopn_count>0){
	        		availablequopn_count++;
	        	}
	        	
	        	if((alreadyissued_count + availablequopn_count)>0){
	        		ContentValues cv1 = new ContentValues();
		    		cv1.put(ITableData.TABLE_QUOPNS.COLUMN_ALREADY_ISSUED,alreadyissued_count);
		    		cv1.put(ITableData.TABLE_QUOPNS.COLUMN_AVAILABLE_QUOPNS,availablequopn_count);
		    		context.getContentResolver().update(ConProvider.CONTENT_URI_QUOPN,	cv1,ITableData.TABLE_QUOPNS.COLUMN_QUOPN_ID + " = ? ",	new String[] { campaignid });
		    		
		    		//here quopnlisting adapter refreshed.
		    		((MainActivity)context).QuopnlistingAdapterRefreshed();
	        	}
	        }
	        cursor.close();
		
		
	}
	private void getRemoveFromCart(Context context, ConnectionListener connectionListener,int cartid) {

		if (QuopnUtils.isInternetAvailable(context)) {
			String api_key = PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
			if (!TextUtils.isEmpty(api_key)) {
				mAnalysisManager.send(AnalysisEvents.REMOVE_FROM_CART,""+cartid);
				Map<String, String> params = new HashMap<String, String>();
				params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
				Map<String, String> params1 = new HashMap<String, String>();
				params1.put(QuopnApi.ParamKey.CARTID,""+cartid);
				ConnectionFactory connectionFactory = new ConnectionFactory(
						context, connectionListener);
				connectionFactory.setHeaderParams(params);
				connectionFactory.setPostParams(params1);
				connectionFactory.createConnection(QuopnConstants.REMOVE_FROM_CART_CODE);
			} else {
				//show error.
			}
		}else{
			Dialog dialog=new Dialog(mContext, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
			dialog.show();
		}
	}
}
