package com.andrewsosa.quietly;

import android.app.NotificationManager;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.UUID;

/**
 *  @author     Andrew Sosa     andrewsosa001@gmail.com
 *  @version    1.0
 *  @since      2015-10-01
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Serializable {


    public static final String ACTIVE = "ACTIVE";
    public static final String START_HOUR = "START_HOUR";
    public static final String START_MINUTE = "START_MINUTE";
    public static final String END_HOUR = "END_HOUR";
    public static final String END_MINUTE = "END_MINUTE";
    public static final String LABEL = "LABEL";
    public static final String DURATION = "DURATION";
    public static final String FILTER = "FILTER";
    public static final String CUSTOM_ID = "customID";

    public static final int ALL = 0;
    public static final int PRIORITY = 1;
    public static final int ALARMS = 2;
    public static final int NONE = 3;

    public static final boolean DEFAULT = true;
    public static final int DEFAULT_START_HOUR = 8;
    public static final int DEFAULT_END_HOUR = 17;

    public Event() {

    }

    public Event(int filter) {
        setFilter(filter);
        setActive(DEFAULT);
        setLabel("Event");
        setStartTime(DEFAULT_START_HOUR, 0);
        setEndTime(DEFAULT_END_HOUR, 0);
        put(CUSTOM_ID, UUID.randomUUID().toString());
    }


    /**
     * Event with default name and state at specified time.
     * @param hour      Hour of the day for event.
     * @param minute    Minute of hour for event.
     */
    public Event(String filter, int hour, int minute) {
        super(filter);
        setStartTime(hour, minute);

    }

    /**
     * Event with default state, custom time and display name.
     * @param label     Display name for event.
     * @param hour      Hour of the day for event.
     * @param minute    Minute of hour for event.
     */
    public Event(String filter, String label, int hour, int minute) {
        this(filter, hour, minute);
        setLabel(label);
    }

    /**
     * Event with all customized parameters.
     * @param label     Display name for event.
     * @param hour      Hour of day for event.
     * @param minute    Minute of hour for event.
     * @param active    Whether event will trigger a reminder.
     */
    public Event(String filter, String label, int hour, int minute, boolean active) {
        this(filter, label, hour, minute);
        setActive(active);

    }

    public String getCustomId() {
        return getString(CUSTOM_ID);
    }

    public boolean isActive() {
        return getBoolean(ACTIVE);
    }

    public void toggleActive() {
        setActive(!isActive());
    }

    public void setActive(boolean active) {
        put(ACTIVE, active);
        pinInBackground();
    }


    public String getLabel() {
        return getString(LABEL);
    }

    public void setLabel(String label) {
        put(LABEL, label);
        pinInBackground();
    }

    public void setStartHour(int hour) {
        put(START_HOUR, hour);
    }

    public int getStartHour() {
        return getInt(START_HOUR);
    }

    public void setStartMinute(int minute) {
        put(START_MINUTE, minute);
    }

    public int getStartMinute() {
        return getInt(START_MINUTE);
    }

    public void setStartTime(int hour, int minute) {

        setStartHour(hour);
        setStartMinute(minute);

        updateComparableTime();
        if(has(END_HOUR)) calculateDuration();

        //int comparableTime = (hour * 60) + minute;
        //put("comparableTime", comparableTime);

        pinInBackground();
    }

    public void setEndTime(int hour, int minute) {
        setEndHour(hour);
        setEndMinute(minute);
        if(has(START_HOUR)) calculateDuration();

    }

    public void setEndHour(int hour) {
        put(END_HOUR, hour);
    }

    public void setEndMinute(int minute) {
        put(END_MINUTE, minute);
    }

    public int getEndHour() {
        return getInt(END_HOUR);
    }

    public int getEndMinute() {
        return getInt(END_MINUTE);
    }

    public void setFilter(int filter) {
        put(FILTER, filter);
    }

    public int getFilter() {
        return getInt(FILTER);
    }

    public void calculateDuration() {
        int startHour = getStartHour();
        int startMinute = getStartMinute();

        int endHour = getEndHour();
        int endMinute = getEndMinute();

        int dHour = endHour - startHour;
        int dMinute = endMinute - startMinute;

        if(dHour < 0) dHour += 24;
        if(dMinute < 0) dMinute += 60;

        int duration = ((dHour * 60) + dMinute) * 60;
        put(DURATION, duration);
    }

    public int getDuration() {
        return getInt(DURATION);
    }

    /**
     * @return Returns a HH:MM AM/PM format string for the event's time.
     */
    public String getStringTime(int hourOfDay, int minute) {
        //int minute = getStartMinute();
        //int hourOfDay = getStartHour();

        String tempMinute = (minute >= 10) ? "" + minute : "0" + minute;

        String tempHour;

        if      (hourOfDay == 0 || hourOfDay == 12) tempHour = "" + 12;
        else if (hourOfDay > 12) tempHour = "" + (hourOfDay % 12);
        else    tempHour = "" + hourOfDay;

        String AMPM = (hourOfDay < 12) ? "AM" : "PM";

        return tempHour + ":" + tempMinute + " " + AMPM;
    }

    public String getStringFilter() {
        switch (getFilter()) {
            case 0: return "All notifications";
            case 1: return "Priority Only";
            case 2: return "Alarms Only";
            case 3: return "Total Silence";
            default: return "Do Not Disturb";
        }
    }

    public String getStringStart() {
        return getStringTime(getStartHour(), getStartMinute());
    }

    public String getStringEnd() {
        return getStringTime(getEndHour(), getEndMinute());
    }

    @Override
    public String toString() {
        return getString(CUSTOM_ID);
    }

    public static ParseQuery<Event> getQuery() {
        return new ParseQuery<>(Event.class).fromLocalDatastore();
    }

    public void onUpdate() {
        updateComparableTime();
    }

    public void updateComparableTime() {
        int comparableTime = (getStartHour() * 60) + getStartMinute();
        put("comparableTime", comparableTime);
    }

    public int getComparableTime() {
        return getInt("comparableTime");
    }

    public int getManagerFilter() {
        switch(getFilter()) {
            case 0:
                return NotificationManager.INTERRUPTION_FILTER_ALL;
            case 1:
                return NotificationManager.INTERRUPTION_FILTER_PRIORITY;
            case 2:
                return NotificationManager.INTERRUPTION_FILTER_ALARMS;
            case 3:
                return NotificationManager.INTERRUPTION_FILTER_NONE;
            default:
                return NotificationManager.INTERRUPTION_FILTER_ALL;
        }
    }

    
}
