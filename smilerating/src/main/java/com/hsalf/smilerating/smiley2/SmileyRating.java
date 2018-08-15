package com.hsalf.smilerating.smiley2;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
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
import android.view.MotionEvent;
import android.view.View;

import com.hsalf.smilerating.FractionEvaluator;
import com.hsalf.smilerating.Point;
import com.hsalf.smilerating.smileys.Bad;
import com.hsalf.smilerating.smileys.Good;
import com.hsalf.smilerating.smileys.Great;
import com.hsalf.smilerating.smileys.Okay;
import com.hsalf.smilerating.smileys.Terrible;
import com.hsalf.smilerating.smileys.base.Smiley;

public class SmileyRating extends View {

    private static final String TAG = "SmileyRating";

    private static final float DRAWING_PADDING_SCALE = .9f;
    private static final float PLACEHOLDER_PADDING_SCALE = .6f;
    private static final float TOTAL_DIVIDER_SPACE_SCALE = .25f;
    private static final float TEXT_SIZE_SCALE_FROM_SMILEY_SIZE = .2f;
    private static final float CONNECTOR_LINE_SCALE_FROM_SMILEY_SIZE = .02f;

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

    public enum Type {
        NONE, GREAT, GOOD, OKAY, BAD, TERRIBLE
    }

    private Type mSelectedSmiley = Type.NONE;

    private float mHolderScale = 0f;
    private float mSmileyPositionX = 0f;
    private int mCurrentFocusedIndex = 0;
    private Path mSmileyPath = new Path();
    private Paint mDrawPaint = new Paint();
    private TextPaint mTextPaint = new TextPaint();

    private int mFaceColor;
    private int mDrawingColor;
    private RectF mFacePosition = new RectF();

    private int mTextSelectedColor = Color.BLACK;
    private int mTextNonSelectedColor = Color.parseColor("#AEB3B5");
    private int mPlaceholderBackgroundColor = Color.parseColor("#e6e8ed");

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
        // setColors();

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(Color.BLACK);
        mDrawPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void setColors() {
        Smiley smiley;
        switch (mSelectedSmiley) {

            case NONE:
                mFaceColor = Color.TRANSPARENT;
                mDrawingColor = Color.TRANSPARENT;
                break;
            case GREAT:
                smiley = mSmileys[4];
                mFaceColor = smiley.getFaceColor();
                mDrawingColor = smiley.getDrawingColor();
                break;
            case GOOD:
                smiley = mSmileys[3];
                mFaceColor = smiley.getFaceColor();
                mDrawingColor = smiley.getDrawingColor();
                break;
            case OKAY:
                smiley = mSmileys[2];
                mFaceColor = smiley.getFaceColor();
                mDrawingColor = smiley.getDrawingColor();
                break;
            case BAD:
                smiley = mSmileys[1];
                mFaceColor = smiley.getFaceColor();
                mDrawingColor = smiley.getDrawingColor();
                break;
            case TERRIBLE:
                smiley = mSmileys[0];
                mFaceColor = smiley.getFaceColor();
                mDrawingColor = smiley.getDrawingColor();
                break;
        }
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
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
                if (i == mCurrentFocusedIndex) {
                    scale *= mHolderScale;
                    textTranslate = mHolderScale;
                }
                drawSmileyInRect(canvas, mPlaceHolders[i], mPlaceHolderPaths[i],
                        scale, Color.WHITE, mPlaceholderBackgroundColor);

                drawText(canvas, mSmileys[i], mTitlePoints[i], textTranslate);
            }

            drawSmileyInRect(canvas, mFacePosition, mSmileyPath,
                    DRAWING_PADDING_SCALE, mDrawingColor, mFaceColor);
        }
    }

    private void drawText(Canvas canvas, Smiley smiley, Text point, float trans) {
        int color = (Integer) ARGB_EVALUATOR.evaluate(1 - trans,
                mTextNonSelectedColor, mTextSelectedColor);
        mTextPaint.setColor(color);
        float textY = FLOAT_EVALUATOR.evaluate(1 - trans, point.fromY, point.toY);
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
                                  float scale, int drawingColor, int faceColor) {
        float padding = ((1 - scale) * (holder.width() / 2f));

        mDrawPaint.setColor(faceColor);
        canvas.drawCircle(holder.centerX(), holder.centerY(),
                (holder.width() / 2) * scale, mDrawPaint);

        int save = canvas.save();
        canvas.translate(holder.left + padding, holder.top + padding);
        canvas.scale(scale, scale, 0, 0);

        mDrawPaint.setColor(drawingColor);
        canvas.drawPath(path, mDrawPaint);

        canvas.restoreToCount(save);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setSmileyPosition(event.getX());
        return true;
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

    private static class Text {

        private float x;
        private float toY;
        private float fromY;

        public void set(float x, float fromY, float toY) {
            this.x = x;
            this.toY = toY;
            this.fromY = fromY;
        }
    }

}
