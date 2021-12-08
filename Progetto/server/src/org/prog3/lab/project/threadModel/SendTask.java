package org.prog3.lab.project.threadModel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;

public class SendTask implements Runnable{

    String path;
    String from;
    String receivers;
    String object;
    String text;
    private final ObjectOutputStream outStream;
    private String wrongAddress = "";
    private ArrayList<String> listReceivers = new ArrayList<>();
    private File file_send;

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

        file_send = new File(path+data+".txt");

        String response = "";

        try {

            if(file_send.exists()){

                response = "Errore durante l'invio. Riprovare.";

            }else {

                if(checkAddress(receivers)){

                    file_send.createNewFile();

                    PrintWriter out = new PrintWriter(file_send);

                    out.println("--NO_READ--");

                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    data = formatter.format(LocalDateTime.now());

                    writeFile(from, false, out);
                    writeFile(receivers, false, out);
                    writeFile(object, false, out);
                    writeFile(data, false, out);
                    writeFile(text, true, out);

                    sendToReceivers();

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

            end = receivers.indexOf(",", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start, receivers.length());

            listReceivers.add(receiver);

            File folder = new File(path+receiver);

            if(!folder.isDirectory())
                wrongAddress += receiver+" ";

            start = end+1;
        }

        if(wrongAddress.length() > 0) {
            listReceivers = null;
            return false;
        }else
            return true;
    }

    private void writeFile(String text, boolean isTextEmail, PrintWriter out){

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

    private void sendToReceivers() throws IOException {

        for (int i = 0; i < listReceivers.size(); i++) {
            String path = "./server/src/org/prog3/lab/project/resources/userClients/" + listReceivers.get(i) + "/receivedEmails/";

            File file_receiver = new File(path + file_send.getName() + "_" + from + ".txt");
            file_receiver.createNewFile();

            FileChannel from = new FileInputStream(file_send).getChannel();
            FileChannel receiver = new FileOutputStream(file_receiver).getChannel();

            receiver.transferFrom(from, 0, from.size());
        }

    }

}
