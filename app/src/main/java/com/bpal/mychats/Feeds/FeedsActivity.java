package com.bpal.mychats.Feeds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bpal.mychats.R;
import com.bpal.mychats.databinding.ActivityFeedsBinding;
import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;
import java.util.List;

public class FeedsActivity extends AppCompatActivity {

    private static final String CHANNEL = "channel";
    ActivityFeedsBinding binding;
    FeedAdapter adapter;
    Pusher pusher;
    private static final String CHANNEL_NAME = "brijesh-development";
    LinearLayoutManager layoutManager;
    List<Event> eventList;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    String ch, event, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textView.setText("Feeds");
        recyclerView = findViewById(R.id.rvfeed);

        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");

        preferences = getSharedPreferences(CHANNEL, MODE_PRIVATE);
        pusher = new Pusher("040996f203fbba0bd2d1", options);

        SharedPreferences.Editor editor = preferences.edit();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe(CHANNEL_NAME);

        channel.bind("event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent e) {
                Log.i("Pusher", "Received event with data:======== " + e.getChannelName()+"; "+e.getEventName() +"; " +e.getData());
                Event data = new Event(e.getChannelName(), e.getEventName(), e.getData());
                eventList.add(data);
                Log.i("Pusher", "Received event with data:======== " + preferences.getString(CHANNEL, null));
            }
        });

        eventList = new ArrayList<>();
        adapter = new FeedAdapter(getApplicationContext(), eventList);
        recyclerView.setAdapter(adapter);

    }

}