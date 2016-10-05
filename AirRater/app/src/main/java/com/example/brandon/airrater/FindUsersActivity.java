package com.example.brandon.airrater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 9/28/2016.
 */
public class FindUsersActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener
{
    private AHBottomNavigation bottomBar;
    private EditText findUsersLocationEditText;
    private TextView findUsersResponseTextView;
    private ListView findUsersListView;
    private String location;
    private UserItem items[];
    private UserAdapter adapter;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        settings = getSharedPreferences("UserPreferences", 0);
        findUsersLocationEditText = (EditText)findViewById(R.id.findUsersLocationEditText);
        findUsersLocationEditText.setText(settings.getString("Location", ""));
        findUsersListView = (ListView)findViewById(R.id.findUsersListView);
        findUsersResponseTextView = (TextView)findViewById(R.id.findUsersResponseTextView);

        bottomBar = (AHBottomNavigation)findViewById(R.id.usersNavBar);
        bottomBar.setOnTabSelectedListener(this);
        this.CreateNavItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void CreateNavItems()
    {
        AHBottomNavigationItem searchItem = new AHBottomNavigationItem("Search", R.drawable.ic_action_name);
        AHBottomNavigationItem usersItem = new AHBottomNavigationItem("Users", R.drawable.ic_action_name);
        AHBottomNavigationItem checkInItem = new AHBottomNavigationItem("CheckIn", R.drawable.ic_action_name);
        AHBottomNavigationItem ratingItem = new AHBottomNavigationItem("Rating", R.drawable.ic_action_name);
        AHBottomNavigationItem profileItem = new AHBottomNavigationItem("Profile", R.drawable.ic_action_name);

        bottomBar.addItem(searchItem);
        bottomBar.addItem(usersItem);
        bottomBar.addItem(checkInItem);
        bottomBar.addItem(ratingItem);
        bottomBar.addItem(profileItem);

        bottomBar.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomBar.setCurrentItem(1);
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

    @Override
    public void onTabSelected(int position, boolean wasSelected)
    {
        if(position == 0)
        {
            Intent activity = new Intent(FindUsersActivity.this, SearchExperienceActivity.class);
            startActivity(activity);
        }
        else if(position == 1)
        {
            //do nothing
        }
        else if(position == 2)
        {
            Intent activity = new Intent(FindUsersActivity.this, CheckinActivity.class);
            startActivity(activity);
        }
        else if(position == 3)
        {
            Intent activity = new Intent(FindUsersActivity.this, RateExperienceActivity.class);
            startActivity(activity);
        }
        else if(position == 4)
        {
            Intent activity = new Intent(FindUsersActivity.this, ProfileActivity.class);
            startActivity(activity);
        }
    }
}
