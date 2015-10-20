package com.andrewsosa.quietly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by andrewsosa on 10/20/15.
 */
public class Scheduler {

    public static String EVENT_ID = "eventid";
    private static int REQUEST_CODE = 1337;

    private static PendingIntent makeStartingPendingIntent(Context c, Event e) {
        Intent intent = new Intent(c, AlarmReceiver.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.putExtra(EVENT_ID, e.getCustomId());
        intent.putExtra("starting", true);
        return PendingIntent.getBroadcast(c, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private static PendingIntent makeEndingPendingIntent(Context c, Event e) {
        Intent intent = new Intent(c, AlarmReceiver.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.putExtra(EVENT_ID, e.getCustomId());
        intent.putExtra("starting", false);
        return PendingIntent.getBroadcast(c, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
    }


    public static void setAlarmsForEvent(Context c, Event event) {

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        PendingIntent spi = makeStartingPendingIntent(c, event);
        PendingIntent epi = makeEndingPendingIntent(c, event);

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        // Times for start and end
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(System.currentTimeMillis());
        startTime.set(Calendar.HOUR_OF_DAY, event.getStartHour());
        startTime.set(Calendar.MINUTE, event.getEndHour());
        if(startTime.before(now)) startTime.add(Calendar.DATE, 1);

        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(System.currentTimeMillis());
        endTime.set(Calendar.HOUR_OF_DAY, event.getEndHour());
        endTime.set(Calendar.MINUTE, event.getEndMinute());
        if(endTime.before(now)) startTime.add(Calendar.DATE, 1);


        // Set alarms
        alarmManager.setRepeating(AlarmManager.RTC, startTime.getTimeInMillis(), 86400000, spi);
        alarmManager.setRepeating(AlarmManager.RTC, endTime.getTimeInMillis(), 86400000, epi);

        Log.d("Set Alarms", "Set alarms for event: " + event.getLabel() + " at " +
                event.getStringStart() + ", " + event.getStringEnd());


        //ComponentName receiver = new ComponentName(context, BootReceiver.class);
        //PackageManager pm = context.getPackageManager();

        //pm.setComponentEnabledSetting(receiver,
        //        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        //        PackageManager.DONT_KILL_APP);
    }

    public static void cancelAlarmForEvent(Context c, Event event) {

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        // If alarms have been set for event, cancel them.
        if (alarmManager != null) {
            alarmManager.cancel(makeStartingPendingIntent(c, event));
            alarmManager.cancel(makeEndingPendingIntent(c, event));


            Log.d("Set Alarms", "Removed alarms for event: " + event.getLabel() + " at " +
                    event.getStringStart() + ", " + event.getStringEnd());

        }
    }

}
