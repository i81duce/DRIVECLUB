package com.theironyard;//Created by KevinBozic on 2/25/16.

public class Event {
    int id;
    String userName;
    String car;
    String time;
    String trackName;

    public Event() {

    }

    public Event(int id, String userName, String car, String time, String trackName) {
        this.id = id;
        this.userName = userName;
        this.car = car;
        this.time = time;
        this.trackName = trackName;
    }
}
