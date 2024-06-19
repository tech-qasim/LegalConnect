package com.example.blacksuits.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class NotificationService extends Service {

    private DatabaseReference notificationsRef;

    String receiverRoom;

    String senderUid;

    String receiverUid;

    private MySharedPreferences mySharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mySharedPreferences = new MySharedPreferences(this);
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("chats").child("Information for unread messages");

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator =
                                new GenericTypeIndicator<Map<String, Object>>() {
                                };

                        Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);

                        if ((data != null && data.containsKey("senderUid") && data.get("senderUid") != null && data.containsKey("receiverUid") && data.get("receiverUid") != null && data.get("receiverUid").equals("LvJJCJ5P7uao69rgnzLtfaNpwL33") && data.get("senderUid").equals("uNIDYVceQvaGMC9ltptcDLnfmbo1"))) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sendNotification(data.get("message").toString(), data.get("message").toString());
                            }

                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String title, String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = new Notification.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.chat_icon)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1, notification);

    }
}
