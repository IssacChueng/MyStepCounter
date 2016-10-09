package com.issac.mystepcounter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.issac.mystepcounter.fragment.FragmentGroup;
import com.issac.mystepcounter.fragment.FragmentHomePage;
import com.issac.mystepcounter.fragment.FragmentStatistics;
import com.issac.mystepcounter.fragment.FragmentUser;
import com.issac.mystepcounter.view.ChangeColorWithIconView;
import com.issac.mystepcounter.view.CircleImageView;
import com.issac.mystepcounter.view.NoScrollViewPager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener, OnChartValueSelectedListener
{
    private NoScrollViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ImageView img_share;
    private CircleImageView img_avatar;
    private TextView textTitle;


    private List<ChangeColorWithIconView> mTabIndicator = new ArrayList<ChangeColorWithIconView>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOverflowShowingAlways();
        //getActionBar().setDisplayShowHomeEnabled(false);
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        mViewPager.setNoScroll(true);
        img_share = (ImageView) findViewById(R.id.img_share);
        img_avatar = (CircleImageView) findViewById(R.id.img_avatar);
        textTitle = (TextView) findViewById(R.id.text_title);
        initDatas();

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initDatas()
    {

        FragmentHomePage fHomePage = new FragmentHomePage();
        FragmentGroup fGroup = new FragmentGroup();
        FragmentStatistics fStatistics = new FragmentStatistics();
        FragmentUser fUser = new FragmentUser();
        mTabs.add(fHomePage);
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
        }else{
            img_share.setVisibility(View.GONE);
            img_avatar.setVisibility(View.GONE);
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
}
