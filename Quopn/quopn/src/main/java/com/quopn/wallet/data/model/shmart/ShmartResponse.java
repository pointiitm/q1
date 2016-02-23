package com.quopn.wallet.data.model.shmart;

import android.util.Log;

import com.quopn.wallet.interfaces.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hari on 24/9/15.
 */
public class ShmartResponse extends JSONObject implements Response {
    private static final String TAG = "Quopn/ShmartResponse";
    boolean isSuccess;
    String message = "";
    int errorCode;

    public ShmartResponse(String responseString) throws JSONException {
        super(responseString);

        try {
            if (has("status")) {
                String status = getString("status").toLowerCase();
                isSuccess = status.equals("success");
            }

            if (has("message")) {
                message = getString("message");
            } else if (has("status_msg")) {
                message = getString("status_msg");
            }
            if (has("error_code")) {
                errorCode = Integer.parseInt(getString("error_code"));
            }

        }
        catch (JSONException e) {}
        catch (NumberFormatException e) {}
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
