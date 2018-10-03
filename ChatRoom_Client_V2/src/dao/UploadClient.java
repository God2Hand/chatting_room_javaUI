/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import view.SingleFrame;
import view.UploadFrame;

/**
 *
 * @author Shinelon
 */
public class UploadClient extends Thread {
    private Client client;
    private String filePath;
    
    public UploadClient(Client client, String filePath) {
        this.client = client;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(client.getIp(), 5001);
            //连成功了再开始提示
            client.uploadFrame = new UploadFrame(client);
            client.uploadFrame.setVisible(true);
        
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
//            long length = new File(filePath).length();//文件大小，计算百分比用
            byte[] bys = new byte[1024];
            int len = 0;
            while ((len = bis.read(bys)) != -1) {
                bos.write(bys, 0, len);
                bos.flush();	//字节流不flush导致最后图片不完整。（一部分里在缓冲流中）
//                client.uploadFrame.setValue(client.uploadFrame.getValue() + (int)(len/length));
            }
            s.shutdownOutput();	//不shutdown就不会停止
            
            //上传完成，反馈
//            client.uploadFrame.setString("文件上传成功！");
            client.uploadFrame.setTitle("文件上传100%");
            client.uploadFrame.setYes();
            client.uploadFrame.getFocusableWindowState();//获得焦点
            
            bis.close();
            s.close();
            
            client.chatFrame.set_sendFile_yes();//设置发送文件按钮可按
            //暴力点，遍历私聊窗口集合病设置发送文件按钮可用
            if (!client.c_singleFrames.isEmpty()) {
                Set<String> set = client.c_singleFrames.keySet();
                for (String key : set) {
                    SingleFrame value = client.c_singleFrames.get(key);
                    value.set_sendFile_yes();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("你还不能上传文件，购买会员即可开启此功能！");
        }
    }
    
    

}
