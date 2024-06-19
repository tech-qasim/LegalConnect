package com.example.blacksuits.Fragments;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchByLocationFragment extends Fragment {

    RecyclerViewAdapterForUserAndLawyerScreen adapter;

    private RecyclerView recyclerView;

    Slider discreteSlider;

    RadioGroup radioGroup;

    RadioButton radioButtonLocation;
    ArrayList<ClientInformation> advocateData = new ArrayList<>();

    private SharedPreferences mySharedPreferences;

    public SearchByLocationFragment() {
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
        return inflater.inflate(R.layout.fragment_search_by_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        discreteSlider = view.findViewById(R.id.discreteSlider);
        radioGroup = view.findViewById(R.id.RadioGroup);

        mySharedPreferences = new MySharedPreferences(requireContext());

        advocateData.clear();



        gettingData();

        searchingMechanism();

        buildRecyclerView(view);



    }



    private void gettingData ()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        System.out.println("gettin data");

        Log.e("getting Data","getting Data executed");
        db.collection("advocates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> usernames = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {

                                String username = document.getString("username");
                                String location = document.getString("location");
                                String phoneNumber = document.getString("phone number");
                                double latitude = document.getDouble("latitude");
                                double longitude = document.getDouble("longitude");
                                String designation = document.getString("designation");
                                String profilePictureSrc = document.getString("profilePictureSrc");
                                String id = document.getString("id");

                                Double latitudeUser = ((MySharedPreferences) mySharedPreferences).getGeolocation().getLatitude();
                                Double longitudeUser = ((MySharedPreferences) mySharedPreferences).getGeolocation().getLongitude();

                                double distanceFromUser = haversineFormula(latitude,longitude,latitudeUser,longitudeUser);

                                advocateData.add(new ClientInformation(username,location,phoneNumber,new GeoLocation(latitude,longitude),distanceFromUser,designation,id, profilePictureSrc));

                                System.out.println(advocateData.get(0).getDistanceFromUser());

                                adapter.notifyItemInserted(advocateData.size()-1);

                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void searchingMechanism()
    {

       discreteSlider.addOnChangeListener(new Slider.OnChangeListener() {
           @Override
           public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
               adapter.filterByLocation(String.valueOf(value));
           }
       });
    }



    private void buildRecyclerView (View view)
    {
        Log.e("recycler view","recycler view");
        recyclerView = view.findViewById(R.id.recyclerViewForSearchByLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecyclerViewAdapterForUserAndLawyerScreen(this.getLayoutInflater(), advocateData, "display all data", false, "Home");
        adapter.setClickListener((RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener) requireContext());
        recyclerView.setAdapter(adapter);
    }


    private double haversineFormula(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        System.out.println(lat1);
        System.out.println(lon1);
        System.out.println(lat2);
        System.out.println(lon2);


        // Calculate differences
        double dlat = Math.abs(lat2 - lat1);
        double dlon = Math.abs(lon2 - lon1);

        // Haversine formula
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        double distance = 6371.0 * c;

        return distance;
    }


}