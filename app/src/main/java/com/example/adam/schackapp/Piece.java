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
    public Integer[] getMoves(ArrayList<Piece> pieces, int boardSize){


        /*
        * allPiecesPosition är en array med alla positions på boarden och har fyllt
        * alla positions utan värde med 0, alla med en vän som 1 och alla med en fiende med 2
        * */
        int[] allPiecesPosition = new int[boardSize];

        for (int i = 0; i < allPiecesPosition.length; i++){     //set all position start values to 0, not sure how necessary it is but it's a catch for null errors
            allPiecesPosition[i] = 0;
        }
        for (Piece piece : pieces) {
            if(piece.currentPosition>0) {       //make sure piece still on the board
                if (piece.color == color) {
                    allPiecesPosition[piece.currentPosition] = 1;
                }//check if piece is friend or foe and sets value
                else {
                    allPiecesPosition[piece.currentPosition] = 2;
                }
            }
        }


    ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

    switch (type){                  //switch dependent on what kind of unit is selected
        case "rook":

            //left
            for(int positionToCheck = currentPosition -1; positionToCheck >= currentPosition - currentPosition%8; positionToCheck--){               //check edges of board and loops in right direction
               if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}                                  //Check if tile is empty or enemy and if so adds to possible moves
               if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;} //checks if the loop should continue (if there are more tiles in same direction worth checking)
            }
            //right
            for(int positionToCheck = currentPosition +1; positionToCheck < currentPosition + 8-currentPosition%8; positionToCheck++){
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}
            }
            //up y-axis
            for(int positionToCheck = currentPosition-8; positionToCheck >= currentPosition - currentPosition%64; positionToCheck-=8){
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;}
            }
            //ner y-axis
            for(int positionToCheck = currentPosition+8; positionToCheck < currentPosition + 64-currentPosition%64; positionToCheck+=8){
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}
            }
            //upp z-led
            for(int positionToCheck = currentPosition+64; positionToCheck < boardSize; positionToCheck+=64){
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = 10000;}
            }
            //ner z-led
            for(int positionToCheck = currentPosition-64; positionToCheck >= 0; positionToCheck-=64){
                if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
                if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ positionToCheck = -10000;}
            }

        case "pawn":



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

