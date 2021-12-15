package org.prog3.lab.project.threadModel;

import java.io.File;

public class RemoveTask implements Runnable {

    String path;

    public RemoveTask(String path) {
        this.path = path;
    }

    public void run(){
        try {
            File file_remove = new File(path);

            file_remove.delete();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

}
