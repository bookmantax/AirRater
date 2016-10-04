package com.example.brandon.airrater;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 9/28/2016.
 */
public class FindUsersActivity extends ActionBarActivity
{
    private EditText findUsersLocationEditText;
    private TextView findUsersResponseTextView;
    private ListView findUsersListView;
    private String location;
    private UserItem items[];
    private UserAdapter adapter;
    private SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        findUsersLocationEditText = (EditText)findViewById(R.id.findUsersLocationEditText);
        findUsersLocationEditText.setText(settings.getString("Location", ""));
        findUsersListView = (ListView)findViewById(R.id.findUsersListView);
        findUsersResponseTextView = (TextView)findViewById(R.id.findUsersResponseTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Search(View view)
    {
        location = String.valueOf(findUsersLocationEditText.getText());
        if(location != null)
        {
            if(location != settings.getString("Location", ""))
            {
                editor = settings.edit();
                editor.putString("Location", location);
            }
            try {
                RequestParams params = new RequestParams();
                JSONObject object = new JSONObject();
                object.put("location", location);
                params.put("data", object.toString());

                new Thread(new AsyncDownload("http://192.168.0.19/WebServices/Bin/FindUsers.ashx",
                        params, false, null) {
                    @Override
                    protected void onPostExecute(String result, Object notes) {
                        //responder.uploadFinished(result != null);
                        super.onPostExecute(result, notes);
                        if(result.equalsIgnoreCase("Missing Information"))
                        {
                            findUsersResponseTextView.setText("Something went wrong please try again.");
                            findUsersResponseTextView.setTextColor(Color.RED);
                        }
                        else if(result.equalsIgnoreCase("User not found"))
                        {
                            items = new UserItem[1];
                            items[0] = new UserItem("No Users Found", "", "");
                            adapter = new UserAdapter(FindUsersActivity.this, R.layout.user_item, items);
                            findUsersListView.setAdapter(adapter);
                            findUsersResponseTextView.setText("");
                        }
                        else
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                items = new UserItem[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    items[i] = new UserItem(oneObject.getString("firstName") + " " + oneObject.getString("lastName"),
                                            oneObject.getString("businessName"), oneObject.getString("airline"));
                                }
                                adapter = new UserAdapter(FindUsersActivity.this, R.layout.user_item, items);
                                findUsersListView.setAdapter(adapter);
                                findUsersResponseTextView.setText("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {

            }
        }
        else
        {
            findUsersResponseTextView.setText("Please make sure Location has a value.");
            findUsersResponseTextView.setTextColor(Color.RED);
        }
    }
}
