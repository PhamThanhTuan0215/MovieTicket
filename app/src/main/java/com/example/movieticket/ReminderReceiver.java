package com.example.movieticket;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Date;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("Reminder")) {
            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");

            Notification notification = new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                    .setContentTitle("Nhắn nhở lịch chiếu")
                    .setContentText("Lịch chiếu cho phim " + name + " sắp diễn ra (" + date + ")")
                    .setSmallIcon(R.drawable.icon_notifications_24)
                    .setColor(Color.RED)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            if(notificationManager != null) {
                notificationManager.notify(getNotificationId(), notification);
                Log.d("message", "đến hẹn: " + name);
            }
        }
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}
