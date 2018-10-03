/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;

import dao.Client;
import dao.Information;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.JtpUtil;
import util.TrimUtil;
import util.UiUtil;

/**
 * 这是聊天室界面
 * @author Shinelon
 */
public class ChatFrame extends javax.swing.JFrame {
    private Client client;

    /**
     * Creates new form ClientFrame
     */
    public ChatFrame(Client client, String title) {
        this.client = client;
        UiUtil.beautiful();//适应平台界面
        initComponents();
        init(title);
    }
    
    private void init(String title) {
        this.setTitle("聊天室  当前用户：" + title);//设置窗体标题
        UiUtil.setFrameImage(this, "socket.jpg");//设置图标
        this.setResizable(false);//设置界面大小不可调
        UiUtil.setFrameCenter(this);//设置界面居中
        addWindowListener(new WindowAdapter() {//设置窗体关闭
           @Override
           public void windowClosing(WindowEvent e) {
               jbt_exit.doClick();
           } 
        });
    }
    
    //更新用户列表
    public void setDisUsers(String head) {
        String[] userInfos = head.split("@userlist");//存放新所有用户名与id信息
        String[] userInfo = new String[userInfos.length / 2];//临时数组
        
        //检查是否同名，改动自己的框，弹出提示
        for (int i = 1; i < userInfos.length; i++) {
            int id_user = 0;
            try {
                id_user = Integer.parseInt(userInfos[i]);
                if (client.getThreadID() == id_user) {//找到自己id
                    if (!client.username.equals(userInfos[i - 1])) {//新旧名字不一样
                        JOptionPane.showMessageDialog(this, "由于有同名的用户登录，所以您的用户名后面加上了编号");
                        client.username = userInfos[i - 1];//取到自己名字
                        this.setTitle("聊天室  " + client.username);
                    }
                    break;
                } else {
                    i++;
                }
            } catch(Exception e) {
            }
        }
        
        if (userInfos.length == 2) {//第一个用户或者剩一个用户
            if (!client.c_singleFrames.isEmpty()) {//有下线的人的私聊窗口
                ListModel list = jlt_disUsers.getModel();//动态操作
                for (int i = 0; i < list.getSize(); i++) {
                    if (client.c_singleFrames.get(list.getElementAt(i)) != null) {
                        client.c_singleFrames.get(list.getElementAt(i)).setExitNotify();
                    }
                }
            }
            jlt_disUsers.removeAll();
            jlt_disUsers.setListData(new String[]{});
        } else {
            if ((userInfos.length / 2 - 1) < client.username_online.size()) {//有人下线
                //得到用户名集合
                List<String> rec = new ArrayList<String>();
                int i = 0;
                for (; i < userInfos.length; i++) {
                    rec.add(0, userInfos[i++]);
                }
                //找到下线的人在列表的索引位置，存到i中
                for (i = 0; i < client.username_online.size(); i++) {
                    if (!rec.contains(client.username_online.get(i))) {//如果收到的人不在列表内
                        break;//已经记录了在列表中的位置
                    }
                }
                String name = client.username_online.get(i);//得到下线的用户名
                client.username_online.remove(i);//用户列表移除下线的用户名
                client.clientuserid.remove(i);//同时移除下线的用户id
                
                //如果有和下线的人私聊的窗口
                if (client.c_singleFrames.containsKey(name)) {
                    //client.c_singleFrames.get(name).closeSingleFrame();//关闭私聊窗口
                    client.c_singleFrames.get(name).setExitNotify();//设置不可发送信息+
                    client.c_singleFrames.remove(name);//私聊窗口集移除掉窗口
                }
                
            } else {//其他用户or新用户 收到新用户加入的更新用户列表
                List<Integer> online = new ArrayList<Integer>();
                for (int i = 0; i < client.username_online.size(); i++) {//非刚登陆的其他用户
                    online.add(0, client.clientuserid.get(i));//创建之前用户id列表online
                }
                
                if (online.isEmpty()) {//刚登录的用户，除自己外全部信息加入即可
                    for (int i = 1; i < userInfos.length; i++) {
                        if ((int) Integer.parseInt(userInfos[i]) != client.getThreadID()) {
                            client.username_online.add(0, userInfos[i - 1]);
                            client.clientuserid.add(0, Integer.parseInt(userInfos[i]));
                        }//注：只完成了信息录入，界面后面统一搞
                        i++;
                    }
                } else {//非刚登陆的其他用户
                    for (int i = 1; i < userInfos.length; i++) {//遍历新列表
                        if (Integer.parseInt(userInfos[i]) != client.getThreadID()) {//如果不是当前线程
                            if (!online.contains(Integer.parseInt(userInfos[i]))) {//如果是新加入的线程
                                client.username_online.add(0, userInfos[i - 1]);
                                client.clientuserid.add(0, Integer.parseInt(userInfos[i]));
                            }
                        }
                        i++;
                    }
                }
            }
            
            //更新列表
            for (int i = 0; i < client.username_online.size(); i++) {
                userInfo[i] = client.username_online.get(i);//从更新后的列表得到所有用户名
            }
            jlt_disUsers.removeAll();
            jlt_disUsers.setListData(userInfo);
            
        }
        
        
    }
    
