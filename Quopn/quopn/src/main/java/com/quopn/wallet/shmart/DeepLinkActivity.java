package com.quopn.wallet.shmart;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.quopn.wallet.R;

public class DeepLinkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE | Window.FEATURE_OPTIONS_PANEL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        String scheme = getIntent().getData().getScheme();
        String host = getIntent().getData().getHost();
        String path = getIntent().getData().getPath();

        ShmartFlow.getInstance().onDeepLinkActivityCreated(
                this, scheme, host, path);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShmartFlow.getInstance().onDeepLinkActivityRestored(this);
    }
}
