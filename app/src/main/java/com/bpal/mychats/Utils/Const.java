package com.bpal.mychats.Utils;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bpal.mychats.Models.User;
import com.bpal.mychats.Services.APIService;
import com.bpal.mychats.Services.MyResponse;
import com.bpal.mychats.Services.Notification;
import com.bpal.mychats.Services.RetrofitClient;
import com.bpal.mychats.Services.Sender;
import com.bpal.mychats.Services.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pusher.client.PusherOptions;
import com.pusher.client.Pusher;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Const {

    //pusher feeds
    public static final String APP_KEY = "040996f203fbba0bd2d1";
    public static final String APP_ID = "1282169";
    public static final String APP_SECRET = "08c386b919c93451d7f4";
    public static final String CHANNEL_NAME = "brijesh-development";
    public static final String EVENT_NAME = "feeds";
    public static final String APP_CLUSTER = "ap2";
    public static String SOCKET_ID = "";

    public static Pusher getFeedsPusher(){
        PusherOptions options = new PusherOptions();
        options.setCluster(APP_CLUSTER);
        Pusher pusher = new Pusher(APP_KEY, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                SOCKET_ID = getFeedsPusher().getConnection().getSocketId();
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
        return pusher;
    }

    public static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(ConstFire.ROOT);
    public static DatabaseReference rootUserRef = FirebaseDatabase.getInstance().getReference(ConstFire.ROOT).child(ConstFire.USER);
    public static DatabaseReference rootChatRef = FirebaseDatabase.getInstance().getReference(ConstFire.ROOT).child(ConstFire.CHATS);
    public static DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(ConstFire.ROOT).child(ConstFire.TOKEN);

    public static FirebaseAuth rootAuth = FirebaseAuth.getInstance();

    public static User currentUser;
    public static User ChatUser;

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }


    public static void sendNotification(String receiverID, String message, Context context){
        tokenRef.child(receiverID);
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                  for (DataSnapshot snap : snapshot.getChildren()) {
                      Token token = snap.getValue(Token.class);
                      Log.d("===KEY1====", snap.getValue().toString());
                      Notification notification = new Notification("My Chats App", message);
                      Sender sender = new Sender(Objects.requireNonNull(token).getToken(), notification);
                      getFCMService().sendNotification(sender).enqueue(new Callback<MyResponse>() {
                          @Override
                          public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                              if (response.code() == 200) {
                                  if (response.body() != null) {
                                      if (response.body().success == 1)
                                          Log.d("===SENT====", "YES");
                                      else
                                          Log.d("===SENT====", "NO");
                                  }
                              }
                          }

                          @Override
                          public void onFailure(Call<MyResponse> call, Throwable t) {

                          }
                      });
                  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String DateTime(Long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy HH:mm",calendar).toString();
        return date;
    }

    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, LENGTH_SHORT).show();
    }
}
