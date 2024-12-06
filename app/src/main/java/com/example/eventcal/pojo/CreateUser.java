package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class CreateUser {
    @SerializedName("email")
    public String email;
    @SerializedName("name")
    public String name;
    @SerializedName("password")
    public String password;
    @SerializedName("message")
    public String message;

    public CreateUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

