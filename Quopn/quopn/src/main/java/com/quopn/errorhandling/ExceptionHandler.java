/**
 * @author Ravishankar
 *
 *@date 04/09/2014
 */
package com.quopn.errorhandling;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.interfaces.Connector;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	private static final String TAG = "Quopn/ExceptionHandler";
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";
	private Connector requestErrorReport;

	public ExceptionHandler(Activity context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, final Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

		String stackTraceDump = getThrowablePresentation(exception);
		Log.d(TAG, stackTraceDump);
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Intent intent = new Intent(myContext, ShowExceptionActivity.class);
		intent.putExtra("device_id", QuopnConstants.android_id);
		intent.putExtra("stack_trace", stackTraceDump);
		intent.putExtra("brand", Build.BRAND);
		intent.putExtra("device", Build.DEVICE);
		intent.putExtra("model", Build.MODEL);
		intent.putExtra("build", Build.ID);
		intent.putExtra("product", Build.PRODUCT);
		intent.putExtra("sdk", Build.VERSION.SDK);
		intent.putExtra("release", Build.VERSION.RELEASE);
		intent.putExtra("increment", Build.VERSION.INCREMENTAL);
		intent.putExtra("version_code", "" + QuopnConstants.versionCode);
		intent.putExtra("version_name", QuopnConstants.versionName);
		intent.putExtra("user_id"
			, PreferenceUtil.getInstance(myContext).getPreference(
                PreferenceUtil.SHARED_PREF_KEYS.USER_ID));
		myContext.startActivity(intent);
		
		AnalysisManager manager = ((QuopnApplication)myContext.getApplicationContext()).getAnalysisManager();
		manager.send(AnalysisEvents.CRASH);
		
		try { Thread.sleep(500); }
		catch (InterruptedException e) {}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
	private String getThrowablePresentation(Throwable error) {
		String presentation = error.toString() + "\n";
		
		for (StackTraceElement ste:error.getStackTrace()) {
			presentation += ste.toString() + "\n";
		}
		Throwable cause = error.getCause();
		if (cause != null) {
			presentation += "due to\n";
			presentation += getThrowablePresentation(cause);
		}
		
		return presentation;
	}

}