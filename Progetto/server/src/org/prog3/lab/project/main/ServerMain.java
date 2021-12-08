package org.prog3.lab.project.main;

import org.prog3.lab.project.threadModel.LoginTask;
import org.prog3.lab.project.threadModel.SendTask;
import org.prog3.lab.project.threadModel.ServerThread;
import org.prog3.lab.project.threadModel.UpdateTask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain extends Thread {

    private static final int NUM_THREAD = 5;

    public ServerMain(){
        setDaemon(true);
    }

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

            try{
                while(true) {
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
                            //System.out.println(v.get(1)+" "+v.get(2));
                            //System.out.println("ok");
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
                        case "terminate":
                            //System.out.println("ok");
                            //System.exit(0);
                            s.close();
                        default:
                            new IOException();
                            break;
                    }
                }
            } finally {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
