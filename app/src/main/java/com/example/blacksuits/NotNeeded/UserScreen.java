package com.example.blacksuits.NotNeeded;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.blacksuits.Fragments.HomeFragment;
import com.example.blacksuits.NotNeeded.ProfileFragment;
import com.example.blacksuits.R;

public class UserScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        HomeFragment chatFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        setCurrentFragment(chatFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.chat_icon) {
                setCurrentFragment(chatFragment);
            } else if (itemId == R.id.home_icon) {
                setCurrentFragment(profileFragment);
            }
            return true;
        });

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}