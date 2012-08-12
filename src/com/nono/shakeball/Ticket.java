package com.nono.shakeball;

import android.graphics.Bitmap;

public class Ticket {
	private float x,y,w,h;
	private Bitmap bitmap;
	public Ticket(int x, int y, Bitmap bitmap){
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
	}
	
	public void draw(int state){
		switch (state) {
		case Constant.TICKET_NORMAL:
			
			break;

		default:
			break;
		}
	}
}
