package com.quopn.wallet.utils;
/**
 * @author Sumeet
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.RegistrationScreen;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CityData;
import com.quopn.wallet.data.model.StateData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.views.QuopnEditTextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author ravi
 *
 */
@SuppressLint("ResourceAsColor")
public class QuopnUtils {

    public static ConnectionListener webissue_connectionlistener;

    public static Response parseResponse(String string, Class<?> c) {
        Gson gson = new Gson();
        Response response = (Response) gson.fromJson(string, c);
        return response;
    }

	/*
     * Draw image in circular shape Note: change the pixel size if you want
	 * image small or large
	 */
	/*public static Bitmap getCircleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}*/


    public static class ViewHolder {

        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public static void sendSMS(String cta_text, String cta_value,
                               Context context) {


        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");

        smsIntent.putExtra("address", new String(cta_value));
        smsIntent.putExtra("sms_body", cta_text);

        try {

            if (smsIntent.resolveActivity(context.getPackageManager()) == null) {
                Toast.makeText(context,
                        "There are no applications to handle your request",
                        Toast.LENGTH_LONG).show();
            } else {
                context.startActivity(smsIntent);
            }
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    public static void sendCall(String cta_text, String cta_value,
                                Context context) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + cta_value));

        if (callIntent.resolveActivity(context.getPackageManager()) == null) {
            Toast.makeText(context,
                    "There are no applications to handle your request",
                    Toast.LENGTH_LONG).show();
        } else {
            context.startActivity(callIntent);
        }
    }


    public static void ShareIntent(Context context, String product_name, String campaign_id) {

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey, Check out this fantastic Quopn for " + product_name + "! " + QuopnApi.QUOPN_DEEP_LINK_URL + campaign_id);

        if (sendIntent.resolveActivity(context.getPackageManager()) == null) {
            Toast.makeText(context,
                    "There are no applications to handle your request",
                    Toast.LENGTH_LONG).show();
        } else {
            context.startActivity(Intent.createChooser(sendIntent, "Send Via"));
        }

    }

    public static void closefragment(Context context) {
        ((Activity) context).finish();
    }

