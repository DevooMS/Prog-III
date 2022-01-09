package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class LogoutTask implements Runnable{

    private final User user;
    private Semaphore accessSemaphore;
    private Semaphore connectionSemaphore;
    private final ExecutorService logThreads;

    public LogoutTask(User user, Semaphore accessSemaphore, Semaphore connectionSemaphore, ExecutorService logThreads){
        this.user = user;
        this.accessSemaphore = accessSemaphore;
        this.connectionSemaphore = connectionSemaphore;
        this.logThreads = logThreads;
    }

    public void run(){

        logThreads.execute(new LogTask(accessSemaphore, "./server/src/org/prog3/lab/project/resources/log/access/"+user.getUserEmail(), "logout"));

        logThreads.execute(new LogTask(connectionSemaphore, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "logout connection"));

    }
}
