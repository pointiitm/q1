package com.quopn.wallet.data.model.citrus;

import com.quopn.wallet.interfaces.Response;

import java.util.ArrayList;

/**
 * Created by Sandeep on 15-Jan-16.
 */
public class CitrusWalletListData implements Response{
    private boolean error;
    private ArrayList<WalletListData> walletList=new ArrayList<WalletListData>();
    private String message;
    private String error_code;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<WalletListData> getWalletList() {
        return walletList;
    }

    public void setWalletList(ArrayList<WalletListData> walletList) {
        this.walletList = walletList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
