package com.issac.mystepcounter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.utils.HttpUtil;
import com.issac.mystepcounter.view.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.socketio.callback.EventCallback;

/**
 * Created by Administrator on 2016/11/23.
 */

public class AppContext extends Application {
    private static final String APPKEY = "bb1702302afc559668ce04af6fdd2bd4";
    public static final String Tag = "tag";
    public static boolean APILEVEL = false;
    public static boolean HASUSER=false;
    private static Resources resources;
    public static String MALE = "m";
    public static String FEMALE = "f";
    public static int REGISTER_OK=1;
    private boolean isFirst = true;
    public static String file="avatar.png";
    public static final String AVATAR="AVATAR";
    public static String avatarFileName;
    public static User user;
    public static SharedPreferences preferences;
    public static final String FILEURL = "http://bmob-cdn-7917.b0.upaiyun.com/2016/12/16/5c35eaafb26240f7b9acd8f8b5a940d5.png";
    public static String SHARE_FILE = "";
    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
        initApp();
        initHttp();
        initBmob();
        initLogin();
    }

    private void initApp() {
        preferences = getSharedPreferences("app",MODE_PRIVATE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SHARE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"share.jpg";
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "ACounter";
            File file = new File(dir);
            if (!file.exists()){
                file.mkdir();
            }
            SHARE_FILE = dir + File.separator + "share.jpg";
            Log.e(Tag,dir);
        }
        if (Build.VERSION.SDK_INT >=21){
            APILEVEL = true;
        }
        if (isFirst) {
            InputStream is = getResources().openRawResource(R.raw.tourist);
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(file, MODE_PRIVATE);
                byte[] in = new byte[2048];
                int count = 0;
                while ((count = is.read(in)) > 0) {
                    fos.write(in, 0, count);
                }
                isFirst = false;
            } catch (FileNotFoundException e) {
                isFirst = true;
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    private void initHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        HttpUtil.setClient(client);
    }

    private void initBmob(){
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId(APPKEY).setConnectTimeout(30)
                .build();
        Bmob.initialize(config);
        BmobFile file1 = new BmobFile(new File(getFilesDir()+File.separator+file));
        file1.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){

                }else{
                    Log.e(Tag,e.getErrorCode()+":"+e.getMessage());
                }
            }
        });
    }

    public void initLogin(){
        user = BmobUser.getCurrentUser(User.class);
        if (user!=null){
            HASUSER = true;
            //avatarFileName = File.separator+user.getAvatar().getFilename();
            avatarFileName = getString(AVATAR);
        }else{
            HASUSER = false;
        }

    }
    
    public void logout(){
        if (HASUSER){
            BmobUser.logOut();
            HASUSER = false;
        }
    }

    public static Bitmap getBitmapById(int id){
        Bitmap result = BitmapFactory.decodeResource(resources,id);
        return result;
    }
    public static Bitmap getBitmapByUrl(String url){
        Bitmap result = BitmapFactory.decodeFile(url);
        return result;
    }

    /*public static void setUserAvatar(final Context context, final CircleImageView avatarView, BmobFile file){
        BmobFile avatar = new BmobFile("xxx","",file.getFileUrl());
        avatar.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Log.i("changed","s="+s);
                    avatarView.setImageBitmap(AppContext.getBitmapByUrl(s));
                }else{
                    Toast.makeText(context,"头像设置错误，请稍后再试",Toast.LENGTH_SHORT);
                    Log.i(Tag,e.getErrorCode()+":"+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }*/

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isInputEmpty(String input){
        return !input.equals("");

    }

    public static void putString(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key){
        return preferences.getString(key,null);
    }


    //截屏方法
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = size.x;
        int height = size.y;
        Log.e(AppContext.Tag,width+","+height);
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 程序入口 截取当前屏幕
    public static void shootLoacleView(Activity a, String picpath) {
        savePic(takeScreenShot(a), picpath);
    }

    public static void shareMsg(Activity a, String activityTitle, String msgTitle, String msgText, String imgPath){
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")){
            intent.setType("text/plain");

        }else{
            File f = new File(imgPath);
            if (f!= null && f.exists() && f.isFile()){
                Log.e(Tag,f.length()+"");
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM,u);
            }
        }
        intent.putExtra(Intent.EXTRA_TEXT,msgText);
        intent.putExtra("Kdescription",msgText);
        intent.putExtra(Intent.EXTRA_HTML_TEXT,msgText);
        /*intent.putExtra(Intent.EXTRA_SUBJECT,msgTitle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        a.startActivity(Intent.createChooser(intent,activityTitle));
    }

    //dp->px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //px->dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



}
