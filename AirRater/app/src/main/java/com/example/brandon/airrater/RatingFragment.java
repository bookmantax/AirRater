package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Brandon on 10/4/2016.
 */

public class RatingFragment extends android.support.v4.app.Fragment
{
    String types[] = {"Restaurant", "Drinks", "Entertainment"};
    String location, businessName, comments, city, country;
    int typeId;
    private Button submit;
    EditText commentsEditText;
    TextView rateResponseTextView;
    Spinner typeSpinner;
    RatingBar starsRatingBar;
    ArrayAdapter<String> spinnerTypeAdapter;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    PlaceAutocompleteFragment autocompleteFragmentLocation, autocompleteFragmentBusiness;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LatLng latLng;
    private Activity mActivity;

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
                mActivity.getFragmentManager().findFragmentById(R.id.ratingCityEditText);
        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.ratingNameEditText);
        if (autocompleteFragmentLocation != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentLocation).commit();
        if(autocompleteFragmentBusiness != null)
            mActivity.getFragmentManager().beginTransaction().remove(autocompleteFragmentBusiness).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = mActivity.getSharedPreferences("UserPreferences", 0);
        typeId = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_rate_experience, container, false);
        location = settings.getString("Location", "");

        autocompleteFragmentLocation = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.ratingCityEditText);

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

        autocompleteFragmentBusiness  = (PlaceAutocompleteFragment)
                mActivity.getFragmentManager().findFragmentById(R.id.ratingNameEditText);

        AutocompleteFilter typeFilterBusiness = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        autocompleteFragmentBusiness.setFilter(typeFilterBusiness);
        autocompleteFragmentBusiness.setText(businessName);
        autocompleteFragmentBusiness.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                businessName = String.valueOf(place.getName());
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

        submit = (Button)rootView.findViewById(R.id.ratingSubmitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });
        rateResponseTextView = (TextView)rootView.findViewById(R.id.rateResponseTextView);
        commentsEditText = (EditText)rootView.findViewById(R.id.commentsEditText);
        starsRatingBar = (RatingBar)rootView.findViewById(R.id.starsRatingBar);

        typeSpinner = (Spinner)rootView.findViewById(R.id.typeSpinner);
        spinnerTypeAdapter = new ArrayAdapter<String>(mActivity, R.layout.support_simple_spinner_dropdown_item, types);
        spinnerTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerTypeAdapter);

        //When type is selected, populate subType
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                typeId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return rootView;
    }

    public void Submit()
    {
        comments = String.valueOf(commentsEditText.getText());
        if(RequiredFieldsHaveValue(settings.getInt("UserId", 0), businessName, typeId, location))
        {
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("UserId", settings.getInt("UserId", 0));
                object.put("location", location);
                object.put("BusinessName", businessName);
                object.put("Stars", starsRatingBar.getNumStars());
                object.put("Comments", comments);
                object.put("businessTypeId", typeId);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/CreateRating.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {

                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        String s = result;
                        if(result != null && s.equalsIgnoreCase("Missing Information"))
                        {
                            rateResponseTextView.setText("Something went wrong please try again.");
                            rateResponseTextView.setTextColor(Color.RED);
                        }
                        else if(result != null)
                        {
                            rateResponseTextView.setText("Saved Successfully.");
                            rateResponseTextView.setTextColor(Color.GREEN);
                            autocompleteFragmentLocation.setText("");
                            typeSpinner.setSelection(0);
                            autocompleteFragmentBusiness.setText("");
                            starsRatingBar.setNumStars(0);
                            commentsEditText.setText("");
                        }
                        else
                        {
                            rateResponseTextView.setText("NULL.");
                            rateResponseTextView.setTextColor(Color.RED);
                        }
                    }
                }).start();
            } catch (Exception e) {
                Log.w("Error", e);

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
