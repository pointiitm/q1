package com.quopn.wallet.data.model;

import com.quopn.wallet.interfaces.Response;

/**
 * @author Vaibhav
 */

public class ShmartGenerateOTPData implements Response {

    private String status;
    private String message;
    private String mobileWalletId;
    private String error_code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobileWalletId() {
        return mobileWalletId;
    }

    public void setMobileWalletId(String mobileWalletId) {
        this.mobileWalletId = mobileWalletId;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
