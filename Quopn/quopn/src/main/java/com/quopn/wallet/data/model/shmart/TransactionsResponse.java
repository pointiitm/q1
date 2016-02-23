package com.quopn.wallet.data.model.shmart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hari on 30/9/15.
 */
public class TransactionsResponse extends ShmartResponse {
    private static final String TIMESTAMP_FORMAT = "y-M-d H:m:s";

    public enum Type {
        CREDIT("Loaded", "+")
        , DEBIT("Transferred", "-")
        , SENT("Send", "-")
        , RECEIVED("Receive", "+");

        private String value;
        private String sign="+";
        Type(String value, String sign) {
            this.value = value;
            this.sign = sign;
        }
        public String getValue() { return value; }
        public String getSign() { return sign; }
        public static Type fromValue(String value) {
            for (Type txnType:Type.values()) {
                if (txnType.getValue().equals(value)) {
                    return txnType;
                }
            }

            return null;
        }
        public static Type fromName(String name) {
            for (Type txnType:Type.values()) {
                if (txnType.name().equals(name)) {
                    return txnType;
                }
            }
            return null;
        }
    }

    public static class Transaction {
        private Type type;
        private double amount;
        private Date timestamp;

        public Type getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }



    List<Transaction> transactions = new ArrayList<Transaction>();

    public TransactionsResponse(String responseString) throws JSONException {
        super(responseString);

        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);

        if (has("transactions")) {
            JSONArray array = getJSONArray("transactions");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonTxn = array.getJSONObject(i);

                Transaction txn = new Transaction();
                txn.type = Type.fromValue(jsonTxn.getString("type"));
                if (txn.type == null) { txn.type = Type.CREDIT; }
                txn.amount = jsonTxn.getDouble("amount");
                try {
                    txn.timestamp = dateFormat.parse(jsonTxn.getString("creationTime"));
                } catch (ParseException e) {
                    throw new JSONException(e.getMessage());
                }
                transactions.add(txn);
            }
        }else if (has("txn_list")) {
            JSONArray array = getJSONArray("txn_list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonTxn = array.getJSONObject(i);

                Transaction txn = new Transaction();
//                txn.type = Type.fromValue(jsonTxn.getString("type"));
                txn.type = Type.fromName(jsonTxn.getString("type"));
                if (txn.type == null) { txn.type = Type.CREDIT; }
                txn.amount = jsonTxn.getDouble("amount");
                try {
                    txn.timestamp = dateFormat.parse(jsonTxn.getString("creationTime"));
                } catch (ParseException e) {
                    throw new JSONException(e.getMessage());
                }
                transactions.add(txn);
            }
        }
    }

    public List<Transaction> getTransactions() { return transactions; }
}
