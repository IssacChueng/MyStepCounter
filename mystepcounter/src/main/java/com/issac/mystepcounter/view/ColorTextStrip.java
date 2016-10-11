package com.issac.mystepcounter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.issac.mystepcounter.R;


/**
 * Created by Administrator on 2016/10/7.
 */

public class ColorTextStrip extends View {
    private int mTextStartX;
    public enum Direction{
        LEFT,RIGHT;
    }
    private int mDirection = DIRECTION_LEFT;
    private static final int DIRECTION_LEFT=0;
    private static final int DIRECTION_RIGHT=1;

    public void setDirection(int direction){
        mDirection = direction;
    }

    private String mText = "zsw";
    private Paint mPaint;
    private Paint linePaint;
    private int mTextSize = sp2px(getContext(),30f);

    private int mTextNormalColor = 0xff000000;
    private int mTextChangeColor = 0x14B9D6;

    private Rect mTextBound = new Rect();
    private Rect lineBound = new Rect();
    private int mTextWidth;

    private int mRealWidth;

    private float mProgress;



    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public ColorTextStrip(Context context) {
        super(context,null);
    }

    public ColorTextStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorTextStrip);
        mText = ta.getString(R.styleable.ColorTextStrip_time_text);
        mTextSize = ta.getDimensionPixelSize(R.styleable.ColorTextStrip_time_text_size,mTextSize);
        mTextNormalColor = ta.getColor(R.styleable.ColorTextStrip_text_normal_color,mTextNormalColor);
        mTextChangeColor = ta.getColor(R.styleable.ColorTextStrip_text_change_color,mTextChangeColor);
        mProgress = ta.getFloat(R.styleable.ColorTextStrip_progress,0);
        mDirection = ta.getInt(R.styleable.ColorTextStrip_direction,mDirection);
        ta.recycle();
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextNormalColor);
        linePaint.setColor(mTextChangeColor);
        linePaint.setAlpha(0);
        linePaint.setStrokeWidth(10f);
        measureText();
    }

    private void measureText() {
        mTextWidth = (int) mPaint.measureText(mText);
        mPaint.getTextBounds(mText,0,mText.length(),mTextBound);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mTextStartX = mRealWidth/2 - mTextWidth/2;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int val = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        switch(mode){
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
                case MeasureSpec.UNSPECIFIED:
                    result = mTextBound.height();
                    break;
        }
        result = mode == MeasureSpec.AT_MOST? Math.min(result,val) : result;

        return result + getPaddingTop()+getPaddingBottom();
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int val = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        switch(mode){
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
                case MeasureSpec.UNSPECIFIED:
                    result = mTextWidth;
                    break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result,val) : result;
        return result + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int r = (int)(mProgress* mTextWidth + mTextStartX);
//        if (mDirection == DIRECTION_LEFT){
//            drawChangeLeft(canvas,r);
//            drawNormalLeft(canvas,r);
//        }else{
//            drawNormalRight(canvas,r);
//            drawChangeRight(canvas,r);
//        }
        drawText(canvas,mTextStartX+mTextWidth);

    }

    private void drawText(Canvas canvas, int endX) {
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(0,0,endX,getMeasuredHeight()+10);
        canvas.drawText(mText,mTextStartX,getMeasuredHeight()/2+mTextBound.height()/2,mPaint);
        canvas.drawLine(mTextStartX,getMeasuredHeight(),endX,getMeasuredHeight(),linePaint);
        canvas.restore();
    }

    public void drawColor(int alpha) {
        mPaint.setColor(mTextChangeColor);
        linePaint.setAlpha(alpha);
        invalidate();

    }
    public void resetColor(){
        mPaint.setColor(mTextNormalColor);
        linePaint.setAlpha(0);
        invalidate();

    }

    private void drawChangeRight(Canvas canvas, int r) {
        drawText(canvas,mTextChangeColor,(int)(mTextStartX+(1-mProgress)*mTextWidth),mTextStartX+mTextWidth);
    }

    private void drawText(Canvas canvas, int mTextChangeColor, int startX, int endX) {
        mPaint.setColor(mTextChangeColor);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX,0,endX,getMeasuredHeight());
        canvas.drawText(mText,mTextStartX,getMeasuredHeight()/2+mTextBound.height()/2,mPaint);
        canvas.restore();
    }


    private void drawNormalRight(Canvas canvas, int r) {
        drawText(canvas,mTextNormalColor,mTextStartX, (int) (mTextStartX+(1-mProgress)*mTextWidth));
    }

    private void drawNormalLeft(Canvas canvas, int r) {
        drawText(canvas,mTextNormalColor,(int) (mTextStartX+mProgress*mTextWidth),mTextStartX+mTextWidth);
    }

    private void drawChangeLeft(Canvas canvas, int r) {
        drawText(canvas,mTextChangeColor,mTextStartX, (int) (mTextStartX+mProgress*mTextWidth));
    }

    public void setmProgress(float mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }
}
