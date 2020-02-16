package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.modernworkshopapp.Prevalent.prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddOrderToCart extends AppCompatActivity {

    private ImageView imageView;
    private Button chooseFileButton, addToCartButton, checkOutButton;
    private ElegantNumberButton numberButton;
    private ProgressBar progressBar;

    private static final int GalleryPick=1;
    private Uri imageUri;

    private String saveCurrentDate, saveCurrentTime, productRandomKey;

    private StorageReference orderImageRef;

    private String downloadImageUrl;

    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_to_cart);

        imageView=findViewById(R.id.order_image_view);
//        countOfOrder=findViewById(R.id.number_of_pdts_et);
        numberButton=findViewById(R.id.order_quantity_button);
        chooseFileButton=findViewById(R.id.choose_button);
        addToCartButton=findViewById(R.id.add_to_cart_button);
        checkOutButton=findViewById(R.id.go_to_cart_button);
        progressBar=findViewById(R.id.order_progress_bar);

        orderImageRef= FirebaseStorage.getInstance().getReference().child("Order images");

        orderRef= FirebaseDatabase.getInstance().getReference().child("Cart List");

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddOrderToCart.this, UserCartList.class);
                startActivity(intent);
            }
        });


    }

    private void addToCart() {


        if(imageUri==null){
            Toast.makeText(AddOrderToCart.this, "Please select your file", Toast.LENGTH_SHORT).show();
        }
        else{

            progressBar.setVisibility(View.VISIBLE);

            storeOrderInformation();

        }
    }

    private void storeOrderInformation() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath=orderImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask=filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                String message=e.toString();
                Toast.makeText(AddOrderToCart.this, "Error : " + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImageUrl=task.getResult().toString();

//                            Toast.makeText(AddOrderToCart.this, "Getting order image Url", Toast.LENGTH_SHORT).show();

                            SaveOrderInfoToDatabase();


                        }
                        else{

                            Toast.makeText(AddOrderToCart.this, "Error loading image, try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }

    private void SaveOrderInfoToDatabase() {

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("pid", productRandomKey);
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("image", downloadImageUrl);
        orderMap.put("quantity", numberButton.getNumber());

        orderRef.child(prevalent.currentOnlineUser.getPhone()).child("WishList")
                .child(productRandomKey).updateChildren(orderMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddOrderToCart.this, "Added To Cart List", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            String message=task.getException().toString();
                            Toast.makeText(AddOrderToCart.this, "Network Problem, Please try again" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void OpenGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();

            imageView.setImageURI(imageUri);

        }


    }
}
