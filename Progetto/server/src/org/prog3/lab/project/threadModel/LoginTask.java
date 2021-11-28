package org.prog3.lab.project.threadModel;

import java.io.*;
import java.net.Socket;

public class LoginTask implements Runnable{
    private final String email;
    private final String password;
    private final String filePath;
    private final PrintWriter out;

    public LoginTask(String email, String password, String filePath, PrintWriter out){
        this.email = email;
        this.password = password;
        this.filePath = filePath;
        this.out = out;
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
            out.println(response);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
