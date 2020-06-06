package com.example.adam.schackapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewProfile extends AppCompatActivity {

    DatabaseReference databaseProfiles;
    private String createdID;
    private String attemptedName;
    private String attemptedPw;
    private String attemptedPw2;
    private String attemptedEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_profile_layout);

        //fetch the profile database
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");

        Button createBtn = (Button) findViewById(R.id.create_new);
        createBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {


                EditText nameET = (EditText) findViewById(R.id.profile_name);               //Read all the input values and assign them to variables
                EditText pwET = (EditText) findViewById(R.id.profile_password);
                EditText pw2ET = (EditText) findViewById(R.id.repeat_password);
                EditText emailET = (EditText) findViewById(R.id.profile_email);
                attemptedName = nameET.getText().toString().trim();
                attemptedPw = pwET.getText().toString().trim();
                attemptedPw2 = pw2ET.getText().toString().trim();
                attemptedEmail = emailET.getText().toString().trim();




                if(checkIfProfileAvailable()){                                         //Check if user already exist
                    if(attemptedPw.equals(attemptedPw2)){                               //Checks that passwords match
                        createProfile();
                    }
                    else{ Toast.makeText(RegisterNewProfile.this, "Passwords must match", Toast.LENGTH_SHORT).show();}
                }
                else{
                    Toast.makeText(RegisterNewProfile.this, "The profile name already exist", Toast.LENGTH_LONG).show();
                }
            }
        });


    }



    private void createProfile(){

        createdID = databaseProfiles.push().getKey();           //Get next item ID
        Profile newProfile = new Profile(createdID, attemptedName, attemptedPw, attemptedEmail);    //Create profile object
        databaseProfiles.child(createdID).setValue(newProfile);                                     //Add Profile to database
        Toast.makeText(RegisterNewProfile.this, "New profile: " + attemptedName + " created", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginScreen.class));             //go back to login page
    }

    private boolean checkIfProfileAvailable(){
        return true;
    }
}

