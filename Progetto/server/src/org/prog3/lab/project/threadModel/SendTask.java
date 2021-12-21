package org.prog3.lab.project.threadModel;

import java.io.*;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SendTask implements Runnable{

    String path;
    String from;
    String receivers;
    String object;
    String text;
    private final ObjectOutputStream outStream;
    private final ArrayList<String> listReceivers = new ArrayList<>();
    private String wrongAddress = "";
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

        String response;

        try {

            if(file_send.exists()){

                response = "Errore durante l'invio. Riprovare.";

            }else {

                //if(checkAddress(receivers)){

                checkAddress(receivers);

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

                out.close();

                sendToReceivers();

                response = "Email inviata correttamente.";

            }

            outStream.writeObject(response);

            outStream.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void checkAddress(String receivers){

        String path = "./server/src/org/prog3/lab/project/resources/userClients/";
        int start=0;
        int end = 0;

        while(end>=0){

            end = receivers.indexOf(",", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start, receivers.length());

            //listReceivers.add(receiver);

            File folder = new File(path + receiver);

            if(receiver.equals(from) || !folder.isDirectory()){
                wrongAddress += receiver + " ";
            } else {
                listReceivers.add(receiver);
            }

            start = end+1;
        }
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

            from.close();
            receiver.close();
        }

        if(wrongAddress.length() > 0){
            wrongAddress = wrongAddress.replace(" ", "\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
            String data = formatter.format(LocalDateTime.now());

            File file_error;
            file_error = new File("./server/src/org/prog3/lab/project/resources/userClients/"+from+"/receivedEmails/"+data+".txt");

            PrintWriter out = new PrintWriter(file_error);

            out.println("--NO_READ--");

            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            data = formatter.format(LocalDateTime.now());

            writeFile("no reply", false, out);
            writeFile(from, false, out);
            writeFile("Indirizzi email errati", false, out);
            writeFile(data, false, out);
            writeFile("Nella mail con oggetto \""+object+"\", iseguenti indirizzi email sono errati: \n\n"+wrongAddress+"\n\nN:B:: si prega di non rispondere a questa email.", true, out);

            out.close();
        }

    }

}
