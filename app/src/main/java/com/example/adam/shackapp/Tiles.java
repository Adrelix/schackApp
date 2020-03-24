package com.example.adam.shackapp;

import android.widget.ImageButton;

public class Tiles {
    public boolean isReachable;
    public boolean isOccupied;
    public ImageButton button;
    public String color;

    public Tiles(String color){
        boolean isReachable = false;
        boolean isOccupied = false;
        this.color = color;

    }

    /*
    * Function runs whenever someone clicks on the tile
    * */
    public void onClick(){

    }
}
