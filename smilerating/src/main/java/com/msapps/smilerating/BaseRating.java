package com.msapps.smilerating;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 16/10/16.
 */
public abstract class BaseRating extends View {

    private static final String TAG = "BaseSmile";

    public static final int TERRIBLE = 0;
    public static final int BAD = 1;
    public static final int OKAY = 2;
    public static final int GOOD = 3;
    public static final int GREAT = 4;

    protected int[] SMILES_LIST = new int[]{TERRIBLE, BAD, OKAY, GOOD, GREAT};

    public static final int POINT_1 = 0;
    public static final int POINT_2 = 1;
    public static final int COTROL_POINT_1 = 2;
    public static final int COTROL_POINT_2 = 3;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TERRIBLE, BAD, OKAY, GOOD, GREAT})
    public @interface Smiley {

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POINT_1, POINT_2, COTROL_POINT_1, COTROL_POINT_2})
    public @interface Coordinate {

    }

    public BaseRating(Context context) {
        super(context);
    }

    public BaseRating(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRating(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected static class Smileys {
        private int mWidth;
        private int mHeight;
        private float mCenterY;
        protected float mCenterSmile;
        private Map<Integer, Eye> mEyes = new HashMap<>();
        private Map<Integer, Smile> mSmileys = new HashMap<>();

        private Smileys(int w, int h) {
            mWidth = w;
            mHeight = h;
            mCenterY = (h / 2f) + (h / 5f);
            mCenterSmile = mHeight / 2f;
            createGreatSmile();
            createGoodSmile();
            createOkaySmile();
            createBadSmile();
            createTerribleSmile();
        }

        public static Smileys newInstance(int w, int h) {
            return new Smileys(w, h);
        }

        public Smile getSmile(@Smiley int smiley) {
            return mSmileys.get(smiley);
        }

        public Eye getEye(@Eye.EyeSide int eye) {
            Eye e = mEyes.get(eye);
            if (e == null) {
                e = new Eye();
                e.eyeSide = eye;
                mEyes.put(eye, e);
            }
            return e;
        }

        public void createSmile(Point smileCenter, Point curveControl1, Point curveControl2,
                                Point point1, Point point2, @Smile.Mode int fillMode,
                                @Smiley int smile, float width, float angle, float length) {
            if (Smile.MIRROR == fillMode) {
                createMirrorSmile(smileCenter,
                        curveControl1, curveControl2, point1, point2, smile);
            } else if (Smile.MIRROR_INVERSE == fillMode) {
                createMirrorInverseSmile(smileCenter,
                        curveControl1, curveControl2, point1, point2, smile);
            } else if (Smile.STRAIGHT == fillMode) {
                createStraightSmile(smileCenter, width, angle, length, smile);
            }
        }

        private void createMirrorInverseSmile(Point smileCenter, Point curveControl1,
                                              Point curveControl2, Point point1,
                                              Point point2, int smileType) {
            float centerX = smileCenter.x;
            float centerY = smileCenter.y;
            // Switching x
            float temp = curveControl1.x;
            curveControl1.x = curveControl2.x;
            curveControl2.x = temp;

            temp = point1.x;
            point1.x = point2.x;
            point2.x = temp;

            // Inverse the y axis of input
            inversePointY(centerY, point1, point2);
            inversePointY(centerY, curveControl1, curveControl2);

            // Generate all points by reflecting given inputs
            Smile smile = new Smile();
            smile.START_POINT = point1;
            smile.BOTTOM_CURVE[2] = point2;
            smile.LEFT_CURVE[0] = curveControl2;
            smile.LEFT_CURVE[1] = curveControl1;
            smile.LEFT_CURVE[2] = point1;

            fillReflectionPoints(centerX, smile);
            mSmileys.put(smileType, smile);
        }

        private void createMirrorSmile(Point smileCenter, Point curveControl1, Point curveControl2,
                                       Point point1, Point point2, @Smiley int smileType) {
            float centerX = smileCenter.x;
            float centerY = smileCenter.y;
            Smile smile = new Smile();
            smile.START_POINT = point1;
            smile.BOTTOM_CURVE[2] = point2;
            smile.LEFT_CURVE[0] = curveControl2;
            smile.LEFT_CURVE[1] = curveControl1;
            smile.LEFT_CURVE[2] = point1;

            fillReflectionPoints(centerX, smile);
            mSmileys.put(smileType, smile);
        }

        private void createStraightSmile(Point smileCenter, float width,
                                         float angle, float length, int smileType) {
            float centerX = smileCenter.x;
            float centerY = smileCenter.y;
            Point start = BaseRating.getPointByAngle(smileCenter, roundDegreeOfAngle(angle - 180), length / 2);
            Smile smile = new Smile();

            smile.LEFT_CURVE[0] = BaseRating.getPointByAngle(start, roundDegreeOfAngle(angle - 270), width);
            smile.LEFT_CURVE[1] = BaseRating.getPointByAngle(start, roundDegreeOfAngle(angle - 90), width);
            start = BaseRating.getPointByAngle(start, angle, length / 6f);
            smile.START_POINT = BaseRating.getPointByAngle(start, roundDegreeOfAngle(angle - 90), width);
            smile.BOTTOM_CURVE[2] = BaseRating.getPointByAngle(start, roundDegreeOfAngle(angle - 270), width);
            smile.LEFT_CURVE[2] = smile.START_POINT;

            fillInverseReflectionPoints(centerX, centerY, smile);
//            smile.START_POINT = BaseRating.getPointByAngle(smileCenter, roundDegreeOfAngle(angle - 180), length / 2);

            mSmileys.put(smileType, smile);
        }

        private void fillInverseReflectionPoints(float centerX, float centerY, Smile smile) {
            // Generate all points by reflecting given inputs
            smile.TOP_CURVE[0] = BaseRating.getNextPoint(smile.LEFT_CURVE[1], smile.START_POINT, new Point());
            smile.TOP_CURVE[1] = getReflectionPointX(centerX, smile.TOP_CURVE[0]);
            smile.TOP_CURVE[2] = getReflectionPointX(centerX, smile.START_POINT);
            smile.RIGHT_CURVE[0] = getReflectionPointX(centerX, smile.LEFT_CURVE[1]);
            smile.RIGHT_CURVE[1] = getReflectionPointX(centerX, smile.LEFT_CURVE[0]);
            smile.RIGHT_CURVE[2] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[2]);
            smile.BOTTOM_CURVE[1] = BaseRating.getNextPoint(smile.LEFT_CURVE[0], smile.BOTTOM_CURVE[2], new Point());
            smile.BOTTOM_CURVE[0] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[1]);
            switchX(smile.TOP_CURVE[1], smile.BOTTOM_CURVE[0]);
            inversePointY(centerY, smile.TOP_CURVE[1], smile.BOTTOM_CURVE[0]);
            switchX(smile.TOP_CURVE[2], smile.RIGHT_CURVE[2]);
            inversePointY(centerY, smile.TOP_CURVE[2], smile.RIGHT_CURVE[2]);
            switchX(smile.RIGHT_CURVE[0], smile.RIGHT_CURVE[1]);
            inversePointY(centerY, smile.RIGHT_CURVE[0], smile.RIGHT_CURVE[1]);
        }

        private void fillReflectionPoints(float centerX, Smile smile) {
            // Generate all points by reflecting given inputs
            smile.TOP_CURVE[0] = BaseRating.getNextPoint(smile.LEFT_CURVE[1], smile.START_POINT, new Point());
            smile.TOP_CURVE[1] = getReflectionPointX(centerX, smile.TOP_CURVE[0]);
            smile.TOP_CURVE[2] = getReflectionPointX(centerX, smile.START_POINT);
            smile.RIGHT_CURVE[0] = getReflectionPointX(centerX, smile.LEFT_CURVE[1]);
            smile.RIGHT_CURVE[1] = getReflectionPointX(centerX, smile.LEFT_CURVE[0]);
            smile.RIGHT_CURVE[2] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[2]);
            smile.BOTTOM_CURVE[1] = BaseRating.getNextPoint(smile.LEFT_CURVE[0], smile.BOTTOM_CURVE[2], new Point());
            smile.BOTTOM_CURVE[0] = getReflectionPointX(centerX, smile.BOTTOM_CURVE[1]);

        }

        private void switchX(Point p1, Point p2) {
            float temp = p1.x;
            p1.x = p2.x;
            p2.x = temp;
        }

        private void inversePointY(float centerY, Point p1, Point p2) {
            float temp = centerY - p1.y;
            p1.y = centerY - (p2.y - centerY);
            p2.y = centerY + temp;
        }

        private void createGreatSmile() {
            float div = 0.10f;
            FloatEvaluator f = new FloatEvaluator();
            createSmile(new Point(mCenterSmile, mCenterY),
                    new Point(f.evaluate(div, mCenterSmile * 0.295, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.23), mCenterY)),  // Top control
                    new Point(f.evaluate(div, mCenterSmile * 0.295, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.088), mCenterY)),  // Bottom control
                    new Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.23), mCenterY)), // Top Point
                    new Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + (mCenterSmile * 0.118), mCenterY)), // Bottom point
                    Smile.MIRROR, GREAT, -1f, -1f, -1f);
        }

        private void createGoodSmile() {
            float div = 0.20f;
            FloatEvaluator f = new FloatEvaluator();
            createSmile(new Point(mCenterSmile, mCenterY),
                    new Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.24), mCenterY)),  // Top control
                    new Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.029), mCenterY)),  // Bottom control
                    new Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.118), mCenterY)), // Top Point
                    new Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + (mCenterSmile * 0.118), mCenterY)), // Bottom point
                    Smile.MIRROR, GOOD, -1f, -1f, -1f);
        }

        private void createOkaySmile() {
            createSmile(new Point(mCenterSmile, mCenterY), null, null, null, null,
                    Smile.STRAIGHT, OKAY, (mCenterSmile * 0.094f), 350f, (mCenterSmile * 0.798f) /*75 + 75*/);
        }

        private void createBadSmile() {
            float div = 0.20f;
            FloatEvaluator f = new FloatEvaluator();
            createSmile(new Point(mCenterSmile, mCenterY),
                    new Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.24), mCenterY)),  // Top control
                    new Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.029), mCenterY)),  // Bottom control
                    new Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.118), mCenterY)), // Top Point
                    new Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + (mCenterSmile * 0.118), mCenterY)), // Bottom point
                    Smile.MIRROR_INVERSE, BAD, -1f, -1f, -1f);
        }

        private void createTerribleSmile() {
            float div = 0.20f;
            FloatEvaluator f = new FloatEvaluator();
            createSmile(new Point(mCenterSmile, mCenterY),
                    new Point(f.evaluate(div, mCenterSmile * 0.414, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.24), mCenterY)),  // Top control
                    new Point(f.evaluate(div, mCenterSmile * 0.355, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.029), mCenterY)),  // Bottom control
                    new Point(f.evaluate(div, mCenterSmile * 0.65, mCenterSmile), f.evaluate(div, mCenterY - (mCenterSmile * 0.118), mCenterY)), // Top Point
                    new Point(f.evaluate(div, mCenterSmile * 0.591, mCenterSmile), f.evaluate(div, mCenterY + (mCenterSmile * 0.118), mCenterY)), // Bottom point
                    Smile.MIRROR_INVERSE, TERRIBLE, -1f, -1f, -1f);
        }

        private Point getReflectionPointX(float centerX, Point source) {
            Point point = new Point();
            BaseRating.getNextPoint(source, new Point(centerX, source.y), point);
            return point;
        }

        private Point getReflectionPointY(float centerY, Point source) {
            Point point = new Point();
            BaseRating.getNextPoint(source, new Point(source.x, centerY), point);
            return point;
        }
    }

    protected static class EyeEmotion {

        private static final float BAD_START_ANGLE = -90;
        private static final float BAD_SWEEP_ANGLE = 270;

        private static final float TERRIBLE_START_ANGLE = -35;
        private static final float TERRIBLE_SWEEP_ANGLE = 280;

        private static final float OTHER_START_ANGLE = -135;
        private static final float OTHER_SWEEP_ANGLE = 360;

        public static Eye prepareEye(Eye eye, FloatEvaluator evaluator, float fraction, @Smiley int smile) {
            if (TERRIBLE == smile) {
                float startAngle = evaluator.evaluate(fraction, TERRIBLE_START_ANGLE, BAD_START_ANGLE);
                float sweepAngle = evaluator.evaluate(fraction, TERRIBLE_SWEEP_ANGLE, BAD_SWEEP_ANGLE);
                if (eye.eyeSide == Eye.LEFT) {
                    eye.startAngle = startAngle;
                    eye.sweepAngle = sweepAngle;
                } else {
                    mirrorEye(eye, startAngle, sweepAngle);
                }
            } else if (BAD == smile) {
                float startAngle = evaluator.evaluate(fraction, BAD_START_ANGLE, OTHER_START_ANGLE);
                float sweepAngle = evaluator.evaluate(fraction, BAD_SWEEP_ANGLE, OTHER_SWEEP_ANGLE);
                if (eye.eyeSide == Eye.LEFT) {
                    eye.startAngle = startAngle;
                    eye.sweepAngle = sweepAngle;
                } else {
                    mirrorEye(eye, startAngle, sweepAngle);
                }
            } else {
                eye.startAngle = OTHER_START_ANGLE;
                eye.sweepAngle = OTHER_SWEEP_ANGLE;
            }
            return eye;
        }

        private static void mirrorEye(Eye eye, float startAngle, float sweepAngle) {
            float d2 = startAngle + sweepAngle - 180;
            startAngle = -d2;
            eye.startAngle = startAngle;
            eye.sweepAngle = sweepAngle;
        }
    }

    protected static class Eye {

        public static final int LEFT = 0;
        public static final int RIGHT = 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({LEFT, RIGHT})
        public @interface EyeSide {

        }

        public float startAngle;
        public float sweepAngle;
        public Point center = new Point();
        @Eye.EyeSide
        public int eyeSide;
        public float radius;
        private RectF eyePosition = new RectF();

        public RectF getEyePosition() {
            if (center != null) {
                eyePosition.set(center.x - radius, center.y - radius, center.x + radius
                        , center.y + radius);
            }
            return eyePosition;
        }

        public Path fillPath(Path path) {
            if (path == null) {
                path = new Path();
            }
            path.addArc(getEyePosition(), startAngle, sweepAngle);
            return path;
        }

    }

    protected static class Smile {

        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int ALL = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({LEFT, RIGHT, ALL})
        public @interface Side {

        }

        public static final int MIRROR = 0;
        public static final int INDEPENDENT = 1;
        public static final int MIRROR_INVERSE = 2;
        public static final int STRAIGHT = 3;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({MIRROR, INDEPENDENT, MIRROR_INVERSE, STRAIGHT})
        public @interface Mode {

        }


        int mode = MIRROR;
        public Point START_POINT;
        public Point[] TOP_CURVE = new Point[3];
        public Point[] RIGHT_CURVE = new Point[3];
        public Point[] BOTTOM_CURVE = new Point[3];
        public Point[] LEFT_CURVE = new Point[3];

        public Smile() {
            this(MIRROR);
        }

        public Smile(@Mode int mode) {
            this.mode = mode;
        }

        public void transform(@Side int side, float x, float y) {
            if (ALL == side) {
                transformLeft(x, y);
                transformRight(x, y);
            } else if (RIGHT == side) {
                transformRight(x, y);
            } else if (LEFT == side) {
                transformLeft(x, y);
            }
        }

        private void transformLeft(float x, float y) {
            START_POINT.trans(x, y);
            LEFT_CURVE[0].trans(x, y);
            LEFT_CURVE[1].trans(x, y);
            BOTTOM_CURVE[2].trans(x, y);
            BOTTOM_CURVE[1].trans(x, y);
            TOP_CURVE[0].trans(x, y);
        }

        private void transformRight(float x, float y) {
            TOP_CURVE[1].trans(x, y);
            TOP_CURVE[2].trans(x, y);
            RIGHT_CURVE[0].trans(x, y);
            RIGHT_CURVE[1].trans(x, y);
            RIGHT_CURVE[2].trans(x, y);
            BOTTOM_CURVE[0].trans(x, y);
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
            /*drawPoint(LEFT_CURVE[1], canvas, paint);
            drawPoint(START_POINT, canvas, paint);*/
        }

        private void drawPointArray(Point[] points, Canvas canvas, Paint paint) {
            for (Point point : points) {
                drawPoint(point, canvas, paint);
            }
        }

        private void drawPoint(Point point, Canvas canvas, Paint paint) {
            if (point == null) return;
            Log.i(TAG, point.toString());
            canvas.drawCircle(point.x, point.y, 6, paint);
        }

    }

    protected static class Point {
        public float x;
        public float y;

        public Point() {

        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void trans(float x, float y) {
            this.x += x;
            this.y += y;
        }

        @Override

        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    protected static class Line {

        public Point start;
        public Point end;

        public Line() {

        }

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        public void draw(Canvas canvas, Paint paint) {
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }

        @Override
        public String toString() {
            return "Line{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    protected void translateSmile(Smile smile, float x, float y) {
        translatePoint(smile.START_POINT, x, y);
        translatePoints(smile.TOP_CURVE, x, y);
        translatePoints(smile.RIGHT_CURVE, x, y);
        translatePoints(smile.BOTTOM_CURVE, x, y);
        translatePoints(smile.LEFT_CURVE, x, y);
    }

    protected void translatePoints(Point[] points, float x, float y) {
        for (Point point : points) {
            translatePoint(point, x, y);
        }
    }

    protected void translatePoint(Point point, float x, float y) {
        point.x += x;
        point.y += y;
    }

    protected static Point getNextPoint(Point start, Point end, Point point) {
        float len = getDistance(start, end);
        float ratio = len < 0 ? -1f : 1f;
        point.x = end.x + ratio * (end.x - start.x);
        point.y = end.y + ratio * (end.y - start.y);
        return point;
    }

    protected static float getDistance(Point p1, Point p2) {
        return (float) Math.sqrt(
                (p1.x - p2.x) * (p1.x - p2.x) +
                        (p1.y - p2.y) * (p1.y - p2.y)
        );
    }

    protected static Point getPointByAngle(Point source, float angle, float width) {
        float endX = (float) (source.x + Math.cos(Math.toRadians(angle)) * width);
        float endY = (float) (source.y + Math.sin(Math.toRadians(angle)) * width);
        return new Point(endX, endY);

    }

    protected Path transformSmile(float trans, float fraction, Path path, Smile s1, Smile s2, FloatEvaluator evaluator) {
        path.reset();
        path.moveTo(
                evaluator.evaluate(fraction, s1.START_POINT.x, s2.START_POINT.x) + trans,
                evaluator.evaluate(fraction, s1.START_POINT.y, s2.START_POINT.y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, s1.TOP_CURVE[0].x, s2.TOP_CURVE[0].x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[0].y, s2.TOP_CURVE[0].y),
                evaluator.evaluate(fraction, s1.TOP_CURVE[1].x, s2.TOP_CURVE[1].x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[1].y, s2.TOP_CURVE[1].y),
                evaluator.evaluate(fraction, s1.TOP_CURVE[2].x, s2.TOP_CURVE[2].x) + trans,
                evaluator.evaluate(fraction, s1.TOP_CURVE[2].y, s2.TOP_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[0].x, s2.RIGHT_CURVE[0].x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[0].y, s2.RIGHT_CURVE[0].y),
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[1].x, s2.RIGHT_CURVE[1].x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[1].y, s2.RIGHT_CURVE[1].y),
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[2].x, s2.RIGHT_CURVE[2].x) + trans,
                evaluator.evaluate(fraction, s1.RIGHT_CURVE[2].y, s2.RIGHT_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[0].x, s2.BOTTOM_CURVE[0].x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[0].y, s2.BOTTOM_CURVE[0].y),
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[1].x, s2.BOTTOM_CURVE[1].x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[1].y, s2.BOTTOM_CURVE[1].y),
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[2].x, s2.BOTTOM_CURVE[2].x) + trans,
                evaluator.evaluate(fraction, s1.BOTTOM_CURVE[2].y, s2.BOTTOM_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, s1.LEFT_CURVE[0].x, s2.LEFT_CURVE[0].x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[0].y, s2.LEFT_CURVE[0].y),
                evaluator.evaluate(fraction, s1.LEFT_CURVE[1].x, s2.LEFT_CURVE[1].x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[1].y, s2.LEFT_CURVE[1].y),
                evaluator.evaluate(fraction, s1.LEFT_CURVE[2].x, s2.LEFT_CURVE[2].x) + trans,
                evaluator.evaluate(fraction, s1.LEFT_CURVE[2].y, s2.LEFT_CURVE[2].y)
        );
        path.close();
        return path;
    }

    public static float roundDegreeOfAngle(float angle) {
        if (angle < 0) {
            return roundDegreeOfAngle(angle + 360);
        } else if (angle >= 360) {
            return angle % 360;
        }
        return angle + 0.0f;
    }

}
