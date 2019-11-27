package com.example.dropratecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.DROP_RATE);
        float dropRate = intent.getFloatExtra(MainActivity.DROP_RATE, MainActivity.dropRate);
        int rolls = intent.getIntExtra(MainActivity.ROLLS, MainActivity.rollCount);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView_dropRate);
        String dropRateText = String.format("%s%%", Float.toString(dropRate));
        textView.setText(dropRateText);

        TextView rollView = findViewById(R.id.textView_rolls);
        String rollText = Integer.toString(rolls);
        rollView.setText(rollText);
    }
}
