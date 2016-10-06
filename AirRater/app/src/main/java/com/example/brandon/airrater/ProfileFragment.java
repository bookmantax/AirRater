package com.example.brandon.airrater;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Brandon on 10/4/2016.
 */

public class ProfileFragment extends android.support.v4.app.Fragment
{
    private EditText profileFirstNameEditText, profileLastNameEditText, profileAirlineEditText,
            profileEmailEditText;
    private TextView profileResponseTextView;
    private String firstName, lastName, airline, emailAddress;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String storedFirstName;
    private String storedLastName;
    private String storedAirline;
    private String storedEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getContext().getSharedPreferences("UserPreferences", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);

        storedFirstName = settings.getString("FirstName", "");
        storedLastName = settings.getString("LastName", "");
        storedAirline = settings.getString("Airline", "");
        storedEmail = settings.getString("EmailAddress", "");

        profileResponseTextView = (TextView)getActivity().findViewById(R.id.profileResponseTextView);

        profileFirstNameEditText = (EditText)getActivity().findViewById(R.id.profileFirstNameEditText);
        profileFirstNameEditText.setText(storedFirstName);

        profileLastNameEditText = (EditText)getActivity().findViewById(R.id.profileLastNameEditText);
        profileLastNameEditText.setText(storedLastName);

        profileAirlineEditText = (EditText)getActivity().findViewById(R.id.profileAirlineEditText);
        profileAirlineEditText.setText(storedAirline);

        profileEmailEditText = (EditText)getActivity().findViewById(R.id.profileEmailEditText);
        profileEmailEditText.setText(storedEmail);

        return rootView;
    }

    public void Submit(View view)
    {
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
                        if(s.equalsIgnoreCase("No Change"))
                        {
                            profileResponseTextView.setText("No changes were made.");
                            profileResponseTextView.setTextColor(Color.RED);
                        }
                        else if(s.equalsIgnoreCase("Missing Information"))
                        {
                            profileResponseTextView.setText("Something went wrong please try again.");
                            profileResponseTextView.setTextColor(Color.RED);
                        }
                        else
                        {
                            profileResponseTextView.setText("Saved.");
                            profileResponseTextView.setTextColor(Color.GREEN);
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
        }
    }

    private boolean IsInfoChanged(String fName, String lName, String airline, String emailAddress)
    {
        boolean changed = false;
        if(firstName != null && firstName != "" && firstName != storedFirstName)
        {
            editor.putString("FirstName", firstName);
            changed = true;
        }
        if(lastName != null && lastName != "" && lastName != storedLastName)
        {
            editor.putString("LastName", lastName);
            changed = true;
        }
        if(airline != null && airline != "" && airline != storedAirline)
        {
            editor.putString("Airline", airline);
            changed = true;
        }
        if(emailAddress != null && emailAddress != "" && emailAddress != storedEmail)
        {
            editor.putString("EmailAddress", emailAddress);
            changed = true;
        }
        return changed;
    }
}
