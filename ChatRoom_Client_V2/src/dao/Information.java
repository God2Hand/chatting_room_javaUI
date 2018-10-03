/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * 这个类是版本2的精华所在
 * 好吧其实是为了传送图片和文件才产生的。
 * 本来可以用更有逻辑的方法结构实现这个类，但是为了照顾版本1，只好呵呵了
*/

package dao;

import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * 这是文本、图片、文件消息的集合,是发送的消息。
 * mess里面标记了是群聊还是单聊
 * 另外有isXxx()标记消息类型
 * @author Shinelon
 */
public class Information implements Serializable{
    private static final long serialVersionUID = 5000;
    
//    private boolean isMess = false;
    private String mess = null;//这是识别群聊单聊登录退出等等的标志，也就是文本信息类型标志，同时它又是文本消息.
    //就叫做消息头吧~
//    public boolean isMess() {
//        return isMess;
//    }
//    public void setIsMess(boolean isMess) {
//        this.isMess = isMess;
//    }
    public String getMess() {
        return mess;
    }
    public void setMess(String mess) {
        this.mess = mess;
    }

    boolean isImag = false;
    ImageIcon imag = null;//这是图片消息
    public boolean isImag() {
        return isImag;
    }
    public void setIsImag(boolean isImag) {
        this.isImag = isImag;
    }
    public ImageIcon getImag() {
        return imag;
    }
    public void setImag(ImageIcon imag) {
        this.imag = imag;
    }

    boolean isFile = false;
    String file = null;//这是文件消息，这里要考虑下用另一个线程来实现。
    public boolean isFile() {
        return isFile;
    }
    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    
}
