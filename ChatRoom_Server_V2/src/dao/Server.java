/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import thread.ServerThread;
import view.ServerFrame;

/**
 * 这是服务器操作类。
 *      实现服务器的启、停。
 * @author Shinelon
 */
public class Server {

    private ServerThread serverThread;//需要与服务器线程交互

    public Server() {
    }

    //启动服务器
    public void startServer(ServerFrame serverFrame) {
        try {
            serverThread = new ServerThread(serverFrame);//创建一个服务器线程，参数是为了与界面交互（退出）
        } catch (Exception e) {
            System.exit(0);//提示后退出界面（提示在ServerThread的构造中）
        }
        serverThread.setFlag_exit(true);//"停止"按钮可用
        serverThread.start();//启动服务器线程
    }
    
    //停止服务器
    public void stopServer() {
        synchronized (serverThread.informations) {
            String str = "@serverexit";
            Information outInfo = new Information();
            outInfo.setMess(str);
            serverThread.informations.add(outInfo);//使用“消息”让广播线程来处理
        }
        serverThread.serverFrame.setDisMess("@serverexit");
        serverThread.serverFrame.setDisUsers("@serverexit");
        serverThread.stopServer();
    }

}
