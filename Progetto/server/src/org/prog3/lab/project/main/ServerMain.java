package org.prog3.lab.project.main;

import org.prog3.lab.project.threadModel.LoginTask;
import org.prog3.lab.project.threadModel.UpdateTask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    private static final int NUM_THREAD = 1;

    public static void main(String[] args) {

        try {
            ServerSocket s = new ServerSocket(8189);

            ExecutorService loginThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService updateThreads = Executors.newFixedThreadPool(NUM_THREAD);

            try{
                while(true) {
                    Socket incoming = s.accept();

                    ObjectOutputStream outObjectStream = new ObjectOutputStream(incoming.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());

                    OutputStream outSimpleStream = incoming.getOutputStream();
                    PrintWriter out = new PrintWriter(outSimpleStream, true);

                    Vector<String> v = null;

                    try {
                        v = ((Vector<String>) inStream.readObject());
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    String operation = null;

                    if (v != null)
                        operation = v.get(0);

                    switch (operation) {
                        case "login":
                            String filePath = "src/org/prog3/lab/project/resources/userEmails";
                            Runnable loginTask = new LoginTask(v.get(1), v.get(2), filePath, out);
                            loginThreads.execute(loginTask);
                            break;
                        case "update":
                            String directoryPath = "C:\\Users\\nicol\\Documents\\Prog III lab\\Progetto - server\\userClients\\"+v.get(1)+"\\"+v.get(2);
                            Runnable updateTask = new UpdateTask(directoryPath, out, outObjectStream);
                            updateThreads.execute(updateTask);
                            break;
                        default:
                            new IOException();
                            break;
                    }
                }
            } finally {
                s.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
