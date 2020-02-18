package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.modernworkshopapp.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserViewNewOrders extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.user_new_order_list);

        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, UserViewNewOrders.UserOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, UserOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                holder.userName.setText("Name : " + model.getName());
                holder.userEmail.setText("Email : " + model.getEmail());

                holder.userDateTime.setText("Date and Time : " + model.getDate() + "  " + model.getTime());

            }

            @NonNull
            @Override
            public UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent,false);
                return new UserOrdersViewHolder(view);
            }
        };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userEmail, userTotalPrice, userDateTime;
        public Button showOrderButton;

        public UserOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.order_user_name);
            userEmail=itemView.findViewById(R.id.order_email_address);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            showOrderButton=itemView.findViewById(R.id.show_order_button);

        }
    }



}