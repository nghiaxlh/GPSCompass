package com.bcg.gpscompass.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CompassImageView extends AppCompatImageView {
    private float mDegrees = 0;
    private int mX, mY;
    private boolean isDrag = false;

    public CompassImageView(@NonNull Context context) {
        super(context);
    }

    public CompassImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDegress(float degress) {
        mDegrees = degress;
    }

    public void setDrag(boolean isMainDrag) {
        this.isDrag = isMainDrag;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isDrag) {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            mX = w / 2;
            mY = h / 2;
        }
        canvas.rotate(mDegrees, mX, mY);
        super.onDraw(canvas);
    }
}
