package com.hsalf.smilerating;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hsalf.smilerating.smileys.Bad;
import com.hsalf.smilerating.smileys.Good;
import com.hsalf.smilerating.smileys.Great;
import com.hsalf.smilerating.smileys.Okay;
import com.hsalf.smilerating.smileys.Terrible;
import com.hsalf.smilerating.smileys.base.Smiley;

public class SmileyView extends View {

    private static final String TAG = "SmileyView";

    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private Path mPath = new Path();

    private Smiley mBad = new Bad();
    private Smiley mGood = new Good();
    private Smiley mOkay = new Okay();
    private Smiley mGreat = new Great();
    private Smiley mTerrible = new Terrible();

    private int mCurrentSmileyIndex = 0;

    private Smiley[] mSmileys = new Smiley[]{
            mGreat, mGood, mOkay, mBad, mTerrible
    };

    private Paint mPathPaint = new Paint();
    private Paint mFacePaint = new Paint();

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
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawSmiley(animation.getAnimatedFraction());
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                to = null;
                from = null;
            }
        });

        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.FILL);

        mFacePaint.setAntiAlias(true);
        mFacePaint.setStyle(Paint.Style.FILL);

        drawSmiley(0, mSmileys[0], mSmileys[1]);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    OnSwipeTouchListener mSwipeListener = new OnSwipeTouchListener(getContext()) {
        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            setSmiley(mCurrentSmileyIndex, mCurrentSmileyIndex + 1);
        }

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            setSmiley(mCurrentSmileyIndex, mCurrentSmileyIndex - 1);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipeListener.onTouch(this, event);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (widthMeasureSpec < heightMeasureSpec) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
            drawSmiley(0, mSmileys[0], mSmileys[1]);
        } else {
            setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
            drawSmiley(0, mSmileys[0], mSmileys[1]);
        }
        setScaleForSmiley();
    }

    private void setScaleForSmiley() {
        for (int i = 0; i < mSmileys.length; i++) {
            Smiley smiley = mSmileys[i];
            smiley.scale(getMeasuredWidth());
        }
    }

    private Smiley to = null;
    private Smiley from = null;

    private void setSmiley(int from, int to) {
        if (to < 0 || to >= mSmileys.length) {
            return;
        }
        if (mAnimator.isRunning()) {
            return;
        }
        this.to = mSmileys[to];
        this.from = mSmileys[from];
        mCurrentSmileyIndex = to;
        mAnimator.start();

    }

    private void drawSmiley(float fraction) {
        drawSmiley(fraction, from, to);
    }

    private void drawSmiley(float fraction, Smiley from, Smiley to) {
        from.drawFace(to, mPath, fraction);

        mPathPaint.setColor(from.getDrawingColor());

        mFacePaint.setColor((Integer) mArgbEvaluator.evaluate(fraction,
                from.getFaceColor(), to.getFaceColor()));

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, mFacePaint);
        canvas.drawPath(mPath, mPathPaint);
    }
}
