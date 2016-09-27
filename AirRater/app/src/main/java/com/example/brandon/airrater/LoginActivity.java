package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/26/2016.
 */
public class LoginActivity extends ActionBarActivity
{
    String username, password;
    EditText usernameEditText,passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        //Service call to get user info (possibly use userId from phone database)
        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("username", username);
            object.put("password", password);
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

    public void ForgotPassword(View view)
    {
        //implement mail chimp to send password.
    }
}
