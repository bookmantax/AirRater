package com.example.brandon.airrater;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/28/2016.
 */
public class ProfileActivity extends ActionBarActivity
{
    private EditText profileFirstNameEditText, profileLastNameEditText, profileAirlineEditText,
        profileEmailEditText;
    private TextView profileResponseTextView;
    private String firstName, lastName, airline, emailAddress;
    SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
    private String storedFirstName = settings.getString("FirstName", "");
    private String storedLastName = settings.getString("LastName", "");
    private String storedAirline = settings.getString("Airline", "");
    private String storedEmail = settings.getString("EmailAddress", "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileResponseTextView = (TextView)findViewById(R.id.profileResponseTextView);

        profileFirstNameEditText = (EditText)findViewById(R.id.profileFirstNameEditText);
        profileFirstNameEditText.setText(storedFirstName);

        profileLastNameEditText = (EditText)findViewById(R.id.profileLastNameEditText);
        profileLastNameEditText.setText(storedLastName);

        profileAirlineEditText = (EditText)findViewById(R.id.profileAirlineEditText);
        profileAirlineEditText.setText(storedAirline);

        profileEmailEditText = (EditText)findViewById(R.id.profileEmailEditText);
        profileEmailEditText.setText(storedEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Submit(View view)
    {
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
        if(fName != storedFirstName || lName != storedLastName || airline != storedAirline || emailAddress != storedEmail)
        {
            return true;
        }
        return false;
    }
}
