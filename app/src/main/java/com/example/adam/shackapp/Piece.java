package com.example.adam.shackapp;

public class Piece {
    String type;
    String color;
    int[] moves;

    public Piece(String type, String color){
        this.type = type;
        this.color = color;
        this.moves = getMoves(type);
    }



   /* private int[] getMoves(String type){
        switch (type){
            case "bonde":
                int[] moves = {};
            break;
            case "lopare":
                int[] moves = {};
                break;
            case "hast":
                int[] moves = {};
                break;
            case "torn":
                int[] moves = {};
                break;
            case "kung":
                int[] moves = {};
                break;
            case "drottning":
                int[] moves = {};
                break;
                default: int[] moves = {};
                break;
        }

        return moves;
    } */
}
