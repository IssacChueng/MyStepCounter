package com.issac.mystepcounter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.issac.mystepcounter.R;
import com.issac.mystepcounter.annotation.ColorInt;
import com.issac.mystepcounter.annotation.FloatRange;


/**
 * Created by Administrator on 2016/9/14.
 */
public class PieView extends View {
    private RelativeLayout baseLayout;
    private String mPercentageText = "";
    private String mCenterBtnText = "";
    private Button mBtnStart;
    private int mPercentageSize;
    private int mInnerCirclePadding;
    private Paint mPercentageFill;
    private Paint mBackgroundFill;

    private Paint mCenterFill;
    private Paint mCenterBtn;
    private Paint mTextPaint;
    private Paint mBtnTextPaint;
    private RectF mRect;
    private RectF mRectCent;
    private Rect mTextBounds;
    private Rect mBtnTextBounds;
    private int mTextWidth, mTextHeight;
    private int mBtnTextWidth,mBtnTextHeight;

    private float mPercentage = 0;
    private float mCurrentPercentage = 0;
    private int percentPercent=1;

    public PieView(Context context) {
        super(context);
        Log.i("Main","1");

    }

    public PieView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        Log.i("Main","2");
        init();
    }

    public PieView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupWidgetWithParams(context, attrs, defStyle);
        init();
    }

    private void setupWidgetWithParams(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieView,
                0, 0);
        try {
            this.mPercentage = a.getFloat(R.styleable.PieView_percentage, 0) / 100;
            this.mPercentageSize = a.getInteger(R.styleable.PieView_percentage_size, 0);
            this.mInnerCirclePadding = a.getInteger(R.styleable.PieView_inner_pie_padding, 0);
            this.mPercentageText=a.getString(R.styleable.PieView_inner_text);
            this.mCenterBtnText = a.getString(R.styleable.PieView_center_btn_text);
        } finally {
            a.recycle();
        }
        /*if (mPercentageTextView.getText().toString().trim().equals("")) {
            int roundedPercentage = (int) (mPercentage * 100);
            mPercentageTextView.setText(Integer.toString(roundedPercentage) + "%");
        }
        mPercentageTextView.setTextSize(mPercentageSize);
        baseLayout.addView(mPercentageTextView);*/
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(getContext(),R.color.percentageTextColor));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mBtnTextPaint = new Paint();
        mBtnTextPaint.setColor(ContextCompat.getColor(getContext(),R.color.percentageTextColor));
        mBtnTextPaint.setAntiAlias(true);
        mBtnTextPaint.setTypeface(Typeface.DEFAULT_BOLD);


        mPercentageFill = new Paint();
        mPercentageFill.setColor(ContextCompat.getColor(getContext(),R.color.percentageFillColor));
        mPercentageFill.setAntiAlias(true);
        mPercentageFill.setStyle(Paint.Style.FILL);
        mBackgroundFill = new Paint();
        mBackgroundFill.setColor(ContextCompat.getColor(getContext(),R.color.percentageUnfilledColor));
        mBackgroundFill.setAntiAlias(true);
        mBackgroundFill.setStyle(Paint.Style.STROKE);

        mCenterBtn = new Paint();
        mCenterBtn.setColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        mCenterBtn.setAntiAlias(true);
        mCenterBtn.setStyle(Paint.Style.FILL);

        mCenterFill = new Paint();
        mCenterFill.setColor(ContextCompat.getColor(getContext(),R.color.percentageTextBackground));
        mCenterFill.setAntiAlias(true);
        mCenterFill.setStyle(Paint.Style.FILL);



        mRect = new RectF();
        mRectCent = new RectF();
        mTextBounds = new Rect();
        mBtnTextBounds = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        /*int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        Log.i("Main",widthSize+"");
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
            Log.i("Main","width:"+width+"");
        } else
        {
            width= getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            height = getWidth();
        }*/

        mTextPaint.setTextSize(mPercentageSize);
        mTextWidth = (int)mTextPaint.measureText(mPercentageText);

        mTextPaint.getTextBounds(mPercentageText,0,mPercentageText.length(),mTextBounds);
        mTextHeight = mTextBounds.height();
        Log.i("Main","textHeight"+mTextHeight);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBtnTextPaint.setTextSize(mPercentageSize);
        mBtnTextWidth = (int) mBtnTextPaint.measureText(mCenterBtnText);
        mBtnTextPaint.getTextBounds(mCenterBtnText,0,mCenterBtnText.length(),mBtnTextBounds);
        mBtnTextHeight = mBtnTextBounds.height();
        Log.i("Main","btntextheight"+mBtnTextHeight);
        mBtnTextPaint.setTextAlign(Paint.Align.CENTER);
        //setMeasuredDimension(width, height);


        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
