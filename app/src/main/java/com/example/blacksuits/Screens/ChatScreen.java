package com.example.blacksuits.Screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.blacksuits.Adapters.ChatAdapter;
import com.example.blacksuits.DataClass.ChatMessage;

import com.example.blacksuits.DataClass.ProfilePictureDataClass;
import com.example.blacksuits.Fragments.ChatFragment;
import com.example.blacksuits.MainActivity;

import com.example.blacksuits.Notifications.FCMNotificationSender;
import com.example.blacksuits.Notifications.NotificationTemplate;
import com.example.blacksuits.Notifications.Room;
import com.example.blacksuits.Notifications.TokenSingletonClass;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatScreen extends AppCompatActivity {

    private Context context;
    private MySharedPreferences mySharedPreferences;

    private AdapterView.OnItemClickListener listener;


    private List<ChatMessage> messageList;
    TokenSingletonClass tokenSingletonClass;

    private RecyclerView recyclerView;

    private ChatAdapter messageAdapter;
    private EditText messageArea;
    private RelativeLayout documentLayout, deleteLayout;
    private ProgressBar uploadProgress;

    private ImageView sendButton;

    DatabaseReference notificationRef;

    private FirebaseFirestore db;

    private DatabaseReference reference1;

    private DatabaseReference reference2;

    private DatabaseReference reference3;


    private FirebaseDatabase database;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";

    final private String serverKey = "key=" + "AAAAY0Pa_BI:APA91bECo_oNrysZT1MYK5AW9YMKu6Gk_NyF1aA_Gfug8JgptCMa5hIeT_gxKb6l98Wmizu3eysWgnjVlt5olSzsWfUfEzDDqYStMySyoZY-7b4KjK4Ev_Z_ULxU2WaBcuuuCyFI28hr";

    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private DatabaseReference chatsReference;

    private RelativeLayout noMessagesLayout;


    private String userId;

    Room room;

    ChatFragment chatFragment;

    String senderUsername, receiverUsername;

    private ImageView image;

    String senderUid, receiverUid, senderRoom, receiverRoom;

    String notificationTitle;


    public static boolean isSendNotification = false;
    String notificationMessage;

    String topic;


    ArrayList<String> receivedUsernames = new ArrayList<>();

    private static final int PICK_PDF_REQUEST = 1;

    private StorageReference mStorageRef;
    private int PERMISSION_REQUEST_CODE = 2;


    String currentUserType;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        tokenSingletonClass = TokenSingletonClass.getInstance();

        receivedUsernames = new ArrayList<>();

        mySharedPreferences = new MySharedPreferences(this);
        MySharedPreferences.UserType userType = ((MySharedPreferences) mySharedPreferences).getUserType();
        senderUsername = ((MySharedPreferences) mySharedPreferences).loadUsername();
        receiverUsername = getIntent().getStringExtra("key");

        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chatScreenFragment);


        ImageView pfp = findViewById(R.id.lawyer_or_client_image_chat_screen);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        noMessagesLayout = findViewById(R.id.no_messages_layout);
        image = findViewById(R.id.image);


        if (userType == MySharedPreferences.UserType.ADVOCATE) {
            Drawable drawable = getResources().getDrawable(R.drawable.client_icon);
            pfp.setImageDrawable(drawable);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.lawyer_icon);
            pfp.setImageDrawable(drawable);
        }

        toolbarTitle.setText(getIntent().getStringExtra("key"));

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatScreen.this, ViewProfileScreen.class);
                String message = getIntent().getStringExtra("key");
                intent.putExtra("key", message);
                intent.putExtra("key_userId", receiverUid);
                startActivity(intent);
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
            }
        });

