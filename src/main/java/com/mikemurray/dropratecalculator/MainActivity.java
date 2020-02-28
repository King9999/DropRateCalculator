/* DROP RATE CALCULATOR VERSION 1.1 BY MIKE MURRAY
    FEBRUARY 2020
 */

package com.mikemurray.dropratecalculator;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    public static final String DROP_RATE = "com.example.dropratecalculator.DROP_RATE";
    public static final String ROLLS = "com.example.dropratecalculator.ROLLS";
    public static final String WEIGHT = "com.example.dropratecalculator.WEIGHT";
    public static final String SEED = "com.example.dropratecalculator.SEED";
    public static float dropRate = 0;
    public static int rollCount = 1;
    public static final int MAX_ROLLS = 1000;
    public static float weightValue = 1;

    //set up the random number generator. Need to be able to capture the seed in case the user wants
    // to use it again.
    public static long seedValue = System.currentTimeMillis();
    public static Random randNum = new Random();

    private String selectedFormat;      //used to track which format is being used so that the
                                        //entered drop rate can be re-calculated if format changes

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //set up toolbar
        Toolbar mainToolbar = findViewById(R.id.toolbar);
        //mainToolbar.setTitle("test");
        setSupportActionBar(mainToolbar);

        //set up spinner. User can select different formats with this
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_format, android.R.layout.simple_spinner_item);

        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(formatAdapter);
        spinner.setOnItemSelectedListener(this);    //SEE METHODS BELOW FOR WHY THIS IS HERE

        //set up ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //used to display ad banner
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /* This is used to update any entered drop rates so that the user doesn't have to re-enter
        the values in the new format.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String previousFormat = selectedFormat;
        selectedFormat = spinner.getSelectedItem().toString();

        if (previousFormat != null && !previousFormat.equals(selectedFormat))
        {
            //Format changed.
            EditText dropRateText = findViewById(R.id.dropRateText);
            TextView instruction = findViewById(R.id.textView_dropRate);

            switch (selectedFormat)
            {
                case "[%] Percent":
                    //update the hint and instructions
                    dropRateText.setHint(R.string.text_percentHint);
                    instruction.setText(R.string.text_enterPercent);

                    if (!TextUtils.isEmpty(dropRateText.getText()))
                    {
                        //Update drop rate value based on what previous format was.
                        if (previousFormat.equals("[.] Decimal"))
                        {
                            //convert decimal to percent
                            float rate = Float.parseFloat(dropRateText.getText().toString())
                                    * 100.0f;
                            dropRateText.setText(String.valueOf(rate));
                        }
                        else    //fraction
                        {
                            //Divide 100 by whatever the value is to get the percent
                            float rate = 100.0f / Float.parseFloat(dropRateText.getText().toString());
                            dropRateText.setText(String.valueOf(rate));
                        }
                    }
                    break;

                case "[/] Odds":
                    //update the hint and instructions
                    dropRateText.setHint(R.string.text_fractionHint);
                    instruction.setText(R.string.text_enterFraction);

                    if (!TextUtils.isEmpty(dropRateText.getText()))
                    {
                        if (previousFormat.equals("[.] Decimal"))
                        {
                            //convert decimal to fraction
                            float rate = Float.parseFloat(dropRateText.getText().toString());
                            rate = (rate <= 0) ? 0 : Math.round((1 / rate) * 100.0f) / 100.0f;
                            dropRateText.setText(String.valueOf(rate));
                        }
                        else    //percent
                        {
                            //Divide 100 by whatever the value is to get the odds
                            float rate = 100.0f / Float.parseFloat(dropRateText.getText().toString());
                            dropRateText.setText(String.valueOf(rate));
                        }
                    }
                    break;

                case "[.] Decimal":
                    //update the hint and instructions
                    dropRateText.setHint(R.string.text_decimalHint);
                    instruction.setText(R.string.text_enterDecimal);

                    if (!TextUtils.isEmpty(dropRateText.getText()))
                    {
                        if (previousFormat.equals("[%] Percent"))
                        {
                            //convert percent to decimal
                            float rate = Float.parseFloat(dropRateText.getText().toString()) / 100.0f;
                            dropRateText.setText(String.valueOf(rate));
                        }
                        else    //fraction
                        {
                            //Divide 1 by whatever the value is to get the decimal
                            float rate = 1 / Float.parseFloat(dropRateText.getText().toString());
                            dropRateText.setText(String.valueOf(rate));
                        }
                    }
                    break;

                default:
                    break;
            }

        }   //end if

            //Log.d("dropRateText", "drop rate is empty");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        //not being used, but will stay just in case
    }

    /* This code creates the menu in the top right corner. When the menu button is clicked,
    the menu options appear.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    /* This code checks what menu item is selected. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        EditText seedText = findViewById(R.id.seedText);

        switch(item.getItemId())
        {
            case R.id.resetSeed:
            {
                //select a random seed from 1 to the current time in milliseconds
                //long maxSeed = System.currentTimeMillis();
                seedValue = randNum.nextLong();
                String seedStr = Long.toString(seedValue);
                seedText.setText(seedStr);
                Log.d("RandSeed", "Random Seed selected. Value is " + seedValue);
                Toast.makeText(this, getString(R.string.toast_newSeedMsg), Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.about:    //go to about screen
            {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }

            default:
                break;
        }
            return super.onOptionsItemSelected(item);
    }


    /** Send button function **/
    public void sendMessage(View view)
    {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //Intent rollIntent =  new Intent(this, DisplayMessageActivity.class);
        EditText dropRateText = findViewById(R.id.dropRateText);
        EditText rollText = findViewById(R.id.rollText);
        EditText weightText = findViewById(R.id.weightText);
        EditText seedText = findViewById(R.id.seedText);

        //float dropRate;
        int roll;
        float weight;
        long seed;
        try
        {
            dropRate = Float.valueOf(dropRateText.getText().toString());
        }
        catch (NumberFormatException n)
        {
            dropRate = 1;
            System.out.println("Drop rate value not valid. Drop rate will be set to 1.");
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

        try
        {
            weight = Float.valueOf(weightText.getText().toString());
        }
        catch (NumberFormatException n)
        {
            weight = 1;
            System.out.println("Weight value not valid. Weight will be set to 1.");
        }

        try
        {
            seed = Long.valueOf(seedText.getText().toString());
        }
        catch (NumberFormatException n)
        {
            //no value entered, so use System clock time.
            seed = seedValue;

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

        if (weight <= 0)
            weight = 1;

        //set up random.
       // randNum.setSeed(seed);

        //Calculate the result and pass it to the next screen
        //dropRate = Math.round(dropRate * 1000.0f) / 1000.0f;  //round to 3 decimal places. Need the ".0f" for the rounding to work
        intent.putExtra(DROP_RATE, dropRate);
        intent.putExtra(ROLLS, roll);
        intent.putExtra(WEIGHT, weight);
        intent.putExtra(SEED, seed);
        startActivity(intent);

    }


}
