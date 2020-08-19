package com.example.adam.schackapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;





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
    GameObject game;
    int gameAtStart;
    Query query;

    String opponentName;
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
    String playerColor;          //indicates whos turn it is, default white
    int prevSelectedTile = -1;                  //shows the index of previously selected tiles, neg number for none selected
    boolean moveDisabled = false;

    ProfileObject profile;

    FirebaseDatabase database;
    DatabaseReference databaseProfiles;
    DatabaseReference databaseGames;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_layout);
        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");


        //make the statusbar(maybe?) and bottom (android) navbar black
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }



        //Load information from intent
        game = (GameObject) getIntent().getSerializableExtra("gameToLoad");
        gameAtStart = game.hashCode();
        loadInGame();

        keepGameUpdated();


        TextView opponentNameView = findViewById(R.id.playerName);
        opponentNameView.setText(opponentName);


        //Load information from database
        database = FirebaseDatabase.getInstance();
        databaseProfiles = database.getReference("profiles");
        databaseGames = database.getReference("games");



        tiles = new Tiles[amountOfTiles];
        //Here we assert the resource used for the black and white tile
        BLACK_TILE = R.drawable.black_tile3;
        WHITE_TILE = R.drawable.white_tile3;

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
                tiles[currTile].button.setAdjustViewBounds(true);
                tiles[currTile].button.setScaleType(ImageView.ScaleType.CENTER_CROP);


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
        //Return if it is the opponents turn
        if((game.getPlayerOne().equals(opponentName) && game.getGameStatus() == 1 )|| (game.getPlayerTwo().equals(opponentName) && game.getGameStatus() == 2) || moveDisabled || game.getGameStatus() == 0){
            System.out.println("CAN'T PRESS TILE, moveDisabled: " + moveDisabled + " GameStatus: " + game.getGameStatus());
            return;
        }


        //Is there a piece selected already?
        if(prevSelectedTile >= 0){
            if(tiles[prevSelectedTile].color.equals("white")){tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(WHITE_TILE));}         //reset color on tile
            else{tiles[prevSelectedTile].button.setBackground(getResources().getDrawable(BLACK_TILE));}          //reset color on tile

            //check if a possible move is clicked and then proceed to update game values for that move
            for (int move : moves){
                if(selectedTile == move) {
                        if (getPieceIDAt(move) >= 0) {                                //check if move is to an occupied tile
                            pieces.get(getPieceIDAt(move)).currentPosition = -10;       //delete any piece that stands at tile moved to
                        }

                        pieces.get(getPieceIDAt(prevSelectedTile)).currentPosition = move;        //Update the moved pieces currentPosition
                    if (!checkPawnPromotion(move)) {
                        changeTurn();
                    }
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
            if(getPieceIDAt(selectedTile)>=0 && pieces.get(getPieceIDAt(selectedTile)).color.equals(playerColor)){
                prevSelectedTile = selectedTile;                    //set this tile to previous selected tile
                tiles[selectedTile].button.setBackgroundColor(getResources().getColor(R.color.selectedTile));             //Highlight tile

                moves = pieces.get(getPieceIDAt(selectedTile)).getMoves(pieces, amountOfTiles, -1);

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

        //Updating all tiles with current piece-values
        for (int i = 0; i < pieces.size(); i++) {

            if (pieces.get(i).color.equals("black") && pieces.get(i).currentPosition >=0) {

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
     * Check if opponent is checked and can't do any other move, if so sets game.gamestatus to 0 and game is over
     * TODO test this
     * */
    private void checkIfCheckMate(){
        for(Piece piece : pieces){
            if((piece.color.equals("white") && game.getGameStatus()==1) || piece.color.equals("black") && game.getGameStatus()==2){
                Integer[] moveOptions = piece.getMoves(pieces, amountOfTiles, -1);

                for (Integer move : moveOptions){
                    System.out.println("NO CHECKMATE, FOUND MOVE FOR PIECE AT POSITION: " + piece.currentPosition + " OF TYPE: " + piece.type + " TO POSITION: " + move);
                    return;
                }
            }
        }
        //TODO Increment both players amountOfGames and increase winners amountOfWins here
       game.setGameStatus(0);
    }

    private boolean checkPawnPromotion(final int move){
        System.out.println("CHECKING PAWN PROMOTION");
        if((move < 8 && pieces.get(getPieceIDAt(move)).type.equals("pawn") &&
                pieces.get(getPieceIDAt(move)).color.equals("black")) ||
                move >= amountOfTiles-8 && pieces.get(getPieceIDAt(move)).type.equals("pawn") &&
                        pieces.get(getPieceIDAt(move)).color.equals("white") ){

            System.out.println("INSIDE PAWN PROMOTION");
            moveDisabled = true;

            final LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
            ImageButton rookButton = (ImageButton) findViewById(R.id.rook_promotion);
            ImageButton knightButton = (ImageButton) findViewById(R.id.knight_promotion);
            ImageButton bishopButton = (ImageButton) findViewById(R.id.bishop_promotion);
            ImageButton queenButton = (ImageButton) findViewById(R.id.queen_promotion);

            if(pieces.get(getPieceIDAt(move)).color.equals("white")){
                rookButton.setImageDrawable(getResources().getDrawable(R.drawable.white_tower));
                knightButton.setImageDrawable(getResources().getDrawable(R.drawable.white_knight));
                bishopButton.setImageDrawable(getResources().getDrawable(R.drawable.white_runner));
                queenButton.setImageDrawable(getResources().getDrawable(R.drawable.white_queen));
            }
            else if(pieces.get(getPieceIDAt(move)).color.equals("black")){
                rookButton.setImageDrawable(getResources().getDrawable(R.drawable.black_tower));
                knightButton.setImageDrawable(getResources().getDrawable(R.drawable.black_knight));
                bishopButton.setImageDrawable(getResources().getDrawable(R.drawable.black_runner));
                queenButton.setImageDrawable(getResources().getDrawable(R.drawable.black_queen));
            }
            buttonLayout.setVisibility(View.VISIBLE);


            rookButton.setOnClickListener(new View.OnClickListener()   {
                public void onClick(View v)  {
                    pieces.get(getPieceIDAt(move)).type = "rook";
                    buttonLayout.setVisibility(View.GONE);
                    moveDisabled = false;
                    changeTurn();
                }
            });
            knightButton.setOnClickListener(new View.OnClickListener()   {
                public void onClick(View v)  {
                    pieces.get(getPieceIDAt(move)).type = "knight";
                    buttonLayout.setVisibility(View.GONE);
                    moveDisabled = false;
                    changeTurn();
                }
            });
            bishopButton.setOnClickListener(new View.OnClickListener()   {
                public void onClick(View v)  {
                    pieces.get(getPieceIDAt(move)).type = "bishop";
                    buttonLayout.setVisibility(View.GONE);
                    moveDisabled = false;
                    changeTurn();
                }
            });
            queenButton.setOnClickListener(new View.OnClickListener()   {
                public void onClick(View v)  {
                    pieces.get(getPieceIDAt(move)).type = "queen";
                    buttonLayout.setVisibility(View.GONE);
                    moveDisabled = false;
                    changeTurn();
                }
            });
        }
        else {
            return false;
        }
        return true;
    }


    /***/
    private void changeTurn(){
        updatePieces();


        //**********************************//
        //THIS IS WHERE THE TURN IS CHANGED
        //**********************************//
        game.changeTurn();
        checkIfCheckMate();
        pushGame(game);
    }


    /**
     * Load in gameinfo based on GameObject fetched from server
     * */
    private void loadInGame(){

        //Load in names and quotes
        if(game.getPlayerOne().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            opponentName = game.getPlayerTwo();
            opponentQuote = "\"" + game.getPlayerTwoQuote() + "\"";
            playerColor = "white";
        }
        else {
            opponentName = game.getPlayerOne();
            opponentQuote = "\"" + game.getPlayerOneQuote() + "\"";
            playerColor = "black";
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


    private void keepGameUpdated(){
        //whenever a query with this valueEventListener is called it runs the sequence below
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                GameObject fetchedGame;
                for(DataSnapshot dbsnap : dataSnapshot.getChildren()){
                    fetchedGame = dbsnap.getValue(GameObject.class);        //Assert the fetched data to a profile object
                    game.setPiecePositions(fetchedGame.getPiecePositions());
                    game.setGameStatus(fetchedGame.getGameStatus());
                    game.setLastPiecePositions(fetchedGame.getLastPiecePositions());
                    game.setLastMoveDate(fetchedGame.getLastMoveDate());
                    game.setRoundNumb(fetchedGame.getRoundNumb());

                    loadInGame();
                    updatePieces();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        System.out.println("ATTEMPTING TO FETCH GAME WITH ID: " + game.getGameID());
        query = FirebaseDatabase.getInstance().getReference("games").orderByChild("gameID").equalTo(game.getGameID());
        query.addValueEventListener(valueEventListener);

    }


    /**
     * Check if game has been updated and if so updates database
     * */
    private void pushGame(GameObject game){
        StringBuilder pushStringBuilder = new StringBuilder();

        //Start by filling out the whole string with 00:s
        for(int i = 0; i < amountOfTiles; i++){
            pushStringBuilder.append("00");
        }

        //Replace the 00:s at positions with pieces to the right value
        for(int i = 0; i < pieces.size(); i++) {
            if(pieces.get(i).currentPosition<0){
                continue;
            }

            //Change the first letter to B/W dependent on color
            switch (pieces.get(i).color){
                case "white":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2, 'W');
                    break;
                case "black":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2, 'B');
                    break;
            }
            //Change the first letter to B/T/H/L/D/K dependent on type
            switch (pieces.get(i).type){
                case "pawn":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'B');
                    break;
                case "rook":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'T');
                    break;
                case "knight":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'H');
                    break;
                case "bishop":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'L');
                    break;
                case "queen":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'D');
                    break;
                case "king":
                    pushStringBuilder.setCharAt(pieces.get(i).currentPosition*2+1, 'K');
                    break;
            }
        }



        String pushString = pushStringBuilder.toString();
        game.setLastPiecePositions(game.getPiecePositions());
        game.setPiecePositions(pushString);
        String currentDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        game.setLastMoveDate(currentDate);


        if(gameAtStart == game.hashCode()){
            databaseGames.child(game.getGameID()).setValue(game);
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        database.getReference().child("games").removeEventListener(valueEventListener);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);

    }

}