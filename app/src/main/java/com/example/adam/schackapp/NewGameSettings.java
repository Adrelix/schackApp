package com.example.adam.schackapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

public class NewGameSettings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game_settings_layout);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));

    }

    public void startNewGame(View view){
        EditText opponentNameET = (EditText) findViewById(R.id.opponent_edit_text);
        String opponentName = opponentNameET.getText().toString().trim();

    }


}



