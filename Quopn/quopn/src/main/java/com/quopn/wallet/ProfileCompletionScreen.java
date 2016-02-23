package com.quopn.wallet;

/**
 * @author Sumeet
 *
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment.DatePickerDialogHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.adapter.CityAdapter;
import com.quopn.wallet.adapter.StateAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.CityData;
import com.quopn.wallet.data.model.InterestedId;
import com.quopn.wallet.data.model.Item;
import com.quopn.wallet.data.model.OTPData;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.RegisterData;
import com.quopn.wallet.data.model.ResendOTPData;
import com.quopn.wallet.data.model.StateCityData;
import com.quopn.wallet.data.model.StateData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.fragments.MainMenuFragment;
import com.quopn.wallet.interfaces.ConfirmDialogListener;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.AssetsFileProvider;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;
import com.quopn.wallet.views.QuopnTextView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

//import com.gc.materialdesign.widgets.Dialog;

public class ProfileCompletionScreen extends FragmentActivity implements
		ConnectionListener, ConfirmDialogListener, DatePickerDialogHandler {
	private ConnectionFactory mConnectionFactory;
	private Map<String, String> params;
	private Map<String, String> headerParams;
	private User mUser;
	private Gson gson = new GsonBuilder().serializeNulls().create();

	/*private RadioButton btnMale;
	private RadioButton btnFemale;*/
	private ImageView mImgGender;
	private TextView textDOB;
	private EditText editEmail;
	private TextView textLocation;
	private Spinner spinnerCity;
	private TextView textInterests;
	private EditText mEditTextOtp;
	private TextView btnSubmit;
	private TextView textViewSignInAsAnotherUser;
	//	private DatePickerDialog datePickerDialog;
	private Handler mHandler;
	private ImageView mImgViewTick;

//	private int year = 0;
//	private int month = 0;
//	private int day = 0;


	private Calendar cal = Calendar.getInstance();

	private int year = cal.get(Calendar.YEAR);
	private int month = cal.get(Calendar.MONTH);
	private int day = cal.get(Calendar.DATE);
	private int mCodeAgeLimitError = 10;

	private TextView mTextViewResendOtp, mProgressText, mManualEntryText;
	private LinearLayout mSmswaitProgress;

	private String stateId = "0";
	private String cityId = "0";
	private String stateId_selected = "0";
	private String cityId_selected = "0";
	//	private String stateName="";
//	private String cityName="";
	private String stateName_selected = "";
	private String cityName_selected = "";
	private CustomProgressDialog mProgressDialog;
	private Context context;
	private Context aContext; // Activity context
	private String mPersonalVideoUrl;

	private RelativeLayout relLayInterests, relLayDob, relLayLocation;
	private String TAG = "PROFILE COMPLETION SCREEN";
	private OnClickListener mConfirmDialogListener;
	private String dob;
	private Button buttonSaveLocation;
	private AnalysisManager mAnalysisManager;
	private Object tagDefault;
	private Object tagMale = "Male";
	private Object tagFemale = "Female";

	private int selectedCityId;
	private int selectedStateId;

	private SmsListener mSmsListener = new SmsListener();
	private static final int RESPONSE_SUCCESS_MESSAGE = 100;
	private CountDownTimer mCountDownTimer;
	private final long mStartTime = 30 * 1000;
	private final long mInterval = 1 * 1000;

	private final int mMaxSmsAttempts = 3;
	private int mSmsAttempt = 0;
	private String mUserId = "", mMobileNo = "", mOtp = "";

	private TextView terms_privacypolicy_text2, terms_privacypolicy_text4;
	private int T_AND_C = 0;
	private int PRIV_POLICY = 1;
	private DatePickerBuilder datePickerBuilder;
	//private StatecityListner mStateCityListener = new StatecityListner();
	private ArrayList<String> list = null;
	private ListView listView1;
	//private EmailListAdapter listadaptor;
	private Spinner spinnerEmail;
	private Button buttonSaveEmail;
	private PopupMenu popupMenu;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
	//String EmailText;
	String EmailName_selected="";
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;
	String emailid;
	String pre_email;
	String pre_registered;
	private DatePickerBuilder dpb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_completion_screen);

		Log.d("location", "ProfileCompletionScreen onCreate");
		list = new ArrayList<String>();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mUserId = bundle.getString(QuopnConstants.INTENT_EXTRA_USERID);
			mMobileNo = bundle.getString(QuopnConstants.INTENT_EXTRA_MOBILE_NO);

			mOtp = bundle.getString(QuopnConstants.INTENT_EXTRA_OTP_NO);
		}
		mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();

		mPersonalVideoUrl = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL);

		context = getApplicationContext();
		aContext = this;

		// ankur
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		if (TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
			QuopnConstants.PROFILE_DATA = PreferenceUtil.getInstance(ProfileCompletionScreen.this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA);
		}
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == mCodeAgeLimitError) {
					Resources res = getResources();
					String errorText = String.format(res.getString(R.string.age_validation),
							QuopnConstants.AGE_CONSTRAINT);
					Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
				} else if (msg.what == RESPONSE_SUCCESS_MESSAGE) {
					mEditTextOtp.setText(msg.getData().getString("message"));
					mTextViewResendOtp.setVisibility(View.GONE);
					mManualEntryText.setVisibility(View.GONE);
					mSmswaitProgress.setVisibility(View.GONE);
					verifyPin();
				}
			}
		};

		registerReceiver(mSmsListener, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));

