package com.example.modernworkshopapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernworkshopapp.Prevalent.prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

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
        else if(checker.equals("checked")){
            uploadImage();
        }


    }

    private void uploadImage() {

        if(imageUri!=null){
            final StorageReference fileRef=storageProfileImageRef
                    .child(LoginActivity.EncodeEmail(prevalent.currentOnlineUser.getEmail()) + ".jpg");

            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            });

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
