package com.hsalf.smilerating.smileys.base;

import android.animation.FloatEvaluator;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.hsalf.smilerating.Point;

public abstract class Smiley {

    private static final String TAG = "Smiley";

    protected static final float CENTER_X = .5f;
    protected static final float CENTER_Y = .5f;
    protected static final float CENTER_SMILE = .5f;
    protected static final float MOUTH_CENTER_Y = (1 / 2f) + (1 / 5f);

    public Point START_POINT;
    public Point[] TOP_CURVE = new Point[3];
    public Point[] RIGHT_CURVE = new Point[3];
    public Point[] BOTTOM_CURVE = new Point[3];
    public Point[] LEFT_CURVE = new Point[3];

    private Eye mLeftEye;
    private Eye mRightEye;

    private static final FloatEvaluator evaluator = new FloatEvaluator();

    public Smiley(float eyeLeftStartAngle, float eyeLeftSweepAngle) {
        mLeftEye = new Eye(Eye.Side.LEFT, eyeLeftStartAngle, eyeLeftSweepAngle);
        mRightEye = new Eye(Eye.Side.RIGHT, eyeLeftStartAngle, eyeLeftSweepAngle);
    }


    public void to(Smiley to, Path path, float fraction) {
        fillPath(this, to, path, fraction);
    }

