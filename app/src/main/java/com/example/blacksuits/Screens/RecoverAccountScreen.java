package com.example.blacksuits.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.blacksuits.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RecoverAccountScreen extends AppCompatActivity {
    private EditText etSendEmail;
    private androidx.cardview.widget.CardView btnSendEmail;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_account);

        etSendEmail = findViewById(R.id.etsendEmail);
        btnSendEmail = findViewById(R.id.send_email_button);
        firebaseAuth = FirebaseAuth.getInstance();
        CardView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        db = FirebaseFirestore.getInstance();
        TextView rememberitButton = findViewById(R.id.remember_it_button);
        ImageView client_or_lawyer_image = findViewById(R.id.lawyer_or_client_image);

        Intent intent = getIntent();
                if (intent!=null){
                    String receivedUserType = intent.getStringExtra("usertype");

                    if (receivedUserType.equals("Client"))
                    {
                        client_or_lawyer_image.setImageResource(R.drawable.client_icon);
                    }
                    else {
                        client_or_lawyer_image.setImageResource(R.drawable.lawyer_icon);
                    }
                }







        rememberitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });





    }



    private void resetPassword() {
        String email = etSendEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        checkCollection("users",email);
        checkCollection("advocates",email);





        }





    private void checkCollection(String collectionName, final String email) {
        // Reference to the collection
        Query query = db.collection(collectionName).whereEqualTo("email", email);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Email exists in the collection
                    // Handle accordingly, e.g., show an error message or take appropriate action

                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(RecoverAccountScreen.this, "Password reset email sent. Check your mail box", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Toast.makeText(RecoverAccountScreen.this,
                                                "Failed to send reset email. Check your email address.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Toast.makeText(RecoverAccountScreen.this,"email exists " + collectionName,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecoverAccountScreen.this,"email doesnt exists " + collectionName,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
