package com.quopn.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.CityData;

import java.util.List;

public class CityAdapter extends ArrayAdapter<CityData> {
	private LayoutInflater mLayoutInflater;
	
	public CityAdapter(Context context, int resource,
			List<CityData> values) {
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	static class ViewHolderItem {
		TextView statename;
		
	}
	ViewHolderItem viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final CityData item = getItem(position);
		viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.location_spinner,null);
			viewHolder.statename=(TextView)convertView.findViewById(R.id.text1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		
		viewHolder.statename.setText(item.getCityName());
		viewHolder.statename.setTag(item.getCityId());
		return convertView;
	}

}
