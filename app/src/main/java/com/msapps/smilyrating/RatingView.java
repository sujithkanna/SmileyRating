package com.msapps.smilyrating;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends BaseRating implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "RatingView";

    private static final int ANGRY_COLOR = Color.parseColor("#f29a68");
    private static final int NORMAL_COLOR = Color.parseColor("#f2dd68");
    private static final int PAINT_COLOR = Color.parseColor("#353431");

    private boolean mShowLines = false;
    private boolean mShowPoints = false;
    private Map<Integer, Point> mTouchPoints = new HashMap<>();
    private float mSmileGap;
    private Paint mPathPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();

    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
    private Path mEyePathLeft = new Path();
    private Path mEyePathRight = new Path();
    private Point mFaceCenter = new Point();
    private Path mSmilePath = new Path();
    private Paint mPlaceHolderPaint = new Paint();
    private float divisions;
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();
    private ArgbEvaluator mColorEvaluator = new ArgbEvaluator();
    private ClickAnalyser mClickAnalyser;

    @Smiley
    private int mSelectedSmile = TERRIBLE;

    private Smileys mSmileys;
    private float mTranslation = 0;
    private float mWidth;
    private float mHeight;
    private float mCenterY;
    private float mFromRange;
    private float mToRange;

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
        mClickAnalyser = ClickAnalyser.newInstance(getResources().getDisplayMetrics().density);

        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(PAINT_COLOR);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.RED);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.BLUE);
        mPointPaint2.setStyle(Paint.Style.STROKE);

        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mPlaceHolderPaint.setColor(PAINT_COLOR);
        mPlaceHolderPaint.setStyle(Paint.Style.FILL);

        mValueAnimator.setDuration(250);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
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
        mWidth = getMeasuredWidth();
        mHeight = mWidth / 5.3f;
        mCenterY = mHeight / 2f;
        mFaceCenter.y = mCenterY;
        divisions = (mHeight / 32f);
        mSmileys = Smileys.newInstance(Math.round(mWidth), Math.round(mHeight));
        setMeasuredDimension(Math.round(mWidth), Math.round(mHeight));
        setFraction(0);
        createTouchPoints();
        mFromRange = mSmileGap + (mHeight / 2);
        mToRange = mWidth - (mHeight / 2) - mSmileGap;
    }

    private void createTouchPoints() {
        mTouchPoints.clear();
        float divisions = mWidth / 5f;
        float divCenter = divisions / 2f;
        mSmileGap = (divisions - mHeight) / 2f;
        for (int i = 0; i < 5; i++) {
            mTouchPoints.put(SMILES_LIST[i], new Point((divisions * i) + divCenter, mCenterY));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Integer integer : mTouchPoints.keySet()) {
            Point point = mTouchPoints.get(integer);
            canvas.drawCircle(point.x, point.y, mHeight / 2, mPlaceHolderPaint);
        }
        canvas.drawCircle(mFaceCenter.x, mFaceCenter.y, mHeight / 2f, mBackgroundPaint);
        if (!mSmilePath.isEmpty()) {
            canvas.drawPath(mSmilePath, mPathPaint);
            canvas.drawPath(mEyePathLeft, mPathPaint);
            canvas.drawPath(mEyePathRight, mPathPaint);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float anim = (float) valueAnimator.getAnimatedValue();
        float fraction = (anim - mFromRange) / (mToRange - mFromRange);
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
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mClickAnalyser.start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mClickAnalyser.move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                mClickAnalyser.stop(x, y);
                if (!mClickAnalyser.isMoved()) {
                    onClickView(x, y);
                }
                break;
        }
        return true;
    }

    private void onClickView(float x, float y) {
        for (Integer smile : mTouchPoints.keySet()) {
            Point point = mTouchPoints.get(smile);
            boolean touched = isPointInCircle(point.x, point.y, x, y, mCenterY);
            if (touched) {
                setSelectedSmile(smile, point);
            }
        }
    }

    private boolean isPointInCircle(float cx, float cy, float tx, float ty, float radius) {
        return Math.sqrt(Math.pow(cx - tx, 2) + Math.pow(cy - ty, 2)) <= radius;
    }

    private void setSelectedSmile(@Smiley int smile, Point point) {
        Log.i(TAG, "Selected smile: " + smile);
        if (mSelectedSmile == smile) {
            return;
        }
        mSelectedSmile = smile;
        mValueAnimator.setFloatValues(mFaceCenter.x, point.x);
        mValueAnimator.start();
    }

    /**
     * Evaluates click actions using touch events
     */
    public static class ClickAnalyser {

        private static final int MAX_CLICK_DISTANCE = 20;
        private static final int MAX_CLICK_DURATION = 200;

        private float mPressX;
        private float mPressY;
        private final float mDensity;
        private long mPressStartTime;
        private boolean mMoved = false;
        private boolean mClickEventOccured = true;

        public ClickAnalyser(float density) {
            mDensity = density;
        }

        public static ClickAnalyser newInstance(float density) {
            return new ClickAnalyser(density);
        }

        public void start(float x, float y) {
            mPressX = x;
            mPressY = y;
            mMoved = false;
            mClickEventOccured = true;
            mPressStartTime = System.currentTimeMillis();
        }

        /**
         * returns long press
         *
         * @param x
         * @param y
         * @return
         */
        public void move(float x, float y) {
            float dist = distance(mPressX, mPressY, x, y);
            long time = System.currentTimeMillis() - mPressStartTime;
            if (!mMoved && dist > MAX_CLICK_DISTANCE) {
                mMoved = true;
            }
            if ((time) > MAX_CLICK_DURATION || mMoved) {
                mClickEventOccured = false;
            }
        }

        public boolean stop(float x, float y) {
            move(x, y);
            return mClickEventOccured;
        }

        private float distance(float x1, float y1, float x2, float y2) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
            return pxToDp(distanceInPx);
        }

        public boolean isMoved() {
            return mMoved;
        }

        private float pxToDp(float px) {
            return px / mDensity;
        }
    }

    public void setFraction(float fraction) {
        if (mSmileys == null) {
            return;
        }
        mTranslation = mFloatEvaluator.evaluate(fraction, mFromRange, mToRange);
        mFaceCenter.x = mTranslation;
        float trans = mTranslation - mCenterY;
        if (fraction > 0.75f) {
            fraction -= 0.75f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, mSmilePath,
                    mSmileys.getSmile(GOOD), mSmileys.getSmile(GREAT), mFloatEvaluator);
            createEyeLocation(fraction, GREAT);
        } else if (fraction > 0.50f) {
            fraction -= 0.50f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, mSmilePath,
                    mSmileys.getSmile(OKAY), mSmileys.getSmile(GOOD), mFloatEvaluator);
            createEyeLocation(fraction, GOOD);
        } else if (fraction > 0.25f) {
            fraction -= 0.25f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, mSmilePath,
                    mSmileys.getSmile(BAD), mSmileys.getSmile(OKAY), mFloatEvaluator);
            createEyeLocation(fraction, BAD);
        } else {
            fraction *= 4;
            mBackgroundPaint.setColor((Integer) mColorEvaluator.evaluate(fraction, ANGRY_COLOR, NORMAL_COLOR));
            transformSmile(trans, fraction, mSmilePath,
                    mSmileys.getSmile(TERRIBLE), mSmileys.getSmile(BAD), mFloatEvaluator);
            createEyeLocation(fraction, TERRIBLE);
        }

        invalidate();
    }

    private void createEyeLocation(float fraction, @Smiley int smile) {
        Eye eyeLeft = EyeEmotion.prepareEye(mSmileys.getEye(Eye.LEFT), mFloatEvaluator, fraction, smile);
        Eye eyeRight = EyeEmotion.prepareEye(mSmileys.getEye(Eye.RIGHT), mFloatEvaluator, fraction, smile);
        eyeLeft.radius = divisions * 2.5f;
        eyeRight.radius = divisions * 2.5f;
        eyeLeft.center.x = (divisions * 11f) + mTranslation - mCenterY;
        eyeLeft.center.y = mCenterY * 0.70f;
        eyeRight.center.x = (divisions * 21f) + mTranslation - mCenterY;
        eyeRight.center.y = mCenterY * 0.70f;
        mEyePathLeft = eyeLeft.fillPath(mEyePathLeft);
        mEyePathRight = eyeRight.fillPath(mEyePathRight);
    }
}
