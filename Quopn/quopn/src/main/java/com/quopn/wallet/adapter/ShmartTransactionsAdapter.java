package com.quopn.wallet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.shmart.TransactionsResponse;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by hari on 1/10/15.
 */
public class ShmartTransactionsAdapter extends ArrayAdapter<TransactionsResponse.Transaction> {
    private static final String TAG = "Quopn/ShmartTxns";
    private Context context;
    private List<TransactionsResponse.Transaction> transactions;

    public ShmartTransactionsAdapter(Context context, List<TransactionsResponse.Transaction> transactions) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public TransactionsResponse.Transaction getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TransactionsResponse.Transaction txn = transactions.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.list_item_transaction, null);
        }

//        ((TextView) convertView.findViewById(R.id.tvTransactionAmount)).setText(
//                txn.getType().getSign() + " " + context.getResources().getString(R.string.Rs) + String.valueOf(txn.getAmount()));
        ((TextView) convertView.findViewById(R.id.tvTransactionAmount)).setText(
                context.getResources().getString(R.string.Rs) + String.valueOf(txn.getAmount()));
//        ((TextView) convertView.findViewById(R.id.tvTransactionName)).setText(
//                String.valueOf(txn.getType().getValue()));
//        String transactionName = String.valueOf(txn.getType().getValue());
        String transactionName = String.valueOf(txn.getType().name());
        if (transactionName.length() == 1) {
            transactionName = transactionName.toUpperCase();
        } else if (transactionName.length() > 1) {
            transactionName = transactionName.substring(0, 1).toUpperCase() + transactionName.substring(1).toLowerCase();
        } else {
            transactionName = "";
            Log.e(TAG, "empty transactionName");
        }
        ((TextView) convertView.findViewById(R.id.tvTransactionName)).setText(
                transactionName);
        if (txn.getTimestamp() != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy h:mm a");
            String timeStamp = dateFormat.format(txn.getTimestamp());
            ((TextView) convertView.findViewById(R.id.tvTransactionTimestamp)).setText(
                    timeStamp);
        } else {
            Log.e(TAG, "empty getTimestamp");
        }

        return convertView;
    }
}
