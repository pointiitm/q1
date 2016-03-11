package com.quopn.wallet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment.DatePickerDialogHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quopn.wallet.MainActivity;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.CityAdapter;
import com.quopn.wallet.adapter.StateAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.ConProvider;
import com.quopn.wallet.data.ITableData;
import com.quopn.wallet.data.model.InterestedId;
import com.quopn.wallet.data.model.ProfileData;
import com.quopn.wallet.data.model.RegisterData;
import com.quopn.wallet.data.model.User;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.utils.Validations;
import com.quopn.wallet.views.CustomProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import eu.janmuller.android.simplecropimage.CropImage;

public class ProfileFragment extends BaseFragment implements ConnectionListener, DatePickerDialogHandler, OnClickListener {

    private TextView textLocation;
    private TextView textInterests;
    // keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    // keep track of gallery capture intent
    final int GALLERY_CAPTURE = 2;
    // keep track of cropping intent
    final int PIC_CROP = 3;
    private int previousRequestCode = 0;
    // captured picture uri
    private Uri picUri;

    // Activity request codes
    private static final int CROP_IMAGE_REQUEST_CODE = 150;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private ImageView imgProfilePic;
    private RelativeLayout relLaySave;
    private ImageView imgViewSave;
    private TextView buttonSaveProfile;
    private EditText editName;
    private EditText editEmailID;

    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoader mImageLoader;
    private Spinner spinnerCity;
    //	private ProgressDialog barProgressDialog;
    private TextView textDOB;
    private CustomProgressDialog mProgressDialog;
    private Handler mHandler;

    private ConnectionFactory mConnectionFactory;
    private Map<String, String> params;
    private Map<String, String> headerParams;

    private int selectedCityId;
    private int selectedStateId;

//	private int year = 1990;
//	private int month = 0;
//	private int day = 01;


    private Calendar cal = Calendar.getInstance();

    private int year = cal.get(Calendar.YEAR);
    private int month = cal.get(Calendar.MONTH);
    private int day = cal.get(Calendar.DATE);
    private int mCodeAgeLimitError = 100;


    //	private List<StateData> stateData;
//	private List<CityData> cityData;
    private String stateId = "0";
    private String cityId = "0";
    private String stateId_selected = "0";
    private String cityId_selected = "0";
    //	private String stateName="";
//	private String cityName="";
    private String stateName_selected = "";
    private String cityName_selected = "";
    private String dob;

    private String TAG = "Quopn/PROFILE FRAGMENT";

    private AnalysisManager mAnalysisManager;
    final Gson gson = new GsonBuilder().serializeNulls().create();

    private ImageView mImgGender;
    private Object tagDefault;
    private Object tagMale = "Male";
    private Object tagFemale = "Female";
    String profilePicPath = "";
    private MainActivity mMainActivity;
    private DatePickerBuilder dpb;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profile, null);

        mAnalysisManager = ((QuopnApplication) mMainActivity.getApplicationContext()).getAnalysisManager();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mMainActivity));

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(0))
                .build();

