package com.hsalf.smileyrating.smileys;

import android.graphics.Color;

import com.hsalf.smilerating.Point;
import com.hsalf.smileyrating.smileys.base.Smiley;

public class Okay extends Smiley {

    public Okay() {
        super(-135, 360);
        createStraightSmile(new Point(CENTER_SMILE, MOUTH_CENTER_Y), (CENTER_SMILE * 0.1f), 350f, (CENTER_SMILE * 0.9f));
        setup(
                getClass().getSimpleName(),
                Color.parseColor("#f2dd68"),
                Color.parseColor("#353431")
        );
    }

}
