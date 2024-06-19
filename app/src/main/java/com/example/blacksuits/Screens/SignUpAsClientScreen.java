package com.example.blacksuits.Screens;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class SignUpAsClientScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    LocationManager locationManager;

    public static final int PERMISSION_REQUEST_CODE = 123;

    GeoLocation geoLocation;

    EditText locationEditText;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_as_client_screen);
        auth = FirebaseAuth.getInstance();
        int newPasswordLength = 8;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView backButton = findViewById(R.id.back_button_sign_up_client);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

//        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
//        fusedLocationClient.getCurrentLocation(locationRequest.QUALITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
//                .addOnSuccessListener(SignUpAsClientScreen.this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            try {
//                                getCityName(location.getLongitude(), location.getLatitude());
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//
//                    }
//                });
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//
//                    @Override
//                    public void onSuccess(Location location) {
//
//                        Toast.makeText(SignUpAsClientScreen.this,String.valueOf(location), Toast.LENGTH_SHORT).show();
//
//                        if (location != null) {
//
//                            try {
//                                getCityName(location.getLongitude(),location.getLatitude());
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }
//                    }
//                });

        TextView already_a_member_button = findViewById(R.id.already_a_member_button_client);
        already_a_member_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to be executed when the Button is clicked
                // For example, you can show a Toast message
                Intent intent = new Intent(SignUpAsClientScreen.this, SigninAsClient.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView profilePicture = findViewById(R.id.clientProfilePicture);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
//                            Toast.makeText(this, selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
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

        locationEditText = findViewById(R.id.etLocation);
        locationButton();
    }

    private void requestLocationPermission() {
        // Request the location permission from the user
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }


    private boolean locationPermissionChecker() {
        return ContextCompat.checkSelfPermission(SignUpAsClientScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


    public void registerAsClient(View view) {
        String email = ((EditText) findViewById(R.id.etClientEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.etClientPassword)).getText().toString();
        String username = ((EditText) findViewById(R.id.etClientUsername)).getText().toString();
        String repeatPassword = ((EditText) findViewById(R.id.etClientConfirmPassword)).getText().toString();

        // Check if any field is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(repeatPassword) || TextUtils.isEmpty(locationEditText.getText().toString())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if any field is empty
        }

        try {
            if (password.length() >= 8) {
                if (password.equals(repeatPassword)) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpAsClientScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                    boolean signUpAsAdvocateClicked = getIntent().getBooleanExtra("signUpAsAdvocateClicked", false);

                                    db = FirebaseFirestore.getInstance();

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("username", username);
                                    user.put("id", auth.getCurrentUser().getUid());
                                    user.put("longitude", geoLocation.getLongitude());
                                    user.put("latitude", geoLocation.getLatitude());

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
                                                                    Toast.makeText(SignUpAsClientScreen.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();
                                                                    if (downloadUri != null) {
                                                                        user.put("profilePictureSrc", downloadUri.toString());
                                                                    }
                                                                    // Now write the user data to Firestore
                                                                    db.collection("users")
                                                                            .document(auth.getCurrentUser().getUid())
                                                                            .set(user)
                                                                            .addOnSuccessListener(aVoid -> finish())
                                                                            .addOnFailureListener(e -> Toast.makeText(SignUpAsClientScreen.this, "Error storing the data ", Toast.LENGTH_SHORT).show());
                                                                } else {
                                                                    Log.e("Upload Profile Picture", "Error getting download URL", task1.getException());
                                                                    Toast.makeText(SignUpAsClientScreen.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.e("Upload Profile Picture", "Upload failed", task1.getException());
                                                        Toast.makeText(SignUpAsClientScreen.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // If no profile picture was selected, just store the user data without profilePictureSrc
                                        db.collection("users")
                                                .document(auth.getCurrentUser().getUid())
                                                .set(user)
                                                .addOnSuccessListener(aVoid -> finish())
                                                .addOnFailureListener(e -> Toast.makeText(SignUpAsClientScreen.this, "Error storing the data ", Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "An unknown error occurred";
                                    Toast.makeText(SignUpAsClientScreen.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
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


    private void locationButton() {
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndClickArea = locationEditText.getRight() - locationEditText.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableEndClickArea) {
                        // Perform your action when the drawableEnd is clicked

//                        Toast.makeText(SignUpAsClientScreen.this, "location button has been clicked.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            if (locationPermissionChecker()) {
                                Toast.makeText(SignUpAsClientScreen.this, "Permission granted to the user", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(SignUpAsLawyerScreen.this, "Permission not granted to the user", Toast.LENGTH_SHORT).show();
                                requestLocationPermission();
                            }
                        } else {
//                            Toast.makeText(SignUpAsClientScreen.this, "else statement", Toast.LENGTH_SHORT).show();
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


    private void setupLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if the location provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // Request location updates

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
                    .addOnSuccessListener(SignUpAsClientScreen.this, new OnSuccessListener<Location>() {
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
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "setuplocation", Toast.LENGTH_SHORT).show();
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1, (LocationListener) locationListener);
//                return;
//            }
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1, (LocationListener) locationListener);
        } else {
            // You can prompt the user to enable the GPS
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "chal raha hai", Toast.LENGTH_SHORT).show();
                setupLocationManager();
            } else {

//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private final LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            // Handle the new location here
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//            String cityName = "";
//
//            Toast.makeText(SignUpAsClientScreen.this, "on Location changed", Toast.LENGTH_SHORT).show();
//
//
//            try {
//                cityName = getCityName(longitude,latitude);
//                Toast.makeText(SignUpAsClientScreen.this, cityName, Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//
//            geoLocation = new GeoLocation(latitude,longitude);
//            locationEditText.setText(cityName);
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//    };

    private String getCityName (double lon, double lat) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat,lon,1);

        String location = addresses.get(0).getLocality();

        Toast.makeText(this, location, Toast.LENGTH_SHORT).show();



        return location;
    }

    public static StorageReference getCurrentProfilePicStorageRef(String uid)
    {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}