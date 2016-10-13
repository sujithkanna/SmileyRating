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
    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
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
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStyle(Paint.Style.FILL);

        mPointPaint1.setColor(Color.BLACK);
        mPointPaint1.setStyle(Paint.Style.FILL);

        mPointPaint2.setColor(Color.GREEN);
        mPointPaint2.setStyle(Paint.Style.FILL);
        refreshPath();
    }

    private void refreshPath() {
        mDrawingPath.reset();

        mDrawingPath.moveTo(100, 500);
        // top curve
        mDrawingPath.cubicTo(150, 500, 350, 500, 400, 500);
        //Right curve
        mDrawingPath.cubicTo(450, 500, 450, 550, 400, 550);

        // bottom curve
        mDrawingPath.cubicTo(350, 550, 150, 550, 100, 550);

        // Left curve
        mDrawingPath.cubicTo(50, 550, 50, 500, 100, 500);

        mDrawingPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(mDrawingPath);
        canvas.drawPath(mDrawingPath, mPathPaint);
    }

}
