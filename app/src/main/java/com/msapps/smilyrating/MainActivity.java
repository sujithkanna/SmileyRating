package com.msapps.smilyrating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private RatingView mRatingView;
    private SeekBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRatingView = (RatingView) findViewById(R.id.ratingView);
        mProgressBar = (SeekBar) findViewById(R.id.progress);
        mProgressBar.setOnSeekBarChangeListener(this);
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mRatingView.setFraction(i / 100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
