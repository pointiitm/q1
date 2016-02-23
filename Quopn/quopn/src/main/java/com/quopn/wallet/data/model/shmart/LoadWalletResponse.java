package com.quopn.wallet.data.model.shmart;

import com.quopn.wallet.interfaces.Response;

/**
 * Created by hari on 29/9/15.
 */
public class LoadWalletResponse implements Response {
    private String html;

    public LoadWalletResponse(String data) {
        html = data;
    }

    public String getHtml() { return html; }
}
