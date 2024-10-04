package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class CreateUser {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("message")
    public String message;

    public CreateUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

