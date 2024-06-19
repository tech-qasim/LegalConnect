package com.example.blacksuits.NotNeeded;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.blacksuits.NotNeeded.LoginActivity;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private static final String PREF_IS_LOGGED_IN = "is_logged_in";

    private FirebaseAuth auth;
    private MySharedPreferences mySharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mySharedPreferences = new MySharedPreferences(requireContext());

        Button logOutButton = view.findViewById(R.id.btnLogOut);

        logOutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        auth.signOut();

        mySharedPreferences.removeKey(MySharedPreferences.KEY_EMAIL);
        mySharedPreferences.removeKey(MySharedPreferences.KEY_USER_TYPE);

        mySharedPreferences.setLoggedIn(false);

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}