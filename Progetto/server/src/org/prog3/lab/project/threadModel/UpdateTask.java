package org.prog3.lab.project.threadModel;
//new version 1.0

import org.prog3.lab.project.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class UpdateTask implements Runnable{
    private final User user;
    private final Semaphore connectionSem;
    private final ExecutorService logThreads;
    private final String path;
    private final boolean startUpdate;
    private final ObjectOutputStream outStream;
    private final DateTimeFormatter logDateFormatter;

    public UpdateTask(User user, Semaphore connectionSem, ExecutorService logThreads, String path, boolean startUpdate, ObjectOutputStream outStream){
        this.user = user;
        this.connectionSem = connectionSem;
        this.logThreads = logThreads;
        this.path = path;
        this.startUpdate = startUpdate;
        this.outStream = outStream;
        logDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }

    public void run(){

        String logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "open update connection", logDate));

        try {
            File dir = new File(path);
            File[] listOfFiles = dir.listFiles();
            int countFiles = listOfFiles.length;
            int countNoRead = 0;

            outStream.writeObject(countFiles);

            try {
                user.getReadWrite().acquire();

                for(int i=0; i<countFiles; i++) {

                    BufferedReader reader = new BufferedReader(new FileReader(path + "/" + listOfFiles[i].getName()));

                    String line = reader.readLine();

                    String lineToSend = "";

                    boolean rewrite = false;

                    StringBuilder fileContent = new StringBuilder();

                    if ((line.equals("--READ--") && startUpdate) || line.equals("--NO_READ--")) {

                        outStream.writeObject(listOfFiles[i].getName());

                        if (line.equals("--NO_READ--")) {
                            rewrite = true;
                            fileContent.append("--READ--" + System.getProperty("line.separator"));
                            countNoRead++;
                        }

                        line = reader.readLine();

                        while (line != null) {

                            if (line.equals("--START--")) {

                                line = reader.readLine();

                                while (!line.equals("--END--")) {
                                    lineToSend += line;

                                    line = reader.readLine();
                                }

                                if (rewrite) {
                                    fileContent.append("--START--" + System.getProperty("line.separator"));
                                    fileContent.append(lineToSend + System.getProperty("line.separator"));
                                    fileContent.append("--END--" + System.getProperty("line.separator"));
                                }

                            } else if (line.equals("--START_TEXT--")) {

                                line = reader.readLine();

                                while (!line.equals("--END_TEXT--")) {
                                    if (line.equals(""))
                                        lineToSend += "\n\n";
                                    else
                                        lineToSend += line + "\n";

                                    line = reader.readLine();
                                }

                                if (rewrite) {
                                    fileContent.append("--START_TEXT--" + System.getProperty("line.separator"));
                                    fileContent.append(lineToSend + System.getProperty("line.separator"));
                                    fileContent.append("--END_TEXT--" + System.getProperty("line.separator"));
                                }

                            }

                            outStream.writeObject(lineToSend);

                            lineToSend = "";

                            line = reader.readLine();

                        }

                    }

                    reader.close();

                    if (rewrite) {
                        FileWriter fstreamWrite = new FileWriter(path + "/" + listOfFiles[i].getName());
                        BufferedWriter out = new BufferedWriter(fstreamWrite);
                        out.write(fileContent.toString());
                        out.flush();
                        fstreamWrite.close();
                        out.close();
                    }

                    outStream.writeObject("--END_EMAIL--");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }finally{
                user.getReadWrite().release();
            }

            outStream.writeObject(countNoRead);

            outStream.close();

            logDate = logDateFormatter.format(LocalDateTime.now());
            logThreads.execute(new LogTask(connectionSem, getClass().getResource("../resources/log/connection/" + user.getUserEmail()).getPath(), "close update connection", logDate));

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}