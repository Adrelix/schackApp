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

    /**
     * Creates, based on type, position and color, an arrayList with all the possible
     * moves the piece can make. Uses ****moves() as a help function to make things less messy
     * @param pieces is all currently active pieces
     * @param boardSize is amount of tiles in current game
     * @param status is either -1 or something >=0, depending on where function is called for.
     *               If status == -1, checkIfMovePutYouInCheck will be called
     *               Else if Status >= 0 it will be skipped (aka from function checkIfMovePutYouInCheck), and allPiecesPosition will have king location as 0
     * */
    public Integer[] getMoves(ArrayList<Piece> pieces, int boardSize, int status){


        /*
         * allPiecesPosition är en array med alla positions på boarden och har fyllt
         * alla positions utan värde med 0, alla med en vän som 1 och alla med en fiende med 2
         * */
        int[] allPiecesPosition = new int[boardSize];

        for (int i = 0; i < allPiecesPosition.length; i++){     //set all position start values to 0, not sure how necessary it is but it's a catch for null errors
            allPiecesPosition[i] = 0;
        }
        for (Piece piece : pieces) {
            if(piece.currentPosition>=0) {       //make sure piece still on the board
                if (piece.color.equals(color)) {
                    allPiecesPosition[piece.currentPosition] = 1;
                }//check if piece is friend or foe and sets value
                else {
                    allPiecesPosition[piece.currentPosition] = 2;
                }
            }
        }
        if(status>=0){
            allPiecesPosition[status] = 2;
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

        //Sort out all moves that would put you into check
        if(status==-1){
            for(int i = 0; i < possibleMoves.size(); i++){
                if(checkIfMovePutYouInCheck(pieces, possibleMoves.get(i), boardSize)){
                    possibleMoves.remove(i);
                    i--;
                }
            }
        }

        Integer[] array = possibleMoves.toArray(new Integer[0]);        //Translates the ArrayList into an array and returns it

        return array;

    }

    private ArrayList<Integer> pawnMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        //TODO implement passant-move

        if(this.color.equals("black")){
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

        else if(this.color.equals("white")){
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
        for(int positionToCheck = currentPosition -1; checkIfSameHorizontalRow(positionToCheck); positionToCheck--){               //check edges of board and loops in right direction
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}                                  //Check if tile is empty or enemy and if so adds to possible moves
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;} //checks if the loop should continue (if there are more tiles in same direction worth checking)
        }
        //right
        for(int positionToCheck = currentPosition +1; checkIfSameHorizontalRow(positionToCheck); positionToCheck++){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        //up y-axis
        for(int positionToCheck = currentPosition-8; checkIfSameBoard(currentPosition, positionToCheck); positionToCheck-=8){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        //ner y-axis
        for(int positionToCheck = currentPosition+8; checkIfSameBoard(currentPosition, positionToCheck); positionToCheck+=8){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        //upp z-led
        for(int positionToCheck = currentPosition+64; positionToCheck < boardSize; positionToCheck+=64){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){break;}
        }
        //ner z-led
        for(int positionToCheck = currentPosition-64; positionToCheck >= 0; positionToCheck-=64){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }

        return possibleMoves;
    }

    private ArrayList<Integer> knightMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        // All 24 possible moves in all directions that are supposed to be sorted out which are possible or not
        int[] allMovesArray = {
                currentPosition-10, currentPosition-17, currentPosition-15, currentPosition-6, currentPosition+10, currentPosition+15, currentPosition+17, currentPosition+6,           //all moves on the same plane
                currentPosition-62, currentPosition-66, currentPosition-80, currentPosition-48, currentPosition-127, currentPosition-129, currentPosition-120, currentPosition-136,     //all moves below
                currentPosition+62, currentPosition+66, currentPosition+80, currentPosition+48, currentPosition+127, currentPosition+129, currentPosition+120, currentPosition+136      //all moves abow
        };

        //Checks all tiles that are supposed to be on the same board
        for (int i = 0; i < 8; i++) {
            if (0 <= allMovesArray[i] && allMovesArray[i]<boardSize && !checkCollisionFriend(allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i]) && checkIfSameBoard(currentPosition, allMovesArray[i])){
                possibleMoves.add(allMovesArray[i]);
            }
        }
        //Check the tiles that can be one board below
        for (int i = 8; i < 12; i++) {
            if (0 <= allMovesArray[i] && allMovesArray[i] < boardSize && !checkCollisionFriend(allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i])&& checkIfSameBoard(currentPosition-64, allMovesArray[i])) {
                possibleMoves.add(allMovesArray[i]);
            }
        }
        //Check the tiles that can be 2 boards below
        for (int i = 12; i < 16; i++) {
            if (0 <= allMovesArray[i] && allMovesArray[i] < boardSize && !checkCollisionFriend(allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i])&& checkIfSameBoard(currentPosition-128, allMovesArray[i])) {
                possibleMoves.add(allMovesArray[i]);
            }
        }
        //Check the tiles that can be 1 board above
        for (int i = 16; i < 20; i++) {
            if (0 <= allMovesArray[i] && allMovesArray[i] < boardSize && !checkCollisionFriend(allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i])&& checkIfSameBoard(currentPosition+64, allMovesArray[i])) {
                possibleMoves.add(allMovesArray[i]);
            }
        }
        //Check the tiles that can be 2 boards above
        for (int i = 20; i < 24; i++) {
            if (0 <= allMovesArray[i] && allMovesArray[i] < boardSize && !checkCollisionFriend(allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i])&& checkIfSameBoard(currentPosition+128, allMovesArray[i])) {
                possibleMoves.add(allMovesArray[i]);
            }
        }

        return possibleMoves;
    }

    private ArrayList<Integer> bishopMoves(int[] allPiecesPosition, int boardSize) {
        int lastCheckedPosition = currentPosition;
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        //left-x up-y
        for(int positionToCheck = currentPosition -9; checkIfSameBoard(currentPosition, positionToCheck) && checkIfSameSideOfBoard(positionToCheck+9, positionToCheck); positionToCheck-=9){ //check edges of board and loops in right direction
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);} //Check if tile is empty or enemy and if so adds to possible moves
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;} //checks if the loop should continue (if there are more tiles in same direction worth checking)
        }
        //right-x up-y
        for(int positionToCheck = currentPosition - 7; checkIfSameBoard(currentPosition, positionToCheck)&& checkIfSameSideOfBoard(positionToCheck+7, positionToCheck); positionToCheck-=7){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        //left-x down-y
        for(int positionToCheck = currentPosition + 7; checkIfSameBoard(currentPosition, positionToCheck)&& checkIfSameSideOfBoard(positionToCheck-7, positionToCheck); positionToCheck+=7){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        //right-x down-y
        for(int positionToCheck = currentPosition+9; checkIfSameBoard(currentPosition, positionToCheck)&& checkIfSameSideOfBoard(positionToCheck-9, positionToCheck); positionToCheck+=9){
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }

        //left-x up-y up-z
        for(int positionToCheck = currentPosition+64-9; positionToCheck < boardSize && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck+=64-9){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){break;}
        }
        lastCheckedPosition=currentPosition;
        //right-x up-y up-z
        for(int positionToCheck = currentPosition+64-7; positionToCheck < boardSize && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck+=64-7){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        lastCheckedPosition = currentPosition;

        //left-x down-y up-z
        for(int positionToCheck = currentPosition+64+7; positionToCheck < boardSize && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck+=64+7){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){break;}
        }
        lastCheckedPosition = currentPosition;

        //right-x down-y up-z
        for(int positionToCheck = currentPosition+64+9; positionToCheck < boardSize && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck+=64+9){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        lastCheckedPosition = currentPosition;



        //left-x up-y down-z
        for(int positionToCheck = currentPosition-64-9; positionToCheck >= 0 && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck-=73){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){break;}
        }
        lastCheckedPosition = currentPosition;

        //right-x up-y down-z
        for(int positionToCheck = currentPosition-64-7; positionToCheck >= 0 && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck-=71){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }
        lastCheckedPosition = currentPosition;

        //left-x down-y down-z
        for(int positionToCheck = currentPosition-64+7; positionToCheck >= 0 && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck-=57){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){break;}
        }
        lastCheckedPosition = currentPosition;

        //right-x down-y down-z
        for(int positionToCheck = currentPosition-64+9; positionToCheck >= 0 && !checkIfSameBoard(lastCheckedPosition, positionToCheck); positionToCheck-=55){
            lastCheckedPosition= positionToCheck;
            if (!checkCollisionFriend(positionToCheck, allPiecesPosition)){possibleMoves.add(positionToCheck);}
            if(checkCollisionFriend(positionToCheck, allPiecesPosition) || checkCollisionFoe(positionToCheck, allPiecesPosition)){ break;}
        }



        return possibleMoves;
    }

    private ArrayList<Integer> queenMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        ArrayList<Integer> rookMoves = rookMoves(allPiecesPosition, boardSize);
        ArrayList<Integer> bishopMoves = bishopMoves(allPiecesPosition, boardSize);

        possibleMoves.addAll(rookMoves);
        possibleMoves.addAll(bishopMoves);


        return possibleMoves;
    }

    private ArrayList<Integer> kingMoves(int[] allPiecesPosition, int boardSize) {
        ArrayList<Integer> possibleMoves = new ArrayList<>(); //The list that is going to be returned

        // All 24 possible moves in all directions that are supposed to be sorted out which are possible or not
        int[] allMovesArray = {
                currentPosition-9, currentPosition-8, currentPosition-7, currentPosition-1, currentPosition+1, currentPosition+7, currentPosition+8, currentPosition+9,           //all moves on the same plane
                currentPosition-64-9, currentPosition-64-8, currentPosition-64-7, currentPosition-64-1, currentPosition-64, currentPosition-64+1, currentPosition-64+7, currentPosition-64+8, currentPosition-64+9,     //all moves one board below
                currentPosition+64-9, currentPosition+64-8, currentPosition+64-7, currentPosition+64-1, currentPosition+64, currentPosition+64+1, currentPosition+64+7, currentPosition+64+8, currentPosition+64+9,     //all moves one board above
        };

        //Check all tiles on same board
        for (int i = 0; i < 8; i++) {
            if (0 <= allMovesArray[i] &&  allMovesArray[i]<boardSize && !checkCollisionFriend( allMovesArray[i], allPiecesPosition) && checkIfSameSideOfBoard(currentPosition, allMovesArray[i]) && checkIfSameBoard(currentPosition, allMovesArray[i])){
                possibleMoves.add( allMovesArray[i]);
            }
        }
        //Check all tiles on board below
        for (int i = 8; i < 17; i++) {
            if (0 <= allMovesArray[i] &&  allMovesArray[i]<boardSize && !checkCollisionFriend( allMovesArray[i], allPiecesPosition)&& checkIfSameSideOfBoard(currentPosition, allMovesArray[i]) && checkIfSameBoard(currentPosition-64, allMovesArray[i])){
                possibleMoves.add( allMovesArray[i]);
            }
        }
        //Check all tiles on board above
        for (int i = 17; i < 26; i++) {
            if (0 <= allMovesArray[i] &&  allMovesArray[i]<boardSize && !checkCollisionFriend( allMovesArray[i], allPiecesPosition)&& checkIfSameSideOfBoard(currentPosition, allMovesArray[i]) && checkIfSameBoard(currentPosition+64, allMovesArray[i])){
                possibleMoves.add( allMovesArray[i]);
            }
        }





        return possibleMoves;
    }


