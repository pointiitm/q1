package com.quopn.wallet.data.model;

import java.util.ArrayList;
import java.util.List;


public class Gift {
	private String category;
	private String categoryid;
	private String icon;
	private String title;
	private List<GiftsContainer> list=new ArrayList<GiftsContainer>();
	
	
	
	public String getTitle() {
		return title;
	}
	public String getCategory() {
		return category;
	}
	public String getCategoryid() {
		return categoryid;
	}
	public String getIcon() {
		return icon;
	}
	public List<GiftsContainer> getList() {
		return list;
	}
	
	
	
	
}
