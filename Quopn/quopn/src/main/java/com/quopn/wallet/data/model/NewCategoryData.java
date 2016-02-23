package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandeep on 9/3/2015.
 */
public class NewCategoryData implements Response {

    private boolean error;
    private String message;
    private List<NewCategoryList> categorylist=new ArrayList<NewCategoryList>();
    private List<FooterTag> footer = new ArrayList<FooterTag>();
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NewCategoryList> getCategorylist() {
        return categorylist;
    }

    public void setCategorylist(List<NewCategoryList> categorylist) {
        this.categorylist = categorylist;
    }
    public List<FooterTag> getFooter() {
        return footer;
    }
}
