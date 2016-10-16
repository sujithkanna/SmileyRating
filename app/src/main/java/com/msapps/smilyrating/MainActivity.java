package com.msapps.smilyrating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG ="MainActivity";

    private SeekBar mProgressBar;
    private RatingView mRatingView;

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

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
