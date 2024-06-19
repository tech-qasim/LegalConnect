package com.example.blacksuits.Fragments;

import static android.content.ContentValues.TAG;


import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.blacksuits.Screens.SignUpAsClientScreen.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.R;
import com.example.blacksuits.Screens.Advocate;
import com.example.blacksuits.Screens.IdentifyYourselfScreen;
import com.example.blacksuits.Screens.SignUpAsLawyerScreen;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class EditProfile extends Fragment {

    private MySharedPreferences mySharedPreferences;

    private FirebaseAuth auth;

    private String email;
    private String username;

    private MySharedPreferences.UserType userType;

    EditText emailEditText;

    ActivityResultLauncher<Intent> imagePickLauncher;

    private FirebaseFirestore db;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    GeoLocation geoLocation;
    private static final int PERMISSION_REQUEST_CODE = 123;


    private Handler handler;
    private Runnable emailVerificationRunnable;

    Uri imageUri;

    EditText usernameEditText;

    private ProfilePicViewModel viewModel;

    Uri selectedImageUri;

    EditText designationEditText;
    private LocationManager locationManager;

    EditText phoneNumberEditText;
    EditText locationEditText;

    private LocationRequest locationRequest;

    private Context mContext;

    private OnProfilePicChangeListener listener = null;
    ImageView pfp;

    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();

        db = FirebaseFirestore.getInstance();

        viewModel = new ViewModelProvider(requireActivity()).get(ProfilePicViewModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        mySharedPreferences = new MySharedPreferences(requireContext());
        email = ((MySharedPreferences) mySharedPreferences).loadEmail();
        username = ((MySharedPreferences) mySharedPreferences).loadUsername();

        Log.e("checking the value of username", username);
        userType = ((MySharedPreferences) mySharedPreferences).getUserType();
         pfp = view.findViewById(R.id.pfp);

        CardView backButton = view.findViewById(R.id.back_button);
        usernameEditText = view.findViewById(R.id.etClientUsername);
        designationEditText = view.findViewById(R.id.etDesignation);
        phoneNumberEditText = view.findViewById(R.id.etPhoneNumber);
        locationEditText = view.findViewById(R.id.etLocation);
        CardView updateButton = view.findViewById(R.id.update_button);
        ImageView btnEdit = view.findViewById(R.id.btnEditInFragment);


        db = FirebaseFirestore.getInstance();

        // Document reference
        DocumentReference docRef = db.collection("advocates").document(mySharedPreferences.getUserID());


        Map<String, Object> updates = new HashMap<>();
        updates.put("profilePictureSrc", "fieldValue");

        usernameEditText.setText(username);

        showExistingData();

        getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();

                        if(listener!=null) {
                            listener.onProfilePicChange(uri);
                        }

                        setProfilePic(getContext(), uri, pfp);
                    }

                });


        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            if (mContext != null) {
                                setProfilePic(mContext, selectedImageUri, pfp);
                            }
                        }
                    }
                }
        );

        pfp.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });


        if (userType == MySharedPreferences.UserType.ADVOCATE) {
            Drawable drawable = getResources().getDrawable(R.drawable.lawyer_icon);
            pfp.setImageDrawable(drawable);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameEditText.setFocusable(true);
                usernameEditText.setFocusableInTouchMode(true);
                designationEditText.setFocusable(true);
                designationEditText.setFocusableInTouchMode(true);
                locationEditText.setFocusable(true);
                locationEditText.setFocusableInTouchMode(true);
                phoneNumberEditText.setFocusable(true);
                phoneNumberEditText.setFocusableInTouchMode(true);


                usernameEditText.requestFocus();
                if (usernameEditText.requestFocus()) {
                    requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }


            }


//                update(emailEditText.getText().toString(),passwordEditText.getText().toString());

        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateData();

