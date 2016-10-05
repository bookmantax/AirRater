package com.example.brandon.airrater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExperienceListActivity extends AppCompatActivity
{
    ExpandableListView experienceExpandableListView;
    private ExpandableExperienceAdapter exAdapter;
    private List<ExperienceItem> listDataHeader;
    private HashMap<ExperienceItem, List<ExperienceDetails>> listDataChild;
    private List<ExperienceDetails> ratings;
    private ExperienceItem expItem;
    private ExperienceDetails expDetails;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_list);
        settings = getSharedPreferences("UserPreferences", 0);
        experienceExpandableListView = (ExpandableListView)findViewById(R.id.experienceExpandableListView);
        prepareListData();
        exAdapter = new ExpandableExperienceAdapter(this, listDataHeader, listDataChild);
        experienceExpandableListView.setAdapter(exAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("location", settings.getString("Location", ""));
            object.put("businessTypeId", settings.getInt("TypeId", 0));
            object.put("businessSubTypeId", settings.getInt("SubTypeId", 0));
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/GetRatings.ashx",
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes) {

                    //responder.uploadFinished(result != null);
                    super.onPostExecute(result, notes);
                    String s = result;
                    if (s.equalsIgnoreCase("Missing Information")) {
                        Context context = getApplicationContext();
                        CharSequence text = "Something went wrong, please try again.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else if(s.equalsIgnoreCase("No Ratings"))
                    {
                        //TODO call google API

                    } else
                    {
                        //Store user info in Shared Preferences
                        editor = settings.edit();
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject oneObject, detailsObject;
                            try {
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    oneObject = jsonArray.getJSONObject(i);
                                    expItem = new ExperienceItem(oneObject.getString("Name"),
                                            oneObject.getInt("numActiveCheckins"), oneObject.getInt("stars"), i);
                                    ratings = new ArrayList<ExperienceDetails>();
                                    JSONArray jsonRatings = new JSONArray(oneObject.getJSONArray("ratings"));
                                    for(int j = 0; j < jsonRatings.length(); j++)
                                    {
                                        detailsObject = jsonRatings.getJSONObject(j);
                                        expDetails = new ExperienceDetails(detailsObject.getString("employeeFirstName"),
                                                detailsObject.getString("employeeLastName"), detailsObject.getString("employeeAirline"),
                                                detailsObject.getString("comments"), detailsObject.getInt("numStars"));
                                        ratings.add(expDetails);
                                    }
                                    listDataHeader.add(expItem);
                                    listDataChild.put(listDataHeader.get(i), ratings);
                                }
                            } catch (JSONException e) {
                                // Oops
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
