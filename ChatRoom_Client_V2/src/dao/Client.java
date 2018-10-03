/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import view.ChatFrame;
import view.DownloadFrame;
import view.SingleFrame;
import view.UploadFrame;

/**
 * 这是用户功能操作类
 * @author Shinelon
 */
public class Client extends Thread {
    private String ip;//为了上传文件
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public UploadFrame uploadFrame;//文件上传进度界面
    
    //文件下载
    public DownloadFrame downloadFrame;//文件下载进度界面

    public Socket c_socket;//通信socket
    public ObjectInputStream dis = null;
    public ObjectOutputStream dos = null;
    public ChatFrame chatFrame;//与聊天室界面交互
    
    private boolean flag_exit = false;//“退出”按钮不可用，连接未启动
    public Information info_re;//读取服务器发来的消息
    public String head;//读取服务器发来的消息头
    
    public Map<String, SingleFrame> c_singleFrames;//私聊窗口集合，名字标识
    public  List<String> username_online;//在线用户名列表
    public  List<Integer> clientuserid;//与在线用户名对应的id

    public String username = null;//自己的用户名
    private int threadID;//自己在服务器中的线程id
    public int getThreadID() {
        return threadID;
    }
    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    public Client(){
        c_singleFrames = new HashMap<String, SingleFrame>();
        username_online = new ArrayList<String>();
        clientuserid = new ArrayList<Integer>();
    }
    
    //连接服务器
    public String login(String username, String ip, String port) {
        this.username = username;
        String login_mess = null;
        try {
            c_socket = new Socket(ip, Integer.parseInt(port));
        } catch (NumberFormatException e) {
            login_mess = "连接的服务器端口号port为整数,取值范围为：1024<port<65535";
            return login_mess;
        } catch (UnknownHostException e) {
            login_mess = "主机地址错误";
            return login_mess;
        } catch (IOException e) {
            login_mess = "连接服务其失败，请稍后再试";
            return login_mess;
        }
        return "true";
    }

    //退出登陆
    public void exitLogin(JFrame loginFrame) {
        loginFrame.dispose();
        System.exit(0);
    }

    //登陆成功，进入聊天室，显示聊天室界面
    public void showChatFrame(String username) {
        //初始化与服务器端通信的输入输出流
        try {
//            System.out.println("执行到这里了");
            dis = new ObjectInputStream(c_socket.getInputStream());
            dos = new ObjectOutputStream(c_socket.getOutputStream());
//            System.out.println("这里没有执行到");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.chatFrame = new ChatFrame(this, username);//用户名是为了设置聊天室标题
        this.chatFrame.setVisible(true);

        flag_exit = true;//“退出”按钮可用，连接启动
        this.start();//启动线程
    }
    
    @Override
    public void run() {
        while (flag_exit) {//“退出”按钮可用，连接启动
            try {
                try {
                    info_re = (Information)dis.readObject();//接收服务器消息
                } catch (ClassNotFoundException ex) {
                    System.out.println("转换出错！");
                }
                head = info_re.getMess();    
            } catch (IOException e) {//连接断开的话，异常
                flag_exit = false;//“退出”按钮不可用，连接关闭
//		if(!chat_re.contains("@serverexit")){
//                    chat_re = null;
//		}
            }
            
            //如果服务器有发来消息
            if (head != null) {
                if(head.contains("@clientThread")) {//如果是核对登陆的话
                    int local = head.indexOf("@clientThread");//分割标志
                    setThreadID(Integer.parseInt(head.substring(0, local)));//设置我客户端的id
                    try {//----------------------------------来这里复制
                        Information outInfo = new Information();
                        outInfo.setMess(username + "@login" + getThreadID() + "@login");
                        dos.writeObject(outInfo);//发送正式登陆，夹带用户名和id
                        dos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//否则是通信消息
                    if(head.contains("@userlist")){//1.更新用户列表
                        chatFrame.setDisUsers(head);
                    } else if(head.contains("@chat")) {//3.收到群发消息
//                        System.out.println(info_re);
                        if(info_re.isImag) {//收到群发图片
                            chatFrame.setDisImage(info_re);
                        } else if (info_re.isFile) {//收到群发文件
                            chatFrame.setDisFile(info_re);
                        } else {//收到群发消息
                            chatFrame.setDisMess(head);
                        }
                    } else if(head.contains("@serverexit")) {//2.服务器退出
                        chatFrame.serverexit();
                    } else if(head.contains("@single")) {//4.收到私聊消息
                        chatFrame.setSingleFrame(info_re);
                    }
                }
            }
            
//            info_re = null;//置空，通知回收资源
              /*
               * 这里不知道为什么加了的话，在退出客户端的时候在138行有个info_re的空指针异常
               * 估计应该是Object流释放时候发送控制信息吧
               * 但是为什么最后一个退出的客户端不会呢
               */
        }
    }
    
    //退出聊天室
    public void exitChat() {
        try {
            Information outInfo = new Information();
            outInfo.setMess(username + "@exit" + getThreadID() + "@exit");//给服务器的退出信息格式
            dos.writeObject(outInfo);
            dos.flush();
            flag_exit = false;
            //我觉得这里应该遍历一下私聊窗口集，然后逐一关闭------------------------
            //不，不用了
            c_socket.close();//关闭socket  
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    
    //服务器关闭，退出聊天室
    public void exitClient() {
        flag_exit = false;
        try {
            //同上，我觉得这里应该遍历一下私聊窗口集，然后逐一关闭------------------------
            c_socket.close();//关闭socket
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    //发送消息
    public void sendMess(String mess) {
        try {
            Information outInfo = new Information();
            outInfo.setMess(username + "@chat" + getThreadID() + "@chat" + mess + "@chat");//给服务器的退出信息格式
            dos.writeObject(outInfo);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    //发送群聊图片
    public void sendImag(String filePath) {
        try {
            //构建图片信息
            Information outInfo = new Information();
            outInfo.setMess(username + "@chat" + getThreadID() + "@chat" + "@imag" + "@chat");
            outInfo.setIsImag(true);//设置消息图片标志
            ImageIcon img = new ImageIcon(filePath);//构建图片对象
            outInfo.setImag(img);
            
            //发送
            dos.writeObject(outInfo);
            dos.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //发送文件
    public void sendFile(String filePath) {
        //文件消息
        String filename = filePath.substring(filePath.lastIndexOf("\\")+1);//获取文件名
        Information outInfo = new Information();
        outInfo.setMess(username + "@chat" + getThreadID() + "@chat" + filename + "@chat");
        outInfo.setIsFile(true);
        outInfo.setFile(getThreadID() + "_" + filename);
        
        //发送
        try {
            dos.writeObject(outInfo);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //同时开启一个发送文件线程
        new Thread(new UploadClient(this, filePath)).start();
    }
    
}
