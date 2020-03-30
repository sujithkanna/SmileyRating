package com.example.smilyrating;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hsalf.smileyrating.SmileyRating;

/**
 * This class is used for ...
 *
 * @autor MAO Hieng 3/30/2020
 */
public class CustomSmileyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customized_smiley);
        SmileyRating rating = findViewById(R.id.smiley);

        rating.setMaxSmiley(3);
        rating.setReverseSmiley(true);
        rating.setRating(1);
    }
}
