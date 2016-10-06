package com.example.brandon.airrater;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 10/4/2016.
 */

public class CheckInFragment extends android.support.v4.app.Fragment
{

    private TextView checkinResponseTextView;
    private ListView checkinUsersListView;
    private String location, businessName;
    private UserItem items[];
    private UserAdapter adapter;
    private Button checkinCheckinButton;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation, autocompleteFragmentBusiness;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getContext().getSharedPreferences("UserPreferences", 0);
        location = settings.getString("Location", "");
        businessName = settings.getString("Business", "");

        if (location != null)
        {
            GetUsers();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_checkin, container, false);

        checkinResponseTextView = (TextView) getActivity().findViewById(R.id.checkinResponseTextView);
        checkinUsersListView = (ListView) getActivity().findViewById(R.id.checkinUsersListView);
        checkinCheckinButton = (Button) getActivity().findViewById(R.id.checkinCheckinButton);

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.checkinLocationEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                GetUsers();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.checkBusinessNameEditText);

        AutocompleteFilter typeFilterBusiness = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilterBusiness);

        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                GetUsers();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });
        if(location != null)
        {
            autocompleteFragmentLocation.setText(location);
        }
        if(businessName != null)
        {
            autocompleteFragmentBusiness.setText(businessName);
        }
        return rootView;
    }

    private void GetUsers()
    {
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("location", location);
            object.put("businessName", businessName);
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx",
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes) {
                    //responder.uploadFinished(result != null);
                    super.onPostExecute(result, notes);
                    String s = result;
                    if(s.equalsIgnoreCase("Missing Information"))
                    {
                        checkinResponseTextView.setText("Something went wrong please try again.");
                        checkinResponseTextView.setTextColor(Color.RED);
                    }
                    else if(s.equalsIgnoreCase("User not found"))
                    {
                        items = new UserItem[1];
                        items[0] = new UserItem("No Users Found", "", "");
                        adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                        checkinUsersListView.setAdapter(adapter);
                        checkinResponseTextView.setText("");
                    }
                    else
                    {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(result);
                            items = new UserItem[jsonArray.length()];
                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject oneObject = jsonArray.getJSONObject(i);
                                items[i] = new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                        "", oneObject.getString("airline"));
                            }
                            adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                            checkinUsersListView.setAdapter(adapter);
                            checkinResponseTextView.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void Checkin(View view)
    {
        location = String.valueOf(autocompleteFragmentLocation.getText(AutocompleteFilter.TYPE_FILTER_CITIES));
        businessName = String.valueOf(autocompleteFragmentLocation.getText(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT));
        if(location != settings.getString("Location", "") || businessName != settings.getString("Business", ""))
        {
            editor = settings.edit();
            editor.putString("Location", location);
            editor.putString("Business", businessName);
        }

        if(location != null && businessName != null)
        {
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("location", location);
                object.put("businessName", businessName);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/CreateCheckin.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes)
                    {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result.equalsIgnoreCase("Missing Information"))
                        {
                            checkinResponseTextView.setText("Something went wrong, please try again.");
                            checkinResponseTextView.setTextColor(Color.RED);
                        }
                        else
                        {
                            checkinResponseTextView.setText("CheckIn created successfully.");
                            checkinResponseTextView.setTextColor(Color.GREEN);
                            checkinCheckinButton.setText("Checked");
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            checkinResponseTextView.setText("Please make sure all fields have a value.");
            checkinResponseTextView.setTextColor(Color.RED);
        }
    }
}
