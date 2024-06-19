package com.example.blacksuits.Screens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;

public class IdentifyYourselfScreen extends AppCompatActivity {
    private SharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify_yourself_screen);
        mySharedPreferences = new MySharedPreferences(this);

        androidx.cardview.widget.CardView btnLawyer = findViewById(R.id.btnLawyer);
        androidx.cardview.widget.CardView btnClient = findViewById(R.id.btnClient);
        androidx.cardview.widget.CardView back_button = findViewById(R.id.back_button);
        if (((MySharedPreferences) mySharedPreferences).isLoggedIn()) {


            MySharedPreferences.UserType userType = ((MySharedPreferences) mySharedPreferences).getUserType();

            if (userType == MySharedPreferences.UserType.ADVOCATE) {
                startActivity(new Intent(this, Advocate.class));
                finish();
            } else {
                startActivity(new Intent(this, User.class));
                finish();
            }
        }
        btnLawyer.setOnClickListener(v -> {
            Intent intent = new Intent(this, SigninAsLawyer.class);
            intent.putExtra("signUpAsAdvocateClicked", true);
            startActivity(intent);
        });

        btnClient.setOnClickListener(v -> {
            Intent intent = new Intent(this, SigninAsClient.class);
            startActivity(intent);
        });

        back_button.setOnClickListener(v -> {
            finish();
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

    }
}
