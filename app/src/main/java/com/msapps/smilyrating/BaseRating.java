package com.msapps.smilyrating;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        private List<Line> mVerticalLines = new ArrayList<>();
        private List<Line> mHorizontalLines = new ArrayList<>();

        private Map<Integer, Smile> mSmileys = new HashMap<>();

        private Smileys() {
            createGreatSmile();
            createGoodSmile();
        }

        public static Smileys newInstance() {
            return new Smileys();
        }

        public Smile getSmile(@Smiley int smiley) {
            return mSmileys.get(smiley);
        }

        public void createSmile(float centerX, Point curveControl1, Point curveControl2,
                                Point point1, Point point2, @Smile.Mode int fillMode, @Smiley int smile) {
            if (Smile.MIRROR == fillMode) {
                createSmile(centerX, curveControl1, curveControl2, point1, point2, smile);
            }
        }

        private void createSmile(float centerX, Point curveControl1, Point curveControl2,
                                 Point point1, Point point2, @Smiley int smileType) {

            Smile smile = new Smile();
            smile.START_POINT = point1;
            smile.BOTTOM_CURVE[2] = point2;
            smile.LEFT_CURVE[0] = curveControl2;
            smile.LEFT_CURVE[1] = curveControl1;
            smile.LEFT_CURVE[2] = point1;

            smile.TOP_CURVE[0] = BaseRating.getOppositePoint(smile.LEFT_CURVE[1], smile.START_POINT, new Point());
            smile.TOP_CURVE[1] = getReflectionPoint(centerX, smile.TOP_CURVE[0]);
            smile.TOP_CURVE[2] = getReflectionPoint(centerX, smile.START_POINT);
            smile.RIGHT_CURVE[0] = getReflectionPoint(centerX, smile.LEFT_CURVE[1]);
            smile.RIGHT_CURVE[1] = getReflectionPoint(centerX, smile.LEFT_CURVE[0]);
            smile.RIGHT_CURVE[2] = getReflectionPoint(centerX, smile.BOTTOM_CURVE[2]);
            smile.BOTTOM_CURVE[1] = BaseRating.getOppositePoint(smile.LEFT_CURVE[0], smile.BOTTOM_CURVE[2], new Point());
            smile.BOTTOM_CURVE[0] = getReflectionPoint(centerX, smile.BOTTOM_CURVE[1]);

            mSmileys.put(smileType, smile);
        }

        private void createGreatSmile() {
            createSmile(175,
                    new Point(50, 500),
                    new Point(50, 525),
                    new Point(100, 500),
                    new Point(100, 560),
                    Smile.MIRROR, GREAT);
        }

        private void createGoodSmile() {
            createSmile(175,
                    new Point(70, 500),  // Top control
                    new Point(60, 535),  // Bottom control
                    new Point(110, 520), // Top Point
                    new Point(100, 560), // Bottom point
                    Smile.MIRROR, GOOD);
        }

        private Point getReflectionPoint(float centerX, Point source) {
            Point point = new Point();
            BaseRating.getOppositePoint(source, new Point(centerX, source.y), point);
            return point;
        }

        public void onChangeLayout(int width, int height) {
            float center = height / 6f;
            mVerticalLines.add(new Line(new Point(0, 0), new Point(width, 0)));
            mVerticalLines.add(new Line(new Point(0, center), new Point(width, center)));
            mVerticalLines.add(new Line(new Point(0, center * 2), new Point(width, center * 2)));
            mVerticalLines.add(new Line(new Point(0, center * 3), new Point(width, center * 3)));
            mVerticalLines.add(new Line(new Point(0, center * 4), new Point(width, center * 4)));
            mVerticalLines.add(new Line(new Point(0, center * 5), new Point(width, center * 5)));
            mVerticalLines.add(new Line(new Point(0, -1 + center * 6), new Point(width, -1 + center * 6)));

            center = width / 6f;
            mHorizontalLines.add(new Line(new Point(center, 0), new Point(center, height)));
            mHorizontalLines.add(new Line(new Point(center * 2, 0), new Point(center * 2, height)));
            mHorizontalLines.add(new Line(new Point(center * 3, 0), new Point(center * 3, height)));
            mHorizontalLines.add(new Line(new Point(center * 4, 0), new Point(center * 4, height)));
            mHorizontalLines.add(new Line(new Point(center * 5, 0), new Point(center * 5, height)));
        }

        public void drawLines(Canvas canvas, Paint paint) {
            for (Line line : mVerticalLines) {
                line.draw(canvas, paint);
            }

            for (Line line : mHorizontalLines) {
                line.draw(canvas, paint);
            }
        }
    }

    protected static class Smile {

        public static final int LEFT = 0;
        public static final int RIGHT = 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({LEFT, RIGHT})
        public @interface Source {

        }

        public static final int MIRROR = 0;
        public static final int INDEPENDENT = 1;
        public static final int MIRROR_INVERSE = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({MIRROR, INDEPENDENT, MIRROR_INVERSE})
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

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private static class Line {

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

    protected static Point getOppositePoint(Point start, Point end, Point point) {
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

    protected Path transformSmile(float fraction, Path path, Smile s1, Smile s2, FloatEvaluator evaluator) {
        float trans = evaluator.evaluate(fraction, 0, 350);
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

}
