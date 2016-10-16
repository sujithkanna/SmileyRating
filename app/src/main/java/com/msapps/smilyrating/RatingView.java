package com.msapps.smilyrating;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 11/10/16.
 */
public class RatingView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "RatingView";

    public static final int TERRIBLE = 0;
    public static final int BAD = 1;
    public static final int OKAY = 2;
    public static final int GOOD = 3;
    public static final int GREAT = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TERRIBLE, BAD, OKAY, GOOD, GREAT})
    public @interface Smiley {

    }

    private Paint mPathPaint = new Paint();
    private Paint mPointPaint1 = new Paint();
    private Paint mPointPaint2 = new Paint();
    private Path mDrawingPath = new Path();

    private ValueAnimator mValueAnimator = new ValueAnimator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();

    private Smileys mSmileys;

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

        mValueAnimator.setDuration(500);
        mValueAnimator.setIntValues(0, 100);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());

        mSmileys = Smileys.newInstance();
    }

    private void refreshPath(float fraction) {
        /*mDrawingPath.reset();

        mDrawingPath.moveTo(100, 500);
        // top curve
        mDrawingPath.cubicTo(150, 500, 350, 500, 400, 500);
        //Right curve
        mDrawingPath.cubicTo(450, 500, 450, 550, 400, mFloatEvaluator.evaluate(fraction, 600, 550));

        // bottom curve
        mDrawingPath.cubicTo(325, mFloatEvaluator.evaluate(fraction, 675, 550),
                mFloatEvaluator.evaluate(fraction, 175, 150),
                mFloatEvaluator.evaluate(fraction, 675, 550), 100,
                mFloatEvaluator.evaluate(fraction, 600, 550));

        // Left curve
        mDrawingPath.cubicTo(50, 550, 50, 500, 100, 500);

        mDrawingPath.close();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Smile smile = mSmileys.getSmile(GOOD);
        canvas.drawPath(smile.fillPath(mDrawingPath), mPathPaint);
        smile.drawPoints(canvas, mPointPaint1);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        refreshPath(fraction);
        invalidate();
    }

    public void start() {
//        mValueAnimator.start();
    }

    public void stop() {
//        mValueAnimator.end();
    }

    public void setFraction(float fraction) {
        refreshPath(fraction);
        invalidate();
    }

    private static class SmileyTransformer {

        private Path mPath;

        public SmileyTransformer() {

        }

    }

    private static class Smileys {

        private Map<Integer, Smile> mSmileys = new HashMap<>();

        private Smileys() {
            createGoodSmile();
            createGreatSmile();
        }

        public static Smileys newInstance() {
            return new Smileys();
        }

        public Path create(Path path, @Smile.Curve int smile) {
            Smile smilePoints = mSmileys.get(smile);
            return smilePoints.fillPath(path);
        }

        public Smile getSmile(@Smiley int smiley) {
            return mSmileys.get(smiley);
        }

        private void createGreatSmile() {
            Smile smile = new Smile();
            smile.START_POINT = new Point(100, 500);

            smile.TOP_CURVE[0] = new Point(150, 500);
            smile.TOP_CURVE[1] = new Point(350, 500);
            smile.TOP_CURVE[2] = new Point(400, 500);

            smile.RIGHT_CURVE[0] = new Point(450, 500);
            smile.RIGHT_CURVE[1] = new Point(450, 550);
            smile.RIGHT_CURVE[2] = new Point(400, 600);

            smile.BOTTOM_CURVE[0] = new Point(325, 675);
            smile.BOTTOM_CURVE[1] = new Point(175, 675);
            smile.BOTTOM_CURVE[2] = new Point(100, 600);

            smile.LEFT_CURVE[0] = new Point(50, 550);
            smile.LEFT_CURVE[1] = new Point(50, 500);
            smile.LEFT_CURVE[2] = new Point(100, 500);
            mSmileys.put(GREAT, smile);
        }

        private void createGoodSmile() {
            Smile smile = new Smile();
            smile.START_POINT = new Point(100, 500);

            smile.TOP_CURVE[0] = new Point(150, 500);
            smile.TOP_CURVE[1] = new Point(350, 500);
            smile.TOP_CURVE[2] = new Point(400, 500);

            smile.RIGHT_CURVE[0] = new Point(450, 500);
            smile.RIGHT_CURVE[1] = new Point(450, 550);
            smile.RIGHT_CURVE[2] = new Point(400, 600);

            smile.BOTTOM_CURVE[0] = new Point(325, 675);
            smile.BOTTOM_CURVE[1] = new Point(175, 675);
            smile.BOTTOM_CURVE[2] = new Point(100, 600);

            smile.LEFT_CURVE[0] = new Point(50, 550);
            smile.LEFT_CURVE[1] = new Point(50, 500);
            smile.LEFT_CURVE[2] = smile.START_POINT;
            mSmileys.put(GOOD, smile);
        }

    }

    private static class Smile {

        public static final int TOP = 0;
        public static final int LEFT = 0;
        public static final int RIGHT = 0;
        public static final int BOTTOM = 0;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TOP, LEFT, RIGHT, BOTTOM})
        public @interface Curve {

        }

        public Point START_POINT;

        public Point[] TOP_CURVE = new Point[3];

        public Point[] RIGHT_CURVE = new Point[3];
        public Point[] BOTTOM_CURVE = new Point[3];
        public Point[] LEFT_CURVE = new Point[3];

        public Smile() {

        }

        public Path fillPath(Path path) {
            path.reset();
            path.moveTo(START_POINT.x, START_POINT.y);
            path = cube(path, TOP_CURVE);
            path = cube(path, RIGHT_CURVE);
            path = cube(path, BOTTOM_CURVE);
            path = cube(path, LEFT_CURVE);
            path.close();
            return path;
        }

        private Path cube(Path path, Point[] curve) {
            path.cubicTo(
                    curve[0].x, curve[0].y,
                    curve[1].x, curve[1].y,
                    curve[2].x, curve[2].y
            );
            return path;
        }

        public void drawPoints(Canvas canvas, Paint paint) {
            drawPoint(START_POINT, canvas, paint);
            drawPointArray(TOP_CURVE, canvas, paint);
            drawPointArray(RIGHT_CURVE, canvas, paint);
            drawPointArray(BOTTOM_CURVE, canvas, paint);
            drawPointArray(LEFT_CURVE, canvas, paint);
        }

        private void drawPointArray(Point[] points, Canvas canvas, Paint paint) {
            for (Point point : points) {
                drawPoint(point, canvas, paint);
            }
        }

        private void drawPoint(Point point, Canvas canvas, Paint paint) {
            Log.i(TAG, point.toString());
            canvas.drawCircle(point.x, point.y, 6, paint);
        }

    }

    private static class Point {
        public float x;
        public float y;

        public Point() {

        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
