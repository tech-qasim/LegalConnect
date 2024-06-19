package com.example.blacksuits.Screens;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.Fragments.EditProfile;
import com.example.blacksuits.Fragments.OnProfilePicChangeListener;
import com.example.blacksuits.Fragments.ProfilePicViewModel;
import com.example.blacksuits.Notifications.Token;
import com.example.blacksuits.R;
import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Advocate extends AppCompatActivity implements RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener, OnProfilePicChangeListener {
    RecyclerViewAdapterForUserAndLawyerScreen adapter;
    private FirebaseAuth auth;
    private MySharedPreferences mySharedPreferences;

    private FirebaseFirestore db;

    private ArrayList<String> clientUsernames;

    private DatabaseReference reference;

    private ArrayList<ClientInformation> usernames = new ArrayList<>();

    private FirebaseDatabase database;

    com.google.android.material.textfield.TextInputEditText searchBar;

    private boolean isEditTextEnabled = true;

    private ProfilePicViewModel viewModel;

    @Override
    protected void onResume() {
        super.onResume();
        ImageView profilePicture = findViewById(R.id.profile_picture_lawyer);

        getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Uri uri = task.getResult();
                        setProfilePic(this,uri,profilePicture);
                    }

                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advocate);

        usernames.clear();

         clientUsernames = new ArrayList<>();
         searchBar = findViewById(R.id.searchBar);
        mySharedPreferences = new MySharedPreferences(this);
        db = FirebaseFirestore.getInstance();

        viewModel = new ViewModelProvider(this).get(ProfilePicViewModel.class);



        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chats").child("Information for unread messages");

        ImageView profilePicture = findViewById(R.id.profile_picture_lawyer);


        buildRecyclerView();
        displayDataFromRealTimeDatabase();
         searchingMechanism();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Toast.makeText(Advocate.this, "Toast is missing", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
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


        getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Uri uri = task.getResult();
                        setProfilePic(this,uri,profilePicture);
                    }

                });




        // Assuming TextViewViewModel extends ViewModel and getData() returns LiveData<String>


        viewModel.getData().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri newData) {
                setProfilePic(getApplicationContext(), newData, profilePicture);
            }
        });









        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new EditProfile());
            }
        });

        auth = FirebaseAuth.getInstance();

        ImageView logOutButton = findViewById(R.id.button_log_out);

        logOutButton.setOnClickListener(v -> logoutUser());


    }

    @Override
    public void onItemClick(View view, int position) {
    }

    private void logoutUser() {
        auth.signOut();

        mySharedPreferences.removeKey(MySharedPreferences.KEY_EMAIL);
        mySharedPreferences.removeKey(MySharedPreferences.KEY_USER_TYPE);

        mySharedPreferences.setLoggedIn(false);

        Intent intent = new Intent(Advocate.this, IdentifyYourselfScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void displayDataFromRealTimeDatabase()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    for (DataSnapshot childSnapshot : snapshot.getChildren())
                    {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator =
                                new GenericTypeIndicator<Map<String, Object>>() {
                                };

                        Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);

                        if (data != null && data.containsKey("receiverUid") && data.get("receiverUid") != null &&
                                data.get("receiverUid").equals(mySharedPreferences.getUserID()))  {
                            if (Integer.parseInt(data.get("count").toString()) > 0
                            ) {

                                ArrayList<String> clients = new ArrayList<>();
                                for (ClientInformation client : usernames)
                                {
                                    clients.add(client.getClientUsername());
                                }

                                if (!clients.contains(data.get("sender username").toString())) {


//                                    usernames.add(new ClientInformation(data.get("sender username").toString(),data.get("senderUid").toString(), "", ""));
//                                    adapter.notifyDataSetChanged();
//
//
//                                                    adapter.notifyDataSetChanged();
                                    CollectionReference advocatesCollections = db.collection("users");
                                    advocatesCollections.whereEqualTo("username", data.get("sender username")).get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                  String profilePicture = documentSnapshot.getString("profilePictureSrc");



                                                    usernames.add(new ClientInformation(data.get("sender username").toString(),data.get("senderUid").toString(), profilePicture, ""));


                                                    adapter.notifyDataSetChanged();

//                                        DataManager.getInstance().setDataForHomeScreen(userId);



                                                } else {
                                                    // Handle the case where no documents match the query
                                                    Toast.makeText(Advocate.this, "User not found", Toast.LENGTH_SHORT).show();
                                                }

                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle any failures in fetching the data
                                                Log.e("Firestore", "Error getting user document", e);
                                                Toast.makeText(Advocate.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                                            });





                                }




                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFragment (Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment with the new one
        fragmentTransaction.replace(R.id.fragmentContainerLawyer, fragment);

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void buildRecyclerView ()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapterForUserAndLawyerScreen(this.getLayoutInflater(), usernames, "display unread texts", true, "Null");
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    private void searchingMechanism()
    {
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // Change the end drawable to the cross icon when focused
                    searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_close_24, 0);
                } else {
                    // Change the end drawable back to the search icon when not focused
                    searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                }
            }
        });

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               searchBar.setFocusable(true);
                searchBar.setEnabled(true);
                searchBar.setFocusable(true);
                searchBar.setFocusableInTouchMode(true);

                searchBar.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndClickArea = searchBar.getRight() - searchBar.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableEndClickArea) {
                        // Perform your action when the drawableEnd is clicked

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        searchBar.setFocusable(false);

                        return true;
                    }
                }
                return false;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filterByName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static StorageReference getCurrentProfilePicStorageRef(String uid)
    {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onProfilePicChange(Uri uri) {

    }

//    @Override
//    public void onProfilePicChange(Uri uri) {
//
//        Toast.makeText(this, "changing profile pic on activity", Toast.LENGTH_SHORT).show();
//
//        ImageView profilePicture = findViewById(R.id.profile_picture_lawyer);
//
//        setProfilePic(getApplicationContext(),uri,profilePicture);
//    }
}