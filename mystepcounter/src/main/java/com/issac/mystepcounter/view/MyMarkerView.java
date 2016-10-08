package com.issac.mystepcounter.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.issac.mystepcounter.R;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MyMarkerView extends MarkerView {
    private TextView tvContent;
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.markvalue);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry){
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText(""+ Utils.formatNumber(ce.getX(),0,true)+Utils.formatNumber(ce.getHigh(),0,true));
        }else{
            tvContent.setText(""+Utils.formatNumber(e.getX(),0,true)+","+Utils.formatNumber(e.getY(),0,true));
        }

    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth()/2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
