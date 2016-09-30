package com.example.brandon.airrater;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

/**
 * Created by Brandon on 9/28/2016.
 */
public class CheckinActivity extends ActionBarActivity
{
    private EditText checkinLocationEditText, checkinBusinessEditText;
    private ListView checkinUsersListView;
    private String location, businessName;
    private UserItem items[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        checkinLocationEditText = (EditText)findViewById(R.id.checkinLocationEditText);
        checkinBusinessEditText = (EditText)findViewById(R.id.checkBusinessNameEditText);
        checkinUsersListView = (ListView)findViewById(R.id.checkinUsersListView);

        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("location", location);
            object.put("businessName", businessName);
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

        items = new UserItem[5];
        items[0] = new UserItem("Brandon True", "Delta", "Arooga's");

        UserAdapter adapter = new UserAdapter(this, R.layout.user_item, items);

        checkinUsersListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void Checkin(View view)
    {
        location = String.valueOf(checkinLocationEditText.getText());
        businessName = String.valueOf(checkinBusinessEditText.getText());

        try {
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            object.put("location", location);
            object.put("businessName", businessName);
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
