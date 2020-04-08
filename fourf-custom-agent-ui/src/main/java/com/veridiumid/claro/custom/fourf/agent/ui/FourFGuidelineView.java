package com.veridiumid.claro.custom.fourf.agent.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import androidx.core.content.res.ResourcesCompat;

import com.veridiumid.claro.custom.fourf.R;
import com.veridiumid.sdk.fourf.FourFUIInterface;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FourFGuidelineView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = FourFGuidelineView.class.getSimpleName();
    private final Typeface mTypeface;

    private Handler mMainHandler;
    private RectF mFourFRegionOfInterest;
    private RectF mRectF;
    private FourFUIInterface.printToCapture mPrintToCapture;

    private Bitmap mHandBitmap;
    private Bitmap mSuccessBitmap;

    private Paint mPaint;
    private final Xfermode mXorXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

    private PathEffect mGuideStrokePathEffect;
    private float mGuideStrokeDashWidth;
    float mGuideStrokePathGap;
    private float mGuideStrokePhase;
    private ValueAnimator mGuideAnimator;

    private Executor mDrawExecutor;

    private Animator mCountdownAnimator;
    private String mCountdownStep;
    private boolean mCounterVisible;

    private PorterDuffColorFilter mHandGuideColorTint = new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);

    private boolean mIsSuccessful;

    public FourFGuidelineView(Context context) {
        this(context, null);
    }

    public FourFGuidelineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourFGuidelineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mRectF = new RectF();
        mDrawExecutor = Executors.newSingleThreadExecutor();
        mTypeface = ResourcesCompat.getFont(getContext(), R.font.roboto_regular);

        mMainHandler = new Handler();
        mGuideStrokePathGap = getResources().getDimensionPixelSize(R.dimen.fourf_guide_dash_gap);
        mGuideStrokeDashWidth = getResources().getDimensionPixelSize(R.dimen.fourf_guide_stroke_width);
        getHolder().addCallback(this);
    }


    public synchronized void setFourFConfiguration(RectF regionOfInterest, FourFUIInterface.printToCapture printToCapture) {

        mFourFRegionOfInterest = regionOfInterest;
        mPrintToCapture = printToCapture;

        float left = regionOfInterest.left * getWidth();
        float right = regionOfInterest.right * getWidth();

        if (mCountdownAnimator != null && mCountdownAnimator.isRunning()) {
            mCountdownAnimator.cancel();
        }

        if (mGuideAnimator != null && mGuideAnimator.isRunning()) {
            mGuideAnimator.cancel();
        }

        if (mHandBitmap != null) {
            mHandBitmap.recycle();
        }

        mHandBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_hand);

        Bitmap bitmapToRecycle;
        // Preparing Hand Guide bitmap to draw
        float handGuideScaleFactor = (right - left) * 1.21f / mHandBitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.setScale(handGuideScaleFactor, handGuideScaleFactor, 0.5f * mHandBitmap.getWidth(), 0.5f * mHandBitmap.getHeight());
        matrix.postRotate(180, 0.5f * mHandBitmap.getWidth(), 0.5f * mHandBitmap.getHeight());

        if (!printToCapture.isLeftHand()) {
            matrix.postScale(-1, 1);
        }

        bitmapToRecycle = mHandBitmap;
        mHandBitmap = Bitmap.createBitmap(bitmapToRecycle, 0, 0, bitmapToRecycle.getWidth(), bitmapToRecycle.getHeight(), matrix, false);
        bitmapToRecycle.recycle();

        requestRender();
    }

    public void startCountDownTimer() {
        mMainHandler.post(() -> {
            if (mCountdownAnimator != null) {
                mCountdownAnimator.cancel();
                mCountdownAnimator.removeAllListeners();
            }
            mCounterVisible = true;
            ValueAnimator valueAnimator = ValueAnimator.ofInt(3, 0);
            valueAnimator.addUpdateListener(animation -> {
                String newStep = String.valueOf(Math.max((int) animation.getAnimatedValue(), 1)); // don't show zero
                if (!newStep.equals(mCountdownStep)) {
                    mCountdownStep = newStep;
                    requestRender();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCountdownAnimator = null;
                }
            });
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(2500);
            valueAnimator.start();
            mCountdownAnimator = valueAnimator;
        });
    }

    public void stopCountDownTimer() {
        if (mCountdownAnimator != null && mCountdownAnimator.isRunning()) {
            mCountdownAnimator.cancel();
        }
        mCounterVisible = false;
        requestRender();
    }

    public synchronized void setSuccessful() {
        if (mFourFRegionOfInterest == null)
            return;

        if (mSuccessBitmap != null)
            mSuccessBitmap.recycle();

        mIsSuccessful = true;

        float top = mFourFRegionOfInterest.left * getWidth();
        float bottom = mFourFRegionOfInterest.right * getWidth();
        mSuccessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fourf_success);
        float scaleFactor = (bottom - top) * 0.75f / mSuccessBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setScale(scaleFactor, scaleFactor, 0.5f * mSuccessBitmap.getWidth(), 0.5f * mSuccessBitmap.getHeight());
        matrix.postRotate(180, 0.5f * mSuccessBitmap.getWidth(), 0.5f * mSuccessBitmap.getHeight());
        Bitmap recycleBitmap = mSuccessBitmap;
        mSuccessBitmap = Bitmap.createBitmap(mSuccessBitmap, 0, 0, mSuccessBitmap.getWidth(), mSuccessBitmap.getHeight(), matrix, false);
        recycleBitmap.recycle();
        requestRender();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        try {
            if (mFourFRegionOfInterest == null)
                return;

            float left = getWidth() * mFourFRegionOfInterest.left;
            float right = getWidth() * mFourFRegionOfInterest.right;
            float top = getHeight() * mFourFRegionOfInterest.top;
            float bottom = getHeight() * mFourFRegionOfInterest.bottom;

            float cx = (right + left) / 2f;
            float cy = (bottom + top) / 2f;
            float radius = (right - left) / 1.85f;

            // We invalidate all pixels first to create a rendering from scratch.
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            // Drawing transparent background
            canvas.drawColor(Color.argb(191, 0, 0, 0));

            // Drawing the clear window for the fourf region of interest with green dash stroke
            mPaint.reset();
            mPaint.setColor(Color.BLACK);
            mPaint.setXfermode(mXorXfermode);
            mRectF.set(cx - radius,
                    cy - radius,
                    cx + radius,
                    cy + radius);

            canvas.drawCircle(cx, cy, radius, mPaint);
            mPaint.reset();
            mPaint.setStyle(Paint.Style.STROKE);
            mGuideStrokePathEffect = new DashPathEffect(new float[]{mGuideStrokePathGap, mGuideStrokePathGap}, mGuideStrokePhase);
            mPaint.setPathEffect(mGuideStrokePathEffect);
            mPaint.setStrokeWidth(mGuideStrokeDashWidth);
            mPaint.setColor(Color.rgb(0, 255, 0));
            canvas.drawCircle(cx, cy, radius, mPaint);

            // draw the hand guide
            mPaint.reset();
            mPaint.setAlpha(77);
            mPaint.setColorFilter(mHandGuideColorTint);
            canvas.save();
            canvas.rotate(180, 0.5f * getWidth(), 0.5f * getHeight());
            if (mPrintToCapture.isLeftHand()) {
                canvas.drawBitmap(mHandBitmap, right - mHandBitmap.getWidth(), cy - mHandBitmap.getHeight() * 0.15f, mPaint);
            } else {
                canvas.drawBitmap(mHandBitmap, left, cy - mHandBitmap.getHeight() * 0.15f, mPaint);
            }

            canvas.restore();

            if (mCounterVisible) {
                mPaint.reset();
                mPaint.setColor(getResources().getColor(R.color.light_blue));

                float counterRadius = 0.45f * Math.min(mRectF.bottom - mRectF.top, mRectF.right - mRectF.left);
                canvas.drawCircle(mRectF.left + 0.5f * (mRectF.right - mRectF.left), mRectF.top + 0.5f * (mRectF.bottom - mRectF.top), counterRadius, mPaint);

                mPaint.reset();
                mPaint.setTextSize(0.75f * counterRadius);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setColor(Color.WHITE);
                mPaint.setTypeface(mTypeface);
                canvas.save();
                canvas.rotate(180, 0.5f * getWidth(), 0.5f * getHeight());
                canvas.drawText(mCountdownStep, mRectF.left + 0.5f * (mRectF.right - mRectF.left), getHeight() - mRectF.bottom + 0.5f * (mRectF.bottom - mRectF.top) + 0.27f * counterRadius, mPaint);
                canvas.restore();
            }

            if (mIsSuccessful) {
                mPaint.reset();
                canvas.drawBitmap(mSuccessBitmap,
                        mRectF.left + 0.5f * (mRectF.right - mRectF.left) - 0.5f * mSuccessBitmap.getWidth(),
                        mRectF.top + 0.5f * (mRectF.bottom - mRectF.top) - 0.5f * mSuccessBitmap.getHeight(),
                        mPaint);
            }

        } catch (Exception e) {
            Log.d(TAG, "FourFGuideView draw exception");
        }
    }

    private void requestRender() {
        mDrawExecutor.execute(() -> {
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                draw(canvas);
            } catch (Exception e) {
                Log.d(TAG, "FourFGuideView draw exception");
            } finally {
                if (canvas != null)
                    getHolder().unlockCanvasAndPost(canvas);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSuccessBitmap != null)
            mSuccessBitmap.recycle();

        if (mHandBitmap != null)
            mHandBitmap.recycle();

        if (mCountdownAnimator != null && mCountdownAnimator.isRunning()) {
            mCountdownAnimator.cancel();
            mCountdownAnimator = null;
        }

        if (mGuideAnimator != null && mGuideAnimator.isRunning()) {
            mGuideAnimator.cancel();
            mGuideAnimator = null;
        }
    }

    public final void setHandGuideColorFilter(int color, PorterDuff.Mode mode) {
        mHandGuideColorTint = new PorterDuffColorFilter(color, mode);
        requestRender();
    }

    public final void setHandGuideColorFilter(int color) {
        setHandGuideColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}