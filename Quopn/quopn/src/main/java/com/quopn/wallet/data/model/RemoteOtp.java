package com.quopn.wallet.data.model;

import com.quopn.wallet.data.model.shmart.ShmartResponse;
import com.quopn.wallet.interfaces.Response;

import org.json.JSONException;

/**
 * Created by hari on 21/10/15.
 */
public class RemoteOtp extends ShmartResponse {
    public RemoteOtp(String data) throws JSONException {
        super(data);
    }
}
