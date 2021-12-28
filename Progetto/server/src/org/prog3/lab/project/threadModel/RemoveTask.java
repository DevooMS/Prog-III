package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.File;

public class RemoveTask implements Runnable {

    User user;
    String path;

    public RemoveTask(User user, String path) {
        this.user = user;
        this.path = path;
    }

    public void run(){
        try {
            user.getReadWrite().acquire();

            File file_remove = new File(path);

            file_remove.delete();

            user.getReadWrite().release();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

}
