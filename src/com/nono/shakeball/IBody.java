package com.nono.shakeball;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 物体接口类
 * @author nono
 *
 */
public interface IBody {
	public void draw(Canvas canvas, Paint paint);
	
	public boolean isCollision();
	
	public void go();
	
	 interface BodyCollisionListener{
		 public void onCollision(IBody body);
	 }
}