    //服务器退出
    public void serverexit() {
        JOptionPane.showMessageDialog(this, "服务器已关闭", "提示", JOptionPane.OK_OPTION);
        client.exitClient();
        this.setVisible(false);
        this.dispose();
    }
    
    //更新群聊消息
    public void setDisMess(String head) {
        int local = head.indexOf("@chat");
        JtpUtil.myAppendStr(this.jtp_disMess, head.substring(0, local) + "\n\n");//先双换行隔行吧
    }
    
    //更新群聊图片
    public void setDisImage(Information info_re) {
        String head = info_re.getMess();
        int local = head.indexOf("@chat");
        JtpUtil.myAppendStr(this.jtp_disMess, head.substring(0, local));
        JtpUtil.myAppendImag(this.jtp_disMess, info_re.getImag());
        JtpUtil.myAppendStr(this.jtp_disMess, "\n\n");
    }
    
    //更新群聊文件
    public void setDisFile(Information info_re) {
        //显示基本信息
        String head = info_re.getMess();
        int local = head.indexOf("@chat");
        JtpUtil.myAppendStr(this.jtp_disMess, head.substring(0, local));
        //显示下载文件按钮
        JtpUtil.myAppendFile(jtp_disMess, info_re.getFile(), client);
        JtpUtil.myAppendStr(this.jtp_disMess, "\n\n");
    }
    
    //创建私聊窗口，放入私聊窗口集合
    public void createSingleFrame(String name) {
        SingleFrame c_singleFrame = new SingleFrame(client, name);//根据用户名创建私聊窗口
        client.c_singleFrames.put(name, c_singleFrame);//把私聊窗口收入私聊窗口集合
        c_singleFrame.userThreadID = client.clientuserid.get(client.username_online.indexOf(name));//帮它设置id
        c_singleFrame.setVisible(true);//让它显示可见
    }
    
    //收到私人聊天信息，设置显示
    public void setSingleFrame(Information info_re) {
        String head = info_re.getMess();
        String[] infos = head.split("@single");//解析
        String sender = infos[0];//发送人名字
        if (Integer.parseInt(infos[1]) == client.getThreadID()) {//如果发送方是自己的话，是自己发的文件
            int index = client.clientuserid.indexOf(Integer.parseInt(infos[2]));
                //这里我错了好多次，数组越界异常。
            sender = client.username_online.get(index);//是对方为伪发送人（私聊窗口对象）
        }
        if (!client.c_singleFrames.containsKey(sender)) {//如果没开聊天窗口的话
            createSingleFrame(sender);//调用创建私聊窗口的方法
        }
        if (info_re.isImag()) {
            client.c_singleFrames.get(sender).setDisImage(infos[3], info_re.getImag());
        } else if (info_re.isFile()) {
            client.c_singleFrames.get(sender).setDisFile(infos[3], info_re.getFile());
        } else {
            client.c_singleFrames.get(sender).setDisMess(infos[3]);
        }
        client.c_singleFrames.get(sender).getFocusableWindowState();//获得焦点
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jlt_disUsers = new javax.swing.JList();
        jbt_single = new javax.swing.JButton();
        jbt_clear = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jtf_input = new javax.swing.JTextField();
        jbt_sendMess = new javax.swing.JButton();
        jbt_sendImag = new javax.swing.JButton();
        jbt_sendFile = new javax.swing.JButton();
        jbt_exit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "聊天消息", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jtp_disMess.setEditable(false);
        jScrollPane1.setViewportView(jtp_disMess);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("在线用户"));

        jScrollPane2.setViewportView(jlt_disUsers);

