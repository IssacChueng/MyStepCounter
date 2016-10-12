package com.issac.mystepcounter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.issac.mystepcounter.utils.Constant;
import com.issac.mystepcounter.utils.DbUtils;

import java.util.Calendar;

public class StepService extends Service implements SensorEventListener {
    private final String TAG = "StepService";
    private SensorManager sensorManager;
    private StepCounter stepCounter;
    private PowerManager.WakeLock mWakeLock;
    private Messenger messenger = new Messenger(new MessengerHanlder());
    public StepService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DbUtils.createDb(this,true,"android");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startStepCounter();
            }
        }).start();
        //开启计时器
    }

    private void startStepCounter() {
        if(sensorManager != null && stepCounter!= null){
            sensorManager.unregisterListener(stepCounter);
            sensorManager = null;
            stepCounter = null;
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getLock(this);
        addStepCountListener();
    }

    private void addStepCountListener() {
        stepCounter = new StepCounter(this);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(stepCounter,sensor,SensorManager.SENSOR_DELAY_UI);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this,gravitySensor,SensorManager.SENSOR_DELAY_UI);


    }

    synchronized private PowerManager.WakeLock getLock(Context context) {
        //取消睡眠
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            if (event.values[2] > 7) {
                StepCounter.flag = false;
            }else{
                StepCounter.flag = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MessengerHanlder extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.MSG_FROM_CLIENT:
                    //将目前的步数传给前端
                    try{
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null,Constant.MSG_FROM_SERVER);
                        Bundle arg = new Bundle();
                        arg.putInt("step",StepCounter.stepCountInHour);
                        replyMsg.setData(arg);
                        messenger.send(replyMsg);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                case Constant.MSG_PAUSE_STEP:
                    StepCounter.pauseFlag=true;
                    break;
                case Constant.MSG_RESUME_STEP:
                    StepCounter.pauseFlag=false;
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}
