package com.issac.mystepcounter.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/19.
 */
public class MyMonthFormatter implements AxisValueFormatter {
    private FormattedStringCache.Generic<Long,Date> mFormattedStringCache =
            new FormattedStringCache.Generic<>(new SimpleDateFormat("MM"));
    private Calendar cal=Calendar.getInstance();
    public MyMonthFormatter() {
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        cal.setTime(new Date());
        cal.add(Calendar.MONTH,(int)value-6);
        return mFormattedStringCache.getFormattedValue(cal.getTime(),(long)value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
