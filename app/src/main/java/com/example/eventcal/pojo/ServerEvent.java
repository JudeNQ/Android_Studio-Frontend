package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class ServerEvent {
    @SerializedName("name")
    public String eventName;
    @SerializedName("org")
    public String org;
    //Start and end time should be in the 24 hr format. Ex; 14:30 & 16:00
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;
    @SerializedName("date")
    public String date;
    @SerializedName("location")
    public String location;
    @SerializedName("bio")
    public String bio;
    @SerializedName("_id")
    public String eventId;
    @SerializedName("message")
    public String message;

    public ServerEvent(String name, String org, String startTime, String endTime, String date, String location, String description) {
        this.eventName = name;
        this.org = org;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.location = location;
        this.bio = description;
    }
}

