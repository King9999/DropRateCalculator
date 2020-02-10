package com.example.dropratecalculator;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;


/*This screen will have information about the app, including who created it, the version number, and contact info. */

public class AboutActivity extends AppCompatActivity
{
    static final String VERSION = "1.0";
    static final String CREATOR = "Mike Murray";
    static final String EMAIL = "mikemurray056@gmail.com";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this line makes a reference to the XML file in the layout directory
        setContentView(R.layout.activity_about);

        //set up toolbar
        Toolbar aboutToolbar = findViewById(R.id.toolbar);
        aboutToolbar.setTitle("About");
        setSupportActionBar(aboutToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);  //sets up a back button in toolbar
        setSupportActionBar(aboutToolbar);
    }

    /* This code checks what menu item is selected. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {
            case android.R.id.home: //back button on toolbar pressed NOTE: must specify "android" for the app to retain any values entered previously
            {
                onBackPressed();
                return true;
            }

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
