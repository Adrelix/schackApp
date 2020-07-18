package com.example.adam.schackapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.text.*;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    ProfileObject profile;
    String playerName;
    String playerQuote;


    DatabaseReference databaseProfiles;
    DatabaseReference databaseGames;

    ArrayList <GameObject> GameList = new ArrayList<GameObject>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Load information from database
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        databaseGames = FirebaseDatabase.getInstance().getReference("games");
        playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");
        playerQuote = profile.getQuote();


        getGames();





    }



    public void getGames(){

        //whenever a query with this valueEventListener is called it runs the sequence below
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                GameObject fetchedGame;
                for(DataSnapshot dbsnap : dataSnapshot.getChildren()){
                    fetchedGame = dbsnap.getValue(GameObject.class);        //Assert the fetched data to a profile object
                    System.out.println("FOUND FETCHED GAME WITH ID: " + fetchedGame.getGameID());

                    //Clear previous entries of game if it has been added before, this system works but isn't great.
                    for(int i = 0; i < GameList.size(); i++){
                        if(GameList.get(i).getGameID().equals(fetchedGame.getGameID())){
                            GameList.remove(i);
                        }
                    }
                    GameList.add(fetchedGame);
                }

                String[] playerTitle = new String[GameList.size()];
                String[] subMessage = new String[GameList.size()];
                Integer[] imgArray = new Integer[GameList.size()];

                //Add each game in the GameList to the information that goes into the adapter
                System.out.println("CREATING GAME LIST");
                for(int i = 0; i < GameList.size(); i++){

                    //Change the info to make sense dependent if player is playerOne or playerTwo
                    //If player = playerOne TODO snygga till det här smh
                    if(playerName.equals(GameList.get(i).getPlayerTwo())){
                        playerTitle[i] = GameList.get(i).getPlayerOne();
                        if(GameList.get(i).getGameStatus() == 2){
                            subMessage[i] = "Your turn!";
                        }
                        else {
                            subMessage[i] = "Waiting for their move...";
                        }
                    }
                    //If player = playerTwo
                    else{playerTitle[i] = GameList.get(i).getPlayerTwo();
                        if(GameList.get(i).getGameStatus() == 1){
                            subMessage[i] = "Your turn!";
                        }
                        else {
                            subMessage[i] = "Waiting for their move...";
                        }
                    }

                    if(GameList.get(i).getGameStatus() == 0){
                        subMessage[i] = "Game is over";
                    }
                    //TODO implement lol
                    imgArray[i] = R.drawable.empty_profile_image;
                }


                //TODO sort GameList based on lastmove here


                //Takes in the list of desired attributes, sends them to an adapter and then puts them in a list, creating the current games list
                GameItemListAdapter adapter=new GameItemListAdapter(MainActivity.this, playerTitle, subMessage, imgArray);
                ListView list=(ListView) findViewById(R.id.game_list);
                list.setAdapter(adapter);                                                                           //Adds all loaded in gamelistitems
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {                                 //what happens after an item is pressed
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startGame(view, GameList.get(position));
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };


        Query query = FirebaseDatabase.getInstance().getReference("games").orderByChild("playerOne").equalTo(playerName);      //Fetch all games where either playerone or playertwo is currentplayer
        Query query2 = FirebaseDatabase.getInstance().getReference("games").orderByChild("playerTwo").equalTo(playerName);

        System.out.println("ATTEMPTING TO FETCH GAMES");
        query.addValueEventListener(valueEventListener);
        query2.addValueEventListener(valueEventListener);

    }


    /**
     * Opens up a new activity where user can search and challenge opponents
     * */
    public void newGame(View view){
        Intent intent = new Intent(MainActivity.this, NewGameSettings.class);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);

    }

    /**
     * Starts an existing game
     * */
    public void startGame(View view, GameObject game){
        Intent intent = new Intent(MainActivity.this, GameBoard.class);
        intent.putExtra("gameToLoad", game);
        startActivity(intent);
    }

    public void profile(View view){
        //TODO
    }

    public void rules(View view){
        //TODO
    }


    /*
     * Sort the current GameList after lastMoveDate
     * *
    private ArrayList<GameObject> sortGamesAfterDate(ArrayList<GameObject> GameList){
        SimpleDateFormat sdformat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        ArrayList<GameObject> sortedList = new ArrayList<GameObject>();

        try{
            while(GameList.size()>0){
                Date dateMin= sdformat.parse(GameList.get(0).getLastMoveDate());
                int indexOfMin = 0;

                for(int k = 0; k < GameList.size(); k++){
                    Date dateAtIndex = sdformat.parse(GameList.get(k).getLastMoveDate());
                    if(dateMin.compareTo(dateAtIndex) > 0){
                        dateMin = dateAtIndex;
                        indexOfMin = k;
                    }
                }
                sortedList.add(GameList.get(indexOfMin));
                GameList.remove(indexOfMin);

            }
        }
        catch (ParseException err){
            err.printStackTrace();
        }

        return sortedList;
    }

    */


}