//                getActivity().recreate();


            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });


        ImageView logOutButton = view.findViewById(R.id.btnLogOutInFragment);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
            designationEditText.setVisibility(View.GONE);
            phoneNumberEditText.setVisibility(View.GONE);
            locationEditText.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);

        }


    }


    private void logoutUser() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();

        mySharedPreferences.removeKey(MySharedPreferences.KEY_EMAIL);
        mySharedPreferences.removeKey(MySharedPreferences.KEY_USER_TYPE);

        mySharedPreferences.setLoggedIn(false);

        Intent intent = new Intent(requireContext(), IdentifyYourselfScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }


    private void showExistingData() {

        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
            CollectionReference advocatesCollections = db.collection("users");


            advocatesCollections.whereEqualTo("username", mySharedPreferences.loadUsername()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String user_name = documentSnapshot.getString("username");

                        usernameEditText.setText(user_name);


//                        etEmail.setText(e_mail);
//                        etPhoneNumber.setText(phone_number);
//                        etDesignation.setText(designation);
//                        etLocation.setText(location);


                    });
        } else {
            CollectionReference advocatesCollections = db.collection("advocates");


            advocatesCollections.whereEqualTo("username", mySharedPreferences.loadUsername()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String user_name = documentSnapshot.getString("username");
                        String e_mail = documentSnapshot.getString("email");
                        String phone_number = documentSnapshot.getString("phone number");
                        String designation = documentSnapshot.getString("designation");
                        String location = documentSnapshot.getString("location");

                        designationEditText.setText(designation);
                        phoneNumberEditText.setText(phone_number);
                        locationEditText.setText(location);


//                        etEmail.setText(e_mail);
//                        etPhoneNumber.setText(phone_number);
//                        etDesignation.setText(designation);
//                        etLocation.setText(location);


                    });
        }


        locationButton();

    }


    private void updateData() {


        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.ADVOCATE) {
            DocumentReference advocatesCollections = db.collection("advocates").document(mySharedPreferences.getUserID());

            Map<String, Object> updates = new HashMap<>();
            updates.put("username", usernameEditText.getText().toString());
            updates.put("designation", designationEditText.getText().toString());
            updates.put("location", locationEditText.getText().toString());
            updates.put("phone number", phoneNumberEditText.getText().toString());

//
//            if (selectedImageUri != null) {
//                StorageReference profilePicRef = getCurrentProfilePicStorageRef(mySharedPreferences.getUserID());
//                profilePicRef.putFile(selectedImageUri)
//                        .addOnCompleteListener(task1 -> {
//                            if (task1.isSuccessful()) {
//                                profilePicRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task1) {
//                                        if (task1.isSuccessful()) {
//                                            Uri downloadUri = task1.getResult();
//                                            Log.e("getting the link", downloadUri.toString());
//
////                                            Toast.makeText(getContext(), downloadUri.toString(), Toast.LENGTH_SHORT).show();
//                                            if (downloadUri != null) {
//                                                updates.put("profilePictureSrc", downloadUri.toString());
//                                            }
//
//                                        } else {
//                                            Log.e("Upload Profile Picture", "Error getting download URL", task1.getException());
//                                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                            } else {
//                                Log.e("Upload Profile Picture", "Upload failed", task1.getException());
//                                Toast.makeText(getContext(), "Profile picture upload failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }




            if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
                usernameEditText.setError("Username cannot be empty");
                return;
            }

            if (TextUtils.isEmpty(designationEditText.getText().toString())) {
                designationEditText.setError("Designation cannot be empty");
                return;
            }

            if (TextUtils.isEmpty(locationEditText.getText().toString())) {
                locationEditText.setError("Location cannot be empty");
                return;
            }

            if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())) {
                phoneNumberEditText.setError("Phone number cannot be empty");
                return;
            }


//            getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Uri uri = task.getResult();
//
//                            setProfilePic(getContext(), uri, pfp);
//                        }
//
//                    });

            if (selectedImageUri != null) {
                getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).putFile(selectedImageUri)
                        .addOnCompleteListener(taskSnapshotTask -> {

                            if (taskSnapshotTask.isSuccessful())
                            {
                                getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            Log.e("getting the link editpro", downloadUri.toString());

//                                            Toast.makeText(getContext(), downloadUri.toString(), Toast.LENGTH_SHORT).show();
                                            if (downloadUri != null) {
                                                updates.put("profilePictureSrc", downloadUri.toString());
                                            }

                                            advocatesCollections.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(getContext(), "successfully updated", Toast.LENGTH_SHORT).show();

                                                        ((MySharedPreferences) mySharedPreferences).saveUsername(usernameEditText.getText().toString());
                                                    } else {
                                                        Log.w(TAG, "Error updating document", task.getException());
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document: ", e);
                                                }
                                            });


//
//                                        }
                                        }
                                    }
                                });
                            }

//                            sendSelectedImageUri(selectedImageUri);
                                sendSelectedImageUri(selectedImageUri);
                        });
            } else {
                Toast.makeText(requireContext(), "Okay cool", Toast.LENGTH_SHORT).show();
            }




