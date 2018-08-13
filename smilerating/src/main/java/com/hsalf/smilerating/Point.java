package com.hsalf.smilerating;

public class Point {
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