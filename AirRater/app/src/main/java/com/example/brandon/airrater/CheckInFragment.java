package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/4/2016.
 */

public class CheckInFragment extends android.support.v4.app.Fragment
{
    private ProgressBar checkinProgressBar;
    private TextView checkinResponseTextView;
    private ListView checkinUsersListView;
    private String location, businessName, city, country;
    private boolean checkedIn = false;
    private ArrayList<UserItem> items = new ArrayList<>();
    private UserAdapter adapter;
    private Button checkinCheckinButton;
    private double latitude, longitude;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation, autocompleteFragmentBusiness;
    private Activity mActivity;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LatLng latLng;

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
                mActivity.getFragmentManager().findFragmentById(R.id.checkinLocationEditText);
        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.checkBusinessNameEditText);
        if (autocompleteFragmentLocation != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentLocation).commit();
        if(autocompleteFragmentBusiness != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentBusiness).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getActivity().getSharedPreferences("UserPreferences", 0);
        editor = settings.edit();
        location = settings.getString("Location", "");
        businessName = settings.getString("Business", "");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_checkin, container, false);

        location = settings.getString("Location", "");
        businessName = settings.getString("Business", "");
        checkinResponseTextView = (TextView) rootView.findViewById(R.id.checkinResponseTextView);
        checkinUsersListView = (ListView) rootView.findViewById(R.id.checkinUsersListView);
        checkinProgressBar = (ProgressBar)rootView.findViewById(R.id.checkinProgressBar);
        if (location != "" && businessName != "")
        {
            GetUsers();
        }

        adapter = new UserAdapter(getContext(), R.layout.user_item, items);
        checkinUsersListView.setAdapter(adapter);
        checkinResponseTextView.setText("");

        checkinCheckinButton = (Button) rootView.findViewById(R.id.checkinCheckinButton);
        checkinCheckinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkin();
            }
        });
        GetUserCheckedIn();

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.checkinLocationEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilter);
        autocompleteFragmentLocation.setText(location);

        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                geocoder = new Geocoder(rootView.getContext(), Locale.getDefault());
                latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
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
                    autocompleteFragmentBusiness.setText("");
                    autocompleteFragmentBusiness.setBoundsBias(new LatLngBounds(
                            new LatLng(latitude - 0.5, longitude - 0.5),
                            new LatLng(latitude + 0.5, longitude + 0.5)));
                }
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

        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.checkBusinessNameEditText);

        AutocompleteFilter typeFilterBusiness = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        autocompleteFragmentBusiness.setFilter(typeFilterBusiness);
        autocompleteFragmentBusiness.setText(businessName);

        autocompleteFragmentBusiness.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                businessName = String.valueOf(place.getName());
                editor.putString("Business", businessName);
                editor.commit();
                GetUsers();
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

    private void GetUserCheckedIn()
    {
        checkinProgressBar.setVisibility(View.VISIBLE);
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("userId", settings.getInt("UserId", 0));
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/GetUserCheckedIn.ashx",
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes)
                {
                    super.onPostExecute(result, notes);
                    if(result != null) {
                        if (result.equalsIgnoreCase("CheckedIn")) {
                            checkedIn = true;
                            checkinCheckinButton.setText("CheckedIn");
                            checkinResponseTextView.setText("You are currently checked in to a business.");
                            checkinResponseTextView.setTextColor(Color.GREEN);
                            checkinProgressBar.setVisibility(View.GONE);
                        }
                        else{
                            checkinProgressBar.setVisibility(View.GONE);
                        }
                    }
                    else{
                        checkinProgressBar.setVisibility(View.GONE);
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    private void GetUsers()
    {
        checkinProgressBar.setVisibility(View.VISIBLE);
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            params.put("data", object.toString());
            new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx?location=" + location.replace(" ", "%20") +
                    (businessName != null && businessName != "" ? "&businessName=" + businessName.replace(" ", "%20") : ""),
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes) {
                    //responder.uploadFinished(result != null);
                    super.onPostExecute(result, notes);
                    String s = result;
                    if(result != null) {
                        if (s.equalsIgnoreCase("Missing Information")) {
                            checkinProgressBar.setVisibility(View.GONE);
                        } else if (s.equalsIgnoreCase("User not found") || s.equalsIgnoreCase("No Users Checked In")) {
                            items.clear();
                            items.add(new UserItem("No Users Found", "", ""));
                            adapter.notifyDataSetChanged();
                            checkinProgressBar.setVisibility(View.GONE);
                        } else if(!result.equalsIgnoreCase("")){
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                items.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    items.add(new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                            oneObject.getString("businessName"), oneObject.getString("airline")));
                                }
                                adapter.notifyDataSetChanged();
                                checkinProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            items.clear();
                            adapter.notifyDataSetChanged();
                            checkinProgressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void Checkin()
    {
        checkinProgressBar.setVisibility(View.VISIBLE);
        if(checkedIn || (location != "" && businessName != ""))
        {
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("userId", settings.getInt("UserId", 0));
                object.put("location", location);
                if(checkedIn) {
                    object.put("businessName", "");
                }
                else {
                    object.put("businessName", businessName);
                }
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/CreateCheckin.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes)
                    {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result != null) {
                            if (result.equalsIgnoreCase("Missing Information")) {
                                checkinResponseTextView.setText("Something went wrong, please try again.");
                                checkinResponseTextView.setTextColor(Color.RED);
                                checkinProgressBar.setVisibility(View.GONE);
                            } else if(result.equalsIgnoreCase("Success")) {
                                checkinResponseTextView.setText("CheckIn created successfully.");
                                checkinResponseTextView.setTextColor(Color.GREEN);
                                checkinCheckinButton.setText("Checked In");
                                checkinProgressBar.setVisibility(View.GONE);
                                GetUsers();
                            } else if(result.equalsIgnoreCase("CheckedOut")) {
                                checkedIn = false;
                                checkinResponseTextView.setText("Checked out.");
                                checkinResponseTextView.setTextColor(Color.GREEN);
                                checkinCheckinButton.setText("CheckIn");
                                checkinProgressBar.setVisibility(View.GONE);
                                GetUsers();
                            } else {
                                checkinResponseTextView.setText("Something went wrong, please try again.");
                                checkinResponseTextView.setTextColor(Color.RED);
                                checkinProgressBar.setVisibility(View.GONE);
                            }
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
            checkinProgressBar.setVisibility(View.GONE);
        }
    }
}
