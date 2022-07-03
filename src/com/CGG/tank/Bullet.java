package com.CGG.tank;

import com.CGG.tank.net.Client;
import com.CGG.tank.net.TankDieMsg;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.UUID;


//子弹类
public class Bullet {
	//子弹速度
	private static final int SPEED = 6;
	//子弹长宽
	public static int WIDTH = ResourceMgr.bulletD.getWidth();

	public static int HEIGHT = ResourceMgr.bulletD.getHeight();

	private UUID id = UUID.randomUUID();
	private UUID playerId;
	//子弹矩形
	Rectangle rect = new Rectangle();
	//坐标方向
	private int x, y;
	private Dir dir;
	//是否存活
	private boolean living = true;
	//界面引用
	TankFrame tf = null;
	//分组
	private Group group = Group.BAD;

	public Bullet(UUID playerId, int x, int y, Dir dir, Group group, TankFrame tf) {
		this.playerId = playerId;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.group = group;
		this.tf = tf;

		//初始化矩形坐标和长宽
		rect.x = this.x;
		rect.y = this.y;
		rect.width = WIDTH;
		rect.height = HEIGHT;
				
	}
	//子弹碰撞检测
	public void collideWith(Tank tank) {
		if(this.playerId.equals(tank.getId())) return;
		//System.out.println("bullet rect:" + this.rect);
		//System.out.println("tank rect:" + tank.rect);
		if(this.living && tank.isLiving() && this.rect.intersects(tank.rect)) {
			tank.die();
			this.die();
			Client.INSTANCE.send(new TankDieMsg(this.id, tank.getId()));
		}
	}

	public void die() {
		this.living = false;
	}

	public Dir getDir() {
		return dir;
	}
	public Group getGroup() {
		return group;
	}
	public UUID getId() {
		return id;
	}
	
	public UUID getPlayerId() {
		return playerId;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public boolean isLiving() {
		return living;
	}

	//子弹移动
	private void move() {
		
		switch (dir) {
		case LEFT:
			x -= SPEED;
			break;
		case UP:
			y -= SPEED;
			break;
		case RIGHT:
			x += SPEED;
			break;
		case DOWN:
			y += SPEED;
			break;
		}
		
		//update rect
		rect.x = this.x;
		rect.y = this.y;
		
		if(x < 0 || y < 0 || x > TankFrame.GAME_WIDTH || y > TankFrame.GAME_HEIGHT) living = false;
		
	}
	public void paint(Graphics g) {
		if(!living) {
			tf.bullets.remove(this);
		}
		
		switch(dir) {
		case LEFT:
			g.drawImage(ResourceMgr.bulletL, x, y, null);
			break;
		case UP:
			g.drawImage(ResourceMgr.bulletU, x, y, null);
			break;
		case RIGHT:
			g.drawImage(ResourceMgr.bulletR, x, y, null);
			break;
		case DOWN:
			g.drawImage(ResourceMgr.bulletD, x, y, null);
			break;
		}
		
		move();
	}
	
	public void setDir(Dir dir) {
		this.dir = dir;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setLiving(boolean living) {
		this.living = living;
	}
	
	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