//            advocatesCollections.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(requireActivity(), "successfully updated", Toast.LENGTH_SHORT).show();
//
//                        ((MySharedPreferences) mySharedPreferences).saveUsername(usernameEditText.getText().toString());
//                    } else {
//                        Log.w(TAG, "Error updating document", task.getException());
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w(TAG, "Error updating document: ", e);
//                }
//            });
        } else {

//            DocumentReference advocatesCollections = db.collection("users").document(mySharedPreferences.getUserID());
//
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("username", usernameEditText.getText().toString());
//            updates.put("designation", designationEditText.getText().toString());
//            updates.put("location", locationEditText.getText().toString());
//            updates.put("phone number", phoneNumberEditText.getText().toString());
//
//
//            advocatesCollections.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(requireContext(), "successfully updated", Toast.LENGTH_SHORT).show();
//
//                        ((MySharedPreferences) mySharedPreferences).saveUsername(usernameEditText.getText().toString());
//                    } else {
//                        Log.w(TAG, "Error updating document", task.getException());
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w(TAG, "Error updating document: ", e);
//                }
//            });

            Toast.makeText(mContext, "this is executed", Toast.LENGTH_SHORT).show();

            if (selectedImageUri != null) {
                getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).putFile(selectedImageUri)
                        .addOnCompleteListener(taskSnapshotTask -> {

                        mySharedPreferences.saveImageUri(selectedImageUri);

                        });
            } else {
                Toast.makeText(requireContext(), "Okay cool", Toast.LENGTH_SHORT).show();
            }

//            getCurrentProfilePicStorageRef(mySharedPreferences.getUserID()).getDownloadUrl()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Uri uri = task.getResult();
//
//
//                            listener.onProfilePicChange(uri);
//
//                            setProfilePic(getContext(), uri, pfp);
//                        }
//
//                    });



        }


    }


    private void locationButton() {
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndClickArea = locationEditText.getRight() - locationEditText.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableEndClickArea) {
                        // Perform your action when the drawableEnd is clicked


//                        Toast.makeText(requireActivity(), "location button has been clicked.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            if (locationPermissionChecker()) {
                                Toast.makeText(requireActivity(), "Permission granted to the user", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(SignUpAsLawyerScreen.this, "Permission not granted to the user", Toast.LENGTH_SHORT).show();
                                requestLocationPermission();
                            }

//                            ActivityCompat.requestPermissions(SignUpAsLawyerScreen.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
//                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                        } else {
//                            Toast.makeText(requireContext(), "else statement", Toast.LENGTH_SHORT).show();
                            setupLocationManager();
                        }

                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        locationEditText.setFocusable(true);

                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void requestLocationPermission() {
        // Request the location permission from the user
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    private void setupLocationManager() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        // Check if the location provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getCurrentLocation(locationRequest.QUALITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try {
                                    String city = getCityName(location.getLongitude(), location.getLatitude());

                                    geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());

                                    locationEditText.setText(city);


                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    });

//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1, (LocationListener) locationListener);
        } else {
            // You can prompt the user to enable the GPS
            Toast.makeText(requireContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCityName(double lon, double lat) throws IOException {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

        String location = addresses.get(0).getLocality();


        return location;
    }

    private boolean locationPermissionChecker() {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static StorageReference getCurrentProfilePicStorageRef(String uid) {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }

    public void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (context != null && !((Activity) context).isFinishing()) {
            Glide.with(context)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            imageView.setTag(imageUri);

                            Toast.makeText(context, "profile picture loaded", Toast.LENGTH_SHORT).show();

                            System.out.println("Profile picture has loaded in the edit profile fragment");



                            viewModel.sendData(imageUri);

//                            if (listener!=null) {
//                                listener.onProfilePicChange(imageUri);
//                            }

                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    private void sendSelectedImageUri(Uri selectedImageUri)
    {
//        Intent intent = new Intent(getActivity(), Advocate.class);
//        intent.putExtra("imageUri", selectedImageUri.toString());
//        startActivity(intent);
    }


//    @Override
//    public void onAttach(@NonNull Activity activity) {
//        super.onAttach(activity);
//
//        if (activity instanceof OnProfilePicChangeListener)
//        {
//            Toast.makeText(activity, "on attached called", Toast.LENGTH_SHORT).show();
//
//            listener = (OnProfilePicChangeListener) activity;
//        }
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        listener = null;
//
//        Toast.makeText(mContext, "on detached called", Toast.LENGTH_SHORT).show();
//    }
}

