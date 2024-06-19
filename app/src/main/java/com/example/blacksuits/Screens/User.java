package com.example.blacksuits.Screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.Fragments.ChatFragment;
import com.example.blacksuits.Fragments.EditProfile;
import com.example.blacksuits.Fragments.HomeFragment;
import com.example.blacksuits.Fragments.LegalAgreementFragment;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User extends AppCompatActivity implements RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener {
    RecyclerViewAdapterForUserAndLawyerScreen adapter;
    private FirebaseAuth auth;
    private MySharedPreferences mySharedPreferences;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bundle = new Bundle();

        auth = FirebaseAuth.getInstance();
        mySharedPreferences = new MySharedPreferences(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);
//        ImageView profilePicture = findViewById(R.id.profile_picture_chat_fragment);
//        ImageView logOutButton = findViewById(R.id.button_log_out_client_chat_fragment);
//        RelativeLayout toolbar = findViewById(R.id.toolbar);
//        TextView toolbarTitle = findViewById(R.id.toolbarTitleChatFragment);
        HomeFragment homeFragment = new HomeFragment();
        ChatFragment chatFragment = new ChatFragment();
        LegalAgreementFragment legalAgreementFragment = new LegalAgreementFragment();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Check if user is authenticated
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (currentUser != null) {
                            // User is authenticated, update token in database
                            String userId = currentUser.getUid();

                            FirebaseDatabase.getInstance().getReference().child("user tokens").child(userId).child("token").setValue(token);
                            FirebaseDatabase.getInstance().getReference().child("user tokens").child(userId).child("last updated").setValue(String.valueOf(new SimpleDateFormat("hh:mm a").format(new Date())));
                        }
                    }
                });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {


                if (s != null) {
                    FirebaseMessaging.getInstance().subscribeToTopic(s);
                    String uid = FirebaseAuth.getInstance().getUid();
                    if (uid != null) {
                        FirebaseDatabase.getInstance().getReference().child("user tokens").child(uid).child("token").setValue(s);
                        FirebaseDatabase.getInstance().getReference().child("user tokens").child(uid).child("last updated").setValue(String.valueOf(new SimpleDateFormat("hh:mm a").format(new Date())));
                    }
                } else {
                    // Handle the case where FCM token is null
                    Log.e("Firebase Token", "FCM token is null");
                }
            }
        });

//        toolbarTitle.setText("Chats");

        loadFragment(chatFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.chat_icon) {
//                toolbarTitle.setText("Chats");
                loadFragment(chatFragment);
                Drawable drawable = getResources().getDrawable(R.drawable.logout);
//                logOutButton.setImageDrawable(drawable);

                new Handler().postDelayed(() -> {
//                    toolbar.setVisibility(View.VISIBLE);
                }, 0);


            } else if (itemId == R.id.home_icon) {

//                toolbarTitle.setText("Home");
                loadFragment(homeFragment);
                Drawable drawable = getResources().getDrawable(R.drawable.logout);
//                logOutButton.setImageDrawable(drawable);

                new Handler().postDelayed(() -> {
//                    toolbar.setVisibility(View.VISIBLE);
                }, 0);

            } else if (itemId == R.id.legal_agreement_icon) {

//                toolbarTitle.setText("Legal Doctrine");

                new Handler().postDelayed(() -> {
//                    toolbar.setVisibility(View.VISIBLE);
                }, 0);



                loadFragment(legalAgreementFragment);

            }
            return true;
        });


//        profilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                load(new EditProfile());
//            }
//        });
//
//
//        logOutButton.setOnClickListener(v -> {
//            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flFragment);
//
//            if (currentFragment instanceof ChatFragment) {
//                logoutUser();
//            } else if (currentFragment instanceof HomeFragment) {
//                logoutUser();
//            }
//
//        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    private void logoutUser() {
        auth.signOut();

        mySharedPreferences.removeKey(MySharedPreferences.KEY_EMAIL);
        mySharedPreferences.removeKey(MySharedPreferences.KEY_USER_TYPE);

        mySharedPreferences.setLoggedIn(false);

        Intent intent = new Intent(User.this, IdentifyYourselfScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void load(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment with the new one
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "fragmenttag");

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }


    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment with the new one
        fragmentTransaction.replace(R.id.flFragment, fragment, "fragmenttag");

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();


    }

    @Override
    public void onBackPressed() {
        // Get the reference to your bottom navigation view
        super.onBackPressed();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);


        // Get the currently selected item id
        int selectedItemId = bottomNavigationView.getSelectedItemId();

        // Check if the current selected item id is not equal to the home icon id
        if (selectedItemId != R.id.home_icon) {
            // If not, select the home icon
            bottomNavigationView.setSelectedItemId(R.id.home_icon);
        } else {
            // If the home icon is already selected, finish the activity
            finish();
        }
    }

}


