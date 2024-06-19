package com.example.blacksuits.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blacksuits.DataClass.GeoLocation;
import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.example.blacksuits.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SigninAsClient extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_as_client);
        mySharedPreferences = new MySharedPreferences(this);

//        Button signUpAsUser = findViewById(R.id.btnSignUpAsUser);
//        Button signUpAsAdvocate = findViewById(R.id.btnSignUpAsAdvocate);
        EditText loginEmailEditText = findViewById(R.id.etUserEmailForSignin);
        EditText loginPasswordEditText = findViewById(R.id.etClientPasswordForSignin);
        androidx.cardview.widget.CardView loginButton = findViewById(R.id.login_button);
        TextView forgotPasswordButton = findViewById(R.id.forgot_password_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(SigninAsClient.this, RecoverAccountScreen.class);
                String userType = "Client";
                intent.putExtra("usertype",userType);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SecondActivity
                Toast.makeText(SigninAsClient.this,loginEmailEditText.getText().toString(),Toast.LENGTH_SHORT).show();
                checkCollection(loginEmailEditText.getText().toString(),loginPasswordEditText.getText().toString());
            }
        });

        loginPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String email = loginEmailEditText.getText().toString();
                String password = loginPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    checkCollection(email, password);
                }
                return true;
            }
            return false;
        });

        CardView back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> {
            finish();
        });

        TextView signupquestionmarkbutton = findViewById(R.id.sign_up_question_mark_button);



        signupquestionmarkbutton.setOnClickListener(v -> {

            Intent intent = new Intent (SigninAsClient.this,SignUpAsClientScreen.class);
            startActivity(intent);

        });
    }

    public void loginAsClient(String email,String password) {
//        String email = ((EditText) findViewById(R.id.etLawyerEmailForSignin)).getText().toString();
//        String password = ((EditText) findViewById(R.id.etLawyerPasswordForSignin)).getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {

            CollectionReference userCollections = db.collection("users");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                            userCollections.whereEqualTo("email", email).get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {

                                        Toast.makeText(this, "Inside the user activity now", Toast.LENGTH_SHORT).show();
                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                        String username = documentSnapshot.getString("username");
                                        Double latitude = documentSnapshot.getDouble("latitude");
                                        Double longitude = documentSnapshot.getDouble("longitude");
                                        String id = documentSnapshot.getString("id");


                                        ProfilePictureDataClass.getCurrentProfilePicStorageRef(id).getDownloadUrl()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful())
                                                            {
                                                                Uri uri = task1.getResult();
                                                                ((MySharedPreferences) mySharedPreferences).saveImageUri(uri);
                                                            }
                                                            else
                                                            {


                                                                Toast.makeText(this, "no profile picture found", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

//                                        getCurrentProfilePicStorageRef().getDownloadUrl()
//                                                .addOnCompleteListener(uriTask -> {
//                                                    if (task.isSuccessful()) {
//                                                        Uri uri = task.getResult();
//                                                        setProfilePic(getContext(), uri, pfp);
//                                                    }
//
//                                                });





                                        ((MySharedPreferences) mySharedPreferences).saveEmail(email);
                                        ((MySharedPreferences) mySharedPreferences).saveUsername(username);
                                        ((MySharedPreferences) mySharedPreferences).setLoggedIn(true);
                                        ((MySharedPreferences) mySharedPreferences).saveUserType(MySharedPreferences.UserType.USER);
                                        ((MySharedPreferences) mySharedPreferences).setLoggedIn(true);
                                        ((MySharedPreferences) mySharedPreferences).saveUserID(auth.getCurrentUser().getUid());
                                        ((MySharedPreferences) mySharedPreferences).saveGeolocation(new GeoLocation(latitude,longitude));





                                        loadData();
                                        startActivity(new Intent(this, User.class));
                                        Intent intent = new Intent("finish_activity");
                                        sendBroadcast(intent);
                                        finish();


                                    });
                        } else {

                            Exception exception = task.getException();
                            if (exception != null) {
                                exception.printStackTrace();
                                System.out.println(exception);

                            }

                            Toast.makeText(this, "Unable to login. Check your input or try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void checkCollection(String email, String password) {
        // Reference to the collection
        Query query = db.collection("users").whereEqualTo("email", email);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty())
                {
                    loginAsClient(email,password);
                }
                else
                {
                    Toast.makeText(SigninAsClient.this,"this email does not exist users' database",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

//    public static StorageReference getCurrentProfilePicStorageRef(String uid) {
//        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
//    }
//
//    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
//        if (context != null && !((Activity) context).isFinishing()) {
//            Glide.with(context)
//                    .load(imageUri)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(imageView);
//        }
//    }
    private void loadData() {
        String email = ((MySharedPreferences) mySharedPreferences).loadEmail();
        String username = ((MySharedPreferences) mySharedPreferences).loadUsername();
        String latitude = String.valueOf(((MySharedPreferences) mySharedPreferences).getGeolocation().getLatitude());
        String longitude = String.valueOf(((MySharedPreferences) mySharedPreferences).getGeolocation().getLongitude());
        Toast.makeText(this, "Email: " + email + "\n" + "Username: "+username + "\n" + "latitude: " + "\n" + latitude + "longitude:"+longitude , Toast.LENGTH_SHORT).show();
    }
}