        jbt_single.setText("私聊");
        jbt_single.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_singleActionPerformed(evt);
            }
        });

        jbt_clear.setText("清空聊天室记录");
        jbt_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_clearActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

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

        jbt_sendImag.setText("发送图片");
        jbt_sendImag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_sendImagActionPerformed(evt);
            }
        });

        jbt_sendFile.setText("发送文件");
        jbt_sendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbt_sendFileActionPerformed(evt);
            }
        });

        jbt_exit.setText("退出");
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addComponent(jbt_single, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(25, 25, 25))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSeparator1)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbt_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jbt_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtf_input)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbt_sendMess, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbt_sendImag)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbt_sendFile)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbt_single, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbt_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbt_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtf_input, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbt_sendMess, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbt_sendImag, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbt_sendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //私聊
    private void jbt_singleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_singleActionPerformed
        String user_names = (String) jlt_disUsers.getSelectedValue();
        if (user_names == null) {//未选中私聊对象
            JOptionPane.showMessageDialog(this, "您未选择聊天对象\n请选择要单独聊天的对象");
        } else {
            if (!client.c_singleFrames.containsKey(user_names)) {//没有私聊界面就创建
                createSingleFrame(user_names);
//                System.out.println("私聊成功！");
            } else {//有私聊界面了，获得焦点就可以了
                client.c_singleFrames.get(user_names).setFocusableWindowState(true);
            }
        }
    }//GEN-LAST:event_jbt_singleActionPerformed

    //清空聊天室聊天记录按钮
    private void jbt_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_clearActionPerformed
        this.jtp_disMess.setText("");
    }//GEN-LAST:event_jbt_clearActionPerformed

    //发送聊天室信息
    private void jbt_sendMessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendMessActionPerformed
        String mess = TrimUtil.trim(jtf_input.getText());
        jtf_input.setText("");
        if (mess.equals("")) {
            JOptionPane.showMessageDialog(this, "消息不能为空！");
            jtf_input.setText("");
        } else {
            client.sendMess(mess);
        }
    }//GEN-LAST:event_jbt_sendMessActionPerformed

    //退出聊天室
    private void jbt_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_exitActionPerformed
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this,"是否确定要退出聊天室？", "提示", JOptionPane.OK_CANCEL_OPTION)) {
            this.setVisible(false);
            client.exitChat();
            this.dispose();//释放资源+
            System.exit(0);
        }
    }//GEN-LAST:event_jbt_exitActionPerformed

    //点击了发送图片按钮
    private void jbt_sendImagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendImagActionPerformed
        JFileChooser jfc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("图片(jpg,gif,bmp,jpeg,png)", "gif", "jpg", "bmp", "jpeg", "png");
        jfc.setFileFilter(filter);

        int rtn = jfc.showOpenDialog(this);//弹框
        if (rtn == JFileChooser.APPROVE_OPTION) {//确认选择
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            client.sendImag(filePath);
        }
    }//GEN-LAST:event_jbt_sendImagActionPerformed

    //发送文件
    private void jbt_sendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbt_sendFileActionPerformed
        if (client.uploadFrame != null) {
            JOptionPane.showMessageDialog(this, "一次只能发送一个文件，请等待私聊文件发送完毕！");
            return;
        }
        JFileChooser jfc = new JFileChooser();
        int rtn = jfc.showOpenDialog(this);//弹框
        if (rtn == JFileChooser.APPROVE_OPTION) {//确认选择
            this.jbt_sendFile.setEnabled(false);//确认后按钮不可用，一次只能传一个文件
            String filePath = jfc.getSelectedFile().getAbsolutePath();
            client.sendFile(filePath);
        }
    }//GEN-LAST:event_jbt_sendFileActionPerformed

    private void jtf_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_inputKeyPressed
        if(evt.getKeyChar()==KeyEvent.VK_ENTER) {
            jbt_sendMess.doClick();
        }
    }//GEN-LAST:event_jtf_inputKeyPressed

    //对外提供设置上传文件按钮可用
    public void set_sendFile_yes() {
        this.jbt_sendFile.setEnabled(true);
    }
            
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton jbt_clear;
    private javax.swing.JButton jbt_exit;
    private javax.swing.JButton jbt_sendFile;
    private javax.swing.JButton jbt_sendImag;
    private javax.swing.JButton jbt_sendMess;
    private javax.swing.JButton jbt_single;
    private javax.swing.JList jlt_disUsers;
    private javax.swing.JTextField jtf_input;
    private javax.swing.JTextPane jtp_disMess;
    // End of variables declaration//GEN-END:variables
}
