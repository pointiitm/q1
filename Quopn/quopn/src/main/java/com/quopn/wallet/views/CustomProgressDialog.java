package com.quopn.wallet.views;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.quopn.wallet.R;

public class CustomProgressDialog extends Dialog {

	private RelativeLayout pdbgclolor;
	ImageView mProgressBar;
	public CustomProgressDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.mydialog);
		mProgressBar=(ImageView)this.findViewById(R.id.customProgressBar);
		AnimationDrawable animation = (AnimationDrawable) mProgressBar.getDrawable();
		if (animation != null) { animation.start(); }
		pdbgclolor = (RelativeLayout) findViewById(R.id.pdbgclolor);
		setCancelable(false);

	}
}
