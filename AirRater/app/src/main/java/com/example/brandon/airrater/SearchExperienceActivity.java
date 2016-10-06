package com.example.brandon.airrater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 9/26/2016.
 */
public class SearchExperienceActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener
{
    private AHBottomNavigation bottomBar;
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String restaurantSubTypes[] = {"Grill", "Seafood", "Japanese", "Mexican", "Italian", "Buffet"};
    String drinksSubTypes[] = {"Sports Bar", "Pub", "Fancy Bar"};
    String entertainmentSubTypes[] = {"Movies", "Plays/Broadway", "Live Music", "Circus"};
    String location;
    int typeId, subTypeId;
    Spinner searchTypeSpinner, searchSubTypeSpinner;
    ArrayAdapter<String> spinnerTypeAdapter;
    ArrayAdapter<String> spinnerSubTypeAdapter;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experience);

        settings = getSharedPreferences("UserPreferences", 0);
        editor = settings.edit();

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.searchCityEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setText(settings.getString("Location", ""));
        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        bottomBar = (AHBottomNavigation)findViewById(R.id.searchNavBar);
        bottomBar.setOnTabSelectedListener(this);
        this.CreateNavItems();

        searchTypeSpinner = (Spinner)findViewById(R.id.searchTypeSpinner);
        spinnerTypeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, types);
        spinnerTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(spinnerTypeAdapter);

        searchSubTypeSpinner = (Spinner)findViewById(R.id.searchSubTypeSpinner);

        //When type is selected, populate subType
        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(SearchExperienceActivity.this, R.layout.support_simple_spinner_dropdown_item, restaurantSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 1;
                }
                else if(position == 1)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(SearchExperienceActivity.this, R.layout.support_simple_spinner_dropdown_item, drinksSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 2;
                }
                else if(position == 2)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(SearchExperienceActivity.this, R.layout.support_simple_spinner_dropdown_item, entertainmentSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        searchSubTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                subTypeId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Submit(View view)
    {
        location = String.valueOf(autocompleteFragmentLocation.getText(AutocompleteFilter.TYPE_FILTER_CITIES));

        if(RequiredInformationProvided(location, typeId)) {
            editor.putString("Location", location);
            editor.putInt("TypeId", typeId);
            editor.putInt("SubTypeId", subTypeId);
            Intent activity = new Intent(SearchExperienceActivity.this, ExperienceListActivity.class);
            startActivity(activity);
        }
        else
        {
            Context context = getApplicationContext();
            CharSequence text = "Please make sure at least Location and Business Type have values.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private boolean RequiredInformationProvided(String location, int typeId)
    {
        if(location != null && typeId != 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected)
    {
        if(position == 0)
        {
            SearchFragment searchFragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, searchFragment).commit();
        }
        else if(position == 1)
        {
            UsersFragment usersFragment = new UsersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, usersFragment).commit();
        }
        else if(position == 2)
        {
            CheckInFragment checkInFragment = new CheckInFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, checkInFragment).commit();
        }
        else if(position == 3)
        {
            RatingFragment ratingFragment = new RatingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, ratingFragment).commit();
        }
        else if(position == 4)
        {
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFragment).commit();
        }
    }
}
