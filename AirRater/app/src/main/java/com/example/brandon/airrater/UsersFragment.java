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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/4/2016.
 */

public class UsersFragment extends android.support.v4.app.Fragment
{
    PlaceAutocompleteFragment autocompleteFragmentLocation;
    private ProgressBar findUsersProgressBar;
    private TextView findUsersResponseTextView;
    private ListView findUsersListView;
    private String location, city, country;
    private ArrayList<UserItem> items = new ArrayList<>();
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
    public void onDestroyView() {
        super.onDestroyView();
        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.findUsersLocationEditText);
        if (autocompleteFragmentLocation != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentLocation).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = mActivity.getSharedPreferences("UserPreferences", 0);
        editor = settings.edit();
        location = settings.getString("Location", "");
        SearchOnStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_find_users, container, false);

        findUsersProgressBar = (ProgressBar)rootView.findViewById(R.id.findUsersProgressBar);
        autocompleteFragmentLocation = (PlaceAutocompleteFragment) mActivity.getFragmentManager().findFragmentById(R.id.findUsersLocationEditText);
        autocompleteFragmentLocation.setText(settings.getString("Location", ""));
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                geocoder = new Geocoder(rootView.getContext(), Locale.getDefault());
                latLng = place.getLatLng();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (Exception e){}
                if (addresses != null && addresses.size() > 0)
                {
                    city = addresses.get(0).getLocality();
                    if(addresses.get(0).getCountryName().equalsIgnoreCase("United States"))
                    {
                        country = addresses.get(0).getAdminArea();
                    }
                    else
                    {
                        country = addresses.get(0).getCountryName();
                    }
                    editor.putString("Location", city + ", " + country);
                    editor.commit();
                    location = city + ", " + country;
                    autocompleteFragmentLocation.setText(location);
                }
                SearchSetAdapter();
            }

            @Override
            public void onError(Status status) {
                Context context = getContext();
                CharSequence text = "Something went wrong, please try again.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        findUsersListView = (ListView)rootView.findViewById(R.id.findUsersListView);
        findUsersResponseTextView = (TextView)rootView.findViewById(R.id.findUsersResponseTextView);

        if(items == null || items.size() == 0)
        {
            if(settings.getString("Location", "") != "")
            {
                SearchSetAdapter();
            }
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
        findUsersProgressBar.setVisibility(View.VISIBLE);
        if(location != null && location != "")
        {
            if(location != settings.getString("Location", ""))
            {
                editor.putString("Location", location);
                editor.commit();
            }
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx?location=" + location.replace(" ", "%20"),
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result != null)
                        {
                            if (result.equalsIgnoreCase("Missing Information"))
                            {
                                findUsersResponseTextView.setText("Something went wrong please try again.");
                                findUsersResponseTextView.setTextColor(Color.RED);
                                findUsersProgressBar.setVisibility(View.GONE);
                            }
                            else if (result.equalsIgnoreCase("User not found") || result.equalsIgnoreCase("No Users Checked In"))
                            {
                                items.clear();
                                items.add(new UserItem("No Users Found", "", ""));
                                adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                                findUsersListView.setAdapter(adapter);
                                findUsersResponseTextView.setText("");
                                findUsersProgressBar.setVisibility(View.GONE);
                            }
                            else
                            {
                                try {
                                    JSONArray jsonArray = new JSONArray(result);
                                    items.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        items.add(new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                                oneObject.getString("businessName"), oneObject.getString("airline")));
                                    }
                                    adapter = new UserAdapter(getContext(), R.layout.user_item, items);
                                    findUsersListView.setAdapter(adapter);
                                    findUsersResponseTextView.setText("");
                                    findUsersProgressBar.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            Context context = getContext();
                            CharSequence text = "The connection timed out, please try again.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            findUsersProgressBar.setVisibility(View.GONE);
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
            findUsersProgressBar.setVisibility(View.GONE);
        }
    }

    public void SearchOnStart()
    {
        if(location != null && location != "")
        {
            if(location != settings.getString("Location", ""))
            {
                editor.putString("Location", location);
                editor.commit();
            }
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("location", location);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx?location=" + location.replace(" ", "%20"),
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result != null && result.equalsIgnoreCase("Missing Information"))
                        {
                        }
                        else if(result != null && result.equalsIgnoreCase("User not found") || result.equalsIgnoreCase("No Users Checked In"))
                        {
                            items.clear();
                            items.add(new UserItem("No Users Found", "", ""));
                        }
                        else if(result != null)
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                items.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    items.add(new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                            oneObject.getString("businessName"), oneObject.getString("airline")));
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
