package com.example.blacksuits.Notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.blacksuits.Adapters.RecyclerViewAdapterForUserAndLawyerScreen;
import com.example.blacksuits.DataClass.ClientInformation;
import com.example.blacksuits.R;
import com.example.blacksuits.Screens.ChatScreen;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FCMNotificationReceiver extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";

    private static final String CHANNEL_ID = "Notification_channel";

    private DatabaseReference databaseReference;

    private MySharedPreferences mySharedPreferences;

    Random random = new Random();

    private FirebaseFirestore db;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = token;
        if (firebaseUser != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Token t = new Token(refreshToken);
            FirebaseDatabase.getInstance().getReference().child("user tokens").child(firebaseUser.getUid()).child("last updated").setValue(String.valueOf(new SimpleDateFormat("hh:mm a").format(new Date())));
            FirebaseDatabase.getInstance().getReference().child("user tokens").child(firebaseUser.getUid()).child("token").setValue(String.valueOf(token));
            reference.child(firebaseUser.getUid()).child("token").setValue(t);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });

        mySharedPreferences = new MySharedPreferences(this);
        createNotificationChannel();

        String receiverUid = message.getData().get("forUid");

        if (mySharedPreferences.getUserID().equals(receiverUid) && !ChatScreen.isSendNotification)
        {
            if(mySharedPreferences.getUserType() == MySharedPreferences.UserType.ADVOCATE)
            {
                retrieveUsers(message);
            }
            else
            {
                retrieveAdvocates(message);
            }
        }


    }


    private void sendLocalNotification(RemoteMessage remoteMessage, String userId) {
        String downloadIntroURL = "https://firebasestorage.googleapis.com";

        NotificationCompat.Builder builder;
        if (remoteMessage.getData().get("body").contains(downloadIntroURL)){
            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.chat_icon)
                    .setContentTitle(remoteMessage.getData().get("for"))
                    .setContentText("Document")
                    .setColor(ContextCompat.getColor(this, R.color.white));

        }else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.chat_icon)
                    .setContentTitle(remoteMessage.getData().get("for"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setColor(ContextCompat.getColor(this, R.color.white));

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Intent intent = new Intent(FCMNotificationReceiver.this,ChatScreen.class);
        intent.putExtra("key", remoteMessage.getData().get("for"));
        intent.putExtra("key_userId",userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(FCMNotificationReceiver.this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);


        NotificationManagerCompat  notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(random.nextInt() + 1000, builder.build());
    }

    private void retrieveAdvocates(RemoteMessage remoteMessage)
    {
        db = FirebaseFirestore.getInstance();
        CollectionReference advocatesCollections = db.collection("advocates");

        advocatesCollections.whereEqualTo("username", remoteMessage.getData().get("for")).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = documentSnapshot.getString("id");

                        sendLocalNotification(remoteMessage, userId);

                    } else {
                        // Handle the case where no documents match the query
                        Toast.makeText(FCMNotificationReceiver.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failures in fetching the data
                    Toast.makeText(FCMNotificationReceiver.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveUsers(RemoteMessage remoteMessage)
    {
        db = FirebaseFirestore.getInstance();
        CollectionReference advocatesCollections = db.collection("users");

        advocatesCollections.whereEqualTo("username", remoteMessage.getData().get("for")).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                       String userId = documentSnapshot.getString("id");


                       sendLocalNotification(remoteMessage,userId);

                    } else {
                        // Handle the case where no documents match the query
                        Toast.makeText(FCMNotificationReceiver.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failures in fetching the data
                    Toast.makeText(FCMNotificationReceiver.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
