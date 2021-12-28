package org.prog3.lab.project.model;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class User implements Serializable {
    private String userEmail;
    private Semaphore readWrite;

    public User(String userEmail) {
        this.userEmail = userEmail;
        this.readWrite = new Semaphore(1);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Semaphore getReadWrite() {
        return readWrite;
    }
}
