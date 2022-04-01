package com.bpal.mychats.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bpal.mychats.Adapters.UsersAdapter;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.R;
import com.bpal.mychats.Services.Token;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.databinding.ActivityFeedsBinding;
import com.bpal.mychats.databinding.ActivitySecondBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    ActivitySecondBinding binding;
    User user = Const.currentUser;
    ArrayList<User> list;
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.uname.setText(user.getName());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvUser.hasFixedSize();
        binding.rvUser.setLayoutManager(layoutManager);

        Const.rootUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User data = snap.getValue(User.class);

                        if (data != null && !user.getUid().equals(data.getUid())) {
                            list.add(data);
                        }
                    }
                    adapter = new UsersAdapter(getApplicationContext(), list);
                    adapter.notifyDataSetChanged();
                    binding.rvUser.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}