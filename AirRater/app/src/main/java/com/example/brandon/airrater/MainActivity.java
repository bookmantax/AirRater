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
    private boolean startUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startUp = true;

        bottomBar = (AHBottomNavigation)findViewById(R.id.mainNavBar);
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
        if(position == 0 && (startUp || !wasSelected))
        {
            startUp = false;
            SearchFragment searchFragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, searchFragment).commit();
        }
        else if(position == 1 && !wasSelected)
        {
            UsersFragment usersFragment = new UsersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, usersFragment).commit();
        }
        else if(position == 2 && !wasSelected)
        {
            CheckInFragment checkInFragment = new CheckInFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, checkInFragment).commit();
        }
        else if(position == 3 && !wasSelected)
        {
            RatingFragment ratingFragment = new RatingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, ratingFragment).commit();
        }
        else if(position == 4 && !wasSelected)
        {
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFragment).commit();
        }
    }

    public void SetStartUp(boolean bool)
    {
        startUp = bool;
    }

    public void SetCurrentTab(int pos)
    {
        bottomBar.setCurrentItem(pos);
    }
}
