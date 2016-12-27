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

    private Face[] mFaces = new Face[SMILES_LIST.length];
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
    private Paint mPlaceHolderFacePaint = new Paint();
    private Paint mPlaceHolderCirclePaint = new Paint();
    private float divisions;
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();
    private ArgbEvaluator mColorEvaluator = new ArgbEvaluator();
    private ClickAnalyser mClickAnalyser;

    @Smiley
    private int mSelectedSmile = TERRIBLE;

    private Smileys mSmileys;
    // private float mTranslation = 0;
    private float mWidth;
    private float mHeight;
    private float mCenterY;
    private float mFromRange;
    private float mToRange;

    private float mPrevX;
    private boolean mFaceClickEngaged = false;

    public RatingView(Context context) {
        super(context);

    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
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

        mPlaceHolderFacePaint.setColor(Color.WHITE);
        mPlaceHolderFacePaint.setStyle(Paint.Style.FILL);

        mPlaceHolderCirclePaint.setColor(Color.parseColor("#ced5e0"));
        mPlaceHolderCirclePaint.setStyle(Paint.Style.FILL);

        mValueAnimator.setDuration(250);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        // mHeight = mWidth / 5.3f;
        mHeight = mWidth / (5.3f * 1.3f);
        mCenterY = mHeight / 2f;
        mFaceCenter.y = mCenterY;
        divisions = (mHeight / 32f);
        mSmileys = Smileys.newInstance(Math.round(mWidth), Math.round(mHeight));
        setMeasuredDimension(Math.round(mWidth), Math.round(mHeight));
        createTouchPoints();
        getSmiley(mSmileys, 0, divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mEyePathLeft, mEyePathRight, mCenterY);
    }

    private void createTouchPoints() {
        mTouchPoints.clear();
        float divisions = mWidth / 5f;
        float divCenter = divisions / 2f;
        mSmileGap = (divisions - mHeight) / 2f;
        mFromRange = mSmileGap + (mHeight / 2);
        mToRange = mWidth - (mHeight / 2) - mSmileGap;
        int count = SMILES_LIST.length;
        for (int i = 0; i < count; i++) {
            mFaces[i] = createFace(i, mCenterY);
            mTouchPoints.put(SMILES_LIST[i], new Point((divisions * i) + divCenter, mCenterY));
        }
    }

    private Face createFace(int index, float centerY) {
        Face face = new Face();
        getSmiley(mSmileys, index * 0.25f, divisions, mFromRange, mToRange, face.place,
                face.smile, face.leftEye, face.rightEye, centerY);
        face.place.y = centerY;
        return face;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Face face : mFaces) {
            canvas.drawCircle(face.place.x, face.place.y, mHeight / 2, mPlaceHolderCirclePaint);
            if (!mSmilePath.isEmpty()) {
                canvas.drawPath(face.smile, mPlaceHolderFacePaint);
                canvas.drawPath(face.leftEye, mPlaceHolderFacePaint);
                canvas.drawPath(face.rightEye, mPlaceHolderFacePaint);
            }
        }
        /*for (Point point : mTouchPoints.values()) {
            canvas.drawCircle(point.x, point.y, 20, mBackgroundPaint);
        }*/
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
        moveSmile(anim);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mClickAnalyser.start(x, y);
                mFaceClickEngaged = isPointInCircle(mFaceCenter.x, mFaceCenter.y,
                        x, y, mCenterY);
                mPrevX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                mClickAnalyser.move(x, y);
                if (mClickAnalyser.isMoved() && mFaceClickEngaged) {
                    moveSmile(mFaceCenter.x - (mPrevX - x));
                }
                mPrevX = x;
                break;
            case MotionEvent.ACTION_UP:
                mFaceClickEngaged = false;
                mClickAnalyser.stop(x, y);
                if (!mClickAnalyser.isMoved()) {
                    onClickView(x, y);
                } else {
                    positionSmile();
                }
                break;
        }
        return true;
    }

    private void positionSmile() {
        float currentPosition = mFaceCenter.x;
        float distance = Integer.MAX_VALUE;
        Point point = null;
        @Smiley
        int smile = GREAT;
        for (Integer s : mTouchPoints.keySet()) {
            Point p = mTouchPoints.get(s);
            float d = Math.abs(p.x - currentPosition);
            if (distance > d) {
                point = p;
                smile = s;
                distance = d;
            }
        }
        setSelectedSmile(smile, point, false);
    }

    private void moveSmile(float x) {
        float fraction = (x - mFromRange) / (mToRange - mFromRange);
        if (fraction >= 0f && fraction <= 1f) {
            getSmiley(mSmileys, fraction, divisions, mFromRange, mToRange,
                    mFaceCenter, mSmilePath, mEyePathLeft, mEyePathRight, mCenterY);
            invalidate();
        }
    }

    private void onClickView(float x, float y) {
        for (Integer smile : mTouchPoints.keySet()) {
            Point point = mTouchPoints.get(smile);
            boolean touched = isPointInCircle(point.x, point.y, x, y, mCenterY);
            if (touched) {
                setSelectedSmile(smile, point, true);
            }
        }
    }

    private boolean isPointInCircle(float cx, float cy, float tx, float ty, float radius) {
        return Math.sqrt(Math.pow(cx - tx, 2) + Math.pow(cy - ty, 2)) <= radius;
    }

    public void setSelectedSmile(@Smiley int smile) {
        setSelectedSmile(smile, mTouchPoints.get(smile), true);
    }

    private void setSelectedSmile(@Smiley int smile, Point point, boolean check) {
        if (mSelectedSmile == smile && check) {
            return;
        }
        mSelectedSmile = smile;
        mValueAnimator.setFloatValues(mFaceCenter.x, point.x);
        mValueAnimator.start();
    }

    /**
     * Evaluates click actions using touch events
     */
    protected static class ClickAnalyser {

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

    private void getSmiley(Smileys smileys, float fraction, float divisions, float fromRange,
                           float toRange, Point point, Path smilePath,
                           Path leftEye, Path rightEye, float centerY) {
        if (smileys == null) {
            return;
        }
        float actualTranslation = mFloatEvaluator.evaluate(fraction, fromRange, toRange);
        point.x = actualTranslation;
        float trans = actualTranslation - centerY;
        if (fraction > 0.75f) {
            fraction -= 0.75f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(GOOD), smileys.getSmile(GREAT), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, GREAT, leftEye, rightEye, centerY);
        } else if (fraction > 0.50f) {
            fraction -= 0.50f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(OKAY), smileys.getSmile(GOOD), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, GOOD, leftEye, rightEye, centerY);
        } else if (fraction > 0.25f) {
            fraction -= 0.25f;
            fraction *= 4;
            mBackgroundPaint.setColor(NORMAL_COLOR);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(BAD), smileys.getSmile(OKAY), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, BAD, leftEye, rightEye, centerY);
        } else {
            fraction *= 4;
            mBackgroundPaint.setColor((Integer) mColorEvaluator.evaluate(fraction, ANGRY_COLOR, NORMAL_COLOR));
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(TERRIBLE), smileys.getSmile(BAD), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, TERRIBLE, leftEye, rightEye, centerY);
        }
    }

    private void createEyeLocation(Smileys smileys, float divisions, float fraction, float actualTranslation, @Smiley int smile, Path leftEye, Path rightEye, float centerY) {
        Eye eyeLeft = EyeEmotion.prepareEye(smileys.getEye(Eye.LEFT), mFloatEvaluator, fraction, smile);
        Eye eyeRight = EyeEmotion.prepareEye(smileys.getEye(Eye.RIGHT), mFloatEvaluator, fraction, smile);
        eyeLeft.radius = divisions * 2.5f;
        eyeRight.radius = divisions * 2.5f;
        eyeLeft.center.x = (divisions * 11f) + actualTranslation - centerY;
        eyeLeft.center.y = centerY * 0.70f;
        eyeRight.center.x = (divisions * 21f) + actualTranslation - centerY;
        eyeRight.center.y = centerY * 0.70f;
        eyeLeft.fillPath(leftEye);
        eyeRight.fillPath(rightEye);
    }

    private static class Face {
        Point place = new Point();
        Path smile = new Path();
        Path leftEye = new Path();
        Path rightEye = new Path();
    }
}
