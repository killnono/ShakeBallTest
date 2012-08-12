package com.nono.shakeball;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShakeBallTestActivity extends Activity implements SensorEventListener,OnClickListener{
    /** Called when the activity is first created. */
	
	private static String TAG = "LILITH";
	private Context mContext;
	private SensorManager sensorManager;
	
	private Button startButton,stopButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        
        //setContentView(R.layout.main);
        setContentView(new GameView(mContext));
//        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
//        startButton = (Button) findViewById(R.id.button1);
//        stopButton = (Button) findViewById(R.id.button2);
//        startButton.setOnClickListener(this);
//        stopButton.setOnClickListener(this);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	//registerListener();
    }
    public boolean registerListener(){
    	if(sensorManager!=null){
    		Sensor oSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//方向传感器
    		Sensor aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器;
    		if(oSensor == null || aSensor == null){
    			Toast.makeText(mContext, "设备不支持", 1000).show();
    			return false;
    		}
    		sensorManager.registerListener(this, oSensor,SensorManager.SENSOR_DELAY_NORMAL);
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			Log.i(TAG, "加速度传感器X == :"+x
					+"   Y == :"+y
					+"   Z == :"+z);
		}
//		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
//			float x = event.values[0];
//			float y = event.values[1];
//			float z = event.values[2];
//			
//			Log.i(TAG, "方向传感器X == :"+x
//					+"   Y == :"+y
//					+"   Z == :"+z);
//		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			registerListener();
			break;

		case R.id.button2:
			unRegisterListener();
			break;
		}
	}
}