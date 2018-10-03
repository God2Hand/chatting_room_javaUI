/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import dao.Information;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import view.ServerFrame;

/**
 * 这是服务器线程
 *      主要功能是监听并注册客户端socket线程
 *          顺便还有供外部停止的方法
 *      初始化中开启了广播线程，运行中开启了客户端socket通信线程
 * @author Shinelon
 */
public class ServerThread extends Thread {

    public ServerSocket serverSocket;//服务器Socket
    public int Port = 5000;//端口，不可更改，未来可能通过与Frame的交流达到可更改
    
    public ServerFrame serverFrame;//监听界面是否停止
    public BroadCast broadcast;//开启广播线程
    
    private boolean flag_exit = false;//未开始前"停止"按钮不可用、
    public void setFlag_exit(boolean b) {//与外界传送“停止”信息用
        flag_exit = b;
    }
    
    public Vector<Information> informations;//信息集合，头是执行何种操作的标志，前加id可区分对谁的操作//Vector线程安全
    public Vector<ClientThread> clients;//用户线程集合，存放用户线程
    public Map<Integer, String> users;//用户信息集合（用户列表），存放用户线程id 和名字[_id]/状态？
    
    //接收文件需要的东西
    public UploadServer uploadServer;//监听客户上传文件的请求
    public Vector<Information> file_info;//文件消息，完成时再发
    //每次加在最后面，读最后面，删除看filename
    
    //被下载文件需要的
    public DownloadServer downloadServer;//监听客户上传文件的请求

    //初始化,并开启了广播线程
    public ServerThread(ServerFrame serverFrame) {
        this.serverFrame = serverFrame;
        informations = new Vector<Information>();
        clients = new Vector<ClientThread>();
        users = new HashMap<Integer, String>();
        try {
            serverSocket = new ServerSocket(Port);//创建服务器Socket
        } catch (IOException e) {
            this.serverFrame.portError();//已经有服务器，端口被占用
            System.exit(0);
        }
        //开启广播线程，
        broadcast = new BroadCast(this);
        broadcast.setFlag_exit(true);//"停止"按钮可用
        broadcast.start();
        
        //开启文件上传线程
        file_info = new Vector<Information>();//没有这一行的话出锁对象异常
        uploadServer = new UploadServer(this);
        uploadServer.setFlag_exit(true);
        uploadServer.start();
        
        //开启文件下载线程
        downloadServer = new DownloadServer();
        downloadServer.setFlag_exit(true);
        downloadServer.start();
    }
    
    @Override
    public void run() {
        Socket socket;//客户端socket
        while (flag_exit) {//"停止"按钮可用，服务器运行
            if (serverSocket.isClosed()) {//如果服务器socket已经关了，"停止"按钮不可用，服务器关闭
                flag_exit = false;
            } else {
                //监听是否有socket连入
                try {
                    socket = serverSocket.accept();//阻塞式方法
                } catch (IOException e) {
                    socket = null;
                    flag_exit = false;
                }
                
                if (socket != null) {//有客户端socket连入
//                    System.out.println("监听到le !");
                    //开启客户线程
                    ClientThread clientThread = new ClientThread(socket, this);
//                    System.out.println("这里没执行到");
                    clientThread.setFlag_exit(true);//"停止"按钮可用，服务器运行
                    clientThread.start();
//                    System.out.println("这里没执行到");
                    synchronized (clients) {
                        clients.addElement(clientThread);//添加用户线程到集合中
                    }
//                    System.out.println("这里没执行到");
                    synchronized (informations) {
                        users.put((int) clientThread.getId(), "@login@");//用户线程id+请求登陆标志
                        Information outInfo = new Information();
                        outInfo.setMess(clientThread.getId() + "@clientThread");
                        informations.add(outInfo);//信息：新开了 用户id 的线程
                    }
//                    System.out.println("这里没执行到");
                    /*
                        用户连接过程：
                            用户     -- 连接-->     服务器
                            用户     <--你的id--    服务器
                            -用户保存id
                            用户     --名+id+登陆标志-->    服务器
                                                -判断重名，id替换，刷新用户列表并设置到界面
                                                 列表加入信息集合，才能广播
                            用户     <--广播所有用户更新列表--    服务器
                            -收到信息：
                                1.同名的更改
                                2.空列表分别存放username和id
                                3.设置显示列表
                    */
                }
            }
        }
    }
    
    //停止服务器
    public void stopServer() {
        if(this.isAlive()){
            try {
                //这里要不要遍历客户线程，逐个移除???????????????????????
                this.setFlag_exit(false);
                uploadServer.setFlag_exit(false);
                downloadServer.setFlag_exit(false);
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }
    
    
}
