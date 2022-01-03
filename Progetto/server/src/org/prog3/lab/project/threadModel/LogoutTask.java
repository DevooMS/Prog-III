package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class LogoutTask implements Runnable{

    private final User user;
    private final Semaphore serverSemaphore;
    private String data;

    public LogoutTask(User user, Semaphore serverSemaphore){
        this.user = user;
        this.serverSemaphore = serverSemaphore;
    }

    public void run(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        data = formatter.format(LocalDateTime.now());

        try {
            serverSemaphore.acquire();

            File fileAccess = new File("./server/src/org/prog3/lab/project/resources/log/access/"+user.getUserEmail());
            FileWriter fileAccessWriter = new FileWriter(fileAccess, true);

            fileAccessWriter.write("logout - "+ data+"\n");

            fileAccessWriter.close();

            FileWriter fwState = new FileWriter("./server/src/org/prog3/lab/project/resources/log/state/"+user.getUserEmail());
            BufferedWriter writeState = new BufferedWriter(fwState);
            writeState.write("unlogged");
            writeState.flush();
            writeState.close();
            fwState.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally{
            serverSemaphore.release();
        }

        try{
            user.getConnection().acquire();

            File fileConnection = new File("./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail());
            FileWriter fileConnectionWriter = new FileWriter(fileConnection, true);

            fileConnectionWriter.write("logout connection - "+ data+"\n");

            fileConnectionWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
           user.getConnection().release();
        }
    }
}
