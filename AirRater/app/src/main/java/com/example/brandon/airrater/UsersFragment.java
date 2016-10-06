package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/4/2016.
 */

public class UsersFragment extends android.support.v4.app.Fragment
{
    PlaceAutocompleteFragment autocompleteFragmentLocation;
    private TextView findUsersResponseTextView;
    private ListView findUsersListView;
    private String location, city, country;
    private UserItem items[];
    private UserAdapter adapter;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Activity mActivity;
    private LatLng latLng;
    Geocoder geocoder;
    List<Address> addresses;

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
        location = settings.getString("Location", "");
        SearchOnStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_find_users, container, false);

        autocompleteFragmentLocation = (PlaceAutocompleteFragment) mActivity.getFragmentManager().findFragmentById(R.id.findUsersLocationEditText);
        autocompleteFragmentLocation.setText(settings.getString("Location", ""));
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                geocoder = new Geocoder(rootView.getContext(), Locale.getDefault());
                latLng = place.getLatLng();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (Exception e){}
                if(addresses != null && addresses.size() > 0)
                {
                    city = addresses.get(0).getAddressLine(1);
                    country = addresses.get(0).getAddressLine(2);
                    if(city != null && country != null)
                    {
                        location = city + ", " + country;
                        editor.putString("Location", location);
                    }
                }
                SearchSetAdapter();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        findUsersListView = (ListView)rootView.findViewById(R.id.findUsersListView);
        findUsersResponseTextView = (TextView)rootView.findViewById(R.id.findUsersResponseTextView);

        if(items == null)
        {
            SearchSetAdapter();
        }
        else
        {
            adapter = new UserAdapter(getContext(), R.layout.user_item, items);
            findUsersListView.setAdapter(adapter);
            findUsersResponseTextView.setText("");
        }
        return rootView;
    }

    private void SearchSetAdapter()
    {
        if(location != null && location != "")
        {
            if(location != settings.getString("Location", ""))
            {
                editor = settings.edit();
                editor.putString("Location", location);
            }
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("location", location);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result.equalsIgnoreCase("Missing Information"))
                        {
                            findUsersResponseTextView.setText("Something went wrong please try again.");
                            findUsersResponseTextView.setTextColor(Color.RED);
                        }
                        else if(result.equalsIgnoreCase("User not found"))
                        {
                            items = new UserItem[1];
                            items[0] = new UserItem("No Users Found", "", "");
                            adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                            findUsersListView.setAdapter(adapter);
                            findUsersResponseTextView.setText("");
                        }
                        else
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                items = new UserItem[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    items[i] = new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                            oneObject.getString("businessName"), oneObject.getString("airline"));
                                }
                                adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                                findUsersListView.setAdapter(adapter);
                                findUsersResponseTextView.setText("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            findUsersResponseTextView.setText("Please make sure Location has a value.");
            findUsersResponseTextView.setTextColor(Color.RED);
        }
    }

    public void SearchOnStart()
    {
        if(location != null && location != "")
        {
            if(location != settings.getString("Location", ""))
            {
                editor = settings.edit();
                editor.putString("Location", location);
            }
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("location", location);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result.equalsIgnoreCase("Missing Information"))
                        {

                        }
                        else if(result.equalsIgnoreCase("User not found"))
                        {
                            items = new UserItem[1];
                            items[0] = new UserItem("No Users Found", "", "");
                        }
                        else
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                items = new UserItem[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    items[i] = new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                            oneObject.getString("businessName"), oneObject.getString("airline"));
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
        else
        {

        }
    }
}
