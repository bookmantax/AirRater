package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/28/2016.
 */
public class ProfileActivity extends ActionBarActivity
{
    private EditText profileFirstNameEditText, profileLastNameEditText, profileAirlineEditText,
        profileEmailEditText;
    private Button profileSubmitButton;
    private String firstName, lastName, airline, emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFirstNameEditText = (EditText)findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = (EditText)findViewById(R.id.profileLastNameEditText);
        profileAirlineEditText = (EditText)findViewById(R.id.profileAirlineEditText);
        profileEmailEditText = (EditText)findViewById(R.id.profileEmailEditText);

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

        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("firstName", firstName);
            object.put("lastName", lastName);
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://hyperracing.com/api/setupsheets.ashx",
                    params, false, null) {
                @Override
                protected void onPostExecute(String result, Object notes) {

                    //responder.uploadFinished(result != null);
                    super.onPostExecute(result, notes);
                    String s = result;
                }
            }).start();
        }
        catch (Exception e)
        {

        }

    }
}
