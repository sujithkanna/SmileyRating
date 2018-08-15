package com.hsalf.smilerating.smiley2;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hsalf.smilerating.FractionEvaluator;
import com.hsalf.smilerating.smileys.Bad;
import com.hsalf.smilerating.smileys.Good;
import com.hsalf.smilerating.smileys.Okay;
import com.hsalf.smilerating.smileys.Terrible;
import com.hsalf.smilerating.smileys.base.Smiley;

public class SmileyRating extends View {

    private static final String TAG = "SmileyRating";

    private static final float PLACEHOLDER_PADDING_SCALE = .6f;
    private static final float TOTAL_DIVIDER_SPACE_SCALE = .25f;
    private static final float CONNECTOR_LINE_SCALE_FROM_SMILEY_SIZE = .02f;

    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final FloatEvaluator FLOAT_EVALUATOR = new FloatEvaluator();
    private static final FractionEvaluator FRACTION_EVALUATOR = new FractionEvaluator();

    private Smiley[] mSmileys = new Smiley[]{
            new Terrible(), new Bad(), new Okay(), new Good(), new Good()
    };

    private RectF[] mPlaceHolders = new RectF[mSmileys.length];
    private Path[] mPlaceHolderPaths = new Path[mSmileys.length];

    public enum Type {
        NONE, GREAT, GOOD, OKAY, BAD, TERRIBLE
    }

    private Type mSelectedSmiley = Type.NONE;

    private float mSmileyPositionX = 0f;
    private Path mSmileyPath = new Path();
    private Paint mDrawPaint = new Paint();

    private int mFaceColor;
    private int mDrawingColor;
    private RectF mFacePosition = new RectF();
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
        // setBackgroundColor(Color.RED);
        setColors();

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(Color.BLACK);
        mDrawPaint.setStyle(Paint.Style.FILL);
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
        setSmileyPosition(0);
    }

    private int calculateHeight(int width) {
        int smileys = mSmileys.length;
        float estimatedHeight = width / smileys;
        // estimatedHeight += estimatedHeight * .25f; // Adding some space to place text below
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
    }

    private void applyScalesToSmileys(float scale) {
        for (int i = 0; i < mSmileys.length; i++) {
            mSmileys[i].scale(scale);
        }
    }

    private void createPlaceHolderSmileys() {
        for (int i = 0; i < mSmileys.length; i++) {
            Path path = new Path();
            RectF holder = mPlaceHolders[i];
            float dimen = holder.width() / 2;
            // float padding = holder.width() * ((1 - PLACEHOLDER_MAX_SCALE) / 2);
            mSmileys[i].drawFace(path);

            /*path.addCircle(
                    dimen, dimen, dimen, Path.Direction.CCW);*/
            mPlaceHolderPaths[i] = path;
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
            /*for (int i = 0; i < mPlaceHolders.length; i++) {
                canvas.drawRect(mPlaceHolders[i], mDrawPaint);
            }*/

            drawConnectorLine(canvas, mPlaceHolders);

            for (int i = 0; i < mPlaceHolderPaths.length; i++) {
                drawSmileyInRect(canvas, mPlaceHolders[i], mPlaceHolderPaths[i],
                        PLACEHOLDER_PADDING_SCALE, Color.WHITE, mPlaceholderBackgroundColor);
            }

            drawSmileyInRect(canvas, mFacePosition, mSmileyPath,
                    1, mDrawingColor, mFaceColor);
        }
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

    private void setSmileyPosition(float pointX) {
        float startX = mPlaceHolders[0].centerX();
        float endX = mPlaceHolders[mPlaceHolders.length - 1].centerX();
        if (pointX < startX) {
            pointX = startX;
        } else if (pointX > endX) {
            pointX = endX;
        }

        mSmileyPositionX = pointX;

        float fraction = FRACTION_EVALUATOR.evaluate(pointX, startX, endX); // fraction will be between 0 - 1

        int index = (int) Math.ceil(fraction / .202f);

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

        RectF smileySpace1 = mPlaceHolders[startIndex];
        RectF smileySpace2 = mPlaceHolders[endIndex];

        Smiley to = mSmileys[startIndex];
        Smiley from = mSmileys[endIndex];

        float drawFraction = FRACTION_EVALUATOR.evaluate(pointX,
                smileySpace1.centerX(), smileySpace2.centerX());

        from.drawFace(to, mSmileyPath, drawFraction);

        mFacePosition.set(smileySpace1);
        float space = smileySpace1.width() / 2;
        mFacePosition.left = pointX - space;
        mFacePosition.right = pointX + space;

        mFaceColor = from.getFaceColor();
        mDrawingColor = (int) ARGB_EVALUATOR.evaluate(drawFraction,
                from.getDrawingColor(), to.getDrawingColor());

    }


}
