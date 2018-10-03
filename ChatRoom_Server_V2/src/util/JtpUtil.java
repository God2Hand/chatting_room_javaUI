/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * 注意服务器这个类和客户端的不一样！！(File那里)
 */

package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
    public static void myAppendFile(JTextPane jtp_disMess, String filename){
        JButton jbt_file = new JButton(filename);
        jbt_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(jtp_disMess, "文件都存在服务器这了，就不需要下载了！");
//                System.out.println("这里用一个方法开启下载文件事件");
            }
        });
        jtp_disMess.insertComponent(jbt_file);
    }
 
}
