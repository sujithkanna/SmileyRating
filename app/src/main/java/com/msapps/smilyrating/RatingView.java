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

        mValueAnimator.setDuration(500);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());

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
        if (mShowLines) {
            mSmileys.drawLines(canvas, mPointPaint2);
        }
        Smile smile = mSmileys.getSmile(GREAT);
        canvas.drawPath(smile.fillPath(mDrawingPath), mPathPaint);
        if (mShowPoints) {
            smile.drawPoints(canvas, mPointPaint1);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        invalidate();
    }

    public void start() {

    }

    public void stop() {

    }


}
