package com.quopn.wallet.analysis.dataclasses;

public class AnalysisBase {
	protected String eventId="null";
	protected String timeStamp;
	
	public AnalysisBase() {
	}
	public AnalysisBase(String userId) {
		this.eventId = userId;
	}
	public AnalysisBase(String userId,String timeStamp) {
		this.eventId = userId;
		this.timeStamp = timeStamp;
	}
	public void setEventId(String userId) {
		this.eventId = userId;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getEventId() {
		return eventId;
	}
	public String getTimeStamp() {
		return ""+System.currentTimeMillis();
	}
}
