package com.quopn.wallet.data.model.shmart;

import org.json.JSONException;

/**
 * Created by hari on 24/9/15.
 */
public class BalanceResponse extends ShmartResponse {
    private double balance;

    public BalanceResponse(String responseString) throws JSONException {
        super(responseString);

        try { balance = Double.parseDouble(getString("available_balance")); }
        catch (JSONException e) {}
        catch (NumberFormatException e) {}
    }

    public double getBalance() { return balance; }
}
