package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernworkshopapp.Model.Admins;
import com.example.modernworkshopapp.Model.Users;
import com.example.modernworkshopapp.Prevalent.prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private String parentDbName="Users";
    private ProgressBar progressBar;
    private CheckBox checkBoxRemMe;
    private TextView adminLink, notAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail=findViewById(R.id.login_email_input);
        inputPassword=findViewById(R.id.login_password_input);
        loginButton=findViewById(R.id.login_btn);
        progressBar=findViewById(R.id.login_progress_bar);
        checkBoxRemMe=findViewById(R.id.remember_me_chkbox);
        adminLink=findViewById(R.id.admin_panel_link);
        notAdminLink=findViewById(R.id.not_admin_panel_link);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);

                parentDbName="Admins";

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);

                parentDbName="Users";

            }
        });

    }

    private void LoginUser() {

        progressBar.setVisibility(View.VISIBLE);

        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else{

            AllowAccessToAccount(email, password);

        }

    }

    private void AllowAccessToAccount(final String email, final String password) {

        if(checkBoxRemMe.isChecked()){
            Paper.book().write(prevalent.userEmailKey, email);
            Paper.book().write(prevalent.userPasswordKey, password);

//            prevalent.individual=parentDbName;
            Paper.book().write(prevalent.individual, parentDbName);

        }


        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();

        final String encodedEmail=EncodeEmail(email);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(encodedEmail).exists()){
                    if(parentDbName.equals("Users")) {
                        Users usersData = dataSnapshot.child(parentDbName).child(encodedEmail).getValue(Users.class);

                        if (usersData.getEmail().equals(email) && usersData.getPassword().equals(password)) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                            finish();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            prevalent.currentOnlineUser = usersData;
                            startActivity(intent);

                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Credentials are Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(parentDbName.equals("Admins")){
                        Admins adminsData = dataSnapshot.child(parentDbName).child(encodedEmail).getValue(Admins.class);

                        if (adminsData.getEmail().equals(email) && adminsData.getPassword().equals(password)) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                            finish();

                            Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            prevalent.currentOnlineAdmin = adminsData;
                            startActivity(intent);

                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Credentials are Incorrect", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Account with email " + email + " does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    public static String EncodeEmail(String email) {
        String encodedEmail="";
        for(int i=0;i<email.length();i++){
            if(email.charAt(i) == '.'){
                encodedEmail = encodedEmail + '%';
            }
            else{
                encodedEmail = encodedEmail + email.charAt(i);
            }
        }
        return encodedEmail;
    }


}
