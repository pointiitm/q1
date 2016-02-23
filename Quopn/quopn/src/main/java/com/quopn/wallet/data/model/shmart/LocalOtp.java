package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.interfaces.Response;

import org.json.JSONException;

/**
 * Created by hari on 21/10/15.
 */
public class LocalOtp extends ShmartResponse {
    public LocalOtp(String data) throws JSONException {
        super(data);
    }
}
