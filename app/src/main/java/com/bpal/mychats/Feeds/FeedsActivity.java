package com.bpal.mychats.Feeds;

import static com.bpal.mychats.Utils.Const.APP_CLUSTER;
import static com.bpal.mychats.Utils.Const.APP_ID;
import static com.bpal.mychats.Utils.Const.APP_KEY;
import static com.bpal.mychats.Utils.Const.APP_SECRET;
import static com.bpal.mychats.Utils.Const.CHANNEL_NAME;
import static com.bpal.mychats.Utils.Const.EVENT_NAME;
import static com.bpal.mychats.Utils.Const.SOCKET_ID;
import static com.bpal.mychats.Utils.Const.USERNAME;
import static com.bpal.mychats.Utils.Const.getFeedsPusher;
import static com.bpal.mychats.Utils.Const.rootRef;
import static com.bpal.mychats.Utils.Const.showToast;
import static com.bpal.mychats.Utils.ConstFire.FEEDS;

import static org.asynchttpclient.Dsl.config;
import static org.asynchttpclient.Dsl.proxyServer;

import static java.util.Collections.singleton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bpal.mychats.R;
import com.bpal.mychats.Utils.ConstFire;
import com.bpal.mychats.databinding.ActivityFeedsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.Pusher;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.pusher.rest.PusherAsync;
import com.pusher.rest.data.Result;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.EncryptedPrivateKeyInfo;

public class FeedsActivity extends AppCompatActivity {

    private static final String CHANNEL = "channel";
    ActivityFeedsBinding binding;
    FeedAdapter adapter;
    LinearLayoutManager layoutManager;
    List<Event> eventList;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    String time = null, message, username;
    Pusher pusher;
    PrivateChannel privateChannel;
    Channel channel;
    PusherOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(CHANNEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        username = getIntent().getStringExtra(USERNAME);

        binding.textView.setText(username+" Feeds (Me)");
        recyclerView = findViewById(R.id.rvfeed);

        eventList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);

        rootRef.child(FEEDS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Event event = snap.getValue(Event.class);
                        time = snap.getKey();
                        eventList.add(event);
                    }
                    adapter = new FeedAdapter(getApplicationContext(), eventList, time);
                    adapter.notifyItemRangeChanged(adapter.getItemCount(), eventList.size());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        options = new PusherOptions();
        options.setCluster(APP_CLUSTER);
        options.setEncrypted(true);

        HttpAuthorizer authorizer = new HttpAuthorizer("https://frozen-caverns-77981.herokuapp.com/pusher/auth");
        options.setAuthorizer(authorizer);

        pusher = new Pusher(APP_KEY, options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    Log.i("Pusher", "State changed from " + change.getPreviousState() +
                            " to " + change.getCurrentState()+"; "+SOCKET_ID);
                    SOCKET_ID = pusher.getConnection().getSocketId();
                }

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

        privateChannel = pusher.subscribePrivate( CHANNEL_NAME, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.i("Pusher", "onAuthenticationFailure:======== " + message+"; "+e.toString());
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                authorizer.authorize("private-feed", SOCKET_ID);
                Log.i("Pusher", "onSubscriptionSucceeded:======== " + channelName);
                showToast(getApplicationContext(), "Subscription Successful to "+channelName, FeedsActivity.this);
                setFeeds();
            }

            @Override
            public void onEvent(PusherEvent event) {

            }
        });

        channel = pusher.subscribe("feed");
        channel.bind(EVENT_NAME, new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received event with data:======== " + event.toString());
                getFeeds(event);
            }
        });

    }

    private void setFeeds() {
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = binding.edMsg.getText().toString();
                if (message.isEmpty()){
                    showToast(getApplicationContext(), "Comment is required!", FeedsActivity.this);
                } else {
                    try {
                       // privateChannel.trigger(EVENT_NAME, String.valueOf(Collections.<String>singleton("event=feeds, data=" + message + ", channel=feed")));
                        Event data = new Event(username, EVENT_NAME, message);
                        privateChannel.trigger(EVENT_NAME, String.valueOf(data));
                        showToast(getApplicationContext(), "Comment: "+message+" is sent.", FeedsActivity.this);
                        binding.edMsg.setText("");
                        rootRef.child(FEEDS).child(String.valueOf(System.currentTimeMillis())).setValue(data);
                        Log.i("Pusher", "onEvent:======== " + message+"==="+username);

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void getFeeds(PusherEvent e) {

        Log.i("Pusher", "Received event with data:======== " + e.getChannelName()+"; "+e.getEventName() +"; " +e.getData());
        Event data = new Event(e.getChannelName(), e.getEventName(), e.getData());
        rootRef.child(FEEDS).child(String.valueOf(System.currentTimeMillis())).setValue(data);

    }
}