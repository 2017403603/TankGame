package com.CGG.tank.decorator;

import java.awt.Graphics;

import com.CGG.tank.GameObject;

public abstract class GODecorator extends GameObject {
	
	GameObject go;
	
	public GODecorator(GameObject go) {
		
		this.go = go;
	}

	@Override
	public abstract void paint(Graphics g);

}
