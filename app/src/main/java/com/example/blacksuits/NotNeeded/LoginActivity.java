package com.example.blacksuits.NotNeeded;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blacksuits.Fragments.HomeFragment;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as_lawyer);
        mySharedPreferences = new MySharedPreferences(this);

//        Button signUpAsUser = findViewById(R.id.btnSignUpAsUser);
//        Button signUpAsAdvocate = findViewById(R.id.btnSignUpAsAdvocate);
        EditText loginEmailEditText = findViewById(R.id.etEmail);
        EditText loginPasswordEditText = findViewById(R.id.etPassword);

        HomeFragment chatFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        if (((MySharedPreferences) mySharedPreferences).isLoggedIn()) {
            loadData();

            MySharedPreferences.UserType userType = ((MySharedPreferences) mySharedPreferences).getUserType();

            if (userType == MySharedPreferences.UserType.ADVOCATE) {
                startActivity(new Intent(this, AdvocateScreen.class));
                finish();
            } else {
                startActivity(new Intent(this, UserScreen.class));
                finish();
            }
        }

//        signUpAsAdvocate.setOnClickListener(v -> {
//            Intent intent = new Intent(this, RegistrationActivity.class);
//            intent.putExtra("signUpAsAdvocateClicked", true);
//            startActivity(intent);
//        });
//
//        signUpAsUser.setOnClickListener(v -> {
//            Intent intent = new Intent(this, RegistrationActivity.class);
//            startActivity(intent);
//        });

        loginPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                loginUser(loginPasswordEditText);
                return true;
            } else {
                return false;
            }
        });
    }

    public void loginUser(View view) {
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            CollectionReference userCollections = db.collection("users");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                            userCollections.whereEqualTo("email", email).get()
                                    .addOnSuccessListener(documents -> {
                                        if (documents.isEmpty()) {
                                            ((MySharedPreferences) mySharedPreferences).saveEmail(email);
                                            ((MySharedPreferences) mySharedPreferences).saveUserType(MySharedPreferences.UserType.ADVOCATE);
                                            ((MySharedPreferences) mySharedPreferences).setLoggedIn(true);
                                            Toast.makeText(this, "Email saved in shared pref", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(this, "Email is not present in the users collection so it is in the advocates collection", Toast.LENGTH_SHORT).show();
                                            loadData();
                                            startActivity(new Intent(this, AdvocateScreen.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Inside the user activity now", Toast.LENGTH_SHORT).show();
                                            ((MySharedPreferences) mySharedPreferences).saveEmail(email);
                                            ((MySharedPreferences) mySharedPreferences).setLoggedIn(true);
                                            ((MySharedPreferences) mySharedPreferences).saveUserType(MySharedPreferences.UserType.USER);
                                            loadData();
                                            startActivity(new Intent(this, UserScreen.class));
                                            finish();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Unable to login. Check your input or try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadData() {
        String email = ((MySharedPreferences) mySharedPreferences).loadEmail();
        String username = ((MySharedPreferences) mySharedPreferences).loadUsername();
        Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();
    }
}