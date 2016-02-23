package com.quopn.wallet.data.model;
/**
 * @author Sandeep
 *
 */
import java.util.ArrayList;
import java.util.List;

public class Category {

	private String name;
	private String thumbimage;
	private String categoryid;
	private List<QuopnList> list=new ArrayList<QuopnList>();
	
	
	public String getCategory() {
		return name;
	}
	public String getIcon() {
		return thumbimage;
	}
	public String getCategoryid() {
		return categoryid;
	}
	public List<QuopnList> getList() {
		return list;
	}
	public void setCategory(String category) { this.name = category; }
	public void setIcon(String icon) {
		this.thumbimage = icon;
	}
	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}
	public void setList(List<QuopnList> list) {
		this.list = list;
	}
	
	
	
}
