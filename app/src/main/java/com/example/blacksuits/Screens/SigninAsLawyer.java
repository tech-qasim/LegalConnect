package com.example.blacksuits.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.example.blacksuits.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SigninAsLawyer extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences mySharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as_lawyer);
        mySharedPreferences = new MySharedPreferences(this);


        EditText loginEmailEditText = findViewById(R.id.etLawyerEmailForSignin);
        EditText loginPasswordEditText = findViewById(R.id.etLawyerPasswordForSignin);
        androidx.cardview.widget.CardView loginButton = findViewById(R.id.login_button);
        TextView forgotPasswordButton = findViewById(R.id.forgot_password_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(SigninAsLawyer.this, RecoverAccountScreen.class);
                String userType = "Lawyer";
                intent.putExtra("usertype",userType);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SecondActivity
                Toast.makeText(SigninAsLawyer.this,loginEmailEditText.getText().toString(),Toast.LENGTH_SHORT).show();
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

            Intent intent = new Intent(SigninAsLawyer.this, SignUpAsLawyerScreen.class);
            startActivity(intent);

        });
    }

    private boolean checkCollection(final String emailAddress, final String password) {
        final boolean check = false;
//        String email = ((EditText) findViewById(R.id.etLawyerEmailForSignin)).getText().toString();
//        String password = ((EditText) findViewById(R.id.etLawyerPasswordForSignin)).getText().toString();


        // Reference to the collection
        Query query = db.collection("advocates").whereEqualTo("email", emailAddress);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty())
                {

                      loginAsAdvocate(emailAddress,password);
//                    Toast.makeText(SigninAsLawyer.this,check,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SigninAsLawyer.this,"this email does not exits in advocates' database",Toast.LENGTH_SHORT).show();
                }

            }
        });


        return check;
    }

    public void loginAsAdvocate(String email,String password) {

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
//                db = FirebaseFirestore.getInstance();
                CollectionReference advocatesCollections = db.collection("advocates");

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                                advocatesCollections.whereEqualTo("email", email).get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {

                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                            String username = documentSnapshot.getString("username");

                                            ((MySharedPreferences) mySharedPreferences).saveEmail(email);
                                            ((MySharedPreferences) mySharedPreferences).saveUsername(username);
                                            ((MySharedPreferences) mySharedPreferences).saveUserType(MySharedPreferences.UserType.ADVOCATE);
                                            ((MySharedPreferences) mySharedPreferences).setLoggedIn(true);
                                            ((MySharedPreferences) mySharedPreferences).saveUserID(auth.getCurrentUser().getUid());
                                            Toast.makeText(this, "Email saved in shared pref", Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(this, "Email is not present in the users collection so it is in the advocates collection", Toast.LENGTH_SHORT).show();
                                            loadData();
                                            startActivity(new Intent(this, Advocate.class));
//                                            startActivity(new Intent(this, User.class));
                                            Intent intent = new Intent("finish_activity");
                                            sendBroadcast(intent);
                                            finish();

                                        });
                            } else {
                                System.out.println("EMAIL:" + email + "\n");
                                Toast.makeText(this, "Unable to login. Check your input or try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }



        }



    private void loadData() {
        String email = ((MySharedPreferences) mySharedPreferences).loadEmail();
//        String username = ((MySharedPreferences) mySharedPreferences).loadUsername();
        Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();
    }



}
