package com.example.eventcal.userStorage;

import java.util.ArrayList;

public class UserInfo {
    private static UserInfo instance = null;

    public String userId;
    public ArrayList<String> groups;

    // Private constructor to prevent direct instantiation
    private UserInfo() {
        userId = "";
        groups = new ArrayList<>();
    }

    // Static method to get the singleton instance
    public static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }
}