/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;

import dao.Client;
import dao.Information;
import dao.UploadClient;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.JtpUtil;
import util.TrimUtil;
import util.UiUtil;

/**
 * 这是私聊界面
 * @author Shinelon
 */
public class SingleFrame extends javax.swing.JFrame {
    
    private Client client;//与客户线程交互
    
    public int userThreadID = 0;//titile存用户名，这个存id

    /**
     * Creates new form SingleFrame
     */
    public SingleFrame(Client client, String title) {
        this.client = client;
        UiUtil.beautiful();//适应平台界面
        initComponents();
        init(title);
    }
    
    private void init(String title) {
        this.setTitle(title);//设置窗体标题为对方用户名
        UiUtil.setFrameImage(this, "socket.jpg");//设置图标
        this.setResizable(false);//设置界面大小不可调
        UiUtil.setFrameCenter(this);//设置界面居中
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//2级窗口，应该不让程序退出//并且还应该remove
        addWindowListener(new WindowAdapter() {//设置窗体关闭
           @Override
           public void windowClosing(WindowEvent e) {
               jbt_exit.doClick();
           } 
        });
    }
    
    public void setExitNotify() {//私聊的对象的退出通知
        JtpUtil.myAppendStr(this.jtp_disMess, this.getTitle() + "已下线.....");
        jbt_sendMess.setEnabled(false);
        jbt_sendImag.setEnabled(false);
        jbt_sendFile.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtp_disMess = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jtf_input = new javax.swing.JTextField();
        jbt_sendMess = new javax.swing.JButton();
        jbt_sendImag = new javax.swing.JButton();
        jbt_sendFile = new javax.swing.JButton();
        jbt_exit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "聊天消息", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jtp_disMess.setEditable(false);
        jScrollPane1.setViewportView(jtp_disMess);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "发送消息", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jtf_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtf_inputActionPerformed(evt);
            }
        });
        jtf_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtf_inputKeyPressed(evt);
            }
        });

        jbt_sendMess.setText("发送");
        jbt_sendMess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_sendMessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jtf_input, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbt_sendMess, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtf_input)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbt_sendMess, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jbt_sendImag.setText("图片");
        jbt_sendImag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_sendImagActionPerformed(evt);
            }
        });

        jbt_sendFile.setText("文件");
        jbt_sendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_sendFileActionPerformed(evt);
            }
        });

        jbt_exit.setText("关闭");
        jbt_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_exitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jbt_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbt_sendImag, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbt_sendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbt_sendImag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbt_sendFile, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(jbt_exit))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtf_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtf_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtf_inputActionPerformed

    //发送私聊消息
    private void jbt_sendMessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendMessActionPerformed
        String str = TrimUtil.trim(jtf_input.getText());
        jtf_input.setText("");
        if(str.equals("")){
            JOptionPane.showMessageDialog(this, "信息不能为空");
        } else {//私聊的话我自己构建聊天信息就好了
            //构建聊天消息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String date = sdf.format(new Date());
            String mess = client.username + "  " + date + "\n" + str;
            //自己显示发的消息
            JtpUtil.myAppendStr(jtp_disMess, mess + "\n\n");//先双换行隔行吧
            //得到对方id
            int index = client.username_online.indexOf(this.getTitle());
            //构建到服务器的信息：自己的名 @single 自己的id @single 对方的id @single 消息内容 @single
            String head = client.username + "@single" + client.getThreadID() + "@single" +
            (int)client.clientuserid.get(index) + "@single" + mess + "@single";
            try {
                //发送
                Information outInfo = new Information();
                outInfo.setMess(head);//给服务器的退出信息格式
                client.dos.writeObject(outInfo);
                client.dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jbt_sendMessActionPerformed

    //窗体关闭
    private void jbt_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_exitActionPerformed
        client.c_singleFrames.remove(this.getTitle());
        this.dispose();
    }//GEN-LAST:event_jbt_exitActionPerformed

    //发送私聊图片
    private void jbt_sendImagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendImagActionPerformed
        JFileChooser jfc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("图片(jpg,gif,bmp,jpeg,png)", "gif", "jpg", "bmp", "jpeg", "png");
        jfc.setFileFilter(filter);

        int rtn = jfc.showOpenDialog(this);//弹框
        if (rtn == JFileChooser.APPROVE_OPTION) {//确认选择
            //构建图片
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            ImageIcon img = new ImageIcon(filePath);//构建图片对象
            
            //信息第一行
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String date = sdf.format(new Date());
            String mess = client.username + "  " + date + "\n";
            
            //得到对方id
            int index = client.username_online.indexOf(this.getTitle());
            //构建到服务器的信息：自己的名 @single 自己的id @single 对方的id @single 消息内容 @single
            String head = client.username + "@single" + client.getThreadID() + "@single" +
            (int)client.clientuserid.get(index) + "@single" + mess + "@single";
            try {
                //发送
                Information outInfo = new Information();
                outInfo.setMess(head);//给服务器的退出信息格式
                outInfo.setIsImag(true);//设置是图片
                outInfo.setImag(img);
                client.dos.writeObject(outInfo);
                client.dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            //自己显示发的消息
            JtpUtil.myAppendStr(jtp_disMess, mess);
            JtpUtil.myAppendImag(jtp_disMess, img);
            JtpUtil.myAppendStr(jtp_disMess, "\n\n");
        }
    }//GEN-LAST:event_jbt_sendImagActionPerformed

    //发送文件
    private void jbt_sendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendFileActionPerformed
        if (client.uploadFrame != null) {
            JOptionPane.showMessageDialog(this, "一次只能发送一个文件，请等待群聊文件发送完毕！");
            return;
        }
        JFileChooser jfc = new JFileChooser();
        int rtn = jfc.showOpenDialog(this);//弹框
        if (rtn == JFileChooser.APPROVE_OPTION) {//确认选择
            this.jbt_sendFile.setEnabled(false);//确认后按钮不可用，一次只能传一个文件
            String filePath = jfc.getSelectedFile().getAbsolutePath();

            //构建信息
            //信息第一行
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String date = sdf.format(new Date());
            String mess = client.username + "  " + date + "  " + "（发送了一个文件，点击下载）\n";
            //得到对方id
            int index = client.username_online.indexOf(this.getTitle());
            //构建到服务器的信息：自己的名 @single 自己的id @single 对方的id @single 消息内容 @single
            String head = client.username + "@single" + client.getThreadID() + "@single" +
            (int)client.clientuserid.get(index) + "@single" + mess + "@single";
            
            //文件信息
            String filename = filePath.substring(filePath.lastIndexOf("\\")+1);//获取文件名
            Information outInfo = new Information();
            outInfo.setMess(head);//忘了加这个服务器就空指针了
            outInfo.setIsFile(true);
            outInfo.setFile(client.getThreadID() + "_" + filename);
            try {
                //发送
                client.dos.writeObject(outInfo);
                client.dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            //同时开启一个发送文件线程
            new Thread(new UploadClient(client, filePath)).start();
        }
    }//GEN-LAST:event_jbt_sendFileActionPerformed

    private void jtf_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inputKeyPressed
        if(evt.getKeyChar()==KeyEvent.VK_ENTER) {
            jbt_sendMess.doClick();
        }
    }//GEN-LAST:event_jtf_inputKeyPressed

    //设置发送文件按钮可用
    public void set_sendFile_yes() {
        this.jbt_sendFile.setEnabled(true);
    }
    
    //设置私聊聊天记录
    public void setDisMess(String  mess) {
        JtpUtil.myAppendStr(jtp_disMess, mess + "\n\n");//先双换行隔行吧
    }   
    
    //设置私聊图片
    public void setDisImage(String mess, ImageIcon img) {
        JtpUtil.myAppendStr(jtp_disMess, mess);
        JtpUtil.myAppendImag(jtp_disMess, img);
        JtpUtil.myAppendStr(jtp_disMess, "\n\n");
    }
    
    //设置私聊文件
    public void setDisFile(String mess, String file) {
        JtpUtil.myAppendStr(jtp_disMess, mess);
        JtpUtil.myAppendFile(jtp_disMess, file, client);
        JtpUtil.myAppendStr(jtp_disMess, "\n\n");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbt_exit;
    private javax.swing.JButton jbt_sendFile;
    private javax.swing.JButton jbt_sendImag;
    private javax.swing.JButton jbt_sendMess;
    private javax.swing.JTextField jtf_input;
    private javax.swing.JTextPane jtp_disMess;
    // End of variables declaration//GEN-END:variables
}