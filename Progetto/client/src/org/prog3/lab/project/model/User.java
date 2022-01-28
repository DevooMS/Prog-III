package org.prog3.lab.project.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class User implements Serializable {
    private final String userEmail;
    private final Semaphore readWrite;
    private final ArrayList<Email> receivedEmails;
    private final ArrayList<Email> sendedEmails;

    public User(String userEmail) {
        this.userEmail = userEmail;
        this.readWrite = new Semaphore(1);
        receivedEmails = new ArrayList<>();
        sendedEmails = new ArrayList<>();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Semaphore getReadWrite() {
        return readWrite;
    }

    public ArrayList<Email> getReceivedEmails() {
        return receivedEmails;
    }

    public ArrayList<Email> getSendedEmails() {
        return sendedEmails;
    }

    public void addReceivedEmail(Email email) {
        receivedEmails.add(email);
        sortList(receivedEmails);
    }

    public void addSendedEmails(Email email) {
        sendedEmails.add(email);
        sortList(sendedEmails);
    }

    public void removeReceivedEmail(Email email){ receivedEmails.remove(email); }

    public void removeSendedEmail(Email email){ sendedEmails.remove(email); }

    private void sortList(ArrayList<Email> list){
        Collections.sort(list, (e1, e2) -> {
            if(e1 != null && e2 != null) {
                if (e1.getDate().compareTo(e2.getDate()) > 0) {
                    return -1;
                } else if (e1.getDate().compareTo(e2.getDate()) < 0) {
                    return 1;
                }
            }
            return 0;
        });
    }
}
