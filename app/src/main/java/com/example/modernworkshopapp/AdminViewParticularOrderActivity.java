package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.modernworkshopapp.Model.Cart;
import com.example.modernworkshopapp.Prevalent.prevalent;
import com.example.modernworkshopapp.ViewHolder.AdminParticularOrderViewHolder;
import com.example.modernworkshopapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class AdminViewParticularOrderActivity extends AppCompatActivity {

    private RecyclerView productList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference adminCartListRef;
    String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_particular_order);

        userID=getIntent().getStringExtra("UID");

        productList=findViewById(R.id.admin_particular_user_order_list);
        productList.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        productList.setLayoutManager(layoutManager);

        adminCartListRef= FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userID)
                .child("WishList");


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(adminCartListRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, AdminParticularOrderViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, AdminParticularOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminParticularOrderViewHolder holder, int i, @NonNull final Cart model) {
                holder.quantity.setText("Quantity : " + model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder.orderImage);

                holder.downloadFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            download(AdminViewParticularOrderActivity.this, "filename", ".jpg", DIRECTORY_DOWNLOADS, model.getImage());
                    }
                });

            }

            @NonNull
            @Override
            public AdminParticularOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_view_paticular_layout, parent, false);
                AdminParticularOrderViewHolder holder=new AdminParticularOrderViewHolder(view);
                return holder;
            }
        };

        productList.setAdapter(adapter);
        adapter.startListening();

    }

    private void download(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }


}
