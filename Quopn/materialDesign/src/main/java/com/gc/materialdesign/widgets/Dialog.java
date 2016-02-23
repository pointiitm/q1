package com.gc.materialdesign.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.R;

public class Dialog extends android.app.Dialog{
	
	Context context;
	View view;
	View backView;
	String message="";
	TextView messageTextView;
	String title="";
	TextView titleTextView;
	int titleID=-1;
	int messageID=-1;

//	ButtonFlat buttonAccept;
//	ButtonFlat buttonCancel;
	
	Button buttonAccept;
	Button buttonCancel;
	
	String buttonCancelText;
	String buttonOkText;
	
	int buttonCancelTextID;
	int buttonOkTextID;
	
	float messagetextsize=16;//22
	float defaultmessagetextsize=16;
	
	View.OnClickListener onAcceptButtonClickListener;
	View.OnClickListener onCancelButtonClickListener;
	
	private boolean isCanceledOnTouchOutsideSet = true;
	
	
	public Dialog(Context context,String title, String message) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.message = message;
		this.title = title;
	}
	
	public Dialog(Context context,String title, int messageID) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.messageID = messageID;
		this.title = title;
		
	}
	
	public Dialog(Context context,int titleID, int messageID) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.messageID = messageID;
		this.titleID = titleID;
	}
	
	public Dialog(Context context,int titleID, String message) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.message = message;
		this.titleID = titleID;
	}
	
	public void addCancelButton(String buttonCancelText){
		this.buttonCancelText = buttonCancelText;
	}
	
	public void addCancelButton(String buttonCancelText, View.OnClickListener onCancelButtonClickListener){
		this.buttonCancelText = buttonCancelText;
		this.onCancelButtonClickListener = onCancelButtonClickListener;
	}
	
	public void addOkButton(String buttonOkText){
		this.buttonOkText = buttonOkText;
	}
	
	public void addOkButton(String buttonOkText, View.OnClickListener onOkButtonClickListener){
		this.buttonOkText = buttonOkText;
		this.onAcceptButtonClickListener = onOkButtonClickListener;
	}
	
	
	
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog);
	    
		view = (RelativeLayout)findViewById(R.id.contentDialog);
		backView = (RelativeLayout)findViewById(R.id.dialog_rootView);
		backView.setOnTouchListener(new OnTouchListener() {
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isCanceledOnTouchOutsideSet)
				{
					if (event.getX() < view.getLeft() 
							|| event.getX() >view.getRight()
							|| event.getY() > view.getBottom() 
							|| event.getY() < view.getTop()) {
						dismiss();
					}
				}
				return false;
			}
		});
		
	    this.titleTextView = (TextView) findViewById(R.id.title);
	    this.messageTextView = (TextView) findViewById(R.id.message);
	    
	    if(!this.title.equals("")){
	    	setTitle(this.title);
	    }
	    if(this.title.equals("")){
	    	setTitle(this.title);
	    }
	    if(this.titleID!=-1){
	    	 setTitleID(this.titleID);
	    }
	   
	    if(!this.message.equals("")){
	    	 setMessage(this.message);
	    }
	    if(this.message.equals("")){
	    	 setMessage(this.message);
	    }
	    if(this.messageID!=-1){
	    	 setMessageID(this.messageID);
	    }
	   
	   
	   
	   
	    
	    this.buttonAccept = (Button) findViewById(R.id.button_accept);
	    if(buttonOkText!=null)
	    	this.buttonAccept.setText(buttonOkText);
	    buttonAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if(onAcceptButtonClickListener != null)
			    	onAcceptButtonClickListener.onClick(v);
			}
		});
	    
	    if(buttonCancelText != null){
		    this.buttonCancel = (Button) findViewById(R.id.button_cancel);
		    this.buttonCancel.setVisibility(View.VISIBLE);
		    this.buttonCancel.setText(buttonCancelText);
	    	buttonCancel.setOnClickListener(new View.OnClickListener() {
	    		
				@Override
				public void onClick(View v) {
					dismiss();	
					if(onCancelButtonClickListener != null)
				    	onCancelButtonClickListener.onClick(v);
				}
			});
	    }
	}
	
	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		// TODO Auto-generated method stub
		super.setCanceledOnTouchOutside(cancel);
		isCanceledOnTouchOutsideSet = cancel;
	}
	
	
	@Override
	public void show() {
		super.show();
		// set dialog enter animations
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
		backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
	}
	
	// GETERS & SETTERS


	public TextView getMessageTextView() {
		return messageTextView;
	}

	public void setMessageTextView(TextView messageTextView) {
		this.messageTextView = messageTextView;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if(this.title == null || this.title.equals("")){
			titleTextView.setVisibility(View.GONE);
		}else{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(this.title.toUpperCase());
		}
	}
	
	
	public int getTitleID() {
		return titleID;
	}

	public void setTitleID(int titleID) {
		this.titleID = titleID;
		if(this.titleID == -1){
			titleTextView.setVisibility(View.GONE);
		}else{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(getContext().getText(this.titleID).toString().toUpperCase());
		}
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		if(this.title.equals("") && this.titleID==-1){
			messageTextView.setTextSize(messagetextsize);
		}else{
			messageTextView.setTextSize(defaultmessagetextsize);
		}
		messageTextView.setText(this.message);
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
		if(this.messageID==-1)
			messageTextView.setVisibility(View.GONE);
		else{
			messageTextView.setVisibility(View.VISIBLE);
			if(this.title.equals("") && this.titleID==-1){
				messageTextView.setTextSize(messagetextsize);
			}else{
				messageTextView.setTextSize(defaultmessagetextsize);
			}
			messageTextView.setText(this.messageID);
		}
		
	}
	
	

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public void setTitleTextView(TextView titleTextView) {
		this.titleTextView = titleTextView;
	}

	public Button getButtonAccept() {
		return buttonAccept;
	}

	public void setButtonAccept(Button buttonAccept) {
		this.buttonAccept = buttonAccept;
	}

	public Button getButtonCancel() {
		return buttonCancel;
	}

	public void setButtonCancel(Button buttonCancel) {
		this.buttonCancel = buttonCancel;
	}

	public void setOnAcceptButtonClickListener(
			View.OnClickListener onAcceptButtonClickListener) {
		this.onAcceptButtonClickListener = onAcceptButtonClickListener;
		if(buttonAccept != null)
			buttonAccept.setOnClickListener(onAcceptButtonClickListener);
	}

	public void setOnCancelButtonClickListener(
			View.OnClickListener onCancelButtonClickListener) {
		this.onCancelButtonClickListener = onCancelButtonClickListener;
		if(buttonCancel != null)
			buttonCancel.setOnClickListener(onCancelButtonClickListener);
	}
	
	@Override
	public void dismiss() {
		Dialog.super.dismiss();
//
//		Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
//		anim.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				view.post(new Runnable() {
//					@Override
//					public void run() {
//						Dialog.super.dismiss();
//					}
//			    });
//
//			}
//		});
//		Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
//
//		view.startAnimation(anim);
//		backView.startAnimation(backAnim);
	}
	
	

}
