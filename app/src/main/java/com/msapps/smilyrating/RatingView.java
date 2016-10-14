package com.msapps.smilyrating;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "RatingView";

    private Paint mPathPaint = new Paint();
    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
    private Path mDrawingPath = new Path();

    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();

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
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.BLACK);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.GREEN);
        mPointPaint2.setStyle(Paint.Style.FILL);

        mValueAnimator.setDuration(500);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    private void refreshPath(float fraction) {
        mDrawingPath.reset();

        mDrawingPath.moveTo(100, 500);
        // top curve
        mDrawingPath.cubicTo(150, 500, 350, 500, 400, 500);
        //Right curve
        mDrawingPath.cubicTo(450, 500, 450, 550, 400, mFloatEvaluator.evaluate(fraction, 600, 550));

        // bottom curve
        mDrawingPath.cubicTo(325, mFloatEvaluator.evaluate(fraction, 675, 550),
                mFloatEvaluator.evaluate(fraction, 175, 150),
                mFloatEvaluator.evaluate(fraction, 675, 550), 100,
                mFloatEvaluator.evaluate(fraction, 600, 550));

        // Left curve
        mDrawingPath.cubicTo(50, 550, 50, 500, 100, 500);

        mDrawingPath.close();
    }

    /*mDrawingPath.moveTo(100, 500);
    // top curve
    mDrawingPath.cubicTo(150, 500, 350, 500, 400, 500);
    //Right curve
    mDrawingPath.cubicTo(450, 500, 450, 550, 400, 550);

    // bottom curve
    mDrawingPath.cubicTo(350, 550, 150, 550, 100, 550);

    // Left curve
    mDrawingPath.cubicTo(50, 550, 50, 500, 100, 500);*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(mDrawingPath);
        canvas.drawPath(mDrawingPath, mPathPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        refreshPath(fraction);
        invalidate();
    }

    public void start() {
//        mValueAnimator.start();
    }

    public void stop() {
//        mValueAnimator.end();
    }

    public void setFraction(float fraction) {
        refreshPath(fraction);
        invalidate();
    }
}
