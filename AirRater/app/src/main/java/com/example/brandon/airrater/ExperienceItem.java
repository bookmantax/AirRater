package com.example.brandon.airrater;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExperienceItem {

    public String businessName;
    public int numUsers;
    public int numStars;
    public int position;

    public ExperienceItem(String businessName, int numUsers, int numStars, int position)
    {
        this.businessName = businessName;
        this.numUsers = numUsers;
        this.numStars = numStars;
        this.position = position;
    }
}
