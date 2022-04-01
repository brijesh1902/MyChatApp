package com.bpal.mychats;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class App extends Application {

    public static final String CHANNEL_ID = "ID";
    public static final String CHANNEL_NAME = "NAME";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Chat");
            channel.enableVibration(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.createNotificationChannel(channel);
        }

    }
}
