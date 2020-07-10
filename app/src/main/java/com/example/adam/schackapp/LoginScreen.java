package com.example.adam.schackapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//TODO change the database system from Realtime Database to Cloud Firestore

public class LoginScreen extends AppCompatActivity {

    DatabaseReference databaseProfiles;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);

        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        mAuth = FirebaseAuth.getInstance();

        Button loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
               login();
            }
        });
        Button registerBtn = (Button) findViewById(R.id.register_new);
        registerBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                register();
            }
        });
    }


    /*
    * Manages the login attempt
    * TODO investigate authorized user etc firebase stuff and maybe make login with FB-account instead
    * TODO fix fetch and control of data
    * */
    private void login(){
        EditText writtenName = (EditText) findViewById(R.id.profile_name);
        final String AttemptedEmail = writtenName.getText().toString().trim();
        EditText writtenPw = (EditText) findViewById(R.id.profile_password);
        final String AttemptedPassword = writtenPw.getText().toString().trim();

        if(!TextUtils.isEmpty(AttemptedEmail) && !TextUtils.isEmpty(AttemptedPassword)) {
            mAuth.signInWithEmailAndPassword(AttemptedEmail, AttemptedPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Welcome back\n" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        /*Old method of login in*/

     /*   if(!TextUtils.isEmpty(AttemptedName) && !TextUtils.isEmpty(AttemptedPassword)){

            //whenever a query with this valueEventListener is called it runs the sequence below
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){                          //Check that the user exist in the database
                        ProfileObject fetchedProfile = null;
                        for(DataSnapshot dbsnap : dataSnapshot.getChildren()){ //TODO atm it read through the whole list even tho it's a single element list, should be fixed
                            fetchedProfile = dbsnap.getValue(Profile.class);        //Assert the fetched data to a profile object
                        }
                        if(AttemptedPassword.equals(fetchedProfile.getPassword())){     //check if PW is correct
                            Toast.makeText(LoginScreen.this, "Found user: " + AttemptedName, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginScreen.this, MainActivity.class));
                        }
                        else {  //If profile exist but pw is wrong
                            //TODO remember to remove full toast here
                            Toast.makeText(LoginScreen.this, "Wrong password for user: " + AttemptedName + "\n Attempted pw: " + AttemptedPassword + "\n Actual pw: " + fetchedProfile.getPassword(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else{       //If user couldn't be found
                        Toast.makeText(LoginScreen.this, "Couldn't find user: " + AttemptedName, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            Query query = FirebaseDatabase.getInstance().getReference("profiles").orderByChild("name").equalTo(AttemptedName);      //Fetch all profiles that match the attemptedname

            query.addValueEventListener(valueEventListener);

        }
        else{
            Toast.makeText(this, "Enter a name and pw", Toast.LENGTH_SHORT).show();
        }

                    */

    }

    //Register new user
    private void register(){
        startActivity(new Intent(LoginScreen.this, RegisterNewProfile.class));
    }


}


