package com.example.smilyrating;


import android.graphics.Path;

public class Curve {

    public Point start = new Point();
    public Point end = new Point();
    public Point controlStart = new Point();
    public Point controlEnd = new Point();

    public void fillPath(Path path) {
        path.moveTo(start.x, start.y);
        path.cubicTo(controlStart.x, controlStart.y, controlEnd.x, controlEnd.y, end.x, end.y);
    }
}
