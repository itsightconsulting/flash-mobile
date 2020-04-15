package com.veridiumid.sdk.fourf.ui.authenticator;

/**
 * Created by lewiscarney on 20/12/2017.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class SlidingImageView extends AppCompatImageView {

    public SlidingImageView(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.MATRIX);
    }
    public SlidingImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    private Rect mRect;
    public float mPercent = 0;
    public int currentXPos;

    @Override
    protected void onDraw(Canvas canvas) {

        if(mRect == null){
            mRect = canvas.getClipBounds();
        }
        canvas.getClipBounds(mRect);
        currentXPos = mRect.right;
        int i = Math.round((float) mRect.width() * mPercent);
        mRect.right = i + mRect.left;
        canvas.clipRect(mRect);

        super.onDraw(canvas);
        invalidate();

    }


}