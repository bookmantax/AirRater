package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/21/2016.
 */
public class SignupActivity extends ActionBarActivity
{
    String firstName, lastName, emailAddress, airline, username, password;
    EditText firstEditText, lastEditText, emailEditText, airlineEditText, usernameEditText,
        passwordEditText;
    Button createUserButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstEditText = (EditText)findViewById(R.id.firstEditText);
        lastEditText = (EditText)findViewById(R.id.lastEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        airlineEditText = (EditText)findViewById(R.id.airlineEditText);
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        createUserButton = (Button)findViewById(R.id.createAccountButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void CreateUser(View view)
    {
        firstName = String.valueOf(firstEditText.getText());
        lastName = String.valueOf(lastEditText.getText());
        emailAddress = String.valueOf(emailEditText.getText());
        airline = String.valueOf(airlineEditText.getText());
        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());

        //Service call to create user
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("firstName", firstName);
            object.put("lastName", lastName);
            object.put("emailAddress", emailAddress);
            object.put("airline", airline);
            object.put("username", username);
            object.put("password", password);
            params.put("data", object.toString());

            new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx",
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

        //Store user info in phonedatabase/text file?
    }
}
