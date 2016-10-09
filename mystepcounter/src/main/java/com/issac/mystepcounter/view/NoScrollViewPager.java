package com.issac.mystepcounter.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/10/9.
 */

public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;


    public NoScrollViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public NoScrollViewPager(Context context){
        super(context);
    }

    public void setNoScroll(boolean noScroll){
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /*if(noScroll){
            return false;
        }else{
            return super.onTouchEvent(ev);
        }*/
//        return super.onTouchEvent(ev);
        return false;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*if(noScroll){
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }*/
//        return super.onInterceptTouchEvent(ev);
        return false;
    }

}
