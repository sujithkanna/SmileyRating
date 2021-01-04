package com.example.smilyrating;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hsalf.smileyrating.SmileyView;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        SmileyView smileyView = findViewById(R.id.smiley_view);
        smileyView.setSmiley(3);
    }
}
