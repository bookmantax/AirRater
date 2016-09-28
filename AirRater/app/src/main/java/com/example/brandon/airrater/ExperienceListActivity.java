package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExperienceListActivity extends ActionBarActivity
{
    ExpandableListView experienceExpandableListView;
    private ExpandableExperienceAdapter exAdapter;
    private List<ExperienceItem> listDataHeader;
    private HashMap<ExperienceItem, List<ExperienceDetails>> listDataChild;
    private List<ExperienceDetails> ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_list);

        experienceExpandableListView = (ExpandableListView)findViewById(R.id.experienceExpandableListView);
        prepareListData();
        exAdapter = new ExpandableExperienceAdapter(this, listDataHeader, listDataChild);
        experienceExpandableListView.setAdapter(exAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        ratings = new ArrayList<ExperienceDetails>();

        // Adding child data
        for(int i = 0; i < 2; i++)
        {
            ExperienceItem item = new ExperienceItem("Arrogas", 1, 1, i);
            listDataHeader.add(item);
        }

        // Adding child data, loop through ratings
        ExperienceDetails rating = new ExperienceDetails("Brandon", "True", "Delta", "I loved it", 5);
        ratings.add(rating);


        listDataChild.put(listDataHeader.get(0), ratings); // Header, Child data
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
