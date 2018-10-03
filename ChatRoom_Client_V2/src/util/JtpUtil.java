/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import dao.Client;
import dao.DownloadThread;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * 这是我对JTextPane追加文字、追加图片
 * @author Shinelon
 */
public class JtpUtil {
    private JtpUtil() {
    }
    
    /**
     * 追加文字
     * @param jtp_disMess
     * @param str
     */
    public static void myAppendStr(JTextPane jtp_disMess, String str) {
        StyledDocument doc = jtp_disMess.getStyledDocument();//获得编辑模型
        jtp_disMess.setCaretPosition(doc.getLength());//设置 TextComponent 的文本插入符的位置。
        try {
            doc.insertString(doc.getLength(), str, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        jtp_disMess.setCaretPosition(doc.getLength());//用来接到后面的位置
    }
    
    //追加图片
    public static void myAppendImag(JTextPane jtp_disMess, ImageIcon img) {
        jtp_disMess.insertIcon(img);
    }
    
    //追加文件（按钮）//其实这个功能不通用，不该在这里
    public static void myAppendFile(JTextPane jtp_disMess, String filename, Client client){
        int local = filename.indexOf("_");
        JButton jbt_file = new JButton(filename.substring(local + 1));
        jbt_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("这里用一个方法开启下载文件事件");
                if (client.downloadFrame != null) {
                    JOptionPane.showMessageDialog(client.chatFrame, "不能同时下载两个文件！（包括群聊和私聊文件）");
                    return;
                }
                JFileChooser jfc = new JFileChooser();  
                jfc.setDialogTitle("选择文件保存的位置");  
                jfc.setSelectedFile(new File("D:\\"));  
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 选择文件夹。  
                int r = jfc.showOpenDialog(jtp_disMess);  
                if (r == JFileChooser.APPROVE_OPTION) { 
                    String path = jfc.getSelectedFile().getPath();
//                    System.out.println(path);
//                    return;
                    jbt_file.setEnabled(false);
                    new Thread(new DownloadThread(jbt_file, client, path, filename)).start();
                }
            }
        });
        jtp_disMess.insertComponent(jbt_file);
    }
 
}
