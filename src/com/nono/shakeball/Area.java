package com.nono.shakeball;

/**
 * 活动空间
 * @author nono
 *
 */
public class Area {
	 float x,y;//原点坐标
	 float w,h;//宽高
	 public float getW() {
		return w;
	}
	public void setW(float w) {
		this.w = w;
	}
	public float getH() {
		return h;
	}
	public void setH(float h) {
		this.h = h;
	}
	public Area(float x, float y ,float w, float h){
		 this.x = x;
		 this.y = y;
		 this.w = w;
		 this.h = h;
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
	 

}
