package com.quopn.wallet.adapter;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quopn.wallet.R;
import com.quopn.wallet.data.model.shmart.FeaturesResponse;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hari on 21/9/15.
 */
public class ShmartOperationsAdapter extends BaseAdapter {
    private static final String TAG = "Quopn/Shmart";

    class ShmartOp {
        private int id;
        private String caption;
        private int drawable;
        private FeaturesResponse.Status status;
        private String statusText;
    }

    private Context context;
    private ArrayList<ShmartOp> ops = new ArrayList<ShmartOp>();
    private Map<Integer, ShmartOp> map = new TreeMap<Integer, ShmartOp>();

    public ShmartOperationsAdapter(Context context) {
        this.context = context;

        XmlResourceParser parser = context.getResources().getXml(R.xml.shmart);
        int eventType;
        try {
            while ((eventType = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG && parser.getName().equals("op")) {
                    ShmartOp op = new ShmartOp();
                    op.id = parser.getAttributeResourceValue(null, "id", 0);
                    Log.d(TAG, "ID: " + op.id);
                    int captionID = parser.getAttributeResourceValue(null, "caption", 0);
                    Log.d(TAG, "caption ID: " + captionID);
                    op.caption = context.getString(captionID);
                    Log.d(TAG, "caption: " + op.caption);
                    op.drawable = parser.getAttributeResourceValue(null, "icon", 0);
                    Log.d(TAG, "drawable icon: " + op.drawable);

                    ops.add(op);
                    map.put(op.id, op);
                }
            }
        }
        catch (XmlPullParserException e) {}
        catch (IOException e) {}
    }

    public FeaturesResponse.Status getStatusForFeature (int i) {
        return ((ShmartOp) getItem(i)).status;
    }

    @Override
    public int getCount() {
        return ops.size();
    }

    @Override
    public Object getItem(int i) {
        return ops.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((ShmartOp) getItem(i)).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ShmartOp op = (ShmartOp) getItem(i);

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.cell_shmart_op, viewGroup, false);
        }

        ((ImageView) view.findViewById(R.id.ivShmartOpIcon)).setImageResource(op.drawable);
        ((TextView) view.findViewById(R.id.tvShmartOpCaption)).setText(op.caption);

        if (op.status != null && op.status.equals(FeaturesResponse.Status.DEFERRED)) {
            Log.d(TAG, "Showing overlay for " + op.caption);
            ((RelativeLayout) view.findViewById(R.id.rlShmartOpOverlay)).setVisibility(View.VISIBLE);
            if (op.statusText != null && !op.statusText.isEmpty()) {
                ((TextView) view.findViewById(R.id.tvShmartOpDeferred)).setText(op.statusText);
            }
        } else {
            Log.d(TAG, "Hiding overlay for " + op.caption);
            ((RelativeLayout) view.findViewById(R.id.rlShmartOpOverlay)).setVisibility(View.GONE);
        }

        return view;
    }

    public void setFeatureListResponseInOps(List<FeaturesResponse.Feature> features) {
        if (features != null && !features.isEmpty()) {
            for (FeaturesResponse.Feature feature : features) {
                ShmartOp op = null;
                FeaturesResponse.ID featureID = feature.getId();

                if (featureID.equals(FeaturesResponse.ID.LOAD)) {
                    op = map.get(R.id.shmart_op_loadwallet);
                } else if (featureID.equals(FeaturesResponse.ID.FAQ)) {
                    op = map.get(R.id.shmart_op_faqs);
                } else if (featureID.equals(FeaturesResponse.ID.PREF)) {
                    op = map.get(R.id.shmart_op_shopatquopn);
                } else if (featureID.equals(FeaturesResponse.ID.SETTINGS)) {
                    op = map.get(R.id.shmart_op_settings);
                } else if (featureID.equals(FeaturesResponse.ID.TNC)) {
                    op = map.get(R.id.shmart_op_terms);
                } else if (featureID.equals(FeaturesResponse.ID.TO_BANK)) {
                    op = map.get(R.id.shmart_op_transfertobank);
                } else if (featureID.equals(FeaturesResponse.ID.TO_MOBILE)) {
                    op = map.get(R.id.shmart_op_sendmoney);
                } else if (featureID.equals(FeaturesResponse.ID.TXNS)) {
                    op = map.get(R.id.shmart_op_mytransactions);
                } else if (featureID.equals(FeaturesResponse.ID.VOUCHER)) {
                    op = map.get(R.id.shmart_op_purchasevouchers);
                }

                FeaturesResponse.Status status = feature.getStatus();
                if (status.equals(FeaturesResponse.Status.DISABLED)) {
                    map.remove(op.id);
                    ops.remove(op);
                } else {
                    op.status = feature.getStatus();
                    op.statusText = feature.getStatusText();
                    if (op.statusText != null && op.statusText.isEmpty()) {
                        op.statusText = op.statusText.toUpperCase();
                    }
                    op.caption = feature.getFeature();
                }
            }
        } else {
//            TODO: inform server
        }
    }

    public void updateOps(List<FeaturesResponse.Feature> features) {
        if (features != null && !features.isEmpty()) {
            for (FeaturesResponse.Feature feature : features) {
                ShmartOp op = null;
                FeaturesResponse.ID featureID = feature.getId();

                if (featureID.equals(FeaturesResponse.ID.LOAD)) {
                    op = map.get(R.id.shmart_op_loadwallet);
                } else if (featureID.equals(FeaturesResponse.ID.FAQ)) {
                    op = map.get(R.id.shmart_op_faqs);
                } else if (featureID.equals(FeaturesResponse.ID.PREF)) {
                    op = map.get(R.id.shmart_op_shopatquopn);
                } else if (featureID.equals(FeaturesResponse.ID.SETTINGS)) {
                    op = map.get(R.id.shmart_op_settings);
                } else if (featureID.equals(FeaturesResponse.ID.TNC)) {
                    op = map.get(R.id.shmart_op_terms);
                } else if (featureID.equals(FeaturesResponse.ID.TO_BANK)) {
                    op = map.get(R.id.shmart_op_transfertobank);
                } else if (featureID.equals(FeaturesResponse.ID.TO_MOBILE)) {
                    op = map.get(R.id.shmart_op_sendmoney);
                } else if (featureID.equals(FeaturesResponse.ID.TXNS)) {
                    op = map.get(R.id.shmart_op_mytransactions);
                } else if (featureID.equals(FeaturesResponse.ID.VOUCHER)) {
                    op = map.get(R.id.shmart_op_purchasevouchers);
                }

                FeaturesResponse.Status status = feature.getStatus();
                if (status.equals(FeaturesResponse.Status.DISABLED)) {
                    map.remove(op.id);
                    ops.remove(op);
                } else {
                    op.status = feature.getStatus();
                    op.statusText = feature.getStatusText();
                    if (op.statusText != null && op.statusText.isEmpty()) {
                        op.statusText = op.statusText.toUpperCase();
                    }
                    op.caption = feature.getFeature();

                }
            }

            notifyDataSetChanged();
        } else {
//            TODO: inform server
        }
    }
}
