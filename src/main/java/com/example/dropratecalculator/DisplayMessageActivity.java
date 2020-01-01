package com.example.dropratecalculator;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static android.os.SystemClock.sleep;

public class DisplayMessageActivity extends AppCompatActivity
{
    public static final float NUMERATOR = 1;
    int rollCount = 0;

    //set up the random number generator. Need to be able to capture the seed in case the user wants to use it again.
    //long seedValue = System.currentTimeMillis();
    //static Random randNum = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //MainActivity.randNum.setSeed(seedValue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.DROP_RATE);
        float dropRate = intent.getFloatExtra(MainActivity.DROP_RATE, MainActivity.dropRate);
        int totalRolls = intent.getIntExtra(MainActivity.ROLLS, MainActivity.rollCount);
        float weight = intent.getFloatExtra(MainActivity.WEIGHT, MainActivity.weightValue);
        long seed = intent.getLongExtra(MainActivity.SEED, MainActivity.seedValue);

        MainActivity.randNum.setSeed(seed);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView_dropRate);
        TextView percentView = findViewById(R.id.textView_percent);


        String dropRateText = Float.toString(dropRate);
        String percentText = String.format("%s%%", "(" + dropRate * 100.0f) + ")";
        textView.setText(dropRateText);
        percentView.setText(percentText);

        //Calculate and display the fractional value beside the drop rate
        TextView fractionView = findViewById(R.id.textView_fraction);
        float newDenominator = (dropRate <= 0) ? 0 : Math.round((NUMERATOR  / dropRate) * 100.0f) / 100.0f; //round to 2 decimal places. Must include ".0f" for the rounding to work
        String fractionText = Float.toString(newDenominator);

        if (newDenominator <= 0)
            fractionView.setText("(No value)");
        else
            fractionView.setText("(1/" + fractionText + ")");


        TextView rollTotalView = findViewById(R.id.textView_rolls);
        String rollTotalText = Integer.toString(totalRolls);
        rollTotalView.setText(rollTotalText);

        TextView weightView = findViewById(R.id.textView_weight);
        String weightText = Float.toString(weight);
        weightView.setText(weightText);

        //Display seed value
        TextView seedView = findViewById(R.id.textView_seed);
        String seedText = Long.toString(seed);
        seedView.setText(seedText);

        //Next we want to show the rolls in increments and show when there's success (a "hit")

        TextView rollCountView = findViewById(R.id.textView_currentRoll);
        String currentRollText;
        //randNum = new Random();
        float currentNum;
        char hit;               //either Y or N
        short hitTotal = 0;     //total number of successes
        float hitRate = 0;      //percentage of successful drops

        float[] rollValues = new float[totalRolls];
        char[] hitResults = new char[totalRolls];
        ArrayList hitLocations = new ArrayList();       //records which rolls a hit occurred


        //display the seed value
        Log.d("Seed", "Seed value is " + seed);


        for (int i = 0; i < totalRolls; i++)
        {
            rollCount++;

            //get a random number and check if it was a hit
            currentNum = MainActivity.randNum.nextFloat() * weight;
            rollValues[i] = currentNum;

            if (currentNum <= dropRate)
            {
                hit = 'Y';
                hitTotal++;
                hitLocations.add(rollCount);
            }
            else
                hit = 'N';

            hitResults[i] = hit;

            currentRollText = "Roll " + rollCount + "   Value: " + currentNum + "   Hit? " + hit + "\n";
            //rollCountView.setText(currentRollText);
            rollCountView.append(currentRollText);

        }

        //Display hit total, hit rate, and hit locations
        TextView hitCountView = findViewById(R.id.textView_hits);
        String hitCountText = Short.toString(hitTotal);
        hitCountView.setText(hitCountText);

        hitRate = (Math.round(((float)hitTotal / (float)totalRolls) * 100.0f) / 100.0f) * 100.0f;
        TextView hitRateView = findViewById(R.id.textView_hitRate);
        String hitRateText = hitRate + "%";
        hitRateView.setText(hitRateText);

        TextView hitLocationView = findViewById(R.id.textView_hitLocations);
        String hitLocationText;

        hitLocationView.setText(hitLocations.toString() + "\n");

    }

}
