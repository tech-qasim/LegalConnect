package com.example.blacksuits.Adapters;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blacksuits.DataClass.ChatMessage;
//import com.example.blacksuits.Manifest;
import com.example.blacksuits.R;
import com.example.blacksuits.SharedPreferences.MySharedPreferences;

import java.io.File;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;

    private MySharedPreferences mySharedPreferences;
    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private static final int VIEW_TYPE_SENDER_WITH_DOWNLOAD = 3;
    private static final int VIEW_TYPE_RECEIVER_WITH_DOWNLOAD = 4;

    String downloadIntroURL = "https://firebasestorage.googleapis.com";

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList, Context context) {
        this.messageList = messageList;
        mySharedPreferences = new MySharedPreferences(context);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType==VIEW_TYPE_RECEIVER) {
            view = inflater.inflate(R.layout.receiver_message_item, parent, false);
            return new ReceiverViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_SENDER) {
            view = inflater.inflate(R.layout.sender_message_item, parent, false);
            return new SenderViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_SENDER_WITH_DOWNLOAD)
        {
            view = inflater.inflate(R.layout.sender_download_button, parent,false);
            return new SenderWithDownloadViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.receiver_download_button,parent,false);
            return new ReceiverWithDownloadViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.senderMessageTextView.setText(message.getText());

                break;
            case VIEW_TYPE_RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.receiverMessageTextView.setText(message.getText());
//                Linkify.addLinks(receiverViewHolder.receiverMessageTextView, Linkify.WEB_URLS);

                Log.e("if condition checking","YES");

                break;
            case VIEW_TYPE_SENDER_WITH_DOWNLOAD:
                SenderWithDownloadViewHolder senderWithDownloadViewHolder = (SenderWithDownloadViewHolder) holder;
                Uri uri = Uri.parse(message.getText());
                Log.d(TAG, "uriEnd: "+uri);

                if (uri.toString().contains(".png") || uri.toString().contains(".jpg")){
                    Glide.with(context)
                            .load(uri)
                            .into(senderWithDownloadViewHolder.senderMessageImageView);
                }
                
                senderWithDownloadViewHolder.senderMessageImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = extractCharactersAfterLastDash(message.getText()+".pdf");
                        String destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String filePath = destinationDirectory + "/" + fileName;
                        File file = new File(filePath);
                        if (file.exists()) {
                            // File already exists, handle accordingly
                            openFile(file);
                        } else {
                            // File doesn't exist, set the destination
                            startDownload(message.getText());
                        }
                    }
                });

                break;

            case VIEW_TYPE_RECEIVER_WITH_DOWNLOAD:
                ReceiverWithDownloadViewHolder receiverWithDownloadViewHolder = (ReceiverWithDownloadViewHolder) holder;
                Glide.with(context)
                        .load(message.getText())
                        .into(receiverWithDownloadViewHolder.receiverMessageImageView);
                receiverWithDownloadViewHolder.receiverMessageImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fileName = extractCharactersAfterLastDash(message.getText()+".pdf");
                        String destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String filePath = destinationDirectory + "/" + fileName;
                        File file = new File(filePath);
                        if (file.exists()) {
                            // File already exists, handle accordingly
                            openFile(file);
                        } else {
                            // File doesn't exist, set the destination
                            startDownload(message.getText());
                        }


                    }
                });

//                receiverWithDownloadViewHolder.receiverMessageImageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage chatMessage = messageList.get(position);

//        Toast.makeText(context,position,Toast.LENGTH_SHORT).show();

        String messageText = chatMessage.getText();


        String loggedInUsername = mySharedPreferences.loadUsername();
        String senderUsername = chatMessage.getSenderUsername();


        if (loggedInUsername.equals(senderUsername))
        {
            if (messageText.contains(downloadIntroURL))
            {
                return VIEW_TYPE_SENDER_WITH_DOWNLOAD;
            }
            else
            {
                return VIEW_TYPE_SENDER;
            }

        }
        else {
            if (messageText.contains(downloadIntroURL))
            {
                return VIEW_TYPE_RECEIVER_WITH_DOWNLOAD;
            }
            else
            {
                return VIEW_TYPE_RECEIVER;
            }

        }

    }

    // ViewHolder for sender messages
    private static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageTextView;

        SenderViewHolder(View itemView) {
            super(itemView);
            senderMessageTextView = itemView.findViewById(R.id.sender_message_body);
        }
    }

    // ViewHolder for receiver messages
    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageTextView;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            receiverMessageTextView = itemView.findViewById(R.id.receiver_message_body);
        }
    }

    private static class SenderWithDownloadViewHolder extends RecyclerView.ViewHolder {
        ImageView senderMessageImageView;

        SenderWithDownloadViewHolder(View itemView) {
            super(itemView);
            senderMessageImageView = itemView.findViewById(R.id.sender_download_button);
        }
    }


    private static class ReceiverWithDownloadViewHolder extends RecyclerView.ViewHolder {
        ImageView receiverMessageImageView;
        TextView downloadButton;

        ReceiverWithDownloadViewHolder(View itemView) {
            super(itemView);
            receiverMessageImageView = itemView.findViewById(R.id.receiver_download_button);
//            downloadButton = itemView.findViewById(R.id.download_button);
//            receiverMessageImageView.setVisibility(View.VISIBLE);
        }
    }




    public void startDownload(String downloadUrl) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions when not granted
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // Extract file name from the URL
            String fileName = extractCharactersAfterLastDash(downloadUrl);

            // Check if the file already exists
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            if (file.exists()) {
                // File already exists, show a message or take appropriate action
                Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
                openFile(file);
            } else {
                // File doesn't exist, initiate the download
                startDownloadProcess(context, downloadUrl, fileName);
            }
        }
    }

    private void startDownloadProcess(Context context, String downloadUrl, String fileName) {
        // Create the download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        // Set download parameters
        request.setTitle("File Download");
        request.setDescription("Downloading file...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/*"); // Set a general MIME type

        // Set the destination directory and file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Get the download manager and enqueue the request
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);

            // Register a BroadcastReceiver to listen for download completion
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctxt, Intent intent) {
                    // Check if the download completed successfully
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                        // Show toast indicating download completion
                        Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
                    }
                    // Unregister the receiver
                    context.unregisterReceiver(this);
                }
            };

            // Register the BroadcastReceiver
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            Toast.makeText(context, "Download manager is not available", Toast.LENGTH_SHORT).show();
        }
    }    private String extractCharactersAfterLastDash(String input) {
        int lastDashIndex = input.lastIndexOf('-');

        if (lastDashIndex != -1 && lastDashIndex < input.length() - 1) {
            // The link contains at least one dash, and it's not the last character
            return input.substring(lastDashIndex + 1);
        } else {
            // No dash found or the last character is a dash
            return "No characters after the last dash";
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private void openFile(File file) {
        // Determine the MIME type of the file
        String mimeType = getMimeType(file.getAbsolutePath());

        // Create an intent to open the file
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Attempt to open the file
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // Handle exceptions if the file cannot be opened
            e.printStackTrace();
        }
    }

    private String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

}


