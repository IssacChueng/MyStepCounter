package com.issac.mystepcounter;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.utils.*;
import com.issac.mystepcounter.utils.AvatarEdit;
import com.issac.mystepcounter.view.CircleImageView;
import com.issac.mystepcounter.view.MyPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UserEdit extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout mUserBack;
    private CircleImageView mAvatar;
    private TextView mUsername;
    private TextView mSex;
    private TextView mHeight;
    private TextView mWeight;
    private TextView mEmail;
    private Button mSave;
    private User user;
    private User newUser;
    private String path;

    private final int ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK=3;
    private File file;
    private Intent pickToView;
    private MyPopupWindow window;
    private boolean changed = false;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        initwidget();
        initData();
        intent = getIntent();
    }



    private void initData() {
        if (AppContext.HASUSER){
            file = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
            file.delete();
            initUser();
        }
    }

    private void initUser() {
        user = BmobUser.getCurrentUser(User.class);
        newUser = new User();
        if (user.getUsername()!=null){
            mUsername.setText(user.getUsername());
        }
        if (user.getEmail()!=null){
            mEmail.setText(user.getEmail());
        }
        if (user.getSex()!=null){
            if (AppContext.MALE.equals(user.getSex())){
                mSex.setText("男");
            }else if (AppContext.FEMALE.equals(user.getSex())){
                mSex.setText("女");
            }
        }

        if (user.getAvatar() != null){
            //AppContext.setUserAvatar(this,mAvatar,user.getAvatar());
            mAvatar.setImageBitmap(AppContext.getBitmapByUrl(getFilesDir()+AppContext.avatarFileName));
        }
        if (user.getWeight()!=null){
            mWeight.setText(user.getWeight());
        }
        if (user.getHeight()!=null){
            mHeight.setText(user.getHeight());
        }
    }

    private void initwidget() {
        mUserBack = (RelativeLayout) findViewById(R.id.user_edit_background);
        mAvatar = (CircleImageView) findViewById(R.id.user_edit_avatar);
        mUsername = (TextView) findViewById(R.id.user_edit_username);
        mSex = (TextView) findViewById(R.id.user_edit_sex);
        mHeight = (TextView) findViewById(R.id.user_edit_height);
        mWeight = (TextView) findViewById(R.id.user_edit_weight);
        mEmail = (TextView) findViewById(R.id.user_edit_email);
        mSave = (Button) findViewById(R.id.user_edit_save);

        mSex.setClickable(true);
        mSex.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        mUsername.setOnClickListener(this);
        mHeight.setOnClickListener(this);
        mWeight.setOnClickListener(this);
        mEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_edit_username:
                Log.i("window","username");
                initPopup(MyPopupWindow.TYPE_USERNAME);
                break;
            case R.id.user_edit_sex:
                initPopup(MyPopupWindow.TYPE_SEX);
                break;
            case R.id.user_edit_height:
                initPopup(MyPopupWindow.TYPE_HEIGHT);
                break;
            case R.id.user_edit_weight:
                initPopup(MyPopupWindow.TYPE_WEIGHT);
                break;
            case R.id.user_edit_email:
                initPopup(MyPopupWindow.TYPE_EMAIL);
                break;
            case R.id.user_edit_save:
                saveUser();
                break;
            case R.id.user_edit_avatar:
                initPopup(MyPopupWindow.TYPE_AVATAR);
                break;
            case R.id.pick_pic:
                window.dismiss();
                Intent albumintent = new Intent(Intent.ACTION_PICK,null);
                albumintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(albumintent,ALBUM_OK);
                break;
            case R.id.pick_camera:
                window.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,CAMERA_OK);
                break;
            case R.id.btn_user_save:
                String userEdit = window.getUserEdit();
                setUser(window.getType(),userEdit);
                window.dismiss();
                break;
        }
    }

    private void initPopup(int type){
        window = null;
        window = new MyPopupWindow(this,this,type);
        window.setAnimationStyle(R.style.PopupAnimation);
        window.showAtLocation(mUserBack,Gravity.BOTTOM|Gravity.CENTER_VERTICAL,0,0);
    }

    private void setUser(int type,String userEdit){
        changed = true;
        switch (type){
            case MyPopupWindow.TYPE_USERNAME:
                newUser.setUsername(userEdit);
                break;
            case MyPopupWindow.TYPE_SEX:
                newUser.setSex(userEdit);
                break;
            case MyPopupWindow.TYPE_HEIGHT:
                newUser.setHeight(userEdit);
                break;
            case MyPopupWindow.TYPE_WEIGHT:
                newUser.setWeight(userEdit);
                break;
            case MyPopupWindow.TYPE_EMAIL:
                newUser.setEmail(userEdit);
                break;
        }

    }



    private void saveUser() {

        Log.i("changed","changed="+changed);
        if (changed) {
            newUser.update(user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(UserEdit.this, "更新成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK,intent);
                        AppContext.putString(AppContext.AVATAR,AppContext.avatarFileName);
                        finish();
                    } else {
                        Toast.makeText(UserEdit.this, "更新失败，请稍后重试"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("changed",e.getMessage()+e.getErrorCode());
                        setResult(RESULT_CANCELED,intent);
                    }
                }
            });
        }else{
            finish();
            setResult(RESULT_CANCELED,intent);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    private void getBitmapFromUri(Intent data) {
        setResult(RESULT_OK,data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALBUM_OK:
                if (data != null) {
                    //clipPhoto(data.getData(), ALBUM_OK);
                    com.issac.mystepcounter.utils.AvatarEdit.clipPhoto(this,data.getData(),ALBUM_OK,file);
                }
                break;
            case CAMERA_OK:
                if (file.exists()) {
                    //clipPhoto(Uri.fromFile(file), CAMERA_OK);
                    AvatarEdit.clipPhoto(this,Uri.fromFile(file),CAMERA_OK,file);
                }
                break;
            case CUT_OK:
                if (data != null) {
                    //setPickToView(data);
                    AvatarEdit.setAvatar(this,newUser,mAvatar,file);
                    changed = true;
                } else {
                    Log.e("Main", "data为空");
                    changed=false;
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clipPhoto(Uri uri,int type) {


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent, CUT_OK);
    }

    private String getImagePath(Uri uri, String selection) {

        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();

        }
        return path;
    }

    public void setPickToView(Intent picdata) {
        try
        {
            //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picdata.getData()));
            //mAvatar.setImageBitmap(bitmap);
            final BmobFile bmobFile = new BmobFile(file);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e== null){
                        //newUser.setAvatar(bmobFile);
                        Log.i("changed",bmobFile.getFileUrl());

                        final BmobFile avatarFile = new BmobFile("xxx.jpg","",bmobFile.getFileUrl());
                        newUser.setAvatar(avatarFile);
                        AppContext.avatarFileName = File.separator+avatarFile.getFilename();
                        File downDir = new File(getFilesDir(),AppContext.avatarFileName);
                        avatarFile.download(downDir,new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    Log.i("changed","s="+s);
                                    mAvatar.setImageBitmap(AppContext.getBitmapByUrl(s));
                                    changed = true;
                                }else{
                                    Toast.makeText(UserEdit.this,"e="+e.getErrorCode()+":"+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }else{

                    }
                }
            });


        }catch (Exception ex)
        {
            changed = false;
            ex.printStackTrace();
        }
    }
}
