package com.alansar.center.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.alansar.center.R;


public class OreoAndAboveNotification extends ContextWrapper {
    private static final String ID = "com.alansar.center.ExamsRequests";
    private static final String Name = "طلبات الإختبارات";

    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID, Name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationCompat.Builder getONotification(String title, String body, PendingIntent pendingIntent,
                                                       Uri soundUri, String icon) {
        return new NotificationCompat.Builder(getApplicationContext(), ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(Color.GREEN)
                .setOnlyAlertOnce(true)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSmallIcon(R.drawable.logo_ansar)
                .setChannelId(ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ansar));
    }
}