package com.quopn.wallet.analysis.dataclasses;

public class MainMenu extends AnalysisBase {
	private int mainMenuKey;
	private int value;
//	private String mainMenuKeyValue;
	
	public MainMenu(){
		
	}
	public MainMenu(String userId,String timeStamp,int mainMenuKey) {
		super(userId,timeStamp);
		this.mainMenuKey=mainMenuKey;
	}
	public void setMainMenuKey(int mainMenuKey) {
		this.mainMenuKey = mainMenuKey;
	}

	public int getMainMenuKey() {
		return mainMenuKey;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

//	public void setMainMenuKeyValue(String mainMenuKeyValue) {
//		this.mainMenuKeyValue = mainMenuKeyValue;
//	}
//	public String getMainMenuKeyValue() {
//		return mainMenuKeyValue;
//	}
}
