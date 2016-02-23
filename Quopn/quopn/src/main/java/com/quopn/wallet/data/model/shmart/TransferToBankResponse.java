package com.quopn.wallet.data.model.shmart;

import org.json.JSONException;

/**
 * Created by hari on 29/9/15.
 */
public class TransferToBankResponse extends ShmartResponse {
    public TransferToBankResponse(String responseString) throws JSONException {
        super(responseString);
    }
}
