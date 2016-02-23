package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.utils.QuopnApi;

import org.json.JSONException;

/**
 * Created by hari on 30/9/15.
 */
public class UpdatePrefResponse extends ShmartResponse {
    private String defaultWallet;
    public UpdatePrefResponse(String responseString) throws JSONException {
        super(responseString);
        if (has(QuopnApi.ParamKey.DEFAULTWALLET)) {
            this.defaultWallet = getString(QuopnApi.ParamKey.DEFAULTWALLET);
        }
    }

    public String getDefaultWallet() {
        return defaultWallet;
    }
}
