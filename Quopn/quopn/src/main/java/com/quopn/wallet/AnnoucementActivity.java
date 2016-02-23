package com.quopn.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.utils.QuopnConstants;

import static android.view.View.OnClickListener;


public class AnnoucementActivity extends Activity {
    private static final String TAG = "Quopn/Announcement";

    private ImageView progressBar;
    private String url;
    private String image;
    private ImageButton btnClose;
    private ImageView image_announce;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private Context context = this;
    private Uri uri;
    //Bitmap bitmap;
    //ProgressDialog pDialog;
    private AnalysisManager mAnalysisManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.annoucement_activity);
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        btnClose = (ImageButton)findViewById(R.id.btn_close);
        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnalysisManager.send(AnalysisEvents.ANNOUNCEMENT_CLOSED);
                finish();

            }
        });

        progressBar = (ImageView) findViewById(R.id.progressBar_announcement);

        AnimationDrawable animation = (AnimationDrawable) progressBar.getDrawable();
        animation.start();
        progressBar.setVisibility(View.VISIBLE);


        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
             url = extras.getString("url");
            image = extras.getString("image");
            image_announce = (ImageView)findViewById(R.id.image_announce);
            mAnalysisManager.send(AnalysisEvents.ANNOUNCEMENT_APPEARED);
            mImageLoader.displayImage(image,image_announce, mDisplayImageOptions, null);

        }

        //new LoadImage().execute(image);

        image_announce.setOnClickListener(new OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  //Log.d(TAG, "Announcement was clicked");
                                                  //Log.d(TAG, "URL is " + url);
                                                  if (!url.isEmpty()) {
                                                      if (url.startsWith("http") || url.startsWith("quopn")) {
                                                          try {
                                                              mAnalysisManager.send(AnalysisEvents.ANNOUNCEMENT_CLICKED, url);
                                                              /*Intent intent = new Intent(Intent.ACTION_VIEW);
                                                              Uri uri = Uri.parse(url);
                                                              intent.setData(uri);
                                                              startActivity(intent);*/

                                                              LocalBroadcastManager localBrdcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
                                                              Intent intent = new Intent(QuopnConstants.BROADCAST_PARSE_ANNOUNCEMENT_DEEP_LINKS);
                                                              intent.putExtra(QuopnConstants.GCM_DEEP_LINK.KEY_01, Uri.parse(url));
                                                              localBrdcastMgr.sendBroadcast(intent);
                                                              finish(); // ankur
                                                          } catch (Exception ex) {
                                                              Log.e(TAG, ex.getLocalizedMessage());
                                                          }
                                                      }
                                                  }
                                              }
                                          });



                    mImageLoader.loadImage(image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnClose.setVisibility(View.VISIBLE);
                        }
                    });
                }

//    private class LoadImage extends AsyncTask<String, String, Bitmap> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(AnnoucementActivity.this);
//           // pDialog.setMessage("Loading Image ....");
//            pDialog.show();
//
//        }
//        protected Bitmap doInBackground(String... args) {
//            try {
//                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        protected void onPostExecute(Bitmap image) {
//
//            if(image != null){
//                image_announce.setImageBitmap(image);
//                pDialog.dismiss();
//
//            }else{
//
//                pDialog.dismiss();
//                Toast.makeText(AnnoucementActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    }

}



