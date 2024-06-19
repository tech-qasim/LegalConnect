package com.example.blacksuits.Fragments;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfilePicViewModel extends ViewModel {

    public MutableLiveData<Uri> sharedProfilePic = new MutableLiveData<Uri>();

    public void sendData (Uri uri)
    {
        sharedProfilePic.setValue(uri);
    }

    public LiveData<Uri> getData()
    {
        return sharedProfilePic;
    }


}
