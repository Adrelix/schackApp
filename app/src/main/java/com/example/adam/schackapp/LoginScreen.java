package com.example.adam.schackapp;


import android.content.Intent;
import android.content.SharedPreferences;
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

    FirebaseDatabase database;
    DatabaseReference databaseProfiles;
    ValueEventListener valueEventListener;
    FirebaseAuth mAuth;
    ProfileObject userProfile;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);

        database = FirebaseDatabase.getInstance();
        databaseProfiles = database.getReference("profiles");
        mAuth = FirebaseAuth.getInstance();


        //Store user information in sharedpref
        sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
        String storedEmail = sharedPreferences.getString("storedEmail", "");
        String storedPassword = sharedPreferences.getString("storedPassword", "");
        EditText writtenEmail = (EditText) findViewById(R.id.profile_name);
        EditText writtenPw = (EditText) findViewById(R.id.profile_password);
        writtenEmail.setText(storedEmail);
        writtenPw.setText(storedPassword);
        login(storedEmail, storedPassword);

        Button loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
               loginButton();
            }
        });
        Button registerBtn = (Button) findViewById(R.id.register_new);
        registerBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                register();
            }
        });
    }





    private void loginButton(){
        final EditText writtenEmail = (EditText) findViewById(R.id.profile_name);
        final String AttemptedEmail = writtenEmail.getText().toString().trim();
        final EditText writtenPw = (EditText) findViewById(R.id.profile_password);
        final String AttemptedPassword = writtenPw.getText().toString().trim();

        login(AttemptedEmail, AttemptedPassword);
    }
    /*
    * Manages the login attempt
    * TODO investigate authorized user etc firebase stuff and maybe make login with FB-account instead
    * TODO fix fetch and control of data
    * */
    private void login(final String attemptedEmail, final String attemptedPassword){
        final EditText writtenEmail = (EditText) findViewById(R.id.profile_name);
        final EditText writtenPw = (EditText) findViewById(R.id.profile_password);



        if(!TextUtils.isEmpty(attemptedEmail) && !TextUtils.isEmpty(attemptedPassword)) {

            //Use Firebase Authentication to sign in user
            mAuth.signInWithEmailAndPassword(attemptedEmail, attemptedPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Welcome back\n" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();


                        //Get profile information from database
                        valueEventListener = new ValueEventListener() {   //whenever a query with this valueEventListener is called it runs the sequence below
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){                          //Check that the user exist in the database
                                    for(DataSnapshot dbsnap : dataSnapshot.getChildren()){ //TODO atm it read through the whole list even tho it's a single element list, should be fixed
                                        userProfile = dbsnap.getValue(ProfileObject.class);        //Assert the fetched data to a profile object
                                    }
                                    sharedPrefEditor.putString("storedEmail", attemptedEmail);
                                    sharedPrefEditor.putString("storedPassword", attemptedPassword);          //TODO encrypt this or smth
                                    sharedPrefEditor.commit();



                                    //Send the information to the next activity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("profileToLoad", userProfile);
                                    database.getReference().child("profiles").removeEventListener(valueEventListener);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}

                        };
                        Query query = FirebaseDatabase.getInstance().getReference("profiles").orderByChild("email").equalTo(attemptedEmail);      //Fetch all profiles that match the attemptedEmail
                        query.addValueEventListener(valueEventListener);





                    } else {
                        writtenEmail.setText("");
                        writtenPw.setText("");
                        sharedPrefEditor.remove("storedPassword");
                        sharedPrefEditor.remove("storedEmail");
                        sharedPrefEditor.commit();
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Register new user
    private void register(){
        startActivity(new Intent(LoginScreen.this, RegisterNewProfile.class));
    }


}


