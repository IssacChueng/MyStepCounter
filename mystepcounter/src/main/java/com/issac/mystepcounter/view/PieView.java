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
    private String mPercentageText = "你已坚持了";
    private String mSecondText="步";//中间步数
    private String mCenterBtnText = "开始";
    private Button mBtnStart;
    private int mPercentageSize;        //步数字体大小
    private int mInnerCirclePadding;
    private Paint mPercentageFill;
    private Paint mBackgroundFill;

    //分段
    private float mSectionRatio = 5.0f;
    private float mRingRadius = 0.15f;
    private RectF mSectionRect;
    private Paint mSectionPaint;

    //text
    private Paint mTextPaint;
    private Paint mBtnTextPaint;
    private Paint mCenterFill;
    private Paint mCenterBtn;

    private RectF mRectCent;
    private Rect mTextBounds;
    private Rect mBtnTextBounds;
    private int mTextWidth, mTextHeight;
    private int mBtnTextWidth,mBtnTextHeight;

    private float mPercentage = 0;  //步数占比，总数5000
    private float mCurrentPercentage = 0;
    private int percentPercent=1;
    private float mCenterX;
    private float mCenterY;
    private float mRadius;

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
        mBackgroundFill.setColor(ContextCompat.getColor(getContext(),R.color.percentageBackColor));
        mBackgroundFill.setAntiAlias(true);
        mBackgroundFill.setStyle(Paint.Style.FILL);

        mCenterBtn = new Paint();
        mCenterBtn.setColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        mCenterBtn.setAntiAlias(true);
        mCenterBtn.setStyle(Paint.Style.FILL);

        mCenterFill = new Paint();
        mCenterFill.setColor(ContextCompat.getColor(getContext(),R.color.percentageTextBackground));
        mCenterFill.setAntiAlias(true);
        mCenterFill.setStyle(Paint.Style.FILL);

        mSectionPaint = new Paint();
        mSectionPaint.setAntiAlias(true);
        mSectionPaint.setStyle(Paint.Style.FILL);


        mRectCent = new RectF();
        mTextBounds = new Rect();
        mBtnTextBounds = new Rect();
        mSectionRect = new RectF();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > height)
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        else
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        setRectDimensions(getWidth(),getHeight());
        setPaintDimensions();

    }

    private void setPaintDimensions() {
        mTextPaint.setTextSize(this.getResources().getDimension(R.dimen.textSize));
        mTextWidth = (int)mTextPaint.measureText(mPercentageText);

        mTextPaint.getTextBounds(mPercentageText,0,mPercentageText.length(),mTextBounds);
        mTextHeight = mTextBounds.height();
        Log.i("Main","textHeight"+mTextHeight);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBtnTextPaint.setTextSize(this.getResources().getDimension(R.dimen.textSize));
        mBtnTextWidth = (int) mBtnTextPaint.measureText(mCenterBtnText);
        mBtnTextPaint.getTextBounds(mCenterBtnText,0,mCenterBtnText.length(),mBtnTextBounds);
        mBtnTextHeight = mBtnTextBounds.height();
        Log.i("Main","btntextheight"+mBtnTextHeight);
        mBtnTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    private void setRectDimensions(int width, int height) {
        mCenterX = width / 2.0f;
        mCenterY = height / 2.0f;

        // Find shortest dimension
        int diameter = Math.min(width, height);

        float outerRadius = diameter / 2;
        float sectionHeight = mInnerCirclePadding;
        float sectionWidth = sectionHeight / mSectionRatio;

        mRadius = outerRadius - sectionHeight / 2;
        mSectionRect.set(-sectionWidth / 2, -sectionHeight / 2, sectionWidth / 2, sectionHeight / 2);
        mRectCent.set(-mRadius,-mRadius,mRadius,mRadius);
        //mSectionHeight = sectionHeight;

    }



    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(mCenterX, mCenterY);

        float rotation = 360.0f / (float) 100;
        for (int i = 0; i < 100; ++i) {
            canvas.save();

            canvas.rotate((float) i * rotation);
            canvas.translate(0, -mRadius);

            if (i < mCurrentPercentage*100) {
                mSectionPaint.setColor(ContextCompat.getColor(getContext(),R.color.percentageFillColor));
            } else {
                canvas.scale(0.7f, 0.7f);
                mSectionPaint.setColor(ContextCompat.getColor(getContext(),R.color.percentageBackColor));
            }

            canvas.drawRect(mSectionRect, mSectionPaint);
            canvas.restore();


        }

        canvas.drawArc(mRectCent, 150, 240f, false, mCenterFill);
        canvas.drawArc(mRectCent,30,120f,false,mCenterBtn);
        canvas.drawText(mPercentageText,0f,-mRadius*1/3,mTextPaint);
        canvas.drawText(mSecondText,0f,mTextHeight/2,mTextPaint);
        canvas.drawText(mCenterBtnText,0f,mRadius*3/4+mBtnTextHeight*1/4,mBtnTextPaint);
        super.onDraw(canvas);


//        int left = 0;
//        int width = getMeasuredWidth();
//        Log.i("Main","onDraw:"+width);
//        int height = width;
//        int top = 0;
//
//        mRectCent.set(left + mInnerCirclePadding, top + mInnerCirclePadding, (left - mInnerCirclePadding) + width,
//                (top - mInnerCirclePadding) + height);
//
//
//
//            canvas.drawArc(mRectCent, 150, 240f, false, mCenterFill);
//            canvas.drawArc(mRectCent,30,120f,false,mCenterBtn);
//
//
//
//        canvas.drawText(mPercentageText,width/2,(height)*3/8+mTextHeight/2+mInnerCirclePadding,mTextPaint);//上部文字
//        canvas.drawText(mCenterBtnText,width/2,(height)*7/8+mBtnTextHeight/2-mInnerCirclePadding,mBtnTextPaint);//btn文字
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
        invalidate();
    }

    public void setmSecondText(int stepCount){
        this.mSecondText = stepCount+getContext().getString(R.string.step);
        this.mCurrentPercentage = (float)stepCount/5000;
        this.mPercentage = stepCount/50;
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