    @SuppressWarnings("unused")
    public static void CustomAlertDialog_ucn(final String cta_text,
                                             final String cta_value, final String ucn_id, final Context context,
                                             final ConnectionListener connectionListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LinearLayout linearlayout;
        final TextView passphrasetxt;
        final EditText ucn_number_edittext;
        TextView title = new TextView(context);
        title.setText(context.getResources().getString(R.string.ucn_txt));
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 20, 10, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        builder.setCustomTitle(title);

        linearlayout = new LinearLayout(context);
        linearlayout.setPadding(10, 20, 10, 20);
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setBackgroundResource(R.color.transparent);
        linearlayout.setGravity(Gravity.CENTER);

        // Use an EditText view to get ucn number.
        ucn_number_edittext = new EditText(context);
        ucn_number_edittext.setText(ucn_id);
        ucn_number_edittext.requestFocus();
        ucn_number_edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        ucn_number_edittext
                .setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        linearlayout.addView(ucn_number_edittext, 0);
        builder.setView(linearlayout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getApplicationContext(),
                // "cta_text is: "+cta_text+"  cta_value is: "+cta_value,Toast.LENGTH_SHORT).show();
                getUCNNumberResponse(ucn_number_edittext.getText().toString(),
                        cta_text, context, connectionListener);
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private static void getUCNNumberResponse(String ucnnumber, String Url,
                                             Context context, ConnectionListener connectionListener) {
        if (QuopnUtils.isInternetAvailable(context)) {
            String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
            String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
            if (!TextUtils.isEmpty(api_key)) {

                Map<String, String> params = new HashMap<String, String>();
                params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
                Map<String, String> params1 = new HashMap<String, String>();
                params1.put(QuopnApi.ParamKey.UCNID, ucnnumber);
                params1.put(QuopnApi.ParamKey.WALLETID, walledId);
                ConnectionFactory connectionFactory = new ConnectionFactory(
                        context, connectionListener);
                connectionFactory.setHeaderParams(params);
                connectionFactory.setPostParams(params1);
                connectionFactory.createConnection(QuopnConstants.UCN_NUMBER_CODE);
            } else {
                // show error
            }
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public static void CustomAlertDialog_sms(final String cta_text,
                                             final String cta_value, final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(context);
        String mobileno = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);
        title.setText(context.getResources().getString(R.string.call_txt)
                + "\n\nCURRENTLY REGISTERED MOBILE NUMBER IS: " + mobileno);

        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 20, 10, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        builder.setCustomTitle(title);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if ((cta_value == null || cta_value.length() < 0)
                        && (cta_text == null || cta_text.length() < 0)) {
                    sendSMS("This is test body", "1234567890", context);
                } else if (cta_value == null || cta_value.length() < 0) {
                    sendSMS(cta_text, "1234567890", context);
                } else if (cta_text == null || cta_text.length() < 0) {
                    sendSMS("This is test body", cta_value, context);
                } else {
                    sendSMS(cta_text, cta_value, context);
                }
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static void CustomAlertDialog_call(final String cta_text,
                                              final String cta_value, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(context);
        String mobileno = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);
        title.setText(context.getResources().getString(R.string.call_txt)
                + "\n\nCURRENTLY REGISTERED MOBILE NUMBER IS: " + mobileno);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 20, 10, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        builder.setCustomTitle(title);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (cta_value == null || cta_value.length() < 0) {
                    sendCall(cta_text, "1234567890", context);
                } else {
                    sendCall(cta_text, cta_value, context);
                }
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    @SuppressLint("NewApi")
    @SuppressWarnings({"unused", "deprecation"})
    public static void CustomAlertDialog_storeId(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LinearLayout linearlayout;
        final EditText store_id_edittext;
        final ImageView submit_btn;
        LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams lparams1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearlayout = new LinearLayout(context);
        linearlayout.setLayoutParams(lparams);
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setGravity(Gravity.CENTER);


        // Use an EditText view to get ucn number.
        store_id_edittext = new EditText(context);
        store_id_edittext.requestFocus();
        store_id_edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        store_id_edittext.setBackgroundResource(R.drawable.enterstoreid);
        store_id_edittext.setLayoutParams(lparams1);
        store_id_edittext.setTextColor(context.getResources().getColor(R.color.white));
        store_id_edittext.setHint(R.string.storeid_txt);
        store_id_edittext.setHintTextColor(context.getResources().getColor(R.color.white));
        store_id_edittext.setPadding(120, 10, 0, 0);
        linearlayout.addView(store_id_edittext, 0);

        submit_btn = new ImageView(context);
        submit_btn.setImageResource(R.drawable.submit_btn);
        submit_btn.setPadding(10, 20, 10, 20);
        submit_btn.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        linearlayout.addView(submit_btn, 1);
        builder.setView(linearlayout);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context, R.string.dialog_title_store_id, R.string.store_id_is_pressed);
                dialog.show();
            }
        });

        builder.show();
    }

