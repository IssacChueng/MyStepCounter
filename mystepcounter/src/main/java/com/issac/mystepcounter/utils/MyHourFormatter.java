package com.issac.mystepcounter.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Created by Administrator on 2016/9/19.
 */
public class MyHourFormatter implements AxisValueFormatter {

    public MyHourFormatter() {
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int hour = (int)value*6;
        return String.valueOf(hour)+":00";
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
