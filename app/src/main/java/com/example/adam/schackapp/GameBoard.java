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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;





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

    //Custom info
    int WHITE_TILE;
    int BLACK_TILE;

    //game values
    private int amountOfBoards = 3;
    private int amountOfTiles = amountOfBoards * 64;
    Tiles[] tiles;
    Piece[] pieces;
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
        opponentNameView.setText(game.getPlayerOne());


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
        initializeBoardPieces();
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
    if(prevSelectedTile >= 0){                          //Is there a piece selected already?
        if(tiles[prevSelectedTile].color.equals("white")){tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(WHITE_TILE));}         //reset color on tile
        else{tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(BLACK_TILE));}          //reset color on tile

        //check if a possible move is clicked and then proceed to update game values for that move
        for (int move : moves){
            if(selectedTile == move){
                if(getPieceIDAt(move) >= 0){                                //check if move is to an occupied tile
                    pieces[getPieceIDAt(move)].currentPosition = -10;       //delete any piece that stands at tile moved to
                }
                pieces[getPieceIDAt(prevSelectedTile)].currentPosition=move;        //Update the moved pieces currentPosition
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
        if(getPieceIDAt(selectedTile)>=0 && pieces[getPieceIDAt(selectedTile)].color.equals(currentTurnColor)){
            prevSelectedTile = selectedTile;                    //set this tile to previous selected tile
            tiles[selectedTile].button.setBackgroundColor(getResources().getColor(R.color.selectedTile));             //Highlight tile

            moves = pieces[getPieceIDAt(selectedTile)].getMoves(pieces, amountOfTiles);

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
        for (int i = 0; i < pieces.length; i++){
            if(pieces[i].currentPosition == tileID){
                return i;
            }
        }

        return -1;
    }


    /**
    * Activate the starting positions and create each piece
    * */
    private void initializeBoardPieces(){
        //placing pieces at starting positions
        pieces = new Piece[32];
        pieces[0] = new Piece("torn", "white", 0+72);
        pieces[1] = new Piece("hast", "white", 1+72);
        pieces[2] = new Piece("lopare", "white", 2+72);
        pieces[3] = new Piece("drottning", "white", 3+72);
        pieces[4] = new Piece("kung", "white", 4+72);
        pieces[5] = new Piece("lopare", "white", 5+72);
        pieces[6] = new Piece("hast", "white", 6+72);
        pieces[7] = new Piece("torn", "white", 7+72+8);
        pieces[8] = new Piece("bonde", "white", 8);
        pieces[9] = new Piece("bonde", "white", 9);
        pieces[10] = new Piece("bonde", "white", 10);
        pieces[11] = new Piece("bonde", "white", 11);
        pieces[12] = new Piece("bonde", "white", 12);
        pieces[13] = new Piece("bonde", "white", 13);
        pieces[14] = new Piece("bonde", "white", 14);
        pieces[15] = new Piece("bonde", "white", 15);
        pieces[16] = new Piece("torn", "black", amountOfTiles-1);
        pieces[17] = new Piece("hast", "black", amountOfTiles-2);
        pieces[18] = new Piece("lopare", "black", amountOfTiles-3);
        pieces[20] = new Piece("kung", "black", amountOfTiles-4);
        pieces[19] = new Piece("drottning", "black", amountOfTiles-5);
        pieces[21] = new Piece("lopare", "black", amountOfTiles-6);
        pieces[22] = new Piece("hast", "black", amountOfTiles-7);
        pieces[23] = new Piece("torn", "black", amountOfTiles-8);
        pieces[24] = new Piece("bonde", "black", amountOfTiles-9);
        pieces[25] = new Piece("bonde", "black", amountOfTiles-10);
        pieces[26] = new Piece("bonde", "black", amountOfTiles-11);
        pieces[27] = new Piece("bonde", "black", amountOfTiles-12);
        pieces[28] = new Piece("bonde", "black", amountOfTiles-13);
        pieces[29] = new Piece("bonde", "black", amountOfTiles-14);
        pieces[30] = new Piece("bonde", "black", amountOfTiles-15);
        pieces[31] = new Piece("bonde", "black", amountOfTiles-16);

        //Setting default start pos tiles to occupied
        for (int i = 0; i < 16; i++){
            tiles[i].isOccupied = true;
            tiles[amountOfTiles - (1 + i)].isOccupied = true;
        }


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

        //Updating all tiles with current piece-values
        for (Piece piece : pieces) {
            if (piece.color.equals("black") && piece.currentPosition >=0) {
                switch (piece.type) {
                    case "bonde":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_farmer));
                        break;
                    case "lopare":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_runner));
                        break;
                    case "hast":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_knight));
                        break;
                    case "torn":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_tower));
                        break;
                    case "drottning":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_queen));
                        break;
                    case "kung":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.black_king));
                        break;
                }
            } else if (piece.color.equals("white") && piece.currentPosition >=0){
                switch (piece.type) {
                    case "bonde":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_farmer));
                        break;
                    case "lopare":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_runner));
                        break;
                    case "hast":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_knight));
                        break;
                    case "torn":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_tower));
                        break;
                    case "drottning":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_queen));
                        break;
                    case "kung":
                        tiles[piece.currentPosition].button.setImageDrawable(getResources().getDrawable(R.drawable.white_king));
                        break;
                }
            }
        }

    }


    private void loadInGame(GameObject game){
        


    }

    private void pushGame(){

    }

}


