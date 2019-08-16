package com.hsalf.smilerating.smiley2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hsalf.smilerating.FractionEvaluator;
import com.hsalf.smilerating.smiley2.smileys.Bad;
import com.hsalf.smilerating.smiley2.smileys.Good;
import com.hsalf.smilerating.smiley2.smileys.Great;
import com.hsalf.smilerating.smiley2.smileys.Okay;
import com.hsalf.smilerating.smiley2.smileys.Terrible;
import com.hsalf.smilerating.smiley2.smileys.base.Smiley;

public class SmileyRating extends View {

    private static final String TAG = "SmileyRating";

    private static final float DRAWING_PADDING_SCALE = .9f;
    private static final float PLACEHOLDER_PADDING_SCALE = .6f;
    private static final float TOTAL_DIVIDER_SPACE_SCALE = .25f;
    private static final float TEXT_SIZE_SCALE_FROM_SMILEY_SIZE = .2f;
    private static final float CONNECTOR_LINE_SCALE_FROM_SMILEY_SIZE = .02f;
    private static final int SHADOW_COLOR = Color.argb(60, Color.red(Color.BLACK),
            Color.green(Color.BLACK), Color.blue(Color.BLACK));

    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final FloatEvaluator FLOAT_EVALUATOR = new FloatEvaluator();
    private static final FractionEvaluator FRACTION_EVALUATOR = new FractionEvaluator();

    private Smiley[] mSmileys = new Smiley[]{
            new Terrible(), new Bad(), new Okay(), new Good(), new Great()
    };

    private Text[] mTitlePoints = new Text[]{
            new Text(), new Text(), new Text(), new Text(), new Text()
    };
    private RectF[] mPlaceHolders = new RectF[mSmileys.length];
    private Path[] mPlaceHolderPaths = new Path[mSmileys.length];
    private OnSmileySelectedListener mOnSmileySelectedListener;

    public enum Type {
        TERRIBLE(0), BAD(1), OKAY(2), GOOD(3), GREAT(4), NONE(-1);

        int index;

        Type(int i) {
            index = i;
        }

        public int getRating() {
            if (NONE == this) {
                return -1;
            }
            return index;
        }
    }

    private Type mSelectedSmiley = Type.NONE;

    private float mHolderScale = 0f;
    private float mSmileyPositionX = 0f;
    private int mCurrentFocusedIndex = 0;
    private Path mSmileyPath = new Path();
    private Paint mDrawPaint = new Paint();
    private Paint mCirclePaint = new Paint();
    private float mSmileyAppearScale = 0.f;
    private TextPaint mTextPaint = new TextPaint();

    private int mFaceColor;
    private int mDrawingColor;
    private ClickAnalyser mClickAnalyser;
    private RectF mFacePosition = new RectF();

    private int mTextSelectedColor = Color.BLACK;
    private int mTextNonSelectedColor = Color.parseColor("#AEB3B5");
    private int mPlaceholderBackgroundColor = Color.parseColor("#e6e8ed");

    private ValueAnimator mSlideAnimator = new ValueAnimator();
    private ValueAnimator mAppearAnimator = new ValueAnimator();

    public SmileyRating(Context context) {
        super(context);
        init();
    }

