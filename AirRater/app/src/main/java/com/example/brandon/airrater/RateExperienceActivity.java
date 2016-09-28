package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/26/2016.
 */
public class RateExperienceActivity extends ActionBarActivity
{
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String restaurantSubTypes[] = {"Grill", "Seafood", "Japanese", "Mexican", "Italian", "Buffet"};
    String drinksSubTypes[] = {"Sports Bar", "Pub", "Fancy Bar"};
    String entertainmentSubTypes[] = {"Movies", "Plays/Broadway", "Live Music", "Circus"};
    String location, businessName, comments;
    int typeId, subTypeId;
    EditText cityEditText, businessEditText, commentsEditText;
    Spinner typeSpinner, subTypeSpinner;
    RatingBar starsRatingBar;
    ArrayAdapter<String> spinnerTypeAdapter;
    ArrayAdapter<String> spinnerSubTypeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_experience);

        cityEditText = (EditText)findViewById(R.id.searchCityEditText);
        businessEditText = (EditText)findViewById(R.id.nameEditText);
        commentsEditText = (EditText)findViewById(R.id.commentsEditText);
        starsRatingBar = (RatingBar)findViewById(R.id.starsRatingBar);

        typeSpinner = (Spinner)findViewById(R.id.typeSpinner);
        spinnerTypeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, types);
        spinnerTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerTypeAdapter);

        subTypeSpinner = (Spinner)findViewById(R.id.subTypeSpinner);

        //When type is selected, populate subType
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(new RateExperienceActivity(), R.layout.support_simple_spinner_dropdown_item, restaurantSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 1;
                }
                else if(position == 1)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(new RateExperienceActivity(), R.layout.support_simple_spinner_dropdown_item, drinksSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 2;
                }
                else if(position == 2)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(new RateExperienceActivity(), R.layout.support_simple_spinner_dropdown_item, entertainmentSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        subTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Submit(View view)
    {
        location = String.valueOf(cityEditText.getText());
        businessName = String.valueOf(businessEditText.getText());
        comments = String.valueOf(commentsEditText.getText());

        //Service call to get user info (possibly use userId from phone database)
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("UserId", 1);//Get userId from phone database.
            object.put("location", location);
            object.put("BusinessName", businessName);
            object.put("Stars", starsRatingBar.getNumStars());
            object.put("Comments", comments);
            object.put("businessTypeId", typeId);
            object.put("businessSubTypeId", subTypeId);
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://hyperracing.com/api/setupsheets.ashx",
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes) {

                    //responder.uploadFinished(result != null);
                    super.onPostExecute(result, notes);
                    String s = result;
                }
            }).start();
        }
        catch (Exception e)
        {

        }
    }
}