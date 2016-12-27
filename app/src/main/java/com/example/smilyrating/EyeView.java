package com.example.smilyrating;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sujith on 28/10/16.
 */
public class EyeView extends View {

    private static final String TAG = "EyeView";

    private Path mPath = new Path();
    private RectF mRectF = new RectF();
    private Paint mPathPaint = new Paint();

    public EyeView(Context context) {
        super(context);
        init();
    }

    public EyeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EyeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.parseColor("#232323"));
        mPathPaint.setStyle(Paint.Style.FILL);

        mRectF.set(0, 0, 50, 50);
        mPath.addArc(mRectF, -90, 270);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(50, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPathPaint);
    }
}
