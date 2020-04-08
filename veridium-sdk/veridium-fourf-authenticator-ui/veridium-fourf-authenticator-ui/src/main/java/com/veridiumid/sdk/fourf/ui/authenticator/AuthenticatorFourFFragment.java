package com.veridiumid.sdk.fourf.ui.authenticator;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.veridiumid.sdk.fourf.FourFUIIntegrationWrapper;
import com.veridiumid.sdk.fourf.FourFUIInterface;
import com.veridiumid.sdk.fourf.UICustomization;
import com.veridiumid.sdk.fourf.onFourFFragmentReadyListener;
import com.veridiumid.sdk.model.help.Devices;
import com.veridiumid.sdk.support.ui.AspectRatioSafeFrameLayout;

/**
 * Default implementation of FourFUIInterface. Handles instructions sent from library, can make
 * requests to the library to cancel, pause, resume etc. Must call onFourFFragmentReadyListener's
 * on FourFFragmentReady once the fragment is inited.
 */
public class AuthenticatorFourFFragment extends Fragment implements FourFUIInterface, SurfaceHolder.Callback {
    
    private final boolean useMeter = true;
    private final boolean useBlobs = true;
    private final boolean useMitten = true;
    
    private AspectRatioSafeFrameLayout cameraLayout;
    private onFourFFragmentReadyListener listener;
    private TextView instruction_text;
    private SurfaceHolder roisSurfaceHolder;
    private Handler mainHandler;
    private Dialog dialog;
    private volatile boolean surfaceAvailable = false;
    private boolean shouldStayStill = false;
    private boolean isWantToShowProcessingDialog = false; //FIXME PLEASE RENAME
    
    private int previewWidth = 0;
    private int previewHeight = 0;
    
    private ImageView iv_arrowLeft;
    private RelativeLayout rl_meterLeft;
    private TextView tv_tooFarLeft;
    private TextView tv_tooCloseLeft;
    private ImageView iv_arrowRight;
    private RelativeLayout rl_meterRight;
    private TextView tv_tooFarRight;
    private TextView tv_tooCloseRight;
    private ImageView iv_arrowChosen;
    private RelativeLayout rl_meterChosen;
    private TextView tv_tooFarChosen;
    private TextView tv_tooCloseChosen;
    private ImageView iv_close;
    
    private CheckBox left_right_switch;
    private Button fake_switch_hand_button;
    private ImageView iv_handSide;
    private TextView tv_handside;
    
    public RelativeLayout screen_overlay;
    public boolean usingRightHand;
    
    private ImageView iv_img_finger_hint;
    
    private printToCapture print_to_capture = printToCapture.HAND_LEFT;
    private captureMode mCaptureMode;
    
    private boolean isConfigureFirstTime = true;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_veridium_fourf_security, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
    
