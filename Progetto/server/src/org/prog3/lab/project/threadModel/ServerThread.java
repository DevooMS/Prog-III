package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.Server;
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

public class ServerThread implements Runnable{

    private static final int NUM_THREAD = 3;
    Semaphore loginSem;
    Semaphore logoutSem;
    Semaphore connectionSem;
    Semaphore sendSem;
    Semaphore receivedSem;

    public ServerThread(Semaphore loginSem, Semaphore logoutSem, Semaphore connectionSem, Semaphore sendSem, Semaphore receivedSem){
        this.loginSem = loginSem;
        this.logoutSem = logoutSem;
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.receivedSem = receivedSem;
    }

    @Override
    public void run(){
        try {
            ServerSocket s = new ServerSocket(8190);
            ExecutorService loginThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService logoutThreads = Executors.newFixedThreadPool(NUM_THREAD);
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

                    User user = null;

                    Vector<String> v = null;

                    try {
                        v = ((Vector<String>) inStream.readObject());
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    String operation = null;

                    if (v != null)
                        operation = v.get(0);

                    String path;

                    switch (operation) {
                        case "login":
                            path = "./server/src/org/prog3/lab/project/resources/userEmails.txt";
                            Runnable loginTask = new LoginTask(v.get(1), v.get(2), loginSem, connectionSem, logThreads,  path, outStream);
                            loginThreads.execute(loginTask);
                            break;
                        case "logout":
                            try {
                                user = (User) inStream.readObject();
                            } catch (ClassNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                            Runnable logoutTask = new LogoutTask(user, logoutSem, connectionSem, logThreads);
                            logoutThreads.execute(logoutTask);
                            break;
                        case "update":
                            //path = "./server/src/org/prog3/lab/project/resources/userClients/"+v.get(1)+"/"+v.get(2);
                            try {
                                user = (User) inStream.readObject();
                            } catch (ClassNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+user.getUserEmail()+"/"+v.get(1);
                            Runnable updateTask = new UpdateTask(user, connectionSem, logThreads, path, Boolean.parseBoolean(v.get(2)), outStream);
                            //System.out.println(v.get(3));
                            updateThreads.execute(updateTask);
                            break;
                        case "send":
                            try {
                                user = (User) inStream.readObject();
                            } catch (ClassNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+user.getUserEmail()+"/sendedEmails/";
                            Runnable sendTask = new SendTask(connectionSem, sendSem, receivedSem, logThreads, path, user, v.get(1), v.get(2), v.get(3), outStream);
                            sendThreads.execute(sendTask);
                            break;
                        case "remove":
                            try {
                                user = (User) inStream.readObject();
                            } catch (ClassNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+user.getUserEmail()+"/"+v.get(1)+"/"+v.get(2);
                            Runnable removeTask = new RemoveTask(user, connectionSem, logThreads, path);
                            removeThreads.execute(removeTask);
                            break;
                        case "terminate":
                            accept=false;
                            //s.close();
                            loginThreads.shutdown();
                            loginThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            updateThreads.shutdown();
                            updateThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            sendThreads.shutdown();
                            sendThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            removeThreads.shutdown();
                            removeThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            logoutThreads.shutdown();
                            logoutThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            logThreads.shutdown();
                            logThreads.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            break;
                        default:
                            new IOException();
                            break;
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
