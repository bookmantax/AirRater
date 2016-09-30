package com.example.brandon.airrater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Brandon on 9/28/2016.
 */
public class UserAdapter extends ArrayAdapter<UserItem> {

    UserHolder holder;
    Context context;
    int layoutResourceId;
    UserItem data[] = null;
    UserItem user;

    public UserAdapter(Context context, int layoutResourceId, UserItem[] data)
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

            holder = new UserHolder();
            holder.txtEmployeeName = (TextView)row.findViewById(R.id.userNameTextView);
            holder.txtAirlineName = (TextView)row.findViewById(R.id.userAirlineTextView);
            holder.txtBusinessName = (TextView)row.findViewById(R.id.userBusinessTextView);

            row.setTag(holder);
        }
        else
        {
            holder = (UserHolder)row.getTag();
        }

        user = data[position];

        holder.txtBusinessName.setText(user.businessName);
        holder.txtAirlineName.setText(user.airline);
        holder.txtEmployeeName.setText(user.employeeName);

        return row;
    }

    static class UserHolder
    {
        TextView txtEmployeeName;
        TextView txtAirlineName;
        TextView txtBusinessName;
    }
}
