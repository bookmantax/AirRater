package com.example.brandon.airrater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 9/26/2016.
 */
public class LoginActivity extends AppCompatActivity
{
    String username, password;
    EditText usernameEditText,passwordEditText;
    TextView loginResponseTextView;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginResponseTextView = (TextView)findViewById(R.id.loginResponseTextView);
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Login(View view)
    {
        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());

        if(RequiredInformationProvided(username, password)) {
            //Service call to get user info (possibly use userId from phone database)
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("username", username);
                object.put("password", password);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/GetInfo.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {

                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        String s = result;
                        if (s.equalsIgnoreCase("Missing Information")) {
                            loginResponseTextView.setText("Something went wrong please try again.");
                            loginResponseTextView.setTextColor(Color.RED);
                        } else {
                            //Store user info in Shared Preferences
                            settings = getSharedPreferences("UserPreferences", 0);
                            editor = settings.edit();
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                try {
                                    JSONObject oneObject = jsonArray.getJSONObject(0);
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Send user to search page.
                            Intent activity = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(activity);
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            loginResponseTextView.setText("Please make sure all fields have a value.");
            loginResponseTextView.setTextColor(Color.RED);
        }
    }

    private boolean RequiredInformationProvided(String username, String password)
    {
        if(username != null && password != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void ForgotPassword(View view)
    {
        //TODO implement mail chimp to send password.
    }
}
