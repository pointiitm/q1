package com.quopn.wallet.shmart;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.gc.materialdesign.widgets.Dialog;
import com.quopn.errorhandling.ExceptionHandler;
import com.quopn.wallet.QuopnApplication;
import com.quopn.wallet.R;
import com.quopn.wallet.adapter.FAQListAdapter;
import com.quopn.wallet.analysis.AnalysisEvents;
import com.quopn.wallet.analysis.AnalysisManager;
import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.connection.ConnectionFactory;
import com.quopn.wallet.data.model.FAQData;
import com.quopn.wallet.data.model.TAndCAndPrivPolData;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnApi;
import com.quopn.wallet.utils.QuopnConstants;
import com.quopn.wallet.utils.QuopnUtils;
import com.quopn.wallet.views.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shmart_Faq_Activity extends ActionBarActivity implements ConnectionListener {

    private FAQListAdapter mListAdapter;
    private FloatingGroupExpandableListView mFAQList;
    private List<FAQData> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    protected int previousGroup = -1;
    private int currentGroup = -1;
    private CustomProgressDialog mCustomProgressDialog;
    private String textFAQ = "";
    private AnalysisManager mAnalysisManager;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_shmart_faq);

        mAnalysisManager = ((QuopnApplication) getApplicationContext()).getAnalysisManager();
        mAnalysisManager.send(AnalysisEvents.SCREEN_WALLET_FAQS);

        mFAQList = (FloatingGroupExpandableListView ) findViewById(R.id.faq_menu_list);

        mCustomProgressDialog = new CustomProgressDialog(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg));

        View actionBarView = View
                .inflate(this, R.layout.actionbar_layout, null);
        getSupportActionBar().setCustomView(actionBarView);

        ImageView slider = (ImageView) actionBarView.findViewById(R.id.slider);
        slider.setImageResource(R.drawable.back);
        slider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView home_btn = (ImageView) actionBarView
                .findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();

            }
        });

        SearchView searchView = (SearchView) actionBarView
                .findViewById(R.id.fragment_address_search);
        searchView.setVisibility(View.INVISIBLE);
        ImageView mCommonCartButton=(ImageView)actionBarView.findViewById(R.id.cmn_cart_btn);
        mCommonCartButton.setVisibility(View.INVISIBLE);
        TextView mNotification_Counter_tv=(TextView)actionBarView.findViewById(R.id.notification_counter_txt);
        mNotification_Counter_tv.setVisibility(View.INVISIBLE);
        TextView mAddtoCard_Counter_tv=(TextView)actionBarView.findViewById(R.id.addtocard_counter_txt);
        mAddtoCard_Counter_tv.setVisibility(View.INVISIBLE);
//		mAboutList = (ExpandableListView) rootView
//				.findViewById(R.id.about_menu_list);
//		return rootView;

        // Ankur
