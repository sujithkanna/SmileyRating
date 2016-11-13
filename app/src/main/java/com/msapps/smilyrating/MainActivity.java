package com.msapps.smilyrating;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "MainActivity";

    private SeekBar mSeekBar;
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
        mSeekBar = (SeekBar) findViewById(R.id.seeker);
        mSeekBar.setOnSeekBarChangeListener(this);
        mShowLines.setOnCheckedChangeListener(this);
        mShowPoints.setOnCheckedChangeListener(this);
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRatingView.switchMode();
            }
        });
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mRatingView.setFraction(i/100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
