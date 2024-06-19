package com.example.blacksuits.Adapters;



import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.Notifications.Room;
import com.example.blacksuits.R;
import com.example.blacksuits.Screens.ChatScreen;
import com.example.blacksuits.Screens.ViewProfileScreen;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterForUserAndLawyerScreen extends RecyclerView.Adapter<RecyclerViewAdapterForUserAndLawyerScreen.ViewHolder>{
    private final LayoutInflater mInflater;
    private final List<ClientInformation> mData;
    private ItemClickListener mClickListener;

    private ArrayList<ClientInformation> filteredData;


    private String calledFromWhere;

    private String fragment;

    private MySharedPreferences mySharedPreferences;


    private boolean itemCheckForRecyclerView;
    private FirebaseFirestore db;

    String userId;

    private DatabaseReference reference;

    private DatabaseReference referenceLastMessage;
    private FirebaseDatabase database;

    Room room;







    public RecyclerViewAdapterForUserAndLawyerScreen(LayoutInflater mInflater, ArrayList<ClientInformation> mData, String calledFromWhere, boolean itemCheckForRecyclerView, String fragment) {
        this.mInflater = mInflater;
        this.mData = mData;
        this.filteredData = new ArrayList<>(mData);
        this.calledFromWhere = calledFromWhere;
        this.itemCheckForRecyclerView = itemCheckForRecyclerView;
        this.fragment = fragment;
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chats").child("Information for unread messages");
        referenceLastMessage = database.getReference("chats").child("messages");
        mySharedPreferences = new MySharedPreferences(mInflater.getContext());
    }

    @NonNull
    @Override
    public RecyclerViewAdapterForUserAndLawyerScreen.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (itemCheckForRecyclerView) {
            View view = mInflater.inflate(R.layout.recycler_item_layout_client, parent, false);
            return new ViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.recycler_item_layout_lawyer, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(calledFromWhere.equals("filter the data"))
        {
            gettingFilteredData(holder, position);
        }
        else if(calledFromWhere.equals("display all data"))
        {
            gettingAllData(holder, position);
        }
        else
        {
            forChatFragment(holder, position);
        }
    }


    private void gettingLocationAndDesignationData(String clientUsername, ViewHolder holder, int position)
    {
        CollectionReference advocatesCollections = db.collection("advocates");

        advocatesCollections.whereEqualTo("username", clientUsername).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String loc = documentSnapshot.getString("location");
                        String des = documentSnapshot.getString("designation");

                        holder.lastMessage.setText(des);
                        holder.location.setText(loc);


                    } else {

                        Toast.makeText(mInflater.getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Log.e("Firestore", "Error getting user document", e);
                    Toast.makeText(mInflater.getContext(), "Error getting user data", Toast.LENGTH_SHORT).show();
                });
    }


    private void gettingFilteredData(ViewHolder holder, int position)
    {
            ClientInformation clientUsername = filteredData.get(position);
            holder.clientTextView.setText(clientUsername.getClientUsername());

        Glide.with(holder.itemView.getContext())
                .load(clientUsername.getProfilePicture())
                .error(R.drawable.lawyer_icon) // Optional error image
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePicture);


//        holder.timeStamp.setVisibility(View.GONE);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.timeStamp.getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        holder.timeStamp.setLayoutParams(params);

//        getCurrentProfilePicStorageRef(clientUsername.getUid()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful())
//                    {
//                        Uri uri = task.getResult();
//                        setProfilePic(mInflater.getContext(),uri,holder.profilePicture);
//                    }
//                    else
//                    {
////                        Toast.makeText(mInflater.getContext(), "Problem loading image", Toast.LENGTH_SHORT).show();
//                    }
//
//                });

            if (itemCheckForRecyclerView)
            {

            }
            else {
                holder.location.setText(clientUsername.getLocation());
//                holder.lastMessage.setText(clientUsername);
            }


