package com.example.brandon.airrater;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

/**
 * Created by Brandon on 10/5/2016.
 */

public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener
{
    private AHBottomNavigation bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = (AHBottomNavigation)findViewById(R.id.searchNavBar);
        bottomBar.setOnTabSelectedListener(this);
        this.CreateNavItems();
    }

    private void CreateNavItems()
    {
        AHBottomNavigationItem searchItem = new AHBottomNavigationItem("Search", R.drawable.ic_action_name);
        AHBottomNavigationItem usersItem = new AHBottomNavigationItem("Users", R.drawable.ic_action_name);
        AHBottomNavigationItem checkInItem = new AHBottomNavigationItem("CheckIn", R.drawable.ic_action_name);
        AHBottomNavigationItem ratingItem = new AHBottomNavigationItem("Rating", R.drawable.ic_action_name);
        AHBottomNavigationItem profileItem = new AHBottomNavigationItem("Profile", R.drawable.ic_action_name);

        bottomBar.addItem(searchItem);
        bottomBar.addItem(usersItem);
        bottomBar.addItem(checkInItem);
        bottomBar.addItem(ratingItem);
        bottomBar.addItem(profileItem);

        bottomBar.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomBar.setCurrentItem(0);
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected)
    {
        if(position == 0)
        {
            //do nothing
        }
        else if(position == 1)
        {
            Intent activity = new Intent(MainActivity.this, FindUsersActivity.class);
            startActivity(activity);
        }
        else if(position == 2)
        {
            Intent activity = new Intent(MainActivity.this, CheckinActivity.class);
            startActivity(activity);
        }
        else if(position == 3)
        {
            Intent activity = new Intent(MainActivity.this, RateExperienceActivity.class);
            startActivity(activity);
        }
        else if(position == 4)
        {
            Intent activity = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(activity);
        }
    }
}
