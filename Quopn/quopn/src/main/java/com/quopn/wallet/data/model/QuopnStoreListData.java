package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

public class QuopnStoreListData implements Response {

	private boolean error;
	private List<StoreDataList> store=new ArrayList<StoreDataList>();
	private List<StoreTypeList> store_cat=new ArrayList<StoreTypeList>();
	private String message;

	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public List<StoreDataList> getStore() {
		return store;
	}
	public void setStore(List<StoreDataList> store) {
		this.store = store;
	}
	public List<StoreTypeList> getStore_cat() {
		return store_cat;
	}
	public void setStore_cat(List<StoreTypeList> store_cat) {
		this.store_cat = store_cat;
	}
	public String getMessage() {
		return message;
	}
}
