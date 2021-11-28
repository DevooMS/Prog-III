package org.prog3.lab.project.model;

import java.util.ArrayList;
import java.util.List;

public class Email {

    private final String id;
    private final String sender;
    private final List<String> receivers;
    private final String object;
    private final String date;
    private final String hour;
    private final String text;

    public Email(String id, String sender, List<String> receivers, String object, String date, String hour, String text){
        this.id = id;
        this.sender = sender;
        this.receivers = new ArrayList<>(receivers);
        this.object = object;
        this.date = date;
        this.hour = hour;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getObject() {
        return object;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {return hour;}

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.sender + " - " + this.date + "\n" + this.object;
    }
}
