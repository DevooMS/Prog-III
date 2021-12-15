package org.prog3.lab.project.model;

import java.util.ArrayList;
import java.util.List;

public class Email {

    private final String id;
    private final String type;
    private final String sender;
    private final String receivers;
    private final String object;
    private final String date;
    private final String text;

    public Email(String id, String type, String sender, String receivers, String object, String date, String text){
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.date = date;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceivers() {
        return receivers;
    }

    public String getObject() {
        return object;
    }

    public String getDate() {
        return date;
    }


    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.sender + " - " + this.date + "\n" + this.object;
    }

}
