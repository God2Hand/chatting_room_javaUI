/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import dao.Information;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这里接收客户端信息的线程，简称客户线程
 *      客户端  --信息--> 服务器
 * @author Shinelon
 */
public class ClientThread extends Thread {
    public Socket clientSocket;//客户传来信息的socket
    public ServerThread serverThread;//本服务器socket
    public ObjectInputStream dis;
    public ObjectOutputStream dos;
    
    private boolean flag_exit = false;//未开启前，"停止"按钮不可用
    public void setFlag_exit(boolean b) {//外界交流“停止”
	flag_exit = b;
    }

    //初始化
    public ClientThread(Socket socket, ServerThread serverThread) {
        clientSocket = socket;
        this.serverThread = serverThread;
        try {
//            System.out.println("这里执行到，下面应该就出错了");
            /*
             * 经验总结，注意这里：
             *      在网络通讯中，主机与客户端若使用ObjectInputStream与ObjectOutputStream建立对象通讯，必须注重声明此两个对象的顺序。
             *      也就是，一方out前，一方in前
             * 因为：在创建ObjectInputStream对象时会检查ObjectOutputStream所传过来了头信息，如果没有信息将一直会阻塞
            */
            dos = new ObjectOutputStream(clientSocket.getOutputStream());//dos提到前面来
            dis = new ObjectInputStream(clientSocket.getInputStream());
//            System.out.println("果然出错了");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        while (flag_exit) {//"停止"按钮可用，服务器运行
            ClientThread client_exit = null;//作为退出线程的标志
            try {//读客户端信息
                Information info_re = (Information)dis.readObject();
                String head = info_re.getMess();
                
                if (head.contains("@login")) {
                //收到登陆反馈，注册登陆信息到user中
                    String[] userInfo = head.split("@login");//用户名+id
                    //得到用户id，移除旧的预备登陆信息，加入新的用户信息
                    int userID = Integer.parseInt(userInfo[1]);
                    serverThread.users.remove(userID);
                    if(serverThread.users.containsValue(userInfo[0])){//如果有这个名字，后面加上“_id”
                        serverThread.users.put(userID, userInfo[0] + "_" + userInfo[1]);
                    } else {
                        serverThread.users.put(userID, userInfo[0]);
                    }
                    //遍历客户线程集，得到id，再拿到用户名，隔开着装入sb中形成userNames
                    head = null;
                    StringBuffer sb = new StringBuffer();
                    synchronized (serverThread.clients) {
                        for(int i = 0; i < serverThread.clients.size(); i++){
                            int threadID = (int) serverThread.clients.elementAt(i).getId();
                            sb.append((String)serverThread.users.get(new Integer(threadID)) + "@userlist");
                            sb.append(threadID + "@userlist");
                        }
                    }
                    String userNames = new String(sb);
                    //设置用户列表
                    serverThread.serverFrame.setDisUsers(userNames);
                    head = userNames;
                } else {
                //收到非登陆信息
                    if(head.contains("@exit")){
                    //如果收到客户线程关闭信息
                        //得到退出线程的id
                        String[] userInfo = head.split("@exit");
                        int userID = Integer.parseInt(userInfo[1]);
                        //用户信息users移除信息
                        serverThread.users.remove(userID);
                        head = null;//清空消息
                        StringBuffer sb = new StringBuffer();
                        synchronized (serverThread.clients) {
                            for(int i = 0; i < serverThread.clients.size(); i++){
                                int threadID = (int) serverThread.clients.elementAt(i).getId();
                                if(userID == threadID){//如果找到这个id
                                    client_exit = serverThread.clients.elementAt(i);//得到这个线程
//                                    client_exit.setFlag_exit(false);//线程终止标志+
//                                    client_exit.clientSocket.close();//关闭线程+//应该放执行后关，为了后面的代码复用
                                    //用户线程信息移去线程
                                    serverThread.clients.removeElementAt(i);
                                    i--;
                                }else{//否则，记录在线用户新列表
                                    sb.append((String)serverThread.users.get(new Integer(threadID)) + "@userlist");
                                    sb.append(threadID + "@userlist");
                                }
                            }
                        }
                        String userNames = new String(sb);
                        if(userNames.equals("")){//如果无用户了
                            serverThread.serverFrame.setDisUsers("@userlist");
                        }else{//否则，更新用户列表界面
                            serverThread.serverFrame.setDisUsers(userNames);
                        }
                        head = userNames;//在下面的代码设置信息，交由广播线程处理
                    } else {
                    //如果收到发送消息
                        if(head.contains("@chat")){//如果是发送群聊
                            //获取到信息
                            String[] chat = head.split("@chat");
                            //获取收到信息时间
                            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            String date = form.format(new Date());
                            //构建群发信息
                            StringBuffer sb = new StringBuffer();
                            if (info_re.isFile()) {//如果是文件消息的话，加上提示
                                sb.append(chat[0] + "  " + date);
                                sb.append("  " + "(上传了文件，点击下载)\n");
                            } else {//其他信息不用提示
                                sb.append(chat[0] + "  " + date + "\n");//"username 时间" + 换行
                            }
                            if (!info_re.isImag() && !info_re.isFile()) {//文本信息的话的话加入聊天信息
                                sb.append(chat[2]);//"聊天消息"
                            }
                            sb.append("@chat");//加入信息结束标记
                            String str = new String(sb);
                            head = str;//存入message，交由广播线程发送
                            //设置自己聊天室的聊天记录  
                            if (info_re.isImag()) {//如果图片的话，还要设置图片
                                info_re.setMess(head);
                                serverThread.serverFrame.setDisImage(info_re);
                            } else if (info_re.isFile()) {//如果是文件的话，新建线程
                                //首先还要传递information过去存储，然后置消息空
                                //等文件上传完了再显示消息
                                //好吧，都放到后面去了
                            } else {
                                serverThread.serverFrame.setDisMess(head);
                            }
                        } else if(head.contains("@single")){//发送私聊的话，只需放入信息集合中，交由广播线程处理
                        }
                            
                    }
                    
                }//信息读取结束
                
                //如果信息非空，加入到待处理的信息messages中，交由广播线程处理
                synchronized (serverThread.informations) {
                    if(head != null){
                        info_re.setMess(head);
                        if (info_re.isFile()) {
                            synchronized (serverThread.file_info) {//双锁注意一下
                                //如果锁对象为空，空指针异常，已解决
                                serverThread.file_info.addElement(info_re);//文件信息存储，上传完成后再发
                                serverThread.file_info.notify();//唤醒监听文件上传线程
                            }
                        } else {
                            serverThread.informations.addElement(info_re);//设置信息，交由广播线程处理
                        }
                    }
                }
                //如果该线程退出
                if (client_exit != null) {
                    client_exit.setFlag_exit(false);//线程终止标志+
                    client_exit.clientSocket.close();
                }
                
            } catch (EOFException e) {
                //如果是读到末尾，不做处理
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
}
