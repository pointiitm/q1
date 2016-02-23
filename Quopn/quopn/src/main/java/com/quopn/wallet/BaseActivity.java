package com.quopn.wallet;

/**
 * @author Sumeet
 *
 *	This class is the base class for all the activity which includes the sliding window.
 *	It handles the back press event which manages the proper page navigation.
 */


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.RequestPinData;
import com.quopn.wallet.fragments.BaseFragment;
import com.quopn.wallet.fragments.CartFragment;
import com.quopn.wallet.fragments.MainMenuFragment;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.PreferenceUtil;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.QuopnTextView;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected Fragment mMenuFragment;
	protected Fragment mRightMenuFragment;
	SlidingMenu mSlidingMenu;

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.sliding_main);
		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		//for right side menu
		mSlidingMenu.setSecondaryMenu(R.layout.sliding_right);
		
		if (savedInstanceState == null) {
			this.getSupportFragmentManager().beginTransaction();
			mMenuFragment = new MainMenuFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.menu_frame, mMenuFragment)
					.addToBackStack(null).commit();
			QuopnConstants.ISREMOVEFROMCART=false;
			mRightMenuFragment = new CartFragment();
			FragmentManager fragmentManager_right = getSupportFragmentManager();
			fragmentManager_right.beginTransaction().replace(R.id.menu_frame_right, mRightMenuFragment)
					.addToBackStack(null).commit();
		} else {
			mMenuFragment = (Fragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
			
			mRightMenuFragment = (Fragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame_right);
		}

		mSlidingMenu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				hideKeyboard();
				
				int stackSize_menu = ((QuopnApplication) getApplication())
						.getFragmentsStack_menu().size();
				if (stackSize_menu >= 1) {
					mMenuFragment = new MainMenuFragment();
					FragmentManager fragmentManager = getSupportFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.menu_frame, mMenuFragment)
							.addToBackStack(null).commit();
					((QuopnApplication) getApplication()).getFragmentsStack_menu()
							.clear();

				} 
				
			}
		});
		mSlidingMenu.setOnOpenedListener(new OnOpenedListener() {
			@Override
			public void onOpened() {
				hideKeyboard();
				QuopnConstants.ISREMOVEFROMCART=true;
				if(mRightMenuFragment!=null && mRightMenuFragment instanceof CartFragment){
					mRightMenuFragment = new CartFragment();
					FragmentManager fragmentManager_right = getSupportFragmentManager();
					fragmentManager_right.beginTransaction().replace(R.id.menu_frame_right, mRightMenuFragment)
							.addToBackStack(null).commit();


				}

                callRequestPin();
                String requestPinNo = PreferenceUtil.getInstance(BaseActivity.this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PIN_KEY);
                QuopnTextView pinNo = (QuopnTextView)findViewById(R.id.slidemenu_pin_no);
                pinNo.setText(getResources().getString(R.string.pin_no) + " " + requestPinNo);
                pinNo.setTypeface(null, Typeface.BOLD);
				
				 
			}
		});

	}

    private void callRequestPin() {

        if (QuopnUtils.isInternetAvailable(this)) {
            //mCustomProgressDialog.show();
            Map<String, String> params = new HashMap<String, String>();
            params.put("walletid",PreferenceUtil.getInstance(this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.WALLET_ID_KEY));

            ConnectionFactory connectionFactory = new ConnectionFactory(this, (ConnectionListener) this);
            connectionFactory.setPostParams(params);
            connectionFactory.createConnection(QuopnConstants.REQUEST_PIN_CODE);
            System.out.println("=====BaseActivity===callRequestPin()===="+params);
        } else {
            //Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            // dialog.show();
        }
    }
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSlidingMenu.getWindowToken(), 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return backeventReceiver(true);
		}

		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 
	 * 
	 * @param isExitable
	 * @return
	 * 
	 * this method receives the back press and checks if the sliding menu is open then first delegates the event to the 
	 * sliding content first or else pass the event onto the main window content.
	 */
	protected boolean backeventReceiver(boolean isExitable) {
		if (mSlidingMenu.isMenuShowing()) {
			int stackSize_menu = ((QuopnApplication) getApplication())
					.getFragmentsStack_menu().size();

			if (stackSize_menu > 0) {
				BaseFragment baseFragment = (BaseFragment) ((QuopnApplication) getApplication())
						.getFragmentsStack_menu().get(stackSize_menu - 1);
				baseFragment.onBackPressed(this);
				((QuopnApplication) getApplication()).getFragmentsStack_menu()
						.remove(baseFragment);
				return true;
			} else {
				mSlidingMenu.toggle();
			}

		} else {

			int stackSize = ((QuopnApplication) getApplication())
					.getFragmentsStack().size();

			if (stackSize == 1) {
				BaseFragment baseFragment = (BaseFragment) ((QuopnApplication) getApplication())
						.getFragmentsStack().get(stackSize - 1);
				baseFragment.setExitable(isExitable);
				baseFragment.onBackPressed(this);

				return true;
			} else if (stackSize > 1) {
				BaseFragment baseFragment = (BaseFragment) ((QuopnApplication) getApplication())
						.getFragmentsStack().get(stackSize - 1);
				baseFragment.onBackPressed(this);
				((QuopnApplication) getApplication()).getFragmentsStack()
						.remove(baseFragment);
				return true;
			} else {
				this.finish();
			}

		}

		return false;

	}
	/**
	 * Clear the fragment stack for sliding menu and main window.
	 * 
	 */
	@Override
	public void finish() {
		((QuopnApplication) getApplication()).getFragmentsStack_menu().clear();
		((QuopnApplication) getApplication()).getFragmentsStack().clear();
		super.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (TextUtils.isEmpty(QuopnConstants.PROFILE_DATA)) {
			QuopnConstants.PROFILE_DATA = PreferenceUtil.getInstance(BaseActivity.this).getPreference(PreferenceUtil.SHARED_PREF_KEYS.PROFILE_DATA);
		}
	}

}
