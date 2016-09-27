package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
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

    public ExperienceAdapter(Context context, int layoutResourceId, ExperienceItem[] data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
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

        return row;
    }

    static class ExperienceHolder
    {
        TextView txtBusinessName;
        TextView txtNumUsers;
        RatingBar ratingStars;
    }
}
