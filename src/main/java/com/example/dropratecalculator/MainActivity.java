package com.example.dropratecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{
    public static final String DROP_RATE = "com.example.dropratecalculator.DROP_RATE";
    public static final String ROLLS = "com.example.dropratecalculator.ROLLS";
    //public static final float DEMONINATOR = 100;    //used to get the fractional drop rate
    public static float dropRate = 0;
    public static int rollCount = 1;
    public static final int MAX_ROLLS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Send button function **/
    public void sendMessage(View view)
    {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //Intent rollIntent =  new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.dropRateText);
        EditText rollText = findViewById(R.id.rollText);

        float dropRate;
        int roll;
        try
        {
            dropRate = Float.valueOf(editText.getText().toString());
        }
        catch (NumberFormatException n)
        {
            dropRate = 0;
            System.out.println("Drop rate value not valid. Drop rate will be set to 0.");
        }

        try
        {
            roll = Integer.valueOf(rollText.getText().toString());
        }
        catch (NumberFormatException n)
        {
            roll = 1;
            System.out.println("Roll value not valid. Roll will be set to 1.");
        }
        //float dropRate = Float.valueOf(editText.getText().toString());
        //int roll = Integer.valueOf(rollText.getText().toString());

        //Need to ensure the value is valid
        if (dropRate < 0)
            dropRate = 0;

        if (dropRate > 1)
            dropRate = 1;

        if (roll < 1)
            roll = 1;

        if (roll > MAX_ROLLS)
            roll = MAX_ROLLS;

        //Calculate the result and pass it to the next screen
        //dropRate = Math.round(dropRate * 1000.0f) / 1000.0f;  //round to 3 decimal places. Need the ".0f" for the rounding to work
        intent.putExtra(DROP_RATE, dropRate);
        intent.putExtra(ROLLS, roll);
        startActivity(intent);

    }
}
