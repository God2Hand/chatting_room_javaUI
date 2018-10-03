/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shinelon
 */
public class DownloadServer extends Thread {
    private boolean flag_exit = false;
    public void setFlag_exit(boolean flag_exit) {
        this.flag_exit = flag_exit;
    }
    

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(5002);
            while (flag_exit) {
                Socket s = ss.accept();
                new Thread(new DownloadThread(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
