package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.shmart.ShmartFlow;

import org.json.JSONException;

/**
 * Created by hari on 30/9/15.
 */
public class GetPrefResponse extends ShmartResponse {
    private ShmartFlow.EWalletUsagePref pref;

    public GetPrefResponse(String responseString) throws JSONException {
        super(responseString);

        if (has("setting")) {
            this.pref = ShmartFlow.EWalletUsagePref.fromPref(getString("setting"));
        }
    }

    public ShmartFlow.EWalletUsagePref getPref() { return pref; }
}
