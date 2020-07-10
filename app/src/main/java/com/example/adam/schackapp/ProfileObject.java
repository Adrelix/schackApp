package com.example.adam.schackapp;

import java.io.Serializable;

public class ProfileObject implements Serializable {
    String profileID;
    String name;
    String password;
    String email;
    String quote = "Jag är inte cool nog för att ha fått min quote fixad";

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

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

}
