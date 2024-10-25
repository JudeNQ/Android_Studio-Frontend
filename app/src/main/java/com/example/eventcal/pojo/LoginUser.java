package com.example.eventcal.pojo;

import com.google.gson.annotations.SerializedName;

public class LoginUser {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("confirmed")
    public String confirmed;
    @SerializedName("id")
    public String id;

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
