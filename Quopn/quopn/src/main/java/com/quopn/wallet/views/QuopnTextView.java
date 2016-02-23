/**
 * 
 */
package com.quopn.wallet.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Sumeet
 * 
 * @Date 03/09/2014
 * 
 */
public class QuopnTextView extends TextView {

	public QuopnTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public QuopnTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public QuopnTextView(Context context) {
		super(context);
		init();
	}

	public void init() {
		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/OPENSANS-LIGHT.TTF");
		setTypeface(typeface, 1);

	}

}
