package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class RemoveTask implements Runnable {

    private User user;
    private Semaphore connectionSemaphore;
    private final ExecutorService logThreads;
    private String path;

    public RemoveTask(User user, Semaphore conncectionSemaphore, ExecutorService logThreads, String path) {
        this.user = user;
        this.connectionSemaphore = conncectionSemaphore;
        this.logThreads = logThreads;
        this.path = path;
    }

    public void run(){
        try {
            user.getReadWrite().acquire();

            File file_remove = new File(path);

            file_remove.delete();

            user.getReadWrite().release();

            logThreads.execute(new LogTask(connectionSemaphore, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "remove connection"));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

}
