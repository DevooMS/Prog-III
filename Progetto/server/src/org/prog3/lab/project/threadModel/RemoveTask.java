package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.File;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class RemoveTask implements Runnable {

    private final User user;
    private final Semaphore connectionSem;
    private final Semaphore removeSem;
    private final ExecutorService logThreads;
    private final String path;
    private final String file_name;
    private final ObjectOutputStream outStream;
    private final DateTimeFormatter logDateFormatter;

    public RemoveTask(User user, Semaphore conncectionSem, Semaphore removeSem, ExecutorService logThreads, String path, String file_name, ObjectOutputStream outStream) {
        this.user = user;
        this.connectionSem = conncectionSem;
        this.removeSem = removeSem;
        this.logThreads = logThreads;
        this.path = path;
        this.file_name = file_name;
        this.outStream = outStream;
        logDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }

    public void run(){

        String logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(connectionSem, Objects.requireNonNull(getClass().getResource("../resources/log/connection/" + user.getUserEmail())).getPath(), "remove connection", logDate));

        try {
            user.getReadWrite().acquire();

            File file_remove = new File(path + file_name);                              //prendo il path preso da ServerThread

            String response;

            if(file_remove.delete()) {

                logDate = logDateFormatter.format(LocalDateTime.now());
                logThreads.execute(new LogTask(removeSem, Objects.requireNonNull(getClass().getResource("../resources/log/remove/" + user.getUserEmail())).getPath(), "remove email", logDate));

                response = "remove_correct";
            } else {

                logDate = logDateFormatter.format(LocalDateTime.now());
                logThreads.execute(new LogTask(removeSem, Objects.requireNonNull(getClass().getResource("../resources/log/remove/" + user.getUserEmail())).getPath(), "remove error", logDate));

                response = "remove_error";
            }

            outStream.writeObject(response);

        } catch(Exception e){
            System.out.println(e.getMessage());
        } finally {
            user.getReadWrite().release();
        }

    }

}
