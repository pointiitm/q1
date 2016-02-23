package com.quopn.wallet.data.model.shmart;

import org.json.JSONException;

/**
 * Created by hari on 7/10/15.
 */
public class WalletStatusResponse extends ShmartResponse {
    public WalletStatusResponse(String responseString) throws JSONException {
        super(responseString);
    }
}
