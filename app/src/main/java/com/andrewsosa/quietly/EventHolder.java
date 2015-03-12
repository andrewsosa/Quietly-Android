package com.andrewsosa.quietly;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrewsosa on 12/16/14.
 */
public class EventHolder {

    private static ArrayList<Event> eventList = new ArrayList<>();
    private static HashMap<Integer, Event> eventMap = new HashMap<>();

    public static int addEvent(Event event) {
        eventList.add(event);
        return eventList.indexOf(event);
    }

    public static void addEvent(int position, Event event) {
        eventList.add(position, event);
    }

    public static Event getEvent(int position) {
        return eventList.get(position);
    }

    public static Event removeEvent(int position) {
        Event e = eventList.get(position);
        eventList.remove(position);
        return e;
    }

    public static int removeEvent(Event event) {
        int i = eventList.indexOf(event);
        eventList.remove(event);
        return i;
    }

    public static int indexOf(Event event) {
        return eventList.indexOf(event);
    }

    public static ArrayList<Event> getEventList() {
        return eventList;
    }


}
