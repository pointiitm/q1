package com.quopn.wallet.data.model.shmart;

import org.json.JSONException;

/**
 * Created by hari on 3/10/15.
 */
public class LoadWalletStatusResponse extends ShmartResponse {
    private int statusCode;

    public LoadWalletStatusResponse(String responseString) throws JSONException {
        super(responseString);
        if (has("status_code")) {
            statusCode = getInt("status_code");
        }
    }

    public int getStatusCode() {
        return statusCode;
    }
}
