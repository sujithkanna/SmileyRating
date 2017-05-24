package com.example.smilyrating;


import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class SampleView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Path mDashPath = new Path();
    private Rect mViewBounds = new Rect();
    private Paint mDashPaint = new Paint();
    private ValueAnimator mAnimator = new ValueAnimator();

    private Curve mLine = new Curve();
    private Curve mCurve = new Curve();
    private Curve mDrawingObject = new Curve();
    private IntEvaluator mEvaluator = new IntEvaluator();

    public SampleView(Context context) {
        super(context);
        init();
    }

    public SampleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SampleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStrokeWidth(8);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        mDashPaint.setStrokeWidth(8);
        mDashPaint.setAntiAlias(true);
        mDashPaint.setColor(Color.RED);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 5));

        mAnimator.setDuration(2000);
        mAnimator.setIntValues(0, 100);
        mAnimator.addUpdateListener(this);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        mCurve.startX = mViewBounds.left + 100;
        mCurve.startY = mViewBounds.height() / 2;
        mCurve.endX = mViewBounds.right - 100;
        mCurve.endY = mViewBounds.height() / 2;

        mCurve.controlStartX = mViewBounds.left + 100;
        mCurve.controlStartY = (mViewBounds.height() / 2) - 300;
        mCurve.controlEndX = mViewBounds.right - 100;
        mCurve.controlEndY = (mViewBounds.height() / 2) - 300;

        mLine.startX = mViewBounds.left + 100;
        mLine.startY = mViewBounds.height() / 2;
        mLine.endX = mViewBounds.right - 100;
        mLine.endY = mViewBounds.height() / 2;

        mLine.controlStartX = mViewBounds.left + 100;
        mLine.controlStartY = (mViewBounds.height() / 2);
        mLine.controlEndX = mViewBounds.right - 100;
        mLine.controlEndY = (mViewBounds.height() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mPath.isEmpty()) {
            canvas.drawPath(mPath, mPaint);
            mPaint.setColor(Color.RED);
            canvas.drawCircle(mDrawingObject.controlStartX,
                    mDrawingObject.controlStartY, 7, mPaint);
            canvas.drawCircle(mDrawingObject.controlEndX,
                    mDrawingObject.controlEndY, 7, mPaint);
            mPaint.setColor(Color.BLACK);
            canvas.drawCircle(mDrawingObject.startX,
                    mDrawingObject.startY, 7, mPaint);
            canvas.drawCircle(mDrawingObject.endX,
                    mDrawingObject.endY, 7, mPaint);
            canvas.drawPath(mDashPath, mDashPaint);
        }

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (!mPath.isEmpty()) {
            mPath.reset();
        }
        float fraction = animation.getAnimatedFraction();
        mDrawingObject.startX = mEvaluator.evaluate(fraction, mLine.startX, mCurve.startX);
        mDrawingObject.startY = mEvaluator.evaluate(fraction, mLine.startY, mCurve.startY);
        mDrawingObject.endX = mEvaluator.evaluate(fraction, mLine.endX, mCurve.endX);
        mDrawingObject.endY = mEvaluator.evaluate(fraction, mLine.endY, mCurve.endY);
        mDrawingObject.controlStartX = mEvaluator.evaluate(fraction,
                mLine.controlStartX, mCurve.controlStartX);
        mDrawingObject.controlStartY = mEvaluator.evaluate(fraction,
                mLine.controlStartY, mCurve.controlStartY);
        mDrawingObject.controlEndX = mEvaluator.evaluate(fraction,
                mLine.controlEndX, mCurve.controlEndX);
        mDrawingObject.controlEndY = mEvaluator.evaluate(fraction,
                mLine.controlEndY, mCurve.controlEndY);
        mDrawingObject.fillPath(mPath);

        prepareDashPath(mDrawingObject);
        invalidate();
    }

    private void prepareDashPath(Curve drawingObject) {
        if (!mDashPath.isEmpty()) {
            mDashPath.reset();
        }
        mDashPath.moveTo(drawingObject.startX, drawingObject.startY);
        mDashPath.lineTo(drawingObject.controlStartX, drawingObject.controlStartY);

        mDashPath.moveTo(drawingObject.startX, drawingObject.startY);
        mDashPath.lineTo(drawingObject.endX, drawingObject.endY);

        mDashPath.moveTo(drawingObject.endX, drawingObject.endY);
        mDashPath.lineTo(drawingObject.controlEndX, drawingObject.controlEndY);
    }

    private static class Curve {
        public int startX;
        public int startY;
        public int endX;
        public int endY;

        public int controlStartX;
        public int controlStartY;
        public int controlEndX;
        public int controlEndY;

        public void fillPath(Path path) {
            path.moveTo(startX, startY);
            path.cubicTo(controlStartX, controlStartY, controlEndX, controlEndY, endX, endY);
        }
    }
}
