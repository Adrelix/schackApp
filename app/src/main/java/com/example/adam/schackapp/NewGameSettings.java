package com.example.adam.schackapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewGameSettings extends AppCompatActivity {
    private DatabaseReference databaseProfiles;
    private DatabaseReference databaseGames;

    private ProfileObject profile;

    String playerName;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game_settings_layout);


        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");

        playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        //Set the window dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));


        //Get the database information from firebase
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        databaseGames = FirebaseDatabase.getInstance().getReference("games");


    }



    public void startNewGame(View view){
        EditText opponentNameET = (EditText) findViewById(R.id.opponent_edit_text);
        final String opponentName = opponentNameET.getText().toString().trim();

        //whenever a query with this valueEventListener is called it runs the sequence below
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){                          //Check that the user exist in the database
                    ProfileObject opponent = null;
                    for(DataSnapshot dbsnap : dataSnapshot.getChildren()){ //TODO atm it read through the whole list even tho it's a single element list, should be fixed
                        opponent = dbsnap.getValue(ProfileObject.class);        //Assert the fetched data to a profile object
                    }
                    Toast.makeText(NewGameSettings.this, "Found user: " + opponent.getName(), Toast.LENGTH_SHORT).show();

                    //TODO Create game with both users
                    String createdID = databaseGames.push().getKey();           //Get next item ID
                    GameObject newGame = new GameObject(createdID, playerName, opponentName, profile.getQuote(),
                            opponent.getQuote(), currentDate, 3);
                    databaseGames.child(createdID).setValue(newGame);                                     //Add Profile to database
                    finish();

                }
                else{       //If user couldn't be found
                    Toast.makeText(NewGameSettings.this, "Couldn't find user: " + opponentName, Toast.LENGTH_SHORT).show();
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        Query query = FirebaseDatabase.getInstance().getReference("profiles").orderByChild("name").equalTo(opponentName);      //Fetch all profiles that match the attemptedname

        query.addValueEventListener(valueEventListener);

    }




    public void cancel(View view){
        finish();
    }


}



