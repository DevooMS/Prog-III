package org.prog3.lab.project.threadModel;
//new version 1.0

import java.io.*;

public class UpdateTask implements Runnable{
    private final String directoryPath;
    private final boolean startUpdate;
    private final ObjectOutputStream outStream;

    public UpdateTask(String directoryPath, boolean startUpdate, ObjectOutputStream outStream){
        this.directoryPath = directoryPath;
        this.startUpdate = startUpdate;
        this.outStream = outStream;
    }

    public void run(){

        try {
            File dir = new File (directoryPath);
            File[] listOfFiles = dir.listFiles();
            int countFiles = listOfFiles.length;
            int countNoRead = 0;

            outStream.writeObject(countFiles);

            for(int i=0; i<countFiles; i++) {

                BufferedReader reader = new BufferedReader(new FileReader(directoryPath + "/"+ listOfFiles[i].getName()));

                String line = reader.readLine();

                String lineToSend = "";

                boolean rewrite = false;

                StringBuilder fileContent = new StringBuilder();

                if((line.equals("--READ--") && startUpdate) || line.equals("--NO_READ--")) {

                    outStream.writeObject(listOfFiles[i].getName());

                    if(line.equals("--NO_READ--")){
                        rewrite = true;
                        fileContent.append("--READ--"+System.getProperty("line.separator"));
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

                            if(rewrite) {
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

                            if(rewrite) {
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

                if(rewrite) {
                    FileWriter fstreamWrite = new FileWriter(directoryPath + "/"+ listOfFiles[i].getName());
                    BufferedWriter out = new BufferedWriter(fstreamWrite);
                    out.write(fileContent.toString());
                    out.flush();
                    fstreamWrite.close();
                    out.close();
                }

                outStream.writeObject("--END_EMAIL--");
            }

            outStream.writeObject(countNoRead);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
