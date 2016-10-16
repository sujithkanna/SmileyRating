package com.msapps.smilyrating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    private CheckBox mShowLines;
    private CheckBox mShowPoints;
    private RatingView mRatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowLines = (CheckBox) findViewById(R.id.show_lines);
        mShowPoints = (CheckBox) findViewById(R.id.show_points);
        mRatingView = (RatingView) findViewById(R.id.ratingView);
        mShowLines.setOnCheckedChangeListener(this);
        mShowPoints.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRatingView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRatingView.stop();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == mShowPoints) {
            mRatingView.showPoints(b);
        } else if (compoundButton == mShowLines) {
            mRatingView.showLines(b);
        }
    }
}
