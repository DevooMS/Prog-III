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
    private Semaphore serverSemaphore;
    private Semaphore connectionSemaphore;
    private final ExecutorService logThreads;
    private final String filePath;
    private final ObjectOutputStream outStream;

    public LoginTask(String email, String password, Semaphore serverSemaphore, Semaphore connectionSemaphore, ExecutorService logThreads, String filePath, ObjectOutputStream outStream){
        this.email = email;
        this.password = password;
        this.serverSemaphore = serverSemaphore;
        this.connectionSemaphore = connectionSemaphore;
        this.logThreads = logThreads;
        this.filePath = filePath;
        this.outStream = outStream;
    }

    public void run(){
        User user = null;
        String response = "denied";
        String data = null;
        boolean find = false;
        boolean accept = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line = reader.readLine();

            while (line != null && !find) {
                if (line.compareTo(email + "-" + password) == 0) {
                    BufferedReader readerState = new BufferedReader(new FileReader("./server/src/org/prog3/lab/project/resources/log/state/"+email));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    data = formatter.format(LocalDateTime.now());

                    File fileAccess = new File("./server/src/org/prog3/lab/project/resources/log/access/"+email);
                    FileWriter fileAccessWriter = new FileWriter(fileAccess, true);

                    try {
                        serverSemaphore.acquire();

                        String lineState = readerState.readLine();

                        readerState.close();

                        if(lineState.equals("unlogged")){

                            fileAccessWriter.write("login - "+ data+"\n");

                            fileAccessWriter.close();

                            FileWriter fwState = new FileWriter("./server/src/org/prog3/lab/project/resources/log/state/"+email);
                            BufferedWriter writeState = new BufferedWriter(fwState);

                            writeState.write("logged");
                            writeState.flush();
                            writeState.close();
                            fwState.close();

                            accept = true;
                        }
                    } finally{
                        serverSemaphore.release();
                    }

                    if(accept) {
                        user = new User(email);

                        logThreads.execute(new LogTask(connectionSemaphore, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "login connection"));

                        response = "accept";
                    } else {
                        response = "logged";
                    }

                    find = true;
                }

                line = reader.readLine();
            }

            outStream.writeObject(user);

            outStream.writeObject(response);

        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