    /**
     * @param context : Context of activity.
     * @Description : This method is use for checking internet connectivity of android device.
     */
    public static boolean isInternetAvailable(Context context) {
        if (context == null) {
            context = QuopnApplication.getInstance().getApplicationContext();
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected())) {
            //connection is avlilable
            return true;
        } else {
            //no connection
            return false;
//                 
        }

    }

    public static void getCampaignValidationResponse(String campaignid,
                                                     Context context, ConnectionListener connectionListener) {
        if (QuopnUtils.isInternetAvailable(context)) {
            String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
            String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
            if (!TextUtils.isEmpty(api_key)) {

                Map<String, String> params = new HashMap<String, String>();
                params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
                Map<String, String> params1 = new HashMap<String, String>();
                params1.put(QuopnApi.ParamKey.WALLETID, walledId);
                params1.put(QuopnApi.ParamKey.CAMPAIGN_ID, campaignid);
                ConnectionFactory connectionFactory = new ConnectionFactory(
                        context, connectionListener);
                connectionFactory.setHeaderParams(params);
                connectionFactory.setPostParams(params1);
                connectionFactory.createConnection(QuopnConstants.CAMPAIGN_VALIDATION_CODE);
            } else {
                // show error
            }
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public static void getWebIssueResponse(String campaignid, String source,
                                           Context context, ConnectionListener connectionListener) {
        if (QuopnUtils.isInternetAvailable(context)) {
            String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
            String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
            if (!TextUtils.isEmpty(api_key)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
                Map<String, String> params1 = new HashMap<String, String>();
                params1.put(QuopnApi.ParamKey.WALLETID, walledId);
                params1.put(QuopnApi.ParamKey.CAMPAIGN_ID, campaignid);
                params1.put(QuopnApi.ParamKey.SOURCE, /*source*/QuopnApi.ParamKey.DIRECT_WALLET); //as per lenin
                ConnectionFactory connectionFactory = new ConnectionFactory(
                        context, connectionListener);
                connectionFactory.setHeaderParams(params);
                connectionFactory.setPostParams(params1);
                connectionFactory.createConnection(QuopnConstants.WEB_ISSUE_CODE);
            } else {
                // show error
            }
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }


    public static void getVideoIssueResponse(String campaignid, String source,
                                             Context context, ConnectionListener connectionListener) {
        if (QuopnUtils.isInternetAvailable(context)) {
            String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
            String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
            if (!TextUtils.isEmpty(api_key)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
                Map<String, String> params1 = new HashMap<String, String>();
                params1.put(QuopnApi.ParamKey.WALLETID, walledId);
                params1.put(QuopnApi.ParamKey.CAMPAIGN_ID, campaignid);
                params1.put(QuopnApi.ParamKey.SOURCE, /*source*/QuopnApi.ParamKey.VIDEO_WALLET);
                ConnectionFactory connectionFactory = new ConnectionFactory(
                        context, connectionListener);
                connectionFactory.setHeaderParams(params);
                connectionFactory.setPostParams(params1);
                connectionFactory.createConnection(QuopnConstants.VIDEO_ISSUE_CODE);
            } else {
                // show error
            }
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
        }
    }

    public static void getMyQuopn(Context context, ConnectionListener connectionListener) {
        String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
        if (!TextUtils.isEmpty(api_key)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put(QuopnApi.ParamKey.WALLETID, walledId);
//			params1.put(QuopnApi.ParamsKey.WALLETID,"WDE943");//this is hardcoded wallet id as per uttam suggest
            ConnectionFactory connectionFactory = new ConnectionFactory(
                    context, connectionListener);
            connectionFactory.setHeaderParams(params);
            connectionFactory.setPostParams(params1);
            connectionFactory.createConnection(QuopnConstants.MYQUOPN_CODE);
        } else {
            //show error.
        }
    }


    public static void getHistory(Context context, ConnectionListener connectionListener) {
        String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
        if (!TextUtils.isEmpty(api_key)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put(QuopnApi.ParamKey.WALLETID, walledId);
//			params1.put(QuopnApi.ParamsKey.WALLETID,"WDE943");//this is hardcoded wallet id as per uttam suggest
            ConnectionFactory connectionFactory = new ConnectionFactory(
                    context, connectionListener);
            connectionFactory.setHeaderParams(params);
            connectionFactory.setPostParams(params1);
            connectionFactory.createConnection(QuopnConstants.HISTORY_CODE);
        } else {
            //show error.
        }
    }

    public static void getCart(Context context, ConnectionListener connectionListener) {
        String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
        if (!TextUtils.isEmpty(api_key)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put(QuopnApi.ParamKey.WALLETID, walledId);
//			params1.put(QuopnApi.ParamsKey.WALLETID,"WDE943");//this is hardcoded wallet id as per uttam suggest
            ConnectionFactory connectionFactory = new ConnectionFactory(
                    context, connectionListener);
            connectionFactory.setHeaderParams(params);
            connectionFactory.setPostParams(params1);
            connectionFactory.createConnection(QuopnConstants.CART_CODE);
        } else {
            //show error.
        }

    }


    public static void addAllToCart(Context context, ConnectionListener connectionListener) {
        String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        String walledId = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY);
        if (!TextUtils.isEmpty(api_key)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put(QuopnApi.ParamKey.WALLETID, walledId);
//			params1.put(QuopnApi.ParamsKey.WALLETID,"WDE943");//this is hardcoded wallet id as per uttam suggest
            ConnectionFactory connectionFactory = new ConnectionFactory(
                    context, connectionListener);
            connectionFactory.setHeaderParams(params);
            connectionFactory.setPostParams(params1);
            connectionFactory.createConnection(QuopnConstants.CART_ADD_ALL);
        } else {
            //show error.
        }
    }


    /**
     * This method is use for set the hint in edittext we pass three argument in it
     * first is edittext it is the edittext in witch we want to set hint
     * second is the message we want to set in edittest
     * third ont is for if the edittext is require fiel then its add red* in the last of hint
     */
    public static QuopnEditTextView setHintEditText(QuopnEditTextView argEditText, String argHintMessage, boolean argIsRequire) {
        try {
            if (argIsRequire) {
                argHintMessage = "   " + argHintMessage;
                String text = "<font color=#5c5c5c>" + argHintMessage + "</font> <font color=#cc0029>*</font>";
//			String text = "<font color=#8c8c8c>"+argHintMessage+"</font>";
                argEditText.setHint(Html.fromHtml(text));
            } else {
                argEditText.setHint(argHintMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return argEditText;
    }


    public static String getStateId(String argName) {
        String stateId = "";
        for (int i = 0; i < QuopnConstants.stateData.size(); i++) {
            StateData state = QuopnConstants.stateData.get(i);
            if (state.getStateName().equals(argName)) {
                stateId = state.getStateId();
                break;
            }
        }
        return stateId;
    }

    //create the new folder in sd card & write the data in text file which is created in that folder.
    public static void QuopnLogWriteFile(String title, String text, boolean textappend) {
        try {
//				File directory = new File(Environment.getExternalStorageDirectory().getPath()+"/", QuopnConstants.QUOPN_LOG_FOLDER); 
//				if (!directory.exists()) { 
//					directory.mkdir(); 
//				} 
//				//make a new text file in that created new directory/folder
//				File file = new File(directory.getPath() ,QuopnConstants.QUOPN_LOG_FILE); 
//
//				if (!file.exists() && directory.exists()){ 
//					file.createNewFile();
//				}
//				OutputStreamWriter osw;
//				osw = new FileWriter(file,textappend);
//
//				BufferedWriter out = new BufferedWriter(osw);
//				out.write("************"+getCurrentDateTime()+"************"+title+": "+text+"\n");
//				out.close();


        } catch (Exception e) {
//				System.out.println("Encryption Exception "+e);
        }
    }


    public static void writeDeviceId(boolean textappend) {
        try {
            String uuid = UUID.randomUUID().toString();
            QuopnConstants.android_id = uuid;

            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/", QuopnConstants.QUOPN_DEVICEID_FOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }
            //make a new text file in that created new directory/folder
            File file = new File(directory.getPath(), QuopnConstants.QUOPN_DEVICEID_FILE);

            if (!file.exists() && directory.exists()) {
                file.createNewFile();
            }

            OutputStreamWriter osw;
            osw = new FileWriter(file, textappend);

            BufferedWriter out = new BufferedWriter(osw);
            out.write(uuid);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.i("exception", e.getMessage());
        }
    }

    public static void readDeviceId() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + QuopnConstants.QUOPN_DEVICEID_FOLDER + "/" + QuopnConstants.QUOPN_DEVICEID_FILE);
            int fileSize = (int) file.length();
            byte[] fileBytes = new byte[fileSize];
            FileInputStream fileStream = new FileInputStream(file);
            fileStream.read(fileBytes);
            fileStream.close();
            QuopnConstants.android_id = new String(fileBytes);
        } catch (Exception e) {
            System.out.println("Decryption Exception " + e);
        }
    }


    public static String getCurrentDateTime() // for enrollmentId
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String Year = String.valueOf(year);
        StringBuffer dString = new StringBuffer();
        dString.append("Date:");
        dString.append((date > 9) ? String.valueOf(date) : ("0" + date));
        dString.append("/");
        dString.append((month > 9) ? String.valueOf(month) : ("0" + month));
        dString.append("/");
        dString.append(Year.substring(2, 4));
        dString.append(" Time:");
        dString.append((hour > 9) ? String.valueOf(hour) : ("0" + hour));
        dString.append("_");
        dString.append((minute > 9) ? String.valueOf(minute) : ("0" + minute));
        dString.append("_");
        dString.append((sec > 9) ? String.valueOf(sec) : ("0" + sec));
        return dString.toString();
    }


    public static ArrayList<StateData> convertStateCursorToList(Cursor c) {

        ArrayList<StateData> mArrayList = new ArrayList<StateData>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            StateData stateData = new StateData();
            stateData.setState_id(c.getString(c.getColumnIndex(ITableData.TABLE_STATES.COLUMN_STATE_ID)));
            stateData.setState_name(c.getString(c.getColumnIndex(ITableData.TABLE_STATES.COLUMN_STATE_NAME)));
            mArrayList.add(stateData);
        }

        return mArrayList;

    }

    public static ArrayList<CityData> convertCityCursorToList(Cursor c) {

        ArrayList<CityData> mArrayList = new ArrayList<CityData>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            CityData cityData = new CityData();
            cityData.setCity_id(c.getString(c.getColumnIndex(ITableData.TABLE_CITIES.COLUMN_CITY_ID)));
            cityData.setCity_name(c.getString(c.getColumnIndex(ITableData.TABLE_CITIES.COLUMN_CITY_NAME)));
            mArrayList.add(cityData);
        }

        return mArrayList;

    }


    /// siv for memory efficient way for retrivieing images from storage.
    public static Bitmap getImageFromStorage(String path, String fileName) {
        Bitmap img = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inBitmap = img;

            File f = new File(path, fileName);
            img = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static void showTimeoutDialog(Context context) {

    }

    public static void logoutCleanUp(Context context, boolean isSessionLogOut) {
        try {
            if (isSessionLogOut) {
                Toast.makeText(context, R.string.session_logout_message, Toast.LENGTH_LONG);
            }
            String imagePath = PreferenceUtil.getInstance(context)
                    .getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.PROFILE_IMAGE_PATH);
            File f = new File(imagePath, QuopnConstants.PROFILE_IMG_NAME);
            boolean isDeleted = f.delete();

            QuopnConstants.PROFILE_PIC_DATA = null;
            PreferenceUtil.getInstance(context).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY, "");
            PreferenceUtil.getInstance(context).clearPreference();
            context.getContentResolver().delete(ConProvider.CONTENT_URI_CATEGORY, null, null);
//            context.getContentResolver().delete(ConProvider.CONTENT_URI_CITIES, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_GIFTS, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART, null, null);
//            context.getContentResolver().delete(ConProvider.CONTENT_URI_STATES, null, null);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

            //Cancel all existing notifications
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.cancelAll();

            context.startActivity(new Intent(context, RegistrationScreen.class));
        } catch (Exception ex) {

        }
    }

    public static void citrusLogout(final Context context){
        if (QuopnApplication.getInstance().getIsCitrusInitiated()) {
            CitrusClient.getInstance(context).signOut(new Callback<CitrusResponse>() {
                @Override
                public void error(CitrusError citrusError) {
                }

                @Override
                public void success(CitrusResponse citrusResponse) {
                }
            });
        }
    }

    public static void logoutCleanUp(Context context) {
        citrusLogout(QuopnApplication.getInstance().getApplicationContext());
        QuopnApplication.getInstance().setIsCitrusInitiated(false);
        try {

            String imagePath = PreferenceUtil.getInstance(context)
                    .getPreference(
                            PreferenceUtil.SHARED_PREF_KEYS.PROFILE_IMAGE_PATH);
            File f = new File(imagePath, QuopnConstants.PROFILE_IMG_NAME);
            boolean isDeleted = f.delete();

            QuopnConstants.PROFILE_PIC_DATA = null;
            PreferenceUtil.getInstance(context).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY, "");
            PreferenceUtil.getInstance(context).clearPreference();
            context.getContentResolver().delete(ConProvider.CONTENT_URI_CATEGORY, null, null);
//            context.getContentResolver().delete(ConProvider.CONTENT_URI_CITIES, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_QUOPN, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_GIFTS, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_NOTIFICATION, null, null);
            context.getContentResolver().delete(ConProvider.CONTENT_URI_MYCART, null, null);
//            context.getContentResolver().delete(ConProvider.CONTENT_URI_STATES, null, null);


            //Cancel all existing notifications
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.cancelAll();

//            // citrus
//            CitrusClient.getInstance(context).signOut(new Callback<CitrusResponse>() {
//                @Override
//                public void error(CitrusError citrusError) {
//
//                }
//
//                @Override
//                public void success(CitrusResponse citrusResponse) {
//
//                }
//            });


            Intent intent = new Intent(context, RegistrationScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            Log.d("quopn/logout", "from QuopnUtils");
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

//            context.startActivity(new Intent(context, RegistrationScreen.class));
        } catch (Exception ex) {

        }
    }

    /**
     * Extracts indian mobile number from any contact
     *
     * @param number
     * @return extracted number or "0"
     */
    public static String getIndianMobileFromContactNumber(String number) {
        if (!number.isEmpty()) {
            number = number.replaceAll("\\D+", "");
            if (!number.isEmpty()) {
                if (9 < number.length() && number.length() < 13) {
                    if (number.length() == 12) {
                        Character d1 = number.charAt(0);
                        Character d2 = number.charAt(1);
                        if (d1.equals('9') && d2.equals('1')) {
                            // removing 91
                            number = number.substring(2);
                        } else {
                            return "0";
                        }
                    } else if (number.length() == 11) {
                        Character d1 = number.charAt(0);
                        if (d1.equals('0')) {
                            // removing 0
                            number = number.substring(1);
                        } else {
                            return "0";
                        }
                    }
                    // now number is 10 digit
                    Character d1 = number.charAt(0);
                    if (d1.equals('7') || d1.equals('8') || d1.equals('9')) {
                        return number;
                    }
                }
            }
        }
        return "0";
    }

    public static Boolean areMobileNosSame (String mobile1, String mobile2) {
        String mobile1_i = getIndianMobileFromContactNumber(mobile1);
        String mobile2_i = getIndianMobileFromContactNumber(mobile2);
        if (mobile1_i.equalsIgnoreCase(mobile2_i)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context : Context of activity.
     * @Description : This method is use for checking internet connectivity of android device and show dialogue.
     */
    public static boolean isInternetAvailableAndShowDialog(Context context) {
        if (isInternetAvailable(context)) {
            return true;
        } else {
            Dialog dialog = new Dialog(context, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
            dialog.show();
            return false;
        }
    }

    public static String trimString(String arg) {
        if (!arg.isEmpty()) {
            arg = arg.trim();
        }
        return arg;
    }

    public static boolean isUnauthorized(VolleyError error) {
        if (!TextUtils.isEmpty(error.getMessage())) {
            if (error.getMessage().contains("No authentication challenges found") || error.getMessage().contains("Received authentication challenge is null")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTimedOut(VolleyError error) {
        if (!TextUtils.isEmpty(error.getMessage())) {
            if (error.getMessage().contains("I/O error during system call, Connection timed out") || error.getMessage().contains("ENETUNREACH (Network is unreachable)")) {
                return true;
            }
        }
        return false;
    }

    public static boolean showDialogForContext(Context context, String message) {
        if (context == null || TextUtils.isEmpty(message)) {
            return false;
        } else {
            if (!(context instanceof Activity)){
                return false;
            }
        }
        if (!((Activity) context).isFinishing()) {
            //R.string.slow_internet_connection
            Dialog dialog = new Dialog(context, R.string.dialog_title, message);
            dialog.show();
            return true;
        }
        return false;
    }

    private static final Map<Integer, QuopnConstants.WalletType> intToTypeMap = new HashMap<Integer, QuopnConstants.WalletType>();
    static {
        for (QuopnConstants.WalletType type : QuopnConstants.WalletType.values()) {
            intToTypeMap.put(type.ordinal(), type);
        }
    }

    public static QuopnConstants.WalletType walletTypeFromInt(int i) {
        QuopnConstants.WalletType type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null) {
            return QuopnConstants.WalletType.NONE;
        }
        return type;
    }

    public static QuopnConstants.WalletType walletTypeFromString(String str) {
        if (str.isEmpty()) {
            return QuopnConstants.WalletType.NONE;
        }
        int i = Integer.parseInt(str);
        QuopnConstants.WalletType type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null) {
            return QuopnConstants.WalletType.NONE;
        }
        return type;
    }

    /**
     * setting default wallet in pref and QuopnApplication
     * @param strWalletType "0",1,2
     * @param context pass application context
     */
    public static void setDefaultWalletInAppAndPref (String strWalletType, Context context) {
//        Logger.d(strWalletType);
        PreferenceUtil.getInstance(context).setPreference(PreferenceUtil.SHARED_PREF_KEYS.DEFAULT_WALLET_KEY, strWalletType);
        QuopnApplication.getInstance().setDefaultWallet(walletTypeFromString(strWalletType));
    }

    public static boolean isActivityRunningForContext (Context context) {
        if (context != null && (context instanceof Activity)){
            if (!((Activity) context).isFinishing()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String sendNonNullValueForString (String str) {
        if (str.isEmpty()) {
            return "";
        } else {
            return str;
        }
    }

    public static void showDialog (final Context dialogContext, final int title, final String message) {
        if (dialogContext != null && dialogContext instanceof Activity) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog(dialogContext, title, message);
                    dialog.show();
                }
            };
            ((Activity)dialogContext).runOnUiThread(runnable);
        }
    }

    public static void showDialog (final Context dialogContext, final int title, final String message, final DialogInterface.OnDismissListener dismissListener) {
        if (dialogContext != null && dialogContext instanceof Activity) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog(dialogContext, title, message);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }
            };
            ((Activity)dialogContext).runOnUiThread(runnable);
        }
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

}
