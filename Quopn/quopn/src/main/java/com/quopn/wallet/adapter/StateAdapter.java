package com.quopn.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.StateData;

import java.util.List;

public class StateAdapter extends ArrayAdapter<StateData> {
	private LayoutInflater mLayoutInflater;
	
	public StateAdapter(Context context, int resource,
			List<StateData> values) {
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
		
		final StateData item = getItem(position);
		viewHolder = new ViewHolderItem();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.location_spinner,null);
			viewHolder.statename=(TextView)convertView.findViewById(R.id.text1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		
		viewHolder.statename.setText(item.getStateName());
		viewHolder.statename.setTag(item.getStateId());
		return convertView;
	}

}
