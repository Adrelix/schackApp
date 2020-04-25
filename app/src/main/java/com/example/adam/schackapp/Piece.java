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

//Kommer få in tiles, pieces, och var alla andra befinner på brädet
    public Integer[] getMoves(Piece[] pieces){
    ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

    switch (type){                  //switch dependent on what kind of unit is selected
        case "torn":
            for(int i = currentPosition -1; i >= currentPosition - currentPosition%8; i--){              //left
               int collisionResult = checkCollision(i, pieces);     //retrieve the result from checkcollision and act accordingly
               if (collisionResult < 1000){possibleMoves.add(i);}  //if the value from checkcollision is less than 1000 it's either a empty tile or an enemy
               i = i - collisionResult;                             //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }


            //TODO create these
        //    for(int i = currentPosition +1; i < currentPosition + 8-currentPosition%8; i++){ } //right
        //    for(int i){}                            //forw
        //    for(int i){}                            //back
        //    for(int i){}                            //up
        //    for(int i ){}                           //down
            break; //end of case


        //TODO create cases for all other types






        default:  possibleMoves.add(currentPosition -8);            //if no type detected, give it ability to walk 1 step in all (2D) directions
            possibleMoves.add(currentPosition +8);
            possibleMoves.add(currentPosition -1);
            possibleMoves.add(currentPosition +1);
           break;
    }

        Integer[] array = possibleMoves.toArray(new Integer[0]);        //Translates the ArrayList into an array and returns it
        return array;

    }




    /**
     * returns a value dependent on the current occupation at the asked for Position.
     * return values:
    * empty = 0
    * friend = 10000
    * foe = 100
    */
    private int checkCollision(int Position, Piece[] pieces){
        int empty = 0;
        int friend = 10000;
        int foe = 100;
        int result;

        result=empty;

        for (Piece piece : pieces) {
            if(piece.currentPosition == Position){      //Check if any piece stands on the position we wanna check
                if(piece.color==color){                 //check if piece is friend or foe and sets value
                    result=friend;
                }
                else result = foe;
            }
        }

        return result;
    }



}

