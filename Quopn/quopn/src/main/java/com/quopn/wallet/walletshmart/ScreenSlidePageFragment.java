package com.quopn.wallet.walletshmart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quopn.wallet.R;
import com.quopn.wallet.views.SlidingUpPanelLayout;

/**
 * Created by Administrator on 21-Sep-15.
 */
public class ScreenSlidePageFragment extends Fragment {

    private SlidingUpPanelLayout slider;
    private Button actionSkip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.wallet_regn_demo_pager, container, false);

        Bundle bundle = this.getArguments();
        int pos = bundle.getInt("pos", 0);
        com.quopn.wallet.views.AspectRatioImageView imgView =  (com.quopn.wallet.views.AspectRatioImageView)rootView.findViewById(R.id.imgDemo);
        actionSkip = (Button) rootView.findViewById(R.id.action_skip);
        actionSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slider != null) {
                    ((ShmartRegn)getActivity()).showPoweredByLayout();
                    slider.collapsePanel();
                }
            }
        });
        switch (pos){
            case 0:
                imgView.setImageResource(R.drawable.cashback_tour_01);
                break;
            case 1:
                imgView.setImageResource(R.drawable.cashback_tour_02);
                break;
            case 2:
                imgView.setImageResource(R.drawable.cashback_tour_03);
                break;
            case 3:
                imgView.setImageResource(R.drawable.cashback_tour_04);
                break;
            default:
                imgView.setImageResource(R.drawable.cashback_tour_01);
                break;
        }

        return rootView;
    }

    public void changeButtonText(String argMsg){
        actionSkip.setText(argMsg);
    }

    public void setSlider(SlidingUpPanelLayout mSlidinguppanellayout) {
        this.slider = mSlidinguppanellayout;
    }
}
