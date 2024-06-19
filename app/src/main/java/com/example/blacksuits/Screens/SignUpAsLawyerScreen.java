package com.example.blacksuits.Screens;

import static com.bumptech.glide.Glide.with;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
 // Replace com.example with the actual package name

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class SignUpAsLawyerScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    GeoLocation geoLocation;

    Uri selectedImageUri;

    ActivityResultLauncher<Intent> imagePickLauncher;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private LocationManager locationManager;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    com.google.android.material.textfield.TextInputEditText locationEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_as_lawyer_screen);
        auth = FirebaseAuth.getInstance();
        int newPasswordLength = 8;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView alreadyAMemberButton = findViewById(R.id.already_a_member_button);

        ImageView profilePicture = findViewById(R.id.lawyerProfilePicture);


        alreadyAMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpAsLawyerScreen.this, SigninAsLawyer.class);

                startActivity(intent);

                finish();
            }
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            setProfilePic(this,selectedImageUri,profilePicture);
                        }
                    }
                }
        );



        profilePicture.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        CardView back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> {
            finish();
        });

        locationEditText = findViewById(R.id.etLocation);


        locationButton();


    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void locationButton() {
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndClickArea = locationEditText.getRight() - locationEditText.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableEndClickArea) {
                        // Perform your action when the drawableEnd is clicked


//                        Toast.makeText(SignUpAsLawyerScreen.this, "location button has been clicked.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            if (locationPermissionChecker()) {
                                Toast.makeText(SignUpAsLawyerScreen.this, "Permission granted to the user", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(SignUpAsLawyerScreen.this, "Permission not granted to the user", Toast.LENGTH_SHORT).show();
                                requestLocationPermission();
                            }

//                            ActivityCompat.requestPermissions(SignUpAsLawyerScreen.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
//                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                        } else {
//                            Toast.makeText(SignUpAsLawyerScreen.this, "else statement", Toast.LENGTH_SHORT).show();
                            setupLocationManager();
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }


    private boolean locationPermissionChecker() {
        return ContextCompat.checkSelfPermission(SignUpAsLawyerScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setupLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // Check if the location provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    .addOnSuccessListener(SignUpAsLawyerScreen.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try {
                                    String city = getCityName(location.getLongitude(), location.getLatitude());

                                    geoLocation = new GeoLocation(location.getLatitude(),location.getLongitude());

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
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    }






    public void registerAsAdvocate(View view) {
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        String username = ((EditText) findViewById(R.id.etUsername)).getText().toString();
        String designation = ((EditText) findViewById(R.id.etDesignation)).getText().toString();
//        String location = ((EditText) findViewById(R.id.etLocation)).getText().toString();
        String phoneNumber = ((EditText) findViewById(R.id.etPhoneNumber)).getText().toString();
        String repeatPassword = ((EditText) findViewById(R.id.etConfirmPassword)).getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(repeatPassword) || TextUtils.isEmpty( locationEditText.getText().toString()) || TextUtils.isEmpty(designation)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if any field is empty
        }

        try {
            if (password.length() >= 8) {
                if (password.equals(repeatPassword)) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpAsLawyerScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                    boolean signUpAsAdvocateClicked = getIntent().getBooleanExtra("signUpAsAdvocateClicked", false);

                                    db = FirebaseFirestore.getInstance();


                                        Map<String, Object> advocate = new HashMap<>();
                                        advocate.put("email", email);
                                        advocate.put("username", username);
                                        advocate.put("id",auth.getCurrentUser().getUid());
                                        advocate.put("designation",designation);
                                        advocate.put("phone number",phoneNumber);
                                        advocate.put("location",locationEditText.getText().toString());
                                        advocate.put("longitude",geoLocation.getLongitude());
                                        advocate.put("latitude", geoLocation.getLatitude());





                                    if (selectedImageUri != null) {
                                        StorageReference profilePicRef = getCurrentProfilePicStorageRef(auth.getCurrentUser().getUid());
                                        profilePicRef.putFile(selectedImageUri)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        profilePicRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task1) {
                                                                if (task1.isSuccessful()) {
                                                                    Uri downloadUri = task1.getResult();
                                                                    Log.e("getting the link", downloadUri.toString());
                                                                    Toast.makeText(SignUpAsLawyerScreen.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();
                                                                    if (downloadUri != null) {
                                                                        advocate.put("profilePictureSrc", downloadUri.toString());
                                                                    }
                                                                    // Now write the user data to Firestore
                                                                    db.collection("advocates")
                                                                            .document(auth.getCurrentUser().getUid())
                                                                            .set(advocate)
                                                                            .addOnSuccessListener(aVoid -> finish())
                                                                            .addOnFailureListener(e -> Toast.makeText(SignUpAsLawyerScreen.this, "Error storing the data ", Toast.LENGTH_SHORT).show());
                                                                } else {
                                                                    Log.e("Upload Profile Picture", "Error getting download URL", task1.getException());
                                                                    Toast.makeText(SignUpAsLawyerScreen.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.e("Upload Profile Picture", "Upload failed", task1.getException());
                                                        Toast.makeText(SignUpAsLawyerScreen.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // If no profile picture was selected, just store the user data without profilePictureSrc
                                        db.collection("users")
                                                .document(auth.getCurrentUser().getUid())
                                                .set(advocate)
                                                .addOnSuccessListener(aVoid -> finish())
                                                .addOnFailureListener(e -> Toast.makeText(SignUpAsLawyerScreen.this, "Error storing the data ", Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "An unknown error occurred";
                                    Toast.makeText(SignUpAsLawyerScreen.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(SignUpAsLawyerScreen.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                    finish();
                } else {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "The length of the password should be 8 or more than 8", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private String getCityName (double lon, double lat) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat,lon,1);

        String location = addresses.get(0).getLocality();



        return location;
    }

    public static StorageReference getCurrentProfilePicStorageRef(String uid)
    {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }


}


