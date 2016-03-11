/**
 * @author VishalNema
 *
 *@date 04/09/2014
 *
 *@Date 026/09/14 Update by Ravishankar 
 *and added functionality for Video upload.
 */
package com.quopn.wallet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quopn.wallet.InviteUserActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.InviteData;
import com.quopn.wallet.data.model.InviteMobileNoCheckingData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.MultipartRequest;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.QuopnEditTextView;

import java.util.HashMap;
import java.util.Map;

public class InviteUserFragment extends BaseFragment implements OnClickListener  ,
ConnectionListener{
	

	private static final String TAG = "InviteUserFragment";
	private static final int MESSAGE_INVITE_MOBILE_NO_CHECK_FROM_SERVER=0x0001;
	private static final int RESPONSE_ERROR=0x0101; 
	private static final int RESPONSE_SUCCESS=0x0102; 
	private static final int RESPONSE_UNEXPECTED=0x0103; 
	private static final int RESPONSE_INVITE_MOBILE_NO_VALID=0x0104; 
	private static final int RESPONSE_INVITE_MOBILE_NO_INVALID=0x0105; 
	private static final int RESPONSE_INVITE_ERROR=0x0201;
	private static final int RESPONSE_INVITE_SUCCESS=0x0202;

	private boolean isFilledMobileNoIsValid=false;
	private boolean isPersonalMessageRecoded=false;
    private ImageView mInviteIcon,mInviteMobileIcon,mImageSelectFromContact;
    //private RadioButton mMale, mFemale;
	//private QuopnTextView  mSendInvitation;//mRecordPersonalMessage,;
    private LinearLayout  mSendInvitation;
    private QuopnEditTextView mInviteName, mInviteMobileNumber;
	private LinearLayout mSelectFromContact;
	private ProgressBar mMobileNoCheckProgress;
	private CustomProgressDialog mSendInviteProgressDialog;
	private String userMobileNo;
	private TextView mInvite_user_text,leagelClause;
	private InviteUserFragment mInviteUserFragment;
	/**Below commented variable use for FFmeg library*/

	
	private  String mVideoPersonalMessage="null";
	
//	public static final int VIDEO_CAPTURE_REQUEST = 100;
//	private static final int VIDEO_DURATION_LIMIT = 10;// in second
//	private static final int VIDEO_QUALITY = 0; //0 for Low and 1 for high.
//	public static String VIDEO_FILE_PATH="";
	public static final int PICK_CONTACT = 1; 
	
	private Handler mMobielNumberCheckHandler;
	
	int digits = 10;
	int plus_sign_pos = 0;
	
	private ConnectionFactory mConnectionFactory;
	private String mobNumber;
	
	private ImageView mImgGender;
	private Object tagDefault;
	private Object tagMale = "Male";
	private Object tagFemale = "Female";
	private InviteUserActivity mInviteUserActivity;
	
	public InviteUserFragment() {
		super();
		mInviteUserFragment = this;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mInviteUserActivity = (InviteUserActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inviteView = inflater.inflate(R.layout.invite_user, null);
		
		userMobileNo=PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);
		String leagaltext=PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_MESSAGE);
		String invitetoptext=PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_TOP_MESSAGE);
		
		mSelectFromContact = (LinearLayout) inviteView.findViewById(R.id.selectFromContact);
		//mImageSelectFromContact = (ImageView) inviteView.findViewById(R.id.imgSelectContact);
		mInviteIcon = (ImageView) inviteView.findViewById(R.id.inviteIcon);
		mInviteMobileIcon = (ImageView) inviteView.findViewById(R.id.inviteMobileIcon);
		mInviteName = (QuopnEditTextView) inviteView.findViewById(R.id.inviteName);
		mInviteMobileNumber = (QuopnEditTextView) inviteView.findViewById(R.id.inviteMobileNumber);
		//mMale = (RadioButton) inviteView.findViewById(R.id.male);
		//mFemale = (RadioButton) inviteView.findViewById(R.id.female);
		//mRecordPersonalMessage = (QuopnTextView) inviteView.findViewById(R.id.recordPersonalMessage);
		//mSendInvitation = (QuopnTextView) inviteView.findViewById(R.id.sendInvitation);
		mSendInvitation =(LinearLayout) inviteView.findViewById(R.id.send_invite_tab);
		leagelClause = (TextView) inviteView.findViewById(R.id.leagelClause);
		mInvite_user_text=(TextView) inviteView.findViewById(R.id.invite_user_text);
		if(leagaltext!=null)
			leagelClause.setText(leagaltext);
		if(invitetoptext!=null)
			mInvite_user_text.setText(invitetoptext);
		
		mMobileNoCheckProgress= (ProgressBar) inviteView.findViewById(R.id.mobileNoCheckProgress);
		mInviteName=QuopnUtils.setHintEditText(mInviteName, getString(R.string.yourname), true);
		mInviteMobileNumber=QuopnUtils.setHintEditText(mInviteMobileNumber, getString(R.string.yourmobileno), true);

		//mImageSelectFromContact.setOnClickListener(this);
		mSelectFromContact.setOnClickListener(this);
		mInviteIcon.setOnClickListener(this);
		mInviteMobileIcon.setOnClickListener(this);
		//mRecordPersonalMessage.setOnClickListener(this);
		
		//mSendInvitation.setVisibility(View.INVISIBLE);
		mSendInvitation.setOnClickListener(this);
		
		mConnectionFactory= new ConnectionFactory(mInviteUserActivity,this);
		mSendInviteProgressDialog = new CustomProgressDialog(mInviteUserActivity);
		mSendInviteProgressDialog.setTitle(R.string.progress_registering_txt);
		
		mInviteMobileNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if(mInviteMobileNumber.getText().length()==10){
					if (userMobileNo.equalsIgnoreCase(mInviteMobileNumber.getText().toString())) {
						 mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
						 mInviteMobileNumber.setText("");
						 Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,R.string.error_invite_notsenttoownmobieno);
						 dialog.show();
					}else if(!Validations.validateFirstCharOfMobNo(mInviteMobileNumber.getText().toString())){
						 Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,R.string.mobileno_validation);
						 dialog.show();
					}else if(QuopnUtils.isInternetAvailable(mInviteUserActivity)){
						mInviteMobileNumber.setError(null);
						 mMobielNumberCheckHandler.sendEmptyMessage(MESSAGE_INVITE_MOBILE_NO_CHECK_FROM_SERVER);
					    (new InviteMobileNoCheckingTask()).execute(new String[]{""+mInviteMobileNumber.getText()});
					}
				}else{
					mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
	
		//String gender = response.getUser().getGender();
		tagDefault = getResources().getString(R.string.gender_na);
		mImgGender = (ImageView) inviteView.findViewById(R.id.imgGender);
//		
//		if (gender != null) {
//			if (gender.toLowerCase().startsWith("m")) {
//				mImgGender.setImageResource(R.drawable.male_on);
//				mImgGender.setTag(tagMale);
//			} else if (gender.toLowerCase().startsWith("f")) {
//				mImgGender.setImageResource(R.drawable.female_on);
//				mImgGender.setTag(tagFemale);
//			}
//		}
		
		
		/// commented by siv to disable the swipe and enable the click toggling of the gender
		/*mImgGender.setOnTouchListener(new Utils.OnSwipeTouchListener(getActivity()) {
		    @Override
		    public void onSwipeLeft() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.toggle_gender_female);
				mImgGender.setTag(tagFemale);
		    }
		    mImgGender.setOnTouchListener(new Utils.OnSwipeTouchListener(getActivity()) {
		    @Override
		    public void onSwipeLeft() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.toggle_gender_female);
				mImgGender.setTag(tagFemale);
		    }
		    
		    @Override
		    public void onSwipeRight() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.toggle_gender_male);
				mImgGender.setTag(tagMale);
		    }
		});
		    @Override
		    public void onSwipeRight() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.toggle_gender_male);
				mImgGender.setTag(tagMale);
		    }
		});*/
		
			mImgGender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				buttonSaveProfile.setVisibility(View.VISIBLE);
				
				Object tag = mImgGender.getTag();
				if(tag.equals(tagDefault) || tag.equals(tagFemale)){
					mImgGender.setImageResource(R.drawable.toggle_gender_male);
					mImgGender.setTag(tagMale);
				} else if(tag.equals(tagMale)){
					mImgGender.setImageResource(R.drawable.toggle_gender_female);
					mImgGender.setTag(tagFemale);
				}
			}
		});
		
		mMobielNumberCheckHandler = new Handler() {
			@Override
			public void handleMessage(Message msssage) {
				switch (msssage.what) {
	            case MESSAGE_INVITE_MOBILE_NO_CHECK_FROM_SERVER:
	            	mMobileNoCheckProgress.setVisibility(View.VISIBLE);
	            	break;
	            case RESPONSE_ERROR :
					isFilledMobileNoIsValid=false;
					mSendInviteProgressDialog.dismiss();
					mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
					reset();
					 Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,msssage.getData().getString("error_message"));
					 dialog.show();
	            	break;
	            case RESPONSE_SUCCESS :
	            	mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
	            	mSendInvitation.setVisibility(View.VISIBLE);
					Log.d(TAG, "inside ------------------RESPONSE_INVITE_MOBILE_NO_VALID");
	            	break;
	            case RESPONSE_UNEXPECTED :
	            	mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
					 Dialog dialog1=new Dialog(mInviteUserActivity, R.string.dialog_title_error,"RESPONSE_UNEXPECTED");
					 dialog1.show();
					
	            	break;
	            	
				 case RESPONSE_INVITE_MOBILE_NO_VALID :
					 mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
					 mSendInvitation.setVisibility(View.VISIBLE);
					 Log.d(TAG, "inside ------------------RESPONSE_INVITE_MOBILE_NO_VALID");
					break;
				 case RESPONSE_INVITE_MOBILE_NO_INVALID :
					 mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
				 	break;
				 	
				 	//*******Send Invitation Request Response*******
				 case RESPONSE_INVITE_ERROR :
					 mSendInviteProgressDialog.dismiss();
					 Dialog dialog2=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,msssage.getData().getString("error_message"));
					 dialog2.show();
				 	break;
				 case RESPONSE_INVITE_SUCCESS :
					 mSendInviteProgressDialog.dismiss();
				 	break;
				 	
				 	
					}
			}
		};

		

		return inviteView;
	}

	private void reset() {
		mInviteMobileNumber.setText("");
		mInviteName.setText("");
		mImgGender.setImageResource(R.drawable.toggle_gender_male);
		mImgGender.setTag(tagMale);
	}

	public void readcontact() {
		reset();
		try {
			/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);*/

			Intent intent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

			if (intent.resolveActivity(mInviteUserActivity
					.getPackageManager()) == null) {
				Toast.makeText(mInviteUserActivity,
						"There are no applications to handle your request",
						Toast.LENGTH_LONG).show();
			} else {
				startActivityForResult(intent, PICK_CONTACT);
			}
			   
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private String removeCountryCode(String number) {
//		if (hasCountryCode(number)) {
//			int country_digits = number.length() - digits;
//			number = number.substring(country_digits);
//		}
//
//		return number;
//	}

//	private boolean hasCountryCode(String number) {
//		return number.charAt(plus_sign_pos) == '+';
//	}

//	private String removeSpace(String number) {
//		number = number.replaceAll("\\s", "");
//		return number;
//	}

//	private boolean hasRemoveSpace(String number) {
//		return true;
//	}
//
//	private String removeFirstDigit(String number) {
//		if (hasRemoveFirstDigit(number)) {
//			int first_digits = number.length() - digits;
//			number = number.substring(first_digits);
//		}
//		return number;
//	}

//	private boolean hasRemoveFirstDigit(String number) {
//		return number.charAt(plus_sign_pos) == '0';
//	}
//	@Override
//	public void onBackPressed(FragmentActivity activity) {
//		super.onBackPressed(activity);
//		activity.getSupportFragmentManager().beginTransaction()
//				.remove(this).commit();
//		Fragment menu = new MainMenuFragment();
//		FragmentManager fragmentManager = activity.getSupportFragmentManager();
//		fragmentManager.beginTransaction().replace(R.id.menu_frame, menu)
//				.commit();
//	}

//		private void resetInvite(){
//			mInviteName.setText("");
//			mInviteMobileNumber.setText("");
//			mMale.setSelected(false);
//			mFemale.setSelected(false);
//		}

	class SendingInvitationTask extends AsyncTask<String,Object, Object>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Object doInBackground(String... urls) {
			if(mVideoPersonalMessage==null||mVideoPersonalMessage.equalsIgnoreCase("null")){
			Map<String, String> params = new HashMap<String, String>();
			params.put(QuopnApi.ParamKey.USER_ID,urls[0]);
			params.put(QuopnApi.ParamKey.NAME,urls[1].trim());
			params.put(QuopnApi.ParamKey.MOBILE, urls[2]);
			params.put(QuopnApi.ParamKey.GENDER,urls[3]);
			mConnectionFactory.setPostParams(params);
			mConnectionFactory.createConnection(QuopnConstants.INVITE_USER);
			}else{
				
			MultipartRequest request = new MultipartRequest();
			request.setUrl(QuopnApi.INVITE);
			request.addFieldValue(QuopnApi.ParamKey.USER_ID, urls[0]);
			request.addFieldValue(QuopnApi.ParamKey.NAME, urls[1].trim());
			request.addFieldValue(QuopnApi.ParamKey.MOBILE, urls[2]);
			request.addFieldValue(QuopnApi.ParamKey.GENDER, urls[3]);
			request.addFieldValue(QuopnApi.ParamKey.VIDEO, "null");
			request.addFile("video", mVideoPersonalMessage, "video/mp4");
			int response = request.request();
//			Log.d(TAG, "Server returned response : " + response);
			String data = new String(request.getReturnData());
				
	
			try {
				Gson gson = new Gson();
				InviteData response1 = (InviteData)gson.fromJson(data, InviteData.class);
				onResponse(ConnectionListener.RESPONSE_OK,response1);
			} catch (JsonSyntaxException e) {
				onResponse(ConnectionListener.PARSE_ERR0R,null);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			
		 }
			return null;
		}

	}
	
@Override
public void onClick(View view){
	switch(view.getId()){
	/*case R.id.recordPersonalMessage :
		recordPersonalVideoMessage();
		break;*/
	case R.id.selectFromContact :
		readcontact();
		break;
	case R.id.sendInvitation :
		checkValidateion();
		break;
   	case R.id.send_invite_tab:
		checkValidateion();
		break;
	}
}

//private void recordPersonalVideoMessage(){
//
//	
//	//siv
//	CameraUtils cameraUtils = new CameraUtils();
//	if(cameraUtils.checkCameraHardware(getActivity()))
//	{
//		Intent intent  = new Intent(getActivity(), RecordVideoActivity.class);
//		startActivityForResult(intent, VIDEO_CAPTURE_REQUEST);
//	}
//	else
//	{
//		DialogConfigData errorDialog = new DialogConfigData(getActivity(), DialogConfigData.TYPE_ERROR,
//				 R.string.dialog_title_error, R.string.error_camera_not_found);
//		AlertManager.show(errorDialog);
//	}
//}

private boolean checkValidateion(){
	final String tempInviteName = mInviteName.getText().toString();
	final String tempInviteMobileNo = mInviteMobileNumber.getText().toString();
	String userMobileNo=PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY);
	
	if(!QuopnUtils.isInternetAvailable(mInviteUserActivity)){
		 Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
		 dialog.show();
		return false;
	}
	else if(TextUtils.isEmpty(tempInviteName)  ){
		Validations.CustomErrorMessage(mInviteUserActivity, R.string.name_validation_invite, mInviteName, null, 0);
	}else if( (tempInviteName.startsWith(" ") && tempInviteName.endsWith(" ")) || (tempInviteName.startsWith(" ")) || (tempInviteName.endsWith(" "))  ){
		//Validations.CustomErrorMessage(mInviteUserActivity, R.string.name_validation, mInviteName, null, 0);
		mInviteName.setText(tempInviteName.trim());
	} else if (!Validations.isValidName(tempInviteName)) {
		Validations.CustomErrorMessage(mInviteUserActivity, R.string.name_validation, mInviteName, null, 0);
	} else if (TextUtils.isEmpty(tempInviteMobileNo)) {
		Validations.CustomErrorMessage(mInviteUserActivity, R.string.blank_mobilenumber_validation, mInviteMobileNumber, null, 0);
	} else if (!tempInviteMobileNo.matches(QuopnConstants.MOBILEPATTERN)) {
		Validations.CustomErrorMessage(mInviteUserActivity,R.string.mobileno_validation,mInviteMobileNumber, null, 0);
	} else if(!Validations.validateFirstCharOfMobNo(tempInviteMobileNo)){
		Validations.CustomErrorMessage(mInviteUserActivity, R.string.mobileno_validation, mInviteMobileNumber, null, 0);
//	} else if (!(mMale.isChecked() || mFemale.isChecked())) {
//		AlertManager.show(new DialogConfigData(getActivity(),DialogConfigData.TYPE_ERROR,R.string.dialog_title_gender_validation,R.string.inviteuser_gender_validation));
	}else if (userMobileNo.equalsIgnoreCase(tempInviteMobileNo) && isFilledMobileNoIsValid) {
		reset();
		Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,R.string.error_invite_notsenttoownmobieno);
		dialog.show();
	}else if(mMobileNoCheckProgress.isShown()){
//		Validations.CustomAlertMessage(getActivity(), "Validating", "Validating invite mobile no...",AlertManager.mDialog_ERROR);
	}else if(!isFilledMobileNoIsValid){
//		Validations.CustomAlertMessage(getActivity(), "Mobile No", "The mobile no you entered is not valid for invite",AlertManager.mDialog_ERROR);
	}else if(!isPersonalMessageRecoded){
		
//		OnClickListener recordButtonListner=new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(AlertManager.mDialog.isShowing()){
//					AlertManager.mDialog.dismiss();
//				}
//				 recordPersonalVideoMessage();
//			}
//		};
		
		
		// commented by siv ,invitation no longer needs video msg. 
		/*
		OnClickListener continueButtonListner=new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSendInviteProgressDialog.show();
		        sendInvitation(tempInviteName,tempInviteMobileNo);
			}
		};
		 
		
		 Dialog dialog=new Dialog(getActivity(), R.string.dialog_title_send_invite,R.string.alert_if_user_send_invitemsg_without_videomsg); 
		 dialog.addCancelButton("CANCEL");
		 dialog.setOnCancelButtonClickListener(continueButtonListner);
		 dialog.show();*/
		
		mSendInviteProgressDialog.show();
		sendInvitation(tempInviteName, tempInviteMobileNo);

	}
	else{
		mSendInviteProgressDialog.show();
		sendInvitation(tempInviteName,tempInviteMobileNo);
	}
	return true;
}
private void sendInvitation(String inviteName,String inviteMobileNo) {
//	String UserId=PreferenceUtil.getInstance(getActivity()).getPreference(QuopnConstants.USER_ID);
//	if(mMale.isChecked()){
//		new SendingInvitationTask().execute(UserId,inviteName,inviteMobileNo,"M");	
//	}else{
//		new SendingInvitationTask().execute(UserId,inviteName,inviteMobileNo,"F");
//	}

	//new code
	String UserId = PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_ID);
	if (mImgGender.equals(tagMale)) {
		new SendingInvitationTask().execute(UserId, inviteName, inviteMobileNo, "M");
	} else {
		new SendingInvitationTask().execute(UserId, inviteName, inviteMobileNo, "F");
	}
}

	class InviteMobileNoCheckingTask extends AsyncTask<String,Object,Object> {
		@Override
		protected Object doInBackground(String... urls) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mobile", urls[0]);
				mConnectionFactory.setPostParams(params);
				mConnectionFactory.createConnection(QuopnConstants.INVITE_MOBIEL_NO_CHECKING);
				return null;
		}
	}
	
	@Override
	public void onResponse(int responseResult,Response response) {
		switch(responseResult){
		case RESPONSE_OK :
		if (response instanceof InviteMobileNoCheckingData) {
			InviteMobileNoCheckingData inviteDataResponse = (InviteMobileNoCheckingData) response;
			if(inviteDataResponse.isError()){
				isFilledMobileNoIsValid=false;
				Message msg = new Message();
				msg.what=RESPONSE_ERROR;
				Bundle b = new Bundle();
				b.putString("error_message", inviteDataResponse.getMessage());
				msg.setData(b);
				mMobielNumberCheckHandler.sendMessage(msg);

			}else{
				isFilledMobileNoIsValid=true;
				mMobielNumberCheckHandler.sendEmptyMessage(RESPONSE_SUCCESS);
			}
		}else if (response instanceof InviteData) {
			final InviteData inviteDataResponse = (InviteData) response;
			if(inviteDataResponse.isError()){
				Message msg = new Message();
				msg.what=RESPONSE_INVITE_ERROR;
				Bundle b = new Bundle();
				b.putString("error_message", inviteDataResponse.getMessage());
				msg.setData(b);
				mMobielNumberCheckHandler.sendMessage(msg);

			}else{
				mMobielNumberCheckHandler.sendEmptyMessage(RESPONSE_INVITE_SUCCESS);
				
				try {
					mInviteUserActivity.runOnUiThread(new Runnable() {
						public void run() {
							showInviteSucessfullyRegisteredDialogue(inviteDataResponse);
						}
					});
					 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		break;
		case CONNECTION_ERROR :
			mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
			 Dialog dialog=new Dialog(mInviteUserActivity, R.string.server_error_title,R.string.server_error);
			 dialog.show();
			break;
		case PARSE_ERR0R :
			mMobileNoCheckProgress.setVisibility(View.INVISIBLE);
			Dialog dialog1=new Dialog(mInviteUserActivity, R.string.server_error_title,R.string.server_error);
			dialog1.show();
			break;
		}
	}
	
	private void showInviteSucessfullyRegisteredDialogue(final InviteData inviteDataResponse) {

		OnClickListener cancelClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

//				if(AlertManager.mDialog.isShowing()){
//					AlertManager.mDialog.dismiss();
//				}
				mInviteUserFragment.mInviteUserActivity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));

			}
		};

		OnClickListener confirmClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					mInviteUserActivity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));

					// TODO add +91 before sending it to the sms
					String inviteMobileNo = mInviteMobileNumber.getText().toString();
					Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", inviteMobileNo, null));
					sendIntent.setType("vnd.android-dir/mms-sms");
					sendIntent.putExtra("address", inviteMobileNo);

					String invitesmstext = PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_SMS);
					if (invitesmstext != null) {
						sendIntent.putExtra("sms_body", invitesmstext);
					} else {
						sendIntent.putExtra("sms_body", getString(R.string.invite_user_suuceesfull_sms_txt) + " " + QuopnApi.PLAY_STORE_ULR + " " + getString(R.string.to_download));
					}

					PreferenceUtil.getInstance(QuopnApplication.getInstance().getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_COUNT, inviteDataResponse.getInvite_count());

					if (sendIntent.resolveActivity(mInviteUserActivity.getPackageManager()) == null) {
						Toast.makeText(
								mInviteUserActivity,
								"There are no applications to handle your request",
								Toast.LENGTH_LONG).show();
					} else {
						mInviteUserActivity.startActivity(sendIntent);
					}

				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(mInviteUserActivity, "There are no message clients installed.", Toast.LENGTH_SHORT).show();
				}

			}
		};
		Dialog dialog = new Dialog(mInviteUserActivity, R.string.dialog_title_send_invite, inviteDataResponse.getMessage());
		dialog.setOnAcceptButtonClickListener(confirmClickListener);
		dialog.show();
