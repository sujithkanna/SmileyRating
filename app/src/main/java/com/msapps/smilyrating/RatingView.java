package com.msapps.smilyrating;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends BaseRating implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "RatingView";

    private boolean mShowLines = false;
    private boolean mShowPoints = false;

    private Paint mPathPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();

    private Paint mPointPaint1 = new Paint();

    private Paint mPointPaint2 = new Paint();
    private Path mDrawingPath = new Path();
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();

    @BaseRating.Smiley
    private int mode = RatingView.GOOD;

    private Smileys mSmileys;
    private float mTranslation = 0;

    public RatingView(Context context) {
        super(context);
        init();
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.parseColor("#232323"));
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.RED);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.BLUE);
        mPointPaint2.setStyle(Paint.Style.STROKE);


        int color = Color.BLACK;
        mBackgroundPaint.setColor(Color.argb(50, Color.red(color),
                Color.green(color), Color.blue(color)));
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mValueAnimator.setDuration(1000);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);

        mSmileys = Smileys.newInstance();
        setFraction(0);
    }

    public void showPoints(boolean b) {
        mShowPoints = b;
        invalidate();
    }

    public void showLines(boolean b) {
        mShowLines = b;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(120 + mTranslation, 435, 20, mPathPaint);
        canvas.drawCircle(220 + mTranslation, 435, 20, mPathPaint);
        canvas.drawCircle(175 + mTranslation, 475, 160, mBackgroundPaint);
        if (!mDrawingPath.isEmpty()) {
            canvas.drawPath(mDrawingPath, mPathPaint);
        }
        /*Smile smile = mSmileys.getSmile(mode);
        canvas.drawPath(smile.fillPath(mDrawingPath), mPathPaint);
        if (mShowPoints) {
            smile.drawPoints(canvas, mPointPaint1);
            canvas.drawCircle(175, 540, 10, mPointPaint2);
        }*/
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        setFraction(fraction);
    }

    public void start() {
//        mValueAnimator.start();
    }

    public void stop() {
//        mValueAnimator.end();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }

    public void switchMode() {
        if (GREAT == mode) {
            mode = GOOD;
        } else if (mode == GOOD) {
            mode = GREAT;
        }
        invalidate();
    }

    public void setFraction(float fraction) {
        mTranslation = mFloatEvaluator.evaluate(fraction, 0, 350);
        transformSmile(fraction, mDrawingPath,
                mSmileys.getSmile(GOOD), mSmileys.getSmile(BAD), mFloatEvaluator);
        invalidate();
    }
}
