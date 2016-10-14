package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

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
    private TextView experienceListCityTextView;
    private ProgressBar experienceListProgressBar;
    private ExpandableExperienceAdapter exAdapter;
    private List<ExperienceItem> listDataHeader;
    private HashMap<ExperienceItem, List<ExperienceDetails>> listDataChild;
    private List<ExperienceDetails> ratings;
    private ExperienceItem expItem;
    private ExperienceDetails expDetails;
    SharedPreferences settings;
    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getContext().getSharedPreferences("UserPreferences", 0);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        exAdapter = new ExpandableExperienceAdapter(mActivity, listDataHeader, listDataChild);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_experience_list, container, false);

        experienceListProgressBar = (ProgressBar)rootView.findViewById(R.id.experienceListProgressBar);
        experienceListCityTextView = (TextView)rootView.findViewById(R.id.experienceListCityTextView);
        experienceListCityTextView.setText(settings.getString("Location", ""));
        experienceExpandableListView = (ExpandableListView)rootView.findViewById(R.id.experienceExpandableListView);
        experienceExpandableListView.setAdapter(exAdapter);
        listDataHeader.clear();
        listDataChild.clear();
        exAdapter.notifyDataSetChanged();
        PrepareListData();
        return rootView;
    }

    private void PrepareListData()
    {
        experienceListProgressBar.setVisibility(View.VISIBLE);
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
                    if (s != null && s.equalsIgnoreCase("Missing Information")) {
                        Context context = getContext();
                        CharSequence text = "Something went wrong, please try again.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        experienceListProgressBar.setVisibility(View.GONE);
                    }
                    else if(s != null && s.equalsIgnoreCase("No Ratings"))
                    {
                        //TODO call google API
                        try {
                            params = new RequestParams();
                            JSONObject object = new JSONObject();
                            String latLong = settings.getFloat("Latitude", 0) + "," + settings.getFloat("Longitude", 0);
                            String type = "";
                            switch (settings.getInt("TypeId", 0)) {
                                case 1:
                                    type = "restaurant";
                                    break;
                                case 2:
                                    type = "bar";
                                    break;
                                case 3:
                                    type = "movie_theater";
                                    break;
                            }
                            params.put("data", object.toString());

                            new Thread(new AsyncDownload("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLong + "&radius=500&type=" +
                                    type + "&key=AIzaSyDLrnBfy2K6qOfbey7F6NOGmo7Hq18PcYc",
                                    params, false, null) {
                                @Override
                                protected void onPostExecute(String result, Object notes) {

                                    //responder.uploadFinished(result != null);
                                    super.onPostExecute(result, notes);
                                    if(result != null) {
                                        try {
                                            JSONObject oneObject = new JSONObject(result);
                                            JSONArray jsonArray = oneObject.getJSONArray("results");
                                            int topFiveOrArrayLength;
                                            if (jsonArray.length() > 5) {
                                                topFiveOrArrayLength = 5;
                                            } else {
                                                topFiveOrArrayLength = jsonArray.length();
                                            }
                                            for (int i = 0; i < topFiveOrArrayLength; i++) {
                                                oneObject = jsonArray.getJSONObject(i);
                                                expItem = new ExperienceItem(oneObject.getString("name"),
                                                        0,
                                                        oneObject.isNull("rating") ? 0.0 : oneObject.getDouble("rating"), i);
                                                ratings = new ArrayList<ExperienceDetails>();
                                                expDetails = new ExperienceDetails("Be the first to write a review!",
                                                        "", "", "", 0.0);
                                                ratings.add(expDetails);
                                                listDataHeader.add(expItem);
                                                listDataChild.put(listDataHeader.get(i), ratings);
                                                experienceListProgressBar.setVisibility(View.GONE);
                                            }
                                            exAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    } else if(result != null)
                    {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject oneObject, detailsObject;
                            try {
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    oneObject = jsonArray.getJSONObject(i);
                                    expItem = new ExperienceItem(oneObject.getString("name"),
                                            oneObject.getInt("numActiveCheckins"), oneObject.getDouble("stars"), i);
                                    ratings = new ArrayList<ExperienceDetails>();
                                    JSONArray jsonRatings = oneObject.getJSONArray("ratings");
                                    for(int j = 0; j < jsonRatings.length(); j++)
                                    {
                                        detailsObject = jsonRatings.getJSONObject(j);
                                        expDetails = new ExperienceDetails(detailsObject.getString("employeeFirstName"),
                                                detailsObject.getString("employeeLastName"), detailsObject.getString("employeeAirline"),
                                                detailsObject.isNull("comments") ? "" : detailsObject.getString("comments"),
                                                detailsObject.isNull("stars") ? 0 : detailsObject.getDouble("stars"));
                                        ratings.add(expDetails);
                                    }
                                    listDataHeader.add(expItem);
                                    listDataChild.put(listDataHeader.get(i), ratings);
                                }
                                exAdapter.notifyDataSetChanged();
                                experienceListProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                // Oops
                                Log.w("Error", e);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        CharSequence text = "Something went wrong, please try again.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(mActivity, text, duration);
                        toast.show();
                        experienceListProgressBar.setVisibility(View.GONE);
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

}
