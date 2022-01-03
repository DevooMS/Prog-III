package org.prog3.lab.project.model;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class User implements Serializable {
    private String userEmail;
    private Semaphore connection;
    private Semaphore access;
    private Semaphore readWrite;

    public User(String userEmail) {
        this.userEmail = userEmail;
        this.connection = new Semaphore(1);
        this.readWrite = new Semaphore(1);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Semaphore getReadWrite() {
        return readWrite;
    }

    public Semaphore getConnection() {
        return connection;
    }
}
