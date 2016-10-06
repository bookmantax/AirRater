package com.example.brandon.airrater;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONObject;

/**
 * Created by Brandon on 10/4/2016.
 */

public class RatingFragment extends android.support.v4.app.Fragment
{
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String restaurantSubTypes[] = {"Grill", "Seafood", "Japanese", "Mexican", "Italian", "Buffet"};
    String drinksSubTypes[] = {"Sports Bar", "Pub", "Fancy Bar"};
    String entertainmentSubTypes[] = {"Movies", "Plays/Broadway", "Live Music", "Circus"};
    String location, businessName, comments;
    int typeId, subTypeId;
    EditText commentsEditText;
    TextView rateResponseTextView;
    Spinner typeSpinner, subTypeSpinner;
    RatingBar starsRatingBar;
    ArrayAdapter<String> spinnerTypeAdapter;
    ArrayAdapter<String> spinnerSubTypeAdapter;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation, autocompleteFragmentBusiness;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getContext().getSharedPreferences("UserPreferences", 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_rate_experience, container, false);
        location = settings.getString("Location", "");

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.searchCityEditText);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilter);
        autocompleteFragmentLocation.setText(location);

        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.nameEditText);

        AutocompleteFilter typeFilterBusiness = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        autocompleteFragmentLocation.setFilter(typeFilterBusiness);

        rateResponseTextView = (TextView)getActivity().findViewById(R.id.rateResponseTextView);
        commentsEditText = (EditText)getActivity().findViewById(R.id.commentsEditText);
        starsRatingBar = (RatingBar)getActivity().findViewById(R.id.starsRatingBar);

        typeSpinner = (Spinner)getActivity().findViewById(R.id.typeSpinner);
        spinnerTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, types);
        spinnerTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerTypeAdapter);

        subTypeSpinner = (Spinner)getActivity().findViewById(R.id.subTypeSpinner);

        //When type is selected, populate subType
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, restaurantSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 1;
                }
                else if(position == 1)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, drinksSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 2;
                }
                else if(position == 2)
                {
                    spinnerSubTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, entertainmentSubTypes);
                    spinnerSubTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    subTypeSpinner.setAdapter(spinnerSubTypeAdapter);
                    typeId = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        subTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        businessName = String.valueOf(autocompleteFragmentBusiness.getText(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT));
        comments = String.valueOf(commentsEditText.getText());

        editor = settings.edit();
        if(location != null && location != "" && location != settings.getString("Location", ""))
        {
            editor.putString("Location", location);
        }
        if(RequiredFieldsHaveValue(settings.getInt("UserId", 0), businessName, typeId, location))
        {
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("UserId", settings.getString("UserId", ""));
                object.put("location", location);
                object.put("BusinessName", businessName);
                object.put("Stars", starsRatingBar.getNumStars());
                object.put("Comments", comments);
                object.put("businessTypeId", typeId);
                object.put("businessSubTypeId", subTypeId);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/CreateRating.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {

                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        String s = result;
                        if(s.equalsIgnoreCase("Missing Information"))
                        {
                            rateResponseTextView.setText("Something went wrong please try again.");
                            rateResponseTextView.setTextColor(Color.RED);
                        }
                        else
                        {
                            rateResponseTextView.setText("Saved Successfully.");
                            rateResponseTextView.setTextColor(Color.GREEN);
                            autocompleteFragmentLocation.setText("");
                            typeSpinner.setSelection(0);
                            subTypeSpinner.setSelection(0);
                            autocompleteFragmentBusiness.setText("");
                            starsRatingBar.setNumStars(0);
                            commentsEditText.setText("");
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            rateResponseTextView.setText("Please make sure at least business name, location, and type have values.");
            rateResponseTextView.setTextColor(Color.RED);
        }
    }

    private boolean RequiredFieldsHaveValue(int userId, String businessName, int typeId, String location)
    {
        if(userId != 0 && businessName != null && typeId != 0 && location != null)
        {
            return true;
        }
        return false;
    }
}
