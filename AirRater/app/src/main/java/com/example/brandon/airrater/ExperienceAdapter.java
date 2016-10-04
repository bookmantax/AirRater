package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExperienceAdapter extends ArrayAdapter<ExperienceItem> {

    ExperienceHolder holder;
    Context context;
    int layoutResourceId;
    ExperienceItem data[] = null;
    ExperienceItem exp;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public ExperienceAdapter(Context context, int layoutResourceId, ExperienceItem[] data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        View row = convertView;
        holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ExperienceHolder();
            holder.txtBusinessName = (TextView)row.findViewById(R.id.businessNameText);
            holder.txtNumUsers = (TextView)row.findViewById(R.id.numUsersText);
            holder.ratingStars = (RatingBar)row.findViewById(R.id.experienceItemRatingBar);
            holder.btnCheckIn = (Button)row.findViewById(R.id.experienceCheckInButton);

            row.setTag(holder);
        }
        else
        {
            holder = (ExperienceHolder)row.getTag();
        }

        exp = data[position];

        holder.txtBusinessName.setText(exp.businessName);
        holder.txtNumUsers.setText(exp.numUsers);
        holder.ratingStars.setNumStars(exp.numStars);
        holder.btnCheckIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                settings = parent.getContext().getSharedPreferences("UserPreferences", 0);
                editor = settings.edit();
                editor.putString("Business", exp.businessName);
                Intent activityChangeIntent = new Intent(parent.getContext(), CheckinActivity.class);
                parent.getContext().startActivity(activityChangeIntent);
            }
        });

        return row;
    }

    static class ExperienceHolder
    {
        TextView txtBusinessName;
        TextView txtNumUsers;
        RatingBar ratingStars;
        Button btnCheckIn;
    }
}
