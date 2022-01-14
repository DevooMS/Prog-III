package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class SendTask implements Runnable{

    private Semaphore connectionSem;
    private Semaphore sendSem;
    private Semaphore receivedSem;
    private final ExecutorService logThreads;
    String path;
    User user;
    String receivers;
    String object;
    String text;
    private final ObjectOutputStream outStream;
    private final ArrayList<String> listReceivers = new ArrayList<>();
    private String wrongAddress = "";
    private File file_send;

    public SendTask(Semaphore connectionSem, Semaphore sendSem, Semaphore receivedSem, ExecutorService logThreads, String path, User user, String receivers, String object, String text, ObjectOutputStream outStream) {
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.receivedSem = receivedSem;
        this.logThreads = logThreads;
        this.path = path;
        this.user = user;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.outStream = outStream;
    }

    public void run(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
        String data = formatter.format(LocalDateTime.now());

        logThreads.execute(new LogTask(connectionSem, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "open send connection"));

        file_send = new File(path+data+".txt");

        String response;

        try {

            if(file_send.exists()){
                response = "send_error";
            }else {

                checkAddress(receivers);

                file_send.createNewFile();

                PrintWriter out = new PrintWriter(file_send);

                out.println("--NO_READ--");
                try {
                    user.getReadWrite().acquire();
                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    data = formatter.format(LocalDateTime.now());

                    writeFile(user.getUserEmail(), false, out);
                    writeFile(receivers, false, out);
                    writeFile(object, false, out);
                    writeFile(data, false, out);
                    writeFile(text, true, out);

                    out.close();
                } finally{
                    user.getReadWrite().release();;
                }

                response = "send_correct";

            }

            outStream.writeObject(response);

            outStream.close();

            sendToReceivers();

            logThreads.execute(new LogTask(sendSem, "./server/src/org/prog3/lab/project/resources/log/send/"+user.getUserEmail(), "send"));

            logThreads.execute(new LogTask(connectionSem, "./server/src/org/prog3/lab/project/resources/log/connection/"+user.getUserEmail(), "close send connection"));
        } catch (IOException | InterruptedException e) {
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

            if(receiver.equals(user.getUserEmail()) || !folder.isDirectory()){
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

            File file_receiver = new File(path + file_send.getName() + "_" + user.getUserEmail() + ".txt");
            file_receiver.createNewFile();

            FileChannel from = new FileInputStream(file_send).getChannel();
            FileChannel receiver = new FileOutputStream(file_receiver).getChannel();

            receiver.transferFrom(from, 0, from.size());

            from.close();
            receiver.close();

            logThreads.execute(new LogTask(receivedSem, "./server/src/org/prog3/lab/project/resources/log/received/"+listReceivers.get(i), "received"));
        }

        if(wrongAddress.length() > 0){
            wrongAddress = wrongAddress.replace(" ", "\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
            String data = formatter.format(LocalDateTime.now());

            File file_error;
            file_error = new File("./server/src/org/prog3/lab/project/resources/userClients/"+user.getUserEmail()+"/receivedEmails/"+data+".txt");

            PrintWriter out = new PrintWriter(file_error);

            out.println("--NO_READ--");

            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            data = formatter.format(LocalDateTime.now());

            writeFile("no reply", false, out);
            writeFile(user.getUserEmail(), false, out);
            writeFile("Indirizzi email errati", false, out);
            writeFile(data, false, out);
            writeFile("Nella mail con oggetto \""+object+"\", iseguenti indirizzi email sono errati: \n\n"+wrongAddress+"\n\nN:B:: si prega di non rispondere a questa email.", true, out);

            out.close();
        }

    }

}
