package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernworkshopapp.Model.Cart;
import com.example.modernworkshopapp.Prevalent.prevalent;
import com.example.modernworkshopapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserCartList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button placeOrderButton;
    private TextView emptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart_list);


        recyclerView=findViewById(R.id.cart_list_view);
        emptyListMessage=findViewById(R.id.empty_cart_list_msg);

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        placeOrderButton=findViewById(R.id.place_order_button);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });

    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference ordersRef=FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("name", prevalent.currentOnlineUser.getName());
        ordersMap.put("email", prevalent.currentOnlineUser.getEmail());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not viewed");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //first empty the cartList
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserCartList.this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent=new Intent(UserCartList.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                        finish();

                                    }
                                }
                            });
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfCartListEmpty();

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options=
            new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(prevalent.currentOnlineUser.getPhone()).child("WishList"), Cart.class)
                    .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                cartViewHolder.quantity.setText("Quantity : " + cart.getQuantity());
                Picasso.get().load(cart.getImage()).into(cartViewHolder.orderImage);

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Delete"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(UserCartList.this);
                        builder.setTitle("Cart Options : ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    //Implement code to change number of items
                                    //tutorial - 25
                                }
                                else if(i==1){
                                   cartListRef.child("User View")
                                           .child(prevalent.currentOnlineUser.getPhone())
                                           .child("WishList")
                                           .child(cart.getPid())
                                           .removeValue()
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()){

                                                       cartListRef.child("Admin View")
                                                               .child(prevalent.currentOnlineUser.getPhone())
                                                               .child("WishList")
                                                               .child(cart.getPid())
                                                               .removeValue()
                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if(task.isSuccessful()){
                                                                           Toast.makeText(UserCartList.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                       else{
                                                                           Toast.makeText(UserCartList.this, "Network error, Try again", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }
                                                               });

                                                   }
                                                   else{
                                                       Toast.makeText(UserCartList.this, "Network error, Try again", Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                                }
                            }
                        });

                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    private void checkIfCartListEmpty(){
        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("User View")
                .child(prevalent.currentOnlineUser.getPhone())
                .child("WishList");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    emptyListMessage.setVisibility(View.VISIBLE);
                    placeOrderButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}


