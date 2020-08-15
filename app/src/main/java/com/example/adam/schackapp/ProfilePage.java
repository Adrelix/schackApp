package com.example.adam.schackapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfilePage extends AppCompatActivity {
    ProfileObject profile;
    String playerName;
    int imageHeight;


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
        TextView playerNameView = findViewById(R.id.playerTitle);

        gamesPlayedAnswer.setText(profile.getAmountOfGames()+ "");
        gamesWonAnswer.setText(profile.getAmountOfWins()+ "");
        gamesLostAnswer.setText(profile.getAmountOfGames()-profile.getAmountOfWins() + "");
        String winRate = "" + (double) profile.getAmountOfWins()/ (double) profile.getAmountOfGames();
        winRateAnswer.setText(winRate);
        playerQuote.setText("\"" + profile.getQuote() + "\"");
        playerNameView.setText(playerName);


        //method to resize profile image
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int imageWidth = displayMetrics.widthPixels/3;
        ImageView profileImageView = findViewById(R.id.profile_image);
        profileImageView.getLayoutParams().height = imageWidth;
        profileImageView.getLayoutParams().width = imageWidth;
        profileImageView.requestLayout();



        //method to round profile image
        final CardView image_card = findViewById(R.id.midNavBarCard);
        ViewTreeObserver viewTreeObserver = image_card.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int viewHeight = image_card.getHeight();
                    if (viewHeight != 0) {
                        image_card.setRadius(viewHeight/2);
                        image_card.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }





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

    public void mainPage(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

    public void rules(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), RulesPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

}
