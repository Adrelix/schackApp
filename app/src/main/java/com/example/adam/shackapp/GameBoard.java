package com.example.adam.shackapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GameBoard extends AppCompatActivity {
    private int amountOfBoards = 3;
    private int amountOfTiles = amountOfBoards * 64;
    Tiles[] tiles;
    Piece[] pieces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tiles = new Tiles[amountOfTiles];



        GridLayout boardLayout=(GridLayout) findViewById(R.id.board);
        GridLayout pieceLayout=(GridLayout) findViewById(R.id.board_pieces);
        boardLayout.setColumnCount(8);
        boardLayout.setRowCount(8);
        pieceLayout.setColumnCount(8);
        pieceLayout.setRowCount(8);


        // Create all the buttons
        for(int i=0;i<64;i++){

            //initiate the button
            final ImageButton button= new ImageButton(this);

            button.setId(i);
            button.setScaleType(ImageView.ScaleType.FIT_XY);

            //Some fkn woodoo magic to create the grid
            GridLayout.LayoutParams param= new GridLayout.LayoutParams(GridLayout.spec(
                    GridLayout.UNDEFINED,GridLayout.FILL,1f),
                    GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f));
            param.height = 0;
            param.width  = 0;
            button.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setLayoutParams(param);




            //initiate the button function inside the loop
            final ImageButton b=button;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //add the button to the grid
            pieceLayout.addView(button);
        }

    }



    private void initializeBoard(){
        //placing pieces at starting positions
        pieces = new Piece[32];
        pieces[0] = new Piece("torn", "vit", 0);
        pieces[1] = new Piece("hast", "vit", 1);
        pieces[2] = new Piece("lopare", "vit", 2);
        pieces[3] = new Piece("drottning", "vit", 3);
        pieces[4] = new Piece("kung", "vit", 4);
        pieces[5] = new Piece("lopare", "vit", 5);
        pieces[6] = new Piece("hast", "vit", 6);
        pieces[7] = new Piece("torn", "vit", 7);
        pieces[8] = new Piece("bonde", "vit", 8);
        pieces[9] = new Piece("bonde", "vit", 9);
        pieces[10] = new Piece("bonde", "vit", 10);
        pieces[11] = new Piece("bonde", "vit", 11);
        pieces[12] = new Piece("bonde", "vit", 12);
        pieces[13] = new Piece("bonde", "vit", 13);
        pieces[14] = new Piece("bonde", "vit", 14);
        pieces[15] = new Piece("bonde", "vit", 15);
        pieces[16] = new Piece("torn", "svart", amountOfTiles-1);
        pieces[17] = new Piece("hast", "svart", amountOfTiles-2);
        pieces[18] = new Piece("lopare", "svart", amountOfTiles-3);
        pieces[19] = new Piece("drottning", "svart", amountOfTiles-4);
        pieces[20] = new Piece("kung", "svart", amountOfTiles-5);
        pieces[21] = new Piece("lopare", "svart", amountOfTiles-6);
        pieces[22] = new Piece("hast", "svart", amountOfTiles-7);
        pieces[23] = new Piece("torn", "svart", amountOfTiles-8);
        pieces[24] = new Piece("bonde", "svart", amountOfTiles-9);
        pieces[25] = new Piece("bonde", "svart", amountOfTiles-10);
        pieces[26] = new Piece("bonde", "svart", amountOfTiles-11);
        pieces[27] = new Piece("bonde", "svart", amountOfTiles-12);
        pieces[28] = new Piece("bonde", "svart", amountOfTiles-13);
        pieces[29] = new Piece("bonde", "svart", amountOfTiles-14);
        pieces[30] = new Piece("bonde", "svart", amountOfTiles-15);
        pieces[31] = new Piece("bonde", "svart", amountOfTiles-16);

        //Setting default start pos tiles to occupied
        for (int i = 0; i < 16; i++){
            tiles[i].isOccupied = true;
            tiles[amountOfTiles - (1 + i)].isOccupied = true;
        }


    }

    }