//		LocalBroadcastManager.getInstance(this).registerReceiver(mStateCityListener,
//				new IntentFilter(QuopnConstants.BROADCAST_UPDATE_STATE_CITY));
//		System.out.println("========ProfileCom==Oncreate==== StateCityData mStateCityListener linked");

		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.setTitle(R.string.progress_registering_txt);
		mProgressDialog.setCanceledOnTouchOutside(false); //ankur
		
		/*btnMale = (RadioButton) findViewById(R.id.gen_male);
		btnFemale = (RadioButton) findViewById(R.id.gen_female);*/

		tagDefault = getResources().getString(R.string.gender_na);
		mImgGender = (ImageView) findViewById(R.id.imgMaleFemale);
		/*mImgGender.setOnTouchListener(new Utils.OnSwipeTouchListener(context) {
			@Override
		    public void onSwipeLeft() {
		    	System.out.println("VAIBHAV IN PROFILE COMPLETION SCREEN onSwipeLeft()");
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.female_on_red);
				mImgGender.setTag(tagFemale);
		    }
		    
		    @Override
		    public void onSwipeRight() {
		    	System.out.println("VAIBHAV IN PROFILE COMPLETION SCREEN onSwipeRight()");
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.male_on_red);
				mImgGender.setTag(tagMale);
		    }
		});*/
		mImgGender.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("ON CLICK");
				Object tag = mImgGender.getTag();
				if (tag.equals(tagDefault) || tag.equals(tagFemale)) {
					mImgGender.setImageResource(R.drawable.male_on_red);
					mImgGender.setTag(tagMale);
				} else if (tag.equals(tagMale)) {
					mImgGender.setImageResource(R.drawable.female_on_red);
					mImgGender.setTag(tagFemale);
				}
			}
		});

		relLayDob = (RelativeLayout) findViewById(R.id.relLayDob);
		relLayInterests = (RelativeLayout) findViewById(R.id.relLayInterests);
		relLayLocation = (RelativeLayout) findViewById(R.id.relLayLocation);
		textDOB = (TextView) findViewById(R.id.editDOB);

		relLayDob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				textDOB.setError(null);
//				DatePickerBuilder dpb = new DatePickerBuilder()
//						.setFragmentManager(getSupportFragmentManager())
//						.setStyleResId(R.style.BetterPickersDialogFragment);
//				if (datePickerBuilder == null) {
//					datePickerBuilder = new DatePickerBuilder()
//							.setFragmentManager(getSupportFragmentManager())
//							.setStyleResId(R.style.BetterPickersDialogFragment);
//					datePickerBuilder.addDatePickerDialogHandler(ProfileCompletionScreen.this);
//					datePickerBuilder.show();
//				}
				if(dpb == null) {
					dpb = new DatePickerBuilder();
					dpb.setFragmentManager(getSupportFragmentManager())
							.setStyleResId(R.style.BetterPickersDialogFragment);

					dpb.show();
					dpb.addDatePickerDialogHandler(ProfileCompletionScreen.this);
				}

//				dpb.show();

//				dpb.addDatePickerDialogHandler(ProfileCompletionScreen.this);

			}

		});


		editEmail = (EditText) findViewById(R.id.editEmailId);
//		editEmail.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//editEmail.setError(null);
//				//bindViews();
//				//addAdapterToViews();
//				list = getData();
////				final Dialog email_dialog = new Dialog(ProfileCompletionScreen.this);
////				email_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////				email_dialog.setContentView(R.layout.dialog_email);
////				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
////				Window window = email_dialog.getWindow();
////				lp.copyFrom(window.getAttributes());
////				// This makes the dialog take up the full width
////				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
////				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
////				window.setAttributes(lp);
//				spinnerEmail = (Spinner) findViewById(R.id.spinner1);
//
//				//listView1 = (ListView) findViewById(R.id.listView1);
//				//EmailListAdapter listadaptor = new EmailListAdapter(ProfileCompletionScreen.this, android.R.layout.simple_spinner_dropdown_item, list);
//				//listView1.setAdapter(listadaptor);
//				//listadaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				ArrayAdapter<String> adapter= new ArrayAdapter<String>(ProfileCompletionScreen.this, android.R.layout.simple_spinner_dropdown_item ,list);
//				spinnerEmail.setAdapter(adapter);
//				editEmail.setText(list.toString());
////				spinnerEmail.setOnItemSelectedListener(new OnItemSelectedListener() {
////					@Override
////					public void onItemSelected(AdapterView<?> adapter,
////											   View view, int position, long id) {
////
////						//item  =  spinnerEmail.getSelectedItem(position);
////						itemValue = spinnerEmail.getItemAtPosition(position).toString();
////						System.out.println("=======OnitemSeleValue======"+itemValue);
////						//spinnerEmail.setVisibility(View.GONE);
////						bindViews();
////						//spinnerEmail.setVisibility(View.GONE);
//////						Toast.makeText(
//////								getApplicationContext(),
//////								item.getValue()+" is",
//////								Toast.LENGTH_LONG
//////						).show();
////					}
////
////					@Override
////					public void onNothingSelected(AdapterView<?> arg0) {
////					}
////				});
////				email_dialog.show();
////
////				buttonSaveEmail = (Button) email_dialog
////						.findViewById(R.id.buttonSubmit);
////				buttonSaveEmail.setOnClickListener(new OnClickListener() {
////
////					@Override
////					public void onClick(View v) {
////						//editEmail.setText(EmailName_selected);
////					}
////				});
//			}
//
//
//		});





		textLocation = (TextView) findViewById(R.id.textLocation);

		relLayLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (textLocation.getError() != null) {
					textLocation.setError(null);
				}
				final Dialog state_city_dialog = new Dialog(ProfileCompletionScreen.this);
				state_city_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				state_city_dialog.setContentView(R.layout.dialog_location);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				Window window = state_city_dialog.getWindow();
				lp.copyFrom(window.getAttributes());
				// This makes the dialog take up the full width
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				window.setAttributes(lp);

				spinnerCity = (Spinner) state_city_dialog.findViewById(R.id.spinnerCity);
