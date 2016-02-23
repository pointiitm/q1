package com.quopn.wallet.shmart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.views.CustomProgressDialogWhite;

public class IFrameActivity extends ActionBarActivity {
    private static final String TAG = "Quopn/IFrame";
    private CustomProgressDialogWhite mCustomProgressDialog;
    class PaymentInterface {
        @JavascriptInterface
        public void onPaymentResponse(String message) {
            Log.d(TAG, message);
            cleanupWebView();
            ShmartFlow.getInstance().onLoadWalletResponse(message);
        }
    }

    private WebView webView;
    private AnalysisManager mAnalysisManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iframe);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_ADDING_MONEY_SHMART);
        mCustomProgressDialog = new CustomProgressDialogWhite(IFrameActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg));

        View actionBarView = View.inflate(this, R.layout.actionbar_layout, null);
        getSupportActionBar().setCustomView(actionBarView);

        ImageView slider = (ImageView) actionBarView.findViewById(R.id.slider);
        slider.setImageResource(R.drawable.back);
        slider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        ImageView home_btn = (ImageView) actionBarView
                .findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();

            }
        });

        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);

        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);


        webView = (WebView) findViewById(R.id.wvIFrame);
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new PaymentInterface(), "Quopn");

        ShmartFlow.getInstance().onIFrameCreated(this);
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void loadHtml(String loadWalletHtml) {
        webView.loadData(loadWalletHtml, "text/html", "UTF-8");
    }

    private void cleanupWebView() {
        finish();
    }

    private class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mCustomProgressDialog.show();
            System.out.println("IFrameActivity onPageStarted====Url=="+url);
            //progressBar.setVisibility(View.VISIBLE);
            //loadText.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            mCustomProgressDialog.dismiss();
            System.out.println("IFrameActivity shouldOverrideUrlLoading====Url==" + url);
            //progressBar.setVisibility(View.GONE);
            //loadText.setVisibility(View.GONE);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mCustomProgressDialog.dismiss();
            System.out.println("IFrameActivity onPageFinished====Url==" + url);
            //progressBar.setVisibility(View.GONE);
            //loadText.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {
//        final Dialog dialog = new Dialog(this, R.string.dialog_title
//                , "Are you sure that you want to cancel this transaction?");
//
//        Dialog.OnCancelListener cancelListener = new Dialog.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                dialogInterface.dismiss();
//            }
//        };
//
//        View.OnClickListener acceptClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
                IFrameActivity.super.onBackPressed();
//            }
//        };

//        View.OnClickListener cancelClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        };
//
//        dialog.setOnCancelListener(cancelListener);
//        dialog.setOnAcceptButtonClickListener(acceptClickListener);
//
//        dialog.addOkButton("Yes");
//        dialog.addCancelButton("No");
//
//        dialog.show();
    }
}
