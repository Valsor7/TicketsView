package com.example.root.myapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MyTabs extends TabLayout implements TabLayout.OnTabSelectedListener {
    private static final String TAG = "MyTabs";
    private Paint mPaint;
    private int mColor;
    private Shader mGradientShader;
    private float movementPosition;
    private ObjectAnimator mTabIndicatorAnimator;
    private int mSelectedTab;

    public MyTabs(Context context) {
        super(context);
    }

    public MyTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTabs(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addOnTabSelectedListener(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColor = Color.YELLOW;
        mTabIndicatorAnimator = new ObjectAnimator();
        mTabIndicatorAnimator.setPropertyName("movementPosition");
        mTabIndicatorAnimator.setTarget(this);
        mTabIndicatorAnimator.setDuration(200);
        mTabIndicatorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.d(TAG, "draw: tab " + getSelectedTabPosition());

        drawPlot(canvas);
    }


    private void drawPlot(Canvas canvas) {
        mGradientShader = new LinearGradient(0, 0, getWidth() / 2 + movementPosition, 0, Color.parseColor("#A7560D"), Color.parseColor("#D0B35B"), Shader.TileMode.CLAMP);
        mPaint.setShader(mGradientShader);
        float indicatorTop = getHeight() - getHeight() / 12;
        canvas.drawRect(movementPosition, indicatorTop, getWidth() / 2 + movementPosition, getHeight(), mPaint);
    }

    @Override
    public void onTabSelected(Tab tab) {
        if (mSelectedTab != tab.getPosition()) {
            mSelectedTab = tab.getPosition();
            moveTabAnimation();
        }
        Log.d(TAG, "onTabSelected() called with: tab = [" + tab.getPosition() + "]");
    }

    private void moveTabAnimation() {
        if (mTabIndicatorAnimator.isRunning() || mSelectedTab == 0) {
            mTabIndicatorAnimator.reverse();
        } else {
            mTabIndicatorAnimator.setFloatValues(0, getWidth() / 2);
            mTabIndicatorAnimator.start();
        }

    }

    @Override
    public void onTabUnselected(Tab tab) {
        Log.d(TAG, "onTabUnselected() called with: tab = [" + tab.getPosition() + "]");
    }

    @Override
    public void onTabReselected(Tab tab) {
        Log.d(TAG, "onTabReselected() called with: tab = [" + tab.getPosition() + "]");
    }

    public float getMovementPosition() {
        return movementPosition;
    }

    public void setMovementPosition(float movementPosition) {
        this.movementPosition = movementPosition;
        invalidate();
    }
}
