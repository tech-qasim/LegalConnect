package com.example.blacksuits.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.R;
import com.example.blacksuits.Screens.IdentifyYourselfScreen;
import com.example.blacksuits.Screens.User;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private RecyclerView recyclerView;

    private FirebaseAuth auth;

//    private FirebaseDatabase db;

    RecyclerViewAdapterForUserAndLawyerScreen adapter;

    private MySharedPreferences mySharedPreferences;

    ArrayList<ClientInformation> usernames = new ArrayList<>();

    private DatabaseReference referenceLastMessage;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernames.clear();

        buildRecyclerView(view);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chats").child("Information for unread messages");
        mySharedPreferences = new MySharedPreferences(requireContext());
        referenceLastMessage = database.getReference("chats").child("messages");

        ImageView profilePicture = view.findViewById(R.id.profile_picture_chat_fragment);
//        ImageView toolbarTitle = view.findViewById(R.id.toolbarTitleChatFragment);
        ImageView logOutButton = view.findViewById(R.id.button_log_out_client_chat_fragment);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load(new EditProfile());
            }
        });


        if(mySharedPreferences.getImageUri()!=null) {
            ProfilePictureDataClass.setProfilePic(getContext(), mySharedPreferences.getImageUri(), profilePicture);
        }

        ProfilePictureDataClass.getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Uri uri = task.getResult();
                        ProfilePictureDataClass.setProfilePic(getContext(),uri,profilePicture);
                    }

                });


        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });




        String loggedInUserId = mySharedPreferences.getUserID();
        referenceLastMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    List<ClientInformation> uniqueUsernamesList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String input = childSnapshot.getKey();
                        String[] parts = input.split("_");

                        if (parts[0].equals(loggedInUserId)) {
                            Log.e("total children", String.valueOf(childSnapshot.getChildrenCount()));

                            if (childSnapshot.getChildrenCount() > 0) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("advocates")
                                        .whereEqualTo("id", parts[1])
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    String username = documentSnapshot.getString("username");
                                                    String profilePicture = documentSnapshot.getString("profilePictureSrc");
                                                    Log.e("checking the value of name", username);

                                                    uniqueUsernamesList.add(new ClientInformation(username, parts[1],profilePicture,""));
                                                }

                                                usernames.clear();
                                                usernames.addAll(uniqueUsernamesList);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }


    private void buildRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChatFragmentChatFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecyclerViewAdapterForUserAndLawyerScreen(this.getLayoutInflater(), usernames, "display unread texts", false, "Chat");
        adapter.setClickListener((RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener) requireContext());
        recyclerView.setAdapter(adapter);
    }


    private void load(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the existing fragment with the new one
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "fragmenttag");

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void logoutUser() {
        auth.signOut();

        mySharedPreferences.removeKey(MySharedPreferences.KEY_EMAIL);
        mySharedPreferences.removeKey(MySharedPreferences.KEY_USER_TYPE);
        mySharedPreferences.removeImageUri();

        mySharedPreferences.setLoggedIn(false);

        Intent intent = new Intent(requireContext(), IdentifyYourselfScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}