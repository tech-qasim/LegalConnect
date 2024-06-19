package com.example.blacksuits.NotNeeded;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.blacksuits.Fragments.HomeFragment;
import com.example.blacksuits.Fragments.LegalAgreementFragment;
import com.example.blacksuits.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdvocateScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advocate_screen);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewForAdvocate);
        HomeFragment chatFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        LegalAgreementFragment legalAgreementFragment = new LegalAgreementFragment();

        setCurrentFragment(chatFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.chat_icon) {
                    setCurrentFragment(chatFragment);
                } else if (itemId == R.id.home_icon) {
                    setCurrentFragment(profileFragment);
                } else if (itemId == R.id.legal_agreement_icon) {
                    setCurrentFragment(legalAgreementFragment);
                }

                return true;
            }
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}