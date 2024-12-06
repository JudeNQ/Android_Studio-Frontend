package com.example.eventcal.userStorage;

import com.example.eventcal.models.Event;

import java.util.ArrayList;

public class UserInfo {
    private static UserInfo instance = null;

    public String userId;
    public ArrayList<String> groups;
    public ArrayList<Event> savedEvents;

    // Private constructor to prevent direct instantiation
    private UserInfo() {
        userId = "";
        groups = new ArrayList<>();
        savedEvents = new ArrayList<>();
    }

    // Static method to get the singleton instance
    public static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }
}