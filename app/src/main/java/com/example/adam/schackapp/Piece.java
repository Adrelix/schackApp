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
    public Integer[] getMoves(Piece[] pieces, int boardSize){


        /*
        * allPiecesPosition är en array med alla positions på boarden och har fyllt
        * alla positions utan värde med 0, alla med en vän som 1 och alla med en fiende med 2
        * */
        int[] allPiecesPosition = new int[boardSize];

        for (int i : allPiecesPosition){
            i=0;
        }
        for (Piece piece : pieces) {
                if(piece.color==color){allPiecesPosition[piece.currentPosition] = 1; }//check if piece is friend or foe and sets value
                else{ allPiecesPosition[piece.currentPosition] = 2;}
        }


    ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

    switch (type){                  //switch dependent on what kind of unit is selected
        case "torn":
            for(int positionToCheck = currentPosition -1; positionToCheck >= currentPosition - currentPosition%8; positionToCheck--){              //left
               if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
               if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;}
            }
            for(int positionToCheck = currentPosition +1; positionToCheck < currentPosition + 8-currentPosition%8; positionToCheck++){              //right
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}  //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }
            for(int positionToCheck = currentPosition-8; positionToCheck >= currentPosition - currentPosition%64; positionToCheck-=8){              //up y-axis
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;}  //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }
            for(int positionToCheck = currentPosition+8; positionToCheck < currentPosition + 64-currentPosition%64; positionToCheck+=8){              //ner y-axis
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}   //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }
            for(int positionToCheck = currentPosition+64; positionToCheck < boardSize; positionToCheck+=64){                                          //upp z-led
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}  //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }
            for(int positionToCheck = currentPosition-64; positionToCheck >= 0; positionToCheck-=64){                                          //ner z-led
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;}  //update the value of i dependet on checkcollision value, if it's a friend or foe it breaks the loop
            }

        case "bond":



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



    private boolean checkCollisionFriend(int Position, int[] allPiecesPosition){
        return allPiecesPosition[Position] == 1;
    }
    private boolean checkCollisionFoe(int Position, int[] allPiecesPosition){
        return allPiecesPosition[Position] == 2;
    }



}

