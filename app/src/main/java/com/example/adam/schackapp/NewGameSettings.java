package com.example.adam.schackapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
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
    Boolean vsadam = false;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game_settings_layout);

        //Get current state
        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");
        playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

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
        String opName;
        Switch randomOpp = (Switch) findViewById(R.id.random_opponent);

        //TODO change to actual random opp
        if(randomOpp.isChecked()){
             opName = "Adrelix";
        }
        else {
            EditText opponentNameET = (EditText) findViewById(R.id.opponent_edit_text);
            opName = opponentNameET.getText().toString().trim();
        }

        final String opponentName = opName;
        //whenever a query with this valueEventListener is called it runs the sequence below
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){                          //Check that the user exist in the database
                    ProfileObject opponent = null;
                    for(DataSnapshot dbsnap : dataSnapshot.getChildren()){
                        opponent = dbsnap.getValue(ProfileObject.class);        //Assert the fetched data to a profile object

                    }

                    if(!playerName.equals(opponentName)){
                        Toast.makeText(NewGameSettings.this, "Found user: " + opponent.getName(), Toast.LENGTH_SHORT).show();
                        //TODO Create game with both users
                        String createdID = databaseGames.push().getKey();           //Get next item ID
                        String currentDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        GameObject newGame = new GameObject(createdID, playerName, opponentName, profile.getQuote(), opponent.getQuote(), currentDate, 3);
                        databaseGames.child(createdID).setValue(newGame);                                     //Add Profile to database
                        finish();
                    }
                    else{
                        Toast.makeText(NewGameSettings.this, "You can't start a game against yourself!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{       //If user couldn't be found
                    Toast.makeText(NewGameSettings.this, "Couldn't find user: " + opponentName, Toast.LENGTH_SHORT).show();
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        Query queryProfiles = databaseProfiles.orderByChild("name").equalTo(opponentName);      //Fetch all profiles that match the attemptedname
        queryProfiles.addValueEventListener(valueEventListener);

    }




    public void cancel(View view){
        finish();
    }


}



