package com.veridiumid.sdk.fourf.ui.authenticator;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.veridiumid.sdk.fourf.FourFUIIntegrationWrapper;
import com.veridiumid.sdk.fourf.FourFUIInterface;


public class AuthenticatorFourFInstructionalDialog extends Dialog {

    public AuthenticatorFourFInstructionalDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    private Context mContext;
    private MediaPlayer instructionMediaPlayer;
    public boolean typeCapture;
    
    public void setUpFourFInstructions(final AuthenticatorFourFFragment fourfFragment, final boolean type) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.layout_fourf_instructional_authenticator);

        this.setOnCancelListener(new Dialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface i) {
                FourFUIIntegrationWrapper.handledBlockingUIInstruction(FourFUIInterface.UserAction.CANCEL);
                dismiss();
            }
        });

        Button btn_getStarted = getWindow().getDecorView().findViewById(R.id.button_next);
        Button btn_cancel = getWindow().getDecorView().findViewById(R.id.button_Cancel);




        btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fourfFragment.screen_overlay.setVisibility(View.INVISIBLE);
                FourFUIIntegrationWrapper.handledBlockingUIInstruction(FourFUIInterface.UserAction.NEXT);
                dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });


        final TextureView instructionalVideoView = getWindow().getDecorView().findViewById(R.id.instructional_animation);
        instructionalVideoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    if (type)
                        instructionMediaPlayer = MediaPlayer.create(fourfFragment.getContext(), R.raw.animation);
                    else
                        instructionMediaPlayer = MediaPlayer.create(fourfFragment.getContext(), R.raw.animation_thumb);

                    instructionMediaPlayer.setSurface(new Surface(surface));
                    instructionMediaPlayer.setLooping(true);
                    instructionMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    instructionMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error playing intro video: " + e.getMessage());
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        getWindow().getAttributes().windowAnimations = R.style.Theme_AppCompat_Dialog;
        setCancelable(true);
    }
}
