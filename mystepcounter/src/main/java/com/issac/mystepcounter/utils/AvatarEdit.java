package com.issac.mystepcounter.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.issac.mystepcounter.AppContext;
import com.issac.mystepcounter.UserEdit;
import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.view.CircleImageView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/12/5.
 */

public class AvatarEdit {
    private static final int ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK=3;
    public static <T extends AppCompatActivity> void setAvatar(final T context, final User user, final CircleImageView avatarView, File file){
        try
        {
            //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picdata.getData()));
            //mAvatar.setImageBitmap(bitmap);
            final BmobFile bmobFile = new BmobFile(file);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e== null){
                        user.setAvatar(bmobFile);
                        Log.i("changed",bmobFile.getFileUrl());
                        final BmobFile avatarFile = new BmobFile("xxx.jpg","",bmobFile.getFileUrl());
                        AppContext.avatarFileName = File.separator+avatarFile.getFilename();
                        File downDir = new File(context.getFilesDir(),AppContext.avatarFileName);
                        avatarFile.download(downDir,new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    Log.i("changed","s="+s);
                                    avatarView.setImageBitmap(AppContext.getBitmapByUrl(s));
                                }else{
                                    //Toast.makeText(context,"e="+e.getErrorCode()+":"+e.getMessage(),Toast.LENGTH_LONG).show();
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
            ex.printStackTrace();
        }

    }

    public static <T extends AppCompatActivity> void clipPhoto(T context, Uri uri, int resultCode, File file){
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
        if(resultCode==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        context.startActivityForResult(intent, CUT_OK);
    }
}
