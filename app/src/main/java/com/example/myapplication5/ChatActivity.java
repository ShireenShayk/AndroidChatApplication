package com.example.myapplication5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    HelperClass sender;
    ChatroomModel chatroomModel;
    String chatroomId;

    ImageButton backButton;
    TextView sender_username;
    RecyclerView recyclerView;

    EditText messageInput;
    ImageButton sendMessageButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered chat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.message_send_btn);
        sender_username = findViewById(R.id.sender_username);
        backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.chat_recycler_view);

        sender = AndroidUtil.getUserModelFromIntent(getIntent());

        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUsername(),sender.getUsername());


        //button actions
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            // Optional: finish the ChatActivity to remove it from the back stack

        });

        sender_username.setText(sender.getUsername());

        sendMessageButton.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
//        setupChatRecyclerView();


    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
//TEst
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            String currentUserName;
//            @Override
//            public void  onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        // DocumentSnapshot contains the data of the current user
//                        HelperClass currentUser = document.toObject(HelperClass.class);
//
//                        // Now you can use the currentUser object as needed
//                        chatroomModel.setLastMessageSenderId(currentUser.getUsername());
//
//
//
//                        // Rest of your code
//                    } else {
//                        // Document does not exist (user details not found)
//                    }
//                } else {
//                    // Task failed with an exception
//                    Log.e("Firestore", "Error getting user details", task.getException());
//                }
//            }
//
//        });

        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUsername());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUsername(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
//                            sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUsername(),sender.getUsername()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }


}
