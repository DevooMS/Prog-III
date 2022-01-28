package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class SendTask implements Runnable{

    private final Semaphore connectionSem;
    private final Semaphore sendSem;
    private final Semaphore errorSendSem;
    private final Semaphore receivedSem;
    private final ExecutorService logThreads;
    private final String path;
    private final User user;
    private final String receivers;
    private final String object;
    private final String text;
    private final ObjectOutputStream outStream;
    private final ArrayList<String> listReceivers = new ArrayList<>();
    private final DateTimeFormatter logDateFormatter;
    private final DateTimeFormatter fileDateFormatter;
    private String wrongAddress;
    private String logDate;
    private String fileDate;
    private File file_send;

    public SendTask(Semaphore connectionSem, Semaphore sendSem, Semaphore errorSendSem, Semaphore receivedSem, ExecutorService logThreads, String path, User user, String receivers, String object, String text, ObjectOutputStream outStream) {
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.errorSendSem = errorSendSem;
       this.receivedSem = receivedSem;
        this.logThreads = logThreads;
        this.path = path;
        this.user = user;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.outStream = outStream;
        wrongAddress = "";
        logDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        fileDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
    }

    public void run(){

        logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "open send connection", logDate));

        fileDate = fileDateFormatter.format(LocalDateTime.now());
        file_send = new File(path + fileDate + ".txt");

        String response = "send_error";

        try {

            if(file_send.exists()){
                response = "send_error";
            }else {

                checkAddress(receivers);

                if(file_send.createNewFile()) {

                    PrintWriter out = new PrintWriter(file_send);

                    out.println("--NO_READ--");
                    try {
                        user.getReadWrite().acquire();

                        logDate = logDateFormatter.format(LocalDateTime.now());

                        writeFile(user.getUserEmail(), false, out);
                        writeFile(receivers, false, out);
                        writeFile(object, false, out);
                        writeFile(logDate, false, out);
                        writeFile(text, true, out);

                        out.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        user.getReadWrite().release();
                    }

                    sendToReceivers();

                    logThreads.execute(new LogTask(sendSem, getClass().getResource("../resources/log/send/" + user.getUserEmail()).getPath(), "send email", logDate));

                    response = "send_correct";

                } else {

                    logDate = logDateFormatter.format(LocalDateTime.now());

                    logThreads.execute(new LogTask(errorSendSem, getClass().getResource("../resources/log/errorSend/" + user.getUserEmail()).getPath(), "send to wrong address", logDate));

                    response = "send_error";

                }
            }

            outStream.close();

            logDate = logDateFormatter.format(LocalDateTime.now());

            logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "close send connection", logDate));

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();

            response = "send_error";

        } finally {
            try {
                outStream.writeObject(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAddress(String receivers){

        int start=0;
        int end = 0;

        while(end>=0){

            end = receivers.indexOf(",", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start);

            File folder = new File(getClass().getResource("/" + receiver).getPath());

            if(receiver.equals(user.getUserEmail()) || !folder.isDirectory()){
                wrongAddress += receiver + " ";
            } else {
                listReceivers.add(receiver);
            }

            start = end+1;
        }

        for(int i=0; i<listReceivers.size(); i++)
            System.out.println(listReceivers.get(i));
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

    private void sendToReceivers() throws IOException, URISyntaxException {

        for (int i = 0; i < listReceivers.size(); i++) {

            //File file_receiver = new File(getClass().getResource("../resources/userClients.userClients/" + listReceivers.get(i) + "/receivedEmails/" + file_send.getName() + "_" + user.getUserEmail() + ".txt").getPath());
            File file_receiver = new File("../src/org/prog3/lab/project/userClients/" + listReceivers.get(i) + "/receivedEmails/" + file_send.getName() + "_" + user.getUserEmail() + ".txt");
            //if(file_receiver.createNewFile()) {
            file_receiver.createNewFile();
                FileChannel from = new FileInputStream(file_send).getChannel();
                FileChannel receiver = new FileOutputStream(file_receiver).getChannel();

                receiver.transferFrom(from, 0, from.size());

                from.close();
                receiver.close();

                logDate = logDateFormatter.format(LocalDateTime.now());

                logThreads.execute(new LogTask(receivedSem, getClass().getResource("../resources/log/received/" + listReceivers.get(i)).getPath(), "received email", logDate));

            /*}else{
                logDate = logDateFormatter.format(LocalDateTime.now());

                logThreads.execute(new LogTask(errorSendSem, getClass().getResource("../resources/log/errorSend/" + user.getUserEmail()).getPath(), "send to wrong address", logDate));

            }*/
        }

        if(wrongAddress.length() > 0){

            logDate = logDateFormatter.format(LocalDateTime.now());

            logThreads.execute(new LogTask(errorSendSem, getClass().getResource("../resources/log/errorSend/" + user.getUserEmail()).getPath(), "error send to receiver", logDate));

            wrongAddress = wrongAddress.replace(" ", "\n");

            fileDate = fileDateFormatter.format(LocalDateTime.now());

            File file_error;

            file_error = new File(getClass().getResource("/" +user.getUserEmail()+"/receivedEmails/"+fileDate+"_error.txt").getPath());

            PrintWriter out = new PrintWriter(file_error);

            out.println("--NO_READ--");

            logDate = logDateFormatter.format(LocalDateTime.now());

            writeFile("no_reply@e.it", false, out);
            writeFile(user.getUserEmail(), false, out);
            writeFile("Indirizzi email errati", false, out);
            writeFile(logDate, false, out);
            writeFile("Nella mail con oggetto \""+object+"\", iseguenti indirizzi email sono errati: \n\n"+wrongAddress+"\n\nN:B:: si prega di non rispondere a questa email.", true, out);

            out.close();
        }

    }

}
