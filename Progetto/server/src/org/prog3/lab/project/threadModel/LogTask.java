package org.prog3.lab.project.threadModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class LogTask implements Runnable{

    private Semaphore sem;
    private final String path;
    private final String operation;

    public LogTask(Semaphore sem, String path, String operation) {
        this.sem = sem;
        this.path = path;
        this.operation = operation;
    }

    public void run(){
        File fileConnection = new File(path);
        FileWriter fileConnectionWriter = null;

        try {
            fileConnectionWriter = new FileWriter(fileConnection, true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String data = formatter.format(LocalDateTime.now());

            try {
                sem.acquire();

                fileConnectionWriter.write(operation+" - " + data + "\n");

            } finally {
                sem.release();
                fileConnectionWriter.close();
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
