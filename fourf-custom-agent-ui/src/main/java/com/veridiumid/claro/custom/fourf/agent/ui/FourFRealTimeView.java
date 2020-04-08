package com.veridiumid.claro.custom.fourf.agent.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.veridiumid.claro.custom.fourf.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FourFRealTimeView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = FourFGuidelineView.class.getSimpleName();

    private volatile boolean VISIBLE;

    private Bitmap mArrowOutBitmap;
    private Bitmap mArrowInBitmap;
    private PointF arrow_location;
    private enum ArrowState {
        NONE, IN, OUT
    }
    private ArrowState arrow_state;

    private RectF[] mRectFingerROI;       // location of fingers ([0,1])
    private Bitmap mFingerprintBitmap;
    private int print_color;

    private int textSize = 40;
    private String instruction = "";
    private PointF instruction_location;

    private Paint mPaint;

    private Executor mDrawExecutor;

    public FourFRealTimeView(Context context) {
        this(context, null);
    }

    public FourFRealTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourFRealTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRectFingerROI = new RectF[4];
        mPaint = new Paint();
        mDrawExecutor = Executors.newSingleThreadExecutor();
        getHolder().addCallback(this);
    }

    public synchronized void setConfig(RectF regionOfInterest){

        float left = regionOfInterest.left * getWidth();
        float right = regionOfInterest.right * getWidth();
        float top = regionOfInterest.top * getHeight();
        float bottom = regionOfInterest.bottom * getHeight();

        if (mFingerprintBitmap != null)
            mFingerprintBitmap.recycle();

        if (mArrowOutBitmap != null)
            mArrowOutBitmap.recycle();

        if (mArrowInBitmap != null)
            mArrowInBitmap.recycle();

        float fingerprintScaleFactor = (right - left) / 7f;
        mFingerprintBitmap = loadBitmapAndScale(fingerprintScaleFactor, R.drawable.ic_fingerprint);

        float arrowScaleFactor = (right - left) / 2f;
        mArrowInBitmap = loadBitmapAndScale(arrowScaleFactor,  R.drawable.arrow_in_nq8);
        mArrowOutBitmap = loadBitmapAndScale(arrowScaleFactor,  R.drawable.arrow_out_nq8);

        // set up where to draw components
        instruction_location = new PointF((left+right)/2f, bottom + (getHeight() * 0.05f) + textSize);
        arrow_location = new PointF((left+right)/2f - mArrowInBitmap.getWidth() * 0.5f,
                bottom - mArrowInBitmap.getHeight()*0.6f);

        arrow_state = ArrowState.NONE;
        VISIBLE = true;
    }

    private Bitmap loadBitmapAndScale(float scalefactor , int resource_id){
        Matrix matrix = new Matrix();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resource_id);
        float scale = scalefactor / bitmap.getWidth();
        matrix.setScale(scale, scale, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        //matrix.postRotate(180, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        Bitmap scaled_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bitmap.recycle();
        return scaled_bitmap;
    }

    public void updateRealTimeInformation(RectF[] fingerROIs){
        mRectFingerROI = fingerROIs;
        requestRender();
    }

    public void setROIColourBad(){
        print_color = Color.argb(250, 200, 0, 0);
    }

    public void setROIColourGood(){
        print_color = Color.argb(250, 0, 200, 0);
    }

    public void setInstructionalText(String t){
        instruction = t;
    }

    public void clearArrows(){
        arrow_state = ArrowState.NONE;
    }

    public void setArrowInwards(){
        arrow_state = ArrowState.IN;
    }

    public void setArrowOutwards(){
        arrow_state = ArrowState.OUT;
    }

    /**
     * Draw a finger ROI as a Bitmap
     * ROI co-ords are in portrait screen space, so you can use them directly.
     * @param canvas
     * @param roi finger roi as a proportion of preview frame resolution (0,1)
     */
    private void drawFingerROIBitmap(Canvas canvas, RectF roi){

        if((roi.left - roi.right) == 0 ) // the rois are size zero
            return;

        float left = getWidth() * roi.left;
        float right = getWidth() * roi.right;
        float top = getHeight() * roi.top;
        float bottom = getHeight() * roi.bottom;

        float cx = (right + left) / 2f;
        float cy = (bottom + top) / 2f;

        canvas.drawBitmap(mFingerprintBitmap,
                cx - mFingerprintBitmap.getWidth() * 0.5f,
                cy - mFingerprintBitmap.getHeight() * 0.5f, mPaint);
    }

    private void drawIntructionalText(Canvas canvas){
        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(instruction, instruction_location.x, instruction_location.y, mPaint);
    }

    private void drawArrows(Canvas canvas){
        mPaint.reset();

        switch (arrow_state){
            case NONE:
                break;
            case IN:
                canvas.drawBitmap(mArrowInBitmap,
                        arrow_location.x,
                        arrow_location.y, mPaint);
                break;
            case OUT:
                canvas.drawBitmap(mArrowOutBitmap,
                        arrow_location.x,
                        arrow_location.y, mPaint);
        }
    }

    /**
     * Draw anything you like to get your co-ord bearings. Normally not used.
     * @param canvas
     */
    private void drawDebug(Canvas canvas){
        mPaint.reset();
        mPaint.setColor(Color.argb(250, 200, 0, 0));
        mPaint.setStyle(Paint.Style.STROKE);

        Path borderPath = new Path();

        RectF drawScaledFocusRect = new RectF(
                0f,//left
                0f, //top
                getWidth()-50, //right
                getHeight()-1 // bottom
        );

        borderPath.addRect(drawScaledFocusRect, Path.Direction.CW);
        canvas.drawPath(borderPath, mPaint);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        try {
            if (mRectFingerROI == null || mArrowInBitmap == null || mArrowOutBitmap == null
               || mFingerprintBitmap == null)
                return;

            // clear everything
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            //drawDebug(canvas);

            drawIntructionalText(canvas);

            // draw finger bitmaps (ROIs)
            mPaint.reset();
            ColorFilter filter = new PorterDuffColorFilter(print_color, PorterDuff.Mode.SRC_IN);
            mPaint.setColorFilter(filter);

            for (RectF roi : mRectFingerROI) {
                drawFingerROIBitmap(canvas, roi);
            }

            drawArrows(canvas);

        }catch (Exception e){
            Log.d(TAG, "FourFRealTimeView draw exception");
        }
    }

    private void requestRender() {
        mDrawExecutor.execute(() -> {
            if(!VISIBLE)
                return;

            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                draw(canvas);
            }catch (Exception e){
                Log.d(TAG, "FourRealTimeView draw exception");
            }finally {
                if(canvas != null)
                    getHolder().unlockCanvasAndPost(canvas);
            }
        });
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
        VISIBLE = false;
    }

    public void show(){
        this.setVisibility(View.VISIBLE);
        VISIBLE = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        VISIBLE = false;

        if (mFingerprintBitmap != null)
            mFingerprintBitmap.recycle();

        if (mArrowOutBitmap != null)
            mArrowOutBitmap.recycle();

        if (mArrowInBitmap != null)
            mArrowInBitmap.recycle();
    }
}