//        ProfilePictureDataClass.getCurrentProfilePicStorageRef()


        ImageView backButton = findViewById(R.id.back_button_chat_screen);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        messageArea = findViewById(R.id.message_area);
        documentLayout = findViewById(R.id.document_layout);
        deleteLayout = findViewById(R.id.delete_layout);

        uploadProgress = findViewById(R.id.progressBar);

        sendButton = findViewById(R.id.send_button);
        sendButton.setImageResource(R.drawable.send_button);
        sendButton.setEnabled(false);

        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    sendButton.setImageResource(R.drawable.send_button);
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setImageResource(R.drawable.sendmessage);
                    sendButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageArea.setEnabled(true);
                messageArea.setFocusable(true);
                messageArea.setFocusableInTouchMode(true);

                messageArea.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageArea, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        messageArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndClickArea = messageArea.getRight() - messageArea.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableEndClickArea) {
                        // Perform your action when the drawableEnd is clicked
                        LayoutInflater inflater = LayoutInflater.from(view.getContext());
                        View dialogView = inflater.inflate(R.layout.dialog_choose_file_type, null);

                        AlertDialog alertDialog = new MaterialAlertDialogBuilder(view.getContext())
                                .setView(dialogView)
                                .create();

                        LinearLayout document = dialogView.findViewById(R.id.document);
                        LinearLayout gallery = dialogView.findViewById(R.id.gallery);

                        document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pickPDFFile();
                                alertDialog.cancel();
                            }
                        });
                        gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkPermission();
                                openGallery();
                                alertDialog.cancel();
                            }
                        });

                        alertDialog.show();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        messageArea.setFocusable(false);

                        return true;
                    }
                }
                return false;
            }
        });

        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance();
        reference1 = database.getReference("chats");

        messageList = new ArrayList<>();

        receiverUid = getIntent().getStringExtra("key_userId");


        ProfilePictureDataClass.getCurrentProfilePicStorageRef(receiverUid).getDownloadUrl().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                Uri uri = task.getResult();
                ProfilePictureDataClass.setProfilePic(this,uri,pfp);
            }
            else
            {

            }
        });



        senderUid = mySharedPreferences.getUserID();

        tokenSingletonClass.setReceiverUid(receiverUid);
        tokenSingletonClass.setSenderUid(senderUid);

        senderRoom = senderUid + "_" + receiverUid;
        receiverRoom = receiverUid + "_" + senderUid;

        reference2 = database.getReference("chats").child("Information for unread messages").child(receiverRoom);
        reference3 = database.getReference("chats").child("Information for unread messages");


        db = FirebaseFirestore.getInstance();

        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.ADVOCATE) {

            CollectionReference advocatesCollections = db.collection("users");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");

                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };
                                childSnapshot.getRef().child("status").setValue("online");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    Toast.makeText(ChatScreen.this, "User not found in users", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });


        } else if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
            CollectionReference advocatesCollections = db.collection("advocates");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");

                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                                Log.e("finding the value", childSnapshot.getValue().toString());

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };
                                childSnapshot.getRef().child("status").setValue("online");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });


                } else {

                    Toast.makeText(ChatScreen.this, "User not found in advocates", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });
        }


        buildRecyclerView();

        getChat();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = sendMessage();
                findReceiverToken(chatMessage);

                messageArea.setVisibility(View.VISIBLE);
                documentLayout.setVisibility(View.GONE);
            }
        });


