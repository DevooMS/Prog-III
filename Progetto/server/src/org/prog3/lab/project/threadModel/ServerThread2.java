package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ServerThread2 implements Runnable{

    private static final int NUM_THREAD = 3;    
    private final Semaphore loginSem;
    private final Semaphore logoutSem;
    private final Semaphore connectionSem;
    private final Semaphore sendSem;
    private final Semaphore errorSendSem;
    private final Semaphore receivedSem;
    private final Semaphore removeSem;
    private User user;

    public ServerThread2(Semaphore loginSem, Semaphore logoutSem, Semaphore connectionSem, Semaphore sendSem, Semaphore errorSendSem, Semaphore receivedSem, Semaphore removeSem){
        this.loginSem = loginSem;
        this.logoutSem = logoutSem;
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.errorSendSem = errorSendSem;
        this.receivedSem = receivedSem;
        this.removeSem = removeSem;
    }

    @Override
    public void run(){
        try {
            ServerSocket s = new ServerSocket(8190);
            ExecutorService accessThreads = Executors.newFixedThreadPool(NUM_THREAD);      //assegno 3 thread a ciascuno di questo threadpools chiama i rispetivi run();
            ExecutorService updateThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService sendThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService removeThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService logThreads = Executors.newFixedThreadPool(NUM_THREAD);

            try{
                boolean accept = true;

                while(accept) {
                    Socket incoming = s.accept();

                    ObjectOutputStream outStream = new ObjectOutputStream(incoming.getOutputStream());          
                    ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());

                    Vector<String> v = null;

                    try {
                        v = ((Vector<String>) inStream.readObject());                       //leggo il contenuto mandato dal client
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    String operation = null;

                    if (v != null)
                        operation = v.get(0);

                    if(operation.equals("login") || operation.equals("logout") || operation.equals("update") || operation.equals("send") || operation.equals("remove")){
                        try {
                            user = (User) inStream.readObject();    //se appartiene a uno di questi operazione gli passo oggetto user
                        } catch (ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    String path;

                    switch (operation) {
                        case "login" -> {
                            path = getClass().getResource("../resources/log/login/" + user.getUserEmail()).getPath();           //prende il path e email

                            Runnable loginTask = new AccessTask(user, connectionSem, loginSem, logThreads, "login", path);  //prepara il nuovo runnable e chiamo la classe AccessTask
                            accessThreads.execute(loginTask);                                                                       //eseguo il runnable chiamando AccessTask run();
                        }
                        case "logout" -> {
                            path = getClass().getResource("../resources/log/logout/" + user.getUserEmail()).getPath();

                            Runnable logoutTask = new AccessTask(user, connectionSem, logoutSem, logThreads, "logout", path);
                            accessThreads.execute(logoutTask);
                        }
                        case "update" -> {
                            path = getClass().getResource("../resources/userClients/" + user.getUserEmail() + "/" + v.get(1)).getPath();
                            Runnable updateTask = new UpdateTask(user, connectionSem, logThreads, path, Boolean.parseBoolean(v.get(2)), outStream);
                            updateThreads.execute(updateTask);
                        }
                        case "send" -> {
                            path = getClass().getResource("../resources/userClients/" + user.getUserEmail() + "/sendedEmails/").getPath();

                            Runnable sendTask = new SendTask(connectionSem, sendSem, errorSendSem, receivedSem, logThreads, path, user, v.get(1), v.get(2), v.get(3), outStream);
                            sendThreads.execute(sendTask);
                        }
                        case "remove" -> {
                            path = getClass().getResource("../resources/userClients/" + user.getUserEmail() + "/" + v.get(1) + "/").getPath();

                            Runnable removeTask = new RemoveTask(user, connectionSem, removeSem, logThreads, path, v.get(2), outStream);
                            removeThreads.execute(removeTask);
                        }
                        case "terminate" -> {
                            accept = false;
                            accessThreads.shutdown();
                            accessThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            updateThreads.shutdown();
                            updateThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            sendThreads.shutdown();
                            sendThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            removeThreads.shutdown();
                            removeThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            logThreads.shutdown();
                            logThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                        }
                        default -> new IOException();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