//		getStateList();
        mProgressDialog = new CustomProgressDialog(mMainActivity);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == mCodeAgeLimitError) {
                    Resources res = getResources();
                    String errorText = String.format(res.getString(R.string.age_validation),
                            QuopnConstants.AGE_CONSTRAINT);
                    Toast.makeText(mMainActivity, errorText, Toast.LENGTH_LONG).show();
                }
            }
        };

        if (TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
            QuopnConstants.PROFILE_DATA = PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA);
        }

        ProfileData response = (ProfileData) gson.fromJson(
                QuopnConstants.PROFILE_DATA, ProfileData.class);

        relLaySave = (RelativeLayout) rootView.findViewById(R.id.relLaySave);
        imgViewSave = (ImageView) rootView.findViewById(R.id.imgViewSave);
        buttonSaveProfile = (TextView) rootView.findViewById(R.id.buttonSave);

        relLaySave.setOnClickListener(this);
        imgViewSave.setOnClickListener(this);
        buttonSaveProfile.setOnClickListener(this);

        imgProfilePic = (ImageView) rootView
                .findViewById(R.id.profile_pic);

        setImage();
        imgProfilePic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                relLaySave.setVisibility(View.VISIBLE);
                QuopnConstants.PROFILE_SAVE_FLAG = false;
                final Dialog dialog = new Dialog(mMainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_profile_pic);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setTitle("Select");
                dialog.show();

                Button buttonCamera = (Button) dialog
                        .findViewById(R.id.buttonCamera);
                buttonCamera.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        previousRequestCode = CAMERA_CAPTURE;
                        try {
                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            picUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);

                            if (intent.resolveActivity(mMainActivity.getPackageManager()) == null) {
                                Toast.makeText(mMainActivity,
                                        "There are no applications to handle your request",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                startActivityForResult(intent, CAMERA_CAPTURE);
                            }

                            dialog.dismiss();
                        } catch (ActivityNotFoundException anfe) {
                            // display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                            com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, errorMessage);
                            dialog.show();

                        }
                    }
                });

                Button buttonGallery = (Button) dialog
                        .findViewById(R.id.buttonGallery);
                buttonGallery.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        previousRequestCode = GALLERY_CAPTURE;
                        try {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            if (i.resolveActivity(mMainActivity.getPackageManager()) == null) {
                                Toast.makeText(mMainActivity,
                                        "There are no applications to handle your request",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                startActivityForResult(i, GALLERY_CAPTURE);
                            }

                            dialog.dismiss();
                        } catch (ActivityNotFoundException anfe) {
                            // display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                            com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, errorMessage);
                            dialog.show();

                        }
                    }
                });
            }
        });


        TextView textID = (TextView) rootView
                .findViewById(R.id.textViewId);
        textID.setTypeface(null, Typeface.BOLD);

        TextView textPin = (TextView) rootView
                .findViewById(R.id.textViewPin);
        textPin.setTypeface(null, Typeface.BOLD);

        TextView textWalletID = (TextView) rootView
                .findViewById(R.id.textWalletID);
        textWalletID.setText(PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));
        textWalletID.setTypeface(null, Typeface.BOLD);

        TextView textWalletPin = (TextView) rootView
                .findViewById(R.id.textWalletPin);
        textWalletPin.setText(" " + PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY));
        textWalletPin.setTypeface(null, Typeface.BOLD);

