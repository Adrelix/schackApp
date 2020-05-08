package com.example.adam.schackapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String[] playertitle ={                                           //sample users here, should be replace with data fetched from database
            "Adrelix","GajicNation",                                //the view that this is represented in should also be changed from a listview
            "Alex189650","Hjerpan",                                 //to probably trying to add listitems directly to the linear layout to make the logo and
            "Meow Meow", "Deadpool",                                //options bar scroll with the list
            "Midas","BowGirl69",
            "Thanos", "Mumbo Jumbo",
    };

    String[] subtitle ={
            "Your turn!","Waiting for their move...",
            "Your turn!","Waiting for their move...",
            "Waiting for their move...", "Your turn!","Waiting for their move...",
            "Your turn!","Waiting for their move...",
            "Waiting for their move...",
    };
    Integer[] imgid={
            R.drawable.empty_profile_image,R.drawable.empty_profile_image,
            R.drawable.empty_profile_image,R.drawable.empty_profile_image,
            R.drawable.empty_profile_image, R.drawable.empty_profile_image,R.drawable.empty_profile_image,
            R.drawable.empty_profile_image,R.drawable.empty_profile_image,
            R.drawable.empty_profile_image,
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Remove the navBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        //Takes in the list of desired attributes, sends them to an adapter and then puts them in a list, creating the current games list
        GameItemListAdapter adapter=new GameItemListAdapter(this, playertitle, subtitle, imgid);
        ListView list=(ListView) findViewById(R.id.game_list);
        list.setAdapter(adapter);                                                                           //Adds all loaded in gamelistitems
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {                                 //what happens after an item is pressed
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(MainActivity.this, GameBoard.class);
                intent.putExtra("playerName", playertitle[position]);

                    startGame(view, intent);
            }
        });



    }



    public void startGame(View view, Intent intent){
        startActivity(intent);
    }
}
