package com.example.modernworkshopapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminViewOrderActivity extends AppCompatActivity {

    String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_order);

        userID=getIntent().getStringExtra("UID");



    }
}
