package com.manasshaktiiui.actions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.manasshaktiiui.R;

/**
 * Android Notification Action handler.
 */
public class NotificationAction {

    private static final String TAG = "NotificationAction";
    private static final String CHANNEL_ID = "ManasShaktiiInterventions";
    private static final int NOTIFICATION_ID = 1002;

    public static void showNotification(Context context, String title, String message) {
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Mindful Interventions",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title != null ? title : "MANASHAKTII Guidance")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            manager.notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Notification displayed successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to display notification", e);
        }
    }
}
