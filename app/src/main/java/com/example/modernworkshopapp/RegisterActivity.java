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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword, inputPhone;
    private Button createAccountButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName=findViewById(R.id.username_input);
        inputEmail=findViewById(R.id.register_email_input);
        inputPassword=findViewById(R.id.register_password_input);
        inputPhone=findViewById(R.id.register_phone_input);
        createAccountButton=findViewById(R.id.register_btn);
        progressBar=findViewById(R.id.register_progress_bar);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });


    }

    private void createAccount() {
        String name=inputName.getText().toString().trim();
        String email=inputEmail.getText().toString().trim();
        String phone=inputPhone.getText().toString().trim();
        String password=inputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else if(phone.length() != 10){
            Toast.makeText(RegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
        }
        else {

            if(email.substring(email.length()-10).equals("iitr.ac.in")) {
                String encodedEmail=EncodeEmail(email);
                validateEmail(name, email, encodedEmail, phone, password);
            }
            else{
                Toast.makeText(RegisterActivity.this, "Enter your IITR email", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private String EncodeEmail(String email) {
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

    private void validateEmail(final String name, final String email, final String encodedEmail, final String phone, final String password) {

        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(encodedEmail).exists()){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("email", email);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);
                    userDataMap.put("phone", phone);

                    rootRef.child("Users").child(encodedEmail).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created", Toast.LENGTH_SHORT).show();

                                        finish();

                                        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Network error, Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "email " + email + " already exists", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