    public SmileyRating(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileyRating(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClickAnalyser = ClickAnalyser.newInstance(getResources().getDisplayMetrics().density);

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(Color.BLACK);
        mDrawPaint.setStyle(Paint.Style.FILL);

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setShadowLayer(15, 0, 0, SHADOW_COLOR);

        setLayerType(LAYER_TYPE_SOFTWARE, mCirclePaint);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mSlideAnimator.setDuration(350);
        mSlideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mSlideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setSmileyPosition((float) animation.getAnimatedValue());
            }
        });
        mSlideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnSmileySelectedListener != null) {
                    mOnSmileySelectedListener.onSmileySelected(mSelectedSmiley);
                }
            }
        });

        mAppearAnimator.setDuration(200);
        mAppearAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAppearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSmileyAppearScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAppearAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mSmileyAppearScale == 0) {
                    mSelectedSmiley = Type.NONE;
                }
                if (mOnSmileySelectedListener != null) {
                    mOnSmileySelectedListener.onSmileySelected(mSelectedSmiley);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = calculateHeight(width);
        setMeasuredDimension(width, height);
        createPlaceHolders(width);
        setSmileyPosition(mSmileyPositionX);
    }

    private int calculateHeight(int width) {
        int smileys = mSmileys.length;
        float estimatedHeight = width / smileys;
        // Reducing 1 divider space from height since divider
        // space will give double space at bottom twice in #createPlaceHolders later
        // estimatedHeight -= estimatedHeight * (TOTAL_DIVIDER_SPACE_SCALE / 3);
        return Math.round(estimatedHeight);
    }

    private void createPlaceHolders(int width) {
        // 25% is allocated for divider
        float totalDivisionSpace = (width * TOTAL_DIVIDER_SPACE_SCALE);
        // divided into 2 to place divider on both sides of the smiley
        float division = totalDivisionSpace / (2 * mSmileys.length);

        float spaceForEachSmiley = (width - totalDivisionSpace) / mSmileys.length;

        applyScalesToSmileys(spaceForEachSmiley);

        float lastLocation = 0;
        for (int i = 0; i < mSmileys.length; i++) {
            lastLocation += division;

            RectF rectF = new RectF();
            rectF.top = 0;
            rectF.left = lastLocation;
            rectF.bottom = spaceForEachSmiley;
            rectF.right = spaceForEachSmiley + lastLocation;

            lastLocation += (spaceForEachSmiley + division);
            mPlaceHolders[i] = rectF;
        }
        mDrawPaint.setStrokeWidth(spaceForEachSmiley * CONNECTOR_LINE_SCALE_FROM_SMILEY_SIZE);

        createPlaceHolderSmileys();
        createTitleSpots(spaceForEachSmiley);
    }

    private void applyScalesToSmileys(float scale) {
        for (Smiley mSmiley : mSmileys) {
            mSmiley.scale(scale);
        }
    }

    private void createPlaceHolderSmileys() {
        for (int i = 0; i < mSmileys.length; i++) {
            Path path = new Path();
            mSmileys[i].drawFace(path);
            mPlaceHolderPaths[i] = path;
        }
    }


    private void createTitleSpots(float smileySpace) {
        mTextPaint.setTextSize(smileySpace * TEXT_SIZE_SCALE_FROM_SMILEY_SIZE);
        int height = getMeasuredHeight();
        float textToY = (smileySpace) + ((height - smileySpace) / 2);
        for (int i = 0; i < mSmileys.length; i++) {
            String text = mSmileys[i].getName();
            float x = mPlaceHolders[i].centerX() - (mTextPaint.measureText(text) / 2);
            float textY = ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
            float fromY = smileySpace - textY;
            float toY = textToY - textY;
            mTitlePoints[i].set(x, fromY, toY);
        }
    }

    private void setSmileyPosition(float pointX) {
        float start = mPlaceHolders[0].centerX();
        float end = mPlaceHolders[mPlaceHolders.length - 1].centerX();
        if (pointX < start) {
            pointX = start;
        } else if (pointX > end) {
            pointX = end;
        }

        mSmileyPositionX = pointX;

        // fraction will be between 0 - 1
        float fraction = FRACTION_EVALUATOR.evaluate(pointX, start, end);

        int index = (int) Math.floor(fraction / .202f);

        RectF holder = mPlaceHolders[index];

        int endIndex;
        int startIndex;

        if (pointX > holder.centerX()) {
            startIndex = index;
            endIndex = index + 1;
        } else if (pointX < holder.centerX()) {
            endIndex = index;
            startIndex = index - 1;
        } else {
            endIndex = index;
            startIndex = index;
        }

        Smiley to = mSmileys[startIndex];
        Smiley from = mSmileys[endIndex];
        RectF smileySpace2 = mPlaceHolders[endIndex];
        RectF smileySpace1 = mPlaceHolders[startIndex];
        float drawFraction = FRACTION_EVALUATOR.evaluate(pointX,
                smileySpace1.centerX(), smileySpace2.centerX());

        calculateHolderScale(startIndex, endIndex, pointX);

        drawFraction = 1 - drawFraction;
        from.drawFace(to, mSmileyPath, drawFraction);

        float space = smileySpace1.width() / 2;
        mFacePosition.set(smileySpace1);
        mFacePosition.left = pointX - space;
        mFacePosition.right = pointX + space;

        mDrawingColor = from.getDrawingColor();
        mFaceColor = (int) ARGB_EVALUATOR.evaluate(drawFraction,
                from.getFaceColor(), to.getFaceColor());
        invalidate();
    }

    private void calculateHolderScale(int startIndex, int endIndex, float pointX) {
        RectF end = mPlaceHolders[endIndex];
        RectF start = mPlaceHolders[startIndex];

        float middle = (end.centerX() - start.centerX()) / 2f;
        int index = (pointX < start.centerX() + middle) ? startIndex : endIndex;

        RectF currentHolder = mPlaceHolders[index];
        if (currentHolder.centerX() >= pointX) {
            mHolderScale = 1 - FRACTION_EVALUATOR.evaluate(pointX,
                    currentHolder.centerX() - middle, currentHolder.centerX());
        } else {
            mHolderScale = FRACTION_EVALUATOR.evaluate(pointX,
                    currentHolder.centerX(), currentHolder.centerX() + middle);
        }

        mCurrentFocusedIndex = index;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPlaceHolders[0] != null) {
            mDrawPaint.setColor(Color.WHITE);

            drawConnectorLine(canvas, mPlaceHolders);

            for (int i = 0; i < mPlaceHolderPaths.length; i++) {
                float scale = PLACEHOLDER_PADDING_SCALE;
                float textTranslate = 1;
                if (i == mCurrentFocusedIndex && Type.NONE != mSelectedSmiley) {
                    float alteredScale = FLOAT_EVALUATOR.evaluate(mSmileyAppearScale,
                            0, mHolderScale);
                    scale *= alteredScale;
                    textTranslate = mHolderScale;
                }
                drawSmileyInRect(canvas, mPlaceHolders[i], mPlaceHolderPaths[i],
                        scale, Color.WHITE, mPlaceholderBackgroundColor, false);

                drawText(canvas, mSmileys[i], mTitlePoints[i], textTranslate);
            }

            if (Type.NONE != mSelectedSmiley) {
                int drawColor = (int) ARGB_EVALUATOR.evaluate(mSmileyAppearScale,
                        Color.WHITE, mDrawingColor);
                int faceColor = (int) ARGB_EVALUATOR.evaluate(mSmileyAppearScale,
                        mPlaceholderBackgroundColor, mFaceColor);
                drawSmileyInRect(canvas, mFacePosition, mSmileyPath,
                        FLOAT_EVALUATOR.evaluate(mSmileyAppearScale,
                                PLACEHOLDER_PADDING_SCALE, DRAWING_PADDING_SCALE),
                        drawColor, faceColor, true);
            }
        }
    }

    private void drawText(Canvas canvas, Smiley smiley, Text point, float trans) {
        int color = (Integer) ARGB_EVALUATOR.evaluate(1 - trans,
                mTextNonSelectedColor, mTextSelectedColor);
        mTextPaint.setColor(color);
        float animTextTrans = FLOAT_EVALUATOR.evaluate(mSmileyAppearScale, point.fromY, point.toY);
        float textY = FLOAT_EVALUATOR.evaluate(1 - trans, point.fromY, animTextTrans);
        canvas.drawText(smiley.getName(), point.x, textY, mTextPaint);
    }

    private void drawConnectorLine(Canvas canvas, RectF[] holders) {
        mDrawPaint.setColor(mPlaceholderBackgroundColor);
        RectF start = holders[0];
        RectF end = holders[holders.length - 1];
        canvas.drawLine(start.centerX(), start.centerY(),
                end.centerX(), end.centerY(), mDrawPaint);
    }

    private void drawSmileyInRect(Canvas canvas, RectF holder, Path path,
                                  float scale, int drawingColor, int faceColor, boolean shadow) {
        float padding = ((1 - scale) * (holder.width() / 2f));

        Paint paint = shadow ? mCirclePaint : mDrawPaint;
        paint.setColor(faceColor);
        canvas.drawCircle(holder.centerX(), holder.centerY(),
                (holder.width() / 2) * scale, paint);

        int save = canvas.save();
        canvas.translate(holder.left + padding, holder.top + padding);
        canvas.scale(scale, scale, 0, 0);

        mDrawPaint.setColor(drawingColor);
        canvas.drawPath(path, mDrawPaint);

        canvas.restoreToCount(save);
    }


    private boolean isActiveSmiley(float x, float y) {
        return inFaceBounds(x, y, mFacePosition);
    }

    private int inPlaceHolder(float x, float y) {
        for (int i = 0; i < mPlaceHolders.length; i++) {
            RectF holder = mPlaceHolders[i];
            if (inFaceBounds(x, y, holder)) {
                return i;
            }
        }
        return -1;
    }

    private boolean inFaceBounds(float x, float y, RectF rectF) {
        return rectF.left <= x && rectF.right >= x;
    }

    private void cancelMovingAnimations() {
        if (mSlideAnimator.isRunning()) {
            mSlideAnimator.cancel();
        }
    }


    private float mPrevX;
    private boolean mActiveFaceClicked = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (Type.NONE == mSelectedSmiley) {
                    int index = inPlaceHolder(x, y);
                    if (index != -1) {
                        mActiveFaceClicked = true;
                        animateAppearance(index);
                    }
                } else if (isActiveSmiley(x, y)) {
                    cancelMovingAnimations();
                    mActiveFaceClicked = true;
                }
                mClickAnalyser.start(x, y);
                mPrevX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                mClickAnalyser.move(x, y);
                if (mActiveFaceClicked) {
                    setSmileyPosition(mFacePosition.centerX() - (mPrevX - x));
                    mPrevX = x;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                boolean clicked = mClickAnalyser.stop(x, y);
                if (!mActiveFaceClicked && clicked) {
                    animateSmileyTo(x, y);
                } else {
                    moveFaceToNearestPlace();
                }
                mActiveFaceClicked = false;
                return true;
            default:
                mActiveFaceClicked = false;
                moveFaceToNearestPlace();
                return super.onTouchEvent(event);
        }
        // setSmileyPosition(event.getX());
        // return super.onTouchEvent(event);
    }

    private void animateAppearance(int index) {
        Type type = Type.values()[index];
        if (mSelectedSmiley == type) {
            return;
        }
        mSelectedSmiley = type;
        setSmileyPosition(mPlaceHolders[index].centerX());
        clearAppearAnimation();
        mAppearAnimator.setFloatValues(0, 1);
        mAppearAnimator.start();
    }

    public void resetSmiley() {
        mSelectedSmiley = Type.NONE;
        clearAppearAnimation();
        mAppearAnimator.setFloatValues(1, 0);
        mAppearAnimator.start();
    }

    private void clearAppearAnimation() {
        if (mAppearAnimator.isRunning()) {
            mAppearAnimator.cancel();
        }
    }

    private void moveFaceToNearestPlace() {
        int index = 4;
        float nearest = Integer.MAX_VALUE;
        for (int i = 0; i < mPlaceHolders.length; i++) {
            RectF holder = mPlaceHolders[i];
            float diff = Math.abs(mFacePosition.centerX() - holder.centerX());
            if (nearest > diff) {
                nearest = diff;
                index = i;
            }
        }
        mSelectedSmiley = Type.values()[index];
        animateSmileyTo(mPlaceHolders[index]);
    }

    private void animateSmileyTo(float x, float y) {
        for (int i = 0; i < mPlaceHolders.length; i++) {
            RectF holder = mPlaceHolders[i];
            if (inFaceBounds(x, y, holder)) {
                mSelectedSmiley = Type.values()[i];
                animateSmileyTo(holder);
                break;
            }
        }
    }

    private void animateSmileyTo(RectF rectF) {
        cancelMovingAnimations();
        mSlideAnimator.setFloatValues(mFacePosition.centerX(), rectF.centerX());
        mSlideAnimator.start();
    }

    public void setRating(int rating) {
        Type[] types = Type.values();
        if (rating < 0 || rating >= types.length) {
            return;
        }
        Type type = types[rating];
    }

    private static class Text {

        private float x;
        private float toY;
        private float fromY;

        private void set(float x, float fromY, float toY) {
            this.x = x;
            this.toY = toY;
            this.fromY = fromY;
        }
    }

    private static class ClickAnalyser {

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

    public void setSmileySelectedListener(OnSmileySelectedListener listener) {
        mOnSmileySelectedListener = listener;
    }

    public interface OnSmileySelectedListener {
        void onSmileySelected(Type type);
    }

}
