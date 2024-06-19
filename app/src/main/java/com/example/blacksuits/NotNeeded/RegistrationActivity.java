package com.example.blacksuits.NotNeeded;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blacksuits.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        int newPasswordLength = 8;
    }

    public void registerUser(View view) {
        String email = ((EditText) findViewById(R.id.email_edit_text)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_edit_text)).getText().toString();
        String username = ((EditText) findViewById(R.id.username_edit_text)).getText().toString();
        String repeatPassword = ((EditText) findViewById(R.id.re_password_edit_text)).getText().toString();

        try {
            if (password.length() >= 8) {
                if (password.equals(repeatPassword)) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                    boolean signUpAsAdvocateClicked = getIntent().getBooleanExtra("signUpAsAdvocateClicked", false);

                                    db = FirebaseFirestore.getInstance();

                                    if (!signUpAsAdvocateClicked) {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("email", email);
                                        user.put("username", username);

                                        db.collection("users")
                                                .document(auth.getCurrentUser().getUid())
                                                .set(user)
                                                .addOnSuccessListener(aVoid -> Toast.makeText(RegistrationActivity.this, "User data is stored in firestore", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, "Error storing the data in firestore", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Map<String, Object> advocate = new HashMap<>();
                                        advocate.put("email", email);
                                        advocate.put("username", username);

                                        db.collection("advocates")
                                                .document(auth.getCurrentUser().getUid())
                                                .set(advocate)
                                                .addOnSuccessListener(aVoid -> Toast.makeText(RegistrationActivity.this, "Advocate data is stored in firestore", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, "Error storing the data in firestore", Toast.LENGTH_SHORT).show());
                                    }

                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "An unknown error occurred";
                                    Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegistrationActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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
}