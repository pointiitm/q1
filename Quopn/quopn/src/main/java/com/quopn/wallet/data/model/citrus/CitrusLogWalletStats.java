package com.quopn.wallet.data.model.citrus;

import com.orhanobut.logger.Logger;
import com.quopn.wallet.interfaces.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sandeep on 15-Jan-16.
 */
public class CitrusLogWalletStats implements Response{
    private String status;
    private String message;
    private String mobileWalletId;
    private String defaultWallet;
    private String error_code;
    private String apiName;
    boolean isSuccess;
    private ArrayList<CitrusBeneficiaryData> beneficiary_list;

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        if (!status.isEmpty()) {
            isSuccess = status.equalsIgnoreCase("success");
        } else {
            isSuccess = false;
        }
        return isSuccess;
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

    public String getDefaultWallet() {
        return defaultWallet;
    }

    public void setDefaultWallet(String defaultWallet) {
        this.defaultWallet = defaultWallet;
    }

    public String getError_code() {
        return error_code;
    }

    public int getError_code_int() { return Integer.parseInt(error_code); }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public ArrayList<CitrusBeneficiaryData> getBeneficiary_list() {
        return beneficiary_list;
    }

    public void setBeneficiary_list(ArrayList<CitrusBeneficiaryData> beneficiary_list) {
        this.beneficiary_list = beneficiary_list;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public JSONObject getJSON () {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", this.status);
            jsonObject.put("message", this.message);
            jsonObject.put("mobileWalletId", this.mobileWalletId);
            jsonObject.put("defaultWallet", this.defaultWallet);
            jsonObject.put("error_code", this.error_code);
            jsonObject.put("apiName", this.apiName);
            jsonObject.put("isSuccess", this.isSuccess);
            if (this.beneficiary_list != null && !this.beneficiary_list.isEmpty()) {
                jsonObject.put("beneficiary_list", this.beneficiary_list.toString());
            }
        } catch (JSONException e) {
            Logger.e(e.getMessage());
        }
        return jsonObject;
    }
}
