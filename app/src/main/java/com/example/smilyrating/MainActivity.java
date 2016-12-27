package com.example.smilyrating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.msapps.smilerating.BaseRating;
import com.msapps.smilerating.SmileRating;

public class MainActivity extends AppCompatActivity implements SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener {

    private static final String TAG = "MainActivity";

    private SmileRating mSmileRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSmileRating = (SmileRating) findViewById(R.id.ratingView);
        mSmileRating.setOnSmileySelectionListener(this);
        mSmileRating.setOnRatingSelectedListener(this);
    }

    @Override
    public void onSmileySelected(@BaseRating.Smiley int smiley) {
        switch (smiley) {
            case BaseRating.BAD:
                Log.i(TAG, "Bad");
                break;
            case BaseRating.GOOD:
                Log.i(TAG, "Good");
                break;
            case BaseRating.GREAT:
                Log.i(TAG, "Great");
                break;
            case BaseRating.OKAY:
                Log.i(TAG, "Okay");
                break;
            case BaseRating.TERRIBLE:
                Log.i(TAG, "Terrible");
                break;
        }
    }

    @Override
    public void onRatingSelected(int level) {
        Log.i(TAG, "Rated as: " + level);
    }
}
