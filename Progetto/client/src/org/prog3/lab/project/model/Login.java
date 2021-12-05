package org.prog3.lab.project.model;

import javafx.scene.control.Label;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Login {

    public String searchUser(String userEmail, String userPassword, Label labelResult) throws IOException {

        String response = "error";

        try {
            if (!userEmail.equals("") && !userPassword.equals("")) {
                Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

                try {
                    InputStream inStream = s.getInputStream();
                    Scanner in = new Scanner(inStream);

                    ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                    Vector<String> operationRequest = new Vector<>();
                    operationRequest.add("login");
                    operationRequest.add(userEmail);
                    operationRequest.add(userPassword);

                    outStream.writeObject(operationRequest);

                    response = in.nextLine();

                    return response;
                }finally{
                    s.close();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return response;
    }
}
