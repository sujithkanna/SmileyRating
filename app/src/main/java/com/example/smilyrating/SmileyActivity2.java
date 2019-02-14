package com.example.smilyrating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hsalf.smilerating.smiley2.SmileyRating;

public class SmileyActivity2 extends AppCompatActivity {

    private SmileyRating mSmileyRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smiley_2);
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmileyRating.resetSmiley();
            }
        });
        mSmileyRating = (SmileyRating) findViewById(R.id.smiley);
    }
}
