package com.example.adam.schackapp;
import java.util.ArrayList;

public class Piece {
    String type;
    String color;
    int currentPosition;

    public Piece(String type, String color, int initialPosition){
        this.type = type;
        this.color = color;
        this.currentPosition = initialPosition;
    }

//Kommer få in tiles, pieces, och var alal andra befinner på brädet

    public Integer[] getMoves(Piece[] pieces){
    ArrayList<Integer> possibleMoves = new ArrayList<>();

    switch (type){
        case "torn":
            possibleMoves.add(currentPosition +1);
            Integer[] array = possibleMoves.toArray(new Integer[0]);
                return array;
        {4, 7, 9}


        default: Integer[] list = {currentPosition -8, currentPosition + 8, currentPosition+1, currentPosition-1};
            return list;
    }
    }
}

