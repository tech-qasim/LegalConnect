package com.example.blacksuits.DataClass;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilePictureDataClass {
    public static StorageReference getCurrentProfilePicStorageRef(String uid)
    {
        return FirebaseStorage.getInstance().getReference().child("profilepic").child(uid);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (context != null && !((Activity) context).isFinishing()) {
            Glide.with(context)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

}