//				int cityIndex = 0;
				{
					Cursor cursor_statecity = getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, null, null, null);
					if (cursor_statecity == null || cursor_statecity.getCount() == 0) {
						AssetsFileProvider assetsFileProvider = new AssetsFileProvider(getApplicationContext());
						String jsonformat = assetsFileProvider.loadJSONFromAsset(QuopnConstants.STATE_CITY_FILE);
						gson = new Gson();
						StateCityData stateCityData = (StateCityData) gson.fromJson(jsonformat, StateCityData.class);
						Log.d("location", "cursor_statecity = null");
						{
							ContentValues cv_state = new ContentValues();
							cv_state.put(ITableData.TABLE_STATES.COLUMN_STATE_ID, QuopnConstants.SELECT_STATE_ID);
							cv_state.put(ITableData.TABLE_STATES.COLUMN_STATE_NAME, "Select State");
							getContentResolver().insert(ConProvider.CONTENT_URI_STATES, cv_state);

							ContentValues cv_city = new ContentValues();
							cv_city.put(ITableData.TABLE_CITIES.COLUMN_CITY_ID, QuopnConstants.SELECT_CITY_ID);
							cv_city.put(ITableData.TABLE_CITIES.COLUMN_CITY_NAME, "Select City");
							cv_city.put(ITableData.TABLE_CITIES.COLUMN_STATE_ID, QuopnConstants.SELECT_STATE_ID);
							getContentResolver().insert(ConProvider.CONTENT_URI_CITIES, cv_city);
						}
						for (StateData statedata : stateCityData.getStates()) {

							ContentValues cv_1 = new ContentValues();
							cv_1.put(ITableData.TABLE_STATES.COLUMN_STATE_ID, statedata.getStateId());
							cv_1.put(ITableData.TABLE_STATES.COLUMN_STATE_NAME, statedata.getStateName());
							getContentResolver().insert(ConProvider.CONTENT_URI_STATES, cv_1);

							for (CityData cityData : statedata.getCities()) {
								{
									ContentValues cv_2 = new ContentValues();
									cv_2.put(ITableData.TABLE_CITIES.COLUMN_CITY_ID, cityData.getCityId());
									cv_2.put(ITableData.TABLE_CITIES.COLUMN_CITY_NAME, cityData.getCityName());
									cv_2.put(ITableData.TABLE_CITIES.COLUMN_STATE_ID, statedata.getStateId());
									getContentResolver().insert(ConProvider.CONTENT_URI_CITIES, cv_2);
								}
							}
						}

					}
				}
				Cursor city_cursor = getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? ", new String[]{stateId}, null);
				city_cursor.getCount();
				CityAdapter cityAdapter = new CityAdapter(ProfileCompletionScreen.this, R.layout.location_spinner, QuopnUtils.convertCityCursorToList(city_cursor));
				cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
				spinnerCity.setAdapter(cityAdapter);

				spinnerCity.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapter,
											   View view, int position, long id) {


						if (view != null) {
							cityId_selected = (String) ((TextView) view)
									.getTag();
							cityName_selected = (((TextView) view)
									.getText()).toString();

						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

				final Spinner spinnerState = (Spinner) state_city_dialog
						.findViewById(R.id.spinnerState);

				int stateIndex = 0;
				Cursor state_cursor = getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, null, null, null);
				StateAdapter stateAdapter = new StateAdapter(ProfileCompletionScreen.this, R.layout.location_spinner, QuopnUtils.convertStateCursorToList(state_cursor));
				spinnerState.setAdapter(stateAdapter);
				spinnerState.setSelection(stateIndex);
				stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

				spinnerState
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> adapter,
													   View view, int position, long id) {
								if (view != null) {
									stateId_selected = (String) ((TextView) view)
											.getTag();
									stateName_selected = ((TextView) view)
											.getText().toString();
									Cursor city_cursor = getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? OR " + ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? ", new String[]{QuopnConstants.SELECT_STATE_ID, stateId_selected}, null);
									city_cursor.getCount();
									CityAdapter cityAdapter = new CityAdapter(ProfileCompletionScreen.this, R.layout.location_spinner, QuopnUtils.convertCityCursorToList(city_cursor));
									spinnerCity.setAdapter(cityAdapter);
									cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
								}

							}

							@Override
							public void onNothingSelected(AdapterView<?> adapter) {
							}
						});

				buttonSaveLocation = (Button) state_city_dialog
						.findViewById(R.id.buttonSave);
				buttonSaveLocation.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (updateLocationOnUI()
								&& state_city_dialog.isShowing()) {
							state_city_dialog.dismiss();
//									cityName = cityName_selected;
//									stateName = stateName_selected;
							cityId = cityId_selected;
							stateId = stateId_selected;
						}
					}
				});

				state_city_dialog.show();
			}
		});

		mEditTextOtp = (EditText) findViewById(R.id.editPin);
		mEditTextOtp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditTextOtp.setError(null);

			}
		});

		mImgViewTick = (ImageView) findViewById(R.id.imgViewTick);

		mTextViewResendOtp = (TextView) findViewById(R.id.resendotp);
		mTextViewResendOtp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resendOTP();
			}
		});

		mSmswaitProgress = (LinearLayout) findViewById(R.id.smswait_progress);
		mManualEntryText = (TextView) findViewById(R.id.textView3);
		mProgressText = (TextView) findViewById(R.id.progresstext);

		mCountDownTimer = new MyCountDownTimer(mStartTime, mInterval);
		mCountDownTimer.start();

		textInterests = (TextView) findViewById(R.id.editInterests);

		relLayInterests.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (textInterests.getError() != null) {
					textInterests.setError(null);
				}
				final Dialog dialog = new Dialog(ProfileCompletionScreen.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_interests);

				ProfileData response = (ProfileData) gson.fromJson(
						QuopnConstants.PROFILE_DATA, ProfileData.class);

				final ArrayList<InterestedId> arrayInterestedId = (ArrayList<InterestedId>) response
						.getUser().getInterestedid();
				boolean isNoInterestSelected = isNoInterestSelected(arrayInterestedId);
				if (isNoInterestSelected) {
					checkAllInterests(arrayInterestedId);
				}
				InterestsListAdapter interestsListAdapter = new InterestsListAdapter(
						ProfileCompletionScreen.this, arrayInterestedId);

				ListView listView = (ListView) dialog
						.findViewById(R.id.listInterests);
				listView.setAdapter(interestsListAdapter);

				Button buttonSaveInterests = (Button) dialog
						.findViewById(R.id.buttonSave);
				buttonSaveInterests
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (isInternetAvailable()) {
									ProfileData response = (ProfileData) gson
											.fromJson(
													QuopnConstants.PROFILE_DATA,
													ProfileData.class);
									((User) response.getUser())
											.setInterestedid(arrayInterestedId);

									QuopnConstants.PROFILE_DATA = gson
											.toJson(response);

									updateInterestsText();
									dialog.dismiss();
								} else {
									Toast.makeText(
											ProfileCompletionScreen.this,
											getResources()
													.getString(
															R.string.please_connect_to_internet),
											Toast.LENGTH_SHORT).show();
								}
							}
						});

				dialog.show();
			}
		});

		btnSubmit = (TextView) findViewById(R.id.btn_submit);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mImgViewTick.getVisibility() != View.VISIBLE) {
					verifyPin();
				} else if (mImgViewTick.getVisibility() == View.VISIBLE) {
					if (mImgViewTick.getTag().equals(PinVerificationTags.Success)) {
						verifyProfile();
					} else if (mImgViewTick.getTag().equals(PinVerificationTags.Failure)) {
						verifyPin();
					}
				}
