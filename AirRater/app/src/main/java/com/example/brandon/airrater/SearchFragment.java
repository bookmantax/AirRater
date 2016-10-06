package com.example.brandon.airrater;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.brandon.airrater.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by Brandon on 10/4/2016.
 */

public class SearchFragment extends android.support.v4.app.Fragment
{
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String restaurantSubTypes[] = {"Grill", "Seafood", "Japanese", "Mexican", "Italian", "Buffet"};
    String drinksSubTypes[] = {"Sports Bar", "Pub", "Fancy Bar"};
    String entertainmentSubTypes[] = {"Movies", "Plays/Broadway", "Live Music", "Circus"};
    String location;
    int typeId, subTypeId;
    Spinner searchTypeSpinner, searchSubTypeSpinner;
    ArrayAdapter<String> spinnerTypeAdapter;
    ArrayAdapter<String> spinnerSubTypeAdapter;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getContext().getSharedPreferences("UserPreferences", 0);
        location = settings.getString("Location", "");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search_experience, container, false);

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.searchCityEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragmentLocation.setFilter(typeFilter);

        autocompleteFragmentLocation.setText(settings.getString("Location", ""));
        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        searchTypeSpinner = (Spinner)getActivity().findViewById(R.id.searchTypeSpinner);
        spinnerTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, types);
        spinnerTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(spinnerTypeAdapter);

        searchSubTypeSpinner = (Spinner)getActivity().findViewById(R.id.searchSubTypeSpinner);

        //When type is selected, populate subType
        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, restaurantSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 1;
                }
                else if(position == 1)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, drinksSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 2;
                }
                else if(position == 2)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, entertainmentSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    searchSubTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        searchSubTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                subTypeId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return rootView;
    }

    public void Submit(View view)
    {
        location = String.valueOf(autocompleteFragmentLocation.getText(AutocompleteFilter.TYPE_FILTER_CITIES));

        if(RequiredInformationProvided(location, typeId)) {
            editor.putString("Location", location);
            editor.putInt("TypeId", typeId);
            editor.putInt("SubTypeId", subTypeId);
            ExperienceListFragment experienceListFragment = new ExperienceListFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.searchLayout, experienceListFragment).commit();
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
