package com.andrewsosa.quietly;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, @Nullable Intent intent) {

        Log.d("onReceive", "Received RTC alarm");
        try {
            Event e = Event.getQuery().whereEqualTo(Event.CUSTOM_ID, intent.getStringExtra(Scheduler.EVENT_ID)).getFirst();
            boolean starting = intent.getBooleanExtra("starting", false);
            NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d("onReceive", "Receiving alarm for event " + e.getLabel());

            if(starting) {
                n.setInterruptionFilter(e.getManagerFilter());
            } else {
                n.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                // TODO RETURN TO PREVIOUS STATE
            }

            Notification not = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notifications_24dp)
                    .setContentTitle("Changed Filter level")
                    .setContentText("Change settings in Quietly app")
                    .build();

            n.notify(317, not);

        } catch (Exception e) {
            Log.e("OnReceive", e.getMessage());
        }

        // Change the filter state for given receive

    }


}