//		Log.d(TAG, "VAIBHAV IN ProfileFragment onCreateView() + "
//				+ QuopnConstants.WALLET_ID);


        editName = (EditText) rootView
                .findViewById(R.id.editName);
        editName.setText("" + response.getUser().getUsername());

        editName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                relLaySave.setVisibility(View.VISIBLE);
                QuopnConstants.PROFILE_SAVE_FLAG = false;
            }
        });
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setError(null);
                relLaySave.setVisibility(View.VISIBLE);
                QuopnConstants.PROFILE_SAVE_FLAG = false;
            }
        });

        EditText editMobileNumber = (EditText) rootView.findViewById(R.id.textMobileNumber);
        editMobileNumber.setText(response.getUser().getMobile());
        editMobileNumber.setKeyListener(null);

        final RadioButton radioBtnMale = (RadioButton) rootView.findViewById(R.id.gen_male);
        radioBtnMale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                relLaySave.setVisibility(View.VISIBLE);

            }
        });

        final RadioButton radioBtnFemale = (RadioButton) rootView.findViewById(R.id.gen_female);
        radioBtnFemale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                relLaySave.setVisibility(View.VISIBLE);
            }
        });

        String gender = response.getUser().getGender();
        tagDefault = getResources().getString(R.string.gender_na);
        mImgGender = (ImageView) rootView.findViewById(R.id.imgGender);

        if (gender != null) {
            if (gender.toLowerCase().startsWith("m")) {
                mImgGender.setImageResource(R.drawable.male_on);
                mImgGender.setTag(tagMale);
            } else if (gender.toLowerCase().startsWith("f")) {
                mImgGender.setImageResource(R.drawable.female_on);
                mImgGender.setTag(tagFemale);
            }
        }

		/*mImgGender.setOnTouchListener(new Utils.OnSwipeTouchListener(getActivity()) {
		    @Override
		    public void onSwipeLeft() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.female_on);
				mImgGender.setTag(tagFemale);
				relLaySave.setVisibility(View.VISIBLE);
		    }
		    
		    @Override
		    public void onSwipeRight() {
		    	Object tag = mImgGender.getTag();
		    	mImgGender.setImageResource(R.drawable.male_on);
				mImgGender.setTag(tagMale);
				relLaySave.setVisibility(View.VISIBLE);
		    }
		});*/

        mImgGender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				buttonSaveProfile.setVisibility(View.VISIBLE);
                relLaySave.setVisibility(View.VISIBLE);
                Object tag = mImgGender.getTag();
                if (tag.equals(tagDefault) || tag.equals(tagFemale)) {
                    mImgGender.setImageResource(R.drawable.male_on);
                    mImgGender.setTag(tagMale);
                } else if (tag.equals(tagMale)) {
                    mImgGender.setImageResource(R.drawable.female_on);
                    mImgGender.setTag(tagFemale);
                }
            }
        });

        textDOB = (TextView) rootView.findViewById(R.id.textDOB);
        dob = response.getUser().getDob();
        textDOB.setText(dob);
        if (dob != null) {
            splitDOB();
        }

        textDOB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                relLaySave.setVisibility(View.VISIBLE);
                if (dpb == null) {
                    dpb = new DatePickerBuilder();
                    dpb.setFragmentManager(mMainActivity.getSupportFragmentManager())
                            .setStyleResId(R.style.BetterPickersDialogFragment);

                    dpb.show();
                    dpb.addDatePickerDialogHandler(ProfileFragment.this);
                }

            }

        });

        editEmailID = (EditText) rootView.findViewById(R.id.editEmailID);
        editEmailID.setText(response.getUser().getEmailid());


        editEmailID.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                relLaySave.setVisibility(View.VISIBLE);
                QuopnConstants.PROFILE_SAVE_FLAG = false;

            }
        });

        editEmailID.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editEmailID.setError(null);
            }
        });

        stateId = response.getUser().getState();
        cityId = response.getUser().getCity();

        textLocation = (TextView) rootView.findViewById(R.id.textLocation);
        updateLocationOnUI();

        textLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {


                    relLaySave.setVisibility(View.VISIBLE);
                    QuopnConstants.PROFILE_SAVE_FLAG = false;

                    final Dialog state_city_dialog = new Dialog(mMainActivity);
                    state_city_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    state_city_dialog.setContentView(R.layout.dialog_location);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = state_city_dialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    //This makes the dialog take up the full width
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);
//				dialog.setTitle("Select Location");

                    spinnerCity = (Spinner) state_city_dialog
                            .findViewById(R.id.spinnerCity);

                    Cursor city_cursor = mMainActivity.getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? ", new String[]{stateId}, null);
                    city_cursor.getCount();
                    CityAdapter cityAdapter = new CityAdapter(mMainActivity, R.layout.location_spinner, QuopnUtils.convertCityCursorToList(city_cursor));
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    spinnerCity.setAdapter(cityAdapter);
                    spinnerCity
                            .setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> adapter,
                                                           View view, int position, long id) {
                                    if (view != null) {
                                        cityId_selected = (String) ((TextView) view)
                                                .getTag();
                                        cityName_selected = ((TextView) view)
                                                .getText().toString();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });

                    final Spinner spinnerState = (Spinner) state_city_dialog
                            .findViewById(R.id.spinnerState);

                    int stateIndex = 0;
                    Cursor state_cursor = mMainActivity.getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, null, null, null);
                    StateAdapter stateAdapter = new StateAdapter(mMainActivity, R.layout.location_spinner, QuopnUtils.convertStateCursorToList(state_cursor));
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

                                        Cursor city_cursor = mMainActivity.getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? OR " + ITableData.TABLE_CITIES.COLUMN_STATE_ID + " = ? ", new String[]{QuopnConstants.SELECT_STATE_ID, stateId_selected}, null);
                                        city_cursor.getCount();
                                        CityAdapter cityAdapter = new CityAdapter(mMainActivity, R.layout.location_spinner, QuopnUtils.convertCityCursorToList(city_cursor));
                                        spinnerCity.setAdapter(cityAdapter);
                                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapter) {
                                    System.out
                                            .println("onNothingSelected() State : ");
                                }
                            });


                    Button buttonSaveLocation = (Button) state_city_dialog
                            .findViewById(R.id.buttonSave);


                    buttonSaveLocation.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (updateLocationOnUI_afterSave() && state_city_dialog.isShowing()) {
                                state_city_dialog.dismiss();
//								cityName = cityName_selected;
//								stateName = stateName_selected;
                                cityId = cityId_selected;
                                stateId = stateId_selected;
                            }

                        }
                    });

                    state_city_dialog.show();
                } else {
                    com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_error, R.string.please_connect_to_internet);
                    dialog.show();
                }
            }
        });

        textInterests = (TextView) rootView.findViewById(R.id.textInterests);
        updateInterestsText();

        textInterests.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textInterests.getError() != null) {
                    textInterests.setError(null);
                }
                relLaySave.setVisibility(View.VISIBLE);
                final Dialog dialog = new Dialog(mMainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_interests);

                ProfileData response = (ProfileData) gson.fromJson(
                        QuopnConstants.PROFILE_DATA, ProfileData.class);

                final ArrayList<InterestedId> arrayInterestedId = (ArrayList<InterestedId>) response
                        .getUser().getInterestedid();
                InterestsListAdapter interestsListAdapter = new InterestsListAdapter(
                        mMainActivity, arrayInterestedId);

                ListView listView = (ListView) dialog
                        .findViewById(R.id.listInterests);
                listView.setAdapter(interestsListAdapter);

                Button buttonSaveInterests = (Button) dialog
                        .findViewById(R.id.buttonSave);
                buttonSaveInterests
                        .setOnClickListener(new View.OnClickListener() {

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
                                    com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_error, R.string.please_connect_to_internet);
                                    dialog.show();
                                }
                            }
                        });

                dialog.show();
            }
        });

		
		/*buttonSaveProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isInternetAvailable()) {
					String name = editName.getText().toString().trim();
					if (TextUtils.isEmpty(name)) {
						Validations.CustomErrorMessage(getActivity(),
								R.string.name_validation, editName, null,
								0);
						return;
					}
					
					if (!radioBtnMale.isChecked() && !radioBtnFemale.isChecked()) {
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.gender_validation),
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					Object tagGender = mImgGender.getTag();
					String gender = "";
					if (radioBtnMale.isChecked()) {
						gender = getResources().getString(
								R.string.gen_male_text).toLowerCase();
					} else if (radioBtnFemale.isChecked()) {
						gender = getResources().getString(
								R.string.gen_female_text).toLowerCase();
					}
					if(tagGender.equals(tagDefault)){
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.gender_validation),
								Toast.LENGTH_SHORT).show();
						return;
					} else if(tagGender.equals(tagMale)) {
						gender = getResources().getString(R.string.gen_male_text).toLowerCase();
					} else if(tagGender.equals(tagFemale)){
						gender = getResources().getString(R.string.gen_female_text).toLowerCase();
					}
					
					String dob = textDOB.getText().toString();
					if (TextUtils.isEmpty(dob)) {
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.dob_validation),
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					
					String emailid = editEmailID.getText().toString();
					if(TextUtils.isEmpty(emailid)){
						Validations.CustomErrorMessage(getActivity(),
								R.string.emailid_validation, editEmailID, null,
								0);
						return;
					}
					if (emailid.length() > 0) {
						if (!Patterns.EMAIL_ADDRESS.matcher(emailidQuopnConstants.EMAILPATTERN).matches()) {
							Validations.CustomErrorMessage(getActivity(),
									R.string.entered_emailid_validation, editEmailID,
									null, 0);
							return;
						}
					}

					editEmailID.clearFocus();
					
					String interests = textInterests.getText().toString();
					if(interests.equals(getResources().getText(R.string.selec_interests)) || TextUtils.isEmpty(interests)){
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.interest_validation),
								Toast.LENGTH_SHORT).show();
						Validations.CustomErrorMessage(getActivity(), R.string.interest_validation, 
								null, textInterests, 1);
						return;
					}
					
					System.out
							.println("VAIBHAV PRINTING INTERESTS buttonSave Clicked() 1 : "
									+ QuopnConstants.PROFILE_DATA);
					ProfileData response = (ProfileData) gson.fromJson(
							QuopnConstants.PROFILE_DATA, ProfileData.class);

					response.getUser().setUsername(
							editName.getText().toString().trim());
					response.getUser().setEmailid(
							editEmailID.getText().toString());
					response.getUser().setGender(gender);
					response.getUser().setDob(dob);
					response.getUser().setCity(String.valueOf(cityId));
					response.getUser().setState(String.valueOf(stateId));
					QuopnConstants.PROFILE_DATA = gson.toJson(response);
					PreferenceUtil.getInstance(getActivity()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA, QuopnConstants.PROFILE_DATA);
					
					updateProfile();

					relLaySave.setVisibility(View.INVISIBLE);

					System.out
							.println("VAIBHAV PRINTING INTERESTS buttonSave Clicked() 2 : "
									+ QuopnConstants.PROFILE_DATA);
					
					System.out
					.println("VAIBHAV PRINTING INTERESTS buttonSave Clicked() 3 : "
							+ stateId + " : " + cityId);
				} else {
					 AlertManager.show(new DialogConfigData(getActivity(),DialogConfigData.TYPE_ERROR,R.string.dialog_title_error,R.string.please_connect_to_internet));

				}
			}

		});*/


        rootView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//					Toast.makeText(getActivity(), "BAK PRESSED",
