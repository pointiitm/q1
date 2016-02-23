package com.quopn.wallet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator {
	Date mDateToValidate, mStartDate, mEndDate;
	SimpleDateFormat mSimpleDateFormat;
	String mDefaultDateFormate = "dd/MM/yyyy";

	public boolean isThisDateWithinRange(String dateToValidate,
			String startDate, String endDate, String dateFromat) {
		mSimpleDateFormat = new SimpleDateFormat(dateFromat);
		mSimpleDateFormat.setLenient(false);
		try {
			// if not valid, it will throw ParseException
			this.mDateToValidate = mSimpleDateFormat.parse(dateToValidate);
			this.mStartDate = mSimpleDateFormat.parse(startDate);
			this.mEndDate = mSimpleDateFormat.parse(endDate);
			
			if (this.mDateToValidate.before(this.mEndDate)
					&& this.mDateToValidate.after(this.mStartDate)) {
				// ok everything is fine, date in range
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean isThisDateWithinRange(String dateToValidate,
			String startDate, String endDate) {
		mSimpleDateFormat = new SimpleDateFormat(mDefaultDateFormate);
		mSimpleDateFormat.setLenient(false);
		try {
			// if not valid, it will throw ParseException
			this.mDateToValidate = mSimpleDateFormat.parse(dateToValidate);
			this.mStartDate = mSimpleDateFormat.parse(startDate);
			this.mEndDate = mSimpleDateFormat.parse(endDate);
			
			if (this.mDateToValidate.before(this.mEndDate)
					&& this.mDateToValidate.after(this.mStartDate)) {
				// ok everything is fine, date in range
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

	}
	public boolean isTodayDateWithinRange(
			String startDate, String endDate) {
		mSimpleDateFormat = new SimpleDateFormat(mDefaultDateFormate);
		mSimpleDateFormat.setLenient(false);
		try {
			// if not valid, it will throw ParseException
			this.mDateToValidate = mSimpleDateFormat.parse(getTodayDate());
			this.mStartDate = mSimpleDateFormat.parse(startDate);
			this.mEndDate = mSimpleDateFormat.parse(endDate);
			
			if (this.mDateToValidate.before(this.mEndDate)
					&& this.mDateToValidate.after(this.mStartDate)) {
				// ok everything is fine, date in range
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean isTodayDateWithinRange(
			String startDate, String endDate,String dateFromat) {
		this.mDefaultDateFormate=dateFromat;
		mSimpleDateFormat = new SimpleDateFormat(dateFromat);
		mSimpleDateFormat.setLenient(false);
		try {
			// if not valid, it will throw ParseException
			this.mDateToValidate = mSimpleDateFormat.parse(getTodayDate());
			this.mStartDate = mSimpleDateFormat.parse(startDate);
			this.mEndDate = mSimpleDateFormat.parse(endDate);
			
			if (this.mDateToValidate.before(this.mEndDate)
					&& this.mDateToValidate.after(this.mStartDate)) {
				// ok everything is fine, date in range
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	private String getTodayDate() { 
		String todayDate = new SimpleDateFormat(this.mDefaultDateFormate).format(new Date());
		 return todayDate; }

}