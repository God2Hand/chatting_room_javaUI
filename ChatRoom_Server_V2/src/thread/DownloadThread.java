/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Shinelon
 */
public class DownloadThread extends Thread {
    private Socket s;
    
    public DownloadThread(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            byte[] bys = new byte[1024];
            int len = 0;
            //接收文件名    
            InputStream is = s.getInputStream();
            len = is.read(bys);
            String filename = new String(bys, 0, len);
            
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src\\download\\" + filename));
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
            while ((len = bis.read(bys)) != -1) {
                bos.write(bys, 0, len);
                bos.flush();	//字节流不flush导致最后图片不完整。（一部分里在缓冲流中）
            }
            s.shutdownOutput();	//不shutdown就不会停止
            
            is.close();
            bis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
