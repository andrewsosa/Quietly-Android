package com.andrewsosa.quietly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.List;

/**
 * Created by andrewsosa on 6/30/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent incomingIntent) {
        if (incomingIntent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // Schedule alarms for all Events
            ParseQuery<Event> query = Event.getQuery();
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> list, ParseException e) {
                    if (e == null) {
                        for(Event event : list) {
                            if(event.isActive()) {
                                Scheduler.setAlarmsForEvent(context, event);
                            }
                        }
                    } else {
                        Log.e("onCreate query find", e.getMessage());
                    }
                }
            });

        }
    }

}