package com.hsalf.smilerating.smileys;

import android.graphics.Color;

import com.hsalf.smilerating.Point;
import com.hsalf.smilerating.smileys.base.Smiley;

public class Okay extends Smiley {

    public Okay() {
        super(-135, 360);

        createStraightSmile(new Point(CENTER_SMILE, MOUTH_CENTER_Y), (CENTER_SMILE * 0.1f), 350f, (CENTER_SMILE * 0.9f));
    }

    @Override
    public int getFaceColor() {
        return Color.parseColor("#f2dd68");
    }

    @Override
    public int getDrawingColor() {
        return Color.parseColor("#353431");
    }
}
