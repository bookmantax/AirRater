package com.example.brandon.airrater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/5/2016.
 */

public class SplashActivity extends AppCompatActivity {

    LocationManager lm;
    Location location;
    String bestProvider, city, country;
    Criteria criteria;
    double latitude, longitude;
    Geocoder geocoder;
    List<Address> addresses;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        settings = getSharedPreferences("UserPreferences", 0);
        editor = settings.edit();
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        bestProvider = lm.getBestProvider(criteria, true);
        try {
            location = lm.getLastKnownLocation(bestProvider);
        } catch (SecurityException e) { }

        if(location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (Exception e){}
            if(addresses != null && addresses.size() > 0)
            {
                city = addresses.get(0).getAddressLine(1);
                country = addresses.get(0).getAddressLine(2);
                if(city != null && country != null)
                {
                    editor.putString("Location", city + ", " + country);
                }
            }
        }

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                if(settings.getInt("UserId", 0) == 0)
                {
                    startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                }
                else
                {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, secondsDelayed * 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
