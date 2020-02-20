package com.veridiumid.sdk.fourf.veridiumui.activity;

import android.os.Bundle;
import android.view.View;

import com.veridiumid.sdk.fourf.FourFInterface;
import com.veridiumid.sdk.fourf.defaultui.activity.DefaultFourFBiometricsActivity;
import com.veridiumid.sdk.fourf.defaultui.activity.DefaultFourFFragment;
import com.veridiumid.sdk.fourf.defaultui.activity.UICustomization;
import com.veridiumid.sdk.fourf.veridiumui.R;
import com.veridiumid.sdk.fourfintegration.HandGuideHelper;

public class VeridiumFourFBiometricActivity extends DefaultFourFBiometricsActivity {


    private final int FOURF_TIMEOUT = 120000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimeout(FOURF_TIMEOUT);
    }

    @Override
    public void onFourFFragmentReady(final DefaultFourFFragment fourFFragment){
        super.onFourFFragmentReady(fourFFragment);
        if(UICustomization.getBackgroundImage() != null) {
            fourFFragment.rl_top_image.setBackground(getResources().getDrawable(R.drawable.hand_scan_ui_banner));
        }
    }

    @Override
    public void showInstructionalFragment(){

        VeridiumFourFInstructionalFragment fragment = new VeridiumFourFInstructionalFragment();
        Bundle fragmentBundle = new Bundle();
        if(isCapture() || isCapture2THUMB() || isCapture8F() || isCaptureIndividualF() || isCaptureTHUMB()){

            fragmentBundle.putString("Heading", String.valueOf(getText(R.string.export)));

        }else if(isEnrollment() || isEnrollExport()){
            fragmentBundle.putString("Heading", String.valueOf(getText(R.string.enroll)));
        }
        fragment.setArguments(fragmentBundle);
        showFragment(fragment);


    }

    public void onInstructionalFragmentReady(final VeridiumFourFInstructionalFragment instructionalFragment) {
        instructionalFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOffBiometricsProcess();
            }
        });

        instructionalFragment.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    public void setHandGuideDesign(){
        HandGuideHelper.setGuideDesign(FourFInterface.GuideDesign.MITTEN_DARK_LARGE);
    }

    @Override
    public boolean displayROIs() {
        return false;
    }


    @Override
    public boolean useHandMeter() { return true; }

    @Override
    public boolean useGuidanceArrows() { return false; }

}