//				verifyProfile();
			}

		});

		mConfirmDialogListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				PreferenceUtil.getInstance(ProfileCompletionScreen.this).clearPreference();
				getContentResolver().delete(
						ConProvider.CONTENT_URI_CATEGORY, null, null);
				getContentResolver().delete(
						ConProvider.CONTENT_URI_QUOPN, null, null);
				finish();
				startActivity(new Intent(ProfileCompletionScreen.this, RegistrationScreen.class));
			}
		};

		textViewSignInAsAnotherUser = (TextView) findViewById(R.id.text_sign_in_as_another_user);
		textViewSignInAsAnotherUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.signin, R.string.sign_in_message);
					dialog.addOkButton("YES");
					dialog.addCancelButton("NO");
					dialog.setOnAcceptButtonClickListener(mConfirmDialogListener);
					dialog.show();
				} catch (Exception ex) {
					Log.e(TAG, ex.getLocalizedMessage());
				}
			}
		});

		terms_privacypolicy_text2 = (QuopnTextView) findViewById(R.id.terms_privacypolicy_text2);
		terms_privacypolicy_text4 = (QuopnTextView) findViewById(R.id.terms_privacypolicy_text4);

		terms_privacypolicy_text2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*Intent webview=new Intent(ProfileCompletionScreen.this, WebViewActivity.class);
				webview.putExtra("url", QuopnApi.TERMS_AND_CONDITION_URL);
				startActivity(webview);*/
				showAboutScreen(T_AND_C);
			}
		});

		terms_privacypolicy_text4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent webview=new Intent(ProfileCompletionScreen.this, WebViewActivity.class);
//				webview.putExtra("url", QuopnApi.PRIVACY_POLICY_URL);
//				startActivity(webview);
				showAboutScreen(PRIV_POLICY);
			}
		});

		//
		///
		Button btnEmail = (Button) findViewById(R.id.btnEmailDropDown);
		btnEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(ProfileCompletionScreen.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
						editEmail.setText(item.getTitle());
						return true;
					}
				});
				popupMenu.show();
			}
		});


		ProfileData profileData = (ProfileData) gson.fromJson(QuopnConstants.PROFILE_DATA, ProfileData.class);
		User user = profileData.getUser();
		pre_email = user.getEmailid();

		popupMenu = new PopupMenu(this,btnEmail);
		//popupMenu.getMenu().add("test");
		if(!pre_email.isEmpty()){
			popupMenu.getMenu().add(pre_email);
		}
		for (String email:getEmailListData()) {
			popupMenu.getMenu().add(email);
		}

		if(popupMenu.getMenu().size() > 0){
			editEmail.setText(popupMenu.getMenu().getItem(0).getTitle());
		} else {
			btnEmail.setEnabled(false);
		}

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
		//bindViews();
	}


//	private void bindViews() {
//		//mACTVCountry = (AutoCompleteTextView) findViewById(R.id.actv_country);
//
//		editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus && TextUtils.isEmpty(editEmail.getText().toString())) {
//					editEmail.setText(getData().toString());
//					//spinnerEmail.setVisibility(View.GONE);
//					//editEmail.setText(itemValue);
//					//System.out.println("========onFocusChanged==Email==" + argEmailID);
//					//System.out.println("========onFocusChanged==Mobile=="+argMobileNo);
//					System.out.println("========onFocusChanged==Email=1111=" + editEmail.getText().toString());
//				} else if (!hasFocus && editEmail.getText().toString().equals("vishal.nema@gmail.com")) {
//					editEmail.setText("");
//					//System.out.println("========onFocusChanged==Email==2222=" + argEmailID);
//				}
//			}
//		});
//
//		//mACTVAddress = (AutoCompleteTextView) findViewById(R.id.actv_address);
//	}
//
//	private void addAdapterToViews() {
//		Account[] accounts = AccountManager.get(this).getAccounts();
//		Set<String> emailSet = new HashSet<String>();
//		for (Account account : accounts) {
//			if (EMAIL_PATTERN.matcher(account.name).matches()) {
//				emailSet.add(account.name);
//			}
//		}
//		editEmail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
//
//	}


	public void showAboutScreen(int argCode) {
		Intent aboutIntent = new Intent(context, AboutUsActivity.class);
		aboutIntent.putExtra(QuopnConstants.ABOUT_US_GROUP_TO_EXPAND, argCode);
		startActivityForResult(aboutIntent,
				QuopnConstants.HOME_PRESS);
	}


	private ArrayList<String> getEmailListData() {
		ArrayList<String> accountsList = new ArrayList<String>();

		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
			Logger.d(account.toString()+ " "+account.name);
			if (EMAIL_PATTERN.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				Item item = new Item(possibleEmail);
				accountsList.add(item.getValue());
			}
		}

		return accountsList;
	}
