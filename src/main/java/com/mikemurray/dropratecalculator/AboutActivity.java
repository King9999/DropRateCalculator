package com.mikemurray.dropratecalculator;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Objects;


/*This screen will have information about the app, including who created it, the version number, and contact info. */

public class AboutActivity extends AppCompatActivity
{
    private AdView mAdView;

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

        //set up ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
