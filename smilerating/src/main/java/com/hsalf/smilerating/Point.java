package com.hsalf.smilerating;

public class Point {
    public float x;
    public float y;

    public Point() {

    }

    public Point(Point point) {
        this(point.x, point.y);
    }

    public Point(float x, float y) {
        set(x, y);
    }

    public void set(Point point) {
        set(point.x, point.y);
    }

    public void set(Point point, float scale) {
        set(point.x, point.y, scale);
    }

    public void set(float x, float y, float scale) {
        set(x * scale, y * scale);
    }

    public void set(float x, float y) {
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