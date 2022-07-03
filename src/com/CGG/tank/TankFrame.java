package com.CGG.tank;

import com.CGG.tank.net.Client;
import com.CGG.tank.net.TankDirChangedMsg;
import com.CGG.tank.net.TankStartMovingMsg;
import com.CGG.tank.net.TankStopMsg;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @ClassName TankFrame  //类名称
 * @Description: 坦克大战桌面类
 * @Author: 程哥哥  //作者
 * @CreateDate: 2022/7/2 17:28	//创建时间
 * @UpdateUser: 更新人
 * @UpdateDate: 2022/7/2 17:28	//更新时间
 * @UpdateRemark: 更新的信息
 * @Version: 1.0  //版本号
 */

public class TankFrame extends Frame {
  //单例模式  只有一个窗口
  public static final TankFrame INSTANCE = new TankFrame();

  Random r = new Random();

  Tank myTank = new Tank(r.nextInt(GAME_WIDTH), r.nextInt(GAME_HEIGHT), Dir.DOWN, Group.GOOD, this);
  //子弹集合
  List<Bullet> bullets = new ArrayList<>();
  Map<UUID,Tank> tanks = new HashMap<>();
  //爆炸集合
  List<Explode> explodes = new ArrayList<>();

  //窗口长宽
  static final int GAME_WIDTH = 960, GAME_HEIGHT = 800;

  public void addBullet(Bullet b) {
    bullets.add(b);
  }

  public void addTank(Tank t) {
    tanks.put(t.getId(), t);
  }

  public Tank findTankByUUID(UUID id) {
    return tanks.get(id);
  }

  public Bullet findBulletByUUID(UUID id) {
    for(int i=0; i<bullets.size(); i++) {
      if(bullets.get(i).getId().equals(id))
        return bullets.get(i);
    }

    return null;
  }

  //初始化窗口
  private TankFrame() {
    setSize(GAME_WIDTH, GAME_HEIGHT);
    setResizable(false);
    setTitle("tank war");
    //setVisible(true);
    //加入键盘按键监听
    this.addKeyListener(new MyKeyListener());
    //加入窗口监听
    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) { // bjmashibing/tank
        System.exit(0);
      }

    });
  }
  //双缓冲解决闪烁问题
  Image offScreenImage = null;

  @Override
  public void update(Graphics g) {
    if (offScreenImage == null) {
      offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
    }
    Graphics gOffScreen = offScreenImage.getGraphics();
    Color c = gOffScreen.getColor();
    gOffScreen.setColor(Color.BLACK);
    gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
    gOffScreen.setColor(c);
    paint(gOffScreen);
    g.drawImage(offScreenImage, 0, 0, null);
  }

  //绘制图像
  @Override
  public void paint(Graphics g) {
    Color c = g.getColor();
    g.setColor(Color.WHITE);
    g.drawString("bullets:" + bullets.size(), 10, 60);
    g.drawString("tanks:" + tanks.size(), 10, 80);
    g.drawString("explodes" + explodes.size(), 10, 100);
    g.setColor(c);

    myTank.paint(g);
    for (int i = 0; i < bullets.size(); i++) {
      bullets.get(i).paint(g);
    }

    //java8 stream api
    tanks.values().stream().forEach((e)->e.paint(g));

    for (int i = 0; i < explodes.size(); i++) {
      explodes.get(i).paint(g);
    }
    //collision detect
    Collection<Tank> values = tanks.values();
    for(int i=0; i<bullets.size(); i++) {
      for(Tank t : values )
        bullets.get(i).collideWith(t);
    }



    // for(Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
    // Bullet b = it.next();
    // if(!b.live) it.remove();
    // }

    // for(Bullet b : bullets) {
    // b.paint(g);
    // }

  }

  //监听键盘类
  class MyKeyListener extends KeyAdapter {

    boolean bL = false;
    boolean bU = false;
    boolean bR = false;
    boolean bD = false;

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      switch (key) {
        case KeyEvent.VK_LEFT:
          bL = true;
          setMainTankDir();
          break;
        case KeyEvent.VK_UP:
          bU = true;
          setMainTankDir();
          break;
        case KeyEvent.VK_RIGHT:
          bR = true;
          setMainTankDir();
          break;
        case KeyEvent.VK_DOWN:
          bD = true;
          setMainTankDir();
          break;

        default:
          break;
      }

      new Thread(()->new Audio("audio/tank_move.wav").play()).start();
    }

    @Override
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();

      switch (key) {
        case KeyEvent.VK_LEFT:
          bL = false;
          setMainTankDir();
          break;
        case KeyEvent.VK_UP:
          bU = false;
          setMainTankDir();
          break;
        case KeyEvent.VK_RIGHT:
          bR = false;
          setMainTankDir();
          break;
        case KeyEvent.VK_DOWN:
          bD = false;
          setMainTankDir();
          break;

        case KeyEvent.VK_CONTROL:
          myTank.fire();
          break;

        default:
          break;
      }

    }
    //设置坦克方向
    private void setMainTankDir() {
      //save the old dir
      Dir dir = myTank.getDir();

      if (!bL && !bU && !bR && !bD) {
        myTank.setMoving(false);
        Client.INSTANCE.send(new TankStopMsg(getMainTank()));
      } else {

        if (bL)
          myTank.setDir(Dir.LEFT);
        if (bU)
          myTank.setDir(Dir.UP);
        if (bR)
          myTank.setDir(Dir.RIGHT);
        if (bD)
          myTank.setDir(Dir.DOWN);
        //发出坦克移动的消息
        if(!myTank.isMoving())
          Client.INSTANCE.send(new TankStartMovingMsg(getMainTank()));

        myTank.setMoving(true);

        if(dir != myTank.getDir()) {
          Client.INSTANCE.send(new TankDirChangedMsg(myTank));
        }
      }


    }
  }

  public Tank getMainTank() {
    return this.myTank;
  }
}
