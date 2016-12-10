package com.issac.mystepcounter.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.issac.mystepcounter.pojo.StepDay;
import com.issac.mystepcounter.pojo.StepHour;
import com.issac.mystepcounter.pojo.StepMonth;
import com.issac.mystepcounter.pojo.StepTemp;
import com.issac.mystepcounter.pojo.StepWeek;
import com.issac.mystepcounter.utils.Constant;
import com.issac.mystepcounter.utils.DbUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StepService extends Service implements SensorEventListener {
    private final String TAG = "StepService";
    private SensorManager sensorManager;
    private StepCounter stepCounter;
    private BroadcastReceiver mBatInfoReceiver;
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
        initDatabase();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startStepCounter();
            }
        }).start();
        //广播

    }

    private void initDatabase() {
        DbUtils.createDb(this,true,"steps");
        StepHour stepHour;

        List<StepHour> list = DbUtils.query(StepHour.class,0L,0L);
        if (list ==null || list.size() ==0) {
            for (int i = 0; i < 25; i++) {
                stepHour = new StepHour(i, 0);
                DbUtils.insert(stepHour);
            }
        }
    }

    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        //日期修改
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        //开机
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        //每分钟一次
        filter.addAction(Intent.ACTION_TIME_TICK);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)){
                    //关机将步数先暂时存储，开机将其取出
                    StepTemp stepTemp = new StepTemp(StepCounter.currentStep);
                    DbUtils.deleteAll(StepTemp.class);
                    DbUtils.insert(stepTemp);
                }else if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
                    if (DbUtils.count(StepTemp.class)>0) {
                        int step = DbUtils.getQueryAll(StepTemp.class).get(0).getSteps();
                        StepCounter.currentStep = step;
                    }
                }else if (Intent.ACTION_DATE_CHANGED.equals(action)){
                    //每天保存一次,并将步数清零
                    Calendar calendar = Calendar.getInstance();
                    long now = calendar.getTimeInMillis();
                    int step = StepCounter.currentStep;
                    StepCounter.currentStep = 0;
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    //StepDay stepDay = new StepDay(now,StepCounter.currentStep);
                    DbUtils.deleteAll(StepHour.class);
                    DbUtils.insert(new StepDay(now,step));

                    //若是周日，保存一次

                    if (Calendar.SUNDAY == day){
                        calendar.add(Calendar.DAY_OF_WEEK,-7);
                        long aWeekAgo = calendar.getTimeInMillis();
                        int steps = DbUtils.sumStep(StepDay.class,aWeekAgo,now);
                        DbUtils.insert(new StepWeek(now,steps));
                        calendar.setTime(new Date());
                    }
                    int date = calendar.get(Calendar.DAY_OF_MONTH);
                    //当月最后一天
                    if (date == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
                        calendar.add(Calendar.MONTH,-1);
                        long aMonthAgo = calendar.getTimeInMillis();
                        int steps = DbUtils.sumStep(StepDay.class,aMonthAgo,now);
                        DbUtils.insert(new StepMonth(now,steps));
                        calendar.setTime(new Date());
                    }
                }else if (Intent.ACTION_TIME_TICK.equals(action)){
                    //每分钟保存，检查是否整点，是则清零
                    Calendar calendar = Calendar.getInstance();
                    int min = calendar.get(Calendar.MINUTE);
                        StepHour stepHour = new StepHour(calendar.get(Calendar.HOUR_OF_DAY),StepCounter.stepCountInHour);
                        DbUtils.update(stepHour);
                    if (min == 0){
                        StepCounter.stepCountInHour = 0;
                    }

                }
            }
        };

        registerReceiver(mBatInfoReceiver,filter);

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

    @Override
    public void onDestroy() {
        DbUtils.closeDb();
        unregisterReceiver(mBatInfoReceiver);

        super.onDestroy();
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
                        arg.putInt("step",StepCounter.currentStep);
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