//        ImageView imgContactUs = (ImageView) findViewById(R.id.contact_us);
//        imgContactUs.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                openDialer();
//            }
//        });
//
//        TextView textMoreQuestions = (TextView) findViewById(R.id.more_questions);
//        textMoreQuestions.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                openDialer();
//            }
//        });
    }

    public void openDialer(){
        Intent dialIntent = new Intent();
        dialIntent.setAction(Intent.ACTION_DIAL);

        Uri phoneUri = Uri.parse(QuopnConstants.PHONE_PROTO + QuopnConstants.CONTACT_US);
        dialIntent.setData(phoneUri);

        startActivity(dialIntent);
    }

    public void getFAQs(){
        if (QuopnUtils.isInternetAvailable(this)) {
            ConnectionFactory mConnectionFactory = new ConnectionFactory(getApplicationContext(),this);
            mConnectionFactory.createConnection(QuopnConstants.QUOPN_MOBILE_WALLET_FAQ);
            mCustomProgressDialog.show();
        } else {
            Dialog dialog=new Dialog(this, R.string.dialog_title_no_internet,R.string.please_connect_to_internet);
            dialog.show();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();

        // preparing list data
        prepareListData();

        mListAdapter = new FAQListAdapter(this, listDataHeader, listDataChild);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(mListAdapter);

        // setting list adapter
        mFAQList.setAdapter(wrapperAdapter);

				/*mFAQList.setOnGroupExpandListener(new OnGroupExpandListener() {
			        @Override
			        public void onGroupExpand(int groupPosition) {
			        	if(QuopnUtils.isInternetAvailable(FAQActivity.this)){
			        		if(groupPosition != previousGroup ){
				            	mFAQList.collapseGroup(previousGroup);
				            	previousGroup = groupPosition;
				            	getFAQs();
				            }
				            previousGroup = groupPosition;
			        	}else{
			        		new Dialog(FAQActivity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
			        	}
			        }
			    });*/

        mFAQList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(QuopnUtils.isInternetAvailable(Shmart_Faq_Activity.this)){
                    currentGroup = groupPosition;
                    if(groupPosition != previousGroup ){
                        mFAQList.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }
                    previousGroup = groupPosition;
                    if(mFAQList.isGroupExpanded(groupPosition)){
                        mFAQList.collapseGroup(groupPosition);
                    } else {
                        mFAQList.collapseGroup(groupPosition);
                        if(groupPosition == 0){
                            if(TextUtils.isEmpty(textFAQ)){
                                getFAQs();
                            } else{
                                setCachedText(textFAQ);
                            }
                        }
                    }
                } else{
                    new Dialog(Shmart_Faq_Activity.this,R.string.dialog_title_no_internet,R.string.please_connect_to_internet).show();
                }
                return false;
            }
        });

        // Listview on child click listener
        mFAQList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        mFAQList.expandGroup(0);
        getFAQs();
    }

	/*@Override
	public void onBackPressed(FragmentActivity activity) {
		super.onBackPressed(activity);
		activity.getSupportFragmentManager().beginTransaction().remove(this)
				.commit();
		Fragment mainmenu = new MainMenuFragment();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.menu_frame, mainmenu)
				.commit();

	}*/

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<FAQData>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        //listDataHeader.add(new AboutUsData("EULA",R.drawable.icon_eula));
        listDataHeader.add(new FAQData("FAQs",R.drawable.icon_faq));
//		listDataHeader.add(new FAQData("Privacy Policy",R.drawable.icon_privacypolicy));
//		listDataHeader.add(new AboutUsData("FAQs",R.drawable.icon_faq));

        // Adding child data
		/*List<String> EULA = new ArrayList<String>();
		EULA.add("http://solutions.techshastra.com/quopnapi/v2_test/eula");*/

//		List<String> Terms = new ArrayList<String>();
//		Terms.add(QuopnApi.TNC_URL);
//
//		List<String> Privacy = new ArrayList<String>();
//		Privacy.add(QuopnApi.PRIVACY_POLICY_URL);

        List<String> FAQs = new ArrayList<String>();
        if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.SHMART) {
            FAQs.add(QuopnApi.QUOPN_MOBILE_WALLET_FAQ_URL_SHMART);
        } else if (QuopnApplication.getInstance().getCurrentWalletMode() == QuopnConstants.WalletType.CITRUS) {
            FAQs.add(QuopnApi.QUOPN_MOBILE_WALLET_FAQ_URL_CITRUS);
        }

        //listDataChild.put(listDataHeader.get(0).getName(), EULA); // Header, Child data
//		listDataChild.put(listDataHeader.get(0).getName(), Terms);
//		listDataChild.put(listDataHeader.get(1).getName(), Privacy);
        listDataChild.put(listDataHeader.get(0).getName(), FAQs);
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if(response != null){
            TAndCAndPrivPolData data = (TAndCAndPrivPolData)response;
            setCachedText(data.getData());
        }
        mCustomProgressDialog.dismiss();
    }

    public void setCachedText(String argText){
        mListAdapter.setText(argText);
        if(currentGroup == 0){
            textFAQ = argText;
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
                    mCustomProgressDialog.dismiss();
                    Dialog dialog=new Dialog(Shmart_Faq_Activity.this, R.string.slow_internet_connection_title,R.string.slow_internet_connection);
                    dialog.show();
                }

            }
        });


    }

    @Override
    public void myTimeout(String requestTag) {

    }
}

