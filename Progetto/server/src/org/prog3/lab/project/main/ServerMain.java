package org.prog3.lab.project.main;

import org.prog3.lab.project.threadModel.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerMain extends Thread {

    private static final int NUM_THREAD = 5;

    /*public ServerMain(){
        setDaemon(true);
    }*/

    public static void main(String[] args) throws Exception {

        ServerThread st = new ServerThread();
        Thread t = new Thread(st);
        t.start();

        ServerOperation ();

    }

    private static void ServerOperation(){

        try {
            ServerSocket s = new ServerSocket(8190);
            ExecutorService loginThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService updateThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService sendThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService removeThreads = Executors.newFixedThreadPool(NUM_THREAD);

            try{
                boolean accept = true;

                while(accept) {
                    Socket incoming = s.accept();

                    ObjectOutputStream outStream = new ObjectOutputStream(incoming.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());

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
                            Runnable loginTask = new LoginTask(v.get(1), v.get(2), path, outStream);
                            loginThreads.execute(loginTask);
                            break;
                        case "update":
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+v.get(1)+"/"+v.get(2);
                            Runnable updateTask = new UpdateTask(path, Boolean.parseBoolean(v.get(3)), outStream);
                            //System.out.println(v.get(3));
                            updateThreads.execute(updateTask);
                            break;
                        case "send":
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+v.get(1)+"/sendedEmails/";
                            Runnable sendTask = new SendTask(path, v.get(1), v.get(2), v.get(3), v.get(4), outStream);
                            sendThreads.execute(sendTask);
                            break;
                        case "remove":
                            path = "./server/src/org/prog3/lab/project/resources/userClients/"+v.get(1)+"/"+v.get(2)+"/"+v.get(3);
                            Runnable removeTask = new RemoveTask(path);
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
