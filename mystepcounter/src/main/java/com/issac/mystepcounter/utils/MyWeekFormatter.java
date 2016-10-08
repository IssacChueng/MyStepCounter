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
public class MyWeekFormatter implements AxisValueFormatter {

    private FormattedStringCache.Generic<Long,Date> mFormattedStringCache =
            new FormattedStringCache.Generic<>(new SimpleDateFormat("MM.dd"));
    private Calendar cal = Calendar.getInstance();

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_WEEK,(int)(value-6)*7);
        return mFormattedStringCache.getFormattedValue(cal.getTime(),(long)value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
