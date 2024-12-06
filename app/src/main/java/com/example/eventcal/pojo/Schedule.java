package com.example.eventcal.pojo;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Schedule {
    @SerializedName("Monday")
    public List<Pair<String, String>> monday;
    @SerializedName("Tuesday")
    public List<Pair<String, String>> tuesday;
    @SerializedName("Wednesday")
    public List<Pair<String, String>> wednesday;
    @SerializedName("Thursday")
    public List<Pair<String, String>> thursday;
    @SerializedName("Friday")
    public List<Pair<String, String>> friday;
    @SerializedName("Saturday")
    public List<Pair<String, String>> saturday;
    @SerializedName("Sunday")
    public List<Pair<String, String>> sunday;

    public Schedule(List<Pair<String,String>> monday, List<Pair<String,String>> tuesday, List<Pair<String,String>> wednesday,
                          List<Pair<String,String>> thursday, List<Pair<String,String>> friday,
                          List<Pair<String,String>> saturday, List<Pair<String,String>> sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
}