Log.i("Main","Width====="+size);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = (int) getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;

    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int screenWidthPx = dm.widthPixels;// 屏幕宽度（像素）
            int screenHeightPx= dm.heightPixels; // 屏幕高度（像素）
            float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
            int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
            //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
            int screenWidth = (int) (screenWidthPx/density);//屏幕宽度(dp)
            Log.i("123", screenWidth + "======" + screenHeightPx+"+++++++++++++"+density);
            result = (int)(screenWidthPx-80*density*2);//根据自己的需要更改
            Log.i("123",result+"");
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


       /* WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidthPx = dm.widthPixels;// 屏幕宽度（像素）
        int screenHeightPx= dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (screenWidthPx/density);//屏幕宽度(dp)
        int screenHeight = (int)(screenHeightPx/density);//屏幕高度(dp)
        Log.e("123", screenWidthPx + "======" + screenHeightPx+"+++++++++++++"+mInnerCirclePadding);
        */

        int left = 0;
        int width = getMeasuredWidth();
        Log.i("Main","onDraw:"+width);
        int height = width;
        int top = 0;

        mRect.set(left, top, left + width, top + height);
        mRectCent.set(left + mInnerCirclePadding, top + mInnerCirclePadding, (left - mInnerCirclePadding) + width,
                (top - mInnerCirclePadding) + height);

        canvas.drawArc(mRect, -90, 360, true, mBackgroundFill);

        if (mPercentage != 0) {
            canvas.drawArc(mRect, -90, (360 * mCurrentPercentage), true, mPercentageFill);
            canvas.drawArc(mRectCent, 150, 240, false, mCenterFill);
            canvas.drawArc(mRectCent,30,120,false,mCenterBtn);

        }

        canvas.drawText(mPercentageText,width/2,(height)*3/8+mTextHeight/2+mInnerCirclePadding,mTextPaint);//上部文字
        canvas.drawText(mCenterBtnText,width/2,(height)*7/8+mBtnTextHeight/2-mInnerCirclePadding,mBtnTextPaint);//btn文字
//        baseLayout.draw(canvas);
    }





    /**
     * Determine the thickness of the mPercentage pie bar
     *
     * @param padding value ranging from 1 to the width of the widget
     */
    public void setPieInnerPadding(int padding) {
        this.mInnerCirclePadding = padding;
        invalidate();
    }

    /**
     * Set a mPercentage between 0 and 100
     *
     * @param mPercentage any float value from 0 to 100
     */
    public void setmPercentage(@FloatRange(from = 0, to = 100) float mPercentage) {
        this.mPercentage = mPercentage / 100;
        int roundedPercentage = (int) mPercentage;
        this.mPercentageText=Integer.toString(roundedPercentage) + "%";
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.i("Main","X==========="+x);
        Log.i("Main","Y==========="+y);
        Log.i("Main","width==========="+getWidth());
        int r = getWidth()/2-mInnerCirclePadding;
        int distanceHeight = (int) Math.pow((y-getWidth()/2),2);
        Log.i("Main","distanceHeight++++++++++"+distanceHeight+"\n"+r);
        String start = getContext().getString(R.string.btn_start);
        String pause = getContext().getString(R.string.btn_pause);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:


                if (Math.pow((x-getWidth()/2),2)<Math.pow(r,2)*3/4){
                    Log.i("Main","true");
                    if (Math.pow(r,2)/4<=distanceHeight && distanceHeight <= r*r-Math.pow((x-getWidth()/2),2)){
                        mCenterBtn.setColor(ContextCompat.getColor(getContext(),R.color.colorBtnDown));
                        invalidate();
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.i("Main","XUP=================="+x);
                Log.i("Main","YUP=================="+y);
                if (Math.pow((x-getWidth()/2),2)<Math.pow(r,2)*3/4){
                    if (Math.pow(r,2)/4<=distanceHeight && distanceHeight <= r*r-Math.pow((x-getWidth()/2),2)){
                        mCenterBtn.setColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                        if (mCenterBtnText.equals(start)){
                            mCenterBtnText = pause;
                        }else{
                            mCenterBtnText =start;
                        }
                        invalidate();
                        return true;
                    }
                }
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * Determine the background color of the center of the widget where the label is shown
     *
     * @param color The new color (including alpha) to set in the paint.
     */
    public void setInnerBackgroundColor(@ColorInt int color) {
        mCenterFill.setColor(color);
    }

    /**
     * Determine the background color of the bar representing the mPercentage set to the widget
     *
     * @param color The new color (including alpha) to set in the paint.
     */
    public void setPercentageBackgroundColor(@ColorInt int color) {
        mPercentageFill.setColor(color);
    }

    /**
     * Determine the background color of the back of the widget
     *
     * @param color The new color (including alpha) to set in the paint.
     */
    public void setMainBackgroundColor(@ColorInt int color) {
        mBackgroundFill.setColor(color);
    }

    public void startCircleAnimation(){
        handler.postDelayed(new CircleAnimation(),5L);
    }

    private Handler handler = new Handler();
    class CircleAnimation implements Runnable{
        @Override
        public void run() {
            if (mCurrentPercentage >= mPercentage){
                mCurrentPercentage = mPercentage;
                invalidate();
                handler.removeCallbacks(this);
            }else{
                mCurrentPercentage = percentPercent*(mPercentage/100);
                percentPercent++;
                handler.postDelayed(this,5L);
                invalidate();
            }
        }
    }
}



