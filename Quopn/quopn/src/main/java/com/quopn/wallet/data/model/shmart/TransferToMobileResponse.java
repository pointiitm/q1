package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.interfaces.Response;

import org.json.JSONException;

/**
 * Created by hari on 29/9/15.
 */
public class TransferToMobileResponse extends ShmartResponse {
    public TransferToMobileResponse(String data) throws JSONException {
        super(data);
    }
}
