package com.issac.mystepcounter;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.issac.mystepcounter.fragment.FragmentNews;
import com.issac.mystepcounter.fragment.FragmentHomePage;
import com.issac.mystepcounter.fragment.FragmentStatistics;
import com.issac.mystepcounter.fragment.FragmentUser;
import com.issac.mystepcounter.service.StepService;
import com.issac.mystepcounter.utils.Constant;
import com.issac.mystepcounter.utils.DbUtils;
import com.issac.mystepcounter.utils.PreferenceHelper;
import com.issac.mystepcounter.view.ChangeColorWithIconView;
import com.issac.mystepcounter.view.CircleImageView;
import com.issac.mystepcounter.view.NoScrollViewPager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener, OnChartValueSelectedListener,Handler.Callback
{
    private static final long TIME_INTERVAL = 500l;
    private NoScrollViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ImageView img_share;
    private CircleImageView img_avatar;
    private TextView textTitle;
    private Messenger messenger;
    private Messenger  mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private FragmentHomePage fragmentHomePage;
    private Message UIMessage;
    private Handler UIHandler;
    private Toolbar title;
    public static int steps = 1;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try{
                messenger = new Messenger(service);
                Message msg = Message.obtain(null,Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private List<ChangeColorWithIconView> mTabIndicator = new ArrayList<ChangeColorWithIconView>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestFeature();
        setContentView(R.layout.activity_main);
        setOverflowShowingAlways();
        //getActionBar().setDisplayShowHomeEnabled(false);
        title = (Toolbar) findViewById(R.id.user_info_title);
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        mViewPager.setNoScroll(true);
        mViewPager.setOffscreenPageLimit(4);
        img_share = (ImageView) findViewById(R.id.img_share);
        img_avatar = (CircleImageView) findViewById(R.id.img_avatar);
        textTitle = (TextView) findViewById(R.id.text_title);
        img_share.setOnClickListener(this);
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @TargetApi(21)
    private void requestFeature(){
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setReenterTransition(new Slide());
        getWindow().setEnterTransition(new Slide());
    }


    private void initDatas()
    {
        delayHandler = new Handler(this);

        fragmentHomePage =new FragmentHomePage();
        FragmentNews fGroup = new FragmentNews();
        FragmentStatistics fStatistics = FragmentStatistics.newInstance("","");
        FragmentUser fUser =FragmentUser.newInstance("","");
        mTabs.add(fragmentHomePage);
        mTabs.add(fGroup);
        mTabs.add(fStatistics);
        mTabs.add(fUser);


        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount()
            {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return mTabs.get(arg0);
            }
        };

        initTabIndicator();

        if (AppContext.HASUSER)
        img_avatar.setImageBitmap(AppContext.getBitmapByUrl(getFilesDir()+AppContext.avatarFileName));

    }

    public void setUserImg(){
        img_avatar.setImageBitmap(AppContext.getBitmapByUrl(getFilesDir()+AppContext.avatarFileName));
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    private void initTabIndicator()
    {
        ChangeColorWithIconView one = (ChangeColorWithIconView) findViewById(R.id.homepage);
        ChangeColorWithIconView two = (ChangeColorWithIconView) findViewById(R.id.group);
        ChangeColorWithIconView three = (ChangeColorWithIconView) findViewById(R.id.statistics);
        ChangeColorWithIconView four = (ChangeColorWithIconView) findViewById(R.id.user);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);


        one.setIconAlpha(1.0f);
    }

    @Override
    public void onPageSelected(int position)
    {
        Log.i("Tag","position="+position);
        if (position != 3){
            img_share.setVisibility(View.VISIBLE);
            img_avatar.setVisibility(View.VISIBLE);
            setElevation(5f);
        }else{
            img_share.setVisibility(View.GONE);
            img_avatar.setVisibility(View.GONE);
            resetElevation();
        }
        switch (position){
            case 0:
                textTitle.setText(getString(R.string.title_home));
                break;
            case 1:
                textTitle.setText(getString(R.string.title_group));
                break;
            case 2:
                textTitle.setText(getString(R.string.title_statistics));
                break;
            case 3:
                textTitle.setText(getString(R.string.title_user));
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setElevation(float dp){
        float px = AppContext.dip2px(this,dp);
        title.setElevation(px);
        title.setTranslationZ(px);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void resetElevation(){
        title.setElevation(0f);
        title.setTranslationZ(0f);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels)
    {
        Log.i("Tag", "position = " + position + " , positionOffset = "
                + positionOffset);

        if (positionOffset > 0)
        {
            ChangeColorWithIconView left = mTabIndicator.get(position);
            ChangeColorWithIconView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
            if (position>1){
                img_share.setAlpha(1-positionOffset);
                img_avatar.setAlpha(1-positionOffset);
            }

        }


    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    @Override
    public void onClick(View v)
    {

        resetOtherTabs();

        switch (v.getId())
        {
            case R.id.homepage:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.group:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.statistics:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.user:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
            case R.id.img_share:
                AppContext.shootLoacleView(this,AppContext.SHARE_FILE);

                AppContext.shareMsg(this,"share","dhizhi","msgText",AppContext.SHARE_FILE);
                break;
        }

    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs()
    {
        for (int i = 0; i < mTabIndicator.size(); i++)
        {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try
                {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e)
                {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways()
    {
        try
        {
            // true if a permanent menu key is present, false otherwise.
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        setupService();
        DbUtils.createDb(this,true,"steps");
    }

    @Override
    protected void onResume() {
        super.onResume();
        int firstCode = PreferenceHelper.readInt(this,"firstStart");
        if (firstCode == -1){
            PreferenceHelper.putInt(this,"firstStart",1);
            //// TODO: 2016/11/11 第一次运行时做的工作
        }
    }

    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }



    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                // 更新界面上的步数
                //Log.i("step",msg.getData().getInt("step")+"");
                UIMessage = Message.obtain(UIHandler,msg.getData().getInt("step"));
                steps = msg.getData().getInt("step");
                UIMessage.sendToTarget();
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case Constant.MSG_PAUSE_STEP:
                Log.i("main","msg send");
                try {
                    Message msg1 =Message.obtain(null,Constant.MSG_PAUSE_STEP);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.MSG_RESUME_STEP:
                try {
                    Message msg1 =Message.obtain(null,Constant.MSG_RESUME_STEP);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    public void setHandler(Handler handler) {
        this.UIHandler= handler;
    }

    public void resetUserImg() {
        img_avatar.setImageResource(R.mipmap.tourist);
    }
}
