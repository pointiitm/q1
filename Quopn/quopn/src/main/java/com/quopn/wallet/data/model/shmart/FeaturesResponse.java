package com.quopn.wallet.data.model.shmart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hari on 30/9/15.
 */
public class FeaturesResponse extends ShmartResponse {
    public enum ID {
        LOAD("1")
        , TO_MOBILE("2")
        , TO_BANK("3")
        , VOUCHER("4")
        , PREF("5")
        , TXNS("6")
        , SETTINGS("7")
        , TNC("8")
        , FAQ("9");

        private String id;
        ID(String id) { this.id = id; }
        public String toString() { return id; }

        public static ID fromValue(String idString) {
            for (ID id:ID.values()) {
                if (id.toString().equals(idString)) { return id; }
            }

            return null;
        }
    }

    public enum Status {
        DISABLED(0)
        , ENABLED(1)
        , DEFERRED(2);

        private int status;
        Status(int status) { this.status = status; }
        public int getStatus() { return status; }

        public static Status fromValue(int value) {
            for (Status status:Status.values()) {
                if (status.getStatus() == value) { return status; }
            }

            return null;
        }
    }

    public static class Feature {
        private ID id;
        private String feature;
        private Status status;
        private String statusText;

        public ID getId() {
            return id;
        }

        public String getFeature() {
            return feature;
        }

        public Status getStatus() {
            return status;
        }

        public String getStatusText() {
            return statusText;
        }
    }

    private List<Feature> features = new ArrayList<Feature>();

    public FeaturesResponse(String responseString) throws JSONException {
        super(responseString);
        if (has("FeatureList")) {
            JSONArray array = getJSONArray("FeatureList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonFeature = array.getJSONObject(i);

                Feature feature = new Feature();
                feature.id = ID.fromValue(jsonFeature.getString("id"));
                feature.feature = jsonFeature.getString("feature");
                feature.status = Status.fromValue(jsonFeature.getInt("status"));
                feature.statusText = jsonFeature.getString("status_text");
                features.add(feature);
            }
        }
    }

    public List<Feature> getFeatures() { return features; }
}
