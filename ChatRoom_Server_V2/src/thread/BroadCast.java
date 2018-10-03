/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import dao.Information;
import java.io.IOException;

/**
 * 这里是广播线程
 *      
 * @author Shinelon
 */
public class BroadCast extends Thread {

    ServerThread serverThread;//服务器线程，使用服务器中的各种信息
    ClientThread clientThread;//客户线程，发送信息用（广播信息or私聊的转发）
    
    Information information;//读取informations中的消息
    String head;//读取头，根据头操作
    
    private boolean flag_exit = false;//"停止"按钮不可用，服务器未启动
    public void setFlag_exit(boolean b) {
        flag_exit = b;
    }

    //初始化
    public BroadCast(ServerThread serverThread) {
        this.serverThread = serverThread;//传入服务器线程
    }
    
    @Override
    public void run(){
        boolean flag = true;//是否正式发消息
        while (flag_exit) {//"停止"按钮可用，服务器启动
            synchronized (serverThread.informations) {
                if(serverThread.informations.isEmpty()){//如果消息空，就跳，继续读
                    continue;
                }else{//informations中有内容的话
                    //按顺序读第一个并移除（读完消息就移除掉了）
                    information = serverThread.informations.firstElement();
                    head = information.getMess();
                    serverThread.informations.removeElement(information);
                    
                    //如果是“id@clientThread”，则是登陆过程，注册ClientTHread
                    if(head.contains("@clientThread")){
                        flag = false;//设置不是正式发送消息
                    }
                }
            }
            //informations中有内容的话
            synchronized (serverThread.clients) {
                for (int i = 0; i < serverThread.clients.size(); i++) {
                    //获取到每一个clientThread
                    clientThread = serverThread.clients.elementAt(i);
                    if (flag) {//如果正式发消息
                        try {
                            //如果information中内容是“群聊”、“用户列表”、“服务器停止”，则把information群发
                            if(head.contains("@chat") || head.contains("@userlist") || head.contains("@serverexit")){
                                clientThread.dos.writeObject(information);
                                clientThread.dos.flush();
                            } 
                            //如果是私聊的话，单发（转发单个）
                            if(head.contains("@single")){
                                String[] info = head.split("@single");//解析
                                int id_thread = Integer.parseInt(info[2]);//得到接收方id
                                int count = 1;//发送人数为1，非文件无意义
                                int id_send = id_thread;//发送方id，非文件无意义
                                if (information.isFile()) {//如果是私聊文件的话，还要提醒发送人
                                    id_send = Integer.parseInt(info[1]);
                                    count = 2;
                                }
                                for(int j = 0; j < serverThread.clients.size(); j++){//遍历集合找人
                                    if(id_thread == serverThread.clients.get(j).getId() || id_send == serverThread.clients.get(j).getId()){//找到他了
                                        serverThread.clients.get(j).dos.writeObject(information);//向他发送
                                        serverThread.clients.get(j).dos.flush();
                                        --count;
                                        if (count == 0) {
                                            i = serverThread.clients.size();//外层循环不必再循环了，都发完了
                                            break;//退出找对的人发消息的循环
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                        }
                        
                        
                    } else {//非正式发消息
                        String value = serverThread.users.get((int)clientThread.getId());//得到用户的状态or名
                        if (value.equals("@login@")) {//如果是请求登陆的用户的话
                            flag = true;//可以准备正式发消息了,因为我会处理登陆
                            try {
                                Information outInfo = new Information();
                                outInfo.setMess(head);
                                clientThread.dos.writeObject(outInfo);//向他发送：他的id@clientThread
                                clientThread.dos.flush();
//                                if(str.contains("@exit")){
//                                    serverThread.clients.remove(i);
//                                    clientThread.closeClienthread(clientThread);
//                                }
                            } catch (IOException e) {
                            }
                            break;//重新开始读message
                        }
                    }
                }
            }
            
            information = null;//置空，通知回收资源
            //服务器因为这里面没哟
            
            if(head.contains("@serverexit")){//如果服务器停止
                serverThread.users.clear();//发完信息了，可以清空用户信息了
                serverThread.informations.clear();
                serverThread.file_info.clear();
                this.flag_exit = false;//停止这个线程
            }
            
        }
    }
    
}
