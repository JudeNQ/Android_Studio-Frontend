package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class CreateGroup {
    @SerializedName("name")
    public String groupName;
    @SerializedName("password")
    public String password;
    @SerializedName("bio")
    public String groupBio;
    @SerializedName("leader")
    public String leader;

    public CreateGroup(String name, String bio, String password, String leader) {
        this.groupName = name;
        this.groupBio = bio;
        this.leader = leader;
        this.password = password;
    }
}