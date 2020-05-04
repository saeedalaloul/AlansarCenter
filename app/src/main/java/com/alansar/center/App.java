package com.alansar.center;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class App extends Application {
    private static final String[] ID = {"com.alansar.center.Downloads", "com.alansar.center.ExamsRequests"};
    private static final String[] Name = {"التحميلات", "طلبات الإختبارات"};
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        for (int i = 0; i < ID.length; i++) {
            NotificationChannel notificationChannel = new NotificationChannel(ID[i], Name[i], NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getNotificationManager().createNotificationChannel(notificationChannel);
        }
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
