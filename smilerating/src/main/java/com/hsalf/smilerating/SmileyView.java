package com.hsalf.smilerating;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hsalf.smilerating.smileys.Good;
import com.hsalf.smilerating.smileys.Great;
import com.hsalf.smilerating.smileys.base.Smiley;

public class SmileyView extends View {
    private static final String TAG = "SmileyView";

    private Path mPath = new Path();
    private Path mDrawPath = new Path();
    private Matrix matrix = new Matrix();
    private Smiley mBad;
    private Smiley mGreat;
    private Paint mPaint = new Paint();

    private ValueAnimator mAnimator = new ValueAnimator();

    public SmileyView(Context context) {
        super(context);
        init();
    }

    public SmileyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mAnimator.setFloatValues(0f, 1f);
        mAnimator.setDuration(250);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBad.to(mGreat, mPath, animation.getAnimatedFraction());
                if (getMeasuredWidth() < getMeasuredHeight()) {
                    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
                    matrix.setScale(getMeasuredWidth(), getMeasuredWidth(), 0, 0);
                } else {
                    setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
                    matrix.setScale(getMeasuredHeight(), getMeasuredHeight(), 0, 0);
                }
                mPath.transform(matrix, mDrawPath);
                invalidate();
            }
        });

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.FILL);

        mBad = new Good();
        mGreat = new Great();
        setBackgroundColor(Color.parseColor("#232323"));
        mAnimator.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (widthMeasureSpec < heightMeasureSpec) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
            matrix.setScale(getMeasuredWidth(), getMeasuredWidth(), 0, 0);
        } else {
            setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
            matrix.setScale(getMeasuredHeight(), getMeasuredHeight(), 0, 0);
        }
        mPath.transform(matrix, mDrawPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mDrawPath, mPaint);
    }
}
