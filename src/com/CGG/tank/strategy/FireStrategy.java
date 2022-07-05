package com.CGG.tank.strategy;

import java.io.Serializable;

import com.CGG.tank.Tank;

public interface FireStrategy extends Serializable {
	void fire(Tank t);
}
