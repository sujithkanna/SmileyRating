package com.hsalf.smilerating.smileys;

import android.animation.FloatEvaluator;

import com.hsalf.smilerating.Point;
import com.hsalf.smilerating.smileys.base.Smiley;

public class Good extends Smiley {

    public Good() {
        super(-135, 360);

        float div = 0.20f;
        FloatEvaluator f = new FloatEvaluator();
        createMirrorSmile(new Point(CENTER_SMILE, MOUTH_CENTER_Y),
                new Point(f.evaluate(div, CENTER_SMILE * 0.414, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.24), MOUTH_CENTER_Y)),  // Top control
                new Point(f.evaluate(div, CENTER_SMILE * 0.355, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.029), MOUTH_CENTER_Y)),  // Bottom control
                new Point(f.evaluate(div, CENTER_SMILE * 0.65, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y - (CENTER_SMILE * 0.118), MOUTH_CENTER_Y)), // Top Point
                new Point(f.evaluate(div, CENTER_SMILE * 0.591, CENTER_SMILE), f.evaluate(div, MOUTH_CENTER_Y + (CENTER_SMILE * 0.118), MOUTH_CENTER_Y)) // Bottom point
        );
    }
}
