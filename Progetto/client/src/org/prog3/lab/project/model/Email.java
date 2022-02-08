package org.prog3.lab.project.model;

import java.io.Serializable;

public class Email implements Serializable{ //Serializazble server per preparare i dati per poi essere inviato sul network

    private final String id;
    private final String type;
    private final String sender;
    private final String receivers;
    private final String object;
    private final String date;
    private final String text;

    public Email(String id, String type, String sender, String receivers, String object, String date, String text){
        this.id = id;
        this.type = type;               //il type indica l'operazione che vado eseguire che puo essere sendedEmail oppure recivedEmail
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.date = date;               //data usato per ordinare le date all' interno della lista
        this.text = text;               //contenuto        
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
        if(this.type.equals("receivedEmails"))
            return this.sender + " - " + this.date + "\n" + this.object;
        else
            return this.receivers + " - " + this.date + "\n" + this.object;
    }
}
