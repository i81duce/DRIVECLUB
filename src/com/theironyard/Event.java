package com.theironyard;//Created by KevinBozic on 2/25/16.

public class Event {
    String onlineId;
    String car;
    String time;
    String trackName;
    int id;

    public Event(String onlineId, String car, String time, String trackName, int id) {
        this.onlineId = onlineId;
        this.car = car;
        this.time = time;
        this.trackName = trackName;
        this.id = id;
    }
}
