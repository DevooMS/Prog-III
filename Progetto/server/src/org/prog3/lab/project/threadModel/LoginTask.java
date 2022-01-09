package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class LoginTask implements Runnable{
    private final String email;
    private final String password;
    private Semaphore accessSemaphore;
    private Semaphore connectionSemaphore;
    private final ExecutorService logThreads;
    private final String filePath;
    private final ObjectOutputStream outStream;

    public LoginTask(String email, String password, Semaphore accessSemaphore, Semaphore connectionSemaphore, ExecutorService logThreads, String filePath, ObjectOutputStream outStream){
        this.email = email;
        this.password = password;
        this.accessSemaphore = accessSemaphore;
        this.connectionSemaphore = connectionSemaphore;
        this.logThreads = logThreads;
        this.filePath = filePath;
        this.outStream = outStream;
    }

    public void run(){
        User user = null;
        String response = "denied";
        boolean find = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line = reader.readLine();

            while (line != null && !find) {
                if (line.compareTo(email + "-" + password) == 0) {

                    logThreads.execute(new LogTask(accessSemaphore, "./server/src/org/prog3/lab/project/resources/log/access/"+email, "login"));

                    user = new User(email);

                    logThreads.execute(new LogTask(connectionSemaphore, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "login connection"));

                    response = "accept";

                    find = true;
                }

                line = reader.readLine();
            }

            outStream.writeObject(user);

            outStream.writeObject(response);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
