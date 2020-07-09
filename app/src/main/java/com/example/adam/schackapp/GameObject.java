package com.example.adam.schackapp;


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
public class GameObject {
    String gameID;
    String playerOne;
    String playerTwo;
    int gameStatus; //GameStatus 0: Game over, 1:Game Active and playerOne:s turn, 2:Game Active and playerTwo:s turn
    int amountOfBoards;
    String LastPiecePositions;
    String PiecePositions;
    int roundNumb;
    String startdate;
    String lastMoveDate;



    public GameObject(){
    }
    public GameObject(String gameID, String playerOne, String playerTwo, String startdate, int amountOfBoards){
        this.gameID = gameID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.startdate = startdate;
        this.amountOfBoards = amountOfBoards;

    }


}
