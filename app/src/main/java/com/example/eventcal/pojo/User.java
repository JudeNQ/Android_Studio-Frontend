package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
