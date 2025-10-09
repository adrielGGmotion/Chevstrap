package com.chevstrap.rbx.UI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Logger;
import com.chevstrap.rbx.Models.Entities.ActivityData;
import com.chevstrap.rbx.R;

public class NotifyIconWrapper {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int NOTIFICATION_ID = 1001;
    private static void runOnUiThread(Runnable action) {
        mainHandler.post(action);
    }

    public static void showConnectionNotification(Context context, String ip) {
        Logger logger = App.getLogger();

        logger.writeLine("NotifyIconWrapper::ShowAlert", ip);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            logger.writeLine("NotifyIconWrapper::ShowAlert", "NotificationManager is null");
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                "rbx_connection_channel",
                "Roblox Connection",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);

        ActivityData data = new ActivityData(ip);

        data.queryServerLocation(new ActivityData.LocationCallback() {
            @Override
            public void onLocationResolved(String location) {
                String message = App.getTextLocale(App.getAppContext(), R.string.notification_server_location_success) + " " + location;
                logger.writeLine("NotifyIconWrapper::ShowAlert", message);

                runOnUiThread(() -> {
                    Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.chevstrap_logo);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rbx_connection_channel")
                            .setSmallIcon(R.drawable.chevstrap_logo)
                            .setLargeIcon(bigIcon)
                            .setContentTitle(App.getTextLocale(App.getAppContext(), R.string.notification_connected_to_a_server))
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                });
            }

            @Override
            public void onFailure() {
                logger.writeLine("NotifyIconWrapper::ShowAlert", App.getTextLocale(App.getAppContext(), R.string.notification_server_location_failed));

                runOnUiThread(() -> {
                    Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.chevstrap_logo);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rbx_connection_channel")
                            .setSmallIcon(R.drawable.chevstrap_logo)
                            .setLargeIcon(bigIcon)
                            .setContentTitle(App.getTextLocale(App.getAppContext(), R.string.notification_connected_to_a_server))
                            .setContentText(App.getTextLocale(App.getAppContext(), R.string.notification_server_location_failed))
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                });
            }
        });
    }

    public static void hideConnectionNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
