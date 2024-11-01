package com.example.eventcal.userStorage;

import java.util.ArrayList;

public class UserInfo {
    public static UserInfo info;

    public String userId;
    public ArrayList<String> groups;

    public void initialize() {
        info = new UserInfo();
        userId = "";
        groups = new ArrayList<String>();
    }
}
