package org.prog3.lab.project.threadModel;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SendTask implements Runnable{

    String path;
    String from;
    String receivers;
    String object;
    String text;
    private final ObjectOutputStream outStream;
    private String wrongAddress = "";

    public SendTask(String path, String from,  String receivers, String object, String text, ObjectOutputStream outStream) {
        this.path = path;
        this.from = from;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.outStream = outStream;
    }

    public void run(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
        String data = formatter.format(LocalDateTime.now());

        File f = new File(path+data+".txt");

        String response = "";

        try {

            if(f.exists()){

                response = "Errore durante l'invio. Riprovare.";

            }else {

                if(checkAddress(receivers)){

                    f.createNewFile();

                    PrintWriter out = new PrintWriter(f);

                    out.println("--NO_READ--");

                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    data = formatter.format(LocalDateTime.now());

                    writeFile(from, false, out);
                    writeFile(receivers, false, out);
                    writeFile(object, false, out);
                    writeFile(data, false, out);
                    writeFile(text, true, out);

                    response = "Email inviata correttamente.";

                } else{
                    wrongAddress = wrongAddress.replace(" ", "\n");
                    response = "I seguenti indirizzi non sono corretti:\n"+ wrongAddress +"\n\nControllare e riprovare";
                }

            }

            outStream.writeObject(response);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private boolean checkAddress(String receivers){

        String path = "./server/src/org/prog3/lab/project/resources/userClients/";
        int start=0;
        int end = 0;

        wrongAddress = "";

        while(end>=0){

            end = receivers.indexOf(" ", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start, receivers.length());

            File folder = new File(path+receiver);

            if(!folder.isDirectory())
                wrongAddress += receiver+" ";

            start = end+1;
        }

        System.out.println(wrongAddress);

        if(wrongAddress.length() > 0)
            return false;
        else
            return true;
    }

    public void writeFile(String text, boolean isTextEmail, PrintWriter out){

        if(!isTextEmail){
            out.println("--START--");
            out.println(text);
            out.println("--END--");
        } else {
            out.println("--START_TEXT--");
            out.println(text);
            out.println("--END_TEXT--");
        }

        out.flush();
    }
}
