package com.bpal.mychats.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.bpal.mychats.MainActivity;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class firebaseMessaging extends FirebaseMessagingService {

    User user = Const.currentUser;
    Intent resultIntent;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (user != null)
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::generateToken);
    }

    private void generateToken(String token) {
        Token data = new Token(token, user.getUid());
        Const.tokenRef.child(user.getUid()).setValue(data);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Log.e( "sendNotification: ", notification.getBody()+"\n"+notification.getTitle() );
        if (notification != null) {

            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Random rand = new Random();
            int i = rand.nextInt(100000000);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), i, resultIntent, 0);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification builder = new Notification.Builder(this)
                    .setWhen(System.currentTimeMillis()) // check params
                    .setSmallIcon(R.drawable.ic_send_24)
                    .setFullScreenIntent(pendingIntent, true)
                    .setStyle(new Notification.BigTextStyle())
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(i, builder);
        }
    }

}
