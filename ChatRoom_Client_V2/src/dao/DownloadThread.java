/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JButton;
import view.DownloadFrame;

/**
 *
 * @author Shinelon
 */
public class DownloadThread extends Thread {
    private JButton jbt_file;
    private Client client;
    private String path;
    private String filename;
    
    public DownloadThread(JButton jbt_file, Client client, String path, String filename) {
        this.jbt_file = jbt_file;
        this.client = client;
        this.path = path;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(client.getIp(), 5002);
            //连成功了再开始提示
            client.downloadFrame = new DownloadFrame(client);
            client.downloadFrame.setVisible(true);
            
            OutputStream os = s.getOutputStream();
            os.write(filename.getBytes());//先发送服务器端文件名过去
            os.flush();
            
            BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator +jbt_file.getText()));//路径+分隔符+文件名
            
            byte[] bys = new byte[1024];
            int len = 0;
            while ((len = bis.read(bys)) != -1) {
                bos.write(bys, 0, len);
                bos.flush();
            }

            //反馈
            client.downloadFrame.setTitle("文件下载100%");
            client.downloadFrame.set_yes();
            jbt_file.setEnabled(true);
            client.downloadFrame.getFocusableWindowState();
            
            os.close();
            bos.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
