package com.issac.mystepcounter.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.issac.mystepcounter.AppContext;
import com.issac.mystepcounter.R;

/**
 * Created by Administrator on 2016/12/1.
 */

public class MyPopupWindow extends PopupWindow {
    private View view;
    private Activity mContext;
    private int type=0;
    private LayoutInflater inflater;

    private EditText user;
    private RadioGroup sex;

    public static final int TYPE_AVATAR=0;
    public static final int TYPE_USERNAME=1;
    public static final int TYPE_SEX=2;
    public static final int TYPE_HEIGHT=3;
    public static final int TYPE_WEIGHT=4;
    public static final int TYPE_EMAIL=5;
    public static final int TYPE_CONFIRM = 6;

    public MyPopupWindow(Activity context, View.OnClickListener onClickListener,int type) {
        super(context);
        initWidget(context,onClickListener,type);


    }

    private void initWidget(final Activity context, View.OnClickListener onClickListener,int type) {
        this.type = type;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        switch (type){
            case TYPE_AVATAR:
                initAvatarView(context,onClickListener);
                break;
            case TYPE_CONFIRM:
                initCONFIRMVIEW(context,onClickListener);
                break;
            case TYPE_SEX:
                initUserView(context, onClickListener, type);
                break;
            default:
                initUserView(context, onClickListener, type);
                this.setInputMethodMode(INPUT_METHOD_NEEDED);
                this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                break;
        }
        setParam(view);
    }



    private void backgroundAlpha(Activity context, float v) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = v;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    private void setParam(View view){
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable drawable = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(drawable);
        backgroundAlpha(mContext,0.75f);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(mContext,1f);
            }
        });
    }
    private void initCONFIRMVIEW(final Activity context, final View.OnClickListener onClickListener) {
        view  = inflater.inflate(R.layout.logout_confirm,null);
        Button logout_confirm = (Button) view.findViewById(R.id.logout_confirm);
        Button logout_cancel = (Button) view.findViewById(R.id.logout_cancel);
        logout_confirm.setOnClickListener(onClickListener);
        logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                backgroundAlpha(context,1f);
            }
        });
    }
    private void initAvatarView(final Activity context, View.OnClickListener onClickListener){
        view = inflater.inflate(R.layout.pick_avatar,null);
        Button pick_pic = (Button) view.findViewById(R.id.pick_pic);
        Button pick_camera = (Button) view.findViewById(R.id.pick_camera);
        Button pick_cancel = (Button) view.findViewById(R.id.pick_cancel);
        pick_pic.setOnClickListener(onClickListener);
        pick_camera.setOnClickListener(onClickListener);
        pick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                backgroundAlpha(context,1f);
            }
        });
    }

    private void initUserView(final Activity context, View.OnClickListener onClickListener, final int type){
        view = inflater.inflate(R.layout.user_save,null);
        user = (EditText) view.findViewById(R.id.edit_user);
        sex = (RadioGroup) view.findViewById(R.id.save_sex);
        Button user_save = (Button) view.findViewById(R.id.btn_user_save);
        Button user_cancel = (Button) view.findViewById(R.id.btn_user_cancel);
        user_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                backgroundAlpha(context,1f);
            }
        });
        if (type != TYPE_SEX){
            sex.setVisibility(View.GONE);
        }
        switch (type){
            case TYPE_SEX:
                user.setVisibility(View.GONE);
                sex.check(R.id.save_sex_male);
                break;
            case TYPE_USERNAME:
                user.setHint("请输入用户名");
                user.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TYPE_HEIGHT:
                user.setHint("请输入身高");
                user.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TYPE_WEIGHT:
                user.setHint("请输入体重");
                user.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TYPE_EMAIL:
                user.setHint("请输入邮箱");
                user.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }
        user_save.setOnClickListener(onClickListener);


    }

    public int getType(){
        return type;
    }

    public String getUserEdit(){
        switch (type){
            case TYPE_SEX:
                if (R.id.save_sex_male == sex.getCheckedRadioButtonId()){
                    return AppContext.MALE;
                }else{
                    return AppContext.FEMALE;
                }
            default:
                return user.getText().toString();
        }
    }

}
