package com.example.brandon.airrater;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExpandableExperienceAdapter extends BaseExpandableListAdapter {

    private final Context _context;
    private final List<ExperienceItem> _listDataHeader;
    private final HashMap<ExperienceItem, List<ExperienceDetails>> _listDataChild;

    public ExpandableExperienceAdapter(Context context, List<ExperienceItem> listDataHeader,
                                       HashMap<ExperienceItem, List<ExperienceDetails>> listDataChild)
    {
        this._context = context;
        this._listDataChild = listDataChild;
        this._listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition;}

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent)
    {
        final ExperienceDetails childDetails = (ExperienceDetails) getChild(groupPosition, childPosition);

        //if (convertView == null) {
        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = infalInflater.inflate(R.layout.experience_details, null);
        //}

        final TextView empName = (TextView) row
                .findViewById(R.id.employeeNameTextView);
        final TextView empAirline = (TextView) row
                .findViewById(R.id.employeeAirlineTextView);
        final RatingBar empRating = (RatingBar) row
                .findViewById(R.id.deatailsRatingBar);
        final TextView empComments = (TextView) row
                .findViewById(R.id.employeeCommentsTextView);

        empName.setText(childDetails.employeeFirstName + " " + childDetails.employeeLastname);
        empAirline.setText(childDetails.employeeAirline);
        empRating.setRating(Float.valueOf(String.valueOf(childDetails.numStars)));
        empComments.setText(childDetails.comments);
        return row;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final ExperienceItem headerTitle = (ExperienceItem) getGroup(groupPosition);

        if (convertView == null) {
            final View finalView;
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            finalView = infalInflater.inflate(R.layout.experience_item, null);

            TextView lblListHeader = (TextView) finalView
                    .findViewById(R.id.businessNameText);
            lblListHeader.setText(headerTitle.businessName);

            RatingBar rating = (RatingBar) finalView.findViewById(R.id.experienceItemRatingBar);
            rating.setRating(Float.valueOf(String.valueOf(headerTitle.numStars)));

            TextView numUser = (TextView) finalView.findViewById(R.id.experienceItemNumUsersText);
            numUser.setText(String.valueOf(headerTitle.numUsers));

            Button experienceCheckInButton = (Button)finalView.findViewById(R.id.experienceCheckInButton);
            experienceCheckInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)finalView.getContext()).SetCurrentTab(2);
                    SharedPreferences settings = finalView.getContext().getSharedPreferences("UserPreferences", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Business", headerTitle.businessName);
                    editor.commit();
                }
            });

            convertView = finalView;
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