//	private BroadcastReceiver mStateCityListener = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			System.out.println("=====ProfileCompletionScreen=======BroadCastReciver=111111==== StateCityData mStateCityListener received");
//
//		}
//	};

	public void verifyProfile() {
		if (QuopnUtils.isInternetAvailableAndShowDialog(aContext)) {

			Object tagGender = mImgGender.getTag();
			String gender = "";
			if (tagGender.equals(tagDefault)) {
				Toast.makeText(
						aContext,
						getResources().getString(
								R.string.gender_validation),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (tagGender.equals(tagMale)) {
				gender = getResources().getString(R.string.gen_male_text).toLowerCase();
			} else if (tagGender.equals(tagFemale)) {
				gender = getResources().getString(R.string.gen_female_text).toLowerCase();
			}
			emailid = editEmail.getText().toString();
			System.out.println("=====VerifyProfile=====Data===email==="+emailid.toString());
			if (TextUtils.isEmpty(emailid)) {
				Validations.CustomErrorMessage(getApplicationContext(), R.string.emailid_validation, editEmail, null, 0);
				return;
			} else if (!TextUtils.isEmpty(emailid)) {
				if (!Validations.isValidEmail(emailid)) {
					Validations.CustomErrorMessage(getBaseContext(), R.string.entered_emailid_validation, editEmail, null, 0);
					return;
				}
				editEmail.setError(null);
			}

			//editEmail.clearFocus();
			String location = textLocation.getText().toString();
			if (location.equals(getResources().getText(R.string.select_location)) || TextUtils.isEmpty(location)) {
				Toast.makeText(
						aContext,
						getResources().getString(
								R.string.location_validation),
						Toast.LENGTH_SHORT).show();
				Validations.CustomErrorMessage(getApplicationContext(), R.string.location_validation,
						null, textLocation, 1);
				return;
			}
			String dob = textDOB.getText().toString();
			if (TextUtils.isEmpty(dob)) {
				Toast.makeText(
						aContext,
						getResources().getString(
								R.string.dob_validation),
						Toast.LENGTH_SHORT).show();
				Validations.CustomErrorMessage(getApplicationContext(), R.string.dob_validation,
						null, textDOB, 1);
				return;
			}
			String interests = textInterests.getText().toString();
			/*if(interests.equals(getResources().getText(R.string.select_interest)) || TextUtils.isEmpty(interests)){
				Toast.makeText(
						getBaseContext(),
						getResources().getString(
								R.string.interest_validation),
						Toast.LENGTH_SHORT).show();
				Validations.CustomErrorMessage(getApplicationContext(), R.string.interest_validation, 
						null, textInterests, 1);
				return;
			}*/
			ProfileData response = (ProfileData) gson.fromJson(
					QuopnConstants.PROFILE_DATA, ProfileData.class);

			response.getUser().setGender(gender);
			response.getUser().setDob(dob);
			response.getUser().setEmailid(emailid);
			response.getUser().setCity(String.valueOf(cityId));
			response.getUser().setState(String.valueOf(stateId));

			QuopnConstants.PROFILE_DATA = gson.toJson(response);
			PreferenceUtil
					.getInstance(ProfileCompletionScreen.this)
					.setPreference(
							PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA,
							QuopnConstants.PROFILE_DATA);
			updateProfile();

		}
	}

	public void resendOTP() {
		if (QuopnUtils.isInternetAvailableAndShowDialog(aContext)) {

			if (mSmsAttempt >= mMaxSmsAttempts) {
				mTextViewResendOtp.setVisibility(View.VISIBLE);
				mTextViewResendOtp.setText(R.string.resend_otp_morethan_3times_validation);
				return;

			}
			if (mProgressDialog != null) {
				mProgressDialog.show();
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("mobileno", mMobileNo);

			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.RESEND_OTP_CODE);
		}
	}

	public boolean isNoInterestSelected(ArrayList<InterestedId> argInterests) {
		boolean isNoInterestSelected = true;
		int size = argInterests.size();
		for (int i = 0; i < size; i++) {
			InterestedId interestId = argInterests.get(i);
			if (interestId.getUserunterest() == 0) {
			} else if (interestId.getUserunterest() == 1) {
				isNoInterestSelected = false;
				break;
			}

		}

		return isNoInterestSelected;
	}

	public void checkAllInterests(ArrayList<InterestedId> argInterests) {
		int size = argInterests.size();
		for (int i = 0; i < size; i++) {
			InterestedId interestId = argInterests.get(i);
//			if (interestId.getUserunterest() == 0) {
			interestId.setUserunterest(1);
//			} else if (interestId.getUserunterest() == 1) {
//			}

		}
	}

	DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
							  int selectedMonth, int selectedDay) {

			Calendar cal = Calendar.getInstance();

			int currYear = cal.get(Calendar.YEAR);
			int currMonth = cal.get(Calendar.MONTH);
			int currDay = cal.get(Calendar.DATE);

//			GregorianCalendar gcCurrent = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
//		    gcCurrent.clear();
//		    gcCurrent.set(currYear, currMonth, currDay);
//		    long currentMillis = gcCurrent.getTimeInMillis();


//			year = selectedYear;
//			month = selectedMonth;
//			day = selectedDay;

			GregorianCalendar gcSelected = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			gcSelected.clear();
			gcSelected.set(selectedYear, selectedMonth, selectedDay);
			long selectedMillis = gcSelected.getTimeInMillis();


			GregorianCalendar gcAgeCheck = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			gcAgeCheck.clear();
			gcAgeCheck.set(currYear - QuopnConstants.AGE_CONSTRAINT, currMonth, currDay);
			long ageLimitMillis = gcAgeCheck.getTimeInMillis();


			if (selectedMillis > ageLimitMillis) {
//		    	mHandler.sendEmptyMessage(mCodeAgeLimitError);
				return;
			}


			// set selected date into textview
			textDOB.setText(new StringBuilder().append(selectedYear).append("-")
					.append(selectedMonth + 1).append("-").append(selectedDay).append(""));

			// editDOB.setText("1999-05-01");

			// set selected date into datepicker also
			// datePickerDialog.init(year, month, day, null);
			dpb = null;
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ProfileCompletionScreen Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.quopn.wallet/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
//		finish();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.disconnect();
	}

	public void updateProfile() {
		try {
			if (QuopnUtils.isInternetAvailable(aContext)) {
				mProgressDialog.show();
				mUser = new User();
				String api_key = PreferenceUtil.getInstance(context).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
				headerParams = new HashMap<String, String>();
				headerParams.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
				ProfileData response = (ProfileData) gson.fromJson(
						QuopnConstants.PROFILE_DATA, ProfileData.class);

				mUser.setUserid(response.getUser().getUserid());
				mUser.setUsername(URLEncoder.encode(response.getUser()
						.getUsername(), "UTF-8"));
				mUser.setEmailid(URLEncoder.encode(response.getUser().getEmailid(),
						"UTF-8"));
				mUser.setGender(URLEncoder.encode(response.getUser().getGender(),
						"UTF-8"));
				mUser.setDob(URLEncoder
						.encode(response.getUser().getDob(), "UTF-8"));
				mUser.setState(URLEncoder.encode(response.getUser().getState(), "UTF-8"));
				mUser.setCity(URLEncoder.encode(response.getUser().getCity(), "UTF-8"));
				String interests = URLEncoder.encode(gson.toJson(response.getUser().getInterestedid(), ArrayList.class), "UTF-8");
				params = new HashMap<String, String>();
				params.put("userid", mUser.getUserid());
				params.put("username", mUser.getUsername());
				if (QuopnConstants.PROFILE_PIC_DATA != null) {
					params.put("pic", QuopnConstants.PROFILE_PIC_DATA);
				} else {
					params.put("pic", "");
				}
				params.put("emailid", mUser.getEmailid());
				params.put("gender", mUser.getGender());
				params.put("dob", mUser.getDob());
				params.put("state", mUser.getState());
				params.put("city", mUser.getCity());
				params.put("interested", interests);
				params.put("screenid", "1");


				mConnectionFactory = new ConnectionFactory(getApplicationContext(), this);
				mConnectionFactory.setHeaderParams(headerParams);
				mConnectionFactory.setPostParams(params);
				mConnectionFactory.createConnection(QuopnConstants.PROFILE_UPDATE_CODE);
			} else {
				try {
					com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.dialog_title_no_internet, R.string.please_connect_to_internet);
					dialog.show();
				} catch (Exception ex) {
					Log.e(TAG, ex.getLocalizedMessage());
				}

			}
		} catch (Exception ex) {

		}
	}
	
	/*public void splitDOB(){
		String[] arrDOB = dob.split("-");
		year = Integer.parseInt(arrDOB[0]);
		month = Integer.parseInt(arrDOB[1]);
		month = month - 1;
		day = Integer.parseInt(arrDOB[2]);
	}*/

	@Override
	public void onResponse(int responseResult, Response response) {
		// ankur

		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

		switch (responseResult) {
			case RESPONSE_OK:
				if (response instanceof RegisterData) {
					Logger.d("");
					RegisterData registerResponse = (RegisterData) response;
					if (registerResponse.getError() == true) {

					} else {

						// Set analysis manager to set that the profile is completed successfully
						mAnalysisManager.send(AnalysisEvents.PROFILE_COMPLETED);
						Logger.d("");
						PreferenceUtil.getInstance(ProfileCompletionScreen.this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, false);
						Intent intent = new Intent(this, MainActivity.class);
						intent.putExtra(QuopnConstants.INTENT_KEYS.should_announcement_be_called, false);
						startActivity(intent);
						finish();
						PreferenceUtil.getInstance(ProfileCompletionScreen.this).setPreference(QuopnConstants.PROFILE_COMPLETE_KEY, "YES");
					}

					Toast.makeText(this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
				} else if (response instanceof ProfileData) {
					Logger.d("");
					ProfileData interestsData = (ProfileData) response;
					if (interestsData.isError() == true) {
						mCountDownTimer.start();
						try {
							com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.dialog_title_error, interestsData.getMessage());
							dialog.show();
						} catch (Exception ex) {
							Log.e(TAG, ex.getLocalizedMessage());
						}
						mAnalysisManager.send(AnalysisEvents.OTP_FAILED);

					} else {
						mAnalysisManager.send(AnalysisEvents.OTP_VERIFIED);
						String apiKey = PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
						Gson gson = new GsonBuilder().serializeNulls().create();
						QuopnConstants.PROFILE_DATA = gson.toJson(response);
				
				/* Save the returned profile data if not saved already */
						PreferenceUtil.getInstance(this).saveProfileIfNull(QuopnConstants.PROFILE_DATA);
				/* End: Save returned profile */

						if (interestsData.getUser().getState() != null) {
							stateId = interestsData.getUser().getState();
							cityId = interestsData.getUser().getCity();
						}

						String gender = interestsData.getUser().getGender();
						String dob = interestsData.getUser().getDob();
						pre_email = interestsData.getUser().getEmailid();
						pre_registered = interestsData.getUser().getPre_registered();
						List listInterests = interestsData.getUser().getInterestedid();


						if (apiKey == null) {
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY, interestsData.getUser().getApi_key());
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY, mMobileNo);
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL, ""/*interestsData.getUser().getVideo()*/);
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_NAME, interestsData.getUser().getSender_name());
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_SENDER_PIC, interestsData.getUser().getSender_pic());
							PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY, interestsData.getUser().getNotification());
							PreferenceUtil.getInstance(this).setPreference(MainMenuFragment.TUTORIAL_USER_STATUS, interestsData.getUser().getTutorial());

							if (interestsData.getUser().getTutorial() != null && interestsData.getUser().getTutorial().equals("1")) {
								makeTutsOn();
							} else {
								makeTutsOff();
							}
						}

						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.NOTITY_STAUS_KEY, interestsData.getUser().getNotification());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PERSONAL_MESSAGE_DOWNLOADED_URL, ""/*interestsData.getUser().getVideo()*/);
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.INVITE_COUNT, interestsData.getUser().getInvite_count());

						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY, interestsData.getUser().getWalletid());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USERNAME_KEY, interestsData.getUser().getUsername());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.MOBILE_KEY, interestsData.getUser().getMobile());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.EMAIL_KEY, interestsData.getUser().getEmailid());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY, interestsData.getUser().getPIN());

						if (gender == null || dob == null || stateId == null || cityId == null ||
								listInterests == null || listInterests.size() == 0) {
							Intent intent = new Intent(this, ProfileCompletionScreen.class);
							startActivity(intent);
							finish();
						} else if (interestsData.getUser().getVideo() != null && interestsData.getUser().getVideo().length() > 0) {
							Intent gotoGiftInfo = new Intent(this, GiftInfo.class);
							startActivity(gotoGiftInfo);
							finish();
						} else {
							Intent intent = new Intent(this, MainActivity.class);
							startActivity(intent);
							finish();
						}
				
				/*if (interestsData.getUser().getGift_count() > 0) {
					Intent gotoGiftInfo = new Intent(this, GiftInfo.class);
					startActivity(gotoGiftInfo);
					finish();
				}*/
					}
				} else if (response instanceof ResendOTPData) {
					ResendOTPData registerResponse = (ResendOTPData) response;
					if (registerResponse.isError() == true) {
						mSmsAttempt = mSmsAttempt + 1;
						com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.dialog_title_error, registerResponse.getMessage());
						dialog.show();
					} else {
						mSmsAttempt = mSmsAttempt + 1;
						com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.dialog_title_resend_pin, registerResponse.getMessage());
						dialog.show();

						// make resend button invisible on click of it and show waiting for sms for 2mins
						mTextViewResendOtp.setVisibility(View.GONE);
						mSmswaitProgress.setVisibility(View.VISIBLE);
						mManualEntryText.setVisibility(View.VISIBLE);
						mCountDownTimer.start();

					}
				} else if (response instanceof OTPData) {
					Logger.d("");
					OTPData otpResponse = (OTPData) response;
					if (otpResponse.isError() == true) {
						mImgViewTick.setTag(PinVerificationTags.Failure);
						mImgViewTick.setImageResource(R.drawable.ic_error);

						// ankur
//				Validations.CustomErrorMessage(this, R.string.pin_validation,
//						mEditTextOtp, null, 0);
						com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.dialog_title_error, R.string.pin_validation);
						dialog.show();
					} else {
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY, otpResponse.getUser().getApi_key());
						PreferenceUtil.getInstance(this).setPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY, otpResponse.getUser().getWalletid());
						mEditTextOtp.setEnabled(false);
						mImgViewTick.setTag(PinVerificationTags.Success);
						mImgViewTick.setImageResource(R.drawable.ic_tick);
						mCountDownTimer.cancel();
					}
					mImgViewTick.setVisibility(View.VISIBLE);
				}
				break;
			case CONNECTION_ERROR:
				com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.server_error_title, R.string.server_error);
				dialog.show();
				//System.out.println("=========ConnectionError======");
				break;
			case PARSE_ERR0R:
				com.gc.materialdesign.widgets.Dialog dialog2 = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.server_error_title, R.string.server_error);
				dialog2.show();
				//System.out.println("=========PARSERROR======");
				break;
		}
	}

	private void makeTutsOn() {
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_DETAILS, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_LISTING, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_MYQUOPNS, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_GIFTING, QuopnConstants.TUTORIAL_ON);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_ON);
		/*
		 * two new keys added for start on and of (N for all tuts are not seen by user and 0 is the count of tuts at first)
		 * Y will denote that all seven tuts are seen by user.
		 * At present we have 7 tuts so we will increase the tuts count as user sees the tuts.
		*/
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.PREF_ALL_TUTS_SEEN, "N");
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.PREF_ALL_TUTS_COUNT, "0");
	}

	private void makeTutsOff() {
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_CAT, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_DETAILS, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_LISTING, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_CART, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_MYQUOPNS, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_GIFTING, QuopnConstants.TUTORIAL_OFF);
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.TUTORIAL_PREF_OPEN, QuopnConstants.TUTORIAL_OFF);
		/*
		 * two new keys added for start on and of (N for all tuts are not seen by user and 0 is the count of tuts at first)
		 * Y will denote that all seven tuts are seen by user.
		 * At present we have 7 tuts so we will increase the tuts count as user sees the tuts.
		*/
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.PREF_ALL_TUTS_SEEN, "Y");
		PreferenceUtil.getInstance(this).setPreference(QuopnConstants.PREF_ALL_TUTS_COUNT, "6");
	}

	public boolean updateLocationOnUI() {

		if (stateName_selected.contains("Select")) {
			Toast.makeText(this, R.string.select_state, Toast.LENGTH_SHORT).show();
			return false;
		} else if (cityName_selected.contains("Select")) {
			Toast.makeText(this, R.string.select_city, Toast.LENGTH_SHORT).show();
			return false;
		} else if (cityName_selected == null || stateName_selected == null || TextUtils.isEmpty(cityName_selected) || TextUtils.isEmpty(stateName_selected)) {
			textLocation.setText(R.string.text_selectLocation);
			return false;
		} else {
			textLocation.setText(cityName_selected + ", " + stateName_selected);
			return true;
		}
	}

	public void updateInterestsText() {
		ProfileData response = (ProfileData) gson.fromJson(
				QuopnConstants.PROFILE_DATA, ProfileData.class);
		ArrayList<InterestedId> arrayInterestedIdForInterests = (ArrayList<InterestedId>) response
				.getUser().getInterestedid();
		String interests = "";
		for (int i = 0; i < arrayInterestedIdForInterests.size(); i++) {
			InterestedId interestedId = arrayInterestedIdForInterests.get(i);
			if (interestedId.getUserunterest() == 1) {
				if (interests.equals("")) {
					interests = interestedId.getInterest();
				} else {
					interests = interests + ", " + interestedId.getInterest();
				}

			}
		}

		if (interests.equals("")) {
			interests = getResources().getString(R.string.select_interest);
		}
		textInterests.setText(interests);
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ProfileCompletionScreen Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.quopn.wallet/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	public class InterestsListAdapter extends BaseAdapter {

		private Context mContext;
		private ArrayList<InterestedId> arrayListInterests;

		public InterestsListAdapter(Context context,
									ArrayList<InterestedId> argArrayList) {
			super();
			mContext = context;
			arrayListInterests = argArrayList;

		}

		public int getCount() {
			return arrayListInterests.size();
		}

		// getView method is called for each item of ListView
		public View getView(final int position, View view, ViewGroup parent) {
			// inflate the layout for each item of listView
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listview_interests_row, null);

			// get the reference of textViews
			TextView textViewConatctNumber = (TextView) view
					.findViewById(R.id.textInterest);

			// Set the Sender number and smsBody to respective TextViews
			final InterestedId interestId = arrayListInterests.get(position);
			String interest = interestId.getInterest();
			textViewConatctNumber.setText(interest);

			final CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.checkBoxInterests);
			if (interestId.getUserunterest() == 0) {
				checkBox.setChecked(false);
			} else if (interestId.getUserunterest() == 1) {
				checkBox.setChecked(true);
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (checkBox.isChecked()) {
						checkBox.setChecked(false);
					} else {
						checkBox.setChecked(true);
					}
				}
			});

			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
											 boolean isChecked) {
					if (isChecked) {
						interestId.setUserunterest(1);
					} else {
						interestId.setUserunterest(0);
					}
				}
			});

			return view;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
	}


	public boolean isInternetAvailable() {
		return QuopnUtils.isInternetAvailable(this);
	}

	@Override
	public void onOkPressed() {

	}

	@Override
	public void onCancelPressed() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mSmsListener);
		if (mAnalysisManager != null) {
			mAnalysisManager.close();
		}
	}

	@Override
	public void onDialogDateSet(int reference, int selectedYear, int selectedMonth,
								int selectedDay) {


		Calendar cal = Calendar.getInstance();

		int currYear = cal.get(Calendar.YEAR);
		int currMonth = cal.get(Calendar.MONTH);
		int currDay = cal.get(Calendar.DATE);

		year = selectedYear;
		month = selectedMonth;
		day = selectedDay;

		GregorianCalendar gcSelected = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gcSelected.clear();
		gcSelected.set(selectedYear, selectedMonth, selectedDay);
		long selectedMillis = gcSelected.getTimeInMillis();

		GregorianCalendar gcAgeCheck = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gcAgeCheck.clear();
		gcAgeCheck.set(currYear - QuopnConstants.AGE_CONSTRAINT, currMonth, currDay);
		long ageLimitMillis = gcAgeCheck.getTimeInMillis();

		// set selected date into textview
		textDOB.setText(new StringBuilder().append(year).append("-")
				.append(month + 1).append("-").append(day).append(""));

		if (selectedMillis > ageLimitMillis) {
//	    	mHandler.sendEmptyMessage(mCodeAgeLimitError);
			return;
		}

	}

	@Override
	public void onDialogCancelled() {
		dpb = null;
	}

	public void verifyPin() {
		String otp = mEditTextOtp.getText().toString();
		if (!QuopnUtils.isInternetAvailableAndShowDialog(aContext)) {
			return;
		} else if (TextUtils.isEmpty(otp)) {
//			Validations.CustomErrorMessage(context, R.string.blank_pin_validation, mEditTextOtp, null, 0);
			mImgViewTick.setImageResource(R.drawable.ic_error);
			// ankur
			com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(aContext, R.string.dialog_title, R.string.blank_pin_validation);
			dialog.show();
			return;
		} else if (!otp.matches(QuopnConstants.PINPATTERN)) {
			mImgViewTick.setImageResource(R.drawable.ic_error);
//			Validations.CustomErrorMessage(getApplicationContext(), R.string.pin_validation, mEditTextOtp, null, 0);
			Dialog dialog = new com.gc.materialdesign.widgets.Dialog(aContext, R.string.dialog_title, R.string.pin_validation);
			dialog.show();
			return;
		} else {
			mProgressDialog.show();
//			mCountDownTimer.cancel();

			Map<String, String> params = new HashMap<String, String>();
			params.put("userid", mUserId);
			params.put("pin", mEditTextOtp.getText().toString());
			params.put("mobileno", mMobileNo);
			params.put("utm_source", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_SOURCE) + "");
			params.put("utm_content", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CONTENT) + "");
			params.put("x-session", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.SESSION_ID) + "");
			params.put("utm_campaign", PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.UTM_CAMPAIGN) + "");
			params.put("device_id", QuopnConstants.android_id);
			params.put("version_name", QuopnConstants.versionName);
			params.put("version_code", "" + QuopnConstants.versionCode);
			params.put("brand", Build.BRAND);
			params.put("device", Build.DEVICE);
			params.put("model", Build.MODEL);
			params.put("build", Build.ID);
			params.put("product", Build.PRODUCT);
			params.put("sdk", Build.VERSION.SDK);
			params.put("release", Build.VERSION.RELEASE);
			params.put("increment", Build.VERSION.INCREMENTAL);

			ConnectionFactory connectionFactory = new ConnectionFactory(this, this);
			connectionFactory.setPostParams(params);
			connectionFactory.createConnection(QuopnConstants.OTP_CODE);

		}
	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			mTextViewResendOtp.setVisibility(View.VISIBLE);
			mManualEntryText.setVisibility(View.GONE);
			mSmswaitProgress.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
