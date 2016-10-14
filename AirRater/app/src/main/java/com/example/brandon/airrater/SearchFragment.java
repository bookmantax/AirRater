package com.example.brandon.airrater;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.brandon.airrater.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/4/2016.
 */

public class SearchFragment extends android.support.v4.app.Fragment
{
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String location, city, country;
    private Button foodButton, drinksButton, entertainmentButton;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation;
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
                mActivity.getFragmentManager().findFragmentById(R.id.searchCityEditText);
        if (autocompleteFragmentLocation != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentLocation).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = mActivity.getSharedPreferences("UserPreferences", 0);
        editor = settings.edit();
        location = settings.getString("Location", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_search_experience, container, false);

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.searchCityEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setText(settings.getString("Location", ""));
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

        foodButton = (Button)rootView.findViewById(R.id.foodButton);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit(1);
            }
        });
        drinksButton = (Button)rootView.findViewById(R.id.drinksButton);
        drinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit(2);
            }
        });
        entertainmentButton = (Button)rootView.findViewById(R.id.entertainmentButton);
        entertainmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit(3);
            }
        });

        return rootView;
    }

    public void Submit(int typeId)
    {
        if(RequiredInformationProvided(location, typeId)) {
            editor.putString("Location", location);
            editor.putInt("TypeId", typeId);
            editor.commit();
            ((MainActivity)getActivity()).SetStartUp(true);
            ExperienceListFragment experienceListFragment = new ExperienceListFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, experienceListFragment, "ExperienceList").commit();
        }
        else
        {
            Context context = getContext();
            CharSequence text = "Please make sure at least Location and Business Type have values.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private boolean RequiredInformationProvided(String location, int typeId)
    {
        if(location != null && typeId != 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
