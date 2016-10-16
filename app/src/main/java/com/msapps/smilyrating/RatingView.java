package com.msapps.smilyrating;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends BaseRating implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "RatingView";

    private boolean mShowLines = false;
    private boolean mShowPoints = false;

    private Paint mPathPaint = new Paint();

    private Paint mPointPaint1 = new Paint();

    private Paint mPointPaint2 = new Paint();
    private Path mDrawingPath = new Path();
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();

    @BaseRating.Smiley
    private int mode = RatingView.GREAT;

    private Smileys mSmileys;

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
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.parseColor("#232323"));
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.RED);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.BLUE);
        mPointPaint2.setStyle(Paint.Style.STROKE);

        mValueAnimator.setDuration(700);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);

        mSmileys = Smileys.newInstance();
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
        mSmileys.onChangeLayout(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(110, 425, 26, mPointPaint1);
        canvas.drawCircle(240, 425, 26, mPointPaint1);
        /*if (!mDrawingPath.isEmpty()) {
            canvas.drawPath(mDrawingPath, mPathPaint);
        }*/
        Smile smile = mSmileys.getSmile(mode);
        canvas.drawPath(smile.fillPath(mDrawingPath), mPathPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        transformSmile(fraction, mDrawingPath,
                mSmileys.getSmile(GREAT), mSmileys.getSmile(GOOD), mFloatEvaluator);
        invalidate();
    }

    public void start() {
//        mValueAnimator.start();
    }

    public void stop() {
//        mValueAnimator.end();
    }


    public void switchMode() {
        if (GREAT == mode) {
            mode = GOOD;
        } else if (mode == GOOD) {
            mode = GREAT;
        }
        invalidate();
    }
}
