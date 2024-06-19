package com.example.blacksuits.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.R;
import com.example.blacksuits.Screens.IdentifyYourselfScreen;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerViewAdapterForUserAndLawyerScreen adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private MySharedPreferences mySharedPreferences;
    private boolean calledFromHomeFLojragment;

    RadioGroup radioGroup;

    ArrayList<ClientInformation> clientUsernames = new ArrayList<>();

    ArrayList<ClientInformation> clientUsernames2 = new ArrayList<>();


    EditText searchBar;

    RadioButton radioButtonName;
    RadioButton radioButtonContact;

    RadioButton radioButtonLocation;

    View view;

    Slider slider;


    private boolean check = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("on resume", String.valueOf(clientUsernames.size()));

        if (searchBar != null) {
            MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
            view.dispatchTouchEvent(event);
        }


        if (searchBar.getText().length() > 0) {
            // Apply the filter
            searchBar.setText("");
        }
        gettingData();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clientUsernames.clear();

        auth = FirebaseAuth.getInstance();

        ImageView profilePicture = view.findViewById(R.id.profile_picture_home_fragment);
//        ImageView toolbarTitle = view.findViewById(R.id.toolbarTitleChatFragment);
        ImageView logOutButton = view.findViewById(R.id.button_log_out_client_home_fragment);

        mySharedPreferences = new MySharedPreferences(requireContext());


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load(new EditProfile());
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

//        ProfilePictureDataClass.getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful())
//                    {
//                        Uri uri = task.getResult();
//                        ProfilePictureDataClass.setProfilePic(getContext(),uri,profilePicture);
//                    }
//
//                });

        if(mySharedPreferences.getImageUri()!=null) {
            ProfilePictureDataClass.setProfilePic(getContext(), mySharedPreferences.getImageUri(), profilePicture);
        }


        searchBar = view.findViewById(R.id.searchBarHomeFragment);
        slider = view.findViewById(R.id.slider);
        mySharedPreferences = new MySharedPreferences(requireContext());

        radioGroup = view.findViewById(R.id.RadioGroup);
        radioButtonName = view.findViewById(R.id.rbName);
        radioButtonContact = view.findViewById(R.id.rbContact);
        radioButtonLocation = view.findViewById(R.id.rbLocation);

        radioButtonName.setChecked(true);
        radioButtonName.setBackgroundResource(R.drawable.custom_radio_button_background_selected);

        slider.setVisibility(View.GONE);



        searchingMechanism();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Handle the checked change event here
                if (checkedId == R.id.rbName) {
                    radioButtonName.setBackgroundResource(R.drawable.custom_radio_button_background_selected);
                    radioButtonLocation.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);
                    radioButtonContact.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);

                    searchBar.setText("");

                    slider.setVisibility(View.GONE);

                } else if (checkedId == R.id.rbContact) {
                    radioButtonContact.setBackgroundResource(R.drawable.custom_radio_button_background_selected);
                    radioButtonLocation.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);
                    radioButtonName.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);

                    searchBar.setText("");

                    slider.setVisibility(View.GONE);

                } else if (checkedId == R.id.rbLocation) {
                    radioButtonLocation.setBackgroundResource(R.drawable.custom_radio_button_background_selected);
                    radioButtonName.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);
                    radioButtonContact.setBackgroundResource(R.drawable.custom_radio_button_background_not_selected);

                    slider.setVisibility(View.VISIBLE);

                    searchBar.setText("");

                    searchByLocation();
                }
            }
        });


        searchBar.post(new Runnable() {
            @Override
            public void run() {
                if (searchBar.getText().length() > 0 && !searchBar.hasFocus()) {
                    applyFilter();
                } else {
                    searchingMechanism();
                }
            }
        });

    }

    private void searchByLocation() {

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                adapter.filterByDistance(String.valueOf(value));
            }
        });
    }

    private void applyFilter() {
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (adapter != null) {
            if (checkedId == R.id.rbName) {
                adapter.filterByName(searchBar.getText().toString());
            } else if (checkedId == R.id.rbContact) {
                adapter.filterByContact(searchBar.getText().toString());
            } else if (checkedId == R.id.rbLocation) {
                adapter.filterByLocation(searchBar.getText().toString());
            }
        } else {
            Log.e(TAG, "Adapter is null when applying filter");
        }
    }


    private void searchingMechanism() {

        System.out.println("searching called");
        int checkedId = radioGroup.getCheckedRadioButtonId();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                applyFilter();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable editable) {

                applyFilter();
            }
        });
    }

    private void buildRecyclerView() {
        if (getView() != null) {
            Log.e("client usernames size", String.valueOf(clientUsernames.size()));
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                adapter = new RecyclerViewAdapterForUserAndLawyerScreen(this.getLayoutInflater(), clientUsernames, "display all data", false, "Home");
                adapter.setClickListener((RecyclerViewAdapterForUserAndLawyerScreen.ItemClickListener) requireContext());
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("RecyclerView", "RecyclerView is null");
            }
        } else {
            Log.e("View", "View is null");
        }
    }


    private void gettingData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        clientUsernames.clear();


        System.out.println("gettin data");

        Log.e("getting Data", "getting Data executed");
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
                                String profilePictureScr = document.getString("profilePictureSrc");
                                String id = document.getString("id");

                                Double latitudeUser = ((MySharedPreferences) mySharedPreferences).getGeolocation().getLatitude();
                                Double longitudeUser = ((MySharedPreferences) mySharedPreferences).getGeolocation().getLongitude();

                                double distanceFromUser = haversineFormula(latitude, longitude, latitudeUser, longitudeUser);

//                                Toast.makeText(getContext(), profilePictureScr, Toast.LENGTH_SHORT).show();

                                clientUsernames.add(new ClientInformation
                                        (username, location, phoneNumber, new GeoLocation(latitude, longitude), distanceFromUser, designation, id, profilePictureScr));

                            }


                            buildRecyclerView();


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
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


}