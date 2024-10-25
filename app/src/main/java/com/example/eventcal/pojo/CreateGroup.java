package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class CreateGroup {
    @SerializedName("name")
    public String groupName;
    @SerializedName("bio")
    public String groupBio;
    @SerializedName("leader")
    public String leader;

    public CreateGroup(String name, String bio, String leader) {
        this.groupName = name;
        this.groupBio = bio;
        this.leader = leader;
    }
}