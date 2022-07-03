package com.CGG.tank;

import org.junit.Test;

/**
 * @ClassName Main  //类名称
 * @Description: 类描述
 * @Author: 程哥哥  //作者
 * @CreateDate: 2022/7/2 17:26	//创建时间
 * @UpdateUser: 更新人
 * @UpdateDate: 2022/7/2 17:26	//更新时间
 * @UpdateRemark: 更新的信息
 * @Version: 1.0  //版本号
 */

public class Main {
  @Test
  public void test(){
    System.out.println("000000");
    new Thread(()->new Audio("audio/tank_fire.wav").play()).start();
  }

  public static void main(String[] args) throws InterruptedException {
    TankFrame tf = TankFrame.INSTANCE;
    tf.setVisible(true);

    //connect to the server


/*		int initTankCount = Integer.parseInt((String)PropertyMgr.get("initTankCount"));

		for(int i=0; i<initTankCount; i++) {
			tf.tanks.add(new Tank(50 + i*80, 200, Dir.DOWN, Group.BAD, tf));
		}*/
    //music
    new Thread(()->new Audio("audio/war1.wav").loop()).start();

    new Thread(()-> {
      while(true) {
        try {
          Thread.sleep(25);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        tf.repaint();
      }
    }).start();

    //or you can new a thread to run this

//    Client.INSTANCE.connect();

  }

}

