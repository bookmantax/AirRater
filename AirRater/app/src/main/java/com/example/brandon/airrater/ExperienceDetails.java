package com.example.brandon.airrater;

/**
 * Created by Brandon on 9/27/2016.
 */
public class ExperienceDetails {

    public String employeeFirstName;
    public String employeeLastname;
    public String employeeAirline;
    public String comments;
    public Double numStars;

    public ExperienceDetails(String employeeFirstName, String employeeLastname, String employeeAirline,
                             String comments, Double numStars)
    {
        this.employeeFirstName = employeeFirstName;
        this.employeeLastname = employeeLastname;
        this.employeeAirline = employeeAirline;
        this.comments = comments;
        this.numStars = numStars;
    }
}
