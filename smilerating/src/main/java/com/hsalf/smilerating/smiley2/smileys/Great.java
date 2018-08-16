package com.hsalf.smilerating.smiley2.smileys;

import android.animation.FloatEvaluator;
import android.graphics.Color;

import com.hsalf.smilerating.Point;
import com.hsalf.smilerating.smiley2.smileys.base.Smiley;

public class Great extends Smiley {

    public Great() {
        super(-135, 360);

        // Create mouth
        float div = 0.10f;
        FloatEvaluator f = new FloatEvaluator();
        createMirrorSmile(new Point(0.5f, 0.5f),
                new Point(f.evaluate(div, CENTER_SMILE * 0.295, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.23), MOUTH_CENTER_Y)),  // Top control
                new Point(f.evaluate(div, CENTER_SMILE * 0.295, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.088), MOUTH_CENTER_Y)),  // Bottom control
                new Point(f.evaluate(div, CENTER_SMILE * 0.591, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.23), MOUTH_CENTER_Y)), // Top Point
                new Point(f.evaluate(div, CENTER_SMILE * 0.591, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y + (CENTER_SMILE * 0.118), MOUTH_CENTER_Y)) // Bottom point
        );


    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
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
