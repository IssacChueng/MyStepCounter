package com.issac.mystepcounter.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.Toast;

import com.issac.mystepcounter.pojo.StepValues;
import com.issac.mystepcounter.utils.DbUtils;
import com.issac.mystepcounter.utils.RingBuffer;

/**
 * Created by Administrator on 2016/10/10.
 */

public class StepCounter implements SensorEventListener {
    //波峰检测窗口
    static RingBuffer<Float> tempValues = new RingBuffer<>();
    //当前小时内步数
    public static int stepCountInHour=0;
    //当前步数
    public static int currentStep = 0;
    //过滤前6步，这集步数据不稳定
    public static int tempStep=0;
    //
    private static int stopStep = 17;
    //开始计步
    static boolean flag=false;
    //pause
    static boolean pauseFlag = true;

    Context context;
    public StepCounter(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                float[] values = event.values;

                //求平方和
                float value = pow(values);
                tempValues.add(value);
        /*StepTemp stepTemp = new StepTemp();
        stepTemp.setSteps(value+"");
        Log.i("main","is liteOrm == null"+(DbUtils.liteOrm==null));
        DbUtils.insert(stepTemp);
        stepCountInHour++;
        tempValues.add(value);
        Intent intent = new Intent("stepcount");
        intent.putExtra("stepCount",(int)value);
        context.sendBroadcast(intent);
        Log.i("Tag",value+"");*/
                //检测波峰，如果检测到波峰且可以计步，currentStep++;
                if (tempValues.hasPeak() && !pauseFlag) {
                    if (!flag) {
                        tempStep++;
                        if (tempStep >4) {
                            flag = true;
                        }
                    } else {
                        currentStep++;
                        stepCountInHour++;
                        Toast.makeText(context, currentStep + "", Toast.LENGTH_SHORT).show();
                        Log.i("Tag", currentStep + "");
                    }
                }
            }
    }


    private float pow(float[] values){
        float result = 0f;
        for (float value: values) {
            Log.i("value",value+"");
            result =result+
                    (float)Math.pow(value,2);
        }
        return result;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
