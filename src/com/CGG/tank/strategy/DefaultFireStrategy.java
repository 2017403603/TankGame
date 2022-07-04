package com.CGG.tank.strategy;

import com.CGG.tank.Audio;
import com.CGG.tank.Bullet;
import com.CGG.tank.Group;
import com.CGG.tank.Tank;

public class DefaultFireStrategy implements FireStrategy {

	@Override
	public void fire(Tank t) {
		int bX = t.x + Tank.WIDTH/2 - Bullet.WIDTH/2;
		int bY = t.y + Tank.HEIGHT/2 - Bullet.HEIGHT/2;
		
		//Bug? new Bullet把自己加了一遍
//		GameModel.getInstance().add(
//				new RectDecorator(
//						new TailDecorator(
//						new Bullet(bX, bY, t.dir, t.group))));
		new Bullet(bX, bY, t.dir, t.group);
		
		if(t.group == Group.GOOD) new Thread(()->new Audio("audio/tank_fire.wav").play()).start();
	}

}
