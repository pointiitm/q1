package com.quopn.wallet.data.model.shmart;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hari on 25/9/15.
 */
public class BeneficiariesResponse extends ShmartResponse {
    private String code = "";
    private String name = "";
    private String account = "";
    private String bank = "";

    public BeneficiariesResponse(String responseString) throws JSONException {
        super(responseString);

        try {
            JSONObject accountJson = getJSONArray("beneficiary_list").getJSONObject(0);
            code = accountJson.getString("beneficiary_code");
            name = accountJson.getString("beneficiary_name");
            account = accountJson.getString("beneficiary_acc");
        } catch (JSONException e) {}
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getBank() {
        return bank;
    }
}
