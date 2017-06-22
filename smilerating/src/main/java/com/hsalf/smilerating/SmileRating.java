package com.hsalf.smilerating;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 11/10/16.
 */
public class SmileRating extends BaseRating {

    private static final String TAG = "RatingView";

    private int mPlaceHolderSmileColor = Color.WHITE;
    private int mAngryColor = Color.parseColor("#f29a68");
    private int mNormalColor = Color.parseColor("#f2dd68");
    private int mDrawingColor = Color.parseColor("#353431");
    private int mTextSelectedColor = Color.BLACK;
    private int mTextNonSelectedColor = Color.parseColor("#AEB3B5");
    private int mPlaceholderBackgroundColor = Color.parseColor("#e6e8ed");

    private String[] mNames = getResources().getStringArray(R.array.names);

    private Face[] mFaces = new Face[SMILES_LIST.length];
    private Map<Integer, Point> mTouchPoints = new HashMap<>();
    private float mSmileGap;
    private boolean mShowLine = true;
    private float mMainSmileyTransformaFraction = 1;
    private Paint mPathPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();

    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
    //    private Path mEyePathLeft = new Path();
//    private Path mEyePathRight = new Path();
    private Point mFaceCenter = new Point();
    private Path mSmilePath = new Path();
    private Paint mPlaceHolderFacePaint = new Paint();
    private Paint mPlaceholderLinePaint = new Paint();
    private Paint mPlaceHolderCirclePaint = new Paint();
    private float divisions;
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();
    private ArgbEvaluator mColorEvaluator = new ArgbEvaluator();
    private OvershootInterpolator mInterpolator = new OvershootInterpolator();
    private ClickAnalyser mClickAnalyser;
    private Matrix mScaleMatrix = new Matrix();
    private RectF mScaleRect = new RectF();
    private RectF mTouchBounds = new RectF();
    private Path mDummyDrawPah = new Path();
    private Paint mTextPaint = new Paint();

    @Smiley
    private int mSelectedSmile = NONE;
    @Smiley
    private int mPreviousSmile = -1;
    @Smiley
    private int mNearestSmile = NONE;
    @Smiley
    private int mPendingActionSmile = NONE;
    private Smileys mSmileys;
    // private float mTranslation = 0;
    private float mWidth;
    private float mHeight;
    private float mCenterY;
    private float mFromRange;
    private float mToRange;
    private float mPrevX;
    private boolean mFaceClickEngaged = false;
    private OnRatingSelectedListener mOnRatingSelectedListener = null;
    private OnSmileySelectionListener mOnSmileySelectionListener = null;
    private float mPlaceHolderScale = 1f;
    private boolean mSmileyNotSelectedPreviously = true;
    private boolean mIndicator = false;

    public SmileRating(Context context) {
        super(context);
        init();
    }

    public SmileRating(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
        init();
    }

    public SmileRating(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(attrs);
        init();
    }

