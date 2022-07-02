package com.CGG.tank;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @ClassName TankFrame  //类名称
 * @Description: 类描述
 * @Author: 程哥哥  //作者
 * @CreateDate: 2022/7/2 17:28	//创建时间
 * @UpdateUser: 更新人
 * @UpdateDate: 2022/7/2 17:28	//更新时间
 * @UpdateRemark: 更新的信息
 * @Version: 1.0  //版本号
 */

public class TankFrame extends Frame {
  //窗口长宽
  static final int GAME_WIDTH = 960, GAME_HEIGHT = 800;

  public TankFrame() {
    setSize(GAME_WIDTH, GAME_HEIGHT);
    setResizable(false);
    setTitle("tank war");
    this.setVisible(true);

    //this.addKeyListener(new MyKeyListener());

    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) { // bjmashibing/tank
        System.exit(0);
      }

    });
  }
}
