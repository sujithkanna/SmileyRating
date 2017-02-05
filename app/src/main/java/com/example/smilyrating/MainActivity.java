package com.example.smilyrating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

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
            case SmileRating.BAD:
                Log.i(TAG, "Bad");
                break;
            case SmileRating.GOOD:
                Log.i(TAG, "Good");
                break;
            case SmileRating.GREAT:
                Log.i(TAG, "Great");
                break;
            case SmileRating.OKAY:
                Log.i(TAG, "Okay");
                break;
            case SmileRating.TERRIBLE:
                Log.i(TAG, "Terrible");
                break;
        }
    }

    @Override
    public void onRatingSelected(int level) {
        Log.i(TAG, "Rated as: " + level);
    }
}
