package com.example.adam.schackapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewProfile extends AppCompatActivity {

    private DatabaseReference databaseProfiles;
    private FirebaseAuth mAuth;

    private String createdID;
    private String attemptedName;
    private String attemptedPw;
    private String attemptedPw2;
    private String attemptedEmail;
    private String quote;
    EditText nameET;
    EditText pwET;
    EditText pw2ET;
    EditText emailET;
    EditText quoteET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_profile_layout);

        //fetch the profile database
        databaseProfiles = FirebaseDatabase.getInstance().getReference("profiles");
        mAuth = FirebaseAuth.getInstance();

        Button createBtn = (Button) findViewById(R.id.create_new);
        createBtn.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {


                nameET = (EditText) findViewById(R.id.profile_name);               //Read all the input values and assign them to variables
                pwET = (EditText) findViewById(R.id.profile_password);
                pw2ET = (EditText) findViewById(R.id.repeat_password);
                emailET = (EditText) findViewById(R.id.profile_email);
                quoteET = (EditText) findViewById(R.id.profile_quote);

                attemptedName = nameET.getText().toString().trim();
                attemptedPw = pwET.getText().toString().trim();
                attemptedPw2 = pw2ET.getText().toString().trim();
                attemptedEmail = emailET.getText().toString().trim();
                quote = quoteET.getText().toString().trim();



                createProfile();


            }
        });


    }



    private void createProfile(){

        //Checks if the username is available
        if(!checkIfProfileAvailable()){
            Toast.makeText(RegisterNewProfile.this, "The profile name already exist", Toast.LENGTH_SHORT).show();
            return;
        }
        //Checks if the passwords match
        if(!attemptedPw.equals(attemptedPw2)) {
            Toast.makeText(RegisterNewProfile.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return;
        }
        //Checks if the passwords length is valid
        if(attemptedPw.length() < 6) {
            Toast.makeText(RegisterNewProfile.this, "Minimum length of password should be 6", Toast.LENGTH_SHORT).show();
            return;
        }
        //Checks email validity using regex
        if(!Patterns.EMAIL_ADDRESS.matcher(attemptedEmail).matches()){
            Toast.makeText(RegisterNewProfile.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }



        //Firebase authentication
        mAuth.createUserWithEmailAndPassword(attemptedEmail.toLowerCase(), attemptedPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(attemptedName)
                            .setPhotoUri(Uri.parse("https://winkeyecare.com/wp-content/uploads/2013/03/Empty-Profile-Picture-450x450.jpg"))
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                    }
                                }
                            });


                    //Old method to create profile, used the database and not the auth function of firebase, still kinda useful as it saves username as key and other information with it
                    createdID = databaseProfiles.push().getKey();           //Get next item ID
                    ProfileObject newProfile = new ProfileObject(createdID, attemptedName, attemptedPw, attemptedEmail, quote);    //Create profile object
                    databaseProfiles.child(createdID).setValue(newProfile);                                     //Add Profile to database


                    Toast.makeText(getApplicationContext(), "User registration successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), LoginScreen.class));             //go back to login page}
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });




    }


    //TODO Write function smh
    private boolean checkIfProfileAvailable(){
        return true;
    }
}