/**
 * Emulates the board if a move should be made and returns true/false dependent on if the player
 * would be set in check if the move was followed through
 * */
    private boolean checkIfMovePutYouInCheck(ArrayList<Piece> pieces, int move,  int boardSize){
    Integer kingPos = -1;
    int savedPosition = currentPosition;

    //First emulating the situation **IF** the move was made
        for (int i = 0; i < pieces.size(); i++) {
            //Finding where the king would be
            if(pieces.get(i).color.equals(color) && pieces.get(i).type.equals("king")){
                kingPos = pieces.get(i).currentPosition;
                if(type.equals("king")){
                    kingPos = move;

                }
            }
            //Placing the moved piece to it's new position
            if (pieces.get(i).currentPosition == currentPosition){
                pieces.get(i).currentPosition = move;
            }
        }

        //Checking all the possible moves the enemy could make
        for (int i = 0; i < pieces.size(); i++){
            if(!pieces.get(i).color.equals(color)){
                Integer[] enemyMoves = pieces.get(i).getMoves(pieces, boardSize, kingPos);
                for (Integer enemyMove: enemyMoves) {
                    System.out.println("ENEMY MOVE BY: "+ pieces.get(i).type + " AT: " + pieces.get(i).currentPosition + " TO: " + enemyMove + " KINGPOS: " + kingPos + " CURRENTPOS: " + currentPosition);

                    //if one of them is a king-killing move, function returns true
                    if(enemyMove.equals(kingPos) && move != pieces.get(i).currentPosition){
                        currentPosition = savedPosition;
                        return true;
                    }
                }
            }
        }

        //Value of current piece is restored
        currentPosition = savedPosition;
        return false;
    }

