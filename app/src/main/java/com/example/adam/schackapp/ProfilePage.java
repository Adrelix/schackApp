package com.example.adam.schackapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfilePage extends AppCompatActivity {
    ProfileObject profile;
    String playerName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page_layout);

        playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");
        TextView gamesPlayedAnswer = findViewById(R.id.gamesPlayerAnswer);
        TextView gamesWonAnswer = findViewById(R.id.gamesWonAnswer);
        TextView gamesLostAnswer = findViewById(R.id.gamesLostAnswer);
        TextView winRateAnswer = findViewById(R.id.winrateAnswer);
        TextView playerQuote = findViewById(R.id.playerQuote);

        gamesPlayedAnswer.setText(profile.getAmountOfGames()+ "");
        gamesWonAnswer.setText(profile.getAmountOfWins()+ "");
        gamesLostAnswer.setText(profile.getAmountOfGames()-profile.getAmountOfWins() + "");
        String winRate = "" + (double) profile.getAmountOfWins()/ (double) profile.getAmountOfGames();
        winRateAnswer.setText(winRate);
        playerQuote.setText(profile.getQuote());




    }

    public void signOut(View view){

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.remove("storedPassword").commit();
        sharedPrefEditor.remove("storedEmail").commit();
        FirebaseAuth.getInstance().signOut();
        Intent newIntent = new Intent(this, LoginScreen.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
        finish();

    }

}
