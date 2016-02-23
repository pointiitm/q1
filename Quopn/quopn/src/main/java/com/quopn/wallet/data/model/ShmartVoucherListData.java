package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vaibhav
 */

public class ShmartVoucherListData implements Response {

    private String status;
    private String error_code;
    private List<VoucherList> voucherList=new ArrayList<VoucherList>();


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public List<VoucherList> getVoucherList() {
        return voucherList;
    }
}
