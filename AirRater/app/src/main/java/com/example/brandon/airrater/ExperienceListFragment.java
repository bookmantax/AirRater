package com.example.brandon.airrater;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Brandon on 10/5/2016.
 */

public class ExperienceListFragment extends android.support.v4.app.Fragment
{

    ExpandableListView experienceExpandableListView;
    private ExpandableExperienceAdapter exAdapter;
    private List<ExperienceItem> listDataHeader;
    private HashMap<ExperienceItem, List<ExperienceDetails>> listDataChild;
    private List<ExperienceDetails> ratings;
    private ExperienceItem expItem;
    private ExperienceDetails expDetails;
    SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getContext().getSharedPreferences("UserPreferences", 0);
        PrepareListData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_experience_list, container, false);

        experienceExpandableListView = (ExpandableListView)getActivity().findViewById(R.id.experienceExpandableListView);
        exAdapter = new ExpandableExperienceAdapter(getContext(), listDataHeader, listDataChild);
        experienceExpandableListView.setAdapter(exAdapter);

        return rootView;
    }

    private void PrepareListData()
    {
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
                        Context context = getContext();
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

}
