package com.example.dropratecalculator;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static android.os.SystemClock.sleep;

public class DisplayMessageActivity extends AppCompatActivity
{
    public static final float NUMERATOR = 1;
    int rollCount = 0;

    float dropRate;         //the chance that a roll is successful.
    int totalRolls;
    float weight;           //multiplier of the current roll. Makes success harder if weight is higher than 1.
    long seed;
    final static float textSize = 18;       //used for displaying table results

    short hitTotal = 0;     //total number of successes
    float hitRate = 0;      //percentage of successful drops

    float[] rollValues;     //tracks all the rolls made by RNG
    char[] hitResults;      //tracks all the successful rolls
    ArrayList hitLocations; //tracks which rolls were successful.

    //Table is used to display drop results
    TableLayout table;
    TableRow tableRow;

    TextView hitLocationView;
    boolean hitLocationsDisplayed = true;   //toggle for the above view
    
    //set up the random number generator. Need to be able to capture the seed in case the user wants to use it again.
    //long seedValue = System.currentTimeMillis();
    //static Random randNum = new Random();


    /* This code creates the menu in the top right corner. When the menu button is clicked,
    the menu options appear.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropresultmenu, menu);
        return true;
    }

    /*Button functionality to show/hide hit locations. */
    public void sendMessage(View view)
    {
        if (hitLocationsDisplayed)
        {
            //button was pressed, so hide the locations
            hitLocationView.setVisibility(View.GONE);
            hitLocationsDisplayed = false;
        }
        else
        {
            hitLocationView.setVisibility(View.VISIBLE);
            hitLocationsDisplayed = true;
        }
    }

