package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;

import com.example.myapplication5.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;




public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton closeButton;
    RecyclerView recyclerView;
    UserAdapter adapter;
    List<HelperClass> usersList;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered search");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        closeButton = findViewById(R.id.close_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        //button actions
        closeButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();

        });

        System.out.println("above arraylist line");

        List<HelperClass> userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        System.out.println("adapter line2");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        System.out.println("db initialized");
// Retrieve all users from Firebase and update the adapter
        reference.addValueEventListener(new ValueEventListener() {
//        reference.(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HelperClass user = snapshot.getValue(HelperClass.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}
