package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class AccessTask implements Runnable{

    private final User user;
    private final Semaphore connectionSem;
    private final Semaphore accessSem;
    private final ExecutorService logThreads;
    private final String accessFilePath;
    private final String operation;
    private final DateTimeFormatter logDateFormatter;

    public AccessTask(User user, Semaphore connectionSem, Semaphore accessSem, ExecutorService logThreads, String operation, String accessFilePath) {
        this.user = user;
        this.connectionSem = connectionSem;
        this.accessSem = accessSem;
        this.logThreads = logThreads;
        this.operation = operation;
        this.accessFilePath = accessFilePath;
        logDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }

    public void run(){
        String logDate = logDateFormatter.format(LocalDateTime.now());

        logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "open "+ operation +" connection", logDate));

        logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(accessSem, accessFilePath, operation, logDate));

        logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "close "+ operation +" connection", logDate));
    }
}