//			mProgressText.setText(" "+millisUntilFinished / 1000 + " secs" );
			String format = String.format(getResources().getString(R.string.waiting_for_sms), millisUntilFinished / 1000);
			mProgressText.setText(format);

		}
	}

//	class StatecityListner extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			System.out.println("=====ProfileCompletionScreen=======BroadCastReciver=111111====");
//			if(intent.getAction().equals(QuopnConstants.BROADCAST_UPDATE_STATE_CITY)){
//				System.out.println("=====ProfileCompletionScreen=======BroadCastReciver=22222====");
//			}
//		}
//	}


	class SmsListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {
				Bundle bundle = intent.getExtras(); // ---get the SMS
				// message passed
				SmsMessage[] msgs = null;
				String msg_from;
				if (bundle != null) {
					// ---retrieve the SMS message received---
					try {
						Object[] pdus = (Object[]) bundle.get("pdus");
						msgs = new SmsMessage[pdus.length];
						for (int i = 0; i < msgs.length; i++) {
							msgs[i] = SmsMessage
									.createFromPdu((byte[]) pdus[i]);
							msg_from = msgs[i].getOriginatingAddress();
							if (msg_from.contains("mQUOPN")) {
								String msgBody = msgs[i].getMessageBody();
								Message msg = Message.obtain();
								msgBody = msgBody.replaceAll("[^-?0-9]+", "");
								if (msgBody.length() == 4) {
									msg.what = RESPONSE_SUCCESS_MESSAGE;
									Bundle b = new Bundle();
									b.putString("message", msgBody);
									msg.setData(b);
									mHandler.sendMessage(msg);
									break;
								}
							}
						}
					} catch (Exception e) {
						// Log.d("Exception caught",e.getMessage());
					}
				}
			}
		}

	}

	private interface PinVerificationTags {
		Object Pending = "Pending";
		Object Success = "Success";
		Object Failure = "Failure";
	}


	@Override
	public void onTimeout(ConnectRequest request) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(ProfileCompletionScreen.this, R.string.slow_internet_connection_title, R.string.slow_internet_connection);
				dialog.show();
			}
		});
	}

	@Override
	public void myTimeout(String requestURL) {

	}
}
