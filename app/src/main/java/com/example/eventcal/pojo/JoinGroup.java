package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class JoinGroup {
    @SerializedName("user")
    public String userId;
    @SerializedName("group")
    public String groupId;
    @SerializedName("password")
    public String password;
    @SerializedName("message")
    public String message;

    public JoinGroup(String userId, String groupId, String password) {
        this.userId = userId;
        this.groupId = groupId;
        this.password = password;
    }
}