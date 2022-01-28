package org.prog3.lab.project.threadModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class LogTask implements Runnable{

    private final Semaphore sem;
    private final String path;
    private final String operation;
    private final String date;

    public LogTask(Semaphore sem, String path, String operation, String date) {
        this.sem = sem;
        this.path = path;
        this.operation = operation;
        this.date = date;
    }

    public void run(){
        File fileLog = new File(path);
        FileWriter fileLogWriter;

        try {
            fileLogWriter = new FileWriter(fileLog, true);

            try {
                sem.acquire();

                fileLogWriter.write(operation+" - " + date + "\n");

                fileLogWriter.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                sem.release();
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
