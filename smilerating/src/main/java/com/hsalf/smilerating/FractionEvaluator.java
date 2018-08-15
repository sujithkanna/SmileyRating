package com.hsalf.smilerating;

import android.util.Log;

public class FractionEvaluator {

    private static final String TAG = "FractionEvaluator";

    public float evaluate(float x, float start, float end) {
        if (start == end) {
            return 1;
        }
        Log.e(TAG, "evaluate: " + x + " - " + start + " - " + end);
        return (x - start) / (end - start);
    }
}