//        findReceiverToken();


    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(image);
                    String selectedImageName = getFileName(selectedImageUri);
                    uploadImageToFirebaseStorage(selectedImageName, selectedImageUri);
                }
            });




    private ChatMessage sendMessage() {
//        Toast.makeText(this, "it is working fine here i guess " + room, Toast.LENGTH_SHORT).show();
        ChatMessage chatMessage = null;

        if (TextUtils.isEmpty(messageArea.getText().toString())) {
            Toast.makeText(ChatScreen.this, "Please type a message", Toast.LENGTH_SHORT).show();
        }
        else {
//                    noMessagesLayout.setVisibility(View.GONE);

            noMessagesLayout.setVisibility(View.GONE);

            // Generate a unique key for the message
            String messageText = messageArea.getText().toString();

            chatMessage = new ChatMessage(messageText, senderUsername, receiverUsername, senderUid, receiverUid, ServerValue.TIMESTAMP.toString());


            Map<String, String> map = new HashMap<String, String>();
            map.put("message", messageText);
            map.put("senderUid", senderUid);
            map.put("sender username", senderUsername);
            map.put("receiver username", receiverUsername);
            map.put("receiverUid", receiverUid);
            map.put("timestamp", String.valueOf(new SimpleDateFormat("hh:mm a").format(new Date())));
            reference1.child("messages").child(senderRoom).push().setValue(map);
            reference1.child("messages").child(receiverRoom).push().setValue(map);

            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };

                    Map<String, Object> map1 = snapshot.getValue(genericTypeIndicator);

                    if (map1 != null) {
                        String status = (String) map1.get("status");
                        if (map1.containsKey("count")) {
                            if (!status.equals("online")) {
                                String count = (String) map1.get("count");
                                int val = Integer.parseInt(count);
                                map.put("count", String.valueOf(++val));
                                map.put("status", "offline");
                                reference2.setValue(map);
                                Log.d("VALUE", "YES" + val);
                            } else {
                                String count = (String) map1.get("count");
                                int val = Integer.parseInt(count);
                                map.put("count", String.valueOf(val++));
                                map.put("status", "online");
                                reference2.setValue(map);
                                Log.d("VALUE", "No");
                            }
                        } else {
                            map.put("count", String.valueOf(1));
                            map.put("status", "offline");
                            reference2.setValue(map);
                            Log.d("VALUE", "No");
                        }
                    } else {
                        map.put("count", String.valueOf(1));
                        map.put("status", "offline");
                        reference2.setValue(map);
                        Log.d("VALUE", "first time");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
        return chatMessage;
    }


    private void sendNotifications(String token, ChatMessage chatMessage) {

        new FCMNotificationSender().send(String.format(NotificationTemplate.message, token, chatMessage.getText(), chatMessage.getSenderUsername(), chatMessage.getSenderId()), new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ChatScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() == 200) {

                        }
                    }
                });
            }
        });
    }


    private void findReceiverToken(ChatMessage chatMessage) {
        FirebaseDatabase.getInstance().getReference().child("user tokens").child(receiverUid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);

//                Toast.makeText(ChatScreen.this, token, Toast.LENGTH_SHORT).show();

                if (token != null) {
                    Log.e("isSendNotification token", String.valueOf(isSendNotification));
                    sendNotifications(token, chatMessage);
                    messageArea.getText().clear();

                } else {
                    Log.d("Token", "not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void buildRecyclerView() {

        recyclerView = findViewById(R.id.recyclerViewChatScreen);
        messageAdapter = new ChatAdapter(messageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }


    private void getChat() {

        reference1.child("messages").child(senderRoom).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                noMessagesLayout.setVisibility(View.GONE);

                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                };

                Map<String, Object> data = dataSnapshot.getValue(genericTypeIndicator);

                String messageText = (String) data.get("message");
                String senderUsername = (String) data.get("sender username");


//
//                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                messageAdapter = new ChatAdapter(messageList, ChatScreen.this);

                messageList.add(new ChatMessage(messageText, senderUsername, receiverUsername, senderUid, receiverUid, ServerValue.TIMESTAMP.toString()));


                receivedUsernames.add(senderUsername);

                Log.e("get chat", String.valueOf(dataSnapshot.getChildrenCount()));


//                Toast.makeText(ChatScreen.this, receivedUsernames.toString(),Toast.LENGTH_SHORT).show();
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


//        Toast.makeText(this, receivedUsernames.toString(), Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onResume() {
        super.onResume();

        isSendNotification = true;

//        Log.e("isSendNotifications",String.valueOf(isSendNotification));


        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.ADVOCATE) {
            CollectionReference advocatesCollections = db.collection("users");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");


                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                                Log.e("finding the value", childSnapshot.getValue().toString());

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };

                                Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);
                                Log.e("testing for the count value", "BACKSPACE");

//                                        childSnapshot.getRef().child("count").setValue(String.valueOf(0));
                                childSnapshot.getRef().child("status").setValue("online");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });


                } else {

                    Toast.makeText(ChatScreen.this, "User not found in users (backspace)", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });


        } else if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
            CollectionReference advocatesCollections = db.collection("advocates");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");


                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                                Log.e("finding the value", childSnapshot.getValue().toString());

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };

                                Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);

//                                    Toast.makeText(ChatScreen.this, "onDataChange has been triggered", Toast.LENGTH_SHORT).show();


                                Log.e("testing for the count value", "YES");

//                                        childSnapshot.getRef().child("count").setValue(String.valueOf(0));
                                childSnapshot.getRef().child("status").setValue("online");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });


                } else {

                    Toast.makeText(ChatScreen.this, "User not found in advocates (backspace)", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        isSendNotification = false;

        if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.ADVOCATE) {
            CollectionReference advocatesCollections = db.collection("users");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");


                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                                Log.e("finding the value", childSnapshot.getValue().toString());

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };

                                Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);
                                Log.e("testing for the count value", "BACKSPACE");

