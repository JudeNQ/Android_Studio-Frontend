package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class TestUser {
    @SerializedName("name")
    public String name;
    @SerializedName("job")
    public String job;
    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public String createdAt;

    public TestUser(String name, String job) {
        this.name = name;
        this.job = job;
    }
}
