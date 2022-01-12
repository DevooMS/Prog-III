package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class LogoutTask implements Runnable{

    private final User user;
    private Semaphore accessSem;
    private Semaphore connectionSem;
    private final ExecutorService logThreads;

    public LogoutTask(User user, Semaphore accessSem, Semaphore connectionSem, ExecutorService logThreads){
        this.user = user;
        this.accessSem= accessSem;
        this.connectionSem = connectionSem;
        this.logThreads = logThreads;
    }

    public void run(){

        logThreads.execute(new LogTask(accessSem, "./server/src/org/prog3/lab/project/resources/log/logout/"+user.getUserEmail(), "logout"));

        logThreads.execute(new LogTask(connectionSem, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "logout connection"));

    }
}
