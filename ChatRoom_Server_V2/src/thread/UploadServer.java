/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thread;

import dao.Information;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Shinelon
 */
public class UploadServer extends Thread {
    private ServerThread serverThread;
    
    public UploadServer(ServerThread serverThread) {
        this.serverThread = serverThread;
    }
    
    private boolean flag_exit = false;
    public void setFlag_exit(boolean flag_exit) {
        this.flag_exit = flag_exit;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(5001);
            while (flag_exit) {
                synchronized (serverThread.file_info) {
                    try {
                        serverThread.file_info.wait();//先有名后有socekt
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Socket s = ss.accept();
                    //接到线程后，从集合拿一个文件名
                    Information last_info = serverThread.file_info.lastElement();
                    new Thread(new UploadThread(s, last_info, this.serverThread)).start();
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("端口5001被占用，无法上传文件！");
        }
    }
      

}
