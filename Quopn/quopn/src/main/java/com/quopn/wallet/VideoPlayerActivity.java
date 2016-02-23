package com.quopn.wallet;
/**
 * @author Sandeep
 *
 *@Modified by Sumeet on 07-10-2014
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.data.model.Video;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.QuopnTextView;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerActivity extends Activity {
	public interface VideoWatchListener {
		public void onVideoCompleted();
	}

	private static final String TAG = "Quopn/VideoPlayer";
	
	/** Reference to the instance for use with the dispatchKeyEvent override method. */
	private Activity mActivity = this;
	
	/** The current activities configuration used to test screen orientation. */
	private Configuration mConfiguration;
	
	/** The activities intent. */
	private Intent mIntent;
	
	/** Thread that runs in the background and checks for UI changes. */
	private Thread mUpdateThread;
	
	
	/** The video view where the video is rendered. */
	private VideoView mVideoView;
	
	/** A view that displays the video's length. */
	private TextView mVideoDuration;
	
	/** The videoview's media controller. */
	private MediaController mController;
	
	/** A video object containing information about the video. */
	private Video mVideo;
	
	private Button mSkipButton;
	CustomProgressDialog pDialog;
	private TextView mTimerTextview; 
	private final long minTime = 9 * 1000;  //instead of 10 seconds set to 9 seconds.
	private final long interval = 1 * 1000;
	private CountDownTimer countDownTimer;
	private boolean timerHasStarted = false;
	private boolean tenseconds_timer=false;
	private int countdown;
	private Timer mTimer;
	private long REFRESH_TIME = 1000;
	private int durationtime ;
	int videoPosition = 0;
	private AnalysisManager mAnalysisManager;
	private String videowatchtimestamp;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mAnalysisManager!=null){
			mAnalysisManager.close();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		getWindow().setFormat(PixelFormat.TRANSLUCENT); //sandeep
		mConfiguration = getResources().getConfiguration();
		mIntent = getIntent();
		mAnalysisManager=((QuopnApplication)getApplicationContext()).getAnalysisManager();
		// Loads in extras passed with the activity intent
		mVideo = (Video) mIntent.getSerializableExtra(Video.class.getName());
		
		// Sets the content view based on the layout
		setContentView(R.layout.activity_simple_videoplayer);
		
		
		// Load in references
		mVideoView = (VideoView) findViewById(R.id.fragmentvideoplayer_videoview);
		mSkipButton=(Button)findViewById(R.id.skip_btn); //button for skip videp
		mTimerTextview=(QuopnTextView)findViewById(R.id.timer); //timer text
		mTimerTextview.setTypeface(null, Typeface.BOLD);
		
		// Create a progressbar
		pDialog = new CustomProgressDialog(VideoPlayerActivity.this);
		// Set progressbar title
//		pDialog.setTitle(getResources().getString(R.string.video_streaming_txt));
		// Set progressbar message
		pDialog.setCancelable(true);
		// Show progressbar
		pDialog.show();
		
		//add the stats event for VIDEO_PLAYER_STARTED i.e Video is started for buffering.
		mAnalysisManager.send(AnalysisEvents.VIDEO_PLAYER_STARTED, QuopnConstants.CAMPAIGNID);
		
		// Create a custom media controller that ignores the back button
		mController = new MediaController(this) {
			@Override
			public boolean dispatchKeyEvent(KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
					((Activity) mActivity).finish();
				
				return super.dispatchKeyEvent(event);
			}
		};
		mController.setVisibility(View.GONE); //sandeep
		mVideoView.setMediaController(mController); 
		// Attach the media controller
		
		if(mVideo.getUrl()!=null){
			mVideoView.setVideoURI(Uri.parse(mVideo.getUrl())); //this is actual code
		}
		else{
			Dialog dialog=new Dialog(this, R.string.dialog_title_no_video,R.string.video_is_not_available); 
			dialog.show();
		}
		
		
		mTimer = new Timer();
        if (savedInstanceState != null)
        	videoPosition = savedInstanceState.getInt("videoPosition");
		
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                mVideoView.requestFocus();
                mVideoView.seekTo(videoPosition); 
                mVideoView.start();
                
                //add the stats event for VIDEO_PLAYING i.e Video is actually start after buffering completed.
                mAnalysisManager.send(AnalysisEvents.VIDEO_PLAYING, QuopnConstants.CAMPAIGNID);
                
                mTimerTextview.setVisibility(View.VISIBLE);
                durationtime = mp.getDuration();
                
                mTimer.scheduleAtFixedRate(new TimerTask() {
    			    @Override
    			    public void run() {
    			    	 mHandler.obtainMessage(1).sendToTarget();
    			         }
    			}, 0, REFRESH_TIME );
                
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //sandeep

            @Override
            public void onCompletion(MediaPlayer mp) {
            	
            	if(tenseconds_timer==true){
            		mTimerTextview.setText("End");
            		mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
            		tenseconds_timer=false;
            		signalVideoWatched();
            		finish();
            	}else{
            		if (mVideoView.isPlaying()){
						 mVideoView.stopPlayback();
					 }
            		mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
					finish();
            	}
            	
            }
        });
        
        
		updateLayout();
		startUpdateThread();
		
		mSkipButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(tenseconds_timer==true){
					mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
	            	tenseconds_timer=false;
	            	signalVideoWatched();
	            	finish();
				}else{
					 if (mVideoView.isPlaying()){
						 mVideoView.stopPlayback();
					 }
					 mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
					finish();
				}
				
			}
		});
		
	}
	
	public Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	
	    	long millisUntilFinished = durationtime - mVideoView.getCurrentPosition();
			int seconds = (int) (millisUntilFinished / 1000) % 60 ;
			int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
			
			long millisUntilFinished_currenttime = mVideoView.getCurrentPosition();
			int seconds_current = (int) (millisUntilFinished_currenttime / 1000) % 60 ;
			int minutes_current = (int) ((millisUntilFinished_currenttime / (1000*60)) % 60);
			String min="";
			String second="";
			String min_current="";
			String second_current="";
			videowatchtimestamp="";
			if(mVideoView.getCurrentPosition() >= minTime){
				tenseconds_timer=true;
			}else{
				tenseconds_timer=false;
			}
			
			if(seconds>9){
				if(seconds!=00){
					min="0"+minutes;
					second=""+seconds;
				}
				mTimerTextview.setText(min + ":"+second);
				
			}else{
				min="0"+minutes;
				second="0"+seconds;
				mTimerTextview.setText(min + ":" +second);
			}
			
			if(seconds_current>9){
				if(seconds_current!=00){
					min_current="0"+minutes_current;
					second_current=""+seconds_current;
				}
				videowatchtimestamp=min_current + ":"+second_current;
				
			}else{
				min_current="0"+minutes_current;
				second_current="0"+seconds_current;
				videowatchtimestamp=min_current + ":"+second_current;
			}
			
	    }
	};
	
	
	@Override
	protected void onResume() {	
		startUpdateThread();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if(mUpdateThread != null)
			mUpdateThread.interrupt();
		
		videoPosition= mVideoView.getCurrentPosition();
		mTimerTextview.setVisibility(View.INVISIBLE);
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		 mVideoView.requestFocus();
         mVideoView.seekTo(videoPosition); 
		 mVideoView.resume();
		 pDialog.show();
		super.onStart();
	}

	@Override
	public void finish() {
		mTimer.cancel();
		if(mUpdateThread != null)
			mUpdateThread.interrupt();
		
		super.finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    
	    if (mVideoView.isPlaying()){
	    	mVideoView.pause();
	    	outState.putInt("videoPosition", mVideoView.getCurrentPosition());
	    }
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		updateLayout();
	}

	@SuppressLint("InlinedApi")
	private void updateLayout() {
			// Hide the status bar
			WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);

			// Hide the software buttons
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				View main_layout = findViewById(android.R.id.content).getRootView();
				main_layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}

			// Hide the media controller
			mController.hide();

	}
	
	@SuppressLint("InlinedApi")
	private void startUpdateThread() {
		if(mUpdateThread == null || mUpdateThread.isInterrupted()) {
			mUpdateThread = new Thread(new Runnable() {
				private View main_layout = findViewById(android.R.id.content).getRootView();
				private Handler mHandler = new Handler();
				private boolean canShow = true;
				
				@Override
				public void run() {
					mHandler.postDelayed(this, 100);
					
					if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						int currentUi = main_layout.getSystemUiVisibility();
						
						if(currentUi == 0 && mController != null && mConfiguration != null && mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
							try {
								if(!mController.isShowing() && canShow) {		
									WindowManager.LayoutParams attrs = getWindow().getAttributes();
							        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
							        getWindow().setAttributes(attrs);
									
									mController.show();
									canShow = false;
								}
							} catch(WindowManager.BadTokenException ex) {
								// WindowManager$BadTokenException will be caught and the app would not display 
								// the 'Force Close' message
							} finally {
								if(!mController.isShowing()) {
									WindowManager.LayoutParams attrs = getWindow().getAttributes();
							        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							        getWindow().setAttributes(attrs);
									
									main_layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
									canShow = true;
								}
							}
						} else if(currentUi == 0 && mController != null && mConfiguration != null && mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
							try {
								if(!mController.isShowing() && canShow) {		
									WindowManager.LayoutParams attrs = getWindow().getAttributes();
							        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
							        getWindow().setAttributes(attrs);
									
									mController.show();
									canShow = false;
								}
							} catch(WindowManager.BadTokenException ex) {
								// WindowManager$BadTokenException will be caught and the app would not display 
								// the 'Force Close' message
							} finally {
								if(!mController.isShowing()) {
									WindowManager.LayoutParams attrs = getWindow().getAttributes();
							        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							        getWindow().setAttributes(attrs);
									
									main_layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
									canShow = true;
								}
							}
						}
					}
				}
			});
	        
	        mUpdateThread.start();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mTimer.cancel();
		if(tenseconds_timer==true){
			mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
        	tenseconds_timer=false;
        	signalVideoWatched();
        	finish();
		}else{
			 if (mVideoView.isPlaying()){
				 mVideoView.stopPlayback();
			 }
			 mAnalysisManager.send(AnalysisEvents.VIDEO_WATCHED_TIME,videowatchtimestamp+"|"+QuopnConstants.CAMPAIGNID);
			finish();
		}
	}
	
	private void signalVideoWatched() {
		Intent intent = new Intent();
		intent.setAction(QuopnConstants.INTENT_ACTION_VIDEO_WATCHED);
		sendBroadcast(intent);
		Log.d(TAG, "Sending intent with action " + intent.getAction());
	}
}