//                                        childSnapshot.getRef().child("count").setValue(String.valueOf(0));
                                childSnapshot.getRef().child("status").setValue("offline");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });


                } else {

                    Toast.makeText(ChatScreen.this, "User not found in users (backspace)", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });


        } else if (mySharedPreferences.getUserType() == MySharedPreferences.UserType.USER) {
            CollectionReference advocatesCollections = db.collection("advocates");
            advocatesCollections.whereEqualTo("username", getIntent().getStringExtra("key")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userId = documentSnapshot.getString("id");


                    reference3.orderByKey().equalTo(mySharedPreferences.getUserID() + "_" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                                Log.e("finding the value", childSnapshot.getValue().toString());

                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };

                                Map<String, Object> data = childSnapshot.getValue(genericTypeIndicator);

//                                    Toast.makeText(ChatScreen.this, "onDataChange has been triggered", Toast.LENGTH_SHORT).show();


                                Log.e("testing for the count value", "YES");

//                                        childSnapshot.getRef().child("count").setValue(String.valueOf(0));
                                childSnapshot.getRef().child("status").setValue("offline");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });


                } else {

                    Toast.makeText(ChatScreen.this, "User not found in advocates (backspace)", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {

                Log.e("Firestore", "Error getting user document", e);
                Toast.makeText(ChatScreen.this, "Error getting user data", Toast.LENGTH_SHORT).show();
            });


        }


    }

    private void pickPDFFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a PDF file"), PICK_PDF_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            Log.e("selected pdf file", selectedFileUri.toString());

                String selectedPdfName = getFileName(selectedFileUri);
                uploadPDFToFirebaseStorage(selectedPdfName, selectedFileUri);

        }
    }

    private void uploadPDFToFirebaseStorage(String filename, Uri pdfUri) {
        StorageReference fileRef = mStorageRef.child(filename);

        fileRef.putFile(pdfUri)
                .addOnProgressListener(taskSnapshot -> {
                    messageArea.setVisibility(View.GONE);
                    uploadProgress.setVisibility(View.VISIBLE);
                    documentLayout.setVisibility(View.VISIBLE);
                    // Calculate progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    // Update UI or show progress
                    uploadProgress.setProgress((int) progress);
                    Log.d("Upload Progress", "Upload is " + progress + "% done");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    uploadProgress.setVisibility(View.GONE);
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadLink = uri.toString();
                        deleteLayout.setVisibility(View.VISIBLE);
                        messageArea.setText(downloadLink);

                        deleteLayout.setOnClickListener(v -> {
                            fileRef.delete().addOnSuccessListener(aVoid -> {
                                // File deleted successfully
                                // Hide delete layout or perform any other actions
                                documentLayout.setVisibility(View.GONE);
                                messageArea.setVisibility(View.VISIBLE);
                                messageArea.getText().clear();
                            }).addOnFailureListener(e -> {
                                documentLayout.setVisibility(View.GONE);
                                messageArea.setVisibility(View.VISIBLE);
                                messageArea.getText().clear();
                                // Failed to delete file
                            });
                        });
                    });
                })
                .addOnFailureListener(e -> {
                    documentLayout.setVisibility(View.GONE);
                    messageArea.setVisibility(View.VISIBLE);
                    messageArea.getText().clear();
                    Toast.makeText(ChatScreen.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageToFirebaseStorage(String filename, Uri imageUri) {
        StorageReference fileRef = mStorageRef.child(filename);

        fileRef.putFile(imageUri)
                .addOnProgressListener(taskSnapshot -> {
                    messageArea.setVisibility(View.GONE);
                    documentLayout.setVisibility(View.VISIBLE);
                    // Calculate progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    // Update UI or show progress
                    uploadProgress.setProgress((int) progress);
                    Log.d("Upload Progress", "Upload is " + progress + "% done");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    uploadProgress.setVisibility(View.GONE);
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadLink = uri.toString();
                        deleteLayout.setVisibility(View.VISIBLE);
                        messageArea.setMovementMethod(LinkMovementMethod.getInstance());
                        messageArea.setText(downloadLink);

                        deleteLayout.setOnClickListener(v -> {
                            fileRef.delete().addOnSuccessListener(aVoid -> {
                                // File deleted successfully
                                // Hide delete layout or perform any other actions
                                documentLayout.setVisibility(View.GONE);
                                messageArea.setVisibility(View.VISIBLE);
                                messageArea.getText().clear();
                            }).addOnFailureListener(e -> {
                                // Failed to delete file
                                documentLayout.setVisibility(View.GONE);
                                messageArea.setVisibility(View.VISIBLE);
                                messageArea.getText().clear();
                            });
                        });
                    });
                })
                .addOnFailureListener(e -> {
                    documentLayout.setVisibility(View.GONE);
                    messageArea.setVisibility(View.VISIBLE);
                    messageArea.getText().clear();
                    Toast.makeText(ChatScreen.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                });
    }


}