package com.issac.mystepcounter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.issac.mystepcounter.R;


/**
 * Created by Administrator on 2016/9/13.
 */
public class ChangeColorWithIconView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    /**
     * 颜色
     */
    private int mColor = 0x14B9D6;
    private int mTextColor;
    private int normalColor;
    private int changeColor;
    /**
     * 透明度 0.0-1.0
     */
    private float mAlpha = 0f;
    /**
     * 图标
     */
    private Bitmap mIconBitmap;
    private String mText;
    /**
     * 限制绘制icon的范围
     */
    private Rect mIconRect;
    private int left;
    private int top;
    private Paint mTextPaint;
    private int mTextHeight;
    private int baseY;

    public ChangeColorWithIconView(Context context) {
        super(context);
    }

    public ChangeColorWithIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取设置的图标
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChangeColorIconView);

        int n = a.getIndexCount();
        normalColor = ContextCompat.getColor(context, R.color.avatarBorder);
        changeColor = ContextCompat.getColor(context,R.color.tabStrip);
        mTextColor = normalColor;
        for (int i = 0; i < n; i++)
        {

            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.ChangeColorIconView_micon:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconView_mcolor:
                    mColor = a.getColor(attr, 0x14B9D6);
                    break;
                case R.styleable.ChangeColorIconView_m_text:
                    mText = a.getString(attr);
                    break;
            }
        }

        a.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(40);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.SERIF);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        mTextHeight = fontMetrics.bottom-fontMetrics.top;
        Log.i("tag","mTextHeight="+mTextHeight+";MeasuredWidth="+getMeasuredWidth());
        // 得到绘制icon的宽
        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom()-mTextHeight );

        left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        top = getPaddingTop();
        // 设置icon的绘制范围
        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        int alpha = (int) Math.ceil((255 * mAlpha));
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        setupTargetBitmap(alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);

        baseY = top+mIconRect.height()+mTextHeight;
        mTextPaint.setColor(mTextColor);
        canvas.drawText(mText,getMeasuredWidth()/2,baseY,mTextPaint);
    }

    private void setupTargetBitmap(int alpha)
    {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), mIconRect.height(),
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(0x14B9D6);
        Log.i("Main",mColor+"");
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }









    public void setIconAlpha(float alpha)
    {
        this.mAlpha = alpha;
        if(alpha==1){
            this.mTextColor = changeColor;
        }else{
            this.mTextColor = normalColor;
        }
        invalidateView();
    }

    private void invalidateView()
    {
        if (Looper.getMainLooper() == Looper.myLooper())
        {
            invalidate();
        } else
        {
            postInvalidate();
        }
    }

    public void setIconColor(int color)
    {
        mColor = color;
    }

    public void setIcon(int resId)
    {
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (mIconRect != null)
            invalidateView();
    }

    public void setIcon(Bitmap iconBitmap)
    {
        this.mIconBitmap = iconBitmap;
        if (mIconRect != null)
            invalidateView();
    }

    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else
        {
            super.onRestoreInstanceState(state);
        }

    }

}

