package com.example.adam.schackapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;





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




public class GameBoard extends AppCompatActivity {

    //Game info
    String opponentName;
    String gameID;
    int gameStatus;
    String lastMoveDate;
    String lastPiecePosition;
    String piecePosition;
    int roundNumb;
    String startDate;
    String opponentQuote;

    //Custom info
    int WHITE_TILE;
    int BLACK_TILE;

    //game values
    private int amountOfBoards = 3;
    private int amountOfTiles = amountOfBoards * 64;
    Tiles[] tiles;
    ArrayList<Piece> pieces;
    Integer[] moves;        //Blåa rutorna som highlightas, dvs möjliga dragen
    String currentTurnColor = "white";          //indicates whos turn it is, default white
    int prevSelectedTile = -1;                  //shows the index of previously selected tiles, neg number for none selected


    DatabaseReference databaseProfiles;
    DatabaseReference databaseGames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_layout);


        //make the statusbar(maybe?) and bottom (android) navbar black
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }



        //Load information from intent
        GameObject game = (GameObject) getIntent().getSerializableExtra("gameToLoad");
        loadInGame(game);



        TextView opponentNameView = findViewById(R.id.playerName);
        opponentNameView.setText(opponentName);


        //Load information from database
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        databaseGames = FirebaseDatabase.getInstance().getReference("games");



        tiles = new Tiles[amountOfTiles];
        //Here we assert the resource used for the black and white tile
        BLACK_TILE = R.drawable.black_tile2;
        WHITE_TILE = R.drawable.white_tile2;

        //Get display width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int tileWidth = displayMetrics.widthPixels - (displayMetrics.widthPixels/10);
        int rowWidth = displayMetrics.widthPixels;


        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlay);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"
        layout.setPadding(0,0,0, tileWidth/8);

        // Create all the tiles
        for(int row=0;row<amountOfBoards*8;row++){
            for(int col=0;col<8;col++) {
                int curBoard;
                if(7 < row && row < 16) curBoard = 1; else curBoard = 0;    //Check if current board is an odd or even board to make sure the patterns arent repeated, atm it's a bit hardcoded, should be fixed
                if((row+col+curBoard)%2 == 0){   tiles[(row*8) + col] = new Tiles("white");}
                else {                           tiles[(row*8) + col] = new Tiles("black");}
            }
        }

        //adding buttons and connecting them to tiles
        for (int i = 0; i < 8*amountOfBoards; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(rowWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            row.setGravity(Gravity.CENTER);
            for (int j = 0; j < 8; j++) {
                final int currTile = (i *8) + j;    //the tile index
                ImageButton btnTag = new ImageButton(this);

                //setting the measurements for each tile
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tileWidth/8, tileWidth/8);
                btnTag.setLayoutParams(params);

                //applying textures to the tiles
                if(tiles[currTile].color.equals("white")){btnTag.setBackground(getResources().getDrawable(WHITE_TILE));}
                else if(tiles[currTile].color.equals("black")){btnTag.setBackground(getResources().getDrawable(BLACK_TILE));}

                //connecting button to tile
                tiles[currTile].button = btnTag;
                tiles[currTile].button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


                //adding button to row
                row.addView(btnTag);

                //The functionallity of each tile/button
                tiles[currTile].button.setOnClickListener(new View.OnClickListener()   {
                    public void onClick(View v)  {
                        tiles[currTile].onClick();
                        onPressedTile(currTile);
                    }
                });
            }

            //Check if row should be empty row (to separate boards)
            if(i%8 == 0){
                LinearLayout emptyrow = new LinearLayout(this);
                emptyrow.setLayoutParams(new LinearLayout.LayoutParams(tileWidth/8, tileWidth/8));
                layout.addView(emptyrow);
            }

            //Add row to board
            layout.addView(row);
        }

        //Activating the pieces at their start positions
        updatePieces();


    }

    /**
    * All actions that happens when any tile is pressed is controlled here, flowchart looks like this:
    *
    *                                                                     / yes-> move piece and update values and change current turnholder
    *                                      / yes -> Is it a possible move - no -> deselect previously selected piece
    * OnClick->Is any other tile selected?
    *                                        \ no -> Is there a piece of current turnholders color here? - yes -> show possible moves
    *                                                                                               \ no -> do nothing
    * */
    private void onPressedTile(int selectedTile){
        //Is there a piece selected already?
        if(prevSelectedTile >= 0){
            if(tiles[prevSelectedTile].color.equals("white")){tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(WHITE_TILE));}         //reset color on tile
            else{tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(BLACK_TILE));}          //reset color on tile

            //check if a possible move is clicked and then proceed to update game values for that move
            for (int move : moves){
                if(selectedTile == move){
                    if(getPieceIDAt(move) >= 0){                                //check if move is to an occupied tile
                        pieces.get(getPieceIDAt(move)).currentPosition = -10;       //delete any piece that stands at tile moved to
                    }
                    pieces.get(getPieceIDAt(prevSelectedTile)).currentPosition=move;        //Update the moved pieces currentPosition
                    updatePieces();
                    if(currentTurnColor.equals("white")){currentTurnColor="black";}
                    else{currentTurnColor="white";}
                }
            }



            //Reset textures for all possible moves
            for(Integer move : moves){
                if(tiles[move].color.equals("white")){tiles[move].button.setBackground(getResources().getDrawable(WHITE_TILE));}
                else if(tiles[move].color.equals("black")){tiles[move].button.setBackground(getResources().getDrawable(BLACK_TILE));}
            }

            prevSelectedTile = -1;    //reset value on prevselectedtile
        }
        //There is not a prevSelect tile
        else{
            //Is this an non-empty tile that belongs to current player
            if(getPieceIDAt(selectedTile)>=0 && pieces.get(getPieceIDAt(selectedTile)).color.equals(currentTurnColor)){
                prevSelectedTile = selectedTile;                    //set this tile to previous selected tile
                tiles[selectedTile].button.setBackgroundColor(getResources().getColor(R.color.selectedTile));             //Highlight tile

                moves = pieces.get(getPieceIDAt(selectedTile)).getMoves(pieces, amountOfTiles);

                for(Integer move : moves){
                    tiles[move].button.setBackgroundColor(getResources().getColor(R.color.highlightedTile));         //Highlight possible moves
                }
            }
            else{
                return;
            }
        }

    }


    /**
    * Get the id of a piece at position
    * @param tileID on board
    * */
    private int getPieceIDAt(int tileID){
        for (int i = 0; i < pieces.size(); i++){
            if(pieces.get(i).currentPosition == tileID){
                return i;
            }
        }

        return -1;
    }


    /**
     * Updating all the textures after the pieces[] arrays currentpos and each piece type/color values
     * */
    private void updatePieces() {
        //TODO hitta ett snyggt sätt att kombinera piece.color och piece.type och sedan hämta kombinationen istället för att ha 1000 cases
        //TODO gör det här ordentligt någon gång istället för att reset:a ALLA tiles

        //clearing all tiles
        for (Tiles tile : tiles){
            tile.button.setImageDrawable(getResources().getDrawable(R.drawable.blank));
        }

        System.out.println("CHECKPOINT 1");
        //Updating all tiles with current piece-values
        for (int i = 0; i < pieces.size(); i++) {
            System.out.println("CHECKPOINT 2: " + i + "/" + pieces.size());

            if (pieces.get(i).color.equals("black") && pieces.get(i).currentPosition >=0) {
                System.out.println("CHECKPOINT 3");

                switch (pieces.get(i).type) {
                    case "pawn":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_farmer));
                        break;
                    case "bishop":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_runner));
                        break;
                    case "knight":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_knight));
                        break;
                    case "rook":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_tower));
                        break;
                    case "queen":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_queen));
                        break;
                    case "king":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_king));
                        break;
                    default:
                        break;
                }
            } else if (pieces.get(i).color.equals("white") && pieces.get(i).currentPosition >=0){
                System.out.println("CHECKPOINT 3");

                switch (pieces.get(i).type) {
                    case "pawn":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_farmer));
                        break;
                    case "bishop":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_runner));
                        break;
                    case "knight":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_knight));
                        break;
                    case "rook":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_tower));
                        break;
                    case "queen":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_queen));
                        break;
                    case "king":
                        tiles[pieces.get(i).currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_king));
                        break;
                    default:
                        break;
                }
            }
        }
        
    }


    /**
     * Load in gameinfo based on GameObject fetched from server
     * */
    private void loadInGame(GameObject game){

        //Load in names and quotes
        if(game.getPlayerOne().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            opponentName = game.getPlayerTwo();
            opponentQuote = game.getPlayerTwoQuote();
        }
        else {
            opponentName = game.getPlayerOne();
            opponentQuote = game.getPlayerOneQuote();
        }

        //Set the quote
        TextView quoteView = (TextView) findViewById(R.id.playerQuote);
        quoteView.setText(opponentQuote);


        //Add all the pieces into their positions
        pieces = new ArrayList<Piece>();
        for(int i = 0; i < amountOfTiles*2; i+=2){
            switch (game.getPiecePositions().substring(i, i+2)){
                case "00":
                break;

                case "WB":
                    pieces.add(new Piece("pawn", "white", i/2));
                    break;

                case "WT":
                    pieces.add(new Piece("rook", "white", i/2));
                    break;

                case "WH":
                    pieces.add(new Piece("knight", "white", i/2));
                    break;

                case "WL":
                    pieces.add(new Piece("bishop", "white", i/2));
                    break;
                case "WD":
                    pieces.add(new Piece("queen", "white", i/2));
                    break;
                case "WK":
                    pieces.add(new Piece("king", "white", i/2));
                    break;
                case "BB":
                    pieces.add(new Piece("pawn", "black", i/2));
                    break;

                case "BT":
                    pieces.add(new Piece("rook", "black", i/2));
                    break;

                case "BH":
                    pieces.add(new Piece("knight", "black", i/2));
                    break;

                case "BL":
                    pieces.add(new Piece("bishop", "black", i/2));
                    break;
                case "BD":
                    pieces.add(new Piece("queen", "black", i/2));
                    break;
                case "BK":
                    pieces.add(new Piece("king", "black", i/2));
                    break;

                    default:
                        pieces.add(new Piece("king", "black", i/2));
            }
        }


    }

    private void pushGame(){

    }

}


