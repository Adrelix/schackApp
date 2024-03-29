package com.example.adam.schackapp;


import java.io.Serializable;

/*
 * Database plan:
 *
 *   User(String id, String name, String password, Drawable? profilePic, String quote)
 *   Game(String gameId, String playerOne, String playerTwo, int gameOver, int amountOfBoards, int currentTurn, String piecePositions, int roundNumb, String startDate)
 *
 *   gameOver: should show if game has been won prev round to disable movements etc (not sure if necessary)
 *   currentTurn: should be a 1 or 2 to show if it's playerOnes or playerTwos turn
 *   piecePositions: should be some stored information about all pieces and there positions, not sure if it can be implemented as the whole Pieces structure or if a clever string should be made
 *
 * */
public class GameObject implements Serializable, Comparable<GameObject> {
    private String gameID;
    private String playerOne;
    private String playerTwo;
    private int gameStatus; //GameStatus 0: Game over, 1:Game Active and playerOne:s turn, 2:Game Active and playerTwo:s turn
    private int amountOfBoards;
    private String lastPiecePositions;
    private String piecePositions;
    private int roundNumb;
    private String startDate;
    private String lastMoveDate;
    private String playerOneQuote;
    private String playerTwoQuote;


    @Override
    public int compareTo(GameObject game) {
        return (game.getLastMoveDate().compareTo(this.getLastMoveDate()));
    }

    public GameObject(){
    }
    public GameObject(String gameID, String playerOne, String playerTwo, String playerOneQuote, String playerTwoQuote, String startDate, int amountOfBoards){
        this.gameID = gameID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneQuote = playerOneQuote;
        this.playerTwoQuote = playerTwoQuote;
        this.startDate = startDate;
        this.lastMoveDate = startDate;
        this.amountOfBoards = amountOfBoards;
        this.piecePositions = generateStartPositions(amountOfBoards);
        this.lastPiecePositions = this.piecePositions;
        this.roundNumb = 0;
        this.gameStatus = 1;
    }

    private String generateStartPositions(int amountOfBoards){
        String startPositions = "BTBHBLBDBKBLBHBTBBBBBBBBBBBBBBBB";

        //Filling up whitespace in string, 64 for each board minus start/end 16 where pieces stand
        for(int i = 0; i < amountOfBoards*64 - 32; i++){
            startPositions += "00";
        }
        startPositions += "WBWBWBWBWBWBWBWBWTWHWLWKWDWLWHWT";
        return startPositions;
    }

    /**
     * Change turn, if gameStatus previously 1 it becomes 2 and vice verse
     * */
    public void changeTurn(){
        this.gameStatus = 3-this.gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setLastPiecePositions(String lastPiecePositions) {
        this.lastPiecePositions = lastPiecePositions;
    }

    public void setPiecePositions(String piecePositions) {
        this.piecePositions = piecePositions;
    }

    public void setRoundNumb(int roundNumb) {
        this.roundNumb = roundNumb;
    }

    public void setLastMoveDate(String lastMoveDate) {
        this.lastMoveDate = lastMoveDate;
    }


    public String getGameID() {
        return gameID;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public String getPlayerOneQuote() {
        return playerOneQuote;
    }

    public String getPlayerTwoQuote() {
        return playerTwoQuote;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public int getAmountOfBoards() {
        return amountOfBoards;
    }

    public String getLastPiecePositions() {
        return lastPiecePositions;
    }

    public String getPiecePositions() {
        return piecePositions;
    }

    public int getRoundNumb() {
        return roundNumb;
    }

    public String getStartdate() {
        return startDate;
    }

    public String getLastMoveDate() {
        return lastMoveDate;
    }




}