//            gettingLocationAndDesignationData(clientUsername.getClientUsername(),holder,position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Pass the position to the interface method

                    if(itemCheckForRecyclerView)
                    {
                        retrieveUsers(holder,clientUsername);
                    }
                    else
                    {
                        retrieveAdvocates(holder,clientUsername);
                    }



                }
            });

    }

    private void forChatFragment(ViewHolder holder, int position)
    {

        ClientInformation clientUsername = mData.get(position);
        holder.clientTextView.setText(clientUsername.getClientUsername());

        Glide.with(holder.itemView.getContext())
                .load(clientUsername.getProfilePicture())
                .error(R.drawable.client_icon_for_recycler_layout)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePicture);

        if (!itemCheckForRecyclerView) {

            holder.location.setVisibility(View.INVISIBLE);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the position to the interface method

                if(itemCheckForRecyclerView)
                {
                    retrieveUsers(holder,clientUsername);
                }
                else
                {
                    retrieveAdvocates(holder,clientUsername);
                }
            }
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren())
                {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator =
                                new GenericTypeIndicator<Map<String, Object>>() {
                                };

                        Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);

                        if((data != null && data.containsKey("senderUid") && data.get("senderUid") != null && data.containsKey("receiverUid") && data.get("receiverUid") != null && data.get("receiverUid").equals(mySharedPreferences.getUserID()) && data.get("sender username").equals(clientUsername.getClientUsername())))
                        {
                            holder.lastMessage.setText(data.get("message").toString());
                            holder.timeStamp.setText(data.get("timestamp").toString());
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


//    private void forChatFragment(ViewHolder holder, int position) {
//        ClientInformation clientUsername = mData.get(position);
//        holder.clientTextView.setText(clientUsername.getClientUsername());
//
//        Glide.with(holder.itemView.getContext())
//                .load(clientUsername.getProfilePicture())
//                .error(R.drawable.client_icon_for_recycler_layout)
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.profilePicture);
//
//        if (!itemCheckForRecyclerView) {
//            holder.location.setVisibility(View.INVISIBLE);
//        }
//
//        holder.itemView.setOnClickListener(view -> {
//            if (itemCheckForRecyclerView) {
//                retrieveUsers(holder, clientUsername);
//            } else {
//                retrieveAdvocates(holder, clientUsername);
//            }
//        });
//
//        String room = mySharedPreferences.getUserID() + "_" + clientUsername.getUid();
//        Log.e("sender room", room);
//
//        referenceLastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChildren()) {
//                    DataSnapshot roomSnapshot = snapshot.child(room);
//                    if (roomSnapshot.exists()) {
//                        Query lastQuery = roomSnapshot.getRef().orderByKey().limitToLast(1);
//                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    String message = data.child("message").getValue(String.class);
//                                    String timestamp = data.child("timestamp").getValue(String.class);
//                                    updateUI(holder, message, timestamp);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.e("FirebaseError", "Error fetching last message", error.toException());
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Error fetching room data", error.toException());
//            }
//        });
//    }

    private void updateUI(ViewHolder holder, String message, String timestamp) {
        String downloadIntroURL = "https://firebasestorage.googleapis.com";
        if (message != null && message.contains(downloadIntroURL)) {
            holder.lastMessage.setText("Document");
        } else {
            holder.lastMessage.setText(message);
        }
        holder.timeStamp.setText(timestamp);
    }


    private void retrieveUsers(ViewHolder holder, ClientInformation clientUsername)
    {
//        Toast.makeText(view.getContext(), "executing recycler view", Toast.LENGTH_SHORT).show();
        CollectionReference advocatesCollections = db.collection("users");

        advocatesCollections.whereEqualTo("username", clientUsername.getClientUsername()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        userId = documentSnapshot.getString("id");



                        // Now you can use userId here or call a method that depends on it
                        // For example, you can start the ChatScreen activity here
//                                    Toast.makeText(view.getContext()," " + userId,Toast.LENGTH_SHORT).show();

//                        room = new Room(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),clientUsername.getClientUsername());

                        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
                            Intent intent = new Intent(mInflater.getContext(), ViewProfileScreen.class);
                            intent.putExtra("key", clientUsername.getClientUsername());
                            intent.putExtra("key_userId", userId);
                            mInflater.getContext().startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(mInflater.getContext(), ChatScreen.class);
                            intent.putExtra("key", clientUsername.getClientUsername());
                            intent.putExtra("key_userId", userId);
                            mInflater.getContext().startActivity(intent);
                        }



//                                        DataManager.getInstance().setDataForHomeScreen(userId);



                    } else {
                        // Handle the case where no documents match the query
                        Toast.makeText(mInflater.getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failures in fetching the data
                    Log.e("Firestore", "Error getting user document", e);
                    Toast.makeText(mInflater.getContext(), "Error getting user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveAdvocates(ViewHolder holder, ClientInformation clientUsername)
    {
//        Toast.makeText(view.getContext(), "executing recycler view", Toast.LENGTH_SHORT).show();
        CollectionReference advocatesCollections = db.collection("advocates");

        advocatesCollections.whereEqualTo("username", clientUsername.getClientUsername()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        userId = documentSnapshot.getString("id");



                        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {

                            if (fragment.equals("Home")) {
                                Intent intent = new Intent(mInflater.getContext(), ViewProfileScreen.class);
                                intent.putExtra("key", clientUsername.getClientUsername());
                                intent.putExtra("key_userId", userId);
                                mInflater.getContext().startActivity(intent);
                            } else {
                                Intent intent = new Intent(mInflater.getContext(), ChatScreen.class);
                                intent.putExtra("key", clientUsername.getClientUsername());
                                intent.putExtra("key_userId", userId);
                                mInflater.getContext().startActivity(intent);
                            }
                        }
                        else
                        {
                            Intent intent = new Intent(mInflater.getContext(), ChatScreen.class);
                            intent.putExtra("key", clientUsername.getClientUsername());
                            intent.putExtra("key_userId", userId);
                            mInflater.getContext().startActivity(intent);
                        }


                    } else {
                        // Handle the case where no documents match the query
                        Toast.makeText(mInflater.getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failures in fetching the data
                    Log.e("Firestore", "Error getting user document", e);
                    Toast.makeText(mInflater.getContext(), "Error getting user data", Toast.LENGTH_SHORT).show();
                });
    }




    private void gettingAllData (ViewHolder holder, int position)
    {

            ClientInformation clientUsername = mData.get(position);
            holder.clientTextView.setText(clientUsername.getClientUsername());


            holder.timeStamp.setVisibility(View.GONE);

//        holder.timeStamp.setVisibility(View.GONE);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.timeStamp.getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        holder.timeStamp.setLayoutParams(params);

        Glide.with(holder.itemView.getContext())
                .load(clientUsername.getProfilePicture())
                .error(R.drawable.lawyer_icon) // Optional error image
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePicture);

//        getCurrentProfilePicStorageRef(clientUsername.getUid()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful())
//                    {
//                        Uri uri = task.getResult();
//                        setProfilePic(mInflater.getContext(),uri,holder.profilePicture);
//                    }
//                    else
//                    {
////                        Toast.makeText(mInflater.getContext(), "Problem loading image", Toast.LENGTH_SHORT).show();
//                    }
//
//                });


            if(itemCheckForRecyclerView)
            {

            }
            else
            {
                holder.location.setText(clientUsername.getLocation());
                holder.lastMessage.setText(clientUsername.getDesignation());

            }




//        gettingLocationAndDesignationData(clientUsername.getClientUsername(),holder,position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                    if(itemCheckForRecyclerView)
                    {
                        retrieveUsers(holder,clientUsername);
                    }
                    else
                    {
                        retrieveAdvocates(holder,clientUsername);
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        if (calledFromWhere.equals("filter the data")) {
            return filteredData.size();
        } else if (calledFromWhere.equals("display all data")){
            return mData.size();
        }
        else
        {
            return mData.size();
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public ClientInformation getItem(int position) {
        if(calledFromWhere.equals("display all data"))
        {
            return mData.get(position);
        }
        else if(calledFromWhere.equals("filter the data")) {
            return filteredData.get(position);
        }
        else {
            return mData.get(position);
        }
    }

    public void filterByName(String toString) {
       Log.e("filter executed","filter executed");
        calledFromWhere = "filter the data";
        filteredData.clear();

        if (toString.isEmpty()) {
//            filteredData.addAll(mData);
            calledFromWhere = "display all data";
            Log.e("called from where",calledFromWhere + mData.size());
//            Log.e("value of mData: ","mData:" + String.valueOf(mData.size()));
        } else  {

            Log.e("called from where","else statement");
//
//            if(mData.size() == 0)
//            {
//                calledFromWhere = "display all data";
//
//            }

//            calledFromWhere = "filter the data";

            String lowerCaseQuery = toString.toLowerCase();

            Log.e("value of mData: ", String.valueOf(mData.size()));

            for (ClientInformation item : mData) {

                Log.e("else statment of filer","else statement executed");

                if (item.getClientUsername().toLowerCase().contains(lowerCaseQuery)) {
                    filteredData.add(item);
                    Log.d("Filtered data", String.valueOf(filteredData.size()));
                }
            }
        }

        notifyDataSetChanged();
    }


    public void filterByContact (String toString)
    {
        Log.e("filter executed","filter executed");
        calledFromWhere = "filter the data";
        filteredData.clear();

        if (toString.isEmpty()) {
//            filteredData.addAll(mData);
            calledFromWhere = "display all data";
//            Log.e("value of mData: ",mData.toString());
        } else  {
            Log.e("else statment of filer","else statement executed");


            String lowerCaseQuery = toString.toLowerCase();

            for (ClientInformation item : mData) {

                if (item.getPhoneNumber().toLowerCase().contains(lowerCaseQuery)) {
                    filteredData.add(item);

                }
            }
        }

        notifyDataSetChanged();
    }



    public void filterByDistance (String toString)
    {
        Log.e("filter executed","filter executed");
        calledFromWhere = "filter the data";
        filteredData.clear();

        if (toString.isEmpty()) {
//            filteredData.addAll(mData);
            calledFromWhere = "display all data";
//            Log.e("value of mData: ",mData.toString());
        } else  {
            Log.e("else statment of filer","else statement executed");
            Double distance = Double.parseDouble(toString.toLowerCase());

            for (ClientInformation item : mData) {

                if (item.getDistanceFromUser() < distance) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByLocation (String toString)
    {
        Log.e("filter executed","filter executed");
        calledFromWhere = "filter the data";
        filteredData.clear();

        if (toString.isEmpty()) {
//            filteredData.addAll(mData);
            calledFromWhere = "display all data";
//            Log.e("value of mData: ",mData.toString());
        } else  {



            Log.e("else statment of filer","else statement executed");
            String lowerCaseQuery = toString.toLowerCase();

            for (ClientInformation item : mData) {

                if (item.getLocation().toLowerCase().contains(lowerCaseQuery)) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView clientTextView;
        TextView lastMessage;

        TextView timeStamp;

        TextView location;

//        ImageView profilePictureClient;
        ImageView profilePicture;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clientTextView = itemView.findViewById(R.id.clientTextView);
            lastMessage = itemView.findViewById(R.id.lastMessageView);
            timeStamp = itemView.findViewById(R.id.timeStampView);
            location = itemView.findViewById(R.id.locationView);

            if (mySharedPreferences.getUserType().equals(MySharedPreferences.UserType.ADVOCATE)) {
                profilePicture = itemView.findViewById(R.id.profilePictureItem);
            }
            else
            {
                profilePicture = itemView.findViewById(R.id.profilePictureItem);
            }

            itemView.setOnClickListener(this);
        }


        public void onClick(View view) {
            if (mClickListener != null) {
//                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public interface ItemClickListener extends RecyclerViewAdapterForLegalAgreement.ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static StorageReference getCurrentProfilePicStorageRef(String uid)
    {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (context != null && !((Activity) context).isFinishing()) {
            Glide.with(context)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

}

