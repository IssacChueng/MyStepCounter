package com.issac.mystepcounter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.issac.mystepcounter.pojo.User;
import com.issac.mystepcounter.utils.*;
import com.issac.mystepcounter.utils.AvatarEdit;
import com.issac.mystepcounter.view.CircleImageView;
import com.issac.mystepcounter.view.MyPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class Login extends AppCompatActivity implements ViewPager.OnPageChangeListener,TextView.OnEditorActionListener{
    private LinearLayout layout_login;
    private TabLayout tabs;
    private ViewPager pagers;
    private List<View> views;
    private List<String> tabTitles;
    private Button btnLogin;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextView passwordForget;
    private Button btnReg;
    private TextInputEditText regUsername;
    private TextInputEditText regPassword;
    private TextInputEditText regPasswordRepeat;
    private TextInputEditText regEmail;
    private Button regRegBtn;
    private CircleImageView regAvatar;
    private MyPopupWindow window;
    private File file;

    private View.OnClickListener onClickListener;
    private View.OnFocusChangeListener onFocusChangeListener;

    private boolean repeat;
    private final int ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK=3;

    private static User newUser = new User();

    private Intent comeIntent;
    private boolean changed = false;
    private InputMethodManager inputMethodManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        repeat = false;
        onClickListener = new MyClickListener();
        onFocusChangeListener = new MyOnFocusChangeListener();
        inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        initWidget();
        initData();


    }



    private void initWidget() {
        layout_login = (LinearLayout) findViewById(R.id.activity_login);
        LayoutInflater inflater = LayoutInflater.from(this);
        View login = inflater.inflate(R.layout.login,null);
        View register = inflater.inflate(R.layout.register,null);
        views = new ArrayList<>();
        views.add(login);
        views.add(register);

        tabs = (TabLayout) findViewById(R.id.login_tabs);
        //tabs.getTabAt(1).select();
        pagers = (ViewPager) findViewById(R.id.login_viewpager);

        username = (TextInputEditText) login.findViewById(R.id.login_username);
        password = (TextInputEditText) login.findViewById(R.id.login_password);
        btnLogin = (Button) login.findViewById(R.id.login_btn);

        passwordForget = (TextView) login.findViewById(R.id.password_forget);
        btnReg = (Button) login.findViewById(R.id.login_register_btn);

        regUsername = (TextInputEditText) register.findViewById(R.id.register_username);
        regPassword = (TextInputEditText) register.findViewById(R.id.register_password_1);
        regPasswordRepeat = (TextInputEditText) register.findViewById(R.id.register_password_2);
        regEmail = (TextInputEditText) register.findViewById(R.id.register_email);
        regAvatar = (CircleImageView) register.findViewById(R.id.user_register_avatar);
        regRegBtn = (Button) register.findViewById(R.id.register_register_btn);


    }

    private void initData() {
        comeIntent = getIntent();
        file = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
        file.delete();
        tabTitles = new ArrayList<>();
        tabTitles.add("登陆");
        tabTitles.add("注册");
        tabs.addTab(tabs.newTab().setText(tabTitles.get(0)));
        tabs.addTab(tabs.newTab().setText(tabTitles.get(1)));
        MyPagerAdapter adapter = new MyPagerAdapter();
        pagers.setAdapter(adapter);
        pagers.addOnPageChangeListener(this);
        tabs.setupWithViewPager(pagers);
        username.setOnEditorActionListener(this);
        btnLogin.setOnClickListener(onClickListener);
        passwordForget.setOnClickListener(onClickListener);
        btnReg.setOnClickListener(onClickListener);

        regAvatar.setOnClickListener(onClickListener);
        regRegBtn.setOnClickListener(onClickListener);
        regPasswordRepeat.setOnFocusChangeListener(onFocusChangeListener);

        username.requestFocus();
        username.setCursorVisible(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {

                           public void run()
                           {
                               inputMethodManager.showSoftInput(username, 0);
                           }

                       },
                998);

        initNewUser();

    }

    private void initNewUser() {
        newUser.set(null,70+"",170+"",AppContext.MALE,"xxx@xxx.xxx",0);
    }

    private void initNewUserAvatar() {
        final BmobFile file = new BmobFile(AppContext.file,null,AppContext.FILEURL);
        newUser.setAvatar(file);
        AppContext.avatarFileName = File.separator+file.getFilename();
        File dir = new File(getFilesDir(),AppContext.avatarFileName);
        file.download(dir, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    regAvatar.setImageBitmap(AppContext.getBitmapByUrl(s));
                    newUser.setAvatar(file);
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALBUM_OK:
                if (data != null) {
                   // clipPhoto(data.getData(), ALBUM_OK);
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
                    AvatarEdit.setAvatar(this,newUser,regAvatar,file);
                    changed = true;
                } else {
                    Log.e("Main", "data为空");
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();

        }
        return path;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i(AppContext.Tag,"select");
        if (position == 0){
            username.requestFocus();
            username.setCursorVisible(true);
            inputMethodManager.showSoftInput(username, 0);
        }else{
            regUsername.requestFocus();
            regUsername.setCursorVisible(true);
            inputMethodManager.showSoftInput(regUsername, 0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT){
            switch (v.getId()){
                case R.id.login_username:
                    Log.e(AppContext.Tag,v.getId()+":"+R.id.login_username);
                    v.clearFocus();
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    password.requestFocus();
                    password.setCursorVisible(true);
                    break;
            }
        }
        return false;
    }

    class MyPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));

            return views.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }


    }

    private boolean isInputValid(EditText regUsername, EditText regPassword, EditText regEmail){
        return (AppContext.isEmailValid(regEmail.getText().toString()) &&
                AppContext.isInputEmpty(regUsername.getText().toString()) &&
                AppContext.isInputEmpty(regPassword.getText().toString()));
    }

    class MyClickListener  implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    User user = new User();
                    //Toast.makeText(getApplicationContext(),user.getAvatar().getFilename(),Toast.LENGTH_LONG).show();
                    user.setUsername(username.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.login(new SaveListener<User>() {
                        @Override
                        public void done(User user1, BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), AppContext.avatarFileName, Toast.LENGTH_SHORT).show();
                                BmobFile file = BmobUser.getCurrentUser(User.class).getAvatar();
                                AppContext.avatarFileName = File.separator+file.getFilename();
                                AppContext.putString(AppContext.AVATAR,AppContext.avatarFileName);
                                Toast.makeText(getApplicationContext(), AppContext.avatarFileName, Toast.LENGTH_SHORT).show();
                                File dir = new File(getFilesDir(),AppContext.avatarFileName);
                                file.download(dir, new DownloadFileListener() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        setResult(RESULT_OK,comeIntent);
                                        AppContext.HASUSER = true;
                                        finish();
                                    }

                                    @Override
                                    public void onProgress(Integer integer, long l) {

                                    }
                                });

                            }else{
                                Toast.makeText(getApplicationContext(),e.getErrorCode()+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                case R.id.password_forget:
                    break;
                case R.id.login_register_btn:
                    tabs.getTabAt(1).select();
                    regUsername.requestFocus();
                    regUsername.setCursorVisible(true);
                    break;
                case R.id.register_register_btn:
                    if (isInputValid(regUsername,regPassword,regEmail) && repeat){
                        newUser.setUsername(regUsername.getText().toString());
                        newUser.setPassword(regPassword.getText().toString());
                        newUser.setEmail(regEmail.getText().toString());
                        if (!changed){
                            initNewUserAvatar();
                        }
                        newUser.signUp(new SaveListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (e == null){
                                    Toast.makeText(Login.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK,comeIntent);
                                    AppContext.putString(AppContext.AVATAR,AppContext.avatarFileName);
                                    AppContext.HASUSER = true;
                                    finish();
                                }else{
                                    Toast.makeText(Login.this,"注册失败，请检查输入，并稍后重试",Toast.LENGTH_SHORT).show();
                                    Log.e("regError",e.getErrorCode()+":"+e.getMessage());
                                }
                            }
                        });
                    }else{
                        Toast.makeText(Login.this,"输入有误，请检查输入",Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.user_register_avatar:
                    window = new MyPopupWindow(Login.this,this,MyPopupWindow.TYPE_AVATAR);
                    window.setAnimationStyle(R.style.PopupAnimation);
                    window.showAtLocation(layout_login, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
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
                default:
                    break;
            }
        }
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.register_password_2:
                    if (hasFocus){
                    }else{
                        String password1 = regPassword.getText().toString();
                        if(!password1.equals(regPasswordRepeat.getText().toString())){
                            regPasswordRepeat.setError("两次密码不一致");
                        }else{
                            repeat = true;
                        }
                    }
                    break;
                case R.id.register_username:
                    if (hasFocus){
                    }else{
                        if (regUsername.getText().toString().equals("")){
                            regUsername.setError("用户名不能为空");
                        }
                    }
                    break;
                case R.id.register_email:
                    if (!hasFocus && regEmail.getText().toString().equals("")){
                        regEmail.setError("请填写邮箱");
                    }

            }

        }
    }



}