//							Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        return rootView;
    }

    private void setImage() {
        ProfileData response = (ProfileData) gson.fromJson(
                QuopnConstants.PROFILE_DATA, ProfileData.class);
        String imageUrl = response.getUser().getPic();
        String imagePath = PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_IMAGE_PATH);
        if (imageUrl != null || !TextUtils.isEmpty(imageUrl) && QuopnUtils.isInternetAvailable(mMainActivity)) {
            mImageLoader.displayImage(imageUrl, imgProfilePic, mDisplayImageOptions, null);
        } else if (imagePath != null) {
            loadImageFromStorage(imagePath);//LOCAL IMAGE FROM INTERNAL STORAGE
        }

    }

    public void getProfile() {
        Log.v(TAG, "*****Getting User Profile Data*****");
        String api_key = PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(PreferenceUtil.SHARED_PREF_KEYS.API_KEY);
        if (!TextUtils.isEmpty(api_key)) {

            params = new HashMap<String, String>();
            params.put(QuopnApi.ParamKey.AUTHORIZATION, api_key);
            mConnectionFactory = new ConnectionFactory(mMainActivity, this);
            mConnectionFactory.setHeaderParams(params);
            mConnectionFactory.createConnection(QuopnConstants.PROFILE_GET_CODE);
        } else {
            // show error
        }
    }

    public void splitDOB() {
        String[] arrDOB = dob.split("-");
        year = Integer.parseInt(arrDOB[0]);
        month = Integer.parseInt(arrDOB[1]);
        month = month - 1;
        day = Integer.parseInt(arrDOB[2]);
    }

    public void updateProfile() {
        try {
            if (isInternetAvailable()) {
                Log.v("", "*****Profile Updating*****");
                User mUser = new User();
                mUser.setApi_key(PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).getPreference(
                        PreferenceUtil.SHARED_PREF_KEYS.API_KEY));
//            Log.v("","API Key : "+mUser.getApi_key());
                headerParams = new HashMap<String, String>();
                headerParams.put("Authorization", mUser.getApi_key());

                ProfileData response = (ProfileData) gson.fromJson(
                        QuopnConstants.PROFILE_DATA, ProfileData.class);

                mUser.setUserid(response.getUser().getUserid());
                mUser.setUsername(URLEncoder.encode(response.getUser().getUsername(), "UTF-8"));
                mUser.setEmailid(URLEncoder.encode(response.getUser().getEmailid(), "UTF-8"));
                mUser.setGender(URLEncoder.encode(response.getUser().getGender(), "UTF-8"));
                mUser.setDob(URLEncoder.encode(response.getUser().getDob(), "UTF-8"));
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
                params.put("screenid", "2");

                mAnalysisManager.send(AnalysisEvents.PROFILE_CHANGED);

                ConnectionFactory mConnectionFactory = new ConnectionFactory(mMainActivity, this);
                mConnectionFactory.setHeaderParams(headerParams);
                mConnectionFactory.setPostParams(params);
                mConnectionFactory.createConnection(QuopnConstants.PROFILE_UPDATE_CODE);

            } else {
                com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_error, R.string.please_connect_to_internet);
                dialog.show();
            }
        } catch (Exception ex) {

        }
    }


    public boolean isInternetAvailable() {
        return QuopnUtils.isInternetAvailable(mMainActivity);
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if (response instanceof RegisterData) {
            RegisterData registerResponse = (RegisterData) response;
            if (registerResponse.getError() == true) {
                Toast.makeText(mMainActivity, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                splitDOB();
                textDOB.setText(dob);
            } else {
                saveProfileImagePath(profilePicPath);
                Toast.makeText(mMainActivity, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_STATE, stateId); //commented 11022015
                PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.USER_CITY, cityId);
                getProfile();
            }
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
            interests = getResources().getString(R.string.selec_interests);
        }
        textInterests.setText(interests);
    }

    /*
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        try {
            return Uri.fromFile(getOutputMediaFile(type));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;

    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                QuopnConstants.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//				Log.d(QuopnConstants.IMAGE_DIRECTORY_NAME,
//						"Oops! Failed create "
//								+ QuopnConstants.IMAGE_DIRECTORY_NAME
//								+ " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

//	@Override
//	public void onResume() {
//		super.onResume();
//		buttonSaveProfile.setVisibility(View.INVISIBLE);
//	}

    private void startCropImage(Uri picUri) {
        try {

            if (picUri.toString().contains("file")) {
                String path = picUri.toString(); // "file:///mnt/sdcard/FileName.mp3"
                File file = new File(new URI(path));
                Intent intent = new Intent(mMainActivity, CropImage.class);
                intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
                intent.putExtra(CropImage.SCALE, true);

                intent.putExtra(CropImage.ASPECT_X, 7);
                intent.putExtra(CropImage.ASPECT_Y, 7);

                startActivityForResult(intent, PIC_CROP);
            } else {
                String path = getRealPathFromURI(picUri).toString(); // "file:///mnt/sdcard/FileName.mp3"
                path = path.replace(" ", "%20");
                if (path.contains("WhatsApp")) {
                    File file = new File(new URI("file://" + path));
                    Intent intent = new Intent(mMainActivity, CropImage.class);
                    intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
                    intent.putExtra(CropImage.SCALE, true);

                    intent.putExtra(CropImage.ASPECT_X, 7);
                    intent.putExtra(CropImage.ASPECT_Y, 7);

                    startActivityForResult(intent, PIC_CROP);
                } else {
                    File file = new File(new URI("file:///" + path));
                    Intent intent = new Intent(mMainActivity, CropImage.class);
                    intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
                    intent.putExtra(CropImage.SCALE, true);

                    intent.putExtra(CropImage.ASPECT_X, 7);
                    intent.putExtra(CropImage.ASPECT_Y, 7);

                    startActivityForResult(intent, PIC_CROP);
                }
            }


        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = mMainActivity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d(TAG, "onActivityResult() : " + requestCode + " : " + resultCode);
        if (resultCode == mMainActivity.RESULT_OK) {
            // user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                // carry out the crop operation
//				performCrop();
                startCropImage(picUri);
            } else if (requestCode == GALLERY_CAPTURE) {
                picUri = data.getData();
//				performCrop();
                startCropImage(picUri);
            }
            // user is returning from cropping the image
//			else if (requestCode == PIC_CROP) {
//				// get the returned data
//				Bundle extras = data.getExtras();
//				// get the cropped bitmap
//				if(extras == null){
//					com.gc.materialdesign.widgets.Dialog dialog=new  com.gc.materialdesign.widgets.Dialog(getActivity(), ""/*R.string.dialog_title_registration*/,R.string.image_capture_failed);
//					dialog.show();
//				} else{
////					Bitmap thePic = data.getParcelableExtra(CropImage.IMAGE_PATH);//extras.getParcelable("data");
//					
//					Uri imageUri = data.getParcelableExtra(CropImage.IMAGE_PATH);
//			        Bitmap thePic;
//					try {
//						thePic = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri/*picUri*/);
//						profilePicPath = saveToInternalSorage(thePic);
////						saveProfileImagePath(profilePicPath);
//						loadImageFromStorage(profilePicPath);
//						
//						ByteArrayOutputStream bao = new ByteArrayOutputStream();
//						Bitmap bitmap = ((BitmapDrawable) imgProfilePic.getDrawable())
//						.getBitmap();
//						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao); 
//						byte[] ba = bao.toByteArray(); byte[] picByte = Base64.encode(ba, 0);
//						  
//						QuopnConstants.PROFILE_PIC_DATA = new String(picByte);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					
//				}
//				
//			}

            else if (requestCode == PIC_CROP) {
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {
                    com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_capture_failed);
                    dialog.show();
                } else {
                    Bitmap thePic = BitmapFactory.decodeFile(path);
                    profilePicPath = saveToInternalSorage(thePic);
//						saveProfileImagePath(profilePicPath);
                    loadImageFromStorage(profilePicPath);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable) imgProfilePic.getDrawable())
                            .getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    byte[] ba = bao.toByteArray();
                    byte[] picByte = Base64.encode(ba, 0);

                    QuopnConstants.PROFILE_PIC_DATA = new String(picByte);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == CAMERA_CAPTURE) {
                com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_capture_cancelled);
                dialog.show();

            } else if (requestCode == GALLERY_CAPTURE) {
                com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_selection_cancelled);
                dialog.show();
            } else if (requestCode == PIC_CROP) {
                if (previousRequestCode == CAMERA_CAPTURE) {
                    com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_capture_cancelled);
                    dialog.show();
                } else if (previousRequestCode == GALLERY_CAPTURE) {
                    com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_selection_cancelled);
                    dialog.show();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_profile, R.string.image_capture_failed);
            dialog.show();

        }

    }

    public void saveProfileImagePath(String argImagePath) {
        PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_IMAGE_PATH, argImagePath);
    }

    private String saveToInternalSorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(mMainActivity);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(QuopnConstants.PROFILE_IMG_FOLDER, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, QuopnConstants.PROFILE_IMG_NAME);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "saveToInternalSorage() : " + directory.getAbsolutePath());                                                     
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, QuopnConstants.PROFILE_IMG_NAME);

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//	        imgProfilePic.setImageBitmap(QuopnUtils.getCircleBitmap(b));
            imgProfilePic.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper method to carry out crop operation
     */
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("circleCrop", "circleCrop");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            if (cropIntent.resolveActivity(mMainActivity.getPackageManager()) == null) {
                Toast.makeText(mMainActivity,
                        "There are no applications to handle your request",
                        Toast.LENGTH_LONG).show();
            } else {
                startActivityForResult(cropIntent, PIC_CROP);
            }
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast
                    .makeText(mMainActivity, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
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

            final String interestText = ((TextView) view
                    .findViewById(R.id.textInterest)).getText().toString();

            final CheckBox checkBox = (CheckBox) view
                    .findViewById(R.id.checkBoxInterests);
            if (interestId.getUserunterest() == 0) {
                checkBox.setChecked(false);
            } else if (interestId.getUserunterest() == 1) {
                checkBox.setChecked(true);
            }

            view.setOnClickListener(new View.OnClickListener() {

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
                    System.out
                            .println("onCheckedChanged() : "
                                    + isChecked);
                    System.out
                            .println("onCheckedChanged() : "
                                    + interestText);
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

    public void updateLocationOnUI() {
        ProfileData response = (ProfileData) gson.fromJson(
                QuopnConstants.PROFILE_DATA, ProfileData.class);
        Cursor city_cursor = mMainActivity.getContentResolver().query(ConProvider.CONTENT_URI_CITIES, null, ITableData.TABLE_CITIES.COLUMN_CITY_ID + " = ? ", new String[]{response.getUser().getCity()}, null);
        city_cursor.moveToFirst();
        Cursor state_cursor = mMainActivity.getContentResolver().query(ConProvider.CONTENT_URI_STATES, null, ITableData.TABLE_STATES.COLUMN_STATE_ID + " = ? ", new String[]{response.getUser().getState()}, null);
        state_cursor.moveToFirst();

        String city = city_cursor.getString(city_cursor.getColumnIndex(ITableData.TABLE_CITIES.COLUMN_CITY_NAME));
        String state = state_cursor.getString(state_cursor.getColumnIndex(ITableData.TABLE_STATES.COLUMN_STATE_NAME));

        if (city == null || state == null || TextUtils.isEmpty(city) || TextUtils.isEmpty(state)) {
            textLocation.setText(R.string.text_selectLocation);
        } else {
            textLocation.setText(city + ", " + state);
        }
    }

    public boolean updateLocationOnUI_afterSave() {

        if (stateName_selected.contains("Select")) {
            Toast.makeText(mMainActivity, R.string.select_state, Toast.LENGTH_SHORT).show();
            return false;
        } else if (cityName_selected.contains("Select")) {
            Toast.makeText(mMainActivity, R.string.select_city, Toast.LENGTH_SHORT).show();
            return false;
        } else if (cityName_selected == null || stateName_selected == null || TextUtils.isEmpty(cityName_selected) || TextUtils.isEmpty(stateName_selected)) {
            textLocation.setText(R.string.text_selectLocation);
            return false;
        } else {
            textLocation.setText(cityName_selected + ", " + stateName_selected);
            return true;
        }


    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onBackPressed(FragmentActivity activity) {
        super.onBackPressed(activity);
        activity.getSupportFragmentManager().beginTransaction()
                .remove(this).commit();
        Fragment menu = new MainMenuFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.menu_frame, menu)
                .commit();
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
            mHandler.sendEmptyMessage(mCodeAgeLimitError);
            return;
        }
        dpb = null;
    }

    @Override
    public void onDialogCancelled() {
        dpb = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.relLaySave || v.getId() == R.id.imgViewSave || v.getId() == R.id.buttonSave) {
            if (isInternetAvailable()) {
                String name = editName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Validations.CustomErrorMessage(mMainActivity,
                            R.string.blank_name_validation, editName, null,
                            0);
                    return;
                } else if (!Validations.isValidName(name)) {
                    Validations.CustomErrorMessage(mMainActivity,
                            R.string.name_validation, editName, null,
                            0);
                    return;
                }
				
				/*if (!radioBtnMale.isChecked() && !radioBtnFemale.isChecked()) {
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.gender_validation),
							Toast.LENGTH_SHORT).show();
					return;
				}*/

                Object tagGender = mImgGender.getTag();
                String gender = "";
				/*if (radioBtnMale.isChecked()) {
					gender = getResources().getString(
							R.string.gen_male_text).toLowerCase();
				} else if (radioBtnFemale.isChecked()) {
					gender = getResources().getString(
							R.string.gen_female_text).toLowerCase();
				}*/
                if (tagGender.equals(tagDefault)) {
                    Toast.makeText(
                            mMainActivity,
                            getResources().getString(
                                    R.string.gender_validation),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (tagGender.equals(tagMale)) {
                    gender = getResources().getString(R.string.gen_male_text).toLowerCase();
                } else if (tagGender.equals(tagFemale)) {
                    gender = getResources().getString(R.string.gen_female_text).toLowerCase();
                }

                String dob = textDOB.getText().toString();
                if (TextUtils.isEmpty(dob)) {
                    Toast.makeText(
                            mMainActivity,
                            getResources().getString(
                                    R.string.dob_validation),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String emailid = editEmailID.getText().toString();
                emailid = QuopnUtils.trimString(emailid);
                if (TextUtils.isEmpty(emailid)) {
                    Validations.CustomErrorMessage(mMainActivity,
                            R.string.emailid_validation, editEmailID, null,
                            0);
                    return;
                } else if (!Validations.isValidEmail(emailid)) {
                    Validations.CustomErrorMessage(mMainActivity,
                            R.string.entered_emailid_validation, editEmailID, null,
                            0);
                    return;
                }
				/*if(TextUtils.isEmpty(emailid)){
					Validations.CustomErrorMessage(getActivity(),
							R.string.emailid_validation, editEmailID, null,
							0);
					return;
				}*/
//				if (emailid.length() > 0) {
//					if (!Patterns.EMAIL_ADDRESS.matcher(emailid/*QuopnConstants.EMAILPATTERN*/).matches()) {
//						Validations.CustomErrorMessage(mMainActivity,
//								R.string.entered_emailid_validation, editEmailID,
//								null, 0);
//						return;
//					}
//				}

                editEmailID.clearFocus();

//				String interests = textInterests.getText().toString();
				/*if(interests.equals(getResources().getText(R.string.selec_interests)) || TextUtils.isEmpty(interests)){
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.interest_validation),
							Toast.LENGTH_SHORT).show();
					Validations.CustomErrorMessage(getActivity(), R.string.interest_validation, 
							null, textInterests, 1);
					return;
				}*/

                ProfileData response = (ProfileData) gson.fromJson(
                        QuopnConstants.PROFILE_DATA, ProfileData.class);

                response.getUser().setUsername(
                        editName.getText().toString().trim());
                response.getUser().setEmailid(
                        editEmailID.getText().toString());
                response.getUser().setGender(gender);
                response.getUser().setDob(dob);
                response.getUser().setCity(String.valueOf(cityId));
                response.getUser().setState(String.valueOf(stateId));
                QuopnConstants.PROFILE_DATA = gson.toJson(response);
                PreferenceUtil.getInstance(mMainActivity.getApplicationContext()).setPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA, QuopnConstants.PROFILE_DATA);

                updateProfile();

                relLaySave.setVisibility(View.INVISIBLE);


            } else {
                com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(mMainActivity, R.string.dialog_title_error, R.string.please_connect_to_internet);
                dialog.show();
            }
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void myTimeout(String requestTag) {

    }

}
