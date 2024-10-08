package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class SaveEvent {

    @SerializedName("user_id")
    public String userId;
    @SerializedName("event_id")
    public String eventId;
    @SerializedName("message")
    public String message;

    public SaveEvent(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
}