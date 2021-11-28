package org.prog3.lab.project.model;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Login {

    public String searchUser(String userEmail, String userPassword) throws IOException {
        /*try {
            if (!userEmail.equals("") && !userPassword.equals("")) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));

                String line = reader.readLine();

                while (line != null) {
                    if (line.compareTo(userEmail + "-" + userPassword) == 0)
                        return true;
                    line = reader.readLine();
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return false;*/
        try {
            if (!userEmail.equals("") && !userPassword.equals("")) {
                Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8189);

                try {
                    InputStream inStream = s.getInputStream();
                    Scanner in = new Scanner(inStream);

                    ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                    Vector<String> operationRequest = new Vector<>();
                    operationRequest.add("login");
                    operationRequest.add(userEmail);
                    operationRequest.add(userPassword);

                    outStream.writeObject(operationRequest);

                    String response = in.nextLine();

                    return response;
                }finally{
                    s.close();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return "denied";
    }
}