    /* This code checks what menu item is selected. */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Upon clicking the save icon, a text file will be saved to device.
        FileOutputStream os = null;
        String fileName = "dropresults.txt";
        File saveFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        String seedText = Long.toString(seed);
        try
        {
            //TODO: Find a way to make file accessible on device.
            saveFile.createNewFile();
            os = new FileOutputStream(saveFile, false);
            //os = openFileOutput(saveFile, MODE_PRIVATE);
            os.write(seedText.getBytes());
            Toast.makeText(this, "Saved to " + getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + fileName, Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
           if (os != null)
           {
               try
               {
                   os.close();      //close output stream. This always must be done!
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
        }

        return super.onOptionsItemSelected(item);
    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //MainActivity.randNum.setSeed(seedValue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //set up toolbar
        Toolbar resultToolbar = findViewById(R.id.toolbar);
        resultToolbar.setTitle("Drop Results");
        setSupportActionBar(resultToolbar);

        //set up table
        table = findViewById(R.id.rollTable);
        table.setColumnStretchable(0, true);
        table.setColumnStretchable(1, true);    //this code determines # of columns
        //table.setColumnStretchable(2, true);

        //tableRow = new TableRow(this);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.DROP_RATE);
         dropRate = intent.getFloatExtra(MainActivity.DROP_RATE, MainActivity.dropRate);
         totalRolls = intent.getIntExtra(MainActivity.ROLLS, MainActivity.rollCount);
         weight = intent.getFloatExtra(MainActivity.WEIGHT, MainActivity.weightValue);
         seed = intent.getLongExtra(MainActivity.SEED, MainActivity.seedValue);

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

        hitLocationView = findViewById(R.id.textView_hitLocations);

        //Next we want to show the rolls in increments and show when there's success (a "hit")

        //TextView rollCountView = findViewById(R.id.textView_currentRoll);
        String currentRollText;
        //randNum = new Random();
        float currentNum;
        char hit;               //either Y or N
        hitTotal = 0;     //total number of successes
        hitRate = 0;      //percentage of successful drops

        rollValues = new float[totalRolls];
        //hitResults = new char[totalRolls];
        hitLocations = new ArrayList();       //records which rolls a hit occurred


        //display the seed value
        Log.d("Seed", "Seed value is " + seed);


        //set up the table headers
        tableRow = new TableRow(this);
        tableRow.setGravity(Gravity.CENTER);

        TextView rollNumCol = new TextView(this);
        TextView rollValCol = new TextView(this);
        //TextView rollHitCol = new TextView(this);

        rollNumCol.setText("Roll#");
        rollNumCol.setGravity(Gravity.CENTER);
        rollNumCol.setTextSize(textSize);
        rollNumCol.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //rollNumCol.setTextAppearance(0x00000002);

        rollValCol.setText("Value");
        rollValCol.setGravity(Gravity.CENTER);
        rollValCol.setTextSize(textSize);
        rollValCol.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        /*rollHitCol.setText("Success?");
        rollHitCol.setGravity(Gravity.CENTER);
        rollHitCol.setTextSize(textSize);
        rollHitCol.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));*/

        tableRow.addView(rollNumCol);
        tableRow.addView(rollValCol);
        //tableRow.addView(rollHitCol);
        table.addView(tableRow);

        //set up other table contents. This should help speed things up in case the roll is large.
       /* TextView[] rollCountView = new TextView[totalRolls];
        TextView[] rollValView = new TextView[totalRolls];
        TextView[] rollHitView = new TextView[totalRolls];



        for (int j = 0; j < totalRolls; j++)
        {
            tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);

            rollCountView[j].setText(Integer.toString(rollCount));
            rollCountView[j].setGravity(Gravity.CENTER);
            rollCountView[j].setTextSize(textSize);


        }*/

        /* For performance reasons, the table will only collect and display the successful rolls. All other information is still presented. */
        for (int i = 0; i < totalRolls; i++)
        {


            rollCount++;

            //get a random number and check if it was a hit
            currentNum = MainActivity.randNum.nextFloat() * weight;
            rollValues[i] = currentNum;

            if (currentNum <= dropRate)
            {
                //hit = 'Y';
                hitTotal++;
                hitLocations.add(rollCount);

                //add new entry to the table
                tableRow = new TableRow(this);
                tableRow.setGravity(Gravity.CENTER);

                TextView rollCountView = new TextView(this);
                TextView rollValView = new TextView(this);

                rollCountView.setText(Integer.toString(rollCount));
                rollCountView.setGravity(Gravity.CENTER);
                rollCountView.setTextSize(textSize);

                rollValView.setText(Float.toString(currentNum));
                rollValView.setGravity(Gravity.CENTER);
                rollValView.setTextSize(textSize);

                tableRow.addView(rollCountView);
                tableRow.addView(rollValView);
                table.addView(tableRow);
            }
            //else
               // hit = 'N';

           // hitResults[i] = hit;


            //currentRollText = "Roll " + rollCount + "   Value: " + currentNum + "   Hit? " + hit + "\n";
            //rollCountView.setText(currentRollText);
            //rollCountView.append(currentRollText);

            //display current result in the table
            /*tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);

            TextView rollCountView = new TextView(this);
            TextView rollValView = new TextView(this);
            TextView rollHitView = new TextView(this);

            rollCountView.setText(Integer.toString(rollCount));
            rollCountView.setGravity(Gravity.CENTER);
            rollCountView.setTextSize(textSize);

            rollValView.setText(Float.toString(currentNum));
            rollValView.setGravity(Gravity.CENTER);
            rollValView.setTextSize(textSize);

            rollHitView.setText(Character.toString(hit));
            rollHitView.setGravity(Gravity.CENTER);
            rollHitView.setTextSize(textSize);

            tableRow.addView(rollCountView);
            tableRow.addView(rollValView);
            tableRow.addView(rollHitView);
            table.addView(tableRow);*/

        }  //end for

        //Display hit total, hit rate, and hit locations
        TextView hitCountView = findViewById(R.id.textView_hits);
        String hitCountText = Short.toString(hitTotal);
        hitCountView.setText(hitCountText);

        float a = (float)hitTotal;
        float b = (float)totalRolls;
        hitRate = 100.0f * ((a / b) * 100.0f) / 100.0f; //for some reason I can't use Math.round as I lose precision. will leave things like this for now.
        TextView hitRateView = findViewById(R.id.textView_hitRate);
        String hitRateText = hitRate + "%";
        hitRateView.setText(hitRateText);

        //TextView hitLocationView = findViewById(R.id.textView_hitLocations);
        //hitLocationView.setVisibility(View.GONE);
        //String hitLocationText;

        hitLocationView.setText(hitLocations.toString() + "\n");

    }

}
