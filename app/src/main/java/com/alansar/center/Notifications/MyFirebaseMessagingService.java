package com.alansar.center.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Activitys.MohafezActivity;
import com.alansar.center.Moshref.Activity.MoshrefActivity;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Activitys.SuperVisorExamsActivity;
import com.alansar.center.testers.Activitys.TesterActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        Log.d("sss", "" + remoteMessage.getData());
        if (Common.currentPerson != null && Common.currentPerson.getId().equals(sent)) {
            if (Common.currentPerson.getId().equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOAndAboveNotifiactions(remoteMessage);
                } else {
                    sendNormalNotifications(remoteMessage);
                }
            }
        }
    }

    private void sendNormalNotifications(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String typeActivity = remoteMessage.getData().get("typeActivity");
        String typeFragment = remoteMessage.getData().get("typeFragment");
        Intent intent = null;
        if (typeActivity != null && typeFragment != null) {
            switch (typeActivity) {
                case "MohafezActivity":
                    intent = new Intent(this, MohafezActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_MOHAFEZ);
                    if (typeFragment.equals("Orders_Exams_Fragment")) {
                        intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    } else if (typeFragment.equals("ExamsFragment")) {
                        intent.putExtra("typeFragment", "ExamsFragment");
                    }
                    break;
                case "MoshrefActivity":
                    intent = new Intent(this, MoshrefActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_SUPER_VISOR);
                    intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    break;
                case "SuperVisorExamsActivity":
                    intent = new Intent(this, SuperVisorExamsActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_SUPER_VISOR_EXAMS);
                    if (typeFragment.equals("Orders_Exams_Fragment")) {
                        intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    } else if (typeFragment.equals("ExamsFragment")) {
                        intent.putExtra("typeFragment", "ExamsFragment");
                    }
                    break;
                case "TesterActivity":
                    intent = new Intent(this, TesterActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_TESTER);
                    intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    break;
            }
        }
        assert user != null;
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Bundle bundle = new Bundle();
        bundle.putString(user, user);
        if (intent != null) {
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder;
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo_ansar)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setColor(Color.GREEN)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo_ansar)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ansar))
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            if (manager != null) {
                if (builder != null) {
                    manager.notify(j + Timestamp.now().getNanoseconds(), builder.build());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotifiactions(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String typeActivity = remoteMessage.getData().get("typeActivity");
        String typeFragment = remoteMessage.getData().get("typeFragment");
        Intent intent = null;
        if (typeActivity != null && typeFragment != null) {
            switch (typeActivity) {
                case "MohafezActivity":
                    intent = new Intent(this, MohafezActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_MOHAFEZ);
                    if (typeFragment.equals("Orders_Exams_Fragment")) {
                        intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    } else if (typeFragment.equals("ExamsFragment")) {
                        intent.putExtra("typeFragment", "ExamsFragment");
                    }
                    break;
                case "MoshrefActivity":
                    intent = new Intent(this, MoshrefActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_SUPER_VISOR);
                    intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    break;
                case "SuperVisorExamsActivity":
                    intent = new Intent(this, SuperVisorExamsActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_SUPER_VISOR_EXAMS);
                    if (typeFragment.equals("Orders_Exams_Fragment")) {
                        intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    } else if (typeFragment.equals("ExamsFragment")) {
                        intent.putExtra("typeFragment", "ExamsFragment");
                    }
                    break;
                case "TesterActivity":
                    intent = new Intent(this, TesterActivity.class);
                    intent.putExtra("Permission", "" + Common.PERMISSIONS_TESTER);
                    intent.putExtra("typeFragment", "Orders_Exams_Fragment");
                    break;
            }
        }
        assert user != null;
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Bundle bundle = new Bundle();
        bundle.putString(user, user);
        if (intent != null) {
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            NotificationCompat.Builder builder = notification1.getONotification(title, body, pendingIntent, soundUri, user);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            if (manager != null) {
                Common.getNotificationManager(manager, this).notify(j + Timestamp.now().getNanoseconds(), builder.build());
            }
        }
    }
}