package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.modernworkshopapp.Model.Admins;
import com.example.modernworkshopapp.Model.Users;
import com.example.modernworkshopapp.Prevalent.prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressBar progressBar;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        joinNowButton = findViewById(R.id.main_join_now_btn);
        loginButton = findViewById(R.id.main_login_btn);
        progressBar = findViewById(R.id.main_progress_bar);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String userEmailKey = Paper.book().read(prevalent.userEmailKey);
        String userPassKey = Paper.book().read(prevalent.userPasswordKey);
        String individual = Paper.book().read(prevalent.individual);

        if (userEmailKey != "" && userPassKey != "") {
            if (!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPassKey)) {
                AllowAccess(userEmailKey, userPassKey, individual);
            }
        }

    }

    private void AllowAccess(final String email, final String password, final String individual) {

        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        final String encodedEmail = LoginActivity.EncodeEmail(email);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentDbName=individual;
                if (dataSnapshot.child(parentDbName).child(encodedEmail).exists()) {
                    if (individual.equals("Users")) {
                        Users usersData = dataSnapshot.child(parentDbName).child(encodedEmail).getValue(Users.class);

                        if (usersData.getEmail().equals(email) && usersData.getPassword().equals(password)) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                            finish();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            prevalent.currentOnlineUser = usersData;
                            startActivity(intent);

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Credentials are Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else if (individual.equals("Admins")) {
                        Admins adminsData = dataSnapshot.child(parentDbName).child(encodedEmail).getValue(Admins.class);

                        if (adminsData.getEmail().equals(email) && adminsData.getPassword().equals(password)) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                            finish();

                            Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                            prevalent.currentOnlineAdmin = adminsData;
                            startActivity(intent);

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Credentials are Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Account with email " + email + " does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}
