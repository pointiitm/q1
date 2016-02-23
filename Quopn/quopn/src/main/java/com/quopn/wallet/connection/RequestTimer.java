package com.quopn.wallet.connection;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class RequestTimer {
	public interface RequestTimerListener {
		public void onTimeout(RequestTimer requestTimer);
	}
	
	private static final String TAG = "Quopn/RequestTimer";
	public static final long DEFAULT_TIMEOUT = 60000; //1 minute for timeout
	
	/* Public attributes */
	private long timeoutMillis;
	private RequestTimerListener listener;
	private Handler handlerMain;
	
	/* Local attributes */
	private HandlerThread threadTimer;
	private Handler handlerTimer;
	private Runnable runnableTimeout;
	
	public RequestTimer(long timeoutMillis, Handler handlerMain) {
		this.setTimeoutMillis(timeoutMillis);
		threadTimer = new HandlerThread("timer");
		threadTimer.start();
		handlerTimer = new Handler(threadTimer.getLooper());
	}
	
	public RequestTimer() {
		this(DEFAULT_TIMEOUT, null);
	}

	public long getTimeoutMillis() {
		return timeoutMillis;
	}

	public RequestTimerListener getListener() {
		return listener;
	}

	public void setListener(RequestTimerListener listener) {
		this.listener = listener;
	}

	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public Handler getCallbackHandler() {
		return handlerMain;
	}

	public void setCallbackHandlerMain(Handler handlerMain) {
		this.handlerMain = handlerMain;
	}
	
	public void start() {
		if (runnableTimeout == null) {
			Log.d(TAG, "Timer " + RequestTimer.this + " fired");
			
			runnableTimeout = new Runnable() {
				@Override
				public void run() {
					if (handlerMain == null) {
						Log.d(TAG, "Timer " + RequestTimer.this + " timed out");
						getListener().onTimeout(RequestTimer.this);
					} else {
						Runnable runnablePostback = new Runnable() {
							@Override
							public void run() {
								Log.d(TAG
									, "Timer "
									+ RequestTimer.this + " timed out");
								getListener().onTimeout(RequestTimer.this);
							}
						};
						
						handlerMain.post(runnablePostback);
					}
					clean();
				}
			};
			
			handlerTimer.postDelayed(runnableTimeout, getTimeoutMillis());
		}
	}
	
	public void cancel() {
		Log.d(TAG, "Timer " + RequestTimer.this + " cancelled");
		handlerTimer.removeCallbacks(runnableTimeout);
		clean();
	}
	
	private void clean() {
		if (runnableTimeout != null) {
			handlerTimer.removeCallbacks(runnableTimeout);
			runnableTimeout = null;
		}
		threadTimer.quit();
	}
}
