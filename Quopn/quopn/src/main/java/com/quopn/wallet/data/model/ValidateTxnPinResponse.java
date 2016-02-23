package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

/**
 * Created by Sandeep on 15-Jan-16.
 */
public class ValidateTxnPinResponse implements Response{
    private String status;
    private String message;
    private String mobileWalletId;
    private String error_code;
    boolean isSuccess;

    public boolean isSuccess() {
        if (!status.isEmpty()) {
            isSuccess = status.equalsIgnoreCase("success");
        } else {
            isSuccess = false;
        }
        return isSuccess;
    }

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

    public String getMobileWalletId() {
        return mobileWalletId;
    }

    public void setMobileWalletId(String mobileWalletId) {
        this.mobileWalletId = mobileWalletId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
