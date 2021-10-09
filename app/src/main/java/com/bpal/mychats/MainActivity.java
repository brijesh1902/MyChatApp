package com.bpal.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bpal.mychats.Adapters.UsersAdapter;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.Services.Token;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.Utils.ConstFire;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView name;
    User user = Const.currentUser;
    RecyclerView recyclerView;
    ArrayList<User> list;
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.uname);
        recyclerView = findViewById(R.id.rv_user);

        name.setText(user.getName());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);

        Const.rootUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User data = snap.getValue(User.class);
                        if (!user.getName().equals(data.getName())) {
                            list.add(data);
                        }
                    }
                    adapter = new UsersAdapter(getApplicationContext(), list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (user != null) {
            generateToken(FirebaseInstanceId.getInstance().getToken());
        }

    }

    private void generateToken(String token) {
        Token data = new Token(token, user.getUid());
        Const.tokenRef.child(user.getUid()).setValue(data);

        /*Const.tokenRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
}