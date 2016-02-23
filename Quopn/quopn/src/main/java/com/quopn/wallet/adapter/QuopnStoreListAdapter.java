package com.quopn.wallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.quopn.wallet.R;
import com.quopn.wallet.ShopsAroundMap;
import com.quopn.wallet.data.DataBaseHelper;
import com.quopn.wallet.data.model.StoreDataList;
import com.quopn.wallet.utils.QuopnConstants;

import java.util.ArrayList;
import java.util.List;

public class QuopnStoreListAdapter extends ArrayAdapter<StoreDataList>{
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	DataBaseHelper dbHelper;
	List<StoreDataList> storedatalist = new ArrayList<StoreDataList>();
	TextView googleplayServies;
	public QuopnStoreListAdapter(Context context, int resource,
			List<StoreDataList> values) {
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		dbHelper = DataBaseHelper.getInstance(mContext);
	}
	
	static class ViewHolderItem {
		RelativeLayout relLayPinBg;
		RelativeLayout relLayAddressBg;
		TextView business_name;
		TextView business_address;
		TextView business_open_hours;
		TextView distance_text;
		TextView open_hours_heading;
		ImageView map_pin_image;
		RelativeLayout layout_quopnlist;
		
	}
	ViewHolderItem viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final StoreDataList item = getItem(position);
			
		viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.quopn_store_list,null);

			viewHolder.map_pin_image=(ImageView)convertView.findViewById(R.id.pin_image);
			viewHolder.business_name=(TextView)convertView.findViewById(R.id.buissness_name);
			viewHolder.business_address=(TextView)convertView.findViewById(R.id.buissness_address);
			viewHolder.business_open_hours=(TextView)convertView.findViewById(R.id.opentext);
			viewHolder.business_open_hours.setTypeface(null, Typeface.BOLD);
			viewHolder.distance_text=(TextView)convertView.findViewById(R.id.distance_text);
			viewHolder.open_hours_heading = (TextView)convertView.findViewById(R.id.heading_open_hours);
			viewHolder.layout_quopnlist = (RelativeLayout)convertView.findViewById(R.id.store_list_layout);
			googleplayServies = (TextView) convertView.findViewById(R.id.googleplayServies);
			convertView.setTag(viewHolder);
			
			viewHolder.relLayAddressBg = (RelativeLayout)convertView.findViewById(R.id.layout_desc_text);
			LayoutParams layParams01 = viewHolder.relLayAddressBg.getLayoutParams();
			
			
			viewHolder.relLayPinBg = (RelativeLayout)convertView.findViewById(R.id.pin_layout);
			RelativeLayout.LayoutParams layParams02 = (RelativeLayout.LayoutParams)viewHolder.relLayPinBg.getLayoutParams();
			layParams02.height = layParams01.height;
			viewHolder.relLayPinBg.setLayoutParams(layParams02);
			layParams02.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.relLayAddressBg.getId());
			layParams02.addRule(RelativeLayout.ALIGN_TOP, viewHolder.relLayAddressBg.getId());
			viewHolder.relLayPinBg.setGravity(Gravity.CENTER);
					
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
			
		}
		try {
			viewHolder.business_name.setText(item.getBusiness_Name().trim());
			viewHolder.business_name.setTypeface(null,Typeface.BOLD);
			viewHolder.business_address.setText(item.getBusiness_Address().trim());
			//viewHolder.business_open_hours.setText(item.getOpen_hours().trim());
			if(item.getDistance()!=null){
				viewHolder.distance_text.setVisibility(View.VISIBLE);
				viewHolder.distance_text.setText(item.getDistance().trim());
			}else{
				viewHolder.distance_text.setVisibility(View.GONE);	
			}
			
			if(item.getOpen_hours()!=null){
				viewHolder.open_hours_heading.setVisibility(View.VISIBLE);
				viewHolder.open_hours_heading.setText("OPEN HOURS:"+item.getOpen_hours());
			}else{
				viewHolder.open_hours_heading.setVisibility(View.GONE);
			}
			
			viewHolder.open_hours_heading.setTypeface(null, Typeface.BOLD);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		viewHolder.layout_quopnlist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
				if (status != ConnectionResult.SUCCESS) {
					updateGoogleplay();
				} else {
					Intent shopsaroundMap = new Intent(mContext, ShopsAroundMap.class);
					//shopsaroundMap.putExtra("storeUid", item.getStore_uid());
					shopsaroundMap.putExtra("business_name", item.getBusiness_Name());
					shopsaroundMap.putExtra("buisness_add", item.getBusiness_Address());
					if (item.getOpen_hours() != null) {
						shopsaroundMap.putExtra("open_hours", item.getOpen_hours());
					} else {
						shopsaroundMap.putExtra("open_hours", "");
					}
					shopsaroundMap.putExtra("latitude", item.getLatitude());
					shopsaroundMap.putExtra("longitude", item.getLongitude());
					shopsaroundMap.putExtra("store_type", item.getStoretype());
					shopsaroundMap.putExtra("store_accuracy", item.getAccuracy());
					shopsaroundMap.putExtra("store_Type_id", item.getStoretypeid());
					((Activity) mContext).startActivityForResult(shopsaroundMap, QuopnConstants.HOME_PRESS);
				}
			}
		});
		
			
		return convertView;
	}

	public void updateGoogleplay() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
// set title
		alertDialogBuilder.setTitle("Update Google Play Services");
// set dialog message
		alertDialogBuilder
				.setMessage("This Application Want To Update You Google Play Services App")
				.setCancelable(true)
				.setPositiveButton("Update",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								callMarketPlace();
								((Activity)mContext).finish();
							}
						});
		alertDialogBuilder.show();
	}
	public void callMarketPlace() {
		try {
			((Activity)mContext).startActivityForResult(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + "com.google.android.gms")), 1);
		}
		catch (android.content.ActivityNotFoundException anfe) {
			((Activity)mContext).startActivityForResult(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.gms")), 1);
		}
	}
}
