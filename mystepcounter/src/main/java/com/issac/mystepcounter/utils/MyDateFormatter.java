package com.issac.mystepcounter.utils;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;
import com.issac.mystepcounter.AppContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyDateFormatter implements AxisValueFormatter {
    private FormattedStringCache.Generic<Long, Date> mFormattedStringCache =
            new FormattedStringCache.Generic<>(new SimpleDateFormat("E"));
    private static Calendar calendar = Calendar.getInstance();

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Log.i(AppContext.Tag,value+"");
        calendar.add(Calendar.DATE, (int) value+1);
        long v = calendar.getTimeInMillis();
        calendar.setTime(new Date());
        return mFormattedStringCache.getFormattedValue(new Date(v), v);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