    public static void fillPath(Smiley from, Smiley to, Path path, float fraction) {
        Log.i(TAG, "fillPath: " + fraction);
        path.reset();

        path.addCircle(CENTER_X, CENTER_Y, 0.5f, Path.Direction.CCW);

        path.moveTo(
                evaluator.evaluate(fraction, from.START_POINT.x, to.START_POINT.x),
                evaluator.evaluate(fraction, from.START_POINT.y, to.START_POINT.y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, from.TOP_CURVE[0].x, to.TOP_CURVE[0].x),
                evaluator.evaluate(fraction, from.TOP_CURVE[0].y, to.TOP_CURVE[0].y),
                evaluator.evaluate(fraction, from.TOP_CURVE[1].x, to.TOP_CURVE[1].x),
                evaluator.evaluate(fraction, from.TOP_CURVE[1].y, to.TOP_CURVE[1].y),
                evaluator.evaluate(fraction, from.TOP_CURVE[2].x, to.TOP_CURVE[2].x),
                evaluator.evaluate(fraction, from.TOP_CURVE[2].y, to.TOP_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, from.RIGHT_CURVE[0].x, to.RIGHT_CURVE[0].x),
                evaluator.evaluate(fraction, from.RIGHT_CURVE[0].y, to.RIGHT_CURVE[0].y),
                evaluator.evaluate(fraction, from.RIGHT_CURVE[1].x, to.RIGHT_CURVE[1].x),
                evaluator.evaluate(fraction, from.RIGHT_CURVE[1].y, to.RIGHT_CURVE[1].y),
                evaluator.evaluate(fraction, from.RIGHT_CURVE[2].x, to.RIGHT_CURVE[2].x),
                evaluator.evaluate(fraction, from.RIGHT_CURVE[2].y, to.RIGHT_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[0].x, to.BOTTOM_CURVE[0].x),
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[0].y, to.BOTTOM_CURVE[0].y),
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[1].x, to.BOTTOM_CURVE[1].x),
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[1].y, to.BOTTOM_CURVE[1].y),
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[2].x, to.BOTTOM_CURVE[2].x),
                evaluator.evaluate(fraction, from.BOTTOM_CURVE[2].y, to.BOTTOM_CURVE[2].y)
        );
        path.cubicTo(
                evaluator.evaluate(fraction, from.LEFT_CURVE[0].x, to.LEFT_CURVE[0].x),
                evaluator.evaluate(fraction, from.LEFT_CURVE[0].y, to.LEFT_CURVE[0].y),
                evaluator.evaluate(fraction, from.LEFT_CURVE[1].x, to.LEFT_CURVE[1].x),
                evaluator.evaluate(fraction, from.LEFT_CURVE[1].y, to.LEFT_CURVE[1].y),
                evaluator.evaluate(fraction, from.LEFT_CURVE[2].x, to.LEFT_CURVE[2].x),
                evaluator.evaluate(fraction, from.LEFT_CURVE[2].y, to.LEFT_CURVE[2].y)
        );
        path.close();

        from.mLeftEye.addEye(path, to.mLeftEye, fraction);
        from.mRightEye.addEye(path, to.mRightEye, fraction);
    }

    private void fillInverseReflectionPoints(float centerX, float centerY) {
        // Generate all points by reflecting given inputs
        TOP_CURVE[0] = getNextPoint(LEFT_CURVE[1], START_POINT, new Point());
        TOP_CURVE[1] = getReflectionPointX(centerX, TOP_CURVE[0]);
        TOP_CURVE[2] = getReflectionPointX(centerX, START_POINT);
        RIGHT_CURVE[0] = getReflectionPointX(centerX, LEFT_CURVE[1]);
        RIGHT_CURVE[1] = getReflectionPointX(centerX, LEFT_CURVE[0]);
        RIGHT_CURVE[2] = getReflectionPointX(centerX, BOTTOM_CURVE[2]);
        BOTTOM_CURVE[1] = getNextPoint(LEFT_CURVE[0], BOTTOM_CURVE[2], new Point());
        BOTTOM_CURVE[0] = getReflectionPointX(centerX, BOTTOM_CURVE[1]);
        switchX(TOP_CURVE[1], BOTTOM_CURVE[0]);
        inversePointY(centerY, TOP_CURVE[1], BOTTOM_CURVE[0]);
        switchX(TOP_CURVE[2], RIGHT_CURVE[2]);
        inversePointY(centerY, TOP_CURVE[2], RIGHT_CURVE[2]);
        switchX(RIGHT_CURVE[0], RIGHT_CURVE[1]);
        inversePointY(centerY, RIGHT_CURVE[0], RIGHT_CURVE[1]);
    }

    private void fillReflectionPoints(float centerX) {
        // Generate all points by reflecting given inputs
        TOP_CURVE[0] = getNextPoint(LEFT_CURVE[1], START_POINT, new Point());
        TOP_CURVE[1] = getReflectionPointX(centerX, TOP_CURVE[0]);
        TOP_CURVE[2] = getReflectionPointX(centerX, START_POINT);
        RIGHT_CURVE[0] = getReflectionPointX(centerX, LEFT_CURVE[1]);
        RIGHT_CURVE[1] = getReflectionPointX(centerX, LEFT_CURVE[0]);
        RIGHT_CURVE[2] = getReflectionPointX(centerX, BOTTOM_CURVE[2]);
        BOTTOM_CURVE[1] = getNextPoint(LEFT_CURVE[0], BOTTOM_CURVE[2], new Point());
        BOTTOM_CURVE[0] = getReflectionPointX(centerX, BOTTOM_CURVE[1]);

    }

    private Point getReflectionPointX(float centerX, Point source) {
        Point point = new Point();
        getNextPoint(source, new Point(centerX, source.y), point);
        return point;
    }

    private Point getReflectionPointY(float centerY, Point source) {
        Point point = new Point();
        getNextPoint(source, new Point(source.x, centerY), point);
        return point;
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

    public static float roundDegreeOfAngle(float angle) {
        if (angle < 0) {
            return roundDegreeOfAngle(angle + 360);
        } else if (angle >= 360) {
            return angle % 360;
        }
        return angle + 0.0f;
    }

    protected void createMirrorSmile(Point smileCenter, Point curveControl1, Point curveControl2,
                                     Point point1, Point point2) {
        float centerX = smileCenter.x;
        START_POINT = point1;
        BOTTOM_CURVE[2] = point2;
        LEFT_CURVE[0] = curveControl2;
        LEFT_CURVE[1] = curveControl1;
        LEFT_CURVE[2] = point1;

        fillReflectionPoints(centerX);
    }

    protected void createMirrorInverseSmile(Point smileCenter, Point curveControl1,
                                            Point curveControl2, Point point1, Point point2) {
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
        START_POINT = point1;
        BOTTOM_CURVE[2] = point2;
        LEFT_CURVE[0] = curveControl2;
        LEFT_CURVE[1] = curveControl1;
        LEFT_CURVE[2] = point1;

        fillReflectionPoints(centerX);
    }

    protected void createStraightSmile(Point smileCenter, float width,
                                       float angle, float length) {
        float centerX = smileCenter.x;
        float centerY = smileCenter.y;
        Point start = getPointByAngle(smileCenter, roundDegreeOfAngle(angle - 180), length / 2);

        LEFT_CURVE[0] = getPointByAngle(start, roundDegreeOfAngle(angle - 270), width);
        LEFT_CURVE[1] = getPointByAngle(start, roundDegreeOfAngle(angle - 90), width);
        start = getPointByAngle(start, angle, length / 6f);
        START_POINT = getPointByAngle(start, roundDegreeOfAngle(angle - 90), width);
        BOTTOM_CURVE[2] = getPointByAngle(start, roundDegreeOfAngle(angle - 270), width);
        LEFT_CURVE[2] = START_POINT;

        fillInverseReflectionPoints(centerX, centerY);

    }

    public static class Eye {

        public Eye(Side side, float startAngle, float sweepAngle) {
            this.eyeSide = side;
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;

            if (Side.LEFT == side) {
                center.x = .33f;
            } else {
                center.x = .67f;
                mirrorEye(this);
            }
            center.y = .35f;
        }

        public enum Side {
            LEFT, RIGHT
        }

        public float startAngle;
        public float sweepAngle;
        public Side eyeSide;
        public float radius = .08f;
        public Point center = new Point();
        private RectF eyePosition = new RectF();

        public RectF getEyePosition() {
            if (center != null) {
                eyePosition.set(center.x - radius, center.y - radius, center.x + radius
                        , center.y + radius);
            }
            return eyePosition;
        }

        public void addEye(Path path, Eye to, float fraction) {
            path.addArc(getEyePosition(),
                    evaluator.evaluate(fraction, this.startAngle, to.startAngle),
                    evaluator.evaluate(fraction, this.sweepAngle, to.sweepAngle)
            );
        }

        private static void mirrorEye(Eye eye) {
            float d2 = eye.startAngle + eye.sweepAngle - 180;
            eye.startAngle = -d2;
        }
    }

}
