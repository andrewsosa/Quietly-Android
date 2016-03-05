package com.andrewsosa.quietly;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("onReceive", "Received RTC alarm");
        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            String id = intent.getStringExtra(Event.CUSTOM_ID);
            Event event = Event.getQuery().whereEqualTo(Event.CUSTOM_ID, id).getFirst();
            boolean starting = intent.getBooleanExtra("starting", false);

            Log.d("onReceive", "Receiving alarm for event " + event.getLabel());

            if(event.isActive()) {
                if (starting) {
                    n.setInterruptionFilter(event.getManagerFilter());
                } else {
                    n.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    // TODO RETURN TO PREVIOUS STATE
                }

                Notification not = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_24dp)
                        .setContentTitle("Changed Filter level")
                        .setContentText(event.getLabel())
                        .build();

                n.notify(317, not);
            }
        } catch (Exception e) {
            Log.e("OnReceive", e.getMessage());

            Notification not = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notifications_24dp)
                    .setContentTitle("Exception receiving alarm.")
                    .build();

            n.notify(317, not);

        }

        // Change the filter state for given receive

    }


}
