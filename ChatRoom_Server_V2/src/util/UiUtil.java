/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 这是对窗口属性进行设置的工具类
 * @author Shinelon
 */
public class UiUtil {

    private UiUtil() {
    }
    
     //修改窗体的图标               
    public static void setFrameImage(JFrame jf, String imageName) {
        //获取工具对象
        Toolkit tk = Toolkit.getDefaultToolkit();

        //根据路径获取图片
        Image i = tk.getImage("src\\resource\\" + imageName);

        //给窗体设置图片
        jf.setIconImage(i);
    }

    //设置窗体居中
    public static void setFrameCenter(JFrame jf) {
        /*
         思路：
         A:获取屏幕的宽和高
         B:获取窗体的宽和高
         C:（A-B）/2
         */
        //获取工具类对象
        Toolkit tk = Toolkit.getDefaultToolkit();
        
        //获取屏幕的宽和高
        Dimension d = tk.getScreenSize();
        double ScreenWidth = d.getWidth();
        double ScreenHeight = d.getHeight();

        //获取窗体的宽和高
        int FrameWidth = jf.getWidth();
        int FrameHeight = jf.getHeight();

        //计算（A-B）/2
        int width = (int) (ScreenWidth - FrameWidth) / 2;
        int height = (int) (ScreenHeight - FrameHeight) / 2;

        //设置窗体坐标
        jf.setLocation(width, height);
    }
    
    //把图形界面外观设置成所使用的平台的外观
    public static void beautiful() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (UnsupportedLookAndFeelException e) {
		e.printStackTrace();
        }
    }
}
