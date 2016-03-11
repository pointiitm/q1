/**
 * Sep 5, 2014
 * @author{Sandeep}
 */
package com.quopn.wallet.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

	private static Drawable errorIcon;
	private static String errorMessage;
	public static final int DIALOG_SUCCESS=1; 
	public static final int DIALOG_ERROR=0;
	public static final int DIALOG_CONFIRM=2;
	public static final int ACCOUNTNO_MAX=19;
	public static final int ACCOUNTHOLDER_MAX=50;
	private static OnClickListener mDialogDefaultOkClickListner,mDialogDefaultCancleClickListner;
//	public static Dialog dialog;

	public static void CustomErrorMessage(Context context, int errMessage,
			EditText edittext, TextView textview, int type) {
		errorIcon = context.getResources().getDrawable(R.drawable.ic_error);
		errorMessage = context.getResources().getString(errMessage);
		errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(),
				errorIcon.getIntrinsicHeight()));
		switch (type) {
		case 0:
			if(TextUtils.isEmpty(errorMessage)){
				edittext.setError(null, errorIcon);
			}else{
				edittext.setError(errorMessage, errorIcon);
			}
			edittext.requestFocus();
			break;
		case 1:
//			Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			textview.requestFocus();
			textview.setError(errorMessage, errorIcon);
			break;
		case 2:
			if(TextUtils.isEmpty(errorMessage)){
				edittext.setError(null, null);
			}else{
				edittext.setError(errorMessage, null);
			}
			edittext.requestFocus();
			break;
		default:
			break;
		}

	}
	
	  public static boolean validateFirstCharOfMobNo(String enteredMobileNo) {
	      try {
	          char ch = enteredMobileNo.charAt(0);
	          if (ch != '7' && ch != '8' && ch != '9') {
	              return false;
	          }
	      } catch (Exception e) {
	      }
	      return true;
	  }

    public static boolean isValidAccountHolder(String name) {
        return name.matches("^[a-zA-Z '-]{2,32}$");
    }

    public static boolean isValidMobile(String mobile) {
        return mobile.matches("^[7-9][0-9]{9}$");
    }

    public static boolean isValidAccount(String account) {
        return account.matches("^[0-9]{9,19}");
    }

    public static boolean isIFSCValid(String ifsc) {
        return ifsc.matches("^[a-zA-Z]{4}0[a-zA-Z0-9]{6}$");
    }

    public static boolean isValidBranchName(String branch) {
        return branch.matches("^[a-zA-Z0-9, -]{2,32}$");
    }

	public static boolean isValidAmount(String amountString, double maxAmount) {
		double amount = 0.0;

		/*
		 * Upto 4 digits before decimal point. The starting digit can only be between 0 and 5.
		 * Upto 2 digits after the decimal point.
		 * The decimal point and the two digits after it are optional.
		 */
		if (!amountString.matches("^[0-9]{1,5}$")) { return false; }

		/* Valid decimal number */
		try { amount = Double.parseDouble(amountString); }
		catch (NumberFormatException e) { return false; }

		return (amount > 0.0 && amount <= maxAmount);
	}

	public static boolean isValidAmount(String amount) {
		return isValidAmount(amount, 10000.0);
	}

	// Ankur
	public static boolean isValidNumberNotDoubleNotZero (String amountString) {
		if (!amountString.matches("^[0-9]{1,5}$")) { return false; }
		/* Valid decimal number */
		double amount = 0.0;
		try { amount = Double.parseDouble(amountString); }
		catch (NumberFormatException e) { return false; }
		return (amount > 0.0);
	}

	public static boolean isValidTxnPIN(String arg) {
		String regex = "^[0-9]{4}$";
		return doValidationForRegexArg(regex, arg);
	}

	public static boolean isValidOTP (String arg) {
        String regex = "^[0-9]{3,6}$";
        return doValidationForRegexArg(regex, arg);
    }

	public static ArrayList<String> extractNumberSequenceFromString (String arg) {
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(arg);
		ArrayList<String> matches = new ArrayList<>();
		while (m.find()) {
			matches.add(m.group());
		}
		return matches;
	}

	public static String getCitrusOTPFromString (String arg) {
		for (String str: extractNumberSequenceFromString(arg)) {
			if (isValidOTP(str)) {
				return str;
			}
		}
		return null;
	}

	public static final int CVV_MAX=4;
	public static final int CVV_MIN=0;
	public static boolean isValidCVV (String arg) {
		String regex = "^[0-9]{0,4}$";
		return doValidationForRegexArg(regex, arg);
	}

	public static boolean isValidName (String arg) {

		String regex = "^[a-zA-Z' -]{1,32}$";
		return doValidationForRegexArg(regex, arg);
	}

	public static boolean isValidEmail (String arg) {

		String regex1 = "\\A[a-zA-Z0-9]+([-._][a-zA-Z0-9]+)*@([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-z]{2,4}\\z";
		String regex2 = "^(?=.{1,64}@.{4,64}$)(?=.{6,100}$).*";

		if (doValidationForRegexArg(regex1, arg)) {
			return doValidationForRegexArg(regex2, arg);
		}
		return false;
	}

	private static boolean doValidationForRegexArg (String regex, String arg) {
		CharSequence inputStr = arg;

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}



	public static String getCitrusOTPErrorForMessage (String arg) {
		ArrayList<String> numbers = extractNumberSequenceFromString(arg);
		String attemptNumber = null;
		for (String str: numbers) {
			if (str.length() == 1) {
				attemptNumber = str;
				break;
			}
		}
		Resources resources = QuopnApplication.getInstance().getApplicationContext().getResources();
		String message = resources.getString(R.string.citrus_dialog_otp_validation);
		if (attemptNumber != null) {
			message = message + resources.getString(R.string.citrus_dialog_otp_attempts) + attemptNumber;
		}
		return message;
	}

