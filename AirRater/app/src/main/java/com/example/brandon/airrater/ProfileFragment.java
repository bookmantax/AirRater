package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import org.json.JSONObject;

/**
 * Created by Brandon on 10/4/2016.
 */

public class ProfileFragment extends android.support.v4.app.Fragment
{
    private EditText profileFirstNameEditText, profileLastNameEditText,
            profileEmailEditText;
    private ProgressBar profileProgressBar;
    private AutoCompleteTextView profileAirlineEditText;
    private ArrayAdapter<String> adapter;
    private TextView profileResponseTextView;
    private Button profileSubmitButton;
    private String firstName, lastName, airline, emailAddress;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String storedFirstName;
    private String storedLastName;
    private String storedAirline;
    private String storedEmail;
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
        settings = mActivity.getSharedPreferences("UserPreferences", 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);

        storedFirstName = settings.getString("FirstName", "");
        storedLastName = settings.getString("Lastname", "");
        storedAirline = settings.getString("Airline", "");
        storedEmail = settings.getString("EmailAddress", "");

        profileProgressBar = (ProgressBar)rootView.findViewById(R.id.profileProgressBar);
        profileResponseTextView = (TextView)rootView.findViewById(R.id.profileResponseTextView);

        profileFirstNameEditText = (EditText)rootView.findViewById(R.id.profileFirstNameEditText);
        profileFirstNameEditText.setText(storedFirstName);

        profileLastNameEditText = (EditText)rootView.findViewById(R.id.profileLastNameEditText);
        profileLastNameEditText.setText(storedLastName);

        String[] airlines = getResources().getStringArray(R.array.Airlines);
        adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, airlines);
        profileAirlineEditText = (AutoCompleteTextView) rootView.findViewById(R.id.profileAirlineEditText);
        profileAirlineEditText.setAdapter(adapter);
        profileAirlineEditText.setThreshold(1);
        profileAirlineEditText.setText(storedAirline);

        profileEmailEditText = (EditText)rootView.findViewById(R.id.profileEmailEditText);
        profileEmailEditText.setText(storedEmail);

        profileSubmitButton = (Button)rootView.findViewById(R.id.profileSubmitButton);
        profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });

        return rootView;
    }

    public void Submit()
    {
        profileProgressBar.setVisibility(View.VISIBLE);
        profileSubmitButton.setEnabled(false);
        editor = settings.edit();
        firstName = String.valueOf(profileFirstNameEditText.getText());
        lastName = String.valueOf(profileLastNameEditText.getText());
        airline = String.valueOf(profileAirlineEditText.getText());
        emailAddress = String.valueOf(profileEmailEditText.getText());

        if(IsInfoChanged(firstName, lastName, airline, emailAddress))
        {
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("userId", settings.getInt("UserId", 0));
                object.put("firstName", firstName);
                object.put("lastName", lastName);
                object.put("airline", airline);
                object.put("emailAddress", emailAddress);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/EditProfile.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        String s = result;
                        if(s != null)
                        {
                            if (s.equalsIgnoreCase("No Change")) {
                                profileResponseTextView.setText("No changes were made.");
                                profileResponseTextView.setTextColor(Color.RED);
                                profileProgressBar.setVisibility(View.GONE);
                                profileSubmitButton.setEnabled(true);
                            } else if (s.equalsIgnoreCase("Missing Information")) {
                                profileResponseTextView.setText("Something went wrong please try again.");
                                profileResponseTextView.setTextColor(Color.RED);
                                profileProgressBar.setVisibility(View.GONE);
                                profileSubmitButton.setEnabled(true);
                            } else if (s.equalsIgnoreCase("Success"))
                            {
                                profileResponseTextView.setText("Saved.");
                                profileResponseTextView.setTextColor(Color.GREEN);
                                profileProgressBar.setVisibility(View.GONE);
                                profileSubmitButton.setEnabled(true);
                            }
                        }
                        else
                        {
                            Context context = getContext();
                            CharSequence text = "The connection timed out, please try again.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            profileProgressBar.setVisibility(View.GONE);
                            profileSubmitButton.setEnabled(true);
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            profileResponseTextView.setText("No changes were made.");
            profileResponseTextView.setTextColor(Color.RED);
            profileProgressBar.setVisibility(View.GONE);
            profileSubmitButton.setEnabled(true);
        }
    }

    private boolean IsInfoChanged(String fName, String lName, String airline, String emailAddress)
    {
        boolean changed = false;
        if(fName != null && !fName.equalsIgnoreCase(storedFirstName))
        {
            editor.putString("FirstName", fName);
            changed = true;
        }
        if(lName != null && !lName.equalsIgnoreCase(storedLastName))
        {
            editor.putString("Lastname", lName);
            changed = true;
        }
        if(airline != null && !airline.equalsIgnoreCase(storedAirline))
        {
            editor.putString("Airline", airline);
            changed = true;
        }
        if(emailAddress != null && !emailAddress.equalsIgnoreCase(storedEmail))
        {
            editor.putString("EmailAddress", emailAddress);
            changed = true;
        }
        editor.commit();
        return changed;
    }
}
