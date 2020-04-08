package com.veridiumid.claro.custom.fourf.agent.ui;

import androidx.fragment.app.Fragment;

import com.veridiumid.sdk.defaultdata.DataStorage;
import com.veridiumid.sdk.fourf.FourFBiometricsActivity;
import com.veridiumid.sdk.fourf.FourFUIInterface;
import com.veridiumid.sdk.model.data.persistence.IKVStore;

/**
 * Custom 4F activity that uses {@link FourFCaptureFragment} to display biometric capture and processing.
 */
public class FourFCaptureActivity extends FourFBiometricsActivity {

    @Override
    protected IKVStore openStorage() {
        return DataStorage.getDefaultStorage();
    }

    @Override
    protected <FourFFragmentInterfaceUnion extends Fragment & FourFUIInterface> FourFFragmentInterfaceUnion fragmentToShow() {
        return (FourFFragmentInterfaceUnion) new FourFCaptureFragment();
    }
}