//		reset(); // ankur
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == VIDEO_CAPTURE_REQUEST) {
//			if (resultCode == Activity.RESULT_OK && data != null) {
//				try {
//					mVideoPersonalMessage = data.getStringExtra(QuopnConstants.PERSONAL_VIDEO_FILE_PATH);
//					mRecordPersonalMessage.setText(R.string.change_video);
//					isPersonalMessageRecoded=true;
//				} catch (ParseException e) {
//					e.printStackTrace();
//				} 
//			} else if (resultCode == Activity.RESULT_CANCELED) {
//				// No video captured
//				isPersonalMessageRecoded=false;
//				AlertManager.show(new DialogConfigData(getActivity(),DialogConfigData.TYPE_ERROR,R.string.server_error_title,R.string.alert_if_user_send_invitemsg_without_videomsg));
//				mRecordPersonalMessage.setText(R.string.record_video);
//				
//
//			} else {
//				isPersonalMessageRecoded=false;
//				AlertManager.show(new DialogConfigData(getActivity(),DialogConfigData.TYPE_ERROR,R.string.server_error_title,R.string.server_error));
//			}
//		}

		if (requestCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				@SuppressWarnings("deprecation")
				Cursor c = mInviteUserActivity.managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				    String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

					number = QuopnUtils.getIndianMobileFromContactNumber(number);
					if (number != "0") {
						mInviteName.setText(name);
						mInviteMobileNumber.setText(number);
					} else {
						Dialog dialog=new Dialog(mInviteUserActivity, R.string.dialog_title_invite_user,R.string.validation_mobile_with_contact_picker);
						dialog.show();
					}
					
//					number = stripNonDigits(number);
//					if(number.length() > 10){
//						number = number.substring(number.length() - 10);
//					}
//					mInviteMobileNumber.setText(stripNonDigits(number));

//					String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//
//					String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					
//					try {
//						if (hasPhone.equalsIgnoreCase("1")) {
//							Cursor phones = mInviteUserActivity
//									.getContentResolver()
//									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//											null,
//											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//													+ " = " + id, null, null);
//							if (phones.getCount() == 1) {
//								phones.moveToFirst();
//								mobNumber = phones.getString(phones.getColumnIndex("data1"));
//								if (hasCountryCode(mobNumber)) {
//									mInviteMobileNumber.setText(stripNonDigits(removeCountryCode(mobNumber)));
//								} else if (hasRemoveFirstDigit(mobNumber)) {
//									mobNumber = mobNumber.replaceAll("\\s", "");
//									mInviteMobileNumber.setText((removeFirstDigit(mobNumber)));
//								} else if (hasRemoveSpace(mobNumber)) {
//									mobNumber = mobNumber.replaceAll("[^\\w\\s]", "");
//									mInviteMobileNumber.setText(stripNonDigits(removeSpace(mobNumber)));
//
//								}
//
//							}
//						}
//					} catch (Exception ex) {
//						Log.e(TAG, ex.getLocalizedMessage());
//					}
				}
			}
		}
	}
	
//	public static String stripNonDigits(
//            final CharSequence input /* inspired by seh's comment */){
//    final StringBuilder sb = new StringBuilder(
//            input.length() /* also inspired by seh's comment */);
//    for(int i = 0; i < input.length(); i++){
//        final char c = input.charAt(i);
//        if(c > 47 && c < 58){
//            sb.append(c);
//        }
//    }
//    return sb.toString();
//}
//
//	public String getRealPathFromURI(Uri contentUri) {
//        // can post image
//        String [] proj={MediaStore.Images.Media.DATA};
//        @SuppressWarnings("deprecation")
//		Cursor cursor = mInviteUserActivity.managedQuery( contentUri,
//                        proj, // Which columns to return
//                        null,       // WHERE clause; which rows to return (all rows)
//                        null,       // WHERE clause selection arguments (none)
//                        null); // Order-by clause (ascending by name)
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//}

	@Override
	public void onTimeout(ConnectRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void myTimeout(String requestTag) {

	}
}

