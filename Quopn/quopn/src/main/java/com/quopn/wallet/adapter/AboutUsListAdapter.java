package com.quopn.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.AboutUsData;

import java.util.HashMap;
import java.util.List;

public class AboutUsListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<com.quopn.wallet.data.model.AboutUsData> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;
	private String url;
	private String finalUrl;
	private String currentUrl;
	private int running = 0;
	private boolean loadComplete = false;
	private WebView txtListChild;
	private ImageView mImgArrow;

	public AboutUsListAdapter(Context context,
			List<AboutUsData> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(
				this._listDataHeader.get(groupPosition).getName()).get(
				childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.aboutlist_item, null);
		}

		txtListChild = (WebView) convertView
				.findViewById(R.id.lblListItem);

		txtListChild.getSettings().setJavaScriptEnabled(true);
		txtListChild.setWebViewClient(new myWebClient());
//		txtListChild.loadUrl(childText);
//		String summary = "<html><body>You scored <b>192</b> points.</body></html>";
//		txtListChild.loadData(summary, "text/html", "utf-8");

		return convertView;
	}
	
	public void setText(String argData) {
//		txtListChild.loadData(argData, "text/html", "utf-8");
		txtListChild.loadDataWithBaseURL("http://quopn.com", argData, "text/html", "utf-8", "");
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(
				this._listDataHeader.get(groupPosition).getName()).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		AboutUsData headerTitle = (AboutUsData) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.aboutlist_group, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.row_title);
		ImageView lblListIcon = (ImageView) convertView
				.findViewById(R.id.row_icon);
		lblListHeader.setText(headerTitle.getName());
		lblListIcon.setImageResource(headerTitle.getPic());
		
		mImgArrow = (ImageView) convertView.findViewById(R.id.arrow_about);
		if(isExpanded){
			mImgArrow.setImageResource(R.drawable.arrowdown);
		} else{
			mImgArrow.setImageResource(R.drawable.arrowright);
		}

		return convertView;
	}
	
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			running = Math.max(running, 1);
			/*
			 * if (loadComplete) { enableButtons(); }
			 */
			super.onPageStarted(view, url, favicon);

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String argUrl) {
			
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(argUrl));
			
			if (i.resolveActivity(_context.getPackageManager()) == null) {
				Toast.makeText(
						_context,
						"There are no applications to handle your request",
						Toast.LENGTH_LONG).show();
			} else {
				_context.startActivity(i);
//				view.loadUrl(argUrl);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub

			super.onPageFinished(view, url);

			if (--running == 0) {
				// webView.clearHistory();
				loadComplete = true;
				running = Integer.MAX_VALUE;
			}

		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			view.loadUrl("file:///android_asset/myerrorpage.html");

		}
	}
}
