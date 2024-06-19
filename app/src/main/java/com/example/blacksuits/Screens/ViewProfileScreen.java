package com.example.blacksuits.Screens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileScreen extends AppCompatActivity {
    private MySharedPreferences mySharedPreferences;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_screen);

        mySharedPreferences = new MySharedPreferences(this);

        TextView toolbarTitle = findViewById(R.id.toolbar_title_view_profile);
        Intent intent = getIntent();
//        String message = intent.getStringExtra("EXTRA_MESSAGE");
//        toolbarTitle.setText(message);


        toolbarTitle.setText("Profile");

        String clientUsername = intent.getStringExtra("key");
        String clientID = intent.getStringExtra("key_userId");


//        Toast.makeText(this, clientUsername, Toast.LENGTH_SHORT).show();



        String username = ((MySharedPreferences) mySharedPreferences).loadUsername();
        String email = ((MySharedPreferences) mySharedPreferences).loadEmail();

        TextView tvUsername = findViewById(R.id.tvClientUsernameViewProfile);
        TextView tvEmail = findViewById(R.id.tvEmailViewProfile);
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumberViewProfile);
        TextView tvDesignation = findViewById(R.id.tvDesignationViewProfile);
        TextView tvLocation = findViewById(R.id.tvLocationViewProfile);
        ImageView profilePicture = findViewById(R.id.pfp_view_profile);

        ProfilePictureDataClass.getCurrentProfilePicStorageRef(clientID).getDownloadUrl()
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()){
                                Uri uri = task.getResult();
                                ProfilePictureDataClass.setProfilePic(this,uri,profilePicture);
                            }
                            else
                            {
                                Exception exception = task.getException();
                                if (exception != null) {
                                    // Log or handle the exception
                                    exception.printStackTrace();
                                }
                            }

                        });

//        getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Uri uri = task.getResult();
//                        setProfilePic(getContext(), uri, pfp);
//                    }
//
//                });



        db = FirebaseFirestore.getInstance();

        ImageView copyToClipboardForEmail = findViewById(R.id.iconCopyToClipboardEmail);
        copyToClipboardForEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", username);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(ViewProfileScreen.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
//
            }
        });

        ImageView backButton = findViewById(R.id.back_button_view_profile);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        ImageView iconSendEmail = findViewById(R.id.iconSendEmail);

        iconSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = tvEmail.getText().toString();
                String subject = "Hello";
                String body = "hello";

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + emailAddress));
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(intent);
            }
        });


        ImageView iconPhoneNumber = findViewById(R.id.iconPhoneCall);

        iconPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvPhoneNumber.getText().toString()));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        ImageView iconLocation = findViewById(R.id.iconLocation);

        iconLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionReference advocatesCollections = db.collection("advocates");
                advocatesCollections.whereEqualTo("username", clientUsername).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);


                            Double latitude = documentSnapshot.getDouble("latitude");
                            Double longitude = documentSnapshot.getDouble("longitude");
                            String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;

                            Uri gmmIntentUri = Uri.parse(geoUri);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }

                        });
            }
        });


        ImageView iconMessage = findViewById(R.id.iconMessage);

        iconMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileScreen.this, ChatScreen.class);
                intent.putExtra("key",clientUsername);
                intent.putExtra("key_userId",clientID);
                startActivity(intent);

            }
        });

        ImageView pfp = findViewById(R.id.pfp_view_profile);
        MySharedPreferences.UserType userType = ((MySharedPreferences) mySharedPreferences).getUserType();

        if (userType == MySharedPreferences.UserType.USER)
        {
            Drawable drawable = getResources().getDrawable(R.drawable.lawyer_icon);
            pfp.setImageDrawable(drawable);

            CollectionReference advocatesCollections = db.collection("advocates");

            advocatesCollections.whereEqualTo("username", clientUsername).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String user_name = documentSnapshot.getString("username");
                        String e_mail = documentSnapshot.getString("email");
                        String phone_number = documentSnapshot.getString("phone number");
                        String designation = documentSnapshot.getString("designation");
                        String location = documentSnapshot.getString("location");

                        tvUsername.setText(user_name);
                        tvEmail.setText(e_mail);
                        tvPhoneNumber.setText(phone_number);
                        tvDesignation.setText(designation);
                        tvLocation.setText(location);

                    });
        }
        else {

            findViewById(R.id.PhoneNumberViewProfile).setVisibility(View.GONE);
            findViewById(R.id.LocationViewProfile).setVisibility(View.GONE);
            findViewById(R.id.DesignationViewProfile).setVisibility(View.GONE);
            CollectionReference advocatesCollections = db.collection("users");
            advocatesCollections.whereEqualTo("username", clientUsername).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String e_mail = documentSnapshot.getString("email");
                        String user_name = documentSnapshot.getString("username");

                        tvEmail.setText(e_mail);
                        tvUsername.setText(user_name);

                    });
        }
    }
}
