package org.prog3.lab.project.threadModel;

import java.io.*;
import java.net.Socket;

public class LoginTask implements Runnable{
    private final String email;
    private final String password;
    private final String filePath;
    private final ObjectOutputStream outStream;

    public LoginTask(String email, String password, String filePath, ObjectOutputStream outStream){
        this.email = email;
        this.password = password;
        this.filePath = filePath;
        this.outStream = outStream;
    }

    public void run(){

        String response = "denied";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line = reader.readLine();

            while (line != null && response.equals("denied")) {
                if (line.compareTo(email + "-" + password) == 0)
                    response = "accept";
                line = reader.readLine();
            }

            //System.out.println(response);
            outStream.writeObject(response);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
