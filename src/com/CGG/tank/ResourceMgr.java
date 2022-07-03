package com.CGG.tank;


import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

//图片资源加载类
public class ResourceMgr {
	//我方坦克上下左右方向坦克图片
	public static BufferedImage goodTankL, goodTankU, goodTankR, goodTankD;
	//敌方坦克上下左右方向坦克图片
	public static BufferedImage badTankL, badTankU, badTankR, badTankD;
	//子弹上下左右图片
	public static BufferedImage bulletL, bulletU, bulletR, bulletD; 
	//爆炸图片特效数组
	public static BufferedImage[] explodes = new BufferedImage[16];
	
 	
	static {
		try {
			goodTankU = ImageIO.read(ResourceMgr.class.getClassLoader().getResourceAsStream("images/GoodTank1.png"));
			goodTankL = ImageUtil.rotateImage(goodTankU, -90);
			goodTankR = ImageUtil.rotateImage(goodTankU, 90);
			goodTankD = ImageUtil.rotateImage(goodTankU, 180);
			
			badTankU = ImageIO.read(ResourceMgr.class.getClassLoader().getResourceAsStream("images/BadTank1.png"));
			badTankL = ImageUtil.rotateImage(badTankU, -90);
			badTankR = ImageUtil.rotateImage(badTankU, 90);
			badTankD = ImageUtil.rotateImage(badTankU, 180);
			
			bulletU = ImageIO.read(ResourceMgr.class.getClassLoader().getResourceAsStream("images/bulletU.png"));
			bulletL = ImageUtil.rotateImage(bulletU, -90);
			bulletR = ImageUtil.rotateImage(bulletU, 90);
			bulletD = ImageUtil.rotateImage(bulletU, 180);
			//加载爆炸图片
			for(int i=0; i<16; i++) 
				explodes[i] = ImageIO.read(ResourceMgr.class.getClassLoader().getResourceAsStream("images/e" + (i+1) + ".gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
