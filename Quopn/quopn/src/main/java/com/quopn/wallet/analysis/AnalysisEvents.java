package com.quopn.wallet.analysis;

public interface AnalysisEvents {

	 //**Main Menu Events Start**
	 int PROFILE=1;
	 int MYCART=2;
	 int MYQUOPN=3;
	 int SHOPEAROUND=4;
	 int MYHISTORY=5;
	 int ABOUT=6;
	 int INVITE=7;
	 //**Main Menu Events Start** 
	
	//**Bottom Bar Events Start**
	 int MYGIFTS=8;
	 int CATEGORIES=9;
	 int ALL=10;
	 int FEATURED=11;
	 int NEW=12;
	 int EXPIRING=13;
	 int SEARCH=14;
	 
	 //**Bottom Bar Events Start** 
	
	 int CATEGORY=15;
	 
	//**Quopn Events Start**
	 int QUOPN=16;
	 int QUOPN_ISSUE=17;
	 int QUOPN_DETAIL=18;
	 int QUOPN_SHARE=19;
	//**Quopn Events End**
	 
	//**Quopn Events Start**
	  int ADD_TO_CART=20;
      int REMOVE_FROM_CART=21;
	  int ADD_ALL_TO_CART=22;
	  int REMOVE_ALL_FROM_CART=23;
		//**Quopn Events End**
	  
	  //Add Analysis for Video watching Time
	  int VIDEO_WATCHED_TIME=24;
		 
	  int GIFT=25;
	  int GIFT_ISSUE=26;
	  int GIFT_SHARE=27;
	  int GIFT_DETAIL=28;
	  
	  int SEARCH_WORD=29;
	  int IMPRESSION=30;
	  
	  int CRASH = 31;
	  int TOUR=32;
	  int PROFILE_COMPLETED=33;
	  int MOBILE_SUBMITTED=34;
	  int PROFILE_CHANGED=35;
	  int OTP_VERIFIED=36;
	  int OTP_FAILED=37;
	  
	  int VIDEO_PLAYER_STARTED=38;
	  int VIDEO_PLAYING=39;
	  int MYQUOPN_CAMPAIGN_DETAILS=40;
	  int MYCART_CAMPAIGN_DETAILS=41;
	
	  int PROMO = 42;
	  int FAQ= 43;
      int NOTIFICATION_ID = 44;
	  int SHMART_MENU_CLICKED= 45;

    int ANNOUNCEMENT_CLOSED=46;
    int ANNOUNCEMENT_CLICKED=47;
    int ANNOUNCEMENT_MOVED_TO_WALLET_SECTION=48;
    int ANNOUNCEMENT_APPEARED=49;

    int SCREEN_WALLET_REGISTRATION=50;
    int SCREEN_WALLET_OTP=51;
    int SCREEN_WALLET_DONE=52;
    int SCREEN_WALLET_HOME=53;
    int SCREEN_WALLET_ADD_MONEY=54;
    int SCREEN_WALLET_SEND_MONEY=55;
    int SCREEN_WALLET_SETTING=56;
    int SCREEN_WALLET_FAQS=57;
    int SCREEN_WALLET_TNC=58;
    int SCREEN_WALLET_MY_TRANSACTIONS=59;
    int SCREEN_WALLET_TRANSFER_TO_BANK=60;
    int SCREEN_WALLET_ADDNEW_BANKACCOUNT=61;
    int SCREEN_WALLET_RESETPIN=62;
    int SCREEN_WALLET_ADDING_MONEY_SHMART=63;

    int UPGRADE_POPUP_GOT=64;
    int UPGRADE_CLICKED=65;
    int UPGRADE_NOT_CLICKED=66;
    int SCREEN_WALLET_UNABLE_TO_ADDMONEY=67;
    int SCREEN_WALLET_UNABLE_TO_SENDMONEY=68;
    int SCREEN_WALLET_DIDNOT_REGISTER=69;
    int ADDED_BENIFICIARY=70;
    int DELETED_BENIFICIARY=71;
    int REQUESTED_FOR_OTP=72;
    int NOTIFICATION_DELIVERED=73;
    int NOTIFICATION_READ=74;
    int NOTIFICATION_CALL_TO_ACTION=75;
    int SENT_MONEY=76;
	int SHOPAT_STORES=77;
	int SCREEN_CITRUS_REGTNC=78;
}