/**
 * Checks a tile to see if there is a friendly piece (piece of the same color) there or not)
 * */
    private boolean checkCollisionFriend(int Position, int[] allPiecesPosition){
        //TODO find a way to not use try/catch, it's ugly.
        try{
            return allPiecesPosition[Position] == 1;

        }catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
    }
    /**
     * Checks a tile to see if there is a foe piece (piece of different color) there or not)
     * */
    private boolean checkCollisionFoe(int Position, int[] allPiecesPosition){
        try{
            return allPiecesPosition[Position] == 2;

        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    /**
     * Check if
     * @param Position is on the same horizontal (x-axis) row as currentPosition
     * */
    private boolean checkIfSameHorizontalRow(int Position){
        return Position < currentPosition + 8-currentPosition%8 && Position >= currentPosition - currentPosition%8;
    }

    /**
     * Compares
     * @param lastPos which is usually currentPosition, with
     * @param Position and returns true if they are on the same side (x-axis) of the board,
     *                 aka hasn't jumped to the other side of the board due to tiles being following numbered.
     * */
    private boolean checkIfSameSideOfBoard(int lastPos, int Position){
        if(type.equals("knight")){
            return Position%8 == lastPos%8+1 || Position%8 == lastPos%8-1 || Position%8 == lastPos%8 || Position%8 == lastPos%8+2 || Position%8 == lastPos%8-2;
        }
        return Position%8 == lastPos%8+1 || Position%8 == lastPos%8-1 || Position%8 == lastPos%8;
    }

    /**
     * Compares
     * @param lastPos which is usually currentPosition, with
     * @param Position and returns true if they are on the same board.
     * */
    private boolean checkIfSameBoard(int lastPos, int Position){
        return Position >= lastPos - lastPos%64 && Position < lastPos + 64-lastPos%64;
    }
}