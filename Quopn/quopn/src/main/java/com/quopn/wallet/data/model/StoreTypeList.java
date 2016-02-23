package com.quopn.wallet.data.model;

/**
 * Created by Ankur on 11/12/15.
 */
public class StoreTypeList {
    int _id; // cursor cat_id
    private String cat_name;
    private int cat_id;
    private String cat_icon_url;

    public StoreTypeList() {
    }

    public StoreTypeList(String cat_name,int cat_id,String cat_icon_url){
        this.cat_name = cat_name;
        this.cat_id = cat_id;
        this.cat_icon_url = cat_icon_url;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_icon_url() {
        return cat_icon_url;
    }

    public void setCat_icon_url(String cat_icon_url) {
        this.cat_icon_url = cat_icon_url;
    }

}
