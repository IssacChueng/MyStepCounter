package com.issac.mystepcounter.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

import com.issac.mystepcounter.BuildConfig;
import com.issac.mystepcounter.R;


/**
 * Created by zhans on 2016/12/14.
 */

public class LoginLayout extends ViewGroup implements View.OnClickListener{
    private static String Tag = "Tag";

    private ImageButton btnNext;
    private ImageButton btnPre;
    private CompleteListener completeListener;

    private int desireWidth;
    private int desireHeight;

    private int topMargin;
    private int leftMargin;
    private int rightMargin;
    private int bottomMargin;
    private int btnWidth;
    private int btnHeight;
    private static final int btnPreTag = 1;
    private static final int btnNextTag = 2;

    private int currentIndex=2;//现在显示的view;
    private int duration = 300;
    private int degree=90;
    //第一个构造方法
    public LoginLayout(Context context) {
        this(context, null);
    }
    //第二个构造方法
    public LoginLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //第三个构造方法
    public LoginLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }


    //第四个构造方法
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoginLayout(Context context, AttributeSet attrs,
                       int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);

        btnPre = new ImageButton(context);
        btnPre.setId(View.NO_ID);
        btnPre.setTag(btnPreTag);
        btnPre.setPadding(10,0,10,0);
        btnPre.setImageResource(R.mipmap.pre);
        btnPre.setBackgroundColor(Color.WHITE);
        btnNext = new ImageButton(context);
        btnNext.setId(View.NO_ID);
        btnNext.setTag(btnNextTag);
        btnNext.setImageResource(R.mipmap.next);
        btnNext.setBackgroundColor(Color.WHITE);
        btnNext.setPadding(10,0,10,0);
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnPre.setLayoutParams(lp);
        btnNext.setLayoutParams(lp);
        addView(btnPre);
        addView(btnNext);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);

    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray ta = c.obtainStyledAttributes(attrs,
                    R.styleable.SlideGroup);

            gravity = ta.getInt(R.styleable.SlideGroup_layout_gravity, -1);
            Log.e(Tag,"gravity:"+gravity);

            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            this(width, height, -1);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.e(Tag,"onMeasure");
        desireWidth = 0;
        desireHeight = 0;
        int count = getChildCount();

        for (int i=0;i<count;i++){
            View view = getChildAt(i);
            if (view.getVisibility() != GONE){
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                measureChildWithMargins(view,widthMeasureSpec,0,heightMeasureSpec,0);
                if (i==0){
                    btnWidth = view.getMeasuredWidth()+view.getPaddingLeft()+view.getPaddingRight();
                    btnHeight = view.getMeasuredHeight();
                    desireWidth = 2*btnWidth;
                    desireHeight = btnHeight;
                    Log.e(Tag, "btnHeight:" + btnHeight+"btnWidth:"+btnWidth);
                }
                    if (i > 1) {
                        desireHeight = Math.max(desireHeight, view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                        lp.height = desireHeight;
                        if (widthMode == MeasureSpec.AT_MOST) {
                            Log.e(Tag, "view.getMeasuredWidth()" + view.getMeasuredWidth());

                            desireWidth = Math.max(desireWidth, view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + 2 * btnWidth);

                        }else if (widthMode == MeasureSpec.EXACTLY){
                            desireWidth = widthSize;
                        }
                        view.setLayoutParams(lp);
                        topMargin = Math.max(topMargin, lp.topMargin);
                        leftMargin = Math.max(leftMargin, lp.leftMargin);
                        rightMargin = Math.max(rightMargin, lp.rightMargin);
                        bottomMargin = Math.max(bottomMargin, lp.bottomMargin);
                    }
            }

            //padding
            desireHeight += getPaddingTop()+getPaddingBottom();
            desireWidth += getPaddingLeft()+getPaddingRight();

            //big enough?
            desireHeight = Math.max(desireHeight,getSuggestedMinimumHeight());
            desireWidth = Math.max(desireWidth,getSuggestedMinimumWidth());

            setMeasuredDimension(resolveSize(desireWidth,widthMeasureSpec),resolveSize(desireHeight,heightMeasureSpec));

        }
        if (count>2){
            setBtnElevation();
        }
        LayoutParams lp = (LayoutParams) btnPre.getLayoutParams();
        lp.topMargin = topMargin;
        lp.leftMargin = leftMargin;
        lp.rightMargin = 0;
        lp.bottomMargin = bottomMargin;
        lp.height = desireHeight;
        btnPre.setLayoutParams(lp);
        lp.leftMargin = 0;
        lp.rightMargin = rightMargin;
        btnNext.setLayoutParams(lp);



    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setBtnElevation(){
        View view = getChildAt(2);
        btnPre.setElevation(view.getElevation());
        btnPre.setTranslationZ(view.getTranslationZ());
        btnNext.setElevation(view.getElevation());
        btnNext.setTranslationZ(view.getTranslationZ());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(Tag,"onLayout");
        final int parentLeft = getPaddingLeft();
        final int parentRight = r-l-getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b-t-getPaddingBottom();

        if (BuildConfig.DEBUG){
            Log.d("Tag","parentleft: " + parentLeft + "   parenttop: "
                    + parentTop + "   parentright: " + parentRight
                    + "   parentbottom: " + parentBottom+"btnWidth"+btnWidth);
        }

        int left = parentLeft, top=parentTop;

        int count = getChildCount();
        int childWidth = desireWidth-2*btnWidth;
        int childHeight = desireHeight;
        if(count>0)
        //btnPre.layout(left+leftMargin,top+topMargin,left+leftMargin+desireHeight,top+topMargin+desireHeight);
        top = parentTop+topMargin;
        left = parentLeft+leftMargin;
        View vfirst = getChildAt(0);
        vfirst.layout(left,top,left+btnWidth,top+btnHeight);
        for (int i=2;i<count;i++){
            View v = getChildAt(i);
            if (i==2){
                v.setVisibility(VISIBLE);
            }
            if (v.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                final int gravity = lp.gravity;
                final int horizontalGravity = gravity
                        & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity
                        & Gravity.VERTICAL_GRAVITY_MASK;
                left =parentLeft+leftMargin+btnWidth;
                top = parentTop + topMargin;
                if (gravity != -1){
                    switch (horizontalGravity){
                        case Gravity.CENTER_HORIZONTAL:
                            int paddingHorizontal = (childWidth-v.getMeasuredWidth())/2;
                            v.setPadding(paddingHorizontal,v.getPaddingTop(),paddingHorizontal,v.getPaddingBottom());
                            break;
                    }
                    switch (verticalGravity) {
                        case Gravity.TOP:
                            break;
                        case Gravity.CENTER_VERTICAL:

                            top = parentTop
                                    + (parentBottom - parentTop - getChildAt(i).getMeasuredHeight())
                                    / 2 + topMargin - bottomMargin;
                            break;
                        case Gravity.BOTTOM:
                            top = parentBottom - childHeight - bottomMargin;
                            break;

                    }
                }
                Log.e(Tag,btnPre.getMeasuredWidth()
                        +"desireWidth:"+desireWidth
                        +"childWidth:"+childWidth
                        +"desireHeight:"+desireHeight
                        +"count=:"+count
                        +"textHeight="+getChildAt(i).getMeasuredHeight());
                v.layout(left, top, left + childWidth, top + childHeight);
                if (i>1 && i != currentIndex){
                    v.setVisibility(INVISIBLE);
                    Log.e(Tag,"v.setVisibility(INVISIBLE);");
                }
            }
        }
        top = parentTop+topMargin;
        left = desireWidth-rightMargin-btnWidth;
        View vLast = getChildAt(1);
        vLast.layout(left,top,left+btnWidth,top+btnHeight);
        /*if(count>0)
        btnNext.layout(parentLeft+desireWidth-rightMargin-desireHeight,top,parentLeft+desireWidth-rightMargin,top+desireHeight);*/


    }


    @Override
    public void onClick(View v) {
        Integer btnTag = (Integer) v.getTag();
        switch (btnTag){
            case btnPreTag:
                if (currentIndex>2) {
                    View v2 = getChildAt(currentIndex-1);
                    v2.setVisibility(VISIBLE);
                    rotateContent(false,v2);
                    currentIndex--;
                    if (currentIndex == getChildCount()-2){
                        btnNext.setImageResource(R.mipmap.next);
                    }
                }
                break;
            case btnNextTag:
                if (currentIndex<getChildCount()-1) {
                    View v2 = getChildAt(currentIndex+1);
                    v2.setVisibility(VISIBLE);
                    rotateContent(true,v2);
                    currentIndex++;
                    if (currentIndex == getChildCount()-1){
                        btnNext.setImageResource(R.mipmap.complete);
                    }
                }else{
                    if (completeListener!= null){
                        completeListener.doComplete(this);
                    }
                }
                break;
        }
    }



    private void rotateContent(boolean reverse, final View v2) {
        final View v1 = getChildAt(currentIndex);
        int degree1, degree2;
        final ObjectAnimator a,b,c,d;

        if (reverse){     //往下
            degree1 = 90;
            degree2 = -degree1;
            v1.setPivotY(v1.getMeasuredHeight());
            v2.setPivotY(0);
            c= ObjectAnimator.ofFloat(v1,"translationY",0,-v1.getMeasuredHeight());
            d = ObjectAnimator.ofFloat(v2,"translationY",v2.getMeasuredHeight(),0);

        }else{      //往上
            degree1 = -90;
            degree2 = -degree1;
            v1.setPivotY(0);
            v2.setPivotY(v2.getMeasuredHeight());
            c= ObjectAnimator.ofFloat(v1,"translationY",0,v1.getMeasuredHeight());
            d = ObjectAnimator.ofFloat(v2,"translationY",-v2.getMeasuredHeight(),0);
        }
        a = ObjectAnimator.ofFloat(v1, "rotationX", 0, degree1);
        b = ObjectAnimator.ofFloat(v2, "rotationX", degree2, 0);


        a.setInterpolator(new LinearInterpolator());
        b.setInterpolator(new LinearInterpolator());
        a.setDuration(duration);
        b.setDuration(duration);

        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        /*b.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(Tag,"onAnimationStart");
                //v2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.INVISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.play(a).with(c);
        set.play(b).with(d);
        set.start();
    }

    public void addCompleteListener(CompleteListener listener){
        this.completeListener = listener;
    }
    public CompleteListener getCompleteListener(){
        if (completeListener == null){
            return null;
        }
        return completeListener;
    }

    public interface CompleteListener{
        void doComplete(View view);
    }
}
