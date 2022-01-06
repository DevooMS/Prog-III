package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class LogoutTask implements Runnable{

    private final User user;
    private Semaphore serverSemaphore;
    private Semaphore connectionSemaphore;
    private final ExecutorService logThreads;
    private String data;

    public LogoutTask(User user, Semaphore serverSemaphore, Semaphore connectionSemaphore, ExecutorService logThreads){
        this.user = user;
        this.serverSemaphore = serverSemaphore;
        this.connectionSemaphore = connectionSemaphore;
        this.logThreads = logThreads;
    }

    public void run(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        data = formatter.format(LocalDateTime.now());

        try {
            File fileAccess = new File("./server/src/org/prog3/lab/project/resources/log/access/"+user.getUserEmail());
            FileWriter fileAccessWriter = new FileWriter(fileAccess, true);

            FileWriter fwState = new FileWriter("./server/src/org/prog3/lab/project/resources/log/state/"+user.getUserEmail());
            BufferedWriter writeState = new BufferedWriter(fwState);

            serverSemaphore.acquire();

            fileAccessWriter.write("logout - "+ data+"\n");

            fileAccessWriter.close();

            writeState.write("unlogged");
            writeState.flush();
            writeState.close();
            fwState.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally{
            serverSemaphore.release();
        }

        logThreads.execute(new LogTask(connectionSemaphore, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "logout connection"));

    }
}
