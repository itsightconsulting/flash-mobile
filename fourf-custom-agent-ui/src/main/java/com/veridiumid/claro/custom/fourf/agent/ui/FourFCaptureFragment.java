package com.veridiumid.claro.custom.fourf.agent.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.veridiumid.claro.custom.fourf.R;
import com.veridiumid.sdk.fourf.FourFUIIntegrationWrapper;
import com.veridiumid.sdk.fourf.FourFUIInterface;
import com.veridiumid.sdk.fourf.onFourFFragmentReadyListener;
import com.veridiumid.sdk.support.ui.AspectRatioSafeFrameLayout;

/**
 * Custom UI fragment for the 4F agent mode. Uses {@link FourFGuidelineView} to overlay the camera preview
 * of the 4FingersID capture.
 */
public class FourFCaptureFragment extends Fragment implements FourFUIInterface, SurfaceHolder.Callback {
    private static final String TAG = FourFCaptureFragment.class.getSimpleName();
    private onFourFFragmentReadyListener mListener;
    private Handler mHandler;
    private AspectRatioSafeFrameLayout mPreviewHolder;
    private FourFGuidelineView mFourFGuidelineView;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourf_capture, container, false);
        mFourFGuidelineView = view.findViewById(R.id.guide_surface);
        mPreviewHolder = view.findViewById(R.id.camera_preview_container);
        mProgressBar = view.findViewById(R.id.processing_loader);

        // on cancel, show spinner and shut down drawing.
        view.findViewById(R.id.btn_close).setOnClickListener(v -> {
            mHandler.post(() -> {
                mFourFGuidelineView.stopCountDownTimer();
                mProgressBar.setVisibility(View.VISIBLE);
            });
            FourFUIIntegrationWrapper.requestCancel();
        });

        mFourFGuidelineView.setZOrderOnTop(true);
        mFourFGuidelineView.getHolder().addCallback(this);
        mFourFGuidelineView.getHolder().setFormat(PixelFormat.RGBA_8888);


        // we setup default color to be red
        mFourFGuidelineView.setHandGuideColorFilter(Color.RED);


        mHandler = new Handler();
        return view;
    }

    @Override
    public void handleUIInstruction(uiInstruction uiInstruction) {
        switch (uiInstruction) {
            case Wait:
            case OutOfFocus:
            case Yes:
            case ImageTooDim:
            case ImageRequestedWaiting:
                mFourFGuidelineView.setHandGuideColorFilter(Color.GREEN);
                break;
            case FingersTooFarApart:
            case FingersHigh:
            case FingersLow:
            case FingersFarLeft:
            case FingersFarRight:
            case InvalidROIS:
            case RoiTooBig:
            case RoiTooSmall:
                mFourFGuidelineView.setHandGuideColorFilter(Color.RED);
                break;
        }
    }

    @Override
    public void handleBlockingUIInstruction(blockingUIInstruction blockingUIInstruction) {
        FourFUIIntegrationWrapper.handledBlockingUIInstruction(UserAction.NEXT);
    }

    @Override
    public void onProcessingStart() {

    }

    @Override
    public void onProcessingStop() {
        mHandler.post(() -> mProgressBar.setVisibility(View.GONE));
    }

    @Override
    public void onTakePictureStart() {
        mFourFGuidelineView.startCountDownTimer();
    }

    @Override
    public void onTakePictureStop() {
        mHandler.post(() -> {
            vibrate();
            mFourFGuidelineView.stopCountDownTimer();
            mProgressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onImageAcceptance() {
    }

    @Override
    public void onImageRejection() {
        mHandler.post(() -> {
            mFourFGuidelineView.stopCountDownTimer();
        });
    }

    @Override
    public void configureUI(captureMode captureMode, printToCapture printToCapture, RectF rectF, boolean b) {
        mHandler.post(() -> {
            mFourFGuidelineView.setFourFConfiguration(rectF, printToCapture);
            /*mProgressBar.setVisibility(View.GONE);*/
        });
    }

    @Override
    public void displayRealTimeInformation(RectF[] rectFS, float v) {
    }

    @Override
    public void setPreviewResolution(int i, int i1) {
    }

    @Override
    public AspectRatioSafeFrameLayout getPreviewHolder() {
        return mPreviewHolder;
    }

    @Override
    public void onReady(onFourFFragmentReadyListener onFourFV2FragmentReadyListener) {
        mListener = onFourFV2FragmentReadyListener;
    }

    @Override
    public void dismiss(FourFFinishReason fourFFinishReason) {
        Log.d(TAG, "Dismiss " + fourFFinishReason);

        switch (fourFFinishReason) {
            // When the fourF capture is successful we display the success statement on the FourFGuideLineView
            case SUCCESS_AUTHENTICATE:
            case SUCCESS_ENROLL:
            case SUCCESS_EXPORT:
                mFourFGuidelineView.setSuccessful();
                mHandler.postDelayed(() -> {
                    getActivity().finish();
                }, 1000);
                mHandler.postDelayed(() -> getActivity().finish(), 1000);
                break;
            default:
                mHandler.post(() -> getActivity().finish());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mListener.onFourFFragmentReady();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mFourFGuidelineView.stopCountDownTimer();
    }

    private void vibrate() {
        mHandler.post(() -> {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);
        });
    }
}
