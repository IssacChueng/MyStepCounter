package com.issac.mystepcounter.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.issac.mystepcounter.MainActivity;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.utils.MyDateFormatter;
import com.issac.mystepcounter.utils.MyMonthFormatter;
import com.issac.mystepcounter.utils.MyWeekFormatter;
import com.issac.mystepcounter.view.ColorTextStrip;
import com.issac.mystepcounter.view.MyMarkerView;
import com.issac.mystepcounter.view.NoScrollViewPager;


import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStatistics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStatistics extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //--------------------------------------------------------
    private LineChart mLineChartDate;
    private LineChart mLineChartWeek;
    private LineChart mLineChartMonth;
    private ColorTextStrip tab1;
    private ColorTextStrip tab2;
    private ColorTextStrip tab3;
    private TextView tv_stepCounts;
    private TextView tv_distance;
    private TextView tv_kal;
    private MarkerView mv;
    private ViewPager pager;
    private PagerTabStrip tabStrip;
    private ArrayList<View> viewContainer = new ArrayList<>();
    private ArrayList<ColorTextStrip> titleContainer = new ArrayList<>();
    public String tag = "TAG";
    private int p=0;
    //--------------------------------------------------------


    public FragmentStatistics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStatistics.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStatistics newInstance(String param1, String param2) {
        FragmentStatistics fragment = new FragmentStatistics();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Main","onCreateView-------------------");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        Context mContext = getContext();
        pager = (ViewPager) view.findViewById(R.id.chartContainer);
        mLineChartDate = new LineChart(mContext,null);
        mLineChartMonth = new LineChart(mContext,null);
        mLineChartWeek = new LineChart(mContext,null);
        initChart(mLineChartDate,1);
        initChart(mLineChartMonth,2);
        initChart(mLineChartWeek,3);
        viewContainer.add(mLineChartDate);
        viewContainer.add(mLineChartMonth);
        viewContainer.add(mLineChartWeek);
        tab1 = (ColorTextStrip) view.findViewById(R.id.tab_1);
        tab1.drawColor(255);
        tab2 = (ColorTextStrip) view.findViewById(R.id.tab_2);
        tab3 = (ColorTextStrip) view.findViewById(R.id.tab_3);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        titleContainer.add(tab1);
        titleContainer.add(tab2);
        titleContainer.add(tab3);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewContainer.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager)container).addView(viewContainer.get(position));
                return viewContainer.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewContainer.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(tag,"onPageScrolled:  position:"+ position);
                Log.d(tag, "onPageScrolled: positionOffset: "+positionOffset);
                Log.i(tag,"onPageScrolled: positionOffsetPixels: "+positionOffsetPixels);
//                if (positionOffset > 0){
//                    ColorTextStrip left = titleContainer.get(position);
//                    ColorTextStrip right = titleContainer.get(position+1);
//
//                    left.setDirection(1);
//                    right.setDirection(0);
//                    left.setmProgress(1-positionOffset);
//                    right.setmProgress(positionOffset);
//                }

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(tag,"onPageSelected position:"+position);
                ColorTextStrip now = titleContainer.get(position);
                ColorTextStrip old = titleContainer.get(p);
                now.drawColor(255);
                old.resetColor();
                p=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(tag,"onPageScrollStateChanged state: "+state);

            }
        });
        return view;
    }

    private void initChart(LineChart lineChart,int flag) {
        lineChart.setDescription("");
        lineChart.getXAxis().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.setOnChartValueSelectedListener((MainActivity)getActivity());
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(0x666666);
        Log.i("Main","initing----------");
        mv = new MyMarkerView(getContext(),R.layout.markview);
        lineChart.setMarkerView(mv);
        switch (flag){
            case 1:
                initDateLineChart(lineChart);
                break;
            case 2:
                initWeekLineChart(lineChart);
                break;
            case 3:
                initMonthLineChart(lineChart);
                break;
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );


        lineChart.setLayoutParams(lp);
    }

    private void initMonthLineChart(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();

        xAxis.setValueFormatter(new MyMonthFormatter());
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisLineColor(0x666666);
        yAxis.removeAllLimitLines();
        yAxis.setAxisMaxValue(200f);
        yAxis.setAxisMinValue(0f);
        lineChart.getAxisRight().setEnabled(false);
        setData(lineChart,7,0);


    }



    private void initWeekLineChart(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();

        xAxis.setValueFormatter(new MyWeekFormatter());
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisLineColor(0x666666);
        yAxis.removeAllLimitLines();
        yAxis.setAxisMaxValue(200f);
        yAxis.setAxisMinValue(0f);
        lineChart.getAxisRight().setEnabled(false);
        setData(lineChart,7,0);

    }

    private void initDateLineChart(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();

        xAxis.setValueFormatter(new MyDateFormatter());
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisLineColor(0x666666);
        yAxis.removeAllLimitLines();
        yAxis.setAxisMaxValue(200f);
        yAxis.setAxisMinValue(0f);
        lineChart.getAxisRight().setEnabled(false);
        setData(lineChart,7,0);

    }

    private void setData(LineChart mLineChart, int count, int initdata) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i=0;i<count;i++){
            values.add(new Entry(i,10));
        }
        Log.i("Main",values+"-------------------------------------------------------------------------------------");
        LineDataSet set1;
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() >0){
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        }else{
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setColor(Color.argb(100,0xff,0x33,0x00));
            set1.setCircleColor(Color.argb(255,0xff,0x33,0x00));
            set1.setLineWidth(1f);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);

        }
        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(set1);

        LineData data = new LineData(dataSet);
        mLineChart.setData(data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_1:
                pager.setCurrentItem(0,false);
                Log.d(tag,"0");
                break;
            case R.id.tab_2:
                pager.setCurrentItem(1,false);
                Log.d(tag,"1");
                break;
            case R.id.tab_3:
                pager.setCurrentItem(2,false);
                Log.d(tag,"2");
                break;
        }
    }
}
