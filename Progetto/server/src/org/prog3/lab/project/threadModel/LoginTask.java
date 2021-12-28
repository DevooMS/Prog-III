package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;

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
        User user = null;
        String response = "denied";
        boolean find = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line = reader.readLine();

            while (line != null && !find) {
                if (line.compareTo(email + "-" + password) == 0) {
                    user = new User(email);
                    response = "accept";
                    find=true;
                }

                line = reader.readLine();
            }

            outStream.writeObject(user);

            outStream.writeObject(response);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
