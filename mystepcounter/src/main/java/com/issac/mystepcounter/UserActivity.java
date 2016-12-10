package com.issac.mystepcounter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.issac.mystepcounter.fragment.FragmentUser;
import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.view.CircleImageView;
import com.issac.mystepcounter.view.MyPopupWindow;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;

import static com.issac.mystepcounter.AppContext.HASUSER;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout actUser;
    private CircleImageView avatar;
    private TextView username;
    private ImageView sex;
    private TextView height;
    private TextView weight;
    private TextView email;
    private ImageView back;
    private TextView userStepCount;
    private ImageView edit;
    private User user;
    private Button btnSignout;
    private MyPopupWindow window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initwidget();
        initData();
    }

    private void initData() {
        if (HASUSER){
            User user= BmobUser.getCurrentUser(User.class);
            BmobFile userAvatar = user.getAvatar();
            if (userAvatar != null){
                avatar.setImageBitmap(AppContext.getBitmapByUrl(getFilesDir()+AppContext.avatarFileName));
                Log.i(AppContext.Tag,AppContext.avatarFileName+"");
            }
            if (user.getUsername()!=null){
                username.setText(user.getUsername());
            }
            if (user.getSex() != null){
                if ("m".equals(user.getSex())){
                    sex.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.male));
                }else{
                    sex.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.female));
                }
            }
            if (user.getHeight() !=null){
                height.setText(user.getHeight());
            }
            if (user.getWeight() != null) {
                weight.setText(user.getWeight());
            }
            if (user.getEmail() != null){
                email.setText(user.getEmail());
            }
            if (user.getStepCount() != null){
                String step = getResources().getString(R.string.user_step);
                step = String.format(step,user.getStepCount());
                userStepCount.setText(step);
            }else{
                String step = getResources().getString(R.string.user_step);
                step = String.format(step,0);
                userStepCount.setText(step);
            }
        }
    }

    private void initwidget() {
        actUser = (RelativeLayout) findViewById(R.id.activity_user);
        avatar = (CircleImageView) findViewById(R.id.user_info_avatar);
        username = (TextView) findViewById(R.id.user_info_username);
        sex = (ImageView) findViewById(R.id.user_info_sex);
        height = (TextView) findViewById(R.id.user_info_height);
        weight = (TextView) findViewById(R.id.user_info_weight);
        email = (TextView) findViewById(R.id.user_info_email);
        back = (ImageView) findViewById(R.id.user_info_back);
        edit = (ImageView) findViewById(R.id.user_info_edit);
        userStepCount = (TextView) findViewById(R.id.user_info_step);
        btnSignout = (Button) findViewById(R.id.signout);

        back.setClickable(true);
        back.setOnClickListener(this);
        edit.setClickable(true);
        edit.setOnClickListener(this);
        btnSignout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_info_back:
                finish();
                break;
            case R.id.user_info_edit:
                Intent intent = new Intent(this,UserEdit.class);
                startActivityForResult(intent,0);
                break;
            case R.id.signout:
                window = new MyPopupWindow(this,this,MyPopupWindow.TYPE_CONFIRM);
                window.setAnimationStyle(R.style.PopupAnimation);
                window.showAtLocation(actUser, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
                break;
            case R.id.logout_confirm:
                window.dismiss();
                BmobUser.logOut();
                HASUSER = false;
                setResult(FragmentUser.RESULT_LOGOUT);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            Log.i("resultCode","RESULT_OK");
            user = BmobUser.getCurrentUser(User.class);
            final BmobFile avatarFile = new BmobFile("xxx.jpg","",user.getAvatar().getFileUrl());

            avatarFile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
                        Log.i("changed","s="+s);
                        avatar.setImageBitmap(AppContext.getBitmapByUrl(s));
                    }else{
                        Toast.makeText(UserActivity.this,"e="+e.getErrorCode()+":"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
            sex.setImageBitmap(AppContext.getBitmapById(user.getSex().equals(AppContext.MALE) ? R.mipmap.male : R.mipmap.female));
            username.setText(user.getUsername());
            height.setText(user.getHeight());
            weight.setText(user.getWeight());
            email.setText(user.getEmail());
        }else{

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
