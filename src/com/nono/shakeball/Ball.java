package com.nono.shakeball;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * 自定义小球类
 * 
 * @author nono
 * 
 */

public class Ball implements IBody {
	private static final int COLLISION_X = 0;
	private static final int COLLISION_Y = COLLISION_X+1;
	
	private float x, y, r;// 圆心坐标x、y，直径r；
	private Bitmap bitmap;// 图形
	private NonoVector speed;// 图形速度
	private Area area;
	
	private BodyCollisionListener collisionListener;
	private float stepTime = 30f;
	private float goFast = 2.0f;
	/**
	 * 1.手机站立时：x = 0，01；y = 9.8,实际方向下微微偏左（0.01，9.8）， 2.手机左高右低 x = -9.8; y = 0.01
	 * 因为我们要表现的其实就是每时每刻改变后图形的坐标（x,y）值，根据得出 x-(v0+ax*t)*t）, y+(v0+ay*t)*t）;
	 */
	private NonoVector directionVector;// 物体加速度

	/**
	 * 
	 * @param x 
	 * @param y
	 * @param bitmap
	 */
	public Ball(float x, float y, Bitmap bitmap) {
		this( x,  y, bitmap,new NonoVector(0.0f, 0.0f));
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param bitmap
	 * @param initialvelocity  初速度
	 */
	public Ball(float x, float y, Bitmap bitmap,NonoVector initialvelocity) {
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
		this.r = this.bitmap.getHeight();
		speed = initialvelocity;
		directionVector = new NonoVector(0.0f, 0.0f);
	}
	
	/**
	 * 活动空间
	 * @param area
	 */
	public void setActivityArea(Area area){
		this.area = area;
	}
	
	/**
	 * 设置碰撞声音
	 */
	public void setCollisionVoice(){
		
	}
	/**
	 * 设置碰撞监听
	 * @param listener
	 */
	public void setCollisionListener(BodyCollisionListener listener){
		this.collisionListener = listener;
	}
	
	
	
	/**
	 * 绘图
	 */
	public void draw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(bitmap, x, y, paint);
	}

	/*
	 * 检测碰撞
	 * (non-Javadoc)
	 * @see com.nono.shakeball.IBody#isCollision()
	 */
	@Override
	public boolean isCollision() {
		// TODO Auto-generated method stub

		if (area.y >= this.y || this.y + r >= area.h) {
			Log.i("LILITH", "was Collision");
		//	collisionListener.onCollision(this);
			rebound(COLLISION_X);
			return true;
		} else if (this.x <= x || this.x >= area.w - r) {
			Log.i("LILITH", "was Collision");
			rebound(COLLISION_Y);
			//collisionListener.onCollision(this);
			return true;
		}	
			return false;
		
	}
	
	/**
	 *开始运动
	 */
	@Override
	public void go() {
		// TODO Auto-generated method stub
		
		speed.x += directionVector.x * goFast;
		speed.y += directionVector.y * goFast;

		Log.i("LILITH", "speed.x=" + speed.x + "...speed.y" + speed.y);

		float willX = this.x - speed.x * stepTime / 500;
		float willY = this.y + speed.y * stepTime / 500;

		if (willY < area.y) {
			willY = area.y;
		}
		if (willY > area.h - r) {
			willY = area.h - r;
		}
		if (willX < area.x) {
			willX = area.x;
		}
		if (willX > area.w - r) {
			willX = area.w - r;
		}
		this.x = willX;
		this.y = willY;
		Log.i("LILITH", ".x=" + willX + "....y" + willY);
		isCollision();
	}
	
	public void go(float x, float y, int w, int h) {
		speed.x += directionVector.x * goFast;
		speed.y += directionVector.y * goFast;

		Log.i("LILITH", "speed.x=" + speed.x + "...speed.y" + speed.y);

		float willX = this.x - speed.x * stepTime / 1000;
		float willY = this.y + speed.y * stepTime / 1000;

		if (willY < y) {
			willY = y;
		}
		if (willY > h - r) {
			willY = h - r;
		}
		if (willX < x) {
			willX = x;
		}
		if (willX > w - r) {
			willX = w - r;
		}
		this.x = willX;
		this.y = willY;
		Log.i("LILITH", ".x=" + willX + "....y" + willY);

	}

	public void setDirectionVector(NonoVector directionVector) {
		this.directionVector = directionVector;
	}

	public NonoVector getDirectionVector() {
		return directionVector;
	}


	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	/**
	 * 与边界检测碰撞
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean isCollision(float x, float y, float w, float h) {

		if (y >= this.y || this.y + r >= h) {
//			if(Math.abs(speed.y) > 80f ){
			Log.i("LILITH", "was Collision");
			rebound(COLLISION_X);
			return true;
//			}
		} else if (this.x <= x || this.x >= w - r) {
//			if(Math.abs(speed.x) > 80f){
			Log.i("LILITH", "was Collision");
			rebound(COLLISION_Y);
			return true;
//			}
		} 
		//	Log.i("LILITH", "was Collision");
			return false;
		

	}

	/**
	 * 碰撞后反弹
	 */
	public void rebound(int tag) {

		if (tag == COLLISION_X) {
			speed.x = (speed.x * 0.9f);
			speed.y = -(speed.y * 0.9f);
		} else if (tag == COLLISION_Y) {
			speed.x = -(speed.x * 0.9f);
			speed.y = (speed.y * 0.9f);
		}
	}

	
}
