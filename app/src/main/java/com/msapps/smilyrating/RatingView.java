package com.msapps.smilyrating;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends View {

    private static final String TAG = "RatingView";

    private Paint mPathPaint = new Paint();
    private Paint mPointPaint = new Paint();
    private Path mDrawingPath = new Path();

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
//        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint.setColor(Color.BLACK);
        mPointPaint.setStyle(Paint.Style.FILL);

        refreshPath();
    }

    private void refreshPath() {
        mDrawingPath.reset();
        mDrawingPath.moveTo(50, 50);
        mDrawingPath.cubicTo(100, 100, 150, 100, 200, 50);
        mDrawingPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(400, 400);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mDrawingPath, mPathPaint);
        canvas.drawCircle(50, 50, 5, mPointPaint);
        canvas.drawCircle(100, 100, 5, mPointPaint);
        canvas.drawCircle(150, 100, 5, mPointPaint);
        canvas.drawCircle(200, 50, 5, mPointPaint);
    }
}
