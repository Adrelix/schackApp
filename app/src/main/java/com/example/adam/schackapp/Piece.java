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


    ArrayList<Integer> possibleMoves; //The list that is going to be returned

    switch (type){                  //switch dependent on what kind of unit is selected
        case "pawn":
            possibleMoves = pawnMoves(allPiecesPosition, boardSize);
            break;

        case "rook":
            possibleMoves = rookMoves(allPiecesPosition, boardSize);
            break;

        case "knight":
            possibleMoves = knightMoves(allPiecesPosition, boardSize);
            break;

        case "bishop":
            possibleMoves = bishopMoves(allPiecesPosition, boardSize);
            break;

        case "queen":
            possibleMoves = queenMoves(allPiecesPosition, boardSize);
            break;

        case "king":
            possibleMoves = kingMoves(allPiecesPosition, boardSize);
            break;

        default:
            possibleMoves = new ArrayList<Integer>();
            possibleMoves.add(currentPosition -8);            //if no type detected, give it ability to walk 1 step in all (2D) directions
            possibleMoves.add(currentPosition +8);
            possibleMoves.add(currentPosition -1);
            possibleMoves.add(currentPosition +1);
           break;
    }

        Integer[] array = possibleMoves.toArray(new Integer[0]);        //Translates the ArrayList into an array and returns it
        return array;

    }

    private ArrayList<Integer> pawnMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        //TODO implement passant-move

        if(this.color.equals("white")){
            //Check same board row in front of piece
            for(int i = 0; i < boardSize; i = i+64){
                if(currentPosition >= i && currentPosition < i+64-8 ){      //check that piece isn't on last row of board
                    if(!checkCollisionFoe(currentPosition+8, allPiecesPosition) && !checkCollisionFriend(currentPosition+8, allPiecesPosition)){ //Check that piece straight in front isn't occupied
                        possibleMoves.add(currentPosition+8);
                        if((!checkCollisionFoe(currentPosition+16, allPiecesPosition) && !checkCollisionFriend(currentPosition+16, allPiecesPosition)) && currentPosition < 16 ){ //Check if the piece is at startpos and if so offer 2 steps
                            possibleMoves.add(currentPosition+16);
                        }
                    }

                    if(checkCollisionFoe(currentPosition+7, allPiecesPosition) && currentPosition % 8 !=0){     //Check if there is an enemy diagonally left and that currentPos isn't at left edge
                        possibleMoves.add(currentPosition+7);
                    }
                    if(checkCollisionFoe(currentPosition+9, allPiecesPosition) && currentPosition % 8 !=7){     //Check if there is an enemy diagonally right and that currentPos isn't at right edge
                        possibleMoves.add(currentPosition+9);
                    }
                }
            }
            //Check board below
            if(currentPosition < boardSize-64){
                if(!checkCollisionFoe(currentPosition+64, allPiecesPosition) && !checkCollisionFriend(currentPosition+64, allPiecesPosition)){ //Check that piece straight in front isn't occupied
                    possibleMoves.add(currentPosition+64);
                }
                if(checkCollisionFoe(currentPosition+63, allPiecesPosition) && currentPosition % 8 !=0){     //Check if there is an enemy diagonally left and that currentPos isn't at left edge
                    possibleMoves.add(currentPosition+63);
                }
                if(checkCollisionFoe(currentPosition+65, allPiecesPosition) && currentPosition % 8 !=7){     //Check if there is an enemy diagonally right and that currentPos isn't at right edge
                    possibleMoves.add(currentPosition+65);
                }
            }
        }

        else if(this.color.equals("black")){
            //Check same board row in front of piece
            for(int i = 0; i < boardSize; i = i+64){
                if(currentPosition >= i+8 && currentPosition < i+64 ){      //check that piece isn't on last row of board
                    if(!checkCollisionFoe(currentPosition-8, allPiecesPosition) && !checkCollisionFriend(currentPosition-8, allPiecesPosition)){ //Check that piece straight in front isn't occupied
                        possibleMoves.add(currentPosition-8);
                        if((!checkCollisionFoe(currentPosition-16, allPiecesPosition) && !checkCollisionFriend(currentPosition-16, allPiecesPosition)) && currentPosition >= boardSize-16 ){ //Check if the piece is at startpos and if so offer 2 steps
                            possibleMoves.add(currentPosition-16);
                        }
                    }
                    if(checkCollisionFoe(currentPosition-7, allPiecesPosition) && currentPosition % 8 !=7){     //Check if there is an enemy diagonally right and that currentPos isn't at right edge
                        possibleMoves.add(currentPosition-7);
                    }
                    if(checkCollisionFoe(currentPosition-9, allPiecesPosition) && currentPosition % 8 !=0){     //Check if there is an enemy diagonally left and that currentPos isn't at left edge
                        possibleMoves.add(currentPosition-9);
                    }
                }
            }
            //Checks board above
            if(currentPosition-64 >= 0){
                if(!checkCollisionFoe(currentPosition-64, allPiecesPosition) && !checkCollisionFriend(currentPosition-64, allPiecesPosition)){ //Check that piece straight in front isn't occupied
                    possibleMoves.add(currentPosition-64);
                }
                if(checkCollisionFoe(currentPosition-63, allPiecesPosition) && currentPosition % 8 !=7){     //Check if there is an enemy diagonally left and that currentPos isn't at left edge
                    possibleMoves.add(currentPosition-63);
                }
                if(checkCollisionFoe(currentPosition-65, allPiecesPosition) && currentPosition % 8 !=0){     //Check if there is an enemy diagonally right and that currentPos isn't at right edge
                    possibleMoves.add(currentPosition-65);
                }
            }
        }


        return possibleMoves;
    }

    private ArrayList<Integer> rookMoves(int[] allPiecesPosition, int boardSize){
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned
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

        return possibleMoves;
    }

    private ArrayList<Integer> knightMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        ArrayList<Integer> allMoves = new ArrayList<Integer>();
        /* All possible moves are:
                -10, -17, -15, -6, +10, +15, +17, +6,           //all moves on the same plane
                -62, -66, -80, -48, -127, -129, -120, -136,     //all moves below
                +62, +66, +80, +48, +127, +129, +120, +136      //all moves abow
        */

        for (int i = 0; i < allMoves.size(); i++) {
         if (0 <= allMoves.get(i) && allMoves.get(i)<boardSize && !checkCollisionFriend(allMoves.get(i), allPiecesPosition)){
             possibleMoves.add(allMoves.get(i));
         }
        }

        return possibleMoves;
    }

    private ArrayList<Integer> bishopMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        return possibleMoves;
    }

    private ArrayList<Integer> queenMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        return possibleMoves;
    }

    private ArrayList<Integer> kingMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        return possibleMoves;
    }

    private boolean checkCollisionFriend(int Position, int[] allPiecesPosition){
        return allPiecesPosition[Position] == 1;
    }
    private boolean checkCollisionFoe(int Position, int[] allPiecesPosition){
        return allPiecesPosition[Position] == 2;
    }



}

