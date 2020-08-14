package com.example.adam.schackapp;

import java.io.Serializable;

public class ProfileObject implements Serializable {
    private String profileID;
    private String name;
    private String password;
    private String email;
    private String quote = "Jag är inte cool nog för att ha fått min quote fixad";
    private int amountOfGames;
    private int amountOfWins;

    public ProfileObject(){

    }

    public ProfileObject(String profileID, String name, String password, String email) {
        this.profileID = profileID;
        this.name = name;
        this.password = password;
        this.email = email;
        this.amountOfGames = 0;
        this.amountOfWins = 0;
    }

    public String getProfileID() {
        return profileID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getQuote() {
        return quote;
    }

    public int getAmountOfGames() {
        return amountOfGames;
    }

    public int getAmountOfWins() {
        return amountOfWins;
    }

    public void setAmountOfGames(int amountOfGames) {
        this.amountOfGames = amountOfGames;
    }

    public void setAmountOfWins(int amountOfWins) {
        this.amountOfWins = amountOfWins;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

}
