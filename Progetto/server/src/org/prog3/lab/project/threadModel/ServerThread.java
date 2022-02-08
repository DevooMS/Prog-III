package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ServerThread implements Runnable{

    private static final int NUM_THREAD = 3;
    private final Semaphore loginSem;
    private final Semaphore logoutSem;
    private final Semaphore connectionSem;
    private final Semaphore sendSem;
    private final Semaphore errorSendSem;
    private final Semaphore receivedSem;
    private final Semaphore removeSem;
    private User user;

    public ServerThread(Semaphore loginSem, Semaphore logoutSem, Semaphore connectionSem, Semaphore sendSem, Semaphore errorSendSem, Semaphore receivedSem, Semaphore removeSem){
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
            ServerSocket s = new ServerSocket(8190);                                            //apro il socket del server
            ExecutorService accessThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService updateThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService sendThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService removeThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService logThreads = Executors.newFixedThreadPool(NUM_THREAD);

            try{
                boolean accept = true;

                while(accept) {
                    Socket incoming = s.accept();                                                //accetto la richiesta da parte del client   

                    ObjectOutputStream outStream = new ObjectOutputStream(incoming.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());

                    Vector<String> v = null;
                    String operation = null;

                    try {
                        v = ((Vector<String>) inStream.readObject());
                        operation = v.get(0);
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    if(operation.equals("login") || operation.equals("logout") || operation.equals("update") || operation.equals("send") || operation.equals("remove")){
                        try {
                            user = (User) inStream.readObject();
                        } catch (ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    String path;

                    switch (operation) {                                                         //v.get(0) indica operazione e altri get(i) cambia secondo il contesto della operazione
                        case "login" -> {
                            path = Objects.requireNonNull(getClass().getResource("../resources/log/login/" + user.getUserEmail())).getPath();
                            //path = "./server/src/org/prog3/lab/project/resources/log/login/" + user.getUserEmail();
                            Runnable loginTask = new AccessTask(user, connectionSem, loginSem, logThreads, "login", path);
                            accessThreads.execute(loginTask);
                        }
                        case "logout" -> {
                            path = Objects.requireNonNull(getClass().getResource("../resources/log/logout/" + user.getUserEmail())).getPath();
                            //path = "./server/src/org/prog3/lab/project/resources/log/logout/" + user.getUserEmail();
                            Runnable logoutTask = new AccessTask(user, connectionSem, logoutSem, logThreads, "logout", path);
                            accessThreads.execute(logoutTask);
                        }
                        case "update" -> {
                            path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/" + user.getUserEmail() + "/" + v.get(1))).getPath();
                            Runnable updateTask = new UpdateTask(user, connectionSem, logThreads, path, Boolean.parseBoolean(v.get(2)), outStream);
                            updateThreads.execute(updateTask);
                        }
                        case "send" -> {
                            path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/" + user.getUserEmail() + "/sendedEmails/")).getPath();
                            //path = "./server/src/org/prog3/lab/project/resources/userClients.userClients/" + user.getUserEmail() + "/sendedEmails/";
                            Runnable sendTask = new SendTask(connectionSem, sendSem, errorSendSem, receivedSem, logThreads, path, user, v.get(1), v.get(2), v.get(3), outStream);
                            sendThreads.execute(sendTask);
                        }
                        case "remove" -> {
                            path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/" + user.getUserEmail() + "/" + v.get(1) + "/")).getPath();
                            //path = "./server/src/org/prog3/lab/project/resources/userClients.userClients/" + user.getUserEmail() + "/" + v.get(1) + "/" + v.get(2);
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
