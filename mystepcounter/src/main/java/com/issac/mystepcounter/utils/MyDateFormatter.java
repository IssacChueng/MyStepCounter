package com.issac.mystepcounter.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyDateFormatter implements AxisValueFormatter {
    private FormattedStringCache.Generic<Long, Date> mFormattedStringCache = new FormattedStringCache.Generic<>(new SimpleDateFormat("E"));

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Long v = ((long) value+4)*3600000L*24L;
        return mFormattedStringCache.getFormattedValue(new Date(v), v);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
