package com.andrewsosa.quietly;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  @author     Andrew Sosa     andrewsosa001@gmail.com
 *  @version    1.0
 *  @since      2015-6-30
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Serializable {


    public static final String ACTIVE = "ACTIVE";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String LABEL = "LABEL";

    public static final boolean DEFAULT = true;

    public Event() {

    }


    /**
     * Event with default name and state at specified time.
     * @param hour      Hour of the day for event.
     * @param minute    Minute of hour for event.
     */
    public Event(int hour, int minute) {
        setActive(DEFAULT);
        setTime(hour, minute);
        setLabel("Reminder");
    }

    /**
     * Event with default state, custom time and display name.
     * @param label     Display name for event.
     * @param hour      Hour of the day for event.
     * @param minute    Minute of hour for event.
     */
    public Event(String label, int hour, int minute) {
        setActive(DEFAULT);
        setTime(hour, minute);
        setLabel(label);
    }

    /**
     * Event with all customized parameters.
     * @param label     Display name for event.
     * @param hour      Hour of day for event.
     * @param minute    Minute of hour for event.
     * @param active    Whether event will trigger a reminder.
     */
    public Event(String label, int hour, int minute, boolean active) {
        setActive(active);
        setTime(hour, minute);
        setLabel(label);
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

    public void setHour(int hour) {
        put(HOUR, hour);
    }

    public int getHour() {
        return getInt(HOUR);
    }

    public void setMinute(int minute) {
        put(MINUTE, minute);
    }

    public int getMinute() {
        return getInt(MINUTE);
    }

    public void setTime(int hour, int minute) {
        setHour(hour);
        setMinute(minute);

        int comparableTime = (hour * 60) + minute;
        put("comparableTime", comparableTime);

        pinInBackground();
    }

    /**
     * @return Returns a HH:MM AM/PM format string for the event's time.
     */
    public String getStringTime() {
        int minute = getMinute();
        int hourOfDay = getHour();

        String tempMinute = (minute >= 10) ? "" + minute : "0" + minute;

        String tempHour;

        if      (hourOfDay == 0 || hourOfDay == 12) tempHour = "" + 12;
        else if (hourOfDay > 12) tempHour = "" + (hourOfDay % 12);
        else    tempHour = "" + hourOfDay;

        String AMPM = (hourOfDay < 12) ? "AM" : "PM";

        return tempHour + ":" + tempMinute + " " + AMPM;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static ParseQuery<Event> getQuery() {
        return new ParseQuery<>(Event.class);
    }

    public void onUpdate() {
        updateComparableTime();
    }

    public void updateComparableTime() {
        int comparableTime = (getHour() * 60) + getMinute();
        put("comparableTime", comparableTime);
    }

    
}
