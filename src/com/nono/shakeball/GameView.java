package com.nono.shakeball;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
/**
 * 
 * @author nono
 *
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable ,SensorEventListener{
	private static String TAG = "LILITH";
	private Context mContext;
	private SurfaceHolder holder;
	
	
	private float accelerationX,accelerationY,accelerationZ;//加速度值
	
	private Paint paint;//画笔
	private Canvas canvas;//画布

	private int screenW,screenH;
	
	private SensorManager sensorManager;
	
	private Thread thread;
	private boolean flag;
	
	private Ball mBall;
	private Bitmap ballBitmap;

	private AudioManager audioManager;//音频服务管理
	private SoundPool pool;//声音池
	private int soundID;//声音池中某个音频id，因为我这边只使用一个，如果有多个，则可以定义一个map来存储key和id，方便下次读取
	 private int currentVol;//当前音量
	public GameView(Context context) {
		super(context);
		this.mContext = context;
	
		this.setKeepScreenOn(true);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		
		paint = new Paint();//实例化画笔
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		
		this.holder = this.getHolder();//获取控制器
		this.holder.addCallback(this);//添加监听
		
		 sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		 audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		 
		 //创建一个可以同时管理2音频的池；
		pool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundID = pool.load(mContext, R.raw.collision, 100);
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(flag){
			 long start = System.currentTimeMillis();  
			 draw();
			 logic();
			 long end = System.currentTimeMillis();  
//	            try {  
//	                if (end - start < 20) {  
//	                    Thread.sleep(50 - (end - start));  
//	                }  
//	            } catch (InterruptedException e) {  
//	                e.printStackTrace();  
//	            }  
		}
	}

	//绘图方法
	private void draw() {
		try {
			canvas = this.holder.lockCanvas();// 获取当前画布
			canvas.drawColor(Color.WHITE);// 清屏
		//	canvas.drawBitmap(ballBitmap, 30, 30,paint); Ticket类绘制！
			mBall.draw(canvas, paint);
		} catch (Exception e) {

		} finally {
			try {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}

	//逻辑代码
	private void logic() {
		
		currentVol = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);//获取当前手机音频声音
		//边界碰撞检测
		if(mBall.isCollision(0.0f, 0.0f, screenW, screenH)){
			pool.play(soundID, currentVol, currentVol, 1, 0, 1);
		}
		mBall.setDirectionVector(new NonoVector(accelerationX,
				accelerationY));
		mBall.go(0.0f, 0.0f, screenW, screenH);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		 Log.i(TAG, "surfaceChanged");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		screenW = getWidth();
		screenH = getHeight();
		registerListener();
		Log.i(TAG, "screenW:" + screenW + "screenH:" + screenH);

		ballBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_ball);
		pool.load(mContext, R.raw.collision,1);
		
		mBall = new Ball(screenW / 2, screenH / 2,ballBitmap);
		mBall.setActivityArea(new Area(0.0f, 0.0f, screenW, screenH));
		flag = true;
		thread = new Thread(this);
		thread.start();
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "surfaceDestroyed");
		 unRegisterListener();
		 flag = false;
	}
	
	
	
    public boolean registerListener(){
    	if(sensorManager!=null){
    		Sensor oSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//方向传感器
    		Sensor aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器;
    		if(oSensor == null || aSensor == null){
    			Toast.makeText(mContext, "设备不支持", 1000).show();
    			return false;
    		}
    		//sensorManager.registerListener(this, oSensor,SensorManager.SENSOR_DELAY_NORMAL);
    		sensorManager.registerListener(this, aSensor,SensorManager.SENSOR_DELAY_NORMAL);
    		return true;
    	}
    	return false;
    }
    
    /**
	 * 反注册传感器
	 */
	public void unRegisterListener() {
		if (sensorManager != null)
			sensorManager.unregisterListener(this);
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	private long preTime;
	private long lastTime;
	private float lastX,lastY,lastZ;
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		//主要是为了清除爆发模式时我们人为因素增大的加速度
		accelerationX = 0;
		accelerationY = 0;
		//方法一：爆发式的，即晃动频率达到一个级别后，调用。
		
		long curTime = java.lang.System.currentTimeMillis();
		if ((curTime - lastTime) > 10) {
			long diffTime = (curTime - lastTime);
			lastTime = curTime;
			float x = event.values[0];
			float	y = event.values[1];
			float z = event.values[2];
			float speed = Math.abs(x + y + z - lastX - lastY - lastZ)
					/ diffTime * 10000;
			Log.i(TAG, "speed是：：："+speed);
			if (speed > 400d) {
				//剧烈晃动，进入疯狂模式～～～
				accelerationX = x*10;
				accelerationY = y*10;
				preTime = curTime;
				//}
			}
//			else if(speed < 50d){
//				accelerationX = 0;
//				accelerationX = 0;
//			}
//			else{//此代码是基本时刻赋予了物体一个加速度，在开启碰撞音效下慎用～～
//				accelerationX = x * 0.5f;
//				accelerationY = y *0.5f;
//			}
			lastX = x;
			lastY = y;
			lastZ = z;
		}
		
		//方法二：适合随时根据手机上传感放下变化
		
		//long curTime = java.lang.System.currentTimeMillis();
		//long preTime = 0;
		//if (curTime - preTime > 10) {
		//	long diffTime = (curTime - preTime);
/*		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			// float z = event.values[2];
			Log.i(TAG, "加速度传感器X == :" + x + "   Y == :" + y);
			// preTime = curTime;
			accelerationX = x;
			accelerationY = y;
			// }
		}*/
			
			// if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			// float x = event.values[0];
			// float y = event.values[1];
			// float z = event.values[2];
			//
			// Log.i(TAG, "方向传感器X == :"+x
			// +"   Y == :"+y
			// +"   Z == :"+z);
			// }
		

	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		if(event.getAction() == MotionEvent.ACTION_DOWN){
//			new AlertDialog.Builder(mContext)
//			        /**设置标题**/
//			         .setTitle("重要")
//		         /**设置icon**/
//			        .setIcon(android.R.drawable.alert_dark_frame)
//			        /**设置内容**/
//			        .setMessage("你确定要关闭吗？")
//			        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
//			
//			           public void onClick(DialogInterface dialog, int which) {
//			               // TODO Auto-generated method stub
//			                
//			            }}).setPositiveButton("确定", new DialogInterface.OnClickListener(){
//			
//			                public void onClick(DialogInterface dialog, int which) {
//			                   /**关闭窗口**/
//			                 
//		                     
//			                 }}).show();
//			        
//		}
		
		return super.onTouchEvent(event);
	}
}
