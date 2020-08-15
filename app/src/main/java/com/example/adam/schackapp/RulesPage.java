package com.example.adam.schackapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RulesPage extends AppCompatActivity {
    ProfileObject profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_layout);
        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");


    }



    public void mainPage(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

    public void profilePage(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

}
