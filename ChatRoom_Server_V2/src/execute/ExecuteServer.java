/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package execute;

import dao.Server;
import view.ServerFrame;

/**
 * 这是启动类
 * @author Shinelon
 */
public class ExecuteServer {
    public static void main(String[] args) {
        Server server = new Server();//创建服务器操作类
        ServerFrame serverFrame = new ServerFrame(server);//服务器界面
        serverFrame.setVisible(true);
    }
}
