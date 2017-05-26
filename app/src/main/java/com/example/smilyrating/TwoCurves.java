package com.example.smilyrating;


import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TwoCurves extends View {


    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Path mDashPath = new Path();
    private Rect mViewBounds = new Rect();
    private Paint mDashPaint = new Paint();
    private Point mJoint;
    private Point mTouchPoint1;
    private Point mTouchPoint2;
    private Point mLockedPoint;
    private Point mBufferPoint = new Point();

    private Curve mCurve1 = new Curve();
    private Curve mCurve2 = new Curve();
    private IntEvaluator mEvaluator = new IntEvaluator();

    public TwoCurves(Context context) {
        super(context);
        init();
    }

    public TwoCurves(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwoCurves(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStrokeWidth(8);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);

        mDashPaint.setStrokeWidth(8);
        mDashPaint.setAntiAlias(true);
        mDashPaint.setColor(Color.RED);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 5));

        mJoint = mCurve1.end;
        mTouchPoint1 = mCurve1.controlEnd;
        mTouchPoint2 = mCurve2.controlStart;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        mCurve1.start.x = mViewBounds.left + 100;
        mCurve1.start.y = mViewBounds.height() / 2;
        mCurve1.end.x = mViewBounds.right / 2;
        mCurve1.end.y = mViewBounds.height() / 2;

        mCurve1.controlStart.x = mViewBounds.left + 100;
        mCurve1.controlStart.y = (mViewBounds.height() / 2) - 300;
        mCurve1.controlEnd.x = mViewBounds.right / 2;
        mCurve1.controlEnd.y = (mViewBounds.height() / 2) - 300;

        mCurve2.start.x = mViewBounds.right / 2;
        mCurve2.start.y = mViewBounds.height() / 2;
        mCurve2.end.x = mViewBounds.right - 100;
        mCurve2.end.y = mViewBounds.height() / 2;

        mCurve2.controlStart.x = mViewBounds.right / 2;
        mCurve2.controlStart.y = (mViewBounds.height() / 2) + 300;
        mCurve2.controlEnd.x = mViewBounds.right - 100;
        mCurve2.controlEnd.y = (mViewBounds.height() / 2) + 300;

        resetPath();
    }

    private void resetPath() {
        if (!mPath.isEmpty()) {
            mPath.reset();
        }
        mCurve1.fillPath(mPath);
        mCurve2.fillPath(mPath);
        fillDashPath();
    }

    private void fillDashPath() {
        if (!mDashPath.isEmpty()) {
            mDashPath.reset();
        }
        mDashPath.moveTo(mCurve1.start.x, mCurve1.start.y);
        mDashPath.lineTo(mCurve1.controlStart.x, mCurve1.controlStart.y);
        mDashPath.moveTo(mCurve1.end.x, mCurve1.end.y);
        mDashPath.lineTo(mCurve1.controlEnd.x, mCurve1.controlEnd.y);

        mDashPath.moveTo(mCurve2.start.x, mCurve2.start.y);
        mDashPath.lineTo(mCurve2.controlStart.x, mCurve2.controlStart.y);
        mDashPath.moveTo(mCurve2.end.x, mCurve2.end.y);
        mDashPath.lineTo(mCurve2.controlEnd.x, mCurve2.controlEnd.y);
    }


    private float mPrevX;
    private float mPrevY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mBufferPoint.x = Math.round(event.getRawX());
                mBufferPoint.y = Math.round(event.getRawY());
                if (isPointInCircle(100, mTouchPoint1, mBufferPoint)) {
                    mPrevX = event.getRawX();
                    mPrevY = event.getRawY();
                    mLockedPoint = mTouchPoint1;
                    return true;
                }
                if (isPointInCircle(100, mTouchPoint2, mBufferPoint)) {
                    mPrevX = mTouchPoint2.x;
                    mPrevY = mTouchPoint2.y;
                    mLockedPoint = mTouchPoint2;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();
                mLockedPoint.x += Math.round(x - mPrevX);
                mLockedPoint.y += Math.round(y - mPrevY);
                if (mLockedPoint == mTouchPoint1) {
                    getNextPoint(mLockedPoint, mJoint, mTouchPoint2);
                } else if (mLockedPoint == mTouchPoint2) {
                    getNextPoint(mLockedPoint, mJoint, mTouchPoint1);
                }
                mPrevX = x;
                mPrevY = y;
                resetPath();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                mPrevX = 0;
                mPrevY = 0;
                mLockedPoint = null;
                break;
        }
        return super.onTouchEvent(event);
    }

    protected static Point getNextPoint(Point start, Point center, Point end) {
        float len = getDistance(start, center);
        float ratio = len < 0 ? -1f : 1f;
        end.x = Math.round(center.x + ratio * (center.x - start.x));
        end.y = Math.round(center.y + ratio * (center.y - start.y));
        return end;
    }

    protected static float getDistance(Point p1, Point p2) {
        return (float) Math.sqrt(
                (p1.x - p2.x) * (p1.x - p2.x) +
                        (p1.y - p2.y) * (p1.y - p2.y)
        );
    }

    public boolean isPointInCircle(int radius, Point circlePoint, Point touchPoint) {
        if (circlePoint == null || touchPoint == null) {
            return false;
        }
        return Math.sqrt(Math.pow(circlePoint.x - touchPoint.x, 2) + Math.pow(circlePoint.y - touchPoint.y, 2)) <= radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(mPath, mPaint);

        canvas.drawPath(mDashPath, mDashPaint);

        mPaint.setColor(Color.RED);
        canvas.drawCircle(mCurve1.end.x, mCurve1.end.y, 7, mPaint);
        canvas.drawCircle(mCurve1.start.x, mCurve1.start.y, 7, mPaint);
        canvas.drawCircle(mCurve1.controlEnd.x, mCurve1.controlEnd.y, 7, mPaint);
        canvas.drawCircle(mCurve1.controlStart.x, mCurve1.controlStart.y, 7, mPaint);

        canvas.drawCircle(mCurve2.end.x, mCurve2.end.y, 7, mPaint);
        canvas.drawCircle(mCurve2.controlEnd.x, mCurve2.controlEnd.y, 7, mPaint);
        canvas.drawCircle(mCurve2.controlStart.x, mCurve2.controlStart.y, 7, mPaint);
    }
}
