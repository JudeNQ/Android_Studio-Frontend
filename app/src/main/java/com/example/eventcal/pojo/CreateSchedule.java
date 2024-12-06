package com.example.eventcal.pojo;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateSchedule {
    @SerializedName("Schedule")
    public Schedule schedule;
    @SerializedName("_id")
    public String userId;

    public CreateSchedule(Schedule schedule, String userId) {
        this.schedule = schedule;
        this.userId = userId;
    }
}