    private void initView(View view) {
        mainHandler = new Handler(this.getContext().getMainLooper());
        cameraLayout = view.findViewById(R.id.camera_preview);
        instruction_text = view.findViewById(R.id.tv_placeYourFingers);
        iv_arrowLeft = view.findViewById(R.id.iv_arrowLeft);
        rl_meterLeft = view.findViewById(R.id.rl_meterLeft);
        tv_tooFarLeft = view.findViewById(R.id.tv_tooFarLeft);
        tv_tooCloseLeft = view.findViewById(R.id.tv_tooCloseLeft);
        iv_arrowRight = view.findViewById(R.id.iv_arrowRight);
        rl_meterRight = view.findViewById(R.id.rl_meterRight);
        tv_tooFarRight = view.findViewById(R.id.tv_tooFarRight);
        tv_tooCloseRight = view.findViewById(R.id.tv_tooCloseRight);
        iv_arrowChosen = iv_arrowLeft;
        rl_meterChosen = rl_meterLeft;
        tv_tooCloseChosen = tv_tooCloseLeft;
        tv_tooFarChosen = tv_tooFarLeft;
        
        screen_overlay = view.findViewById(R.id.screen_overlay);
        screen_overlay.setVisibility(View.VISIBLE);
        
        iv_img_finger_hint = view.findViewById(R.id.img_finger_hint);
        
        iv_handSide = view.findViewById(R.id.iv_handSide);
        tv_handside = view.findViewById(R.id.tv_handside);
        
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        SurfaceView roisSurface = (SurfaceView) inflater.inflate(R.layout.layout_component_roi_surface_authenticator, cameraLayout, false);
        cameraLayout.addView(roisSurface);
        roisSurface.setZOrderOnTop(true);
        roisSurfaceHolder = roisSurface.getHolder();
        roisSurfaceHolder.addCallback(this);
        roisSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        
        left_right_switch = view.findViewById(R.id.left_right_switch);
        fake_switch_hand_button = view.findViewById(R.id.fake_switch_hand_button);
        fake_switch_hand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FourFUIIntegrationWrapper.requestSwitchHand();
            }
        });
        
        iv_close = view.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FourFUIIntegrationWrapper.requestCancel();
            }
        });
        
        listener.onFourFFragmentReady();
    }
    
    @Override
    public void handleUIInstruction(uiInstruction instruction) {
        if (instruction == uiInstruction.InvalidROIS) {
            iv_arrowChosen.setVisibility(View.INVISIBLE);
        } else {
            iv_arrowChosen.setVisibility(View.VISIBLE);
        }
        
        tv_tooCloseChosen.setShadowLayer(0, 0, 0, Color.WHITE);
        tv_tooFarChosen.setShadowLayer(0, 0, 0, Color.WHITE);
        if (instruction == uiInstruction.RoiTooBig) {
            tv_tooCloseChosen.setShadowLayer(20, 0, 0, Color.WHITE);
            iv_arrowChosen.setImageDrawable(getResources().getDrawable(R.drawable.triangle_white));
        } else if (instruction == uiInstruction.RoiTooSmall) {
            tv_tooFarChosen.setShadowLayer(20, 0, 0, Color.WHITE);
            iv_arrowChosen.setImageDrawable(getResources().getDrawable(R.drawable.triangle_white));
        } else {
            iv_arrowChosen.setImageDrawable(getResources().getDrawable(R.drawable.triangle_green));
        }
        
        shouldStayStill = instruction == uiInstruction.Yes || instruction == uiInstruction.Wait;
        
        if (mCaptureMode == captureMode.Thumb) {
            if (shouldStayStill) {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.thumb_guide_green));
            } else {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.thumb_guide_red));
            }
        } else if (mCaptureMode == captureMode.Finger) {
            if (shouldStayStill) {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.digit_border_green));
            } else {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.digit_border_red));
            }
        }
        
        String textInstruction = "";
        switch (instruction) {
            case Wait:
                textInstruction = "Mantente quieto";
                break;
            case OutOfFocus:
                textInstruction = "Fuera de foco";
                break;
            case Yes:
                textInstruction = "Tomando foto";
                break;
            case RoiTooBig:
                textInstruction = "Mueva la mano más lejos";
                break;
            case RoiTooSmall:
                textInstruction = "Mueva la mano más cerca";
                break;
            case FingersTooFarApart:
                textInstruction = "Mantén tus dedos juntos";
                break;
            case FingersHigh:
                textInstruction = "Mueve tu mano hacia abajo";
                break;
            case FingersLow:
                textInstruction = "Mueve tu mano hacia arriba";
                break;
            case FingersFarLeft:
                textInstruction = "Mueve tu mano hacia la derecha";
                break;
            case FingersFarRight:
                textInstruction = "Mueve tu mano hacia la izquierda";
                break;
            case ImageRequestedWaiting:
                textInstruction = "Mantente quieto";
                break;
            case InvalidROIS:
                textInstruction = "Buscando...";
                break;
            case ImageTooDim:
                textInstruction = "";
                break;
        }
        instruction_text.setText(textInstruction);
    }
    
    private String getSwitchHandInstructionString() {
        // FIXME internationalise
        
        String message = "Por favor coloca tu " + (print_to_capture.isLeftHand() ? "izquierda" : "derecha");
        
        switch (mCaptureMode) {
            case Hand:
                return message + " mano.";
            case Thumb:
                return message + " pulgar.";
            case Finger:
                return message + "dedo con ID " + print_to_capture.getCode() + "."; // FIXME kinda bad
        }
        
        return getString(R.string.please_change_hands);
    }
    
    @Override
    public void handleBlockingUIInstruction(blockingUIInstruction instruction) {
    
        if (instruction == blockingUIInstruction.DISPLAY_HELP) {
            if (mCaptureMode == captureMode.Hand) {
                if (isConfigureFirstTime) {
                    showInstructionDialog();
                } else {
                    screen_overlay.setVisibility(View.INVISIBLE);
                    FourFUIIntegrationWrapper.handledBlockingUIInstruction(UserAction.NEXT);
                }
            } else {
                if (isConfigureFirstTime) {
                    showInstructionDialog();
                } else {
                    screen_overlay.setVisibility(View.INVISIBLE);
                    FourFUIIntegrationWrapper.handledBlockingUIInstruction(UserAction.NEXT);
                }
            }
            return;
        }
    
    
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.fourf_authenticator_ui_dialog_custom);
        
        
        TextView mainText = dialog.findViewById(R.id.tv_mainText);
        TextView smallText = dialog.findViewById(R.id.tv_smallMessage);
        Button cancel = dialog.findViewById(R.id.button_cancel);
        Button next = dialog.findViewById(R.id.button_next);
        ImageView image = dialog.findViewById(R.id.imageView);
        ImageView image2 = dialog.findViewById(R.id.imageView2);
        View verticalLine = dialog.findViewById(R.id.lineAcross2);
        View horizontalLine = dialog.findViewById(R.id.lineAcross1);
        
        next.setText(R.string.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FourFUIIntegrationWrapper.handledBlockingUIInstruction(UserAction.NEXT);
            }
        });
        
        
        cancel.setVisibility(View.GONE);
        verticalLine.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
        params.weight = 2.0f;
        next.setLayoutParams(params);
        
        
        switch (instruction) {
            case NONE:
                // Should never happen
                break;
            case SWITCH_CAPTURE_TARGET:
                // FIXME update UI options
                mainText.setText(getString(R.string.next_nchange_hands));
                smallText.setText(getSwitchHandInstructionString());
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.switch_hand_background)));
                image2.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.switch_hand_foreground)));
                dialog.show();
                break;
            case ENROLLMENT_STEP2_OF_2:
                mainText.setText(getString(R.string.success_nfirst_scan_complete));
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
                dialog.show();
                break;
            case ENROLLMENT_STEP2_OF_3:
                mainText.setText(getString(R.string.success_nfirst_scan_of_three_complete));
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
                dialog.show();
                break;
            case ENROLLMENT_STEP3_OF_3:
                mainText.setText(getString(R.string.success_nsecond_scan_of_three_complete));
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
                dialog.show();
                break;
            case INTERNAL_MISMATCH:
                mainText.setText(getString(R.string.requires_restart)); // FIXME use the right symbols
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
                dialog.show();
                break;
        }
        
    }
    
    @Override
    public void displayRealTimeInformation(RectF[] rois, float distance) {
        //Log.d("DISTANCE: ", "" + distance);
        //Log.d("rois: ", "" + rois[0] + ", " + rois[1] + ", " + rois[2] + ", " + rois[3]);
        
        
        if (useMeter) {
            float proportional_height_of_acceptable = 0.102f;
            iv_arrowChosen.setTranslationY((int) (distance * (float) rl_meterChosen.getHeight() * proportional_height_of_acceptable));
        }
        
        if (useBlobs) {
            if (!surfaceAvailable) {
                return;
            }
            
            if (roisSurfaceHolder == null) {
                return;
            }
            
            Canvas c = roisSurfaceHolder.lockCanvas();
            if (c == null) {
                // Surface isn't available to write on, so just return immediately.
                return;
            }
            
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            
            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            
            float canvasHeight = c.getHeight();
            float canvasWidth = c.getWidth();
            
            Path path = new Path();
            
            if (shouldStayStill) {
                paint.setColor(Color.argb(150, 0, 200, 0));
            } else {
                paint.setColor(Color.argb(150, 200, 0, 0));
            }
            
            
            for (int i = 0; i <= 3; i++) {
                
                RectF oval = new RectF(
                        rois[i].left * canvasWidth,
                        rois[i].top * canvasHeight,
                        rois[i].right * canvasWidth,
                        rois[i].bottom * canvasHeight
                );
                
                
                float r = Math.min(oval.height(), oval.width()) / 2;
                
                path.reset();
                path.addRoundRect(oval, r, r, Path.Direction.CW);
                c.drawPath(path, paint);
            }
            
            roisSurfaceHolder.unlockCanvasAndPost(c);
        }
    }
    
    @Override
    public void setPreviewResolution(int width, int height) {
        previewWidth = width;
        previewHeight = height;
    }
    
    @Override
    public void onProcessingStart() {
        isWantToShowProcessingDialog = true;
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                
                dialog = new Dialog(AuthenticatorFourFFragment.this.getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_processing_fourfauthenticator);
                
                final SlidingImageView slidingImageView = dialog.findViewById(R.id.processing_fingerprint_image_authenticator);
                final ImageView line = dialog.findViewById(R.id.splash_line_authenticator);
                
                
                final ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
                va.setRepeatMode(ValueAnimator.REVERSE);
                va.setRepeatCount(ValueAnimator.INFINITE);
                int mDuration = 800; //in millis
                va.setDuration(mDuration);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        slidingImageView.mPercent = ((float) animation.getAnimatedValue());
                        line.setTranslationX((((float) animation.getAnimatedValue()) * slidingImageView.getWidth()) - (line.getWidth() * 0.5f));
                    }
                    
                });
                va.start();
                
                dialog.setCancelable(false);
                dialog.show();
                
            }
        });
    }
    
    @Override
    public void onProcessingStop() {
        //FIXME implement
        isWantToShowProcessingDialog = false;
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if (!isWantToShowProcessingDialog) {
                    dialog.dismiss();
                }
            }
        }, 100);
    }
    
    @Override
    public void onTakePictureStart() {
    
    }
    
    @Override
    public void onTakePictureStop() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
            }
        });
    }
    
    @Override
    public void onImageAcceptance() {
    
    }
    
    @Override
    public void onImageRejection() {
    
    }
    
    private void showInstructionDialog() {
        AuthenticatorFourFInstructionalDialog dialog = new AuthenticatorFourFInstructionalDialog(this.getContext(), R.style.Theme_AppCompat_Light_NoActionBar);
        
        if (mCaptureMode == captureMode.Hand) {
            dialog.setUpFourFInstructions(this, true);
            dialog.typeCapture = true;
        } else {
            dialog.setUpFourFInstructions(this, false);
            dialog.typeCapture = false;
        }
        
        dialog.show();
    }
    
    
    @Override
    public void configureUI(captureMode mode, printToCapture print, RectF regionOfInterest, boolean canSwitchHand) {
        
        usingRightHand = !print.isLeftHand();
        
        /*if(isConfigureFirstTime){*/
        FourFUIIntegrationWrapper.requestHelp();
        /*isConfigureFirstTime = false;*/
        /*}*/
        
        if (canSwitchHand) {
            left_right_switch.setChecked(print.isLeftHand());
            left_right_switch.setVisibility(View.VISIBLE);
        } else {
            left_right_switch.setVisibility(View.INVISIBLE);
        }
        
        iv_arrowChosen.setVisibility(View.INVISIBLE);
        rl_meterChosen.setVisibility(View.INVISIBLE);
        iv_handSide.setVisibility(View.INVISIBLE);
        tv_handside.setVisibility(View.INVISIBLE);
        
        if (mode == captureMode.Hand) {
            if (print.isLeftHand()) {
                iv_arrowChosen = iv_arrowLeft;
                rl_meterChosen = rl_meterLeft;
                tv_tooCloseChosen = tv_tooCloseLeft;
                tv_tooFarChosen = tv_tooFarLeft;
            } else {
                iv_arrowChosen = iv_arrowRight;
                rl_meterChosen = rl_meterRight;
                tv_tooCloseChosen = tv_tooCloseRight;
                tv_tooFarChosen = tv_tooFarRight;
            }
            
            // Scale both the guide and the camera preview
            // We want the guide to take 50% of the screen, and then scale the camera view so that
            // the target region is similar.
            final float targetUIHandProportion = 0.5f; // How much of the camera view we want to
            // be filled with the hand when it is in the right place.
            float proportionOfHandWidth = regionOfInterest.width();
            float cameraScale = Math.max(targetUIHandProportion / proportionOfHandWidth, 1.0f);
            
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                cameraScale = 1;
                // The HTC causes no end of trouble because it is unable to scale *up*
                // the camera preview and instead just translates it off screen.
                // We suspect it is an android version issue.
            }
            
            
            String deviceName = Devices.getDeviceName();
            if (deviceName.startsWith("SM-N920") || deviceName.startsWith("Samsung SM-N920")) {
                // Samsung Galaxy Note 5; has a UI bug to do with surface views.
                // Falling back to scaling the guide instead of the camera view.
                // FIXME find root of bug.
                cameraScale = 1;
            }
            
            cameraLayout.setScaleX(cameraScale);
            cameraLayout.setScaleY(cameraScale);
            
            
            float actualUIHandProportion = cameraScale * proportionOfHandWidth;
            
            float hand_guide_proportion; // the proportion of the image that the actual fingers take up
            if (useMitten) {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.mitten));
                hand_guide_proportion = 0.23f / 1.2f; // this is measured by hand from the image:
                // It is the proportion of the image that the actual fingers take up, adjusted for the
                // fact that the mitten shouldn't be a snug fit.
            } else {
                iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.kirkhand_nq8));
                hand_guide_proportion = 0.145f; // this is measured by hand from the image.
            }
            
            
            float scaleOfGuide = (actualUIHandProportion / hand_guide_proportion);
            iv_img_finger_hint.setScaleX(print.isLeftHand() ? scaleOfGuide : -scaleOfGuide);
            iv_img_finger_hint.setScaleY(scaleOfGuide);
            iv_img_finger_hint.setTranslationY((float) ((regionOfInterest.centerY() - 0.5) * cameraScale * iv_img_finger_hint.getHeight()));
            iv_img_finger_hint.setTranslationX((float) ((regionOfInterest.centerX() - 0.5) * cameraScale * iv_img_finger_hint.getWidth()));
            
            if (useMeter) {
                rl_meterChosen.setVisibility(View.VISIBLE);
            }
        } else if (mode == captureMode.Thumb) {
            iv_handSide.setVisibility(View.VISIBLE);
            tv_handside.setVisibility(View.VISIBLE);
            if (print.isLeftHand()) {
                iv_handSide.setScaleX(1.0f);
                tv_handside.setText(getText(R.string.left_thumb));
            } else {
                iv_handSide.setScaleX(-1.0f);
                tv_handside.setText(getText(R.string.right_thumb));
            }
            
            iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.thumb_guide_red));
            float thumb_guide_proportion = 0.125f;//FIXME
            float proportionOfDesiredThumbWidth = regionOfInterest.height();
            float scaleOfGuide = proportionOfDesiredThumbWidth / thumb_guide_proportion;
            iv_img_finger_hint.setScaleX(scaleOfGuide);
            iv_img_finger_hint.setScaleY(scaleOfGuide);
        } else if (mode == captureMode.Finger) {
            iv_img_finger_hint.setImageDrawable(getResources().getDrawable(R.drawable.digit_border_red));
            float thumb_guide_proportion = 0.07f; //FIXME
            float proportionOfDesiredFingerWidth = regionOfInterest.width();
            float scaleOfGuide = proportionOfDesiredFingerWidth / thumb_guide_proportion;
            iv_img_finger_hint.setScaleX(print.isLeftHand() ? scaleOfGuide : -scaleOfGuide);
            iv_img_finger_hint.setScaleY(scaleOfGuide);
        } else if (mode == captureMode.AgentHand) {
            Log.e("configureUI", "Invalid capture mode: this UI does not work with agent capture");
            assert (false);
        }
        
        mCaptureMode = mode;
        print_to_capture = print;
    }
    
    @Override
    public AspectRatioSafeFrameLayout getPreviewHolder() {
        return cameraLayout;
    }
    
    @Override
    public void onReady(onFourFFragmentReadyListener onFourFFragmentReadyListener) {
        listener = onFourFFragmentReadyListener;
    }
    
    @Override
    public void dismiss(FourFFinishReason reason) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.fourf_authenticator_ui_dialog_custom);
        
        
        TextView mainText = dialog.findViewById(R.id.tv_mainText);
        TextView smallText = dialog.findViewById(R.id.tv_smallMessage);
        Button cancel = dialog.findViewById(R.id.button_cancel);
        Button next = dialog.findViewById(R.id.button_next);
        ImageView image = dialog.findViewById(R.id.imageView);
        ImageView image2 = dialog.findViewById(R.id.imageView2);
        View verticalLine = dialog.findViewById(R.id.lineAcross2);
        View horizontalLine = dialog.findViewById(R.id.lineAcross1);
        
        next.setText(R.string.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AuthenticatorFourFFragment.this.getActivity().finish();
            }
        });
        
        
        cancel.setVisibility(View.GONE);
        verticalLine.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
        params.weight = 2.0f;
        next.setLayoutParams(params);
        
        
        switch (reason) {
            case SUCCESS_ENROLL:
                mainText.setText(getString(R.string.success_nall_scans_complete));
                smallText.setText(getString(R.string.you_have_completed_enrollment));
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.encrypting_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.encrypting_foreground)));
                next.setText(getString(R.string.ok));
                cancel.setVisibility(View.GONE);
                verticalLine.setVisibility(View.GONE);
                dialog.show();
                break;
            case SUCCESS_AUTHENTICATE:
                mainText.setText(R.string.success);
                smallText.setText(R.string.fingerprint_recognition_complete);
                image.setImageDrawable(getResources().getDrawable(R.drawable.fourf_authenticator_ui_complete));
                dialog.show();
                break;
            case SUCCESS_EXPORT:
                /*mainText.setText(R.string.success);
                smallText.setText(R.string.fingerprint_capture_complete);
                image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
                image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
                dialog.show();*/
                AuthenticatorFourFFragment.this.getActivity().finish();
                break;
            case FAIL_AUTHENTICATE:
                mainText.setText(R.string.fail_authenticate);
                smallText.setText(R.string.fingerprint_recognition_failed);
                image.setImageDrawable(getResources().getDrawable(R.drawable.fourf_authenticator_ui_error));
                dialog.show();
                break;
            case FAIL_LIVENESS:
                mainText.setText(R.string.liveness_fail);
                smallText.setText(R.string.fingerprint_recognition_failed);
                image.setImageDrawable(getResources().getDrawable(R.drawable.fourf_authenticator_ui_error));
                dialog.show();
                break;
            case FAIL_ENROLL:
                mainText.setText(R.string.sorry);
                smallText.setText(R.string.enrollment_failed);
                image.setImageDrawable(getResources().getDrawable(R.drawable.fourf_authenticator_ui_error));
                dialog.show();
                break;
            case TIMEOUT:
                mainText.setText(R.string.sorry);
                smallText.setText(R.string.timeout);
                image.setImageDrawable(getResources().getDrawable(R.drawable.fourf_authenticator_ui_error));
                dialog.show();
                break;
            default:
                AuthenticatorFourFFragment.this.getActivity().finish();
                break;
        }
    }
    
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        roisSurfaceHolder = holder;
        holder.addCallback(this);
        surfaceAvailable = true;
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (roisSurfaceHolder != null) {
            roisSurfaceHolder.removeCallback(this);
        }
        roisSurfaceHolder = holder;
        holder.addCallback(this);
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceAvailable = false;
    }
    
}
