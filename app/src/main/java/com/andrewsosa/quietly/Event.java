package com.andrewsosa.quietly;

import java.io.Serializable;

public class Event implements Serializable {

    // Basic Info
    private String label;
    private int hourOfDay;
    private int minute;
    private boolean active;

    // Start time
    // End time


    //
    // Constructor chain
    //

    private Event() {
        // Default active is on
        this.active = true;
    }

    private Event(int hour, int minute) {
        this();
        this.hourOfDay = hour;
        this.minute = minute;
    }

    public Event(String label, int hour, int minute) {
        this(hour, minute);
        this.label = label;
    }

    public Event(String label, int hourOfDay, int minute, boolean active) {
        this.label = label;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.active = active;
    }

    //
    // "Getters"
    //

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {active = !active;}

    public String getLabel() {
        return label;
    }

    public String getStringTime() {
        String tempMinute = (minute >= 10) ? "" + minute : "0" + minute;

        String tempHour;

        if(hourOfDay == 0 || hourOfDay == 12) tempHour = "" + 12;
        else if (hourOfDay > 12) tempHour = "" + (hourOfDay % 12);
        else tempHour = "" + hourOfDay;

        String AMPM = (hourOfDay < 12) ? "AM" : "PM";

        return tempHour + ":" + tempMinute + " " + AMPM;
    }

    //
    // Setters
    //


    public void setLabel(String label) {
        this.label = label;
    }

    public void setTime(int hour, int minute) {
        this.hourOfDay = hour;
        this.minute = minute;
    }
}
