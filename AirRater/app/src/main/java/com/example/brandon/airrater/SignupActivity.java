package com.example.brandon.airrater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 9/21/2016.
 */
public class SignupActivity extends AppCompatActivity
{
    String firstName, lastName, emailAddress, airline, username, password;
    private ArrayAdapter<String> adapter;
    private ProgressBar signupProgressBar;
    EditText firstEditText, lastEditText, emailEditText, usernameEditText,
        passwordEditText;
    AutoCompleteTextView airlineEditText;
    TextView signupResponseTextView;
    Button createUserButton, signupLoginButton;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String[] airlines = getResources().getStringArray(R.array.Airlines);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, airlines);
        airlineEditText = (AutoCompleteTextView) findViewById(R.id.airlineEditText);
        airlineEditText.setAdapter(adapter);
        airlineEditText.setThreshold(1);

        signupResponseTextView = (TextView)findViewById(R.id.signupResponseTextView);
        firstEditText = (EditText)findViewById(R.id.firstEditText);
        lastEditText = (EditText)findViewById(R.id.lastEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        createUserButton = (Button)findViewById(R.id.createAccountButton);
        signupLoginButton = (Button)findViewById(R.id.signupLoginButton);
        signupProgressBar = (ProgressBar)findViewById(R.id.signupProgressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Login(View view)
    {
        Intent activity = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(activity);
    }

    public void CreateUser(View view)
    {
        createUserButton.setEnabled(false);
        signupLoginButton.setEnabled(false);
        signupProgressBar.setVisibility(View.VISIBLE);

        firstName = String.valueOf(firstEditText.getText());
        lastName = String.valueOf(lastEditText.getText());
        emailAddress = String.valueOf(emailEditText.getText());
        airline = String.valueOf(airlineEditText.getText());
        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());

        if(RequiredInformationProvided(firstName, lastName, airline, emailAddress, username, password))
        {
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

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/CreateUser.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes)
                    {
                        super.onPostExecute(result, notes);
                        String s = result;
                        if (s != null && s.equalsIgnoreCase("Missing Information")) {
                            signupResponseTextView.setText("Something went wrong please try again.");
                            signupResponseTextView.setTextColor(Color.RED);
                            createUserButton.setEnabled(true);
                            signupLoginButton.setEnabled(true);
                            signupProgressBar.setVisibility(View.GONE);
                        }
                        else if(s!= null)
                        {
                            //Store user info in Shared Preferences
                            settings = getSharedPreferences("UserPreferences", 0);
                            editor = settings.edit();
                            try {
                                    JSONObject oneObject = new JSONObject(result);
                                    // Pulling items from the array
                                    editor.putInt("UserId", Integer.valueOf(oneObject.getString("UserId")));
                                    editor.putString("FirstName", oneObject.getString("FirstName"));
                                    editor.putString("Lastname", oneObject.getString("LastName"));
                                    editor.putString("EmailAddress", oneObject.getString("EmailAddress"));
                                    editor.putString("Airline", oneObject.getString("Airline"));
                                    editor.putString("Username", oneObject.getString("Username"));
                                    editor.putString("Password", oneObject.getString("Password"));
                                    editor.commit();

                            } catch (JSONException e) {
                                // Oops
                            }
                            signupProgressBar.setVisibility(View.GONE);
                            //Send user to search page.
                            Intent activity = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(activity);
                            finish();
                        }
                        else{
                            signupResponseTextView.setText("Something went wrong please try again.");
                            signupResponseTextView.setTextColor(Color.RED);
                            createUserButton.setEnabled(true);
                            signupLoginButton.setEnabled(true);
                            signupProgressBar.setVisibility(View.GONE);
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            signupResponseTextView.setText("Please make sure all fields have a value.");
            signupResponseTextView.setTextColor(Color.RED);
            createUserButton.setEnabled(true);
            signupLoginButton.setEnabled(true);
            signupProgressBar.setVisibility(View.GONE);
        }
    }

    private boolean RequiredInformationProvided(String firstName, String lastName, String airline,
                                                String emailAddress, String username, String password)
    {
        if(firstName != null && lastName != null && airline != null && emailAddress != null &&
                username != null && password != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
