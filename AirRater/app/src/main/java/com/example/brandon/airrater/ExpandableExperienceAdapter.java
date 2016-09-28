package com.example.brandon.airrater;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
        final String childText = (String) getChild(groupPosition, childPosition);

        //if (convertView == null) {
        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = infalInflater.inflate(R.layout.experience_details, null);
        //}

        final TextView txtListChild = (TextView) row
                .findViewById(R.id.brandon);

        txtListChild.setText(childText);
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
        ExperienceItem headerTitle = (ExperienceItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.experience_item, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.businessNameText);
        lblListHeader.setText(headerTitle.businessName);

        RatingBar rating = (RatingBar) convertView.findViewById(R.id.experienceItemRatingBar);
        rating.setNumStars(headerTitle.numStars);

        TextView numUser = (TextView) convertView.findViewById(R.id.numUsersText);
        numUser.setText(headerTitle.numUsers);

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
