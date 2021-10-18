package com.bpal.mychats.Feeds;

import static com.bpal.mychats.Utils.Const.APP_CLUSTER;
import static com.bpal.mychats.Utils.Const.APP_ID;
import static com.bpal.mychats.Utils.Const.APP_KEY;
import static com.bpal.mychats.Utils.Const.APP_SECRET;
import static com.bpal.mychats.Utils.Const.CHANNEL_NAME;
import static com.bpal.mychats.Utils.Const.EVENT_NAME;
import static com.bpal.mychats.Utils.Const.SOCKET_ID;
import static com.bpal.mychats.Utils.Const.getFeedsPusher;
import static com.bpal.mychats.Utils.Const.rootRef;
import static com.bpal.mychats.Utils.Const.showToast;

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
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.rest.Pusher;

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
    String time = null, message, data;
    Pusher pusher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(CHANNEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        binding.textView.setText("Feeds");
        recyclerView = findViewById(R.id.rvfeed);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getFeeds();

        setFeeds();

    }

    private void setFeeds() {
        pusher = new Pusher(APP_ID, APP_KEY, APP_SECRET);
        //Pusher pusher = new Pusher("http://"+APIKEY+":"+APISECRET+"@api-"+APICLUSTER+".pusher.com/apps/app_id");
        pusher.setCluster(APP_CLUSTER);
        pusher.setEncrypted(true);
        pusher.setHost("api-"+APP_CLUSTER+".pusher.com");

        HttpClientBuilder builder = Pusher.defaultHttpClientBuilder();
        builder.setProxy(new HttpHost("proxy.example.com"));
        pusher.configureHttpClient(builder);

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = binding.edMsg.getText().toString();
                if (message.isEmpty()){
                    showToast(getApplicationContext(), "Comment is required!");
                } else {
                 try {
                      pusher.trigger(CHANNEL_NAME, EVENT_NAME, Collections.singletonMap("message", "hello world"), SOCKET_ID);
                      showToast(getApplicationContext(), "Comment is sent.");
                      binding.edMsg.setText("");
                  } catch (Exception e){
                      e.printStackTrace();
                  }
                }
            }
        });

    }

    private void getFeeds() {

        Channel channel = getFeedsPusher().subscribe(CHANNEL_NAME);

        channel.bind(EVENT_NAME, new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent e) {
                Log.i("Pusher", "Received event with data:======== " + e.getChannelName()+"; "+e.getEventName() +"; " +e.getData());
                Event data = new Event(e.getChannelName(), e.getEventName(), e.getData());
                rootRef.child(ConstFire.FEEDS).child(String.valueOf(System.currentTimeMillis())).setValue(data);
                Log.i("Pusher", "Received event with data:======== " + preferences.getString(CHANNEL, null));
            }
        });

        rootRef.child(ConstFire.FEEDS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    eventList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Event event = snap.getValue(Event.class);
                        time = snap.getKey();
                        eventList.add(event);
                    }
                    adapter = new FeedAdapter(getApplicationContext(), eventList, time);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    class SendFeedTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {

            return "";
        }
    }
}