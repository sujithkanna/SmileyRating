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

    private float mEyeRadius = 25f;
    private Paint mPathPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();

    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
    private Path mEyePathLeft = new Path();
    private Path mEyePathRight = new Path();
    private Point mFaceCenter = new Point();
    private Path mSmilePath = new Path();
    private float divisions;
    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();
    private ArgbEvaluator mColorEvaluator = new ArgbEvaluator();

    @BaseRating.Smiley
    private int mode = RatingView.GOOD;

    private Smileys mSmileys;
    private float mTranslation = 0;
    private float mWidth;
    private float mHeight;
    private float mCenterY;

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
        mPathPaint.setColor(PAINT_COLOR);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.RED);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.BLUE);
        mPointPaint2.setStyle(Paint.Style.STROKE);

        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mValueAnimator.setDuration(1000);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mFaceCenter.x, mFaceCenter.y, mHeight / 2f, mBackgroundPaint);
        if (!mSmilePath.isEmpty()) {
            canvas.drawPath(mSmilePath, mPathPaint);
            canvas.drawPath(mEyePathLeft, mPathPaint);
            canvas.drawPath(mEyePathRight, mPathPaint);
        }
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
        if (OKAY == mode) {
            mode = GOOD;
        } else if (mode == GOOD) {
            mode = OKAY;
        }
        invalidate();
    }

    public void setFraction(float fraction) {
        if (mSmileys == null) {
            return;
        }
        mTranslation = mFloatEvaluator.evaluate(fraction, mHeight / 2, mWidth - (mHeight / 2));
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
        eyeLeft.center.y = mCenterY * 0.65f;
        eyeRight.center.x = (divisions * 21f) + mTranslation - mCenterY;
        eyeRight.center.y = mCenterY * 0.65f;
        mEyePathLeft = eyeLeft.fillPath(mEyePathLeft);
        mEyePathRight = eyeRight.fillPath(mEyePathRight);
    }
}
