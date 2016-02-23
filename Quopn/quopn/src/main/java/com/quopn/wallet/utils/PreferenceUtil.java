package com.quopn.wallet.utils;
/**
 * @author Sumeet
 *
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

public class PreferenceUtil implements Cloneable {

	private static final String PREFERENCE_FILE_NAME = "com.quopn.wallet.pref";

	private static PreferenceUtil sPreferenceUtil;
	private static SharedPreferences sPrefernces;
	private static SharedPreferences.Editor sEditor;

	public static PreferenceUtil getInstance(Context context) {
		if (null == sPreferenceUtil) {
			sPrefernces = context.getSharedPreferences(PREFERENCE_FILE_NAME,
					Context.MODE_PRIVATE);
			sEditor = sPrefernces.edit();
			sPreferenceUtil = new PreferenceUtil();
		}

		return sPreferenceUtil;
	}

	private PreferenceUtil() {

	}

	protected Object clone() throws CloneNotSupportedException {
		return null;

	}

	public void setPreference(String key, boolean value){
		sEditor.putBoolean(key, value);
		sEditor.commit();
	}
	
	public boolean getPreference_bool(String key) {
		return sPrefernces.getBoolean(key, false);
	}
	
	public void setPreference(String key, String value) {
		sEditor.putString(key, value);
		sEditor.commit();
	}
	
	public void setPreference(String key, int value) {
		sEditor.putInt(key, value);
		sEditor.commit();
	}

	public void setPreference(String key, Set<String> set){
		sEditor.putStringSet(key, set);
		sEditor.commit();
	}
	
	public String getPreference(String key) {
		return sPrefernces.getString(key, null);
	}
	
	public int getPreference_int(String key) {
		return sPrefernces.getInt(key, 0);
	}
	
	public boolean getPreference_boolean(String key) {
		return sPrefernces.getBoolean(key, false);
	}
	
	public Set<String> getPreference_set(String key) {
		return sPrefernces.getStringSet(key, null);
	}

	public boolean hasContainedPreferenceKey(String key) {
		if (sPrefernces.contains(key)) {
			return true;
		}

		return false;
	}
	public void clearPreference() {
		boolean isShmartWalletShown = false;
		if (sPreferenceUtil != null) {
			if (sPreferenceUtil.hasContainedPreferenceKey(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN)) {
				isShmartWalletShown = sPreferenceUtil.getPreference_bool(SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN);
			}
		}
		sEditor.clear();
		sEditor.commit();

		if (sPreferenceUtil != null) {

			sPreferenceUtil.setPreference(PreferenceUtil.SHARED_PREF_KEYS.IS_SHMART_WALLET_SHOWN, isShmartWalletShown);
		} else {
			Log.e("PreferenceUtil", "user cant go shmart wallet");
		}

	}

	public void removePreferenceValue(String key) {
		sEditor.remove(key);
		sEditor.commit();
	}
	
	public void saveProfileIfNull(String profileJson) {
		
		String existingProfile = getPreference(SHARED_PREF_KEYS.PROFILE_DATA);
		if (existingProfile == null || existingProfile.isEmpty()) {
			setPreference(SHARED_PREF_KEYS.PROFILE_DATA, profileJson);
		}
	}
	
	public interface SHARED_PREF_KEYS{

		 String API_KEY = "api_key";
		 String PROFILE_DATA = "PROFILE_DATA";
//		 String LOCATION_STATE_INDEX = "state_index";
//		 String LOCATION_STATE_NAME = "state_name";
//		 String LOCATION_CITY_INDEX = "city_index";
//		 String LOCATION_CITY_NAME = "city_name";
		 String PROFILE_IMAGE_PATH = "PROFILE_IMAGE_PATH";
		 String FOOTER_TAGS = "FOOTER_TAGS";
		 String STATES = "STATES";
		 String CITIES = "CITIES";
		 //String MYQUOPNCOUNT = "MYQUOPNCOUNT";
		 String MYCARTCOUNT = "MYCARTCOUNT";
		 String INVITE_COUNT = "invite_count";
		 String INVITE_MESSAGE = "invite_message";
		 String INVITE_TOP_MESSAGE = "invite_top_message";
		 String INVITE_SMS = "invite_sms";
		 String PERSONAL_MESSAGE_DOWNLOADED_PATH = "personal_message_downloaded_path";
		 String PERSONAL_MESSAGE_DOWNLOADED_URL = "personal_message_downloaded_url";
		 String NOTIFICATIONCOUNT = "NOTIFICATIONCOUNT";
		 String IS_SHMART_WALLET_SHOWN = "IS_SHMART_WALLET_SHOWN";

		 String PERSONAL_MESSAGE_SENDER_NAME = "personal_message_sender";
		 String PERSONAL_MESSAGE_SENDER_PIC = "personal_message_sender_pic";
		 String IS_ANY_TUT_ON = "is_any_tut_on";
		 String IS_GiftTutShown = "is_gifttutshown";
		 String IS_CatTutShown = "is_cattutshown";
		 String IS_TutShown = "is_tutshown";
		 String SPLASH_SCREEN_URL="animated_splash_screen_url";
		 
		 String SPLASH_SCREEN_LOCAL_PATH="splash_screen_local_path";
		 String SPLASH_SCREEN_START_DATE="start_date";
		 String SPLASH_SCREEN_END_DATE="end_date";
		 String SPLASH_SCREEN_FILE_NAME="splash_screen_file_name";
		 String SPLASH_SCREEN_FILE_TYPE="splash_screen_file_type";
		 
		 
		 String NOTIFICATION_SET = "notification_set";
		 String TOUR_REACHED = "tour_reached";
		 
		 String GIFT_TITLE = "gift_title";
		 
		 String IS_FIRSTLAUNCH = "isFirstLaunch";
		 
		 String UTM_SOURCE = "utm_source";
		 
		 String UTM_CONTENT = "utm_content";
		 
		 String UTM_CAMPAIGN = "utm_campaign";
		 
		 String PROMO_MESSAGE = "promo_message";
		 String PROMO_TOP_MESSAGE = "promo_top_message";
		 String PROMO_BOTTOM_MESSAGE = "promo_bottom_message";
         String NOTITY_STAUS_KEY = "Notify_Status";

        String WALLET_ID_KEY = "walletId";
        String USERNAME_KEY = "username";
        String MOBILE_KEY = "mobile";
        String EMAIL_KEY = "email";
        String USER_STATE = "state";
        String USER_CITY = "city";
        String PIN_KEY = "pin";
        String USER_ID = "userId";
        String TYPE_OF_USER ="type_of_user";
        String ANNOUCEMENT_URL ="annoucement_url";
        String SESSION_ID = "session_id";

		String SHMART_STATUS = "shmart_status";

		String DEFAULT_WALLET_KEY = "default_wallet";
		String MOBILE_WALLETS_KEY = "mobile_wallet";
		String ACCESS_TOKEN_KEY = "access_token";
		String REFRESH_TOEKN_KEY = "refresh_token";
		String CITRUS_EMAIL_KEY = "citrus_email";

		String IS_UPDATED = "is_updated";
	}
	
}
