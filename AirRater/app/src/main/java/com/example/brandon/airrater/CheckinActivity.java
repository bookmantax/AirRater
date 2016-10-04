package com.example.brandon.airrater;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
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
 * Created by Brandon on 9/28/2016.
 */
public class CheckinActivity extends AppCompatActivity
{
    private TextView checkinResponseTextView;
    private ListView checkinUsersListView;
    private String location, businessName;
    private UserItem items[];
    private UserAdapter adapter;
    SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation, autocompleteFragmentBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.checkinLocationEditText);

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
                getFragmentManager().findFragmentById(R.id.checkBusinessNameEditText);

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

        checkinResponseTextView = (TextView) findViewById(R.id.checkinResponseTextView);
        checkinUsersListView = (ListView) findViewById(R.id.checkinUsersListView);

        location = settings.getString("Location", "");
        autocompleteFragmentLocation.setText(location);
        businessName = settings.getString("Business", "");
        autocompleteFragmentBusiness.setText(businessName);

        if (location != null)
        {
            GetUsers();
        }
        else
        {
            checkinResponseTextView.setText("Please make sure at least Location has a value.");
            checkinResponseTextView.setTextColor(Color.RED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
                        adapter = new UserAdapter(CheckinActivity.this, R.layout.user_item, items);
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
                            adapter = new UserAdapter(CheckinActivity.this, R.layout.user_item, items);
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
