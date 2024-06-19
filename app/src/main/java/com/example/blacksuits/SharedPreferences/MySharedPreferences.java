package com.example.blacksuits.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.blacksuits.DataClass.GeoLocation;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Set;

public class MySharedPreferences implements SharedPreferences {

    private final SharedPreferences sharedPreferences;

    // Keys for user type
    public static final String KEY_USER_TYPE = "userType";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";

    public static final String KEY_USERID = "userId";

    public static final String KEY_GEOLOCATION = "geolocation";

    private static final String KEY_IMAGE_URI = "imageUri";


    // User types
    public enum UserType {
        USER,
        ADVOCATE
    }

    public MySharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    // Save user type
    public void saveUserType(UserType userType) {
        sharedPreferences.edit().putString(KEY_USER_TYPE, userType.name()).apply();
    }



    // Retrieve user type
    public UserType getUserType() {
        String userTypeString = sharedPreferences.getString(KEY_USER_TYPE, UserType.USER.name());
        return UserType.valueOf(userTypeString);
    }

    // Save login status
    public void setLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Save email
    public void saveEmail(String email) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public void saveImageUri(Uri uri) {
        sharedPreferences.edit().putString(KEY_IMAGE_URI, uri.toString()).apply();
    }

    // Retrieve image URI
    // Retrieve image URI
    public Uri getImageUri() {
        String uriString = sharedPreferences.getString(KEY_IMAGE_URI, null);
        if (uriString != null) {
            return Uri.parse(uriString);
        } else {
            return null;
        }
    }

    // Load email
    public String loadEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }


    public void saveUserID (String userID){
        sharedPreferences.edit().putString(KEY_USERID, userID).apply();
    }

    public void saveGeolocation(GeoLocation geolocation) {
        Gson gson = new Gson();
        String geolocationJson = gson.toJson(geolocation);
        sharedPreferences.edit().putString(KEY_GEOLOCATION, geolocationJson).apply();
    }

    // Retrieve Geolocation
    public GeoLocation getGeolocation() {
        String geolocationJson = sharedPreferences.getString(KEY_GEOLOCATION, null);
        if (geolocationJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(geolocationJson, GeoLocation.class);
        } else {
            return null;
        }
    }






    public String getUserID ()
    {
        return sharedPreferences.getString(KEY_USERID, "");
    }

    public void saveUsername(String username) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }

    public void removeImageUri() {
        sharedPreferences.edit().remove(KEY_IMAGE_URI).apply();
    }

    public String loadUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    // Remove a key from SharedPreferences
    public void removeKey(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getString(String key, String defValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        return null;
    }


    @Override
    public int getInt(String key, int defValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long getLong(String key, long defValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public float getFloat(String key, float defValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean contains(String key) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Editor edit() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}