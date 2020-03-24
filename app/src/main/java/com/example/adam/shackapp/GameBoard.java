package com.example.adam.shackapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collections;


public class GameBoard extends AppCompatActivity {
    private int amountOfBoards = 3;
    private int amountOfTiles = amountOfBoards * 64;
    Tiles[] tiles;
    Piece[] pieces;
    int[] moves;
    String currentTurnColor = "white";          //indicates whos turn it is, default white
    int prevSelectedTile = -1;                  //shows the index of previously selected tiles, neg number for none selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_layout);

        tiles = new Tiles[amountOfTiles];

        //Get display width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlay);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"

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
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < 8; j++) {
                final int currTile = (i *8) + j;    //the tile index
                ImageButton btnTag = new ImageButton(this);

                //setting the measurements for each tile
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width/8, width/8);
                btnTag.setLayoutParams(params);

                //applying textures to the tiles
                if(tiles[currTile].color.equals("white")){btnTag.setBackground(getResources().getDrawable(R.drawable.ic_launcher_foreground));}
                else if(tiles[currTile].color.equals("black")){btnTag.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background));}

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
                        Toast toast = Toast.makeText(getApplicationContext(), ("" + tiles[currTile].isOccupied), Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });
            }

            //Check if row should be empty row (to separate boards)
            if(i%8 == 0){
                LinearLayout emptyrow = new LinearLayout(this);
                emptyrow.setLayoutParams(new LinearLayout.LayoutParams(width/8, width/8));
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
        if(tiles[prevSelectedTile].color.equals("white")){tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(R.drawable.ic_launcher_foreground));}         //reset color on tile
        else{tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background));}          //reset color on tile

        //TODO check if a possible move is clicked and then proceed to update game values for that move


        //Reset textures for all possible moves
        for(int move : moves){
            if(tiles[move].color.equals("white")){tiles[move].button.setBackground(getResources().getDrawable(R.drawable.ic_launcher_foreground));}
            else if(tiles[move].color.equals("black")){tiles[move].button.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background));}
        }

        prevSelectedTile = -1;          //reset value on prevselectedtile
    }
    else{
        if(tiles[selectedTile].isOccupied && pieces[getPieceIDAt(selectedTile)].color.equals(currentTurnColor)){             //Is this an non-empty tile that belongs to current player
            prevSelectedTile = selectedTile;                    //set this tile to previous selected tile
            tiles[selectedTile].button.setBackgroundColor(Color.parseColor("#FFC0CB"));             //Highlight tile

            moves = pieces[getPieceIDAt(selectedTile)].getMoves();

            for(int move : moves){
                tiles[move].button.setBackgroundColor(Color.parseColor("#add8e6"));
            }
            //TODO highlight possible moves
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
        pieces[0] = new Piece("torn", "white", 0);
        pieces[1] = new Piece("hast", "white", 1);
        pieces[2] = new Piece("lopare", "white", 2);
        pieces[3] = new Piece("drottning", "white", 3);
        pieces[4] = new Piece("kung", "white", 4);
        pieces[5] = new Piece("lopare", "white", 5);
        pieces[6] = new Piece("hast", "white", 6);
        pieces[7] = new Piece("torn", "white", 7);
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
        //TODO gör det här ordentligt någon gång istället för att reset:a alla tiles

        //clearing all tiles
        for (Tiles tile : tiles){
            tile.button.setImageDrawable(getResources().getDrawable(R.drawable.blank));
        }

        //Updating all tiles with current piece-values
        for (Piece piece : pieces) {
            if (piece.color.equals("black")) {
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
            } else {
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

}


