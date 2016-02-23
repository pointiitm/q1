package com.quopn.wallet.views;

/**
 * @author Sivnarayan Roul
 *
 *@date 01/06/2015
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.quopn.wallet.R;

public class CustomSeekbar extends FrameLayout implements OnSeekBarChangeListener{

	private Activity mActivity;
	private LayoutInflater mLayoutInflater;
	private SeekBar mSeekBar;
	private TextView mUserGivenFeedbackValue;
	private View mParentView ;
	
	public CustomSeekbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public CustomSeekbar(Activity activity,String seekbarTitle)
	{
		super(activity);
		mActivity = activity;
		initView(seekbarTitle);
	}
	
	private void initView(String seekbarTitle)
	{
		if (mLayoutInflater == null)
		{
           /* mLayoutInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
            mLayoutInflater = mActivity.getLayoutInflater();
		}
		
		mParentView = mLayoutInflater.inflate(R.layout.seekbar_widget,null);
		TextView titleTxtView = (TextView)mParentView.findViewById(R.id.seekbar_title);
		//titleTxtView.setWidth(getResources().getDrawable(R.drawable.number_bg).getBounds().width());
		titleTxtView.setText(seekbarTitle);
		
		mUserGivenFeedbackValue = (TextView)mParentView.findViewById(R.id.user_given_feedback);
		
		mSeekBar = (SeekBar)mParentView.findViewById(R.id.custom_feedback_seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setProgress(5);
		/*mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				mUserGivenFeedbackValue.setText(String.valueOf(progress));
			}
		});		
		*/
		
		//setManualGradient();
		this.addView(mParentView);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		try {
			//mUserGivenFeedbackValue = (TextView)mParentView.findViewById(R.id.user_given_feedback);
			//int MIN = 1;
			Log.d("CustomSeekbar", " onProgressChanged progress --------------" + progress);
           /* if (progress == 1) {
            	mUserGivenFeedbackValue.setText(1);
                //value.setText(" Time Interval (" + seektime + " sec)");
            }else if(progress == 2){
            	mUserGivenFeedbackValue.setText(String.valueOf(progress));
            } else {
            	progress = progress +1;
            	mUserGivenFeedbackValue.setText(String.valueOf(progress));
            }*/
			
			if(progress > 0)
			{
				progress = progress +1;
            	mUserGivenFeedbackValue.setText(String.valueOf(progress));
			}
			if(progress == 0)
				mUserGivenFeedbackValue.setText("1");
			setManualGradient(progress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public String getUserProvidedFeedbackValue()
	{
		return (String) mUserGivenFeedbackValue.getText();
	}
	
	public void setGradient(Drawable drawable)
	{
		
		mSeekBar.setProgressDrawable(drawable);		
	}
	
	/*@SuppressLint("NewApi")
	public void setManualGradient()
	{
		
        Drawable dr = mActivity.getResources().getDrawable(R.drawable.custom_progressbar);
        Rect rect = dr.getBounds();
        Integer intgr = new Integer(rect.width()); 
        Integer intHeight = new Integer(rect.height());
        //new int[]{0xFE1577, 0xFEB231, 0xB6E600, 0x1CC799,0x35C0FC},
        LinearGradient test = new LinearGradient(0.0f, 0.0f, intgr.floatValue(),intHeight.floatValue() ,
                new int[]{Color.RED, Color.GRAY, Color.GREEN, Color.CYAN,Color.BLUE},
                new float[]{1,2,3,4,5},
                 TileMode.CLAMP);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{Color.RED, Color.GRAY, Color.GREEN, Color.CYAN,Color.BLUE});
		ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(g);
        
        mSeekBar.setProgressDrawable(gradientDrawable);		
	}*/
	
	public void setManualGradient(int progress)
	{
		switch(progress)
		{
		case 1:
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.custom_pink_progressbar));
			setProgressBarColor(Color.rgb(255,70,88));
			// mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.slider_bar_01));
			break;
		case 2:
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.custom_orange_progressbar));
			setProgressBarColor(Color.rgb(254,178,49));
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.slider_bar_02));
			break;
		case 3:
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.custom_green_progressbar));
			setProgressBarColor(Color.rgb(254,218,49));
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.slider_bar_03));
			break;
		case 4:
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.custom_cyan_progressbar));
			setProgressBarColor(Color.rgb(182,230,0));
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.slider_bar_04));
			break;
		case 5:
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.custom_light_blue_progressbar));
			setProgressBarColor(Color.rgb(28,198,152));
			//mSeekBar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.slider_bar_05));
			break;
		default:
			break;
		}
	}
	
	public void setProgressBarColor(int newColor){ 
		  LayerDrawable ld = (LayerDrawable) mSeekBar.getProgressDrawable();
		  ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progressshape);
		  d1.setColorFilter(newColor,Mode.OVERLAY);
		  
		}
}
