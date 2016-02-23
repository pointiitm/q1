package com.quopn.wallet.fragments;

/**
 * @author Sumeet
 *
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.quopn.wallet.interfaces.BackPressedListener;

public class BaseFragment extends Fragment implements BackPressedListener {

	public boolean isExitable = false;

	@Override
	public void onBackPressed(FragmentActivity activity) {
		// TODO Auto-generated method stub

	}

	public void setExitable(boolean isExitable) {
		this.isExitable = isExitable;
	}

	public boolean getExitable() {
		return isExitable;
	}
}
