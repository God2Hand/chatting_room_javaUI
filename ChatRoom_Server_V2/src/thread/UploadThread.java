/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thread;

import dao.Information;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Shinelon
 */
public class UploadThread extends Thread {
    private Socket s;
    private Information last_info;
    private ServerThread serverThread;

    public UploadThread(Socket s, Information last_info, ServerThread serverThread) {
        this.s = s;
        this.last_info = last_info;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        try {
            BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("src\\download\\" + last_info.getFile()));
            
            byte[] bys = new byte[1024];
            int len = 0;
            while ((len = bis.read(bys)) != -1) {
                bos.write(bys, 0, len);
                bos.flush();
            }

            //上传成功，反馈
            synchronized (serverThread.informations) {
                synchronized (serverThread.file_info) {
                    serverThread.informations.addElement(last_info);
                    serverThread.file_info.removeElement(last_info);
                    //如果是群聊的话，设置聊天记录
                    if (last_info.getMess().contains("@chat")) {
                        serverThread.serverFrame.setDisFile(last_info);
                    }
                }
            }
            
            bos.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