//	public static boolean isValidEmail(String email) {
//		/*
//		 * Starts with an alphabet
//		 * followed by none or more combinations of alphabets or numbers,
//		 * which may be preceded by a dot, dash or underscore
//		 * followed by a single @
//		 * followed by an alphabet or number
//		 * followed by one of more combinations of alphabets or numbers,
//		 * which may be preceded by a dash or underscore
//		 * followed by one or more combinations of a dot followed by 2-4 alphabets
//		 */
//		return email.matches(
//				"[a-zA-Z]([\\._-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]([_-]?[a-zA-Z0-9])+([\\.][a-z]{2,4})");
//	}

//	  public static void show(DialogConfigData dialogConfigData){
//			dialog = new Dialog(dialogConfigData.getContext());
//			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//			dialog.setCancelable(true);
//		   QuopnTextView negativeButton;
//		   
//		   mDialogDefaultCancleClickListner=new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			};
//			
//			switch (dialogConfigData.getDialogType()) {
//			case DialogConfigData.DIALOG_ERROR: // error
//				dialog.setContentView(R.layout.custom_alert_error);
//				break;
//			case DialogConfigData.DIALOG_CONFIRM: // success
//				 dialog.setContentView(R.layout.custom_alert_confirmation);
//				 negativeButton= (QuopnTextView) dialog.findViewById(R.id.Cancel_btn);
//				 
//				 if(dialogConfigData.getNegativeButtonTitleId() != 0)
//					negativeButton.setText(dialogConfigData.getContext().getString(dialogConfigData.getNegativeButtonTitleId()));
//				 else  if(dialogConfigData.getNegativeButtonTitle() != null)
//					negativeButton.setText(dialogConfigData.getNegativeButtonTitle());
//				 else
//					negativeButton.setText(dialogConfigData.getContext().getString(android.R.string.cancel));
//				  
//				  if(dialogConfigData.getCancelButtonClickListner() != null)
//					  mDialogDefaultCancleClickListner=dialogConfigData.getCancelButtonClickListner();
//					  negativeButton.setOnClickListener(mDialogDefaultCancleClickListner);
//					 
//				break;
//			case DialogConfigData.DIALOG_SUCCESS: // success
//				dialog.setContentView(R.layout.custom_alert_success);
//				break;
//				default :
//				dialog.setContentView(R.layout.custom_alert_success);
//				break;
//			}
//								
//			QuopnTextView dialogTitle = (QuopnTextView)dialog.findViewById(R.id.alerttitle);
//			QuopnTextView dialogMessage = (QuopnTextView)dialog.findViewById(R.id.alertmessage);
//			QuopnTextView positiveButton = (QuopnTextView) dialog.findViewById(R.id.Ok_btn);
//	      
//			 mDialogDefaultOkClickListner=new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dialog.dismiss();
//					}
//				};
//			  
//			if(dialogConfigData.getTitleId() != 0)
//				dialogTitle.setText(dialogConfigData.getContext().getString(dialogConfigData.getTitleId()));
//			if(dialogConfigData.getTitle() != null)
//				dialogTitle.setText(dialogConfigData.getTitle());
//			
//			if(dialogConfigData.getMessageId() != 0)
//				dialogMessage.setText(dialogConfigData.getContext().getString(dialogConfigData.getMessageId()));
//			if(dialogConfigData.getMessage() != null)
//				dialogMessage.setText(dialogConfigData.getMessage());
//			
//			if(dialogConfigData.getPosiveButtonTitleId() != 0)
//				positiveButton.setText(dialogConfigData.getContext().getString(dialogConfigData.getPosiveButtonTitleId()));
//			 else  if(dialogConfigData.getPosiveButtonTitle() != null)
//				positiveButton.setText(dialogConfigData.getPosiveButtonTitle());
//			else
//				positiveButton.setText(dialogConfigData.getContext().getString(android.R.string.ok));
//
//			
//			
//			if(dialogConfigData.getoKButtonClickListner() != null)
//				mDialogDefaultOkClickListner=dialogConfigData.getoKButtonClickListner();
//				positiveButton.setOnClickListener(mDialogDefaultOkClickListner);
//				
//
//			dialog.show();
//	  }
	
}