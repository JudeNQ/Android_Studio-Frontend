package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ServerGroup {
    @SerializedName("name")
    public String groupName;
    @SerializedName("bio")
    public String bio;
    @SerializedName("leader")
    public String leader;
    @SerializedName("members")
    public List<String> members = new ArrayList<>();
    @SerializedName("password")
    public String password;
    @SerializedName("_id")
    public String groupId;

    public ServerGroup(String name, String bio, String password) {
        this.groupName = name;
        this.bio = bio;
        this.password = password;
    }
}

