package com.example.root.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;


public class TicketsVew extends View {
    private static final String TAG = "TicketsVew";
    private Matrix mAnimMatrix;
    private Paint mPaint;
    List<Card> mCards;

    private AnimatorSet mAnimatorSet;

    public TicketsVew(Context context) {
        super(context);
        init();
    }

    public TicketsVew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TicketsVew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mCards = new ArrayList<>();
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        Bitmap doc1 = BitmapFactory.decodeResource(getResources(), R.drawable.card1);
        Bitmap doc2 = BitmapFactory.decodeResource(getResources(), R.drawable.card2);
        Bitmap doc3 = BitmapFactory.decodeResource(getResources(), R.drawable.card3);

        mCards.add(new Card(doc1, 90, pxToDp(100), pxToDp(100)));
        mCards.add(new Card(doc2, 90, pxToDp(160)));
        mCards.add(new Card(doc3, 90, pxToDp(200)));

        mAnimMatrix = new Matrix();
        mAnimatorSet = new AnimatorSet();
        animateMyView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Card card : mCards) {
            drawCard(canvas, card);
        }
    }

    private void drawCard(Canvas canvas, Card card) {
        Bitmap image = card.image;
        mAnimMatrix.setTranslate(card.currentPos, getHeight() - image.getHeight());
        mAnimMatrix.postRotate(card.imageAngle, card.currentPos + image.getWidth() / 2, getHeight() - image.getHeight() / 2);
        canvas.drawBitmap(card.image, mAnimMatrix, mPaint);


        //drawPlot(canvas);
    }

    private void drawPlot(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 2, mPaint);
    }

    private void animateMyView() {
        List<Animator> animators = new ArrayList<>();
        for (Card card : mCards) {
            AnimatorSet cardAnimatorSet = new AnimatorSet();
            cardAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(card, "imageAngle", 0),
                    ObjectAnimator.ofFloat(card, "currentPos", card.startXPos, card.endXPos)
                    );
            animators.add(cardAnimatorSet);
            cardAnimatorSet.setDuration(600);
            cardAnimatorSet.setInterpolator(new AccelerateInterpolator());
        }

        mAnimatorSet.playSequentially(animators);
        mAnimatorSet.start();
    }

    public void restart() {
        animateMyView();
    }

    public float pxToDp(int px) {
        Log.d(TAG, "pxToDp: " + getResources().getDisplayMetrics().density);
        return px * getResources().getDisplayMetrics().density;
    }

    private class Card {
        Bitmap image;
        float imageAngle;
        float startXPos;
        float endXPos;
        float currentPos;

        Card(Bitmap image, float imageAngle, float startXPos, float endXPos) {
            this.image = image;
            this.imageAngle = imageAngle;
            this.startXPos = startXPos;
            this.endXPos = endXPos;
            this.currentPos = startXPos;
        }

        Card(Bitmap image, float imageAngle, float endXPos) {
            this(image, imageAngle, - image.getHeight(), endXPos);
        }



        public float getImageAngle() {
            return imageAngle;
        }

        public void setImageAngle(float imageAngle) {
            this.imageAngle = imageAngle;
            TicketsVew.this.invalidate();
        }

        public float getCurrentPos() {
            return currentPos;
        }

        public void setCurrentPos(float currentPos) {
            this.currentPos = currentPos;
        }
    }
}
