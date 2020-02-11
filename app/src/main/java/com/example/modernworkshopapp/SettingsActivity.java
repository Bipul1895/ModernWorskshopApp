package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernworkshopapp.Prevalent.prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText nameEditText, emailEditText, phoneEditText;
    private TextView updateTextButton, closeTextButton, profileChangeTextButton;

    private Uri imageUri;
    private  String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfileImageRef;
    private String checker="";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView=findViewById(R.id.settings_profile_image);
        nameEditText=findViewById(R.id.settings_name_et);
        emailEditText=findViewById(R.id.settings_email_et);
        phoneEditText=findViewById(R.id.settings_phone_et);
        updateTextButton=findViewById(R.id.update_settings_tv);
        closeTextButton=findViewById(R.id.close_settings_tv);
        profileChangeTextButton=findViewById(R.id.profile_image_change_tv);

        storageProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");

        progressBar=findViewById(R.id.settings_progress_bar);

        userInfoDisplay(profileImageView, nameEditText, emailEditText);

        closeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else{
                    updateOnlyUserInfo();
                }

            }
        });

        profileChangeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            profileImageView.setImageURI(imageUri);

        }
        else{
            Toast.makeText(SettingsActivity.this, "Error, try again",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap=new HashMap<>();
        userMap.put("name", nameEditText.getText().toString());
        userMap.put("email", emailEditText.getText().toString());
        userMap.put("phone", phoneEditText.getText().toString());
        ref.child(LoginActivity.EncodeEmail(prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);

        prevalent.currentOnlineUser.setName(nameEditText.getText().toString());
        prevalent.currentOnlineUser.setEmail(emailEditText.getText().toString());
        prevalent.currentOnlineUser.setPhone(phoneEditText.getText().toString());

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

        Toast.makeText(SettingsActivity.this, "Profile Info updated successfully", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void userInfoSaved() {

        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this, "Name is mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(emailEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this, "Email is mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this, "Phone is mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }



    }

    private void uploadImage() {

        progressBar.setVisibility(View.VISIBLE);

        if(imageUri!=null){
            final StorageReference fileRef=storageProfileImageRef
                    .child(LoginActivity.EncodeEmail(prevalent.currentOnlineUser.getEmail()) + ".jpg");

            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this, "Sorry, an error occured", Toast.LENGTH_SHORT).show();
                        throw  task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap=new HashMap<>();
                        userMap.put("name", nameEditText.getText().toString());
                        userMap.put("email", emailEditText.getText().toString());
                        userMap.put("phone", phoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(LoginActivity.EncodeEmail(prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);

                        prevalent.currentOnlineUser.setName(nameEditText.getText().toString());
                        prevalent.currentOnlineUser.setEmail(emailEditText.getText().toString());
                        prevalent.currentOnlineUser.setPhone(phoneEditText.getText().toString());
                        prevalent.currentOnlineUser.setImage(myUrl);

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(SettingsActivity.this, "Profile Info updated successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

                        finish();

                    }
                    else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(SettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText nameEditText, final EditText emailEditText) {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.EncodeEmail(prevalent.currentOnlineUser.getEmail()));

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        nameEditText.setText(name);
                        emailEditText.setText(email);
                        phoneEditText.setText(phone);

                    }
                    else{
                        String name=dataSnapshot.child("name").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();

                        nameEditText.setText(name);
                        emailEditText.setText(email);
                        phoneEditText.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
