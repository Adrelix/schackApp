package com.example.adam.schackapp;

public class ProfileObject {
    String profileID;
    String name;
    String password;
    String email;

    public ProfileObject(){

    }

    public ProfileObject(String profileID, String name, String password, String email) {
        this.profileID = profileID;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getProfileID() {
        return profileID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
