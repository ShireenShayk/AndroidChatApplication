package com.example.myapplication5;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication5.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

//username auth check new
import com.google.firebase.auth.*;

import java.util.Objects;

//Log in
public class LoginActivity extends AppCompatActivity {

    EditText loginUsername;
    Button loginButton;
    TextView signupRedirectText;

//    HelperClass userModel;

    //username auth check new
//    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

//        if (mAuth != null) {
//            System.out.println("Auth test" + mAuth.getCurrentUser());
//        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername()) {
                    loginUsername.setError("Username cannot be empty");

                } else {
                    System.out.println("Clicked login");
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }


    public void checkUser() {

        //get user entered username from UI
        String userUsername = loginUsername.getText().toString().trim();

        System.out.println("Usercheck in progress-" + userUsername);

        //connect with db to check if entered username exists in db or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    loginUsername.setError(null);
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    System.out.println("Logged in user data from db-" + usernameFromDB);
                    if (usernameFromDB.equals(userUsername)) {
                        loginUsername.setError(null);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        startActivity(intent);
                    }

                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}