    private void parseAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SmileRating);
            mAngryColor = a.getColor(R.styleable.SmileRating_angryColor, mAngryColor);
            mNormalColor = a.getColor(R.styleable.SmileRating_normalColor, mNormalColor);
            mDrawingColor = a.getColor(R.styleable.SmileRating_drawingColor, mDrawingColor);
            mPlaceHolderSmileColor = a.getColor(R.styleable.SmileRating_placeHolderSmileColor,
                    mPlaceHolderSmileColor);
            mPlaceholderBackgroundColor = a.getColor(R.styleable.SmileRating_placeHolderBackgroundColor,
                    mPlaceholderBackgroundColor);
            mTextSelectedColor = a.getColor(R.styleable.SmileRating_textSelectionColor,
                    mTextSelectedColor);
            mTextNonSelectedColor = a.getColor(R.styleable.SmileRating_textNonSelectionColor,
                    mTextNonSelectedColor);
            mShowLine = a.getBoolean(R.styleable.SmileRating_showLine, true);
            mIndicator = a.getBoolean(R.styleable.SmileRating_isIndicator, false);
            a.recycle();
        }
    }


    private void init() {
        mClickAnalyser = ClickAnalyser.newInstance(getResources().getDisplayMetrics().density);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(mDrawingColor);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setAntiAlias(true);
        mPointPaint1.setColor(Color.RED);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setAntiAlias(true);
        mPointPaint2.setColor(Color.BLUE);
        mPointPaint2.setStyle(Paint.Style.STROKE);

        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mPlaceHolderFacePaint.setAntiAlias(true);
        mPlaceHolderFacePaint.setColor(mPlaceHolderSmileColor);
        mPlaceHolderFacePaint.setStyle(Paint.Style.FILL);

        mPlaceHolderCirclePaint.setAntiAlias(true);
        mPlaceHolderCirclePaint.setColor(mPlaceholderBackgroundColor);
        mPlaceHolderCirclePaint.setStyle(Paint.Style.FILL);

        mPlaceholderLinePaint.setAntiAlias(true);
        mPlaceholderLinePaint.setColor(mPlaceholderBackgroundColor);
        mPlaceholderLinePaint.setStyle(Paint.Style.STROKE);

        mValueAnimator.setDuration(250);
        mValueAnimator.addListener(mAnimatorListener);
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (mSmileyNotSelectedPreviously) {
                mMainSmileyTransformaFraction = animation.getAnimatedFraction();
                if (NONE == mSelectedSmile) {
                    mMainSmileyTransformaFraction = 1f - mMainSmileyTransformaFraction;
                }
                invalidate();
            } else {
                float anim = (float) animation.getAnimatedValue();
                moveSmile(anim);
            }
        }
    };

    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (NONE != mSelectedSmile) {
                moveSmile(mTouchPoints.get(mSelectedSmile).x);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            notifyListener();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void notifyListener() {
        boolean reselected = mPreviousSmile == getSelectedSmile();
        mPreviousSmile = mSelectedSmile;
        mPendingActionSmile = mSelectedSmile;
        if (mOnSmileySelectionListener != null) {
            mOnSmileySelectionListener.onSmileySelected(mSelectedSmile, reselected);
        }
        if (mOnRatingSelectedListener != null) {
            mOnRatingSelectedListener.onRatingSelected(getRating(), reselected);
        }
    }

    public void setOnSmileySelectionListener(OnSmileySelectionListener l) {
        mOnSmileySelectionListener = l;
    }


    public void setOnRatingSelectedListener(OnRatingSelectedListener l) {
        mOnRatingSelectedListener = l;
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
        mTextPaint.setTextSize(mHeight / 4.5f);
        mSmileys = Smileys.newInstance(Math.round(mWidth), Math.round(mHeight));
        setMeasuredDimension(Math.round(mWidth), (int) Math.round(mHeight + (mHeight * 0.48)));
        createTouchPoints();
        mPlaceholderLinePaint.setStrokeWidth(mHeight * 0.05f);
        /*getSmiley(mSmileys, 0.5f, divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY);*/
        setSelectedSmile(mPendingActionSmile, mTouchPoints.get(mPendingActionSmile), false, false);
        Log.i(TAG, "Selected smile:" + getSmileName(mPendingActionSmile));
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
        face.smileType = index;
        getSmiley(mSmileys, index * 0.25f, divisions, mFromRange, mToRange, face.place,
                face.smile, centerY);
        face.place.y = centerY;
        return face;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point start = mFaces[0].place;
        Point end = mFaces[mFaces.length - 1].place;
        if (mShowLine) {
            canvas.drawLine(start.x, start.y, end.x, end.y, mPlaceholderLinePaint);
        }
        Log.i(TAG, "******************");
        for (Face face : mFaces) {
            float scale = getScale(face.smileType);
            canvas.drawCircle(face.place.x, face.place.y,
                    scale * (mHeight / 2), mPlaceHolderCirclePaint);
            mScaleMatrix.reset();
            face.smile.computeBounds(mScaleRect, true);
            if (mSmileyNotSelectedPreviously) {
                float nonSelectedScale = getScale(NONE);
                mScaleMatrix.setScale(nonSelectedScale, nonSelectedScale,
                        mScaleRect.centerX(), mScaleRect.centerY());
                if (mSelectedSmile == face.smileType) {
                    scale = mFloatEvaluator.evaluate(1 - mMainSmileyTransformaFraction, 0, nonSelectedScale);
                }
            } else {
                mScaleMatrix.setScale(scale, scale,
                        mScaleRect.centerX(), mScaleRect.centerY());
            }
            mDummyDrawPah.reset();
            mDummyDrawPah.addPath(face.smile, mScaleMatrix);
            canvas.drawPath(mDummyDrawPah, mPlaceHolderFacePaint);
            float transY = 0.15f - (scale * 0.15f);
            mTextPaint.setColor((int) mColorEvaluator.evaluate(((transY / 0.15f) - 0.2f) / 0.8f,
                    mTextNonSelectedColor, mTextSelectedColor));
            drawTextCentered(getSmileName(face.smileType), face.place.x,
                    face.place.y + (mHeight * (0.70f + transY)), mTextPaint, canvas);
        }
        if (!mSmilePath.isEmpty()) {
            if (mSmileyNotSelectedPreviously) {
                Log.i(TAG, "Non selection");
                /*mPathPaint.setAlpha(Math.round(255 * mMainSmileyTransformaFraction));
                mBackgroundPaint.setAlpha(Math.round(255 * mMainSmileyTransformaFraction));*/
                mPathPaint.setColor((Integer) mColorEvaluator
                        .evaluate(mMainSmileyTransformaFraction, mPlaceHolderFacePaint.getColor(), mDrawingColor));
                mBackgroundPaint.setColor((Integer) mColorEvaluator
                        .evaluate(mMainSmileyTransformaFraction, mPlaceHolderCirclePaint.getColor(), mNormalColor));
                mScaleMatrix.reset();
                mSmilePath.computeBounds(mScaleRect, true);
                float nonSelectedScale = mFloatEvaluator.evaluate(
                        mInterpolator.getInterpolation(mMainSmileyTransformaFraction), getScale(NONE), 1f);
                mScaleMatrix.setScale(nonSelectedScale, nonSelectedScale,
                        mScaleRect.centerX(), mScaleRect.centerY());
                mDummyDrawPah.reset();
                mDummyDrawPah.addPath(mSmilePath, mScaleMatrix);

                canvas.drawCircle(mFaceCenter.x, mFaceCenter.y,
                        nonSelectedScale * (mHeight / 2f), mBackgroundPaint);
                canvas.drawPath(mDummyDrawPah, mPathPaint);
            } else {
                canvas.drawCircle(mFaceCenter.x, mFaceCenter.y, mHeight / 2f, mBackgroundPaint);
                canvas.drawPath(mSmilePath, mPathPaint);
            }
        }
    }

    private void drawTextCentered(String text, float x, float y, Paint paint, Canvas canvas) {
        float xPos = x - (paint.measureText(text) / 2);
        float yPos = (y - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
    }

    private float getScale(@Smiley int smile) {
        if (mSelectedSmile == NONE) {
            return 0.80f;
        }
        if (smile == mNearestSmile) {
            return mPlaceHolderScale;
        }
        return 0.80f;
    }

    public String getSmileName(int smile) {
        if (smile >= mNames.length || smile < 0) {
            return null;
        }
        return mNames[smile];
    }

    public void setIndicator(boolean isIndicator) {
        mIndicator = isIndicator;
    }

    public boolean isIndicator() {
        return mIndicator;
    }

    public void setNameForSmile(@Smiley int smile, @StringRes int stringRes) {
        setNameForSmile(smile, getResources().getString(stringRes));
    }

    public void setNameForSmile(@Smiley int smile, String title) {
        mNames[smile] = title != null ? title : "";
        invalidate();
    }

    public void setAngryColor(@ColorInt int color) {
        this.mAngryColor = color;
        getSmiley(mSmileys, getFractionBySmiley(mSelectedSmile), divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY);
    }

    public void setNormalColor(@ColorInt int color) {
        this.mNormalColor = color;
        getSmiley(mSmileys, getFractionBySmiley(mSelectedSmile), divisions, mFromRange, mToRange,
                mFaceCenter, mSmilePath, mCenterY);
    }

    public void setDrawingColor(@ColorInt int color) {
        this.mDrawingColor = color;
        mPathPaint.setColor(mDrawingColor);
        invalidate();
    }

    public void setTextSelectedColor(@ColorInt int color) {
        this.mTextSelectedColor = color;
        invalidate();
    }

    public void setTextNonSelectedColor(@ColorInt int color) {
        this.mTextNonSelectedColor = color;
        invalidate();
    }

    public void setPlaceHolderSmileColor(@ColorInt int color) {
        this.mPlaceHolderSmileColor = color;
        mPlaceHolderFacePaint.setColor(mPlaceHolderSmileColor);
        invalidate();
    }

    public void setPlaceholderBackgroundColor(@ColorInt int color) {
        this.mPlaceholderBackgroundColor = color;
        mPlaceholderLinePaint.setColor(mPlaceholderBackgroundColor);
        mPlaceHolderCirclePaint.setColor(mPlaceholderBackgroundColor);
        invalidate();
    }

    public void setShowLine(boolean showLine) {
        mShowLine = showLine;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        if (typeface == null) {
            typeface = Typeface.DEFAULT;
        }
        mTextPaint.setTypeface(typeface);
    }

    public boolean isShowingLine() {
        return mShowLine;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIndicator) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mClickAnalyser.start(x, y);
                mFaceClickEngaged = isSmileyBounds(mFaceCenter.x, mFaceCenter.y,
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
        if (NONE == mSelectedSmile) {
            return;
        }
        float currentPosition = mFaceCenter.x;
        float distance = Integer.MAX_VALUE;
        Point point = null;
        @Smiley
        int smile = NONE;
        for (Integer s : mTouchPoints.keySet()) {
            Point p = mTouchPoints.get(s);
            float d = Math.abs(p.x - currentPosition);
            if (distance > d) {
                point = p;
                smile = s;
                distance = d;
            }
        }
        setSelectedSmile(smile, point, false, true);
    }

    private void moveSmile(float position) {
        float fraction = (position - mFromRange) / (mToRange - mFromRange);
        moveSmileByFraction(fraction);
    }

    private void moveSmileByFraction(float fraction) {
        if (fraction >= 0f && fraction <= 1f) {
            getSmiley(mSmileys, fraction, divisions, mFromRange, mToRange,
                    mFaceCenter, mSmilePath, mCenterY);
            invalidate();
        }
    }

    private void onClickView(float x, float y) {
        for (Integer smile : mTouchPoints.keySet()) {
            Point point = mTouchPoints.get(smile);
            boolean touched = isSmileyBounds(point.x, point.y, x, y, mCenterY);
            if (touched) {
                if (smile == getSelectedSmile()) {
                    notifyListener();
                } else {
                    setSelectedSmile(smile, point, true, true);
                }
            }
        }
    }

    private boolean isSmileyBounds(float cx, float cy, float tx, float ty, float width) {
        mTouchBounds.set(cx - width, 0, cx + width, getMeasuredHeight());
        return mTouchBounds.contains(tx, ty);
    }

    /**
     * Set the selected smiley
     *
     * @param smile is the smiley type you want this rating bar to show as selected smile
     */
    public void setSelectedSmile(@Smiley int smile) {
        setSelectedSmile(smile, false);
    }

    /**
     * Set the selected smiley
     *
     * @param smile   is the smiley type you want this rating bar to show as selected smile
     * @param animate true if you want to set the selected smiley and animate it,
     *                false for no animation
     */
    public void setSelectedSmile(@Smiley int smile, boolean animate) {
        mPendingActionSmile = smile;
        setSelectedSmile(smile, mTouchPoints.get(smile), true, animate);
    }

    private void setSelectedSmile(@Smiley int smile, Point point, boolean check, boolean animate) {
        if (mSelectedSmile == smile && check) {
            return;
        }
        if (mSelectedSmile == NONE) {
            mSmileyNotSelectedPreviously = true;
        } else if (smile == NONE) {
            mSmileyNotSelectedPreviously = true;
        } else {
            mSmileyNotSelectedPreviously = false;
        }
        mSelectedSmile = smile;
        if (mFaceCenter == null) {
            return;
        }
        mValueAnimator.setFloatValues(mFaceCenter.x, point == null ? 0 : point.x);
        if (animate) {
            mValueAnimator.start();
        } else if (mSelectedSmile == NONE) {
            if (!mSmilePath.isEmpty()) {
                mSmilePath.reset();
            }
            invalidate();
        } else if (point != null) {
            moveSmile(point.x);
        }
    }

    /**
     * @return the current selected smiley {@link #TERRIBLE} ,{@link #BAD},
     * {@link #OKAY},{@link #GOOD},{@link #GREAT}
     */
    @Smiley
    public int getSelectedSmile() {
        return mSelectedSmile;
    }

    /**
     * @return the selected rating level from range of 1 to 5
     */
    public int getRating() {
        return getSelectedSmile() + 1;
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

    private float getFractionBySmiley(@Smiley int smiley) {
        switch (smiley) {

            case BaseRating.BAD:
                return 1f;
            case BaseRating.GOOD:
                return 0.75f;
            case BaseRating.GREAT:
                return 0.5f;
            case BaseRating.OKAY:
                return 0.25f;
            case BaseRating.TERRIBLE:
                return 0f;
        }
        return 0;
    }

    private void getSmiley(Smileys smileys, float fraction, float divisions, float fromRange,
                           float toRange, Point point, Path smilePath,
                           float centerY) {
        if (smileys == null) {
            return;
        }
        float actualTranslation = mFloatEvaluator.evaluate(fraction, fromRange, toRange);
        point.x = actualTranslation;
        float trans = actualTranslation - centerY;
        if (fraction > 0.75f) {
            fraction -= 0.75f;
            fraction *= 4;
            findNearestSmile(fraction, GOOD, GREAT);
            mBackgroundPaint.setColor(mNormalColor);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(GOOD), smileys.getSmile(GREAT), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, GREAT, smilePath, smilePath, centerY);
        } else if (fraction > 0.50f) {
            fraction -= 0.50f;
            fraction *= 4;
            findNearestSmile(fraction, OKAY, GOOD);
            mBackgroundPaint.setColor(mNormalColor);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(OKAY), smileys.getSmile(GOOD), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, GOOD, smilePath, smilePath, centerY);
        } else if (fraction > 0.25f) {
            fraction -= 0.25f;
            fraction *= 4;
            findNearestSmile(fraction, BAD, OKAY);
            mBackgroundPaint.setColor(mNormalColor);
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(BAD), smileys.getSmile(OKAY), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, BAD, smilePath, smilePath, centerY);
        } else if (fraction >= 0) {
            fraction *= 4;
            findNearestSmile(fraction, TERRIBLE, BAD);
            mBackgroundPaint.setColor((Integer) mColorEvaluator.evaluate(fraction, mAngryColor, mNormalColor));
            transformSmile(trans, fraction, smilePath,
                    smileys.getSmile(TERRIBLE), smileys.getSmile(BAD), mFloatEvaluator);
            createEyeLocation(smileys, divisions, fraction, actualTranslation, TERRIBLE, smilePath, smilePath, centerY);
        } else {
            if (!mSmilePath.isEmpty()) {
                mSmilePath.reset();
            }
        }
    }

    private void findNearestSmile(float fraction, @Smiley int leftSmile, @Smiley int rightSmile) {
        if (fraction < 0.5f) {
            mPlaceHolderScale = limitNumberInRange(fraction * 2);
            mNearestSmile = leftSmile;
        } else {
            mPlaceHolderScale = limitNumberInRange(1f - (fraction - 0.5f) * 2);
            mNearestSmile = rightSmile;
        }
    }

    private float limitNumberInRange(float num) {
        // The range is going to be in between 0 to 0.80
        num *= 0.80f;
        return num;
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
        @Smiley
        int smileType;
        /*Path leftEye = new Path();
        Path rightEye = new Path();*/
    }

    public interface OnSmileySelectionListener {
        /**
         * Called when a smiley is selected
         *
         * @param smiley     is the type of smiley the user selected ({@link #GREAT}, {@link #BAD},
         *                   {@link #OKAY},{@link #GOOD},{@link #GREAT})
         * @param reselected is false when user selects different smiley that previously selected
         *                   one true when the same smiley is selected. Except if it first time,
         *                   then the value will be false.
         */
        void onSmileySelected(@Smiley int smiley, boolean reselected);
    }

    public interface OnRatingSelectedListener {
        /**
         * Called when a smiley is selected
         *
         * @param level      is the level of the rating (0 to 4)
         * @param reselected is false when user selects different smiley that previously selected
         *                   one true when the same smiley is selected. Except if it first time,
         *                   then the value will be false.
         */
        void onRatingSelected(int level, boolean reselected);
    }
}
