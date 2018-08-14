package com.hsalf.smilerating.smiley2;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SmileyRating extends View {
    public SmileyRating(Context context) {
        super(context);
        init();
    }

    public SmileyRating(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileyRating(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
