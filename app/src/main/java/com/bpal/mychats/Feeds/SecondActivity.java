package com.bpal.mychats.Feeds;

import static com.bpal.mychats.Utils.Const.APP_CLUSTER;
import static com.bpal.mychats.Utils.Const.APP_KEY;
import static com.bpal.mychats.Utils.Const.CHANNEL_NAME;
import static com.bpal.mychats.Utils.Const.EVENT_NAME;
import static com.bpal.mychats.Utils.Const.SOCKET_ID;
import static com.bpal.mychats.Utils.Const.rootRef;
import static com.bpal.mychats.Utils.ConstFire.FEEDS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.databinding.ActivityFeedsBinding;
import com.bpal.mychats.databinding.ActivitySecondBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    ActivitySecondBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textView.setText("Select your name");

        binding.brijesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Const.USERNAME, "Brijesh");
                startActivity(intent);
            }
        });

        binding.raj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Const.USERNAME, "Rajendra");
                startActivity(intent);
            }
        });

        binding.admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Const.USERNAME, "Admin");
                startActivity(intent);
            }
        });

    }
}