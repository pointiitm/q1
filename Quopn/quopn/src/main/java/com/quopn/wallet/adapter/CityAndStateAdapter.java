package com.quopn.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.CityStateTuple;

import java.util.List;

public class CityAndStateAdapter extends ArrayAdapter<CityStateTuple> {
	private LayoutInflater mLayoutInflater;
	
	public CityAndStateAdapter(Context context, int resource,
			List<CityStateTuple> values) {
		
		super(context, resource, values);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final CityStateTuple item = getItem(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.location_spinner,null);
			
		}
		
		TextView tvCity = (TextView)convertView.findViewById(R.id.text1);
		tvCity.setText(item.getCityName() + ", " + item.getStateName());
		tvCity.setTag(item.getCityId()+"~"+item.getStateId());
		return convertView;
	}

}
