package com.issac.mystepcounter.fragment;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.test.internal.runner.junit3.AndroidSuiteBuilder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.issac.mystepcounter.MainActivity;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.pojo.StepHour;
import com.issac.mystepcounter.utils.Constant;
import com.issac.mystepcounter.utils.DbUtils;
import com.issac.mystepcounter.utils.MyHourFormatter;
import com.issac.mystepcounter.view.MyMarkerView;
import com.issac.mystepcounter.view.PieView;

import org.hamcrest.Condition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHomePage extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //-------------------------------
    private View mFragmentView;
    private PieView mPieView;
    private LineChart mLineChartHome;
    private MarkerView mv;
    private MainActivity activity;
    private Message msg ;
    private boolean pauseFlag = false;
    private Handler mHandler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mPieView.setmSecondText(msg.what);
            return false;
        }
    });

    //-------------------------------
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        activity.setHandler(mHandler);
        msg = Message.obtain(new Handler(activity));

    }

    public FragmentHomePage() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHomePage.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHomePage newInstance(String param1, String param2) {
        FragmentHomePage fragment = new FragmentHomePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("fragmentHome","onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("fragmentHome","onCreateView");
            mFragmentView = inflater.inflate(R.layout.fragment_home_page,container,false);
            mPieView = (PieView) mFragmentView.findViewById(R.id.stepCounts);
            mPieView.setOnClickListener(this);
            mLineChartHome = (LineChart) mFragmentView.findViewById(R.id.lineChartHome);
            initChart();
            Calendar c = Calendar.getInstance();




        return mFragmentView;
    }



    @Override
    public void onClick(View v) {
        if (pauseFlag){
            msg = Message.obtain(new Handler((MainActivity)getActivity()));
            msg.what = Constant.MSG_PAUSE_STEP;
            msg.sendToTarget();
            pauseFlag = false;
        }else{
            msg = Message.obtain(new Handler((MainActivity)getActivity()));
            msg.what = Constant.MSG_RESUME_STEP;
            msg.sendToTarget();
            pauseFlag = true;
        }
    }

    private void initChart() {
        mLineChartHome.setOnChartValueSelectedListener((MainActivity)getActivity());
        mLineChartHome.setDrawGridBackground(false);
        mLineChartHome.setNoDataText(getContext().getString(R.string.no_data));
        mLineChartHome.setTouchEnabled(true);
        mLineChartHome.setDragEnabled(false);
        mLineChartHome.setScaleEnabled(false);
        mLineChartHome.setVisibility(View.VISIBLE);
        //双手触摸缩放
        mLineChartHome.setPinchZoom(false);
        mLineChartHome.setDescription("");

        mv = new MyMarkerView(getContext(),R.layout.markview);
        mLineChartHome.setMarkerView(mv);

        /*LimitLine llXAxis = new LimitLine(20f,"Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f,10f,0f);
        llXAxis.setLineColor(Color.DKGRAY);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);*/

        XAxis xAxis = mLineChartHome.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.disableGridDashedLine();
        xAxis.setValueFormatter(new MyHourFormatter());
        xAxis.setLabelCount(5,true);




        /*Typeface tf = Typeface.DEFAULT_BOLD;
        LimitLine ll1 = new LimitLine(150f,"Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f,10f,0f);
        ll1.setLineColor(Color.TRANSPARENT);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);*/

        /*LimitLine ll2 = new LimitLine(-30f,"Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f,10f,0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);*/

        YAxis yAxis = mLineChartHome.getAxisLeft();
        yAxis.removeAllLimitLines();
        yAxis.setAxisMaxValue(1000f);
        yAxis.setAxisMinValue(0f);
        yAxis.enableGridDashedLine(10f,10f,0f);
        yAxis.setDrawZeroLine(true);
        yAxis.setDrawLimitLinesBehindData(true);
        mLineChartHome.getAxisRight().setEnabled(false);
        //先将表格绘制出来，否则因为查询数据较慢出现“没有数据"提示
        float[] datas = new float[25];
        setData(datas);
        Observable.just(1L)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<Long, List<StepHour>>() {
                    @Override
                    public List<StepHour> call(Long aLong) {
                        List<StepHour> stepHours = new ArrayList<StepHour>();

                        long head =0;
                        long tail =24;
                        Log.i("main",tail+"");
                        stepHours = DbUtils.query(StepHour.class,head,tail);
                        Log.i("main",stepHours.size()+"");
                        return stepHours;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(new Func1<List<StepHour>, float[]>() {
                    @Override
                    public float[] call(List<StepHour> stepHours) {
                        float[] steps=null;
                        if (stepHours!= null && stepHours.size()>0){
                            //将0点置为0步
                            steps = new float[25];
                            StepHour step;
                            Log.i("main","stephours.size ="+stepHours.size());
                            for (int i=1;i<stepHours.size();i++){
                                step = stepHours.get(i);
                                steps[(int) step.getTime()] = step.getStep();
                            }


                        }
                        return steps;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<float[]>() {
                    @Override
                    public void call(float[] ints) {
                        if(ints!=null)
                            setData(ints);
                    }
                });
        //mLineChartHome.animateX(2500);

        Legend l = mLineChartHome.getLegend();
        l.setForm(Legend.LegendForm.LINE);



        //--------------------------




    }



    private void setData(float[] datas) {
        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {

            float val = datas[i];
            Log.i("main","val="+val);
            values.add(new Entry(i, val));
        }


        LineDataSet set1;
        if (mLineChartHome.getData() != null &&
                mLineChartHome.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChartHome.getData().getDataSetByIndex(0);
            set1.setValues(values);
            /*mLineChartHome.getData().notifyDataChanged();
            mLineChartHome.notifyDataSetChanged();*/
            mLineChartHome.moveViewToX(values.size() - 24);
        }else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values,null);

            set1.disableDashedLine();
            set1.setLineWidth(0f);
            set1.setHighlightLineWidth(0f);
            set1.setDrawCircles(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFillColor(Color.argb(255, 0x66, 0xCC, 0xCC));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mLineChartHome.setData(data);
            /*mLineChartHome.getData().notifyDataChanged();
            mLineChartHome.notifyDataSetChanged();*/
            mLineChartHome.moveViewToX(data.getEntryCount() - 24);
        }

    }


    @Override
    public void onResume() {
        Log.i("fragmentHome","onResume");
        mPieView.startCircleAnimation();
        Log.i("main","onresume");
        Observable.interval(5,TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<Long, List<StepHour>>() {
                    @Override
                    public List<StepHour> call(Long aLong) {
                        List<StepHour> stepHours = new ArrayList<StepHour>();

                        long head =0;
                        long tail =24;
                        Log.i("main",tail+"");
                        stepHours = DbUtils.query(StepHour.class,head,tail);
                        Log.i("main",stepHours.size()+"");
                        return stepHours;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(new Func1<List<StepHour>, float[]>() {
                    @Override
                    public float[] call(List<StepHour> stepHours) {
                        float[] steps=null;
                        if (stepHours!= null && stepHours.size()>0){
                            //将0点置为0步
                            steps = new float[25];
                            StepHour step;
                            Log.i("main","stephours.size ="+stepHours.size());
                            for (int i=1;i<stepHours.size();i++){
                                step = stepHours.get(i);
                                steps[(int) step.getTime()] = step.getStep();
                            }


                        }
                        return steps;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<float[]>() {
                    @Override
                    public void call(float[] ints) {
                        if(ints!=null)
                        setData(ints);
                    }
                });
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("Main","-->onDestroyView");
        super.onDestroyView();
    }

    public void drawMarkerView(Entry e, Highlight h){


    }

    @Override
    public void onDestroy() {
        Log.i("fragmentHome","onDestroy");
        super.onDestroy();
    }
}
