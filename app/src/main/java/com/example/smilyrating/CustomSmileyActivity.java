package com.example.smilyrating;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hsalf.smileyrating.SmileyRating;

/**
 * This class is used for ...
 *
 * @autor MAO Hieng 3/30/2020
 */
public class CustomSmileyActivity extends AppCompatActivity {

    private static final String TAG = "CustomSmileyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customized_smiley);
        SmileyRating rating = findViewById(R.id.smiley);

        rating.setMaxSmiley(3);
        rating.setReverseSmiley(true);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                Log.i(TAG, "onSmileySelected: "+type);
            }
        });
        rating.setRating(1);
    }
}
