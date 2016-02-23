package com.quopn.wallet.data.model;

import com.android.volley.VolleyError;
import com.quopn.wallet.interfaces.Response;

/**
 * Created by hari on 9/10/15.
 */
public class NetworkError implements Response {
    private VolleyError networkError;

    public NetworkError(VolleyError networkError) {
        this.networkError = networkError;
    }

    public VolleyError getError() { return networkError; }
}
