package com.example.adam.schackapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {
    ProfileObject profile;
    String playerName;
    String playerQuote;

    String currentSelectedList;
    Button yourTurnButton;
    Button opponentTurnButton;
    Button finishedGameButton;
    Button plusButton;

    DatabaseReference databaseProfiles;
    DatabaseReference databaseGames;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    ArrayList <GameObject> GameList = new ArrayList<GameObject>();
    ArrayList<Integer> indexList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Load information from database
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        databaseGames = FirebaseDatabase.getInstance().getReference("games");
        playerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        profile = (ProfileObject) getIntent().getSerializableExtra("profileToLoad");


        //Get last used settings and restore
        sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
        currentSelectedList = sharedPreferences.getString("currentSelectedList", "yourTurn");

        getGames();


        //Initiating middle navbar and setting the last pressed button to be activated
        yourTurnButton = (Button) findViewById(R.id.midNavBarActiveGames);
        opponentTurnButton = (Button) findViewById(R.id.midNavBarOpponentGames);
        finishedGameButton = (Button) findViewById(R.id.midNavBarDoneGames);
        plusButton = (Button) findViewById(R.id.plusButton);
        switch (currentSelectedList){
            case "yourTurn":
                yourTurnButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
                break;
            case "opponentTurn":
                opponentTurnButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
                break;
            case "finishedGame":
                finishedGameButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
                break;
        }

        //Activate the middle navbar and the functions of it
        yourTurnButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                currentSelectedList = "yourTurn";
                sharedPrefEditor.putString("currentSelectedList", currentSelectedList).apply();
                yourTurnButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
                opponentTurnButton.setForeground(null);
                finishedGameButton.setForeground(null);
               // plusButton.setVisibility(View.VISIBLE);
                updateGameList();
            }
        });
        opponentTurnButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                currentSelectedList = "opponentTurn";
                sharedPrefEditor.putString("currentSelectedList", currentSelectedList).apply();
                yourTurnButton.setForeground(null);
                opponentTurnButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
                finishedGameButton.setForeground(null);
               // plusButton.setVisibility(View.GONE);
                updateGameList();
            }
        });
        finishedGameButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                currentSelectedList = "finishedGame";
                sharedPrefEditor.putString("currentSelectedList", currentSelectedList).apply();
                yourTurnButton.setForeground(null);
                opponentTurnButton.setForeground(null);
                finishedGameButton.setForeground(getResources().getDrawable(R.drawable.active_button_background));
              //  plusButton.setVisibility(View.GONE);
                updateGameList();
            }
        });

    }


    /**
     * Fetch users games from database and place them in to GameList. Uses realtime database so games are always up to date
     * */
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
                            //TODO implement an actual working notification system
                            /*if(playerName.equals(fetchedGame.getPlayerOne()) && fetchedGame.getGameStatus() == 1){
                                notification(fetchedGame.getPlayerTwo() + " has made their move!");
                            }
                            else if(playerName.equals(fetchedGame.getPlayerTwo()) && fetchedGame.getGameStatus() == 2) {
                                notification(fetchedGame.getPlayerOne() + " has made their move!");
                            }       */
                        }
                    }
                    GameList.add(fetchedGame);
                }

                updateGameList();

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);

    }


    /**
     * Updates the displayed gamelist based on currently selected option
     * and games in database
     * */
    public void updateGameList(){

        indexList = new ArrayList<>();
        Collections.sort(GameList);

        //Sort the list into sublist dependent on currentSelectedList
        for(Integer i = 0; i < GameList.size(); i++){

            switch (currentSelectedList){
                case "yourTurn":
                    if(GameList.get(i).getGameStatus() == 1 && playerName.equals(GameList.get(i).getPlayerOne())){
                        indexList.add(i);
                    }
                    else if(GameList.get(i).getGameStatus() == 2 && playerName.equals(GameList.get(i).getPlayerTwo())){
                        indexList.add(i);
                    }
                    break;

                case "opponentTurn":
                    if(GameList.get(i).getGameStatus() == 2 && playerName.equals(GameList.get(i).getPlayerOne())){
                        indexList.add(i);
                    }
                    else if(GameList.get(i).getGameStatus() == 1 && playerName.equals(GameList.get(i).getPlayerTwo())){
                        indexList.add(i);
                    }
                    break;

                case "finishedGame":
                    if(GameList.get(i).getGameStatus() == 0 ){
                        indexList.add(i);
                    }
                    break;
            }
        }




        String[] playerTitle = new String[indexList.size()];
        String[] subMessage = new String[indexList.size()];
        Integer[] imgArray = new Integer[indexList.size()];

        //Add each game in the GameList to the information that goes into the adapter
        for(int i = 0; i < indexList.size(); i++){

            //Change the info to make sense dependent if player is playerOne or playerTwo
            //If player = playerOne else player = playerTwo
            if(playerName.equals(GameList.get(indexList.get(i)).getPlayerTwo())){
                playerTitle[i] = GameList.get(indexList.get(i)).getPlayerOne();
            } else{
                playerTitle[i] = GameList.get(indexList.get(i)).getPlayerTwo();
            }

            subMessage[i] = "20" + GameList.get(indexList.get(i)).getLastMoveDate();
            //TODO implement lol
            imgArray[i] = R.drawable.empty_profile_image;
        }


        //Takes in the list of desired attributes, sends them to an adapter and then puts them in a list, creating the current games list
        GameItemListAdapter adapter=new GameItemListAdapter(MainActivity.this, playerTitle, subMessage, imgArray);
        ListView list=(ListView) findViewById(R.id.game_list);
        list.setAdapter(adapter);                                                                           //Adds all loaded in gamelistitems
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {                                 //what happens after an item is pressed
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startGame(view, GameList.get(indexList.get(position)));
            }
        });


    }


    /**
     * Starts an existing game
     * */
    public void startGame(View view, GameObject game){
        Intent intent = new Intent(MainActivity.this, GameBoard.class);
        intent.putExtra("gameToLoad", game);
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

    public void profile(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION );
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }

    public void rules(View view){
        //Send the information to the next activity
        Intent intent = new Intent(getApplicationContext(), RulesPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION );
        intent.putExtra("profileToLoad", profile);
        startActivity(intent);
    }


    /**
     * A pretty simple notification function, sends a message to the user but only works if app is running or currently in background
     * TODO replace with a server that checks whenever a move is made and if so sends a push notification
     * */
   /* public void notification(String message){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.white_knight)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(message)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setContentText("It's your turn!");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());





/*
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.white_knight)
                .setTicker("Hearty365")
                .setContentTitle("Default notification")
                .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build()); /asterix här/
    }*/

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
